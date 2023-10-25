package ru.koreanika.sketchDesigner.Edge;

import ru.koreanika.Common.Material.Material;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.project.Project;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;


public class EdgeManager {

    ArrayList<SketchEdge> sketchEdgeList;

    private static final int MATERIAL_TYPE_1 = 0;
    private static final int MATERIAL_TYPE_2 = 1;
    int materialType = MATERIAL_TYPE_1;

    Scene mainScene;
    Scene edgeManagerScene;
    AnchorPane anchorPaneRoot;
    TreeView<String> treeViewEdge;
    TreeView<String> treeViewBorder;
    Button btnApply, btnDelete;

    Label labelBorder1 = new Label();
    Label labelBorder2 = new Label();
    ChoiceBox<String> choiceBoxBorderSideCut;
    RadioButton radioBtnBorderCut1, radioBtnBorderCut2, radioBtnBorderCut3, radioBtnBorderCut4;
    RadioButton radioBtnBorderSideCut1, radioBtnBorderSideCut2;
    ToggleGroup toggleGroupBorderCut = new ToggleGroup();
    ToggleGroup toggleGroupBorderSideCut = new ToggleGroup();

    CheckBox checkBoxEdgeStoneHemOrLeakGroove = new CheckBox("");


    Group borderSettingsGroup = new Group();
    Group edgesSettingsGroup = new Group();

