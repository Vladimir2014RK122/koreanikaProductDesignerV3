package utils;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class InfoMessage {
    private static AnchorPane rootAnchorPane;

    private static Pane paneNotification = new Pane();
    private static Label label = new Label();
    private static ImageView imageViewError = new ImageView();
    private static ImageView imageViewWarning = new ImageView();
    private static ImageView imageViewInfo = new ImageView();
    private static ImageView imageViewSuccess = new ImageView();

    private InfoMessage() {
    }

    private static Timer timer;
    private static TimerTask timerTask;
    private static Thread threadTimer;

    public static SequentialTransition sequentialTransition = new SequentialTransition();

    public static void initInfoMessage(AnchorPane anchorPane) {
        rootAnchorPane = anchorPane;

        paneNotification.getStylesheets().clear();
        paneNotification.getStylesheets().add(InfoMessage.class.getResource("/styles/notification.css").toString());

        imageViewError = new ImageView(new Image(InfoMessage.class.getResource("/styles/images/InfoImages/imageError.png").toString()));
        imageViewError.setFitWidth(30.0);
        imageViewError.setFitHeight(30.0);
        imageViewWarning = new ImageView(new Image(InfoMessage.class.getResource("/styles/images/InfoImages/imageWarning.png").toString()));
        imageViewWarning.setFitWidth(30.0);
        imageViewWarning.setFitHeight(30.0);
        imageViewSuccess = new ImageView(new Image(InfoMessage.class.getResource("/styles/images/InfoImages/imageSuccess.png").toString()));
        imageViewSuccess.setFitWidth(30.0);
        imageViewSuccess.setFitHeight(30.0);
        imageViewInfo = new ImageView(new Image(InfoMessage.class.getResource("/styles/images/InfoImages/imageInfo.png").toString()));
        imageViewInfo.setFitWidth(30.0);
        imageViewInfo.setFitHeight(30.0);
        //imageViewError.setPreserveRatio(true);

        paneNotification.setOnMousePressed(event -> {
            rootAnchorPane.getChildren().remove(paneNotification);
        });

        rootAnchorPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            rootAnchorPane.getChildren().remove(paneNotification);
        });
        rootAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            rootAnchorPane.getChildren().remove(paneNotification);
        });


    }

    private static void initNotification(MessageType messageType, double width, double height) {

        paneNotification.getChildren().clear();

        //paneNotification.setPrefSize(210, 60);
        paneNotification.setPrefSize(width, height);
        paneNotification.setTranslateY(rootAnchorPane.getScene().getHeight() - paneNotification.getPrefHeight() - 45);

        paneNotification.getChildren().add(label);
        label.setPrefWidth(width - 60);
        label.setWrapText(true);
        label.setPrefHeight(height);
        label.setAlignment(Pos.CENTER);
        label.setTranslateX(60);
        label.setTranslateY(0);
        label.setPadding(new Insets(5));

        if (messageType == MessageType.ERROR) {
            paneNotification.setId("errorPaneNotification");

            paneNotification.getChildren().add(imageViewError);
            imageViewError.setTranslateX(15);
            imageViewError.setTranslateY(15);

        } else if (messageType == MessageType.WARNING) {
            paneNotification.setId("warningPaneNotification");

            paneNotification.getChildren().add(imageViewWarning);
            imageViewWarning.setTranslateX(15);
            imageViewWarning.setTranslateY(15);

        } else if (messageType == MessageType.SUCCESS) {

            paneNotification.setId("successPaneNotification");

            paneNotification.getChildren().add(imageViewSuccess);
            imageViewSuccess.setTranslateX(15);
            imageViewSuccess.setTranslateY(15);

        } else if (messageType == MessageType.INFO) {

            paneNotification.setId("infoPaneNotification");

            paneNotification.getChildren().add(imageViewInfo);
            imageViewInfo.setTranslateX(15);
            imageViewInfo.setTranslateY(15);
        }
    }

    public static void showMessage(MessageType messageType, String message, Pane parent) {

        if (parent == null){
            parent = rootAnchorPane;
        }

        double duration = 5;
        if (message.length() < 50) {
            initNotification(messageType, 300, 60);
        }else{
            initNotification(messageType, 300, 150);
            duration = 10;
        }

        label.setText(message);

        parent.getChildren().remove(paneNotification);

        parent.getChildren().add(paneNotification);

        //start animation:
        sequentialTransition.stop();
        sequentialTransition.getChildren().clear();

        TranslateTransition showTransition = new TranslateTransition(Duration.millis(300), paneNotification);
        showTransition.setFromX(rootAnchorPane.getWidth());
        showTransition.setToX(rootAnchorPane.getWidth() - paneNotification.getPrefWidth() - 10);

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(duration));


        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), paneNotification);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        TranslateTransition hideTransition = new TranslateTransition(Duration.millis(300), paneNotification);
        hideTransition.setFromX(rootAnchorPane.getWidth() - paneNotification.getPrefWidth() - 50);
        hideTransition.setToX(rootAnchorPane.getWidth());

//        sequentialTransition.getChildren().addAll(showTransition, pauseTransition, hideTransition);
        sequentialTransition.getChildren().addAll(showTransition, pauseTransition, fadeTransition);
        sequentialTransition.play();


        Pane finalParent = parent;
        sequentialTransition.setOnFinished(event -> {
            finalParent.getChildren().remove(paneNotification);
        });

    }

    public static void stopMessage() {
        if (threadTimer != null) {
            threadTimer.interrupt();
        }
    }

    public enum MessageType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }
}
