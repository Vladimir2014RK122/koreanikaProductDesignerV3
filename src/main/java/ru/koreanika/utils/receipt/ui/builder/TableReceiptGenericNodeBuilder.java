package ru.koreanika.utils.receipt.ui.builder;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import org.json.simple.JSONObject;
import ru.koreanika.PortalClient.UserEventHandler.UserEventService;
import ru.koreanika.project.Project;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.domain.Receipt;
import ru.koreanika.utils.receipt.domain.ReceiptItem;
import ru.koreanika.utils.receipt.policy.TableDesignerItemMapper;
import ru.koreanika.utils.receipt.ui.controller.ReceiptManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class TableReceiptGenericNodeBuilder extends BaseTableReceiptNodeBuilder {

    public TableReceiptGenericNodeBuilder(ReceiptManager receiptManager) {
        super(receiptManager);
    }

    @Override
    public void createMeasuringPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getMeasurerReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getDeliveryReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getMountingReceiptList()) {
            int rowIndex = addRowToGridPaneTop();

            double price = ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0);
            if (price * RUBtoUSD < 4000) {
                price = 4000 / RUBtoUSD;
            }

            Label labelMountName = buildLabel(null, receiptItem.getName(), Arrays.asList("labelProduct", "labelProduct-right"));
            Label labelMountPercent = buildLabel(null, receiptItem.getPriceForOne() + "%", "labelProduct");
            Label labelMountPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price * RUBtoUSD), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

            //calculate allPrice:
            addPriceUSD += price;
        }
        receiptManager.allPriceForUSD += addPriceUSD;
    }

    @Override
    public void createDiscountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getDiscountReceiptList()) {
            int rowIndex = addRowToGridPaneTop();

            double price = -1 * (((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) +
                    ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

            Label labelMountName = buildLabel(null, receiptItem.getName(), Arrays.asList("labelProduct", "labelProduct-right"));
            Label labelMountPercent = buildLabel(null, receiptItem.getPriceForOne() + "%", "labelProduct");
            Label labelMountPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price * RUBtoUSD), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

            //calculate allPrice:
            addPriceUSD += price;
        }
        receiptManager.allPriceForUSD += addPriceUSD;
    }

    @Override
    public void createResultPart() {
        double forEventMaterialCoeff = Project.getPriceMaterialCoefficient().doubleValue();
        double forEventMainCoeff = Project.getPriceMainCoefficient().doubleValue();
        double forEventStonePriceRUR = 0;
        double forEventAddPriceRUR = 0;
        double forEventResultPriceRUR = 0;

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        int rowIndex = addRowToGridPaneTop();

        // Additional work price
        {
            double price = receiptManager.allAddPriceForRUR + (receiptManager.allAddPriceForUSD * RUBtoUSD) + ((receiptManager.allAddPriceForEUR * RUBtoEUR));//in RUR

            Label labelAdditionalAllPriceName = buildLabel(null, "  Итого стоимость дополнительных работ", "labelTableResult");
            Label labelAdditionalAllPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price), "labelTableResultPrice");

            receiptManager.gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);

            forEventAddPriceRUR = price;
        }

        System.out.println("all  price in RUR = " + (receiptManager.allPriceForRUR + receiptManager.allPriceForUSD * RUBtoUSD + receiptManager.allPriceForEUR * RUBtoEUR));
        System.out.println("all add price in RUR = " + (receiptManager.allAddPriceForRUR + receiptManager.allAddPriceForUSD * RUBtoUSD + receiptManager.allAddPriceForEUR * RUBtoEUR));

        System.out.println("all product prices EUR = " + receiptManager.allPriceForEUR);
        System.out.println("all product prices USD = " + receiptManager.allPriceForUSD);
        System.out.println("all product prices RUR = " + receiptManager.allPriceForRUR);

        // MOUNT
        createMountPartGridPaneTD();
        createDiscountPartGridPaneTD();

        // additional price percent for small product
        {
            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();
            if (receiptManager.stoneItems == 0) {
                coeff = 0;
            }

            double price = (receiptManager.allStoneProductsPriceInRUR + receiptManager.allStoneProductsPriceInUSD * RUBtoUSD + receiptManager.allStoneProductsPriceInEUR * RUBtoEUR) * coeff;//in RUR

            if (coeff != 0.0) {
                rowIndex = addRowToGridPaneTop();

                Label labelMountName = buildLabel(null, " Доплата за изделие менее 2 кв.м.", "labelProduct");
                Label labelMountPercent = buildLabel(null, null, "labelProduct");
                Label labelMountPrice = buildLabel(null, String.format(Locale.ENGLISH, Currency.RUR_SYMBOL + "%.0f", price), "labelProductPrice");

                receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
                receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
                receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                receiptManager.allPriceForUSD += price / RUBtoUSD;
            }
        }

        /** STOCKS */
        double stockSizeAll = 0;

        {
            System.out.println("receipt.getMaterialStocks() = " + Receipt.getMaterialStocks());

            for (Map.Entry<String, Double> entry : Receipt.getMaterialStocks().entrySet()) {
                rowIndex = addRowToGridPaneTop();
                double stock = entry.getValue();

                Label labelResultPriceRURName = buildLabel(null, "Скидка по акции: \"" + entry.getKey() + "\"", "labelProduct");
                Label labelResultPriceRUR = buildLabel(null, "- " + Currency.RUR_SYMBOL + formatPrice(stock), "labelProductPrice");

                receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
                receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);

                stockSizeAll += stock; // in RUR
                System.out.println("STOCK SIZE ALL = " + stockSizeAll);
            }

            System.out.println("receipt.getItemStocks() = " + Receipt.getItemStocks());

            for (Map.Entry<String, Double> entry : Receipt.getItemStocks().entrySet()) {
                rowIndex = addRowToGridPaneTop();
                double stock = entry.getValue();

                Label labelResultPriceRURName = buildLabel(null, "Скидка по акции: \"" + entry.getKey() + "\"", "labelProduct");
                Label labelResultPriceRUR = buildLabel(null, "- " + Currency.RUR_SYMBOL + formatPrice(stock), "labelProductPrice");

                receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
                receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);

                stockSizeAll += stock; // in RUR
                System.out.println("STOCK SIZE ALL = " + stockSizeAll);
            }
        }

        // result price in RUR
        {
            rowIndex = addRowToGridPaneTop();

            double price = receiptManager.allPriceForRUR + (receiptManager.allPriceForUSD * RUBtoUSD) + (receiptManager.allPriceForEUR * RUBtoEUR);
            forEventResultPriceRUR = price;

            price -= stockSizeAll; // in RUR

            Label labelResultPriceRURName = buildLabel(null, "  Стоимость в рублях действительна на день просчета:", Arrays.asList("labelTableResult", "labelTableResultEnd"));
            Label labelResultPriceRUR = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price), Arrays.asList("labelTableResultPrice", "labelTableResultEndPrice"));

            receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
        }

        // EMPTY STR
        {
            rowIndex = addRowToGridPaneTop(1.0);

            Label labelResultPriceRURName = buildLabel(null, null, "labelLastRowTransparent");
            labelResultPriceRURName.setAlignment(Pos.CENTER_RIGHT);

            Label labelResultPriceRUR = buildLabel(null, null, "labelLastRowTransparent");
            labelResultPriceRUR.setAlignment(Pos.CENTER_RIGHT);

            receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
        }

        System.out.println("result prices EUR = " + receiptManager.allPriceForEUR);
        System.out.println("result product prices USD = " + receiptManager.allPriceForUSD);
        System.out.println("result product prices RUR = " + receiptManager.allPriceForRUR);

        // if have old prices
        {
            if (!Receipt.pricesActual) {
                rowIndex = addRowToGridPaneTop();

                Label labelPricesNotActual = buildLabel("labelResultPriceRUR",
                        "Внимание! Цены на материал устарели. Необходимо согласование у персонального менеджера.",
                        Collections.emptyList());
                labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
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
