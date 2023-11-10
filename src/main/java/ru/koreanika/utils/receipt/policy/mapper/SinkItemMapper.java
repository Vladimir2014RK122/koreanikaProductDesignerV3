package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.tableDesigner.item.SinkItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class SinkItemMapper implements ItemMapper<SinkItem> {
    @Nullable
    @Override
    public ReceiptItem apply(SinkItem sinkItem) {
        if (sinkItem.getMaterial().getName().contains("Кварцевый агломерат") ||
                sinkItem.getMaterial().getName().contains("Натуральный камень") ||
                sinkItem.getMaterial().getName().contains("Dektone") ||
                sinkItem.getMaterial().getName().contains("Мраморный агломерат") ||
                sinkItem.getMaterial().getName().contains("Кварцекерамический камень")) {
            return null; // TODO maybe convert to filter???
        }

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
            return new ReceiptItem(name, units, count, currency, priceForOne);
        } else {
            return null;
        }

    }
}
