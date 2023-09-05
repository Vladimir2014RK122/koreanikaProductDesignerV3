package ru.koreanika.tableDesigner.Items;

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
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SiphonItem extends TableDesignerItem{

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalItemsList();


    // row controls:
    Label labelRowNumber, labelName, labelNull1, labelNull2, labelNull3, labelNull4, labelQuantity, labelRowPrice;
    ImageView imageView;
    Button btnPlus, btnMinus, btnDelete, btnEdit;


    int type = 0;
    Image imageMain;

    public SiphonItem(int type, int quantity) {

        this.type = type;
        this.quantity = quantity;

        imageMain = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SiphonItem/siphonType" + type + ".png").toString()).getImage();

        FXMLLoader fxmlLoaderRow = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/SiphonRow.fxml")
        );

        try {
            anchorPaneTableRow = fxmlLoaderRow.load();
//            anchorPaneCardView = fxmlLoaderCard.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rowControlElementsInit();
        rowControlElementLogicInit();

        cardControlElementsInit();
        cardControlElementLogicInit();

        updateItemView();

    }

    public int getType() {
        return type;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/SiphonItem/siphonType" + type + ".png";
        imagesList.put("Сифон#" + imgPath, new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SiphonItem/siphonType" + type + ".png").toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            SiphonItem.exitFromEditMode(this);
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
        labelNull1 = (Label) hBox.getChildren().get(3);
        labelNull2 = (Label) hBox.getChildren().get(4);
        labelNull3 = (Label) hBox.getChildren().get(5);
        labelNull4 = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");

        labelRowNumber.setText("");
        labelName.setText("Сифон" + ((type == 1) ? " с одним выпуском" : " с двумя выпусками"));
        imageView.setImage(imageMain);
        labelNull1.setText("");
        labelNull2.setText("");
        labelNull3.setText("");
        labelNull4.setText("");
        labelQuantity.setText("" + quantity);

        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelNull2, Priority.ALWAYS);
        HBox.setHgrow(labelNull3, Priority.ALWAYS);
        HBox.setHgrow(labelNull4, Priority.ALWAYS);
        HBox.setHgrow(labelQuantity, Priority.ALWAYS);
        HBox.setHgrow(labelRowPrice, Priority.ALWAYS);

    }

    private void rowControlElementLogicInit() {

        btnPlus.setOnAction(event -> {
            btnPlusClicked(event);
        });

        btnMinus.setOnAction(event -> {
            btnMinusClicked(event);
        });

        btnDelete.setOnAction(event -> {
            btnDeleteClicked(event);
        });

        btnEdit.setOnAction(event -> {
            btnEditClicked(event);
        });
    }

    private void cardControlElementLogicInit() {

        btnPlusCard.setOnAction(event -> {
            btnPlusClicked(event);
        });

        btnMinusCard.setOnAction(event -> {
            btnMinusClicked(event);
        });

        btnDeleteCard.setOnAction(event -> {
            btnDeleteClicked(event);
        });

        btnEditCard.setOnAction(event -> {
            btnEditClicked(event);
        });
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
        labelName.setText("Сифон" + ((type == 1) ? " с одним выпуском" : " с двумя выпусками"));
        imageView.setImage(imageMain);
        labelNull1.setText("");
        labelNull2.setText("");
        labelNull3.setText("");
        labelNull4.setText("");
        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Сифон");
        tooltipNameCard.setText("Сифон");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Тип");
        labelValue1Card.setText(((type == 1) ? " C одним выпуском" : " C двумя выпусками"));

        labelName2Card.setText("Толщина материала");
        labelValue2Card.setText("-");

        labelName3Card.setText("Ширина");
        labelValue3Card.setText("-");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }


    @Override
    public void updateRowPrice() {

        double priceForOne = -1.0;
        if(ProjectHandler.getDefaultMaterial() == null) return;
        double priceForType1 = ProjectHandler.getDefaultMaterial().getSiphonsTypesAndPrices().get(0);
        double priceForType2 = ProjectHandler.getDefaultMaterial().getSiphonsTypesAndPrices().get(1);
        priceForOne = (type == 1) ? priceForType1/100 : priceForType2/100;

        priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + ReceiptManager.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);
    }

    /**
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK"), btnCancel = new Button("Отмена");


    private static ToggleButton toggleButtonSiphonType1, toggleButtonSiphonType2;
    private static ToggleGroup toggleGroupSiphonType = new ToggleGroup();

    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StoneProductItem.class.getResource("/fxmls/TableDesigner/TableItems/SiphonSettings.fxml"));

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

        //choiceBoxType = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxType");
        toggleButtonSiphonType1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonSiphonType1");
        toggleButtonSiphonType2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonSiphonType2");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        toggleButtonSiphonType1.setToggleGroup(toggleGroupSiphonType);
        toggleButtonSiphonType2.setToggleGroup(toggleGroupSiphonType);
        toggleButtonSiphonType1.setSelected(true);

//        ImageView image1 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SiphonItem/siphonType1.png").toString());
//        image1.setFitWidth(45);
//        image1.setFitHeight(45);
//        toggleButtonSiphonType1.setGraphic(image1);
//
//        ImageView image2 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/SiphonItem/siphonType2.png").toString());
//        image2.setFitWidth(45);
//        image2.setFitHeight(45);
//        toggleButtonSiphonType2.setGraphic(image2);


    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> {

            addItem(getTableDesignerItemsList().size(), 1);
        });

        toggleGroupSiphonType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){

        int type = (toggleGroupSiphonType.getSelectedToggle() == toggleButtonSiphonType1)? 1 : 2;

        tableDesignerItemsList.add(index, new SiphonItem(type, quantity));
    }

    public static void settingsControlElementsRefresh() {
        updatePriceInSettings();

    }

    public static void updatePriceInSettings() {

        if(ProjectHandler.getDefaultMaterial() == null) return;

        String currency = ProjectHandler.getDefaultMaterial().getSiphonsCurrency();
        String units = "шт";
        double priceForOne = -1.0;
        double priceForType1 = ProjectHandler.getDefaultMaterial().getSiphonsTypesAndPrices().get(0);
        double priceForType2 = ProjectHandler.getDefaultMaterial().getSiphonsTypesAndPrices().get(1);
        priceForOne = (toggleGroupSiphonType.getSelectedToggle() == toggleButtonSiphonType1) ? priceForType1/100 : priceForType2/100;

        priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));

    }

    private static void enterToEditMode(SiphonItem siphonItem){
        TableDesigner.openSettings(SiphonItem.class);


        //get row data to settings
        if(siphonItem.type == 1){
            toggleButtonSiphonType1.setSelected(true);
        }else{
            toggleButtonSiphonType2.setSelected(true);
        }

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

            int index = getTableDesignerItemsList().indexOf(siphonItem);
            addItem(index, siphonItem.quantity);

            exitFromEditMode(siphonItem);
            siphonItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(siphonItem);

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
        jsonObject.put("itemName", "SiphonItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("type", type);

        return jsonObject;
    }

    public static SiphonItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        int type = ((Long) jsonObject.get("type")).intValue();

        SiphonItem siphonItem = new SiphonItem(type, quantity);
        siphonItem.labelQuantity.setText("" + quantity);
        siphonItem.updateRowPrice();
        return siphonItem;
    }
}
