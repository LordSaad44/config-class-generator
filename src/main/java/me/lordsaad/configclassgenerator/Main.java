package me.lordsaad.configclassgenerator;

import com.google.common.base.CaseFormat;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * Created by Saad on 24/8/2015.
 */
public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("Configuration Class Generator");
        stage.setResizable(false);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 700, 600);
        stage.setScene(scene);
        stage.show();

        Text scenetitle = new Text("Configuration Class Generator");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0);

        final TextArea input = new TextArea();
        input.setMaxSize(600, 600);
        input.setPromptText("Input");
        grid.add(input, 0, 1);

        Button generate = new Button("Generate");
        grid.add(generate, 0, 2);

        final TextArea output = new TextArea();
        output.setEditable(false);
        output.setMaxSize(600, 600);
        output.setPromptText("Output");
        grid.add(output, 0, 3);

        generate.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Yaml yaml = new Yaml();
                Map config = (Map) yaml.load(input.getText());
                String s = "public class Configuration {\n";
                for (Object key : config.keySet()) {

                    s += "\n    public static class "
                            + CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, key.toString())
                            + " { \n        private static final String PREFIX = \"" + key.toString() + ".\";\n";

                    Map map = (Map) config.get(key);
                    for (Object subContents : map.keySet()) {
                        if (map.get(subContents).toString().contains("{")) {
                            s += "        private static final String "
                                    + subContents.toString().replace("-", "_").toUpperCase()
                                    + " = PREFIX + \""
                                    + subContents.toString()
                                    + ".\";\n";

                            Map subMap = (Map) map.get(subContents);
                            for (Object subSubContents : subMap.keySet()) {

                                if (subMap.get(subSubContents).toString().contains("{")) {
                                    s += "        private static final String "
                                            + subContents.toString().replace("-", "_").toUpperCase()
                                            + "_"
                                            + subSubContents.toString().replace("-", "_").toUpperCase()
                                            + " = " + subContents.toString().replace("-", "_").toUpperCase()
                                            + " + \""
                                            + subSubContents.toString()
                                            + ".\";\n";

                                    Map subSubMap = (Map) subMap.get(subSubContents);
                                    for (Object subSubSubContents : subSubMap.keySet()) {
                                        s += "        public static final String "
                                                + subContents.toString().replace("-", "_").toUpperCase()
                                                + "_"
                                                + subSubContents.toString().replace("-", "_").toUpperCase()
                                                + "_"
                                                + subSubSubContents.toString().replace("-", "_").toUpperCase()
                                                + " = "
                                                + subContents.toString().replace("-", "_").toUpperCase()
                                                + "_"
                                                + subSubContents.toString().replace("-", "_").toUpperCase()
                                                + " + \""
                                                + subSubSubContents.toString()
                                                + "\";\n";
                                    }
                                } else {
                                    s += "        public static final String "
                                            + subContents.toString().replace("-", "_").toUpperCase()
                                            + "_"
                                            + subSubContents.toString().replace("-", "_").toUpperCase()
                                            + " = " + subContents.toString().replace("-", "_").toUpperCase()
                                            + " + \""
                                            + subSubContents.toString()
                                            + "\";\n";
                                }
                            }
                        } else {
                            if (map.values().contains(map.get(subContents))) {
                                s += "        public static final String "
                                        + subContents.toString().replace("-", "_").toUpperCase()
                                        + " = PREFIX + \""
                                        + subContents.toString()
                                        + "\";\n";
                            } else {
                                s += "        private static final String "
                                        + subContents.toString().replace("-", "_").toUpperCase()
                                        + " = PREFIX + \""
                                        + subContents.toString()
                                        + "\";\n";
                            }
                        }
                    }
                    s += "    }\n";
                }
                s += "}";
                output.setText(s);
            }
        });
    }
}