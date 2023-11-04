package ru.koreanika.utils.receipt.builder;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.json.simple.JSONObject;
import ru.koreanika.PortalClient.UserEventHandler.UserEventService;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.ProjectType;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.Receipt;
import ru.koreanika.utils.receipt.ReceiptItem;
import ru.koreanika.utils.receipt.controller.ReceiptManager;

import java.util.Locale;
import java.util.Map;

public class TableReceiptNodeBuilder extends BaseTableReceiptNodeBuilder {

    public TableReceiptNodeBuilder(ReceiptManager receiptManager) {
        super(receiptManager);
    }

    @Override
    public void createMeasuringPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getMeasurerReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, Currency.RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    @Override
    public void createDeliveryPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getDeliveryReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, Currency.RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    @Override
    public void createMountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getMountingReceiptList()) {
            int rowIndex = addRowToGridPaneTop();
            //label labelMountName
            {
                Label labelMountName = new Label(receiptItem.getName());
                labelMountName.setAlignment(Pos.CENTER_LEFT);
                labelMountName.getStyleClass().add("labelProduct");
                labelMountName.getStyleClass().add("labelProduct-right");
                labelMountName.setMaxWidth(Double.MAX_VALUE);
                labelMountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            }

            //label labelMountPercent
            {
                Label labelMountPercent = new Label();
                labelMountPercent.setText(receiptItem.getPriceForOne() + "%");
                labelMountPercent.getStyleClass().add("labelProduct");
                labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            }

            //label labelMountPrice
            {
                double price = ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0);
                if (price * RUBtoUSD < 4000) price = 4000 / RUBtoUSD;

                Label labelMountPrice = new Label();
                labelMountPrice.setText(Currency.RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                addPriceUSD += price;
            }
        }

        receiptManager.allPriceForUSD += addPriceUSD;
    }

    @Override
    public void createDiscountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getDiscountReceiptList()) {
            int rowIndex = addRowToGridPaneTop();
            //label labelMountName
            {
                Label labelMountName = new Label(receiptItem.getName());
                labelMountName.setAlignment(Pos.CENTER_LEFT);
                labelMountName.getStyleClass().add("labelProduct");
                labelMountName.getStyleClass().add("labelProduct-right");
                labelMountName.setMaxWidth(Double.MAX_VALUE);
                labelMountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            }

            //label labelMountPercent
            {
                Label labelMountPercent = new Label();
                labelMountPercent.setText(receiptItem.getPriceForOne() + "%");
                labelMountPercent.getStyleClass().add("labelProduct");
                labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            }

            //label labelMountPrice
            {
                double price = -1 * (((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

                Label labelMountPrice = new Label();
                labelMountPrice.setText(Currency.RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                addPriceUSD += price;
            }
        }

        receiptManager.allPriceForUSD += addPriceUSD;
    }

    @Override
    public void createResultPart() {
        double forEventMaterialCoeff = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
        double forEventMainCoeff = ProjectHandler.getPriceMainCoefficient().doubleValue();
        double forEventStonePriceRUR = 0;
        double forEventAddPriceRUR = 0;
        double forEventResultPriceRUR = 0;

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        int rowIndex = addRowToGridPaneTop();

        /** Additional work price*/
        {
            //label labelAdditionalFeatureName
            {
                Label labelAdditionalAllPriceName = new Label("  Итого стоимость дополнительных работ");
                labelAdditionalAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAdditionalAllPriceName.getStyleClass().add("labelTableResult");
                labelAdditionalAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
            }

            //label labelAdditionalAllPrice
            {
                double price = receiptManager.allAddPriceForRUR + (receiptManager.allAddPriceForUSD * RUBtoUSD) + ((receiptManager.allAddPriceForEUR * RUBtoEUR));//in RUR
                Label labelAdditionalAllPrice = new Label();
                labelAdditionalAllPrice.setText(Currency.RUR_SYMBOL + formatPrice(price));
                labelAdditionalAllPrice.getStyleClass().add("labelTableResultPrice");
                labelAdditionalAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);

                forEventAddPriceRUR = price;
            }
        }

        System.out.println("all  price in RUR = " + (receiptManager.allPriceForRUR + receiptManager.allPriceForUSD * RUBtoUSD + receiptManager.allPriceForEUR * RUBtoEUR));
        System.out.println("all add price in RUR = " + (receiptManager.allAddPriceForRUR + receiptManager.allAddPriceForUSD * RUBtoUSD + receiptManager.allAddPriceForEUR * RUBtoEUR));

        System.out.println("all product prices EUR = " + receiptManager.allPriceForEUR);
        System.out.println("all product prices USD = " + receiptManager.allPriceForUSD);
        System.out.println("all product prices RUR = " + receiptManager.allPriceForRUR);

        /** All product price in USD */
        {
            //label labelAdditionalFeatureName
            {
                Label labelAllPriceName = new Label("  Итого сумма по заказу в долларах США");
                labelAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAllPriceName.setId("labelAllPriceName");
                labelAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPriceName, Priority.ALWAYS);
            }

            //label labelAllPrice
            {
                double price = (receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD);
                Label labelAllPrice = new Label();
                labelAllPrice.setText(Currency.USD_SYMBOL + formatPrice(price));
                labelAllPrice.setId("labelAllPrice");
                labelAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPrice, Priority.ALWAYS);
            }
        }

        /** MOUNT */
        {
            if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
                RowConstraints row2 = new RowConstraints(40);
                receiptManager.gridPaneTop.getRowConstraints().add(row2);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                //label labelMountName
                {
                    Label labelMountName = new Label(" Монтаж изделий +10% (но не менее 4000 рублей)");
                    labelMountName.setAlignment(Pos.CENTER_LEFT);
                    labelMountName.getStyleClass().add("labelProduct");
                    labelMountName.setMaxWidth(Double.MAX_VALUE);
                    labelMountName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
                }

                //label labelMountPercent
                {
                    Label labelMountPercent = new Label();
                    labelMountPercent.setText("10%");
                    labelMountPercent.getStyleClass().add("labelProduct");
                    labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                    labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
                }

                //label labelMountPrice
                {
                    double price = (receiptManager.allPriceForRUR + (receiptManager.allPriceForUSD * RUBtoUSD) + (receiptManager.allPriceForEUR * RUBtoEUR)) * 0.1;
                    if (price * RUBtoUSD < 4000) price = 4000;//in RUR

                    Label labelMountPrice = new Label();
                    labelMountPrice.setText(Currency.USD_SYMBOL + formatPrice(price));
                    labelMountPrice.getStyleClass().add("labelProductPrice");
                    labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                    labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                    //calculate allPrice:
                    receiptManager.allPriceForUSD += price;
                }
            } else {
                createMountPartGridPaneTD();
                createDiscountPartGridPaneTD();
            }
        }

        /** result price in USD*/
        {
            //label labelResultPriceUSDName
            {
                Label labelResultPriceUSDName = new Label("  Итог по заказу в долларах США:");
                labelResultPriceUSDName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceUSDName.setId("labelResultPriceUSDName");
                labelResultPriceUSDName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSDName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSDName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSDName, Priority.ALWAYS);
            }

            //label labelResultPriceUSD
            {
                double price = (receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD);

                Label labelResultPriceUSD = new Label();
                labelResultPriceUSD.setText(Currency.USD_SYMBOL + formatPrice(price));
                labelResultPriceUSD.setId("labelResultPriceUSD");
                labelResultPriceUSD.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSD.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSD, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSD, Priority.ALWAYS);
            }
        }

        /** additional price percent for small product */
        {
            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();
            if (receiptManager.stoneItems == 0) {
                coeff = 0;
            }

            double price = (receiptManager.allStoneProductsPriceInRUR + receiptManager.allStoneProductsPriceInUSD * RUBtoUSD + receiptManager.allStoneProductsPriceInEUR * RUBtoEUR) * coeff;//in RUR

            if (coeff != 0.0) {
                RowConstraints row2 = new RowConstraints(40);
                receiptManager.gridPaneTop.getRowConstraints().add(row2);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

                //label labelMountName
                {
                    Label labelMountName = new Label(" Доплата за изделие менее 2 кв.м.");
                    labelMountName.setAlignment(Pos.CENTER_LEFT);
                    labelMountName.getStyleClass().add("labelProduct");
                    labelMountName.setMaxWidth(Double.MAX_VALUE);
                    labelMountName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
                }
                //label labelMountPercent
                {
                    Label labelMountPercent = new Label();
                    labelMountPercent.setText("");
                    labelMountPercent.getStyleClass().add("labelProduct");
                    labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                    labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
                }
                //label labelMountPrice
                {
                    Label labelMountPrice = new Label();
                    labelMountPrice.setText(String.format(Locale.ENGLISH, Currency.RUR_SYMBOL + "%.0f", price));
                    labelMountPrice.getStyleClass().add("labelProductPrice");
                    labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                    labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                    //calculate allPrice:
                    receiptManager.allPriceForUSD += price / RUBtoUSD;
                }
            }
        }

        /** STOCKS */
        double stockSizeAll = 0;

        {
            System.out.println("receipt.getMaterialStocks() = " + Receipt.getMaterialStocks());

            for (Map.Entry<String, Double> entry : Receipt.getMaterialStocks().entrySet()) {
                String stockName = "\"" + entry.getKey() + "\"";
                double stock = entry.getValue();

                RowConstraints row4 = new RowConstraints(40);
                receiptManager.gridPaneTop.getRowConstraints().add(row4);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

                //label labelResultPriceRURName
                {
                    Label labelResultPriceRURName = new Label("Скидка по акции: " + stockName);
                    labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                    labelResultPriceRURName.getStyleClass().add("labelProduct");
                    labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
                }
                //label labelResultPriceRUR
                {
                    Label labelResultPriceRUR = new Label();
                    labelResultPriceRUR.setText("- " + Currency.RUR_SYMBOL + formatPrice(stock));
                    labelResultPriceRUR.getStyleClass().add("labelProductPrice");
                    labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
                }

                stockSizeAll += stock; //in RUR
                System.out.println("STOCK SIZE ALL = " + stockSizeAll);
            }

            System.out.println("receipt.getItemStocks() = " + Receipt.getItemStocks());

            for (Map.Entry<String, Double> entry : Receipt.getItemStocks().entrySet()) {
                String stockName = "\"" + entry.getKey() + "\"";
                double stock = entry.getValue();

                RowConstraints row4 = new RowConstraints(40);
                receiptManager.gridPaneTop.getRowConstraints().add(row4);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

                //label labelResultPriceRURName
                {
                    Label labelResultPriceRURName = new Label("Скидка по акции: " + stockName);
                    labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                    labelResultPriceRURName.getStyleClass().add("labelProduct");
                    labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
                }
                //label labelResultPriceRUR
                {
                    Label labelResultPriceRUR = new Label();
                    labelResultPriceRUR.setText("- " + Currency.RUR_SYMBOL + formatPrice(stock));
                    labelResultPriceRUR.getStyleClass().add("labelProductPrice");
                    labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                    receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
                }

                stockSizeAll += stock; //in RUR
                System.out.println("STOCK SIZE ALL = " + stockSizeAll);
            }
        }

        /** result price in RUR*/
        {
            RowConstraints row4 = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(row4);
            rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
            {
                Label labelResultPriceRURName = new Label("  Стоимость в рублях действительна на день просчета:");
                labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceRURName.getStyleClass().add("labelTableResult");
                labelResultPriceRURName.getStyleClass().add("labelTableResultEnd");
                labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            }

            //label labelResultPriceRUR
            {
                double price = receiptManager.allPriceForRUR + (receiptManager.allPriceForUSD * RUBtoUSD) + (receiptManager.allPriceForEUR * RUBtoEUR);
                forEventResultPriceRUR = price;

                price -= stockSizeAll;//in RUR

                Label labelResultPriceRUR = new Label();
                labelResultPriceRUR.setText(Currency.RUR_SYMBOL + formatPrice(price));
                labelResultPriceRUR.getStyleClass().add("labelTableResultPrice");
                labelResultPriceRUR.getStyleClass().add("labelTableResultEndPrice");
                labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
            }
        }

        /** EMPTY STR */
        {
            RowConstraints row4 = new RowConstraints(1);
            receiptManager.gridPaneTop.getRowConstraints().add(row4);
            rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
            //label labelResultPriceRURName
            {
                Label labelResultPriceRURName = new Label("");
                labelResultPriceRURName.setAlignment(Pos.CENTER_RIGHT);
                labelResultPriceRURName.getStyleClass().add("labelLastRowTransparent");
                labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            }

            //label labelResultPriceRUR
            {
                Label labelResultPriceRUR = new Label();
                labelResultPriceRUR.setAlignment(Pos.CENTER_RIGHT);
                labelResultPriceRUR.setText("");
                labelResultPriceRUR.getStyleClass().add("labelLastRowTransparent");
                labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
            }
        }

        System.out.println("result prices EUR = " + receiptManager.allPriceForEUR);
        System.out.println("result product prices USD = " + receiptManager.allPriceForUSD);
        System.out.println("result product prices RUR = " + receiptManager.allPriceForRUR);

        /* if have old prices*/
        {
            if (!Receipt.pricesActual) {
                RowConstraints row5 = new RowConstraints(40);
                receiptManager.gridPaneTop.getRowConstraints().add(row5);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                Label labelPricesNotActual = new Label("Внимание! Цены на материал устарели. Необходимо согласование у персонального менеджера.");
                labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelPricesNotActual.setMaxWidth(Double.MAX_VALUE);
                labelPricesNotActual.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelPricesNotActual, Priority.ALWAYS);
                GridPane.setVgrow(labelPricesNotActual, Priority.ALWAYS);
                labelPricesNotActual.setId("labelResultPriceRUR");
                receiptManager.gridPaneTop.add(labelPricesNotActual, 0, rowIndex, 9, 1);
            }
        }

        String materials = "";

        for (String n : receiptManager.materialsForEvent) {
            materials += n + ",";
        }

        double price = (receiptManager.allStoneProductsPriceInRUR + receiptManager.allStoneProductsPriceInUSD * RUBtoUSD +
                receiptManager.allStoneProductsPriceInEUR * RUBtoEUR);//in RUR

        forEventStonePriceRUR = price;

        receiptManager.jsonObjectLastCalcEvent = new JSONObject();
        receiptManager.jsonObjectLastCalcEvent.put("type", "show receipt");
        receiptManager.jsonObjectLastCalcEvent.put("materials", materials);
        receiptManager.jsonObjectLastCalcEvent.put("materialPrice", forEventStonePriceRUR);
        receiptManager.jsonObjectLastCalcEvent.put("addPrice", forEventAddPriceRUR);
        receiptManager.jsonObjectLastCalcEvent.put("allPrice", forEventResultPriceRUR);
        receiptManager.jsonObjectLastCalcEvent.put("mainCoeff", forEventMainCoeff);
        receiptManager.jsonObjectLastCalcEvent.put("materialCoeff", forEventMaterialCoeff);
        receiptManager.jsonObjectLastCalcEvent.put("slabs", Receipt.getUsesSlabs());
        receiptManager.jsonObjectLastCalcEvent.put("productSquare", Receipt.getAllSquare());

        UserEventService.getInstance().sendEventRequest(receiptManager.jsonObjectLastCalcEvent);//materials, material prices, add price, all price
    }

}