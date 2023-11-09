package ru.koreanika.utils.receipt.ui.controller;

import javafx.scene.layout.RowConstraints;
import ru.koreanika.utils.receipt.policy.CalculateItemStocks;
import ru.koreanika.utils.receipt.ui.builder.TableReceiptZettaNodeBuilder;

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
    public void updateReceiptFull() {
        System.out.println("UPDATE RECEIPT TABLE  FULL");
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        CalculateItemStocks.calculateItemsStocks();

        TableReceiptZettaNodeBuilder receiptNodeBuilder = new TableReceiptZettaNodeBuilder(this);

        receiptNodeBuilder.createTopPartGridPane();
        receiptNodeBuilder.createMaterialsPartGridPane();
        receiptNodeBuilder.createImagesPartGridPaneTD();
        receiptNodeBuilder.createHeaderForAdditionalWorks();
        receiptNodeBuilder.createSinkQuartzPartGridPaneTD();
        receiptNodeBuilder.createEdgesAndBordersPartGridPaneTD();
        receiptNodeBuilder.createLeakGroovePartGridPaneTD();
        receiptNodeBuilder.createStoneHemPartGridPaneTD();
        receiptNodeBuilder.createSinkAcrylPartGridPaneTD();
        receiptNodeBuilder.createSinkInstallTypesPartGridPaneTD();
        receiptNodeBuilder.createJointsPartGridPaneTD();
        receiptNodeBuilder.createRadiusElementsPartGridPaneTD();
        receiptNodeBuilder.createCutoutPartGridPaneTD();
        receiptNodeBuilder.createPlumbingAlveusPartGridPaneTD();
        receiptNodeBuilder.createPlumbingPartGridPaneTD();
        receiptNodeBuilder.createPalletPartGridPaneTD();
        receiptNodeBuilder.createGroovesPartGridPaneTD();
        receiptNodeBuilder.createRodsPartGridPaneTD();
        receiptNodeBuilder.createMetalFootingPartGridPaneTD();
        receiptNodeBuilder.createPlywoodPartGridPaneTD();
        receiptNodeBuilder.createStonePolishingPartGridPaneTD();
        receiptNodeBuilder.createSiphonPartGridPaneTD();
        receiptNodeBuilder.createCustomPartGridPaneTD();

        receiptNodeBuilder.createDiscountPartGridPaneTD();
        receiptNodeBuilder.createResultPart();

        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }

    @Override
    public void updateReceiptShort() {
        System.out.println("UPDATE RECEIPT TABLE SHORT");
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        CalculateItemStocks.calculateItemsStocks();

        TableReceiptZettaNodeBuilder receiptNodeBuilder = new TableReceiptZettaNodeBuilder(this);

        receiptNodeBuilder.createTopPartGridPane();
        receiptNodeBuilder.createMaterialsPartGridPaneShort();
        receiptNodeBuilder.createImagesPartGridPaneTD();
        receiptNodeBuilder.createAdditionalRowShort();
        receiptNodeBuilder.createResultPart();

        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }

}
