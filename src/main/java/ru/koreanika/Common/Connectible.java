package ru.koreanika.Common;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;

public interface Connectible {

    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint);

    void showConnectionPoints();

    void hideConnectionPoints();
}
