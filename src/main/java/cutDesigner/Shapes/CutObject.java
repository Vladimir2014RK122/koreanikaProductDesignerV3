package cutDesigner.Shapes;

import Common.ConnectPoints.ConnectPoint;
import Common.Connectible;
import Common.Material.Material;
import cutDesigner.CutDesigner;
import cutDesigner.CutPane;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import sketchDesigner.Shapes.SketchShape;

import java.util.ArrayList;
import java.util.Map;

public abstract class CutObject extends Pane implements Connectible {

    //for mouse moving:
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

    //for dimensions:
    boolean addDimensionMode = false;
    boolean correctPlaced = true;

    //colors:
    //Color DISCONNECT_COLOR = Color.BLUEVIOLET;
    Color ERROR_COLOR = Color.RED;
    Color shapeColor;


    //SketchObject sketchObjectOwner = null;


    Polygon polygon;
    Polygon cutZonePolygon;
    Rotate rotateTransform = new Rotate();
    double rotateAngle = 0.0;
    protected Point2D globalCenter;

    Rotate rotateTransformGlobal = new Rotate();

    boolean containInUnion = false;
    int unionNumber = 0;
    CutShapeUnion cutShapeUnionOwner;

    String sizesInfo = "-";

    public CutObject() {
        //shapeColor = DISCONNECT_COLOR;
        //this.sketchObjectOwner = sketchObjectOwner;

        this.setOnMouseClicked(event -> onMouseClickedCenterArea(event));
        this.setOnMousePressed(event -> onMousePressedCenterArea(event));
        this.setOnMouseReleased(event -> onMouseReleasedCenterArea(event));
        this.setOnDragDetected(event -> onDragDetectedCenterArea(event));
        this.setOnMouseDragged(event -> onMouseDraggedCenterArea(event));

        this.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            if(polygon == null) return;
            this.checkCorrectPlaceOrNot();
        });


        this.pickOnBoundsProperty().set(false);

        this.getTransforms().add(rotateTransform);
        this.getTransforms().add(rotateTransformGlobal);
    }


    public void setSizesInfo(String sizesInfo) {
        this.sizesInfo = sizesInfo;
    }

    public String getSizesInfo() {
        return sizesInfo;
    }

    public void setGlobalCenter(Point2D globalCenter) {
        this.globalCenter = globalCenter;
    }

    public double getRotateAngle() {
        return rotateTransform.getAngle();
    }

    public boolean isCorrectPlaced() {
        return correctPlaced;
    }

    public Rotate getRotateTransform() {
        return rotateTransform;
    }

    public Rotate getRotateTransformGlobal() {
        return rotateTransformGlobal;
    }

    public static double getPolygonSquare(Polygon polygon) {

        //Gaus theorem
        double S = 0;
        double firstPart = 0;
        double secondPart = 0;
        for (int i = 0; i < polygon.getPoints().size() - 3; i++) {
            if (i % 2 == 0) {
                //X
                double x = polygon.getPoints().get(i).doubleValue();
                double y = polygon.getPoints().get(i + 3).doubleValue();
                firstPart += x * y;
            }
        }

        //System.out.println("firstPart = " + firstPart);
        for (int i = 0; i < polygon.getPoints().size() - 1; i++) {
            if (i % 2 != 0) {
                //Y
                double x = polygon.getPoints().get(i + 1).doubleValue();
                double y = polygon.getPoints().get(i).doubleValue();
                secondPart -= x * y;
            }
        }

        //System.out.println("secondPart = " + secondPart);

        S = (firstPart + secondPart) / 2;

        return Math.abs(S);
    }


    public boolean checkCorrectPlaceOrNot() {

        boolean intoMaterial = false;
        try {
            if (CutPane.checkIntoMaterialOrNot(this)) {
                intoMaterial = true;
            }
        } catch (IndexOutOfBoundsException e) {

        }


        /* CHECK OVER OR NOT OTHER SHAPE */
        ArrayList<CutObject> cutObjectsList = new ArrayList<>(CutDesigner.getInstance().getCutShapesList().size()
                + CutDesigner.getInstance().getCutShapeEdgesList().size() + CutDesigner.getInstance().getCutShapeUnionsList().size());

        // cutObjectsList.addAll(CutDesigner.getCutShapesList());
        //cutObjectsList.addAll(CutDesigner.getCutShapeEdgesList());

        //for(Node node : CutDesigner.getCutPane().getChildren()){
        for (Node node : CutDesigner.getInstance().getCutPane().getCutObjectsGroup().getChildren()) {
            if (node instanceof CutObject) {
                cutObjectsList.add((CutObject) node);
            }
        }
        //cutObjectsList.addAll(CutDesigner.getCutShapeUnionsList());

        boolean overShape = false;
        try {
            if (intoMaterial) {

                overShape = overOtherShape(this, cutObjectsList);
            }
        } catch (IndexOutOfBoundsException e) {

        }

//        if(this instanceof CutShape){
//            System.out.println("shape#" + ((CutShape)this).getShapeNumber() + "overShape = " + overShape);
//            System.out.println("shape#" + ((CutShape)this).getShapeNumber() + "intoMaterial = " + intoMaterial);
//        if(this instanceof CutShapeEdge){
//            System.out.println("edge#" + ((CutShapeEdge)this).getOwner().getShapeNumber() + "overShape = " + overShape);
//            System.out.println("edge#" + ((CutShapeEdge)this).getOwner().getShapeNumber() + "intoMaterial = " + intoMaterial);
//        }



        //boolean overShape = false;
        if (overShape || !intoMaterial) {
            shapeColor = ERROR_COLOR;
            correctPlaced = false;
        } else {
            shapeColor = getShapeColor();
            correctPlaced = true;
        }

        polygon.setFill(shapeColor);

        return correctPlaced;
    }


    public abstract Color getShapeColor();

    public abstract void setShapeColor(Color color);


    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
        //System.out.println(this.polygon);
    }

    public Polygon getCutZonePolygon() {
        return cutZonePolygon;
    }

    public void setCutZonePolygon(Polygon cutZonePolygon) {
        this.cutZonePolygon = cutZonePolygon;
    }

    /* MOUSE LOGIC START */
    protected void onMousePressedCenterArea(MouseEvent event) {
        double scale = CutPane.getCutPaneScale();//SketchDesigner.getSketchPane().getScaleX();
        orgSceneX = event.getSceneX() / scale;
        orgSceneY = event.getSceneY() / scale;
        orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
        orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
        ((Pane) (event.getSource())).toFront();

        event.consume();
    }

    protected void onMouseClickedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;
        if (event.getButton() == MouseButton.PRIMARY) {
            if (CutDesigner.getInstance().multipleSelectionMode != true) {
                if (CutDesigner.getInstance().selectedShapes.size() != 0) CutDesigner.getInstance().selectedShapes.get(0).unSelectShape();

                CutDesigner.getInstance().unSelectAllShapes();
            }


            if (isContainInUnion()) {
                for (CutShape cutShape : getCutShapeUnionOwner().getCutShapesInUnionList()) {
                    int shapeNumber = cutShape.getShapeNumber();
                    CutDesigner.getInstance().getCutPane().getCutShapeByNumber(shapeNumber).selectShape();
                    CutDesigner.getInstance().selectedShapes.add(CutDesigner.getInstance().getCutPane().getCutShapeByNumber(shapeNumber));

                    for (CutShapeEdge edge : cutShape.getCutShapeEdgesList()) {
                        edge.selectShape();
                        CutDesigner.getInstance().selectedShapes.add(edge);
                    }
                }
            } else {

                if (this instanceof CutShape) {

                    CutDesigner.getInstance().selectedShapes.add(this);
                    this.selectShape();
//                    for(CutShapeEdge edge : ((CutShape)this).getCutShapeEdgesList()){
//
//                        edge.selectShape();
//                        CutDesigner.selectedShapes.add(edge);
//                    }
                    System.out.println("select shape");

                } else if (this instanceof CutShapeEdge) {
                    CutDesigner.getInstance().selectedShapes.add(this);
                    this.selectShape();
//                    CutShape cutShape = ((CutShapeEdge)this).getOwner();
//                    for(CutShapeEdge edge : cutShape.getCutShapeEdgesList()){
//                        edge.selectShape();
//                        CutDesigner.selectedShapes.add(edge);
//                    }
//                    CutDesigner.selectedShapes.add(cutShape);
                } else if (this instanceof CutShapeAdditionalFeature) {

                    this.selectShape();
                    CutDesigner.getInstance().selectedShapes.add(this);
                }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {


        }
        event.consume();
    }

    protected void onDragDetectedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;
        if (ConnectPoint.draggablePoint != null) {

            //for(Node node : CutDesigner.getCutPane().getChildren()){
            for (Node node : CutDesigner.getInstance().getCutPane().getCutObjectsGroup().getChildren()) {
                if (node instanceof Connectible) {
                    if (node.equals(event.getSource())) continue;
                    ((Connectible) node).showConnectionPoints();
                }
            }
            for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : CutPane.getMaterialSheetsMap().entrySet()) {
                for (Material.MaterialSheet sheet : entry.getValue()) {
                    sheet.showConnectionPoints();
                }
            }
//            for(CutShape shape : CutDesigner.getCutShapesList()){
//                if (!shape.equals(event.getSource())){
//                    if(shape.isContainInUnion() != true){
//                        shape.showConnectionPoints();
//                    }
//                }
//            }
//            for(CutShapeEdge cutShapeEdge : CutDesigner.getCutShapeEdgesList()){
//                if (!cutShapeEdge.equals(event.getSource())){
//                    if(cutShapeEdge.getOwner().isContainInUnion() != true){
//                        cutShapeEdge.showConnectionPoints();
//                    }
//                }
//            }
//            for(CutShapeUnion shapeUnion : CutDesigner.getCutShapeUnionsList()){
//                if (!shapeUnion.equals(event.getSource())){
//
//                    shapeUnion.showConnectionPoints();
//
//                }
//            }

            if (this instanceof CutShape) {
                if (((CutShape) this).isSaveMaterialImage()) {
                    for (CutShapeEdge edge : ((CutShape) this).getCutShapeEdgesList()) {
                        edge.hideConnectionPoints();
                        edge.setMouseTransparent(true);
                    }
                }

            } else if (this instanceof CutShapeEdge) {
                if (((CutShapeEdge) this).getOwner().isSaveMaterialImage()) {
                    ((CutShapeEdge) this).getOwner().hideConnectionPoints();
                    ((CutShapeEdge) this).getOwner().setMouseTransparent(true);
                    for (CutShapeEdge edge : ((CutShapeEdge) this).getOwner().getCutShapeEdgesList()) {
                        if (this == edge) continue;
                        edge.hideConnectionPoints();
                        edge.setMouseTransparent(true);
                    }
                }
            }

        }

        setMouseTransparent(true);
        startFullDrag();
        event.consume();
    }


    protected void onMouseReleasedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;

        //for(Node node : CutDesigner.getCutPane().getChildren()){
        for (Node node : CutDesigner.getInstance().getCutPane().getCutObjectsGroup().getChildren()) {
            if (node instanceof Connectible) {
                if (node.equals(event.getSource())) continue;
                ((Connectible) node).hideConnectionPoints();
            }
        }

        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : CutPane.getMaterialSheetsMap().entrySet()) {
            for (Material.MaterialSheet sheet : entry.getValue()) {
                sheet.hideConnectionPoints();
            }
        }

