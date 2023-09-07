package ru.koreanika.cutDesigner.TreeViewProjectElements;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DataFormat;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.Shapes.ShapeType;

public class TreeCellCutShape extends TreeCellProjectElement {

    public static final String TREE_CELL_FORMAT = "CELL_CUT_SHAPE";

    public static final int FOLDER_TYPE = 0;
    public static final int ELEMENT_TYPE = 1;

    DataFormat dataFormat;
    ShapeType shapeType;

    int shapeNumber = 0;
    ElementTypes elementType;

    boolean draggable = true;

    boolean uses = false;

    public TreeCellCutShape(int cellType, int shapeNumber, ElementTypes elementType, ShapeType shapeType, Node image, Tooltip tooltip) {

        super(cellType, image, tooltip);

        this.shapeNumber = shapeNumber;
        this.elementType = elementType;
        //this.dataFormat = dataFormat;
        this.shapeType = shapeType;

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


    public ShapeType getShapeType() {
        return shapeType;
    }

    public int getShapeNumber() {
        return shapeNumber;
    }

    public ElementTypes getElementType() {
        return elementType;
    }


    public boolean isDraggable() {
        return draggable;
    }
}
