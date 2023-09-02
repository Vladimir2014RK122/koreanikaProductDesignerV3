package utils;

import cutDesigner.CutPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;


import java.io.IOException;

public class LoadingProgressDialog {

    Scene scene;
    Scene progressBarScene;

    FXMLLoader fxmlLoader;
    AnchorPane root;

    Label label;
    ProgressBar progressBar;
    Button btnStop;

    public LoadingProgressDialog(Scene scene) {
        this.scene = scene;

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/ProgressDialog.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        progressBarScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

        label = (Label) root.lookup("#label");
        progressBar = (ProgressBar) root.lookup("#progressBar");
        btnStop = (Button) root.lookup("#btnStop");

        btnStop.setOnAction(event -> CutPane.setExternalStopAutoCutting(true));
    }

    public Button getBtnStop() {
        return btnStop;
    }



    public void show() {

        Window primaryStage = Main.getMainScene().getWindow();

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Сохранение PDF");
        newWindow.setScene(progressBarScene);

        newWindow.initStyle(StageStyle.UNDECORATED);
        //newWindow.initStyle(StageStyle.UTILITY);
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        newWindow.setX(primaryStage.getX() + scene.getWidth() / 2 - root.getPrefWidth() / 2);
        newWindow.setY(primaryStage.getY() + scene.getHeight() / 2 - root.getPrefHeight() / 2);

        newWindow.setResizable(false);
        newWindow.show();
        newWindow.toFront();


    }

    public void close() {
        ((Stage) progressBarScene.getWindow()).close();
    }

    public void setMessage(String message) {
        label.setText(message);
    }

    public void setValue(double value) {
        progressBar.setProgress(value);
    }

    public double getValue() {
        return progressBar.getProgress();
    }
}