//        for(CutShape shape : CutDesigner.getCutShapesList()){
//            if (!shape.equals(event.getSource()))
//                shape.hideConnectionPoints();
//        }
//        for(CutShapeEdge cutShapeEdge : CutDesigner.getCutShapeEdgesList()){
//            if (!cutShapeEdge.equals(event.getSource())){
//                if(cutShapeEdge.getOwner().isContainInUnion() != true){
//                    cutShapeEdge.hideConnectionPoints();
//                }
//            }
//        }
//        for(CutShapeUnion shapeUnion : CutDesigner.getCutShapeUnionsList()){
//            if (!shapeUnion.equals(event.getSource())){
//                shapeUnion.hideConnectionPoints();
//            }
//        }


        if (this instanceof CutShape) {
            for (CutShapeEdge edge : ((CutShape) this).getCutShapeEdgesList()) {
                edge.setMouseTransparent(false);
                edge.checkCorrectPlaceOrNot();
            }
            //this.checkCorrectPlaceOrNot();
        } else if (this instanceof CutShapeEdge) {
            ((CutShapeEdge) this).getOwner().setMouseTransparent(false);
            ((CutShapeEdge) this).getOwner().checkCorrectPlaceOrNot();
            for (CutShapeEdge edge : ((CutShapeEdge) this).getOwner().getCutShapeEdgesList()) {
                //edge.checkCorrectPlaceOrNot();
                if (this == edge) continue;
                edge.setMouseTransparent(false);
                edge.checkCorrectPlaceOrNot();
            }
        }
        setMouseTransparent(false);

        CutPane.hideHalfOfMaterialIfNotUsed();


        event.consume();
    }

    protected void onMouseDraggedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;


