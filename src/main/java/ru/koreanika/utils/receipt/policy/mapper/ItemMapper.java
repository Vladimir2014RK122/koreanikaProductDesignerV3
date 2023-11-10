package ru.koreanika.utils.receipt.policy.mapper;

import ru.koreanika.tableDesigner.item.TableDesignerItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import javax.annotation.Nullable;

public interface ItemMapper<T extends TableDesignerItem> {
    @Nullable
    ReceiptItem apply(T item);
}
