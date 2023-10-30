package ru.koreanika.Common.Material;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.Common.Connectible;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.project.Project;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaterialSheet extends Pane implements Connectible {

    private final Material material;

    int partsOfSheet = 1;
    int usesList = 0; //0- not uses, 1 part, 2 - parts;
    double materialScale = 0.1;

    double sheetWidth;//mm
    double sheetHeight;//mm

    double sheetMinWidth;//mm
    double sheetMinHeight;//mm
    int sheetDepth;


    double sheetCustomPriceForMeter = 0;
    String sheetCurrency = "RUB";

    double sheetSquare = 0;
    double minSheetSquare = 0;

    boolean additionalSheet = false;

    Label labelSize = new Label();

    /* FIXED prices and coefficients START */
    boolean actualPrice = true;

    private Map<Integer, Integer> tableTopDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private Map<Integer, Integer> wallPanelDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private Map<Integer, Integer> windowSillDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private Map<Integer, Integer> footDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>

    private ArrayList<Double> tableTopCoefficientList = new ArrayList<>(3); //<Coefficient>
    private ArrayList<Double> wallPanelCoefficientList = new ArrayList<>(3); //<Coefficient>
    private ArrayList<Double> windowSillCoefficientList = new ArrayList<>(3); //<Coefficient>
    private ArrayList<Double> footCoefficientList = new ArrayList<>(3); //<Coefficient>

    /* FIXED prices and coefficients END */
    Polygon polygon = new Polygon();
    Polygon hideHorizontalPolygon1 = new Polygon();
    Polygon hideHorizontalPolygon2 = new Polygon();

    Polygon hideVerticalPolygon1 = new Polygon();
    Polygon hideVerticalPolygon2 = new Polygon();
    Polygon hideVerticalPolygon3 = new Polygon();
    Polygon hideVerticalPolygon4 = new Polygon();

    //connect points:
    CornerConnectPoint leftUpConnectPoint = new CornerConnectPoint(this);
    CornerConnectPoint leftDownConnectPoint = new CornerConnectPoint(this);
    CornerConnectPoint rightUpConnectPoint = new CornerConnectPoint(this);
    CornerConnectPoint rightDownConnectPoint = new CornerConnectPoint(this);

    ArrayList<ConnectPoint> connectPointArrayList = new ArrayList<>();

    public MaterialSheet(Material material, int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, boolean additionalSheet) {
        this(material, depth, sheetWidth, sheetHeight, sheetMinWidth, sheetMinHeight, 0.0, material.getCurrency(), additionalSheet);
    }

    public MaterialSheet(Material material, int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, double sheetCustomPriceForMeter, String currency, boolean additionalSheet) {
        this.material = material;
        this.sheetDepth = depth;
        this.sheetWidth = sheetWidth;
        this.sheetHeight = sheetHeight;
        this.sheetMinWidth = sheetMinWidth;
        this.sheetMinHeight = sheetMinHeight;
        setPrefWidth(sheetWidth * materialScale);
        setPrefHeight(sheetHeight * materialScale);

        this.sheetCustomPriceForMeter = sheetCustomPriceForMeter;
        this.sheetCurrency = currency;
        this.additionalSheet = additionalSheet;

        sheetSquare = (this.sheetWidth * this.sheetHeight);
        minSheetSquare = (sheetMinWidth * sheetMinHeight);

        partsOfSheet = (int) (((sheetHeight / 1000) * (sheetWidth / 1000)) / ((sheetMinWidth / 1000) * (sheetMinHeight / 1000)));
        System.out.println("partsOfSheet = " + partsOfSheet);

        polygon.getPoints().addAll(
                0.0, 0.0,
                sheetWidth * materialScale, 0.0,
                sheetWidth * materialScale, sheetHeight * materialScale,
                0.0, sheetHeight * materialScale
        );
        polygon.setFill(Color.web("0xE1DFDD"));
        this.getChildren().add(polygon);

        labelSize.setPrefWidth(polygon.getBoundsInLocal().getWidth());
        labelSize.setPrefHeight(polygon.getBoundsInLocal().getHeight());
        labelSize.setAlignment(Pos.CENTER);
        labelSize.setText("" + ((int) sheetWidth) + " x " + ((int) sheetHeight));
        labelSize.setFont(Font.font(19.0));
        labelSize.setStyle("-fx-text-fill: white");
        this.getChildren().remove(labelSize);
        this.getChildren().add(labelSize);

        createHidingPolygons();
        initConnectionPoints();
    }

    public boolean isAdditionalSheet() {
        return additionalSheet;
    }

    public void setAdditionalSheet(boolean additionalSheet) {
        this.additionalSheet = additionalSheet;
    }

    public double getSheetSquare() {
        return sheetSquare;
    }

    public double getMinSheetSquare() {
        return minSheetSquare;
    }

    public double getSheetWidth() {
        return sheetWidth;
    }

    public void setSheetWidth(double sheetWidth) {
        this.sheetWidth = sheetWidth;
    }

    public double getSheetHeight() {
        return sheetHeight;
    }

    public void setSheetHeight(double sheetHeight) {
        this.sheetHeight = sheetHeight;
    }

    public int getSheetDepth() {
        return sheetDepth;
    }

    public void setSheetDepth(int sheetDepth) {
        this.sheetDepth = sheetDepth;
    }

    public boolean isActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(boolean actualPrice) {
        this.actualPrice = actualPrice;
    }

    public int getDepth() {
        return sheetDepth;
    }

    public double getPartsOfSheet() {
        return partsOfSheet;
    }

    public String getCuttingDirection() {
        if (material.isHorizontalCuttingParts()) {
            return "h";
        } else {
            return "v";
        }
    }

    public int getCountOfAvailableParts() {
        if (additionalSheet) {
            return 1;
        }
        if (getCuttingDirection().equals("h")) {
            return 2;
        } else if (getCuttingDirection().equals("v")) {
            return 4;
        }
        return 1;
    }

    public String getSheetCurrency() {
        return sheetCurrency;
    }

    public ArrayList<ConnectPoint> getConnectPointArrayList() {
        return connectPointArrayList;
    }

    public Material getMaterial() {
        return material;
    }

    private void createHidingPolygons() {
        Polygon templatePolygon = new Polygon(
                0.0, 0.0,
                sheetMinWidth * materialScale, 0.0,
                sheetMinWidth * materialScale, sheetMinHeight * materialScale,
                0.0, sheetMinHeight * materialScale
        );

        if (additionalSheet) {
            hideHorizontalPolygon1.getPoints().addAll(templatePolygon.getPoints());
            this.getChildren().addAll(hideHorizontalPolygon1);
            hideHorizontalPolygon1.setMouseTransparent(true);
            hideHorizontalPolygon1.setFill(new Color(0, 0, 0, 0.5));
            hideHorizontalPolygon1.setTranslateX(0.0);
        } else {
            if (material.isHorizontalCuttingParts()) {
                hideHorizontalPolygon1.getPoints().addAll(templatePolygon.getPoints());
                hideHorizontalPolygon2.getPoints().addAll(templatePolygon.getPoints());

                if (partsOfSheet == 1) {
                    this.getChildren().addAll(hideHorizontalPolygon1);
                } else {
                    this.getChildren().addAll(hideHorizontalPolygon1, hideHorizontalPolygon2);
                }

                hideHorizontalPolygon1.setMouseTransparent(true);
                hideHorizontalPolygon2.setMouseTransparent(true);

                hideHorizontalPolygon1.setFill(new Color(1, 1, 1, 0.4));
                hideHorizontalPolygon2.setFill(new Color(1, 1, 1, 0.4));

                hideHorizontalPolygon1.setTranslateX(0.0);
                hideHorizontalPolygon1.setTranslateY(0.0);
                hideHorizontalPolygon2.setTranslateX(0.0);
                hideHorizontalPolygon2.setTranslateY((sheetHeight / 2) * materialScale);
            } else {
                hideVerticalPolygon1.getPoints().addAll(templatePolygon.getPoints());
                hideVerticalPolygon2.getPoints().addAll(templatePolygon.getPoints());
                hideVerticalPolygon3.getPoints().addAll(templatePolygon.getPoints());
                hideVerticalPolygon4.getPoints().addAll(templatePolygon.getPoints());

                if (partsOfSheet == 1) {
                    this.getChildren().addAll(hideVerticalPolygon1);
                } else if (partsOfSheet == 2) {
                    this.getChildren().addAll(hideVerticalPolygon1, hideVerticalPolygon2);
                } else if (partsOfSheet == 4) {
                    this.getChildren().addAll(hideVerticalPolygon1, hideVerticalPolygon2);
                    this.getChildren().addAll(hideVerticalPolygon3, hideVerticalPolygon4);
                }

                hideVerticalPolygon1.setMouseTransparent(true);
                hideVerticalPolygon2.setMouseTransparent(true);
                hideVerticalPolygon3.setMouseTransparent(true);
                hideVerticalPolygon4.setMouseTransparent(true);

                hideVerticalPolygon1.setFill(new Color(1, 1, 1, 0.4));
                hideVerticalPolygon2.setFill(new Color(1, 1, 1, 0.4));
                hideVerticalPolygon3.setFill(new Color(1, 1, 1, 0.4));
                hideVerticalPolygon4.setFill(new Color(1, 1, 1, 0.4));

                hideVerticalPolygon1.setTranslateX(0.0);
                hideVerticalPolygon1.setTranslateY(0.0);
                hideVerticalPolygon2.setTranslateX((sheetMinWidth * 1) * materialScale);
                hideVerticalPolygon2.setTranslateY(0.0);
                hideVerticalPolygon3.setTranslateX((sheetMinWidth * 2) * materialScale);
                hideVerticalPolygon3.setTranslateY(0.0);
                hideVerticalPolygon4.setTranslateX((sheetMinWidth * 3) * materialScale);
                hideVerticalPolygon4.setTranslateY(0.0);
            }
        }
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public double getMaterialScale() {
        return materialScale;
    }

    public void initConnectionPoints() {
        getChildren().removeAll(leftUpConnectPoint, leftDownConnectPoint, rightUpConnectPoint, rightDownConnectPoint);
        leftUpConnectPoint.changeSetPoint(new Point2D(0, 0));
        leftUpConnectPoint.hide();
        leftDownConnectPoint.changeSetPoint(new Point2D(0, sheetHeight * materialScale));
        leftDownConnectPoint.hide();
        rightUpConnectPoint.changeSetPoint(new Point2D(sheetWidth * materialScale, 0));
        rightUpConnectPoint.hide();
        rightDownConnectPoint.changeSetPoint(new Point2D(sheetWidth * materialScale, sheetHeight * materialScale));
        rightDownConnectPoint.hide();
        getChildren().addAll(leftUpConnectPoint, leftDownConnectPoint, rightUpConnectPoint, rightDownConnectPoint);

        connectPointArrayList.clear();
        connectPointArrayList.add(leftUpConnectPoint);
        connectPointArrayList.add(leftDownConnectPoint);
        connectPointArrayList.add(rightUpConnectPoint);
        connectPointArrayList.add(rightDownConnectPoint);
    }

    public void hideHorizontalHalf(int part, boolean hide) {
        usesList = 0;
        if (part == 1) {
            hideHorizontalPolygon1.setVisible(!hide);
        } else {
            hideHorizontalPolygon2.setVisible(!hide);
        }
    }

    public double getSheetMinHeight() {
        return sheetMinHeight;
    }

    public double getSheetMinWidth() {
        return sheetMinWidth;
    }

    public double getSheetCustomPriceForMeter() {
        return sheetCustomPriceForMeter;
    }

    public void hideVerticalHalf(int part, boolean hide) {
        usesList = 0;
        if (part == 1) {
            hideVerticalPolygon1.setVisible(!hide);
        } else if (part == 2) {
            hideVerticalPolygon2.setVisible(!hide);
        } else if (part == 3) {
            hideVerticalPolygon3.setVisible(!hide);
        } else if (part == 4) {
            hideVerticalPolygon4.setVisible(!hide);
        }
    }

    public int getUsesList() {
        usesList = 0;

        if (additionalSheet) {
            return 1;
        }
        if (getCuttingDirection().equals("h")) {
            if (hideHorizontalPolygon1.isVisible()) usesList++;
            if (hideHorizontalPolygon2.isVisible()) usesList++;
        } else if (getCuttingDirection().equals("v")) {
            if (hideVerticalPolygon1.isVisible()) usesList++;
            if (hideVerticalPolygon2.isVisible()) usesList++;
            if (hideVerticalPolygon3.isVisible()) usesList++;
            if (hideVerticalPolygon4.isVisible()) usesList++;
        }
        return usesList;
    }

    public double getUsesSlabs() {
        double slabs = 0;
        if (additionalSheet) {

            if (hideHorizontalPolygon1.isVisible()) {
                slabs = 1;
            }

            return slabs;
        }

        if (getCuttingDirection().equals("h")) {
            if (hideHorizontalPolygon1.isVisible()) slabs += 0.5;
            if (hideHorizontalPolygon2.isVisible()) slabs += 0.5;
        } else if (getCuttingDirection().equals("v")) {
            if (hideVerticalPolygon1.isVisible()) slabs += 0.25;
            if (hideVerticalPolygon2.isVisible()) slabs += 0.25;
            if (hideVerticalPolygon3.isVisible()) slabs += 0.25;
            if (hideVerticalPolygon4.isVisible()) slabs += 0.25;
        }

        return slabs;
    }

    public int getRawUsesList() {
        return usesList;
    }

    public void setUsesList(int usesList) {
        this.usesList = usesList;
    }

    /* FIXED prices and coefficients START */

    public Map<Integer, Integer> getTableTopDepthsAndPrices() {
        return tableTopDepthsAndPrices;
    }

    public Map<Integer, Integer> getWallPanelDepthsAndPrices() {
        return wallPanelDepthsAndPrices;
    }

    public Map<Integer, Integer> getWindowSillDepthsAndPrices() {
        return windowSillDepthsAndPrices;
    }

    public Map<Integer, Integer> getFootDepthsAndPrices() {
        return footDepthsAndPrices;
    }

    public ArrayList<Double> getTableTopCoefficientList() {
        return tableTopCoefficientList;
    }

    public ArrayList<Double> getWallPanelCoefficientList() {
        return wallPanelCoefficientList;
    }

    public ArrayList<Double> getFootCoefficientList() {
        return footCoefficientList;
    }

    public ArrayList<Double> getWindowSillCoefficientList() {
        return windowSillCoefficientList;
    }

    public double getPrice(ElementTypes elementType, int depth) {
        double price = getPriceRaw(elementType, depth);

        double materialCoefficient = Project.getPriceMaterialCoefficient().doubleValue();
        double commonCoefficient = Project.getPriceMainCoefficient().doubleValue();

        price = price * materialCoefficient * commonCoefficient;
        return price;
    }

    public double getPriceRaw(ElementTypes elementType, int depth) {
        double price = 0;
        if (additionalSheet) {
            price = sheetCustomPriceForMeter;
        } else {

            //System.out.println(name);
            System.out.println("INTO SHEET tableTopDepthsAndPrices = " + tableTopDepthsAndPrices);

            if (elementType == ElementTypes.TABLETOP) {
                price = (tableTopDepthsAndPrices.get(depth).doubleValue()) / 100.0;
            } else if (elementType == ElementTypes.WALL_PANEL) {
                price = wallPanelDepthsAndPrices.get(depth).doubleValue() / 100.0;
            } else if (elementType == ElementTypes.WINDOWSILL) {
                price = windowSillDepthsAndPrices.get(depth).doubleValue() / 100.0;
            } else if (elementType == ElementTypes.FOOT) {
                price = footDepthsAndPrices.get(depth).doubleValue() / 100.0;
            } else {
                price = 0;
            }
        }

        return price;
    }

    /* FIXED prices and coefficients END */

    @Override
    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint) {
        CutShape draggableShape = (CutShape) draggablePoint.getPointOwner();
        MaterialSheet staticShape;

        staticShape = (MaterialSheet) staticPoint.getPointOwner();

        draggableShape.setTranslateX(((staticPoint.getTranslateX() + 5 + staticShape.getTranslateX()) - (draggablePoint.getTranslateX() + 5)));
        draggableShape.setTranslateY((staticPoint.getTranslateY() + 5 + staticShape.getTranslateY()) - (draggablePoint.getTranslateY() + 5));
    }

    @Override
    public void showConnectionPoints() {
        for (ConnectPoint p : connectPointArrayList) {
            p.show();
        }
    }

    @Override
    public void hideConnectionPoints() {
        for (ConnectPoint p : connectPointArrayList) {
            p.hide();
        }
    }

}
