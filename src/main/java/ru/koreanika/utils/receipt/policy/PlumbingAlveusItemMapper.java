package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.PlumbingAlveusItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class PlumbingAlveusItemMapper implements ItemMapper<PlumbingAlveusItem> {
    @Nullable
    @Override
    public ReceiptItem apply(PlumbingAlveusItem plumbingAlveusItem) {
        String name = plumbingAlveusItem.getName() + ", " + plumbingAlveusItem.getSize() + "мм";
        String units = plumbingAlveusItem.getUnits();
        double count = plumbingAlveusItem.getQuantity();
        String currency = "RUB";
        double priceForOne = plumbingAlveusItem.getPriceForOne();

        return new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
    }
}
