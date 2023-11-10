package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.GroovesItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class GroovesItemMapper implements ItemMapper<GroovesItem> {
    @Nullable
    @Override
    public ReceiptItem apply(GroovesItem groovesItem) {
        String name = "Проточки для стока воды, вариант №" + groovesItem.getType() + "#" + groovesItem.getMaterial().getReceiptName();
        String units = "шт";
        double count = groovesItem.getQuantity();
        String currency = groovesItem.getMaterial().getGroovesCurrency();
        double priceForOne = (groovesItem.getMaterial().getGroovesTypesAndPrices().get(groovesItem.getType() - 1)) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
