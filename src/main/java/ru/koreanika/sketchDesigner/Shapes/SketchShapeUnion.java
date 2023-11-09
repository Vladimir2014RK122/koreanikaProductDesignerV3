package ru.koreanika.sketchDesigner.Shapes;

import ru.koreanika.common.ConnectPoints.ConnectPoint;
import ru.koreanika.common.material.Material;
//import com.sun.javafx.geometry.BoundsUtils;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.cutDesigner.Shapes.CutShapeEdge;
import ru.koreanika.cutDesigner.Shapes.CutShapeUnion;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.InfoMessage;

import java.util.ArrayList;

public class SketchShapeUnion {

    ArrayList<Rotate> rotateTransformList = new ArrayList<>();
    double rotateAngle = 0.0;

    Polygon polygon;
    Point2D[] points;
    Path path = new Path();

    Color unionColor = Color.TRANSPARENT;

    Material shapeMaterial = null;
    int shapeDepth = 0;

    private boolean addDimensionMode = false;

    ArrayList<SketchShape> sketchShapesInUnion = new ArrayList<>();
    ArrayList<Point2D> sketchShapesPositions = new ArrayList<>();
    ArrayList<CutShape> cutShapesInUnionList;
    ArrayList<CutShapeEdge> cutShapeEdgesInUnion;

    ArrayList<ConnectPoint> connectPoints = new ArrayList<>();

    private CutShapeUnion cutShapeUnion = null;

    private int unionNumber = 0;

    private ElementTypes unionType;


    private Image imageForFill = null;

    private SketchShapeUnion() {

//        this.setOnMouseClicked(event -> onMouseClickedCenterArea(event));
//        this.setOnMousePressed(event -> onMousePressedCenterArea(event));
//        this.setOnMouseReleased(event -> onMouseReleasedCenterArea(event));
//        this.setOnDragDetected(event -> onDragDetectedCenterArea(event));
//        this.setOnMouseDragged(event -> onMouseDraggedCenterArea(event));
//
//        this.setPickOnBounds(false);
    }

    public static SketchShapeUnion createSketchShapeUnion(ArrayList<SketchShape> sketchShapesInUnion) {


        SketchShapeUnion sketchShapeUnion = new SketchShapeUnion();

        //sketchShapeUnion.sketchShapesInUnion.addAll(sketchShapesInUnion);

        if (sketchShapesInUnion.size() < 2) {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Выберите более 1-й фигуры", null);
            return null;
        }

        for (SketchObject skObj : sketchShapesInUnion) {
            if (!(skObj instanceof SketchShape)) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Объединения можно создавать только из фигур!", null);
                return null;
            }
        }

        sketchShapeUnion.unionType = ((SketchShape) sketchShapesInUnion.get(0)).getElementType();
        sketchShapeUnion.shapeMaterial = ((SketchShape) sketchShapesInUnion.get(0)).getMaterial();
        sketchShapeUnion.shapeDepth = ((SketchShape) sketchShapesInUnion.get(0)).getShapeDepth();

        for (SketchShape sketchShape : sketchShapesInUnion) {
            sketchShapeUnion.getSketchShapesInUnion().add(sketchShape);

            if (!sketchShape.getMaterial().equals(sketchShapeUnion.shapeMaterial)) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Материалы фигур не идентичны!", null);
                sketchShapeUnion = null;
                break;
            }
            if (sketchShape.getShapeDepth() != sketchShapeUnion.shapeDepth) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Толщина материала фигур отличается!", null);
                sketchShapeUnion = null;
                break;
            }
        }

