package ru.koreanika.sketchDesigner.Shapes;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.Common.Material.Material;

import ru.koreanika.cutDesigner.CutDesigner;
//import ru.koreanika.cutDesigner.CutSheet;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.cutDesigner.Shapes.CutShapeEdge;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.Dimensions.LinearDimension;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Edge.EdgeManager;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Joint;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class SketchShapeRectangle extends SketchShape {

    Pane sketchPane;

    Point2D[] points;
    double widthRectangle = 60;
    double heightRectangle = 60;
    double connectAreaWidth = 0;


    //connect points:


    CutShapeEdge cutShapeEdgeTop;
    CutShapeEdge cutShapeEdgeBottom;
    CutShapeEdge cutShapeEdgeLeft;
    CutShapeEdge cutShapeEdgeRight;

//    CornerConnectPoint leftUpConnectPoint = new CornerConnectPoint(this);
//    CornerConnectPoint leftDownConnectPoint = new CornerConnectPoint(this);
//    CornerConnectPoint rightUpConnectPoint = new CornerConnectPoint(this);
//    CornerConnectPoint rightDownConnectPoint = new CornerConnectPoint(this);

    //sides free or busy for connect with other shapes
    boolean leftSideFree = true;
    boolean rightSideFree = true;
    boolean topSideFree = true;
    boolean bottomSideFree = true;

    //Shape edges
    Point2D[] topShapeEdge = new Point2D[2];
    Point2D[] bottomShapeEdge = new Point2D[2];
    Point2D[] leftShapeEdge = new Point2D[2];
    Point2D[] rightShapeEdge = new Point2D[2];

    //Element edges
    double widthEdge = 5;

    SketchEdge topEdge = null;
    SketchEdge bottomEdge = null;
    SketchEdge leftEdge = null;
    SketchEdge rightEdge = null;

    Polygon triangleIconTopEdge;
    Polygon triangleIconBottomEdge;
    Polygon triangleIconLeftEdge;
    Polygon triangleIconRightEdge;

    Line lineGrooveTopEdge;
    Line lineGrooveBottomEdge;
    Line lineGrooveLeftEdge;
    Line lineGrooveRightEdge;

    //Joints:
    Line lineTopJoint = null;
    Line lineBottomJoint = null;
    Line lineLeftJoint = null;
    Line lineRightJoint = null;

    ArrayList<Joint> sideTopJointsList = new ArrayList<>();
    ArrayList<Joint> sideBottomJointsList = new ArrayList<>();
    ArrayList<Joint> sideLeftJointsList = new ArrayList<>();
    ArrayList<Joint> sideRightJointsList = new ArrayList<>();


    //links to connected shapes:
    private SketchShape leftConnectedShape, rightConnectedShape, topConnectedShape, bottomConnectedShape;


    //Shape settings:
    boolean materialDefault = true; //default = true;
    boolean edgesHeightsDefault = true; //default = true;


    int edgeHeight = 0;
    int borderHeight = 0;


    double width = 0;//widthRectangle / commonShapeScale;
    double height = 0;//heightRectangle / commonShapeScale;

    CheckBox checkBoxMaterialDefault, checkBoxDefaultHeights, checkBoxSaveImage;
    ChoiceBox<String> choiceBoxMaterial;
    ChoiceBox<String> choiceBoxMaterialDepth;
    TextField textFieldWidth, textFieldHeight;
    TextField textFieldX, textFieldY;
    TextField textFieldEdgeHeight, textFieldBorderHeight;

    Group groupEdges;

    boolean correctEdgeHeight = true, correctBorderHeight = true, correctX = true, correctY = true, correctWidth = true, correctHeight = true;


    public SketchShapeRectangle(double layoutX, double layoutY, ElementTypes elementType, Pane sketchPane) {
        setChildShape(this);

        shapeType = ShapeType.RECTANGLE;
        //dragShapeFormat = new DataFormat(elementType.toString());
        this.elementType = elementType;
        setTranslateX(layoutX);
        setTranslateY(layoutY);
        this.sketchPane = sketchPane;

        if (elementType == ElementTypes.TABLETOP) {
            shapeColor = SketchShape.TABLE_TOP_COLOR;
            imagePath = TABLE_TOP_IMAGE_PATH;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            shapeColor = SketchShape.WALL_PANEL_COLOR;
            imagePath = WALL_PANEL_IMAGE_PATH;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            shapeColor = SketchShape.WINDOWSILL_COLOR;
            imagePath = WINDOWSILL_IMAGE_PATH;
        } else if (elementType == ElementTypes.FOOT) {
            shapeColor = SketchShape.FOOT_COLOR;
            imagePath = FOOT_IMAGE_PATH;
        }


        initShapeMaterial(Project.getDefaultMaterial(), Project.getDefaultMaterial().getDefaultDepth());
        setEdgesHeights(true, Project.getDefaultMaterial().getDefaultDepth(), Border.DEFAULT_HEIGHT);

        initShapeSettings();
        initShapeSettingsControlLogic();
        createContextMenu();

        points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(widthRectangle, 0.0),
                new Point2D(widthRectangle, heightRectangle),
                new Point2D(0.0, heightRectangle),
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
        );


        // create ImagePattern

        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, widthRectangle, heightRectangle, false);
            polygon.setFill(image_pattern);
        }

        connectionPoints.add(new CornerConnectPoint(this));
        connectionPoints.add(new CornerConnectPoint(this));
        connectionPoints.add(new CornerConnectPoint(this));
        connectionPoints.add(new CornerConnectPoint(this));


        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(heightRectangle + connectAreaWidth * 2);
        setPrefWidth(widthRectangle + connectAreaWidth * 2);

        //polygon.setFill(shapeColor);


        getChildren().add(polygon);

        updateShapeNumber();
        initEdgesZones();
        initConnectionPoints();


    }

    public SketchShapeRectangle(ElementTypes elementType, Material material, int depth, double width, double height) {

        this.elementType = elementType;
        this.width = width;
        this.height = height;

        widthRectangle = this.width * commonShapeScale;
        heightRectangle = this.height * commonShapeScale;

//        System.out.println("this.width = " + this.width);
//        System.out.println("this.height = " + this.height);
//
//        System.out.println("widthRectangle = " + widthRectangle);
//        System.out.println("heightRectangle = " + heightRectangle);

        initShapeMaterial(material, depth);

        setChildShape(this);

        shapeType = ShapeType.RECTANGLE;
        //dragShapeFormat = new DataFormat(elementType.toString());


        if (elementType == ElementTypes.TABLETOP) {
            shapeColor = SketchShape.TABLE_TOP_COLOR;
            imagePath = TABLE_TOP_IMAGE_PATH;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            shapeColor = SketchShape.WALL_PANEL_COLOR;
            imagePath = WALL_PANEL_IMAGE_PATH;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            shapeColor = SketchShape.WINDOWSILL_COLOR;
            imagePath = WINDOWSILL_IMAGE_PATH;
        } else if (elementType == ElementTypes.FOOT) {
            shapeColor = SketchShape.FOOT_COLOR;
            imagePath = FOOT_IMAGE_PATH;
        }


        //initShapeMaterial(ProjectHandler.getDefaultMaterial(), ProjectHandler.getDefaultMaterial().getDefaultDepth());
        //setEdgesHeights(true, depth, Border.DEFAULT_HEIGHT);

//        initShapeSettings();
//        initShapeSettingsControlLogic();
//        createContextMenu();

        points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(widthRectangle, 0.0),
                new Point2D(widthRectangle, heightRectangle),
                new Point2D(0.0, heightRectangle),
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
        );


        // create ImagePattern

        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, widthRectangle, heightRectangle, false);
            polygon.setFill(image_pattern);
        }

        connectionPoints.add(new CornerConnectPoint(this));
        connectionPoints.add(new CornerConnectPoint(this));
        connectionPoints.add(new CornerConnectPoint(this));
        connectionPoints.add(new CornerConnectPoint(this));


        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(heightRectangle + connectAreaWidth * 2);
        setPrefWidth(widthRectangle + connectAreaWidth * 2);

        //polygon.setFill(shapeColor);


        getChildren().add(polygon);

        updateShapeNumber();
        initEdgesZones();
        initConnectionPoints();


    }

    @Override
    public void initConnectionPoints() {

//        if(connectionPoints.size() != 0){
//            for(ConnectPoint connectPoint : connectionPoints){
//                getChildren().remove(connectPoint);
//            }
//            connectionPoints.clear();
//        }
        if (connectionPoints.size() == 0) {
            for (int i = 0; i < 4; i++) {
                connectionPoints.add(new CornerConnectPoint(this));
            }
        }


        connectionPoints.get(0).setTranslateX(-(widthConnectPoint / 2));
        connectionPoints.get(0).setTranslateY(-(widthConnectPoint / 2));
        connectionPoints.get(0).hide();

        connectionPoints.get(1).setTranslateX(-(widthConnectPoint / 2));
        connectionPoints.get(1).setTranslateY(heightRectangle - (widthConnectPoint / 2));
        connectionPoints.get(1).hide();

        connectionPoints.get(2).setTranslateX(widthRectangle - (widthConnectPoint / 2));
        connectionPoints.get(2).setTranslateY(-(widthConnectPoint / 2));
        connectionPoints.get(2).hide();

        connectionPoints.get(3).setTranslateX(widthRectangle - (widthConnectPoint / 2));
        connectionPoints.get(3).setTranslateY(heightRectangle - (widthConnectPoint / 2));
        connectionPoints.get(3).hide();

        for (ConnectPoint connectPoint : connectionPoints) {
            getChildren().remove(connectPoint);
        }
        for (ConnectPoint connectPoint : connectionPoints) {
            getChildren().add(connectPoint);
        }
        //getChildren().addAll(leftUpConnectPoint, leftDownConnectPoint, rightUpConnectPoint, rightDownConnectPoint);

    }

    @Override
    public void setWidthConnectPoint(double widthConnectPoint) {
        if (widthConnectPoint > 10) {
            this.widthConnectPoint = 10;
        } else {
            this.widthConnectPoint = widthConnectPoint;
        }
        connectionPoints.forEach(connectPoint -> {
            connectPoint.changeSide(this.widthConnectPoint);
        });
        initConnectionPoints();

    }

    @Override
    public ArrayList<ConnectPoint> getConnectPoints() {
        return connectionPoints;
    }

    /**
     * SETTINGS  >>>
     */

    @Override
    public void initShapeSettings() {
        //get shape settings pane:
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchShapeRectangleSettings.fxml"));
        try {
            shapeSettings = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ScrollPane scrollPane = (ScrollPane) shapeSettings.lookup("#scrollPane");
        settingsRootAnchorPane = (AnchorPane) scrollPane.getContent();

        checkBoxMaterialDefault = (CheckBox) settingsRootAnchorPane.lookup("#checkBoxMaterialDefault");
        checkBoxDefaultHeights = (CheckBox) settingsRootAnchorPane.lookup("#checkBoxDefaultHeights");
        checkBoxSaveImage = (CheckBox) settingsRootAnchorPane.lookup("#checkBoxSaveImage");

        choiceBoxMaterial = (ChoiceBox<String>) settingsRootAnchorPane.lookup("#choiceBoxMaterial");
        choiceBoxMaterialDepth = (ChoiceBox<String>) settingsRootAnchorPane.lookup("#choiceBoxMaterialDepth");

        textFieldWidth = (TextField) settingsRootAnchorPane.lookup("#textFieldWidth");
        textFieldHeight = (TextField) settingsRootAnchorPane.lookup("#textFieldHeight");
        textFieldX = (TextField) settingsRootAnchorPane.lookup("#textFieldX");
        textFieldY = (TextField) settingsRootAnchorPane.lookup("#textFieldY");
        textFieldEdgeHeight = (TextField) settingsRootAnchorPane.lookup("#textFieldEdgeHeight");
        textFieldBorderHeight = (TextField) settingsRootAnchorPane.lookup("#textFieldBorderHeight");


        groupEdges = (Group) settingsRootAnchorPane.lookup("#groupEdges");


        if (elementType == ElementTypes.WALL_PANEL || elementType == ElementTypes.FOOT) {
            groupEdges.setVisible(false);
        }


        checkBoxMaterialDefault.setSelected(materialDefault);
        checkBoxDefaultHeights.setSelected(edgesHeightsDefault);

        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());
        if (checkBoxMaterialDefault.isSelected()) {
            choiceBoxMaterial.setDisable(true);
            choiceBoxMaterialDepth.setDisable(true);
        }

        if (checkBoxDefaultHeights.isSelected()) {
            textFieldEdgeHeight.setDisable(true);
            textFieldBorderHeight.setDisable(true);
        }

        for (String s : shapeMaterial.getDepths()) {
            choiceBoxMaterialDepth.getItems().add(s);
        }
        choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(shapeDepth));

        //System.out.println("INIT SHAPE SETTINGS ");
        textFieldWidth.setText(String.format("%.0f", width));
        textFieldHeight.setText(String.format("%.0f", height));
        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));
        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));
    }

    @Override
    public void initShapeSettingsControlLogic() {
        checkBoxMaterialDefault.setOnMouseClicked(event -> {
            if (checkBoxMaterialDefault.isSelected()) {
                choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());
                choiceBoxMaterial.setDisable(true);


                choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(shapeMaterial.getDefaultDepth()));
                choiceBoxMaterialDepth.setDisable(true);

                if (Project.getDefaultMaterial().getName().indexOf("Акриловый камень") != -1 || Project.getDefaultMaterial().getName().indexOf("Полиэфирный камень") != -1) {

                    checkBoxSaveImage.setDisable(true);
                    checkBoxSaveImage.setSelected(false);
                    saveMaterialImageOnEdges = false;
                } else {
                    checkBoxSaveImage.setDisable(false);
                }

            } else {
                choiceBoxMaterial.setDisable(false);
                choiceBoxMaterialDepth.setDisable(false);
            }
        });
        checkBoxDefaultHeights.setOnMouseClicked(event -> {
            if (checkBoxDefaultHeights.isSelected()) {
//                edgeHeight = shapeDepth;
//                borderHeight = Border.DEFAULT_HEIGHT;
                if (shapeMaterial.getName().indexOf("Акриловый камень") != -1) {
                    if (elementType == ElementTypes.TABLETOP) {
                        textFieldEdgeHeight.setText("" + 40);
                    } else if (elementType == ElementTypes.WALL_PANEL) {
                        textFieldEdgeHeight.setText(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                    } else if (elementType == ElementTypes.WINDOWSILL) {
                        textFieldEdgeHeight.setText("" + 40);
                    } else if (elementType == ElementTypes.FOOT) {
                        textFieldEdgeHeight.setText("" + 40);
                    }

                } else {
                    textFieldEdgeHeight.setText(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                }

                textFieldBorderHeight.setText("" + Border.DEFAULT_HEIGHT);
                textFieldEdgeHeight.setDisable(true);
                textFieldBorderHeight.setDisable(true);
            } else {
                textFieldEdgeHeight.setDisable(false);
                textFieldBorderHeight.setDisable(false);
            }
        });
        checkBoxSaveImage.setOnMouseClicked(event -> {
        });

        choiceBoxMaterial.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //choiceBoxMaterial.setOnAction(event -> {
            //System.out.println(" choiceBoxMaterial. newValue = " +  newValue);
            //System.out.println("shapeMaterial = " + shapeMaterial.getName());
            //if(choiceBoxMaterial.getSelectionModel().getSelectedItem() == null) return;
            if (newValue == null) return;
            //if(!choiceBoxMaterial.getSelectionModel().getSelectedItem().equals(shapeMaterial.getReceiptName())){

            for (Material m : Project.getMaterialsListInProject()) {
                if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                    choiceBoxMaterialDepth.getItems().clear();
                    for (String s : m.getDepths()) {
                        choiceBoxMaterialDepth.getItems().add(s);
                    }
                    choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(m.getDefaultDepth()));

                    if (m.getName().indexOf("Акриловый камень") != -1 || m.getName().indexOf("Полиэфирный камень") != -1) {

                        checkBoxSaveImage.setDisable(true);
                        checkBoxSaveImage.setSelected(false);
                        saveMaterialImageOnEdges = false;
                    } else {
                        checkBoxSaveImage.setDisable(false);
                    }
                }
            }


            //}
        });

        choiceBoxMaterialDepth.setOnAction(event -> {
            try {
                int selectedDepth = Integer.parseInt(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                int typedEdgeHeight = Integer.parseInt(textFieldEdgeHeight.getText());

                if (selectedDepth > typedEdgeHeight) {
                    textFieldEdgeHeight.setText("" + selectedDepth);
                }
            } catch (NumberFormatException ex) {
            }

        });

        textFieldWidth.textProperty().addListener(observable -> {
            double value;
            try {
                value = Double.parseDouble(textFieldWidth.getText());
                correctWidth = true;
                textFieldWidth.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > 100000) {
                    correctWidth = false;
                    textFieldWidth.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctWidth = false;
                textFieldWidth.setStyle("-fx-text-fill: red");
            }
            //check that it correct
        });
        textFieldHeight.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldHeight.getText());
                correctHeight = true;
                textFieldHeight.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > 10000) {
                    correctHeight = false;
                    textFieldHeight.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctHeight = false;
                textFieldHeight.setStyle("-fx-text-fill: red");
            }
        });
        textFieldX.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldX.getText());
                correctX = true;
                textFieldX.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > sketchPane.getPrefWidth() - 10) {
                    correctX = false;
                    textFieldX.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctX = false;
                textFieldX.setStyle("-fx-text-fill: red");
            }
        });
        textFieldY.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldY.getText());
                correctY = true;
                textFieldY.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > sketchPane.getPrefHeight() - 10) {
                    correctY = false;
                    textFieldY.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctY = false;
                textFieldY.setStyle("-fx-text-fill: red");
            }
        });
        textFieldEdgeHeight.textProperty().addListener(observable -> {
            int value;
            try {
                value = Integer.parseInt(textFieldEdgeHeight.getText());
                correctEdgeHeight = true;
                textFieldEdgeHeight.setStyle("-fx-text-fill: #B3B4B4");
                int selectedDepth = Integer.parseInt(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                if (value < selectedDepth || value > Edge.MAX_HEIGHT) {
                    correctEdgeHeight = false;
                    textFieldEdgeHeight.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctEdgeHeight = false;
                textFieldEdgeHeight.setStyle("-fx-text-fill: red");
            }
        });
        textFieldBorderHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            int value;
            try {
                value = Integer.parseInt(textFieldBorderHeight.getText());
                correctBorderHeight = true;
                textFieldBorderHeight.setStyle("-fx-text-fill: #B3B4B4");
                if (value < Border.MIN_HEIGHT || value > Border.MAX_HEIGHT) {
                    correctBorderHeight = false;
                    textFieldBorderHeight.setStyle("-fx-text-fill: red");
                }

            } catch (NumberFormatException ex) {
                correctBorderHeight = false;
                textFieldBorderHeight.setStyle("-fx-text-fill: red");
            }
        });


        translateXProperty().addListener((observable, oldValue, newValue) -> {
            textFieldX.setText(String.format("%.0f", getTranslateX()));
        });
        translateYProperty().addListener((observable, oldValue, newValue) -> {
            textFieldY.setText(String.format("%.0f", getTranslateY()));
        });


        shapeSettings.heightProperty().addListener((observable, oldValue, newValue) -> {
            settingsRootAnchorPane.setPrefHeight(newValue.doubleValue());
        });


        //shapeSettings

    }

    @Override
    public void refreshShapeSettings() {

        System.out.println("REFRESH SHAPE SETTINGS ");

        checkBoxMaterialDefault.setSelected(materialDefault);
        checkBoxDefaultHeights.setSelected(edgesHeightsDefault);

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        if (!choiceBoxMaterial.getItems().contains(shapeMaterial.getReceiptName())) {
            setShapeMaterial(Project.getDefaultMaterial(), Project.getDefaultMaterial().getDefaultDepth());
            //shapeMaterial = ProjectHandler.getDefaultMaterial();
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());

        if (checkBoxMaterialDefault.isSelected()) {
            choiceBoxMaterial.setDisable(true);
            choiceBoxMaterialDepth.setDisable(true);
        } else {
            choiceBoxMaterial.setDisable(false);
            choiceBoxMaterialDepth.setDisable(false);
        }

        if (checkBoxDefaultHeights.isSelected()) {
            textFieldEdgeHeight.setDisable(true);
            textFieldBorderHeight.setDisable(true);
        } else {
            textFieldEdgeHeight.setDisable(false);
            textFieldBorderHeight.setDisable(false);
        }

        if (saveMaterialImageOnEdges) {
            checkBoxSaveImage.setSelected(true);
        }
        choiceBoxMaterialDepth.getItems().clear();
        for (String s : shapeMaterial.getDepths()) {
            choiceBoxMaterialDepth.getItems().add(s);
        }
        choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(shapeDepth));

        textFieldWidth.setText(String.format("%.0f", width));
        textFieldHeight.setText(String.format("%.0f", height));

        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));

    }

    @Override
    public void shapeSettingsSaveBtnClicked() {

        if ((!correctBorderHeight) || (!correctEdgeHeight) || (!correctWidth) || (!correctHeight) || (!correctX) || (!correctY)) {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Проверьте введенные данные!", null);
            return;
        }

        saveMaterialImageOnEdges = checkBoxSaveImage.isSelected();

        width = Integer.parseInt(textFieldWidth.getText());
        height = Integer.parseInt(textFieldHeight.getText());

        double minMaterialSize = (shapeMaterial.getMaterialWidth() < shapeMaterial.getMaterialHeight()) ? shapeMaterial.getMaterialWidth() : shapeMaterial.getMaterialHeight();
        double maxMaterialSize = (shapeMaterial.getMaterialWidth() > shapeMaterial.getMaterialHeight()) ? shapeMaterial.getMaterialWidth() : shapeMaterial.getMaterialHeight();

        if ((width > minMaterialSize && height > minMaterialSize) || (width > maxMaterialSize || height > maxMaterialSize)) {
            width = widthRectangle / commonShapeScale;
            height = heightRectangle / commonShapeScale;

            if ((width > shapeMaterial.getMaterialWidth() && width > shapeMaterial.getMaterialHeight()) || (height > shapeMaterial.getMaterialWidth() && height > shapeMaterial.getMaterialHeight())) {
                width = shapeMaterial.getMaterialWidth();
                height = shapeMaterial.getMaterialHeight();
                widthRectangle = width * commonShapeScale;
                heightRectangle = height * commonShapeScale;
            }
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Не соответствует размеру материала. Необходимо разделить фигуру", null);
            textFieldWidth.setText(String.format("%.0f", width));
            textFieldHeight.setText(String.format("%.0f", height));
            return;
        } else {
            widthRectangle = width * commonShapeScale;
            heightRectangle = height * commonShapeScale;
        }

        widthRectangle = width * commonShapeScale;
        heightRectangle = height * commonShapeScale;


        textFieldWidth.setText(String.format("%.0f", width));
        textFieldHeight.setText(String.format("%.0f", height));

        try {
            setTranslateX(Double.parseDouble(textFieldX.getText()));
        } catch (NumberFormatException ex) {
            textFieldX.setText(String.format("%.0f", getTranslateX()));
        }

        try {
            setTranslateY(Double.parseDouble(textFieldY.getText()));
        } catch (NumberFormatException ex) {
            textFieldY.setText(String.format("%.0f", getTranslateY()));
        }


        materialDefault = checkBoxMaterialDefault.isSelected();
        edgesHeightsDefault = checkBoxDefaultHeights.isSelected() ? true : false;

        if (materialDefault) {
            if (shapeDepth != Project.getDefaultMaterial().getDefaultDepth() || (!shapeMaterial.getName().equals(Project.getDefaultMaterial()))) {
                setShapeMaterial(Project.getDefaultMaterial(), Project.getDefaultMaterial().getDefaultDepth());
                choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());
            }
        } else {
            for (Material material : Project.getMaterialsListInProject()) {
                if (choiceBoxMaterial.getSelectionModel().getSelectedItem().equals(material.getReceiptName())) {
                    setShapeMaterial(material, Integer.parseInt(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem()));
                }
            }
        }


        //if(edgeHeight != Integer.parseInt(textFieldEdgeHeight.getText()) || borderHeight != Integer.parseInt(textFieldBorderHeight.getText())){
        setEdgesHeights(false, Integer.parseInt(textFieldEdgeHeight.getText()), Integer.parseInt(textFieldBorderHeight.getText()));
        //}


        rebuildShapeView();
        selectShape();

    }

    @Override
    public void shapeSettingsCancelBtnClicked() {
        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        refreshShapeSettings();
    }

    @Override
    public AnchorPane getShapeSettings() {
        if (connected == true) {
            textFieldWidth.setDisable(true);
            textFieldHeight.setDisable(true);
        } else {
            textFieldWidth.setDisable(false);
            textFieldHeight.setDisable(false);
        }

        refreshShapeSettings();
        return shapeSettings;
    }

    /**
     * SETTINGS  <<<
     */

    public void edgeManagerShow(SketchEdge edge) {

        if (SketchDesigner.getSelectionModeForEdges()) {
            if (!SketchDesigner.getSelectedEdges().contains(edge)) {
                if (SketchDesigner.getSelectedEdgeMaterial() == null || SketchDesigner.getSelectedEdgeMaterial().getName().equals(shapeMaterial.getName())) {
                    SketchDesigner.setSelectedEdgeMaterial(shapeMaterial);
                    SketchDesigner.getSelectedEdges().add(edge);
                    edge.select(true);
                } else {
                    InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Разные типы Материалов", null);
                }

            }

        } else {
            ArrayList<SketchEdge> edgesList = new ArrayList<>();
            edgesList.add(edge);
            EdgeManager edgeManager = new EdgeManager(edgesList);
            edgeManager.show(this.getScene(), edge);
        }
    }

    @Override
    public void rebuildShapeView() {

        //sketchPane.getChildren().remove(dimensionsPane);
        double saveRotateAngle = rotateAngle;//because after next line it wil be "0"
        rotateShape(-rotateAngle);
        getChildren().clear();
        points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(widthRectangle, 0.0),
                new Point2D(widthRectangle, heightRectangle),
                new Point2D(0.0, heightRectangle),
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
        );

