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

public class PlywoodItem extends TableDesignerItem implements DependOnMaterial {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalWorkItemsList();


    Label labelRowNumber, labelName, labelPaintingType, labelNull1, labelLength, labelWidth, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    double length;
    double width;
    int paintingType;
    Image imageMain;


    public PlywoodItem(Material material, double length, double width, int paintingType, int quantity) {

        this.material = material;
        this.length = length;
        this.width = width;
        this.paintingType = paintingType;
        this.quantity = quantity;


        imageMain = new ImageView(Project.class.getResource("/styles/images/TableDesigner/PlywoodItem/plywoodPaintingType" + paintingType + "_100px.png").toString()).getImage();


        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/PlywoodRow.fxml")
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

    private static void updateMaterial(PlywoodItem item) {

        PlywoodItem oldPlywoodItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterialsListInProject().contains(item.getMaterial())) {
            newMaterial = oldPlywoodItem.material;
        } else {

            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType())) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterialsListInProject()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType())) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldPlywoodItem.removeThisItem();
                    return;
                }
            }

        }

        PlywoodItem newPlywoodItem = new PlywoodItem(newMaterial,
                oldPlywoodItem.length, oldPlywoodItem.width, oldPlywoodItem.paintingType, oldPlywoodItem.quantity);

        oldPlywoodItem.removeThisItem();
        tableDesignerItemsList.add(newPlywoodItem);
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public int getPaintingType() {
        return paintingType;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/PlywoodItem/plywoodPaintingType" + paintingType + "_100px.png";
        String imageName = "";
        if(material.getMainType().equals("Кварцекерамический камень") || material.getMainType().equals("Мраморный агломерат")){
            imageName = "Подложка полимерная";
        }else{
            imageName = "Подложка из фанеры";
        }

        imagesList.put(imageName + "#" + imgPath, new ImageView(getClass().getResource(imgPath).toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            PlywoodItem.exitFromEditMode(this);
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
        HBox.setHgrow(labelPaintingType, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelWidth, Priority.ALWAYS);
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

        String name = "";
        if(material.getMainType().equals("Кварцекерамический камень") || material.getMainType().equals("Мраморный агломерат")){
            name = "Подложка полимерная";
        }else{
            name = "Подложка из фанеры";
        }
        name +=(paintingType == 1) ? " без покраски" : " с покраской";

        labelName.setText(name);
        imageViewMain.setImage(imageMain);

        //labelPaintingType.setText((paintingType == 1) ? "Без покраски" : "С покраской");
        labelPaintingType.setText(material.getReceiptName());
        labelNull1.setText("");
        labelLength.setText("" + length);
        labelWidth.setText("" + width);

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText(material.getMainType().equals("Кварцекерамический камень") ? "Подложка полимерная" : "Подложка из фанеры");
        tooltipNameCard.setText(material.getMainType().equals("Кварцекерамический камень") ? "Подложка полимерная" : "Подложка из фанеры");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText((paintingType == 1) ? " без покраски" : " с покраской");

        labelName3Card.setText("Длина");
        labelValue3Card.setText("" + (int)length + " мм");

        labelName4Card.setText("Ширина");
        labelValue4Card.setText("" + (int)width + " мм");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        double priceForOne = -1.0;
        //Material material = ProjectHandler.getDefaultMaterial();
//        if(material == null){
//            System.out.println("PLYWOOD default material == null");
//        }
        priceForOne = (paintingType == 1) ? (material.getPlywoodPrices().get(0)/100) : (material.getPlywoodPrices().get(1)/100);
        priceForOne *= Project.getPriceMainCoefficient().doubleValue();


        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000) * (width/1000)) + ReceiptManager.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length/1000) * (width/1000)) + ReceiptManager.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity * (length/1000) * (width/1000)) + ReceiptManager.RUR_SYMBOL);
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
    private static TextField textFieldLength, textFieldWidth;
    private static ToggleButton toggleButtonPlywoodType1, toggleButtonPlywoodType2;
    private static ToggleGroup toggleGroupPaintingType = new ToggleGroup();
    private static Label labelPrice;

    private static boolean lengthOk = true, widthOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/PlywoodSettings.fxml"));

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

        toggleButtonPlywoodType1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonPlywoodType1");
        toggleButtonPlywoodType2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonPlywoodType2");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());


        toggleButtonPlywoodType1.setToggleGroup(toggleGroupPaintingType);
        toggleButtonPlywoodType2.setToggleGroup(toggleGroupPaintingType);
        toggleButtonPlywoodType1.setSelected(true);

