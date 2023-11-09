package ru.koreanika.tableDesigner.Items;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.sketchDesigner.Shapes.*;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class EdgeItem extends TableDesignerItem implements Cuttable, DependOnMaterial {

    /**
     * static variables
     */
    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalItemsList();

    /**
     * instance variables
     */
    //private ArrayList<SketchShape> sketchShapeArrayList = new ArrayList<>();
    private ArrayList<ArrayList<SketchShape>> sketchShapeArrayList = new ArrayList<>();

    private ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<>();
    private ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<>();

    private static ArrayList<EdgeType> edgeTypesAcryl = new ArrayList<>();
    private static ArrayList<EdgeType> edgeTypesQuarz = new ArrayList<>();
    private static ArrayList<EdgeType> edgeTypesWood = new ArrayList<>();

    Label labelRowNumber, labelName, labelMaterial, labelDepth, labelLength, labelHeight, labelQuantity, labelRowPrice;
    ImageView imageView;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int depth;
    int height;
    int type;
    double length;
    Image image;
    String name;

    double workCoefficient;
    int workCoefficientIndex;

    public EdgeItem(ArrayList<ArrayList<Point2D>> cutShapesCoordinates, ArrayList<ArrayList<Double>> cutShapesAngles,
                    int quantity, Material material, int depth, int height, int type, double length,
                    double workCoefficient,int workCoefficientIndex, String name) {

        this.cutShapesAngles = cutShapesAngles;
        this.cutShapesCoordinates = cutShapesCoordinates;
        this.quantity = quantity;
        this.material = material;
        this.depth = depth;
        this.height = height;
        this.type = type;
        this.length = length;
        this.workCoefficient = workCoefficient;
        this.workCoefficientIndex = workCoefficientIndex;
        this.name = name;

        File file;
        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {
            file = new File("edges_resources/" + "edge_" + type + ".png");
        }else if (material.getName().contains("Массив") || material.getName().contains("Массив_шпон")) {
            file = new File("edges_resources/wood/" + type + "_200.png");
        } else {
            file = new File("edges_resources/" + "edge_1_" + type + ".png");
        }

        try {
            image = new Image(file.toURI().toURL().toString());

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }


        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/EdgeRow.fxml")
        );

        try {
            anchorPaneTableRow = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rowControlElementsInit();
        rowControlElementLogicInit();

        cardControlElementsInit();
        cardControlElementLogicInit();

        updateItemView();

        //create shape and add it to sketchDesignerList

//        System.out.println("length = " + length);
//        System.out.println("height = " + height);
//        System.out.println("depth = " + depth);
        if (length != 0 && height > depth) {
            int pieces = (int) ((length) / material.getMaterialWidth());
            if (pieces == 0) pieces = 1;
            else pieces += 1;

            //System.out.println("material.getMaterialWidth() EDGE  = " + material.getMaterialWidth());
            //System.out.println("pieces EDGE = " + pieces);
            if(!material.isUseMainSheets()){
                pieces = 1;
            }

            for (int i = 0; i < quantity; i++) {

                ArrayList<SketchShape> edgeItemShapes = new ArrayList<>(pieces);
                for (int j = 0; j < pieces; j++) {
                    double w = material.getMaterialWidth();
                    if (j == pieces - 1) {
                        w = length - (material.getMaterialWidth()) * (pieces - 1);
                    }


                    //System.out.println("w = " + w);

                    SketchShape shape = new SketchShapeRectangle(ElementTypes.TABLETOP, material, depth, w, height);
                    shape.setWorkCoefficient(workCoefficient);
                    shape.setProductName(name);

                    SketchDesigner.getSketchShapesList().add(shape);
                    //add shape to cutPane
                    CutShape cutShape = shape.getCutShape();
                    CutDesigner.getInstance().getCutShapesList().add(cutShape);
                    CutDesigner.getInstance().usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
                    CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);

                    edgeItemShapes.add(shape);

                    //System.out.println("size()" + this.cutShapesCoordinates.get(i).size() + " i=" + i + " j=" + j);
                    //System.out.println("get(j)" + this.cutShapesCoordinates.get(i).get(j));
                    cutShape.setTranslateX(this.cutShapesCoordinates.get(i).get(j).getX());
                    cutShape.setTranslateY(this.cutShapesCoordinates.get(i).get(j).getY());

                    cutShape.rotateShapeLocal(cutShapesAngles.get(i).get(j).doubleValue());

                }
                sketchShapeArrayList.add(edgeItemShapes);
            }
        }


    }


    public int getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(EdgeItem item) {

        EdgeItem oldEdgeItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldEdgeItem.material;
        } else {
            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType()) && defaultMaterial.getDepths().contains("" + oldEdgeItem.depth)) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterials()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType()) && material.getDepths().contains("" + oldEdgeItem.depth)) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldEdgeItem.removeThisItem();
                    return;
                }
            }
        }


        if (newMaterial.getDepths().contains("" + oldEdgeItem.depth)) {

            ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<ArrayList<Point2D>>();
            ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<ArrayList<Double>>();

//            System.out.println("newMaterial.getMaterialWidth() = " + newMaterial.getMaterialWidth());
//            System.out.println("oldEdgeItem.material.getMaterialWidth() = " + oldEdgeItem.material.getMaterialWidth());

            if(newMaterial.getMaterialWidth() != oldEdgeItem.material.getMaterialWidth()){
                int pieces = (int) ((oldEdgeItem.length) / newMaterial.getMaterialWidth());
                if (pieces == 0) pieces = 1;
                else pieces += 1;

                for(int j=0;j< oldEdgeItem.quantity;j++){
                    ArrayList<Point2D> coordList = new ArrayList<>(5);
                    ArrayList<Double> angleList = new ArrayList<>(5);
                    for (int i = 0; i < pieces; i++) {
                        coordList.add(new Point2D(0, 0));
                        angleList.add(Double.valueOf(0));
                    }
                    cutShapesCoordinates.add(coordList);
                    cutShapesAngles.add(angleList);
                }
            }else{
                for (ArrayList<Point2D> arr1 : oldEdgeItem.cutShapesCoordinates) {

                    ArrayList<Point2D> arrayPoints = new ArrayList<>();
                    for (Point2D p : arr1) {
                        arrayPoints.add(new Point2D(p.getX(), p.getY()));
                    }
                    cutShapesCoordinates.add(arrayPoints);
                }



                for (ArrayList<Double> arr1 : oldEdgeItem.cutShapesAngles) {

                    ArrayList<Double> arrayAngles = new ArrayList<>();
                    for (Double d : arr1) {
                        arrayAngles.add(Double.valueOf(d.doubleValue()));
                    }
                    cutShapesAngles.add(arrayAngles);
                }
            }




            EdgeItem newEdgeItem = new EdgeItem(cutShapesCoordinates, cutShapesAngles, oldEdgeItem.quantity,
                    newMaterial, oldEdgeItem.depth, oldEdgeItem.height, oldEdgeItem.type, oldEdgeItem.length,
                    oldEdgeItem.workCoefficient, oldEdgeItem.workCoefficientIndex, oldEdgeItem.name);

            oldEdgeItem.removeThisItem();
            tableDesignerItemsList.add(newEdgeItem);
        } else {
            oldEdgeItem.removeThisItem();
        }
    }

    @Override
    public void updateWorkCoefficient() {
        workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

        for(ArrayList<SketchShape> sketchShapes : sketchShapeArrayList){
            for(SketchShape sketchShape : sketchShapes){
                sketchShape.setWorkCoefficient(workCoefficient);
            }
        }
    }

    public int getDepth() {
        return depth;
    }

    public double getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }


    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        File file;
        String imgPath = "";
        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {
            imgPath = "edges_resources/" + "edge_" + type + ".png";
        }else if(material.getName().contains("Массив") || material.getName().contains("Массив_шпон")){
            imgPath = "edges_resources/wood/" + type + "_200.png";
        } else {
            imgPath = "edges_resources/" + "edge_1_" + type + ".png";
        }
        file = new File(imgPath);
        try {
            imagesList.put("Кромка#" + imgPath, new ImageView(new Image(file.toURI().toURL().toString())));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);

        for (ArrayList<SketchShape> shapeList : sketchShapeArrayList) {
            for (SketchShape shape : shapeList) {
                CutDesigner.getInstance().getCutPane().deleteCutShape(shape.getShapeNumber());
                SketchDesigner.getSketchShapesList().remove(shape);
            }
        }

        sketchShapeArrayList.clear();
        cutShapesCoordinates.clear();
        cutShapesAngles.clear();
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            EdgeItem.exitFromEditMode(this);
        }
    }

    public static ObservableList<TableDesignerItem> getTableDesignerItemsList() {
        return tableDesignerItemsList;
    }

    @Override
    public void setRowNumber(int number) {
        labelRowNumber.setText("" + number);
    }

    @Override
    public AnchorPane getTableView() {
        return anchorPaneTableRow;
    }


    /**
     * Table ROW part
     */


    private void rowControlElementsInit() {

        HBox hBox = (HBox) anchorPaneTableRow.lookup("#hBox");
        labelRowNumber = (Label) hBox.getChildren().get(0);
        labelName = (Label) hBox.getChildren().get(1);
        AnchorPane anchorPaneImageView = (AnchorPane) hBox.getChildren().get(2);
        imageView = (ImageView) anchorPaneImageView.lookup("#imageView");
        labelMaterial = (Label) hBox.getChildren().get(3);
        labelDepth = (Label) hBox.getChildren().get(4);
        labelLength = (Label) hBox.getChildren().get(5);
        labelHeight = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");



        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelMaterial, Priority.ALWAYS);
        HBox.setHgrow(labelDepth, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelHeight, Priority.ALWAYS);
        HBox.setHgrow(labelQuantity, Priority.ALWAYS);
        HBox.setHgrow(labelRowPrice, Priority.ALWAYS);
    }

    private void rowControlElementLogicInit() {

        btnPlus.setOnAction(event -> btnPlusClicked(event));

        btnMinus.setOnAction(event -> btnMinusClicked(event));

        btnDelete.setOnAction(event -> btnDeleteClicked(event));

        btnEdit.setOnAction(event -> btnEditClicked(event));
    }

    private void cardControlElementLogicInit() {

        btnPlusCard.setOnAction(event -> btnPlusClicked(event));

        btnMinusCard.setOnAction(event -> btnMinusClicked(event));

        btnDeleteCard.setOnAction(event -> btnDeleteClicked(event));

        btnEditCard.setOnAction(event -> btnEditClicked(event));
    }

    private void btnPlusClicked(ActionEvent event){
        quantity++;

        //create new shape
        ArrayList<SketchShape> edgeItemShapes = new ArrayList<>(5);
        ArrayList<Point2D> coordList = new ArrayList<>();
        ArrayList<Double> angleList = new ArrayList<>();

        if (length != 0 && height > depth) {
            int pieces = (int) ((length) / material.getMaterialWidth());
            if (pieces == 0) pieces = 1;
            else pieces += 1;

            if(!material.isUseMainSheets()){
                pieces = 1;
            }


            for (int j = 0; j < pieces; j++) {
                double w = material.getMaterialWidth();
                if (j == pieces - 1) {
                    w = length - (material.getMaterialWidth()) * (pieces - 1);
                }
                SketchShape shape = new SketchShapeRectangle(ElementTypes.TABLETOP, material, depth, w, height);
                shape.setWorkCoefficient(workCoefficient);
                shape.setProductName(name);
                SketchDesigner.getSketchShapesList().add(shape);
                //add shape to cutPane
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().add(cutShape);
                CutDesigner.getInstance().usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);
                edgeItemShapes.add(shape);
                coordList.add(new Point2D(0, 0));
                cutShape.setTranslateX(0);
                cutShape.setTranslateY(0);
                angleList.add(Double.valueOf(0));
                cutShape.rotateShapeLocal(0);
            }


            sketchShapeArrayList.add(edgeItemShapes);
            cutShapesCoordinates.add(coordList);
            cutShapesAngles.add(angleList);
        }


        updateItemView();
    }
    private void btnMinusClicked(ActionEvent event){
        if (quantity == 1) return;
        quantity--;

        //delete one shape:

        if(sketchShapeArrayList.size() != 0){
            ArrayList<SketchShape> shapesItemList = sketchShapeArrayList.remove(sketchShapeArrayList.size() - 1);
            for (SketchShape shape : shapesItemList) {

                SketchDesigner.getSketchShapesList().remove(shape);
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().remove(cutShape);
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().remove(cutShape);
            }
            //System.out.println("cutShapesCoordinates.size() = " + cutShapesCoordinates.size());
            cutShapesCoordinates.remove(cutShapesCoordinates.size() - 1);
            //System.out.println("cutShapesAngles.size() = " + cutShapesAngles.size());
            cutShapesAngles.remove(cutShapesAngles.size() - 1);
        }


        updateItemView();
    }
    private void btnDeleteClicked(ActionEvent event){
        if(editModeProperty.get()) exitFromEditMode(this);

        tableDesignerItemsList.remove(this);

        for (ArrayList<SketchShape> shapesItemList : sketchShapeArrayList) {

            for (SketchShape shape : shapesItemList) {
                SketchDesigner.getSketchShapesList().remove(shape);
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().remove(cutShape);
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().remove(cutShape);
            }
        }
        sketchShapeArrayList.clear();
        cutShapesCoordinates.clear();
        cutShapesAngles.clear();
    }
    private void btnEditClicked(ActionEvent event){
        //setting change mode to edit
        for(TableDesignerItem item : TableDesigner.getTableDesignerAllItemsList()){
            item.setEditModeProperty(false);
        }
        editModeProperty.setValue(true);
        enterToEditMode(this);
    }

    public void updateItemView(){

        labelRowNumber.setText("");
        labelName.setText(name);
        imageView.setImage(image);
        labelMaterial.setText(material.getReceiptName());
        labelDepth.setText("" + depth);
        labelLength.setText(String.format(Locale.ENGLISH, "%.0fмм", length));
        labelHeight.setText("" + height + "мм");
        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText(name);
        tooltipNameCard.setText(name);
        imageViewBackCard.setImage(image);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Толщина материала");
        labelValue2Card.setText("" + depth + " мм");

        labelName3Card.setText("Длина");
        labelValue3Card.setText("" + (int)length + " мм");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("" + (int)height + " мм");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getEdgesCurrency();
        String units = "мм.";
        double priceForOne = -1.0;

        priceForOne = material.getEdgesAndPrices().get(type).doubleValue();

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();



        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000) * quantity) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000)) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000) * quantity) + Currency.RUR_SYMBOL);

    }

    /**
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static VBox vBox = null;
    private static AnchorPane anchorPaneShape = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK"), btnCancel = new Button("Отмена");

    private static ChoiceBox<String> choiceBoxMaterial;
    private static ChoiceBox<String> choiceBoxDepth;
    private static ChoiceBox<String> choiceBoxSurface;
    private static ComboBox<ShapeView> comboBoxShape;
    private static ComboBox<EdgeType> comboBoxEdgeType;
    private static TextField textFieldHeight, textFieldLength;
    private static Label labelPrice;

    private static boolean heightOk = true, lengthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/EdgeSettings.fxml"));

            try {
                anchorPaneSettingsView = fxmlLoader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            settingsControlElementsInit();
            settingsControlElementsLogicInit();
        }
        settingsControlElementsRefresh();

        return anchorPaneSettingsView;
    }

    private static void settingsControlElementsInit() {

        btnApply.getStyleClass().add("btnBrown");
        btnCancel.getStyleClass().add("btnBrown");

        vBox = (VBox) anchorPaneSettingsView.lookup("#vBox");

        anchorPaneShape = (AnchorPane) anchorPaneSettingsView.lookup("#anchorPaneShape");


        choiceBoxMaterial = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxMaterial");
        choiceBoxDepth = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxDepth");
        choiceBoxSurface = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxSurface");
        comboBoxShape = (ComboBox<ShapeView>) anchorPaneSettingsView.lookup("#comboBoxShape");
        comboBoxEdgeType = (ComboBox<EdgeType>) anchorPaneSettingsView.lookup("#comboBoxEdgeType");

        textFieldHeight = (TextField) anchorPaneSettingsView.lookup("#textFieldHeight");
        textFieldLength = (TextField) anchorPaneSettingsView.lookup("#textFieldLength");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }

        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select(0);

        choiceBoxSurface.getItems().add("Прямолинейная");
        choiceBoxSurface.getItems().add("Криволинейная");
        choiceBoxSurface.getSelectionModel().select(0);

        vBox.getChildren().remove(anchorPaneShape);

        comboBoxShape.setCellFactory(new Callback<ListView<ShapeView>, ListCell<ShapeView>>() {
            @Override
            public ListCell<ShapeView> call(ListView<ShapeView> param) {

                ListCell<ShapeView> cell = new ListCell<ShapeView>() {
                    @Override
                    protected void updateItem(ShapeView item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            //setText(item.getName());
                            setGraphic(item.getImage());
                            //setTooltip(item.getTooltip());
                        }
                    }
                };
                return cell;
            }
        });

        comboBoxShape.setButtonCell(new ListCell<ShapeView>() {

            @Override
            protected void updateItem(ShapeView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText(item.getName());
                    //setText("#" + item.getType());
                    setGraphic(item.getImage());
                }
            }
        });


        comboBoxEdgeType.setCellFactory(new Callback<ListView<EdgeType>, ListCell<EdgeType>>() {
            @Override
            public ListCell<EdgeType> call(ListView<EdgeType> param) {

                ListCell<EdgeType> cell = new ListCell<EdgeType>() {
                    @Override
                    protected void updateItem(EdgeType item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            //setText("#" + item.getType());
                            setGraphic(item.getImage());
                            setTooltip(item.getTooltip());
                        }
                    }
                };
                return cell;
            }
        });

        comboBoxEdgeType.setButtonCell(new ListCell<EdgeType>() {

            @Override
            protected void updateItem(EdgeType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    //setText("#" + item.getType());
                    setGraphic(item.getImageCopy());
                    setTooltip(item.getTooltipCopy());
                }
            }
        });

        edgeTypesAcryl.clear();
        edgeTypesAcryl.add(null); //for shift elements on 1 position
        for(int i = 1; i <= 17; i++){
            edgeTypesAcryl.add(new EdgeType(1, i));
        }

        edgeTypesQuarz.clear();
        edgeTypesQuarz.add(null); //for shift elements on 1 position
        for(int i = 1; i <= 27; i++){
            edgeTypesQuarz.add(new EdgeType(2, i));
        }

        edgeTypesWood.clear();
        edgeTypesWood.add(null); //for shift elements on 1 position
        for(int i = 1; i <= 17; i++){
            edgeTypesWood.add(new EdgeType(3, i));
        }

        if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != 0 || Project.getDefaultMaterial().getMainType().indexOf("Полиэфирный камень") != 0) {
            for (int i = 1; i <= 17; i++) {
                comboBoxEdgeType.getItems().add(edgeTypesAcryl.get(i));
            }
        }else if(Project.getDefaultMaterial().getMainType().contains("Массив") ||
                Project.getDefaultMaterial().getMainType().contains("Массив_шпон")){
            for (int i = 1; i <= 3; i++) {
                comboBoxEdgeType.getItems().add(edgeTypesWood.get(i));
            }
        } else {
            for (int i = 1; i <= 27; i++) {
                if(Project.getDefaultMaterial().getEdgesAndPrices().get(i) != null){
                    comboBoxEdgeType.getItems().add(edgeTypesQuarz.get(i));
                }

            }
        }
        comboBoxEdgeType.getSelectionModel().select(0);
        //comboBoxEdgeType.setTooltip(comboBoxEdgeType.getSelectionModel().getSelectedItem().getTooltip());

        Material selectedMaterial = null;
        int depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());
        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {
                selectedMaterial = material;
            }
        }
        if (selectedMaterial.getName().indexOf("Акриловый камень") != -1 || selectedMaterial.getName().indexOf("Полиэфирный камень") != -1) {
            textFieldHeight.setText("40");
        }else if(selectedMaterial.getName().contains("Массив") || selectedMaterial.getName().contains("Массив_шпон")){
            textFieldHeight.setText("" + depth);
        }else{
            textFieldHeight.setText("" + depth);
        }


        textFieldLength.setText("600");

        settingsControlElementsRefresh();

    }


    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));


        choiceBoxMaterial.setOnAction(event -> {

            Material selectedMaterial = null;

            comboBoxEdgeType.getItems().clear();

            for (Material material : Project.getMaterials()) {
                if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {

                    selectedMaterial = material;

                    if (material.getName().indexOf("Акриловый камень") != -1 ||
                            material.getName().indexOf("Полиэфирный камень") != -1) {

                        for (int i = 1; i <= 17; i++) {
                            comboBoxEdgeType.getItems().add(edgeTypesAcryl.get(i));
                        }
                        choiceBoxSurface.setDisable(true);
                    }if (material.getName().contains("Массив") || material.getName().contains("Массив_шпон")) {

                        for (int i = 1; i <= 3; i++) {
                            comboBoxEdgeType.getItems().add(edgeTypesWood.get(i));
                        }
                        choiceBoxSurface.setDisable(true);
                    } else {

                        for (int i = 1; i <= 27; i++) {
                            if(material.getEdgesAndPrices().get(i) != null){
                                comboBoxEdgeType.getItems().add(edgeTypesQuarz.get(i));
                            }

                        }

                        choiceBoxSurface.setDisable(false);
                    }
                    comboBoxEdgeType.getSelectionModel().select(0);

                    //update depths:
                    choiceBoxDepth.getItems().clear();
                    choiceBoxDepth.getItems().addAll(material.getDepths());
                    choiceBoxDepth.getSelectionModel().select(0);

                    break;
                }
            }

            //edge logic from .xls file from Marsel
            if(selectedMaterial != null){
                if (selectedMaterial.getName().indexOf("Акриловый камень") != -1 || selectedMaterial.getName().indexOf("Полиэфирный камень") != -1) {
                    textFieldHeight.setDisable(false);
                    textFieldHeight.setText("40");
                }else if (selectedMaterial.getName().contains("Массив") || selectedMaterial.getName().contains("Массив_шпон")) {
                    textFieldHeight.setDisable(false);
                    textFieldHeight.setText(choiceBoxDepth.getValue());
                }else{
                    textFieldHeight.setDisable(true);
                    textFieldHeight.setText(choiceBoxDepth.getValue());
                }
            }

            try {
                checkCorrectSettings();
            }catch(NumberFormatException|NullPointerException e){
                System.err.println("EDGE ITEM settings. NumberFormatException.");
            }


            updatePriceInSettings();
        });

        choiceBoxDepth.setOnAction(event -> {

            Material selectedMaterial = null;
            if(comboBoxEdgeType.getSelectionModel().getSelectedItem() == null) return;
            int edgeType = comboBoxEdgeType.getSelectionModel().getSelectedItem().getType();

            System.out.println("EDGE ITEM CHOICE BOX DEPTH");
            for (Material material : Project.getMaterials()) {
                if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {

                    selectedMaterial = material;
                }
//                System.out.println(material);
            }

            if (selectedMaterial.getName().indexOf("Кварцевый агломераат") != -1 ||
                    selectedMaterial.getName().indexOf("Натуральный камень") != -1 ||
                    selectedMaterial.getName().indexOf("Dektone") != -1 ||
                    selectedMaterial.getName().indexOf("Мраморный агломерат") != -1 ||
                    selectedMaterial.getName().indexOf("Кварцекерамический камень") != -1) {
                if (edgeType >= 1 && edgeType <= 7) {
                    textFieldHeight.setText(choiceBoxDepth.getValue());
                }
            }

            //edge logic from .xls file from Marsel
            try {
                checkCorrectSettings();
            }catch(NumberFormatException|NullPointerException e){
                System.err.println("EDGE ITEM settings. NumberFormatException.");
            }
        });

        choiceBoxSurface.setOnAction(event -> {
            String selectedItem = choiceBoxSurface.getSelectionModel().getSelectedItem();
            if(selectedItem == null) return;

            if(selectedItem.equals("Прямолинейная")){
                vBox.getChildren().remove(anchorPaneShape);
                textFieldLength.setDisable(false);

                textFieldLength.setText("600");
            }else if(selectedItem.equals("Криволинейная")){
                vBox.getChildren().add(3, anchorPaneShape);
                textFieldLength.setDisable(true);

                ShapeView shapeView = comboBoxShape.getSelectionModel().getSelectedItem();
                if(shapeView == null) return;

                //double len = 2 * shapeView.getR() * Math.PI * shapeView.getCoefficient();

                /** Find length */
                int a, b;
                int height = 40;
                {
                    double R = shapeView.R;
                    double v1 = 10;
                    double v2 = 60;

                    try {
                        height = Integer.parseInt(textFieldHeight.getText());
                    }catch (NumberFormatException e){
                        height = 60;
                    }

                    if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE ||
                            shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF ||
                            shapeView.stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS){

//                    length = R * Math.pow(2, 0.5) + 2* v1 * Math.pow(2, 0.5) + 5;
//                    length *= count;
//                    length = (int)((R - R*Math.pow(0.5, 0.5)) + v1 + (v2* Math.pow(2, 0.5)) + 5);

                        b = (int)(R * Math.pow(2, 0.5) + 2* v1 * Math.pow(2, 0.5) + 5); //length
                        a = (int)((R - R*Math.pow(0.5, 0.5)) + v1 + (v2* Math.pow(2, 0.5)) + 5); //height

                        if (height > 40) a = a*2;
                    }else{
                        b = (int)(R * Math.pow(2, 0.5) + 2* v2 / Math.pow(2, 0.5)); //length
                        a = (int)((R - R*Math.pow(0.5, 0.5)) + v2 + (v1/ Math.pow(2, 0.5))); //height

                        if (height > 40) a = a*2;
                    }

                    if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF) b = b*2;
                    if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE) b = b*4;

                }

                textFieldLength.setText("" + b);

            }
        });

        comboBoxShape.setOnAction(event -> {

            Material selectedMaterial = null;
            for (Material material : Project.getMaterials()) {
                if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {

                    selectedMaterial = material;
                }
            }

            ShapeView shapeView = comboBoxShape.getSelectionModel().getSelectedItem();
            if(shapeView == null) return;

//            double length = 2 * shapeView.getR() * Math.PI * shapeView.getCoefficient();




            if(selectedMaterial.getMainType().equals("Акриловый камень")
                    || selectedMaterial.getMainType().equals("Полиэфирный камень") ||
                    selectedMaterial.getMainType().equals("Массив") ||
                    selectedMaterial.getMainType().equals("Массив_шпон")){
                //length += 100;
            }else{

            }

            /** Find length */
            int a, b;
            int height = 40;
            {
                double R = shapeView.R;
                double v1 = 10;
                double v2 = 60;

                try {
                    height = Integer.parseInt(textFieldHeight.getText());
                }catch (NumberFormatException e){
                    height = 60;
                }

                if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE ||
                        shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF ||
                        shapeView.stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS){

//                    length = R * Math.pow(2, 0.5) + 2* v1 * Math.pow(2, 0.5) + 5;
//                    length *= count;
//                    length = (int)((R - R*Math.pow(0.5, 0.5)) + v1 + (v2* Math.pow(2, 0.5)) + 5);

                    b = (int)(R * Math.pow(2, 0.5) + 2* v1 * Math.pow(2, 0.5) + 5); //length
                    a = (int)((R - R*Math.pow(0.5, 0.5)) + v1 + (v2* Math.pow(2, 0.5)) + 5); //height

                    if (height > 40) a = a*2;
                }else{
                    b = (int)(R * Math.pow(2, 0.5) + 2* v2 / Math.pow(2, 0.5)); //length
                    a = (int)((R - R*Math.pow(0.5, 0.5)) + v2 + (v1/ Math.pow(2, 0.5))); //height

                    if (height > 40) a = a*2;
                }

                if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF) b = b*2;
                if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE) b = b*4;

            }

