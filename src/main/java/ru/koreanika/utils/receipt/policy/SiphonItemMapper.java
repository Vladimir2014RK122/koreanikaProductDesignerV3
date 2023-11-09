package ru.koreanika.utils.receipt.policy;

import ru.koreanika.project.Project;
import ru.koreanika.tableDesigner.item.SiphonItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

class SiphonItemMapper implements ItemMapper<SiphonItem> {
    @Nullable
    @Override
    public ReceiptItem apply(SiphonItem siphonItem) {
        String name = "Сифон " + ((siphonItem.getType() == 1) ? "с одним выпуском" : "с двумя выпусками");
        String units = "шт";
        double count = siphonItem.getQuantity();
        String currency = "RUB";
        double priceForOne = ((siphonItem.getType() == 1) ?
                Project.getDefaultMaterial().getSiphonsTypesAndPrices().get(0) / 100 :
                Project.getDefaultMaterial().getSiphonsTypesAndPrices().get(1) / 100);

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
