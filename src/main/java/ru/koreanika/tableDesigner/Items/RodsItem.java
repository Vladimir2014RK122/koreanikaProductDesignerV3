package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.Material.Material;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.Features.Rods;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.*;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RodsItem extends TableDesignerItem implements DependOnMaterial {

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalItemsList();


    Label labelRowNumber, labelName, labelMaterial, labelType, labelModel, labelNull1, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int type;
    String model;
    Image imageMain;


    public RodsItem(Material material, int type, String model, int quantity) {

        this.material = material;
        this.type = type;
        this.model = model;
        this.quantity = quantity;


        File file = new File("features_resources/rods/icons/" + "rods_" + type + "_icon.png");
        try {
            imageMain = new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/RodsRow.fxml")
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

    private static void updateMaterial(RodsItem item) {

        RodsItem oldRodsItem = item;

        Material newMaterial = null;
        Material defaultMaterial = ProjectHandler.getDefaultMaterial();

        if (ProjectHandler.getMaterialsListInProject().contains(item.getMaterial())) {
            newMaterial = oldRodsItem.material;
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
                    oldRodsItem.removeThisItem();
                    return;
                }
            }

        }

        RodsItem newRodsItem = new RodsItem(newMaterial, oldRodsItem.type, oldRodsItem.model, oldRodsItem.quantity);

        oldRodsItem.removeThisItem();
        tableDesignerItemsList.add(newRodsItem);
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

        String imgPath = "features_resources/rods/icons/" + "rods_" + type + "_icon.png";
        File file = new File(imgPath);
        try {
            imagesList.put("Прутки под горячее#" + imgPath, new ImageView(new Image(file.toURI().toURL().toString())));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            RodsItem.exitFromEditMode(this);
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
        labelName.setText("Подставка под горячее");
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);


        labelMaterial.setText(material.getReceiptName());
        labelType.setText("#" + type);
        labelModel.setText(model);

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Подставка под горячее");
        tooltipNameCard.setText("Подставка под горячее");
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

        String currency = material.getRodsCurrency();
        String units = "шт.";
        double priceForOne = -1.0;

        int type = comboBoxType.getSelectionModel().getSelectedItem().getType();


        priceForOne = material.getRodsTypesAndPrices().get(type - 1).doubleValue();

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
    private static ComboBox<RodsType> comboBoxType;
    private static ChoiceBox<String> choiceBoxModel;
    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/RodsSettings.fxml"));

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
        comboBoxType = (ComboBox<RodsType>) anchorPaneSettingsView.lookup("#comboBoxType");
        choiceBoxModel = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxModel");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getReceiptName());


        comboBoxType.setCellFactory(new Callback<ListView<RodsType>, ListCell<RodsType>>() {
            @Override
            public ListCell<RodsType> call(ListView<RodsType> param) {

                ListCell<RodsType> cell = new ListCell<RodsType>() {
                    @Override
                    protected void updateItem(RodsType item, boolean empty) {
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
        comboBoxType.setButtonCell(new ListCell<RodsType>() {
            @Override
            protected void updateItem(RodsType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText("#" + item.getType());
                    setGraphic(item.getImage());
                }
            }
        });


        for (int i = 1; i <= 4; i++) {
            comboBoxType.getItems().add(new RodsType(ProjectHandler.getDefaultMaterial(), i));
        }
        comboBoxType.getSelectionModel().select(0);
        comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());

        choiceBoxModel.getItems().addAll(Rods.getAvailableModels(comboBoxType.getSelectionModel().getSelectedItem().getType()));
        choiceBoxModel.getSelectionModel().select(0);

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));


        choiceBoxMaterial.setOnAction(event -> {
            updatePriceInSettings();
        });


        comboBoxType.setOnAction(event -> {
            if (comboBoxType.getItems().size() == 0 || comboBoxType.getSelectionModel().getSelectedIndex() == -1)
                return;

            choiceBoxModel.getItems().clear();
            choiceBoxModel.getItems().addAll(Rods.getAvailableModels(comboBoxType.getSelectionModel().getSelectedItem().getType()));
            choiceBoxModel.getSelectionModel().select(0);

            if (comboBoxType.getSelectionModel().getSelectedItem() != null) {
                comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());
            }

            updatePriceInSettings();

        });

        choiceBoxModel.setOnAction(event -> {

        });

    }

    private static void addItem(int index, int quantity){

        Material material = null;
        for (Material m : ProjectHandler.getMaterialsListInProject()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        int type = comboBoxType.getSelectionModel().getSelectedItem().getType();
        String model = choiceBoxModel.getSelectionModel().getSelectedItem();

        tableDesignerItemsList.add(index, new RodsItem(material, type, model, quantity));

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getReceiptName());

        comboBoxType.getItems().clear();
        for (int i = 1; i <= 2; i++) {
            comboBoxType.getItems().add(new RodsType(ProjectHandler.getDefaultMaterial(), i));
        }
        comboBoxType.getSelectionModel().select(0);

        choiceBoxModel.getItems().clear();
        choiceBoxModel.getItems().addAll(Rods.getAvailableModels(comboBoxType.getSelectionModel().getSelectedItem().getType()));
        choiceBoxModel.getSelectionModel().select(0);

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (comboBoxType.getSelectionModel().getSelectedItem() == null) return;

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getRodsCurrency();
                String units = "шт.";
                double priceForOne = -1.0;

                int type = comboBoxType.getSelectionModel().getSelectedItem().getType();


                priceForOne = material.getRodsTypesAndPrices().get(type - 1).doubleValue();

                priceForOne /= 100.0;

                priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(RodsItem rodsItem){
        TableDesigner.openSettings(RodsItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(rodsItem.material.getReceiptName());
        for(int i =0;i<comboBoxType.getItems().size();i++){
            if(comboBoxType.getItems().get(i).getType() == rodsItem.getType()){
                comboBoxType.getSelectionModel().select(i);
                break;
            }
        }
        choiceBoxModel.getSelectionModel().select(rodsItem.model);

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

            int index = getTableDesignerItemsList().indexOf(rodsItem);
            addItem(index, rodsItem.quantity);

            exitFromEditMode(rodsItem);
            rodsItem.removeThisItem();

        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(rodsItem);
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
        jsonObject.put("itemName", "RodsItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("type", type);
        jsonObject.put("model", model);


        return jsonObject;
    }

    public static RodsItem initFromJSON(JSONObject jsonObject) {

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

        int type = ((Long) jsonObject.get("type")).intValue();
        String model = (String) jsonObject.get("model");

        RodsItem rodsItem = new RodsItem(material, type, model, quantity);
        rodsItem.quantity = quantity;
        rodsItem.labelQuantity.setText("" + quantity);
        rodsItem.updateRowPrice();
        return rodsItem;
    }
}


class RodsType {
    ImageView image;
    int type = 1;
    Material material;


    RodsType(Material material, int type) {
        this.type = type;
        this.material = material;
    }

    public Node getImage() {
        File file;
        file = new File("features_resources/rods/icons/" + "rods_" + type + "_icon.png");

        try {
            image = new ImageView(new Image(file.toURI().toURL().toString()));
            image.setFitWidth(100);
            image.setFitHeight(100);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        String brief = "null";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("features_resources/rods/brief/rods_" + type + ".txt"), "UTF8"));
            brief = reader.readLine();
            ;
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Rods");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Rods io exception");
        }

        Label label = new Label("Tип " + type);
        //label.setPrefWidth(120);
        Pane pane = new Pane();
        pane.setPrefSize(120, 120);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setTranslateY(95);
        label.setPadding(new Insets(5, 10, 5, 10));
        label.setFont(Font.font(10));

        return pane;
    }

    public Tooltip getTooltip() {

        Tooltip tooltip = new Tooltip();

        File file;
        file = new File("features_resources/rods/infoImages/" + "rods_" + type + "_info_img.png");

        ImageView tooltipImage = null;

        try {
            tooltipImage = new ImageView(new Image(file.toURI().toURL().toString()));
            tooltipImage.setFitWidth(250);
            tooltipImage.setFitHeight(250);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        String brief = "null";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("features_resources/rods/brief/rods_" + type + ".txt"), "UTF8"));
            brief = reader.readLine();
            ;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Rods");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Rods io exception");
        }


        Label label = new Label(brief + " ,тип " + type);
        Pane pane = new Pane();
        pane.setPrefSize(250, 300);
        pane.getChildren().add(tooltipImage);
        pane.getChildren().add(label);
        label.setTranslateY(250);
        label.setPadding(new Insets(20, 10, 20, 10));
        label.setFont(Font.font(15));
        tooltip.setGraphic(pane);


        return tooltip;
    }

    public int getType() {
        return type;
    }
}