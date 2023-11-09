package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.DiscountItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class DiscountItemMapper implements ItemMapper<DiscountItem> {
    @Nullable
    @Override
    public ReceiptItem apply(DiscountItem discountItem) {
        String name = "Скидка";
        String units = "%";
        double count = discountItem.getQuantity();
        String currency = "RUB";
        double priceForOne = discountItem.getPercent();

        return new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
    }
}
