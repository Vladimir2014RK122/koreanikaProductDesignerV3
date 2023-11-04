package ru.koreanika.utils.Receipt;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;

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
            RowConstraints row2 = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(row2);
            int rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
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
                receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            }

            //label labelMountPrice
            {
                double price = ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0);
                if (price * RUBtoUSD < 4000) price = 4000 / RUBtoUSD;

                Label labelMountPrice = new Label();
                //labelMountPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                //labelMountPrice.setText("$" + formatPrice(price));
                labelMountPrice.setText(Currency.RUR_SYMBOL + BaseReceiptNodeBuilder.formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                addPriceUSD += price;

            }

            //calculate allPrice:
            {
                receiptManager.allPriceForUSD += addPriceUSD;
            }

        }
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
            RowConstraints row2 = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(row2);
            int rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
            //label labelDiscountName
            {

                Label labelDiscountName = new Label(receiptItem.getName());
                labelDiscountName.setAlignment(Pos.CENTER_LEFT);
                labelDiscountName.getStyleClass().add("labelProduct");
                labelDiscountName.setMaxWidth(Double.MAX_VALUE);
                labelDiscountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountName, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountName, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelDiscountName, 0, rowIndex, 6, 1);
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
                receiptManager.gridPaneTop.add(labelDiscountPercent, 6, rowIndex, 1, 1);
            }

            //label labelDiscountPrice
            {

                System.out.println("price part in RUR = " + receiptManager.allPriceForRUR);
                System.out.println("price part in USD = " + receiptManager.allPriceForUSD);
                System.out.println("price part in EUR = " + receiptManager.allPriceForEUR);
                System.out.println("ALL PRICE ID USD =" + ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)));

                double price = -1 * (((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

                Label labelDiscountPrice = new Label();
                //labelDiscountPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                //labelDiscountPrice.setText("$" + formatPrice(price));
                labelDiscountPrice.setText(Currency.RUR_SYMBOL + BaseReceiptNodeBuilder.formatPrice(price * RUBtoUSD));
                labelDiscountPrice.getStyleClass().add("labelProductPrice");
                labelDiscountPrice.setMaxWidth(Double.MAX_VALUE);
                labelDiscountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountPrice, Priority.ALWAYS);
                receiptManager.gridPaneTop.add(labelDiscountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                {
                    //ReceiptItem receiptItem = entry.getValue();
                    discountPriceInUSD = price;

                }

            }
        }

        receiptManager.allPriceForUSD += discountPriceInUSD;
    }

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

            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = "*";
            if (receiptItem.getCurrency().equals("USD")) currency = Currency.USD_SYMBOL;
            else if (receiptItem.getCurrency().equals("EUR")) currency = Currency.EUR_SYMBOL;
            if (receiptItem.getCurrency().equals("RUB")) currency = Currency.RUR_SYMBOL;


            labelDeliveryValueName.setText(receiptItem.getName());

            labelDeliveryPrice.setText(currency + receiptItem.getPriceForOne());
            labelDeliveryCount.setText(receiptItem.getCount());
            labelDeliveryResultPrice.setText(currency + receiptItem.getAllPrice());

            RowConstraints rowForEdge = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(rowForEdge);

            //labelDeliveryValueName:
            {
                labelDeliveryValueName.getStyleClass().add("labelProduct");
                labelDeliveryValueName.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryValueName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryValueName, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryValueName, Priority.ALWAYS);
                labelDeliveryValueName.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelDeliveryValueName, 0, rowIndex, 5, 1);
            }
            //labelDeliveryValueSubName:
            {
                labelDeliveryValueSubName.getStyleClass().add("labelProduct");
                labelDeliveryValueSubName.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryValueSubName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryValueSubName, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryValueSubName, Priority.ALWAYS);
                labelDeliveryValueSubName.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelDeliveryInches, 5, rowIndex, 1, 1);
            }
            //labelDeliveryPrice:
            {
                labelDeliveryPrice.getStyleClass().add("labelProduct");
                labelDeliveryPrice.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryPrice, Priority.ALWAYS);
                labelDeliveryPrice.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelDeliveryPrice, 6, rowIndex, 1, 1);
            }
            //labelDeliveryCount:
            {
                labelDeliveryCount.getStyleClass().add("labelProduct");
                labelDeliveryCount.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryCount, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryCount, Priority.ALWAYS);
                labelDeliveryCount.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelDeliveryCount, 7, rowIndex, 1, 1);
            }
            //labelDeliveryResultPrice:
            {
                labelDeliveryResultPrice.getStyleClass().add("labelProductPrice");
                labelDeliveryResultPrice.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryResultPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryResultPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryResultPrice, Priority.ALWAYS);
                labelDeliveryResultPrice.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelDeliveryResultPrice, 8, rowIndex, 1, 1);
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
    protected void createMeasuringPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getMeasurerReceiptList()) {

            int rowIndex;

            Label labelMeasuringValueName = new Label(receiptItem.getName());
            Label labelMeasuringValueSubName = new Label("");
            Label labelMeasuringPrice = new Label(receiptItem.getPriceForOne());
            Label labelMeasuringCount = new Label(receiptItem.getCount());
            Label labelMeasuringResultPrice = new Label(receiptItem.getAllPrice());

            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = "*";
            if (receiptItem.getCurrency().equals("USD")) currency = Currency.USD_SYMBOL;
            else if (receiptItem.getCurrency().equals("EUR")) currency = Currency.EUR_SYMBOL;
            if (receiptItem.getCurrency().equals("RUB")) currency = Currency.RUR_SYMBOL;


            labelMeasuringValueName.setText(receiptItem.getName());

            labelMeasuringPrice.setText(currency + receiptItem.getPriceForOne());
            labelMeasuringCount.setText(receiptItem.getCount());
            labelMeasuringResultPrice.setText(currency + receiptItem.getAllPrice());

            RowConstraints rowForEdge = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(rowForEdge);

            //labelMeasuringValueName:
            {
                labelMeasuringValueName.getStyleClass().add("labelProduct");
                labelMeasuringValueName.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringValueName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringValueName, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringValueName, Priority.ALWAYS);
                labelMeasuringValueName.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelMeasuringValueName, 0, rowIndex, 5, 1);
            }
            //labelMeasuringValueSubName:
            {
                labelMeasuringValueSubName.getStyleClass().add("labelProduct");
                labelMeasuringValueSubName.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringValueSubName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringValueSubName, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringValueSubName, Priority.ALWAYS);
                labelMeasuringValueSubName.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
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
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelMeasuringInches, 5, rowIndex, 1, 1);
            }
            //labelMeasuringPrice:
            {
                labelMeasuringPrice.getStyleClass().add("labelProduct");
                labelMeasuringPrice.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringPrice, Priority.ALWAYS);
                labelMeasuringPrice.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelMeasuringPrice, 6, rowIndex, 1, 1);
            }
            //labelMeasuringCount:
            {
                labelMeasuringCount.getStyleClass().add("labelProduct");
                labelMeasuringCount.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringCount, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringCount, Priority.ALWAYS);
                labelMeasuringCount.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelMeasuringCount, 7, rowIndex, 1, 1);
            }
            //labelMeasuringResultPrice:
            {
                labelMeasuringResultPrice.getStyleClass().add("labelProductPrice");
                labelMeasuringResultPrice.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringResultPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringResultPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringResultPrice, Priority.ALWAYS);
                labelMeasuringResultPrice.setWrapText(true);
                rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
                receiptManager.gridPaneTop.add(labelMeasuringResultPrice, 8, rowIndex, 1, 1);
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
    public void createResultPart() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        /** All product price in USD */
        {
            int rowIndex = addRowToGridPaneTop();

            Label labelAllPriceName = buildLabel(null, "  Итого стоимость изделия в долларах США", "labelTableResult");

            double price = (receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD);
            Label labelAllPrice = buildLabel(null, Currency.USD_SYMBOL + BaseReceiptNodeBuilder.formatPrice(price), "labelTableResultPrice");

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
            Label labelServicesWorksPriceValue = buildLabel(null, Currency.USD_SYMBOL + BaseReceiptNodeBuilder.formatPrice(servicesWorksPriceInUSD), "labelTableResultPrice");

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

                Label labelSmallProductName = buildLabel(null, " Доплата за изделие менее 2 кв.м.", null);
                labelSmallProductName.getStyleClass().add("labelProduct-right");
                labelSmallProductName.getStyleClass().add("labelProduct");

                Label labelSmallProductPercent = buildLabel(null, null, "labelProduct");
                Label labelSmallProductPrice = buildLabel(null, String.format(Locale.ENGLISH, Currency.RUR_SYMBOL + "%.0f", price), "labelProductPrice");

                receiptManager.gridPaneTop.add(labelSmallProductName, 0, rowIndex, 6, 1);
                receiptManager.gridPaneTop.add(labelSmallProductPercent, 6, rowIndex, 1, 1);
                receiptManager.gridPaneTop.add(labelSmallProductPrice, 7, rowIndex, 2, 1);

                receiptManager.allPriceForUSD += price / RUBtoUSD;
            }
        }

        /** result price in RUR*/
        {
            int rowIndex = addRowToGridPaneTop();

            System.out.println("allPriceForRUR = " + receiptManager.allPriceForRUR);
            System.out.println("allPriceForUSD = " + receiptManager.allPriceForUSD);
            System.out.println("allPriceForEUR = " + receiptManager.allPriceForEUR);

            Label labelResultPriceRURName = buildLabel(null, "  Стоимость в рублях действительна на день просчета:", "labelTableResultEnd");
            labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);

            double price = ((receiptManager.allPriceForRUR / RUBtoUSD) + (receiptManager.allPriceForUSD) + ((receiptManager.allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * RUBtoUSD;
            Label labelResultPriceRUR = buildLabel(null, Currency.RUR_SYMBOL + BaseReceiptNodeBuilder.formatPrice(price), "labelTableResultEndPrice");

            receiptManager.gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            receiptManager.gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
        }

        /** FOR CLIENT*/
        int rowIndex = addRowToGridPaneTop();

        Label labelText = buildLabel(null, "С выбранным декором и конфигурацией согласен", "labelProduct");
        Label labelField = buildLabel(null, null, "labelProduct");

        receiptManager.gridPaneTop.add(labelText, 0, rowIndex, 7, 1);
        receiptManager.gridPaneTop.add(labelField, 7, rowIndex, 2, 1);

        // NOTIFICATION if have old prices
        if (!Receipt.pricesActual) {
            int rowIndex2 = addRowToGridPaneTop();

            Label labelPricesNotActual = buildLabel("labelResultPriceRUR",
                    "Внимание! Цены на материал устарели. Необходимо согласование у персонального менеджера.", null);
            labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
            receiptManager.gridPaneTop.add(labelPricesNotActual, 0, rowIndex2, 9, 1);
        }

    }

}
