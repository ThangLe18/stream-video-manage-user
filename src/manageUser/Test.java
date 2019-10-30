/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Test extends Application {

    Stage window;
    ListView<String> listView;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Game");

        GridPane grid = new GridPane();



        listView = new ListView<>();
        listView.getItems().addAll("Action 1", "Action 2", "Action 3", "Action 4");
        listView.setPrefWidth(260);
        listView.setMaxWidth(260);
        listView.setPrefHeight(150);
        GridPane.setConstraints(listView, 0, 2, 1, 1);


        grid.getChildren().add(listView);

        Scene scene = new Scene(grid, 950, 750);
        window.setScene(scene);
        window.show();
    }
}