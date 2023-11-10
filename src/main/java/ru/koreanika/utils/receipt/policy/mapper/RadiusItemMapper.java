package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.RadiusItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class RadiusItemMapper implements ItemMapper<RadiusItem> {
    @Nullable
    @Override
    public ReceiptItem apply(RadiusItem radiusItem) {
        String name = "Радиусный элемент" + "#" + radiusItem.getMaterial().getReceiptName();
        String units = "шт";
        double count = radiusItem.getQuantity();
        String currency = radiusItem.getMaterial().getRadiusElementCurrency();
        double priceForOne = (radiusItem.getMaterial().getRadiusElementPrice()) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
