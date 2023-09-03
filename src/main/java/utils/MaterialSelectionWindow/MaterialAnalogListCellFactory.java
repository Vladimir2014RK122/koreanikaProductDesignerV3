package utils.MaterialSelectionWindow;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class MaterialAnalogListCellFactory implements Callback<ListView<MaterialListCellItem>, ListCell<MaterialListCellItem>> {

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
                    item.disableChoiceBox(true);
                    item.disableControls(true);
                }
            }
        };


        cell.setOnMousePressed(event -> onMousePressed(event, cell.getItem()));

        return cell;
    }

    public void onMousePressed(MouseEvent event, MaterialListCellItem materialListCellItem) {

        System.out.println("event.getClickCount() = " + event.getClickCount());
        if (event.getClickCount() == 2) {
            MaterialSelectionWindow.getInstance().addAnalogMaterialToProjectListView(materialListCellItem.getMaterial());
        }
    }
}