//        Shape path = new Path();
//        for(SketchShape sketchShape : sketchShapesInUnion){
//
//            path = Shape.union(path, sketchShape.getPolygon());
//        }
//
//        System.out.println("path.getWidth = " + path.getBoundsInLocal().getWidth()/ ProjectHandler.getCommonShapeScale());
//        System.out.println("path.getHeight = " + path.getBoundsInLocal().getHeight()/ ProjectHandler.getCommonShapeScale());
//        System.out.println("sketchShapesInUnion.get(0).getMaterial().getMaterialHeight() = " + sketchShapesInUnion.get(0).getMaterial().getMaterialHeight());
//        System.out.println("sketchShapesInUnion.get(0).getMaterial().getMaterialWidth() = " + sketchShapesInUnion.get(0).getMaterial().getMaterialWidth());
//
//        if(path.getBoundsInLocal().getHeight()/ ProjectHandler.getCommonShapeScale() > sketchShapesInUnion.get(0).getMaterial().getMaterialHeight() &&
//                path.getBoundsInLocal().getWidth()/ ProjectHandler.getCommonShapeScale() > sketchShapesInUnion.get(0).getMaterial().getMaterialHeight()){
//
//            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Размер объединения слишком велик!");
//            sketchShapeUnion = null;
//
//        }else if(path.getBoundsInLocal().getHeight()/ ProjectHandler.getCommonShapeScale() > sketchShapesInUnion.get(0).getMaterial().getMaterialWidth() &&
//                path.getBoundsInLocal().getWidth()/ ProjectHandler.getCommonShapeScale() > sketchShapesInUnion.get(0).getMaterial().getMaterialWidth()){
//
//            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Размер объединения слишком велик!");
//            sketchShapeUnion = null;
//        }

        SketchDesigner.unSelectAllShapes();
        SketchDesigner.selectedSketchObjects.clear();
        System.out.println("sketchShapeUnion = " + sketchShapeUnion);

        if (sketchShapeUnion != null) {
//            if(sketchShapeUnion.createUnionPolygon()){
//                sketchShapeUnion.setTranslateX(translateX);
//                sketchShapeUnion.setTranslateY(translateY);
//                sketchShapeUnion.initConnectionPoints();
//                sketchShapeUnion.unionNumber = getNewUnionNumber();
//            }else{
//                return null;
//            }
//            sketchShapeUnion.setTranslateX(translateX);
//            sketchShapeUnion.setTranslateY(translateY);
//            sketchShapeUnion.initConnectionPoints();

            sketchShapeUnion.sketchShapesPositions.clear();

            double minX = 10000;
            double minY = 10000;
            for (SketchObject sketchObject : sketchShapeUnion.sketchShapesInUnion) {
                if (sketchObject.getTranslateX() < minX) minX = sketchObject.getTranslateX();
                if (sketchObject.getTranslateY() < minY) minY = sketchObject.getTranslateY();
            }

            for (SketchObject sketchObject : sketchShapeUnion.sketchShapesInUnion) {
                System.out.println("sketchShapeUnion = " + sketchShapeUnion);
                SketchShape sketchShape = (SketchShape) sketchObject;
                sketchShape.setSketchShapeUnionOwner(true, sketchShapeUnion);


                Rotate rotateTransform = new Rotate();
                sketchShapeUnion.rotateTransformList.add(rotateTransform);
                sketchShape.getTransforms().add(rotateTransform);


                //getPositions:
                sketchShapeUnion.sketchShapesPositions.add(new Point2D(sketchShape.getTranslateX() - minX, sketchShape.getTranslateY() - minY));

            }

            sketchShapeUnion.setRotatePivots();

            sketchShapeUnion.unionNumber = getNewUnionNumber();


        } else {
            System.out.println();
            for (SketchObject sketchShape : sketchShapesInUnion) {
                ((SketchShape) sketchShape).setSketchShapeUnionOwner(false, null);
            }
        }

        return sketchShapeUnion;
    }

    public ArrayList<Point2D> getSketchShapesPositions() {
        return sketchShapesPositions;
    }

    private static int getNewUnionNumber() {
        int newNumber = 0;
        ArrayList<Integer> unionNumbersList = new ArrayList<>();
        for (SketchShapeUnion shUnion : SketchDesigner.getSketchShapeUnionsList()) {
            unionNumbersList.add(new Integer(shUnion.getUnionNumber()));
        }
        while (true) {
            if (!unionNumbersList.contains(new Integer(newNumber))) {
                unionNumbersList.add(new Integer(newNumber));
                return newNumber;
            } else {
                newNumber++;
            }
        }
    }

    public int getUnionNumber() {
        return unionNumber;
    }

    public ElementTypes getUnionType() {
        return unionType;
    }

    //    private boolean createUnionPolygon(){