//        double scale = CutPane.getCutPaneScale();//SketchDesigner.getSketchPane().getScaleX();
//
//        double offsetX = event.getSceneX() / scale - orgSceneX;
//        double offsetY = event.getSceneY() / scale - orgSceneY;
//        double newTranslateX = orgTranslateX + offsetX;
//        double newTranslateY = orgTranslateY + offsetY;
//
//        double oldNewTranslateX = getTranslateX() - newTranslateX;
//        double oldNewTranslateY = getTranslateY() - newTranslateY;
//
//
//        setTranslateX(newTranslateX);
//        setTranslateY(newTranslateY);


    }
    /* MOUSE LOGIC END*/



    /* SELECTION */


    public void selectShape() {
        //shapeColor = SELECTED_COLOR;
        //shapeColor = SELECTED_COLOR;
        polygon.setFill(SketchShape.SELECTED_COLOR);
//        CutDesigner.getInstance().updateShapeInfo();

    }

    public void unSelectShape() {
        //shapeColor = DISCONNECT_COLOR;
        polygon.setFill(getShapeColor());
//        CutDesigner.getInstance().updateShapeInfo();

    }

    /* SELECTION */


    protected boolean overOtherShape(CutObject cutObject1, ArrayList<CutObject> otherCutObjects) throws IndexOutOfBoundsException {

        boolean result = false;
        //get cutObject1 poligon:
        if(cutObject1.getCutZonePolygon() == null) return false;
        ArrayList<Point2D> thisPolygonPoints = new ArrayList<>();
        for (int i = 0; i < cutObject1.getCutZonePolygon().getPoints().size(); i += 2) {
            double x = cutObject1.getCutZonePolygon().getPoints().get(i);
            double y = cutObject1.getCutZonePolygon().getPoints().get(i + 1);
            Point2D pointOnCutPane = CutDesigner.getInstance().getCutPane().sceneToLocal(cutObject1.localToScene(x, y));
            thisPolygonPoints.add(pointOnCutPane);
        }
        Polygon thisPolygon = new Polygon();
        for (Point2D p : thisPolygonPoints) {
            thisPolygon.getPoints().add(p.getX());
            thisPolygon.getPoints().add(p.getY());
        }

        //remove cutObject1 from otherCutObjectsList:
        otherCutObjects.remove(cutObject1);

        //remove edges for cutObject1 if saveImage == true form otherCutObjectsList:
        if (cutObject1 instanceof CutShape && ((CutShape) cutObject1).isSaveMaterialImage()) {
            otherCutObjects.removeAll(((CutShape) cutObject1).getCutShapeEdgesList());
        }

        if (cutObject1 instanceof CutShapeEdge && ((CutShapeEdge) cutObject1).getOwner().isSaveMaterialImage()) {
            otherCutObjects.removeAll(((CutShapeEdge) cutObject1).getOwner().getCutShapeEdgesList());
            otherCutObjects.remove(((CutShapeEdge) cutObject1).getOwner());
        }

        //remove from check union shapes:
//        System.out.println("cutObject1 = " + cutObject1 + " contain in union =" + cutObject1.isContainInUnion());
        if (cutObject1.isContainInUnion()) {

            otherCutObjects.removeAll(cutObject1.getCutShapeUnionOwner().getCutShapesInUnionList());
            otherCutObjects.removeAll(cutObject1.getCutShapeUnionOwner().getCutShapeEdgesInUnion());

//            System.out.println("cutObject1.getCutShapeUnionOwner().getCutShapesInUnionList() = " + cutObject1.getCutShapeUnionOwner().getCutShapesInUnionList());
//            System.out.println("cutObject1.getCutShapeUnionOwner().getCutShapeEdgesInUnion() = " + cutObject1.getCutShapeUnionOwner().getCutShapeEdgesInUnion());
        }


        for (CutObject otherCutObject : otherCutObjects) {

            ArrayList<Point2D> otherPolygonPoints = new ArrayList<>();
            for (int i = 0; i < otherCutObject.getCutZonePolygon().getPoints().size(); i += 2) {
                double x = otherCutObject.getCutZonePolygon().getPoints().get(i);
                double y = otherCutObject.getCutZonePolygon().getPoints().get(i + 1);
                Point2D pointOnCutPane = CutDesigner.getInstance().getCutPane().sceneToLocal(otherCutObject.localToScene(x, y));
                otherPolygonPoints.add(pointOnCutPane);
            }
            Polygon otherCutObjectPolygon = new Polygon();
            for (Point2D p : otherPolygonPoints) {
                otherCutObjectPolygon.getPoints().add(p.getX());
                otherCutObjectPolygon.getPoints().add(p.getY());
            }
//          Shape shape = Shape.intersect(thisPolygon, otherCutObjectPolygon);
//          shape.setScaleX(10);
//          shape.setScaleY(10);
//          CutDesigner.getCutPane().getChildren().add(shape);
//          shape.setFill(Color.BLACK);
            if (((Path) Shape.intersect(thisPolygon, otherCutObjectPolygon)).getElements().size() != 0) {
                //System.out.println("OVER! " + otherCutObject);
                if (Math.abs(pathSquare(((Path) Shape.intersect(thisPolygon, otherCutObjectPolygon)))) > 0.5) {
                    //System.out.println("OVER! but < 0.5 = " + Math.abs(pathSquare(((Path)Shape.intersect(thisPolygon, otherCutObjectPolygon)))));
                    //result =  true;
                    return true;
                }
                //result =  false;

            }
        }
        return false;
    }

    public static double pathSquare(Path path) {
        double square = 0.0;
        ArrayList<Polygon> polygonsList = new ArrayList<>();
        Polygon polygon = new Polygon();
        ;
        for (PathElement element : path.getElements()) {
            if (element instanceof MoveTo) {
                polygon = new Polygon();
                polygon.getPoints().add(Double.valueOf(((MoveTo) element).getX()));
                polygon.getPoints().add(Double.valueOf(((MoveTo) element).getY()));

            } else if (element instanceof LineTo) {
                polygon.getPoints().add(Double.valueOf(((LineTo) element).getX()));
                polygon.getPoints().add(Double.valueOf(((LineTo) element).getY()));

            } else if (element instanceof ClosePath) {
                polygonsList.add(polygon);
            }
        }

        for (Polygon pol : polygonsList) {
            square = square + getPolygonSquare(pol);
        }

        return square;
    }

    public abstract void rotateShapeLocal(double angle);

    public abstract void rotateShapeGlobal(double angle, Point2D pivot);

    public abstract void showConnectionPoints();

    public abstract void hideConnectionPoints();

    public abstract Material getMaterial();

    public abstract int getDepth();

    public boolean isContainInUnion() {
        return containInUnion;
    }


    public void setContainInUnion(boolean containInUnion) {
        this.containInUnion = containInUnion;
    }

    public void setUnionNumber(int unionNumber) {
        this.unionNumber = unionNumber;
    }

    public int getUnionNumber() {
        return cutShapeUnionOwner.getUnionNumber();
    }

    public void setCutShapeUnionOwner(CutShapeUnion cutShapeUnionOwner) {
        this.cutShapeUnionOwner = cutShapeUnionOwner;
    }

    public CutShapeUnion getCutShapeUnionOwner() {
        return cutShapeUnionOwner;
    }


    @Override
    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint) {

        double shiftForCutting = 10;

        if (staticPoint.getPointOwner() instanceof CutObject) {

            CutObject draggableShape = (CutObject) draggablePoint.getPointOwner();
            CutObject staticShape = (CutObject) staticPoint.getPointOwner();

            draggableShape.hideConnectionPoints();
            staticShape.hideConnectionPoints();

            Bounds staticBounds = CutDesigner.getInstance().getCutPane().sceneToLocal(staticPoint.localToScene(staticPoint.getBoundsInLocal()));
            Bounds draggableBounds = CutDesigner.getInstance().getCutPane().sceneToLocal(draggablePoint.localToScene(draggablePoint.getBoundsInLocal()));

            Point2D staticPointOnWorkPane = CutDesigner.getInstance().getCutPane().sceneToLocal(staticShape.localToScene(staticPoint.getSetPointShift()));
            Point2D draggablePointOnWorkPane = CutDesigner.getInstance().getCutPane().sceneToLocal(draggableShape.localToScene(draggablePoint.getSetPointShift()));


            double deltaX = (staticPointOnWorkPane.getX() - draggablePointOnWorkPane.getX());
            double deltaY = (staticPointOnWorkPane.getY() - draggablePointOnWorkPane.getY());



//            Circle staticCircle = new Circle(staticBounds.getMinX() + staticBounds.getWidth() / 2, staticBounds.getMinY() + staticBounds.getHeight() / 2, 3, Color.RED);
//            CutDesigner.getCutPane().getChildren().add(staticCircle);
//
//            Circle draggableCircle = new Circle(draggablePointOnWorkPane.getX(), draggablePointOnWorkPane.getY(), 5, Color.YELLOW);
//            CutDesigner.getCutPane().getChildren().add(draggableCircle);

            draggableShape.setTranslateX(draggableShape.getTranslateX() + deltaX);
            draggableShape.setTranslateY(draggableShape.getTranslateY() + deltaY);

            if (draggableShape instanceof CutShape) {
                if (((CutShape) draggableShape).isSaveMaterialImage()) {
                    for (CutShapeEdge edge : ((CutShape) draggableShape).getCutShapeEdgesList()) {
                        edge.setTranslateX(edge.getTranslateX() + deltaX);
                        edge.setTranslateY(edge.getTranslateY() + deltaY);
                    }
                }

                CutShape cutShape = (CutShape) draggableShape;
                if (cutShape.isContainInUnion()) {

                    for (CutShape cutShapeOther : cutShapeUnionOwner.getCutShapesInUnionList()) {

                        if (cutShapeOther.getShapeNumber() == cutShape.getShapeNumber()) continue;
                        cutShapeOther.setTranslateX(cutShapeOther.getTranslateX() + deltaX);
                        cutShapeOther.setTranslateY(cutShapeOther.getTranslateY() + deltaY);

                        if (cutShapeOther.isSaveMaterialImage()) {
                            for (CutShapeEdge edge : cutShapeOther.getCutShapeEdgesList()) {
                                edge.setTranslateX(edge.getTranslateX() + deltaX);
                                edge.setTranslateY(edge.getTranslateY() + deltaY);
                            }
                        }
                    }

                }
            } else if (draggablePoint.getPointOwner() instanceof CutShapeEdge) {

                if (((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().isSaveMaterialImage()) {
                    for (CutShapeEdge edge : ((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().getCutShapeEdgesList()) {
                        if (edge == draggablePoint.getPointOwner()) continue;
                        edge.setTranslateX(edge.getTranslateX() + deltaX);
                        edge.setTranslateY(edge.getTranslateY() + deltaY);
                    }
                    ((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().setTranslateX(((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().getTranslateX() + deltaX);
                    ((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().setTranslateY(((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().getTranslateY() + deltaY);
                }

                CutShape cutShape = ((CutShapeEdge) draggableShape).getOwner();
                if (cutShape.isContainInUnion()) {

                    for (CutShape cutShapeOther : cutShapeUnionOwner.getCutShapesInUnionList()) {

                        if (cutShapeOther.getShapeNumber() == cutShape.getShapeNumber()) continue;
                        cutShapeOther.setTranslateX(cutShapeOther.getTranslateX() + deltaX);
                        cutShapeOther.setTranslateY(cutShapeOther.getTranslateY() + deltaY);

                        if (cutShapeOther.isSaveMaterialImage()) {
                            for (CutShapeEdge edge : cutShapeOther.getCutShapeEdgesList()) {
                                edge.setTranslateX(edge.getTranslateX() + deltaX);
                                edge.setTranslateY(edge.getTranslateY() + deltaY);
                            }
                        }
                    }

                }
            }
            //draggableShape.hideConnectionPoints();

            staticBounds = CutDesigner.getInstance().getCutPane().sceneToLocal(staticShape.localToScene(staticShape.getCutZonePolygon().getBoundsInParent()));
            draggableBounds = CutDesigner.getInstance().getCutPane().sceneToLocal(draggableShape.localToScene(draggableShape.getCutZonePolygon().getBoundsInParent()));

            System.out.println("staticBounds=" + staticBounds);
            System.out.println("draggableBounds=" + draggableBounds);

            Bounds s = staticShape.localToParent(staticShape.getCutZonePolygon().getBoundsInParent());
            Bounds d = draggableShape.localToParent(draggableShape.getCutZonePolygon().getBoundsInParent());

            System.out.println("s=" + s);
            System.out.println("d=" + d);


        } else if (staticPoint.getPointOwner() instanceof Material.MaterialSheet) {

            Material.MaterialSheet sheet = (Material.MaterialSheet) staticPoint.getPointOwner();

            CutObject draggableShape = (CutObject) draggablePoint.getPointOwner();

            Bounds staticBounds = sheet.localToParent(staticPoint.getBoundsInParent());
            Bounds draggableBounds = draggableShape.localToParent(draggablePoint.getBoundsInParent());


            double deltaX = (staticBounds.getMinX() - draggableBounds.getMinX());
            double deltaY = (staticBounds.getMinY() - draggableBounds.getMinY());

            draggableShape.setTranslateX(draggableShape.getTranslateX() + deltaX);
            draggableShape.setTranslateY(draggableShape.getTranslateY() + deltaY);

//            System.out.println("Shape = " + draggableShape.localToParent(draggableShape.getPolygon().getBoundsInParent()));
//            System.out.println("staticShapeMaterial = " + sheet.localToParent(sheet.getPolygon().getBoundsInParent()));
//            System.out.println("staticShapeMaterialPoint = " + sheet.localToParent(staticPoint.getBoundsInParent()));
//            System.out.println("draggableShapeMaterialPoint = " + draggableShape.localToParent(draggablePoint.getBoundsInParent()));

            if (draggablePoint.getPointOwner() instanceof CutShape) {
                if (((CutShape) draggablePoint.getPointOwner()).isSaveMaterialImage()) {
                    for (CutShapeEdge edge : ((CutShape) draggablePoint.getPointOwner()).getCutShapeEdgesList()) {
                        edge.setTranslateX(edge.getTranslateX() + deltaX);
                        edge.setTranslateY(edge.getTranslateY() + deltaY);
                    }
                }

                CutShape cutShape = (CutShape) draggableShape;
                if (cutShape.isContainInUnion()) {

                    for (CutShape cutShapeOther : cutShapeUnionOwner.getCutShapesInUnionList()) {

                        if (cutShapeOther.getShapeNumber() == cutShape.getShapeNumber()) continue;
                        cutShapeOther.setTranslateX(cutShapeOther.getTranslateX() + deltaX);
                        cutShapeOther.setTranslateY(cutShapeOther.getTranslateY() + deltaY);

                        if (cutShapeOther.isSaveMaterialImage()) {
                            for (CutShapeEdge edge : cutShapeOther.getCutShapeEdgesList()) {
                                edge.setTranslateX(edge.getTranslateX() + deltaX);
                                edge.setTranslateY(edge.getTranslateY() + deltaY);
                            }
                        }
                    }

                }

            } else if (draggablePoint.getPointOwner() instanceof CutShapeEdge) {

                if (((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().isSaveMaterialImage()) {
                    for (CutShapeEdge edge : ((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().getCutShapeEdgesList()) {
                        if (edge == draggablePoint.getPointOwner()) continue;
                        edge.setTranslateX(edge.getTranslateX() + deltaX);
                        edge.setTranslateY(edge.getTranslateY() + deltaY);
                    }
                    ((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().setTranslateX(((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().getTranslateX() + deltaX);
                    ((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().setTranslateY(((CutShapeEdge) draggablePoint.getPointOwner()).getOwner().getTranslateY() + deltaY);
                }

                CutShape cutShape = ((CutShapeEdge) draggableShape).getOwner();
                if (cutShape.isContainInUnion()) {

                    for (CutShape cutShapeOther : cutShapeUnionOwner.getCutShapesInUnionList()) {

                        if (cutShapeOther.getShapeNumber() == cutShape.getShapeNumber()) continue;
                        cutShapeOther.setTranslateX(cutShapeOther.getTranslateX() + deltaX);
                        cutShapeOther.setTranslateY(cutShapeOther.getTranslateY() + deltaY);

                        if (cutShapeOther.isSaveMaterialImage()) {
                            for (CutShapeEdge edge : cutShapeOther.getCutShapeEdgesList()) {
                                edge.setTranslateX(edge.getTranslateX() + deltaX);
                                edge.setTranslateY(edge.getTranslateY() + deltaY);
                            }
                        }
                    }

                }

            }
        }
    }
}
