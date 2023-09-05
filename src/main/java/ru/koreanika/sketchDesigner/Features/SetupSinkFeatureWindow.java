package ru.koreanika.sketchDesigner.Features;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.utils.ProjectHandler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SetupSinkFeatureWindow {
    Scene scene;

    AdditionalFeature feature;
    SketchShape shape;

    AnchorPane rootAnchorPane;
    ImageView imageViewInfo;
    TextField textFieldLeftAnchor, textFieldRightAnchor, textFieldTopAnchor, textFieldBottomAnchor;
    ToggleGroup toggleGroup = new ToggleGroup();
    RadioButton radioBtnMod1, radioBtnMod2, radioBtnMod3, radioBtnMod4, radioBtnMod5, radioBtnMod6;
    Button btnApply, btnCancel;

    boolean leftAnchorOk = true, rightAnchorOk = true, topAnchorOk = true, bottomAnchorOk = true;

    private SetupSinkFeatureWindow(Sink feature, SketchShape shape) {
        this.feature = feature;
        this.shape = shape;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchShapeFeatureSetup.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        scene = new Scene(rootAnchorPane, rootAnchorPane.getPrefWidth(), rootAnchorPane.getPrefHeight());

        imageViewInfo = (ImageView) rootAnchorPane.lookup("#imageViewInfo");
        textFieldLeftAnchor = (TextField) rootAnchorPane.lookup("#textFieldLeftAnchor");
        textFieldRightAnchor = (TextField) rootAnchorPane.lookup("#textFieldRightAnchor");
        textFieldTopAnchor = (TextField) rootAnchorPane.lookup("#textFieldTopAnchor");
        textFieldBottomAnchor = (TextField) rootAnchorPane.lookup("#textFieldBottomAnchor");
        radioBtnMod1 = (RadioButton) rootAnchorPane.lookup("#radioBtnMod1");
        radioBtnMod2 = (RadioButton) rootAnchorPane.lookup("#radioBtnMod2");
        radioBtnMod3 = (RadioButton) rootAnchorPane.lookup("#radioBtnMod3");
        radioBtnMod4 = (RadioButton) rootAnchorPane.lookup("#radioBtnMod4");
        radioBtnMod5 = (RadioButton) rootAnchorPane.lookup("#radioBtnMod5");
        radioBtnMod6 = (RadioButton) rootAnchorPane.lookup("#radioBtnMod6");
        btnApply = (Button) rootAnchorPane.lookup("#btnApply");
        btnCancel = (Button) rootAnchorPane.lookup("#btnCancel");

        radioBtnMod1.setToggleGroup(toggleGroup);
        radioBtnMod2.setToggleGroup(toggleGroup);
        radioBtnMod3.setToggleGroup(toggleGroup);
        radioBtnMod4.setToggleGroup(toggleGroup);
        radioBtnMod5.setToggleGroup(toggleGroup);
        radioBtnMod6.setToggleGroup(toggleGroup);

        imageViewInfo.setImage(feature.getInfoImage().getImage());


        radioBtnMod1.setSelected(true);

        for (int i = 1; i <= 6; i++) {
            ((RadioButton) rootAnchorPane.lookup("#radioBtnMod" + i)).setVisible(false);
        }

        for (int i = 1; i <= feature.getModels().size(); i++) {
            ((RadioButton) rootAnchorPane.lookup("#radioBtnMod" + i)).setVisible(true);
        }
        int i = 1;
        for (Map.Entry<String, ArrayList<Integer>> entry : feature.getModels().entrySet()) {
            String text = entry.getKey() + "\t";
            //System.out.println("entry.getValue().size()" + entry.getValue().size());
            for (int j = 0; j < entry.getValue().size(); j++) {
                text += entry.getValue().get(j);
                if (j != entry.getValue().size() - 1) text += "x";
            }
            ((RadioButton) rootAnchorPane.lookup("#radioBtnMod" + i)).setText(text);

            i++;
        }


        btnApply.setOnMouseClicked(event -> btnApplyClicked(event));
        btnCancel.setOnMouseClicked(event -> btnCancelClicked(event));

        textFieldLeftAnchor.textProperty().addListener((observable, oldValue, newValue) -> leftAnchorOk = checkTextFieldLeft(newValue));
        textFieldRightAnchor.textProperty().addListener((observable, oldValue, newValue) -> rightAnchorOk = checkTextFieldRight(newValue));
        textFieldTopAnchor.textProperty().addListener((observable, oldValue, newValue) -> topAnchorOk = checkTextFieldTop(newValue));
        textFieldBottomAnchor.textProperty().addListener((observable, oldValue, newValue) -> bottomAnchorOk = checkTextFieldBottom(newValue));
    }

    private boolean checkTextFieldLeft(String newValue) {
        double value = 0;
        double maxValue = shape.getPolygon().getBoundsInLocal().getWidth() - feature.getBoundsInLocal().getWidth();
        double minValue = 0;
        boolean result = true;

        if (!newValue.matches("")) {
            textFieldRightAnchor.setText("");
            rightAnchorOk = true;
        }

        try {
            value = Double.parseDouble(newValue) * ProjectHandler.getCommonShapeScale();
            textFieldLeftAnchor.setStyle("-fx-text-fill: #A8A8A8");

            if (value < minValue || value > maxValue) {
                textFieldLeftAnchor.setStyle("-fx-text-fill: red");
                result = false;
            } else {
                textFieldLeftAnchor.setStyle("-fx-text-fill: #A8A8A8");
            }

        } catch (NumberFormatException ex) {
            textFieldLeftAnchor.setStyle("-fx-text-fill: red");
            result = false;
        }

        return result;
    }

    private boolean checkTextFieldRight(String newValue) {
        double value = 0;
        double maxValue = shape.getPolygon().getBoundsInLocal().getWidth() - feature.getBoundsInLocal().getWidth();
        double minValue = 0;
        boolean result = true;

        if (!newValue.matches("")) {
            textFieldLeftAnchor.setText("");
            leftAnchorOk = true;
        }

        try {
            value = Double.parseDouble(newValue) * ProjectHandler.getCommonShapeScale();
            textFieldRightAnchor.setStyle("-fx-text-fill: #A8A8A8");

            if (value < minValue || value > maxValue) {
                textFieldRightAnchor.setStyle("-fx-text-fill: red");
                result = false;
            } else {
                textFieldRightAnchor.setStyle("-fx-text-fill: #A8A8A8");
            }

        } catch (NumberFormatException ex) {
            textFieldRightAnchor.setStyle("-fx-text-fill: red");
            result = false;
        }

        return result;
    }

    private boolean checkTextFieldTop(String newValue) {
        double value = 0;
        double maxValue = shape.getPolygon().getBoundsInLocal().getHeight() - feature.getBoundsInLocal().getHeight();
        double minValue = 0;
        boolean result = true;

        if (!newValue.matches("")) {
            textFieldBottomAnchor.setText("");
            bottomAnchorOk = true;
        }

        try {
            value = Double.parseDouble(newValue) * ProjectHandler.getCommonShapeScale();
            textFieldTopAnchor.setStyle("-fx-text-fill: #A8A8A8");

            if (value < minValue || value > maxValue) {
                textFieldTopAnchor.setStyle("-fx-text-fill: red");
                result = false;
            } else {
                textFieldTopAnchor.setStyle("-fx-text-fill: #A8A8A8");
            }

        } catch (NumberFormatException ex) {
            textFieldTopAnchor.setStyle("-fx-text-fill: red");
            result = false;
        }

        return result;
    }

    private boolean checkTextFieldBottom(String newValue) {
        double value = 0;
        double maxValue = shape.getPolygon().getBoundsInLocal().getHeight() - feature.getBoundsInLocal().getHeight();
        double minValue = 0;
        boolean result = true;

        if (!newValue.matches("")) {
            textFieldTopAnchor.setText("");
            topAnchorOk = true;
        }

        try {
            value = Double.parseDouble(newValue) * ProjectHandler.getCommonShapeScale();
            textFieldBottomAnchor.setStyle("-fx-text-fill: #A8A8A8");

            if (value < minValue || value > maxValue) {
                textFieldBottomAnchor.setStyle("-fx-text-fill: red");
                result = false;
            } else {
                textFieldBottomAnchor.setStyle("-fx-text-fill: #A8A8A8");
            }

        } catch (NumberFormatException ex) {
            textFieldBottomAnchor.setStyle("-fx-text-fill: red");
            result = false;
        }

        return result;
    }

    private void btnCancelClicked(MouseEvent event) {
        ((Stage) (scene.getWindow())).close();
    }

    private void btnApplyClicked(MouseEvent event) {

        if (checkCorrectData() == false) return;

        double x = 0;
        double y = 0;

        if (!textFieldLeftAnchor.getText().matches("")) {
            x = Double.parseDouble(textFieldLeftAnchor.getText()) * ProjectHandler.getCommonShapeScale();
        }
        if (!textFieldRightAnchor.getText().matches("")) {
            x = shape.getPolygon().getBoundsInLocal().getWidth() - feature.getBoundsInLocal().getWidth() - Double.parseDouble(textFieldRightAnchor.getText()) * ProjectHandler.getCommonShapeScale();
        }

        if (!textFieldTopAnchor.getText().matches("")) {
            y = Double.parseDouble(textFieldTopAnchor.getText()) * ProjectHandler.getCommonShapeScale();
        }
        if (!textFieldBottomAnchor.getText().matches("")) {
            y = shape.getPolygon().getBoundsInLocal().getHeight() - feature.getBoundsInLocal().getHeight() - Double.parseDouble(textFieldBottomAnchor.getText()) * ProjectHandler.getCommonShapeScale();
        }

        String model = "";

        feature.setTranslateX(x);
        feature.setTranslateY(y);
        shape.addFeature(feature);
        ((Stage) (scene.getWindow())).close();
    }

    private boolean checkCorrectData() {
        return leftAnchorOk && rightAnchorOk && topAnchorOk && bottomAnchorOk;
    }

    public Scene getScene() {
        return scene;
    }

    public static void show(Scene mainScene, Sink feature, SketchShape shape) {
        SetupSinkFeatureWindow setupSinkFeatureWindow = new SetupSinkFeatureWindow(feature, shape);

        Stage stage = new Stage();
        stage.setTitle("Выберите модель и расположение");
        stage.initOwner(mainScene.getWindow());
        stage.setScene(setupSinkFeatureWindow.getScene());
        stage.setX(mainScene.getWindow().getX() + mainScene.getWindow().getWidth() / 2 - setupSinkFeatureWindow.getScene().getWindow().getWidth() / 2);
        stage.setY(mainScene.getWindow().getY() + mainScene.getWindow().getHeight() / 2 - setupSinkFeatureWindow.getScene().getWindow().getHeight() / 2);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();

//        if(ProjectHandler.getDefaultMaterial() != null){
//            listViewInProject.getItems().clear();
//            choiceBoxDefault.getItems().clear();
//            for(Material material : ProjectHandler.getMaterialsListInProject()){
//                listViewInProject.getItems().add(new MaterialCellItem(material));
//                choiceBoxDefault.getItems().add(material.getName());
//            }
//            choiceBoxDefault.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getName());
//        }
//
//        edgeHeight = ProjectHandler.getDefaultEdgeHeight();
//        borderHeight = ProjectHandler.getDefaultBorderHeight();
//
//        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
//        textFieldBorderHeight.setText(String.valueOf(borderHeight));
//
//        ((Stage)(materialScene.getWindow())).setOnCloseRequest(event -> {
//            System.out.println("close");
//            if(ProjectHandler.getDefaultMaterial() == null){
//                show(mainScene);
//            }
//        });
    }
}
