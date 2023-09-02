package Common;

import Common.ConnectPoints.ConnectPoint;

public interface Connectible {

    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint);

    void showConnectionPoints();

    void hideConnectionPoints();
}
