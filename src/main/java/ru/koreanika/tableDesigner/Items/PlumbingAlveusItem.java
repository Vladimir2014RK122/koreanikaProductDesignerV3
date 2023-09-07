package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
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
import javafx.util.Callback;
import org.json.simple.JSONObject;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PlumbingAlveusItem extends TableDesignerItem{

    private static ObservableList<TableDesignerItem> tableDesignerItemsList = TableDesigner.getTableDesignerAdditionalItemsList();


    Label labelRowNumber, labelName, labelMaterial, labelType, labelModel, labelNull1, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    PlumbingElement plumbingElement;

    int id;
    String model;
    String name;
    String size;
    Image imageMain;

    double price;
    double priceForOne;
    String units;


    public PlumbingAlveusItem(int id, String model, int quantity) {

        plumbingElement = ProjectHandler.getPlumbingElementsList()
                .stream()
                .filter(c -> c.getId() == id)
                .collect(Collectors.toList())
                .get(0);

        this.id = id;
        this.model = model;
        this.quantity = quantity;

        name = plumbingElement.getName() + " " + model;

        size = plumbingElement.getSize(model);


        URL imgURL = getClass().getResource("/styles/images/TableDesigner/PlumbingAlveus/plumbing_alveus_250_" + this.id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        imageMain = new Image(imgURL.toString());

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/PlumbingAlveusRow.fxml")
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



    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceForOne() {
        return priceForOne;
    }

    public String getUnits() {
        return units;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/PlumbingAlveus/plumbing_alveus_250_" + this.id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        String imgPath = "/styles/images/TableDesigner/PlumbingAlveus/plumbing_alveus_250_" + this.id + ".png";

        imagesList.put("Alveus#" + imgPath, new ImageView(new Image(imgURL.toString())));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        tableDesignerItemsList.remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            PlumbingAlveusItem.exitFromEditMode(this);
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

        labelName.setText(name);
        labelName.setWrapText(true);
        imageViewMain.setImage(imageMain);


        labelMaterial.setText(plumbingElement.getSize(model) + "мм");
        labelType.setText("");
        labelModel.setText("");

        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText(name);
        tooltipNameCard.setText(name);
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Размер");
        labelValue1Card.setText(plumbingElement.getSize(model) + "мм");

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

        String currency = plumbingElement.getCurrency();
        units = "шт.";


        priceForOne = plumbingElement.getPrice(model);

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;

        priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + ReceiptManager.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + ReceiptManager.RUR_SYMBOL);

        price = priceForOne * quantity;
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

    private static ComboBox<PlumbingAlveusType> comboBoxType;
    private static ChoiceBox<String> choiceBoxModel;
    private static TextField textFieldCount;
    private static Label labelPrice;

   // private static ArrayList<PlumbingAlveusType> plumbingAlveusTypes = new ArrayList<>();

    private static boolean countOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/PlumbingAlveusSettings.fxml"));

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

//        System.out.println("ProjectHandler.getExternalElementsList() = ");
        ProjectHandler.getPlumbingElementsList().forEach(System.out::println);

        comboBoxType = (ComboBox<PlumbingAlveusType>) anchorPaneSettingsView.lookup("#comboBoxType");
        choiceBoxModel = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxModel");
        textFieldCount = (TextField) anchorPaneSettingsView.lookup("#textFieldCount");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");


        comboBoxType.setCellFactory(new Callback<ListView<PlumbingAlveusType>, ListCell<PlumbingAlveusType>>() {
            @Override
            public ListCell<PlumbingAlveusType> call(ListView<PlumbingAlveusType> param) {

                ListCell<PlumbingAlveusType> cell = new ListCell<PlumbingAlveusType>() {
                    @Override
                    protected void updateItem(PlumbingAlveusType item, boolean empty) {
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
        comboBoxType.setButtonCell(new ListCell<PlumbingAlveusType>() {
            @Override
            protected void updateItem(PlumbingAlveusType item, boolean empty) {
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

        ProjectHandler.getPlumbingElementsList().forEach(e->{

            comboBoxType.getItems().add(new PlumbingAlveusType(e.getId()));
        });

        comboBoxType.getSelectionModel().select(0);
        comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());

        int id = comboBoxType.getSelectionModel().getSelectedItem().getId();
        PlumbingElement plumbingElement = ProjectHandler.getPlumbingElementsList()
                .stream()
                .filter(e->e.getId() == id)
                .collect(Collectors.toList()).get(0);

        List<String> modelAndSize = new ArrayList<>();
        if(plumbingElement.getModels().size() == plumbingElement.getSizes().size()){
            for(int i=0;i<plumbingElement.getModels().size();i++){
                String ms = plumbingElement.getModels().get(i) + " : " + plumbingElement.getSizes().get(i);
                modelAndSize.add(ms);
            }
        }else{
            modelAndSize = new ArrayList<>(plumbingElement.getModels());
        }
        choiceBoxModel.getItems().addAll(modelAndSize);
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

        btnAdd.setOnMouseClicked(event -> addItem(getTableDesignerItemsList().size(), 1));

        comboBoxType.setOnAction(event -> {
            if (comboBoxType.getItems().size() == 0 || comboBoxType.getSelectionModel().getSelectedIndex() == -1)
                return;

            int id = comboBoxType.getSelectionModel().getSelectedItem().getId();
            PlumbingElement plumbingElement = ProjectHandler.getPlumbingElementsList()
                    .stream()
                    .filter(e->e.getId() == id)
                    .collect(Collectors.toList()).get(0);

            choiceBoxModel.getItems().clear();

            List<String> modelAndSize = new ArrayList<>();
            if(plumbingElement.getModels().size() == plumbingElement.getSizes().size()){
                for(int i=0;i<plumbingElement.getModels().size();i++){
                    String ms = plumbingElement.getModels().get(i) + " : " + plumbingElement.getSizes().get(i);
                    modelAndSize.add(ms);
                }
            }else{
                modelAndSize = new ArrayList<>(plumbingElement.getModels());
            }

            choiceBoxModel.getItems().addAll(modelAndSize);
            choiceBoxModel.getSelectionModel().select(0);

            if (comboBoxType.getSelectionModel().getSelectedItem() != null) {
                comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());
            }

            updatePriceInSettings();
        });

        choiceBoxModel.setOnAction(event -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){


        int id = comboBoxType.getSelectionModel().getSelectedItem().getId();
        String model = choiceBoxModel.getSelectionModel().getSelectedItem().split(" : ")[0];

        if (countOk) {
            quantity = Integer.parseInt(textFieldCount.getText());
            if(index == -1) index = tableDesignerItemsList.size();
            tableDesignerItemsList.add(index, new PlumbingAlveusItem(id, model, quantity));
        }

    }

    public static void settingsControlElementsRefresh() {
        comboBoxType.getItems().clear();
        ProjectHandler.getPlumbingElementsList().forEach(e->{
            comboBoxType.getItems().add(new PlumbingAlveusType(e.getId()));
        });

        comboBoxType.getSelectionModel().select(0);
        comboBoxType.setTooltip(comboBoxType.getSelectionModel().getSelectedItem().getTooltip());

        int id = comboBoxType.getSelectionModel().getSelectedItem().getId();
        PlumbingElement plumbingElement = ProjectHandler.getPlumbingElementsList()
                .stream()
                .filter(e->e.getId() == id)
                .collect(Collectors.toList()).get(0);

        choiceBoxModel.getItems().clear();
        List<String> modelAndSize = new ArrayList<>();
        if(plumbingElement.getModels().size() == plumbingElement.getSizes().size()){
            for(int i=0;i<plumbingElement.getModels().size();i++){
                String ms = plumbingElement.getModels().get(i) + " : " + plumbingElement.getSizes().get(i);
                modelAndSize.add(ms);
            }
        }else{
            modelAndSize = new ArrayList<>(plumbingElement.getModels());
        }


        choiceBoxModel.getItems().addAll(modelAndSize);
        choiceBoxModel.getSelectionModel().select(0);

        textFieldCount.setText("1");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (comboBoxType.getSelectionModel().getSelectedItem() == null) return;

        int id = comboBoxType.getSelectionModel().getSelectedItem().getId();
        PlumbingElement plumbingElement = ProjectHandler.getPlumbingElementsList()
                .stream()
                .filter(e->e.getId() == id)
                .collect(Collectors.toList()).get(0);


        if(choiceBoxModel.getSelectionModel().getSelectedItem() == null) return;
        String model = choiceBoxModel.getSelectionModel().getSelectedItem().split(" : ")[0];

        String currency = plumbingElement.getCurrency();
        String units = "шт.";
        double priceForOne = -1.0;

        priceForOne = plumbingElement.getPrice(model);

        priceForOne *= ProjectHandler.getPriceMainCoefficient().doubleValue();

        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
    }

    private static void enterToEditMode(PlumbingAlveusItem plumbingAlveus){
        TableDesigner.openSettings(PlumbingAlveusItem.class);


        //get row data to settings

        textFieldCount.setText("" + plumbingAlveus.quantity);

        for(PlumbingAlveusType plumbingAlveusType : comboBoxType.getItems()){
            if(plumbingAlveusType.getId() == plumbingAlveus.getId()){
                comboBoxType.getSelectionModel().select(plumbingAlveusType);
                break;
            }
        }

        choiceBoxModel.getSelectionModel().select(plumbingAlveus.model + " : " + plumbingAlveus.size);


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

            int index = getTableDesignerItemsList().indexOf(plumbingAlveus);
            addItem(index, plumbingAlveus.quantity);

            exitFromEditMode(plumbingAlveus);
            plumbingAlveus.removeThisItem();


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(plumbingAlveus);

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
        jsonObject.put("itemName", "PlumbingAlveusItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("id", id);
        jsonObject.put("model", model);


        return jsonObject;
    }

    public static PlumbingAlveusItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();
        int id = ((Long) jsonObject.get("id")).intValue();
        String model = (String) jsonObject.get("model");

        PlumbingAlveusItem plumbingAlveusItem = new PlumbingAlveusItem(id, model, quantity);
        //cutoutItem.quantity = quantity;
        //cutoutItem.labelQuantity.setText("" + quantity);

        plumbingAlveusItem.updateRowPrice();
        return plumbingAlveusItem;
    }
}

class PlumbingAlveusType {
    ImageView image;
    int id = 1;
    //Material material;

    Pane paneMain = new Pane();
    Pane paneCopy = new Pane();

    Tooltip tooltip = new Tooltip();
    Tooltip tooltipCopy = new Tooltip();


    PlumbingAlveusType(int id) {
        this.id = id;

        createImage(paneMain);
        createImage(paneCopy);

        createTooltip(tooltip);
        createTooltip(tooltipCopy);
    }

    private void createImage(Pane pane){

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/PlumbingAlveus/plumbing_alveus_250_" + id + ".png");
        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        image = new ImageView(new Image(imgURL.toString()));
        image.setFitWidth(100);
        image.setFitHeight(100);

        String brief = ProjectHandler.getPlumbingElementsList()
                .stream()
                .filter(e-> e.getId() == id)
                .collect(Collectors.toList())
                .get(0).getName();

        Label label = new Label(brief);
        label.setPrefWidth(100);
        label.setWrapText(true);

        pane.setPrefSize(120, 120);
        pane.getChildren().add(image);
        image.setTranslateX(10);
        image.setTranslateY(0);
        pane.getChildren().add(label);
        label.setTranslateY(95);
        label.setPadding(new Insets(1, 5, 1, 5));
        label.setFont(Font.font(10));
    }

    private void createTooltip(Tooltip tooltip){

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/PlumbingAlveus/plumbing_alveus_250_" + id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        ImageView tooltipImage = null;

        tooltipImage = new ImageView(new Image(imgURL.toString()));
        tooltipImage.setFitWidth(250);
        tooltipImage.setFitHeight(250);

        String brief = ProjectHandler.getPlumbingElementsList()
                .stream()
                .filter(e-> e.getId() == id)
                .collect(Collectors.toList())
                .get(0).getName();


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