//        polygon = new Polygon();
//
//
//        for(int i = 0;i< sketchShapesInUnion.size();i++){
//            path = (Path)Polygon.union(path, sketchShapesInUnion.get(i).getPolygon());
//            //shape = Shape.union(shape, sketchShapesInUnion.get(i).getPolygon());
//            sketchShapesInUnion.get(i).setSketchShapeUnionOwner(true, this);
//        }
//
//
//        //Double[] points = new Double[(path.getElements().size() - 1)*2];
//
//        ArrayList<Point2D> points = new ArrayList<>();
//
//        int i = 0;
//        for(PathElement el : path.getElements()){
//            if(el instanceof MoveTo){
//                MoveTo mt = (MoveTo) el;
//                Point2D p = new Point2D(mt.getX()/SketchDesigner.getSketchPaneScale(), mt.getY()/SketchDesigner.getSketchPaneScale());
//                points.add(p);
////                points[i] = mt.getX()/SketchDesigner.getSketchPaneScale();
////                points[i+1] = mt.getY()/SketchDesigner.getSketchPaneScale();
//            }else if(el instanceof LineTo){
//                LineTo lt = (LineTo) el;
//                Point2D p = new Point2D(lt.getX()/SketchDesigner.getSketchPaneScale(), lt.getY()/SketchDesigner.getSketchPaneScale());
//                points.add(p);
////                points[i] = lt.getX()/SketchDesigner.getSketchPaneScale();
////                points[i+1] = lt.getY()/SketchDesigner.getSketchPaneScale();
//            }else if(el instanceof ClosePath){
//                System.out.println("ClosePath");
//            }
//            i += 2;
//        }
//
//        for(Point2D p : points){
//            polygon.getPoints().add(p.getX());
//            polygon.getPoints().add(p.getY());
//        }
//        //polygon.getPoints().addAll(points);
//        System.out.println(path);
//        System.out.println(polygon);
//        System.out.println(polygon.getBoundsInLocal());
//        polygon.setFill(Color.BLACK);
//        try {
//            setPrefHeight(path.getBoundsInLocal().getHeight());
//            setPrefWidth(path.getBoundsInLocal().getWidth());
//            polygon.setFill(new Color(1,0,0,0.3));
//
//            getChildren().add(polygon);
//           // setStyle("-fx-background-color: Blue");
//
//            polygon.setTranslateX(polygon.getTranslateX() + (-1) * polygon.getBoundsInParent().getMinX());
//            polygon.setTranslateY(polygon.getTranslateY() + (-1) * polygon.getBoundsInParent().getMinY());
//
//            //SketchDesigner.getSketchPane().getChildren().add(this);
//        }catch(NullPointerException ex){
//            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Фигуры не соеденены или образуют кольцо!");
//            ex.printStackTrace();
//
//            for(SketchShape sketchShape : sketchShapesInUnion) {
//                sketchShape.setSketchShapeUnionOwner(false, null);
//            }
//            SketchDesigner.unSelectAllShapes();
//            SketchDesigner.selectedSketchObjects.clear();
//            return false;
//        }
//
//        polygon.setFill(Color.TRANSPARENT);
//        polygon.setStroke(Color.RED);
//        polygon.setStrokeType(StrokeType.INSIDE);
//
//        return true;
//    }

    public ArrayList<SketchShape> getSketchShapesInUnion() {
        return sketchShapesInUnion;
    }


