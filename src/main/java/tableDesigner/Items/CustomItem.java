package tableDesigner.Items;

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
import tableDesigner.TableDesigner;
import utils.InfoMessage;
import utils.ProjectHandler;
import utils.Receipt.CustomReceiptItems.Mounting;
import utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CustomItem extends TableDesignerItem {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerMainWorkItemsList();


    Label labelRowNumber, labelName, labelNull1, labelNull2, labelNull3, labelNull4, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    String name;
    double price;

    String units;


    public CustomItem(String name, double price, int quantity, String units) {

        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.units = units;



        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/CustomRow.fxml")
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


    public String getName() {
        return name;
    }



    public double getPrice() {
        return price;
    }

    public String getUnits() {
        return units;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            CustomItem.exitFromEditMode(this);
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
        labelName.setText(name);
//        imageViewMain.setImage(imageMain);

        labelNull1.setText("");
        labelNull2.setText("");
        labelNull3.setText(String.format(Locale.ENGLISH, "%.0f₽", price));
        labelNull4.setText(units);

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText(name);
        tooltipNameCard.setText(name);
        imageViewBackCard.setImage(new Image(getClass().getResource("/styles/images/no_img.png").toString()));
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Единицы измерения");
        labelValue1Card.setText(units);

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

        double priceForOne = price;

        //priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();
        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + ReceiptManager.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);
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

    private static TextField textFieldName, textFieldPrice, textFieldCount, textFieldUnits;
    private static Label labelPrice;

    private static boolean priceOk = true, countOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/CustomSettings.fxml"));

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

        textFieldName = (TextField) anchorPaneSettingsView.lookup("#textFieldName");
        textFieldPrice = (TextField) anchorPaneSettingsView.lookup("#textFieldPrice");
        textFieldCount = (TextField) anchorPaneSettingsView.lookup("#textFieldCount");
        textFieldUnits = (TextField) anchorPaneSettingsView.lookup("#textFieldUnits");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        textFieldName.setText("Дополнительно");
        textFieldPrice.setText("1");
        textFieldCount.setText("1");
        textFieldUnits.setText("шт");

        labelPrice.setText("Цена: по счету");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));

        textFieldPrice.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldPrice.setStyle("-fx-text-fill:red");
                priceOk = false;
                return;
            }
            textFieldPrice.setStyle("-fx-text-fill:#A8A8A8");
            priceOk = true;

            labelPrice.setText("Цена: " + textFieldPrice.getText() + "₽");


        });

        textFieldCount.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldCount.setStyle("-fx-text-fill:red");
                priceOk = false;
                return;
            }
            textFieldCount.setStyle("-fx-text-fill:#A8A8A8");
            priceOk = true;
        });

    }

    private static void addItem(int index, int quantity){
        if (!(priceOk && countOk)) return;
//            for(TableDesignerItem item : MountingItem.getTableDesignerItemsList()){
//                if(item instanceof MountingItem){
//                    InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Не более одного элемента этого типа!");
//                    return;
//                }
//            }

        String name = textFieldName.getText();
        double price;

        String units = textFieldUnits.getText();

        try {
            price = Double.parseDouble(textFieldPrice.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        quantity = (int)Double.parseDouble(textFieldCount.getText());

        tableDesignerItemsList.add(index, new CustomItem(name, price, quantity, units));

    }

    public static void settingsControlElementsRefresh() {

        textFieldName.setText("Дополнительно");
        textFieldPrice.setText("1");
        textFieldCount.setText("1");
        textFieldUnits.setText("шт");

        labelPrice.setText("Цена: " + textFieldPrice.getText() + "₽");

    }

    private static void enterToEditMode(CustomItem customItem){
        TableDesigner.openSettings(CustomItem.class);


        //get row data to settings

        textFieldName.setText(customItem.name);
        textFieldUnits.setText(customItem.units);
        textFieldPrice.setText("" + customItem.price);
        textFieldCount.setText("" + customItem.quantity);

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

            int index = getTableDesignerItemsList().indexOf(customItem);
            addItem(index, customItem.quantity);

            exitFromEditMode(customItem);
            customItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(customItem);

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
        jsonObject.put("itemName", "CustomItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("name", name);
        jsonObject.put("price", price);

        jsonObject.put("units", units);


        return jsonObject;
    }

    public static CustomItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        String name = (String) jsonObject.get("name");
        double price = ((Double) jsonObject.get("price")).doubleValue();

        String units = (String) jsonObject.get("units");

        CustomItem customItem = new CustomItem(name, price, quantity, units);
        //customItem.quantity = quantity;
        customItem.labelQuantity.setText("" + quantity);
        customItem.updateRowPrice();


        return customItem;
    }
}
