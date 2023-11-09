package ru.koreanika.tableDesigner.Items;

import ru.koreanika.common.material.Material;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.*;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RadiatorGroovesItem extends TableDesignerItem implements DependOnMaterial {

    Label labelRowNumber, labelName, labelMaterial, labelType, labelModel, labelNull1, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int type = 7;
    String model;
    Image imageMain;

    public RadiatorGroovesItem(Material material, String model, int quantity) {

        this.material = material;
        //this.type = type;
        this.model = model;
        this.quantity = quantity;


        File file = new File("features_resources/cutout/icons/" + "cutout_" + type + "_icon.png");
        try {
            imageMain = new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/RadiatorGroovesRow.fxml")
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

    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(RadiatorGroovesItem item) {

        RadiatorGroovesItem oldRadiatorGroovesItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldRadiatorGroovesItem.material;
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
                    oldRadiatorGroovesItem.removeThisItem();
                    return;
                }
            }

        }

        RadiatorGroovesItem newRadiatorGroovesItem = new RadiatorGroovesItem(newMaterial, oldRadiatorGroovesItem.model, oldRadiatorGroovesItem.quantity);

        oldRadiatorGroovesItem.removeThisItem();
        TableDesignerSession.getTableDesignerAdditionalItemsList().add(newRadiatorGroovesItem);

    }

    public int getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        String imgPath = "features_resources/cutout/icons/" + "cutout_" + type + "_icon.png";
        File file = new File(imgPath);
        try {
            imagesList.put("Проточки под радиатор#" + imgPath, new ImageView(new Image(file.toURI().toURL().toString())));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerAdditionalItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            RadiatorGroovesItem.exitFromEditMode(this);
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

        labelRowNumber.setText("");
        labelName.setText("Проточки под радиатор");
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);

        labelMaterial.setText(material.getReceiptName());
        labelType.setText("#" + type);
        labelModel.setText(model);

        labelQuantity.setText("" + quantity);

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

        TableDesignerSession.getTableDesignerAdditionalItemsList().remove(this);
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
        labelName.setText("Проточки под радиатор");
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);


        labelMaterial.setText(material.getReceiptName());
        labelType.setText("#" + type);
        labelModel.setText(model);

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Проточки под радиатор");
        tooltipNameCard.setText("Проточки под радиатор");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText("#" + type);

        labelName3Card.setText("Модель");
        labelValue3Card.setText(model);

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getCutoutCurrency();
        String units = "шт.";
        double priceForOne = -1.0;

        int type = 7;


        priceForOne = material.getCutoutTypesAndPrices().get(type).doubleValue();

        priceForOne /= 100.0;

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;
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
    private static ComboBox<CutoutType> comboBoxType;
    private static ChoiceBox<String> choiceBoxModel;
    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/RadiatorGroovesSettings.fxml"));

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
        comboBoxType = (ComboBox<CutoutType>) anchorPaneSettingsView.lookup("#comboBoxType");
        choiceBoxModel = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxModel");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());


        comboBoxType.setCellFactory(new Callback<ListView<CutoutType>, ListCell<CutoutType>>() {
            @Override
            public ListCell<CutoutType> call(ListView<CutoutType> param) {

                ListCell<CutoutType> cell = new ListCell<CutoutType>() {
                    @Override
                    protected void updateItem(CutoutType item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item.getImage());
                            setTooltip(item.getTooltip());
                        }
                    }
                };
                return cell;
            }
        });
        comboBoxType.setButtonCell(new ListCell<CutoutType>() {
            @Override
            protected void updateItem(CutoutType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item.getImageCopy());
                    setTooltip(item.getTooltipCopy());
                }
            }
        });

        comboBoxType.getItems().add(new CutoutType(7));
        comboBoxType.getSelectionModel().select(0);
        comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());

        choiceBoxModel.getItems().addAll(Cutout.getAvailableModels(7));//7 - cutout type for radiator
        choiceBoxModel.getSelectionModel().select(0);

    }

    private static void settingsControlElementsLogicInit() {
        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerAdditionalItemsList().size(), 1));
        choiceBoxMaterial.setOnAction(event -> updatePriceInSettings());
    }

    private static void addItem(int index, int quantity){
        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        String model = choiceBoxModel.getSelectionModel().getSelectedItem();
        TableDesignerSession.getTableDesignerAdditionalItemsList().add(index, new RadiatorGroovesItem(material, model, quantity));
    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxModel.getItems().clear();
        choiceBoxModel.getItems().addAll(Cutout.getAvailableModels(7));
        choiceBoxModel.getSelectionModel().select(0);

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {
        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getCutoutCurrency();
                String units = "шт.";
                double priceForOne = -1.0;

                int type = 7;


                priceForOne = material.getCutoutTypesAndPrices().get(type).doubleValue();

                priceForOne /= 100.0;

                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(RadiatorGroovesItem radiatorGroovesItem){
        TableDesigner.openSettings(RadiatorGroovesItem.class);

        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(radiatorGroovesItem.material.getReceiptName());
        choiceBoxModel.getSelectionModel().select(radiatorGroovesItem.model);

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

            int index = TableDesignerSession.getTableDesignerAdditionalItemsList().indexOf(radiatorGroovesItem);
            addItem(index, radiatorGroovesItem.quantity);

            exitFromEditMode(radiatorGroovesItem);
            radiatorGroovesItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(radiatorGroovesItem);

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
        jsonObject.put("itemName", "RadiatorGroovesItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("type", type);
        jsonObject.put("model", model);


        return jsonObject;
    }

    public static RadiatorGroovesItem initFromJSON(JSONObject jsonObject) {

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


        int type = ((Long) jsonObject.get("type")).intValue();
        String model = (String) jsonObject.get("model");

        RadiatorGroovesItem radiatorGroovesItem = new RadiatorGroovesItem(material, model, quantity);
        //radiatorGroovesItem.quantity = quantity;
        radiatorGroovesItem.labelQuantity.setText("" + quantity);
        radiatorGroovesItem.updateRowPrice();
        return radiatorGroovesItem;
    }
}

