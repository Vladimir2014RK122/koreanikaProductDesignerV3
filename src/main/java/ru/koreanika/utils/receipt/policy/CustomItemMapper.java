package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.CustomItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class CustomItemMapper implements ItemMapper<CustomItem> {
    @Nullable
    @Override
    public ReceiptItem apply(CustomItem customItem) {
        String name = customItem.getName();
        String units = customItem.getUnits();
        double count = customItem.getQuantity();
        String currency = "RUB";
        double priceForOne = customItem.getPrice();

        return new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
    }
}