//        if(imageForFill == null){
//            polygon.setFill(shapeColor);
//            System.err.println("CANT FILL RECTANGLE SHAPE");
//        }else {
//            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, widthRectangle, heightRectangle, false);
//            polygon.setFill(image_pattern);
//        }

        unSelectShape();

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.CENTERED);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(heightRectangle + connectAreaWidth * 2);
        setPrefWidth(widthRectangle + connectAreaWidth * 2);
        //setStyle("-fx-background-color: Blue");

        getChildren().add(polygon);
        //getChildren().add(allDimensions);
        //System.out.println("THIS SHAPE NUMBER( rebuildShapeView()) = " + thisShapeNumber);
        updateShapeNumber();

        if (elementType == ElementTypes.TABLETOP || elementType == ElementTypes.WINDOWSILL) {
            initEdgesZones();
            refreshEdgeView();
        }

        initConnectionPoints();
        //CutDesigner.refreshCutView();

        for (AdditionalFeature feature : featuresList) {
            getChildren().add(feature);
            feature.toFront();
        }
        //rotateFeatures(-rotateAngle);

        rotateShape(saveRotateAngle);


    }

    @Override
    public void deleteShape() {

        Project.getMaterialsUsesInProjectObservable().remove(shapeMaterial.getName() + "#" + shapeDepth);
        if (elementType == ElementTypes.TABLETOP)
            Project.getDepthsTableTopsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            Project.getDepthsWallPanelsUsesInProjectObservable().remove(String.valueOf(shapeDepth));

        Project.getEdgesHeightsUsesInProjectObservable().remove(String.valueOf(edgeHeight));
        Project.getBordersHeightsUsesInProjectObservable().remove(String.valueOf(borderHeight));

        SketchDesigner.getSketchPane().getChildren().remove(this);
        SketchDesigner.getSketchShapesList().remove(this);
        if (topConnectedShape != null) topConnectedShape.disconnectFromShape(this);
        if (bottomConnectedShape != null) bottomConnectedShape.disconnectFromShape(this);
        if (leftConnectedShape != null) leftConnectedShape.disconnectFromShape(this);
        if (rightConnectedShape != null) rightConnectedShape.disconnectFromShape(this);

        for (String s : Project.getMaterialsUsesInProjectObservable()) {
            System.out.println(s);
        }

        for (String s : Project.getDepthsTableTopsUsesInProjectObservable()) {
            System.out.println(s);
        }

        Iterator<LinearDimension> it = SketchDesigner.getAllDimensions().iterator();
        while (it.hasNext()) {
            LinearDimension ld = it.next();
            if (ld.getConnectPoint1().getParent().equals(this) || ld.getConnectPoint2().getParent().equals(this)) {
                it.remove();
                sketchPane.getChildren().remove(ld);
            }
        }


    }

    @Override
    public void disconnectFromShape(SketchShape connectedShape) {
        if (topConnectedShape != null && topConnectedShape.equals(connectedShape)) {
            topConnectedShape = null;
            topSideFree = true;
        } else if (bottomConnectedShape != null && bottomConnectedShape.equals(connectedShape)) {
            bottomConnectedShape = null;
            bottomSideFree = true;
        } else if (leftConnectedShape != null && leftConnectedShape.equals(connectedShape)) {
            leftConnectedShape = null;
            leftSideFree = true;
        } else if (rightConnectedShape != null && rightConnectedShape.equals(connectedShape)) {
            rightConnectedShape = null;
            rightSideFree = true;
        }

        if (topSideFree == true && bottomSideFree == true && leftSideFree == true && rightSideFree == true) {
            connected = false;
        }

        /*if(connected == false)shapeColor = DISCONNECT_COLOR;
        else shapeColor = CONNECT_COLOR;*/

        polygon.setFill(shapeColor);
    }

    public void disconnectFromAll() {

        setTranslateX(getTranslateX() + 10);
        setTranslateY(getTranslateY() + 10);

        if (topConnectedShape != null) topConnectedShape.disconnectFromShape(this);
        if (bottomConnectedShape != null) bottomConnectedShape.disconnectFromShape(this);
        if (leftConnectedShape != null) leftConnectedShape.disconnectFromShape(this);
        if (rightConnectedShape != null) rightConnectedShape.disconnectFromShape(this);

        topConnectedShape = null;
        bottomConnectedShape = null;
        leftConnectedShape = null;
        rightConnectedShape = null;

        topSideFree = true;
        bottomSideFree = true;
        leftSideFree = true;
        rightSideFree = true;

        //setStyle("-fx-background-color: blue");
        connected = false;

        //shapeColor = DISCONNECT_COLOR;
        polygon.setFill(shapeColor);
    }

    @Override
    public void createContextMenu() {
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem disconnectMenuItem = new MenuItem("Disconnect");
        MenuItem unionMenuItem = new MenuItem("Union");
        //MenuItem disconnectMenuItem = new MenuItem("Create");

        shapeContextMenu.getItems().add(deleteMenuItem);
        shapeContextMenu.getItems().add(disconnectMenuItem);
        //shapeContextMenu.getItems().add(unionMenuItem);

        deleteMenuItem.setOnAction(event -> {
            deleteShape();

        });

        disconnectMenuItem.setOnAction(event -> {
            disconnectFromAll();
        });
        unionMenuItem.setOnAction(event -> {
            SketchDesigner.createUnionShape();
        });

    }

    @Override
    public void setEdgesZoneWidth(double widthEdge) {
        if (widthEdge <= 5) {
            this.widthEdge = widthEdge;
        } else {
            this.widthEdge = 5;
        }
        initEdgesZones();
    }

    @Override
    public void initEdgesZones() {

        if (topEdge == null) topEdge = new SketchEdge();
        topEdge.getPoints().clear();
        topEdge.getPoints().addAll(
                0.0, 0.0,
                widthRectangle, 0.0,
                widthRectangle, widthEdge,
                0.0, widthEdge
        );
        if (!getChildren().contains(topEdge)) getChildren().add(topEdge);


        topEdge.setOnMouseClicked(event -> edgeManagerShow(topEdge));

        if (bottomEdge == null) bottomEdge = new SketchEdge();
        bottomEdge.getPoints().clear();
        bottomEdge.getPoints().addAll(
                0.0, heightRectangle - widthEdge,
                widthRectangle, heightRectangle - widthEdge,
                widthRectangle, heightRectangle,
                0.0, heightRectangle
        );
        if (!getChildren().contains(bottomEdge)) getChildren().add(bottomEdge);

        bottomEdge.setOnMouseClicked(event -> edgeManagerShow(bottomEdge));

        if (leftEdge == null) leftEdge = new SketchEdge();
        leftEdge.getPoints().clear();
        leftEdge.getPoints().addAll(
                0.0, 0.0,
                widthEdge, 0.0,
                widthEdge, heightRectangle,
                0.0, heightRectangle
        );
        if (!getChildren().contains(leftEdge)) getChildren().add(leftEdge);

        leftEdge.setOnMouseClicked(event -> edgeManagerShow(leftEdge));

        if (rightEdge == null) rightEdge = new SketchEdge();
        rightEdge.getPoints().clear();
        rightEdge.getPoints().addAll(
                widthRectangle - widthEdge, 0.0,
                widthRectangle, 0.0,
                widthRectangle, heightRectangle,
                widthRectangle - widthEdge, heightRectangle
        );
        if (!getChildren().contains(rightEdge)) getChildren().add(rightEdge);

        rightEdge.setOnMouseClicked(event -> edgeManagerShow(rightEdge));


        topEdge.setSketchEdgeOwner(this);
        bottomEdge.setSketchEdgeOwner(this);
        leftEdge.setSketchEdgeOwner(this);
        rightEdge.setSketchEdgeOwner(this);

        refreshEdgeView();
    }


    @Override
    public void changeElementEdge(SketchEdge edge, SketchEdge newEdge) {
        newEdge.setSketchEdgeOwner(this);
        if (topEdge == edge) {

            if (!topEdge.getName().equals(newEdge.getName())) {
                if (topEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().remove(topEdge);
                } else if (topEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().remove(topEdge);
                }

                if (newEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().add((Border) newEdge);
                }

            }

            getChildren().remove(topEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(topEdge.getPoints());
            topEdge = newEdge;

            getChildren().add(topEdge);
            topEdge.setOnMouseClicked(event -> edgeManagerShow(topEdge));

            //create icon
        } else if (bottomEdge == edge) {

            if (!bottomEdge.getName().equals(newEdge.getName())) {
                if (bottomEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().remove(bottomEdge);
                } else if (bottomEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().remove(bottomEdge);
                }

                if (newEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }

            getChildren().remove(bottomEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(bottomEdge.getPoints());
            bottomEdge = newEdge;

            getChildren().add(bottomEdge);
            bottomEdge.setOnMouseClicked(event -> edgeManagerShow(bottomEdge));
            //create icon
        } else if (leftEdge == edge) {

            if (!leftEdge.getName().equals(newEdge.getName())) {
                if (leftEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().remove(leftEdge);
                } else if (leftEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().remove(leftEdge);
                }

                if (newEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }

            getChildren().remove(leftEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(leftEdge.getPoints());
            leftEdge = newEdge;

            getChildren().add(leftEdge);
            leftEdge.setOnMouseClicked(event -> edgeManagerShow(leftEdge));
            //create icon
        } else if (rightEdge == edge) {

            if (!rightEdge.getName().equals(newEdge.getName())) {
                if (rightEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().remove(rightEdge);
                } else if (rightEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().remove(rightEdge);
                }

                if (newEdge instanceof Edge) {
                    Project.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    Project.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }

            getChildren().remove(rightEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(rightEdge.getPoints());
            rightEdge = newEdge;

            getChildren().add(rightEdge);
            rightEdge.setOnMouseClicked(event -> edgeManagerShow(rightEdge));
            //create icon
        }


        //initEdgesZones();
        refreshEdgeView();
        //refreshShapeSettings();
    }

    @Override
    public ArrayList<SketchEdge> getEdges() {
        return new ArrayList<SketchEdge>(Arrays.asList(topEdge, bottomEdge, leftEdge, rightEdge));
    }

    @Override
    public void selectEdge(SketchEdge edge) {

    }

    @Override
    public void deSelectEdge(SketchEdge edge) {

    }

    @Override
    public void deSelectAllEdges() {
        bottomEdge.select(false);
        topEdge.select(false);
        leftEdge.select(false);
        rightEdge.select(false);
    }

    private void refreshEdgeView() {

        getChildren().remove(triangleIconTopEdge);
        getChildren().remove(triangleIconBottomEdge);
        getChildren().remove(triangleIconLeftEdge);
        getChildren().remove(triangleIconRightEdge);

        getChildren().remove(lineGrooveTopEdge);
        getChildren().remove(lineGrooveBottomEdge);
        getChildren().remove(lineGrooveLeftEdge);
        getChildren().remove(lineGrooveRightEdge);

        if (topEdge instanceof Edge) {
            triangleIconTopEdge = new Polygon(
                    widthRectangle / 2, 1.0,
                    widthRectangle / 2 - 2.5, 6.0,
                    widthRectangle / 2 + 2.5, 6.0);


        } else {
            triangleIconTopEdge = new Polygon(
                    widthRectangle / 2 - 2.5, 2.0,
                    widthRectangle / 2 + 2.5, 7.0,
                    widthRectangle / 2, 4.5,
                    widthRectangle / 2 + 2.5, 2.0,
                    widthRectangle / 2 - 2.5, 7.0,
                    widthRectangle / 2, 4.5,
                    widthRectangle / 2 - 2.5, 2.0);
            triangleIconTopEdge.setStroke(Color.BLACK);

        }
        triangleIconTopEdge.setFill(Color.BLACK);
        getChildren().add(triangleIconTopEdge);


        Tooltip.install(triangleIconTopEdge, topEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconTopEdge, getRotationPivot(), rotateAngle);

        if (topEdge.isDefined()) {
            triangleIconTopEdge.setVisible(true);

            if (topEdge instanceof Edge && ((Edge) topEdge).isStoneHemOrLeakGroove()) {
                lineGrooveTopEdge = new Line(0, 5, widthRectangle, 5);
                lineGrooveTopEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveTopEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveTopEdge);
                lineGrooveTopEdge.setVisible((true));
            }

        } else {
            triangleIconTopEdge.setVisible(false);
            //lineGrooveTopEdge.setVisible(false);
        }


        if (bottomEdge instanceof Edge) {
            triangleIconBottomEdge = new Polygon(
                    widthRectangle / 2, heightRectangle - 1,
                    widthRectangle / 2 - 2.5, heightRectangle - 6.0,
                    widthRectangle / 2 + 2.5, heightRectangle - 6.0);
        } else {
            triangleIconBottomEdge = new Polygon(
                    widthRectangle / 2 - 2.5, heightRectangle - 2,
                    widthRectangle / 2 + 2.5, heightRectangle - 7.0,
                    widthRectangle / 2, heightRectangle - 4.5,
                    widthRectangle / 2 - 2.5, heightRectangle - 7.0,
                    widthRectangle / 2 + 2.5, heightRectangle - 2,
                    widthRectangle / 2, heightRectangle - 4.5,
                    widthRectangle / 2 - 2.5, heightRectangle - 2);
            triangleIconBottomEdge.setStroke(Color.BLACK);
        }
        triangleIconBottomEdge.setFill(Color.BLACK);
        getChildren().add(triangleIconBottomEdge);
        Tooltip.install(triangleIconBottomEdge, bottomEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconBottomEdge, getRotationPivot(), rotateAngle);

        if (bottomEdge.isDefined()) {
            triangleIconBottomEdge.setVisible(true);

            if (bottomEdge instanceof Edge && ((Edge) bottomEdge).isStoneHemOrLeakGroove()) {
                lineGrooveBottomEdge = new Line(0, heightRectangle - 5, widthRectangle, heightRectangle - 5);
                lineGrooveBottomEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveBottomEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveBottomEdge);
                lineGrooveBottomEdge.setVisible((true));
            }

        } else {
            triangleIconBottomEdge.setVisible(false);
        }


        if (leftEdge instanceof Edge) {
            triangleIconLeftEdge = new Polygon(
                    1.0, heightRectangle / 2,
                    6.0, heightRectangle / 2 + 2.5,
                    6.0, heightRectangle / 2 - 2.5);
        } else {
            triangleIconLeftEdge = new Polygon(
                    2.0, heightRectangle / 2 - 2.5,
                    7.0, heightRectangle / 2 + 2.5,
                    4.5, heightRectangle / 2,
                    7.0, heightRectangle / 2 - 2.5,
                    2.0, heightRectangle / 2 + 2.5,
                    4.5, heightRectangle / 2,
                    2.0, heightRectangle / 2 - 2.5);
            triangleIconLeftEdge.setStroke(Color.BLACK);
        }
        triangleIconLeftEdge.setFill(Color.BLACK);
        getChildren().add(triangleIconLeftEdge);
        Tooltip.install(triangleIconLeftEdge, leftEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconLeftEdge, getRotationPivot(), rotateAngle);

        if (leftEdge.isDefined()) {
            triangleIconLeftEdge.setVisible(true);

            if (leftEdge instanceof Edge && ((Edge) leftEdge).isStoneHemOrLeakGroove()) {
                lineGrooveLeftEdge = new Line(5, 0, 5, heightRectangle);
                lineGrooveLeftEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveLeftEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveLeftEdge);
                lineGrooveLeftEdge.setVisible((true));
            }
        } else {
            triangleIconLeftEdge.setVisible(false);
        }


        if (rightEdge instanceof Edge) {
            triangleIconRightEdge = new Polygon(
                    widthRectangle - 1, heightRectangle / 2,
                    widthRectangle - 6.0, heightRectangle / 2 - 2.5,
                    widthRectangle - 6.0, heightRectangle / 2 + 2.5);
        } else {
            triangleIconRightEdge = new Polygon(
                    widthRectangle - 2, heightRectangle / 2 - 2.5,
                    widthRectangle - 7.0, heightRectangle / 2 + 2.5,
                    widthRectangle - 4.5, heightRectangle / 2,
                    widthRectangle - 7.0, heightRectangle / 2 - 2.5,
                    widthRectangle - 2, heightRectangle / 2 + 2.5,
                    widthRectangle - 4.5, heightRectangle / 2,
                    widthRectangle - 2, heightRectangle / 2 - 2.5);
            triangleIconRightEdge.setStroke(Color.BLACK);
        }
        triangleIconRightEdge.setFill(Color.BLACK);
        getChildren().add(triangleIconRightEdge);
        Tooltip.install(triangleIconRightEdge, rightEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconRightEdge, getRotationPivot(), rotateAngle);

        if (rightEdge.isDefined()) {
            triangleIconRightEdge.setVisible(true);

            if (rightEdge instanceof Edge && ((Edge) rightEdge).isStoneHemOrLeakGroove()) {
                lineGrooveRightEdge = new Line(widthRectangle - 5, 0, widthRectangle - 5, heightRectangle);
                lineGrooveRightEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveRightEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveRightEdge);
                lineGrooveRightEdge.setVisible((true));
            }
        } else {
            triangleIconRightEdge.setVisible(false);
        }
    }

    @Override
    public void updateMaterialList() {
        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        if (!choiceBoxMaterial.getItems().contains(shapeMaterial.getReceiptName())) {
            shapeMaterial = Project.getDefaultMaterial();
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());
    }

    @Override
    public void updateEdgesHeight() {
        if (edgesHeightsDefault) {
            setEdgesHeights(false, shapeDepth, Border.DEFAULT_HEIGHT);
        }
        CutDesigner.getInstance().refreshCutView();
    }

    @Override
    public double getEdgeOrBorderLength(SketchEdge edge) {

        if (edge == topEdge || edge == bottomEdge) {
            return getHorizontalSize() / Project.getCommonShapeScale();
        } else if (edge == leftEdge || edge == rightEdge) {
            return getVerticalSize() / Project.getCommonShapeScale();
        }
        return 0;
    }

    @Override
    public double getEdgesLength() {

        double length = 0;
        if (topEdge != null && (topEdge instanceof Edge)) length += width;
        if (bottomEdge != null && (bottomEdge instanceof Edge)) length += width;
        if (leftEdge != null && (leftEdge instanceof Edge)) length += height;
        if (rightEdge != null && (rightEdge instanceof Edge)) length += height;
        return length;

    }

    @Override
    public double getBordersType1Length() {

        double length = 0;
        if (topEdge != null && (topEdge instanceof Border) && topEdge.getName().indexOf("1") != -1) length += width;
        if (bottomEdge != null && (bottomEdge instanceof Border) && bottomEdge.getName().indexOf("1") != -1)
            length += width;
        if (leftEdge != null && (leftEdge instanceof Border) && leftEdge.getName().indexOf("1") != -1) length += height;
        if (rightEdge != null && (rightEdge instanceof Border) && rightEdge.getName().indexOf("1") != -1)
            length += height;
        return length;

    }

    @Override
    public double getBordersType2Length() {

        double length = 0;
        if (topEdge != null && (topEdge instanceof Border) && topEdge.getName().indexOf("2") != -1) length += width;
        if (bottomEdge != null && (bottomEdge instanceof Border) && bottomEdge.getName().indexOf("2") != -1)
            length += width;
        if (leftEdge != null && (leftEdge instanceof Border) && leftEdge.getName().indexOf("2") != -1) length += height;
        if (rightEdge != null && (rightEdge instanceof Border) && rightEdge.getName().indexOf("2") != -1)
            length += height;
        return length;

    }

    @Override
    public void rotateShape(double angle) {
        super.rotateShape(angle);

//        rotateAngle += angle;
//
//        //rotate main polygon:
//        SketchObject.rotatePolygon(polygon, getRotationPivot(), angle);
//
////        System.out.println("ROTATE SHAPE********************************* " + getRotationPivot().toString() );
//
//        //rotate connect points:
//        for(ConnectPoint cp : connectionPoints){
//            double x = cp.getTranslateX() + 5;
//            double y = cp.getTranslateY() + 5;
//            Point2D newPoint = SketchObject.rotatePoint(new Point2D(x, y), new Point2D(getRotationPivot().getX(), getRotationPivot().getY()), angle);
//            cp.setTranslateX(newPoint.getX()-5);
//            cp.setTranslateY(newPoint.getY()-5);
//        }
//
//        //rotate edges/borders zone:
//        SketchObject.rotatePolygon(topEdge, getRotationPivot(), angle);
//        SketchObject.rotatePolygon(bottomEdge, getRotationPivot(), angle);
//        SketchObject.rotatePolygon(leftEdge, getRotationPivot(), angle);
//        SketchObject.rotatePolygon(rightEdge, getRotationPivot(), angle);
//
//
//        //rotate edges/borders icons:
//        SketchObject.rotatePolygon(triangleIconTopEdge, getRotationPivot(), angle);
//        SketchObject.rotatePolygon(triangleIconBottomEdge, getRotationPivot(), angle);
//        SketchObject.rotatePolygon(triangleIconLeftEdge, getRotationPivot(), angle);
//        SketchObject.rotatePolygon(triangleIconRightEdge, getRotationPivot(), angle);
//
//        //rotate features
//        rotateFeatures(angle);

        //showConnectionPoints();
    }


    private void rotateFeatures(double angle) {
        for (AdditionalFeature feature : featuresList) {

            feature.setRotate(rotateAngle);
            double x = feature.getTranslateX() + feature.getBoundsInLocal().getWidth() / 2;
            double y = feature.getTranslateY() + feature.getBoundsInLocal().getHeight() / 2;
            Point2D newPoint = SketchObject.rotatePoint(new Point2D(x, y), new Point2D(getRotationPivot().getX(), getRotationPivot().getY()), angle);
            feature.setTranslateX(newPoint.getX() - feature.getBoundsInLocal().getWidth() / 2);
            feature.setTranslateY(newPoint.getY() - feature.getBoundsInLocal().getHeight() / 2);
        }
    }


//    @Override
//    public Point2D getRotationPivot() {
//        Point2D pivot = new Point2D(getPrefWidth()/2, getPrefHeight()/2);
//        //Point2D pivot = new Point2D(getBoundsInLocal().getWidth()/2, getBoundsInLocal().getHeight()/2);
//
//        return pivot;
//    }


    @Override
    public void addDimensionsMode(boolean mode) {
        connectionPoints.get(0).setSelectionMode(mode);
        connectionPoints.get(1).setSelectionMode(mode);
        connectionPoints.get(2).setSelectionMode(mode);
        connectionPoints.get(3).setSelectionMode(mode);
        addDimensionMode = mode;
    }


    public void initShapeMaterial(Material material, int depth) {
        shapeMaterial = material;
        this.shapeDepth = depth;
        if (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1) {

//            checkBoxSaveImage.setDisable(true);
//            checkBoxSaveImage.setSelected(false);
//            saveMaterialImageOnEdges = false;

            if (elementType == ElementTypes.TABLETOP) {
                edgeHeight = 40;
                //textFieldEdgeHeight.setText("" + 40);
            } else if (elementType == ElementTypes.WALL_PANEL) {
                edgeHeight = shapeDepth;
                //textFieldEdgeHeight.setText("" + shapeDepth);
            } else if (elementType == ElementTypes.WINDOWSILL) {
                //textFieldEdgeHeight.setText("" + 40);
                edgeHeight = 40;
            } else if (elementType == ElementTypes.FOOT) {
                //textFieldEdgeHeight.setText("" + 40);
                edgeHeight = 40;
            }

        } else {

//            checkBoxSaveImage.setDisable(false);

            edgeHeight = shapeDepth;
        }


        //ProjectHandler.getMaterialsUsesInProjectObservable().add(shapeMaterial.getName() + "#" + shapeDepth);

//        if(elementType == ElementTypes.TABLETOP)ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
//        else if(elementType == ElementTypes.WALL_PANEL)ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));

        /*System.out.println("INIT");
        System.out.println("Shape Number: " + thisShapeNumber);
        for(String s : ProjectHandler.getMaterialsUsesInProjectObservable()){
            System.out.println(s);
        }
        System.out.println("TableTops");
        for(String s : ProjectHandler.getDepthsTableTopsUsesInProjectObservable()){
            System.out.println(s);
        }
        System.out.println("WallPannels");
        for(String s : ProjectHandler.getDepthsWallPanelsUsesInProjectObservable()){
            System.out.println(s);
        }*/
    }

    public void setShapeMaterial(Material material, int depth) {

        if (material != shapeMaterial) {

            if (featuresList.size() != 0) {
                for (AdditionalFeature feature : featuresList) {
                    this.getChildren().remove(feature);
                }
                featuresList.clear();

                MainWindow.showInfoMessage(InfoMessage.MessageType.WARNING, "Дополнительные элементы были удалены");
            }

        }


        if (shapeMaterial != null)
            Project.getMaterialsUsesInProjectObservable().remove(shapeMaterial.getName() + "#" + shapeDepth);

        if (elementType == ElementTypes.TABLETOP)
            Project.getDepthsTableTopsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            Project.getDepthsWallPanelsUsesInProjectObservable().remove(String.valueOf(shapeDepth));

        shapeMaterial = material;
        this.shapeDepth = depth;

        Project.getMaterialsUsesInProjectObservable().add(shapeMaterial.getName() + "#" + shapeDepth);

        if (elementType == ElementTypes.TABLETOP)
            Project.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            Project.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));


        if (checkBoxDefaultHeights.isSelected()) {
            if (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1) {
                if (elementType == ElementTypes.TABLETOP) {
                    edgeHeight = 40;
                    textFieldEdgeHeight.setText("" + 40);
                } else if (elementType == ElementTypes.WALL_PANEL) {
                    edgeHeight = shapeDepth;
                    textFieldEdgeHeight.setText("" + shapeDepth);
                } else if (elementType == ElementTypes.WINDOWSILL) {
                    textFieldEdgeHeight.setText("" + 40);
                    edgeHeight = 40;
                } else if (elementType == ElementTypes.FOOT) {
                    textFieldEdgeHeight.setText("" + 40);
                    edgeHeight = 40;
                }
            }
        } else {
            try {
                int typedEdgeHeight = Integer.parseInt(textFieldEdgeHeight.getText());

                System.out.println("shapeDepth = " + shapeDepth);
                System.out.println("typedEdgeHeight = " + typedEdgeHeight);
                if (shapeDepth > typedEdgeHeight) {
                    edgeHeight = shapeDepth;
                    textFieldEdgeHeight.setText("" + shapeDepth);
                } else {
                    edgeHeight = typedEdgeHeight;
                }
            } catch (NumberFormatException ex) {
            }
        }


        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {

            checkBoxSaveImage.setDisable(true);
            checkBoxSaveImage.setSelected(false);
            saveMaterialImageOnEdges = false;

            if (topEdge.getType() != 1) {
                changeElementEdge(topEdge, new SketchEdge());
            }
            if (bottomEdge.getType() != 1) {
                changeElementEdge(bottomEdge, new SketchEdge());
            }
            if (leftEdge.getType() != 1) {
                changeElementEdge(leftEdge, new SketchEdge());
            }
            if (rightEdge.getType() != 1) {
                changeElementEdge(rightEdge, new SketchEdge());
            }


        } else if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                material.getName().indexOf("Натуральный камень") != -1 ||
                material.getName().indexOf("Dektone") != -1 ||
                material.getName().indexOf("Мраморный агломерат") != -1 ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {

            checkBoxSaveImage.setDisable(false);

            if (topEdge.getType() != 2) {
                changeElementEdge(topEdge, new SketchEdge());
            }
            if (bottomEdge.getType() != 2) {
                changeElementEdge(bottomEdge, new SketchEdge());
            }
            if (leftEdge.getType() != 2) {
                changeElementEdge(leftEdge, new SketchEdge());
            }
            if (rightEdge.getType() != 2) {
                changeElementEdge(rightEdge, new SketchEdge());
            }
        }


    }


    public void setEdgesHeights(boolean init, int edgeHeight, int borderHeight) {


        boolean haveBorder = true;
        if (!init) {
            Project.getEdgesHeightsUsesInProjectObservable().remove("" + this.edgeHeight);
            if (haveBorder) Project.getBordersHeightsUsesInProjectObservable().remove("" + this.borderHeight);
        }

        if (edgesHeightsDefault) {
            //

            if (shapeMaterial.getName().indexOf("Акриловый камень") != -1) {
                if (elementType == ElementTypes.TABLETOP) {
                    this.edgeHeight = 40;
                } else if (elementType == ElementTypes.WALL_PANEL) {
                    this.edgeHeight = shapeDepth;

                } else if (elementType == ElementTypes.WINDOWSILL) {
                    this.edgeHeight = 40;
                } else if (elementType == ElementTypes.FOOT) {
                    this.edgeHeight = 40;
                }
            } else {
                this.edgeHeight = shapeDepth;
            }

        } else {
            if (shapeDepth > edgeHeight) {
                this.edgeHeight = shapeDepth;
            } else {
                this.edgeHeight = edgeHeight;
            }
        }
        if (shapeMaterial != null && (shapeMaterial.getName().indexOf("Кварцевый агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Натуральный камень") != -1 ||
                shapeMaterial.getName().indexOf("Dektone") != -1 ||
                shapeMaterial.getName().indexOf("Мраморный агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Кварцекерамический камень") != -1)) {
            System.out.println(this.edgeHeight + " " + shapeDepth);
            if (this.edgeHeight == shapeDepth) {
                if (topEdge instanceof Edge && ((Edge) topEdge).getSubType() > 7)
                    changeElementEdge(topEdge, new SketchEdge());
                if (bottomEdge instanceof Edge && ((Edge) bottomEdge).getSubType() > 7)
                    changeElementEdge(bottomEdge, new SketchEdge());
                if (leftEdge instanceof Edge && ((Edge) leftEdge).getSubType() > 7)
                    changeElementEdge(leftEdge, new SketchEdge());
                if (rightEdge instanceof Edge && ((Edge) rightEdge).getSubType() > 7)
                    changeElementEdge(rightEdge, new SketchEdge());

            } else if (this.edgeHeight > shapeDepth) {
                if (topEdge instanceof Edge && ((Edge) topEdge).getSubType() <= 7)
                    changeElementEdge(topEdge, new SketchEdge());
                if (bottomEdge instanceof Edge && ((Edge) bottomEdge).getSubType() <= 7)
                    changeElementEdge(bottomEdge, new SketchEdge());
                if (leftEdge instanceof Edge && ((Edge) leftEdge).getSubType() <= 7)
                    changeElementEdge(leftEdge, new SketchEdge());
                if (rightEdge instanceof Edge && ((Edge) rightEdge).getSubType() <= 7)
                    changeElementEdge(rightEdge, new SketchEdge());
            }
        }


        if (textFieldEdgeHeight != null) textFieldEdgeHeight.setText("" + this.edgeHeight);
        this.borderHeight = borderHeight;

        Project.getEdgesHeightsUsesInProjectObservable().add("" + this.edgeHeight);
        if (haveBorder) Project.getBordersHeightsUsesInProjectObservable().add("" + this.borderHeight);
    }

    @Override
    public void setShapeDepth(int shapeDepth) {
        System.out.println("String.valueOf(shapeDepth)" + String.valueOf(shapeDepth));

        if (elementType == ElementTypes.TABLETOP)
            Project.getDepthsTableTopsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            Project.getDepthsWallPanelsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        super.setShapeDepth(shapeDepth);


        // ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        if (elementType == ElementTypes.TABLETOP)
            Project.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            Project.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));

        if (shapeDepth > edgeHeight) {
            edgeHeight = shapeDepth;
            textFieldEdgeHeight.setText("" + shapeDepth);
        }

    }

    @Override
    public Node getViewForListCell() {
        Pane pane = new Pane();

        double newScale;
        if (widthRectangle > heightRectangle) {
            newScale = 30.0 / widthRectangle;
        } else {
            newScale = 30.0 / heightRectangle;
        }


        Point2D[] points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(widthRectangle * newScale, 0.0),
                new Point2D(widthRectangle * newScale, heightRectangle * newScale),
                new Point2D(0.0, heightRectangle * newScale),
        };

        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
        );
        pane.getChildren().add(polygon);
        polygon.setFill(Color.BLUE);
        Label labelShapeNumber = new Label(String.valueOf(thisShapeNumber));
        labelShapeNumber.setStyle("-fx-text-fill:#B3B4B4;");
        /*pane.setTranslateX(0);
        pane.setTranslateY(0);*/
        pane.getChildren().add(labelShapeNumber);

        //pane.setStyle("-fx-background-color:red;");


        //polygon.setScaleX(newScale);
        //polygon.setScaleY(newScale);
        return pane;
    }

    @Override
    public Tooltip getTooltipForListCell() {
        return new Tooltip("Фигура");
    }


    public static Node getStaticViewForListCell(ElementTypes elementType) {
        Pane pane = new Pane();
        Point2D[] points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(100, 0.0),
                new Point2D(100, 100),
                new Point2D(0.0, 100),
        };
        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
        );
        pane.getChildren().add(polygon);
        if (elementType == ElementTypes.TABLETOP) polygon.setFill(TABLE_TOP_COLOR);
        else if (elementType == ElementTypes.WALL_PANEL) polygon.setFill(WALL_PANEL_COLOR);
        else if (elementType == ElementTypes.WINDOWSILL) polygon.setFill(WINDOWSILL_COLOR);
        else if (elementType == ElementTypes.FOOT) polygon.setFill(FOOT_COLOR);
        return pane;
    }

    public static Tooltip getStaticTooltipForListCell() {
        return new Tooltip("Фигура");
    }


    @Override
    public void showConnectionPoints() {
        if (containInUnion) return;
        connectionPoints.get(0).show();
        connectionPoints.get(1).show();
        connectionPoints.get(2).show();
        connectionPoints.get(3).show();
    }

    @Override
    public void hideConnectionPoints() {
        connectionPoints.get(0).hide();
        connectionPoints.get(1).hide();
        connectionPoints.get(2).hide();
        connectionPoints.get(3).hide();
    }

    @Override
    public void edgesDisable(boolean edgesDisable) {
        topEdge.setDisable(edgesDisable);
        bottomEdge.setDisable(edgesDisable);
        leftEdge.setDisable(edgesDisable);
        rightEdge.setDisable(edgesDisable);
    }

    @Override
    public boolean isConnectedToShapeOutOfUnion() {

        return false;
    }

    @Override
    public double getVerticalSize() {
        return heightRectangle;
    }

    @Override
    public double getHorizontalSize() {
        return widthRectangle;
    }


    @Override
    public CutShape getCutShapeFromJSON(JSONObject obj) {
        return null;
    }

    /* CUT SHAPE START */
    @Override
    public void refreshCutShapeView() {
        if (cutShape == null) {
            createCutShape();
            createCutShapeFeatures();
            createCutEdges();
            //System.out.println("CREATE CUT SHAPE Rectangle " + thisShapeNumber);
        } else {

            updateCutShapeView();
            updateCutShapeFeatures();
            updateCutEdgesView();

        }
    }

    private void createCutShape() {
        cutShape = new CutShape(this);

        //return polygon, areas around, connect points
        cutShape.getChildren().clear();


        //create Cut zone polygon
        Point2D[] cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {
            cutZonePolygonPoints = new Point2D[]{
                    new Point2D(-1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT, -1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT),
                    new Point2D(widthRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT, -1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT),
                    new Point2D(widthRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT, heightRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT),
                    new Point2D(-1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT, heightRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT),
            };

            cutZonePolygon = new Polygon(
                    cutZonePolygonPoints[0].getX(), cutZonePolygonPoints[0].getY(),
                    cutZonePolygonPoints[1].getX(), cutZonePolygonPoints[1].getY(),
                    cutZonePolygonPoints[2].getX(), cutZonePolygonPoints[2].getY(),
                    cutZonePolygonPoints[3].getX(), cutZonePolygonPoints[3].getY()
            );

            cutZonePolygon.setTranslateX(0.0);
            cutZonePolygon.setTranslateY(0.0);
            cutZonePolygon.setStyle("-fx-background-color: yellow; -fx-opacity: 50%");
            cutZonePolygon.setStrokeType(StrokeType.INSIDE);
            cutZonePolygon.setStroke(Color.BLACK);
            cutShape.setCutZonePolygon(cutZonePolygon);
            cutShape.getChildren().add(cutZonePolygon);
        }

        //create main poligon:
        Point2D[] polygonPoints = null;
        Polygon cutShapePolygon = null;
        {
            polygonPoints = new Point2D[]{
                    new Point2D(0.0, 0.0),
                    new Point2D(widthRectangle, 0.0),
                    new Point2D(widthRectangle, heightRectangle),
                    new Point2D(0.0, heightRectangle),
            };


            cutShapePolygon = new Polygon(
                    polygonPoints[0].getX(), polygonPoints[0].getY(),
                    polygonPoints[1].getX(), polygonPoints[1].getY(),
                    polygonPoints[2].getX(), polygonPoints[2].getY(),
                    polygonPoints[3].getX(), polygonPoints[3].getY()
            );


            cutShapePolygon.setTranslateX(0.0);
            cutShapePolygon.setTranslateY(0.0);
            cutShapePolygon.setFill(shapeColor);

            cutShapePolygon.setStroke(Color.GREY);
            //cutShapePolygon.setStrokeWidth(0.1);
            cutShapePolygon.setStrokeType(StrokeType.INSIDE);
            cutShape.setPolygon(cutShapePolygon);
            cutShape.getChildren().add(cutShapePolygon);
        }

        cutShape.setPrefWidth(cutShapePolygon.getBoundsInLocal().getWidth());
        cutShape.setPrefHeight(cutShapePolygon.getBoundsInLocal().getHeight());

        //cutShape.setStyle("-fx-background-color: yellow;");




        //create connect points:
        cutShapeConnectPoints.clear();
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints[0]);
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints[1]);
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints[2]);
        cutShapeConnectPoints.get(3).changeSetPoint(polygonPoints[3]);

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints[0]);
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints[1]);
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints[2]);
            cutShapeConnectPoints.get(3).changeSetPointShift(cutZonePolygonPoints[3]);
        }

        cutShape.setConnectPoints(cutShapeConnectPoints);
        for (ConnectPoint connectPoint : cutShapeConnectPoints) {
            cutShape.getChildren().add(connectPoint);
        }
        cutShape.hideConnectionPoints();

        //add label with shape number
        cutShape.refreshLabelNumber();

        //CREATE DIMENSIONS

        double sizeH = points[1].getX();
        double sizeY = points[2].getY();

        Label dimensionVLabel = new Label();
        dimensionVLabel.setId("dimensionVLabel");
        dimensionVLabel.setPickOnBounds(false);

        dimensionVLabel.setAlignment(Pos.CENTER);
        dimensionVLabel.setPrefWidth(sizeY);
        //dimensionVLabel.setPrefWidth(60);
        dimensionVLabel.setPrefHeight(8);
        dimensionVLabel.setTranslateX(sizeH - 11);
        dimensionVLabel.setTranslateY(sizeY);
        dimensionVLabel.setText(String.format("%.0f", sizeY / commonShapeScale));
        dimensionVLabel.setFont(Font.font(8));
        //dimensionVLabel.setRotate(-90);
        Rotate rotateV = new Rotate(-90);
        dimensionVLabel.getTransforms().add(rotateV);

        cutShape.setDimensionV(dimensionVLabel);

        Label dimensionHLabel = new Label();
        dimensionHLabel.setId("dimensionHLabel");
        dimensionHLabel.setPickOnBounds(false);
        double shiftX = 2;
        double shiftY = sizeY - 5;

        dimensionHLabel.setAlignment(Pos.CENTER);
        dimensionHLabel.setPrefWidth(sizeH);
        dimensionHLabel.setTranslateX(sizeH / 2 - dimensionHLabel.getPrefWidth() / 2);
        dimensionHLabel.setTranslateY(shiftY - 6);
        dimensionHLabel.setText(String.format("%.0f", sizeH / commonShapeScale));
        dimensionHLabel.setFont(Font.font(8));

        cutShape.setDimensionH(dimensionHLabel);

        //set rotation pivot
        cutShape.setRotationPivot(getRotationPivot());
    }

    private void createCutShapeFeatures() {
        double saveAngle = rotateAngle;
        this.rotateShape(-rotateAngle);
        cutShape.getFeaturesList().clear();
        for (AdditionalFeature feature : getFeaturesList()) {
            AdditionalFeature newFeature = AdditionalFeature.getDuplicateFeature(feature);
            cutShape.getChildren().add(newFeature);
            cutShape.getFeaturesList().add(newFeature);

            newFeature.rotate(feature.getShapeScheme().getRotate());
            newFeature.setTranslateX(feature.getTranslateX());
            newFeature.setTranslateY(feature.getTranslateY());
            newFeature.setMouseTransparent(true);
        }
        this.rotateShape(saveAngle);
    }

    private void createCutEdges() {

        createTopCutEdge();
        createBottomCutEdge();
        createLeftCutEdge();
        createRightCutEdge();
        cutShapesEdgesList.clear();
        cutShapesEdgesList.addAll(Arrays.asList(cutShapeEdgeTop, cutShapeEdgeBottom, cutShapeEdgeLeft, cutShapeEdgeRight));
        cutShape.setCutShapeEdgesList(cutShapesEdgesList);


    }

    private void createTopCutEdge() {
        cutShapeEdgeTop = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon topEdgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (topEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(widthRectangle, 0.0));
        pointsForEdge.add(new Point2D(widthRectangle, 0.0 + h));
        pointsForEdge.add(new Point2D(0.0, 0.0 + h));

        for (Point2D p : pointsForEdge) {
            topEdgePolygon.getPoints().add(p.getX());
            topEdgePolygon.getPoints().add(p.getY());
        }

//            cutShapeEdgeTop.setDepth(shapeDepth);
        topEdgePolygon.setFill(shapeColor);
        topEdgePolygon.setStroke(Color.BLACK);
        topEdgePolygon.setStrokeType(StrokeType.INSIDE);
        //topEdgePolygon.setStrokeWidth(0.1);


//            cutShapeEdgeTop.setPadding(new Insets(0));
        cutShapeEdgeTop.setPrefWidth(topEdgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeTop.setPrefHeight(topEdgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeTop.getChildren().remove(cutShapeEdgeTop.getPolygon());
        cutShapeEdgeTop.setPolygon(topEdgePolygon);
        cutShapeEdgeTop.getChildren().add(topEdgePolygon);


        //create connect points:
        ArrayList<ConnectPoint> topEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeTop);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeTop);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeTop);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeTop);

        topEdgeConnectPoints.add(point1);
        topEdgeConnectPoints.add(point2);
        topEdgeConnectPoints.add(point3);
        topEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : topEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeTop);
        }

        cutShapeEdgeTop.setConnectPoints(topEdgeConnectPoints);
        for (ConnectPoint connectPoint : topEdgeConnectPoints) {
            cutShapeEdgeTop.getChildren().add(connectPoint);
        }
        cutShapeEdgeTop.hideConnectionPoints();

        if (topEdge.isDefined()) {
            cutShapeEdgeTop.setStartCoordinate(new Point2D(0.0, 0.0 - h));
        } else {
            cutShapeEdgeTop.setStartCoordinate(null);
        }
    }

    private void createBottomCutEdge() {

        cutShapeEdgeBottom = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon bottomEdgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (topEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(widthRectangle, 0.0));
        pointsForEdge.add(new Point2D(widthRectangle, h));
        pointsForEdge.add(new Point2D(0.0, h));

        for (Point2D p : pointsForEdge) {
            bottomEdgePolygon.getPoints().add(p.getX());
            bottomEdgePolygon.getPoints().add(p.getY());
        }

        bottomEdgePolygon.setFill(shapeColor);
        bottomEdgePolygon.setStroke(Color.BLACK);
        bottomEdgePolygon.setStrokeType(StrokeType.INSIDE);
        // bottomEdgePolygon.setStrokeWidth(0.1);

        cutShapeEdgeBottom.setPrefWidth(bottomEdgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeBottom.setPrefHeight(bottomEdgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeBottom.getChildren().remove(cutShapeEdgeBottom.getPolygon());
        cutShapeEdgeBottom.setPolygon(bottomEdgePolygon);
        cutShapeEdgeBottom.getChildren().add(bottomEdgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> bottomEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeBottom);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeBottom);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeBottom);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeBottom);

        bottomEdgeConnectPoints.add(point1);
        bottomEdgeConnectPoints.add(point2);
        bottomEdgeConnectPoints.add(point3);
        bottomEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : bottomEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeBottom);
        }
        for (ConnectPoint connectPoint : bottomEdgeConnectPoints) {
            cutShapeEdgeBottom.getChildren().add(connectPoint);
        }

        cutShapeEdgeBottom.setConnectPoints(bottomEdgeConnectPoints);


        cutShapeEdgeBottom.hideConnectionPoints();

        if (bottomEdge.isDefined()) {
            cutShapeEdgeBottom.setStartCoordinate(new Point2D(0.0, heightRectangle));
        } else {
            cutShapeEdgeBottom.setStartCoordinate(null);
        }
    }

    private void createLeftCutEdge() {

        cutShapeEdgeLeft = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon leftEdgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (topEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(h, 0.0));
        pointsForEdge.add(new Point2D(h, heightRectangle));
        pointsForEdge.add(new Point2D(0.0, heightRectangle));

        for (Point2D p : pointsForEdge) {
            leftEdgePolygon.getPoints().add(p.getX());
            leftEdgePolygon.getPoints().add(p.getY());
        }

        leftEdgePolygon.setFill(shapeColor);
        leftEdgePolygon.setStroke(Color.BLACK);
        leftEdgePolygon.setStrokeType(StrokeType.INSIDE);
        //leftEdgePolygon.setStrokeWidth(0.1);

        cutShapeEdgeLeft.setPrefWidth(leftEdgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeLeft.setPrefHeight(leftEdgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeLeft.getChildren().remove(cutShapeEdgeLeft.getPolygon());
        cutShapeEdgeLeft.setPolygon(leftEdgePolygon);
        cutShapeEdgeLeft.getChildren().add(leftEdgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> leftEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeLeft);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeLeft);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeLeft);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeLeft);

        leftEdgeConnectPoints.add(point1);
        leftEdgeConnectPoints.add(point2);
        leftEdgeConnectPoints.add(point3);
        leftEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : leftEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeLeft);
        }
        for (ConnectPoint connectPoint : leftEdgeConnectPoints) {
            cutShapeEdgeLeft.getChildren().add(connectPoint);
        }

        cutShapeEdgeLeft.setConnectPoints(leftEdgeConnectPoints);
        cutShapeEdgeLeft.hideConnectionPoints();

        if (leftEdge.isDefined()) {
            cutShapeEdgeLeft.setStartCoordinate(new Point2D(0.0 - h, 0.0));
        } else {
            cutShapeEdgeLeft.setStartCoordinate(null);
        }
    }

    private void createRightCutEdge() {

        cutShapeEdgeRight = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon rightEdgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (topEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(h, 0.0));
        pointsForEdge.add(new Point2D(h, heightRectangle));
        pointsForEdge.add(new Point2D(0.0, heightRectangle));

        for (Point2D p : pointsForEdge) {
            rightEdgePolygon.getPoints().add(p.getX());
            rightEdgePolygon.getPoints().add(p.getY());
        }

        rightEdgePolygon.setFill(shapeColor);
        rightEdgePolygon.setStroke(Color.BLACK);
        rightEdgePolygon.setStrokeType(StrokeType.INSIDE);
        //rightEdgePolygon.setStrokeWidth(0.1);

        cutShapeEdgeRight.setPrefWidth(rightEdgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeRight.setPrefHeight(rightEdgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeRight.getChildren().remove(cutShapeEdgeRight.getPolygon());
        cutShapeEdgeRight.setPolygon(rightEdgePolygon);
        cutShapeEdgeRight.getChildren().add(rightEdgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeRight);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeRight);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeRight);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeRight);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeRight);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeRight.getChildren().add(connectPoint);
        }

        cutShapeEdgeRight.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeRight.hideConnectionPoints();

        if (rightEdge.isDefined()) {
            cutShapeEdgeRight.setStartCoordinate(new Point2D(widthRectangle, 0.0));
        } else {
            cutShapeEdgeRight.setStartCoordinate(null);
        }
    }

    private void updateCutShapeView() {
        double oldTranslateX = cutShape.getTranslateX();
        double oldTranslateY = cutShape.getTranslateY();

        cutShape.setSizesInfo("ШхВ "+ (int)(widthRectangle/ Project.getCommonShapeScale()) + "x" + (int)(heightRectangle/ Project.getCommonShapeScale()));

        //update Cut zone polygon
        Point2D[] cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
                {
            cutZonePolygonPoints = new Point2D[]{
                    new Point2D(-1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT, -1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT),
                    new Point2D(widthRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT, -1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT),
                    new Point2D(widthRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT, heightRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT),
                    new Point2D(-1.0 * CutDesigner.CUT_SHAPES_CUTSHIFT, heightRectangle + CutDesigner.CUT_SHAPES_CUTSHIFT),
            };
            cutZonePolygon = cutShape.getCutZonePolygon();
            cutZonePolygon.getPoints().clear();

            cutZonePolygon.getPoints().addAll(Arrays.asList(
                    cutZonePolygonPoints[0].getX(), cutZonePolygonPoints[0].getY(),
                    cutZonePolygonPoints[1].getX(), cutZonePolygonPoints[1].getY(),
                    cutZonePolygonPoints[2].getX(), cutZonePolygonPoints[2].getY(),
                    cutZonePolygonPoints[3].getX(), cutZonePolygonPoints[3].getY()
            ));
            //cutZonePolygon.setStyle("-fx-background-color: yellow; -fx-opacity: 50%");
        }

        //update main polygon
        Point2D[] polygonPoints = null;
        Polygon cutShapePolygon = null;
        {
            polygonPoints = new Point2D[]{
                    new Point2D(0.0, 0.0),
                    new Point2D(widthRectangle, 0.0),
                    new Point2D(widthRectangle, heightRectangle),
                    new Point2D(0.0, heightRectangle),
            };


            cutShapePolygon = cutShape.getPolygon();
            cutShapePolygon.getPoints().clear();

            cutShapePolygon.getPoints().addAll(Arrays.asList(
                    polygonPoints[0].getX(), polygonPoints[0].getY(),
                    polygonPoints[1].getX(), polygonPoints[1].getY(),
                    polygonPoints[2].getX(), polygonPoints[2].getY(),
                    polygonPoints[3].getX(), polygonPoints[3].getY()
            ));
        }

        //cutShapePolygon.setFill(shapeColor);


        cutShape.setPrefWidth(cutShapePolygon.getBoundsInLocal().getWidth());
        cutShape.setPrefHeight(cutShapePolygon.getBoundsInLocal().getHeight());
        //cutShape.setTranslateX(oldTranslateX);
        //cutShape.setTranslateY(oldTranslateY);

        cutShape.setStyle("-fx-background-color:blue;");

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints[0]);
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints[1]);
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints[2]);
        cutShapeConnectPoints.get(3).changeSetPoint(polygonPoints[3]);

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints[0]);
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints[1]);
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints[2]);
            cutShapeConnectPoints.get(3).changeSetPointShift(cutZonePolygonPoints[3]);
        }
