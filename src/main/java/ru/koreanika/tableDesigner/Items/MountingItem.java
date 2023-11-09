package ru.koreanika.tableDesigner.Items;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.UserPreferences;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.tableDesigner.TableDesignerSession;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MountingItem extends TableDesignerItem {

    Label labelRowNumber, labelName, labelNull1, labelNull2, labelPercent, labelNull3, labelQuantity, labelRowPrice;
    ImageView imageViewMain;
    Button btnPlus, btnMinus, btnDelete, btnEdit;

    int percent;
    Image imageMain;


    public MountingItem(int quantity, int percent) {
        this.percent = percent;

        this.quantity = quantity;

        imageMain = new ImageView(Project.class.getResource("/styles/images/TableDesigner/MountingItem/mountingItem.png").toString()).getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/TableItems/MountingRow.fxml"));
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

    public double getPercent() {
        return percent;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/MountingItem/mountingItem.png";
        imagesList.put("Монтаж#" + imgPath, new ImageView(getClass().getResource(imgPath).toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if(this.editModeProperty.get()){
            MountingItem.exitFromEditMode(this);
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
        labelNull1 = (Label) hBox.getChildren().get(3);
        labelNull2 = (Label) hBox.getChildren().get(4);
        labelPercent = (Label) hBox.getChildren().get(5);
        labelNull3 = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");

        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelNull2, Priority.ALWAYS);
        HBox.setHgrow(labelPercent, Priority.ALWAYS);
        HBox.setHgrow(labelNull3, Priority.ALWAYS);
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

        btnPlus.setDisable(true);
        btnMinus.setDisable(true);

        labelRowNumber.setText("");
        labelName.setText("Монтаж");
        imageViewMain.setImage(imageMain);

        labelNull1.setText("");
        labelNull2.setText("");
        labelPercent.setText("" + percent + "%");
        labelNull3.setText("");

        labelQuantity.setText("" + quantity);



        btnPlusCard.setDisable(true);
        btnMinusCard.setDisable(true);

        labelHeaderCard.setText("Монтаж");
        tooltipNameCard.setText("Монтаж");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);



        labelName1Card.setText("Материал");
        labelValue1Card.setText("-");

        labelName2Card.setText("Соотношение");
        labelValue2Card.setText("" + percent + " %");

        labelName3Card.setText("Ширина");
        labelValue3Card.setText("-");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {
        labelRowPrice.setText("-");

        labelPriceForOneCard.setText("-");
        labelPriceForAllCard.setText("-");
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

    private static TextField textFieldPercent;
    private static Label labelPrice;

    private static boolean percentOk = true;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(EdgeItem.class.getResource("/fxmls/TableDesigner/TableItems/MountingSettings.fxml"));

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

        textFieldPercent = (TextField) anchorPaneSettingsView.lookup("#textFieldPercent");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        textFieldPercent.setText("10");

        labelPrice.setText("Цена: по счету");

    }

    private static void settingsControlElementsLogicInit() {

        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerMainWorkItemsList().size(), 1));

        textFieldPercent.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldPercent.setStyle("-fx-text-fill:red");
                percentOk = false;
                return;
            }
            textFieldPercent.setStyle("-fx-text-fill:#A8A8A8");
            percentOk = true;
        });

    }

    private static void addItem(int index, int quantity){
        if (!(percentOk)) return;
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof MountingItem) {
                InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Не более одного элемента этого типа!", null);
                return;
            }
        }

        int percent = 0;
        try {
            percent = Integer.parseInt(textFieldPercent.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        TableDesignerSession.getTableDesignerMainWorkItemsList().add(index, new MountingItem(quantity, percent));
    }

    public static void settingsControlElementsRefresh() {
        if (UserPreferences.getInstance().getSelectedApp() == AppType.ZETTA) {
            textFieldPercent.setText("5");
        } else {
            textFieldPercent.setText("10");
        }
    }

    private static void enterToEditMode(MountingItem mountingItem){
        TableDesigner.openSettings(MountingItem.class);

        //get row data to settings
        textFieldPercent.setText("" + mountingItem.percent);

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

            int index = TableDesignerSession.getTableDesignerMainWorkItemsList().indexOf(mountingItem);
            mountingItem.removeThisItem();
            addItem(index, mountingItem.quantity);

            exitFromEditMode(mountingItem);


        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(mountingItem);

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
        jsonObject.put("itemName", "MountingItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("percent", percent);


        return jsonObject;
    }

    public static MountingItem initFromJSON(JSONObject jsonObject) {

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        int percent = ((Long) jsonObject.get("percent")).intValue();

        MountingItem mountingItem = new MountingItem(quantity, percent);
        //mountingItem.quantity = quantity;
        mountingItem.labelQuantity.setText("" + quantity);
        mountingItem.updateRowPrice();
        return mountingItem;
    }
}
