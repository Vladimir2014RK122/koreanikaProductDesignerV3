package ru.koreanika.cutDesigner.TreeViewProjectElements;

import ru.koreanika.cutDesigner.CutDesigner;
//import ru.koreanika.cutDesigner.CutSheet;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.transform.Transform;
import javafx.util.Callback;

public class CellFactory implements Callback<TreeView<TreeCellProjectElement>, TreeCell<TreeCellProjectElement>> {
    @Override
    public TreeCell<TreeCellProjectElement> call(TreeView<TreeCellProjectElement> param) {
        TreeCell<TreeCellProjectElement> cell = new TreeCell<TreeCellProjectElement>() {
            @Override
            protected void updateItem(TreeCellProjectElement item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    Node image = item.getImage();
                    if (image != null) {

                        image.maxHeight(30);
                        image.maxWidth(30);
                        //System.out.println(image.getChildren().get(0));
                    }

                    setGraphic(image);
                    setTooltip(item.getTooltip());
                    //System.out.println(item.getImage());
                }
            }
        };


        cell.setOnDragDetected(event -> startDragged(event, cell));

        return cell;
    }

    public void startDragged(MouseEvent event, TreeCell<TreeCellProjectElement> cell) {
        /*System.out.println("startDragged");
        System.out.println(cell);
        System.out.println(cell.getItem().isDraggable());*/
        if (cell == null) return;
        if (cell.getItem().isDraggable() == false) return;
        //if(cell.getItem().isUses()) return;
        //if (draggedItem.getParent() == null) return;

        Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        if (cell.getItem() instanceof TreeCellCutShape) {
            content.put(CutDesigner.CELL_FORMAT, TreeCellCutShape.TREE_CELL_FORMAT);
            content.put(CutDesigner.ELEMENT_DATA_FORMAT, ((TreeCellCutShape) cell.getItem()).getElementType());
            content.put(CutDesigner.SHAPE_NUMBER_DATA_FORMAT, new Integer(((TreeCellCutShape) cell.getItem()).getShapeNumber()));
        } else if (cell.getItem() instanceof TreeCellFeature) {
            content.put(CutDesigner.CELL_FORMAT, TreeCellFeature.TREE_CELL_FORMAT);
            content.put(CutDesigner.SHAPE_OWNER_DF, ((TreeCellFeature) cell.getItem()).getSketchShapeOwnerNumber());
            content.put(CutDesigner.FEATURE_NUMBER_DF, new Integer(((TreeCellFeature) cell.getItem()).getFeatureNumber()));
        }

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