//        System.out.println("cutShapeConnectPoints.size() = " + cutShapeConnectPoints.size());
//
//        for(StackTraceElement s : Thread.currentThread().getStackTrace()){
//            System.out.println(s);
//        }


        //Update dimensions labels:
        double sizeH = cutShapePolygon.getPoints().get(2);
        double sizeY = cutShapePolygon.getPoints().get(5);

        //cutShape.getDimensionVLabel().setRotate(0);
        cutShape.getDimensionVLabel().setPrefWidth(sizeY);
        cutShape.getDimensionVLabel().setTranslateX(sizeH - 11);
        cutShape.getDimensionVLabel().setTranslateY(sizeY);
        cutShape.getDimensionVLabel().setText(String.format("%.0f", sizeY / Project.getCommonShapeScale()));
        cutShape.getDimensionVLabel().toFront();


        double shiftX = 2;
        double shiftY = sizeY - 5;

        //cutShape.getDimensionHLabel().setRotate(0);
        cutShape.getDimensionHLabel().setPrefWidth(sizeH);
        cutShape.getDimensionHLabel().setTranslateX(sizeH / 2 - cutShape.getDimensionHLabel().getPrefWidth() / 2);
        cutShape.getDimensionHLabel().setTranslateY(shiftY - 6);
        cutShape.getDimensionHLabel().setText(String.format("%.0f", sizeH / Project.getCommonShapeScale()));
        cutShape.getDimensionHLabel().toFront();

        cutShape.refreshLabelNumber();
    }

    private void updateCutShapeFeatures() {
        Iterator<Node> it = cutShape.getChildren().iterator();
        while (it.hasNext()) {
            if (it.next() instanceof AdditionalFeature) {
                it.remove();
            }
        }

        createCutShapeFeatures();
    }

    private void updateCutEdgesView() {

        if (edgeHeight <= MIN_EDGE_HEIGHT_FOR_CUTSHAPE && (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1)) {

            if (topEdge instanceof Border) {
                updateTopCutEdge();
            } else {
                cutShapeEdgeTop.setStartCoordinate(null);
            }

            if (bottomEdge instanceof Border) {
                updateBottomCutEdge();
            } else {
                cutShapeEdgeBottom.setStartCoordinate(null);
            }

            if (leftEdge instanceof Border) {
                updateLeftCutEdge();
            } else {
                cutShapeEdgeLeft.setStartCoordinate(null);
            }

            if (rightEdge instanceof Border) {
                updateRightCutEdge();
            } else {
                cutShapeEdgeRight.setStartCoordinate(null);
            }

        } else {
            updateTopCutEdge();
            updateBottomCutEdge();
            updateLeftCutEdge();
            updateRightCutEdge();
        }

    }

    private void updateTopCutEdge() {

        if (topEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (topEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(widthRectangle, 0.0));
            pointsForEdge.add(new Point2D(widthRectangle, 0.0 + h));
            pointsForEdge.add(new Point2D(0.0, 0.0 + h));

            Polygon topEdgePolygon = cutShapeEdgeTop.getPolygon();
            topEdgePolygon.getPoints().clear();

            for (Point2D p : pointsForEdge) {
                topEdgePolygon.getPoints().add(p.getX());
                topEdgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeTop.setPrefWidth(topEdgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeTop.setPrefHeight(topEdgePolygon.getBoundsInLocal().getHeight());


            ArrayList<ConnectPoint> topEdgeConnectPoints = cutShapeEdgeTop.getConnectPoints();

            topEdgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            topEdgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            topEdgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            topEdgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeTop.setStartCoordinate(new Point2D(0.0, 0.0 - h));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeTop.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeTop.getStartCoordinate().getX());
                cutShapeEdgeTop.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeTop.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeTop.setStartCoordinate(null);
        }
    }

    private void updateBottomCutEdge() {

        if (bottomEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (bottomEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(widthRectangle, 0.0));
            pointsForEdge.add(new Point2D(widthRectangle, h));
            pointsForEdge.add(new Point2D(0.0, h));

            Polygon bottomEdgePolygon = cutShapeEdgeBottom.getPolygon();
            bottomEdgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                bottomEdgePolygon.getPoints().add(p.getX());
                bottomEdgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeBottom.setPrefWidth(bottomEdgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeBottom.setPrefHeight(bottomEdgePolygon.getBoundsInLocal().getHeight());

            //cutShapeEdgeBottom.setStartCoordinate(new Point2D(cutShapeEdgeBottom.getTranslateX(), cutShapeEdgeBottom.getTranslateY()));

            //create connect points:
            ArrayList<ConnectPoint> bottomEdgeConnectPoints = cutShapeEdgeBottom.getConnectPoints();
            bottomEdgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            bottomEdgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            bottomEdgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            bottomEdgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeBottom.setStartCoordinate(new Point2D(0.0, heightRectangle));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeBottom.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeBottom.getStartCoordinate().getX());
                cutShapeEdgeBottom.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeBottom.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeBottom.setStartCoordinate(null);
        }
    }

    private void updateLeftCutEdge() {

        if (leftEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (leftEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(h, 0.0));
            pointsForEdge.add(new Point2D(h, heightRectangle));
            pointsForEdge.add(new Point2D(0.0, heightRectangle));

            Polygon leftEdgePolygon = cutShapeEdgeLeft.getPolygon();
            leftEdgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                leftEdgePolygon.getPoints().add(p.getX());
                leftEdgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeLeft.setPrefWidth(leftEdgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeLeft.setPrefHeight(leftEdgePolygon.getBoundsInLocal().getHeight());

            // cutShapeEdgeLeft.setStartCoordinate(new Point2D(cutShapeEdgeLeft.getTranslateX(), cutShapeEdgeLeft.getTranslateY()));

            //connect points:
            ArrayList<ConnectPoint> leftEdgeConnectPoints = cutShapeEdgeLeft.getConnectPoints();
            leftEdgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            leftEdgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            leftEdgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            leftEdgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeLeft.setStartCoordinate(new Point2D(0.0 - h, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeLeft.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeLeft.getStartCoordinate().getX());
                cutShapeEdgeLeft.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeLeft.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeLeft.setStartCoordinate(null);
        }
    }

    private void updateRightCutEdge() {

        if (rightEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (rightEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(h, 0.0));
            pointsForEdge.add(new Point2D(h, heightRectangle));
            pointsForEdge.add(new Point2D(0.0, heightRectangle));

            Polygon rightEdgePolygon = cutShapeEdgeRight.getPolygon();
            rightEdgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                rightEdgePolygon.getPoints().add(p.getX());
                rightEdgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeRight.setPrefWidth(rightEdgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeRight.setPrefHeight(rightEdgePolygon.getBoundsInLocal().getHeight());

            //cutShapeEdgeRight.setStartCoordinate(new Point2D(cutShapeEdgeRight.getTranslateX(), cutShapeEdgeRight.getTranslateY()));

            //connect points:
            ArrayList<ConnectPoint> rightEdgeConnectPoints = cutShapeEdgeRight.getConnectPoints();
            rightEdgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            rightEdgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            rightEdgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            rightEdgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeRight.setStartCoordinate(new Point2D(widthRectangle, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeRight.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeRight.getStartCoordinate().getX());
                cutShapeEdgeRight.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeRight.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeRight.setStartCoordinate(null);
        }
    }
    /* CUT SHAPE END */


    /**
     * JOINTS
     */

    @Override
    public void clearJointsLists() {
        sideTopJointsList.clear();
        sideBottomJointsList.clear();
        sideLeftJointsList.clear();
        sideRightJointsList.clear();
    }

    @Override
    public void refreshLinesForJoints() {
        Point2D point1;
        Point2D point2;

        point1 = new Point2D(0.0, 0.0);
        point2 = new Point2D(widthRectangle, 0.0);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineTopJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(0.0, heightRectangle);
        point2 = new Point2D(widthRectangle, heightRectangle);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineBottomJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(0.0, 0.0);
        point2 = new Point2D(0.0, heightRectangle);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineLeftJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(widthRectangle, 0.0);
        point2 = new Point2D(widthRectangle, heightRectangle);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineRightJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    @Override
    public ArrayList<Line> getLineForJoints() {
        refreshLinesForJoints();
        return new ArrayList<Line>(Arrays.asList(lineTopJoint, lineBottomJoint, lineLeftJoint, lineRightJoint));
    }

    @Override
    public void addJoint(Line lineForJointSide, Joint newJoint) {

        if (lineForJointSide.equals(lineTopJoint)) {
            sideTopJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineBottomJoint)) {
            sideBottomJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineLeftJoint)) {
            sideLeftJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineRightJoint)) {
            sideRightJointsList.add(newJoint);
        }
    }

    @Override
    public ArrayList<Joint> getJoints() {
        ArrayList<Joint> list = new ArrayList<>();

        list.addAll(sideTopJointsList);
        list.addAll(sideBottomJointsList);
        list.addAll(sideLeftJointsList);
        list.addAll(sideRightJointsList);

        return list;
    }

    /**
     * JOINTS
     */


    @Override
    public void updateShapeNumber() {

        //System.out.println("THIS SHAPE NUMBER(updateShapeNumber()) = " + thisShapeNumber);

        if (labelShapeNumber == null) {
            labelShapeNumber = new Label(String.valueOf(thisShapeNumber));
            labelShapeNumber.setId("shapeNumberLabel");
            labelShapeNumber.setPrefHeight(15);
            labelShapeNumber.setPrefWidth(15);
            labelShapeNumber.setFont(Font.font(8));
            //labelShapeNumber.setStyle("-fx-text-fill:#black;");
            labelShapeNumber.setAlignment(Pos.CENTER);
        } else {
            labelShapeNumber.setText(String.valueOf(thisShapeNumber));
        }
        getChildren().remove(labelShapeNumber);
        getChildren().add(labelShapeNumber);

        labelShapeNumber.setTranslateX(0);
        labelShapeNumber.setTranslateY(0);
        labelShapeNumber.setMouseTransparent(true);

    }


    @Override
    public ArrayList<Point2D[]> getShapeEdges() {

        Point2D[] pointsEdges = new Point2D[]{
                new Point2D(points[0].getX() + getTranslateX(), points[0].getY() + getTranslateY()),
                new Point2D(points[1].getX() + getTranslateX(), points[1].getY() + getTranslateY()),
                new Point2D(points[2].getX() + getTranslateX(), points[2].getY() + getTranslateY()),
                new Point2D(points[3].getX() + getTranslateX(), points[3].getY() + getTranslateY()),
        };
        topShapeEdge = new Point2D[]{pointsEdges[0], pointsEdges[1]};
        bottomShapeEdge = new Point2D[]{pointsEdges[2], pointsEdges[3]};
        leftShapeEdge = new Point2D[]{pointsEdges[3], pointsEdges[0]};
        rightShapeEdge = new Point2D[]{pointsEdges[1], pointsEdges[2]};

        ArrayList<Point2D[]> edges = new ArrayList<>();
        edges.add(topShapeEdge);
        edges.add(bottomShapeEdge);
        edges.add(leftShapeEdge);
        edges.add(rightShapeEdge);

        return edges;
    }

    @Override
    public int getEdgeHeight() {
        return edgeHeight;
    }

    @Override
    public int getBorderHeight() {
        return borderHeight;
    }

    @Override
    public double getShapeWidth() {
        return widthRectangle / commonShapeScale;
    }

    @Override
    public double getShapeHeight() {
        return heightRectangle / commonShapeScale;
    }


    @Override
    public void unSelectShape() {
        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
           // System.err.println("CANT FILL RECTANGLE SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, widthRectangle, heightRectangle, false);
            polygon.setFill(image_pattern);
        }
    }

    @Override
    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();

        object.put("shapeNumber", thisShapeNumber);
        object.put("elementType", elementType.toString());
        object.put("shapeType", shapeType.toString());
        object.put("width", width);
        object.put("height", height);
        object.put("material", shapeMaterial.getName());
        object.put("shapeDepth", shapeDepth);
        object.put("edgesHeightsDefault", edgesHeightsDefault);
        object.put("saveMaterialImageOnEdges", saveMaterialImageOnEdges);
        object.put("edgeHeight", edgeHeight);
        object.put("borderHeight", borderHeight);
        object.put("rotateAngle", rotateTransform.getAngle());
        object.put("opacity", this.getOpacity());

        JSONObject topEdgeObject = new JSONObject();
        JSONObject bottomEdgeObject = new JSONObject();
        JSONObject leftEdgeObject = new JSONObject();
        JSONObject rightEdgeObject = new JSONObject();

        topEdgeObject.put("edgeType", (topEdge instanceof Edge) ? "edge" : "border");
        topEdgeObject.put("name", topEdge.getName());
        if (topEdge instanceof Border) {
            topEdgeObject.put("topCutType", ((Border) topEdge).getBorderCutType());
            topEdgeObject.put("sideCutType", ((Border) topEdge).getBorderSideCutType());
            topEdgeObject.put("anglesCutType", ((Border) topEdge).getBorderAnglesCutType());
        } else if (topEdge instanceof Edge) {

            topEdgeObject.put("stoneHemOrLeakGroove", ((Edge) topEdge).isStoneHemOrLeakGroove());
        }

        bottomEdgeObject.put("edgeType", (bottomEdge instanceof Edge) ? "edge" : "border");
        bottomEdgeObject.put("name", bottomEdge.getName());
        if (bottomEdge instanceof Border) {
            bottomEdgeObject.put("topCutType", ((Border) bottomEdge).getBorderCutType());
            bottomEdgeObject.put("sideCutType", ((Border) bottomEdge).getBorderSideCutType());
            bottomEdgeObject.put("anglesCutType", ((Border) bottomEdge).getBorderAnglesCutType());
        } else if (bottomEdge instanceof Edge) {
            bottomEdgeObject.put("stoneHemOrLeakGroove", ((Edge) bottomEdge).isStoneHemOrLeakGroove());
        }

        leftEdgeObject.put("edgeType", (leftEdge instanceof Edge) ? "edge" : "border");
        leftEdgeObject.put("name", leftEdge.getName());
        if (leftEdge instanceof Border) {
            leftEdgeObject.put("topCutType", ((Border) leftEdge).getBorderCutType());
            leftEdgeObject.put("sideCutType", ((Border) leftEdge).getBorderSideCutType());
            leftEdgeObject.put("anglesCutType", ((Border) leftEdge).getBorderAnglesCutType());
        } else if (leftEdge instanceof Edge) {
            leftEdgeObject.put("stoneHemOrLeakGroove", ((Edge) leftEdge).isStoneHemOrLeakGroove());
        }

        rightEdgeObject.put("edgeType", (rightEdge instanceof Edge) ? "edge" : "border");
        rightEdgeObject.put("name", rightEdge.getName());
        if (rightEdge instanceof Border) {
            rightEdgeObject.put("topCutType", ((Border) rightEdge).getBorderCutType());
            rightEdgeObject.put("sideCutType", ((Border) rightEdge).getBorderSideCutType());
            rightEdgeObject.put("anglesCutType", ((Border) rightEdge).getBorderAnglesCutType());
        } else if (rightEdge instanceof Edge) {
            rightEdgeObject.put("stoneHemOrLeakGroove", ((Edge) rightEdge).isStoneHemOrLeakGroove());
        }
        System.out.println(rightEdge);
        System.out.println(rightEdge);

        object.put("topEdge", topEdgeObject);
        object.put("bottomEdge", bottomEdgeObject);
        object.put("leftEdge", leftEdgeObject);
        object.put("rightEdge", rightEdgeObject);

        JSONArray sketchDesignerXY = new JSONArray();
        sketchDesignerXY.add(getTranslateX());
        sketchDesignerXY.add(getTranslateY());
        object.put("sketchDesignerXY", sketchDesignerXY);

        rotateFeatures(-rotateAngle);
        JSONArray featuresArray = new JSONArray();
        for (AdditionalFeature feature : featuresList) {
            featuresArray.add(feature.getJsonView());
        }
        rotateFeatures(rotateAngle);
        object.put("featuresArray", featuresArray);

        return object;
    }

    @Override
    public void initFromJson(JSONObject jsonObject) {
        thisShapeNumber = ((Long) jsonObject.get("shapeNumber")).intValue();
        elementType = ElementTypes.valueOf(((String) jsonObject.get("elementType")));
        shapeType = ShapeType.valueOf(((String) jsonObject.get("shapeType")));
        width = ((Double) jsonObject.get("width")).doubleValue();
        height = ((Double) jsonObject.get("height")).doubleValue();

        edgesHeightsDefault = ((Boolean) jsonObject.get("edgesHeightsDefault")).booleanValue();
        saveMaterialImageOnEdges = ((Boolean) jsonObject.get("saveMaterialImageOnEdges")).booleanValue();
        this.setOpacity(((Double) jsonObject.get("opacity")).doubleValue());
//        edgeHeight = ((Long)jsonObject.get("edgeHeight")).intValue();
//        borderHeight = ((Long)jsonObject.get("borderHeight")).intValue();
        setEdgesHeights(false, ((Long) jsonObject.get("edgeHeight")).intValue(), ((Long) jsonObject.get("borderHeight")).intValue());


        //shapeDepth = ((Long)jsonObject.get("shapeDepth")).intValue();
        //if(elementType == ElementTypes.TABLETOP)ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        //else if(elementType == ElementTypes.WALL_PANEL)ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));

        //System.out.println("initFromJson shapeNumber = " + thisShapeNumber);
        String materialName = ((String) jsonObject.get("material"));
        for (Material material : Project.getMaterialsListInProject()) {
            if (materialName.equals(material.getName())) {
                //setShapeMaterial(material);
                //shapeMaterial = material;
                //ProjectHandler.getMaterialsUsesInProjectObservable().add(shapeMaterial.getName() + "#" + shapeDepth);
                setShapeMaterial(material, ((Long) jsonObject.get("shapeDepth")).intValue());
                //shapeMaterial = material;
                break;
            }
        }


        //init edges:
        JSONObject topEdgeObject = ((JSONObject) jsonObject.get("topEdge"));
        JSONObject bottomEdgeObject = ((JSONObject) jsonObject.get("bottomEdge"));
        JSONObject leftEdgeObject = ((JSONObject) jsonObject.get("leftEdge"));
        JSONObject rightEdgeObject = ((JSONObject) jsonObject.get("rightEdge"));

        int edgeType = 0;
        if (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1) {
            edgeType = 1;
        } else if (shapeMaterial.getName().indexOf("Кварцевый агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Натуральный камень") != -1 ||
                shapeMaterial.getName().indexOf("Dektone") != -1 ||
                shapeMaterial.getName().indexOf("Мраморный агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Кварцекерамический камень") != -1) {
            edgeType = 2;
        }

        String topEdgeType = (String) topEdgeObject.get("edgeType");
        String topEdgeName = (String) topEdgeObject.get("name");
        if (topEdgeType.equals("edge")) {
            Edge newEdge = new Edge(topEdgeName, edgeType);
            newEdge.setStoneHemOrLeakGroove(((Boolean) topEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
            changeElementEdge(topEdge, newEdge);

        } else {
            Border border = new Border(topEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) topEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) topEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) topEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }

            changeElementEdge(topEdge, border);
        }

//        System.out.println("Shape #:" + thisShapeNumber + " topEdge.isSelected():" + (new Border(topEdgeName)).isSelected() + " topEdge.getName():" + topEdge.getName());

        String bottomEdgeType = (String) bottomEdgeObject.get("edgeType");
        String bottomEdgeName = (String) bottomEdgeObject.get("name");
        if (bottomEdgeType.equals("edge")) {
            Edge newEdge = new Edge(bottomEdgeName, edgeType);
            newEdge.setStoneHemOrLeakGroove(((Boolean) bottomEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
            changeElementEdge(bottomEdge, newEdge);

        } else {
            Border border = new Border(bottomEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) bottomEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) bottomEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) bottomEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(bottomEdge, border);
        }

        String leftEdgeType = (String) leftEdgeObject.get("edgeType");
        String leftEdgeName = (String) leftEdgeObject.get("name");
        if (leftEdgeType.equals("edge")) {
            Edge newEdge = new Edge(leftEdgeName, edgeType);
            newEdge.setStoneHemOrLeakGroove(((Boolean) leftEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
            changeElementEdge(leftEdge, newEdge);

        } else {
            Border border = new Border(leftEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) leftEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) leftEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) leftEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }

            changeElementEdge(leftEdge, border);
        }

        String rightEdgeType = (String) rightEdgeObject.get("edgeType");
        String rightEdgeName = (String) rightEdgeObject.get("name");
        if (rightEdgeType.equals("edge")) {
            Edge newEdge = new Edge(rightEdgeName, edgeType);
            newEdge.setStoneHemOrLeakGroove(((Boolean) rightEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
            changeElementEdge(rightEdge, newEdge);

        } else {
            Border border = new Border(rightEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) rightEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) rightEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) rightEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }

            changeElementEdge(rightEdge, border);
        }


