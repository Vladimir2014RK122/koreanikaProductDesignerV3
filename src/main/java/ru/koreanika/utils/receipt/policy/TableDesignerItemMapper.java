package ru.koreanika.utils.receipt.policy;

import ru.koreanika.common.material.Material;
import ru.koreanika.project.Project;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.tableDesigner.Items.*;
import ru.koreanika.utils.receipt.domain.ReceiptItem;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.util.ArrayList;
import java.util.List;

public class TableDesignerItemMapper {

    public static List<ReceiptItem> getEdgesReceiptList() {
        List<ReceiptItem> edgesReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof EdgeItem edgeItem) {
                String name = "Кромка, Вариант №" + edgeItem.getType() + "#" + edgeItem.getMaterial().getReceiptName();
                String units = "м.п.";
                double count = edgeItem.getLength() / 1000.0 * edgeItem.getQuantity();
                String currency = edgeItem.getMaterial().getEdgesCurrency();
                double priceForOne = edgeItem.getMaterial().getEdgesAndPrices().get(edgeItem.getType());

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                edgesReceiptItems.add(receiptItem);
            }
        }
        return edgesReceiptItems;
    }

    public static List<ReceiptItem> getLeakGroovesReceiptItems() {
        List<ReceiptItem> leakGroovesReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof LeakGrooveItem leakGrooveItem) {
                if (leakGrooveItem.getMaterial().getName().indexOf("Кварцевый агломерат") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Натуральный камень") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Dektone") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Мраморный агломерат") != -1 ||
                        leakGrooveItem.getMaterial().getName().indexOf("Кварцекерамический камень") != -1) {

                    String name = "Выборка капельника" + "#" + leakGrooveItem.getMaterial().getReceiptName();
                    String units = "м.п.";
                    double count = (leakGrooveItem.getLength() / 1000) * leakGrooveItem.getQuantity();
                    String currency = leakGrooveItem.getMaterial().getLeakGrooveCurrency();
                    double priceForOne = leakGrooveItem.getMaterial().getLeakGroovePrice() / 100;

                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    leakGroovesReceiptItems.add(receiptItem);
                } else {
                    String name = "Подгиб камня к каплесборником" + "#" + leakGrooveItem.getMaterial().getReceiptName();
                    String units = "м.п.";
                    double count = (leakGrooveItem.getLength() / 1000);
                    String currency = leakGrooveItem.getMaterial().getStoneHemCurrency();
                    double priceForOne = leakGrooveItem.getMaterial().getStoneHemPrice() / 100;

                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    leakGroovesReceiptItems.add(receiptItem);
                }
            }
        }
        return leakGroovesReceiptItems;
    }

    public static List<ReceiptItem> getBordersReceiptList() {
        List<ReceiptItem> bordersReceiptItems = new ArrayList<>();
        List<ReceiptItem> bordersTopCutReceiptItems = new ArrayList<>();
        List<ReceiptItem> bordersSideCutReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof BorderItem borderItem) {
                //Border:
                {
                    String name = "Бортик, Вариант №" + borderItem.getType() + "#" + borderItem.getMaterial().getReceiptName();
                    String units = "м.п.";
                    double count = borderItem.getLength() / 1000.0 * borderItem.getQuantity();
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
                    double count = borderItem.getLength() / 1000.0 * borderItem.getQuantity();
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

    public static List<ReceiptItem> getSinkAcrylReceiptList() {
        List<ReceiptItem> sinkReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof SinkItem sinkItem) {
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
                    priceForOne = sinkItem.getMaterial().getSinkCommonTypesAndPrices().get(sinkItem.getType()) / 100.0;
                } else {
                    if (sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0]) == null) {
                        //sink unavailable
                        priceForOne = -1;
                        count = 1;
                    } else {
                        priceForOne = (sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0])) / 100.0;
                    }
                }

                if (priceForOne != 0) {
                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    sinkReceiptItems.add(receiptItem);
                }

            }
        }

        return sinkReceiptItems;
    }

    public static List<ReceiptItem> getSinkQuarzReceiptList() {
        List<ReceiptItem> sinkReceiptItems = new ArrayList<>();

        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof SinkItem sinkItem) {
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
                    priceForOne = sinkItem.getMaterial().getSinkCommonTypesAndPrices().get(sinkItem.getType()) / 100;
                } else {
                    System.out.println(sinkItem.getMaterial().getAvailableSinkModels() + "  " + sinkItem.getModel().split(" ")[0]);
                    priceForOne = (sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0])) / 100;
                }

                if (priceForOne != 0) {
                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    sinkReceiptItems.add(receiptItem);
                }
            }
        }

        return sinkReceiptItems;
    }

    public static List<ReceiptItem> getSinkInstallReceiptList() {
        List<ReceiptItem> sinkInstallTypeReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof SinkItem sinkItem) {
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
                    } else if (sinkItem.getType() == Sink.SINK_TYPE_19) {
                        priceForOne = sinkItem.getMaterial().getCutoutTypesAndPrices().get(Integer.valueOf(15)) / 100;
                        name = "Вырез для накладной мойки (прямоугольный), без обработки" + "#" + sinkItem.getMaterial().getReceiptName();

                    } else if (sinkItem.getType() == Sink.SINK_TYPE_21) {
                        priceForOne = sinkItem.getMaterial().getCutoutTypesAndPrices().get(Integer.valueOf(16)) / 100;
                        name = "Установка мойки в ровень со столешницей + вырез без обработки" + "#" + sinkItem.getMaterial().getReceiptName();

                    } else {
                        if (sinkItem.getType() == Sink.SINK_TYPE_16) {
                            name = "Вклейка мойки накладной#" + sinkItem.getMaterial().getReceiptName();
                        } else if (sinkItem.getType() == Sink.SINK_TYPE_17) {
                            name = "Вклейка мойки подстольной прямоугольной#" + sinkItem.getMaterial().getReceiptName();
                        } else if (sinkItem.getType() == Sink.SINK_TYPE_20) {
                            name = "Вклейка мойки подстольной круглой#" + sinkItem.getMaterial().getReceiptName();
                        } else {
                            name = "Вклейка мойки " + sinkItem.getModel().split(" ")[0] + "#" + sinkItem.getMaterial().getReceiptName();
                        }

                        priceForOne = (sinkItem.getMaterial().getSinkInstallTypesAndPrices().get(sinkItem.getInstallType() - 1)) / 100;
                    }

                    if (!name.isEmpty()) {
                        ReceiptItem receiptItemForInstallType = new ReceiptItem(name, units, count, currency, priceForOne);
                        sinkInstallTypeReceiptItems.add(receiptItemForInstallType);
                    }
                }

                //edgeType
                {
                    if (sinkItem.getType() != Sink.SINK_TYPE_16 &&
                            sinkItem.getType() != Sink.SINK_TYPE_19 &&
                            sinkItem.getType() != Sink.SINK_TYPE_21) {

                        String name = "none";
                        String units = "шт";
                        double count = sinkItem.getQuantity();
                        String currency = sinkItem.getMaterial().getSinkEdgeTypeCurrency();
                        double priceForOne = 0;
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

    public static List<ReceiptItem> getJointsReceiptList() {
        List<ReceiptItem> jointsReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof JointItem jointItem) {
                String name = "Соединение элементов по " + ((jointItem.getType() == 1) ? "прямому" : "косому") + " стыку" + "#" + jointItem.getMaterial().getReceiptName();
                String units = "м.п.";
                double count = (jointItem.getLength() / 1000) * jointItem.getQuantity();
                String currency = jointItem.getMaterial().getLeakGrooveCurrency();
                double priceForOne = (jointItem.getMaterial().getJointsTypesAndPrices().get(jointItem.getType() - 1)) / 100;

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                jointsReceiptItems.add(receiptItem);
            }
        }

        return jointsReceiptItems;
    }

    public static List<ReceiptItem> getCutoutsReceiptList() {
        List<ReceiptItem> cutoutsReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof CutoutItem cutoutItem) {
                String name = "Вырез ";
                switch (cutoutItem.getType()) {
                    case Cutout.CUTOUT_TYPE_1 -> name += "под питьевой кран. d = 12мм";
                    case Cutout.CUTOUT_TYPE_2 -> name += "под смеситель. d = 35мм";
                    case Cutout.CUTOUT_TYPE_3 -> name += "под варочную панель/раковину.";
                    case Cutout.CUTOUT_TYPE_4 -> name += "под розетку. d = 65мм";
                    case Cutout.CUTOUT_TYPE_5 -> name += "под накладную мойку.";
                    case Cutout.CUTOUT_TYPE_6 -> name += "под варочную панель вровень со столешницей.";
                    case Cutout.CUTOUT_TYPE_7 -> name += "под радиатор.";
                    case Cutout.CUTOUT_TYPE_8 -> name += "прямолиннейный. Без обработки.";
                    case Cutout.CUTOUT_TYPE_9 -> name += "криволинейный. Без обработки.";
                    case Cutout.CUTOUT_TYPE_10 -> name += "прямолинейный. С обработкой.";
                    case Cutout.CUTOUT_TYPE_11 -> name += "криволинейный. С обработкой.";
                    case Cutout.CUTOUT_TYPE_12 -> name += "под раковину/мойку, для установки в уровень со столешницей.";
                    case Cutout.CUTOUT_TYPE_13 -> name += "под измельчитель.";
                    case Cutout.CUTOUT_TYPE_14 -> name += "под дозатор.";
                }
                name += "#" + cutoutItem.getMaterial().getReceiptName();

                String units = "шт";
                double count = cutoutItem.getQuantity();
                String currency = cutoutItem.getMaterial().getCutoutCurrency();
                double priceForOne = (cutoutItem.getMaterial().getCutoutTypesAndPrices().get(cutoutItem.getType())) / 100;

                ReceiptItem receiptItemForCutout = new ReceiptItem(name, units, count, currency, priceForOne);

                cutoutsReceiptItems.add(receiptItemForCutout);
            }
        }

        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof RadiatorGroovesItem radiatorGroovesItem) {
                String name = "Проточки под радиатор #" + radiatorGroovesItem.getMaterial().getReceiptName();
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

    public static List<ReceiptItem> getGroovesReceiptList() {
        List<ReceiptItem> groovesReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof GroovesItem groovesItem) {
                String name = "Проточки для стока воды, вариант №" + groovesItem.getType() + "#" + groovesItem.getMaterial().getReceiptName();
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

    public static List<ReceiptItem> getRodsReceiptList() {
        List<ReceiptItem> rodsReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof RodsItem rodsItem) {
                String name = "Подставка под горячее, вариант №" + rodsItem.getType() + "#" + rodsItem.getMaterial().getReceiptName();
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

    public static List<ReceiptItem> getRadiusReceiptList() {
        List<ReceiptItem> radiusReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof RadiusItem radiusItem) {
                String name = "Радиусный элемент" + "#" + radiusItem.getMaterial().getReceiptName();
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

    public static List<ReceiptItem> getMetalFootingReceiptList() {
        List<ReceiptItem> metalFootReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalWorkItemsList()) {
            if (item instanceof MetalFootingItem metalFootingItem) {
                String name = "Металлокаркас";
                String units = "м.п.";
                double count = (metalFootingItem.getLength() / 1000.0) * metalFootingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = ((metalFootingItem.getPaintingType() == 1) ? Project.getDefaultMaterial().getMetalFootingPrices().get(0) / 100 : Project.getDefaultMaterial().getMetalFootingPrices().get(1) / 100);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                metalFootReceiptItems.add(receiptItem);
            }
        }

        return metalFootReceiptItems;
    }

    public static List<ReceiptItem> getPlywoodReceiptList() {
        List<ReceiptItem> plywoodReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalWorkItemsList()) {
            if (item instanceof PlywoodItem plywoodItem) {
                String name = "";
                if (plywoodItem.getMaterial().getMainType().equals("Кварцекерамический камень") ||
                        plywoodItem.getMaterial().getMainType().equals("Мраморный агломерат")) {
                    name = "Подложка полимерная";
                } else {
                    name = "Подложка из фанеры";
                }
                name += (plywoodItem.getPaintingType() == 1) ? " без покраски" : " с покраской";
                name += "#" + plywoodItem.getMaterial().getReceiptName();

                String units = "м.кв.";
                double count = (plywoodItem.getLength() / 1000) * (plywoodItem.getWidth() / 1000) * plywoodItem.getQuantity();

                Material material = plywoodItem.getMaterial();
                String currency = material.getPlywoodCurrency().get(0);

                double priceForOne = ((plywoodItem.getPaintingType() == 1) ? material.getPlywoodPrices().get(0) / 100 : material.getPlywoodPrices().get(1) / 100);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                plywoodReceiptItems.add(receiptItem);
            }
        }

        return plywoodReceiptItems;
    }

    public static List<ReceiptItem> getStonePolishingReceiptList() {
        List<ReceiptItem> stonePolishingReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalWorkItemsList()) {
            if (item instanceof StonePolishingItem stonePolishingItem) {
                String name = "Полировка поверхности камня";
                String units = "м.кв.";
                double count = (stonePolishingItem.getLength() / 1000) * (stonePolishingItem.getWidth() / 1000) * stonePolishingItem.getQuantity();
                String currency = "RUB";

                Material material = stonePolishingItem.getMaterial();
                double priceForOne = material.getStonePolishingPrice() / 100;

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                stonePolishingReceiptItems.add(receiptItem);
            }
        }

        return stonePolishingReceiptItems;
    }

    public static List<ReceiptItem> getSiphonReceiptList() {
        List<ReceiptItem> siphonReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof SiphonItem siphonItem) {
                String name = "Сифон " + ((siphonItem.getType() == 1) ? "с одним выпуском" : "с двумя выпусками");
                String units = "шт";
                double count = siphonItem.getQuantity();
                String currency = "RUB";
                double priceForOne = ((siphonItem.getType() == 1) ?
                        Project.getDefaultMaterial().getSiphonsTypesAndPrices().get(0) / 100 :
                        Project.getDefaultMaterial().getSiphonsTypesAndPrices().get(1) / 100);

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                siphonReceiptItems.add(receiptItem);
            }
        }

        return siphonReceiptItems;
    }

    public static List<ReceiptItem> getMeasurerReceiptList() {
        List<ReceiptItem> measurerReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof MeasurerItem measurerItem) {
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

    public static List<ReceiptItem> getDeliveryReceiptList() {
        List<ReceiptItem> deliveryReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof DeliveryItem deliveryItem) {
                String name = deliveryItem.getReceiptName();
                String units = "";
                double count = deliveryItem.getQuantity();
                String currency = "RUB";
                double priceForOne = deliveryItem.getPriceForOne();

                if (deliveryItem.getHandCarryPrice() != 0) {
                    deliveryReceiptItems.add(new ReceiptItem(name, units, count, currency, priceForOne - deliveryItem.getHandCarryPrice()));
                    deliveryReceiptItems.add(new ReceiptItem("Ручной пронос изделия", units, count, currency, deliveryItem.getHandCarryPrice()));
                } else {
                    deliveryReceiptItems.add(new ReceiptItem(name, units, count, currency, priceForOne));
                }
            }
        }

        return deliveryReceiptItems;
    }

    public static List<ReceiptItem> getMountingReceiptList() {
        List<ReceiptItem> mountingReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof MountingItem mountingItem) {
                String name = "Монтаж изделий в % (но не менее 4000 рублей)";
                String units = "";
                double count = mountingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = mountingItem.getPercent();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
                mountingReceiptItems.add(receiptItem);
            }
        }

        return mountingReceiptItems;
    }

    public static List<ReceiptItem> getPlumbingAlveusReceiptList() {
        List<ReceiptItem> PlumbingAlveusReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof PlumbingAlveusItem plumbingAlveusItem) {
                String name = plumbingAlveusItem.getName() + ", " + plumbingAlveusItem.getSize() + "мм";
                String units = plumbingAlveusItem.getUnits();
                double count = plumbingAlveusItem.getQuantity();
                String currency = "RUB";
                double priceForOne = plumbingAlveusItem.getPriceForOne();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
                PlumbingAlveusReceiptItems.add(receiptItem);
            }
        }

        return PlumbingAlveusReceiptItems;
    }

    public static List<ReceiptItem> getPlumbingReceiptList() {
        List<ReceiptItem> plumbingReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof PlumbingItem plumbingItem) {
                String name = plumbingItem.getName() + ", " + plumbingItem.getSize() + "мм";
                String units = plumbingItem.getUnits();
                double count = plumbingItem.getQuantity();
                String currency = "RUB";
                double priceForOne = plumbingItem.getPriceForOne();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
                plumbingReceiptItems.add(receiptItem);
            }
        }

        return plumbingReceiptItems;
    }

    public static List<ReceiptItem> getPalletReceiptList() {
        List<ReceiptItem> palletReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
            if (item instanceof PalletItem palletItem) {
                String name = palletItem.getName() + ", " + palletItem.getModel() + "мм";
                String units = palletItem.getUnits();
                double count = palletItem.getQuantity();
                String currency = "RUB";
                double priceForOne = palletItem.getPriceForOne();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
                palletReceiptItems.add(receiptItem);
            }
        }

        return palletReceiptItems;
    }

    public static List<ReceiptItem> getCustomReceiptList() {
        List<ReceiptItem> customReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof CustomItem customItem) {

                String name = customItem.getName();
                String units = customItem.getUnits();
                double count = customItem.getQuantity();
                String currency = "RUB";
                double priceForOne = customItem.getPrice();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
                customReceiptItems.add(receiptItem);
            }
        }

        return customReceiptItems;
    }

    public static List<ReceiptItem> getDiscountReceiptList() {
        List<ReceiptItem> customReceiptItems = new ArrayList<>();
        for (TableDesignerItem item : TableDesignerSession.getTableDesignerMainWorkItemsList()) {
            if (item instanceof DiscountItem discountItem) {
                String name = "Скидка";
                String units = "%";
                double count = discountItem.getQuantity();
                String currency = "RUB";
                double priceForOne = discountItem.getPercent();

                ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
                customReceiptItems.add(receiptItem);
            }
        }

        return customReceiptItems;
    }
}
