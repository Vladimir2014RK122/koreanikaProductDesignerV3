package ru.koreanika.tableDesigner;

import ru.koreanika.Common.Material.Material;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.Features.*;
import ru.koreanika.tableDesigner.Items.*;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.ProjectType;
import ru.koreanika.utils.Receipt.ReceiptImageItem;
import ru.koreanika.utils.Receipt.ReceiptItem;

import java.io.IOException;
import java.util.*;

public class TableDesigner {


    private AnchorPane anchorPaneRoot = null;

    private static Accordion accordionItems;
    private static AnchorPane anchorPaneMenu;
    private static ScrollPane scrollPaneItems;
    private static AnchorPane anchorPaneIntoScrollPaneItems;
    private static ScrollPane scrollPaneVBoxTable;
    private static VBox vBoxTableRowsZone;


    private static FlowPane flowPaneTableCardsZone;
    private static Label labelMainItemsHeaderCards = new Label("Основные изделия");
    private static Label labelAddItemsHeaderCards = new Label("Дополнительные изделия");
    private static Label labelMainWorksHeaderCards = new Label("Основные работы");
    private static Label labelAddWorksHeaderCards = new Label("Дополнительные работы");

    /* Menu btns: */
    private static ToggleButton btnRowsView, btnCardsView;
    private static ToggleGroup toggleGroupViewType = new ToggleGroup();

    //private ArrayList<TableDesignerItem> tableDesignerItemsList = new ArrayList<>();
    private static ObservableList<TableDesignerItem> tableDesignerMainItemsList = FXCollections.observableList(new ArrayList<TableDesignerItem>());
    private static ObservableList<TableDesignerItem> tableDesignerAdditionalItemsList = FXCollections.observableList(new ArrayList<TableDesignerItem>());
    private static ObservableList<TableDesignerItem> tableDesignerMainWorkItemsList = FXCollections.observableList(new ArrayList<TableDesignerItem>());
    private static ObservableList<TableDesignerItem> tableDesignerAdditionalWorkItemsList = FXCollections.observableList(new ArrayList<TableDesignerItem>());


    public TableDesigner() {

        tableDesignerMainItemsList.clear();
        tableDesignerAdditionalItemsList.clear();
        tableDesignerMainWorkItemsList.clear();
        tableDesignerAdditionalWorkItemsList.clear();

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/tableDesigner.fxml"));
        //fxmlLoader.setLocation(this.getClass().getResource("/fxmls/TableDesigner/ru.koreanika.tableDesigner.fxml"));
        try {
            anchorPaneRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (anchorPaneRoot != null) {
            initControlElements();
            initControlElementLogic();
            initAccordionItems();
        }

        tableDesignerMainItemsList.addListener(new ListChangeListener<TableDesignerItem>() {
            @Override
            public void onChanged(Change<? extends TableDesignerItem> c) {
                tableViewRefresh();
            }
        });
        tableDesignerAdditionalItemsList.addListener(new ListChangeListener<TableDesignerItem>() {
            @Override
            public void onChanged(Change<? extends TableDesignerItem> c) {
                tableViewRefresh();
            }
        });
        tableDesignerMainWorkItemsList.addListener(new ListChangeListener<TableDesignerItem>() {
            @Override
            public void onChanged(Change<? extends TableDesignerItem> c) {
                tableViewRefresh();
            }
        });
        tableDesignerAdditionalWorkItemsList.addListener(new ListChangeListener<TableDesignerItem>() {
            @Override
            public void onChanged(Change<? extends TableDesignerItem> c) {
                tableViewRefresh();
            }
        });


        MainWindow.EURValueProperty().addListener((observableValue, number, newValue) -> {
            updatePriceInRows();
        });
        MainWindow.USDValueProperty().addListener((observableValue, number, newValue) -> {
            updatePriceInRows();
        });

        ProjectHandler.getPriceMainCoefficient().addListener((observable, oldValue, newValue) -> {
            TableDesigner.updatePriceInRows();

            if (ProjectHandler.getProjectType() == ProjectType.TABLE_TYPE) {
                if(ProjectHandler.getDefaultMaterial() == null) return;
                //update prices in settings
                BorderItem.updatePriceInSettings();
                CutoutItem.updatePriceInSettings();
                DeliveryItem.updatePriceInSettings();
                EdgeItem.updatePriceInSettings();
                GroovesItem.updatePriceInSettings();
                JointItem.updatePriceInSettings();
                LeakGrooveItem.updatePriceInSettings();
                MeasurerItem.updatePriceInSettings();
                MetalFootingItem.updatePriceInSettings();
                PlywoodItem.updatePriceInSettings();
                RadiatorGroovesItem.updatePriceInSettings();
                RadiusItem.updatePriceInSettings();
                RodsItem.updatePriceInSettings();
                SinkItem.updatePriceInSettings();
                PlumbingItem.updatePriceInSettings();
                PalletItem.updatePriceInSettings();
                StonePolishingItem.updatePriceInSettings();
            }
        });

    }

    /**
     * RECEIPT resources START
     */
    public static ArrayList<ReceiptImageItem> getReceiptImages() {
        ArrayList<ReceiptImageItem> imagesList = new ArrayList<>();

        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(tableDesignerMainItemsList);
        allItems.addAll(tableDesignerAdditionalItemsList);
        allItems.addAll(tableDesignerMainWorkItemsList);
        allItems.addAll(tableDesignerAdditionalWorkItemsList);


        //checkDuplicates via image paths:
        Set<String> pathsSet = new LinkedHashSet<>();

        for (TableDesignerItem item : allItems) {
            for (Map.Entry<String, ImageView> entry : item.getMainImageView().entrySet()) {

                if (!pathsSet.contains(entry.getKey().split("#")[1])) {
                    pathsSet.add(entry.getKey().split("#")[1]);
                    imagesList.add(new ReceiptImageItem(entry.getKey().split("#")[0], entry.getValue()));
                }

            }
        }

        return imagesList;
    }

    public static ArrayList<ReceiptItem> getEdgesReceiptList() {

        ArrayList<ReceiptItem> edgesReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : EdgeItem.getTableDesignerItemsList()) {
            if (item instanceof EdgeItem) {
                EdgeItem edgeItem = (EdgeItem) item;
                String name = "Кромка, Вариант №" + edgeItem.getType() + "#" + edgeItem.getMaterial().getReceiptName();
                String units = "м.п.";
                double count = edgeItem.getLength()/1000.0 * edgeItem.getQuantity();
                String currency = edgeItem.getMaterial().getEdgesCurrency();
                double priceForOne = edgeItem.getMaterial().getEdgesAndPrices().get(edgeItem.getType()).doubleValue();
                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                edgesReceiptItems.add(receiptItem);
            }
        }

        return edgesReceiptItems;
    }

