package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.common.material.Material;
import ru.koreanika.tableDesigner.item.MeasurerItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class MeasurerItemMapper implements ItemMapper<MeasurerItem> {
    @Nullable
    @Override
    public ReceiptItem apply(MeasurerItem measurerItem) {
        Material material = measurerItem.getMaterial();

        String name = measurerItem.getReceiptName();
        String units = "";
        double count = measurerItem.getQuantity();
        String currency = "RUB";
        double priceForOne = material.getMeasurerPrice() + measurerItem.getLength() * material.getMeasurerKMPrice();

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
