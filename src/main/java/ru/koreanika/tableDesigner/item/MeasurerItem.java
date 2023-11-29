package ru.koreanika.tableDesigner.item;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MeasurerItem extends TableDesignerItem implements DependOnMaterial {

    public static final String NAME_ROW = "Выезд замерщика";
    public static final String NAME_RECEIPT_IMAGE = "Замер";
    public static final String NAME_RECEIPT_ROW = "Замер";
    public static final String NAME_RECEIPT_DISTANCE = "Удаленность";

    Label labelRowNumber, labelName, labelMaterial, labelNull2, labelLength, labelNull3, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;

    double length;
    Image imageMain;


    public MeasurerItem(Material material, int quantity, double length) {

        this.material = material;

        this.length = length;

        this.quantity = quantity;

        imageMain = new ImageView(Project.class.getResource("/styles/images/TableDesigner/MeasurerItem/measurer.png").toString()).getImage();


        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/MeasurerRow.fxml")
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

    public String getReceiptName(){
        String receiptName = NAME_RECEIPT_ROW;

        if(length > 0.0){
            receiptName += "/" + NAME_RECEIPT_DISTANCE;
        }

        return receiptName;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/MeasurerItem/measurer.png";
        imagesList.put(NAME_RECEIPT_IMAGE +"#" + imgPath, new ImageView(getClass().getResource(imgPath).toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            MeasurerItem.exitFromEditMode(this);
        }
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
        labelMaterial = (Label) hBox.getChildren().get(3);
        labelNull2 = (Label) hBox.getChildren().get(4);
        labelLength = (Label) hBox.getChildren().get(5);
        labelNull3 = (Label) hBox.getChildren().get(6);
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
        HBox.setHgrow(labelNull2, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelNull3, Priority.ALWAYS);
        HBox.setHgrow(labelQuantity, Priority.ALWAYS);
        HBox.setHgrow(labelRowPrice, Priority.ALWAYS);
    }

    private void rowControlElementLogicInit() {
        btnPlus.setOnAction(this::btnPlusClicked);
        btnMinus.setOnAction(this::btnMinusClicked);
        btnDelete.setOnAction(this::btnDeleteClicked);
        btnEdit.setOnAction(this::btnEditClicked);
    }

    private void cardControlElementLogicInit() {
        btnPlusCard.setOnAction(this::btnPlusClicked);
        btnMinusCard.setOnAction(this::btnMinusClicked);
        btnDeleteCard.setOnAction(this::btnDeleteClicked);
        btnEditCard.setOnAction(this::btnEditClicked);
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

        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
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
        labelName.setText(NAME_ROW);
        imageViewMain.setImage(imageMain);

        labelMaterial.setText(material.getReceiptName());
        labelNull2.setText("");
        labelLength.setText("" + length + " км");
        labelNull3.setText("");

        labelQuantity.setText("" + quantity);

        labelHeaderCard.setText(NAME_ROW);
        tooltipNameCard.setText(NAME_ROW);
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);

        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText("-");

        labelName3Card.setText("Удаленность");
        labelValue3Card.setText("" + length + " км");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {
        double priceForOne = (material.getMeasurerPrice() + material.getMeasurerKMPrice() * length);
        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + Currency.RUR_SYMBOL);
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
    private static TextField textFieldCount, textFieldLength;
    private static Label labelPrice;

    private static boolean countOk = true, lengthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/MeasurerSettings.fxml"));

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

        textFieldCount = (TextField) anchorPaneSettingsView.lookup("#textFieldCount");
        textFieldLength = (TextField) anchorPaneSettingsView.lookup("#textFieldLength");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        textFieldCount.setText("1");
        textFieldLength.setText("0");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerMainWorkItemsList().size(), 1));

        choiceBoxMaterial.setOnAction(event -> {
            updatePriceInSettings();
        });

        textFieldCount.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException ex) {
                textFieldCount.setStyle("-fx-text-fill:red");
                countOk = false;
                return;
            }
            textFieldCount.setStyle("-fx-text-fill:#A8A8A8");
            countOk = true;
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
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){
        if (!(lengthOk && countOk)) return;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        try {
            quantity = Integer.parseInt(textFieldCount.getText());
        } catch (NumberFormatException ex) {
            return;
        }
        if (quantity == 0) return;

        double length = 0;
        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        TableDesignerSession.getTableDesignerMainWorkItemsList().add(index, new MeasurerItem(material, quantity, length));

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        textFieldCount.setText("1");
        textFieldLength.setText("0");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (!(countOk & lengthOk)) return;
        String currency = "RUB";
        String units = "выезд";
        double priceForOne = -1.0;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        if (material == null) return;


        double len = Double.parseDouble(textFieldLength.getText());
        int quantity = Integer.parseInt(textFieldCount.getText());
        double KMPriceForOne = material.getMeasurerKMPrice();

        priceForOne = (material.getMeasurerPrice() + len * KMPriceForOne) * quantity;

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();
        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));


    }

    private static void enterToEditMode(MeasurerItem measurerItem){
        TableDesigner.openSettings(MeasurerItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(measurerItem.material.getReceiptName());
        textFieldCount.setText("" + measurerItem.quantity);
        textFieldLength.setText("" + measurerItem.length);

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

            int index = TableDesignerSession.getTableDesignerMainWorkItemsList().indexOf(measurerItem);
            addItem(index, measurerItem.quantity);

            exitFromEditMode(measurerItem);
            measurerItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(measurerItem);

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
        jsonObject.put("itemName", "MeasurerItem");
        jsonObject.put("quantity", quantity);
        jsonObject.put("material", material.getName());

        jsonObject.put("length", length);


        return jsonObject;
    }

    public static MeasurerItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        double length = ((Double) jsonObject.get("length")).doubleValue();

        String materialName = (String) jsonObject.get("material");
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {

                MeasurerItem measurerItem = new MeasurerItem(m, quantity, length);
                //measurerItem.quantity = quantity;
                measurerItem.labelQuantity.setText("" + quantity);
                measurerItem.updateRowPrice();
                return measurerItem;

            }
        }
        return null;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(MeasurerItem item) {

        MeasurerItem oldMeasurerItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldMeasurerItem.material;
        } else {

            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType())) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterials()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType())) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldMeasurerItem.removeThisItem();
                    return;
                }
            }
        }


        MeasurerItem newMeasurerItem = new MeasurerItem(newMaterial, oldMeasurerItem.quantity, oldMeasurerItem.length);

        oldMeasurerItem.removeThisItem();
        TableDesignerSession.getTableDesignerMainWorkItemsList().add(newMeasurerItem);

    }
}