//    public void initConnectionPoints() {
//        for(SketchShape sketchShape : sketchShapesInUnion){
//
//            for(ConnectPoint cp : sketchShape.getConnectPoints()){
//                ConnectPoint newConnectPoint = new CornerConnectPoint(this);
//
//                double x = (sketchShape.getTranslateX() - this.getTranslateX()) + cp.getTranslateX();
//                double y = (sketchShape.getTranslateY() - this.getTranslateY()) + cp.getTranslateY();
//
//                newConnectPoint.setTranslateX(x);
//                newConnectPoint.setTranslateY(y);
//                connectPoints.add(newConnectPoint);
//                getChildren().add(newConnectPoint);
//                newConnectPoint.hide();
//                newConnectPoint.toFront();
//
//                System.out.println(x);
//                System.out.println(y);
//
//            }
//        }
//
//    }


    public ArrayList<ConnectPoint> getConnectPoints() {
        return connectPoints;
    }


    public void showConnectionPoints() {
        for (ConnectPoint cp : connectPoints) {
            cp.show();
        }
    }


    public void hideConnectionPoints() {
        for (ConnectPoint cp : connectPoints) {
            cp.hide();
        }
    }


    Point2D setRotatePivots() {
        ArrayList<Point2D> polygonsPoints = new ArrayList<>();
        for (int i = 0; i < sketchShapesInUnion.size(); i++) {

            path = (Path) Polygon.union(path, sketchShapesInUnion.get(i).getPolygon());

            for (int j = 0; j < sketchShapesInUnion.get(i).getPolygon().getPoints().size(); j += 2) {
                double x = sketchShapesInUnion.get(i).getPolygon().getPoints().get(j);
                double y = sketchShapesInUnion.get(i).getPolygon().getPoints().get(j + 1);
                Point2D point = new Point2D(x, y);

                point = SketchDesigner.getSketchPane().sceneToLocal(sketchShapesInUnion.get(i).localToScene(point));
                polygonsPoints.add(point);
//                System.out.println(point);
//                Circle circlePivot = new Circle(point.getX(), point.getY(), 2, Color.RED);
//                SketchDesigner.getSketchPane().getChildren().add(circlePivot);
            }

        }

        double minX = 10000, maxX = 0;
        double minY = 10000, maxY = 0;
        for (Point2D p : polygonsPoints) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();

            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
        }
        //System.out.println("minX = " + minX + " maxX = " + maxX);
        //System.out.println("minY = " + minY + " maxY = " + maxY);

        Point2D pivot = new Point2D(minX + ((maxX - minX) / 2.0), minY + ((maxY - minY) / 2.0));
        //Point2D pivot = new Point2D(minX, minY);

        for (int i = 0; i < sketchShapesInUnion.size(); i++) {
            Point2D internalPivot = sketchShapesInUnion.get(i).sceneToLocal(SketchDesigner.getSketchPane().localToScene(pivot));
            rotateTransformList.get(i).setPivotX(internalPivot.getX());
            rotateTransformList.get(i).setPivotY(internalPivot.getY());

//            Circle circlePivot = new Circle(internalPivot.getX(), internalPivot.getY(), 2, Color.RED);
//            sketchShapesInUnion.get(i).getChildren().add(circlePivot);
        }

        return pivot;
    }

    public void rotate(double angle) {

        //Point2D pivot = getRotatePivot();


        for (int i = 0; i < sketchShapesInUnion.size(); i++) {

            //Point2D internalPivot = sketchShapesInUnion.get(i).sceneToLocal(SketchDesigner.getSketchPane().localToScene(pivot));
            //rotateTransformList.get(i).setPivotX(internalPivot.getX());
            //rotateTransformList.get(i).setPivotY(internalPivot.getY());
            rotateTransformList.get(i).setAngle(rotateAngle + angle);

            sketchShapesInUnion.get(i).getLabelShapeNumber().setRotate((-1) * (rotateTransformList.get(i).getAngle() + sketchShapesInUnion.get(i).getRotateTransform().getAngle()));
            for (ConnectPoint cp : sketchShapesInUnion.get(i).getConnectPoints()) {
                cp.setRotate((-1) * (rotateTransformList.get(i).getAngle() + sketchShapesInUnion.get(i).getRotateTransform().getAngle()));
            }


            //Circle circlePivot = new Circle(internalPivot.getX(), internalPivot.getY(), 2, Color.RED);
            //sketchShapesInUnion.get(i).getChildren().add(circlePivot);
        }

        rotateAngle += angle;

    }
//    @Override
//    public Polygon getPolygon() {
//        return polygon;
//    }
//
//    @Override
//    public int getShapeNumber() {
//        return unionNumber;
//    }



    /* CUT SHAPE START */

