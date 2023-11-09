package ru.koreanika.utils.receipt.builder;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import ru.koreanika.common.material.Material;
import ru.koreanika.cutDesigner.Shapes.CutObject;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.project.Project;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.UserPreferences;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.Receipt;
import ru.koreanika.utils.receipt.ReceiptItem;
import ru.koreanika.utils.receipt.controller.ReceiptManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BaseReceiptNodeBuilder {

    protected final ReceiptManager receiptManager;

    public BaseReceiptNodeBuilder(ReceiptManager receiptManager) {
        this.receiptManager = receiptManager;
    }

    protected static Label buildLabel(String id, String text, List<String> classNames) {
        Label label = buildLabel(id, text, null, true);
        label.getStyleClass().addAll(classNames);
        return label;
    }

    protected static Label buildLabel(String id, String text, String className) {
        return buildLabel(id, text, className, true);
    }

    protected static Label buildLabel(String id, String text, String className, boolean wrapText) {
        Label label = new Label(text);
        if (id != null && !id.isEmpty()) {
            label.setId(id);
        }
        if (className != null && !className.isEmpty()) {
            label.getStyleClass().add(className);
        }
        label.setAlignment(Pos.CENTER_LEFT);
        label.setWrapText(wrapText);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        return label;
    }

    public static String formatPrice(double price) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        return formatter.format(price);
    }

    public static String getCurrency(ReceiptItem entry) {
        return switch (entry.getCurrency()) {
            case "USD" -> Currency.USD_SYMBOL;
            case "EUR" -> Currency.EUR_SYMBOL;
            case "RUB" -> Currency.RUR_SYMBOL;
            default -> "*";
        };
    }

    public void createMaterialsPartGridPane() {
        receiptManager.stoneItems = 0;

        receiptManager.allStoneProductsPriceInRUR = 0;
        receiptManager.allStoneProductsPriceInUSD = 0;
        receiptManager.allStoneProductsPriceInEUR = 0;

        /* sorting */
        LinkedHashMap<CutShape, ReceiptItem> receiptItems = new LinkedHashMap<>();

        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Столешница")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Стеновая панель")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Подоконник")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Опора")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Кромка")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Бортик")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Раковина")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }

        if (receiptItems.size() != Receipt.getCutShapesAndReceiptItem().size()) {
            System.out.println("ERROR WITH SORTING STONE PRODUCT!!!");

            for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
                createCutShapeRow(entry.getKey(), entry.getValue());
            }
        } else {
            System.out.println("SUCCESS SORTING STONE PRODUCT!!!");

            for (Map.Entry<CutShape, ReceiptItem> entry : receiptItems.entrySet()) {
                createCutShapeRow(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getCuttableSinkAndReceiptItem().entrySet()) {
            createCutShapeRow(entry.getKey(), entry.getValue());
        }
        System.out.println("MATERIAL PART ONLY:");
        System.out.println("allStoneProductsPriceInRUR = " + receiptManager.allStoneProductsPriceInRUR);
        System.out.println("allStoneProductsPriceInUSD = " + receiptManager.allStoneProductsPriceInUSD);
        System.out.println("allStoneProductsPriceInEUR = " + receiptManager.allStoneProductsPriceInEUR);

        System.out.println("FOR currency USD:" + MainWindow.getUSDValue().get());
    }

    public abstract void createResultPart();

    public void createTopPartGridPane() {
        receiptManager.gridPaneTop.getColumnConstraints().clear();
        receiptManager.gridPaneTop.getRowConstraints().clear();
        receiptManager.gridPaneTop.getChildren().clear();

        for (int i = 0; i < 9; i++) {
            ColumnConstraints column = new ColumnConstraints(100);
            receiptManager.gridPaneTop.getColumnConstraints().add(column);
        }
        receiptManager.gridPaneTop.getColumnConstraints().get(0).setPercentWidth(20);
        receiptManager.gridPaneTop.getColumnConstraints().get(1).setPercentWidth(12);
        receiptManager.gridPaneTop.getColumnConstraints().get(2).setPercentWidth(18);
        receiptManager.gridPaneTop.getColumnConstraints().get(3).setPercentWidth(10);
        receiptManager.gridPaneTop.getColumnConstraints().get(4).setPercentWidth(10);
        receiptManager.gridPaneTop.getColumnConstraints().get(5).setPercentWidth(7);
        receiptManager.gridPaneTop.getColumnConstraints().get(6).setPercentWidth(8);
        receiptManager.gridPaneTop.getColumnConstraints().get(7).setPercentWidth(6);
        receiptManager.gridPaneTop.getColumnConstraints().get(8).setPercentWidth(9);

        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints(20);
            receiptManager.gridPaneTop.getRowConstraints().add(row);
        }
        receiptManager.gridPaneTop.getRowConstraints().get(0).setPrefHeight(40);
        receiptManager.gridPaneTop.getRowConstraints().get(1).setPrefHeight(40);
        receiptManager.gridPaneTop.getRowConstraints().get(2).setPrefHeight(40);
        receiptManager.gridPaneTop.getRowConstraints().get(3).setPrefHeight(40);
        receiptManager.gridPaneTop.getRowConstraints().get(4).setPrefHeight(40);
        receiptManager.gridPaneTop.getRowConstraints().get(5).setPrefHeight(40);

        receiptManager.gridPaneTop.add(receiptManager.anchorPaneReceiptHeader, 0, 0, 9, 5);

        receiptManager.labelGeneralName.setText(UserPreferences.getInstance().getCompanyAddress());
        System.out.println(UserPreferences.getInstance().getCompanyAddress());

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        receiptManager.labelDate.setText(formatter.format(date));

        //label Slab Count and coefficients:
        {
            double usesSlabs = Receipt.getUsesSlabs();
            double coeffMaterial = Project.getPriceMaterialCoefficient().doubleValue();
            double coeffMain = Project.getPriceMainCoefficient().doubleValue();
            double allSquare = Receipt.getAllSquare();
            receiptManager.labelCutoutInfo.setText("S:" + usesSlabs + " K:" +
                    String.format(Locale.ENGLISH, "%.1f", coeffMain) + " P:" +
                    String.format(Locale.ENGLISH, "%.1f", coeffMaterial) +
                    " Sq:" + String.format(Locale.ENGLISH, "%.2f", allSquare));
        }

        receiptManager.labelManagerName = buildLabel("labelManagerName", "Менеджер ФИО/Подпись", null, false);

        receiptManager.textFieldDocName.setText(receiptManager.docName);
        if (receiptManager.docName.isEmpty()) {
            receiptManager.textFieldDocName.setText("Приложение №1 к заказу №");
        }

        receiptManager.textFieldCustomerName.setText(receiptManager.customerName);
        receiptManager.textFieldCustomerAddress.setText(receiptManager.customerAddress);
        receiptManager.textFieldManagerName.setText(receiptManager.managerName);

        receiptManager.labelUSD.setText(Currency.USD_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getUSDValue().doubleValue()));
        receiptManager.labelEUR.setText(Currency.EUR_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));

        addTableHeaderToGridPaneTop();

        receiptManager.topPartChildrenCount = receiptManager.gridPaneTop.getChildren().size();
    }

    public void addTableHeaderToGridPaneTop() {
        Label labelElementType = buildLabel(null, "Наименование изделия", "labelTableHeader", false);
        Label labelStoneType = buildLabel(null, "Материал", "labelTableHeader", false);
        Label labelColor = buildLabel(null, "Цвет", "labelTableHeader", false);
        Label labelLength = buildLabel(null, "Сторона 1, мм.", "labelTableHeader", true);
        Label labelWidth = buildLabel(null, "Сторона 2, мм.", "labelTableHeader", true);
        Label labelInchType = buildLabel(null, "Ед.", "labelTableHeader", false);
        Label labelCount = buildLabel(null, "Кол-во", "labelTableHeader", false);
        Label labelResultPrice = buildLabel(null, "Стоимость", "labelTableHeader", false);

        receiptManager.gridPaneTop.add(labelElementType, 0, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneType, 1, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelColor, 2, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelLength, 3, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelWidth, 4, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelInchType, 5, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelCount, 6, 5, 1, 1);
        receiptManager.gridPaneTop.add(labelResultPrice, 7, 5, 2, 1);
    }

    public void createAdditionalPartTop() {
        int rowIndex = addRowToGridPaneTop();

        Label labelAdditionalFeatureName = buildLabel(null, "Дополнительные работы и опции в зависимости от камня", "labelTableHeader");
        Label labelAdditionalFeatureInches = buildLabel(null,"Ед.", "labelTableHeader");
        Label labelAdditionalFeaturePrice = buildLabel(null, "цена", "labelTableHeader");
        Label labelAdditionalFeatureCount = buildLabel(null, "кол-во", "labelTableHeader");
        Label labelAdditionalFeatureResultPrice = buildLabel(null, "Стоимость", "labelTableHeader");

        receiptManager.gridPaneTop.add(labelAdditionalFeatureName, 0, rowIndex, 5, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureInches, 5, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeaturePrice, 6, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureCount, 7, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureResultPrice, 8, rowIndex, 1, 1);
    }

    protected int addRowToGridPaneTop() {
        return addRowToGridPaneTop(40);
    }

    protected int addRowToGridPaneTop(double height) {
        RowConstraints rowForEdge = new RowConstraints(height);
        receiptManager.gridPaneTop.getRowConstraints().add(rowForEdge);
        int rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;
        return rowIndex;
    }

    public void createMaterialsPartGridPaneShort() {
        receiptManager.stoneItems = 0;

        receiptManager.allStoneProductsPriceInRUR = 0;
        receiptManager.allStoneProductsPriceInUSD = 0;
        receiptManager.allStoneProductsPriceInEUR = 0;

        Map<Material, Map<CutShape, ReceiptItem>> cutShapesAndMaterials = new LinkedHashMap<>();
        Map<Material, Map<CutShape, ReceiptItem>> cutShapesForSinkAndMaterials = new LinkedHashMap<>();
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {

            Material material = entry.getKey().getMaterial();

            if (entry.getValue().getName().contains("Раковина")) {
                Map<CutShape, ReceiptItem> map = cutShapesForSinkAndMaterials.get(material);
                if (map == null) {
                    map = new LinkedHashMap<CutShape, ReceiptItem>();
                }
                map.put(entry.getKey(), entry.getValue());
                cutShapesForSinkAndMaterials.put(material, map);
            } else {
                Map<CutShape, ReceiptItem> map = cutShapesAndMaterials.get(material);
                if (map == null) {
                    map = new LinkedHashMap<CutShape, ReceiptItem>();
                }
                map.put(entry.getKey(), entry.getValue());

                cutShapesAndMaterials.put(material, map);
            }
        }

        for (Map.Entry<Material, Map<CutShape, ReceiptItem>> entry : cutShapesAndMaterials.entrySet()) {
            double priceForPartInRUB = 0.0;
            double square = 0.0;
            for (Map.Entry<CutShape, ReceiptItem> entryInner : entry.getValue().entrySet()) {
                receiptManager.stoneItems++;

                ReceiptItem receiptItem = entryInner.getValue();
                square += receiptItem.getPseudoCountDouble();

                //calculate allPrice:
                addToAllPriceRunningTotal(receiptItem);
                addToStoneProductsPriceRunningTotal(receiptItem);

                priceForPartInRUB += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            }

            Material material = entry.getKey();
            String squareStr = String.format(Locale.ENGLISH, "%.2f", square);
            String strPrice = formatPrice(priceForPartInRUB);

            createCutShapeRow("Стоимость изделия с обработкой", material.getMainType(), material.getSubType(),
                    material.getColor(), "-", "-", "м2", squareStr, strPrice);
        }

        for (Map.Entry<Material, Map<CutShape, ReceiptItem>> entry : cutShapesForSinkAndMaterials.entrySet()) {
            double priceForPartInRUB = 0.0;

            Material material = entry.getKey();

            String name = "Стоимость изделия для мойки с изготовлением";
            String materialMainType = material.getMainType();
            String materialSubType = material.getSubType();
            String materialCollectionType = material.getCollection();
            String materialColor = material.getColor();
            String units = "м2";
            String strPrice;
            double square = 0;
            String squareStr = "";

            for (Map.Entry<CutShape, ReceiptItem> entryInner : entry.getValue().entrySet()) {
                receiptManager.stoneItems++;

                ReceiptItem receiptItem = entryInner.getValue();
                square += receiptItem.getPseudoCountDouble();

                //calculate allPrice:
                addToAllPriceRunningTotal(receiptItem);
                addToStoneProductsPriceRunningTotal(receiptItem);

                priceForPartInRUB += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            }

            for (ReceiptItem receiptItemSink : TableDesigner.getSinkQuarzReceiptList()) {
                String subNameSink = receiptItemSink.getName().split("#")[1];
                if (subNameSink.equals(materialCollectionType + " " + materialColor)) {
                    priceForPartInRUB += Double.parseDouble(receiptItemSink.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
                }
            }

            squareStr = String.format(Locale.ENGLISH, "%.2f", square);
            strPrice = formatPrice(priceForPartInRUB);

            createCutShapeRow(name, materialMainType, materialSubType, materialColor, "-", "-", units, squareStr, strPrice);
        }

        // allPrice
        for (ReceiptItem receiptItem : TableDesigner.getSinkQuarzReceiptList()) {
            addToAllPriceRunningTotal(receiptItem);
            addToStoneProductsPriceRunningTotal(receiptItem);
        }
    }

    private void createCutShapeRow(String name, String materialMainType, String materialSubType, String materialColor,
                                   String width, String height, String units, String square, String priceInRUR) {

        // TODO what's this ???
        receiptManager.materialsForEvent.add(materialMainType + "-" + materialSubType + "-" + materialColor);

        int rowIndex = addRowToGridPaneTop();

        Label labelElementTypeValue = buildLabel(null, name, "labelStoneProduct");
        Label labelStoneTypeValue = buildLabel(null, materialMainType, "labelStoneProduct");
        Label labelStoneColorValue = buildLabel(null, materialSubType + ", " + materialColor, "labelStoneProduct");
        Label labelStoneWidthValue = buildLabel(null, width, "labelStoneProduct");
        Label labelStoneHeightValue = buildLabel(null, height, "labelStoneProduct");
        Label labelInchesValue = buildLabel(null, units, "labelStoneProduct");
        Label labelSquareValue = buildLabel(null, square, "labelStoneProduct");
        Label labelResultPriceValue = buildLabel(null, Currency.RUR_SYMBOL + priceInRUR, "labelStoneProductPrice");

        receiptManager.gridPaneTop.add(labelElementTypeValue, 0, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneTypeValue, 1, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneColorValue, 2, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneWidthValue, 3, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneHeightValue, 4, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelInchesValue, 5, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelSquareValue, 6, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelResultPriceValue, 7, rowIndex, 2, 1);
    }

    public void createCutShapeRow(Object object, ReceiptItem receiptItem) {
        receiptManager.stoneItems++;
        receiptItem.setCoefficient(receiptManager.coefficient);

        int rowIndex = addRowToGridPaneTop();

        Material material;
        double width;
        double height;
        if (object instanceof CutObject cutObject) {
            material = cutObject.getMaterial();
            width = ((SketchShape) ((CutShape) cutObject).getSketchObjectOwner()).getShapeWidth();
            height = ((SketchShape) ((CutShape) cutObject).getSketchObjectOwner()).getShapeHeight();
        } else {
            Sink sink = (Sink) object;
            material = sink.getSketchShapeOwner().getMaterial();
            width = sink.getFeatureWidth();
            height = sink.getFeatureHeight();
        }

        String message = "";
        if (material.getNotification1() == 1) {
            message += "Выбранный цвет требует обязательного уточнения по наличию.\n\t";
        }
        if (material.getNotification2() == 1) {
            message += "Количество стыков и их расположение в данной коллекции выполняется по усмотрению производителя.";
        }

        // labels
        Label labelElementTypeValue = buildLabel(null, receiptItem.getName(), "labelStoneProduct");
        if (!message.isEmpty()) {
            labelElementTypeValue.setTooltip(new Tooltip(message));
        }

        Label labelStoneTypeValue = buildLabel(null, material.getMainType(), "labelStoneProduct");
        if (!message.isEmpty()) {
            labelStoneTypeValue.setTooltip(new Tooltip(message));
        }

        Label labelStoneColorValue = buildLabel(null, material.getCollection() + ", " + material.getColor(), "labelStoneProduct");
        if (!message.isEmpty()) {
            labelStoneColorValue.setTooltip(new Tooltip(message));
        }

        Label labelStoneWidthValue = buildLabel(null, String.format(Locale.ENGLISH, "%.1f", width), "labelStoneProduct");
        Label labelStoneHeightValue = buildLabel(null, String.format(Locale.ENGLISH, "%.1f", height), "labelStoneProduct");
        Label labelInchesValue = buildLabel(null, receiptItem.getUnits(), "labelStoneProduct");

        String count = (receiptItem.getPseudoCount().equals("-1.00") ? receiptItem.getCount() : receiptItem.getPseudoCount());
        Label labelSquareValue = buildLabel(null, count, "labelStoneProduct");

        Label labelResultPriceValue = buildLabel(null, Currency.RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelStoneProductPrice");
        labelResultPriceValue.setStyle("-fx-text-fill: " + receiptItem.getPriceColor() + ";");

        // add labels to row
        receiptManager.gridPaneTop.add(labelElementTypeValue, 0, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneTypeValue, 1, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneColorValue, 2, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneWidthValue, 3, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelStoneHeightValue, 4, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelInchesValue, 5, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelSquareValue, 6, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelResultPriceValue, 7, rowIndex, 2, 1);

        //calculate allPrice:
        addToAllPriceRunningTotal(receiptItem);
        addToStoneProductsPriceRunningTotal(receiptItem);
    }

    protected void addToAllPriceRunningTotal(ReceiptItem receiptItem) {
        double allPrice = Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        switch (receiptItem.getCurrency()) {
            case "USD" -> receiptManager.allPriceForUSD += allPrice;
            case "EUR" -> receiptManager.allPriceForEUR += allPrice;
            case "RUB" -> receiptManager.allPriceForRUR += allPrice;
        }
    }

    protected void addToAllAddPriceRunningTotal(ReceiptItem receiptItem) {
        double allPrice = Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        switch (receiptItem.getCurrency()) {
            case "USD" -> receiptManager.allAddPriceForUSD += allPrice;
            case "EUR" -> receiptManager.allAddPriceForEUR += allPrice;
            case "RUB" -> receiptManager.allAddPriceForRUR += allPrice;
        }
    }

    private void addToStoneProductsPriceRunningTotal(ReceiptItem receiptItem) {
        double allPrice = Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        switch (receiptItem.getCurrency()) {
            case "USD" -> receiptManager.allStoneProductsPriceInUSD += allPrice;
            case "EUR" -> receiptManager.allStoneProductsPriceInEUR += allPrice;
            case "RUB" -> receiptManager.allStoneProductsPriceInRUR += allPrice;
        }
    }

}
