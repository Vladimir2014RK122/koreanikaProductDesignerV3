package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.PalletItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class PalletItemMapper implements ItemMapper<PalletItem> {
    @Nullable
    @Override
    public ReceiptItem apply(PalletItem palletItem) {
        String name = palletItem.getName() + ", " + palletItem.getModel() + "мм";
        String units = palletItem.getUnits();
        double count = palletItem.getQuantity();
        String currency = "RUB";
        double priceForOne = palletItem.getPriceForOne();

        return new ReceiptItem(name, units, count, currency, priceForOne / Project.getPriceMainCoefficient().doubleValue());
    }
}
