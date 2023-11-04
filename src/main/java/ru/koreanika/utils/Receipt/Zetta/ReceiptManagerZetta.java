package ru.koreanika.utils.Receipt.Zetta;


import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.*;
import ru.koreanika.utils.Receipt.Receipt;
import ru.koreanika.utils.Receipt.ReceiptItem;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.util.*;

public class ReceiptManagerZetta extends ReceiptManager {

    public ReceiptManagerZetta() {
        super();
        rootAnchorPane.getStylesheets().clear();

        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsZetta.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerCommon.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerZetta.css").toExternalForm());
    }

    @Override
    protected void createTopPartGridPane() {
        super.createTopPartGridPane();
        topPartChildrenCount = gridPaneTop.getChildren().size();
    }

    /**
     * FOR TABLE ProjectType START KOREANIKA
     */
    @Override
    protected void createMountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getMountingReceiptList()) {
            RowConstraints row2 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row2);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelMountName
            {

                Label labelMountName = new Label(receiptItem.getName());
                labelMountName.setAlignment(Pos.CENTER_LEFT);
                labelMountName.getStyleClass().add("labelProduct-right");
                labelMountName.getStyleClass().add("labelProductPrice");
                labelMountName.setMaxWidth(Double.MAX_VALUE);
                labelMountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            }

            //label labelMountPercent
            {
                Label labelMountPercent = new Label();
                labelMountPercent.setText(receiptItem.getPriceForOne() + "%");
                labelMountPercent.getStyleClass().add("labelProductPrice");
                labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            }

            //label labelMountPrice
            {
                double price = ((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0);
                if (price * RUBtoUSD < 4000) price = 4000 / RUBtoUSD;

                Label labelMountPrice = new Label();
                //labelMountPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                //labelMountPrice.setText("$" + formatPrice(price));
                labelMountPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                addPriceUSD += price;

            }

            //calculate allPrice:
            {
                allPriceForUSD += addPriceUSD;
            }

        }
    }

    /**
     * DISCOUNT should be placed before services works
     */
    @Override
    protected void createDiscountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double discountPriceInUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getDiscountReceiptList()) {
            RowConstraints row2 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row2);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelDiscountName
            {

                Label labelDiscountName = new Label(receiptItem.getName());
                labelDiscountName.setAlignment(Pos.CENTER_LEFT);
                labelDiscountName.getStyleClass().add("labelProduct");
                labelDiscountName.setMaxWidth(Double.MAX_VALUE);
                labelDiscountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountName, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountName, Priority.ALWAYS);
                gridPaneTop.add(labelDiscountName, 0, rowIndex, 6, 1);
            }

            //label labelDiscountPercent
            {
                Label labelDiscountPercent = new Label();
                labelDiscountPercent.setText(receiptItem.getPriceForOne() + "%");
                labelDiscountPercent.getStyleClass().add("labelProduct");
                labelDiscountPercent.setMaxWidth(Double.MAX_VALUE);
                labelDiscountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountPercent, Priority.ALWAYS);
                gridPaneTop.add(labelDiscountPercent, 6, rowIndex, 1, 1);
            }

            //label labelDiscountPrice
            {

                System.out.println("price part in RUR = " + allPriceForRUR);
                System.out.println("price part in USD = " + allPriceForUSD);
                System.out.println("price part in EUR = " + allPriceForEUR);
                System.out.println("ALL PRICE ID USD =" + ((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)));

                double price = -1 * (((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

                Label labelDiscountPrice = new Label();
                //labelDiscountPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                //labelDiscountPrice.setText("$" + formatPrice(price));
                labelDiscountPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelDiscountPrice.getStyleClass().add("labelProductPrice");
                labelDiscountPrice.setMaxWidth(Double.MAX_VALUE);
                labelDiscountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountPrice, Priority.ALWAYS);
                gridPaneTop.add(labelDiscountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                {
                    //ReceiptItem receiptItem = entry.getValue();
                    discountPriceInUSD = price;

                }

            }
        }

        allPriceForUSD += discountPriceInUSD;
    }

    /* Services works: (START)*/
    double servicesWorksPriceInUSD = 0;
    @Override
    protected void createDeliveryPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getDeliveryReceiptList()) {

            int rowIndex;

            //Cutout cutout = entry.getKey();
            //Material material = cutout.getSketchShapeOwner().getMaterial();
            //ReceiptItem receiptItem = entry.getValue();

            Label labelDeliveryValueName = new Label(receiptItem.getName());
            Label labelDeliveryValueSubName = new Label("");
            Label labelDeliveryPrice = new Label(receiptItem.getPriceForOne());
            Label labelDeliveryCount = new Label(receiptItem.getCount());
            Label labelDeliveryResultPrice = new Label(receiptItem.getAllPrice());

            receiptItem.setCoefficient(coefficient);

            String currency = "*";
            if (receiptItem.getCurrency().equals("USD")) currency = USD_SYMBOL;
            else if (receiptItem.getCurrency().equals("EUR")) currency = EUR_SYMBOL;
            if (receiptItem.getCurrency().equals("RUB")) currency = RUR_SYMBOL;


            labelDeliveryValueName.setText(receiptItem.getName());

            labelDeliveryPrice.setText(currency + receiptItem.getPriceForOne());
            labelDeliveryCount.setText(receiptItem.getCount());
            labelDeliveryResultPrice.setText(currency + receiptItem.getAllPrice());

            RowConstraints rowForEdge = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(rowForEdge);

            //labelDeliveryValueName:
            {
                labelDeliveryValueName.getStyleClass().add("labelProduct");
                labelDeliveryValueName.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryValueName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryValueName, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryValueName, Priority.ALWAYS);
                labelDeliveryValueName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryValueName, 0, rowIndex, 5, 1);
            }
            //labelDeliveryValueSubName:
            {
                labelDeliveryValueSubName.getStyleClass().add("labelProduct");
                labelDeliveryValueSubName.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryValueSubName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryValueSubName, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryValueSubName, Priority.ALWAYS);
                labelDeliveryValueSubName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelDeliveryValueSubName, 2, rowIndex, 2, 1);
            }

            //labelDeliveryNull1:
            {
                Label labelDeliveryNull1 = new Label();
                labelDeliveryNull1.getStyleClass().add("labelProduct");
                labelDeliveryNull1.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryNull1.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryNull1, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryNull1, Priority.ALWAYS);
                labelDeliveryNull1.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelEdgeNull1, 3, rowIndex, 1, 1);
            }
            //labelDeliveryNull2:
            {
                Label labelDeliveryNull2 = new Label();
                labelDeliveryNull2.getStyleClass().add("labelProduct");
                labelDeliveryNull2.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryNull2.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryNull2, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryNull2, Priority.ALWAYS);
                labelDeliveryNull2.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelDeliveryNull2, 4, rowIndex, 1, 1);
            }
            //labelDeliveryInches:
            {
                Label labelDeliveryInches = new Label(receiptItem.getUnits());
                labelDeliveryInches.getStyleClass().add("labelProduct");
                labelDeliveryInches.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryInches.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryInches, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryInches, Priority.ALWAYS);
                labelDeliveryInches.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryInches, 5, rowIndex, 1, 1);
            }
            //labelDeliveryPrice:
            {
                labelDeliveryPrice.getStyleClass().add("labelProduct");
                labelDeliveryPrice.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryPrice, Priority.ALWAYS);
                labelDeliveryPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryPrice, 6, rowIndex, 1, 1);
            }
            //labelDeliveryCount:
            {
                labelDeliveryCount.getStyleClass().add("labelProduct");
                labelDeliveryCount.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryCount, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryCount, Priority.ALWAYS);
                labelDeliveryCount.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryCount, 7, rowIndex, 1, 1);
            }
            //labelDeliveryResultPrice:
            {
                labelDeliveryResultPrice.getStyleClass().add("labelProductPrice");
                labelDeliveryResultPrice.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryResultPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryResultPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryResultPrice, Priority.ALWAYS);
                labelDeliveryResultPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryResultPrice, 8, rowIndex, 1, 1);
            }

            //calculate servicesWorksPriceInUSD
            {
                if (receiptItem.getCurrency().equals("USD")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                } else if (receiptItem.getCurrency().equals("EUR")) {
                    servicesWorksPriceInUSD += (Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) * RUBtoEUR) / RUBtoUSD;
                } else if (receiptItem.getCurrency().equals("RUB")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) / RUBtoUSD;
                }
            }
            //calculate allPrice:
