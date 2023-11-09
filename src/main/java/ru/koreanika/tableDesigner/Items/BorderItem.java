package ru.koreanika.tableDesigner.Items;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.Shapes.SketchShapeRectangle;
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

public class BorderItem extends TableDesignerItem implements Cuttable, DependOnMaterial {

    /**
     * static variables
     */
    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalItemsList();

    /**
     * instance variables
     */
    private ArrayList<ArrayList<SketchShape>> sketchShapeArrayList = new ArrayList<>();

    private ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<>();
    private ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<>();

    Label labelRowNumber, labelName, labelMaterial, labelType, labelLength, labelHeight, labelQuantity, labelRowPrice;
    ImageView imageViewMain, imageViewCut, imageViewAngleCut;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int depth;
    int height;
    int type;
    double length;
    int cutType;
    int angleCutType;
    int angleCutQuantity;
    String name;

    double price;

    Image imageMain;
    Image imageCut;
    Image imageAngleCut;
    Label labelAngleCutQuantity;

    double workCoefficient;
    int workCoefficientIndex;

    public BorderItem(ArrayList<ArrayList<Point2D>> cutShapesCoordinates, ArrayList<ArrayList<Double>> cutShapesAngles,
                      int quantity, Material material, int depth, int height, int type, double length,
                      int cutType, int angleCutType, int angleCutQuantity, double workCoefficient,
                      int workCoefficientIndex, String name) {

        this.cutShapesAngles = cutShapesAngles;
        this.cutShapesCoordinates = cutShapesCoordinates;
        this.quantity = quantity;
        this.material = material;
        this.depth = depth;
        this.height = height;
        this.type = type;
        this.length = length;
        this.cutType = cutType;
        this.angleCutType = angleCutType;
        this.angleCutQuantity = angleCutQuantity;
        this.workCoefficient = workCoefficient;
        this.workCoefficientIndex = workCoefficientIndex;
        this.name = name;

        File file = new File("borders_resources/" + "border_" + type + ".png");

        if (material.getMainType().contains("Акриловый камень") ||
                material.getMainType().contains("Полиэфирный камень")){
            file = new File("borders_resources/" + "border_" + this.type + ".png");
        }else if(material.getMainType().contains("Массив") ||
                material.getMainType().contains("Массив_шпон")){
            file = new File("borders_resources/wood/" + this.type + "_200.png");
        }else{
            file = new File("borders_resources/" + "border_" + this.type + ".png");
        }

        try {
            imageMain = new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        imageCut = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderCut" + this.cutType + "_100px.png").toString()).getImage();
        imageAngleCut = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderSideCut" + this.angleCutType + "_100px.png").toString()).getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/BorderRow.fxml")
        );

