package ru.koreanika.utils.updater;

import ru.koreanika.utils.Main;
import ru.koreanika.PortalClient.Update.UpdateService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.stage.StageStyle;


import java.io.*;

import java.util.Properties;

public class UpdateManager{

    private static String UPDATE_PROPERTIES_FILENAME = "updater.properties";

//    private static String availableVersion = null;

    private static UpdateManager updateManager;
    private static Properties properties;

    //private static Scene mainScene;
    private static Stage updaterStage;
    private static Scene updaterScene;

    private static AnchorPane rootAnchorPane;
    private static Button btnWindowClose;
    private static Button btnDownload;
//    private static ProgressBar progressBar;
    private static ProgressIndicator progressIndicator;
    //private static Button btnApplyNewURI;
    //private static TextField textFieldURI;
    private static Label labelInfo, labelThisVersion;

    private static boolean showedAlertForUpdate = false;

    private static boolean showing = false;

    private double xOffset = 0;
    private double yOffset = 0;

    private UpdateManager() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/updateManager.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
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

        updaterScene = new Scene(root);
        updaterScene.setFill(Color.TRANSPARENT);

        initControls();
        initControlsLogic();
        initProperties();

        labelThisVersion.setText(Main.actualAppVersion);
    }

    private void initControls() {

        btnWindowClose = (Button) rootAnchorPane.lookup("#btnWindowClose");
//        btnUpdate = (Button) rootAnchorPane.lookup("#btnUpdateInfo");
        btnDownload = (Button) rootAnchorPane.lookup("#btnDownload");
        //btnApplyNewURI = (Button) rootAnchorPane.lookup("#btnApplyNewURI");
        //textFieldURI = (TextField) rootAnchorPane.lookup("#textFieldURI");
        labelInfo = (Label) rootAnchorPane.lookup("#labelInfo");
        labelThisVersion = (Label) rootAnchorPane.lookup("#labelThisVersion");
//        labelProgress= (Label) rootAnchorPane.lookup("#labelProgress");
//
//        progressBar = (ProgressBar) rootAnchorPane.lookup("#progressBar");
        progressIndicator = (ProgressIndicator) rootAnchorPane.lookup("#progressIndicator");
        progressIndicator.setVisible(false);
    }

    public void setAvailableVersion(String newVersion){
        labelInfo.setText(newVersion);
    }

    public static ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void disableControls(boolean disable){
        btnDownload.setDisable(true);
//        btnUpdate.setDisable(true);
    }

    private void initControlsLogic() {

        rootAnchorPane.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        rootAnchorPane.setOnMouseDragged((MouseEvent event) -> {
            updaterStage.setX(event.getScreenX() - xOffset);
            updaterStage.setY(event.getScreenY() - yOffset);
        });

        btnWindowClose.setOnAction((e) -> {
            updaterStage.close();
            System.out.println("close update manager");
            //event.consume();

            showing = false;
        });

        btnDownload.setOnMouseClicked(event -> {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean result = UpdateService.getInstance().downloadUpdateFile();

                        if(result){
                            Platform.runLater(()->((Stage)(updaterScene.getWindow())).close());
                            showing = false;
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0);
                    btnDownload.setDisable(false);

                }
            }).start();
        });
    }

    public static boolean isShowedAlertForUpdate() {
        return showedAlertForUpdate;
    }

    public static void setShowedAlertForUpdate(boolean showedAlertForUpdate) {

        UpdateManager.showedAlertForUpdate = showedAlertForUpdate;
    }

    public static UpdateManager getUpdateManager() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        return updateManager;
    }

    public static void show(Scene mainScene) {

        if(showing) return;



        updateManager = getUpdateManager();

        updaterStage = new Stage();
        updaterStage.setTitle("Обновление");
        updaterStage.initOwner(mainScene.getWindow());
        updaterStage.setScene(updaterScene);

        updaterStage.initStyle(StageStyle.TRANSPARENT);
        updaterScene.setFill(Color.TRANSPARENT);
//        updaterStage.setX(mainScene.getWindow().getX() + mainScene.getWindow().getWidth() / 2 - updaterScene.getWidth() / 2);
//        updaterStage.setY(mainScene.getWindow().getY() + mainScene.getWindow().getHeight() / 2 - updaterScene.getHeight() / 2);
        updaterStage.initModality(Modality.APPLICATION_MODAL);

        updaterStage.setResizable(false);
        updaterStage.show();


        ((Stage) (updaterScene.getWindow())).setOnCloseRequest(event -> {
            System.out.println("close update manager");
            //event.consume();

            showing = false;
        });

        showing = true;
    }

    private static void initProperties(){
        FileInputStream fis = null;
        properties = new Properties();
        try {
            fis = new FileInputStream(UPDATE_PROPERTIES_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        if(properties == null) initProperties();
        return properties;
    }

    public static void updateProperty(String key, String value) {
        properties.setProperty(key, value);

        try (OutputStream output = new FileOutputStream(UPDATE_PROPERTIES_FILENAME)) {
            // save properties to project root folder
            properties.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }

        initProperties();
        UpdateChecker.restartCheckerThread();
    }

    public static void updateProperties() {
        initProperties();
    }

    public static boolean needToUpdate(){
        return (UpdateService.getInstance().getAvailableVersion() != null && UpdateService.getInstance().getAvailableVersion().equals(Main.actualAppVersion));
    }
}