//            System.out.println("EDGE LENGTH textfield= " + length);
            //length = 2 * shapeView.getR() * Math.PI * shapeView.getCoefficient();

            //textFieldLength.setText(String.format(Locale.ENGLISH, "%.1f",(length)));
            textFieldLength.setText("" + b);

        });

        textFieldHeight.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                int height = Integer.parseInt(newValue);
                int depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());

                if(height < depth || height > 500){
                    textFieldHeight.setStyle("-fx-text-fill:red");
                    heightOk = false;
                    return;
                }

            } catch (NumberFormatException ex) {
                textFieldHeight.setStyle("-fx-text-fill:red");
                heightOk = false;
                return;
            }
            textFieldHeight.setStyle("-fx-text-fill:#A8A8A8");
            heightOk = true;
        });

        textFieldLength.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldLength.setStyle("-fx-text-fill:red");
                lengthOk = false;
                return;
            }
            textFieldLength.setStyle("-fx-text-fill:#A8A8A8");
            lengthOk = true;
        });


        comboBoxEdgeType.setOnAction(event -> {
            if (comboBoxEdgeType.getSelectionModel().getSelectedItem() == null) return;
            comboBoxEdgeType.setTooltip(comboBoxEdgeType.getSelectionModel().getSelectedItem().getTooltip());


            //edge logic from .xls file from Marsel
            try {
                int edgeType = comboBoxEdgeType.getSelectionModel().getSelectedItem().getType();
                if (edgeType >= 8) {
                    textFieldHeight.setText("40");
                }

                checkCorrectSettings();
            }catch(NumberFormatException|NullPointerException e){
                System.err.println("EDGE ITEM settings. NumberFormatException.");
            }

            updatePriceInSettings();
        });


    }

    //edge logic from .xls file from Marsel
    public static void checkCorrectSettings() throws NumberFormatException, NullPointerException{

        if(comboBoxEdgeType.getSelectionModel().getSelectedItem() == null ||
                choiceBoxDepth.getSelectionModel().getSelectedItem() == null ||
                textFieldHeight.getText() == "" ||
                choiceBoxMaterial.getSelectionModel().getSelectedItem() == null)return;

        Material selectedMaterial = null;
        int edgeType = comboBoxEdgeType.getSelectionModel().getSelectedItem().getType();
        int depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());
        int height = Integer.parseInt(textFieldHeight.getText());

        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {
                selectedMaterial = material;
            }
        }

        if (selectedMaterial.getName().indexOf("Акриловый камень") != -1 || selectedMaterial.getName().indexOf("Полиэфирный камень") != -1) {
            textFieldHeight.setDisable(false);
            if(height < depth) textFieldHeight.setText("40");
        }else if (selectedMaterial.getName().contains("Массив") || selectedMaterial.getName().contains("Массив_шпон")) {
            textFieldHeight.setDisable(false);
//            if(height < depth) textFieldHeight.setText("40");
            textFieldHeight.setText(choiceBoxDepth.getValue());
        }else {

            if (edgeType >= 1 && edgeType <= 7) {

                textFieldHeight.setDisable(true);
                textFieldHeight.setText(choiceBoxDepth.getValue());

            }else{
                textFieldHeight.setDisable(false);
                if(height < depth) textFieldHeight.setText("40");
            }
        }
    }

    private static void addItem(int index, int quantity){

        if (!(heightOk && lengthOk)) return;

        //int quantity = 1;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        int depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());

        int height;
        try {
            height = Integer.parseInt(textFieldHeight.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        int type = comboBoxEdgeType.getSelectionModel().getSelectedItem().getType();

        double length;
        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        int a = height;
        int b = (int)length;
        String name = "Кромка №" + type;

        if(choiceBoxSurface.getSelectionModel().getSelectedItem().equals("Криволинейная")){
            ShapeView shapeView = comboBoxShape.getSelectionModel().getSelectedItem();

            name = "Кромка №" + type + ", криволинейная, H = " + height + ", R = " + shapeView.R;

            //System.out.println(material.getMainType());


            if(material.getMainType().equals("Акриловый камень") ||
                    material.getMainType().equals("Полиэфирный камень") ||
                    material.getMainType().equals("Массив") ||
                    material.getMainType().equals("Массив_шпон")){
                //len += 100;//added in logic block
            }else{

                int count = 1;
                if(height%depth == 0) count = (height/depth) - 1;
                else{
                    count = (int)Math.floor((double)height/(double)depth);
                }
//                length = (shapeView.R + 70)*count;//70 - default size
//                height = shapeView.R + 70;//70 - default size

                double R = shapeView.R;
                double v1 = 10;
                double v2 = 60;

                if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE ||
                        shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF ||
                        shapeView.stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS){

//                    length = R * Math.pow(2, 0.5) + 2* v1 * Math.pow(2, 0.5) + 5;
//                    length *= count;
//                    length = (int)((R - R*Math.pow(0.5, 0.5)) + v1 + (v2* Math.pow(2, 0.5)) + 5);

                    b = (int)(R * Math.pow(2, 0.5) + 2* v1 * Math.pow(2, 0.5) + 5); //length
                    a = (int)((R - R*Math.pow(0.5, 0.5)) + v1 + (v2* Math.pow(2, 0.5)) + 5); //height

                    if (height > 40) a = a*2;
                }else{
                    b = (int)(R * Math.pow(2, 0.5) + 2* v2 / Math.pow(2, 0.5)); //length
                    a = (int)((R - R*Math.pow(0.5, 0.5)) + v2 + (v1/ Math.pow(2, 0.5))); //height

                    if (height > 40) a = a*2;
                }

                if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF) length = length*2;
                if(shapeView.stoneProductItem.getShapeType() == ShapeType.CIRCLE) length = length*4;

            }
        }

        System.out.println("EDGE LENGTH = " + length);

        //check sizes:
//        double materialLength = material.getMaterialWidth();//mm
//        double materialHeight = material.getMaterialHeight();//mm
//        double shapeLen = length;//mm
//        double shapeHeight = height;//mm

           /* if(((shapeLen > materialLength && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeHeight > materialHeight)) ||
                    (shapeHeight > materialHeight && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeLen > materialLength)) {
                InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Размер не соответствует материалу!");
                return;
            }*/

        int pieces = (int) ((length) / material.getMaterialWidth());
        if (pieces == 0) pieces = 1;
        else pieces += 1;

        ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<>(1);
        ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<>(5);
        for(int j=0;j< quantity;j++){
            ArrayList<Point2D> coordList = new ArrayList<>(5);
            ArrayList<Double> angleList = new ArrayList<>(5);
            for (int i = 0; i < pieces; i++) {
                coordList.add(new Point2D(0, 0));
                angleList.add(Double.valueOf(0));
            }
            cutShapesCoordinates.add(coordList);
            cutShapesAngles.add(angleList);
        }

        double workCoefficient = 0;
        int workCoefficientIndex = 0;
        if (material.getMainType().equals("Кварцевый агломерат") ||
                material.getMainType().indexOf("Натуральный камень") != -1 ||
                material.getMainType().equals("Dektone") ||
                material.getMainType().equals("Мраморный агломерат") ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {

            if (depth == 12 && height == 12){
                workCoefficientIndex = 0;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }else if (depth == 12 && height > 12){
                workCoefficientIndex = 1;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }else if (depth == 20 && height == 20){
                workCoefficientIndex = 2;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }else if (depth == 20 && height > 20){
                workCoefficientIndex = 3;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }else if (depth == 30){
                workCoefficientIndex = 4;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
            }

        }

//        System.out.println("EDGE LENGTH = " + b);
//        System.out.println("EDGE HEIGHT = " + a);

        tableDesignerItemsList.add(index, new EdgeItem(cutShapesCoordinates, cutShapesAngles, quantity, material,
                depth, a, type, b, workCoefficient, workCoefficientIndex, name));
    }

    public static void settingsControlElementsRefresh() {


        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
            System.out.println("MATERIALS IN PROJECT: "+material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());
        System.out.println("Default = " + Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().clear();
        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select("" + Project.getDefaultMaterial().getDefaultDepth());

        comboBoxShape.getItems().clear();
        for(TableDesignerItem tableDesignerItem : TableDesigner.getTableDesignerMainItemsList()){
            if(tableDesignerItem instanceof StoneProductItem){
                StoneProductItem stoneProductItem = (StoneProductItem) tableDesignerItem;

                if(stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS ||
                        stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS_INTO ||
                        stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF ||
                        stoneProductItem.getShapeType() == ShapeType.CIRCLE){
                    comboBoxShape.getItems().add(new ShapeView(stoneProductItem));
                }
            }
        }
        if(comboBoxShape.getItems().size() != 0) comboBoxShape.getSelectionModel().select(0);


        choiceBoxSurface.getItems().clear();
        choiceBoxSurface.getItems().add("Прямолинейная");
        if(comboBoxShape.getItems().size() != 0) choiceBoxSurface.getItems().add("Криволинейная");
        choiceBoxSurface.getSelectionModel().select(0);

        comboBoxEdgeType.getItems().clear();

        if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1 ||
                Project.getDefaultMaterial().getMainType().indexOf("Полиэфирный камень") != -1) {
            for (int i = 1; i <= 17; i++) {
                comboBoxEdgeType.getItems().add(edgeTypesAcryl.get(i));
            }
        }else if(Project.getDefaultMaterial().getMainType().contains("Массив") ||
                Project.getDefaultMaterial().getMainType().contains("Массив_шпон")){
            for (int i = 1; i <= 3; i++) {
                comboBoxEdgeType.getItems().add(edgeTypesWood.get(i));
            }
        } else {
            for (int i = 1; i <= 27; i++) {
                if(Project.getDefaultMaterial().getEdgesAndPrices().get(i) != null){
                    comboBoxEdgeType.getItems().add(edgeTypesQuarz.get(i));
                }
            }
        }
        comboBoxEdgeType.getSelectionModel().select(0);


        System.out.println("choiceBoxMaterial.getValue() = " + choiceBoxMaterial.getValue());
        Material selectedMaterial = null;
        int depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());
        for (Material material : Project.getMaterials()) {
            System.out.println(material.getReceiptName());
            if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {
                selectedMaterial = material;
            }
        }
        //if(selectedMaterial == null) return;
        if (selectedMaterial.getName().indexOf("Акриловый камень") != -1 ||
                selectedMaterial.getName().indexOf("Полиэфирный камень") != -1) {
            textFieldHeight.setText("40");
        }else if(selectedMaterial.getName().indexOf("Массив") != -1 ||
                selectedMaterial.getName().indexOf("Массив_шпон") != -1){

            textFieldHeight.setText("" + depth);
        }else{
            textFieldHeight.setText("" + depth);
        }
        textFieldLength.setText("600");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {
        if (!heightOk) return;
        if (comboBoxEdgeType.getSelectionModel().getSelectedItem() == null) return;

        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getEdgesCurrency();
                String units = "м.п.";
                double priceForOne = -1.0;

                int type = comboBoxEdgeType.getSelectionModel().getSelectedItem().getType();
                int height = Integer.parseInt(textFieldHeight.getText());

                priceForOne = material.getEdgesAndPrices().get(type).doubleValue();

                //priceForOne /= 100.0;

                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(EdgeItem edgeItem){
        TableDesigner.openSettings(EdgeItem.class);

        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(edgeItem.material.getReceiptName());
        choiceBoxDepth.getSelectionModel().select("" + edgeItem.depth);
        textFieldHeight.setText("" + edgeItem.height);
        for(int i =0;i<comboBoxEdgeType.getItems().size();i++){
            if(comboBoxEdgeType.getItems().get(i).getType() == edgeItem.getType()){
                comboBoxEdgeType.getSelectionModel().select(i);
                break;
            }
        }

        choiceBoxSurface.getSelectionModel().select(0);
        textFieldLength.setText("" + edgeItem.length);

        //disable button "add"
        btnAdd.setVisible(false);
        //create buttons "apply" and "cancel"
        anchorPaneSettingsView.getChildren().remove(btnApply);
        anchorPaneSettingsView.getChildren().remove(btnCancel);
        AnchorPane.setBottomAnchor(btnApply, 10.0);
        AnchorPane.setRightAnchor(btnApply, 10.0);
        AnchorPane.setBottomAnchor(btnCancel, 10.0);
        AnchorPane.setRightAnchor(btnCancel, 55.0);
        anchorPaneSettingsView.getChildren().add(btnApply);
        anchorPaneSettingsView.getChildren().add(btnCancel);

        //add listeners to new buttons
        btnApply.setOnAction(event -> {

            int index = getTableDesignerItemsList().indexOf(edgeItem);
            if(index == -1) index = getTableDesignerItemsList().size();
            addItem(index, edgeItem.quantity);

            exitFromEditMode(edgeItem);
            edgeItem.removeThisItem();

        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(edgeItem);

        });

        //in listeners:
        //"apply". delete old row and create new row
        //"cancel". exit from edit mode
    }

    protected static void exitFromEditMode(TableDesignerItem tableDesignerItem){
        btnAdd.setVisible(true);
        //delete buttons "apply" and "cancel"
        anchorPaneSettingsView.getChildren().remove(btnApply);
        anchorPaneSettingsView.getChildren().remove(btnCancel);
        //unselect row
        tableDesignerItem.setEditModeProperty(false);
        settingsControlElementsRefresh();
    }

    protected static void exitFromEditMode(){

        if(anchorPaneSettingsView.getChildren().contains(btnCancel)){
            btnAdd.setVisible(true);
            anchorPaneSettingsView.getChildren().remove(btnApply);
            anchorPaneSettingsView.getChildren().remove(btnCancel);
        }

        for(TableDesignerItem item : tableDesignerItemsList){
            if(item instanceof EdgeItem && item.editModeProperty.get()){
                item.setEditModeProperty(false);
            }
        }

        settingsControlElementsRefresh();
    }
    /**
     * JSON SAVING & OPENING PART
     */

    @Override
    public JSONObject getJsonView() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", "EdgeItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("depth", depth);
        jsonObject.put("height", height);
        jsonObject.put("type", type);
        jsonObject.put("length", length);
        jsonObject.put("workCoefficient", workCoefficient);
        jsonObject.put("workCoefficientIndex", workCoefficientIndex);

        JSONArray coordinatesGlobal = new JSONArray();
        for (ArrayList<SketchShape> shapeList : sketchShapeArrayList) {

            JSONArray coordinatesArray = new JSONArray();
            for (SketchShape shape : shapeList) {
                JSONObject obj = new JSONObject();

                CutShape cutShape = shape.getCutShapeWithoutRefresh();
                obj.put("x", cutShape.getTranslateX());
                obj.put("y", cutShape.getTranslateY());
                coordinatesArray.add(obj);
            }
            coordinatesGlobal.add(coordinatesArray);
        }
        jsonObject.put("cutShapesCoordinates", coordinatesGlobal);

        JSONArray anglesGlobal = new JSONArray();
        for (ArrayList<SketchShape> shapeList : sketchShapeArrayList) {
            JSONArray anglesArray = new JSONArray();
            for (SketchShape shape : shapeList) {
                double angle = shape.getCutShapeWithoutRefresh().getRotateAngle();
                anglesArray.add(angle);
            }
            anglesGlobal.add(anglesArray);
        }
        jsonObject.put("cutShapeAngles", anglesGlobal);

        return jsonObject;
    }

    public static EdgeItem initFromJSON(JSONObject jsonObject) {

        String materialName = (String) jsonObject.get("material");

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {
                material = m;
                break;
            }
        }
        if (material == null) return null;

        int depth = ((Long) jsonObject.get("depth")).intValue();
        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        int height = ((Long) jsonObject.get("height")).intValue();
        int type = ((Long) jsonObject.get("type")).intValue();
        double length = ((Double) jsonObject.get("length")).doubleValue();
        double workCoefficient = ((Double) jsonObject.get("workCoefficient")).doubleValue();
        int workCoefficientIndex = 0;
        if(jsonObject.get("workCoefficientIndex") != null){
            workCoefficientIndex = ((Long) jsonObject.get("workCoefficientIndex")).intValue();
        }

        //get data for cutShapes
        ArrayList<ArrayList<Point2D>> cutShapeCoordinatesGlobal = new ArrayList<>(1);
        JSONArray coordinatesArrayGlobal = (JSONArray) jsonObject.get("cutShapesCoordinates");
        for (Object obj : coordinatesArrayGlobal) {

            ArrayList<Point2D> cutShapeCoordinates = new ArrayList<>(5);
            JSONArray coordArray = (JSONArray) obj;
            for (Object obj1 : coordArray) {
                JSONObject point = (JSONObject) obj1;
                cutShapeCoordinates.add(new Point2D(((Double) point.get("x")).doubleValue(), ((Double) point.get("y")).doubleValue()));
            }
            cutShapeCoordinatesGlobal.add(cutShapeCoordinates);
        }

        ArrayList<ArrayList<Double>> cutShapeAnglesGlobal = new ArrayList<>(1);
        JSONArray anglesArrayGlobal = (JSONArray) jsonObject.get("cutShapeAngles");
        for (Object obj : anglesArrayGlobal) {

            ArrayList<Double> cutShapeAngles = new ArrayList<>(5);
            JSONArray anglesArray = (JSONArray) obj;
            for (Object obj1 : anglesArray) {
                Double angle = (Double) obj1;
                cutShapeAngles.add(angle);
            }
            cutShapeAnglesGlobal.add(cutShapeAngles);
        }
        String name = "Кромка №" + type;
        EdgeItem edgeItem = new EdgeItem(cutShapeCoordinatesGlobal, cutShapeAnglesGlobal, quantity, material, depth,
                height, type, length, workCoefficient, workCoefficientIndex, name);

        edgeItem.updateRowPrice();
        return edgeItem;
    }

}

