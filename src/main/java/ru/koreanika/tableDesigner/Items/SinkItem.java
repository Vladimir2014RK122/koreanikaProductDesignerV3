package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
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
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.Shapes.SketchShapeRectangle;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SinkItem extends TableDesignerItem implements Cuttable, DependOnMaterial {

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

    private static ArrayList<SinkType> sinkTypes = new ArrayList<>();


    Label labelRowNumber, labelName, labelMaterial, labelType, labelModel, labelNull1, labelQuantity, labelRowPrice;
    ImageView imageViewMain, imageViewInstallType, imageViewEdgeType;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int depth;
    int type;
    String model;
    int installType;
    int edgeType;
    Image imageMain;
    Image imageInstallType;
    Image imageEdgeType;
    String name;

    int cutForm; //circle or rectangle

    double workCoefficient;
    int workCoefficientIndex;

    double rowPrice = 0;

    public SinkItem(ArrayList<ArrayList<Point2D>> cutShapesCoordinates, ArrayList<ArrayList<Double>> cutShapesAngles,
                    int quantity, Material material, int depth, int type, String model, int installType, int edgeType,
                    double workCoefficient, int workCoefficientIndex, String name) {

        this.cutShapesCoordinates = cutShapesCoordinates;
        this.cutShapesAngles = cutShapesAngles;
        this.quantity = quantity;
        this.material = material;
        this.depth = depth;
        this.type = type;
        this.model = model;
        this.installType = installType;
        this.edgeType = edgeType;
        this.workCoefficient = workCoefficient;
        this.workCoefficientIndex = workCoefficientIndex;
        this.name = name;

        if (type == 1 || type == 20 || (type >= 4 && type <= 10)) {
            cutForm = Sink.SINK_CUTOUT_CIRCLE_FORM;
        } else {
            cutForm = Sink.SINK_CUTOUT_RECTANGLE_FORM;
        }


        File file = new File("features_resources/sink/icons/" + "sink_" + type + "_icon.png");
        try {
            imageMain = new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        imageInstallType = new ImageView(Project.class
                .getResource("/styles/images/TableDesigner/SinkItem/sink_install_type_" + installType + ".png")
                .toString()).getImage();
        imageEdgeType = new ImageView(Project.class
                .getResource("/styles/images/TableDesigner/SinkItem/sink_edge_type_" + edgeType + ".png")
                .toString()).getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/SinkRow.fxml")
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
        ArrayList<ArrayList<Double>> sizeList = new ArrayList<>();
        if (Sink.isCuttable(type, model.split(" ")[0], sizeList)) {
            System.out.println(sizeList.toString());

            for (int i = 0; i < quantity; i++) {

                ArrayList<SketchShape> sinkItemShapes = new ArrayList<>(5);
                for (int j = 0; j < sizeList.size(); j++) {

                    double width = sizeList.get(j).get(0);
                    double height = sizeList.get(j).get(1);

                    //create five shapes:

                    SketchShape shape = new SketchShapeRectangle(ElementTypes.TABLETOP, material, depth, width, height);
                    shape.setWorkCoefficient(workCoefficient);
                    shape.setProductName(name);
                    SketchDesigner.getSketchShapesList().add(shape);
                    //add shape to cutPane
                    CutShape cutShape = shape.getCutShape();
                    CutDesigner.getInstance().getCutShapesList().add(cutShape);
                    CutDesigner.getInstance().usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
                    CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);

                    sinkItemShapes.add(shape);

                    if(cutShapesCoordinates.get(i).size() < j+1){
                        cutShapesCoordinates.get(i).add(new Point2D(0, 0));
                    }
                    cutShape.setTranslateX(cutShapesCoordinates.get(i).get(j).getX());
                    cutShape.setTranslateY(cutShapesCoordinates.get(i).get(j).getY());

                    if(cutShapesAngles.get(i).size() < j+1){
                        cutShapesAngles.get(i).add(0.0);
                    }
                    cutShape.rotateShapeLocal(cutShapesAngles.get(i).get(j).doubleValue());

                }

                sketchShapeArrayList.add(sinkItemShapes);

            }
        }

    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(SinkItem item) {

        SinkItem oldSinkItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterialsListInProject().contains(item.getMaterial())) {
            newMaterial = oldSinkItem.material;
        } else {
            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType()) && defaultMaterial.getDepths().contains("" + oldSinkItem.depth)) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterialsListInProject()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType()) && material.getDepths().contains("" + oldSinkItem.depth)) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldSinkItem.removeThisItem();
                    return;
                }
            }
        }


        if (newMaterial.getDepths().contains("" + oldSinkItem.depth)) {

            ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<ArrayList<Point2D>>();
            ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<ArrayList<Double>>();

            for (ArrayList<Point2D> arr1 : oldSinkItem.cutShapesCoordinates) {

                ArrayList<Point2D> arrayPoints = new ArrayList<>();
                for (Point2D p : arr1) {
                    arrayPoints.add(new Point2D(p.getX(), p.getY()));
                }
                cutShapesCoordinates.add(arrayPoints);
            }

            for (ArrayList<Double> arr1 : oldSinkItem.cutShapesAngles) {

                ArrayList<Double> arrayAngles = new ArrayList<>();
                for (Double d : arr1) {
                    arrayAngles.add(Double.valueOf(d.doubleValue()));
                }
                cutShapesAngles.add(arrayAngles);
            }

            SinkItem newSinkItem = new SinkItem(cutShapesCoordinates, cutShapesAngles, oldSinkItem.quantity,
                    newMaterial, oldSinkItem.depth, oldSinkItem.type, oldSinkItem.model, oldSinkItem.installType,
                    oldSinkItem.edgeType, oldSinkItem.workCoefficient, oldSinkItem.workCoefficientIndex, oldSinkItem.name);

            oldSinkItem.removeThisItem();
            tableDesignerItemsList.add(newSinkItem);
        } else {
            oldSinkItem.removeThisItem();
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

    public int getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    public int getInstallType() {
        return installType;
    }

    public int getEdgeType() {
        return edgeType;
    }

    public int getCutForm() {
        return cutForm;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        String imgPath = "features_resources/sink/icons/" + "sink_" + type + "_icon.png";
        File file = new File(imgPath);
        try {
            imagesList.put("Раковина#" + imgPath, new ImageView(new Image(file.toURI().toURL().toString())));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }


        if (type != 16 && type != 19) {
            String imgPath1 = "/styles/images/TableDesigner/SinkItem/sink_install_type_" + installType + ".png";
            imagesList.put("Установка раковины#" + imgPath1, new ImageView(imgPath1));

            String imgPath2 = "/styles/images/TableDesigner/SinkItem/sink_edge_type_" + edgeType + ".png";
            imagesList.put("Кромка раковины#" + imgPath2, new ImageView(imgPath2));
        }

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);

        for (ArrayList<SketchShape> shapesItemList : sketchShapeArrayList) {

            for (SketchShape shape : shapesItemList) {
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
            SinkItem.exitFromEditMode(this);
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
        imageViewInstallType = (ImageView) anchorPaneImageView.lookup("#imageViewInstallType");
        imageViewEdgeType = (ImageView) anchorPaneImageView.lookup("#imageViewEdgeType");
        labelMaterial = (Label) hBox.getChildren().get(3);
        labelType = (Label) hBox.getChildren().get(4);
        labelModel = (Label) hBox.getChildren().get(5);
        labelNull1 = (Label) hBox.getChildren().get(6);
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
        HBox.setHgrow(labelModel, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
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

        //create shape and add it to sketchDesignerList
        ArrayList<ArrayList<Double>> sizeList = new ArrayList<>();
        if (Sink.isCuttable(type, model.split(" ")[0], sizeList)) {


            ArrayList<SketchShape> sinkItemShapes = new ArrayList<>(5);
            ArrayList<Point2D> coordList = new ArrayList<>();
            ArrayList<Double> angleList = new ArrayList<>();
            for (int j = 0; j < sizeList.size(); j++) {
                double width = sizeList.get(j).get(0);
                double height = sizeList.get(j).get(1);
                //create five shapes:
                SketchShape shape = new SketchShapeRectangle(ElementTypes.TABLETOP, material, depth, width, height);
                shape.setWorkCoefficient(workCoefficient);
                SketchDesigner.getSketchShapesList().add(shape);
                //add shape to cutPane
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().add(cutShape);
                CutDesigner.getInstance().usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);
                sinkItemShapes.add(shape);

                coordList.add(new Point2D(0, 0));
                cutShape.setTranslateX(0);
                cutShape.setTranslateY(0);

                angleList.add(Double.valueOf(0));
                cutShape.rotateShapeLocal(0);
            }
            sketchShapeArrayList.add(sinkItemShapes);
            cutShapesCoordinates.add(coordList);
            cutShapesAngles.add(angleList);
        }
        updateItemView();

    }
    private void btnMinusClicked(ActionEvent event){
        if (quantity == 1) return;
        quantity--;

        //delete one shape:
        System.out.println("sketchShapeArrayList.size() = " + sketchShapeArrayList.size());

        ArrayList<ArrayList<Double>> sizeList = new ArrayList<>();
        if (Sink.isCuttable(type, model.split(" ")[0], sizeList)) {
            ArrayList<SketchShape> shapesItemList = sketchShapeArrayList.remove(sketchShapeArrayList.size() - 1);
            for (SketchShape shape : shapesItemList) {

                SketchDesigner.getSketchShapesList().remove(shape);
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().remove(cutShape);
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().remove(cutShape);
            }
            System.out.println("cutShapesCoordinates.size() = " + cutShapesCoordinates.size());
            cutShapesCoordinates.remove(cutShapesCoordinates.size() - 1);
            System.out.println("cutShapesAngles.size() = " + cutShapesAngles.size());
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
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);


        if (type == 16 || type == 19) {
            imageViewInstallType.setImage(null);
            imageViewEdgeType.setImage(null);
        } else {
            imageViewInstallType.setImage(imageInstallType);
            imageViewEdgeType.setImage(imageEdgeType);
        }


        labelMaterial.setText(material.getReceiptName() + " " + depth + "мм");
        labelType.setText("#" + type);
        labelModel.setText(model);
        labelNull1.setText("");

        labelQuantity.setText("" + quantity);

        //card view:
        labelHeaderCard.setText(name);
        tooltipNameCard.setText(name);
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);

        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("толщина материала");
        labelValue2Card.setText("" + depth + " мм");

        labelName3Card.setText("Тип");
        labelValue3Card.setText("#" + type);

        labelName4Card.setText("Модель");
        labelValue4Card.setText(model);

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getSinkCurrency();
        String units = "шт.";
        double priceForOne = -1.0;

        if (type == Sink.SINK_TYPE_16 || type == Sink.SINK_TYPE_17 || type == Sink.SINK_TYPE_19 || type == Sink.SINK_TYPE_20 || type == Sink.SINK_TYPE_21) {
            priceForOne = material.getSinkCommonTypesAndPrices().get(type);
            currency = material.getSinkCommonCurrency().get(type);
            //priceForOne += material.getSinkInstallTypesAndPrices()
        } else {
            String modelShort = model.split(" ")[0];

//            System.out.println("material name = " + material.getName());
//            System.out.println("material.getAvailableSinkModels() = " + material.getAvailableSinkModels());
//            System.out.println("modelShort = " + modelShort);
//            System.out.println("material.getAvailableSinkModels().get(modelShort) = " + material.getAvailableSinkModels().get(modelShort));

            if(material.getAvailableSinkModels().get(modelShort) == null){
                priceForOne = -1; //sink unavailable now

                labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + ReceiptManager.RUR_SYMBOL);
                labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

                rowPrice = priceForOne * quantity;

                return;
            }else{
                priceForOne = material.getAvailableSinkModels().get(modelShort).intValue();
            }
        }

        priceForOne /= 100.0;

//        System.out.println("MainWindow.getUSDValue().doubleValue() = " + MainWindow.getUSDValue().doubleValue());
//        System.out.println("MainWindow.getEURValue().doubleValue() = " + MainWindow.getEURValue().doubleValue());

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;

//        System.out.println("priceForOne = " + priceForOne);

        //edgeType
        {
            double priceForOneEdgeType = 0;
            if (type != Sink.SINK_TYPE_16 && type != Sink.SINK_TYPE_19 && type != Sink.SINK_TYPE_19 && type != Sink.SINK_TYPE_21) {

                if (cutForm == Sink.SINK_CUTOUT_RECTANGLE_FORM) {
                    priceForOneEdgeType = (material.getSinkEdgeTypesRectangleAndPrices().get(edgeType - 1)) / 100;
                } else if (cutForm == Sink.SINK_CUTOUT_CIRCLE_FORM) {
                    priceForOneEdgeType = (material.getSinkEdgeTypesCircleAndPrices().get(edgeType - 1)) / 100;
                }
            }

            priceForOne += priceForOneEdgeType;
        }
        //installType:
        {
            double priceForOneInstall = 0;

            if (type == Sink.SINK_TYPE_16){
                priceForOneInstall = material.getCutoutTypesAndPrices().get(Integer.valueOf(5)) / 100;
            }else if(type == Sink.SINK_TYPE_19){
                priceForOneInstall = material.getCutoutTypesAndPrices().get(Integer.valueOf(15)) / 100;
            }else if(type == Sink.SINK_TYPE_21){
                priceForOneInstall = material.getCutoutTypesAndPrices().get(Integer.valueOf(16)) / 100;
            }else if (type == Sink.SINK_TYPE_17){
                //priceForOneInstall = material.getCutoutTypesAndPrices().get(new Integer(5)) / 100;
                priceForOneInstall += (material.getSinkInstallTypesAndPrices().get(1)) / 100;
            }else{
                priceForOneInstall = (material.getSinkInstallTypesAndPrices().get(installType-1)) / 100;
            }

            priceForOne += priceForOneInstall;
        }

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + ReceiptManager.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

        rowPrice = priceForOne * quantity;

//        System.out.println("rowPrice = " + rowPrice);
    }

    public double getRowPrice() {
        return rowPrice;
    }

    public double getOnlySinkPrice(){
        String currency = material.getSinkCurrency();
        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        return (material.getAvailableSinkModels().get(model.split(" ")[0]).intValue()/100.0)*multiplier;
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
    private static ComboBox<SinkType> comboBoxSinkType;
    private static ChoiceBox<String> choiceBoxSinkModel;
    private static ToggleButton toggleButtonSinkInstallType1, toggleButtonSinkInstallType2;
    private static ToggleGroup toggleGroupSinkInstallType = new ToggleGroup();
    private static ToggleButton toggleButtonSinkEdge1, toggleButtonSinkEdge2;
    private static ToggleGroup toggleGroupSinkEdge = new ToggleGroup();
    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/SinkSettings.fxml"));

            try {
                anchorPaneSettingsView = fxmlLoader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            settingsControlElementsInit();
            //System.out.println(choiceBoxMaterial);
            settingsControlElementsLogicInit();
            //System.out.println(choiceBoxMaterial);
        }
        settingsControlElementsRefresh();

        return anchorPaneSettingsView;
    }

    private static void settingsControlElementsInit() {

        btnApply.getStyleClass().add("btnBrown");
        btnCancel.getStyleClass().add("btnBrown");

        choiceBoxMaterial = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxMaterial");
        choiceBoxDepth = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxDepth");
        comboBoxSinkType = (ComboBox<SinkType>) anchorPaneSettingsView.lookup("#comboBoxSinkType");
        choiceBoxSinkModel = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxSinkModel");

        toggleButtonSinkInstallType1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonSinkInstallType1");
        toggleButtonSinkInstallType2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonSinkInstallType2");
        toggleButtonSinkEdge1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonSinkEdge1");
        toggleButtonSinkEdge2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonSinkEdge2");


        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select(0);


        comboBoxSinkType.setCellFactory(new Callback<ListView<SinkType>, ListCell<SinkType>>() {
            @Override
            public ListCell<SinkType> call(ListView<SinkType> param) {

                ListCell<SinkType> cell = new ListCell<SinkType>() {
                    @Override
                    protected void updateItem(SinkType item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            //setText("#" + item.getType());
                            setText(null);
                            setGraphic(item.getImage());
                            setTooltip(item.getTooltip());
                        }
                    }
                };
                return cell;
            }
        });
        comboBoxSinkType.setButtonCell(new ListCell<SinkType>() {
            @Override
            protected void updateItem(SinkType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText("#" + item.getType());
                    setText(null);
                    setGraphic(item.getImageCopy());
                    setTooltip(item.getTooltipCopy());
                }
            }
        });

        sinkTypes.clear();
        sinkTypes.add(null);
        for(int i = 1; i<= 22;i++){
            sinkTypes.add(new SinkType(i));
        }


        if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1 || Project.getDefaultMaterial().getMainType().indexOf("Полиэфирный камень") != -1) {

            for (int i = 1; i <= 11; i++) {
//                comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), i));
                comboBoxSinkType.getItems().add(sinkTypes.get(i));
            }
            if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1)
