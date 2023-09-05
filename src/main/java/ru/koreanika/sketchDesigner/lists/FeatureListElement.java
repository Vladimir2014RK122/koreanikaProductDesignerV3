package ru.koreanika.sketchDesigner.lists;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import ru.koreanika.sketchDesigner.Features.FeatureType;

import java.io.Serializable;

public class FeatureListElement implements Serializable {

    String name;
    Node imageIcon;
    FeatureType featureType;
    int subType = 1;
    Tooltip tooltip;

    public FeatureListElement(String name, Node imageIcon, FeatureType featureType, int subType, Tooltip tooltip) {
        this.name = name;
        this.imageIcon = imageIcon;
        this.featureType = featureType;
        this.subType = subType;
        this.tooltip = tooltip;
    }

    public String getName() {
        return name;
    }

    public Node getImageIcon() {
        return imageIcon;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public int getSubType() {
        return subType;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }
}
