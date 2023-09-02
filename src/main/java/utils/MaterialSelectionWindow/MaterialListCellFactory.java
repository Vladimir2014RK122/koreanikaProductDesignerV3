package utils.MaterialSelectionWindow;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import utils.MaterialSelectionWindow.ListCellItems.MaterialListCellItem;
import utils.MaterialSelectionWindow.TreeViewItems.MaterialTreeCellItem;

public class MaterialListCellFactory implements Callback<ListView<MaterialListCellItem>, ListCell<MaterialListCellItem>> {

    @Override
    public ListCell<MaterialListCellItem> call(ListView<MaterialListCellItem> param) {

        ListCell<MaterialListCellItem> cell = new ListCell<MaterialListCellItem>() {
            @Override
            protected void updateItem(MaterialListCellItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {


                    //setText(item.getName());
                    setGraphic(item);
                }
            }
        };


        cell.setOnMousePressed(event -> onMousePressed(event));

        return cell;
    }

    public void onMousePressed(MouseEvent event) {

        System.out.println("event.getClickCount() = " + event.getClickCount());

    }
}