//    public CutShapeUnion getCutShapeUnion() {
//        //refreshCutShapeUnion();
//        if(cutShapeUnion == null){
//            createCutShapeUnion();
//        }
//        boolean unionCorrect = checkUnionAsCorrect(cutShapesInUnionList, cutShapeEdgesInUnion);
//        if(!unionCorrect) return null;
//        return cutShapeUnion;
//    }
//
//    public void refreshCutShapeUnion(){
//        //cutShapeUnion = null;
//        if(cutShapeUnion == null){
//            createCutShapeUnion();
//        }
//        //get all edges
//        //move edges if its over other shapes or edges
//    }

//    private void createCutShapeUnion(){
//        ArrayList<CutShape> cutShapesInUnionList = new ArrayList<>();
//        ArrayList<CutShapeEdge> cutShapeEdgesInUnion = new ArrayList<>();
//
//        cutShapeUnion = new CutShapeUnion(this);
//        Polygon cutShapeUnionPolygon = new Polygon();
//
//        //get minX and minY on relative position on Sketch pane
//        double minX = sketchShapesInUnion.get(0).getBoundsInParent().getMinX();
//        double minY = sketchShapesInUnion.get(0).getBoundsInParent().getMinY();
//        for(SketchShape shape: sketchShapesInUnion){
//            if(minX > shape.getBoundsInParent().getMinX()){
//                minX = shape.getBoundsInParent().getMinX();
//            }
//
//            if(minY > shape.getBoundsInParent().getMinY()){
//                minY = shape.getBoundsInParent().getMinY();
//            }
//        }
//
//        for(SketchShape shape: sketchShapesInUnion){
//            //get all cutShapes
//            //shape.refreshCutShape();
//            CutShape cutShape = shape.getCutShape();
//            cutShape.setTranslateX(shape.getTranslateX() - minX);
//            cutShape.setTranslateY(shape.getTranslateY() - minY);
//            cutShapesInUnionList.add(cutShape);
//            cutShape.setContainInUnion(true);
//
//            //get all edges
//            for(CutShapeEdge cutEdge : cutShape.getCutShapeEdgesList()){
//                if(cutEdge.getStartCoordinate() != null){
//                    cutShapeEdgesInUnion.add(cutEdge);
//                    cutEdge.setTranslateX(shape.getTranslateX() - minX + cutEdge.getStartCoordinate().getX());
//                    cutEdge.setTranslateY(shape.getTranslateY() - minY + cutEdge.getStartCoordinate().getY());
//                    System.out.println("cutEdge.getStartCoordinate().getX() = " + cutEdge.getStartCoordinate().getX());
//                }
//            }
//        }
//
//        //create polygon:
//        Path cutPath = new Path();
//        for(CutShape cutShape : cutShapesInUnionList){
//            cutPath = (Path)Polygon.union(cutPath, cutShape.getPolygon());
//            //shape = Shape.union(shape, sketchShapesInUnion.get(i).getPolygon());
//            //sketchShapesInUnion.get(i).setSketchShapeUnionOwner(true, this);
//            for(CutShapeEdge cutShapeEdge : cutShapeEdgesInUnion){
//                cutPath = (Path)Polygon.union(cutPath, cutShapeEdge.getPolygon());
//            }
//        }
//
//        Double[] points = new Double[(cutPath.getElements().size() - 1)*2];
//
//        int i = 0;
//        for(PathElement el : cutPath.getElements()){
//            if(el instanceof MoveTo){
//                MoveTo mt = (MoveTo) el;
//                points[i] = mt.getX()/CutDesigner.getCutPane().getScaleX();
//                points[i+1] = mt.getY()/CutDesigner.getCutPane().getScaleY();
//            }
//            if(el instanceof LineTo){
//                LineTo lt = (LineTo) el;
//                points[i] = lt.getX()/CutDesigner.getCutPane().getScaleX();
//                points[i+1] = lt.getY()/CutDesigner.getCutPane().getScaleY();
//            }
//            i += 2;
//        }
//
//        cutShapeUnionPolygon.getPoints().addAll(points);
//
//        cutShapeUnionPolygon.setFill(Color.BLUEVIOLET);
//        cutShapeUnionPolygon.setStroke(Color.BLACK);
//        cutShapeUnionPolygon.setStrokeType(StrokeType.INSIDE);
//        cutShapeUnionPolygon.setOpacity(0.5);
//
//        cutShapeUnion.setCutShapesInUnionList(cutShapesInUnionList);
//        cutShapeUnion.setCutShapeEdgesInUnion(cutShapeEdgesInUnion);
//
//        cutShapeUnion.getChildren().clear();
//        cutShapeUnion.setPolygon(cutShapeUnionPolygon);
//        cutShapeUnion.getChildren().add(cutShapeUnionPolygon);
//
//        //move edges if its over other shapes or edges
//    }

    private boolean checkUnionAsCorrect(ArrayList<CutShape> cutShapesInUnionList, ArrayList<CutShapeEdge> cutShapeEdgesInUnion) {

        //get cutShape
        //check that its edges not over any other shapes and edges
        //repeat with all cutShapes

        return false;
    }
    /* CUT SHAPE END */