//                comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 18));
                comboBoxSinkType.getItems().add(sinkTypes.get(18));

            comboBoxSinkType.getItems().add(sinkTypes.get(16));
            comboBoxSinkType.getItems().add(sinkTypes.get(17));

            comboBoxSinkType.getItems().add(sinkTypes.get(19));
            comboBoxSinkType.getItems().add(sinkTypes.get(20));
            comboBoxSinkType.getItems().add(sinkTypes.get(21));
//            comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 16));
//            comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 17));
        }else if(Project.getDefaultMaterial().getMainType().indexOf("Массив") != -1 ||
                Project.getDefaultMaterial().getMainType().indexOf("Массив_шпон") != -1){
            comboBoxSinkType.getItems().add(sinkTypes.get(16));
            comboBoxSinkType.getItems().add(sinkTypes.get(19));
            comboBoxSinkType.getSelectionModel().select(0);

            toggleButtonSinkInstallType2.setSelected(true);
        } else {

            for (int i = 12; i <= 14; i++) {
               // comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), i));
                comboBoxSinkType.getItems().add(sinkTypes.get(i));
            }

            comboBoxSinkType.getItems().add(sinkTypes.get(22));

            comboBoxSinkType.getItems().add(sinkTypes.get(15));
            comboBoxSinkType.getItems().add(sinkTypes.get(16));
            comboBoxSinkType.getItems().add(sinkTypes.get(17));

            comboBoxSinkType.getItems().add(sinkTypes.get(19));
            comboBoxSinkType.getItems().add(sinkTypes.get(20));
            comboBoxSinkType.getItems().add(sinkTypes.get(21));

        }