//            {
//                //ReceiptItem receiptItem = entry.getValue();
//                if (receiptItem.getCurrency().equals("USD"))
//                    allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                else if (receiptItem.getCurrency().equals("EUR"))
//                    allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                if (receiptItem.getCurrency().equals("RUB"))
//                    allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//            }

            //calculate allAddPrice:
//            {
//                //ReceiptItem receiptItem = entry.getValue();
//                if (receiptItem.getCurrency().equals("USD"))
//                    allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                else if (receiptItem.getCurrency().equals("EUR"))
//                    allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                if (receiptItem.getCurrency().equals("RUB"))
//                    allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//            }

        }
    }
    @Override
    protected void createMeasuringPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getMeasurerReceiptList()) {

            int rowIndex;

            //Cutout cutout = entry.getKey();
            //Material material = cutout.getSketchShapeOwner().getMaterial();
            //ReceiptItem receiptItem = entry.getValue();

            Label labelMeasuringValueName = new Label(receiptItem.getName());
            Label labelMeasuringValueSubName = new Label("");
            Label labelMeasuringPrice = new Label(receiptItem.getPriceForOne());
            Label labelMeasuringCount = new Label(receiptItem.getCount());
            Label labelMeasuringResultPrice = new Label(receiptItem.getAllPrice());

            receiptItem.setCoefficient(coefficient);

            String currency = "*";
            if (receiptItem.getCurrency().equals("USD")) currency = USD_SYMBOL;
            else if (receiptItem.getCurrency().equals("EUR")) currency = EUR_SYMBOL;
            if (receiptItem.getCurrency().equals("RUB")) currency = RUR_SYMBOL;


            labelMeasuringValueName.setText(receiptItem.getName());

            labelMeasuringPrice.setText(currency + receiptItem.getPriceForOne());
            labelMeasuringCount.setText(receiptItem.getCount());
            labelMeasuringResultPrice.setText(currency + receiptItem.getAllPrice());

            RowConstraints rowForEdge = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(rowForEdge);

            //labelMeasuringValueName:
            {
                labelMeasuringValueName.getStyleClass().add("labelProduct");
                labelMeasuringValueName.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringValueName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringValueName, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringValueName, Priority.ALWAYS);
                labelMeasuringValueName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringValueName, 0, rowIndex, 5, 1);
            }
            //labelMeasuringValueSubName:
            {
                labelMeasuringValueSubName.getStyleClass().add("labelProduct");
                labelMeasuringValueSubName.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringValueSubName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringValueSubName, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringValueSubName, Priority.ALWAYS);
                labelMeasuringValueSubName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelMeasuringValueSubName, 2, rowIndex, 2, 1);
            }

            //labelMeasuringNull1:
            {
                Label labelMeasuringNull1 = new Label();
                labelMeasuringNull1.getStyleClass().add("labelProduct");
                labelMeasuringNull1.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringNull1.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringNull1, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringNull1, Priority.ALWAYS);
                labelMeasuringNull1.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelEdgeNull1, 3, rowIndex, 1, 1);
            }
            //labelMeasuringNull2:
            {
                Label labelMeasuringNull2 = new Label();
                labelMeasuringNull2.getStyleClass().add("labelProduct");
                labelMeasuringNull2.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringNull2.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringNull2, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringNull2, Priority.ALWAYS);
                labelMeasuringNull2.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelMeasuringNull2, 4, rowIndex, 1, 1);
            }
            //labelMeasuringNull2:
            {
                Label labelMeasuringInches = new Label(receiptItem.getUnits());
                labelMeasuringInches.getStyleClass().add("labelProduct");
                labelMeasuringInches.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringInches.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringInches, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringInches, Priority.ALWAYS);
                labelMeasuringInches.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringInches, 5, rowIndex, 1, 1);
            }
            //labelMeasuringPrice:
            {
                labelMeasuringPrice.getStyleClass().add("labelProduct");
                labelMeasuringPrice.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringPrice, Priority.ALWAYS);
                labelMeasuringPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringPrice, 6, rowIndex, 1, 1);
            }
            //labelMeasuringCount:
            {
                labelMeasuringCount.getStyleClass().add("labelProduct");
                labelMeasuringCount.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringCount, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringCount, Priority.ALWAYS);
                labelMeasuringCount.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringCount, 7, rowIndex, 1, 1);
            }
            //labelMeasuringResultPrice:
            {
                labelMeasuringResultPrice.getStyleClass().add("labelProductPrice");
                labelMeasuringResultPrice.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringResultPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringResultPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringResultPrice, Priority.ALWAYS);
                labelMeasuringResultPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringResultPrice, 8, rowIndex, 1, 1);
            }

            //calculate servicesWorksPriceInUSD
            {
                if (receiptItem.getCurrency().equals("USD")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                } else if (receiptItem.getCurrency().equals("EUR")) {
                    servicesWorksPriceInUSD += (Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) * RUBtoEUR) / RUBtoUSD;
                } else if (receiptItem.getCurrency().equals("RUB")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) / RUBtoUSD;
                }
            }

        }

    }

    @Override
    protected void createResultPart() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        createDiscountPartGridPaneTD();

        /** All product price in USD */
        {
            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);

            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelAdditionalFeatureName
            {

                Label labelAllPriceName = new Label("  Итого стоимость изделия в долларах США");
                labelAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAllPriceName.getStyleClass().add("labelTableResult");
                labelAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelAllPriceName, 0, rowIndex, 7, 1);
            }

            //label labelAllPrice
            {
                double price = (allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD);
                Label labelAllPrice = new Label();
                //labelAllPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                labelAllPrice.setText(USD_SYMBOL + formatPrice(price));
                //labelAllPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelAllPrice.getStyleClass().add("labelTableResultPrice");
                labelAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPrice, Priority.ALWAYS);
                gridPaneTop.add(labelAllPrice, 7, rowIndex, 2, 1);
            }
        }

        /** Services works*/
        {
            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);

            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelServicesWorksName
            {

                Label labelServicesWorksName = new Label("Сервисные услуги");
                labelServicesWorksName.setAlignment(Pos.CENTER_LEFT);
                labelServicesWorksName.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksName.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksName, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksName, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksName, 0, rowIndex, 5, 1);
            }

            //label labelServicesWorksInches
            {
                Label labelServicesWorksInches = new Label("Ед.");
                labelServicesWorksInches.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksInches.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksInches.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksInches, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksInches, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksInches, 5, rowIndex, 1, 1);
            }
            //label labelServicesWorksCost
            {
                Label labelServicesWorksCost = new Label("Цена");
                labelServicesWorksCost.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksCost.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksCost.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksCost, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksCost, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksCost, 6, rowIndex, 1, 1);
            }
            //label labelServicesWorksCount
            {
                Label labelServicesWorksCount = new Label("Кол-во");
                labelServicesWorksCount.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksCount.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksCount, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksCount, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksCount, 7, rowIndex, 1, 1);
            }
            //label labelServicesWorksPrice
            {
                Label labelServicesWorksPrice = new Label("Стоимость");
                labelServicesWorksPrice.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksPrice.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksPrice, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksPrice, 8, rowIndex, 1, 1);
            }

        }

        servicesWorksPriceInUSD = 0;


        createDeliveryPartGridPaneTD();
        createMeasuringPartGridPaneTD();

        /** Services work price*/
        {
            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);

            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelServicesWorksPriceName
            {

                Label labelServicesWorksPriceName = new Label("  Итого стоимость Сервисных услуг в долларах США");
                labelServicesWorksPriceName.setAlignment(Pos.CENTER_LEFT);
                labelServicesWorksPriceName.getStyleClass().add("labelTableResult");
                labelServicesWorksPriceName.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksPriceName, 0, rowIndex, 7, 1);
            }

            //label labelServicesWorksPriceValue
            {
                Label labelServicesWorksPriceValue = new Label();
                //labelAdditionalAllPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                labelServicesWorksPriceValue.setText(USD_SYMBOL + formatPrice(servicesWorksPriceInUSD));
                //labelServicesWorksPriceValue.setText(RUR_SYMBOL + formatPrice(servicesWorksPriceInUSD * RUBtoUSD));
                labelServicesWorksPriceValue.getStyleClass().add("labelTableResultPrice");
                labelServicesWorksPriceValue.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksPriceValue.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksPriceValue, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksPriceValue, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksPriceValue, 7, rowIndex, 2, 1);

            }

        }


        System.out.println("all  price in RUR = " + (allPriceForRUR + allPriceForUSD * RUBtoUSD + allPriceForEUR * RUBtoEUR));
        System.out.println("all add price in RUR = " + (allAddPriceForRUR + allAddPriceForUSD * RUBtoUSD + allAddPriceForEUR * RUBtoEUR));

        /** MOUNT */
        {
            createMountPartGridPaneTD();
        }

        allPriceForUSD += servicesWorksPriceInUSD;

        /** result price in USD*/
        {
            RowConstraints row3 = new RowConstraints(40);
            //gridPaneTop.getRowConstraints().add(row3);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelResultPriceUSDName
            {

                Label labelResultPriceUSDName = new Label("  Итог по заказу в долларах США:");
                labelResultPriceUSDName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceUSDName.setId("labelResultPriceUSDName");
                labelResultPriceUSDName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSDName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSDName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSDName, Priority.ALWAYS);
                //gridPaneTop.add(labelResultPriceUSDName, 0, rowIndex, 7, 1);
            }


            //label labelResultPriceUSD
            {
                double price = (allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD);

                Label labelResultPriceUSD = new Label();
                //labelResultPriceUSD.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                labelResultPriceUSD.setText(USD_SYMBOL + formatPrice(price));
                labelResultPriceUSD.setId("labelResultPriceUSD");
                labelResultPriceUSD.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSD.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSD, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSD, Priority.ALWAYS);
                //gridPaneTop.add(labelResultPriceUSD, 7, rowIndex, 2, 1);
            }
        }

        /** additional price percent for small product */

        {

            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();

            if(stoneItems == 0){
                coeff = 0;
            }

            double price = (allPriceForRUR + allPriceForUSD * RUBtoUSD + allPriceForEUR * RUBtoEUR) * coeff;//in RUR
            if (coeff != 0.0) {

                RowConstraints row2 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row2);
                int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //label labelSmallProductName
                {

                    Label labelSmallProductName = new Label(" Доплата за изделие менее 2 кв.м.");
                    //labelSmallProductName.setText(receiptItem.getName());
                    labelSmallProductName.setAlignment(Pos.CENTER_LEFT);
                    labelSmallProductName.getStyleClass().add("labelProduct-right");
                    labelSmallProductName.getStyleClass().add("labelProduct");
                    labelSmallProductName.setMaxWidth(Double.MAX_VALUE);
                    labelSmallProductName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelSmallProductName, Priority.ALWAYS);
                    GridPane.setVgrow(labelSmallProductName, Priority.ALWAYS);
                    gridPaneTop.add(labelSmallProductName, 0, rowIndex, 6, 1);
                }

                //label labelSmallProductPercent
                {
                    Label labelSmallProductPercent = new Label();
                    //labelMountPercent.setText(Receipt.getAdditionalPricePercent() + "%");
                    labelSmallProductPercent.setText("");
                    labelSmallProductPercent.getStyleClass().add("labelProduct");
                    labelSmallProductPercent.setMaxWidth(Double.MAX_VALUE);
                    labelSmallProductPercent.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelSmallProductPercent, Priority.ALWAYS);
                    GridPane.setVgrow(labelSmallProductPercent, Priority.ALWAYS);
                    gridPaneTop.add(labelSmallProductPercent, 6, rowIndex, 1, 1);
                }

                //label labelSmallProductPrice
                {
                    //double price = receiptItem.getAllPrice();


                    Label labelSmallProductPrice = new Label();
                    //labelSmallProductPrice.setText(String.format(Locale.ENGLISH, USD_SYMBOL + "%.2f", price));
                    labelSmallProductPrice.setText(String.format(Locale.ENGLISH, RUR_SYMBOL + "%.0f", price));
                    //labelMountPrice.setText(USD_SYMBOL + price);
                    labelSmallProductPrice.getStyleClass().add("labelProductPrice");
                    labelSmallProductPrice.setMaxWidth(Double.MAX_VALUE);
                    labelSmallProductPrice.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelSmallProductPrice, Priority.ALWAYS);
                    GridPane.setVgrow(labelSmallProductPrice, Priority.ALWAYS);
                    gridPaneTop.add(labelSmallProductPrice, 7, rowIndex, 2, 1);


                    //calculate allPrice:
                    {
                        //ReceiptItem receiptItem = entry.getValue();
                        allPriceForUSD += price/RUBtoUSD;
                    }

                }
            }
        }

        /** result price in RUR*/
        {
            RowConstraints row4 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row4);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelResultPriceRURName
            {

                Label labelResultPriceRURName = new Label("  Стоимость в рублях действительна на день просчета:");
                labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceRURName.getStyleClass().add("labelTableResultEnd");
                labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            }


            //label labelResultPriceRUR
            {
                System.out.println("allPriceForRUR = " + allPriceForRUR);
                System.out.println("allPriceForUSD = " + allPriceForUSD);
                System.out.println("allPriceForEUR = " + allPriceForEUR);
                double price = ((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * RUBtoUSD;

                Label labelResultPriceRUR = new Label();
                //labelResultPriceRUR.setText(String.format(Locale.ENGLISH, "₽%.2f", price));
                labelResultPriceRUR.setText(RUR_SYMBOL + formatPrice(price));
                labelResultPriceRUR.getStyleClass().add("labelTableResultEndPrice");
                labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
            }


        }

        /** FOR CLIENT*/
        {


            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;

            //label labelText
            {
                Label labelText = new Label("С выбранным декором и конфигурацией согласен");
                //labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelText.setMaxWidth(Double.MAX_VALUE);
                labelText.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelText, Priority.ALWAYS);
                GridPane.setVgrow(labelText, Priority.ALWAYS);
                labelText.getStyleClass().add("labelProduct");
                gridPaneTop.add(labelText, 0, rowIndex, 7, 1);
            }
            //label labelField
            {
                Label labelField = new Label("");
                //labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelField.setMaxWidth(Double.MAX_VALUE);
                labelField.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelField, Priority.ALWAYS);
                GridPane.setVgrow(labelField, Priority.ALWAYS);
                labelField.getStyleClass().add("labelProduct");
                gridPaneTop.add(labelField, 7, rowIndex, 2, 1);
            }

        }

        /** NOTIFICATION if have old prices*/
        {

            if (!Receipt.pricesActual) {
                RowConstraints row5 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row5);
                int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                Label labelPricesNotActual = new Label("Внимание! Цены на материал устарели. Необходимо согласование у персонального менеджера.");
                labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelPricesNotActual.setMaxWidth(Double.MAX_VALUE);
                labelPricesNotActual.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelPricesNotActual, Priority.ALWAYS);
                GridPane.setVgrow(labelPricesNotActual, Priority.ALWAYS);
                labelPricesNotActual.setId("labelResultPriceRUR");
                gridPaneTop.add(labelPricesNotActual, 0, rowIndex, 9, 1);
            }
        }


    }

    @Override
    public void updateReceiptFull(){

        System.out.println("UPDATE RECEIPT TABLE  FULL");
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        Receipt.calculateItemsStocks();

        createTopPartGridPane();

        createMaterialsPartGridPane();


        createImagesPartGridPaneTD();

        createHeaderForAdditionalWorks();

        createSinkQuartzPartGridPaneTD();
        createEdgesAndBordersPartGridPaneTD();
        createLeakGroovePartGridPaneTD();
        createStoneHemPartGridPaneTD();

        createSinkAcrylPartGridPaneTD();
        createSinkInstallTypesPartGridPaneTD();
        createJointsPartGridPaneTD();
        createRadiusElementsPartGridPaneTD();

        // receiptManager.createAdditionalPartTopTD();
        createCutoutPartGridPaneTD();
        createPlumbingAlveusPartGridPaneTD();
        createPlumbingPartGridPaneTD();
        createPalletPartGridPaneTD();
        createGroovesPartGridPaneTD();
        createRodsPartGridPaneTD();
        //receiptManager.createCustomReceiptItemsPartGridPaneTD();

        createMetalFootingPartGridPaneTD();
        createPlywoodPartGridPaneTD();
        createStonePolishingPartGridPaneTD();
        createSiphonPartGridPaneTD();
        createCustomPartGridPaneTD();
//        createMeasuringPartGridPaneTD();
//        createDeliveryPartGridPaneTD();

        createResultPart();


        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
//        stackPaneIntoScrollPane.setPrefHeight(gridPaneHeight);

    }

    @Override
    public void updateReceiptShort(){
        System.out.println("UPDATE RECEIPT TABLE SHORT");
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        Receipt.calculateItemsStocks();

        createTopPartGridPane();
        createMaterialsPartGridPaneShort();
        createImagesPartGridPaneTD();
        createAdditionalRowShort();
        createResultPart();

        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }

    public TextField getTextFieldUSD() {
        return textFieldUSD;
    }

    public TextField getTextFieldEUR() {
        return textFieldEUR;
    }

}
