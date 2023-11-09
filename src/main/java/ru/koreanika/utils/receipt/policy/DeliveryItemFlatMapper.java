package ru.koreanika.utils.receipt.policy;

import ru.koreanika.tableDesigner.item.DeliveryItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import java.util.ArrayList;
import java.util.List;

class DeliveryItemFlatMapper implements ItemFlatMapper<DeliveryItem> {
    @Override
    public List<ReceiptItem> apply(DeliveryItem deliveryItem) {
        List<ReceiptItem> result = new ArrayList<>();

        String name = deliveryItem.getReceiptName();
        String units = "";
        double count = deliveryItem.getQuantity();
        String currency = "RUB";
        double priceForOne = deliveryItem.getPriceForOne();

        if (deliveryItem.getHandCarryPrice() != 0) {
            result.add(new ReceiptItem(name, units, count, currency, priceForOne - deliveryItem.getHandCarryPrice()));
            result.add(new ReceiptItem("Ручной пронос изделия", units, count, currency, deliveryItem.getHandCarryPrice()));
        } else {
            result.add(new ReceiptItem(name, units, count, currency, priceForOne));
        }

        return result;
    }
}
