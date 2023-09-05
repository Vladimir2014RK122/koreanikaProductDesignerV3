package ru.koreanika.sketchDesigner.lists;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import ru.koreanika.sketchDesigner.Features.FeatureType;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.Shapes.ShapeType;

import java.io.Serializable;

public class ListElement implements Serializable {
    String name;
    Node image;
    Tooltip tooltip;

    //DataFormat dataFormat;
    ElementTypes elementType;
    ShapeType shapeType;
    FeatureType featureType;

    public ListElement(String name, Node image, Tooltip tooltip, ShapeType shapeType, ElementTypes elementType) {
        this.name = name;
        this.image = image;
        this.tooltip = tooltip;
        //this.dataFormat = new DataFormat(elementType.toString());
        this.shapeType = shapeType;
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public Node getImage() {
        return image;
    }

    /*public DataFormat getDataFormat() {
        return dataFormat;
    }*/

    public ElementTypes getElementType() {
        return elementType;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }
}
