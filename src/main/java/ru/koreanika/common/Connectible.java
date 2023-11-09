package ru.koreanika.common;

import ru.koreanika.common.ConnectPoints.ConnectPoint;

public interface Connectible {

    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint);

    void showConnectionPoints();

    void hideConnectionPoints();
}
