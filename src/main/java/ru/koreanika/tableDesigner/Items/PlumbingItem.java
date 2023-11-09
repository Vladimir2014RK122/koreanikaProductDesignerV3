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
import ru.koreanika.catalog.Catalogs;
import ru.koreanika.common.PlumbingElementForSale.PlumbingType;
import ru.koreanika.common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PlumbingItem extends TableDesignerItem{

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

    public PlumbingItem(int id, String model, int quantity) {

        plumbingElement = Catalogs.getPlumbingElementsList()
                .stream()
                .filter(c -> c.getId() == id)
                .collect(Collectors.toList())
                .get(0);

        this.id = id;
        this.model = model;
        this.quantity = quantity;

        name = plumbingElement.getName() + " " + model;

        size = plumbingElement.getSize(model);


        URL imgURL = getClass().getResource("/styles/images/TableDesigner/Plumbing/plumbing_250_id" + this.id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        imageMain = new Image(imgURL.toString());

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/TableItems/PlumbingRow.fxml"));
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

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/Plumbing/plumbing_250_id" + this.id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        String imgPath = "/styles/images/TableDesigner/Plumbing/plumbing_250_id" + this.id + ".png";

        imagesList.put("Plumbing#" + imgPath, new ImageView(new Image(imgURL.toString())));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerAdditionalItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            PlumbingItem.exitFromEditMode(this);
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

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * quantity) + Currency.RUR_SYMBOL);

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

    private static ChoiceBox<PlumbingType> choiceBoxType;
    private static ComboBox<PlumbingView> comboBoxView;
    private static ChoiceBox<String> choiceBoxModel;
    private static TextField textFieldCount;
    private static Label labelPrice;


    private static boolean countOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/PlumbingSettings.fxml"));

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

        System.out.println("ExternalElements (Plumbing): ");
        Catalogs.getPlumbingElementsList().forEach(System.out::println);

        choiceBoxType = (ChoiceBox<PlumbingType>) anchorPaneSettingsView.lookup("#choiceBoxType");
        comboBoxView = (ComboBox<PlumbingView>) anchorPaneSettingsView.lookup("#comboBoxView");
        choiceBoxModel = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxModel");
        textFieldCount = (TextField) anchorPaneSettingsView.lookup("#textFieldCount");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");


        comboBoxView.setCellFactory(new Callback<ListView<PlumbingView>, ListCell<PlumbingView>>() {
            @Override
            public ListCell<PlumbingView> call(ListView<PlumbingView> param) {

                ListCell<PlumbingView> cell = new ListCell<PlumbingView>() {
                    @Override
                    protected void updateItem(PlumbingView item, boolean empty) {
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
        comboBoxView.setButtonCell(new ListCell<PlumbingView>() {
            @Override
            protected void updateItem(PlumbingView item, boolean empty) {
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

        Catalogs.getPlumbingElementsList().forEach(e->{

            comboBoxView.getItems().add(new PlumbingView(e.getId()));
        });

        comboBoxView.getSelectionModel().select(0);
        comboBoxView.setTooltip(comboBoxView.getSelectionModel().getSelectedItem().getTooltip());

        int id = comboBoxView.getSelectionModel().getSelectedItem().getId();
        PlumbingElement externalElement = Catalogs.getPlumbingElementsList()
                .stream()
                .filter(e->e.getId() == id)
                .collect(Collectors.toList()).get(0);

        List<String> modelAndSize = new ArrayList<>();
        if(externalElement.getModels().size() == externalElement.getSizes().size()){
            for(int i=0;i<externalElement.getModels().size();i++){
                String ms = externalElement.getModels().get(i) + " : " + externalElement.getSizes().get(i);
                modelAndSize.add(ms);
            }
        }else{
            modelAndSize = new ArrayList<>(externalElement.getModels());
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

        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerAdditionalItemsList().size(), 1));

        choiceBoxType.setOnAction(actionEvent -> {
            PlumbingType plumbingType = choiceBoxType.getSelectionModel().getSelectedItem();

            comboBoxView.getItems().clear();

            Catalogs.getPlumbingElementsList().forEach((pe -> {
                if(pe.getPlumbingType() == plumbingType && pe.isAvailable()){
                    comboBoxView.getItems().add(new PlumbingView(pe.getId()));
                }
            }));
            comboBoxView.getSelectionModel().select(0);

        });

        comboBoxView.setOnAction(event -> {
            if (comboBoxView.getItems().size() == 0 || comboBoxView.getSelectionModel().getSelectedIndex() == -1)
                return;

            int id = comboBoxView.getSelectionModel().getSelectedItem().getId();
            PlumbingElement plumbingElement = Catalogs.getPlumbingElementsList()
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

            if (comboBoxView.getSelectionModel().getSelectedItem() != null) {
                comboBoxView.setTooltip(comboBoxView.getSelectionModel().getSelectedItem().getTooltip());
            }

            updatePriceInSettings();
        });

        choiceBoxModel.setOnAction(event -> {
            updatePriceInSettings();
        });

    }

    private static void addItem(int index, int quantity){


        int id = comboBoxView.getSelectionModel().getSelectedItem().getId();
        String model = choiceBoxModel.getSelectionModel().getSelectedItem().split(" : ")[0];

        if (countOk) {
            quantity = Integer.parseInt(textFieldCount.getText());
            if(index == -1) index = TableDesignerSession.getTableDesignerAdditionalItemsList().size();
            TableDesignerSession.getTableDesignerAdditionalItemsList().add(index, new PlumbingItem(id, model, quantity));
        }

    }

    public static void settingsControlElementsRefresh() {

        choiceBoxType.getItems().clear();
        choiceBoxType.getItems().addAll(Catalogs.getAvailablePlumbingTypes());
        choiceBoxType.getSelectionModel().select(0);

        comboBoxView.getItems().clear();

        Catalogs.getPlumbingElementsList().forEach((pe -> {
            if(pe.getPlumbingType() == choiceBoxType.getSelectionModel().getSelectedItem() && pe.isAvailable()){
                comboBoxView.getItems().add(new PlumbingView(pe.getId()));
            }
        }));

        comboBoxView.getSelectionModel().select(0);
        comboBoxView.setTooltip(comboBoxView.getSelectionModel().getSelectedItem().getTooltip());

        int id = comboBoxView.getSelectionModel().getSelectedItem().getId();
        PlumbingElement externalElement = Catalogs.getPlumbingElementsList()
                .stream()
                .filter(e->e.getId() == id)
                .collect(Collectors.toList()).get(0);

        choiceBoxModel.getItems().clear();
        List<String> modelAndSize = new ArrayList<>();
        if(externalElement.getModels().size() == externalElement.getSizes().size()){
            for(int i=0;i<externalElement.getModels().size();i++){
                String ms = externalElement.getModels().get(i) + " : " + externalElement.getSizes().get(i);
                modelAndSize.add(ms);
            }
        }else{
            modelAndSize = new ArrayList<>(externalElement.getModels());
        }


        choiceBoxModel.getItems().addAll(modelAndSize);
        choiceBoxModel.getSelectionModel().select(0);

        textFieldCount.setText("1");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (comboBoxView.getSelectionModel().getSelectedItem() == null) return;

        int id = comboBoxView.getSelectionModel().getSelectedItem().getId();
        PlumbingElement externalElement = Catalogs.getPlumbingElementsList()
                .stream()
                .filter(e->e.getId() == id)
                .collect(Collectors.toList()).get(0);


        if(choiceBoxModel.getSelectionModel().getSelectedItem() == null) return;
        String model = choiceBoxModel.getSelectionModel().getSelectedItem().split(" : ")[0];

        String currency = externalElement.getCurrency();
        String units = "шт.";
        double priceForOne = -1.0;

        priceForOne = externalElement.getPrice(model);

        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
    }

    private static void enterToEditMode(PlumbingItem plumbingItem){
        TableDesigner.openSettings(PlumbingItem.class);


        //get row data to settings

        textFieldCount.setText("" + plumbingItem.quantity);

        for(PlumbingView plumbingView : comboBoxView.getItems()){
            if(plumbingView.getId() == plumbingItem.getId()){
                comboBoxView.getSelectionModel().select(plumbingView);
                break;
            }
        }

        choiceBoxModel.getSelectionModel().select(plumbingItem.model + " : " + plumbingItem.size);


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
            int index = TableDesignerSession.getTableDesignerAdditionalItemsList().indexOf(plumbingItem);
            addItem(index, plumbingItem.quantity);

            exitFromEditMode(plumbingItem);
            plumbingItem.removeThisItem();
        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(plumbingItem);

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
        jsonObject.put("itemName", "PlumbingItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("id", id);
        jsonObject.put("model", model);


        return jsonObject;
    }

    public static PlumbingItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();
        int id = ((Long) jsonObject.get("id")).intValue();
        String model = (String) jsonObject.get("model");

        PlumbingItem plumbingItem = new PlumbingItem(id, model, quantity);
        //cutoutItem.quantity = quantity;
        //cutoutItem.labelQuantity.setText("" + quantity);

        plumbingItem.updateRowPrice();
        return plumbingItem;
    }
}




class PlumbingView {
    ImageView image;
    int id = 1;
    //Material material;

    Pane paneMain = new Pane();
    Pane paneCopy = new Pane();

    Tooltip tooltip = new Tooltip();
    Tooltip tooltipCopy = new Tooltip();


    PlumbingView(int id) {
        this.id = id;

        createImage(paneMain);
        createImage(paneCopy);

        createTooltip(tooltip);
        createTooltip(tooltipCopy);
    }

    private void createImage(Pane pane){

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/Plumbing/plumbing_250_id" + id + ".png");
        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        image = new ImageView(new Image(imgURL.toString()));
        image.setFitWidth(100);
        image.setFitHeight(100);

        String brief = Catalogs.getPlumbingElementsList()
                .stream()
                .filter(e-> e.getId() == id)
                .collect(Collectors.toList())
                .get(0).getName();

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

        URL imgURL = getClass().getResource("/styles/images/TableDesigner/Plumbing/plumbing_250_id" + id + ".png");

        if(imgURL == null){
            imgURL = getClass().getResource("/styles/images/no_img.png");
        }

        ImageView tooltipImage = null;

        tooltipImage = new ImageView(new Image(imgURL.toString()));
        tooltipImage.setFitWidth(250);
        tooltipImage.setFitHeight(250);

        String brief = Catalogs.getPlumbingElementsList()
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