class ShapeView {
    ImageView image;
    String name;
    StoneProductItem stoneProductItem;
    int R = 0;
    double coefficient = 1;

    ShapeView(StoneProductItem stoneProductItem) {
        this.stoneProductItem = stoneProductItem;
        R = (int) stoneProductItem.getSizeC();
        name = stoneProductItem.getShapeType().toString() + " R = " + R;

        if(stoneProductItem.getShapeType() == ShapeType.CIRCLE)coefficient = 1;
        else if(stoneProductItem.getShapeType() == ShapeType.CIRCLE_HALF)coefficient = 0.5;
        else if(stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS_INTO)coefficient = 0.25;
        else if(stoneProductItem.getShapeType() == ShapeType.RECTANGLE_WITH_RADIUS)coefficient = 0.25;
    }

    public Node getImage() {


        image = stoneProductItem.getImageViewShapeType();
        image.setFitWidth(70);
        image.setFitHeight(70);



        Label label = new Label("R = " + R);
        Pane pane = new Pane();
        pane.setPrefSize(90, 90);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setPrefWidth(90);
        label.setAlignment(Pos.CENTER);
        label.setTranslateY(70);
        label.setPadding(new Insets(5, 10, 5, 10));
        label.setFont(Font.font(10));

        return pane;
    }

