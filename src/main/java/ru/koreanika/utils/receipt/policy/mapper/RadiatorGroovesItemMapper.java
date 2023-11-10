package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.RadiatorGroovesItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class RadiatorGroovesItemMapper implements ItemMapper<RadiatorGroovesItem> {
    @Nullable
    @Override
    public ReceiptItem apply(RadiatorGroovesItem radiatorGroovesItem) {
        String name = "Проточки под радиатор #" + radiatorGroovesItem.getMaterial().getReceiptName();
        String units = "шт";
        double count = radiatorGroovesItem.getQuantity();
        String currency = radiatorGroovesItem.getMaterial().getCutoutCurrency();
        double priceForOne = (radiatorGroovesItem.getMaterial().getCutoutTypesAndPrices().get(radiatorGroovesItem.getType())) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