//    @Override
//    protected void onMousePressedCenterArea(MouseEvent event){
//        if(addDimensionMode) return;
//        //System.out.println("pressed" + consistInUnion + (consistInSketchShapeUnion != null?  consistInSketchShapeUnion.isConnected() : consistInSketchShapeUnion));
//
//        double scale = SketchDesigner.getSketchPaneScale();
//        orgSceneX = event.getSceneX()/scale;
//        orgSceneY = event.getSceneY()/scale;
//        orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
//        orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
//        ((Pane) (event.getSource())).toFront();
//        //show connectors area for all shapes on Pane
//        shapeContextMenu.hide();
//
//        for(SketchObject shObj : sketchShapesInUnion){
//            shObj.toFront();
//        }
//        this.toFront();
//        event.consume();
//    }
//    @Override
//    protected void onMouseClickedCenterArea(MouseEvent event){
//        if(addDimensionMode) return;
//        //System.out.println("clicked");
//
//        if(event.getButton() == MouseButton.PRIMARY){
//            if(SketchDesigner.getSelectionModeForEdges()){
//                event.consume();
//                return;
//            }
//            if(SketchDesigner.multipleSelectionMode == true){
//                SketchDesigner.selectedSketchObjects.add(this);
//                SketchDesigner.hideShapeSettings();
//            }else{
//                if(SketchDesigner.selectedSketchObjects.size() != 0)SketchDesigner.selectedSketchObjects.get(0).unSelectShape();
//                //SketchDesigner.selectedShapes.clear();
//                SketchDesigner.unSelectAllShapes();
//                SketchDesigner.selectedSketchObjects.add(this);
//                SketchDesigner.showShapeSettings();
//            }
//            this.selectShape();
//        }else if(event.getButton() == MouseButton.SECONDARY){
//            //shapeContextMenu.show(childShape.getScene().getWindow(), event.getScreenX(), event.getScreenY());
//        }
//
//        event.consume();
//    }
//
//    @Override
//    protected void onDragDetectedCenterArea(MouseEvent event){
//        if(addDimensionMode) return;
//
//        for(SketchShape shape : SketchDesigner.getSketchShapesList()){
//            if (!shape.equals(event.getSource()))
//                if(ConnectPoint.draggablePoint != null){
//                    shape.showConnectionPoints();
//                    setMouseTransparent(true);
//                }
//        }
//        for(SketchShapeUnion shapeUnion : SketchDesigner.getSketchShapeUnionsList()){
//            if (!shapeUnion.equals(event.getSource()))
//                if(ConnectPoint.draggablePoint != null){
//                    shapeUnion.showConnectionPoints();
//                    setMouseTransparent(true);
//                }
//        }
//        SketchDesigner.setDraggedShape((SketchShapeUnion)event.getSource());
//
//        startFullDrag();
//        event.consume();
//    }
//
//    protected void onMouseReleasedCenterArea(MouseEvent event){
//        if(addDimensionMode) return;
//
//        for(SketchShape shape : SketchDesigner.getSketchShapesList()){
//            if (!shape.equals(event.getSource()))
//                shape.hideConnectionPoints();
//        }
//        for(SketchShapeUnion shapeUnion : SketchDesigner.getSketchShapeUnionsList()){
//            if (!shapeUnion.equals(event.getSource()))
//                shapeUnion.hideConnectionPoints();
//        }
//        SketchDesigner.setDraggedShape(null);
//
//        setMouseTransparent(false);
//
////        for(SketchObject shObj : sketchShapesInUnion){
//////            shObj.toFront();
//////        }
//////        this.toFront();
//
//        SketchDesigner.allDimensionsToFront();
//
//        ArrayList<SketchObject> otherSketchObjects = new ArrayList<>();
//        otherSketchObjects.addAll(SketchDesigner.getSketchShapesList());
//
//        if(overOtherShape(this, otherSketchObjects)){
//            this.setOpacity(0.5);
//            for(SketchObject shObj : sketchShapesInUnion){
//                shObj.setOpacity(0.5);
//            }
//        }else{
//            this.setOpacity(1.0);
//            for(SketchObject shObj : sketchShapesInUnion){
//                shObj.setOpacity(1.0);
//            }
//        }
//
//        event.consume();
//    }
//    @Override
//    protected void onMouseDraggedCenterArea(MouseEvent event){
//        if(addDimensionMode) return;
//
//        double scale = SketchDesigner.getSketchPaneScale();
//
//        double offsetX = event.getSceneX() / scale - orgSceneX;
//        double offsetY = event.getSceneY() / scale - orgSceneY;
//
//        double oldTranslateX = this.getTranslateX();
//        double oldTranslateY = this.getTranslateY();
//
//        double newTranslateX = orgTranslateX + offsetX;
//        double newTranslateY = orgTranslateY + offsetY;
//
//        setTranslateX(newTranslateX);
//        setTranslateY(newTranslateY);
//
//        double shiftX = newTranslateX - oldTranslateX;
//        double shiftY = newTranslateY - oldTranslateY;
//
//        for(SketchObject shObj : sketchShapesInUnion){
//            shObj.setTranslateX(shObj.getTranslateX() + shiftX);
//            shObj.setTranslateY(shObj.getTranslateY() + shiftY);
//        }
//
//        ArrayList<SketchObject> otherSketchObjects = new ArrayList<>();
//        otherSketchObjects.addAll(SketchDesigner.getSketchShapesList());
//
//        if(overOtherShape(this, otherSketchObjects)){
//            this.setOpacity(0.5);
//            for(SketchObject shObj : sketchShapesInUnion){
//                shObj.setOpacity(0.5);
//            }
//        }else{
//            this.setOpacity(1.0);
//            for(SketchObject shObj : sketchShapesInUnion){
//                shObj.setOpacity(1.0);
//            }
//        }
//
//        event.consume();
//        //hideDimensions();
//
//
//    }
//
//    @Override
//    public void rotateShape(double angle) {
//
//    }


    public void createContextMenu() {

    }


    public void addDimensionsMode(boolean mode) {
        for (ConnectPoint connectPoint : connectPoints) {
            connectPoint.setSelectionMode(mode);
        }
        addDimensionMode = mode;
    }


    public Node getViewForListCell() {
        Pane pane = new Pane();

        double newScale = 1.0;

        path = new Path();
        for (int i = 0; i < sketchShapesInUnion.size(); i++) {
            path = (Path) Polygon.union(path, sketchShapesInUnion.get(i).getPolygon());
        }

        if (path.getBoundsInLocal().getWidth() > path.getBoundsInLocal().getHeight()) {
            newScale = 30.0 / path.getBoundsInLocal().getWidth();
        } else {
            newScale = 30.0 / path.getBoundsInLocal().getHeight();
        }

        pane.getChildren().add(path);

        path.setRotate(-rotateAngle);
        path.setScaleX(newScale);
        path.setScaleY(newScale);


        path.setTranslateX(-path.getBoundsInParent().getMinX() + (30 - path.getBoundsInParent().getWidth()) / 2);
        path.setTranslateY(-path.getBoundsInParent().getMinY() + (30 - path.getBoundsInParent().getHeight()) / 2);
        path.setFill(Color.BLUE);

        Label labelShapeNumber = new Label(String.valueOf(unionNumber));
        labelShapeNumber.setStyle("-fx-text-fill:#B3B4B4;");
        //pane.getChildren().add(labelShapeNumber);

        pane.setPrefWidth(30);
        pane.setPrefHeight(30);


        return pane;
    }

    public Tooltip getTooltipForListCell() {
        Tooltip tooltip = new Tooltip("Union #" + unionNumber);

        return tooltip;
    }

    public void deleteCutShapeUnion() {
        cutShapeUnion = null;
    }