    public String getName() {
        return name;
    }

    public int getR() {
        return R;
    }

    public double getCoefficient() {
        return coefficient;
    }
}

class EdgeType {
    ImageView image;
    int type = 1;
    //Material material;
    int materialType = 1;

    Pane paneMain = new Pane();
    Pane paneCopy = new Pane();

    Tooltip tooltip = new Tooltip();
    Tooltip tooltipCopy = new Tooltip();




    EdgeType(int materialType, int type) {
        this.type = type;
        this.materialType = materialType;
        createImage(paneMain);
        createImage(paneCopy);
        createTooltip(tooltip);
        createTooltip(tooltipCopy);

    }

    private void createImage(Pane pane){

        File file = null;
        if (materialType == 1) {
            file = new File("edges_resources/" + "edge_" + type + ".png");
        } else if (materialType == 2){
            file = new File("edges_resources/" + "edge_1_" + type + ".png");
        } else if (materialType == 3){
            file = new File("edges_resources/wood/" + type + "_200.png");
        }

        try {
            image = new ImageView(new Image(file.toURI().toURL().toString()));
            image.setFitWidth(100);
            image.setFitHeight(100);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        Label label = new Label("Кромка, тип " + type);
        pane.setPrefSize(120, 120);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setTranslateY(95);
        label.setPadding(new Insets(5, 10, 5, 10));
        label.setFont(Font.font(10));

    }

    private void createTooltip(Tooltip tooltip){

        File file = null;
        if (materialType == 1) {
            file = new File("edges_resources/250x250/material_1/" + "edge_" + type + "_250x250.png");
        } else if (materialType == 2){
            file = new File("edges_resources/250x250/material_2/" + "edge_1_" + type + "_250x250.png");
        } else if (materialType == 3){
            file = new File("edges_resources/wood/" + type + "_200.png");
        }
        ImageView tooltipImage = null;

        try {
            tooltipImage = new ImageView(new Image(file.toURI().toURL().toString()));
            tooltipImage.setFitWidth(250);
            tooltipImage.setFitHeight(250);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        Label label = new Label("Кромка, тип " + type);
        Pane pane = new Pane();
        pane.setPrefSize(250, 300);
        pane.getChildren().add(tooltipImage);
        pane.getChildren().add(label);
        label.setTranslateY(250);
        label.setPadding(new Insets(20, 10, 20, 10));
        label.setFont(Font.font(15));
        tooltip.setGraphic(pane);
    }

    public Node getImage() {
        return paneMain;
    }

    public Node getImageCopy() {
        return paneCopy;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public Tooltip getTooltipCopy() {
        return tooltipCopy;
    }

    public int getType() {
        return type;
    }
}
