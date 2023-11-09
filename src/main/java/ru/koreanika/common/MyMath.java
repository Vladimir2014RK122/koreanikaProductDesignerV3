package ru.koreanika.common;

import javafx.geometry.Point2D;

public class MyMath {

    public static Point2D lineIntersection(Point2D line1p1, Point2D line1p2, Point2D line2p1, Point2D line2p2) {
        //line 1
        double x1 = line1p1.getX();
        double y1 = line1p1.getY();
        double x2 = line1p2.getX();
        double y2 = line1p2.getY();

        //line2
        double x3 = line2p1.getX();
        double y3 = line2p1.getY();
        double x4 = line2p2.getX();
        double y4 = line2p2.getY();

        double x = 0;
        double y = 0;

        double p = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        //line are parallel each other
        if (p == 0) {
            System.out.println("line are parallel");
            return null;
        } else {
            x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / p;
            y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / p;
        }

        return new Point2D(x, y);
    }

}
