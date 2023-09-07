package ru.koreanika.cutDesigner.TreeViewProjectElements;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class TreeCellFeature extends TreeCellProjectElement {

    public static final String TREE_CELL_FORMAT = "CELL_FEATURE";

    public static final int FOLDER_TYPE = 0;
    public static final int ELEMENT_TYPE = 1;

    int sketchShapeOwnerNumber;
    int featureNumber;

    boolean draggable = true;

    boolean uses = false;


    public TreeCellFeature(int cellType, Node image, Tooltip tooltip, int sketchShapeOwnerNumber, int featureNumber) {

        super(cellType, image, tooltip);
        this.sketchShapeOwnerNumber = sketchShapeOwnerNumber;
        this.featureNumber = featureNumber;
    }

    @Override
    public String toString() {
        if (cellType == FOLDER_TYPE) {
            return folderName;
        } else {
            //return "" + elementType + "  #" + shapeNumber;
            return "";
        }
    }

    public int getFeatureNumber() {
        return featureNumber;
    }

    public int getSketchShapeOwnerNumber() {
        return sketchShapeOwnerNumber;
    }

    public boolean isDraggable() {
        return draggable;
    }
}
