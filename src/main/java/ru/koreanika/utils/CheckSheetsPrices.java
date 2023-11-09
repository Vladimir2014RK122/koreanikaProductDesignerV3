package ru.koreanika.utils;

import ru.koreanika.Common.Material.Material;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.koreanika.Common.Material.MaterialSheet;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.ProjectClosedEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.currency.Currency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CheckSheetsPrices {

    public static boolean checkPrices(Material material, MaterialSheet materialSheet){
        boolean theSame = false;

        if(materialSheet.isAdditionalSheet()) return true;

        //check depths and prices:
        boolean equalsTableTopDepthAndPrices = material.getTableTopDepthsAndPrices().toString().equals(materialSheet.getTableTopDepthsAndPrices().toString());
        boolean equalsWallPanelDepthsAndPrices = material.getWallPanelDepthsAndPrices().toString().equals(materialSheet.getWallPanelDepthsAndPrices().toString());
        boolean equalsWindowSillDepthsAndPrices = material.getWindowSillDepthsAndPrices().toString().equals(materialSheet.getWindowSillDepthsAndPrices().toString());
        boolean equalsFootDepthsAndPrices = material.getFootDepthsAndPrices().toString().equals(materialSheet.getFootDepthsAndPrices().toString());

        //check coefficients:
        boolean equalsTopCoefficientList = material.getTableTopCoefficientList().toString().equals(materialSheet.getTableTopCoefficientList().toString());
        boolean equalsWallPanelCoefficientList = material.getWallPanelCoefficientList().toString().equals(materialSheet.getWallPanelCoefficientList().toString());
        boolean equalsWindowSillCoefficientList = material.getWindowSillCoefficientList().toString().equals(materialSheet.getWindowSillCoefficientList().toString());
        boolean equalsFootCoefficientList = material.getFootCoefficientList().toString().equals(materialSheet.getFootCoefficientList().toString());

        theSame = equalsTableTopDepthAndPrices &
                equalsWallPanelDepthsAndPrices &
                equalsWindowSillDepthsAndPrices &
                equalsFootDepthsAndPrices &

                equalsTopCoefficientList &
                equalsWallPanelCoefficientList &
                equalsWindowSillCoefficientList &
                equalsFootCoefficientList;

        materialSheet.setActualPrice(theSame);

        return theSame;
    }

    public static void setActualPrices(Material material, MaterialSheet materialSheet){

        //set prices:
        materialSheet.getTableTopDepthsAndPrices().clear();
        materialSheet.getWallPanelDepthsAndPrices().clear();
        materialSheet.getWindowSillDepthsAndPrices().clear();
        materialSheet.getFootDepthsAndPrices().clear();

        for(Map.Entry<Integer, Integer> entry : material.getTableTopDepthsAndPrices().entrySet()){
            materialSheet.getTableTopDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : material.getWallPanelDepthsAndPrices().entrySet()){
            materialSheet.getWallPanelDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : material.getWindowSillDepthsAndPrices().entrySet()){
            materialSheet.getWindowSillDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : material.getFootDepthsAndPrices().entrySet()){
            materialSheet.getFootDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }

        //set coefficients:
        materialSheet.getTableTopCoefficientList().clear();
        materialSheet.getWallPanelCoefficientList().clear();
        materialSheet.getWindowSillCoefficientList().clear();
        materialSheet.getFootCoefficientList().clear();

        for (Double coefficient : material.getTableTopCoefficientList()) {
            materialSheet.getTableTopCoefficientList().add(coefficient);
        }
        for (Double coefficient : material.getWallPanelCoefficientList()) {
            materialSheet.getWallPanelCoefficientList().add(coefficient);
        }
        for (Double coefficient : material.getWindowSillCoefficientList()) {
            materialSheet.getWindowSillCoefficientList().add(coefficient);
        }
        for (Double coefficient : material.getFootCoefficientList()) {
            materialSheet.getFootCoefficientList().add(coefficient);
        }
        materialSheet.setActualPrice(true);

    }

    public static void showInfoWindow(Scene mainScene, LinkedHashMap<Material, ArrayList<MaterialSheet>> differenceMap){
        new InfoWindow().showInfoWindow(mainScene, differenceMap);
    }

    private static class InfoWindow{

        Scene mainScene;
        Scene checkerScene;

        AnchorPane rootAnchorPane = null;

        Button btnUpdatePrices, btnSaveOldPrices;
        TableView<TableItem> tableView;

        LinkedHashMap<Material, ArrayList<MaterialSheet>> differenceMap;

        public void showInfoWindow(Scene mainScene, LinkedHashMap<Material, ArrayList<MaterialSheet>> differenceMap){

            this.differenceMap = differenceMap;
            this.mainScene = mainScene;

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(CheckSheetsPrices.class.getResource("/fxmls/checkerMaterialPrices.fxml"));
            try {
                rootAnchorPane = fxmlLoader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            checkerScene = new Scene(rootAnchorPane, rootAnchorPane.getPrefWidth(), rootAnchorPane.getPrefHeight());

            mainScene.getRoot().setDisable(true);

            //updateManager = getUpdateManager();

            Stage checkerStage = new Stage();
            checkerStage.setTitle("Цены на материал устарели!");
            checkerStage.initOwner(mainScene.getWindow());
            checkerStage.setScene(checkerScene);
            checkerStage.setX(mainScene.getWindow().getX() + mainScene.getWindow().getWidth() / 2 - checkerScene.getWidth() / 2);
            checkerStage.setY(mainScene.getWindow().getY() + mainScene.getWindow().getHeight() / 2 - checkerScene.getHeight() / 2);
            checkerStage.initModality(Modality.APPLICATION_MODAL);
            checkerStage.setResizable(false);
            checkerStage.show();



        initControls();
        initControlsLogic();
//        initProperties();
        }

        private void initControls(){
            tableView = (TableView<TableItem>) rootAnchorPane.lookup("#tableView");
            btnUpdatePrices = (Button) rootAnchorPane.lookup("#btnUpdatePrices");
            btnSaveOldPrices = (Button) rootAnchorPane.lookup("#btnSaveOldPrices");

            TableColumn<TableItem, Integer> tableColumnNumber = (TableColumn<TableItem, Integer>) tableView.getColumns().get(0);
            TableColumn<TableItem, String> tableColumnMaterialName = (TableColumn<TableItem, String>) tableView.getColumns().get(1);
            TableColumn<TableItem, Integer> tableColumnCount = (TableColumn<TableItem, Integer>) tableView.getColumns().get(2);
            //TableColumn<TableItem, String> tableColumnOldPrice = (TableColumn<TableItem, String>) tableView.getColumns().get(3);
            //TableColumn<TableItem, String> tableColumnNewPrice = (TableColumn<TableItem, String>) tableView.getColumns().get(4);

            tableColumnNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
            tableColumnMaterialName.setCellValueFactory(new PropertyValueFactory<>("materialName"));
            tableColumnCount.setCellValueFactory(new PropertyValueFactory<>("sheetsNumber"));
            //tableColumnOldPrice.setCellValueFactory(new PropertyValueFactory<>("oldPrice"));
            //tableColumnNewPrice.setCellValueFactory(new PropertyValueFactory<>("newPrice"));

            ObservableList<TableItem> list = FXCollections.observableList(new ArrayList<>());
            int i =1;
            for(Map.Entry<Material, ArrayList<MaterialSheet>> entry : differenceMap.entrySet()){
                int number = i++;
                String materialName = entry.getKey().getReceiptName();
                int sheetsNumber = entry.getValue().size();
                String currency = entry.getKey().getCurrency();

                if (currency.equals("USD")) currency = Currency.USD_SYMBOL;
                else if (currency.equals("EUR")) currency = Currency.EUR_SYMBOL;
                if (currency.equals("RUB")) currency = Currency.RUR_SYMBOL;


                String oldPrice = String.format(Locale.ENGLISH, "%.2f" + currency, entry.getValue().get(0).getPrice(ElementTypes.TABLETOP, entry.getKey().getDefaultDepth()));
                String newPrice = String.format(Locale.ENGLISH, "%.2f" + currency, entry.getKey().getPrice(ElementTypes.TABLETOP, entry.getKey().getDefaultDepth()));
                TableItem tableItem = new TableItem(number, materialName, sheetsNumber, oldPrice, newPrice);

                list.add(tableItem);
            }

            tableView.setItems(list);
        }

        private void initControlsLogic(){
            btnUpdatePrices.setOnMouseClicked(event -> {

                for(Map.Entry<Material, ArrayList<MaterialSheet>> entry : differenceMap.entrySet()){
                    for(MaterialSheet sheet : entry.getValue()){
                        CheckSheetsPrices.setActualPrices(entry.getKey(), sheet);
                    }
                }

                TableDesigner.updateWorkCoefficientsInCuttableItems();

                mainScene.getRoot().setDisable(false);
                ((Stage)checkerScene.getWindow()).close();
            });
            btnSaveOldPrices.setOnMouseClicked(event -> {


                mainScene.getRoot().setDisable(false);
                ((Stage)checkerScene.getWindow()).close();
            });

            ((Stage) (checkerScene.getWindow())).setOnCloseRequest(event -> {
                System.out.println("close checker");
                mainScene.getRoot().setDisable(false);

                EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);
                eventBus.fireEvent(new ProjectClosedEvent());
            });
        }

    }

    public static class TableItem{
        int number;
        String materialName;
        int sheetsNumber;
        String oldPrice;
        String newPrice;

        public TableItem(int number, String materialName, int sheetsNumber, String oldPrice, String newPrice) {
            this.number = number;
            this.materialName = materialName;
            this.sheetsNumber = sheetsNumber;
            this.oldPrice = oldPrice;
            this.newPrice = newPrice;
        }

        public int getNumber() {
            return number;
        }

        public String getMaterialName() {
            return materialName;
        }

        public int getSheetsNumber() {
            return sheetsNumber;
        }

        public String getOldPrice() {
            return oldPrice;
        }

        public String getNewPrice() {
            return newPrice;
        }
    }

}
