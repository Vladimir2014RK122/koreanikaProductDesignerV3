package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.RodsItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class RodsItemMapper implements ItemMapper<RodsItem> {
    @Nullable
    @Override
    public ReceiptItem apply(RodsItem rodsItem) {
        String name = "Подставка под горячее, вариант №" + rodsItem.getType() + "#" + rodsItem.getMaterial().getReceiptName();
        String units = "шт";
        double count = rodsItem.getQuantity();
        String currency = rodsItem.getMaterial().getGroovesCurrency();
        double priceForOne = (rodsItem.getMaterial().getRodsTypesAndPrices().get(rodsItem.getType() - 1)) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
