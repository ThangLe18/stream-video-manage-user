/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author ThayLe
 */
public class ClientFX extends Application implements Serializable{
    Scene scene2;   //main screen
    Scene scene;    //login screen
    public static ListView<String> listView;
    UserData currentUser;
    Client client;
    boolean ready = false;
    public static ArrayList<UserData> userData = new ArrayList<>();
    public static void main(String[] args) {
        userData.add(new UserData("12347162", "Mickey js","a", "1"));
        userData.add(new UserData("12341527", "Rancix ft","b", "1"));
        userData.add(new UserData("66211343", "Aslhycole","c", "1"));
        userData.add(new UserData("42211455", "Michel Owen","d", "1"));
        userData.add(new UserData("55223114", "Frank lampard","e", "1"));
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        // Screen login
        primaryStage.setTitle("StreamVideo");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        final Text actiontarget = new Text();
        actiontarget.setStyle("-fx-text-inner-color: red;");
        grid.add(actiontarget, 1, 6);
        
        Button btn = new Button("Sign in");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                UserData m = checkLogin(userTextField.getText(), pwBox.getText());
                if(m != null) {
                    currentUser = m;
                    primaryStage.setScene(scene2);
                    client = new Client();
                    try {
                        client.connectSocket(currentUser.userID);
                        client.listenFromServer();
                        Thread.sleep(1000);
                        for(UserStateDataSend ust : client.listUserStateDataSend){
                            listView.getItems().add(ust.userName);
                        }
                        ready = true;
                    } catch (IOException ex) {} catch (InterruptedException ex) {}
                }
                else actiontarget.setText("Login failed!");
            }
        });
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        primaryStage.show();
        
        
        
        
        
        //---------------------------Main screen-----------------------------//
        
        
        
        
        
        
        GridPane grid2 = new GridPane();
        grid2.setAlignment(Pos.CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(25, 25, 25, 25));
        scene2 = new Scene(grid2, 300, 275);
        
        
        
        //button call
        Button btn_call = new Button("Call");
        btn_call.setMinWidth(100);btn_call.setMaxWidth(100);
        btn_call.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            }
        });
        HBox hbBtn_call = new HBox(10);
        hbBtn_call.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_call.getChildren().add(btn_call);
        grid2.add(hbBtn_call, 0, 0);
        
        
        
        
        
        //nutton end call
        Button btn_endcall = new Button("End call");
        btn_endcall.setMinWidth(100);btn_endcall.setMaxWidth(100);
        btn_endcall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            }
        });
        HBox hbBtn_endcall = new HBox(10);
        hbBtn_endcall.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_endcall.getChildren().add(btn_endcall);
        grid2.add(hbBtn_endcall, 0, 3);
        
        
        
        //button accept call
        Button btn_acceptcall = new Button("Accept call");
        btn_acceptcall.setMinWidth(100);btn_acceptcall.setMaxWidth(100);
        btn_acceptcall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println(currentUser.name);
            }
        });
        HBox hbBtn_acceptcall = new HBox(10);
        hbBtn_acceptcall.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_acceptcall.getChildren().add(btn_acceptcall);
        grid2.add(hbBtn_acceptcall, 0, 2);
        
        
        
        // button eject call
        Button btn_rejectcall = new Button("Eject call");
        btn_rejectcall.setMinWidth(100);btn_rejectcall.setMaxWidth(100);
        btn_rejectcall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            }
        });
        HBox hbBtn_rejectcall = new HBox(10);
        hbBtn_rejectcall.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_rejectcall.getChildren().add(btn_rejectcall);
        grid2.add(hbBtn_rejectcall, 0, 1);
        
        
        
        // button sign out
        Button btn_logout = new Button("Sign out");
        btn_logout.setMinWidth(100);btn_logout.setMaxWidth(100);
        btn_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                currentUser = null;
                primaryStage.setScene(scene);
            }
        });
        HBox hbBtn_logout = new HBox(10);
        hbBtn_logout.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_logout.getChildren().add(btn_logout);
        grid2.add(hbBtn_logout, 0, 4);
        
        
        
        
        
        
        // List user obline
        listView = new ListView<>();
        listView.setPrefWidth(260);
        listView.setMaxWidth(260);
        listView.setPrefHeight(150);
        GridPane.setConstraints(listView, 1, 0, 6, 5);
        grid2.getChildren().add(listView);
        
        
        
        new Thread(new Runnable() {
                @Override
                public void run()
                {
                        while(true){
                            try {
                                if(ready == false);
                                else
                                {
                                    if(client.isUpdateState() == true)
                                    {
                                        //listView.refresh();
                                        test();
                                        System.out.println("updaue : true");
                                        client.setUpdateState(false);
                                        Thread.sleep(1000);
                                    }
                                }
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ClientFX.class.getName()).log(Level.SEVERE, null, ex);
                            }
                     }
                }
            }).start();  
    }


    public static void test(){
        System.out.println("asd");
        try {
            listView.getItems().add("change1");
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    public static UserData checkLogin(String username,String password){
        for(UserData u : userData){
            if(u.userName.equals(username) && u.userPassword.equals(password)){
                return u;
            }
        }
        return null;
    }
    
}
