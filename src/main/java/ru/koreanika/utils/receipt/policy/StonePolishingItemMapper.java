package ru.koreanika.utils.receipt.policy;

import ru.koreanika.common.material.Material;
import ru.koreanika.tableDesigner.item.StonePolishingItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class StonePolishingItemMapper implements ItemMapper<StonePolishingItem> {
    @Nullable
    @Override
    public ReceiptItem apply(StonePolishingItem stonePolishingItem) {
        String name = "Полировка поверхности камня";
        String units = "м.кв.";
        double count = (stonePolishingItem.getLength() / 1000) * (stonePolishingItem.getWidth() / 1000) * stonePolishingItem.getQuantity();
        String currency = "RUB";

        Material material = stonePolishingItem.getMaterial();
        double priceForOne = material.getStonePolishingPrice() / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
