package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.MetalFootingItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class MetalFootingItemMapper implements ItemMapper<MetalFootingItem> {
    @Nullable
    @Override
    public ReceiptItem apply(MetalFootingItem metalFootingItem) {
        String name = "Металлокаркас";
        String units = "м.п.";
        double count = (metalFootingItem.getLength() / 1000.0) * metalFootingItem.getQuantity();
        String currency = "RUB";
        double priceForOne = ((metalFootingItem.getPaintingType() == 1) ? Project.getDefaultMaterial().getMetalFootingPrices().get(0) / 100 : Project.getDefaultMaterial().getMetalFootingPrices().get(1) / 100);

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
