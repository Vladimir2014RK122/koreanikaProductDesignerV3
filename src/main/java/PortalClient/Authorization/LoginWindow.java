package PortalClient.Authorization;

import com.sun.javafx.application.HostServicesDelegate;
import cutDesigner.CutDesigner;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LoginWindow {

    Stage stage = new Stage();
    Scene mainScene;
    Scene scene;

    AnchorPane rootAnchorPaneLogin;

    private double xOffset = 0;
    private double yOffset = 0;

    private static LoginWindow instance;

    //controls:
    Button btnWindowClose;
    Button btnWindowMaxMin;
    Button btnWindowSubtract;
    Button btnLogin;

    Hyperlink linkForgetPassword;

    PasswordField passwordField;
    TextField textFieldLogin;

    private LoginWindow(){

        try{
            rootAnchorPaneLogin = FXMLLoader.load(getClass().getResource("/fxmls/Authorization/LoginWindow.fxml"));
        }catch (IOException ex){

        }
        scene = new Scene(rootAnchorPaneLogin);
        initControls();
        initControlsLogic();
    }

    public static LoginWindow getInstance() {

        if(instance == null){
            instance = new LoginWindow();
        }
        return instance;
    }

    private void initControls(){

        btnWindowClose = (Button) rootAnchorPaneLogin.lookup("#btnWindowClose");
        btnWindowMaxMin = (Button) rootAnchorPaneLogin.lookup("#btnWindowMaxMin");
        btnWindowSubtract = (Button) rootAnchorPaneLogin.lookup("#btnWindowSubtract");

        passwordField = (PasswordField) rootAnchorPaneLogin.lookup("#passwordField");
        textFieldLogin = (TextField) rootAnchorPaneLogin.lookup("#textFieldLogin");

        linkForgetPassword = (Hyperlink) rootAnchorPaneLogin.lookup("#linkForgetPassword");

        btnLogin = (Button) rootAnchorPaneLogin.lookup("#btnLogin");


        btnWindowMaxMin.setDisable(true);
        btnWindowSubtract.setDisable(true);
        btnWindowMaxMin.setVisible(false);
        btnWindowSubtract.setVisible(false);
    }

    private void initControlsLogic(){

        rootAnchorPaneLogin.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                btnLogin.fire();
            }
        });

        linkForgetPassword.setOnMouseClicked(mouseEvent -> {


            try {
                Desktop.getDesktop().browse(new URI("http://portal.koreanika.ru/forgotPassword"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });


        rootAnchorPaneLogin.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        rootAnchorPaneLogin.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }

    protected void setOnLoginClicked(EventHandler<ActionEvent> var1){
        btnLogin.setOnAction(var1);
    }

    protected void setOnCloseClicked(EventHandler<ActionEvent> var1){
        btnWindowClose.setOnAction(var1);
    }


    public LoginValues getLoginValues(){
        return new LoginValues(textFieldLogin.getText(), passwordField.getText());
    }

    protected void show(Scene parentScene){

        if(stage!= null && stage.isShowing())return;

        this.mainScene = parentScene;
        mainScene.getRoot().setDisable(true);


        stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(mainScene.getWindow());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
        stage.setOnHiding(windowEvent -> {
            mainScene.getRoot().setDisable(false);
        });
    }

    protected void close(){

        if(stage != null) stage.close();

    }


}
