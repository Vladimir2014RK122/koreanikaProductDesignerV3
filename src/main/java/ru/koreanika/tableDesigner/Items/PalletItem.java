package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.Material.Material;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.json.simple.JSONObject;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PalletItem extends TableDesignerItem implements DependOnMaterial  {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalItemsList();


    Label labelRowNumber, labelName, labelMaterial, labelType, labelModel, labelNull1, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int type = 1;
    String model;
    Image imageMain;

    double price;
    double priceForOne;
    String units;


    public PalletItem(Material material, int type, String model, int quantity) {

        this.material = material;
        this.type = type;
        this.model = model;
        this.quantity = quantity;

        imageMain = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/PalleteItem/pallete_100_id1.png").toString()).getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/PalletRow.fxml")
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

    private static void updateMaterial(PalletItem item) {

        PalletItem oldPalletItem = item;

        Material newMaterial = null;
        Material defaultMaterial = ProjectHandler.getDefaultMaterial();

        if (ProjectHandler.getMaterialsListInProject().contains(item.getMaterial())){
            newMaterial = oldPalletItem.material;
        }else{
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
                    oldPalletItem.removeThisItem();
                    return;
                }
            }
        }



        PalletItem newPalletItem = new PalletItem(newMaterial, oldPalletItem.type, oldPalletItem.model, oldPalletItem.quantity);

        oldPalletItem.removeThisItem();
        tableDesignerItemsList.add(newPalletItem);

    }

    public String getModel() {
        return model;
    }


    public int getType() {
        return type;
    }

    public String getName(){
        return "Поддон";
    }

    public String getUnits(){
        return units;
    }

    public double getPriceForOne(){
        return priceForOne;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/PalleteItem/pallete_100_id1.png";
        imagesList.put("Поддон#" + imgPath, new ImageView(getClass().getResource(imgPath).toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            PalletItem.exitFromEditMode(this);
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
        labelName.setText("Поддон");
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);


        labelMaterial.setText(material.getReceiptName());
        labelType.setText("" );
        labelModel.setText(model);

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Поддон");
        tooltipNameCard.setText("Поддон");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText("-");

        labelName3Card.setText("Модель");
        labelValue3Card.setText(model);

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getPalletsCurrency();
        units = "шт.";
        priceForOne = -1.0;

        priceForOne = material.getPalletsModelsAndPrices().get(model).doubleValue();

        priceForOne /= 100.0;

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;
        priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

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

    private static ChoiceBox<String> choiceBoxMaterial;
    private static ComboBox<PalletView> comboBoxView;
    private static ChoiceBox<String> choiceBoxModel;
    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {

            FXMLLoader fxmlLoader = new FXMLLoader(PalletItem.class.getResource("/fxmls/TableDesigner/TableItems/PalletSettings.fxml"));

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
        comboBoxView = (ComboBox<PalletView>) anchorPaneSettingsView.lookup("#comboBoxView");
        choiceBoxModel = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxModel");

        comboBoxView.setCellFactory(new Callback<ListView<PalletView>, ListCell<PalletView>>() {
            @Override
            public ListCell<PalletView> call(ListView<PalletView> param) {

                ListCell<PalletView> cell = new ListCell<PalletView>() {
                    @Override
                    protected void updateItem(PalletView item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            //setText("#" + item.getType());
                            setGraphic(item.getImage());
                            setTooltip(item.getTooltip());
                        }
                    }
                };
                return cell;
            }
        });
        comboBoxView.setButtonCell(new ListCell<PalletView>() {
            @Override
            protected void updateItem(PalletView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText("#" + item.getType());
                    setGraphic(item.getImageCopy());
                    setTooltip(item.getTooltipCopy());
                }
            }
        });

        comboBoxView.getItems().add(new PalletView(1));

        comboBoxView.getSelectionModel().select(0);
        comboBoxView.setTooltip(comboBoxView.getSelectionModel().getSelectedItem().getTooltip());


        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getReceiptName());


        choiceBoxModel.getItems().addAll(ProjectHandler.getDefaultMaterial().getPalletsModelsAndPrices().keySet());
        choiceBoxModel.getSelectionModel().select(0);

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));


        choiceBoxMaterial.setOnAction(event -> {

            Material material = null;
            for (Material m : ProjectHandler.getMaterialsListInProject()) {
                if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                    material = m;
                }
            }

            if(material == null) return;
            choiceBoxModel.getItems().clear();
            choiceBoxModel.getItems().addAll(material.getPalletsModelsAndPrices().keySet());
            choiceBoxModel.getSelectionModel().select(0);

            updatePriceInSettings();
        });


        choiceBoxModel.setOnAction(event -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){

        Material material = null;
        for (Material m : ProjectHandler.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        int type = comboBoxView.getSelectionModel().getSelectedItem().getId();
        String model = choiceBoxModel.getSelectionModel().getSelectedItem();

        tableDesignerItemsList.add(index, new PalletItem(material, type, model, quantity));

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            if(material.getPalletsModelsAndPrices().size() != 0) choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(0);

        Material material = null;
        for (Material m : ProjectHandler.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        if(choiceBoxMaterial.getItems().size() == 0){
            labelPrice.setText("Недоступен");
            choiceBoxMaterial.setDisable(true);
            comboBoxView.setDisable(true);
            choiceBoxModel.setDisable(true);
            btnAdd.setDisable(true);

            return;
        }else{
            choiceBoxMaterial.setDisable(false);
            comboBoxView.setDisable(false);
            choiceBoxModel.setDisable(false);
            btnAdd.setDisable(false);
        }


        choiceBoxModel.getItems().clear();
        choiceBoxModel.getItems().addAll(material.getPalletsModelsAndPrices().keySet());
        choiceBoxModel.getSelectionModel().select(0);

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getPalletsCurrency();
                String units = "шт.";
                double priceForOne = -1.0;


//                System.out.println("PALLETE MODEL AND PRICES: " + material.getPalletsModelsAndPrices());
                if (choiceBoxModel.getSelectionModel().getSelectedItem() == null) return;

                priceForOne = material.getPalletsModelsAndPrices().get(choiceBoxModel.getSelectionModel().getSelectedItem()).doubleValue();

                priceForOne /= 100.0;

                priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(PalletItem palletItem){
        TableDesigner.openSettings(PalletItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(palletItem.material.getReceiptName());

        choiceBoxModel.getSelectionModel().select(palletItem.model);

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

            int index = getTableDesignerItemsList().indexOf(palletItem);
            addItem(index, palletItem.quantity);

            exitFromEditMode(palletItem);
            palletItem.removeThisItem();

        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(palletItem);
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
        jsonObject.put("itemName", "PalletItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("type", type);
        jsonObject.put("model", model);

        return jsonObject;
    }

    public static PalletItem initFromJSON(JSONObject jsonObject) {

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

        String model = (String) jsonObject.get("model");
        int type = 1;
        if(jsonObject.get("type") != null) type = ((Long) jsonObject.get("type")).intValue();

        PalletItem palletItem = new PalletItem(material, type, model, quantity);

        palletItem.labelQuantity.setText("" + quantity);
        palletItem.updateRowPrice();
        return palletItem;
    }
}

class PalletView {
    ImageView image;
    int id = 1;
    //Material material;

    Pane paneMain = new Pane();
    Pane paneCopy = new Pane();

    Tooltip tooltip = new Tooltip();
    Tooltip tooltipCopy = new Tooltip();


    PalletView(int id) {
        this.id = id;

        createImage(paneMain);
        createImage(paneCopy);

        createTooltip(tooltip);
        createTooltip(tooltipCopy);
    }

    private void createImage(Pane pane){

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/PalleteItem/pallete_100_id" + id + ".png");
        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        image = new ImageView(new Image(imgURL.toString()));
        image.setFitWidth(100);
        image.setFitHeight(100);

        String brief = "Поддон, тип " + id;

        Label label = new Label(brief);
        label.setPrefWidth(100);
        label.setPrefHeight(30);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        pane.setPrefSize(120, 130);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setTranslateX(10);
        label.setTranslateY(100);
//        label.setPadding(new Insets(1, 5, 1, 5));
        label.setFont(Font.font(10));
    }

    private void createTooltip(Tooltip tooltip){

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/PalleteItem/pallete_1000_id" + id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        ImageView tooltipImage = null;

        tooltipImage = new ImageView(new Image(imgURL.toString()));
        tooltipImage.setFitWidth(250);
        tooltipImage.setFitHeight(250);

        String brief = "Поддон, тип " + id;


        Label label = new Label(brief);
        label.setAlignment(Pos.TOP_LEFT);
        label.setWrapText(true);
        label.setPrefWidth(250);
        Pane pane = new Pane();
        pane.setPrefSize(250, 325);
        pane.getChildren().add(tooltipImage);
        pane.getChildren().add(label);
        label.setTranslateY(250);
        label.setPadding(new Insets(10, 10, 20, 10));
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

    public int getId() {
        return id;
    }
}