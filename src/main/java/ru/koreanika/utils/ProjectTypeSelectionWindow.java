package ru.koreanika.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


import java.io.IOException;

public class ProjectTypeSelectionWindow {

    private static ProjectType projectType = ProjectType.TABLE_TYPE;

    public static Button show(Scene mainScene) {


        Stage selectTypeOfProjectStage;
        Scene selectTypeOfProjectScene;
        Window windowOwner = mainScene.getWindow();

        AnchorPane anchorPaneRoot = null;
        Button btnCreateProject = null;
        RadioButton radioBtnSketchProject;
        RadioButton radioBtnTableProject;
        ToggleGroup toggleGroup = new ToggleGroup();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ProjectTypeSelectionWindow.class.getResource("/fxmls/TypeOfProject.fxml"));
        try {
            anchorPaneRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (anchorPaneRoot != null) {

////            if(Main.appOwner.toUpperCase().equals("KOREANIKA")){
//            if(Main.appType == AppType.KOREANIKA || Main.appType == AppType.KOREANIKAMASTER){
//
//                anchorPaneRoot.getStylesheets().add(ProjectTypeSelectionWindow.class.getResource("/styles/colorsKoreanika.css").toExternalForm());
////            }else if(Main.appOwner.toUpperCase().equals("ZETTA")){
//            }else if(Main.appType == AppType.ZETTA){
//
//                anchorPaneRoot.getStylesheets().add(ProjectTypeSelectionWindow.class.getResource("/styles/colorsZetta.css").toExternalForm());
//            }else if(Main.appType == AppType.PROMEBEL){
//
//                anchorPaneRoot.getStylesheets().add(ProjectTypeSelectionWindow.class.getResource("/styles/colorsPromebel.css").toExternalForm());
//            }
//            anchorPaneRoot.getStylesheets().add(ProjectTypeSelectionWindow.class.getResource("/styles/rootTheme.css").toExternalForm());
//            anchorPaneRoot.getStylesheets().add(ProjectTypeSelectionWindow.class.getResource("/styles/projectTypeSelectionWindow.css").toExternalForm());


            selectTypeOfProjectScene = new Scene(anchorPaneRoot);

            btnCreateProject = (Button) anchorPaneRoot.lookup("#btnCreateProject");
            radioBtnSketchProject = (RadioButton) anchorPaneRoot.lookup("#radioBtnSketchProject");
            radioBtnTableProject = (RadioButton) anchorPaneRoot.lookup("#radioBtnTableProject");

            radioBtnSketchProject.setDisable(true);

            radioBtnSketchProject.setToggleGroup(toggleGroup);
            radioBtnTableProject.setToggleGroup(toggleGroup);
            if (projectType == ProjectType.SKETCH_TYPE) {
                radioBtnSketchProject.setSelected(true);
            } else if (projectType == ProjectType.TABLE_TYPE) {
                radioBtnTableProject.setSelected(true);
            }


            selectTypeOfProjectStage = new Stage();
            selectTypeOfProjectStage.setTitle("Создание проекта");
            selectTypeOfProjectStage.initOwner(windowOwner);
            selectTypeOfProjectStage.setScene(selectTypeOfProjectScene);
            selectTypeOfProjectStage.setX(windowOwner.getX() + windowOwner.getWidth() / 2 - selectTypeOfProjectScene.getWidth() / 2);
            selectTypeOfProjectStage.setY(windowOwner.getY() + windowOwner.getHeight() / 2 - selectTypeOfProjectScene.getHeight() / 2);
            selectTypeOfProjectStage.initModality(Modality.APPLICATION_MODAL);
            selectTypeOfProjectStage.setResizable(false);

            selectTypeOfProjectStage.show();

            radioBtnSketchProject.setOnMouseClicked(event -> {
                projectType = ProjectType.SKETCH_TYPE;
                //System.out.println(projectType);
            });

            radioBtnTableProject.setOnMouseClicked(event -> {
                projectType = ProjectType.TABLE_TYPE;
                //System.out.println(projectType);
            });

        }

        return btnCreateProject;

    }

    public static ProjectType getProjectType() {
        return projectType;
    }
}
