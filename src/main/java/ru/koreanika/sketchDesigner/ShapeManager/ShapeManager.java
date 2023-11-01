package ru.koreanika.sketchDesigner.ShapeManager;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Features.FeatureType;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.project.Project;

import java.io.IOException;
import java.util.ArrayList;

public class ShapeManager {

    private static ShapeManager shapeManager;

    Scene sceneShapeManager;

    AnchorPane rootAnchorPane;
    SplitPane splitPaneMain;
    AnchorPane anchorPaneRootRightZone;
    SplitPane splitPaneRightZone;

    //top menu buttons:
    AnchorPane anchorPaneTopMenu;
    Button btnScaleMinus, btnScalePlus, btnRotateRight, btnRotateLeft, btnCenter, btnDelete;

    //workZone view elements:
    AnchorPane anchorPaneWorkZone;
    ScrollPane scrollPaneWorkZone;
    Pane workPane;
    Group workGroup = new Group();

    //Anchor zone:
    AnchorPane anchorPaneAnchorZone;
    TextField textFieldToLeft, textFieldToRight, textFieldToBottom, textFieldToTop;
    RadioButton radioBtnAnchorToShape, radioBtnAnchorToItem;
    ToggleGroup toggleGroupAnchorChoice = new ToggleGroup();
    boolean leftAnchorOk = true, rightAnchorOk = true, topAnchorOk = true, bottomAnchorOk = true;

    //items zone view elements:
    AnchorPane anchorPaneAccordionZone;
    ItemsAccordion itemsAccordion;

    //modelsZone view elements:
    AnchorPane anchorPaneRootModelsZone;
    ScrollPane scrollPaneModelsZone;
    ModelsZone modelsZone;

    Pane shapePane = new Pane();
    Polygon shapePolygon = new Polygon();

    Button btnSave;

    SketchShape shape;
    ArrayList<AdditionalFeature> shapeFeatureArrayList;
    ArrayList<AdditionalFeature> newFeatureArrayList = new ArrayList<>();
    private static AdditionalFeature selectedFeature;

    double shapeRotateAngle;

    private ShapeManager(SketchShape shape, ArrayList<AdditionalFeature> shapeFeatureArrayList, AdditionalFeature featureItem) {

        this.shape = shape;
        this.shapeFeatureArrayList = shapeFeatureArrayList;
        if (featureItem != null) newFeatureArrayList.add(featureItem);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/ShapeManager/shapeManager.fxml"));

        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/shapeManager.css").toExternalForm());

        sceneShapeManager = new Scene(rootAnchorPane, rootAnchorPane.getPrefWidth(), rootAnchorPane.getPrefHeight());

        initShape();
        initWorkPane();
        initZoom();
        initItemsList();
        initInfoModelsZone();
        initMenuButtons();
        initAnchorZone();
        initSaveBtn();

        shapePane.setPickOnBounds(false);

        for (AdditionalFeature feature : this.shapeFeatureArrayList) {
            shape.getChildren().remove(feature);
            shapePane.getChildren().add(feature);
            feature.setMouseTransparent(false);
        }
        if (featureItem != null) shapePane.getChildren().add(newFeatureArrayList.get(0));

        if (featureItem == null) {
            this.textFieldToLeft.setDisable(true);
            this.textFieldToRight.setDisable(true);
            this.textFieldToTop.setDisable(true);
            this.textFieldToBottom.setDisable(true);
        }
    }

    public Scene getSceneShapeManager() {
        return sceneShapeManager;
    }

