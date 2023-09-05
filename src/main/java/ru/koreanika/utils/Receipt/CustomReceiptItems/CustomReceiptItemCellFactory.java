package ru.koreanika.utils.Receipt.CustomReceiptItems;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.transform.Transform;
import javafx.util.Callback;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.lists.FeatureListElement;


public class CustomReceiptItemCellFactory implements Callback<ListView<CustomReceiptItem>, ListCell<CustomReceiptItem>> {
    @Override
    public ListCell<CustomReceiptItem> call(ListView<CustomReceiptItem> param) {

        ListCell<CustomReceiptItem> cell = new ListCell<CustomReceiptItem>() {
            @Override
            protected void updateItem(CustomReceiptItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    //setGraphic(item.getImageView());
                    //getGraphic().setTranslateX(35);
                    //setTooltip(item.getTooltip());
                }

            }
        };

        //cell.setOnDragDetected(event -> startDragged(event,cell));
        return cell;
    }

    public void startDragged(MouseEvent event, ListCell<FeatureListElement> cell) {

        if (cell.getItem() == null) return;
        //System.out.println(cell);
        //if (draggedItem.getParent() == null) return;

        Dragboard db = cell.startDragAndDrop(TransferMode.ANY);

        ClipboardContent content = new ClipboardContent();
        content.put(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_TYPE, cell.getItem().getFeatureType());
        content.put(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_SUBTYPE, cell.getItem().getSubType());
        db.setContent(content);
        //db.setDragView(treeCell.snapshot(null, null));

        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(0.9, 0.9));

        /*Text dragText = new Text(treeCell.getText());
        dragText.setStyle("-fx-background-color: red");


        db.setDragView(dragText.snapshot(sp, null));*/
        //ImageView dragImg = new ImageView(new Image(cell.getItem().getListImage()));
        db.setDragView(cell.getItem().getImageIcon().snapshot(sp, null));
        event.consume();

    }
}
