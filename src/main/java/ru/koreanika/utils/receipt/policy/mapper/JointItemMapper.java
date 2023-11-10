package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.JointItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class JointItemMapper implements ItemMapper<JointItem> {
    @Nullable
    @Override
    public ReceiptItem apply(JointItem jointItem) {
        String name = "Соединение элементов по " + ((jointItem.getType() == 1) ? "прямому" : "косому") + " стыку" + "#" + jointItem.getMaterial().getReceiptName();
        String units = "м.п.";
        double count = (jointItem.getLength() / 1000) * jointItem.getQuantity();
        String currency = jointItem.getMaterial().getLeakGrooveCurrency();
        double priceForOne = (jointItem.getMaterial().getJointsTypesAndPrices().get(jointItem.getType() - 1)) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