        try {
            anchorPaneTableRow = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        //create shape and add it to sketchDesignerList
        int pieces = (int) ((length) / material.getMaterialWidth());
        if (pieces == 0) pieces = 1;
        else pieces += 1;

        if(!material.isUseMainSheets()){
            pieces = 1;
        }
        //System.out.println("pieces = " + pieces);

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

                cutShape.setTranslateX(cutShapesCoordinates.get(i).get(j).getX());
                cutShape.setTranslateY(cutShapesCoordinates.get(i).get(j).getY());

                cutShape.rotateShapeLocal(cutShapesAngles.get(i).get(j).doubleValue());

            }
            sketchShapeArrayList.add(edgeItemShapes);
        }

        rowControlElementsInit();
        rowControlElementLogicInit();

        cardControlElementsInit();
        cardControlElementLogicInit();

        updateItemView();
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(BorderItem item) {

        BorderItem oldBorderItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldBorderItem.material;
        } else {

            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType()) && defaultMaterial.getDepths().contains("" + oldBorderItem.depth)) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterials()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType()) && material.getDepths().contains("" + oldBorderItem.depth)) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldBorderItem.removeThisItem();
                    return;
                }
            }

        }


        if (newMaterial.getDepths().contains("" + oldBorderItem.depth)) {

            ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<ArrayList<Point2D>>();
            ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<ArrayList<Double>>();

            if(newMaterial.getMaterialWidth() != oldBorderItem.material.getMaterialWidth()){
                int pieces = (int) ((oldBorderItem.length) / newMaterial.getMaterialWidth());
                if (pieces == 0) pieces = 1;
                else pieces += 1;

                for(int j =0;j<oldBorderItem.quantity;j++){
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
                for (ArrayList<Point2D> arr1 : oldBorderItem.cutShapesCoordinates) {

                    ArrayList<Point2D> arrayPoints = new ArrayList<>();
                    for (Point2D p : arr1) {
                        arrayPoints.add(new Point2D(p.getX(), p.getY()));
                    }
                    cutShapesCoordinates.add(arrayPoints);
                }

                for (ArrayList<Double> arr1 : oldBorderItem.cutShapesAngles) {

                    ArrayList<Double> arrayAngles = new ArrayList<>();
                    for (Double d : arr1) {
                        arrayAngles.add(Double.valueOf(d.doubleValue()));
                    }
                    cutShapesAngles.add(arrayAngles);
                }
            }



            BorderItem newBorderItem = new BorderItem(cutShapesCoordinates, cutShapesAngles, oldBorderItem.quantity,
                    newMaterial, oldBorderItem.depth, oldBorderItem.height, oldBorderItem.type, oldBorderItem.length,
                    oldBorderItem.cutType, oldBorderItem.angleCutType, oldBorderItem.angleCutQuantity,
                    oldBorderItem.workCoefficient,oldBorderItem.workCoefficientIndex, oldBorderItem.name);

            oldBorderItem.removeThisItem();
            tableDesignerItemsList.add(newBorderItem);
        } else {
            oldBorderItem.removeThisItem();
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

    public int getHeight() {
        return height;
    }

    public int getType() {
        return type;
    }

    public double getLength() {
        return length;
    }

    public int getCutType() {
        return cutType;
    }

    public int getAngleCutType() {
        return angleCutType;
    }

    public int getAngleCutQuantity() {
        return angleCutQuantity;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        String imgPath = "borders_resources/" + "border_" + type + ".png";

        if (material.getMainType().contains("Акриловый камень") ||
                material.getMainType().contains("Полиэфирный камень")){
            imgPath = "borders_resources/" + "border_" + this.type + ".png";
        }else if(material.getMainType().contains("Массив") ||
                material.getMainType().contains("Массив_шпон")){
            imgPath = "borders_resources/wood/" + this.type + "_200.png";
        }else{
            imgPath = "borders_resources/" + "border_" + this.type + ".png";
        }

        File file = new File(imgPath);
        try {
            imagesList.put("Бортик#" + imgPath, new ImageView(new Image(file.toURI().toURL().toString())));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        String imgPath1 = "/styles/images/edgeManager/borderCut" + cutType + "_100px.png";
        imagesList.put("Кромка бортика#" + imgPath1, new ImageView(BorderItem.class.getResource(imgPath1).toString()));
        if (angleCutQuantity != 0) {
            String imgPath2 = "/styles/images/edgeManager/borderSideCut" + angleCutType + "_100px.png";
            imagesList.put("Запил бортика#" + imgPath2, new ImageView(BorderItem.class.getResource(imgPath2).toString()));
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
//                CutShape cutShape = shape.getCutShape();
//                CutDesigner.getCutShapesList().remove(cutShape);
//                CutDesigner.getCutPane().cutObjectsGroup.getChildren().remove(cutShape);
            }
        }

        sketchShapeArrayList.clear();
        cutShapesCoordinates.clear();
        cutShapesAngles.clear();
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            BorderItem.exitFromEditMode(this);
        }
    }


    public static ObservableList<TableDesignerItem> getTableDesignerItemsList() {
        return tableDesignerItemsList;
    }

    /**
     * Table ROW part
     */

    private void rowControlElementsInit() {

        HBox hBox = (HBox) anchorPaneTableRow.lookup("#hBox");
        labelRowNumber = (Label) hBox.getChildren().get(0);
        labelName = (Label) hBox.getChildren().get(1);
        AnchorPane anchorPaneImageView = (AnchorPane) hBox.getChildren().get(2);
        imageViewMain = (ImageView) anchorPaneImageView.lookup("#imageViewMain");
        imageViewCut = (ImageView) anchorPaneImageView.lookup("#imageViewCut");
        imageViewAngleCut = (ImageView) anchorPaneImageView.lookup("#imageViewAngleCut");
        labelAngleCutQuantity = (Label) anchorPaneImageView.lookup("#labelAngleCutQuantity");
        labelMaterial = (Label) hBox.getChildren().get(3);
        labelType = (Label) hBox.getChildren().get(4);
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
        HBox.setHgrow(labelType, Priority.ALWAYS);
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
        ArrayList<SketchShape> borderItemShapes = new ArrayList<>(5);
        ArrayList<Point2D> coordList = new ArrayList<>();
        ArrayList<Double> angleList = new ArrayList<>();

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
            SketchDesigner.getSketchShapesList().add(shape);
            //add shape to cutPane
            CutShape cutShape = shape.getCutShape();
            CutDesigner.getInstance().getCutShapesList().add(cutShape);
            CutDesigner.getInstance().usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
            CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);
            borderItemShapes.add(shape);
            coordList.add(new Point2D(0, 0));
            cutShape.setTranslateX(0);
            cutShape.setTranslateY(0);
            angleList.add(Double.valueOf(0));
            cutShape.rotateShapeLocal(0);
        }


        sketchShapeArrayList.add(borderItemShapes);
        cutShapesCoordinates.add(coordList);
        cutShapesAngles.add(angleList);

        updateItemView();
    }
    private void btnMinusClicked(ActionEvent event){
        if (quantity == 1) return;
        quantity--;


        //delete one shape:
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
        labelName.setText("Бортик");
        imageViewMain.setImage(imageMain);
        imageViewCut.setImage(imageCut);
        if (angleCutQuantity != 0) {
            imageViewAngleCut.setImage(imageAngleCut);
            labelAngleCutQuantity.setText("" + angleCutQuantity);
        } else {
            labelAngleCutQuantity.setText("");
        }


        labelMaterial.setText(material.getReceiptName() + " " + depth + " мм");
        labelType.setText("#" + type);
        labelLength.setText("" + length + "мм");
        labelHeight.setText("" + height + "мм");
        labelQuantity.setText("" + quantity);
        labelRowPrice.setText("0");


        labelHeaderCard.setText("Бортик" + "#" + type);
        tooltipNameCard.setText("Бортик" + "#" + type);
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Толщина материала");
        labelValue2Card.setText("" + depth + " мм");

        labelName3Card.setText("Длина");
        labelValue3Card.setText("" + (int)length + "мм");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("" + height + "мм");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {
        String currencyBorder = material.getBorderCurrency();

        String symbol = (material.getCurrency().equals("RUB")) ?
                Currency.RUR_SYMBOL : ((material.getCurrency().equals("USD")) ?
                Currency.USD_SYMBOL : Currency.EUR_SYMBOL);
        String units = "м.п.";
        double priceForOne = -1.0;

        if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                material.getName().indexOf("Натуральный камень") != -1 ||
                material.getName().indexOf("Dektone") != -1 ||
                material.getName().indexOf("Мраморный агломерат") != -1 ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {

            priceForOne = material.getBorderTypesAndPrices().get(0).doubleValue();

        } else {
            if (height <= 50 && type == 1) {
                priceForOne = material.getBorderTypesAndPrices().get(0).doubleValue();
            }
            if (height > 50 && type == 1) {
                priceForOne = material.getBorderTypesAndPrices().get(1).doubleValue();
            }

            if (height <= 50 && type == 2) {
                priceForOne = material.getBorderTypesAndPrices().get(2).doubleValue();
            }
            if (height > 50 && type == 2) {
                priceForOne = material.getBorderTypesAndPrices().get(3).doubleValue();
            }
        }


        double multiplier = 1;
        if (currencyBorder.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currencyBorder.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currencyBorder.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;

        //System.out.println("cutType = " + cutType);
        priceForOne += material.getBorderTopCutTypesAndPrices().get(Integer.valueOf(cutType - 1)).intValue();

        priceForOne /= 100.0;

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * length/1000 * quantity) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * length/1000) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * length/1000 * quantity) + Currency.RUR_SYMBOL);

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
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK"), btnCancel = new Button("Отмена");

    private static ChoiceBox<String> choiceBoxMaterial;
    private static ChoiceBox<String> choiceBoxDepth;
    private static ComboBox<BorderType> comboBoxBorderType;
    private static TextField textFieldHeight, textFieldLength;
    private static ToggleButton toggleButtonBorderCut1, toggleButtonBorderCut2, toggleButtonBorderCut3, toggleButtonBorderCut4;
    private static ToggleGroup toggleGroupBorderCut = new ToggleGroup();
    private static ToggleButton toggleButtonBorderAngleCut1, toggleButtonBorderAngleCut2;
    private static ToggleGroup toggleGroupBorderAngleCut = new ToggleGroup();
    private static ChoiceBox<String> choiceBoxAngleCutQuantity;
    private static Label labelPrice;

    private static boolean heightOk = true, lengthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/BorderSettings.fxml"));

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

        choiceBoxMaterial = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxMaterial");
        choiceBoxDepth = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxDepth");
        comboBoxBorderType = (ComboBox<BorderType>) anchorPaneSettingsView.lookup("#comboBoxBorderType");

        textFieldHeight = (TextField) anchorPaneSettingsView.lookup("#textFieldHeight");
        textFieldLength = (TextField) anchorPaneSettingsView.lookup("#textFieldLength");

        toggleButtonBorderCut1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonBorderCut1");
        toggleButtonBorderCut2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonBorderCut2");
        toggleButtonBorderCut3 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonBorderCut3");
        toggleButtonBorderCut4 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonBorderCut4");

        toggleButtonBorderAngleCut1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonBorderAngleCut1");
        toggleButtonBorderAngleCut2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonBorderAngleCut2");

        choiceBoxAngleCutQuantity = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxAngleCutQuantity");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");

        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");


        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }

        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select(0);

        comboBoxBorderType.setCellFactory(new Callback<ListView<BorderType>, ListCell<BorderType>>() {
            @Override
            public ListCell<BorderType> call(ListView<BorderType> param) {

                ListCell<BorderType> cell = new ListCell<BorderType>() {
                    @Override
                    protected void updateItem(BorderType item, boolean empty) {
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
        comboBoxBorderType.setButtonCell(new ListCell<BorderType>() {
            @Override
            protected void updateItem(BorderType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    //setText("#" + item.getType());
                    setGraphic(item.getImage());
                }

            }
        });


        if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1 ||
                Project.getDefaultMaterial().getMainType().indexOf("Полиэфирный камень") != -1) {
            toggleButtonBorderCut1.setDisable(false);
            toggleButtonBorderCut4.setDisable(false);

            for (int i = 1; i <= 2; i++) {
                comboBoxBorderType.getItems().add(new BorderType(Project.getDefaultMaterial(), i));
            }
        } else {
            toggleButtonBorderCut1.setDisable(true);
            toggleButtonBorderCut4.setDisable(true);
            if (toggleButtonBorderCut1.isSelected() || toggleButtonBorderCut4.isSelected()) {
                toggleButtonBorderCut1.setSelected(true);
            }

            for (int i = 1; i <= 1; i++) {
                comboBoxBorderType.getItems().add(new BorderType(Project.getDefaultMaterial(), i));
            }
        }
        comboBoxBorderType.getSelectionModel().select(0);

        comboBoxBorderType.setTooltip(comboBoxBorderType.getSelectionModel().getSelectedItem().getTooltip());

        textFieldHeight.setText("50");
        textFieldLength.setText("0.6");

        toggleButtonBorderCut1.setToggleGroup(toggleGroupBorderCut);
        toggleButtonBorderCut2.setToggleGroup(toggleGroupBorderCut);
        toggleButtonBorderCut3.setToggleGroup(toggleGroupBorderCut);
        toggleButtonBorderCut4.setToggleGroup(toggleGroupBorderCut);
        toggleButtonBorderCut2.setSelected(true);

//        ImageView image1 = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderCut1.png").toString());
//        image1.setFitWidth(45);
//        image1.setFitHeight(45);
//        toggleButtonBorderCut1.setGraphic(image1);
//
//        ImageView image2 = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderCut2.png").toString());
//        image2.setFitWidth(45);
//        image2.setFitHeight(45);
//        toggleButtonBorderCut2.setGraphic(image2);
//
//        ImageView image3 = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderCut3.png").toString());
//        image3.setFitWidth(45);
//        image3.setFitHeight(45);
//        toggleButtonBorderCut3.setGraphic(image3);
//
//        ImageView image4 = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderCut4.png").toString());
//        image4.setFitWidth(45);
//        image4.setFitHeight(45);
//        toggleButtonBorderCut4.setGraphic(image4);

        toggleButtonBorderAngleCut1.setToggleGroup(toggleGroupBorderAngleCut);
        toggleButtonBorderAngleCut2.setToggleGroup(toggleGroupBorderAngleCut);
        toggleButtonBorderAngleCut1.setSelected(true);

//        ImageView image5 = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderSideCut1.png").toString());
//        image5.setFitWidth(45);
//        image5.setFitHeight(45);
//        toggleButtonBorderAngleCut1.setGraphic(image5);
//
//        ImageView image6 = new ImageView(BorderItem.class.getResource("/styles/images/edgeManager/borderSideCut2.png").toString());
//        image6.setFitWidth(45);
//        image6.setFitHeight(45);
//        toggleButtonBorderAngleCut2.setGraphic(image6);

        choiceBoxAngleCutQuantity.getItems().add("Нет");
        choiceBoxAngleCutQuantity.getItems().add("С одной стороны");
        choiceBoxAngleCutQuantity.getItems().add("С двух сторон");


    }

    private static void settingsControlElementsLogicInit() {
        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));


        choiceBoxMaterial.setOnAction(event -> {

            comboBoxBorderType.getItems().clear();

            for (Material material : Project.getMaterials()) {
                if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {

                    if (material.getName().indexOf("Акриловый камень") != -1 ||
                            material.getName().indexOf("Полиэфирный камень") != -1) {
                        toggleButtonBorderCut1.setDisable(false);
                        toggleButtonBorderCut4.setDisable(false);

                        for (int i = 1; i <= 2; i++) {
                            comboBoxBorderType.getItems().add(new BorderType(material, i));
                        }
                    }else {
                        toggleButtonBorderCut1.setDisable(true);
                        toggleButtonBorderCut4.setDisable(true);

                        if (toggleButtonBorderCut1.isSelected() || toggleButtonBorderCut4.isSelected()) {
                            toggleButtonBorderCut1.setSelected(true);
                        }

                        for (int i = 1; i <= 1; i++) {
                            comboBoxBorderType.getItems().add(new BorderType(material, i));
                        }
                    }
                    comboBoxBorderType.getSelectionModel().select(0);


                    //update depths:
                    choiceBoxDepth.getItems().clear();
                    choiceBoxDepth.getItems().addAll(material.getDepths());
                    choiceBoxDepth.getSelectionModel().select(0);

                    break;
                }
            }

            updatePriceInSettings();

        });

        textFieldHeight.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                int height = Integer.parseInt(newValue);
                if(height > 400) {
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

            updatePriceInSettings();
        });

        textFieldLength.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                double len = Double.parseDouble(newValue);
                if(len > 15000) {
                    textFieldLength.setStyle("-fx-text-fill:red");
                    lengthOk = false;
                    return;
                }
            } catch (NumberFormatException ex) {
                textFieldLength.setStyle("-fx-text-fill:red");
                lengthOk = false;
                return;
            }
            textFieldLength.setStyle("-fx-text-fill:#A8A8A8");
            lengthOk = true;
        });

        choiceBoxAngleCutQuantity.setOnAction(event -> {
            if (choiceBoxAngleCutQuantity.getSelectionModel().getSelectedIndex() == 0) {
                toggleButtonBorderAngleCut1.setDisable(true);
                toggleButtonBorderAngleCut2.setDisable(true);
            } else {
                toggleButtonBorderAngleCut1.setDisable(false);
                toggleButtonBorderAngleCut2.setDisable(false);
            }
        });

        comboBoxBorderType.setOnAction(event -> {
            if (comboBoxBorderType.getSelectionModel().getSelectedItem() == null) return;
            comboBoxBorderType.setTooltip(comboBoxBorderType.getSelectionModel().getSelectedItem().getTooltip());

            updatePriceInSettings();

        });


        toggleGroupBorderCut.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){

        if (!(heightOk && lengthOk)) return;



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

        int type = comboBoxBorderType.getSelectionModel().getSelectedItem().getType();

        double length;
        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        int cutType = 1;
        if (toggleButtonBorderCut1.isSelected()) cutType = 1;
        else if (toggleButtonBorderCut2.isSelected()) cutType = 2;
        else if (toggleButtonBorderCut3.isSelected()) cutType = 3;
        else if (toggleButtonBorderCut4.isSelected()) cutType = 4;

        int angleCutType = 1;
        if (toggleButtonBorderAngleCut1.isSelected()) angleCutType = 1;
        else if (toggleButtonBorderAngleCut2.isSelected()) angleCutType = 2;

        int angleCutQuantity = choiceBoxAngleCutQuantity.getSelectionModel().getSelectedIndex();

        //check sizes:
        double materialLength = material.getMaterialWidth();//mm
        double materialHeight = material.getMaterialHeight();//mm
        double shapeLen = length;//mm
        double shapeHeight = height;//mm

//            if(((shapeLen > materialLength && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeHeight > materialHeight)) ||
//                    (shapeHeight > materialHeight && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeLen > materialLength)) {
//                InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Размер не соответствует материалу!");
//                return;
//            }

        int pieces = (int) ((length) / material.getMaterialWidth());
        if (pieces == 0) pieces = 1;
        else pieces += 1;

        ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<>(1);
        ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<>(5);
        for(int j =0;j<quantity;j++){
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
                material.getName().indexOf("Натуральный камень") != -1 ||
                material.getMainType().equals("Dektone") ||
                material.getMainType().equals("Мраморный агломерат") ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {
            if (depth == 12){
                workCoefficientIndex = 0;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }else if (depth == 20){
                workCoefficientIndex = 2;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }else if (depth == 30){
                workCoefficientIndex = 4;
                workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);

            }
        }

        String name = "Бортик №" + type;


        tableDesignerItemsList.add(index, new BorderItem(cutShapesCoordinates, cutShapesAngles, quantity, material,
                depth, height, type, length, cutType, angleCutType, angleCutQuantity, workCoefficient,
                workCoefficientIndex, name));

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().clear();
        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select("" + Project.getDefaultMaterial().getDefaultDepth());

        comboBoxBorderType.getItems().clear();

        if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1 ||
                Project.getDefaultMaterial().getMainType().indexOf("Полиэфирный камень") != -1) {
            toggleButtonBorderCut1.setDisable(false);
            toggleButtonBorderCut4.setDisable(false);

            for (int i = 1; i <= 2; i++) {
                comboBoxBorderType.getItems().add(new BorderType(Project.getDefaultMaterial(), i));
            }
        }else{
            toggleButtonBorderCut1.setDisable(true);
            toggleButtonBorderCut4.setDisable(true);
            if (toggleButtonBorderCut1.isSelected() || toggleButtonBorderCut4.isSelected()) {
                toggleButtonBorderCut1.setSelected(true);
            }

            for (int i = 1; i <= 1; i++) {
                comboBoxBorderType.getItems().add(new BorderType(Project.getDefaultMaterial(), i));
            }
        }
        comboBoxBorderType.getSelectionModel().select(0);

        updatePriceInSettings();

        textFieldHeight.setText("25");
        textFieldLength.setText("600");

        toggleButtonBorderCut2.setSelected(true);

        choiceBoxAngleCutQuantity.getSelectionModel().select(0);
        toggleButtonBorderAngleCut1.setSelected(true);
    }

    public static void updatePriceInSettings() {
        if (!heightOk) return;
        if (comboBoxBorderType.getSelectionModel().getSelectedItem() == null) return;
        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getBorderCurrency();
                String units = "м.п.";
                double priceForOne = -1.0;

                int type = comboBoxBorderType.getSelectionModel().getSelectedItem().getType();
                int height = Integer.parseInt(textFieldHeight.getText());

                if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                        material.getName().indexOf("Натуральный камень") != -1 ||
                        material.getName().indexOf("Dektone") != -1 ||
                        material.getName().indexOf("Мраморный агломерат") != -1 ||
                        material.getName().indexOf("Кварцекерамический камень") != -1) {

                    priceForOne = material.getBorderTypesAndPrices().get(0).doubleValue();

                } else {
                    if (height <= 50 && type == 1) {
                        priceForOne = material.getBorderTypesAndPrices().get(0).doubleValue();
                    }
                    if (height > 50 && type == 1) {
                        priceForOne = material.getBorderTypesAndPrices().get(1).doubleValue();
                    }

                    if (height <= 50 && type == 2) {
                        priceForOne = material.getBorderTypesAndPrices().get(2).doubleValue();
                    }
                    if (height > 50 && type == 2) {
                        priceForOne = material.getBorderTypesAndPrices().get(3).doubleValue();
                    }
                }

                int topCut = 0;
                if (toggleButtonBorderCut1.isSelected()) topCut = 0;
                if (toggleButtonBorderCut2.isSelected()) topCut = 1;
                if (toggleButtonBorderCut3.isSelected()) topCut = 2;
                if (toggleButtonBorderCut4.isSelected()) topCut = 3;
                priceForOne += material.getBorderTopCutTypesAndPrices().get(Integer.valueOf(topCut)).intValue();

                priceForOne /= 100.0;

                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(BorderItem borderItem){
        TableDesigner.openSettings(BorderItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(borderItem.material.getReceiptName());
        choiceBoxDepth.getSelectionModel().select("" + borderItem.depth);
        textFieldHeight.setText("" + borderItem.height);
        comboBoxBorderType.getSelectionModel().select(borderItem.getType() - 1);
        textFieldLength.setText(""+ borderItem.length);
        if(borderItem.cutType == 1) toggleButtonBorderCut1.setSelected(true);
        else if(borderItem.cutType == 2) toggleButtonBorderCut2.setSelected(true);
        else if(borderItem.cutType == 3) toggleButtonBorderCut3.setSelected(true);
        else if(borderItem.cutType == 4) toggleButtonBorderCut4.setSelected(true);

        if(borderItem.angleCutType == 1) toggleButtonBorderAngleCut1.setSelected(true);
        else if(borderItem.angleCutType == 2) toggleButtonBorderAngleCut2.setSelected(true);

        choiceBoxAngleCutQuantity.getSelectionModel().select(borderItem.angleCutQuantity);

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

            int index = getTableDesignerItemsList().indexOf(borderItem);
            addItem(index, borderItem.quantity);

            exitFromEditMode(borderItem);
            borderItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(borderItem);

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
    /**
     * JSON SAVING & OPENING PART
     */

    @Override
    public JSONObject getJsonView() {


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", "BorderItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("depth", depth);
        jsonObject.put("height", height);
        jsonObject.put("type", type);
        jsonObject.put("length", length);
        jsonObject.put("cutType", cutType);
        jsonObject.put("angleCutType", angleCutType);
        jsonObject.put("angleCutQuantity", angleCutQuantity);
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

    public static BorderItem initFromJSON(JSONObject jsonObject) {

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
        int cutType = ((Long) jsonObject.get("cutType")).intValue();
        int angleCutType = ((Long) jsonObject.get("angleCutType")).intValue();
        int angleCutQuantity = ((Long) jsonObject.get("angleCutQuantity")).intValue();
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

        String name = "Бортик №" + type;
        BorderItem borderItem = new BorderItem(cutShapeCoordinatesGlobal, cutShapeAnglesGlobal, quantity, material,
                depth, height, type, length, cutType, angleCutType, angleCutQuantity, workCoefficient,
                workCoefficientIndex, name);
        //borderItem.quantity = quantity;
        borderItem.labelQuantity.setText("" + quantity);
        borderItem.updateRowPrice();
        return borderItem;
    }

}

class BorderType {
    ImageView image;
    int type = 1;
    Material material;

    int materialType = 1;


    BorderType(Material material, int type) {
        this.type = type;
        this.material = material;

        if (material.getMainType().contains("Акриловый камень") ||
                material.getMainType().contains("Полиэфирный камень")){
            this.materialType = 1;
        }else if(material.getMainType().contains("Массив") ||
                material.getMainType().contains("Массив_шпон")){
            this.materialType = 3;//wood
        }else{
            this.materialType = 2;//quartz
        }


    }

    public Node getImage() {
        File file;
        if(materialType == 1 || materialType == 2){
            file = new File("borders_resources/" + "border_" + type + ".png");
        }else{
            file = new File("borders_resources/wood/" + type + "_200.png");
        }


        try {
            image = new ImageView(new Image(file.toURI().toURL().toString()));
            image.setFitWidth(100);
            image.setFitHeight(100);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        Label label = new Label("Бортик " + ((type == 2) ? "галтель" : "прямой"));
        Pane pane = new Pane();
        pane.setPrefSize(120, 120);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setTranslateY(95);
        label.setPadding(new Insets(5, 10, 5, 10));
        label.setFont(Font.font(10));

        return pane;
    }

    public Tooltip getTooltip() {

        Tooltip tooltip = new Tooltip();

        File file;
        if(materialType == 1 || materialType == 2){
            file = new File("borders_resources/250x250/" + "border_" + type + "_250x250.png");
        }else{
            file = new File("borders_resources/wood/" + type + "_1000.png");
        }


        ImageView tooltipImage = null;

        try {
            tooltipImage = new ImageView(new Image(file.toURI().toURL().toString()));
            tooltipImage.setFitWidth(250);
            tooltipImage.setFitHeight(250);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        Label label = new Label("Бортик " + ((type == 2) ? "галтель" : "прямой"));
        Pane pane = new Pane();
        pane.setPrefSize(250, 300);
        pane.getChildren().add(tooltipImage);
        pane.getChildren().add(label);
        label.setTranslateY(250);
        label.setPadding(new Insets(20, 10, 20, 10));
        label.setFont(Font.font(15));
        tooltip.setGraphic(pane);


        return tooltip;
    }

    public int getType() {
        return type;
    }
}
