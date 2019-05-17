package ru.hse.tpc.gui;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.hse.tpc.gui.controller.MainController;

public class TPCApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Flow flow = new Flow(MainController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        ViewFlowContext context = new ViewFlowContext();
        context.register("Stage", stage);
        flow.createHandler(context).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(() -> System.exit(0));
        decorator.setGraphic(new SVGGlyph(""));

        stage.setTitle("TPC 1.1");

        double width = 1000;
        double height = 600;

        //Scene scene = new Scene(decorator, width, height);
        Scene scene = new Scene(decorator);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(TPCApp.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                TPCApp.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                TPCApp.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
