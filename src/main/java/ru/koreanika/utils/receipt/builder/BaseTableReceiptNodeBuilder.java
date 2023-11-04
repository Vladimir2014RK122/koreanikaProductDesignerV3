package ru.koreanika.utils.receipt.builder;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.ReceiptImageItem;
import ru.koreanika.utils.receipt.ReceiptItem;
import ru.koreanika.utils.receipt.controller.ReceiptManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseTableReceiptNodeBuilder extends BaseReceiptNodeBuilder {

    public BaseTableReceiptNodeBuilder(ReceiptManager receiptManager) {
        super(receiptManager);
    }

    public void createImagesPartGridPaneTD() {
        double heightFieldForSketch = 40;

        RowConstraints rowForFlowPaneAndSketchImage = new RowConstraints(heightFieldForSketch);
        receiptManager.gridPaneTop.getRowConstraints().add(rowForFlowPaneAndSketchImage);

        int rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

        //Field for Sketch
        {
            BorderPane sketchPane = new BorderPane();
            sketchPane.setId("sketchPane");

            Button btnAttach = new Button("Attach");
            Button btnDelete = new Button("Delete");
            AnchorPane anchorPaneButtons = new AnchorPane();

            if (receiptManager.imageViewSketch != null) {
                heightFieldForSketch = 280;
            } else if (ProjectHandler.getReceiptManagerSketchImage() != null) {
                heightFieldForSketch = 280;
                receiptManager.imageViewSketch = new ImageView(ProjectHandler.getReceiptManagerSketchImage());
            }

            if (receiptManager.imageViewSketch != null) {
                receiptManager.imageViewSketch.setFitWidth(390);
                receiptManager.imageViewSketch.setFitHeight(240);
                receiptManager.imageViewSketch.setPreserveRatio(true);
            }

            sketchPane.setMinWidth(400);
            sketchPane.setMaxWidth(400);
            sketchPane.setMinHeight(heightFieldForSketch);

            anchorPaneButtons.setId("anchorPaneButtonsSketchImage");
            anchorPaneButtons.setPrefHeight(40);
            anchorPaneButtons.setMaxWidth(400);
            anchorPaneButtons.getChildren().add(btnAttach);
            anchorPaneButtons.getChildren().add(btnDelete);

            AnchorPane.setBottomAnchor(btnDelete, 10.0);
            AnchorPane.setLeftAnchor(btnDelete, 10.0);
            AnchorPane.setBottomAnchor(btnAttach, 10.0);
            AnchorPane.setRightAnchor(btnAttach, 10.0);

            btnAttach.setId("btnAttachSketchImage");

            btnDelete.setId("btnDeleteSketchImage");
            btnDelete.setVisible(receiptManager.imageViewSketch != null);

            sketchPane.setCenter(receiptManager.imageViewSketch);
            sketchPane.setBottom(anchorPaneButtons);

            rowForFlowPaneAndSketchImage.setPrefHeight(heightFieldForSketch);
            receiptManager.gridPaneTop.add(sketchPane, 4, rowIndex, 5, 1);

            GridPane.setHalignment(sketchPane, HPos.LEFT);

            btnAttach.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Some Files");

                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Эскизы", "*.png", "*.jpeg", "*.jpg"));

                if (ProjectHandler.getCurProjectPath() != null) {
                    String[] pathArr = ProjectHandler.getCurProjectPath().split("\\\\");
                    String path1 = "";
                    for (int i = 0; i < pathArr.length - 1; i++) {
                        path1 += "/" + pathArr[i];
                    }
                    System.out.println(ProjectHandler.getCurProjectPath());
                    System.out.println(path1);

                    fileChooser.setInitialDirectory(new File(path1));
                }

                File file = fileChooser.showOpenDialog(receiptManager.sceneReceiptManager.getWindow());

                if (file == null) {
                    return;
                }
                try {
                    receiptManager.imageViewSketch = new ImageView(new Image(new FileInputStream(file)));
                    receiptManager.imageViewSketch.setFitWidth(240);
                    receiptManager.imageViewSketch.setFitHeight(240);
                    receiptManager.imageViewSketch.setPreserveRatio(true);

                    sketchPane.setCenter(receiptManager.imageViewSketch);
                    System.out.println("imageViewSketch = " + receiptManager.imageViewSketch);

                    btnDelete.setVisible(true);
                    receiptManager.updateReceiptTable();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });

            btnDelete.setOnAction(event -> {
                receiptManager.imageViewSketch = null;
                ProjectHandler.setReceiptManagerSketchImage(null);
                btnDelete.setVisible(false);
                receiptManager.updateReceiptTable();
            });
        }

        if (!TableDesigner.getReceiptImages().isEmpty()) {
            FlowPane flowPaneForFeaturesPictures = new FlowPane();
            flowPaneForFeaturesPictures.setId("flowPaneForFeaturesPictures");

            int countInRow = 6;
            if (heightFieldForSketch == 40) {
                flowPaneForFeaturesPictures.setMinWidth(960);
                flowPaneForFeaturesPictures.setMaxWidth(960);
                countInRow = 9;
            } else {
                flowPaneForFeaturesPictures.setMinWidth(600);
                flowPaneForFeaturesPictures.setMaxWidth(600);
                countInRow = 6;
            }

            for (ReceiptImageItem receiptImageItem : TableDesigner.getReceiptImages()) {
                flowPaneForFeaturesPictures.getChildren().add(receiptImageItem);
            }

            double flowPaneHeight = (TableDesigner.getReceiptImages().size() / countInRow) * 140;
            if (TableDesigner.getReceiptImages().size() % countInRow != 0) {
                flowPaneHeight += 140;
            }
            System.out.println("TableDesigner.getReceiptImages().size() = " + TableDesigner.getReceiptImages().size());
            System.out.println("flowPaneHeight = " + flowPaneHeight);

            if (flowPaneHeight < heightFieldForSketch) {
                flowPaneHeight = heightFieldForSketch;
            }

            rowForFlowPaneAndSketchImage.setPrefHeight(flowPaneHeight);

            flowPaneForFeaturesPictures.setPrefHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMaxHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMinHeight(flowPaneHeight);
            GridPane.setHgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            GridPane.setVgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            GridPane.setHalignment(flowPaneForFeaturesPictures, HPos.LEFT);

            receiptManager.gridPaneTop.add(flowPaneForFeaturesPictures, 0, rowIndex, 4, 1);
        }

        /** MAIN work price*/
        rowIndex = addRowToGridPaneTop();

        Label labelAdditionalAllPriceName = buildLabel(null, "  Итого стоимость обязательных работ", "labelTableResult");
        labelAdditionalAllPriceName.setAlignment(Pos.CENTER_LEFT);

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();
        double price = receiptManager.allStoneProductsPriceInRUR + (receiptManager.allStoneProductsPriceInUSD * RUBtoUSD) + ((receiptManager.allStoneProductsPriceInEUR * RUBtoEUR));//in RUR
        Label labelAdditionalAllPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price), "labelTableResultPrice");

        receiptManager.gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
        receiptManager.gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);
    }

    public void createHeaderForAdditionalWorks() {
        int rowIndex = addRowToGridPaneTop();

        Label labelAdditionalFeatureName = buildLabel(null, "Фиксированые дополнительные работы и опции", "labelTableHeader-2");
        Label labelAdditionalFeatureInches = buildLabel(null, "Ед.", "labelTableHeader-2");
        Label labelAdditionalFeatureCount = buildLabel(null, "кол-во", "labelTableHeader-2");
        Label labelAdditionalFeatureResultPrice = buildLabel(null, "Стоимость", "labelTableHeader-2");

        receiptManager.gridPaneTop.add(labelAdditionalFeatureName, 0, rowIndex, 5, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureInches, 5, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureCount, 6, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureResultPrice, 7, rowIndex, 2, 1);
    }

    public void createEdgesAndBordersPartGridPaneTD() {
        int rowIndex;

        for (ReceiptItem receiptItem : TableDesigner.getEdgesReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            RowConstraints rowForEdge = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(rowForEdge);
            rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
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

        for (ReceiptItem receiptItem : TableDesigner.getBordersReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            RowConstraints rowForEdge = new RowConstraints(40);
            receiptManager.gridPaneTop.getRowConstraints().add(rowForEdge);
            rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
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

    public void createSinkAcrylPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSinkAcrylReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
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

    public void createSinkQuartzPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSinkQuarzReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
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

    public void createSinkInstallTypesPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSinkInstallReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
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

    public void createJointsPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getJointsReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
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

    public void createCutoutPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getCutoutsReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
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

    public void createPlumbingAlveusPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPlumbingAlveusReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
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

    public void createPlumbingPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPlumbingReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
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

    public void createPalletPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPalletReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
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

    public void createGroovesPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getGroovesReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
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

    public void createRodsPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getRodsReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, "Нержавеющая сталь", "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт.", "labelProduct");
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

    public void createRadiusElementsPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getRadiusReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
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

    public void createLeakGroovePartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getLeakGroovesReceiptItems()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
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

    public void createStoneHemPartGridPaneTD() {
        //not uses, logic inside createLeakGroovePartGridPaneTD()
    }

    public void createMetalFootingPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getMetalFootingReceiptList()) {
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

    public void createPlywoodPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPlywoodReceiptList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
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

    public void createStonePolishingPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getStonePolishingReceiptList()) {
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

    public void createSiphonPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSiphonReceiptList()) {
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

    public void createCustomPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getCustomReceiptList()) {
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

    protected abstract void createMeasuringPartGridPaneTD();

    protected abstract void createDeliveryPartGridPaneTD();

    protected abstract void createMountPartGridPaneTD();

    protected abstract void createDiscountPartGridPaneTD();

    public void createAdditionalRowShort() {
        List<ReceiptItem> allAdditionalItems = new ArrayList<>();

        allAdditionalItems.addAll(TableDesigner.getEdgesReceiptList());
        allAdditionalItems.addAll(TableDesigner.getBordersReceiptList());
        allAdditionalItems.addAll(TableDesigner.getLeakGroovesReceiptItems());
        allAdditionalItems.addAll(TableDesigner.getSinkAcrylReceiptList());
        allAdditionalItems.addAll(TableDesigner.getSinkInstallReceiptList());
        allAdditionalItems.addAll(TableDesigner.getJointsReceiptList());
        allAdditionalItems.addAll(TableDesigner.getRadiusReceiptList());
        allAdditionalItems.addAll(TableDesigner.getCutoutsReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPlumbingAlveusReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPlumbingReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPalletReceiptList());
        allAdditionalItems.addAll(TableDesigner.getGroovesReceiptList());
        allAdditionalItems.addAll(TableDesigner.getRodsReceiptList());
        allAdditionalItems.addAll(TableDesigner.getMetalFootingReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPlywoodReceiptList());
        allAdditionalItems.addAll(TableDesigner.getStonePolishingReceiptList());
        allAdditionalItems.addAll(TableDesigner.getSiphonReceiptList());
        allAdditionalItems.addAll(TableDesigner.getCustomReceiptList());

        for (ReceiptItem receiptItem : allAdditionalItems) {
            receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        int rowIndex = addRowToGridPaneTop();

        // Additional work price
        Label labelAdditionalAllPriceName = buildLabel(null, "Стоимость дополнительных работ", Collections.emptyList());
        labelAdditionalAllPriceName.setAlignment(Pos.CENTER_LEFT);
        labelAdditionalAllPriceName.getStyleClass().add("labelTableResult");
        labelAdditionalAllPriceName.getStyleClass().add("labelProduct-right");

        double price = receiptManager.allAddPriceForRUR + (receiptManager.allAddPriceForUSD * RUBtoUSD) + ((receiptManager.allAddPriceForEUR * RUBtoEUR));//in RUR
        Label labelAdditionalAllPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price), "labelTableResultPrice");

        receiptManager.gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
        receiptManager.gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);
    }
}
