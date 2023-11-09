package ru.koreanika.utils.receipt.policy;

import ru.koreanika.tableDesigner.item.LeakGrooveItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class LeakGrooveItemMapper implements ItemMapper<LeakGrooveItem> {
    @Nullable
    @Override
    public ReceiptItem apply(LeakGrooveItem item) {
        ReceiptItem result;

        String name;
        String units = "м.п.";
        double count;
        String currency;
        double priceForOne;

        if (item.getMaterial().getName().contains("Кварцевый агломерат") ||
                item.getMaterial().getName().contains("Натуральный камень") ||
                item.getMaterial().getName().contains("Dektone") ||
                item.getMaterial().getName().contains("Мраморный агломерат") ||
                item.getMaterial().getName().contains("Кварцекерамический камень")) {
            name = "Выборка капельника" + "#" + item.getMaterial().getReceiptName();
            count = (item.getLength() / 1000) * item.getQuantity();
            currency = item.getMaterial().getLeakGrooveCurrency();
            priceForOne = item.getMaterial().getLeakGroovePrice() / 100;
        } else {
            name = "Подгиб камня к каплесборником" + "#" + item.getMaterial().getReceiptName();
            count = (item.getLength() / 1000);
            currency = item.getMaterial().getStoneHemCurrency();
            priceForOne = item.getMaterial().getStoneHemPrice() / 100;
        }
        result = new ReceiptItem(name, units, count, currency, priceForOne);
        return result;
    }

}
