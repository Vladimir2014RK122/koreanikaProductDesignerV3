package ru.koreanika.cutDesigner;

import ru.koreanika.cutDesigner.Shapes.CutShape;
import javafx.geometry.Bounds;

import java.util.Comparator;

public class SortCutShapesByPerimeter  implements Comparator<CutShape> {
    @Override
    public int compare(CutShape cutShape1, CutShape cutShape2) {

        Bounds cutShapeBounds1 = cutShape1.localToParent(cutShape1.getPolygon().getBoundsInParent());
        Bounds cutShapeBounds2 = cutShape2.localToParent(cutShape2.getPolygon().getBoundsInParent());
        double height1 = cutShapeBounds1.getHeight();
        double height2 = cutShapeBounds2.getHeight();

        double width1 = cutShapeBounds1.getWidth();
        double width2 = cutShapeBounds2.getWidth();

        double p1 = 2*height1 + 2*width1;
        double p2 = 2*height2 + 2*width2;

        if ( p1 < p2 ) return -1;
        else if ( p1 == p2 ) return 0;
        else return 1;

    }

}
