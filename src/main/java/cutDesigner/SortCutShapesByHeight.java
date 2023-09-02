package cutDesigner;

import cutDesigner.Shapes.CutShape;
import javafx.geometry.Bounds;

import java.util.Comparator;

public class SortCutShapesByHeight implements Comparator<CutShape> {
    @Override
    public int compare(CutShape cutShape1, CutShape cutShape2) {

        Bounds cutShapeBounds1 = cutShape1.localToParent(cutShape1.getPolygon().getBoundsInParent());
        Bounds cutShapeBounds2 = cutShape2.localToParent(cutShape2.getPolygon().getBoundsInParent());
        double height1 = cutShapeBounds1.getHeight();
        double height2 = cutShapeBounds2.getHeight();

        if ( height1 < height2 ) return -1;
        else if ( height1 == height2 ) return 0;
        else return 1;

    }
}
