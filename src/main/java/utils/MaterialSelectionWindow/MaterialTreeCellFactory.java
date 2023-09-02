package utils.MaterialSelectionWindow;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import utils.MaterialSelectionWindow.TreeViewItems.MaterialTreeCellItem;

public class MaterialTreeCellFactory implements Callback<TreeView<MaterialTreeCellItem>, TreeCell<MaterialTreeCellItem>> {

    @Override
    public TreeCell<MaterialTreeCellItem> call(TreeView<MaterialTreeCellItem> param) {

        TreeCell<MaterialTreeCellItem> cell = new TreeCell<MaterialTreeCellItem>() {
            @Override
            protected void updateItem(MaterialTreeCellItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    //setGraphic(item);
                }
            }
        };


        cell.setOnMousePressed(event -> onMousePressed(event));

        return cell;
    }

    public void onMousePressed(MouseEvent event) {

        System.out.println("event.getClickCount() = " + event.getClickCount());
        if (event.getClickCount() == 2) {
            MaterialSelectionWindow.getBtnToProject().fire();
           //MaterialSelectionWindow.getMaterialSelectionWindow().addMaterialToProjectListView(MaterialSelectionWindow.getSelectedMaterials());
        }

    }
}
