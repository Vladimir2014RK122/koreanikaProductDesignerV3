package ru.koreanika.tableDesigner;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.tableDesigner.Items.*;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.project.ProjectType;
import ru.koreanika.utils.receipt.ui.component.ReceiptImageItem;

import java.io.IOException;
import java.util.*;

public class TableDesigner {

    @Getter
    private AnchorPane anchorPaneRoot = null;

    private static Accordion accordionItems;
    private static AnchorPane anchorPaneMenu;
    private static ScrollPane scrollPaneItems;
    private static AnchorPane anchorPaneIntoScrollPaneItems;
    private static ScrollPane scrollPaneVBoxTable;
    private static VBox vBoxTableRowsZone;

    private static FlowPane flowPaneTableCardsZone;
    private static Label labelMainItemsHeaderCards = new Label("Основные изделия");
    private static Label labelAddItemsHeaderCards = new Label("Дополнительные изделия");
    private static Label labelMainWorksHeaderCards = new Label("Основные работы");
    private static Label labelAddWorksHeaderCards = new Label("Дополнительные работы");

    /* Menu btns: */
    private static ToggleButton btnRowsView, btnCardsView;
    private static ToggleGroup toggleGroupViewType = new ToggleGroup();

    public TableDesigner() {
        TableDesignerSession.tableDesignerMainItemsList.clear();
        TableDesignerSession.tableDesignerAdditionalItemsList.clear();
        TableDesignerSession.tableDesignerMainWorkItemsList.clear();
        TableDesignerSession.tableDesignerAdditionalWorkItemsList.clear();

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/tableDesigner.fxml"));
        try {
            anchorPaneRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (anchorPaneRoot != null) {
            initControlElements();
            initControlElementLogic();
            initAccordionItems();
        }

        TableDesignerSession.tableDesignerMainItemsList.addListener((ListChangeListener<TableDesignerItem>) c -> tableViewRefresh());
        TableDesignerSession.tableDesignerAdditionalItemsList.addListener((ListChangeListener<TableDesignerItem>) c -> tableViewRefresh());
        TableDesignerSession.tableDesignerMainWorkItemsList.addListener((ListChangeListener<TableDesignerItem>) c -> tableViewRefresh());
        TableDesignerSession.tableDesignerAdditionalWorkItemsList.addListener((ListChangeListener<TableDesignerItem>) c -> tableViewRefresh());


        MainWindow.EURValueProperty().addListener((observableValue, number, newValue) -> updatePriceInRows());
        MainWindow.USDValueProperty().addListener((observableValue, number, newValue) -> updatePriceInRows());

        Project.getPriceMainCoefficient().addListener((observable, oldValue, newValue) -> {
            TableDesigner.updatePriceInRows();

            if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
                if(Project.getDefaultMaterial() == null) return;
                //update prices in settings
                BorderItem.updatePriceInSettings();
                CutoutItem.updatePriceInSettings();
                DeliveryItem.updatePriceInSettings();
                EdgeItem.updatePriceInSettings();
                GroovesItem.updatePriceInSettings();
                JointItem.updatePriceInSettings();
                LeakGrooveItem.updatePriceInSettings();
                MeasurerItem.updatePriceInSettings();
                MetalFootingItem.updatePriceInSettings();
                PlywoodItem.updatePriceInSettings();
                RadiatorGroovesItem.updatePriceInSettings();
                RadiusItem.updatePriceInSettings();
                RodsItem.updatePriceInSettings();
                SinkItem.updatePriceInSettings();
                PlumbingItem.updatePriceInSettings();
                PalletItem.updatePriceInSettings();
                StonePolishingItem.updatePriceInSettings();
            }
        });

    }

