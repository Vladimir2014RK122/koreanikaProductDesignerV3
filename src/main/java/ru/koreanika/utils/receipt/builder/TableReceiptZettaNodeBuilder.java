package ru.koreanika.utils.receipt.builder;

import javafx.scene.control.Label;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.Receipt;
import ru.koreanika.utils.receipt.ReceiptItem;
import ru.koreanika.utils.receipt.controller.ReceiptManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class TableReceiptZettaNodeBuilder extends BaseTableReceiptNodeBuilder {

    private double servicesWorksPriceInUSD;

    public TableReceiptZettaNodeBuilder(ReceiptManager receiptManager) {
        super(receiptManager);
        this.servicesWorksPriceInUSD = 0.0;
    }

    @Override
    protected void createMountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;
        for (ReceiptItem receiptItem : TableDesigner.getMountingReceiptList()) {
            int rowIndex = addRowToGridPaneTop();

            double price = ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0);
            if (price * RUBtoUSD < 4000) {
                price = 4000 / RUBtoUSD; // TODO wtf?
            }

            Label labelMountName = buildLabel(null, receiptItem.getName(), Arrays.asList("labelProduct-right", "labelProductPrice"));
            Label labelMountPercent = buildLabel(null, receiptItem.getPriceForOne() + "%", "labelProductPrice");
            Label labelMountPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price * RUBtoUSD), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

            addPriceUSD += price;
        }
        receiptManager.allPriceForUSD += addPriceUSD;
    }

    /**
     * DISCOUNT should be placed before services works
     */
    @Override
    public void createDiscountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double discountPriceInUSD = 0;
        for (ReceiptItem receiptItem : TableDesigner.getDiscountReceiptList()) {
            int rowIndex = addRowToGridPaneTop();

            System.out.println("price part in RUR = " + receiptManager.allPriceForRUR);
            System.out.println("price part in USD = " + receiptManager.allPriceForUSD);
            System.out.println("price part in EUR = " + receiptManager.allPriceForEUR);
            System.out.println("ALL PRICE ID USD =" + ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)));

            double price = -1 * (((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

            Label labelDiscountName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelDiscountPercent = buildLabel(null, receiptItem.getPriceForOne() + "%", "labelProduct");
            Label labelDiscountPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price * RUBtoUSD), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelDiscountName, 0, rowIndex, 6, 1);
            receiptManager.gridPaneTop.add(labelDiscountPercent, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelDiscountPrice, 7, rowIndex, 2, 1);

            discountPriceInUSD = price;
        }
        receiptManager.allPriceForUSD += discountPriceInUSD;
    }

    @Override
    protected void createDeliveryPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getDeliveryReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);
            String currency = getCurrency(receiptItem);

            int rowIndex = addRowToGridPaneTop();

            Label labelDeliveryValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelDeliveryInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelDeliveryPrice = buildLabel(null, currency + receiptItem.getPriceForOne(), "labelProduct");
            Label labelDeliveryCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelDeliveryResultPrice = buildLabel(null, currency + receiptItem.getAllPrice(), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelDeliveryValueName, 0, rowIndex, 5, 1);
            receiptManager.gridPaneTop.add(labelDeliveryInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelDeliveryPrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelDeliveryCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelDeliveryResultPrice, 8, rowIndex, 1, 1);

            switch (receiptItem.getCurrency()) {
                case "USD" ->
                        servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                case "EUR" ->
                        servicesWorksPriceInUSD += (Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) * RUBtoEUR) / RUBtoUSD;
                case "RUB" ->
                        servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) / RUBtoUSD;
            }
        }
    }

    @Override
    protected void createMeasuringPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getMeasurerReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);
            String currency = getCurrency(receiptItem);

            int rowIndex = addRowToGridPaneTop();

            Label labelMeasuringValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelMeasuringInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelMeasuringPrice = buildLabel(null, currency + receiptItem.getPriceForOne(), "labelProduct");
            Label labelMeasuringCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelMeasuringResultPrice = buildLabel(null, currency + receiptItem.getAllPrice(), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelMeasuringValueName, 0, rowIndex, 5, 1);
            receiptManager.gridPaneTop.add(labelMeasuringInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMeasuringPrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMeasuringCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMeasuringResultPrice, 8, rowIndex, 1, 1);

            switch (receiptItem.getCurrency()) {
                case "USD" ->
                        servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                case "EUR" ->
                        servicesWorksPriceInUSD += (Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) * RUBtoEUR) / RUBtoUSD;
                case "RUB" ->
                        servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) / RUBtoUSD;
            }
        }
    }

    @Override
    public void createResultPart() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        /** All product price in USD */
        {
            int rowIndex = addRowToGridPaneTop();

            Label labelAllPriceName = buildLabel(null, "  Итого стоимость изделия в долларах США", "labelTableResult");

            double price = (receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD);
            Label labelAllPrice = buildLabel(null, Currency.USD_SYMBOL + formatPrice(price), "labelTableResultPrice");

            receiptManager.gridPaneTop.add(labelAllPriceName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelAllPrice, 7, rowIndex, 2, 1);
        }

        /** Services works*/
        {
            int rowIndex = addRowToGridPaneTop();

            Label labelServicesWorksName = buildLabel(null, "Сервисные услуги", "labelTableHeader-2");
            Label labelServicesWorksInches = buildLabel(null, "Ед.", "labelTableHeader-2");
            Label labelServicesWorksCost = buildLabel(null, "Цена", "labelTableHeader-2");
            Label labelServicesWorksCount = buildLabel(null, "Кол-во", "labelTableHeader-2");
            Label labelServicesWorksPrice = buildLabel(null, "Стоимость", "labelTableHeader-2");

            receiptManager.gridPaneTop.add(labelServicesWorksName, 0, rowIndex, 5, 1);
            receiptManager.gridPaneTop.add(labelServicesWorksInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelServicesWorksCost, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelServicesWorksCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelServicesWorksPrice, 8, rowIndex, 1, 1);
        }

        // TODO sic!
        servicesWorksPriceInUSD = 0;

        createDeliveryPartGridPaneTD();
        createMeasuringPartGridPaneTD();

        /** Services work price*/
        {
            int rowIndex = addRowToGridPaneTop();

            Label labelServicesWorksPriceName = buildLabel(null, "  Итого стоимость Сервисных услуг в долларах США", "labelTableResult");
            Label labelServicesWorksPriceValue = buildLabel(null, Currency.USD_SYMBOL + formatPrice(servicesWorksPriceInUSD), "labelTableResultPrice");

            receiptManager.gridPaneTop.add(labelServicesWorksPriceName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelServicesWorksPriceValue, 7, rowIndex, 2, 1);
        }

        System.out.println("all  price in RUR = " + (receiptManager.allPriceForRUR + receiptManager.allPriceForUSD * RUBtoUSD + receiptManager.allPriceForEUR * RUBtoEUR));
        System.out.println("all add price in RUR = " + (receiptManager.allAddPriceForRUR + receiptManager.allAddPriceForUSD * RUBtoUSD + receiptManager.allAddPriceForEUR * RUBtoEUR));

        /** MOUNT */
        createMountPartGridPaneTD();

        receiptManager.allPriceForUSD += servicesWorksPriceInUSD;

        /** additional price percent for small product */
        {
            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();
            if (receiptManager.stoneItems == 0) {
                coeff = 0;
            }

            double price = (receiptManager.allPriceForRUR + receiptManager.allPriceForUSD * RUBtoUSD + receiptManager.allPriceForEUR * RUBtoEUR) * coeff;//in RUR
            if (coeff != 0.0) {
                int rowIndex = addRowToGridPaneTop();

                Label labelSmallProductName = buildLabel(null, " Доплата за изделие менее 2 кв.м.", Arrays.asList("labelProduct-right", "labelProduct"));
                Label labelSmallProductPercent = buildLabel(null, null, "labelProduct");
                Label labelSmallProductPrice = buildLabel(null, String.format(Locale.ENGLISH, Currency.RUR_SYMBOL + "%.0f", price), "labelProductPrice");

                receiptManager.gridPaneTop.add(labelSmallProductName, 0, rowIndex, 6, 1);
                receiptManager.gridPaneTop.add(labelSmallProductPercent, 6, rowIndex, 1, 1);
                receiptManager.gridPaneTop.add(labelSmallProductPrice, 7, rowIndex, 2, 1);

                receiptManager.allPriceForUSD += price / RUBtoUSD;
            }
        }

        // result price in RUR
        {
            int rowIndex = addRowToGridPaneTop();

            System.out.println("allPriceForRUR = " + receiptManager.allPriceForRUR);
            System.out.println("allPriceForUSD = " + receiptManager.allPriceForUSD);
            System.out.println("allPriceForEUR = " + receiptManager.allPriceForEUR);

            double price = ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * RUBtoUSD;

            Label labelResultPriceRURName = buildLabel(null, "  Стоимость в рублях действительна на день просчета:", "labelTableResultEnd");
            Label labelResultPriceRUR = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price), "labelTableResultEndPrice");

            receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
        }

        // FOR CLIENT
        int rowIndex = addRowToGridPaneTop();

        Label labelText = buildLabel(null, "С выбранным декором и конфигурацией согласен", "labelProduct");
        Label labelField = buildLabel(null, null, "labelProduct");

        receiptManager.gridPaneTop.add(labelText, 0, rowIndex, 7, 1);
        receiptManager.gridPaneTop.add(labelField, 7, rowIndex, 2, 1);

        // NOTIFICATION if have old prices
        if (!Receipt.pricesActual) {
            int rowIndex2 = addRowToGridPaneTop();

            Label labelPricesNotActual = buildLabel("labelResultPriceRUR",
                    "Внимание! Цены на материал устарели. Необходимо согласование у персонального менеджера.",
                    Collections.emptyList());
            labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
            receiptManager.gridPaneTop.add(labelPricesNotActual, 0, rowIndex2, 9, 1);
        }
    }

}
