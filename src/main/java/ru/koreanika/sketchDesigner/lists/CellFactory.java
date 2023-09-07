package ru.koreanika.sketchDesigner.lists;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.transform.Transform;
import javafx.util.Callback;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;

public class CellFactory implements Callback<ListView<ListElement>, ListCell<ListElement>> {
    @Override
    public ListCell<ListElement> call(ListView<ListElement> param) {

        ListCell<ListElement> cell = new ListCell<ListElement>() {
            @Override
            protected void updateItem(ListElement item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText(item.getName());
                    setGraphic(item.getImage());
                    getGraphic().setTranslateX(3);
                    setTooltip(item.getTooltip());
                }
            }
        };
        cell.setOnDragDetected(event -> startDragged(event, cell));

        return cell;
    }

    public void startDragged(MouseEvent event, ListCell<ListElement> cell) {

        if (cell.getItem() == null) return;
        System.out.println(cell);
        //if (draggedItem.getParent() == null) return;

        Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.put(SketchShape.DRAG_DATA_FORMAT_ELEMENT_TYPE, cell.getItem().getElementType());
        content.put(SketchShape.DRAG_DATA_FORMAT_SHAPE_TYPE, cell.getItem().getShapeType());
        db.setContent(content);
        //db.setDragView(treeCell.snapshot(null, null));

        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(0.9, 0.9));

        /*Text dragText = new Text(treeCell.getText());
        dragText.setStyle("-fx-background-color: red");


        db.setDragView(dragText.snapshot(sp, null));*/
        //ImageView dragImg = new ImageView(new Image(cell.getItem().getListImage()));
        db.setDragView(cell.snapshot(sp, null));
        event.consume();

    }
}
