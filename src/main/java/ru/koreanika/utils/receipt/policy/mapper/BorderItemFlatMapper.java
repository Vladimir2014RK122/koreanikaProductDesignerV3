package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.BorderItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import java.util.Arrays;
import java.util.List;

public class BorderItemFlatMapper implements ItemFlatMapper<BorderItem> {
    @Override
    public List<ReceiptItem> apply(BorderItem borderItem) {
        ReceiptItem item1 = mapToBorderReceiptItem(borderItem);
        ReceiptItem item2 = mapToTopCutReceiptItem(borderItem);
        ReceiptItem item3 = mapToSideCutReceiptItem(borderItem);
        return Arrays.asList(item1, item2, item3);
    }

    private static ReceiptItem mapToSideCutReceiptItem(BorderItem borderItem) {
        String name = "Запил бортика" + "#" + borderItem.getMaterial().getReceiptName();
        String units = "шт";
        double count = borderItem.getAngleCutQuantity() * borderItem.getQuantity();
        String currency = "RUB";
        double priceForOne = borderItem.getMaterial().getBorderSideCutTypesAndPrices().get(Integer.valueOf(borderItem.getAngleCutType() - 1)) / 100;

        System.out.println("borderItem.getAngleCutQuantity() = " + borderItem.getAngleCutQuantity());
        System.out.println("borderItem.getQuantity() = " + borderItem.getQuantity());

        if (count != 0) {
            return new ReceiptItem(name, units, count, currency, priceForOne);
        } else {
            return null;
        }
    }

    private static ReceiptItem mapToTopCutReceiptItem(BorderItem borderItem) {
        String name = "Обработка верхней грани бортика" + "#" + borderItem.getMaterial().getReceiptName();
        String units = "м. п.";
        double count = borderItem.getLength() / 1000.0 * borderItem.getQuantity();
        String currency = "RUB";
        double priceForOne = borderItem.getMaterial().getBorderTopCutTypesAndPrices().get(Integer.valueOf(borderItem.getCutType() - 1)) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }

    private static ReceiptItem mapToBorderReceiptItem(BorderItem borderItem) {
        String name = "Бортик, Вариант №" + borderItem.getType() + "#" + borderItem.getMaterial().getReceiptName();
        String units = "м.п.";
        double count = borderItem.getLength() / 1000.0 * borderItem.getQuantity();
        String currency = borderItem.getMaterial().getEdgesCurrency();
        double priceForOne = -1.0;

        if (borderItem.getMaterial().getName().contains("Кварцевый агломерат") ||
                borderItem.getMaterial().getName().contains("Натуральный камень") ||
                borderItem.getMaterial().getName().contains("Dektone") ||
                borderItem.getMaterial().getName().contains("Мраморный агломерат") ||
                borderItem.getMaterial().getName().contains("Кварцекерамический камень")) {

            priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(0).doubleValue();

        } else {
            if (borderItem.getHeight() <= 50 && borderItem.getType() == 1) {
                priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(0).doubleValue();
            }
            if (borderItem.getHeight() > 50 && borderItem.getType() == 1) {
                priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(1).doubleValue();
            }

            if (borderItem.getHeight() <= 50 && borderItem.getType() == 2) {
                priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(2).doubleValue();
            }
            if (borderItem.getHeight() > 50 && borderItem.getType() == 2) {
                priceForOne = borderItem.getMaterial().getBorderTypesAndPrices().get(3).doubleValue();
            }
        }
        priceForOne /= 100.0;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }

}