//        comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 19));
//        comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 20));
//        comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 21));

        comboBoxSinkType.getSelectionModel().select(0);
        comboBoxSinkType.setTooltip(comboBoxSinkType.getSelectionModel().getSelectedItem().getTooltip());

        choiceBoxSinkModel.getItems().addAll(Sink.getAvailableModels(comboBoxSinkType.getSelectionModel().getSelectedItem().getType()));
        choiceBoxSinkModel.getSelectionModel().select(0);

        toggleButtonSinkInstallType1.setToggleGroup(toggleGroupSinkInstallType);
        toggleButtonSinkInstallType2.setToggleGroup(toggleGroupSinkInstallType);
        toggleButtonSinkEdge1.setToggleGroup(toggleGroupSinkEdge);
        toggleButtonSinkEdge2.setToggleGroup(toggleGroupSinkEdge);

        if (comboBoxSinkType.getSelectionModel().getSelectedIndex() <= 11) {
            toggleButtonSinkInstallType2.setSelected(true);
        } else {
            toggleButtonSinkInstallType1.setSelected(true);
        }
        toggleButtonSinkEdge1.setSelected(true);

//        ImageView image1 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SinkItem/sink_install_type_1.png").toString());
//        image1.setFitWidth(45);
//        image1.setFitHeight(45);
//        toggleButtonSinkInstallType1.setGraphic(image1);
//
//        ImageView image2 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SinkItem/sink_install_type_2.png").toString());
//        image2.setFitWidth(45);
//        image2.setFitHeight(45);
//        toggleButtonSinkInstallType2.setGraphic(image2);
//
//        ImageView image3 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SinkItem/sink_edge_type_1.png").toString());
//        image3.setFitWidth(45);
//        image3.setFitHeight(45);
//        toggleButtonSinkEdge1.setGraphic(image3);
//
//        ImageView image4 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SinkItem/sink_edge_type_2.png").toString());
//        image4.setFitWidth(45);
//        image4.setFitHeight(45);
//        toggleButtonSinkEdge2.setGraphic(image4);
    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));


        choiceBoxMaterial.setOnAction(event -> {

            comboBoxSinkType.getItems().clear();

            for (Material material : Project.getMaterialsListInProject()) {
                if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {

                    if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {

                        for (int i = 1; i <= 11; i++) {
//                            comboBoxSinkType.getItems().add(new SinkType(material, i));
                            comboBoxSinkType.getItems().add(sinkTypes.get(i));
                        }

                        if (material.getMainType().indexOf("Акриловый камень") != -1)
                            comboBoxSinkType.getItems().add(sinkTypes.get(18));
//                            comboBoxSinkType.getItems().add(new SinkType(material, 18));

                        comboBoxSinkType.getItems().add(sinkTypes.get(16));
                        comboBoxSinkType.getItems().add(sinkTypes.get(17));

                        comboBoxSinkType.getItems().add(sinkTypes.get(19));
                        comboBoxSinkType.getItems().add(sinkTypes.get(20));
                        comboBoxSinkType.getItems().add(sinkTypes.get(21));
//                        comboBoxSinkType.getItems().add(new SinkType(material, 16));
//                        comboBoxSinkType.getItems().add(new SinkType(material, 17));
                        comboBoxSinkType.getSelectionModel().select(0);

                        toggleButtonSinkInstallType2.setSelected(true);
                    }else if(material.getName().indexOf("Массив") != -1 ||
                            material.getName().indexOf("Массив_шпон") != -1){
                        comboBoxSinkType.getItems().add(sinkTypes.get(16));
                        comboBoxSinkType.getItems().add(sinkTypes.get(19));
                        comboBoxSinkType.getSelectionModel().select(0);

                        toggleButtonSinkInstallType2.setSelected(true);
                    } else {

                        for (int i = 12; i <= 14; i++) {
//                            comboBoxSinkType.getItems().add(new SinkType(material, i));
                            comboBoxSinkType.getItems().add(sinkTypes.get(i));
                        }

                        comboBoxSinkType.getItems().add(sinkTypes.get(22));

                        comboBoxSinkType.getItems().add(sinkTypes.get(15));
                        comboBoxSinkType.getItems().add(sinkTypes.get(16));
                        comboBoxSinkType.getItems().add(sinkTypes.get(17));


                        comboBoxSinkType.getItems().add(sinkTypes.get(19));
                        comboBoxSinkType.getItems().add(sinkTypes.get(20));
                        comboBoxSinkType.getItems().add(sinkTypes.get(21));



                        comboBoxSinkType.getSelectionModel().select(0);


                        toggleButtonSinkInstallType1.setSelected(true);


                    }



//                    comboBoxSinkType.getItems().add(new SinkType(material, 19));
//                    comboBoxSinkType.getItems().add(new SinkType(material, 20));
//                    comboBoxSinkType.getItems().add(new SinkType(material, 21));

                    //update depths:
                    choiceBoxDepth.getItems().clear();
                    choiceBoxDepth.getItems().addAll(material.getDepths());
                    choiceBoxDepth.getSelectionModel().select(0);


                    toggleButtonSinkInstallType1.setDisable(false);
                    toggleButtonSinkInstallType2.setDisable(false);
                    toggleButtonSinkEdge1.setDisable(false);
                    toggleButtonSinkEdge2.setDisable(false);
                    if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == 1){
                        toggleButtonSinkInstallType1.setSelected(true);
                        toggleButtonSinkEdge1.setSelected(true);
                    }else if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == 12){
                        toggleButtonSinkInstallType2.setSelected(true);
                        toggleButtonSinkEdge1.setSelected(true);
                    }
                }
            }

            updatePriceInSettings();
        });


        comboBoxSinkType.setOnAction(event -> {
            if (comboBoxSinkType.getItems().size() == 0 || comboBoxSinkType.getSelectionModel().getSelectedIndex() == -1)
                return;

            toggleButtonSinkInstallType1.setDisable(false);
            toggleButtonSinkInstallType2.setDisable(false);
            toggleButtonSinkEdge1.setDisable(false);
            toggleButtonSinkEdge2.setDisable(false);
            if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() >= 1 &&
                    comboBoxSinkType.getSelectionModel().getSelectedItem().getType() <=11) {

                toggleButtonSinkInstallType1.setSelected(true);
                toggleButtonSinkEdge1.setSelected(true);

            }else if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() >= 12 &&
                    comboBoxSinkType.getSelectionModel().getSelectedItem().getType() <=14) {

                toggleButtonSinkInstallType2.setSelected(true);
                toggleButtonSinkEdge1.setSelected(true);

            }else if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == 18) {

                toggleButtonSinkInstallType1.setSelected(true);
                toggleButtonSinkEdge1.setSelected(true);

            }else if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == Sink.SINK_TYPE_16 ||
                    comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == Sink.SINK_TYPE_19 ||
                    comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == Sink.SINK_TYPE_21) {
//                ImageView image1 = new ImageView("styles/images/TableDesigner/SinkItem/sink_install_type_0.png");
//                image1.setFitWidth(45);
//                image1.setFitHeight(45);
//                toggleButtonSinkInstallType1.setGraphic(image1);
//
//                ImageView image2 = new ImageView("styles/images/TableDesigner/SinkItem/sink_install_type_1.png");
//                image2.setFitWidth(45);
//                image2.setFitHeight(45);
//                toggleButtonSinkInstallType2.setGraphic(image2);
                toggleButtonSinkInstallType1.setSelected(false);
                toggleButtonSinkInstallType2.setSelected(false);
                toggleButtonSinkEdge1.setSelected(false);
                toggleButtonSinkEdge2.setSelected(false);

                toggleButtonSinkInstallType1.setDisable(true);
                toggleButtonSinkInstallType2.setDisable(true);

                toggleButtonSinkEdge1.setDisable(true);
                toggleButtonSinkEdge2.setDisable(true);

            } else if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == 17) {
//                ImageView image1 = new ImageView("styles/images/TableDesigner/SinkItem/sink_install_type_0.png");
//                image1.setFitWidth(45);
//                image1.setFitHeight(45);
//                toggleButtonSinkInstallType1.setGraphic(image1);
//
//                ImageView image2 = new ImageView("styles/images/TableDesigner/SinkItem/sink_install_type_1.png");
//                image2.setFitWidth(45);
//                image2.setFitHeight(45);
//                toggleButtonSinkInstallType2.setGraphic(image2);


                toggleButtonSinkInstallType2.setSelected(true);

                toggleButtonSinkEdge1.setSelected(true);

                toggleButtonSinkInstallType1.setDisable(true);
                toggleButtonSinkInstallType2.setDisable(true);

                toggleButtonSinkEdge1.setDisable(false);
                toggleButtonSinkEdge2.setDisable(false);

            } else if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == Sink.SINK_TYPE_20){
                toggleButtonSinkInstallType1.setSelected(true);
                toggleButtonSinkEdge1.setSelected(true);


            }


            if (comboBoxSinkType.getSelectionModel().getSelectedItem() != null) {
                comboBoxSinkType.setTooltip(comboBoxSinkType.getSelectionModel().getSelectedItem().getTooltip());
            }


            choiceBoxSinkModel.getItems().clear();
            choiceBoxSinkModel.getItems().addAll(Sink.getAvailableModels(comboBoxSinkType.getSelectionModel().getSelectedItem().getType()));
            choiceBoxSinkModel.getSelectionModel().select(0);

            updatePriceInSettings();
        });

        choiceBoxSinkModel.setOnAction(event -> {
            updatePriceInSettings();
        });


        toggleGroupSinkInstallType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == toggleButtonSinkInstallType1) {
                if (comboBoxSinkType.getSelectionModel().getSelectedItem().getType() == Sink.SINK_TYPE_16) {
                    toggleButtonSinkEdge1.setDisable(true);
                    toggleButtonSinkEdge2.setDisable(true);
                } else {
                    toggleButtonSinkEdge1.setDisable(false);
                    toggleButtonSinkEdge2.setDisable(false);
                }
            } else if (newValue == toggleButtonSinkInstallType2){
                toggleButtonSinkEdge1.setDisable(false);
                toggleButtonSinkEdge2.setDisable(false);
            }else{
                toggleGroupSinkInstallType.selectToggle(oldValue);
            }


            updatePriceInSettings();
        });

        toggleGroupSinkEdge.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null){
                toggleGroupSinkEdge.selectToggle(oldValue);
            }
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){

        Material material = null;
        for (Material m : Project.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        int depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());

        int type = comboBoxSinkType.getSelectionModel().getSelectedItem().getType();


        int installType = 1;
        if (toggleButtonSinkInstallType1.isSelected()) installType = 1;
        else if (toggleButtonSinkInstallType2.isSelected()) installType = 2;

        if (type == 16) installType = 1;


        int edgeType = 1;
        if (toggleButtonSinkEdge1.isSelected()) edgeType = 1;
        else if (toggleButtonSinkEdge2.isSelected()) edgeType = 2;

        String model = choiceBoxSinkModel.getSelectionModel().getSelectedItem();

        ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<>(1);
        ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<>(5);

        for(int j=0;j<quantity;j++){
            ArrayList<Point2D> coordList = new ArrayList<>(5);
            coordList.add(new Point2D(0, 0));
            coordList.add(new Point2D(0, 0));
            coordList.add(new Point2D(0, 0));
            coordList.add(new Point2D(0, 0));
            coordList.add(new Point2D(0, 0));


            ArrayList<Double> angleList = new ArrayList<>(5);
            angleList.add(Double.valueOf(0));
            angleList.add(Double.valueOf(0));
            angleList.add(Double.valueOf(0));
            angleList.add(Double.valueOf(0));
            angleList.add(Double.valueOf(0));

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

        String name = "Раковина " + model;
        if(type == Sink.SINK_TYPE_16) name = "Раковина накладная";
        else if(type == Sink.SINK_TYPE_17) name = "Раковина подстольная";
        else if(type == Sink.SINK_TYPE_19) name = "Раковина накладная (прямоугольная)";
        else if(type == Sink.SINK_TYPE_20) name = "Раковина подстольная (круглая)";
        else if(type == Sink.SINK_TYPE_21) name = "Раковина в ровень со столешницей";
        tableDesignerItemsList.add(index, new SinkItem(cutShapesCoordinates, cutShapesAngles, quantity, material,
                depth, type, model, installType, edgeType, workCoefficient, workCoefficientIndex, name));

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().clear();
        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select("" + Project.getDefaultMaterial().getDefaultDepth());

        comboBoxSinkType.getItems().clear();
        if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1 || Project.getDefaultMaterial().getMainType().indexOf("Полиэфирный камень") != -1) {

            for (int i = 1; i <= 11; i++) {

                if(Project.getDefaultMaterial().getAvailableSinkTypes().contains(i)){
                    comboBoxSinkType.getItems().add(sinkTypes.get(i));
                }


//                comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), i));
            }
            if (Project.getDefaultMaterial().getMainType().indexOf("Акриловый камень") != -1)
                comboBoxSinkType.getItems().add(sinkTypes.get(18));
//                comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 18));

            comboBoxSinkType.getItems().add(sinkTypes.get(16));
            comboBoxSinkType.getItems().add(sinkTypes.get(17));

            comboBoxSinkType.getItems().add(sinkTypes.get(19));
            comboBoxSinkType.getItems().add(sinkTypes.get(20));
            comboBoxSinkType.getItems().add(sinkTypes.get(21));

//            comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 16));
//            comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 17));
            //toggleButtonSinkInstallType2.setSelected(true);
        }else if(Project.getDefaultMaterial().getMainType().indexOf("Массив") != -1 ||
                Project.getDefaultMaterial().getMainType().indexOf("Массив_шпон") != -1){
            comboBoxSinkType.getItems().add(sinkTypes.get(16));
            comboBoxSinkType.getItems().add(sinkTypes.get(19));


//            comboBoxSinkType.getItems().add(sinkTypes.get(17));
        } else {

            for (int i = 12; i <= 14; i++) {
                comboBoxSinkType.getItems().add(sinkTypes.get(i));
//                comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), i));
            }

            comboBoxSinkType.getItems().add(sinkTypes.get(22));

            comboBoxSinkType.getItems().add(sinkTypes.get(15));
            comboBoxSinkType.getItems().add(sinkTypes.get(16));
            comboBoxSinkType.getItems().add(sinkTypes.get(17));


            comboBoxSinkType.getItems().add(sinkTypes.get(19));
            comboBoxSinkType.getItems().add(sinkTypes.get(20));
            comboBoxSinkType.getItems().add(sinkTypes.get(21));


            //toggleButtonSinkInstallType1.setSelected(true);
        }


//        comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 19));
//        comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 20));
//        comboBoxSinkType.getItems().add(new SinkType(ProjectHandler.getDefaultMaterial(), 21));

        comboBoxSinkType.getSelectionModel().select(0);

        choiceBoxSinkModel.getItems().clear();
        choiceBoxSinkModel.getItems().addAll(Sink.getAvailableModels(comboBoxSinkType.getSelectionModel().getSelectedItem().getType()));
        choiceBoxSinkModel.getSelectionModel().select(0);

        int selectedSinkType = comboBoxSinkType.getSelectionModel().getSelectedItem().getType();


        if(selectedSinkType == 16 || selectedSinkType == 17){
            toggleButtonSinkInstallType1.setDisable(true);
            toggleButtonSinkInstallType2.setDisable(true);
            toggleButtonSinkEdge1.setDisable(true);
            toggleButtonSinkEdge2.setDisable(true);
        }else{
            toggleButtonSinkInstallType1.setDisable(false);
            toggleButtonSinkInstallType2.setDisable(false);
            toggleButtonSinkEdge1.setDisable(false);
            toggleButtonSinkEdge2.setDisable(false);
        }

        if (selectedSinkType == 1){
            toggleButtonSinkInstallType1.setSelected(true);
            toggleButtonSinkEdge1.setSelected(true);
        }else if (selectedSinkType == 12){
            toggleButtonSinkInstallType2.setSelected(true);
            toggleButtonSinkEdge1.setSelected(true);
        }



        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (comboBoxSinkType.getSelectionModel().getSelectedItem() == null) return;
        if (choiceBoxSinkModel.getSelectionModel().getSelectedItem() == null) return;

        for (Material material : Project.getMaterialsListInProject()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getSinkCurrency();
                String units = "шт.";
                double priceForOne = -1.0;

                int type = comboBoxSinkType.getSelectionModel().getSelectedItem().getType();
                String model = choiceBoxSinkModel.getSelectionModel().getSelectedItem().split(" ")[0];


                if (type == Sink.SINK_TYPE_16 || type == Sink.SINK_TYPE_17 || type == Sink.SINK_TYPE_19 || type == Sink.SINK_TYPE_20 || type == Sink.SINK_TYPE_21) {
                    //System.out.println(material.getSinkCommonTypesAndPrices().toString());
                    priceForOne = material.getSinkCommonTypesAndPrices().get(type);
                    currency = material.getSinkCommonCurrency().get(type);
                } else {
                    if(material.getAvailableSinkModels().get(model) != null){
                        priceForOne = material.getAvailableSinkModels().get(model).intValue();
                    }
                }

                //System.out.println("priceForOne = " + priceForOne);


                priceForOne /= 100.0;

                double multiplier = 1;
                if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
                else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
                else if (currency.equals("RUB")) multiplier = 1;
                priceForOne *= multiplier;

                //System.out.println("priceForOne = " + priceForOne);
                //edgeType
                {
                    double priceForOneEdgeType = 0;
                    if (type != Sink.SINK_TYPE_16 && type != Sink.SINK_TYPE_19 && type != Sink.SINK_TYPE_21) {

                        int edgeType = 0;
                        int cutForm = 0;

                        if (toggleButtonSinkEdge1.isSelected()) edgeType = 0;
                        else if (toggleButtonSinkEdge2.isSelected()) edgeType = 1;

                        if (type == 1 || type == 20 || (type >= 4 && type <= 10)) {
                            cutForm = Sink.SINK_CUTOUT_CIRCLE_FORM;
                        } else {
                            cutForm = Sink.SINK_CUTOUT_RECTANGLE_FORM;
                        }


                        if (cutForm == Sink.SINK_CUTOUT_RECTANGLE_FORM) {
                            priceForOneEdgeType = (material.getSinkEdgeTypesRectangleAndPrices().get(edgeType)) / 100;
                        } else if (cutForm == Sink.SINK_CUTOUT_CIRCLE_FORM) {
                            priceForOneEdgeType = (material.getSinkEdgeTypesCircleAndPrices().get(edgeType)) / 100;
                        }
                    }

                    priceForOne += priceForOneEdgeType;

                    //System.out.println("priceForOneEdgeType = " + priceForOneEdgeType);
                }
                //installType:
                {
                    double priceForOneInstall = 0;

                    int installType = 0;
                    if(toggleButtonSinkInstallType1.isSelected()) installType = 1;
                    if(toggleButtonSinkInstallType2.isSelected()) installType = 2;

                    if (type == Sink.SINK_TYPE_16){
                        //priceForOneInstall = 0;
                        priceForOneInstall = material.getCutoutTypesAndPrices().get(Integer.valueOf(5)) / 100;
                    }else if(type == Sink.SINK_TYPE_19){
                        priceForOneInstall = material.getCutoutTypesAndPrices().get(Integer.valueOf(15)) / 100;
                    }else if(type == Sink.SINK_TYPE_21){
                        priceForOneInstall = material.getCutoutTypesAndPrices().get(Integer.valueOf(16)) / 100;
                    }else if (type == Sink.SINK_TYPE_17){
                        //priceForOneInstall = material.getCutoutTypesAndPrices().get(new Integer(5)) / 100;
                        priceForOneInstall += (material.getSinkInstallTypesAndPrices().get(1)) / 100;
                    }else{
//                        System.out.println("installType = " + installType);
//                        System.out.println("material.getSinkInstallTypesAndPrices() = " + material.getSinkInstallTypesAndPrices().toString());
//                        System.out.println("material.getSinkInstallTypesAndPrices().get(installType-1) = " + material.getSinkInstallTypesAndPrices().get(installType-1));
                        priceForOneInstall = (material.getSinkInstallTypesAndPrices().get(installType-1)) / 100;
                    }

                    priceForOne += priceForOneInstall;


                    //System.out.println("priceForOneInstall = " + priceForOneInstall);
                }

                //System.out.println("priceForOne = " + priceForOne);
                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                //System.out.println("priceForOne * coeff = " + priceForOne);

                labelPrice.setText("Цена: " + String.format(Locale.ENGLISH, "%.0f", priceForOne) + " " + ReceiptManager.RUR_SYMBOL + "/" + units);
                break;
            }
        }
    }

    private static void enterToEditMode(SinkItem sinkItem){
        TableDesigner.openSettings(SinkItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(sinkItem.material.getReceiptName());
        choiceBoxDepth.getSelectionModel().select("" + sinkItem.depth);

        for(int i =0;i< comboBoxSinkType.getItems().size();i++){
            if(comboBoxSinkType.getItems().get(i).getType() == sinkItem.type){
                comboBoxSinkType.getSelectionModel().select(i);
                break;
            }
        }

        if(sinkItem.installType == 1) toggleButtonSinkInstallType1.setSelected(true);
        else if(sinkItem.installType == 2) toggleButtonSinkInstallType2.setSelected(true);

        if(sinkItem.edgeType == 1) toggleButtonSinkEdge1.setSelected(true);
        else if(sinkItem.edgeType == 2) toggleButtonSinkEdge2.setSelected(true);

        choiceBoxSinkModel.getSelectionModel().select(sinkItem.model);


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

            int index = getTableDesignerItemsList().indexOf(sinkItem);
            addItem(index, sinkItem.quantity);

            exitFromEditMode(sinkItem);
            sinkItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(sinkItem);

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
        jsonObject.put("itemName", "SinkItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("depth", depth);
        jsonObject.put("type", type);
        jsonObject.put("model", model);
        jsonObject.put("installType", installType);
        jsonObject.put("edgeType", edgeType);
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

    public static SinkItem initFromJSON(JSONObject jsonObject) {

        String materialName = (String) jsonObject.get("material");

        Material material = null;
        for (Material m : Project.getMaterialsListInProject()) {
            if (materialName.equals(m.getName())) {
                material = m;
                break;
            }
        }
        if (material == null) return null;

        int depth = ((Long) jsonObject.get("depth")).intValue();
        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        int type = ((Long) jsonObject.get("type")).intValue();
        String model = (String) jsonObject.get("model");
        int installType = ((Long) jsonObject.get("installType")).intValue();
        int edgeType = ((Long) jsonObject.get("edgeType")).intValue();
        double workCoefficient = ((Double) jsonObject.get("workCoefficient")).doubleValue();
        int workCoefficientIndex = 0;
        if(jsonObject.get("workCoefficientIndex") != null){
            workCoefficientIndex = ((Long) jsonObject.get("workCoefficientIndex")).intValue();
        }



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

        String name = "Раковина " + model;
        if(type == Sink.SINK_TYPE_16) name = "Раковина накладная";
        else if(type == Sink.SINK_TYPE_17) name = "Раковина подстольная";
        SinkItem sinkItem = new SinkItem(cutShapeCoordinatesGlobal, cutShapeAnglesGlobal, quantity, material,
                depth, type, model, installType, edgeType, workCoefficient, workCoefficientIndex, name);
        sinkItem.updateRowPrice();
        return sinkItem;
    }

}


class SinkType {

    int type = 1;
    //Material material;

    Pane paneMain = new Pane();
    Pane paneCopy = new Pane();

    Tooltip tooltip = new Tooltip();
    Tooltip tooltipCopy = new Tooltip();
    String brief = "null";


    SinkType(int type) {
        this.type = type;
        //this.material = material;

        createImage(paneMain);
        createImage(paneCopy);
        createTooltip(tooltip);
        createTooltip(tooltipCopy);
    }

    private void createImage(Pane pane){

        File file;
        file = new File("features_resources/sink/icons/" + "sink_" + type + "_icon.png");

        ImageView image = null;

        try {
            image = new ImageView(new Image(file.toURI().toURL().toString()));
            image.setFitWidth(100);
            image.setFitHeight(100);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("features_resources/sink/brief/sink_" + type + ".txt"), "UTF8"));
            brief = reader.readLine();

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Sink");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Sink io exception");
        }

        Label label = new Label(brief);
        //pane = new Pane();
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

        //tooltip = new Tooltip();

        File file;
        file = new File("features_resources/sink/infoImages/" + "sink_" + type + "_info_img.png");

        ImageView tooltipImage = null;

        try {
            tooltipImage = new ImageView(new Image(file.toURI().toURL().toString()));
            tooltipImage.setFitWidth(250);
            tooltipImage.setFitHeight(250);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        String brief = "null";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("features_resources/sink/brief/sink_" + type + ".txt"), "UTF8"));
            brief = reader.readLine();
            ;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Sink");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Sink io exception");
        }


        Label label = new Label(brief);
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