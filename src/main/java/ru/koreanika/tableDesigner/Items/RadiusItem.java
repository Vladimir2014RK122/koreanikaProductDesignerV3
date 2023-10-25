package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.Material.Material;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.*;

public class RadiusItem extends TableDesignerItem implements DependOnMaterial {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerMainWorkItemsList();



    Label labelRowNumber, labelName, labelMaterial, labelNull2, labelNull3, labelNull4, labelQuantity, labelRowPrice;
    ImageView imageView;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    Image imageMain;

    public RadiusItem(Material material, int quantity) {

        this.material = material;
        this.quantity = quantity;

        imageMain = material.getImageView().getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/RadiusRow.fxml")
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

    private static void updateMaterial(RadiusItem item) {

        RadiusItem oldRadiusItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldRadiusItem.material;
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
                    oldRadiusItem.removeThisItem();
                    return;
                }
            }

        }

        RadiusItem newRadiusItem = new RadiusItem(newMaterial, oldRadiusItem.quantity);

        oldRadiusItem.removeThisItem();
        if (oldRadiusItem.slave == false) tableDesignerItemsList.add(newRadiusItem);

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
            RadiusItem.exitFromEditMode(this);
        }
    }

    public static ObservableList<TableDesignerItem> getTableDesignerItemsList() {
        return tableDesignerItemsList;
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
        HBox.setHgrow(labelMaterial, Priority.ALWAYS);
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
        if (quantity == 1) {
            return;
        }
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
        labelName.setText("Радиусный элемент");
        //imageView.setImage(imageMain);
        labelMaterial.setText(material.getReceiptName());
        labelNull2.setText("");
        labelNull3.setText("");
        labelNull4.setText("");
        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Радиусный элемент");
        tooltipNameCard.setText("Радиусный элемент");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

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

        String currency = material.getRadiusElementCurrency();
        String units = "шт.";
        double priceForOne = -1.0;


        priceForOne = material.getRadiusElementPrice();

        priceForOne /= 100.0;

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;
        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

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

    private static ChoiceBox<String> choiceBoxMaterial;
    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StoneProductItem.class.getResource("/fxmls/TableDesigner/TableItems/RadiusSettings.fxml"));

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
        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));

        choiceBoxMaterial.setOnAction(event -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        tableDesignerItemsList.add(index, new RadiusItem(material, quantity));
    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getRadiusElementCurrency();
                String units = "шт.";
                double priceForOne = -1.0;


                priceForOne = material.getRadiusElementPrice();

                priceForOne /= 100.0;

                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(RadiusItem radiusItem){
        TableDesigner.openSettings(RadiusItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(radiusItem.material.getReceiptName());

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

            int index = getTableDesignerItemsList().indexOf(radiusItem);
            addItem(index, radiusItem.quantity);

            exitFromEditMode(radiusItem);
            radiusItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(radiusItem);

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
     * Dependency Part
     */

    boolean slave = false;

    public static RadiusItem getSlaveInstance(Material material, int quantity) {
        RadiusItem radiusItem = new RadiusItem(material, quantity);
        radiusItem.slave = true;

        radiusItem.lockRowButtons();

        return radiusItem;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        labelQuantity.setText("" + quantity);
    }

    private void lockRowButtons() {
        btnMinus.setDisable(true);
        btnPlus.setDisable(true);
        btnDelete.setDisable(true);
        btnEdit.setDisable(true);

        btnMinusCard.setDisable(true);
        btnPlusCard.setDisable(true);
        btnDeleteCard.setDisable(true);
        btnEditCard.setDisable(true);
    }

    public boolean isSlave() {
        return slave;
    }

    /**
     * JSON SAVING & OPENING PART
     */
    @Override
    public JSONObject getJsonView() {
        if (slave) return null;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", "RadiusItem");

        jsonObject.put("material", material.getName());
        jsonObject.put("quantity", quantity);

        return jsonObject;
    }

    public static RadiusItem initFromJSON(JSONObject jsonObject) {

        String materialName = (String) jsonObject.get("material");
        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        RadiusItem radiusItem = null;
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {
                radiusItem = new RadiusItem(m, quantity);
            }
        }

        //radiusItem.quantity = quantity;
        radiusItem.labelQuantity.setText("" + quantity);
        radiusItem.updateRowPrice();

        return radiusItem;

    }

}
