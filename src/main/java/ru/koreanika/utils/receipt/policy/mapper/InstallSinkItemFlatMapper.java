package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.tableDesigner.item.SinkItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import java.util.Arrays;
import java.util.List;

public class InstallSinkItemFlatMapper implements ItemFlatMapper<SinkItem> {
    @Override
    public List<ReceiptItem> apply(SinkItem sinkItem) {
        ReceiptItem receiptItemForInstallType = mapInstallTypeReceiptItem(sinkItem);
        ReceiptItem receiptItemForEdgeType = mapEdgeTypeReceiptItem(sinkItem);
        return Arrays.asList(receiptItemForInstallType, receiptItemForEdgeType);
    }

    private ReceiptItem mapEdgeTypeReceiptItem(SinkItem sinkItem) {
        if (sinkItem.getType() != Sink.SINK_TYPE_16 && sinkItem.getType() != Sink.SINK_TYPE_19 && sinkItem.getType() != Sink.SINK_TYPE_21) {
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
            return new ReceiptItem(name, units, count, currency, priceForOne);
        } else {
            return null;
        }
    }

    private ReceiptItem mapInstallTypeReceiptItem(SinkItem sinkItem) {
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

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