    /**
     * RECEIPT resources START
     */
    public static ArrayList<ReceiptImageItem> getReceiptImages() {
        ArrayList<ReceiptImageItem> imagesList = new ArrayList<>();

        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(TableDesignerSession.tableDesignerMainItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerMainWorkItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalWorkItemsList);


        //checkDuplicates via image paths:
        Set<String> pathsSet = new LinkedHashSet<>();

        for (TableDesignerItem item : allItems) {
            for (Map.Entry<String, ImageView> entry : item.getMainImageView().entrySet()) {

                if (!pathsSet.contains(entry.getKey().split("#")[1])) {
                    pathsSet.add(entry.getKey().split("#")[1]);
                    imagesList.add(new ReceiptImageItem(entry.getKey().split("#")[0], entry.getValue()));
                }

            }
        }

        return imagesList;
    }

    /** RECEIPT resources END */

    /**
     * MAIN TABLE part
     */

    AnchorPane anchorPaneMainHeader = null;
    AnchorPane anchorPaneAdditionalFeaturesHeader = null;
    AnchorPane anchorPaneMainWorkHeader = null;
    AnchorPane anchorPaneAdditionalWorkHeader = null;


    private void tableViewRefresh() {

        if(toggleGroupViewType.getSelectedToggle() == btnRowsView){
            showRowsView();
        }else{
            showCardsView();
        }

    }

    private static AnchorPane getDivider() {
        AnchorPane divider = new AnchorPane();
        divider.setMinHeight(25);
        return divider;
    }

    public static void exitAllItemsFromEditMode(){

        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(TableDesignerSession.tableDesignerMainItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerMainWorkItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalWorkItemsList);

        for(TableDesignerItem item : allItems){
            if(item instanceof StoneProductItem) item.exitEditMode();
        }
    }

    public static void updateMaterialsInProject() {
        System.out.println();

        exitAllItemsFromEditMode();



        MainWindow.getTableDesigner().accordionItemsRefreshSettings();

        //check all items and delete items with deleted material
        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(TableDesignerSession.tableDesignerMainItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerMainWorkItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalWorkItemsList);

        for (TableDesignerItem item : allItems) {
            if (item instanceof DependOnMaterial mItem) {
                mItem.autoUpdateMaterial();
            }
        }
    }

    public static void updateWorkCoefficientsInCuttableItems(){
        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(TableDesignerSession.tableDesignerMainItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerMainWorkItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalWorkItemsList);

        for (TableDesignerItem item : allItems) {
            if (item instanceof Cuttable mItem) {
                mItem.updateWorkCoefficient();
            }
        }
    }


    public static void updatePriceInRows() {
        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(TableDesignerSession.tableDesignerMainItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerMainWorkItemsList);
        allItems.addAll(TableDesignerSession.tableDesignerAdditionalWorkItemsList);

        if(Project.getDefaultMaterial() == null) return;
        for (TableDesignerItem item : allItems) {
            item.updateRowPrice();
        }
    }

    public static ObservableList<TableDesignerItem> getTableDesignerAllItemsList(){
        ObservableList<TableDesignerItem> list = FXCollections.observableList(new ArrayList<TableDesignerItem>());
        list.addAll(TableDesignerSession.tableDesignerMainItemsList);
        list.addAll(TableDesignerSession.tableDesignerAdditionalItemsList);
        list.addAll(TableDesignerSession.tableDesignerAdditionalWorkItemsList);
        list.addAll(TableDesignerSession.tableDesignerMainWorkItemsList);

        return list;
    }
    private void initControlElements() {

        anchorPaneMenu = (AnchorPane) anchorPaneRoot.lookup("#anchorPaneMenu");
        btnRowsView = (ToggleButton) anchorPaneRoot.lookup("#btnRowsView");
        btnCardsView = (ToggleButton) anchorPaneRoot.lookup("#btnCardsView");
        btnRowsView.setToggleGroup(toggleGroupViewType);
        btnCardsView.setToggleGroup(toggleGroupViewType);
        btnCardsView.setSelected(true);

        scrollPaneItems = (ScrollPane) anchorPaneRoot.lookup("#scrollPaneItems");
        anchorPaneIntoScrollPaneItems = (AnchorPane) scrollPaneItems.getContent();

        accordionItems = (Accordion) anchorPaneIntoScrollPaneItems.lookup("#accordionItems");

        scrollPaneVBoxTable = (ScrollPane) anchorPaneRoot.lookup("#scrollPaneVBoxTable");
        scrollPaneVBoxTable.setFitToWidth(true);

        vBoxTableRowsZone = (VBox) scrollPaneVBoxTable.getContent();

        flowPaneTableCardsZone = new FlowPane();
        flowPaneTableCardsZone.setId("flowPaneCardsRoot");
    }

