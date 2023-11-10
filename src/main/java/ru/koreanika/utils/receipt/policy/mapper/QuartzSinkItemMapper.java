package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.tableDesigner.item.SinkItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class QuartzSinkItemMapper implements ItemMapper<SinkItem> {
    @Nullable
    @Override
    public ReceiptItem apply(SinkItem sinkItem) {
        if (sinkItem.getMaterial().getName().contains("Акриловый камень") ||
                sinkItem.getMaterial().getName().contains("Полиэфирный камень") ||
                sinkItem.getMaterial().getName().contains("Массив") ||
                sinkItem.getMaterial().getName().contains("Массив_шпон")) {
            return null;
        }

        String name = "Раковина " + sinkItem.getModel() + "#" + sinkItem.getMaterial().getReceiptName();
        String units = "шт";
        double count = sinkItem.getQuantity();
        String currency = sinkItem.getMaterial().getSinkCurrency();
        double priceForOne;

        if (sinkItem.getType() == Sink.SINK_TYPE_16 || sinkItem.getType() == Sink.SINK_TYPE_17 ||
                sinkItem.getType() == Sink.SINK_TYPE_19 || sinkItem.getType() == Sink.SINK_TYPE_20 ||
                sinkItem.getType() == Sink.SINK_TYPE_21) {
            priceForOne = sinkItem.getMaterial().getSinkCommonTypesAndPrices().get(sinkItem.getType()) / 100;
        } else {
            System.out.println(sinkItem.getMaterial().getAvailableSinkModels() + "  " + sinkItem.getModel().split(" ")[0]);
            priceForOne = (sinkItem.getMaterial().getAvailableSinkModels().get(sinkItem.getModel().split(" ")[0])) / 100;
        }

        if (priceForOne != 0) {
            return new ReceiptItem(name, units, count, currency, priceForOne);
        } else {
            return null;
        }
    }

}
