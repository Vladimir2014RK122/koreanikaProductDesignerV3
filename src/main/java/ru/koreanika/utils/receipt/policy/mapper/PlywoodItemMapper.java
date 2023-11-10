package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.common.material.Material;
import ru.koreanika.tableDesigner.item.PlywoodItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class PlywoodItemMapper implements ItemMapper<PlywoodItem> {
    @Nullable
    @Override
    public ReceiptItem apply(PlywoodItem plywoodItem) {
        String name = "";
        if (plywoodItem.getMaterial().getMainType().equals("Кварцекерамический камень") ||
                plywoodItem.getMaterial().getMainType().equals("Мраморный агломерат")) {
            name = "Подложка полимерная";
        } else {
            name = "Подложка из фанеры";
        }
        name += (plywoodItem.getPaintingType() == 1) ? " без покраски" : " с покраской";
        name += "#" + plywoodItem.getMaterial().getReceiptName();

        String units = "м.кв.";
        double count = (plywoodItem.getLength() / 1000) * (plywoodItem.getWidth() / 1000) * plywoodItem.getQuantity();

        Material material = plywoodItem.getMaterial();
        String currency = material.getPlywoodCurrency().get(0);

        double priceForOne = ((plywoodItem.getPaintingType() == 1) ? material.getPlywoodPrices().get(0) / 100 : material.getPlywoodPrices().get(1) / 100);

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