//    @Override
//    public void refreshCutShapeView() {
//
//    }

    public void selectShape() {
        polygon.setFill(new Color(1, 0.647, 0, 0.6));
        polygon.setStroke(Color.ORANGE);
        polygon.setStrokeWidth(2.0);
    }

    public void unSelectShape() {
        polygon.setStroke(Color.BLUEVIOLET);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setStrokeWidth(1);
        polygon.setFill(Color.TRANSPARENT);
    }

//    @Override
//    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint) {
//
//        SketchObject draggableShape = (SketchObject) draggablePoint.getPointOwner();
//        SketchObject staticShape = (SketchObject) staticPoint.getPointOwner();
//
//        draggableShape.hideConnectionPoints();
//        staticShape.hideConnectionPoints();
//
//        double deltaX = draggableShape.getTranslateX() - (staticPoint.getTranslateX() + 5 + staticShape.getTranslateX() - (draggablePoint.getTranslateX() + 5));
//        double deltaY = draggableShape.getTranslateY() - (staticPoint.getTranslateY() + 5 + staticShape.getTranslateY() - (draggablePoint.getTranslateY() + 5));
//
//        draggableShape.setTranslateX(staticPoint.getTranslateX() + 5 + staticShape.getTranslateX() - (draggablePoint.getTranslateX() + 5));
//        draggableShape.setTranslateY(staticPoint.getTranslateY() + 5 + staticShape.getTranslateY() - (draggablePoint.getTranslateY() + 5));
//    }

    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();

        object.put("unionNumber", unionNumber);

        object.put("material", shapeMaterial.getName());
        object.put("shapeDepth", shapeDepth);
        object.put("rotateAngle", rotateAngle);
