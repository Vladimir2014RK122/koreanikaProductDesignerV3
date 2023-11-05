package ru.koreanika.utils.receipt.builder;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.PortalClient.UserEventHandler.UserEventService;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.sketchDesigner.Features.Grooves;
import ru.koreanika.sketchDesigner.Features.Rods;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.utils.receipt.Receipt;
import ru.koreanika.utils.receipt.ReceiptImageItem;
import ru.koreanika.utils.receipt.ReceiptItem;
import ru.koreanika.utils.receipt.controller.ReceiptManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class SketchReceiptNodeBuilder extends BaseReceiptNodeBuilder {

    public SketchReceiptNodeBuilder(ReceiptManager receiptManager) {
        super(receiptManager);
    }

    public void createEdgesAndBordersPartGridPane() {
        //create rows for edges:
        for (Map.Entry<SketchEdge, ReceiptItem> entry : Receipt.getEdgesAndBordersReceiptItemMap().entrySet()) {
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());

            String labelEdgeValueNameText = null;
            String labelEdgeValueSubNameText = null;
            if (entry.getKey() instanceof Edge) {
                SketchEdge sketchEdge = entry.getKey();
                labelEdgeValueNameText = entry.getValue().getName() + ", Вариант №" + sketchEdge.getEdgeNumber();
                labelEdgeValueSubNameText = sketchEdge.getSketchEdgeOwner().getMaterial().getReceiptName();
            } else if (entry.getKey() instanceof Border) {
                SketchEdge sketchBorder = entry.getKey();
                labelEdgeValueNameText = entry.getValue().getName() + ", Высота - " + ((SketchShape) sketchBorder.getSketchEdgeOwner()).getBorderHeight() + "мм";
                labelEdgeValueSubNameText = sketchBorder.getSketchEdgeOwner().getMaterial().getReceiptName();
            }

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", labelEdgeValueNameText, Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", labelEdgeValueSubNameText, Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + entry.getValue().getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", entry.getValue().getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + entry.getValue().getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD")) {
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            } else if (receiptItem.getCurrency().equals("EUR")) {
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
            if (receiptItem.getCurrency().equals("RUB")) {
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
        }

        for (Map.Entry<Border, ReceiptItem> entry : Receipt.getBordersTopCutReceiptItemMap().entrySet()) {
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", entry.getValue().getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", "Вариант №" + entry.getKey().getBorderCutType(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + entry.getValue().getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", entry.getValue().getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + entry.getValue().getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            {
                if (receiptItem.getCurrency().equals("USD"))
                    receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                else if (receiptItem.getCurrency().equals("EUR"))
                    receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                if (receiptItem.getCurrency().equals("RUB"))
                    receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
        }

        for (Map.Entry<Border, ReceiptItem> entry : Receipt.getBordersSideCutReceiptItemMap().entrySet()) {
            Border border = entry.getKey();
            if (border.getBorderAnglesCutType() == Border.BORDER_ANGLE_CUT_NONE) {
                continue;
            }

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", entry.getValue().getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", "Вариант №" + entry.getKey().getBorderSideCutType(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + entry.getValue().getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", entry.getValue().getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + entry.getValue().getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createSinkPartGridPane() {
        if (Receipt.getSinkAndReceiptItem().isEmpty()) {
            return;
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getSinkAndReceiptItem().entrySet()) {
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName() + " " + entry.getKey().getModel(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", entry.getKey().getSketchShapeOwner().getMaterial().getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createSinkInstallTypesPartGridPane() {
        if (Receipt.getSinkAndReceiptItem().isEmpty()) {
            return;
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getSinkInstallTypesAndReceiptItem().entrySet()) {
            Sink sink = entry.getKey();
            Material material = sink.getSketchShapeOwner().getMaterial();

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getSinkEdgeTypesAndReceiptItem().entrySet()) {
            Sink sink = entry.getKey();

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", sink.getSketchShapeOwner().getMaterial().getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createJointsPartGridPane() {
        for (ReceiptItem receiptItem : Receipt.getJointReceiptItemsList()) {
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(receiptItem);
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", null, Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createCutoutPartGridPane() {
        for (Map.Entry<Cutout, ReceiptItem> entry : Receipt.getCutoutAndReceiptItem().entrySet()) {
            Cutout cutout = entry.getKey();
            Material material = cutout.getSketchShapeOwner().getMaterial();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createGroovesPartGridPane() {
        for (Map.Entry<Grooves, ReceiptItem> entry : Receipt.getGroovesAndReceiptItem().entrySet()) {
            Grooves grooves = entry.getKey();
            Material material = grooves.getSketchShapeOwner().getMaterial();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createRodsPartGridPane() {
        for (Map.Entry<Rods, ReceiptItem> entry : Receipt.getRodsAndReceiptItem().entrySet()) {
            Rods rods = entry.getKey();
            Material material = rods.getSketchShapeOwner().getMaterial();

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createRadiusElementsPartGridPane() {
        for (Map.Entry<Material, ReceiptItem> entry : Receipt.getRadiusElementReceiptItemMap().entrySet()) {
            Material material = entry.getKey();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createLeakGroovePartGridPane() {
        for (Map.Entry<Material, ReceiptItem> entry : Receipt.getLeakGrooveReceiptItemMap().entrySet()) {
            Material material = entry.getKey();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createStoneHemPartGridPane() {
        for (Map.Entry<Material, ReceiptItem> entry : Receipt.getStoneHemReceiptItemMap().entrySet()) {
            Material material = entry.getKey();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(receiptManager.coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), Collections.emptyList());
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), Collections.emptyList());
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, Collections.emptyList());
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", Collections.emptyList());
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), Collections.emptyList());
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), Collections.emptyList());
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), Collections.emptyList());

            receiptManager.gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            receiptManager.gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                receiptManager.allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                receiptManager.allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                receiptManager.allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    public void createImagesPartGridPane() {
        int rowIndex;

        if (!Receipt.getReceiptImageItemsList().isEmpty()) {

            System.out.println("***************** receipt.getReceiptImageItemsList().size() = " + Receipt.getReceiptImageItemsList().size());
            FlowPane flowPaneForFeaturesPictures = new FlowPane();
            flowPaneForFeaturesPictures.setId("flowPaneForFeaturesPictures");
            flowPaneForFeaturesPictures.setMinWidth(600);
            flowPaneForFeaturesPictures.setMaxWidth(600);
            flowPaneForFeaturesPictures.setPrefWidth(600);

            for (ReceiptImageItem receiptImageItem : Receipt.getReceiptImageItemsList()) {
                flowPaneForFeaturesPictures.getChildren().add(receiptImageItem);
            }

            double flowPaneHeight = (Receipt.getReceiptImageItemsList().size() / 6) * 140;
            if (Receipt.getReceiptImageItemsList().size() % 6 != 0) {
                flowPaneHeight += 140;
            }
            System.out.println(Receipt.getReceiptImageItemsList().size());
            System.out.println(flowPaneHeight);

            RowConstraints rowForFlowPane = new RowConstraints(flowPaneHeight);
            receiptManager.gridPaneTop.getRowConstraints().add(rowForFlowPane);
            rowIndex = receiptManager.gridPaneTop.getRowConstraints().size() - 1;

            flowPaneForFeaturesPictures.setPrefHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMaxHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMinHeight(flowPaneHeight);
            GridPane.setHgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            GridPane.setVgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            GridPane.setHalignment(flowPaneForFeaturesPictures, HPos.LEFT);

            receiptManager.gridPaneTop.add(flowPaneForFeaturesPictures, 0, rowIndex, 5, 1);
        }

        rowIndex = addRowToGridPaneTop();

        Label labelAdditionalFeatureName = buildLabel(null, "Фиксированые дополнительные работы и опции", "labelTableHeader");
        Label labelAdditionalFeatureInches = buildLabel(null, "Ед.", "labelTableHeader");
        Label labelAdditionalFeatureCount = buildLabel(null, "кол-во", "labelTableHeader");
        Label labelAdditionalFeatureResultPrice = buildLabel(null, "Стоимость", "labelTableHeader");

        receiptManager.gridPaneTop.add(labelAdditionalFeatureName, 0, rowIndex, 5, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureInches, 5, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureCount, 6, rowIndex, 1, 1);
        receiptManager.gridPaneTop.add(labelAdditionalFeatureResultPrice, 7, rowIndex, 2, 1);
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
            double price = receiptManager.allAddPriceForRUR + (receiptManager.allAddPriceForUSD * RUBtoUSD) +
                    ((receiptManager.allAddPriceForEUR * RUBtoEUR)); // in RUR

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
        {
            rowIndex = addRowToGridPaneTop();

            double price = (receiptManager.allPriceForRUR + (receiptManager.allPriceForUSD * RUBtoUSD) + (receiptManager.allPriceForEUR * RUBtoEUR)) * 0.1;
            if (price * RUBtoUSD < 4000) {
                price = 4000; // in RUR
            }

            Label labelMountName = buildLabel(null, " Монтаж изделий +10% (но не менее 4000 рублей)", "labelProduct");
            Label labelMountPercent = buildLabel(null, "10%", "labelProduct");
            Label labelMountPrice = buildLabel(null, Currency.USD_SYMBOL + formatPrice(price), "labelProductPrice");

            receiptManager.gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            receiptManager.gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            receiptManager.gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

            //calculate allPrice:
            receiptManager.allPriceForUSD += price;
        }

        /** additional price percent for small product */
        {
            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();
            if (receiptManager.stoneItems == 0) {
                coeff = 0.0;
            }

            if (coeff != 0.0) {
                rowIndex = addRowToGridPaneTop();

                double price = (receiptManager.allStoneProductsPriceInRUR + receiptManager.allStoneProductsPriceInUSD * RUBtoUSD +
                        receiptManager.allStoneProductsPriceInEUR * RUBtoEUR) * coeff; // in RUR

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

                stockSizeAll += stock; //in RUR
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

        /* if have old prices*/
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