    public static ArrayList<ReceiptItem> getLeakGroovesReceiptItems() {
        ArrayList<ReceiptItem> leakGroovesReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : LeakGrooveItem.getTableDesignerItemsList()) {
            if (item instanceof LeakGrooveItem) {
                LeakGrooveItem leakGrooveItem = (LeakGrooveItem) item;

                if (leakGrooveItem.getMaterial().getName().indexOf("Кварцевый агломерат") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Натуральный камень") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Dektone") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Мраморный агломерат") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Кварцекерамический камень") != -1) {

                    String name = "Выборка капельника" + "#" + leakGrooveItem.getMaterial().getReceiptName();
                    String units = "м.п.";
                    double count = (leakGrooveItem.getLength()/1000) * leakGrooveItem.getQuantity();
                    String currency = leakGrooveItem.getMaterial().getLeakGrooveCurrency();
                    double priceForOne = leakGrooveItem.getMaterial().getLeakGroovePrice() / 100;

                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    leakGroovesReceiptItems.add(receiptItem);
                } else {

                    String name = "Подгиб камня к каплесборником" + "#" + leakGrooveItem.getMaterial().getReceiptName();
                    String units = "м.п.";
                    double count = (leakGrooveItem.getLength()/1000);
                    String currency = leakGrooveItem.getMaterial().getStoneHemCurrency();
                    double priceForOne = leakGrooveItem.getMaterial().getStoneHemPrice() / 100;

                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    leakGroovesReceiptItems.add(receiptItem);
                }
            }
        }


        return leakGroovesReceiptItems;
    }

    public static ArrayList<ReceiptItem> getBordersReceiptList() {

        ArrayList<ReceiptItem> bordersReceiptItems = new ArrayList<>();

        ArrayList<ReceiptItem> bordersTopCutReceiptItems = new ArrayList<>();
        ArrayList<ReceiptItem> bordersSideCutReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : BorderItem.getTableDesignerItemsList()) {
            if (item instanceof BorderItem) {

                BorderItem borderItem = (BorderItem) item;

                //Border:
                {
                    String name = "Бортик, Вариант №" + borderItem.getType() + "#" + borderItem.getMaterial().getReceiptName();
                    String units = "м.п.";
                    double count = borderItem.getLength()/1000.0 * borderItem.getQuantity();
                    String currency = borderItem.getMaterial().getEdgesCurrency();
                    double priceForOne = -1.0;

                    if (borderItem.getMaterial().getName().indexOf("Кварцевый агломерат") != -1 ||
                            borderItem.getMaterial().getName().indexOf("Натуральный камень") != -1 ||
                            borderItem.getMaterial().getName().indexOf("Dektone") != -1 ||
                            borderItem.getMaterial().getName().indexOf("Мраморный агломерат") != -1 ||
                            borderItem.getMaterial().getName().indexOf("Кварцекерамический камень") != -1) {

                        priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(0).doubleValue();

                    } else {
                        if (borderItem.getHeight() <= 50 && borderItem.getType() == 1) {
                            priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(0).doubleValue();
                        }
                        if (borderItem.getHeight() > 50 && borderItem.getType() == 1) {
                            priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(1).doubleValue();
                        }

                        if (borderItem.getHeight() <= 50 && borderItem.getType() == 2) {
                            priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(2).doubleValue();
                        }
                        if (borderItem.getHeight() > 50 && borderItem.getType() == 2) {
                            priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(3).doubleValue();
                        }
                    }
                    priceForOne /= 100.0;

                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    bordersReceiptItems.add(receiptItem);
                }

                //topCut:
                {
                    String name = "Обработка верхней грани бортика" + "#" + borderItem.getMaterial().getReceiptName();
                    String units = "м. п.";
                    double count = borderItem.getLength()/1000.0 * borderItem.getQuantity();
                    String currency = "RUB";
                    double priceForOne = borderItem.getMaterial().getBorderTopCutTypesAndPrices()
                            .get(Integer.valueOf(borderItem.getCutType() - 1)) / 100;

                    ReceiptItem receiptItemTopCut = new ReceiptItem(name, units, count, currency, priceForOne);
                    bordersTopCutReceiptItems.add(receiptItemTopCut);
                }


                //sideCut:
                {
                    String name = "Запил бортика" + "#" + borderItem.getMaterial().getReceiptName();
                    String units = "шт";
                    double count = borderItem.getAngleCutQuantity() * borderItem.getQuantity();
                    String currency = "RUB";
                    double priceForOne = borderItem.getMaterial().getBorderSideCutTypesAndPrices().get(Integer.valueOf(borderItem.getAngleCutType() - 1)) / 100;

                    System.out.println("borderItem.getAngleCutQuantity() = " + borderItem.getAngleCutQuantity());
                    System.out.println("borderItem.getQuantity() = " + borderItem.getQuantity());
                    if (count != 0) {
                        ReceiptItem receiptItemTopCut = new ReceiptItem(name, units, count, currency, priceForOne);
                        bordersSideCutReceiptItems.add(receiptItemTopCut);
                    }
                }
            }
        }

        bordersReceiptItems.addAll(bordersTopCutReceiptItems);
        bordersReceiptItems.addAll(bordersSideCutReceiptItems);

        return bordersReceiptItems;
    }

    public static ArrayList<ReceiptItem> getSinkAcrylReceiptList() {

        ArrayList<ReceiptItem> sinkReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : SinkItem.getTableDesignerItemsList()) {
            if (item instanceof SinkItem) {
                SinkItem sinkItem = (SinkItem) item;
                if (sinkItem.getMaterial().getName().indexOf("Кварцевый агломерат") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Натуральный камень") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Dektone") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Мраморный агломерат") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Кварцекерамический камень") != -1) continue;

                String name = "Раковина " + sinkItem.getModel() + "#" + sinkItem.getMaterial().getReceiptName();
                String units = "шт";
                double count = sinkItem.getQuantity();
                String currency = sinkItem.getMaterial().getSinkCurrency();
                double priceForOne;
                if (sinkItem.getType() == Sink.SINK_TYPE_16 ||
                        sinkItem.getType() == Sink.SINK_TYPE_17 ||
                        sinkItem.getType() == Sink.SINK_TYPE_19 ||
                        sinkItem.getType() == Sink.SINK_TYPE_20 ||
                        sinkItem.getType() == Sink.SINK_TYPE_21) {
                    priceForOne = sinkItem.getMaterial().getSinkCommonTypesAndPrices().get(sinkItem.getType())/100.0;
                } else {

                    if(sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0]) == null){
                        //sink unavailable
                        priceForOne = -1;
                        count = 1;
                    }else{
                        priceForOne = (sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0])) / 100.0;
                    }



                }

                if(priceForOne != 0){
                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    sinkReceiptItems.add(receiptItem);
                }



            }
        }

        return sinkReceiptItems;
    }

    public static ArrayList<ReceiptItem> getSinkQuarzReceiptList() {

        ArrayList<ReceiptItem> sinkReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : SinkItem.getTableDesignerItemsList()) {
            if (item instanceof SinkItem) {
                SinkItem sinkItem = (SinkItem) item;
                if (sinkItem.getMaterial().getName().indexOf("Акриловый камень") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Полиэфирный камень") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Массив") != -1 ||
                        sinkItem.getMaterial().getName().indexOf("Массив_шпон") != -1)
                    continue;

                String name = "Раковина " + sinkItem.getModel() + "#" + sinkItem.getMaterial().getReceiptName();
                String units = "шт";
                double count = sinkItem.getQuantity();
                String currency = sinkItem.getMaterial().getSinkCurrency();
                double priceForOne;

                if (sinkItem.getType() == Sink.SINK_TYPE_16 ||
                        sinkItem.getType() == Sink.SINK_TYPE_17 ||
                        sinkItem.getType() == Sink.SINK_TYPE_19 ||
                        sinkItem.getType() == Sink.SINK_TYPE_20 ||
                        sinkItem.getType() == Sink.SINK_TYPE_21) {
                    priceForOne = sinkItem.getMaterial().getSinkCommonTypesAndPrices().get(sinkItem.getType())/100;
                } else {
                    System.out.println(sinkItem.getMaterial().getAvailableSinkModels() + "  " + sinkItem.getModel().split(" ")[0]);
                    priceForOne = (sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0])) / 100;



                }

                if(priceForOne != 0){
                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    sinkReceiptItems.add(receiptItem);
                }



            }
        }

        return sinkReceiptItems;
    }

    public static ArrayList<ReceiptItem> getSinkInstallReceiptList() {

        ArrayList<ReceiptItem> sinkInstallTypeReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : SinkItem.getTableDesignerItemsList()) {
            if (item instanceof SinkItem) {
                SinkItem sinkItem = (SinkItem) item;

                //installType:
                {
                    String name;
                    String units = "шт";
                    double count = sinkItem.getQuantity();
                    String currency = sinkItem.getMaterial().getSinkInstallTypeCurrency();
                    double priceForOne = 0;

                    if (sinkItem.getType() == Sink.SINK_TYPE_16 && (sinkItem.getInstallType() - 1) == Sink.SINK_INSTALL_TYPE_1) {
                        name = "Вырез для накладной мойки (круглый), без обработки" + "#" + sinkItem.getMaterial().getReceiptName();
                        priceForOne = sinkItem.getMaterial().getCutoutTypesAndPrices().get(Integer.valueOf(5)) / 100;
                    }else if(sinkItem.getType() == Sink.SINK_TYPE_19){
                        priceForOne = sinkItem.getMaterial().getCutoutTypesAndPrices().get(Integer.valueOf(15)) / 100;
                        name = "Вырез для накладной мойки (прямоугольный), без обработки" + "#" + sinkItem.getMaterial().getReceiptName();

                    }else if(sinkItem.getType() == Sink.SINK_TYPE_21 ){
                        priceForOne = sinkItem.getMaterial().getCutoutTypesAndPrices().get(Integer.valueOf(16)) / 100;
                        name = "Установка мойки в ровень со столешницей + вырез без обработки" + "#" + sinkItem.getMaterial().getReceiptName();

                    }  else {
                        if(sinkItem.getType() == Sink.SINK_TYPE_16){
                            name = "Вклейка мойки накладной#" + sinkItem.getMaterial().getReceiptName();
                        }else if(sinkItem.getType() == Sink.SINK_TYPE_17){
                            name = "Вклейка мойки подстольной прямоугольной#" + sinkItem.getMaterial().getReceiptName();
                        }else if(sinkItem.getType() == Sink.SINK_TYPE_20){
                            name = "Вклейка мойки подстольной круглой#" + sinkItem.getMaterial().getReceiptName();
                        }else{
                            name = "Вклейка мойки " + sinkItem.getModel().split(" ")[0] + "#" + sinkItem.getMaterial().getReceiptName();
                        }

                        priceForOne = (sinkItem.getMaterial().getSinkInstallTypesAndPrices().get(sinkItem.getInstallType() - 1)) / 100;
                    }

                    if(!name.equals("")) {
                        ReceiptItem receiptItemForInstallType = new ReceiptItem(name, units, count, currency, priceForOne);
                        sinkInstallTypeReceiptItems.add(receiptItemForInstallType);
                    }
                }

                //edgeType
                {
                    if (sinkItem.getType() != Sink.SINK_TYPE_16 &&
                            sinkItem.getType() != Sink.SINK_TYPE_19 &&
                            sinkItem.getType() != Sink.SINK_TYPE_21 ) {

                        String name = "none";
                        String units = "шт";
                        double count = sinkItem.getQuantity();
                        String currency = sinkItem.getMaterial().getSinkEdgeTypeCurrency();
                        double priceForOne = 0;
                        //System.out.println(material.getSinkEdgeTypesCircleAndPrices());
                        //System.out.println(material.getSinkEdgeTypesRectangleAndPrices());
                        if (sinkItem.getCutForm() == Sink.SINK_CUTOUT_RECTANGLE_FORM) {
                            name = "Обработка прямолинейной кромки мойки, Вариант №" + (sinkItem.getEdgeType()) + "#" + sinkItem.getMaterial().getReceiptName();
                            priceForOne = (sinkItem.getMaterial().getSinkEdgeTypesRectangleAndPrices().get(sinkItem.getEdgeType() - 1)) / 100;
                        } else if (sinkItem.getCutForm() == Sink.SINK_CUTOUT_CIRCLE_FORM) {
                            name = "Обработка криволинейной кромки мойки, Вариант №" + (sinkItem.getEdgeType()) + "#" + sinkItem.getMaterial().getReceiptName();
                            priceForOne = (sinkItem.getMaterial().getSinkEdgeTypesCircleAndPrices().get(sinkItem.getEdgeType() - 1)) / 100;
                        }

                        ReceiptItem receiptItemForEdgeType = new ReceiptItem(name, units, count, currency, priceForOne);
                        sinkInstallTypeReceiptItems.add(receiptItemForEdgeType);
                    }
                }
            }
        }

        return sinkInstallTypeReceiptItems;
    }

    public static ArrayList<ReceiptItem> getJointsReceiptList() {

        ArrayList<ReceiptItem> jointsReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : JointItem.getTableDesignerItemsList()) {
            if (item instanceof JointItem) {
                JointItem jointItem = (JointItem) item;

                String name = "Соединение элементов по " + ((jointItem.getType() == 1) ? "прямому" : "косому") + " стыку" + "#" + jointItem.getMaterial().getReceiptName();
                String units = "м.п.";
                double count = (jointItem.getLength()/1000) * jointItem.getQuantity();
                String currency = jointItem.getMaterial().getLeakGrooveCurrency();
                double priceForOne = (jointItem.getMaterial().getJointsTypesAndPrices().get(jointItem.getType() - 1)) / 100;

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                jointsReceiptItems.add(receiptItem);
            }
        }

        return jointsReceiptItems;
    }

    public static ArrayList<ReceiptItem> getCutoutsReceiptList() {

        ArrayList<ReceiptItem> cutoutsReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : CutoutItem.getTableDesignerItemsList()) {
            if (item instanceof CutoutItem) {
                CutoutItem cutoutItem = (CutoutItem) item;

                String name = "Вырез ";
                if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_1) name += "под питьевой кран. d = 12мм";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_2) name += "под смеситель. d = 35мм";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_3) name += "под варочную панель/раковину.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_4) name += "под розетку. d = 65мм";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_5) name += "под накладную мойку.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_6)
                    name += "под варочную панель вровень со столешницей.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_7) name += "под радиатор.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_8) name += "прямолиннейный. Без обработки.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_9) name += "криволинейный. Без обработки.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_10) name += "прямолинейный. С обработкой.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_11) name += "криволинейный. С обработкой.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_12)
                    name += "под раковину/мойку, для установки в уровень со столешницей.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_13) name += "под измельчитель.";
                else if (cutoutItem.getType() == Cutout.CUTOUT_TYPE_14) name += "под дозатор.";


                name += "#" + cutoutItem.getMaterial().getReceiptName();

                String units = "шт";
                double count = cutoutItem.getQuantity();
                String currency = cutoutItem.getMaterial().getCutoutCurrency();
                double priceForOne = (cutoutItem.getMaterial().getCutoutTypesAndPrices().get(cutoutItem.getType())) / 100;

                ReceiptItem receiptItemForCutout = new ReceiptItem(name, units, count, currency, priceForOne);

                cutoutsReceiptItems.add(receiptItemForCutout);
            }
        }

        for (TableDesignerItem item : RadiatorGroovesItem.getTableDesignerItemsList()) {
            if (item instanceof RadiatorGroovesItem) {
                RadiatorGroovesItem radiatorGroovesItem = (RadiatorGroovesItem) item;

                String name = "Проточки под радиатор ";
                name += "#" + radiatorGroovesItem.getMaterial().getReceiptName();

                String units = "шт";
                double count = radiatorGroovesItem.getQuantity();
                String currency = radiatorGroovesItem.getMaterial().getCutoutCurrency();
                double priceForOne = (radiatorGroovesItem.getMaterial().getCutoutTypesAndPrices().get(radiatorGroovesItem.getType())) / 100;

                ReceiptItem receiptItemForCutout = new ReceiptItem(name, units, count, currency, priceForOne);

                cutoutsReceiptItems.add(receiptItemForCutout);
            }
        }

        return cutoutsReceiptItems;
    }

    public static ArrayList<ReceiptItem> getGroovesReceiptList() {

        ArrayList<ReceiptItem> groovesReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : GroovesItem.getTableDesignerItemsList()) {
            if (item instanceof GroovesItem) {
                GroovesItem groovesItem = (GroovesItem) item;

                String name = "Проточки для стока воды, вариант №" + groovesItem.getType() + "#" + groovesItem.getMaterial().getReceiptName();
                ;
                String units = "шт";
                double count = groovesItem.getQuantity();
                String currency = groovesItem.getMaterial().getGroovesCurrency();
                double priceForOne = (groovesItem.getMaterial().getGroovesTypesAndPrices().get(groovesItem.getType() - 1)) / 100;

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                groovesReceiptItems.add(receiptItem);
            }
        }

        return groovesReceiptItems;
    }

    public static ArrayList<ReceiptItem> getRodsReceiptList() {

        ArrayList<ReceiptItem> rodsReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : RodsItem.getTableDesignerItemsList()) {
            if (item instanceof RodsItem) {
                RodsItem rodsItem = (RodsItem) item;

                String name = "Подставка под горячее, вариант №" + rodsItem.getType() + "#" + rodsItem.getMaterial().getReceiptName();
                ;
                String units = "шт";
                double count = rodsItem.getQuantity();
                String currency = rodsItem.getMaterial().getGroovesCurrency();
                double priceForOne = (rodsItem.getMaterial().getRodsTypesAndPrices().get(rodsItem.getType() - 1)) / 100;

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                rodsReceiptItems.add(receiptItem);
            }
        }

        return rodsReceiptItems;
    }

    public static ArrayList<ReceiptItem> getRadiusReceiptList() {

        ArrayList<ReceiptItem> radiusReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : RadiusItem.getTableDesignerItemsList()) {
            if (item instanceof RadiusItem) {
                RadiusItem radiusItem = (RadiusItem) item;

                String name = "Радиусный элемент" + "#" + radiusItem.getMaterial().getReceiptName();
                ;
                String units = "шт";
                double count = radiusItem.getQuantity();
                String currency = radiusItem.getMaterial().getRadiusElementCurrency();
                double priceForOne = (radiusItem.getMaterial().getRadiusElementPrice()) / 100;

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                radiusReceiptItems.add(receiptItem);
            }
        }

        return radiusReceiptItems;
    }

    public static ArrayList<ReceiptItem> getMetalFootingReceiptList() {

        ArrayList<ReceiptItem> metalFootReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : MetalFootingItem.getTableDesignerItemsList()) {
            if (item instanceof MetalFootingItem) {
                MetalFootingItem metalFootingItem = (MetalFootingItem) item;

                String name = "Металлокаркас";
                String units = "м.п.";
                double count = (metalFootingItem.getLength()/1000.0) * metalFootingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = ((metalFootingItem.getPaintingType() == 1) ? ProjectHandler.getDefaultMaterial().getMetalFootingPrices().get(0)/100  : ProjectHandler.getDefaultMaterial().getMetalFootingPrices().get(1)/100);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                metalFootReceiptItems.add(receiptItem);
            }
        }

        return metalFootReceiptItems;
    }

    public static ArrayList<ReceiptItem> getPlywoodReceiptList() {

        ArrayList<ReceiptItem> plywoodReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : PlywoodItem.getTableDesignerItemsList()) {
            if (item instanceof PlywoodItem) {
                PlywoodItem plywoodItem = (PlywoodItem) item;


                String name = "";
                if(plywoodItem.getMaterial().getMainType().equals("Кварцекерамический камень") ||
                        plywoodItem.getMaterial().getMainType().equals("Мраморный агломерат")){
                    name = "Подложка полимерная";
                }else{
                    name = "Подложка из фанеры";
                }
                name +=(plywoodItem.getPaintingType() == 1) ? " без покраски" : " с покраской";

                name += "#" + plywoodItem.getMaterial().getReceiptName();

                String units = "м.кв.";
                double count = (plywoodItem.getLength()/1000) * (plywoodItem.getWidth()/1000) * plywoodItem.getQuantity();
                String currency = "RUB";

                Material material = plywoodItem.getMaterial();
                currency = material.getPlywoodCurrency().get(0);

                double priceForOne = ((plywoodItem.getPaintingType() == 1) ? material.getPlywoodPrices().get(0)/100 : material.getPlywoodPrices().get(1)/100);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                plywoodReceiptItems.add(receiptItem);
            }
        }

        return plywoodReceiptItems;
    }

    public static ArrayList<ReceiptItem> getStonePolishingReceiptList() {

        ArrayList<ReceiptItem> stonePolishingReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : StonePolishingItem.getTableDesignerItemsList()) {
            if (item instanceof StonePolishingItem) {
                StonePolishingItem stonePolishingItem = (StonePolishingItem) item;

                String name = "Полировка поверхности камня";
                String units = "м.кв.";
                double count = (stonePolishingItem.getLength()/1000) * (stonePolishingItem.getWidth()/1000) * stonePolishingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = 0;

                Material material = stonePolishingItem.getMaterial();
                priceForOne = material.getStonePolishingPrice() / 100;
//                if(stonePolishingItem.getMaterial().getName().indexOf("Кварцевый агломерат") != -1 || stonePolishingItem.getMaterial().getName().indexOf("Натуральный камень") != -1){
//                    priceForOne = 5000;
//                }else{
//                    priceForOne = 700;
//                }

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                stonePolishingReceiptItems.add(receiptItem);
            }
        }

        return stonePolishingReceiptItems;
    }

    public static ArrayList<ReceiptItem> getSiphonReceiptList() {

        ArrayList<ReceiptItem> siphonReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : SiphonItem.getTableDesignerItemsList()) {
            if (item instanceof SiphonItem) {
                SiphonItem siphonItem = (SiphonItem) item;

                String name = "Сифон " + ((siphonItem.getType() == 1) ? "с одним выпуском" : "с двумя выпусками");
                String units = "шт";
                double count = siphonItem.getQuantity();
                String currency = "RUB";
//                double priceForOne = ((siphonItem.getType() == 1) ? 700 : 1000);
                double priceForOne = ((siphonItem.getType() == 1)?
                        ProjectHandler.getDefaultMaterial().getSiphonsTypesAndPrices().get(0)/100 :
                        ProjectHandler.getDefaultMaterial().getSiphonsTypesAndPrices().get(1)/100);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                siphonReceiptItems.add(receiptItem);
            }
        }

        return siphonReceiptItems;
    }

    public static ArrayList<ReceiptItem> getMeasurerReceiptList() {

        ArrayList<ReceiptItem> measurerReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : MeasurerItem.getTableDesignerItemsList()) {
            if (item instanceof MeasurerItem) {
                MeasurerItem measurerItem = (MeasurerItem) item;
                Material material = measurerItem.getMaterial();

                String name = measurerItem.getReceiptName();
                String units = "";
                double count = measurerItem.getQuantity();
                String currency = "RUB";
                double priceForOne = material.getMeasurerPrice() + measurerItem.getLength() * material.getMeasurerKMPrice();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                measurerReceiptItems.add(receiptItem);
            }
        }

        return measurerReceiptItems;
    }

    public static ArrayList<ReceiptItem> getDeliveryReceiptList() {

        ArrayList<ReceiptItem> deliveryReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : DeliveryItem.getTableDesignerItemsList()) {
            if (item instanceof DeliveryItem) {
                DeliveryItem deliveryItem = (DeliveryItem) item;
                Material material = deliveryItem.getMaterial();

                String name = deliveryItem.getReceiptName();
                String units = "";
                double count = deliveryItem.getQuantity();
                String currency = "RUB";
                double priceForOne = deliveryItem.getPriceForOne();

                System.out.println("DELIVERY ITEM PRICE FOR ONE = " + priceForOne);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                deliveryReceiptItems.add(receiptItem);
            }
        }

        return deliveryReceiptItems;
    }

    public static ArrayList<ReceiptItem> getMountingReceiptList() {

        ArrayList<ReceiptItem> mountingReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : MountingItem.getTableDesignerItemsList()) {
            if (item instanceof MountingItem) {
                MountingItem mountingItem = (MountingItem) item;

                String name = "Монтаж изделий в % (но не менее 4000 рублей)";
                String units = "";
                double count = mountingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = mountingItem.getPercent();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / ProjectHandler.getPriceMainCoefficient().doubleValue());
                mountingReceiptItems.add(receiptItem);
            }
        }

        return mountingReceiptItems;
    }

    public static ArrayList<ReceiptItem> getPlumbingAlveusReceiptList() {

        ArrayList<ReceiptItem> PlumbingAlveusReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : PlumbingAlveusItem.getTableDesignerItemsList()) {
            if (item instanceof PlumbingAlveusItem) {
                PlumbingAlveusItem plumbingAlveusItem = (PlumbingAlveusItem) item;

                String name = plumbingAlveusItem.getName() + ", " + plumbingAlveusItem.getSize() + "мм";
                String units = plumbingAlveusItem.getUnits();
                double count = plumbingAlveusItem.getQuantity();
                String currency = "RUB";
                double priceForOne = plumbingAlveusItem.getPriceForOne();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne/ProjectHandler.getPriceMainCoefficient().doubleValue());
                PlumbingAlveusReceiptItems.add(receiptItem);
            }
        }

        return PlumbingAlveusReceiptItems;
    }

    public static ArrayList<ReceiptItem> getPlumbingReceiptList() {

        ArrayList<ReceiptItem> plumbingReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : PlumbingItem.getTableDesignerItemsList()) {
            if (item instanceof PlumbingItem) {
                PlumbingItem plumbingItem = (PlumbingItem) item;

                String name = plumbingItem.getName() + ", " + plumbingItem.getSize() + "мм";
                String units = plumbingItem.getUnits();
                double count = plumbingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = plumbingItem.getPriceForOne();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne/ProjectHandler.getPriceMainCoefficient().doubleValue());
                plumbingReceiptItems.add(receiptItem);
            }
        }

        return plumbingReceiptItems;
    }

    public static ArrayList<ReceiptItem> getPalletReceiptList() {

        ArrayList<ReceiptItem> palletReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : PalletItem.getTableDesignerItemsList()) {
            if (item instanceof PalletItem) {
                PalletItem palletItem = (PalletItem) item;

                String name = palletItem.getName() + ", " + palletItem.getModel() + "мм";
                String units = palletItem.getUnits();
                double count = palletItem.getQuantity();
                String currency = "RUB";
                double priceForOne = palletItem.getPriceForOne();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne/ProjectHandler.getPriceMainCoefficient().doubleValue());
                palletReceiptItems.add(receiptItem);
            }
        }

        return palletReceiptItems;
    }

    public static ArrayList<ReceiptItem> getCustomReceiptList() {

        ArrayList<ReceiptItem> customReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : CustomItem.getTableDesignerItemsList()) {
            if (item instanceof CustomItem) {
                CustomItem customItem = (CustomItem) item;

                String name = customItem.getName();
                String units = customItem.getUnits();
                double count = customItem.getQuantity();
                String currency = "RUB";
                double priceForOne = customItem.getPrice();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne/ProjectHandler.getPriceMainCoefficient().doubleValue());
                customReceiptItems.add(receiptItem);
            }
        }

        return customReceiptItems;
    }

    public static ArrayList<ReceiptItem> getDiscountReceiptList() {

        ArrayList<ReceiptItem> customReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : CustomItem.getTableDesignerItemsList()) {
            if (item instanceof DiscountItem) {
                DiscountItem discountItem = (DiscountItem) item;

                String name = "Скидка";
                String units = "%";
                double count = discountItem.getQuantity();
                String currency = "RUB";
                double priceForOne = discountItem.getPercent();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / ProjectHandler.getPriceMainCoefficient().doubleValue());
                customReceiptItems.add(receiptItem);
            }
        }

        return customReceiptItems;
    }
    /** RECEIPT resources END */

    /**
     * MAIN TABLE part
     */

    AnchorPane anchorPaneMainHeader = null;
    AnchorPane anchorPaneAdditionalFeaturesHeader = null;
    AnchorPane anchorPaneMainWorkHeader = null;
    AnchorPane anchorPaneAdditionalWorkHeader = null;


    private void tableViewRefresh() {

        if(toggleGroupViewType.getSelectedToggle() == btnRowsView){
            showRowsView();
        }else{
            showCardsView();
        }

    }

    private static AnchorPane getDivider() {
        AnchorPane divider = new AnchorPane();
        divider.setMinHeight(25);
        return divider;
    }

    public static void exitAllItemsFromEditMode(){

        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(tableDesignerMainItemsList);
        allItems.addAll(tableDesignerAdditionalItemsList);
        allItems.addAll(tableDesignerMainWorkItemsList);
        allItems.addAll(tableDesignerAdditionalWorkItemsList);

        for(TableDesignerItem item : allItems){
            if(item instanceof StoneProductItem) item.exitEditMode();
        }
    }

    public static void updateMaterialsInProject() {
        System.out.println();

        exitAllItemsFromEditMode();



        MainWindow.getTableDesigner().accordionItemsRefreshSettings();

        //check all items and delete items with deleted material

        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(tableDesignerMainItemsList);
        allItems.addAll(tableDesignerAdditionalItemsList);
        allItems.addAll(tableDesignerMainWorkItemsList);
        allItems.addAll(tableDesignerAdditionalWorkItemsList);


        for (TableDesignerItem item : allItems) {

            if (item instanceof DependOnMaterial) {
                DependOnMaterial mItem = (DependOnMaterial) item;
                mItem.autoUpdateMaterial();

            }

        }
    }

    public static void updateWorkCoefficientsInCuttableItems(){
        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(tableDesignerMainItemsList);
        allItems.addAll(tableDesignerAdditionalItemsList);
        allItems.addAll(tableDesignerMainWorkItemsList);
        allItems.addAll(tableDesignerAdditionalWorkItemsList);

        for (TableDesignerItem item : allItems) {

            if (item instanceof Cuttable) {
                Cuttable mItem = (Cuttable) item;
                mItem.updateWorkCoefficient();
            }
        }
    }


    public static void updatePriceInRows() {
        ArrayList<TableDesignerItem> allItems = new ArrayList<>();
        allItems.addAll(tableDesignerMainItemsList);
        allItems.addAll(tableDesignerAdditionalItemsList);
        allItems.addAll(tableDesignerMainWorkItemsList);
        allItems.addAll(tableDesignerAdditionalWorkItemsList);


        if(ProjectHandler.getDefaultMaterial() == null) return;
        for (TableDesignerItem item : allItems) {

            item.updateRowPrice();

        }
    }

    public AnchorPane getAnchorPaneRoot() {
        return anchorPaneRoot;
    }

    public static ObservableList<TableDesignerItem> getTableDesignerMainItemsList() {
        return tableDesignerMainItemsList;
    }

    public static ObservableList<TableDesignerItem> getTableDesignerAdditionalItemsList() {
        return tableDesignerAdditionalItemsList;
    }

    public static ObservableList<TableDesignerItem> getTableDesignerAdditionalWorkItemsList() {
        return tableDesignerAdditionalWorkItemsList;
    }

    public static ObservableList<TableDesignerItem> getTableDesignerMainWorkItemsList() {
        return tableDesignerMainWorkItemsList;
    }

    public static ObservableList<TableDesignerItem> getTableDesignerAllItemsList(){
        ObservableList<TableDesignerItem> list = FXCollections.observableList(new ArrayList<TableDesignerItem>());
        list.addAll(tableDesignerMainItemsList);
        list.addAll(tableDesignerAdditionalItemsList);
        list.addAll(tableDesignerAdditionalWorkItemsList);
        list.addAll(tableDesignerMainWorkItemsList);

        return list;
    }
    private void initControlElements() {

        anchorPaneMenu = (AnchorPane) anchorPaneRoot.lookup("#anchorPaneMenu");
        btnRowsView = (ToggleButton) anchorPaneRoot.lookup("#btnRowsView");
        btnCardsView = (ToggleButton) anchorPaneRoot.lookup("#btnCardsView");
        btnRowsView.setToggleGroup(toggleGroupViewType);
        btnCardsView.setToggleGroup(toggleGroupViewType);
        btnCardsView.setSelected(true);

        scrollPaneItems = (ScrollPane) anchorPaneRoot.lookup("#scrollPaneItems");
        anchorPaneIntoScrollPaneItems = (AnchorPane) scrollPaneItems.getContent();

        accordionItems = (Accordion) anchorPaneIntoScrollPaneItems.lookup("#accordionItems");

        scrollPaneVBoxTable = (ScrollPane) anchorPaneRoot.lookup("#scrollPaneVBoxTable");
        scrollPaneVBoxTable.setFitToWidth(true);

        vBoxTableRowsZone = (VBox) scrollPaneVBoxTable.getContent();

        flowPaneTableCardsZone = new FlowPane();
        flowPaneTableCardsZone.setId("flowPaneCardsRoot");
    }

    private void initControlElementLogic() {


        toggleGroupViewType.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null){
                oldValue.setSelected(true);
                return;
            }

            if(newValue == btnRowsView){
                //showRows
                showRowsView();
            }else{
                //show cards
                showCardsView();
            }
        });

        scrollPaneItems.heightProperty().addListener((observable, oldValue, newValue) -> {
            anchorPaneIntoScrollPaneItems.setPrefHeight(newValue.doubleValue());
        });

        scrollPaneVBoxTable.widthProperty().addListener((observable, oldValue, newValue) -> {

            vBoxTableRowsZone.setPrefWidth(newValue.doubleValue());

            flowPaneTableCardsZone.setPrefWidth(newValue.doubleValue());
            labelMainItemsHeaderCards.setPrefWidth(newValue.doubleValue()-40);
            labelAddItemsHeaderCards.setPrefWidth(newValue.doubleValue()-40);
            labelMainWorksHeaderCards.setPrefWidth(newValue.doubleValue()-40);
            labelAddWorksHeaderCards.setPrefWidth(newValue.doubleValue()-40);

        });

        accordionItems.expandedPaneProperty().addListener((observableValue, titledPane, t1) -> {
//            if(t1 != null){
//
//
//                double accordionHeight = accordionItems.getPanes().size()* t1.getBoundsInLocal().getHeight();
//                double paneHeight = t1.getContent().getBoundsInLocal().getHeight();
//                double allHeight = accordionHeight + paneHeight;
//                double t1Position = t1.getBoundsInParent().getMinY();
//
//                scrollPaneItems.setVvalue(t1Position/allHeight);
//                System.out.println("t1.getBoundsInParent().getMinY() = " + t1.getBoundsInParent().getMinY());
//                System.out.println("accordionItems.getHeight()= " + t1.getContent().getBoundsInLocal().getHeight());
//                System.out.println("t1.getBoundsInParent().getMinY()/accordionItems.getHeight() = " + t1.getBoundsInLocal().getMinY()/accordionItems.getHeight());
//            }
        });
    }

    private void showRowsView(){
        scrollPaneVBoxTable.setContent(vBoxTableRowsZone);

        vBoxTableRowsZone.setId("vBoxTableRowsZone");

        vBoxTableRowsZone.getChildren().clear();

        int rowNumber = 1;

        //createMainHeader:
        {
            if (anchorPaneMainHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/MainHeader.fxml")
                );

                try {
                    anchorPaneMainHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneMainHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneMainHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneMainHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneMainHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneMainHeader.lookup("#labelMaterial");
                Label labelDepth = (Label) anchorPaneMainHeader.lookup("#labelDepth");
                Label labelWidth = (Label) anchorPaneMainHeader.lookup("#labelWidth");
                Label labelHeight = (Label) anchorPaneMainHeader.lookup("#labelHeight");
                Label labelQuantity = (Label) anchorPaneMainHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneMainHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneMainHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelDepth, Priority.ALWAYS);
                HBox.setHgrow(labelWidth, Priority.ALWAYS);
                HBox.setHgrow(labelHeight, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);


            }
            if (tableDesignerMainItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(anchorPaneMainHeader);
        }
        //create main items:
        for (TableDesignerItem item : tableDesignerMainItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (tableDesignerMainItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());

        //create Additional features header:
        {
            if (anchorPaneAdditionalFeaturesHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/AdditionalFeaturesHeader.fxml")
                );

                try {
                    anchorPaneAdditionalFeaturesHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelMaterial");
                Label labelNull1 = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNull1");
                Label labelNull2 = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNull2");
                Label labelNull3 = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelNull3");
                Label labelQuantity = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneAdditionalFeaturesHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelNull1, Priority.ALWAYS);
                HBox.setHgrow(labelNull2, Priority.ALWAYS);
                HBox.setHgrow(labelNull3, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);


            }
            if (tableDesignerAdditionalItemsList.size() != 0)
                vBoxTableRowsZone.getChildren().add(anchorPaneAdditionalFeaturesHeader);
        }
        //create Additional features items:
        for (TableDesignerItem item : tableDesignerAdditionalItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (tableDesignerAdditionalItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());

        //create Main work header:
        {
            if (anchorPaneMainWorkHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/MainWorkHeader.fxml")
                );

                try {
                    anchorPaneMainWorkHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneMainWorkHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneMainWorkHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneMainWorkHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneMainWorkHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneMainWorkHeader.lookup("#labelMaterial");
                Label labelNull1 = (Label) anchorPaneMainWorkHeader.lookup("#labelNull1");
                Label labelNull2 = (Label) anchorPaneMainWorkHeader.lookup("#labelNull2");
                Label labelNull3 = (Label) anchorPaneMainWorkHeader.lookup("#labelNull3");
                Label labelQuantity = (Label) anchorPaneMainWorkHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneMainWorkHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneMainWorkHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelNull1, Priority.ALWAYS);
                HBox.setHgrow(labelNull2, Priority.ALWAYS);
                HBox.setHgrow(labelNull3, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);


            }
            if (tableDesignerMainWorkItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(anchorPaneMainWorkHeader);
        }
        //create Main work items:
        for (TableDesignerItem item : tableDesignerMainWorkItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (tableDesignerMainWorkItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());

        //create Additional work header:
        {
            if (anchorPaneAdditionalWorkHeader == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        this.getClass().getResource("/fxmls/TableDesigner/TableItems/addWorkHeader.fxml")
                );

                try {
                    anchorPaneAdditionalWorkHeader = fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label labelNumber = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNumber");
                Label labelMain = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelMain");
                Label labelName = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelName");
                Label labelImage = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelImage");
                Label labelMaterial = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelMaterial");
                Label labelNull1 = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNull1");
                Label labelNull2 = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNull2");
                Label labelNull3 = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelNull3");
                Label labelQuantity = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelQuantity");
                Label labelPrice = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelPrice");
                Label labelButtons = (Label) anchorPaneAdditionalWorkHeader.lookup("#labelButtons");

                HBox.setHgrow(labelMain, Priority.ALWAYS);
                HBox.setHgrow(labelName, Priority.ALWAYS);
                HBox.setHgrow(labelMaterial, Priority.ALWAYS);
                HBox.setHgrow(labelNull1, Priority.ALWAYS);
                HBox.setHgrow(labelNull2, Priority.ALWAYS);
                HBox.setHgrow(labelNull3, Priority.ALWAYS);
                HBox.setHgrow(labelQuantity, Priority.ALWAYS);
                HBox.setHgrow(labelPrice, Priority.ALWAYS);


            }
            if (tableDesignerAdditionalWorkItemsList.size() != 0)
                vBoxTableRowsZone.getChildren().add(anchorPaneAdditionalWorkHeader);
        }
        //create Additional work items:
        for (TableDesignerItem item : tableDesignerAdditionalWorkItemsList) {
            item.setRowNumber(rowNumber++);
            vBoxTableRowsZone.getChildren().add(item.getTableView());
        }
        if (tableDesignerAdditionalWorkItemsList.size() != 0) vBoxTableRowsZone.getChildren().add(getDivider());
    }

    private void showCardsView(){

        scrollPaneVBoxTable.setContent(flowPaneTableCardsZone);
        flowPaneTableCardsZone.getChildren().clear();

        int rowNumber = 1;

        //createHeaders:
        labelMainItemsHeaderCards.setPrefHeight(55);
        labelMainItemsHeaderCards.getStyleClass().add("cards-header");
        labelAddItemsHeaderCards.setPrefHeight(55);
        labelAddItemsHeaderCards.getStyleClass().add("cards-header");
        labelMainWorksHeaderCards.setPrefHeight(55);
        labelMainWorksHeaderCards.getStyleClass().add("cards-header");
        labelAddWorksHeaderCards.setPrefHeight(55);
        labelAddWorksHeaderCards.getStyleClass().add("cards-header");


        if (tableDesignerMainItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelMainItemsHeaderCards);
        //create main items:
        for (TableDesignerItem item : tableDesignerMainItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }


        if (tableDesignerAdditionalItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelAddItemsHeaderCards);
        //create Additional features items:
        for (TableDesignerItem item : tableDesignerAdditionalItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }


        if (tableDesignerMainWorkItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelMainWorksHeaderCards);
        //create Main work items:
        for (TableDesignerItem item : tableDesignerMainWorkItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }


        if (tableDesignerAdditionalWorkItemsList.size() != 0) flowPaneTableCardsZone.getChildren().add(labelAddWorksHeaderCards);
        //create Additional work items:
        for (TableDesignerItem item : tableDesignerAdditionalWorkItemsList) {
            item.setRowNumber(rowNumber++);
            if(item.getCardView() == null) continue;
            flowPaneTableCardsZone.getChildren().add(item.getCardView());
        }

    }

    private void initAccordionItems() {
        //System.out.println(accordionItems);



        if (accordionItems == null) return;



        accordionItems.getPanes().get(0).setContent(StoneProductItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(1).setContent(SinkItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(2).setContent(EdgeItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(3).setContent(BorderItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(4).setContent(RadiusItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(5).setContent(CutoutItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(6).setContent(StonePolishingItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(7).setContent(JointItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(8).setContent(LeakGrooveItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(9).setContent(RadiatorGroovesItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(10).setContent(GroovesItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(11).setContent(RodsItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(12).setContent(PlywoodItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(13).setContent(MetalFootingItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(14).setContent(MeasurerItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(15).setContent(DeliveryItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(16).setContent(MountingItem.getAnchorPaneSettingsView());

        accordionItems.getPanes().get(17).setContent(PlumbingItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(18).setContent(PalletItem.getAnchorPaneSettingsView());

        accordionItems.getPanes().get(19).setContent(CustomItem.getAnchorPaneSettingsView());
        accordionItems.getPanes().get(20).setContent(DiscountItem.getAnchorPaneSettingsView());

        for(TitledPane node : accordionItems.getPanes()){
            node.setOnMouseEntered(mouseEvent -> {
                Main.getMainScene().setCursor(Cursor.DEFAULT);});
        }

        accordionItems.expandedPaneProperty().addListener((observable, oldValue, newValue) ->{

            if (newValue != null && newValue.getContent() == StoneProductItem.getAnchorPaneSettingsView()){StoneProductItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == SinkItem.getAnchorPaneSettingsView()){SinkItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == EdgeItem.getAnchorPaneSettingsView()){EdgeItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == BorderItem.getAnchorPaneSettingsView()){BorderItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == RadiusItem.getAnchorPaneSettingsView()){RadiusItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == CutoutItem.getAnchorPaneSettingsView()){CutoutItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == StonePolishingItem.getAnchorPaneSettingsView()){StonePolishingItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == JointItem.getAnchorPaneSettingsView()){JointItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == LeakGrooveItem.getAnchorPaneSettingsView()){LeakGrooveItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == RadiatorGroovesItem.getAnchorPaneSettingsView()){RadiatorGroovesItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == GroovesItem.getAnchorPaneSettingsView()){GroovesItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == RodsItem.getAnchorPaneSettingsView()){RodsItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == PlywoodItem.getAnchorPaneSettingsView()){PlywoodItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == MetalFootingItem.getAnchorPaneSettingsView()){MetalFootingItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == MeasurerItem.getAnchorPaneSettingsView()){MeasurerItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == DeliveryItem.getAnchorPaneSettingsView()){DeliveryItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == MountingItem.getAnchorPaneSettingsView()){MountingItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == PlumbingItem.getAnchorPaneSettingsView()){PlumbingItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == PalletItem.getAnchorPaneSettingsView()){PalletItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == CustomItem.getAnchorPaneSettingsView()){CustomItem.settingsControlElementsRefresh();}
            else if (newValue != null && newValue.getContent() == DiscountItem.getAnchorPaneSettingsView()){DiscountItem.settingsControlElementsRefresh();}
        });

    }



    public static void openSettings(Class c){
        int index = 0;
        if(c == StoneProductItem.class){index = 0;}
        else if(c == SinkItem.class){index = 1;}
        else if(c == EdgeItem.class){index = 2;}
        else if(c == BorderItem.class){index = 3;}
        else if(c == RadiusItem.class){index = 4;}
        else if(c == CutoutItem.class){index = 5;}
        else if(c == StonePolishingItem.class){index = 6;}
        else if(c == JointItem.class){index = 7;}
        else if(c == LeakGrooveItem.class){index = 8;}
        else if(c == RadiatorGroovesItem.class){index = 9;}
        else if(c == GroovesItem.class){index = 10;}
        else if(c == RodsItem.class){index = 11;}
        else if(c == PlywoodItem.class){index = 12;}
        else if(c == MetalFootingItem.class){index = 13;}
//        else if(c == SiphonItem.class){index = 14;}
        else if(c == MeasurerItem.class){index = 14;}
        else if(c == DeliveryItem.class){index = 15;}
        else if(c == MountingItem.class){index = 16;}
        else if(c == PlumbingItem.class){index = 17;}
        else if(c == PalletItem.class){index = 18;}
        else if(c == CustomItem.class){index = 19;}
        else if(c == DiscountItem.class){index = 20;}
        else return;

        accordionItems.getPanes().get(index).setExpanded(true);
    }

    public void accordionItemsRefreshSettings() {
        StoneProductItem.settingsControlElementsRefresh();
        SinkItem.settingsControlElementsRefresh();
        EdgeItem.settingsControlElementsRefresh();
        BorderItem.settingsControlElementsRefresh();
        RadiusItem.settingsControlElementsRefresh();
        CutoutItem.settingsControlElementsRefresh();
        StonePolishingItem.settingsControlElementsRefresh();
        JointItem.settingsControlElementsRefresh();
        LeakGrooveItem.settingsControlElementsRefresh();
        RadiatorGroovesItem.settingsControlElementsRefresh();
        GroovesItem.settingsControlElementsRefresh();
        RodsItem.settingsControlElementsRefresh();
        PlywoodItem.settingsControlElementsRefresh();
        MetalFootingItem.settingsControlElementsRefresh();
        MeasurerItem.settingsControlElementsRefresh();
        DeliveryItem.settingsControlElementsRefresh();
        MountingItem.settingsControlElementsRefresh();
        PlumbingItem.settingsControlElementsRefresh();
        PalletItem.settingsControlElementsRefresh();
        CustomItem.settingsControlElementsRefresh();
        DiscountItem.settingsControlElementsRefresh();
    }

    public JSONObject getJsonView() {
        System.out.println("TableDesigner.getJsonView()");
        JSONObject jsonObject = new JSONObject();

        JSONArray tableDesignerMainItemsListJSONArray = new JSONArray();
        JSONArray tableDesignerAdditionalItemsListJSONArray = new JSONArray();
        JSONArray tableDesignerMainWorkItemsListJSONArray = new JSONArray();
        JSONArray tableDesignerAdditionalWorkItemsListJSONArray = new JSONArray();

        for (TableDesignerItem item : tableDesignerMainItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerMainItemsListJSONArray.add(item.getJsonView());
        }
        for (TableDesignerItem item : tableDesignerAdditionalItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerAdditionalItemsListJSONArray.add(item.getJsonView());
        }
        for (TableDesignerItem item : tableDesignerMainWorkItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerMainWorkItemsListJSONArray.add(item.getJsonView());
        }
        for (TableDesignerItem item : tableDesignerAdditionalWorkItemsList) {
            if (item.getJsonView() == null) continue;
            tableDesignerAdditionalWorkItemsListJSONArray.add(item.getJsonView());
        }

        jsonObject.put("MainItemsList", tableDesignerMainItemsListJSONArray);
        jsonObject.put("AdditionalItemsList", tableDesignerAdditionalItemsListJSONArray);
        jsonObject.put("MainWorkItemsList", tableDesignerMainWorkItemsListJSONArray);
        jsonObject.put("AdditionalWorkItemsList", tableDesignerAdditionalWorkItemsListJSONArray);

        return jsonObject;
    }

    public static TableDesigner initFromJSON(JSONObject jsonObject) {
        TableDesigner tableDesigner = new TableDesigner();

        //!!! radius item should be init after StoneProduct item because radius item depend on StoneProductItem

        JSONArray tableDesignerMainItemsListJSONArray = (JSONArray) jsonObject.get("MainItemsList");
        JSONArray tableDesignerAdditionalItemsListJSONArray = (JSONArray) jsonObject.get("AdditionalItemsList");
        JSONArray tableDesignerMainWorkItemsListJSONArray = (JSONArray) jsonObject.get("MainWorkItemsList");
        JSONArray tableDesignerAdditionalWorkItemsListJSONArray = (JSONArray) jsonObject.get("AdditionalWorkItemsList");


        for (Object obj : tableDesignerMainItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);

                if(tableDesignerItem != null) StoneProductItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) BorderItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CutoutItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiatorGroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) EdgeItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) GroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RodsItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SinkItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SiphonItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DeliveryItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) JointItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) LeakGrooveItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MeasurerItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiusItem.getTableDesignerItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MetalFootingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlywoodItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) StonePolishingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MountingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingAlveusItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PalletItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CustomItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DiscountItem.getTableDesignerItemsList().add(tableDesignerItem);
            }
        }

        for (Object obj : tableDesignerAdditionalItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);

                if(tableDesignerItem != null) StoneProductItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) BorderItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CutoutItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiatorGroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) EdgeItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) GroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RodsItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SinkItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SiphonItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DeliveryItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) JointItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) LeakGrooveItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MeasurerItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiusItem.getTableDesignerItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MetalFootingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlywoodItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) StonePolishingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MountingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingAlveusItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PalletItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CustomItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DiscountItem.getTableDesignerItemsList().add(tableDesignerItem);
            }
        }

        for (Object obj : tableDesignerMainWorkItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);

                if(tableDesignerItem != null) StoneProductItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) BorderItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CutoutItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiatorGroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) EdgeItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) GroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RodsItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SinkItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SiphonItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DeliveryItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) JointItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) LeakGrooveItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MeasurerItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiusItem.getTableDesignerItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MetalFootingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlywoodItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) StonePolishingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MountingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingAlveusItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PalletItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CustomItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DiscountItem.getTableDesignerItemsList().add(tableDesignerItem);
            }
        }

        for (Object obj : tableDesignerAdditionalWorkItemsListJSONArray) {
            JSONObject objectItem = (JSONObject) obj;
            String itemName = (String) objectItem.get("itemName");

            TableDesignerItem tableDesignerItem = null;
            if (itemName.equals("StoneProductItem")) {
                tableDesignerItem = StoneProductItem.initFromJSON(objectItem);

                if(tableDesignerItem != null) StoneProductItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("BorderItem")) {
                tableDesignerItem = BorderItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) BorderItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CutoutItem")) {
                tableDesignerItem = CutoutItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CutoutItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiatorGroovesItem")) {
                tableDesignerItem = RadiatorGroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiatorGroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("EdgeItem")) {
                tableDesignerItem = EdgeItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) EdgeItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("GroovesItem")) {
                tableDesignerItem = GroovesItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) GroovesItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RodsItem")) {
                tableDesignerItem = RodsItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RodsItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SinkItem")) {
                tableDesignerItem = SinkItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SinkItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("SiphonItem")) {
                tableDesignerItem = SiphonItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) SiphonItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DeliveryItem")) {
                tableDesignerItem = DeliveryItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DeliveryItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("JointItem")) {
                tableDesignerItem = JointItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) JointItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("LeakGrooveItem")) {
                tableDesignerItem = LeakGrooveItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) LeakGrooveItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MeasurerItem")) {
                tableDesignerItem = MeasurerItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MeasurerItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("RadiusItem")) {
                tableDesignerItem = RadiusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) RadiusItem.getTableDesignerItemsList().add(tableDesignerItem);
            }//should be above Stone
            else if (itemName.equals("MetalFootingItem")) {
                tableDesignerItem = MetalFootingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MetalFootingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlywoodItem")) {
                tableDesignerItem = PlywoodItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlywoodItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("StonePolishingItem")) {
                tableDesignerItem = StonePolishingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) StonePolishingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("MountingItem")) {
                tableDesignerItem = MountingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) MountingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingAlveusItem")) {
                tableDesignerItem = PlumbingAlveusItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingAlveusItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PlumbingItem")) {
                tableDesignerItem = PlumbingItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PlumbingItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("PalletItem")) {
                tableDesignerItem = PalletItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) PalletItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("CustomItem")) {
                tableDesignerItem = CustomItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) CustomItem.getTableDesignerItemsList().add(tableDesignerItem);
            } else if (itemName.equals("DiscountItem")) {
                tableDesignerItem = DiscountItem.initFromJSON(objectItem);
                if(tableDesignerItem != null) DiscountItem.getTableDesignerItemsList().add(tableDesignerItem);
            }
        }

        return tableDesigner;
    }

}