//        object.put("opacity", this.getOpacity());

        JSONArray shapesNumbersIntoUnion = new JSONArray();
        for (SketchShape sketchShape : sketchShapesInUnion) {
            shapesNumbersIntoUnion.add(sketchShape.getShapeNumber());
        }
        object.put("shapesNumbersIntoUnion", shapesNumbersIntoUnion);

        return object;
    }


    public static SketchShapeUnion initFromJson(JSONObject jsonObject) {

        JSONArray shapesNumbersIntoUnion = (JSONArray) (jsonObject.get("shapesNumbersIntoUnion"));
        ArrayList<SketchShape> shapesList = new ArrayList<SketchShape>(shapesNumbersIntoUnion.size());
        for (Object obj : shapesNumbersIntoUnion) {
            int n = ((Long) obj).intValue();
            shapesList.add(SketchDesigner.getSketchShape(n));
            SketchDesigner.getSketchShape(n).edgesDisable(true);
            //System.out.println(SketchDesigner.getSketchShape(n));
        }


        SketchShapeUnion sketchShapeUnion = createSketchShapeUnion(shapesList);
        if (sketchShapeUnion == null) return null;
        int unionNumber = ((Long) jsonObject.get("unionNumber")).intValue();
        sketchShapeUnion.unionNumber = unionNumber;

        double rotateAngle = ((Double) jsonObject.get("rotateAngle")).intValue();
        sketchShapeUnion.rotate(rotateAngle);
//
//        sketchShapeUnion.setOpacity(((Double)jsonObject.get("opacity")).doubleValue());

        return sketchShapeUnion;
    }
}
