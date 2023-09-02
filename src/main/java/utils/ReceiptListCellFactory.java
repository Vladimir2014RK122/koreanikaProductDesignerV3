package utils;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import sketchDesigner.lists.ListElement;

import javax.tools.Tool;

public class ReceiptListCellFactory implements Callback<ListView<ReceiptCellItem>, ListCell<ReceiptCellItem>> {
    @Override
    public ListCell<ReceiptCellItem> call(ListView<ReceiptCellItem> param) {

        ListCell<ReceiptCellItem> cell = new ListCell<ReceiptCellItem>() {
            @Override
            protected void updateItem(ReceiptCellItem item, boolean empty) {
                super.updateItem(item, empty);

                this.setId("#receiptListCell");

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText(item.getName());
                    setGraphic(item);
                }


            }
        };


//        cell.setOnMouseEntered(event -> {
//            if(cell.getItem() != null)
//                cell.getItem().getTooltip().show(cell.getScene().getWindow(),
//                        event.getScreenX(), event.getScreenY());
//        });
//        cell.setOnMouseExited(event -> {
//            if(cell.getItem() != null)
//                cell.getItem().getTooltip().hide();
//        });


        return cell;
    }


}
