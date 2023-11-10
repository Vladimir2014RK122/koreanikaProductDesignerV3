package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.EdgeItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class EdgeItemMapper implements ItemMapper<EdgeItem> {
    @Nullable
    @Override
    public ReceiptItem apply(EdgeItem edgeItem) {
        String name = "Кромка, Вариант №" + edgeItem.getType() + "#" + edgeItem.getMaterial().getReceiptName();
        String units = "м.п.";
        double count = edgeItem.getLength() / 1000.0 * edgeItem.getQuantity();
        String currency = edgeItem.getMaterial().getEdgesCurrency();
        double priceForOne = edgeItem.getMaterial().getEdgesAndPrices().get(edgeItem.getType());

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
