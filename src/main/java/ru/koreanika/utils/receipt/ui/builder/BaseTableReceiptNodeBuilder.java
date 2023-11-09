package ru.koreanika.utils.receipt.ui.builder;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import ru.koreanika.project.Project;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.policy.TableDesignerItemMapper;
import ru.koreanika.utils.receipt.ui.component.ReceiptImageItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;
import ru.koreanika.utils.receipt.ui.controller.ReceiptManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
            } else if (Project.getReceiptManagerSketchImage() != null) {
                heightFieldForSketch = 280;
                receiptManager.imageViewSketch = new ImageView(Project.getReceiptManagerSketchImage());
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

                final ProjectHandler projectHandler = ServiceLocator.getService("ProjectHandler", ProjectHandler.class);
                if (projectHandler.getCurrentProjectPath() != null) {
                    File projectFile = new File(projectHandler.getCurrentProjectPath());
                    fileChooser.setInitialDirectory(projectFile.getParentFile());
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
                Project.setReceiptManagerSketchImage(null);
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

        for (ReceiptItem receiptItem : TableDesignerItemMapper.getEdgesReceiptList()) {
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

        for (ReceiptItem receiptItem : TableDesignerItemMapper.getBordersReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getSinkAcrylReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getSinkQuarzReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getSinkInstallReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getJointsReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getCutoutsReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getPlumbingAlveusReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getPlumbingReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getPalletReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getGroovesReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getRodsReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getRadiusReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getLeakGroovesReceiptItems()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getMetalFootingReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getPlywoodReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getStonePolishingReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getSiphonReceiptList()) {
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
        for (ReceiptItem receiptItem : TableDesignerItemMapper.getCustomReceiptList()) {
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

        allAdditionalItems.addAll(TableDesignerItemMapper.getEdgesReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getBordersReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getLeakGroovesReceiptItems());
        allAdditionalItems.addAll(TableDesignerItemMapper.getSinkAcrylReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getSinkInstallReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getJointsReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getRadiusReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getCutoutsReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getPlumbingAlveusReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getPlumbingReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getPalletReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getGroovesReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getRodsReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getMetalFootingReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getPlywoodReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getStonePolishingReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getSiphonReceiptList());
        allAdditionalItems.addAll(TableDesignerItemMapper.getCustomReceiptList());

        for (ReceiptItem receiptItem : allAdditionalItems) {
            receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        int rowIndex = addRowToGridPaneTop();

        // Additional work price
        double price = receiptManager.allAddPriceForRUR + (receiptManager.allAddPriceForUSD * RUBtoUSD) + ((receiptManager.allAddPriceForEUR * RUBtoEUR));//in RUR

        Label labelAdditionalAllPriceName = buildLabel(null, "Стоимость дополнительных работ", Arrays.asList("labelTableResult", "labelProduct-right"));
        Label labelAdditionalAllPrice = buildLabel(null, Currency.RUR_SYMBOL + formatPrice(price), "labelTableResultPrice");

        receiptManager.gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
        receiptManager.gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);
    }
}
