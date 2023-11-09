package ru.koreanika.tableDesigner.Items;

import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class DeliveryItem extends TableDesignerItem implements DependOnMaterial {

    public static final String NAME_ROW = "Доставка";
    public static final String NAME_RECEIPT_IMAGE = "Доставка";
    public static final String NAME_RECEIPT_ROW = "Доставка";
    public static final String NAME_RECEIPT_DISTANCE = "Удаленность";
    public static final String NAME_RECEIPT_LIFTING = "Разгрузка";
    public static final String NAME_RECEIPT_HAND_CARRY = "Ручной пронос";
    public static final double HAND_CARRY_COEFFICIENT = 1.1;

    private static TextField textFieldHandCarry;
    private static CheckBox checkBoxHandCarry;


    Label labelRowNumber, labelName, labelMaterial, labelLifting, labelLength, labelNull2, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;

    int insideMKADCount;
    int lengthOutsideMKAD;
    int priceForUnbox;
    int handCarryPrice;

    boolean userLifting = false;
    Image imageMain;

    double priceForOne = 0;


    public DeliveryItem(Material material, int quantity, int insideMKADCount, int lengthOutsideMKAD, int priceForUnbox, int handCarryPrice) {

        this.material = material;
        this.quantity = quantity;
        this.insideMKADCount = insideMKADCount;
        this.lengthOutsideMKAD = lengthOutsideMKAD;
        this.priceForUnbox = priceForUnbox;
        this.handCarryPrice = handCarryPrice;


        imageMain = new ImageView(Project.class.getResource("/styles/images/TableDesigner/DeliveryItem/delivery.png").toString()).getImage();


        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/DeliveryRow.fxml")
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

    public String getReceiptName(){
        String receiptName = NAME_RECEIPT_ROW;

        if(lengthOutsideMKAD > 0.0){
            receiptName += "/" + NAME_RECEIPT_DISTANCE;
        }

        if(priceForUnbox > 0){
            receiptName += "/" + NAME_RECEIPT_LIFTING;
        }

        return receiptName;
    }


    public double getLength() {
        return lengthOutsideMKAD;
    }

    public int getPriceForUnbox() {
        return priceForUnbox;
    }

    public int getHandCarryPrice() {
        return handCarryPrice;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/DeliveryItem/delivery.png";
        imagesList.put(NAME_RECEIPT_IMAGE + "#" + imgPath, new ImageView(getClass().getResource(imgPath).toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            DeliveryItem.exitFromEditMode(this);
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
        labelLifting = (Label) hBox.getChildren().get(4);
        labelLength = (Label) hBox.getChildren().get(5);
        labelNull2 = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");

        labelRowNumber.setText("");
        labelName.setText(NAME_ROW);
        imageViewMain.setImage(imageMain);

        labelMaterial.setText(material.getReceiptName());
        labelLifting.setText("" + priceForUnbox);
        labelLength.setText("" + lengthOutsideMKAD + " км");
        labelNull2.setText("");

        labelQuantity.setText("" + quantity);

        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelMaterial, Priority.ALWAYS);
        HBox.setHgrow(labelLifting, Priority.ALWAYS);
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
        labelName.setText(NAME_ROW);
        imageViewMain.setImage(imageMain);

        labelMaterial.setText(material.getReceiptName());
        labelLifting.setText("" + priceForUnbox);
        labelLength.setText("" + lengthOutsideMKAD + " км");
        labelNull2.setText("");

        labelQuantity.setText("" + quantity);

        labelHeaderCard.setText(NAME_ROW);
        tooltipNameCard.setText(NAME_ROW);
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);

        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Стоимость разгрузки");
        labelValue2Card.setText(priceForUnbox + " руб.");

        labelName3Card.setText("Стоимость проноса");
        labelValue3Card.setText(handCarryPrice + " руб.");

        labelName4Card.setText("Расстояние");
        labelValue4Card.setText(lengthOutsideMKAD + " км");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {
        priceForOne = 0.0;
        double priceForDelivery = material.getDeliveryInsideMKADPrice();

        double priceForDeliveryKM = material.getMeasurerKMPrice();

        priceForOne += insideMKADCount * priceForDelivery + priceForDeliveryKM * lengthOutsideMKAD + priceForUnbox + handCarryPrice;

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", (priceForOne * Project.getPriceMainCoefficient().doubleValue() * quantity)) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", (priceForOne * Project.getPriceMainCoefficient().doubleValue())) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", (priceForOne * Project.getPriceMainCoefficient().doubleValue() * quantity)) + Currency.RUR_SYMBOL);
    }

    public double getPriceForOne() {
        return priceForOne;
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
    private static Button btnApply = new Button("OK");
    private static Button btnCancel = new Button("Отмена");

    private static TextField textFieldCount, textFieldLength, textFieldLifting;
    private static ChoiceBox<String> choiceBoxMaterial;
    private static Label labelPrice;

    private static boolean countOk = true, lengthOk = true, liftingOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/DeliverySettings.fxml"));

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
        textFieldLifting = (TextField) anchorPaneSettingsView.lookup("#textFieldLifting");

        checkBoxHandCarry = (CheckBox) anchorPaneSettingsView.lookup("#checkBoxHandCarry");
        checkBoxHandCarry.setSelected(false);
        textFieldHandCarry = (TextField) anchorPaneSettingsView.lookup("#textFieldHandCarry");
        textFieldHandCarry.setDisable(true);

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        textFieldCount.setText("1");
        textFieldLength.setText("0");
        textFieldLength.setText("0");
        textFieldHandCarry.setText("0");
    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> {
            addItem(TableDesignerSession.getTableDesignerMainWorkItemsList().size(), 1);
        });

        choiceBoxMaterial.setOnAction(event -> {
            updatePriceInSettings();
        });

        textFieldCount.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Double.parseDouble(newValue);
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

        textFieldLifting.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldLifting.setStyle("-fx-text-fill:red");
                liftingOk = false;
                return;
            }
            textFieldLifting.setStyle("-fx-text-fill:#A8A8A8");
            liftingOk = true;

            updatePriceInSettings();
        });

        checkBoxHandCarry.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                textFieldHandCarry.setText("0");
            }
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){
        if (!(lengthOk && countOk && liftingOk)) return;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }


        int insideMKADCount = 0;
        try {
            insideMKADCount = Integer.parseInt(textFieldCount.getText());
        } catch (NumberFormatException ex) {
            return;
        }
        if (quantity == 0) return;

        int length = 0;
        try {
            length = Integer.parseInt(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        int lifting = 0;
        try {
            lifting = Integer.parseInt(textFieldLifting.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        int handCarry = 0;
        try {
            handCarry = Integer.parseInt(textFieldHandCarry.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        TableDesignerSession.getTableDesignerMainWorkItemsList().add(index, new DeliveryItem(material, quantity, insideMKADCount, length, lifting, handCarry));

    }

    public static void settingsControlElementsRefresh() {
        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        textFieldCount.setText("1");
        textFieldLength.setText("0");
        textFieldLifting.setText("0");
        checkBoxHandCarry.setSelected(false);
        textFieldHandCarry.setText("0");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (!(liftingOk & countOk & lengthOk)) return;

        String currency = "RUB";
        String units = "рейс";
        double priceForOne = 0.0;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        if (material == null) return;
        int priceForDelivery = material.getDeliveryInsideMKADPrice();
        int priceForDeliveryKM = material.getMeasurerKMPrice();

        int priceForUnBox = 0;

        for (TableDesignerItem tableDesignerItem : TableDesignerSession.getTableDesignerMainItemsList()) {
            if (tableDesignerItem instanceof StoneProductItem) {
                StoneProductItem stoneProductItem = (StoneProductItem) tableDesignerItem;

                for (ArrayList<SketchShape> listOfShapes : stoneProductItem.getSketchShapeArrayList()) {
                    for (SketchShape shape : listOfShapes) {
                        priceForUnBox += (stoneProductItem.getMaterial().getManualLiftingPrice() / 100) * (shape.getCutShape().getShapeSquare() / 10000);
                    }
                }
            }
        }

        if (textFieldLifting.getText().equals("") || textFieldLifting.getText().equals("0")) {
            textFieldLifting.setText("" + priceForUnBox);
        }

        // стоимость ручного проноса изделия = кол. кв. м. * стоимость ручного подъема (руб/м^2) * коэфф. ручного проноса
        int priceForHandCarry = 0;
        if (!checkBoxHandCarry.isSelected()) {
            textFieldHandCarry.setText("0");
        } else if (textFieldHandCarry.getText().isEmpty() || textFieldHandCarry.getText().equals("0")) {
            priceForHandCarry = (int) (priceForUnBox * HAND_CARRY_COEFFICIENT);
            textFieldHandCarry.setText(String.valueOf(priceForHandCarry));
        }

        if (textFieldCount.getText().equals("")) return;
        if (textFieldLifting.getText().equals("")) return;
        if (textFieldLength.getText().equals("")) return;
        int deliveryCount = (int) Double.parseDouble(textFieldCount.getText());
        int lengthCount = (int) Double.parseDouble(textFieldLength.getText());

        priceForUnBox = Integer.parseInt(textFieldLifting.getText());
        priceForHandCarry = Integer.parseInt(textFieldHandCarry.getText());

        priceForOne += deliveryCount * priceForDelivery + priceForDeliveryKM * lengthCount + priceForUnBox + priceForHandCarry;

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
    }

    private static void enterToEditMode(DeliveryItem deliveryItem){
        TableDesigner.openSettings(DeliveryItem.class);

        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(deliveryItem.material.getReceiptName());
        textFieldCount.setText(String.valueOf(deliveryItem.insideMKADCount));
        textFieldLength.setText(String.valueOf( deliveryItem.lengthOutsideMKAD));
        textFieldLifting.setText(String.valueOf(deliveryItem.priceForUnbox));

        if (deliveryItem.handCarryPrice != 0) {
            checkBoxHandCarry.setSelected(true);
            textFieldHandCarry.setText(String.valueOf(deliveryItem.handCarryPrice));
        } else {
            checkBoxHandCarry.setSelected(false);
            textFieldHandCarry.setText("0");
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
            int index = TableDesignerSession.getTableDesignerMainWorkItemsList().indexOf(deliveryItem);
            if(index == -1) index = TableDesignerSession.getTableDesignerMainWorkItemsList().size();
            addItem(index, deliveryItem.quantity);

            exitFromEditMode(deliveryItem);
            deliveryItem.removeThisItem();
        });

        btnCancel.setOnAction(event -> exitFromEditMode(deliveryItem));
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
        jsonObject.put("itemName", "DeliveryItem");
        jsonObject.put("quantity", quantity);
        jsonObject.put("material", material.getName());

        jsonObject.put("insideMKADCount", insideMKADCount);
        jsonObject.put("length", lengthOutsideMKAD);
        jsonObject.put("lifting", priceForUnbox);
        jsonObject.put("handCarryPrice", handCarryPrice);

        return jsonObject;
    }

    public static DeliveryItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();


        int length = 0;
        boolean doubleType = true;
        try{
            ((Double) jsonObject.get("length")).doubleValue();

        }catch(Exception e){
            doubleType = false;
        }

        if(doubleType){
            length = (int)((Double) jsonObject.get("length")).doubleValue();
        }else{
            length = ((Long) jsonObject.get("length")).intValue();
        }

        int lifting = ((Long) jsonObject.get("lifting")).intValue();

        int handCarryPrice = 0;
        if (jsonObject.get("handCarryPrice") != null) {
            handCarryPrice = ((Long) jsonObject.get("handCarryPrice")).intValue();
        }

        int insideMKADCount = 0;
        if (jsonObject.get("insideMKADCount") != null) {
            insideMKADCount = ((Long) jsonObject.get("insideMKADCount")).intValue();
        }

        String materialName = (String) jsonObject.get("material");
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {

                DeliveryItem deliveryItem = new DeliveryItem(m, quantity, insideMKADCount, length, lifting, handCarryPrice);
                deliveryItem.quantity = quantity;
                deliveryItem.labelQuantity.setText("" + quantity);
                deliveryItem.updateRowPrice();
                return deliveryItem;

            }
        }

        System.out.println("DeliveryItem.initFromJSON() return null");
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

    private static void updateMaterial(DeliveryItem item) {

        DeliveryItem oldDeliveryItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldDeliveryItem.material;
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
                    oldDeliveryItem.removeThisItem();
                    return;
                }
            }
        }


        DeliveryItem newDeliveryItem = new DeliveryItem(newMaterial, oldDeliveryItem.quantity,
                oldDeliveryItem.insideMKADCount, oldDeliveryItem.lengthOutsideMKAD, oldDeliveryItem.priceForUnbox,
                oldDeliveryItem.handCarryPrice);

        oldDeliveryItem.removeThisItem();
        TableDesignerSession.getTableDesignerMainWorkItemsList().add(newDeliveryItem);

    }
}