//        System.out.println("Shape #:" + thisShapeNumber + " bottomEdge.isSelected():" + bottomEdge.isSelected() + " bottomEdge.getName():" + bottomEdge.getName());
//        System.out.println("Shape #:" + thisShapeNumber + " leftEdge.isSelected():" + leftEdge.isSelected() + " leftEdge.getName():" + leftEdge.getName());
//        System.out.println("Shape #:" + thisShapeNumber + " rightEdge.isSelected():" + rightEdge.isSelected() + " rightEdge.getName():" + rightEdge.getName());


        JSONArray sketchDesignerXY = (JSONArray) (jsonObject.get("sketchDesignerXY"));
        this.setTranslateX(((Double) sketchDesignerXY.get(0)).doubleValue());
        this.setTranslateY(((Double) sketchDesignerXY.get(1)).doubleValue());


        if (shapeMaterial.getName().equals(Project.getDefaultMaterial().getName())) {
            materialDefault = true;
        } else {
            materialDefault = false;
        }
        widthRectangle = width / 10.0;
        heightRectangle = height / 10.0;


        JSONArray featuresArray = (JSONArray) (jsonObject.get("featuresArray"));
        for (Object o : featuresArray) {
            JSONObject jsonObj = (JSONObject) o;
            AdditionalFeature feature = AdditionalFeature.initFromJson(jsonObj, this);

            addFeature(feature);

        }
        //rotateFeatures(-((Double)jsonObject.get("rotateAngle")).doubleValue());

        //rotateAngle = ((Double)jsonObject.get("rotateAngle")).doubleValue();
        rebuildShapeView();
        refreshShapeSettings();


        rotateShape(((Double) jsonObject.get("rotateAngle")).doubleValue());


    }
}
