package ru.koreanika.tableDesigner.Items;

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
import ru.koreanika.common.material.Material;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CutoutItem extends TableDesignerItem implements DependOnMaterial {

    Label labelRowNumber, labelName, labelMaterial, labelType, labelModel, labelNull1, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    Material material;
    int type;
    String model;
    Image imageMain;


    public CutoutItem(Material material, int type, String model, int quantity) {

        this.material = material;
        this.type = type;
        this.model = model;
        this.quantity = quantity;

        File file = new File("features_resources/cutout/icons/" + "cutout_" + type + "_icon.png");
        if (file == null) {
            file = new File(getClass().getResource("/styles/images/no_img.png").toString());
        }
        try {
            imageMain = new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/CutoutRow.fxml")
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

    private static void updateMaterial(CutoutItem item) {

        CutoutItem oldCutOutItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldCutOutItem.material;
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
                    oldCutOutItem.removeThisItem();
                    return;
                }
            }
        }


        CutoutItem newBorderItem = new CutoutItem(newMaterial, oldCutOutItem.type, oldCutOutItem.model, oldCutOutItem.quantity);

        oldCutOutItem.removeThisItem();
        TableDesignerSession.getTableDesignerAdditionalItemsList().add(newBorderItem);

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

        if (file == null) {
            file = new File(getClass().getResource("/styles/images/no_img.png").toString());
        }

        try {
            imagesList.put("Вырез#" + imgPath, new ImageView(new Image(file.toURI().toURL().toString())));
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
            CutoutItem.exitFromEditMode(this);
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

        String name = "Вырез";
        String subName = " ";
        if (type == Cutout.CUTOUT_TYPE_1) subName += "под питьевой кран. d = 12мм";
        else if (type == Cutout.CUTOUT_TYPE_2) subName += "под смеситель. d = 35мм";
        else if (type == Cutout.CUTOUT_TYPE_3) subName += "под варочную панель/раковину";
        else if (type == Cutout.CUTOUT_TYPE_4) subName += "под розетку. d = 65мм";
        else if (type == Cutout.CUTOUT_TYPE_5) subName += "под накладную мойку";
        else if (type == Cutout.CUTOUT_TYPE_6) subName += "под варочную панель вровень со столешницей";
        else if (type == Cutout.CUTOUT_TYPE_7) subName += "под радиатор";
        else if (type == Cutout.CUTOUT_TYPE_8) subName += "прямолиннейный. Без обработки";
        else if (type == Cutout.CUTOUT_TYPE_9) subName += "криволинейный. Без обработки";
        else if (type == Cutout.CUTOUT_TYPE_10) subName += "прямолинейный. С обработкой";
        else if (type == Cutout.CUTOUT_TYPE_11) subName += "криволинейный. С обработкой";
        else if (type == Cutout.CUTOUT_TYPE_12) subName += "под раковину/мойку, для установки в уровень со столешницей";
        else if (type == Cutout.CUTOUT_TYPE_13) subName += "под измельчитель";
        else if (type == Cutout.CUTOUT_TYPE_14) subName += "под дозатор";
        else if (type == Cutout.CUTOUT_TYPE_14) subName += "под дозатор";

        labelName.setText(name + subName);
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);


        labelMaterial.setText(material.getReceiptName());
        labelType.setText("#" + type);
        labelModel.setText(model);

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText(name);
        tooltipNameCard.setText(name);
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText("#" + type);

        labelName3Card.setText("Модель");
        labelValue3Card.setText(model);

        labelName4Card.setText("Вид");
        labelValue4Card.setText(subName);

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getCutoutCurrency();
        String units = "шт.";
        double priceForOne = -1.0;


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
    private static TextField textFieldCount;
    private static Label labelPrice;

    private static ArrayList<CutoutType> cutoutTypes = new ArrayList<>();

    private static boolean countOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/CutoutSettings.fxml"));

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
        textFieldCount = (TextField) anchorPaneSettingsView.lookup("#textFieldCount");

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
                            //setText("#" + item.getType());
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
                    //setText("#" + item.getType());
                    setGraphic(item.getImageCopy());
                    setTooltip(item.getTooltipCopy());
                }
            }
        });

        cutoutTypes.clear();
        cutoutTypes.add(null); //for shift elements on 1 position
        for (int i = 1; i <= 14; i++){
            cutoutTypes.add(new CutoutType(i));
        }

        for (int i = 1; i <= 6; i++) {
            comboBoxType.getItems().add(cutoutTypes.get(i));
        }
        for (int i = 8; i <= 8; i++) {
            comboBoxType.getItems().add(cutoutTypes.get(i));
        }
        for (int i = 13; i <= 14; i++) {
            comboBoxType.getItems().add(cutoutTypes.get(i));
        }

        comboBoxType.getSelectionModel().select(0);
        comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());

        choiceBoxModel.getItems().addAll(Cutout.getAvailableModels(comboBoxType.getSelectionModel().getSelectedItem().getType()));
        choiceBoxModel.getSelectionModel().select(0);


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
        });
    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerAdditionalItemsList().size(), 1));


        choiceBoxMaterial.setOnAction(event -> {

            updatePriceInSettings();
        });


        comboBoxType.setOnAction(event -> {
            if (comboBoxType.getItems().size() == 0 || comboBoxType.getSelectionModel().getSelectedIndex() == -1)
                return;

            choiceBoxModel.getItems().clear();
            choiceBoxModel.getItems().addAll(Cutout.getAvailableModels(comboBoxType.getSelectionModel().getSelectedItem().getType()));
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
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        int type = comboBoxType.getSelectionModel().getSelectedItem().getType();
        String model = choiceBoxModel.getSelectionModel().getSelectedItem();

        if (countOk) {
            quantity = Integer.parseInt(textFieldCount.getText());
            if(index == -1) index = TableDesignerSession.getTableDesignerAdditionalItemsList().size();
            TableDesignerSession.getTableDesignerAdditionalItemsList().add(index, new CutoutItem(material, type, model, quantity));
        }

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        comboBoxType.getItems().clear();
        for (int i = 1; i <= 6; i++) {
            comboBoxType.getItems().add(cutoutTypes.get(i));
        }

        for (int i = 8; i <= 8; i++) {
            comboBoxType.getItems().add(cutoutTypes.get(i));
        }

        for (int i = 13; i <= 14; i++) {
            comboBoxType.getItems().add(cutoutTypes.get(i));
        }
        comboBoxType.getSelectionModel().select(0);

        choiceBoxModel.getItems().clear();
        choiceBoxModel.getItems().addAll(Cutout.getAvailableModels(comboBoxType.getSelectionModel().getSelectedItem().getType()));
        choiceBoxModel.getSelectionModel().select(0);

        textFieldCount.setText("1");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (comboBoxType.getSelectionModel().getSelectedItem() == null) return;

        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getCutoutCurrency();
                String units = "шт.";
                double priceForOne = -1.0;

                int type = comboBoxType.getSelectionModel().getSelectedItem().getType();


                priceForOne = material.getCutoutTypesAndPrices().get(type).doubleValue();

                priceForOne /= 100.0;

                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(CutoutItem cutoutItem){
        TableDesigner.openSettings(CutoutItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(cutoutItem.material.getReceiptName());
        for(int i =0;i<comboBoxType.getItems().size();i++){
            if(comboBoxType.getItems().get(i).getType() == cutoutItem.getType()){
                comboBoxType.getSelectionModel().select(i);
                break;
            }
        }
        textFieldCount.setText("" + cutoutItem.quantity);

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

            int index = TableDesignerSession.getTableDesignerAdditionalItemsList().indexOf(cutoutItem);
            addItem(index, cutoutItem.quantity);

            exitFromEditMode(cutoutItem);
            cutoutItem.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(cutoutItem);

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
        jsonObject.put("itemName", "CutoutItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("type", type);
        jsonObject.put("model", model);


        return jsonObject;
    }

    public static CutoutItem initFromJSON(JSONObject jsonObject) {

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

        CutoutItem cutoutItem = new CutoutItem(material, type, model, quantity);
        //cutoutItem.quantity = quantity;
        //cutoutItem.labelQuantity.setText("" + quantity);

        cutoutItem.updateRowPrice();
        return cutoutItem;
    }
}


class CutoutType {
    ImageView image;
    int type = 1;
    //Material material;

    Pane paneMain = new Pane();
    Pane paneCopy = new Pane();

    Tooltip tooltip = new Tooltip();
    Tooltip tooltipCopy = new Tooltip();


    CutoutType(int type) {
        this.type = type;

        createImage(paneMain);
        createImage(paneCopy);

        createTooltip(tooltip);
        createTooltip(tooltipCopy);
    }

    private void createImage(Pane pane){
        File file;
        file = new File("features_resources/cutout/icons/" + "cutout_" + type + "_icon.png");

        if (file == null) {
            file = new File(getClass().getResource("/styles/images/no_img.png").toString());
        }

        try {
            image = new ImageView(new Image(file.toURI().toURL().toString()));
            image.setFitWidth(100);
            image.setFitHeight(100);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        String brief = "null";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("features_resources/cutout/summary/cutout_" + type + ".txt"), "UTF8"));
            brief = reader.readLine();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Cutout");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Cutout io exception");
        }

        Label label = new Label(brief);
        label.setPrefWidth(100);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);

        pane.setPrefSize(120, 130);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setTranslateY(95);
        label.setPadding(new Insets(1, 5, 1, 5));
        label.setFont(Font.font(10));
    }

    private void createTooltip(Tooltip tooltip){

        File file = null;
        file = new File("features_resources/cutout/infoImages/" + "cutout_" + type + "_info_img.png");

        if (file == null) {
            file = new File(getClass().getResource("/styles/images/no_img.png").toString());
        }

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("features_resources/cutout/brief/cutout_" + type + ".txt"), "UTF8"));
            brief = reader.readLine();

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Cutout");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Cutout io exception");
        }


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

    public int getType() {
        return type;
    }
}