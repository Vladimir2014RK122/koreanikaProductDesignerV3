package ru.koreanika.tableDesigner.item;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class LeakGrooveItem extends TableDesignerItem implements DependOnMaterial {

    Label labelRowNumber, labelName, labelMaterial, labelNull1, labelLength, labelNull2, labelQuantity, labelRowPrice;
    ImageView imageView;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    double length = 0;

    public LeakGrooveItem(Material material, double length, int quantity) {
        this.material = material;
        this.length = length;
        this.quantity = quantity;

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/TableItems/LeakGrooveRow.fxml"));
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

    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(LeakGrooveItem item) {
        LeakGrooveItem oldLeakGrooveItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldLeakGrooveItem.material;
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
                    oldLeakGrooveItem.removeThisItem();
                    return;
                }
            }

        }

        LeakGrooveItem newLeakGrooveItem = new LeakGrooveItem(newMaterial, oldLeakGrooveItem.length, oldLeakGrooveItem.quantity);

        oldLeakGrooveItem.removeThisItem();
        TableDesignerSession.getTableDesignerMainWorkItemsList().add(newLeakGrooveItem);
    }

    public double getLength() {
        return length;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            LeakGrooveItem.exitFromEditMode(this);
        }
    }

    /**
     * Table ROW part
     */

    @Override
    public void setRowNumber(int number) {
        labelRowNumber.setText("" + number);
    }

    @Override
    public AnchorPane getTableView() {
        return anchorPaneTableRow;
    }

    private void rowControlElementsInit() {
        HBox hBox = (HBox) anchorPaneTableRow.lookup("#hBox");
        labelRowNumber = (Label) hBox.getChildren().get(0);
        labelName = (Label) hBox.getChildren().get(1);
        AnchorPane anchorPaneImageView = (AnchorPane) hBox.getChildren().get(2);
        imageView = (ImageView) anchorPaneImageView.lookup("#imageView");
        labelMaterial = (Label) hBox.getChildren().get(3);
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
        HBox.setHgrow(labelMaterial, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelNull2, Priority.ALWAYS);
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
        labelName.setText("Каплесборник");
        //imageView.setImage(image);
        labelMaterial.setText(material.getReceiptName());
        labelNull1.setText("");
        labelLength.setText("" + length + "мм");
        labelNull2.setText("");
        labelQuantity.setText("" + quantity);

        labelHeaderCard.setText("Каплесборник");
        tooltipNameCard.setText("Каплесборник");
        imageViewBackCard.setImage(material.getImageView().getImage());
        labelQuantityCard.setText("" + quantity);

        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText("-");

        labelName3Card.setText("Длина");
        labelValue3Card.setText("" + (int)length + " мм");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {
        String currency = "";
        String units = "м.п.";
        double priceForOne = -1.0;

        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {
            currency = material.getStoneHemCurrency();
            priceForOne = material.getStoneHemPrice();
        } else {
            currency = material.getLeakGrooveCurrency();
            priceForOne = material.getLeakGroovePrice();
        }

        priceForOne /= 100.0;

        double multiplier = switch (currency) {
            case "USD" -> MainWindow.getUSDValue().get();
            case "EUR" -> MainWindow.getEURValue().get();
            case "RUB" -> 1;
            default -> 1;
        };

        priceForOne *= multiplier;
        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000)) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000)) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000)) + Currency.RUR_SYMBOL);

    }


    /**
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK"), btnCancel = new Button("Отмена");

    private static ChoiceBox<String> choiceBoxMaterial;
    private static TextField textFieldLength;
    private static Label labelPrice;

    private static boolean lengthOk = true, widthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StoneProductItem.class.getResource("/fxmls/TableDesigner/TableItems/LeakGrooveSettings.fxml"));

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

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        textFieldLength.setText("1000");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerMainWorkItemsList().size(), 1));

        choiceBoxMaterial.setOnAction(event -> {

            if(choiceBoxMaterial.getSelectionModel().getSelectedItem() != null){
                Material material = null;
                for (Material m : Project.getMaterials()) {
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

    }

    private static void addItem(int index, int quantity){

        if (!(lengthOk && widthOk)) return;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        double length = 0;

        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        if (length == 0) return;
        TableDesignerSession.getTableDesignerMainWorkItemsList().add(index, new LeakGrooveItem(material, length, quantity));
    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        textFieldLength.setText("1000");

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }
        if(material != null)btnAdd.setDisable(material.getName().contains("Массив_шпон"));

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {
        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = "";
                String units = "м.п.";
                double priceForOne = -1.0;

                if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {
                    currency = material.getStoneHemCurrency();
                    priceForOne = material.getStoneHemPrice();
                } else {
                    currency = material.getLeakGrooveCurrency();
                    priceForOne = material.getLeakGroovePrice();
                }

                priceForOne /= 100.0;
                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(LeakGrooveItem leakGrooveItem){
        TableDesigner.openSettings(LeakGrooveItem.class);

        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(leakGrooveItem.material.getReceiptName());
        textFieldLength.setText("" + leakGrooveItem.length);

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
            int index = TableDesignerSession.getTableDesignerMainWorkItemsList().indexOf(leakGrooveItem);
            addItem(index, leakGrooveItem.quantity);

            exitFromEditMode(leakGrooveItem);
            leakGrooveItem.removeThisItem();
        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(leakGrooveItem);
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
        jsonObject.put("itemName", "LeakGrooveItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("length", length);

        return jsonObject;
    }

    public static LeakGrooveItem initFromJSON(JSONObject jsonObject) {
        String materialName = (String) jsonObject.get("material");

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {
                material = m;
                break;
            }
        }
        if (material == null) return null;

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        double length = (Double) jsonObject.get("length");

        LeakGrooveItem leakGrooveItem = new LeakGrooveItem(material, length, quantity);
        leakGrooveItem.labelQuantity.setText("" + quantity);
        leakGrooveItem.updateRowPrice();
        return leakGrooveItem;
    }
}
