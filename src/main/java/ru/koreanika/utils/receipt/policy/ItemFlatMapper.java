package ru.koreanika.utils.receipt.policy;

import ru.koreanika.tableDesigner.item.TableDesignerItem;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import java.util.List;

public interface ItemFlatMapper<T extends TableDesignerItem> {
    List<ReceiptItem> apply(T item);
}
