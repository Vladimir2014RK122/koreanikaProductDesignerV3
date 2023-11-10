package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.tableDesigner.item.CutoutItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public class CutoutItemMapper implements ItemMapper<CutoutItem> {
    @Nullable
    @Override
    public ReceiptItem apply(CutoutItem cutoutItem) {
        String name = "Вырез ";
        switch (cutoutItem.getType()) {
            case Cutout.CUTOUT_TYPE_1 -> name += "под питьевой кран. d = 12мм";
            case Cutout.CUTOUT_TYPE_2 -> name += "под смеситель. d = 35мм";
            case Cutout.CUTOUT_TYPE_3 -> name += "под варочную панель/раковину.";
            case Cutout.CUTOUT_TYPE_4 -> name += "под розетку. d = 65мм";
            case Cutout.CUTOUT_TYPE_5 -> name += "под накладную мойку.";
            case Cutout.CUTOUT_TYPE_6 -> name += "под варочную панель вровень со столешницей.";
            case Cutout.CUTOUT_TYPE_7 -> name += "под радиатор.";
            case Cutout.CUTOUT_TYPE_8 -> name += "прямолиннейный. Без обработки.";
            case Cutout.CUTOUT_TYPE_9 -> name += "криволинейный. Без обработки.";
            case Cutout.CUTOUT_TYPE_10 -> name += "прямолинейный. С обработкой.";
            case Cutout.CUTOUT_TYPE_11 -> name += "криволинейный. С обработкой.";
            case Cutout.CUTOUT_TYPE_12 -> name += "под раковину/мойку, для установки в уровень со столешницей.";
            case Cutout.CUTOUT_TYPE_13 -> name += "под измельчитель.";
            case Cutout.CUTOUT_TYPE_14 -> name += "под дозатор.";
        }
        name += "#" + cutoutItem.getMaterial().getReceiptName();

        String units = "шт";
        double count = cutoutItem.getQuantity();
        String currency = cutoutItem.getMaterial().getCutoutCurrency();
        double priceForOne = (cutoutItem.getMaterial().getCutoutTypesAndPrices().get(cutoutItem.getType())) / 100;

        return new ReceiptItem(name, units, count, currency, priceForOne);
    }
}
