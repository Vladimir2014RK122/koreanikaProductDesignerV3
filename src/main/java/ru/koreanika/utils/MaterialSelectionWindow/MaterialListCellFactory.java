package ru.koreanika.utils.MaterialSelectionWindow;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class MaterialListCellFactory implements Callback<ListView<MaterialListCellItem>, ListCell<MaterialListCellItem>> {

    @Override
    public ListCell<MaterialListCellItem> call(ListView<MaterialListCellItem> param) {
        ListCell<MaterialListCellItem> cell = new ListCell<>() {
            @Override
            protected void updateItem(MaterialListCellItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        };
        cell.setOnMousePressed(this::onMousePressed);
        return cell;
    }

    public void onMousePressed(MouseEvent event) {
        System.out.println("event.getClickCount() = " + event.getClickCount());
    }
}