    private void initControlElementLogic() {
        toggleGroupViewType.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null){
                oldValue.setSelected(true);
                return;
            }

            if(newValue == btnRowsView){
                //showRows
                showRowsView();
            }else{
                //show cards
                showCardsView();
            }
        });

        scrollPaneItems.heightProperty().addListener((observable, oldValue, newValue) -> {
            anchorPaneIntoScrollPaneItems.setPrefHeight(newValue.doubleValue());
        });

        scrollPaneVBoxTable.widthProperty().addListener((observable, oldValue, newValue) -> {

            vBoxTableRowsZone.setPrefWidth(newValue.doubleValue());

            flowPaneTableCardsZone.setPrefWidth(newValue.doubleValue());
            labelMainItemsHeaderCards.setPrefWidth(newValue.doubleValue()-40);
            labelAddItemsHeaderCards.setPrefWidth(newValue.doubleValue()-40);
            labelMainWorksHeaderCards.setPrefWidth(newValue.doubleValue()-40);
            labelAddWorksHeaderCards.setPrefWidth(newValue.doubleValue()-40);

        });
    }

    private void showRowsView(){
        scrollPaneVBoxTable.setContent(vBoxTableRowsZone);

        vBoxTableRowsZone.setId("vBoxTableRowsZone");
        vBoxTableRowsZone.getChildren().clear();

        int rowNumber = 1;

        //createMainHeader:
        {
            if (anchorPaneMainHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/MainHeader.fxml")
                );

                try {
                    anchorPaneMainHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneMainHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneMainHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneMainHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneMainHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneMainHeader.lookup("#labelMaterial");
                Label labelDepth = (Label) anchorPaneMainHeader.lookup("#labelDepth");
                Label labelWidth = (Label) anchorPaneMainHeader.lookup("#labelWidth");
                Label labelHeight = (Label) anchorPaneMainHeader.lookup("#labelHeight");
                Label labelQuantity = (Label) anchorPaneMainHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneMainHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneMainHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelDepth, Priority.ALWAYS);
                HBox.setHgrow(labelWidth, Priority.ALWAYS);
                HBox.setHgrow(labelHeight, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);
            }
            if (TableDesignerSession.tableDesignerMainItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(anchorPaneMainHeader);
        }
        //create main items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerMainItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (TableDesignerSession.tableDesignerMainItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());

        //create Additional features header:
        {
            if (anchorPaneAdditionalFeaturesHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/AdditionalFeaturesHeader.fxml")
                );

                try {
                    anchorPaneAdditionalFeaturesHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelMaterial");
                Label labelNull1 = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNull1");
                Label labelNull2 = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNull2");
                Label labelNull3 = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNull3");
                Label labelQuantity = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelNull1, Priority.ALWAYS);
                HBox.setHgrow(labelNull2, Priority.ALWAYS);
                HBox.setHgrow(labelNull3, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);
            }
            if (TableDesignerSession.tableDesignerAdditionalItemsList.size() != 0)
                vBoxTableRowsZone.getChildren().add(anchorPaneAdditionalFeaturesHeader);
        }
        //create Additional features items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerAdditionalItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (TableDesignerSession.tableDesignerAdditionalItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());

        //create Main work header:
        {
            if (anchorPaneMainWorkHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/MainWorkHeader.fxml")
                );

                try {
                    anchorPaneMainWorkHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneMainWorkHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneMainWorkHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneMainWorkHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneMainWorkHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneMainWorkHeader.lookup("#labelMaterial");
                Label labelNull1 = (Label) anchorPaneMainWorkHeader.lookup("#labelNull1");
                Label labelNull2 = (Label) anchorPaneMainWorkHeader.lookup("#labelNull2");
                Label labelNull3 = (Label) anchorPaneMainWorkHeader.lookup("#labelNull3");
                Label labelQuantity = (Label) anchorPaneMainWorkHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneMainWorkHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneMainWorkHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelNull1, Priority.ALWAYS);
                HBox.setHgrow(labelNull2, Priority.ALWAYS);
                HBox.setHgrow(labelNull3, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);
            }
            if (TableDesignerSession.tableDesignerMainWorkItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(anchorPaneMainWorkHeader);
        }
        //create Main work items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerMainWorkItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (TableDesignerSession.tableDesignerMainWorkItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());

        //create Additional work header:
        {
            if (anchorPaneAdditionalWorkHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/addWorkHeader.fxml")
                );

                try {
                    anchorPaneAdditionalWorkHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelMaterial");
                Label labelNull1 = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNull1");
                Label labelNull2 = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNull2");
                Label labelNull3 = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNull3");
                Label labelQuantity = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelNull1, Priority.ALWAYS);
                HBox.setHgrow(labelNull2, Priority.ALWAYS);
                HBox.setHgrow(labelNull3, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);
            }
            if (!TableDesignerSession.tableDesignerAdditionalWorkItemsList.isEmpty())
                vBoxTableRowsZone.getChildren().add(anchorPaneAdditionalWorkHeader);
        }
        //create Additional work items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerAdditionalWorkItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (!TableDesignerSession.tableDesignerAdditionalWorkItemsList.isEmpty()) vBoxTableRowsZone.getChildren().add(getDivider());
    }

    private void showCardsView(){

        scrollPaneVBoxTable.setContent(flowPaneTableCardsZone);
        flowPaneTableCardsZone.getChildren().clear();

        int rowNumber = 1;

        //createHeaders:
        labelMainItemsHeaderCards.setPrefHeight(55);
        labelMainItemsHeaderCards.getStyleClass().add("cards-header");
        labelAddItemsHeaderCards.setPrefHeight(55);
        labelAddItemsHeaderCards.getStyleClass().add("cards-header");
        labelMainWorksHeaderCards.setPrefHeight(55);
        labelMainWorksHeaderCards.getStyleClass().add("cards-header");
        labelAddWorksHeaderCards.setPrefHeight(55);
        labelAddWorksHeaderCards.getStyleClass().add("cards-header");


        if (TableDesignerSession.tableDesignerMainItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelMainItemsHeaderCards);
        //create main items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerMainItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }


        if (TableDesignerSession.tableDesignerAdditionalItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelAddItemsHeaderCards);
        //create Additional features items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerAdditionalItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }


        if (TableDesignerSession.tableDesignerMainWorkItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelMainWorksHeaderCards);
        //create Main work items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerMainWorkItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }


        if (TableDesignerSession.tableDesignerAdditionalWorkItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelAddWorksHeaderCards);
        //create Additional work items:
        for (TableDesignerItem item : TableDesignerSession.tableDesignerAdditionalWorkItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }

    }

    private void initAccordionItems() {
        if (accordionItems == null) {
            return;
        }

        accordionItems.getPanes().get(0).setContent(StoneProductItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(1).setContent(SinkItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(2).setContent(EdgeItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(3).setContent(BorderItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(4).setContent(RadiusItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(5).setContent(CutoutItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(6).setContent(StonePolishingItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(7).setContent(JointItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(8).setContent(LeakGrooveItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(9).setContent(RadiatorGroovesItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(10).setContent(GroovesItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(11).setContent(RodsItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(12).setContent(PlywoodItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(13).setContent(MetalFootingItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(14).setContent(MeasurerItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(15).setContent(DeliveryItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(16).setContent(MountingItem.getAnchorPaneSettingsView());

        accordionItems.getPanes().get(17).setContent(PlumbingItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(18).setContent(PalletItem.getAnchorPaneSettingsView());

        accordionItems.getPanes().get(19).setContent(CustomItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(20).setContent(DiscountItem.getAnchorPaneSettingsView());

        for (TitledPane node : accordionItems.getPanes()) {
            node.setOnMouseEntered(mouseEvent -> Main.getMainScene().setCursor(Cursor.DEFAULT));
        }

        accordionItems.expandedPaneProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue == null) {
                return;
            }

            if (newValue.getContent() == StoneProductItem.getAnchorPaneSettingsView()) StoneProductItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == SinkItem.getAnchorPaneSettingsView()) SinkItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == EdgeItem.getAnchorPaneSettingsView()) EdgeItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == BorderItem.getAnchorPaneSettingsView()) BorderItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == RadiusItem.getAnchorPaneSettingsView()) RadiusItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == CutoutItem.getAnchorPaneSettingsView()) CutoutItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == StonePolishingItem.getAnchorPaneSettingsView()) StonePolishingItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == JointItem.getAnchorPaneSettingsView()) JointItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == LeakGrooveItem.getAnchorPaneSettingsView()) LeakGrooveItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == RadiatorGroovesItem.getAnchorPaneSettingsView()) RadiatorGroovesItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == GroovesItem.getAnchorPaneSettingsView()) GroovesItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == RodsItem.getAnchorPaneSettingsView()) RodsItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == PlywoodItem.getAnchorPaneSettingsView()) PlywoodItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == MetalFootingItem.getAnchorPaneSettingsView()) MetalFootingItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == MeasurerItem.getAnchorPaneSettingsView()) MeasurerItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == DeliveryItem.getAnchorPaneSettingsView()) DeliveryItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == MountingItem.getAnchorPaneSettingsView()) MountingItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == PlumbingItem.getAnchorPaneSettingsView()) PlumbingItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == PalletItem.getAnchorPaneSettingsView()) PalletItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == CustomItem.getAnchorPaneSettingsView()) CustomItem.settingsControlElementsRefresh();
            else if (newValue.getContent() == DiscountItem.getAnchorPaneSettingsView()) DiscountItem.settingsControlElementsRefresh();
        });

    }



    public static void openSettings(Class c){
        int index = 0;
        if(c == StoneProductItem.class){index = 0;}
        else if(c == SinkItem.class){index = 1;}
        else if(c == EdgeItem.class){index = 2;}
        else if(c == BorderItem.class){index = 3;}
        else if(c == RadiusItem.class){index = 4;}
        else if(c == CutoutItem.class){index = 5;}
        else if(c == StonePolishingItem.class){index = 6;}
        else if(c == JointItem.class){index = 7;}
        else if(c == LeakGrooveItem.class){index = 8;}
        else if(c == RadiatorGroovesItem.class){index = 9;}
        else if(c == GroovesItem.class){index = 10;}
        else if(c == RodsItem.class){index = 11;}
        else if(c == PlywoodItem.class){index = 12;}
        else if(c == MetalFootingItem.class){index = 13;}
//        else if(c == SiphonItem.class){index = 14;}
        else if(c == MeasurerItem.class){index = 14;}
        else if(c == DeliveryItem.class){index = 15;}
        else if(c == MountingItem.class){index = 16;}
        else if(c == PlumbingItem.class){index = 17;}
        else if(c == PalletItem.class){index = 18;}
        else if(c == CustomItem.class){index = 19;}
        else if(c == DiscountItem.class){index = 20;}
        else return;

        accordionItems.getPanes().get(index).setExpanded(true);
    }

    public void accordionItemsRefreshSettings() {
        StoneProductItem.settingsControlElementsRefresh();
        SinkItem.settingsControlElementsRefresh();
        EdgeItem.settingsControlElementsRefresh();
        BorderItem.settingsControlElementsRefresh();
        RadiusItem.settingsControlElementsRefresh();
        CutoutItem.settingsControlElementsRefresh();
        StonePolishingItem.settingsControlElementsRefresh();
        JointItem.settingsControlElementsRefresh();
        LeakGrooveItem.settingsControlElementsRefresh();
        RadiatorGroovesItem.settingsControlElementsRefresh();
        GroovesItem.settingsControlElementsRefresh();
        RodsItem.settingsControlElementsRefresh();
        PlywoodItem.settingsControlElementsRefresh();
        MetalFootingItem.settingsControlElementsRefresh();
        MeasurerItem.settingsControlElementsRefresh();
        DeliveryItem.settingsControlElementsRefresh();
        MountingItem.settingsControlElementsRefresh();
        PlumbingItem.settingsControlElementsRefresh();
        PalletItem.settingsControlElementsRefresh();
        CustomItem.settingsControlElementsRefresh();
        DiscountItem.settingsControlElementsRefresh();
    }

    public JSONObject getJsonView() {
        System.out.println("TableDesigner.getJsonView()");
        JSONObject jsonObject = new JSONObject();

        JSONArray tableDesignerMainItemsListJSONArray = new JSONArray();
        JSONArray tableDesignerAdditionalItemsListJSONArray = new JSONArray();
        JSONArray tableDesignerMainWorkItemsListJSONArray = new JSONArray();
        JSONArray tableDesignerAdditionalWorkItemsListJSONArray = new JSONArray();

        for (TableDesignerItem item : TableDesignerSession.tableDesignerMainItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerMainItemsListJSONArray.add(item.getJsonView());
        }
        for (TableDesignerItem item : TableDesignerSession.tableDesignerAdditionalItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerAdditionalItemsListJSONArray.add(item.getJsonView());
        }
        for (TableDesignerItem item : TableDesignerSession.tableDesignerMainWorkItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerMainWorkItemsListJSONArray.add(item.getJsonView());
        }
        for (TableDesignerItem item : TableDesignerSession.tableDesignerAdditionalWorkItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerAdditionalWorkItemsListJSONArray.add(item.getJsonView());
        }

        jsonObject.put("MainItemsList", tableDesignerMainItemsListJSONArray);
        jsonObject.put("AdditionalItemsList", tableDesignerAdditionalItemsListJSONArray);
        jsonObject.put("MainWorkItemsList", tableDesignerMainWorkItemsListJSONArray);
        jsonObject.put("AdditionalWorkItemsList", tableDesignerAdditionalWorkItemsListJSONArray);

        return jsonObject;
    }

    public static TableDesigner initFromJSON(JSONObject jsonObject) {
        TableDesigner tableDesigner = new TableDesigner();

        //!!! radius item should be init after StoneProduct item because radius item depend on StoneProductItem

        JSONArray tableDesignerMainItemsListJSONArray = (JSONArray) jsonObject.get("MainItemsList");
        JSONArray tableDesignerAdditionalItemsListJSONArray = (JSONArray) jsonObject.get("AdditionalItemsList");
        JSONArray tableDesignerMainWorkItemsListJSONArray = (JSONArray) jsonObject.get("MainWorkItemsList");
        JSONArray tableDesignerAdditionalWorkItemsListJSONArray = (JSONArray) jsonObject.get("AdditionalWorkItemsList");


        for (Object obj : tableDesignerMainItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }
        }

        for (Object obj : tableDesignerAdditionalItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }
        }

        for (Object obj : tableDesignerMainWorkItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);

                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }
        }

        for (Object obj : tableDesignerAdditionalWorkItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);

                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerAdditionalItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) TableDesignerSession.getTableDesignerMainWorkItemsList().add(tableDesignerItem);
            }
        }

        return tableDesigner;
    }

}
