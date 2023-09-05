package ru.koreanika.cutDesigner;

import ru.koreanika.cutDesigner.Shapes.CutShape;
import javafx.geometry.Bounds;

import java.util.Comparator;

public class SortCutShapesByMinY   implements Comparator<CutShape> {
    @Override
    public int compare(CutShape cutShape1, CutShape cutShape2) {

        Bounds cutShapeBounds1 = cutShape1.localToParent(cutShape1.getPolygon().getBoundsInParent());
        Bounds cutShapeBounds2 = cutShape2.localToParent(cutShape2.getPolygon().getBoundsInParent());
        double maxY1 = cutShapeBounds1.getMaxY();
        double maxY2 = cutShapeBounds2.getMaxY();

        if ( maxY1 < maxY2 ) return -1;
        else if ( maxY1 == maxY2 ) return 0;
        else return 1;

    }

}
