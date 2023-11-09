package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.PlumbingItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class PlumbingItemMapper implements ItemMapper<PlumbingItem> {
    @Nullable
    @Override
    public ReceiptItem apply(PlumbingItem plumbingItem) {
        String name = plumbingItem.getName() + ", " + plumbingItem.getSize() + "мм";
        String units = plumbingItem.getUnits();
        double count = plumbingItem.getQuantity();
        String currency = "RUB";
        double priceForOne = plumbingItem.getPriceForOne();

        return new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
    }
}