//        ImageView image1 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/PlywoodItem/plywoodPaintingType1.png").toString());
//        image1.setFitWidth(45);
//        image1.setFitHeight(45);
//        toggleButtonPaintingType1.setGraphic(image1);
//
//        ImageView image2 = new ImageView(ProjectHandler.class.getResource("/styles/images/TableDesigner/PlywoodItem/plywoodPaintingType2.png").toString());
//        image2.setFitWidth(45);
//        image2.setFitHeight(45);
//        toggleButtonPaintingType2.setGraphic(image2);

        textFieldLength.setText("600");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));

        choiceBoxMaterial.setOnAction(event -> {

            Material material = null;
            for (Material m : Project.getMaterialsListInProject()) {
                if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                    material = m;
                }
            }
            if(material != null)btnAdd.setDisable(material.getName().contains("Массив_шпон"));

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

        toggleGroupPaintingType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){
        if (!(lengthOk && widthOk)) return;

        Material material = null;
        for (Material m : Project.getMaterialsListInProject()) {
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

        double width = 0;
        try {
            width = Double.parseDouble(textFieldWidth.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        int paintingType = 1;
        if (toggleButtonPlywoodType1.isSelected()) paintingType = 1;
        else if (toggleButtonPlywoodType2.isSelected()) paintingType = 2;

        tableDesignerItemsList.add(index, new PlywoodItem(material, length, width, paintingType, quantity));

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        Material material = null;
        for (Material m : Project.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }
        if(material != null)btnAdd.setDisable(material.getName().contains("Массив_шпон"));

        textFieldLength.setText("600");
        textFieldWidth.setText("600");
        toggleButtonPlywoodType1.setSelected(true);

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {
        Material material = null;
        for (Material m : Project.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }

        }

        String currency = "RUB";
        String units = "м^2";
        double priceForOne = -1.0;
        //ProjectHandler.getDefaultMaterial().getP
        if(material == null) return;

        priceForOne = ((toggleButtonPlywoodType1.isSelected()) ? material.getPlywoodPrices().get(0)/100 : material.getPlywoodPrices().get(1)/100);
        currency = material.getPlywoodCurrency().get(0);

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();
        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));

    }

    private static void enterToEditMode(PlywoodItem plywoodItem){
        TableDesigner.openSettings(PlywoodItem.class);

        choiceBoxMaterial.getSelectionModel().select(plywoodItem.material.getReceiptName());

        //get row data to settings
        textFieldLength.setText("" + plywoodItem.length);
        textFieldWidth.setText("" + plywoodItem.width);
        if(plywoodItem.paintingType == 1) toggleButtonPlywoodType1.setSelected(true);
        else if(plywoodItem.paintingType == 2) toggleButtonPlywoodType2.setSelected(true);

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

            int index = getTableDesignerItemsList().indexOf(plywoodItem);
            addItem(index, plywoodItem.quantity);

            exitFromEditMode(plywoodItem);
            plywoodItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(plywoodItem);

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
        jsonObject.put("itemName", "PlywoodItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("length", length);
        jsonObject.put("width", width);
        jsonObject.put("paintingType", paintingType);

        return jsonObject;
    }

    public static PlywoodItem initFromJSON(JSONObject jsonObject) {

        String materialName = (String) jsonObject.get("material");
        Material material = null;
        if(materialName != null){
            for (Material m : Project.getMaterialsListInProject()) {
                if (materialName.equals(m.getName())) {
                    material = m;
                    break;
                }
            }
        }
        if (material == null) {
            material = Project.getDefaultMaterial();
        }

        int quantity = ((Long) jsonObject.get("quantity")).intValue();


        double length = ((Double) jsonObject.get("length")).doubleValue();
        double width = ((Double) jsonObject.get("width")).doubleValue();
        int paintingType = ((Long) jsonObject.get("paintingType")).intValue();


        PlywoodItem plywoodItem = new PlywoodItem(material, length, width, paintingType, quantity);
        //plywoodItem.quantity = quantity;
        plywoodItem.labelQuantity.setText("" + quantity);
        plywoodItem.updateRowPrice();
        return plywoodItem;
    }

}
