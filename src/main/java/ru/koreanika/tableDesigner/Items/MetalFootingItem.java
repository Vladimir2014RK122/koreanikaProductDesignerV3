package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.Material.Material;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.project.Project;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MetalFootingItem extends TableDesignerItem {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalWorkItemsList();


    Label labelRowNumber, labelName, labelPaintingType, labelNull1, labelLength, labelNull2, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    double length;
    int paintingType;
    Image imageMain;


    public MetalFootingItem(double length, int paintingType, int quantity) {

        this.length = length;
        this.paintingType = paintingType;
        this.quantity = quantity;

        imageMain = new ImageView(Project.class.getResource("/styles/images/TableDesigner/MetalFooting/paintingType" + paintingType + "_100px.png").toString()).getImage();


        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/MetalFootingRow.fxml")
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
    }

    public double getLength() {
        return length;
    }

    public int getPaintingType() {
        return paintingType;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/MetalFooting/paintingType" + paintingType + "_100px.png";
        imagesList.put("Металлокаркас#" + imgPath, new ImageView(getClass().getResource(imgPath).toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            MetalFootingItem.exitFromEditMode(this);
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
        labelPaintingType = (Label) hBox.getChildren().get(3);
        labelNull1 = (Label) hBox.getChildren().get(4);
        labelLength = (Label) hBox.getChildren().get(5);
        labelNull2 = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");



        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelPaintingType, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelNull2, Priority.ALWAYS);
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
        updateItemView();
    }
    private void btnMinusClicked(ActionEvent event){
        if (quantity == 1) return;
        quantity--;
        updateItemView();
    }
    private void btnDeleteClicked(ActionEvent event){
        if(editModeProperty.get()) exitFromEditMode(this);

        tableDesignerItemsList.remove(this);
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
        labelName.setText("Металлокаркас");
        imageViewMain.setImage(imageMain);

        labelPaintingType.setText((paintingType == 1) ? "Покраска порошковая" : "Покраска обычная");
        labelNull1.setText("");
        labelLength.setText("" + length + "мм");
        labelNull2.setText("");

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Металлокаркас");
        tooltipNameCard.setText("Металлокаркас");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText("-");

        labelName2Card.setText("Покраска");
        labelValue2Card.setText((paintingType == 1) ? "Порошковая" : "Обычная");

        labelName3Card.setText("Длина");
        labelValue3Card.setText("" + (int)length + " мм");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        double priceForOne = -1.0;
        Material defaultMaterial = Project.getDefaultMaterial();

        priceForOne = (paintingType == 1) ? defaultMaterial.getMetalFootingPrices().get(0)/100 : defaultMaterial.getMetalFootingPrices().get(1)/100;

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000)) + ReceiptManager.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000)) + ReceiptManager.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000)) + ReceiptManager.RUR_SYMBOL);
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

    private static TextField textFieldLength;
    private static ToggleButton toggleButtonPaintingType1, toggleButtonPaintingType2;
    private static ToggleGroup toggleGroupPaintingType = new ToggleGroup();
    private static Label labelPrice;

    private static boolean lengthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/MetalFootingSettings.fxml"));

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

        textFieldLength = (TextField) anchorPaneSettingsView.lookup("#textFieldLength");

        toggleButtonPaintingType1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonPaintingType1");
        toggleButtonPaintingType2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonPaintingType2");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        toggleButtonPaintingType1.setToggleGroup(toggleGroupPaintingType);
        toggleButtonPaintingType2.setToggleGroup(toggleGroupPaintingType);
        toggleButtonPaintingType1.setSelected(true);

//        ImageView image1 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/MetalFooting/paintingType1.png").toString());
//        image1.setFitWidth(45);
//        image1.setFitHeight(45);
//        toggleButtonPaintingType1.setGraphic(image1);
//
//        ImageView image2 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/MetalFooting/paintingType2.png").toString());
//        image2.setFitWidth(45);
//        image2.setFitHeight(45);
//        toggleButtonPaintingType2.setGraphic(image2);

        textFieldLength.setText("1000");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));


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

        toggleGroupPaintingType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceInSettings();
        });


    }

    private static void addItem(int index, int quantity){
        if (!lengthOk) return;

        double length = 0;

        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }


        int paintingType = 1;
        if (toggleButtonPaintingType1.isSelected()) paintingType = 1;
        else if (toggleButtonPaintingType2.isSelected()) paintingType = 2;


        tableDesignerItemsList.add(index, new MetalFootingItem(length, paintingType, quantity));

    }

    public static void settingsControlElementsRefresh() {

        textFieldLength.setText("1000");
        toggleButtonPaintingType1.setSelected(true);

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        String currency = "RUB";
        String units = "м.п.";
        double priceForOne = -1.0;
        Material defaultMaterial = Project.getDefaultMaterial();

        if(defaultMaterial == null) return;
        priceForOne = ((toggleButtonPaintingType1.isSelected()) ? defaultMaterial.getMetalFootingPrices().get(0)/100 : defaultMaterial.getMetalFootingPrices().get(1)/100);
        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));

    }

    private static void enterToEditMode(MetalFootingItem metalFootingItem){
        TableDesigner.openSettings(MetalFootingItem.class);


        //get row data to settings
        textFieldLength.setText("" + metalFootingItem.length);
        if(metalFootingItem.paintingType == 1)toggleButtonPaintingType1.setSelected(true);
        else if(metalFootingItem.paintingType == 2)toggleButtonPaintingType2.setSelected(true);

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

            int index = getTableDesignerItemsList().indexOf(metalFootingItem);
            addItem(index, metalFootingItem.quantity);

            exitFromEditMode(metalFootingItem);
            metalFootingItem.removeThisItem();

        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(metalFootingItem);
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
        jsonObject.put("itemName", "MetalFootingItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("length", length);
        jsonObject.put("paintingType", paintingType);

        return jsonObject;
    }

    public static MetalFootingItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        double length = ((Double) jsonObject.get("length")).doubleValue();
        int paintingType = ((Long) jsonObject.get("paintingType")).intValue();


        MetalFootingItem metalFootingItem = new MetalFootingItem(length, paintingType, quantity);
        //metalFootingItem.quantity = quantity;
        metalFootingItem.labelQuantity.setText("" + quantity);
        metalFootingItem.updateRowPrice();
        return metalFootingItem;
    }
}
