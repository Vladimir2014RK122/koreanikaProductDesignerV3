package cutDesigner.TreeViewProjectElements;

import cutDesigner.CutDesigner;
//import cutDesigner.CutSheet;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DataFormat;
import sketchDesigner.Shapes.ElementTypes;
import sketchDesigner.Shapes.ShapeType;

import java.io.Serializable;

public class TreeCellProjectElement implements Serializable {

    //public static final DataFormat CELL_FORMAT = new DataFormat("CELL11_FORMAT");
//    public static final DataFormat SHAPE_NUMBER_DATA_FORMAT = new DataFormat("SHAPE_NUMBER_DATA_FORMAT");
//    public static final DataFormat ELEMENT_DATA_FORMAT = new DataFormat("ELEMENT_TYPE_DATA_FORMAT");

    public static final int FOLDER_TYPE = 0;
    public static final int ELEMENT_TYPE = 1;

    DataFormat dataFormat;

    Node image;
    Tooltip tooltip;

    String folderName;
    int cellType = FOLDER_TYPE;

    boolean draggable = true;

    boolean uses = false;

    public TreeCellProjectElement(String folderName) {
        this.cellType = FOLDER_TYPE;
        this.folderName = folderName;

    }

    public TreeCellProjectElement(int cellType, Node image, Tooltip tooltip) {
        this.cellType = cellType;
        this.image = image;
        this.tooltip = tooltip;

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


    public Node getImage() {
        return image;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public boolean isDraggable() {
        return draggable;
    }
}
