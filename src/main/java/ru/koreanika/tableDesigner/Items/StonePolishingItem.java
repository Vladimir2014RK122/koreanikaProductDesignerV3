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
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.currency.Currency;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class StonePolishingItem extends TableDesignerItem implements DependOnMaterial {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalWorkItemsList();


    Label labelRowNumber, labelName, labelMaterial, labelNull1, labelLength, labelWidth, labelQuantity, labelRowPrice;
    ImageView imageView;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;

    double length = 0;
    double width = 0;

    Image imageMain;

    public StonePolishingItem(Material material, double length, double width, int quantity) {

        this.material = material;
        this.length = length;
        this.width = width;
        this.quantity = quantity;

        //imageMain = new ImageView(ProjectHandler.class.getResource("/styles/images/no_img.png").toString()).getImage();
        imageMain = material.getImageView().getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/StonePolishingRow.fxml")
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

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(StonePolishingItem item) {

        StonePolishingItem oldStonePolishingItem = item;

        Material newMaterial = null;
        Material defaultMaterial = ProjectHandler.getDefaultMaterial();

        if (ProjectHandler.getMaterialsListInProject().contains(item.getMaterial())) {
            newMaterial = oldStonePolishingItem.material;
        } else {

            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType())) {
                newMaterial = ProjectHandler.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : ProjectHandler.getMaterialsListInProject()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType())) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldStonePolishingItem.removeThisItem();
                    return;
                }
            }

        }

        StonePolishingItem newStonePolishingItem = new StonePolishingItem(newMaterial, oldStonePolishingItem.length, oldStonePolishingItem.width, oldStonePolishingItem.quantity);

        oldStonePolishingItem.removeThisItem();
        tableDesignerItemsList.add(newStonePolishingItem);

    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
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
            StonePolishingItem.exitFromEditMode(this);
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
        labelNull1 = (Label) hBox.getChildren().get(4);
        labelLength = (Label) hBox.getChildren().get(5);
        labelWidth = (Label) hBox.getChildren().get(6);
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
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelWidth, Priority.ALWAYS);
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
        labelName.setText("Полировка камня");
        imageView.setImage(imageMain);
        labelMaterial.setText(material.getReceiptName());
        labelNull1.setText("");
        labelLength.setText("" + length + "мм");
        labelWidth.setText("" + width + "мм");
        labelQuantity.setText("" + quantity);

        //card view:
        labelHeaderCard.setText("Полировка камня");
        tooltipNameCard.setText("Полировка камня");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);

        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Длина");
        labelValue2Card.setText("" + (int)length + "мм");

        labelName3Card.setText("Ширина");
        labelValue3Card.setText("" + (int)width + "мм");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getStonePolishingCurrency();
        String units = "м^2";
        double priceForOne = -1.0;


        priceForOne = material.getStonePolishingPrice();

        priceForOne /= 100.0;

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;
        priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000.0) * (width/1000.0)) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000.0) * (width/1000.0)) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000.0) * (width/1000.0)) + Currency.RUR_SYMBOL);

    }


    /**
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK"), btnCancel = new Button("Отмена");

    private static ChoiceBox<String> choiceBoxMaterial;
    private static TextField textFieldLength, textFieldWidth;
    private static Label labelPrice;

    private static boolean lengthOk = true, widthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StoneProductItem.class.getResource("/fxmls/TableDesigner/TableItems/StonePolishing_settings.fxml"));

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

        textFieldLength = (TextField) anchorPaneSettingsView.lookup("#textFieldLength");
        textFieldWidth = (TextField) anchorPaneSettingsView.lookup("#textFieldWidth");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getReceiptName());

        textFieldLength.setText("600");
        textFieldWidth.setText("600");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));

        choiceBoxMaterial.setOnAction(event -> {

            if(choiceBoxMaterial.getSelectionModel().getSelectedItem() != null){
                Material material = null;
                for (Material m : ProjectHandler.getMaterialsListInProject()) {
                    if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                        material = m;
                    }
                }
                btnAdd.setDisable(material.getName().contains("Массив_шпон"));
            }

            updatePriceInSettings();
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

        textFieldWidth.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldWidth.setStyle("-fx-text-fill:red");
                widthOk = false;
                return;
            }
            textFieldWidth.setStyle("-fx-text-fill:#A8A8A8");
            widthOk = true;
        });

    }

    private static void addItem(int index, int quantity){

        if (!(lengthOk && widthOk)) return;

        Material material = null;
        for (Material m : ProjectHandler.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }


        double length = 0;
        double width = 0;


        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            length = 0;
            return;
        }

        try {
            width = Double.parseDouble(textFieldWidth.getText());
        } catch (NumberFormatException ex) {
            width = 0;
            return;
        }


        if (length == 0 || width == 0) return;
        tableDesignerItemsList.add(index, new StonePolishingItem(material, length, width, quantity));
    }
    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getReceiptName());

        btnAdd.setDisable(ProjectHandler.getDefaultMaterial().getName().contains("Массив_шпон"));

        textFieldLength.setText("600");
        textFieldWidth.setText("600");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getStonePolishingCurrency();
                String units = "м^2";
                double priceForOne = -1.0;


                priceForOne = material.getStonePolishingPrice();

                priceForOne /= 100.0;

                priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(StonePolishingItem stonePolishingItem){
        TableDesigner.openSettings(StonePolishingItem.class);

        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(stonePolishingItem.material.getReceiptName());
        textFieldLength.setText("" + stonePolishingItem.length);
        textFieldWidth.setText("" + stonePolishingItem.width);

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

            int index = getTableDesignerItemsList().indexOf(stonePolishingItem);
            addItem(index, stonePolishingItem.quantity);

            exitFromEditMode(stonePolishingItem);
            stonePolishingItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(stonePolishingItem);

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
        jsonObject.put("itemName", "StonePolishingItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("length", length);
        jsonObject.put("width", width);

        return jsonObject;
    }

    public static StonePolishingItem initFromJSON(JSONObject jsonObject) {

        String materialName = (String) jsonObject.get("material");

        Material material = null;
        for (Material m : ProjectHandler.getMaterialsListInProject()) {
            if (materialName.equals(m.getName())) {
                material = m;
                break;
            }
        }
        if (material == null) return null;

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        double length = ((Double) jsonObject.get("length")).doubleValue();
        double width = ((Double) jsonObject.get("width")).doubleValue();

        StonePolishingItem stonePolishingItem = new StonePolishingItem(material, length, width, quantity);
        stonePolishingItem.quantity = quantity;
        stonePolishingItem.labelQuantity.setText("" + quantity);
        stonePolishingItem.updateRowPrice();
        return stonePolishingItem;
    }

}