    public EdgeManager(ArrayList<SketchEdge> sketchEdgeList) {

        this.sketchEdgeList = sketchEdgeList;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/edgeManager.fxml"));
        try {
            anchorPaneRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        edgeManagerScene = new Scene(anchorPaneRoot, anchorPaneRoot.getPrefWidth(), anchorPaneRoot.getPrefHeight());

        btnApply = (Button) anchorPaneRoot.lookup("#btnApply");
        btnDelete = (Button) anchorPaneRoot.lookup("#btnDelete");
        treeViewEdge = (TreeView<String>) anchorPaneRoot.lookup("#treeViewEdge");
        treeViewBorder = (TreeView<String>) anchorPaneRoot.lookup("#treeViewBorder");

        labelBorder1 = (Label) anchorPaneRoot.lookup("#labelBorder1");
        labelBorder2 = (Label) anchorPaneRoot.lookup("#labelBorder2");

        choiceBoxBorderSideCut = (ChoiceBox<String>) anchorPaneRoot.lookup("#choiceBoxBorderSideCut");
        radioBtnBorderCut1 = (RadioButton) anchorPaneRoot.lookup("#radioBtnBorderCut1");
        radioBtnBorderCut2 = (RadioButton) anchorPaneRoot.lookup("#radioBtnBorderCut2");
        radioBtnBorderCut3 = (RadioButton) anchorPaneRoot.lookup("#radioBtnBorderCut3");
        radioBtnBorderCut4 = (RadioButton) anchorPaneRoot.lookup("#radioBtnBorderCut4");

        choiceBoxBorderSideCut.getItems().add("Нет");
        choiceBoxBorderSideCut.getItems().add("С одной стороны");
        choiceBoxBorderSideCut.getItems().add("С двух сторон");
        choiceBoxBorderSideCut.getSelectionModel().select(0);


        radioBtnBorderCut1.setToggleGroup(toggleGroupBorderCut);
        radioBtnBorderCut2.setToggleGroup(toggleGroupBorderCut);
        radioBtnBorderCut3.setToggleGroup(toggleGroupBorderCut);
        radioBtnBorderCut4.setToggleGroup(toggleGroupBorderCut);

        radioBtnBorderSideCut1 = (RadioButton) anchorPaneRoot.lookup("#radioBtnBorderSideCut1");
        radioBtnBorderSideCut2 = (RadioButton) anchorPaneRoot.lookup("#radioBtnBorderSideCut2");
        radioBtnBorderSideCut1.setToggleGroup(toggleGroupBorderSideCut);
        radioBtnBorderSideCut2.setToggleGroup(toggleGroupBorderSideCut);

        //radioBtnBorderCut1.setBackground(new Background(new BackgroundImage(new Image(ProjectHandler.class.getResource("/styles/icons/save_project.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        radioBtnBorderCut1.getStyleClass().remove("radio-button");
        radioBtnBorderCut2.getStyleClass().remove("radio-button");
        radioBtnBorderCut3.getStyleClass().remove("radio-button");
        radioBtnBorderCut4.getStyleClass().remove("radio-button");

        radioBtnBorderCut1.getStyleClass().add("toggle-button");
        radioBtnBorderCut2.getStyleClass().add("toggle-button");
        radioBtnBorderCut3.getStyleClass().add("toggle-button");
        radioBtnBorderCut4.getStyleClass().add("toggle-button");

        radioBtnBorderCut1.setText("");
        radioBtnBorderCut2.setText("");
        radioBtnBorderCut3.setText("");
        radioBtnBorderCut4.setText("");

        radioBtnBorderCut1.setGraphic(new ImageView(new Image(Project.class.getResource("/styles/images/edgeManager/borderCut1.png").toString())));
        radioBtnBorderCut2.setGraphic(new ImageView(new Image(Project.class.getResource("/styles/images/edgeManager/borderCut2.png").toString())));
        radioBtnBorderCut3.setGraphic(new ImageView(new Image(Project.class.getResource("/styles/images/edgeManager/borderCut3.png").toString())));
        radioBtnBorderCut4.setGraphic(new ImageView(new Image(Project.class.getResource("/styles/images/edgeManager/borderCut4.png").toString())));


        radioBtnBorderSideCut1.getStyleClass().remove("radio-button");
        radioBtnBorderSideCut2.getStyleClass().remove("radio-button");
        radioBtnBorderSideCut1.getStyleClass().add("toggle-button");
        radioBtnBorderSideCut2.getStyleClass().add("toggle-button");

        radioBtnBorderSideCut1.setText("");
        radioBtnBorderSideCut2.setText("");

        radioBtnBorderSideCut1.setGraphic(new ImageView(new Image(Project.class.getResource("/styles/images/edgeManager/borderSideCut1.png").toString())));
        radioBtnBorderSideCut2.setGraphic(new ImageView(new Image(Project.class.getResource("/styles/images/edgeManager/borderSideCut2.png").toString())));
        //radioBtnBorderCut1.set

        borderSettingsGroup.getChildren().addAll(
                labelBorder1,
                labelBorder2,
                choiceBoxBorderSideCut,
                radioBtnBorderCut1,
                radioBtnBorderCut2,
                radioBtnBorderCut3,
                radioBtnBorderCut4,
                radioBtnBorderSideCut1,
                radioBtnBorderSideCut2);

        anchorPaneRoot.getChildren().add(borderSettingsGroup);


        edgesSettingsGroup.getChildren().add(checkBoxEdgeStoneHemOrLeakGroove);

        checkBoxEdgeStoneHemOrLeakGroove.setTranslateX(400);
        checkBoxEdgeStoneHemOrLeakGroove.setTranslateY(450);

        anchorPaneRoot.getChildren().add(edgesSettingsGroup);

        if (!sketchEdgeList.get(0).isDefined()) {
            toggleGroupBorderCut.getToggles().get(0).setSelected(true);
            toggleGroupBorderSideCut.getToggles().get(0).setSelected(true);
            choiceBoxBorderSideCut.getSelectionModel().select(0);

            borderSettingsGroup.setVisible(false);
            edgesSettingsGroup.setVisible(false);

            checkBoxEdgeStoneHemOrLeakGroove.setSelected(false);
        } else {
            if (sketchEdgeList.get(0) instanceof Border) {
                Border inputBorder = (Border) sketchEdgeList.get(0);
                toggleGroupBorderCut.getToggles().get(inputBorder.getBorderCutType() - 1).setSelected(true);
                toggleGroupBorderSideCut.getToggles().get(inputBorder.getBorderSideCutType() - 1).setSelected(true);
                choiceBoxBorderSideCut.getSelectionModel().select(inputBorder.getBorderAnglesCutType() - 1);

                borderSettingsGroup.setVisible(true);
                edgesSettingsGroup.setVisible(false);
            } else {
                Edge edge = (Edge) sketchEdgeList.get(0);
                toggleGroupBorderCut.getToggles().get(0).setSelected(true);
                toggleGroupBorderSideCut.getToggles().get(0).setSelected(true);
                choiceBoxBorderSideCut.getSelectionModel().select(0);
                checkBoxEdgeStoneHemOrLeakGroove.setSelected(edge.isStoneHemOrLeakGroove());

                borderSettingsGroup.setVisible(false);
                edgesSettingsGroup.setVisible(true);
            }

        }


        btnApply.setOnMouseClicked(event -> btnApplyClicked());
        btnDelete.setOnMouseClicked(event -> btnDeleteClicked());


    }


    private void fillEdgeTreeView(Material material, int edgeHeight, int shapeDepth) throws MalformedURLException {
        TreeItem<String> rootItem = new TreeItem<>("root");
        treeViewEdge.setRoot(rootItem);
        treeViewEdge.setShowRoot(false);

        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {
            materialType = MATERIAL_TYPE_1;

            for (int i = 1; i <= 17; i++) {
                TreeItem<String> item = new TreeItem<>("edge_" + i + ".png");
                File file = new File(ProjectHandler.EDGES_IMG_PATH + "edge_" + i + ".png");
                ImageView imageView = new ImageView(new Image(file.toURI().toURL().toString()));
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                item.setGraphic(imageView);
                rootItem.getChildren().add(item);
            }

            checkBoxEdgeStoneHemOrLeakGroove.setText("Подгиб камня с каплесборником");
        } else if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                material.getName().indexOf("Натуральный камень") != -1 ||
                material.getName().indexOf("Dektone") != -1 ||
                material.getName().indexOf("Мраморный агломерат") != -1 ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {
            materialType = MATERIAL_TYPE_2;
            int startIndex = 1;
            int endIndex = 27;
            if (edgeHeight == shapeDepth) {
                startIndex = 1;
                endIndex = 7;
            } else if (edgeHeight > shapeDepth) {
                startIndex = 8;
                endIndex = 27;
            }

            for (int i = startIndex; i <= endIndex; i++) {
                TreeItem<String> item = new TreeItem<>("edge_1_" + i + ".png");
                File file = new File(ProjectHandler.EDGES_IMG_PATH + "edge_1_" + i + ".png");
                ImageView imageView = new ImageView(new Image(file.toURI().toURL().toString()));
                imageView.setFitWidth(150);
                imageView.setFitHeight(100);
                item.setGraphic(imageView);
                rootItem.getChildren().add(item);
            }

            checkBoxEdgeStoneHemOrLeakGroove.setText("Выборка капельника");
        }

        treeViewEdge.setOnMouseClicked(event -> {
            treeViewBorder.getSelectionModel().clearSelection();
            radioBtnBorderCut1.setDisable(true);
            radioBtnBorderCut2.setDisable(true);
            radioBtnBorderCut3.setDisable(true);
            radioBtnBorderCut4.setDisable(true);
            radioBtnBorderSideCut1.setDisable(true);
            radioBtnBorderSideCut2.setDisable(true);
            choiceBoxBorderSideCut.setDisable(true);

            borderSettingsGroup.setVisible(false);
            edgesSettingsGroup.setVisible(true);

        });

    }

    private void fillBorderTreeView(Material material) throws MalformedURLException {
        TreeItem<String> rootItem = new TreeItem<>("root");
        treeViewBorder.setRoot(rootItem);
        treeViewBorder.setShowRoot(false);

        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {
            materialType = MATERIAL_TYPE_1;
            for (int i = 1; i <= 2; i++) {
                TreeItem<String> item = new TreeItem<>("border_" + i + ".png");
                File file = new File(ProjectHandler.BORDERS_IMG_PATH + "border_" + i + ".png");
                item.setGraphic(new ImageView(new Image(file.toURI().toURL().toString())));
                rootItem.getChildren().add(item);
            }
        } else if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                material.getName().indexOf("Натуральный камень") != -1 ||
                material.getName().indexOf("Dektone") != -1 ||
                material.getName().indexOf("Мраморный агломерат") != -1 ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {

            materialType = MATERIAL_TYPE_2;
            for (int i = 1; i <= 1; i++) {
                TreeItem<String> item = new TreeItem<>("border_" + i + ".png");
                File file = new File(ProjectHandler.BORDERS_IMG_PATH + "border_" + i + ".png");
                item.setGraphic(new ImageView(new Image(file.toURI().toURL().toString())));
                rootItem.getChildren().add(item);
            }
        }

        treeViewBorder.setOnMouseClicked(event -> {
            treeViewEdge.getSelectionModel().clearSelection();
            radioBtnBorderCut2.setSelected(true);//default selection
            if (materialType == MATERIAL_TYPE_2) {
                radioBtnBorderCut1.setDisable(true);
            } else {
                radioBtnBorderCut1.setDisable(false);
            }
            radioBtnBorderCut2.setDisable(false);
            radioBtnBorderCut3.setDisable(false);
            if (materialType == MATERIAL_TYPE_2) {
                radioBtnBorderCut4.setDisable(true);
            } else {
                radioBtnBorderCut4.setDisable(false);
            }
            radioBtnBorderSideCut1.setDisable(false);
            radioBtnBorderSideCut2.setDisable(false);
            choiceBoxBorderSideCut.setDisable(false);

            borderSettingsGroup.setVisible(true);
            edgesSettingsGroup.setVisible(false);
        });

    }

    public void show(Scene mainScene, SketchEdge sketchEdge) {
        Material material = ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial();
        int edgeHeight = ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getEdgeHeight();
        int depth = ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getShapeDepth();
        this.mainScene = mainScene;

        try {
            fillEdgeTreeView(material, edgeHeight, depth);
            fillBorderTreeView(material);
        } catch (MalformedURLException ex) {

        }

        radioBtnBorderCut1.setDisable(true);
        radioBtnBorderCut2.setDisable(true);
        radioBtnBorderCut3.setDisable(true);
        radioBtnBorderCut4.setDisable(true);
        radioBtnBorderSideCut1.setDisable(true);
        radioBtnBorderSideCut2.setDisable(true);
        choiceBoxBorderSideCut.setDisable(true);

        Stage edgeManagerStage = new Stage();
        edgeManagerStage.setTitle("Выберите кромку");
        edgeManagerStage.initOwner(mainScene.getWindow());
        edgeManagerStage.setScene(edgeManagerScene);
        edgeManagerStage.setX(mainScene.getWindow().getX() + mainScene.getWindow().getWidth() / 2 - edgeManagerScene.getWidth() / 2);
        edgeManagerStage.setY(mainScene.getWindow().getY() + mainScene.getWindow().getHeight() / 2 - edgeManagerScene.getHeight() / 2);
        edgeManagerStage.initModality(Modality.APPLICATION_MODAL);
        edgeManagerStage.setResizable(false);
        edgeManagerStage.show();


        edgeManagerScene.getWindow().setOnCloseRequest(event -> {
            for (SketchEdge edge : SketchDesigner.getSelectedEdges()) {
                edge.select(false);
            }
            SketchDesigner.getSelectedEdges().clear();
            SketchDesigner.setSelectedEdgeMaterial(null);
        });


        if (sketchEdge instanceof Edge && sketchEdge.isDefined()) {
            Edge edge = (Edge) sketchEdge;
            treeViewEdge.getSelectionModel().select(edge.getEdgeNumber() - 1);
        } else if (sketchEdge instanceof Border && sketchEdge.isDefined()) {
            Border border = (Border) sketchEdge;
            treeViewBorder.getSelectionModel().select(border.getEdgeNumber() - 1);
            if (material.getMainType().indexOf("Кварцевый агломерат") != -1 ||
                    material.getName().indexOf("Натуральный камень") != -1 ||
                    material.getMainType().indexOf("Dektone") != -1 ||
                    material.getMainType().indexOf("Мраморный агломерат") != -1 ||
                    material.getName().indexOf("Кварцекерамический камень") != -1) {
                radioBtnBorderCut2.setDisable(false);
                radioBtnBorderCut3.setDisable(false);
            } else {
                radioBtnBorderCut1.setDisable(false);
                radioBtnBorderCut2.setDisable(false);
                radioBtnBorderCut3.setDisable(false);
                radioBtnBorderCut4.setDisable(false);
            }
            radioBtnBorderSideCut1.setDisable(false);
            radioBtnBorderSideCut2.setDisable(false);
            choiceBoxBorderSideCut.setDisable(false);

            if (border.getBorderCutType() == Border.BORDER_CUT_TYPE_A) {
                radioBtnBorderCut1.setSelected(true);
            } else if (border.getBorderCutType() == Border.BORDER_CUT_TYPE_B) {
                radioBtnBorderCut2.setSelected(true);
            } else if (border.getBorderCutType() == Border.BORDER_CUT_TYPE_C) {
                radioBtnBorderCut3.setSelected(true);
            } else if (border.getBorderCutType() == Border.BORDER_CUT_TYPE_D) {
                radioBtnBorderCut4.setSelected(true);
            }

            if (border.getBorderSideCutType() == Border.BORDER_SIDE_CUT_TYPE_A) {
                radioBtnBorderSideCut1.setSelected(true);
            } else if (border.getBorderSideCutType() == Border.BORDER_SIDE_CUT_TYPE_B) {
                radioBtnBorderSideCut2.setSelected(true);
            }

            choiceBoxBorderSideCut.getSelectionModel().select(border.getBorderAnglesCutType() - 1);
        }
    }


    private void btnApplyClicked() {


        TreeItem<String> selectedItemEdge = treeViewEdge.getSelectionModel().getSelectedItem();
        TreeItem<String> selectedItemBorder = treeViewBorder.getSelectionModel().getSelectedItem();

        int edgeType = 0;
        if (((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Акриловый камень") != -1 ||
                ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Полиэфирный камень") != -1) {
            edgeType = 1;
        } else if (((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Кварцевый агломерат") != -1 ||
                ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Натуральный камень") != -1 ||
                ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Dektone") != -1 ||
                ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Мраморный агломерат") != -1 ||
                ((SketchShape) (sketchEdgeList.get(0).getSketchEdgeOwner())).getMaterial().getName().indexOf("Кварцекерамический камень") != -1) {
            edgeType = 2;
        }

        if (treeViewEdge.getSelectionModel().getSelectedIndex() != -1) {

            for (SketchEdge sketchEdge : sketchEdgeList) {
                Edge edge = new Edge(selectedItemEdge.getValue(), edgeType);
                edge.setSketchEdgeOwner(sketchEdge.getSketchEdgeOwner());

                edge.setStoneHemOrLeakGroove(checkBoxEdgeStoneHemOrLeakGroove.isSelected());
                ((SketchShape) (sketchEdge.getSketchEdgeOwner())).changeElementEdge(sketchEdge, edge);
                ((SketchShape) (sketchEdge.getSketchEdgeOwner())).refreshShapeSettings();
            }

            ((Stage) (edgeManagerScene.getWindow())).close();

        } else if (treeViewBorder.getSelectionModel().getSelectedIndex() != -1) {
            for (SketchEdge sketchEdge : sketchEdgeList) {
                Border newBorder = new Border(selectedItemBorder.getValue(), edgeType);

                if (radioBtnBorderCut1.isSelected()) newBorder.setBorderCut(Border.BORDER_CUT_TYPE_A);
                else if (radioBtnBorderCut2.isSelected()) newBorder.setBorderCut(Border.BORDER_CUT_TYPE_B);
                else if (radioBtnBorderCut3.isSelected()) newBorder.setBorderCut(Border.BORDER_CUT_TYPE_C);
                else if (radioBtnBorderCut4.isSelected()) newBorder.setBorderCut(Border.BORDER_CUT_TYPE_D);

                if (radioBtnBorderSideCut1.isSelected()) newBorder.setBorderSideCutType(Border.BORDER_SIDE_CUT_TYPE_A);
                else if (radioBtnBorderSideCut2.isSelected())
                    newBorder.setBorderSideCutType(Border.BORDER_SIDE_CUT_TYPE_B);

                newBorder.setBorderAnglesCutType(choiceBoxBorderSideCut.getSelectionModel().getSelectedIndex() + 1);

                System.out.println(newBorder);
                newBorder.setSketchEdgeOwner(sketchEdge.getSketchEdgeOwner());
                ((SketchShape) (sketchEdge.getSketchEdgeOwner())).changeElementEdge(sketchEdge, newBorder);
                ((SketchShape) (sketchEdge.getSketchEdgeOwner())).refreshShapeSettings();
                System.out.println(newBorder);
            }

            ((Stage) (edgeManagerScene.getWindow())).close();
        }


        for (SketchEdge edge : SketchDesigner.getSelectedEdges()) {
            edge.select(false);
        }
        SketchDesigner.getSelectedEdges().clear();
        SketchDesigner.setSelectedEdgeMaterial(null);


    }

    private void btnDeleteClicked() {


        for (SketchEdge sketchEdge : sketchEdgeList) {
            SketchEdge newEdge = new SketchEdge();
            newEdge.setSketchEdgeOwner(sketchEdge.getSketchEdgeOwner());
            ((SketchShape) (sketchEdge.getSketchEdgeOwner())).changeElementEdge(sketchEdge, newEdge);
            ((SketchShape) (sketchEdge.getSketchEdgeOwner())).refreshShapeSettings();
        }
        ((Stage) (edgeManagerScene.getWindow())).close();

        for (SketchEdge edge : SketchDesigner.getSelectedEdges()) {
            edge.select(false);
        }

        SketchDesigner.getSelectedEdges().clear();
        SketchDesigner.setSelectedEdgeMaterial(null);

    }
}
