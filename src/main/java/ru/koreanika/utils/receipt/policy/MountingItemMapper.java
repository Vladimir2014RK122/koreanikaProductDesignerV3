package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.MountingItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class MountingItemMapper implements ItemMapper<MountingItem> {
    @Nullable
    @Override
    public ReceiptItem apply(MountingItem mountingItem) {
        String name = "Монтаж изделий в % (но не менее 4000 рублей)";
        String units = "";
        double count = mountingItem.getQuantity();
        String currency = "RUB";
        double priceForOne = mountingItem.getPercent();

        return new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
    }
}
