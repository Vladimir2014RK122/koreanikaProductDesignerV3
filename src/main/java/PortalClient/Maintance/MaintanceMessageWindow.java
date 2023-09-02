package PortalClient.Maintance;

import PortalClient.Authorization.LoginWindow;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.w3c.dom.Text;
import utils.InfoMessage;
import utils.MainWindow;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MaintanceMessageWindow {

    Stage stage = new Stage();
    Scene mainScene;
    Scene scene;

    AnchorPane rootAnchorPane;

    Button btnWindowClose;
    Button btnWindowMaxMin;
    Button btnWindowSubtract;

    Button btnSend = null;
    Button btnCancel = null;
    TextArea textAreaMsg;

    private double xOffset = 0;
    private double yOffset = 0;

    private static MaintanceMessageWindow instance;

    public MaintanceMessageWindow(){

        try{
            rootAnchorPane = FXMLLoader.load(getClass().getResource("/fxmls/MaintanceMessage.fxml"));
        }catch (IOException ex){

        }
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: transparent;");
        root.setPrefHeight(rootAnchorPane.getPrefHeight()+10);
        root.setPrefWidth(rootAnchorPane.getPrefWidth()+10);
        root.getChildren().add(rootAnchorPane);

        AnchorPane.setTopAnchor(rootAnchorPane, 5.0);
        AnchorPane.setBottomAnchor(rootAnchorPane, 5.0);
        AnchorPane.setLeftAnchor(rootAnchorPane, 5.0);
        AnchorPane.setRightAnchor(rootAnchorPane, 5.0);

        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        initControls();
        initControlsLogic();
    }

    private void initControls(){

        btnWindowClose = (Button) rootAnchorPane.lookup("#btnWindowClose");
        btnWindowMaxMin = (Button) rootAnchorPane.lookup("#btnWindowMaxMin");
        btnWindowSubtract = (Button) rootAnchorPane.lookup("#btnWindowSubtract");

        btnSend = (Button) rootAnchorPane.lookup("#btnSend");
        btnCancel = (Button) rootAnchorPane.lookup("#btnCancel");

        textAreaMsg = (TextArea) rootAnchorPane.lookup("#textAreaMsg");

        btnWindowMaxMin.setVisible(false);
        btnWindowSubtract.setVisible(false);
    }

    private void initControlsLogic(){

        rootAnchorPane.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                btnSend.fire();
            }
        });

        btnSend.setOnAction(actionEvent -> sendMessage());
        btnCancel.setOnAction(actionEvent -> stage.close());

        rootAnchorPane.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        rootAnchorPane.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        btnWindowClose.setOnAction(actionEvent -> {
            btnCancel.fire();
        });

        MaintanceMessage.readyToSendProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1.booleanValue()) btnSend.setDisable(false);
            else btnSend.setDisable(true);
        });
    }

    public synchronized static MaintanceMessageWindow getInstance() {
        if(instance == null){
            instance = new MaintanceMessageWindow();
        }
        return instance;
    }

    public void show(Scene parentScene){

        if(stage!= null && stage.isShowing())return;

        this.mainScene = parentScene;



        stage = new Stage();
        stage.setScene(scene);


        stage.initOwner(mainScene.getWindow());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
    }

    private void sendMessage(){

        MaintanceMessage maintanceMessage = new MaintanceMessage(textAreaMsg.getText());

        try {
            maintanceMessage.sendMessageToPortal(ClimeType.ADVICE);
            maintanceMessage.setResultCallBacks(new MaintanceMessage.ResultCallBacks() {
                @Override
                public void success() {
                    MainWindow.showInfoMessage(InfoMessage.MessageType.SUCCESS, "Ваше сообщение отправлено!");
                }

                @Override
                public void failed() {
                    MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ваше сообщение НЕ отправлено!");
                }
            });

        } catch (ExecutionException e) {
            MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ваше сообщение НЕ отправлено!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ваше сообщение НЕ отправлено!");
            e.printStackTrace();
        }


        stage.close();
        textAreaMsg.setText("");

    }
}
