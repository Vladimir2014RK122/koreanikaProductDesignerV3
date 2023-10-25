package ru.koreanika.utils.MaterialSelectionWindow;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MaterialSettings {

    Material materialTemplate;
    Material materialForAdd;

    //Scene materialSettingsScene;
    //Scene parentScene;

    AnchorPane rootAnchorPane;

    Label labelMainType, labelSubType, labelCollection,labelCurrency;
    TextField textFieldColor, textFieldPrice, textFieldWidth, textFieldHeight;
    ChoiceBox<String> choiceBoxSheetDepth;
    CheckBox checkBoxUseMainSheets, checkBoxUseAdditionalSheets;
    Button btnAddPrice, btnAddSheet, btnCancel, btnSave;
    //ListView<PriceListCell> listViewPrices;
    ListView<AdditionalSheetsListCell> listViewAdditionalSheets;

    String mainType, subType, collection, currency, color;
    int depth, price, width, height;
    boolean useMainSheets, useAdditionalSheet;
    Map<Integer, Integer> depthsAndPrices = new LinkedHashMap<>();
    ArrayList<Material.MaterialSheet> materialSheets = new ArrayList<>();

    public MaterialSettings(Material materialTemplate) {

        this.materialTemplate = materialTemplate;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/MaterialManager/materialSettings.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        labelMainType = (Label) rootAnchorPane.lookup("#labelMainType");
        labelSubType = (Label) rootAnchorPane.lookup("#labelSubType");
        labelCollection = (Label) rootAnchorPane.lookup("#labelCollection");
        labelCurrency = (Label) rootAnchorPane.lookup("#labelCurrency");
        textFieldColor = (TextField) rootAnchorPane.lookup("#textFieldColor");
        //textFieldDepth = (TextField) rootAnchorPane.lookup("#textFieldDepth");
        textFieldPrice = (TextField) rootAnchorPane.lookup("#textFieldPrice");
        textFieldWidth = (TextField) rootAnchorPane.lookup("#textFieldWidth");
        textFieldHeight = (TextField) rootAnchorPane.lookup("#textFieldHeight");
        choiceBoxSheetDepth = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxSheetDepth");
        checkBoxUseMainSheets = (CheckBox) rootAnchorPane.lookup("#checkBoxUseMainSheets");
        checkBoxUseAdditionalSheets = (CheckBox) rootAnchorPane.lookup("#checkBoxUseAdditionalSheets");
        btnAddPrice = (Button) rootAnchorPane.lookup("#btnAddPrice");
        btnAddSheet = (Button) rootAnchorPane.lookup("#btnAddSheet");
        btnCancel = (Button) rootAnchorPane.lookup("#btnCancel");
        btnSave = (Button) rootAnchorPane.lookup("#btnSave");
        //listViewPrices = (ListView<PriceListCell>) rootAnchorPane.lookup("#listViewPrices");
        listViewAdditionalSheets = (ListView<AdditionalSheetsListCell>) rootAnchorPane.lookup("#listViewAdditionalSheets");

        refreshView();
        initControlElementsLogic();
    }

    private void refreshView(){

        labelMainType.setText(materialTemplate.getMainType());
        labelSubType.setText(materialTemplate.getSubType());
        labelCollection.setText(materialTemplate.getCollection());
        textFieldColor.setText(materialTemplate.getColor());

        checkBoxUseMainSheets.setSelected(materialTemplate.isUseMainSheets());
        checkBoxUseAdditionalSheets.setSelected(materialTemplate.isUseAdditionalSheets());


        listViewAdditionalSheets.getItems().clear();
        //listViewPrices.getItems().clear();

        materialSheets = materialTemplate.getAvailableAdditionalSheets();
        depthsAndPrices = materialTemplate.getTableTopDepthsAndPrices();

        if(materialTemplate.getAvailableMainSheetsCount() == 0){
            choiceBoxSheetDepth.getItems().addAll("12", "20", "30");
        }
        else{
            choiceBoxSheetDepth.getItems().addAll(materialTemplate.getDepths());
        }
        choiceBoxSheetDepth.getSelectionModel().select(0);

        for(Material.MaterialSheet materialSheet : materialSheets){
            double sheetS = materialSheet.getSheetHeight()*materialSheet.getSheetWidth()/1000000;
            listViewAdditionalSheets.getItems().add(new AdditionalSheetsListCell(materialSheet.getSheetWidth(), materialSheet.getSheetHeight(), materialSheet.getSheetDepth(), sheetS*materialSheet.getPriceRaw(ElementTypes.TABLETOP, materialSheet.getDepth())));
        }


        double RURToUSD = MainWindow.getUSDValue().get();
        double RURToEUR = MainWindow.getEURValue().get();

        double materialCoefficient = Project.getPriceMaterialCoefficient().doubleValue();
        double commonCoefficient = Project.getPriceMainCoefficient().doubleValue();

        if(materialTemplate.getAvailableMainSheetsCount() != 0){

            for(Map.Entry<Integer, Integer> entry : depthsAndPrices.entrySet()){

                double priceForOne = materialTemplate.getRawPrice(ElementTypes.TABLETOP, entry.getKey());
                //String symbol = ReceiptManager.RUR_SYMBOL;
                // String price = String.format(Locale.ENGLISH, "%.0f", materialTemplate.getRawPrice(ElementTypes.TABLETOP, materialTemplate.getDefaultDepth()));
//            if(materialTemplate.getCurrency().equals("RUR")){}
//            else if(materialTemplate.getCurrency().equals("EUR")){price = String.format(Locale.ENGLISH, "%.0f", priceForOne * RURToEUR);}
//            else if(materialTemplate.getCurrency().equals("USD")){price = String.format(Locale.ENGLISH, "%.0f", priceForOne * RURToUSD);}

//
//                PriceListCell priceListCell = new PriceListCell(entry.getKey(), priceForOne, materialTemplate.getCurrency());
//                if(!materialTemplate.isTemplate()) priceListCell.setRemoveDisable(true);
                //listViewPrices.getItems().add(priceListCell);


            }
        }



        if(materialTemplate.isTemplate()){
            textFieldColor.setDisable(false);
        }else{
            textFieldColor.setDisable(true);
        }

        if(materialTemplate.getAvailableMainSheetsCount() == 0){
            checkBoxUseMainSheets.setSelected(false);
            checkBoxUseMainSheets.setDisable(true);

            checkBoxUseAdditionalSheets.setSelected(true);
            checkBoxUseAdditionalSheets.setDisable(true);
        }else{
            //checkBoxUseMainSheets.setSelected(true);
            checkBoxUseMainSheets.setDisable(false);
        }


    }

    private void initControlElementsLogic(){

//        btnAddPrice.setOnAction(event -> {
//            System.out.println("add price " + listViewPrices.getItems().size());
//            PriceListCell newPriceListCell = new PriceListCell(Integer.parseInt(textFieldPriceDepth.getText()), Double.parseDouble(textFieldPrice.getText()), "RUR");
//
//            //check is there have the same depth yet:
//            boolean copyFound = false;
//            for(PriceListCell plc : listViewPrices.getItems()){
//                if(plc.getDepth() == newPriceListCell.getDepth())copyFound = true;
//            }
//
//            if(!copyFound)listViewPrices.getItems().add(newPriceListCell);
//
//            //update choiceBox with depths
//            choiceBoxSheetDepth.getItems().clear();
//            for(PriceListCell plc : listViewPrices.getItems()){
//                choiceBoxSheetDepth.getItems().add("" + plc.getDepth());
//            }
//            choiceBoxSheetDepth.getSelectionModel().select(0);
//
//
//        });

        btnAddSheet.setOnAction(event -> {

            try{
                double width = Double.parseDouble(textFieldWidth.getText());
                double height = Double.parseDouble(textFieldHeight.getText());
                int depth = Integer.parseInt(choiceBoxSheetDepth.getSelectionModel().getSelectedItem());
                double price = Double.parseDouble(textFieldPrice.getText());

                listViewAdditionalSheets.getItems().add(new AdditionalSheetsListCell(width, height, depth, price));

            }catch (Exception e){
                System.out.println("ERROR TO ADD SHEET");
            }

        });

        btnSave.setOnAction(event -> {



            if(!isFieldsCorrect()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неверный ввод");
                alert.setHeaderText("Неверный ввод");
                alert.setContentText("Проверьте корректность ввода данных.");
                alert.show();
                return;
            }

            //System.out.println("TEMPLATE MATERIAL = " + materialTemplate.getName());
            ArrayList<Material> materialsForDelete = new ArrayList<>();
            materialsForDelete.add(materialTemplate);
            MaterialSelectionWindow.removeMaterialFromProjectListView(materialsForDelete);

            save();

            ArrayList<Material> materialsForAdd = new ArrayList<>();
            materialsForAdd.add(materialForAdd);

//            System.out.println("material.getTableTopDepthsAndPrices() = " + material.getTableTopDepthsAndPrices());
            //System.out.println("MATERIAL for ADD = " + materialForAdd.getName());


            MaterialSelectionWindow.addMaterialToProjectListView(materialsForAdd);

            //((Stage)(materialSettingsScene.getWindow())).close();
            MaterialSelectionWindow.getInstance().hideMaterialSettings();

            if(CutDesigner.getInstance().getCutPane() != null )CutDesigner.getInstance().getCutPane().refreshCutPaneView();
        });

        btnCancel.setOnAction(event -> {
            //((Stage)(materialSettingsScene.getWindow())).close();
            MaterialSelectionWindow.getInstance().hideMaterialSettings();
        });

    }



    public AnchorPane getView(){
        return rootAnchorPane;
    }

    private void save(){


        String color = textFieldColor.getText();
        double width = 1000;
        double height = 1000;
        String imgPath = materialTemplate.getImgPath();
        ArrayList<String> depthsList = new ArrayList<>();



        if (materialTemplate.getAvailableMainSheetsCount() == 0){
//            System.out.println("TEMPLATE = " + material);

            for(AdditionalSheetsListCell additionalSheetsListCell : listViewAdditionalSheets.getItems()){
                if(!depthsList.contains("" + additionalSheetsListCell.sheetDepth))depthsList.add("" + additionalSheetsListCell.sheetDepth);
            }

            this.materialForAdd = materialTemplate.copyMaterial(color, width, height, imgPath, depthsList);
            this.materialForAdd.setTemplate(false);

            materialForAdd.setUseMainSheets(false);
            materialForAdd.setAvailableMainSheetsCount(0);

        }else{
            materialForAdd = materialTemplate;
        }


        materialForAdd.setUseMainSheets(checkBoxUseMainSheets.isSelected());
        materialForAdd.setUseAdditionalSheets(checkBoxUseAdditionalSheets.isSelected());

        //set main list prices:
        if(materialForAdd.getAvailableMainSheetsCount() == 0){

            materialForAdd.getTableTopDepthsAndPrices().clear();
            materialForAdd.getWallPanelDepthsAndPrices().clear();
            materialForAdd.getWindowSillDepthsAndPrices().clear();
            materialForAdd.getFootDepthsAndPrices().clear();

            for(AdditionalSheetsListCell additionalSheetsListCell : listViewAdditionalSheets.getItems()){
                materialForAdd.getTableTopDepthsAndPrices().put(new Integer(additionalSheetsListCell.sheetDepth), new Integer((int)(additionalSheetsListCell.sheetPriceForMeter*100)));
                materialForAdd.getWallPanelDepthsAndPrices().put(new Integer(additionalSheetsListCell.sheetDepth), new Integer((int)(additionalSheetsListCell.sheetPriceForMeter*100)));
                materialForAdd.getWindowSillDepthsAndPrices().put(new Integer(additionalSheetsListCell.sheetDepth), new Integer((int)(additionalSheetsListCell.sheetPriceForMeter*100)));
                materialForAdd.getFootDepthsAndPrices().put(new Integer(additionalSheetsListCell.sheetDepth), new Integer((int)(additionalSheetsListCell.sheetPriceForMeter*100)));
            }
        }


        materialForAdd.getAvailableAdditionalSheets().clear();
        for(AdditionalSheetsListCell additionalSheetsListCell : listViewAdditionalSheets.getItems()){

            //System.out.println("SHEEt PRICE = " + additionalSheetsListCell.sheetPriceForMeter);

            materialForAdd.createAdditionalMaterialSheet(
                    additionalSheetsListCell.sheetDepth,
                    additionalSheetsListCell.sheetWidth,
                    additionalSheetsListCell.sheetHeight,
                    additionalSheetsListCell.sheetWidth,
                    additionalSheetsListCell.sheetHeight,
                    additionalSheetsListCell.sheetPriceForMeter,
                    additionalSheetsListCell.currency
            );
        }

//        System.out.println("material.getTableTopDepthsAndPrices() = " + material.getTableTopDepthsAndPrices());
        boolean addPostfix = false;
        String postfix = " (fragment)";
        String newColor = textFieldColor.getText();

        materialForAdd.setColor(newColor);

        String newCondName = materialForAdd.getMainType() +
                materialForAdd.getSubType() +
                materialForAdd.getCollection() +
                materialForAdd.getColor();

        for( MaterialListCellItem materialListCellItem : MaterialSelectionWindow.getListViewInProject().getItems()){

            String condName = materialListCellItem.getMaterial().getMainType() +
                    materialListCellItem.getMaterial().getSubType() +
                    materialListCellItem.getMaterial().getCollection() +
                    materialListCellItem.getMaterial().getColor();
            if(newCondName.equals(condName)){
               addPostfix = true;
            }

        }
        System.out.println("addPostfix = " + addPostfix);

        if(addPostfix) newColor += postfix;
        materialForAdd.setColor(newColor);
//        System.out.println("AFTER COPY21 = " + material);


    }

    private boolean isFieldsCorrect(){

        boolean result = true;

        if(textFieldColor.getText().equals("")) result = false;


        if(checkBoxUseMainSheets.isSelected() == false && checkBoxUseAdditionalSheets.isSelected() == false) result = false;

        if(listViewAdditionalSheets.getItems().size() == 0 && checkBoxUseAdditionalSheets.isSelected() == true) result = false;

        boolean haveNameWithPostfix = false;
        boolean errorName = false;
        boolean haveTheSameName = false;

        String newCondName = materialTemplate.getMainType() +
                materialTemplate.getSubType() +
                materialTemplate.getCollection() +
                textFieldColor.getText();

        for( MaterialListCellItem materialListCellItem : MaterialSelectionWindow.getListViewInProject().getItems()){

            String condName = materialListCellItem.getMaterial().getMainType() +
                    materialListCellItem.getMaterial().getSubType() +
                    materialListCellItem.getMaterial().getCollection() +
                    materialListCellItem.getMaterial().getColor();

            if(condName.equals(newCondName + " (fragment)")){
                haveNameWithPostfix = true;
            }
            if(newCondName.equals(condName)){
                haveTheSameName = true;
            }
        }


        //System.out.println("newCondName = " + newCondName);
        System.out.println("haveNameWithPostfix = " + haveNameWithPostfix);
        System.out.println("haveTheSameName = " + haveTheSameName);

        if(textFieldColor.getText().endsWith(" (fragment)") && !textFieldColor.isDisable())errorName = true;

        if((haveNameWithPostfix && haveTheSameName)  || errorName) result = false;


        return result;

    }
//    public void show(Scene parentScene){
//
//        this.parentScene = parentScene;
//
//        Stage materialStage = new Stage();
//        materialStage.setTitle("Редактирование материала");
//        materialStage.initOwner(parentScene.getWindow());
//        materialStage.setScene(materialSettingsScene);
//        materialStage.setX(parentScene.getWindow().getX() + parentScene.getWindow().getWidth() / 2 - materialSettingsScene.getWidth() / 2);
//        materialStage.setY(parentScene.getWindow().getY() + parentScene.getWindow().getHeight() / 2 - materialSettingsScene.getHeight() / 2);
//        materialStage.initModality(Modality.APPLICATION_MODAL);
//        materialStage.setResizable(false);
//        materialStage.show();
//    }

//    private class PriceListCell extends AnchorPane{
//
//        int depth;
//        double price;
//        String currency;
//
//        Label label = new Label();
//        Button btnRemove = new Button("X");
//
//        public PriceListCell(int depth, double price, String currency) {
//            this.depth = depth;
//            this.price = price;
//            this.currency = currency;
//
//            this.getChildren().add(label);
//            this.getChildren().add(btnRemove);
//
//            String priceStr = String.format(Locale.ENGLISH, "%.0f", price);
//
//            String symbol = ReceiptManager.RUR_SYMBOL;
//            //String priceStr = String.format(Locale.ENGLISH, "%.0f", materialTemplate.getRawPrice(ElementTypes.TABLETOP, materialTemplate.getDefaultDepth()));
//            if(materialTemplate.getCurrency().equals("RUR")){
//                symbol = ReceiptManager.RUR_SYMBOL;
//            }
//            else if(materialTemplate.getCurrency().equals("EUR")){
//                symbol = ReceiptManager.EUR_SYMBOL;
//                //price = String.format(Locale.ENGLISH, "%.0f", priceForOne * RURToEUR);
//            }
//            else if(materialTemplate.getCurrency().equals("USD")){
//                symbol = ReceiptManager.USD_SYMBOL;
//                //price = String.format(Locale.ENGLISH, "%.0f", priceForOne * RURToUSD);
//            }
//
//            label.setText("Толщина - " + depth + "мм , цена = " + price + " " + symbol);
//
//            AnchorPane.setRightAnchor(btnRemove, 10.0);
//
//            btnRemove.setOnAction(event -> {
//                //listViewPrices.getItems().remove(this);
//            });
//        }
//
//        private void setRemoveDisable(boolean disable){
//            btnRemove.setDisable(true);
//        }
//
//        public int getDepth() {
//            return depth;
//        }
//
//        public double getPrice() {
//            return price;
//        }
//    }

    private class AdditionalSheetsListCell extends AnchorPane{

        private double sheetWidth;
        private double sheetHeight;
        private double sheetPriceForMeter;
        private int sheetDepth;
        private String currency = "RUB";

        Label label = new Label();
        Button btnRemove = new Button("X");

        public AdditionalSheetsListCell(double sheetWidth, double sheetHeight, int sheetDepth, double price) {
            this.sheetWidth = sheetWidth;
            this.sheetHeight = sheetHeight;
            this.sheetDepth = sheetDepth;
            this.sheetPriceForMeter = price/((sheetWidth*sheetHeight)/1000000);

            this.getChildren().add(label);
            this.getChildren().add(btnRemove);

            this.setMinWidth(0);
            this.setPrefWidth(1);

            label.setText(String.format(Locale.ENGLISH,"Д х Ш %.0f Х %.0fмм, толщина = %dмм, цена листа = %.0f, цена за м2 = %.0f руб.", sheetWidth, sheetHeight, sheetDepth, price, sheetPriceForMeter));

            label.setPrefWidth(USE_COMPUTED_SIZE);
            label.setMaxWidth(USE_COMPUTED_SIZE);
            label.setMinWidth(USE_COMPUTED_SIZE);
            AnchorPane.setLeftAnchor(label, 5.0);
            AnchorPane.setRightAnchor(label, 60.0);

            AnchorPane.setRightAnchor(btnRemove, 10.0);

            label.getStyleClass().add("labelAddSheetName");
            btnRemove.getStyleClass().add("btnRemove");
            btnRemove.setOnAction(event -> {
                listViewAdditionalSheets.getItems().remove(this);
            });
        }
    }
}