    private void initWorkPane() {

        splitPaneMain = (SplitPane) rootAnchorPane.lookup("#splitPaneMain");
        anchorPaneWorkZone = (AnchorPane) splitPaneMain.getItems().get(1);
        scrollPaneWorkZone = (ScrollPane) anchorPaneWorkZone.lookup("#scrollPaneWorkZone");
        workPane = (Pane) scrollPaneWorkZone.getContent();

        workPane.setPrefSize(1000.0, 1000.0);
        scrollPaneWorkZone.setHvalue(0.5);
        scrollPaneWorkZone.setVvalue(0.5);
        scrollPaneWorkZone.setPannable(true);


        workPane.getChildren().add(workGroup);


        workGroup.getChildren().add(shapePane);
        shapePane.setTranslateX(workPane.getPrefWidth() / 2 - shapePane.getBoundsInLocal().getWidth() / 2);
        shapePane.setTranslateY(workPane.getPrefHeight() / 2 - shapePane.getBoundsInLocal().getHeight() / 2);


        workPane.setOnMouseClicked(event -> {
            selectElement(null);
        });

        shapePane.setOnDragEntered(event -> {
            System.out.println("Entered");
        });
        shapePane.setOnDragExited(event -> {
            System.out.println("Exited");
            System.out.println(shapePane.getOnDragDropped());
        });

        shapePane.setOnDragOver(event -> {

            Dragboard db = event.getDragboard();
            if (db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_TYPE) == null) return;
            if (db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_SUBTYPE) == null) return;

            event.acceptTransferModes(TransferMode.ANY);
        });

        shapePane.setOnDragDropped(event -> {

            Dragboard db = event.getDragboard();
            if (db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_TYPE) == null) return;
            if (db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_SUBTYPE) == null) return;

            FeatureType featureType = (FeatureType) db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_TYPE);
            int featureSubtype = (int) db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_SUBTYPE);


            if (featureType == FeatureType.SINK) {
                if (featureSubtype != Sink.SINK_TYPE_16) {
                    if (!shape.getMaterial().getAvailableSinkTypes().contains(new Integer(featureSubtype))) {
                        InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить для этого материала", null);
                        return;
                    }
                }
            } else if (featureType == FeatureType.GROOVES) {
                if (!shape.getMaterial().getAvailableGroovesTypes().contains(new Integer(featureSubtype))) {
                    InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить для этого материала", null);
                    return;
                }
            } else if (featureType == FeatureType.RODS) {
                if (!shape.getMaterial().getAvailableRodsTypes().contains(new Integer(featureSubtype))) {
                    InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить для этого материала", null);
                    return;
                }
            }
            AdditionalFeature feature = AdditionalFeature.createFeature(featureType, featureSubtype, shape);
            shapePane.getChildren().add(feature);
            feature.setTranslateX(event.getX());
            feature.setTranslateX(event.getY());
            newFeatureArrayList.add(feature);
            feature.select();

        });

    }

    private void initShape() {
        shapeRotateAngle = shape.getRotateAngle();
        shape.rotateShape(-shapeRotateAngle);
        for (int i = 0; i < shape.getPolygon().getPoints().size(); i++) {
            shapePolygon.getPoints().add(shape.getPolygon().getPoints().get(i));
        }
        shapePolygon.setFill(Color.BLUEVIOLET);
        //shapePolygon.setStroke(Color.BLACK);
        //shapePolygon.setStrokeType(StrokeType.INSIDE);
        //shapePolygon.setStrokeWidth(1.0);

        shapePane.getChildren().add(shapePolygon);
    }

    private void initZoom() {

        scrollPaneWorkZone.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                System.out.println("scroll x = " + event.getDeltaX() + " y = " + event.getDeltaY());
                System.out.println("workGroup.getScaleX() = " + workGroup.getScaleX());
                if (event.getDeltaY() > 0) {
                    if (workGroup.getScaleX() <= 5) {
                        workGroup.setScaleX(workGroup.getScaleX() + 0.1);
                        workGroup.setScaleY(workGroup.getScaleY() + 0.1);
                    }
                } else if (event.getDeltaY() < 0) {
                    if (workGroup.getScaleX() >= 0.3) {
                        workGroup.setScaleX(workGroup.getScaleX() - 0.1);
                        workGroup.setScaleY(workGroup.getScaleY() - 0.1);
                    }
                }
                event.consume();
            }
        });
    }

    private void initItemsList() {
        ItemsAccordion itemsAccordion = new ItemsAccordion();
        itemsAccordion.getAnchorPaneAccordion();


        splitPaneMain.getItems().set(0, itemsAccordion.getAnchorPaneAccordion());
    }

    private void initInfoModelsZone() {

        modelsZone = new ModelsZone();
        anchorPaneRootRightZone = (AnchorPane) splitPaneMain.getItems().get(2);
        splitPaneRightZone = (SplitPane) anchorPaneRootRightZone.lookup("#splitPaneRightZone");
        anchorPaneRootModelsZone = (AnchorPane) splitPaneRightZone.getItems().get(1);
        scrollPaneModelsZone = (ScrollPane) anchorPaneRootModelsZone.lookup("#scrollPaneModelsZone");
        scrollPaneModelsZone.setContent(modelsZone.getRootAnchorPane());

        scrollPaneModelsZone.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneModelsZone.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneModelsZone.heightProperty().addListener((observable, oldValue, newValue) -> {
            //if(newValue.doubleValue() > 440){
            modelsZone.getRootAnchorPane().setPrefHeight(newValue.doubleValue());
            //}
        });

        if (newFeatureArrayList.size() != 0 && newFeatureArrayList.get(0) instanceof Sink) {
            modelsZone.getRadioBtnSinkInstallType1().setVisible(true);
            modelsZone.getRadioBtnSinkInstallType2().setVisible(true);
        }


    }

    private void initAnchorZone() {

        anchorPaneAnchorZone = (AnchorPane) splitPaneRightZone.getItems().get(0);
        textFieldToLeft = (TextField) anchorPaneAnchorZone.lookup("#textFieldToLeft");
        textFieldToRight = (TextField) anchorPaneAnchorZone.lookup("#textFieldToRight");
        textFieldToBottom = (TextField) anchorPaneAnchorZone.lookup("#textFieldToBottom");
        textFieldToTop = (TextField) anchorPaneAnchorZone.lookup("#textFieldToTop");
        radioBtnAnchorToShape = (RadioButton) anchorPaneAnchorZone.lookup("#radioBtnAnchorToShape");
        radioBtnAnchorToItem = (RadioButton) anchorPaneAnchorZone.lookup("#radioBtnAnchorToItem");

        radioBtnAnchorToShape.setToggleGroup(toggleGroupAnchorChoice);
        radioBtnAnchorToItem.setToggleGroup(toggleGroupAnchorChoice);
        radioBtnAnchorToShape.setSelected(true);
        radioBtnAnchorToItem.setDisable(true);

        radioBtnAnchorToShape.setVisible(false);
        radioBtnAnchorToItem.setVisible(false);

        textFieldToLeft.textProperty().addListener((observable, oldValue, newValue) -> leftAnchorOk = checkLeftAnchor(newValue));
        textFieldToLeft.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (textFieldToLeft.focusedProperty().get() == false) {

                if (leftAnchorOk == false) {
                    textFieldToLeft.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
                }
            } else {
                textFieldToLeft.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
                textFieldToRight.setText("" + (int) ((shapePolygon.getBoundsInLocal().getWidth() - (selectedFeature.getTranslateX() + selectedFeature.getBoundsInLocal().getWidth())) / Project.getCommonShapeScale()));
            }
        });
        textFieldToLeft.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                shapePane.requestFocus();
                if (leftAnchorOk == false) {
                    textFieldToLeft.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
                } else {
                    selectedFeature.setTranslateX(Double.parseDouble(textFieldToLeft.getText()) * Project.getCommonShapeScale());
                    textFieldToRight.setText("" + (int) ((shapePolygon.getBoundsInLocal().getWidth() - (selectedFeature.getTranslateX() + selectedFeature.getBoundsInLocal().getWidth())) / Project.getCommonShapeScale()));
                }
            }
        });


        textFieldToRight.textProperty().addListener((observable, oldValue, newValue) -> rightAnchorOk = checkRightAnchor(newValue));
        textFieldToRight.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (textFieldToRight.focusedProperty().get() == false) {

                if (rightAnchorOk == false) {
                    textFieldToRight.setText("" + (int) ((shapePolygon.getBoundsInLocal().getWidth() - (selectedFeature.getTranslateX() + selectedFeature.getBoundsInLocal().getWidth())) / Project.getCommonShapeScale()));
                } else {
                    //selectedFeature.setTranslateX(shapePane.getBoundsInLocal().getWidth() - Double.parseDouble(textFieldToRight.getText()) * ProjectHandler.getCommonShapeScale() + selectedFeature.getBoundsInLocal().getWidth());
                }
            } else {
                textFieldToRight.setText("" + (int) ((shapePolygon.getBoundsInLocal().getWidth() - (selectedFeature.getTranslateX() + selectedFeature.getBoundsInLocal().getWidth())) / Project.getCommonShapeScale()));
                textFieldToLeft.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
                //textFieldToLeft.setText("");
            }
        });
        textFieldToRight.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                shapePane.requestFocus();
                if (rightAnchorOk == false) {
                    textFieldToRight.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
                } else {
                    selectedFeature.setTranslateX(shapePolygon.getBoundsInLocal().getWidth() - (Double.parseDouble(textFieldToRight.getText()) * Project.getCommonShapeScale() + selectedFeature.getBoundsInLocal().getWidth()));
                    textFieldToLeft.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
                }
            }
        });


        textFieldToTop.textProperty().addListener((observable, oldValue, newValue) -> topAnchorOk = checkTopAnchor(newValue));
        textFieldToTop.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (textFieldToTop.focusedProperty().get() == false) {

                if (topAnchorOk == false) {
                    textFieldToTop.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
                }
            } else {
                textFieldToTop.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
                textFieldToBottom.setText("" + (int) ((shapePolygon.getBoundsInLocal().getHeight() - ((selectedFeature.getTranslateY() + selectedFeature.getBoundsInLocal().getHeight()))) / Project.getCommonShapeScale()));
            }
        });
        textFieldToTop.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                shapePane.requestFocus();
                if (topAnchorOk == false) {
                    textFieldToTop.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
                } else {
                    selectedFeature.setTranslateY(Double.parseDouble(textFieldToTop.getText()) * Project.getCommonShapeScale());
                    textFieldToBottom.setText("" + (int) ((shapePolygon.getBoundsInLocal().getHeight() - ((selectedFeature.getTranslateY() + selectedFeature.getBoundsInLocal().getHeight()))) / Project.getCommonShapeScale()));
                }
            }
        });


        textFieldToBottom.textProperty().addListener((observable, oldValue, newValue) -> bottomAnchorOk = checkBottomAnchor(newValue));
        textFieldToBottom.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (textFieldToBottom.focusedProperty().get() == false) {

                if (bottomAnchorOk == false) {
                    textFieldToBottom.setText("" + (int) ((shapePolygon.getBoundsInLocal().getHeight() - ((selectedFeature.getTranslateY() + selectedFeature.getBoundsInLocal().getHeight()))) / Project.getCommonShapeScale()));
                } else {
                    //selectedFeature.setTranslateX(shapePane.getBoundsInLocal().getWidth() - Double.parseDouble(textFieldToRight.getText()) * ProjectHandler.getCommonShapeScale() + selectedFeature.getBoundsInLocal().getWidth());
                }
            } else {
                textFieldToBottom.setText("" + (int) ((shapePolygon.getBoundsInLocal().getHeight() - ((selectedFeature.getTranslateY() + selectedFeature.getBoundsInLocal().getHeight()))) / Project.getCommonShapeScale()));
                textFieldToTop.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
                //textFieldToLeft.setText("");
            }
        });
        textFieldToBottom.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                shapePane.requestFocus();
                if (bottomAnchorOk == false) {
                    textFieldToBottom.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
                } else {
                    selectedFeature.setTranslateY(shapePolygon.getBoundsInLocal().getHeight() - (Double.parseDouble(textFieldToBottom.getText()) * Project.getCommonShapeScale() + selectedFeature.getBoundsInLocal().getHeight()));
                    textFieldToTop.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
                }
            }
        });
    }

    private boolean checkLeftAnchor(String text) {
        int value = 0;
        boolean result = true;
        try {
            value = Integer.parseInt(text);
            textFieldToLeft.setStyle("-fx-text-fill:#A8A8A8");
        } catch (NumberFormatException ex) {
            textFieldToLeft.setStyle("-fx-text-fill:red");
            result = false;
        }
        return result;
    }

    private boolean checkRightAnchor(String text) {
        int value = 0;
        boolean result = true;
        try {
            value = Integer.parseInt(text);
            textFieldToRight.setStyle("-fx-text-fill:#A8A8A8");
        } catch (NumberFormatException ex) {
            textFieldToRight.setStyle("-fx-text-fill:red");
            result = false;
        }
        return result;
    }

    private boolean checkTopAnchor(String text) {
        int value = 0;
        boolean result = true;
        try {
            value = Integer.parseInt(text);
            textFieldToTop.setStyle("-fx-text-fill:#A8A8A8");
        } catch (NumberFormatException ex) {
            textFieldToTop.setStyle("-fx-text-fill:red");
            result = false;
        }
        return result;
    }

    private boolean checkBottomAnchor(String text) {
        int value = 0;
        boolean result = true;
        try {
            value = Integer.parseInt(text);
            textFieldToBottom.setStyle("-fx-text-fill:#A8A8A8");
        } catch (NumberFormatException ex) {
            textFieldToBottom.setStyle("-fx-text-fill:red");
            result = false;
        }
        return result;
    }

    private void initMenuButtons() {
        anchorPaneTopMenu = (AnchorPane) rootAnchorPane.lookup("#anchorPaneTopMenu");
        btnScaleMinus = (Button) anchorPaneTopMenu.lookup("#btnScaleMinus");
        btnScalePlus = (Button) anchorPaneTopMenu.lookup("#btnScalePlus");
        btnRotateRight = (Button) anchorPaneTopMenu.lookup("#btnRotateRight");
        btnRotateLeft = (Button) anchorPaneTopMenu.lookup("#btnRotateLeft");
        btnCenter = (Button) anchorPaneTopMenu.lookup("#btnCenter");
        btnDelete = (Button) anchorPaneTopMenu.lookup("#btnDelete");

        btnRotateRight.setOnMouseClicked(event -> {
            rotateElement(selectedFeature, 45);
        });
        btnRotateLeft.setOnMouseClicked(event -> {
            rotateElement(selectedFeature, -45);
        });
        btnDelete.setOnMouseClicked(event -> {
            deleteElement(selectedFeature);
            selectElement(null);
        });
        btnScaleMinus.setOnMouseClicked(event -> {
            workGroup.setScaleX(workGroup.getScaleX() - 0.1);
            workGroup.setScaleY(workGroup.getScaleY() - 0.1);
        });
        btnScalePlus.setOnMouseClicked(event -> {
            workGroup.setScaleX(workGroup.getScaleX() + 0.1);
            workGroup.setScaleY(workGroup.getScaleY() + 0.1);
        });
        btnCenter.setOnMouseClicked(event -> {
            scrollPaneWorkZone.setHvalue(0.5);
            scrollPaneWorkZone.setVvalue(0.5);
        });
    }

    private void initSaveBtn() {
        btnSave = (Button) rootAnchorPane.lookup("#btnSave");
        System.out.println(btnSave);

        btnSave.setOnMouseClicked(event -> {

            for (AdditionalFeature feature : shapeFeatureArrayList) {
                newFeatureArrayList.add(feature);
            }
            shapeFeatureArrayList.clear();

            for (AdditionalFeature feature : newFeatureArrayList) {
                feature.deselect();
                feature.setMouseTransparent(true);
                shape.addFeature(feature);
            }

            shape.rotateShape(shapeRotateAngle);
            ((Stage) sceneShapeManager.getWindow()).close();

        });
    }

    public static void selectElement(AdditionalFeature feature) {
        selectedFeature = feature;
        shapeManager.modelsZone.changeFeature(selectedFeature);

        if (selectedFeature == null) {
            shapeManager.textFieldToLeft.setDisable(true);
            shapeManager.textFieldToRight.setDisable(true);
            shapeManager.textFieldToTop.setDisable(true);
            shapeManager.textFieldToBottom.setDisable(true);

            shapeManager.textFieldToLeft.setText("");
            shapeManager.textFieldToRight.setText("");
            shapeManager.textFieldToTop.setText("");
            shapeManager.textFieldToBottom.setText("");
        } else {
            shapeManager.textFieldToLeft.setDisable(false);
            shapeManager.textFieldToRight.setDisable(false);
            shapeManager.textFieldToTop.setDisable(false);
            shapeManager.textFieldToBottom.setDisable(false);

            shapeManager.textFieldToLeft.setText("" + (int) (selectedFeature.getTranslateX() / Project.getCommonShapeScale()));
            shapeManager.textFieldToRight.setText("" + (int) ((shapeManager.shapePolygon.getBoundsInLocal().getWidth() - (selectedFeature.getTranslateX() + selectedFeature.getBoundsInLocal().getWidth())) / Project.getCommonShapeScale()));
            shapeManager.textFieldToTop.setText("" + (int) (selectedFeature.getTranslateY() / Project.getCommonShapeScale()));
            shapeManager.textFieldToBottom.setText("" + (int) ((shapeManager.shapePolygon.getBoundsInLocal().getHeight() - ((selectedFeature.getTranslateY() + selectedFeature.getBoundsInLocal().getHeight()))) / Project.getCommonShapeScale()));
        }

        for (AdditionalFeature f : shapeManager.shapeFeatureArrayList) {
            if (f == selectedFeature) continue;
            f.deselect();
        }
        for (AdditionalFeature f : shapeManager.newFeatureArrayList) {
            if (f == selectedFeature) continue;
            f.deselect();
        }


//        if(feature != null){
//            selectedFeature.select();
//        }
    }

    private void deleteElement(AdditionalFeature feature) {

        shapeManager.shapeFeatureArrayList.remove(feature);
        shapeManager.newFeatureArrayList.remove(feature);
        shapePane.getChildren().remove(feature);
        if (selectedFeature == feature) selectedFeature = null;
    }

    private void rotateElement(AdditionalFeature feature, double angle) {
        if (feature == null) return;
        feature.rotate(angle);
    }

    public static void show(Scene parentScene, SketchShape shape, AdditionalFeature feature) {

        //if(shapeManager == null){
        shapeManager = new ShapeManager(shape, shape.getFeaturesList(), feature);
        //}


        Stage shapeManagerStage = new Stage();
        shapeManagerStage.setTitle("Настройка дополнительных элементов");
        shapeManagerStage.initOwner(parentScene.getWindow());
        shapeManagerStage.setScene(shapeManager.getSceneShapeManager());
        shapeManagerStage.setX(parentScene.getWindow().getX() + parentScene.getWindow().getWidth() / 2 - shapeManager.getSceneShapeManager().getWidth() / 2);
        shapeManagerStage.setY(parentScene.getWindow().getY() + parentScene.getWindow().getHeight() / 2 - shapeManager.getSceneShapeManager().getHeight() / 2);
        shapeManagerStage.initModality(Modality.APPLICATION_MODAL);
        shapeManagerStage.setResizable(true);
        shapeManagerStage.show();


        shapeManager.getSceneShapeManager().getWindow().setOnCloseRequest(event -> {
            System.out.println("close Shape Manager");

            shapeManager.newFeatureArrayList.clear();
            for (AdditionalFeature featureItem : shapeManager.shapeFeatureArrayList) {
                shapeManager.newFeatureArrayList.add(featureItem);
            }
            shapeManager.shapeFeatureArrayList.clear();

            for (AdditionalFeature featureItem : shapeManager.newFeatureArrayList) {
                featureItem.deselect();
                featureItem.setMouseTransparent(true);
                shape.addFeature(featureItem);
            }
        });

        if (feature != null) shapeManager.newFeatureArrayList.get(0).select();
    }

    public static ShapeManager getShapeManager() {
        return shapeManager;
    }
}
