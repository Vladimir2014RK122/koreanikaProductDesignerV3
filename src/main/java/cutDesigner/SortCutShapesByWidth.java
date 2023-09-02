package cutDesigner;

import cutDesigner.Shapes.CutShape;
import javafx.geometry.Bounds;

import java.util.Comparator;

public class SortCutShapesByWidth  implements Comparator<CutShape> {

    @Override
    public int compare(CutShape cutShape1, CutShape cutShape2) {

        Bounds cutShapeBounds1 = cutShape1.localToParent(cutShape1.getPolygon().getBoundsInParent());
        Bounds cutShapeBounds2 = cutShape2.localToParent(cutShape2.getPolygon().getBoundsInParent());
        double width1 = cutShapeBounds1.getWidth();
        double width2 = cutShapeBounds2.getWidth();

        if ( width1 < width2 ) return -1;
        else if ( width1 == width2 ) return 0;
        else return 1;

    }
}
