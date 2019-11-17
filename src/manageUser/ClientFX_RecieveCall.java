/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;


import Lib.UserData;
import Lib.UserStateDataSend;
import Lib.MessagePackage;
import Lib.TypeProtocol;
import ServerVideo.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 *
 * @author ThayLe
 */
public class ClientFX_RecieveCall extends Application implements Serializable{
    Scene scene2;   //main screen
    Scene scene;    //login screen
    Text nameUser;
    public static Button btn_call;
    public static int h=0;
    public static Button btn_endcall;
    public static Button btn_acceptcall;
    public static Button btn_rejectcall;
    public static Button btn_logout;
    public static Button btn_displayvideo;
    public static Text activity;
    public static ListView<String> listView;
    public static UserData currentUser;
    public static String currentSelectedUser;
    public static String currentSelectedUserID;
    public static Client client;
    public static String currentActivity;
    public static Stage stage,stage2;
    private static volatile Thread playThread;
    public static Socket socketVideo;
    public static VideoClientFXWebcam_Client1 startUpTest;
    boolean ready = false;
    public static ArrayList<UserData> userData = new ArrayList<>();
    public static void main(String[] args) {
        
        
        
        
        
        
        currentSelectedUser = new String("");
        currentSelectedUserID = new String("");
        currentActivity = new String();
        userData.add(new UserData("12347162", "Mickey Jr","a", "12345678"));
        userData.add(new UserData("12341527", "Rancix Sr","s", "12345678"));
        userData.add(new UserData("66211343", "Aslhycole","d", "12345678"));
        userData.add(new UserData("42211455", "Micl Owen","q", "12345678"));
        userData.add(new UserData("55223114", "FrLampard","w", "12345678"));
        launch(args);
    }
    @Override
    public void start(Stage primaryStage1) throws Exception {
        stage = primaryStage1;
        
        
        // Screen login
        stage.setTitle("StreamVideo-RecieveCall");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        
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
        pwBox.setText("12345678");
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
                    nameUser.setText(currentUser.name);
                    stage.setScene(scene2);
                    client = new Client();
                    try {
                        client.connectSocket(currentUser.userID);
                        client.listenFromServer();
                        Thread.sleep(1000);
                        for(UserStateDataSend ust : client.listUserStateDataSend){
                            listView.getItems().add(ust.userName);
                        }
                        ready = true;
                        connectToVideoSocket();
                    } catch (IOException ex) {} catch (InterruptedException ex) {}
                }
                else showMyAlert("Login Failed!");
           
                
            }
        });
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        stage.show();
        
        
        
        
        
        //---------------------------Main screen-----------------------------//
        
        
        
        
        
        
        GridPane grid2 = new GridPane();
        grid2.setAlignment(Pos.CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(25, 25, 25, 25));
        scene2 = new Scene(grid2, 300, 275);
        
        
        nameUser = new Text("Your name");
        nameUser.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid2.add(nameUser, 0, 0, 5, 1);
        
        activity = new Text("Free");
        grid2.add(activity, 1, 0, 5, 1);
        
        
        
        
        //button call
        btn_call = new Button("Call");
        btn_call.setDisable(true);
        btn_call.setMinWidth(100);btn_call.setMaxWidth(100);
        btn_call.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    if(client.listUserStateDataSend.get(findIndexUserByID(currentSelectedUserID)).getState().equals("free"))
                    {
                        client.sendMessage(new MessagePackage(TypeProtocol.REQUEST_CALL_VIDEO,
                            currentSelectedUserID, currentUser.userID, 0));
                    }
                    else{
                        showMyAlert(currentSelectedUser + " is busy!");
                    }
                } catch (IOException ex) {} catch (InterruptedException ex) {}
            }
        });
        HBox hbBtn_call = new HBox(10);
        hbBtn_call.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_call.getChildren().add(btn_call);
        grid2.add(hbBtn_call, 0, 1);
        
        
        
        
        
        //button end call
        btn_endcall = new Button("End call");
        btn_endcall.setDisable(true);
        btn_endcall.setMinWidth(100);btn_endcall.setMaxWidth(100);
        btn_endcall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    client.sendMessage(new MessagePackage(TypeProtocol.END_CALL_VIDEO,
                            findDesIDByID(currentUser.userID), currentUser.userID, 0));
                } catch (IOException ex) {} catch (InterruptedException ex) {}
            }
        });
        HBox hbBtn_endcall = new HBox(10);
        hbBtn_endcall.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_endcall.getChildren().add(btn_endcall);
        grid2.add(hbBtn_endcall, 0, 4);
        
        
        
        //button accept call
        btn_acceptcall = new Button("Accept call");
        btn_acceptcall.setDisable(true);
        btn_acceptcall.setMinWidth(100);btn_acceptcall.setMaxWidth(100);
        btn_acceptcall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    if(client.listUserStateDataSend.get(findIndexUserByID(currentUser.userID)).getState().equals("iscalled"))
                    {
                        client.sendMessage(new MessagePackage(TypeProtocol.ACCEPT_CALL_VIDEO,
                            findIDByDesID(currentUser.userID), currentUser.userID, 0));
                    }
                } catch (IOException ex) {} catch (InterruptedException ex) {}
            }
        });
        HBox hbBtn_acceptcall = new HBox(10);
        hbBtn_acceptcall.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_acceptcall.getChildren().add(btn_acceptcall);
        grid2.add(hbBtn_acceptcall, 0, 3);
        
        
        
        // button eject call
        btn_rejectcall = new Button("Eject call");
        btn_rejectcall.setDisable(true);
        btn_rejectcall.setMinWidth(100);btn_rejectcall.setMaxWidth(100);
        btn_rejectcall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    if(client.listUserStateDataSend.get(findIndexUserByID(currentUser.userID)).getState().equals("iscalled"))
                    {
                        client.sendMessage(new MessagePackage(TypeProtocol.REJECT_CALL_VIDEO,
                            findIDByDesID(currentUser.userID), currentUser.userID, 0));
                    }
                } catch (IOException ex) {} catch (InterruptedException ex) {}
            }
        });
        HBox hbBtn_rejectcall = new HBox(10);
        hbBtn_rejectcall.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_rejectcall.getChildren().add(btn_rejectcall);
        grid2.add(hbBtn_rejectcall, 0, 2);
        
        
        
        // button sign out
        btn_logout = new Button("Sign out");
        btn_logout.setDisable(true);
        btn_logout.setMinWidth(100);btn_logout.setMaxWidth(100);
        btn_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    client.sendMessage(new MessagePackage(TypeProtocol.REQUEST_LOGOUT, currentUser.userID));
                    currentUser = null;
                    stage.setScene(scene);
                } catch (IOException ex) {} catch (InterruptedException ex) {}
                        
            }
        });
        HBox hbBtn_logout = new HBox(10);
        hbBtn_logout.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_logout.getChildren().add(btn_logout);
        grid2.add(hbBtn_logout, 0, 5);
        
        
        
        
        // button display video
        btn_displayvideo = new Button("Display Video");
        btn_displayvideo.setDisable(true);
        btn_displayvideo.setMinWidth(100);btn_displayvideo.setMaxWidth(100);
        btn_displayvideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    startStream();
                } catch (IOException ex) {
                    Logger.getLogger(ClientFX_Call.class.getName()).log(Level.SEVERE, null, ex);
                }     
            }
        });
        HBox hbBtn_displayvideo = new HBox(10);
        hbBtn_displayvideo.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_displayvideo.getChildren().add(btn_displayvideo);
        grid2.add(hbBtn_displayvideo, 0, 6);
        
        
        
        
        
        
        // List user obline
        listView = new ListView<>();
        listView.setPrefWidth(260);
        listView.setMaxWidth(260);
        listView.setPrefHeight(150);
        GridPane.setConstraints(listView, 1, 1, 6, 6);
        grid2.getChildren().add(listView);
        
        
        
        
        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    currentSelectedUser = cell.getItem();
                    currentSelectedUserID = findIDByName(currentSelectedUser);
                    System.out.println("You selected : " + currentSelectedUser+"---"+currentSelectedUserID);
                    e.consume();
                }
            });
            return cell;
        });
        listView.setOnMouseClicked(e -> {
            System.out.println("You clicked on an empty cell");
        });
        
        
        
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
                                        updateListUserOnline();
                                        updateMyActivity();
                                        client.setUpdateState(false);
                                        Thread.sleep(1000);
                                    }
                                }
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {} catch (Exception ex) {}
                     }
                }
            }).start();  
    }


    public static void updateListUserOnline() throws InterruptedException{
        
        listView.getItems().add("");
        Thread.sleep(1000);
        listView.getItems().clear();
        listView.getItems().add("----ListUserOnline----");
        listView.getItems().add("----ListUserOnline----");
        for(UserStateDataSend u2 : client.listUserStateDataSend ){
            if(!u2.userName.equals(currentUser.name)) listView.getItems().add(u2.userName);
        }
    }
    
    public static void updateMyActivity() throws Exception{
        String state = client.listUserStateDataSend.get(findIndexUserByID(currentUser.userID)).getState();
        String desID = client.listUserStateDataSend.get(findIndexUserByID(currentUser.userID)).getDesID();
        if(state.equals("iscalling")) {
            setStateButton(false, true, false, false, false);
            activity.setText("Calling to "+ currentSelectedUser);
        }
        if(state.equals("iscalled")) {
            setStateButton(false, false, true, true, false);
            activity.setText("Calling from "+ findNameByDesID(currentUser.userID));
        }
        if(state.equals("free")) {
            setStateButton(true, false, false, false, true);
            activity.setText("Free");
        }
        if(state.equals("incalling")) {
            setStateButton(false, true, false, false, false);
            //activity.setText("In calling with "+ findNameByDesID(currentUser.userID));
            activity.setText("In calling...");
            btn_displayvideo.setDisable(false);
        }
    }
    
    public static void setStateButton(Boolean btn1,Boolean btn2,Boolean btn3,Boolean btn4,Boolean btn5){
        btn_call.setDisable(!btn1);
        btn_endcall.setDisable(!btn2);
        btn_acceptcall.setDisable(!btn3);
        btn_rejectcall.setDisable(!btn4);
        btn_logout.setDisable(!btn5);
    }
    
    public static String findNameByDesID(String id){
        for(UserStateDataSend uu : client.listUserStateDataSend){
            if(uu.desID.equals(id)) 
                return uu.userName;
        }
        return "unknow";
    }
    
    public static String findIDByDesID(String id){
        for(UserStateDataSend uu : client.listUserStateDataSend){
            if(uu.desID.equals(id)) 
                return uu.userID;
        }
        return "idnull";
    }
    
    public static String findDesIDByID(String id){
        for(UserStateDataSend uu : client.listUserStateDataSend){
            if(uu.userID.equals(id)) 
                return uu.desID;
        }
        return "idnull";
    }
    
    public static String findIDByName(String name){
        for(UserStateDataSend uu : client.listUserStateDataSend){
            if(uu.userName.equals(name)) 
                return uu.userID;
        }
        return "idnull";
    }
    
    public static int findIndexUserByID(String id){
        for(int i=0;i<client.listUserStateDataSend.size();i++){
            if(client.listUserStateDataSend.get(i).userID.equals(id)) 
                return i;
        }
        return -1;
    }
    
    public static void showMyAlert(String message){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static UserData checkLogin(String username,String password){
        for(UserData u : userData){
            if(u.userName.equals(username) && u.userPassword.equals(password)){
                return u;
            }
        }
        return null;
    }
    
    
    public static void connectToVideoSocket() throws IOException, InterruptedException{
        socketVideo = new Socket("localhost", 5555);
        System.out.println("Connected!" + socketVideo);
        client.sendMessage(new MessagePackage(TypeProtocol.SAVE_SOCKET_VIDEO,
                            "null", currentUser.userID, socketVideo.getLocalPort()));
    }
    
    @Override
    public void stop(){
         try {
               client.sendMessage(new MessagePackage(TypeProtocol.REQUEST_LOGOUT, currentUser.userID));
         } catch (IOException ex) {} catch (InterruptedException ex) {}
    }
    
    
    
    
    
    
    
    
    public static void startStream() throws IOException{
        System.out.println("start stream");
        Stage primaryStage = new Stage();
        final StackPane root = new StackPane();
        final ImageView imageView = new ImageView();

        root.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());

        final Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle(currentUser.name);
        primaryStage.setScene(scene);
        primaryStage.show();

        
        InputStream is=socketVideo.getInputStream();
        System.out.println("Connected to server, start playing...");
        
        playThread = new Thread(new Runnable() {
            public void run() {
                try {
                    //final String videoFilename = getParameters().getRaw().get(0);
                    final String videoFilename = "/home/kiosk01/bunny.mp4";
                    //final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilename);
                    final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(is);
                    grabber.start();
                    primaryStage.setWidth(grabber.getImageWidth());
                    primaryStage.setHeight(grabber.getImageHeight());
                    final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);

                    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    final SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                    soundLine.open(audioFormat);
                    soundLine.start();

                    final Java2DFrameConverter converter = new Java2DFrameConverter();

                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    while (!Thread.interrupted()) {
                        Frame frame = grabber.grab();
                        if (frame == null) {
                            break;
                        }
                        if (frame.image != null) {
                            final Image image = SwingFXUtils.toFXImage(converter.convert(frame), null);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    imageView.setImage(image);
                                }
                            });
                        } else if (frame.samples != null) {
                            final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                            channelSamplesShortBuffer.rewind();

                            final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

                            for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                                short val = channelSamplesShortBuffer.get(i);
                                outBuffer.putShort(val);
                            }

                            /**
                             * We need this because soundLine.write ignores
                             * interruptions during writing.
                             */
                            try {
                                executor.submit(new Runnable() {
                                    public void run() {
                                        soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                        outBuffer.clear();
                                    }
                                }).get();
                            } catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    executor.shutdownNow();
                    executor.awaitTermination(10, TimeUnit.SECONDS);
                    soundLine.stop();
                    grabber.stop();
                    grabber.release();
                    Platform.exit();
                } catch (Exception exception) {
                    System.exit(1);
                }
            }
        });
        playThread.start();
    }
    

    
    
}
