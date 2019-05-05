# Utility for computing transition priorities for live Petri nets

### Requirements
* JDK 1.8+
* Maven 3+

### Build
* `mvn clean package`

### Run
`java -jar ./target/transition-priority-computer.jar petri_net_path output_dir_path`
* `petri_net_path` - path to json with input Petri net  (see ./src/test/resources/pn.json for an example of input format)
* `output_dir_path` - path to output dir
