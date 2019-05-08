package ru.hse.tpc.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.vavr.control.Either;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.tpc.PetriNet;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.DeselAlgo;
import ru.hse.tpc.desel.DeselAlgoParallel;
import ru.hse.tpc.pnml.PNMLToInnerModelMapper;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@ViewController(value = "/fxml/Main.fxml")
public class MainController {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:");

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private TextArea console;
    @FXML
    private StackPane fileImportStateContainer;
    @FXML
    private JFXButton importFileBtn;
    @FXML
    private Label fileImportState;
    @FXML
    private StackPane cyclicRunsCheckboxContainer;
    @FXML
    private Label cyclicRunsPlaceholder;
    @FXML
    private JFXButton repeatSearchBtn;

    private StringProperty consoleText;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private PNMLToInnerModelMapper pnmlMapper = new PNMLToInnerModelMapper();
    private DeselAlgo deselAlgo = new DeselAlgoParallel(ForkJoinPool.commonPool());

    private PetriNet petriNet;
    private Map<String, CyclicRun> cyclicRunMap;

    @PostConstruct
    public void init() {
        setupConsole();
        setupImportFileButton();
        setupRepeatSearchBtn();
    }

    private void setupConsole() {
        consoleText = new SimpleStringProperty();
        console.textProperty().bind(consoleText);
        consoleText.addListener((o, oldVal, newVal) -> {
            console.selectPositionCaret(console.getLength());
            console.deselect();
        });
        consolelog("Import file to start work");
    }

    private void consolelog(String text) {
        String line = DATE_FORMAT.format(new Date()) + " " + text + "\n";
        Platform.runLater(() -> consoleText.setValue(consoleText.getValueSafe() + line));
    }

    private void errorlog(String text) {
        consolelog("ERROR \u2014 " + text);
    }

    private void setupImportFileButton() {
        final Stage stage = (Stage) context.getRegisteredObject("Stage");
        final JFXProgressBar progressBar = new JFXProgressBar(0);
        progressBar.getStyleClass().add("custom-jfx-progress-bar-stroke");
        importFileBtn.setOnAction((a) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNML files (*.pnml)", "*.pnml");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                consolelog("Importing model from " + file.getName() + "...");
                importFileBtn.setDisable(true);
                progressBar.setProgress(-1.0f);
                fileImportStateContainer.getChildren().setAll(progressBar);

                Task<Either<String, PetriNet>> importer = createImporter(file);
                importer.messageProperty().addListener((o, oldVal, newVal) -> {
                    Either<String, PetriNet> result;
                    try {
                        result = importer.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        result = Either.left("Internal application error");
                    }
                    if (result.isLeft()) {
                        errorlog(result.getLeft());
                        petriNet = null;
                        fileImportState.setText("No file imported");
                    } else {
                        consolelog("Model is successfully imported. Click REPEAT SEARCH to find cyclic runs");
                        petriNet = result.get();
                        fileImportState.setText(file.getName());
                    }
                    fileImportStateContainer.getChildren().setAll(fileImportState);
                    importFileBtn.setDisable(false);
                });
                executorService.execute(importer);
            }
        });
    }

    private Task<Either<String, PetriNet>> createImporter(File file) {
        return new Task<Either<String, PetriNet>>() {
            @Override
            protected Either<String, PetriNet> call() throws Exception {
                Either<String, PetriNet> result = pnmlMapper.map(file);
                updateMessage("IMPORT FINISHED");
                return result;
            }
        };
    }

    private void setupRepeatSearchBtn() {
        VBox vBox = new VBox();
        vBox.setSpacing(20);
        ScrollPane scrollPane = new ScrollPane(vBox);
        JFXSpinner spinner = new JFXSpinner();
        spinner.setRadius(13);
        spinner.getStyleClass().add("blue-spinner");
        repeatSearchBtn.setOnAction((a) -> {
            if (petriNet != null) {
                consolelog("Searching for cyclic runs...");
                repeatSearchBtn.setDisable(true);
                cyclicRunsCheckboxContainer.getChildren().setAll(spinner);

                Task<List<CyclicRun>> searcher = createSearcher(petriNet.getTransitions(), petriNet.getMarking());
                searcher.messageProperty().addListener((o, oldVal, newVal) -> {
                    List<CyclicRun> cyclicRuns;
                    try {
                        cyclicRuns = searcher.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        errorlog("Internal application error");
                        cyclicRuns = Collections.emptyList();
                    }
                    if (cyclicRuns.isEmpty()) {
                        errorlog("No cyclic runs detected");
                        cyclicRunsCheckboxContainer.getChildren().setAll(cyclicRunsPlaceholder);
                    } else {
                        consolelog("Cycle runs were found. Select required cycle runs and click COMPUTE RELATION to proceed");
                        cyclicRunMap = new HashMap<>(cyclicRuns.size());
                        JFXCheckBox[] boxes = new JFXCheckBox[cyclicRuns.size()];
                        for (int i = 0; i < boxes.length; i++) {
                            CyclicRun run = cyclicRuns.get(i);
                            String runStr = run.toString();
                            JFXCheckBox box = new JFXCheckBox(runStr);
                            cyclicRunMap.put(runStr, run);
                            boxes[i] = box;
                        }
                        vBox.getChildren().setAll(boxes);
                        cyclicRunsCheckboxContainer.getChildren().setAll(scrollPane);
                    }
                    repeatSearchBtn.setDisable(false);
                });
                executorService.execute(searcher);
            } else {
                errorlog("No file imported");
            }
        });
    }

    private Task<List<CyclicRun>> createSearcher(List<Transition> transitions, Marking marking) {
        return new Task<List<CyclicRun>>() {
            @Override
            protected List<CyclicRun> call() throws Exception {
                List<CyclicRun> cyclicRuns = deselAlgo.findCyclicRuns(transitions, marking);
                updateMessage("SEARCH FINISHED");
                return cyclicRuns;
            }
        };
    }
}
