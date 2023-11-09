package ru.koreanika.tableDesigner.item;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.sketchDesigner.Shapes.*;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.IOException;
import java.util.*;

public class StoneProductItem extends TableDesignerItem implements Cuttable, DependOnMaterial {

    private ArrayList<ArrayList<SketchShape>> sketchShapeArrayList = new ArrayList<>();

    private ArrayList<ArrayList<Double>> cutShapesAngles;
    private ArrayList<ArrayList<Point2D>> cutShapesCoordinates;

    ArrayList<TableDesignerItem> slaveItems = new ArrayList<>();

    // row controls:
    Label labelRowNumber, labelName, labelMaterial, labelDepth, labelWidth, labelHeight, labelQuantity, labelRowPrice;
    ImageView imageView;
    ImageView imageViewShapeType;
    Button btnPlus, btnMinus, btnDelete, btnEdit;


    ImageView imageViewCardLogo;

    StoneProductType stoneProductType;
    ShapeType shapeType;
    Material material;

    @Getter
    int depth;

    @Getter
    double sizeA;

    @Getter
    double sizeB;

    @Getter
    double sizeC;

    @Getter
    double sizeD;

    double workCoefficient;
    int workCoefficientIndex;

    String name;

    public StoneProductItem(ArrayList<ArrayList<Point2D>> cutShapesCoordinates,
                            ArrayList<ArrayList<Double>> cutShapesAngles, int quantity,
                            StoneProductType stoneProductType, ShapeType shapeType, Material material,
                            int depth, double sizeA, double sizeB, double sizeC, double sizeD,
                            double workCoefficient, int workCoefficientIndex, String name) {

        this.cutShapesAngles = cutShapesAngles;
        this.cutShapesCoordinates = cutShapesCoordinates;
        this.quantity = quantity;
        this.stoneProductType = stoneProductType;
        this.shapeType = shapeType;
        this.material = material;
        this.depth = depth;
        this.sizeA = sizeA;
        this.sizeB = sizeB;
        this.sizeC = sizeC;
        this.sizeD = sizeD;
        this.workCoefficient = workCoefficient;
        this.workCoefficientIndex = workCoefficientIndex;
        this.name = name;

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/TableItems/StoneProductRow.fxml"));
        try {
            anchorPaneTableRow = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        FXMLLoader fxmlLoaderCard = new FXMLLoader(this.getClass().getResource("/fxmls/TableDesigner/TableItems/ItemCardViewTemplateStoneProduct.fxml"));
        try {
            anchorPaneCardView = fxmlLoaderCard.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rowControlElementsInit();
        rowControlElementLogicInit();

        cardControlElementsInit();
        cardControlElementLogicInit();

        updateItemView();

        Project.getMaterialsInUse().add(material.getName() + "#" + depth);

        //create depend on Radius element:
        if (shapeType == ShapeType.RECTANGLE_WITH_RADIUS || shapeType == ShapeType.RECTANGLE_WITH_RADIUS_INTO) {
            RadiusItem radiusItem = RadiusItem.getSlaveInstance(material, quantity);
            slaveItems.add(radiusItem);
            TableDesignerSession.getTableDesignerMainWorkItemsList().add(radiusItem);
        }else if(shapeType == ShapeType.CIRCLE_HALF){
            RadiusItem radiusItem = RadiusItem.getSlaveInstance(material, 2*quantity);
            slaveItems.add(radiusItem);
            TableDesignerSession.getTableDesignerMainWorkItemsList().add(radiusItem);
        }else if(shapeType == ShapeType.CIRCLE){
            RadiusItem radiusItem = RadiusItem.getSlaveInstance(material, 4*quantity);
            slaveItems.add(radiusItem);
            TableDesignerSession.getTableDesignerMainWorkItemsList().add(radiusItem);
        }

        //create shape and add it to sketchDesignerList
        for (int i = 0; i < quantity; i++) {
            ElementTypes elementType = ElementTypes.valueOf(stoneProductType.name());

            //check sizes:
            double materialLength = material.getMaterialWidth();//mm
            double materialHeight = material.getMaterialHeight();//mm
            double shapeLen = sizeA;//mm
            double shapeHeight = sizeB;//mm

            if (shapeType == ShapeType.RECTANGLE || shapeType == ShapeType.RECTANGLE_WITH_CORNER ||
                    shapeType == ShapeType.RECTANGLE_WITH_RADIUS_INTO || shapeType == ShapeType.RECTANGLE_WITH_RADIUS ||
                    shapeType == ShapeType.TRIANGLE || shapeType == ShapeType.TRAPEZE || shapeType == ShapeType.RHOMBUS) {
                shapeLen = sizeA;
                shapeHeight = sizeB;
            } else if (shapeType == ShapeType.CIRCLE_HALF) {
                shapeLen = sizeC * 2;
                shapeHeight = sizeC;
            } else if (shapeType == ShapeType.CIRCLE) {
                shapeLen = sizeC * 2;
                shapeHeight = sizeC * 2;
            }

            boolean oversize = false;
            if (((shapeLen > materialLength && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeHeight > materialHeight)) ||
                    (shapeHeight > materialHeight && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeLen > materialLength)) {
                InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Размер не соответствует материалу!", null);
                oversize = true;
            }

            double maxPieceXSize = materialLength;
            double maxPieceYSize = materialHeight;

            if (shapeLen > shapeHeight) {
                maxPieceXSize = materialLength;
                maxPieceYSize = materialHeight;
            } else {
                maxPieceXSize = materialHeight;
                maxPieceYSize = materialLength;
            }

            int xPieces = (int) Math.floor(shapeLen / maxPieceXSize);
            if (shapeLen % maxPieceXSize != 0) xPieces += 1;

            int yPieces = (int) Math.floor(shapeHeight / maxPieceYSize);
            if (shapeHeight % maxPieceYSize != 0) yPieces += 1;

            ArrayList<SketchShape> itemShapes = new ArrayList<>(xPieces * yPieces);

            //for materials from temlate or for fragmebts only:
            if(!material.isUseMainSheets()){
                oversize = false;
            }

            if (oversize) {
                int position = 0;
                for (int x = 0; x < xPieces; x++) {
                    for (int y = 0; y < yPieces; y++) {

                        double xSize = maxPieceXSize;
                        double ySize = maxPieceYSize;
                        //last x:
                        if (x == xPieces - 1) {
                            xSize = shapeLen - (xPieces - 1) * xSize;
                        }
                        //last y:
                        if (y == yPieces - 1) {
                            ySize = shapeHeight - (yPieces - 1) * ySize;
                        }

                        SketchShape shape = new SketchShapeRectangle(elementType, material, depth, xSize, ySize);
                        shape.setProductName(name);
                        shape.setWorkCoefficient(workCoefficient);
                        SketchDesigner.getSketchShapesList().add(shape);
                        //add shape to cutPane
                        CutShape cutShape = shape.getCutShape();
                        CutDesigner.getInstance().getCutShapesList().add(cutShape);
                        CutDesigner.getInstance().usedShapesNumberList.add(cutShape.getShapeNumber());
                        CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);

                        itemShapes.add(shape);
                        cutShape.setTranslateX(cutShapesCoordinates.get(i).get(position).getX());
                        cutShape.setTranslateY(cutShapesCoordinates.get(i).get(position).getY());

                        cutShape.rotateShapeLocal(cutShapesAngles.get(i).get(position).doubleValue());

                        position++;
                    }
                }

            } else {
                SketchShape shape = switch (shapeType) {
                    case RECTANGLE -> new SketchShapeRectangle(elementType, material, depth, sizeA, sizeB);
                    case RECTANGLE_WITH_CORNER -> new SketchShapeRectangleCutCorner(elementType, material, depth, sizeA, sizeB, sizeC, sizeD);
                    case RECTANGLE_WITH_RADIUS -> new SketchShapeRectangleCircleCorner(elementType, material, depth, sizeA, sizeB, sizeC);
                    case RECTANGLE_WITH_RADIUS_INTO -> new SketchShapeRectangleCircleCornerInto(elementType, material, depth, sizeA, sizeB, sizeC);
                    case TRIANGLE -> new SketchShapeTriangle(elementType, material, depth, sizeA, sizeB);
                    case CIRCLE_HALF -> new SketchShapeCircleHalf(elementType, material, depth, sizeC);
                    case CIRCLE -> new SketchShapeCircle(elementType, material, depth, sizeC);
                    case TRAPEZE -> new SketchShapeTrapeze(elementType, material, depth, sizeA, sizeB, sizeC, sizeD);
                    case RHOMBUS -> new SketchShapeRhombus(elementType, material, depth, sizeA, sizeB, sizeC, sizeD);
                };

                shape.setWorkCoefficient(workCoefficient);
                shape.setProductName(name);

                SketchDesigner.getSketchShapesList().add(shape);

                //add shape to cutPane
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().add(cutShape);
                CutDesigner.getInstance().usedShapesNumberList.add(cutShape.getShapeNumber());
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);

                itemShapes.add(shape);

                cutShape.setTranslateX(cutShapesCoordinates.get(i).get(0).getX());
                cutShape.setTranslateY(cutShapesCoordinates.get(i).get(0).getY());

                cutShape.rotateShapeLocal(cutShapesAngles.get(i).get(0));
            }
            sketchShapeArrayList.add(itemShapes);
        }

    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    public ArrayList<ArrayList<SketchShape>> getSketchShapeArrayList() {
        return sketchShapeArrayList;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    private static void updateMaterial(StoneProductItem item) {
        StoneProductItem oldStoneProductItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldStoneProductItem.material;
        } else {
            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType()) && defaultMaterial.getDepths().contains("" + oldStoneProductItem.depth)) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterials()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType()) && material.getDepths().contains("" + oldStoneProductItem.depth)) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldStoneProductItem.removeThisItem();
                    return;
                }
            }
        }

        if (newMaterial.getDepths().contains("" + oldStoneProductItem.depth)) {
            ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<ArrayList<Point2D>>();
            ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<ArrayList<Double>>();

            if(newMaterial.getMaterialWidth() != oldStoneProductItem.material.getMaterialWidth() ||
                    newMaterial.getMaterialHeight() != oldStoneProductItem.material.getMaterialHeight()){
                double materialLength = newMaterial.getMaterialWidth();//mm
                double materialHeight = newMaterial.getMaterialHeight();//mm
                double shapeLen = oldStoneProductItem.sizeA;//mm
                double shapeHeight = oldStoneProductItem.sizeB;//mm

                if (oldStoneProductItem.shapeType == ShapeType.RECTANGLE || oldStoneProductItem.shapeType == ShapeType.RECTANGLE_WITH_CORNER ||
                        oldStoneProductItem.shapeType == ShapeType.RECTANGLE_WITH_RADIUS_INTO || oldStoneProductItem.shapeType == ShapeType.RECTANGLE_WITH_RADIUS ||
                        oldStoneProductItem.shapeType == ShapeType.TRIANGLE || oldStoneProductItem.shapeType == ShapeType.TRAPEZE || oldStoneProductItem.shapeType == ShapeType.RHOMBUS) {
                    shapeLen = oldStoneProductItem.sizeA;
                    shapeHeight = oldStoneProductItem.sizeB;
                } else if (oldStoneProductItem.shapeType == ShapeType.CIRCLE_HALF) {
                    shapeLen = oldStoneProductItem.sizeC * 2;
                    shapeHeight = oldStoneProductItem.sizeC;
                } else if (oldStoneProductItem.shapeType == ShapeType.CIRCLE) {
                    shapeLen = oldStoneProductItem.sizeC * 2;
                    shapeHeight = oldStoneProductItem.sizeC * 2;
                }

                boolean oversize = false;
                if (((shapeLen > materialLength && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeHeight > materialHeight)) ||
                        (shapeHeight > materialHeight && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeLen > materialLength)) {
                    InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Размер не соответствует материалу!", null);
                    oversize = true;
                }

                double maxPieceXSize = materialHeight;
                double maxPieceYSize = materialHeight;

                int xPieces = (int) Math.floor(shapeLen / maxPieceXSize);
                if (shapeLen % maxPieceXSize != 0) xPieces += 1;

                int yPieces = (int) Math.floor(shapeHeight / maxPieceYSize);
                if (shapeHeight % maxPieceYSize != 0) yPieces += 1;

                System.out.println("xPieces = " + xPieces);
                System.out.println("yPieces = " + yPieces);

                for(int j=0;j< oldStoneProductItem.quantity;j++){
                    ArrayList<Point2D> coordList = new ArrayList<>();
                    ArrayList<Double> angleList = new ArrayList<>();

                    //for (int i = 0; i < oldStoneProductItem.cutShapesCoordinates.size(); i++) {
                    for (int i = 0; i < xPieces; i++) {
                        coordList.add(new Point2D(0, 0));
                        angleList.add(Double.valueOf(0));
                    }
                    cutShapesCoordinates.add(coordList);
                    cutShapesAngles.add(angleList);
                }
            }else{
                for (ArrayList<Point2D> arr1 : oldStoneProductItem.cutShapesCoordinates) {
                    ArrayList<Point2D> arrayPoints = new ArrayList<>();
                    for (Point2D p : arr1) {
                        arrayPoints.add(new Point2D(p.getX(), p.getY()));
                    }
                    cutShapesCoordinates.add(arrayPoints);
                }

                for (ArrayList<Double> arr1 : oldStoneProductItem.cutShapesAngles) {

                    ArrayList<Double> arrayAngles = new ArrayList<>();
                    for (Double d : arr1) {
                        arrayAngles.add(d.doubleValue());
                    }
                    cutShapesAngles.add(arrayAngles);
                }
            }

            System.out.println("cutShapesCoordinates = " + cutShapesCoordinates);
            System.out.println("cutShapesAngles = " + cutShapesAngles);

            StoneProductItem newStoneProductItem = new StoneProductItem(cutShapesCoordinates, cutShapesAngles,
                    oldStoneProductItem.quantity, oldStoneProductItem.stoneProductType,
                    oldStoneProductItem.shapeType, newMaterial, oldStoneProductItem.depth, oldStoneProductItem.sizeA,
                    oldStoneProductItem.sizeB, oldStoneProductItem.sizeC, oldStoneProductItem.sizeD,
                    oldStoneProductItem.workCoefficient,oldStoneProductItem.workCoefficientIndex, oldStoneProductItem.name);

            oldStoneProductItem.removeThisItem();
            TableDesignerSession.getTableDesignerMainItemsList().add(newStoneProductItem);
        } else {
            oldStoneProductItem.removeThisItem();
        }
    }

    @Override
    public void updateWorkCoefficient() {
        if(stoneProductType == StoneProductType.TABLETOP){
            workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
        }else if(stoneProductType == StoneProductType.WALL_PANEL){
            workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
        }else if(stoneProductType == StoneProductType.WINDOWSILL){
            workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
        }else if(stoneProductType == StoneProductType.FOOT){
            workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
        }

        for(ArrayList<SketchShape> sketchShapes : sketchShapeArrayList){
            for(SketchShape sketchShape : sketchShapes){
                sketchShape.setWorkCoefficient(workCoefficient);
            }
        }
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = material.getImgPath();
        imagesList.put(material.getColor() + " лого#" + imgPath, new ImageView(material.getImageViewLogo().getImage()));
        imagesList.put(material.getColor() + " текстура#" + imgPath + material.getColor() + "2", new ImageView(material.getTextureImage()));

        ImageView shapeImage = new ImageView();
        String imgPath1 = switch (shapeType) {
            case RECTANGLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_2.png";
            case RECTANGLE_WITH_CORNER -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_corner_2.png";
            case RECTANGLE_WITH_RADIUS -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_circle_2.png";
            case RECTANGLE_WITH_RADIUS_INTO -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_circle_into_2.png";
            case TRIANGLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/triangle_2.png";
            case CIRCLE_HALF -> "/styles/images/TableDesigner/StoneProductItem/shapes/circle_half_2.png";
            case CIRCLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/circle_2.png";
            case TRAPEZE -> "/styles/images/TableDesigner/StoneProductItem/shapes/trapeze_2.png";
            case RHOMBUS -> "/styles/images/TableDesigner/StoneProductItem/shapes/rhombus_2.png";
        };
        shapeImage.setImage(new Image(StoneProductItem.class.getResource(imgPath1).toString()));
        imagesList.put(stoneProductType.getName() + "#" + imgPath1, shapeImage);

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerMainItemsList().remove(this);

        for (ArrayList<SketchShape> shapeList : sketchShapeArrayList) {
            for (SketchShape shape : shapeList) {
                CutDesigner.getInstance().getCutPane().deleteCutShape(shape.getShapeNumber());
                SketchDesigner.getSketchShapesList().remove(shape);
            }
        }

        deleteSlaves();

        sketchShapeArrayList.clear();
        cutShapesCoordinates.clear();
        cutShapesAngles.clear();
    }

    public ImageView getImageViewShapeType() {
        return new ImageView(imageViewShapeType.getImage());
    }

    public void exitEditMode(){
        if(this.editModeProperty.get()){
            StoneProductItem.exitFromEditMode(this);
        }
    }

    /**
     * Table ROW part
     */

    @Override
    public AnchorPane getTableView() {
        return anchorPaneTableRow;
    }

    @Override
    public void setRowNumber(int number) {
        labelRowNumber.setText("" + number);
    }

    private void rowControlElementsInit() {
        HBox hBox = (HBox) anchorPaneTableRow.lookup("#hBox");
        labelRowNumber = (Label) hBox.getChildren().get(0);
        labelName = (Label) hBox.getChildren().get(1);
        AnchorPane anchorPaneImageView = (AnchorPane) hBox.getChildren().get(2);
        imageView = (ImageView) anchorPaneImageView.lookup("#imageView");
        imageViewShapeType = (ImageView) anchorPaneImageView.lookup("#imageViewShapeType");
        labelMaterial = (Label) hBox.getChildren().get(3);
        labelDepth = (Label) hBox.getChildren().get(4);
        labelWidth = (Label) hBox.getChildren().get(5);
        labelHeight = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");

        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelMaterial, Priority.ALWAYS);
        HBox.setHgrow(labelDepth, Priority.ALWAYS);
        HBox.setHgrow(labelWidth, Priority.ALWAYS);
        HBox.setHgrow(labelHeight, Priority.ALWAYS);
        HBox.setHgrow(labelQuantity, Priority.ALWAYS);
        HBox.setHgrow(labelRowPrice, Priority.ALWAYS);
    }

    private void rowControlElementLogicInit() {
        btnPlus.setOnAction(this::btnPlusClicked);
        btnMinus.setOnAction(this::btnMinusClicked);
        btnDelete.setOnAction(this::btnDeleteClicked);
        btnEdit.setOnAction(this::btnEditClicked);
    }

    @Override
    protected void cardControlElementsInit() {
        labelHeaderCard = (Label) anchorPaneCardView.lookup("#labelHeader");
        tooltipNameCard = labelHeaderCard.getTooltip();

        labelPriceForOneCard = (Label) anchorPaneCardView.lookup("#labelPriceForOne");
        labelQuantityCard = (Label) anchorPaneCardView.lookup("#labelQuantity");

        labelName1Card = (Label) anchorPaneCardView.lookup("#labelName1");
        labelName2Card = (Label) anchorPaneCardView.lookup("#labelName2");
        labelName3Card = (Label) anchorPaneCardView.lookup("#labelName3");
        labelName4Card = (Label) anchorPaneCardView.lookup("#labelName4");

        labelValue1Card = (Label) anchorPaneCardView.lookup("#labelValue1");
        labelValue2Card = (Label) anchorPaneCardView.lookup("#labelValue2");
        labelValue3Card = (Label) anchorPaneCardView.lookup("#labelValue3");
        labelValue4Card = (Label) anchorPaneCardView.lookup("#labelValue4");

        labelPriceForAllCard = (Label) anchorPaneCardView.lookup("#labelPriceForAll");

        btnMinusCard = (Button) anchorPaneCardView.lookup("#btnMinus");
        btnPlusCard = (Button) anchorPaneCardView.lookup("#btnPlus");

        btnDeleteCard = (Button) anchorPaneCardView.lookup("#btnDelete");
        btnEditCard = (Button) anchorPaneCardView.lookup("#btnEdit");

        imageViewBackCard = (ImageView) anchorPaneCardView.lookup("#imageViewBack");
        imageViewFrontCard = (ImageView) anchorPaneCardView.lookup("#imageViewFront");
        imageViewCardLogo = (ImageView) anchorPaneCardView.lookup("#imageViewCardLogo");
    }

    private void cardControlElementLogicInit() {
        btnPlusCard.setOnAction(this::btnPlusClicked);
        btnMinusCard.setOnAction(this::btnMinusClicked);
        btnDeleteCard.setOnAction(this::btnDeleteClicked);
        btnEditCard.setOnAction(this::btnEditClicked);
    }

    private void btnPlusClicked(ActionEvent event){
        quantity++;
        if(shapeType == ShapeType.CIRCLE_HALF){
            changeSlaveQuantity(quantity * 2);
        }else if(shapeType == ShapeType.CIRCLE){
            changeSlaveQuantity(quantity * 4);
        }else{
            changeSlaveQuantity(quantity);
        }

        //create new shape
        ArrayList<SketchShape> itemShapes = new ArrayList<>(5);
        ArrayList<Point2D> coordList = new ArrayList<>();
        ArrayList<Double> angleList = new ArrayList<>();

        ElementTypes elementType = ElementTypes.valueOf(stoneProductType.name());

        //check sizes:
        double materialLength = material.getMaterialWidth();//mm
        double materialHeight = material.getMaterialHeight();//mm
        double shapeLen = sizeA;//mm
        double shapeHeight = sizeB;//mm
        boolean oversize = false;
        if (((shapeLen > materialLength && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeHeight > materialHeight)) ||
                (shapeHeight > materialHeight && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeLen > materialLength)) {
            InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Размер не соответствует материалу!", null);
            oversize = true;
        }

        double maxPieceXSize = materialHeight;
        double maxPieceYSize = materialHeight;

        if (shapeLen > shapeHeight) {
            maxPieceXSize = materialLength;
            maxPieceYSize = materialHeight;
        } else {
            maxPieceXSize = materialHeight;
            maxPieceYSize = materialLength;
        }

        int xPieces = (int) Math.floor(shapeLen / maxPieceXSize);
        if (shapeLen % maxPieceXSize != 0) xPieces += 1;

        int yPieces = (int) Math.floor(shapeHeight / maxPieceYSize);
        if (shapeHeight % maxPieceYSize != 0) yPieces += 1;

        //for materials from temlate or for fragmebts only:
        if(!material.isUseMainSheets()){
            oversize = false;
        }

        if (oversize) {
            int position = 0;
            for (int x = 0; x < xPieces; x++) {
                for (int y = 0; y < yPieces; y++) {

                    double xSize = maxPieceXSize;
                    double ySize = maxPieceYSize;
                    //last x:
                    if (x == xPieces - 1) {
                        xSize = shapeLen - (xPieces - 1) * xSize;
                    }
                    //last y:
                    if (y == yPieces - 1) {
                        ySize = shapeHeight - (yPieces - 1) * ySize;
                    }
                    SketchShape shape = new SketchShapeRectangle(elementType, material, depth, xSize, ySize);
                    shape.setProductName(name);
                    shape.setWorkCoefficient(workCoefficient);
                    SketchDesigner.getSketchShapesList().add(shape);
                    //add shape to cutPane
                    CutShape cutShape = shape.getCutShape();
                    CutDesigner.getInstance().getCutShapesList().add(cutShape);
                    CutDesigner.getInstance().usedShapesNumberList.add(cutShape.getShapeNumber());
                    CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);

                    itemShapes.add(shape);
                    coordList.add(new Point2D(0, 0));
                    cutShape.setTranslateX(0);
                    cutShape.setTranslateY(0);
                    angleList.add(Double.valueOf(0));
                    cutShape.rotateShapeLocal(0);

                    position++;
                }
            }
        } else {
            SketchShape shape = switch (shapeType) {
                case RECTANGLE -> new SketchShapeRectangle(elementType, material, depth, sizeA, sizeB);
                case RECTANGLE_WITH_CORNER -> new SketchShapeRectangleCutCorner(elementType, material, depth, sizeA, sizeB, sizeC, sizeD);
                case RECTANGLE_WITH_RADIUS -> new SketchShapeRectangleCircleCorner(elementType, material, depth, sizeA, sizeB, sizeC);
                case RECTANGLE_WITH_RADIUS_INTO -> new SketchShapeRectangleCircleCornerInto(elementType, material, depth, sizeA, sizeB, sizeC);
                case TRIANGLE -> new SketchShapeTriangle(elementType, material, depth, sizeA, sizeB);
                case CIRCLE_HALF -> new SketchShapeCircleHalf(elementType, material, depth, sizeC);
                case CIRCLE -> new SketchShapeCircle(elementType, material, depth, sizeC);
                case TRAPEZE -> new SketchShapeTrapeze(elementType, material, depth, sizeA, sizeB, sizeC, sizeD);
                case RHOMBUS -> new SketchShapeRhombus(elementType, material, depth, sizeA, sizeB, sizeC, sizeD);
            };

            shape.setWorkCoefficient(workCoefficient);
            shape.setProductName(name);
            SketchDesigner.getSketchShapesList().add(shape);
            //add shape to cutPane
            CutShape cutShape = shape.getCutShape();
            CutDesigner.getInstance().getCutShapesList().add(cutShape);
            CutDesigner.getInstance().usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
            CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().add(cutShape);

            itemShapes.add(shape);
            coordList.add(new Point2D(0, 0));
            cutShape.setTranslateX(0);
            cutShape.setTranslateY(0);
            angleList.add(Double.valueOf(0));
            cutShape.rotateShapeLocal(0);
        }

        sketchShapeArrayList.add(itemShapes);
        cutShapesCoordinates.add(coordList);
        cutShapesAngles.add(angleList);

        updateItemView();
    }

    private void btnMinusClicked(ActionEvent event){
        if (quantity == 1) return;
        quantity--;

        if(shapeType == ShapeType.CIRCLE_HALF){
            changeSlaveQuantity(quantity * 2);
        }else if(shapeType == ShapeType.CIRCLE){
            changeSlaveQuantity(quantity * 4);
        }else{
            changeSlaveQuantity(quantity);
        }

        //delete one shape:
        ArrayList<SketchShape> shapesItemList = sketchShapeArrayList.remove(sketchShapeArrayList.size() - 1);
        for (SketchShape shape : shapesItemList) {

            shape.deleteShape();
            SketchDesigner.getSketchShapesList().remove(shape);
            CutShape cutShape = shape.getCutShape();
            CutDesigner.getInstance().getCutShapesList().remove(cutShape);
            CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().remove(cutShape);
        }
        cutShapesCoordinates.remove(cutShapesCoordinates.size() - 1);
        cutShapesAngles.remove(cutShapesAngles.size() - 1);

        updateItemView();
    }

    private void btnDeleteClicked(ActionEvent event){
        if(editModeProperty.get()) exitFromEditMode(this);

        TableDesignerSession.getTableDesignerMainItemsList().remove(this);

        deleteSlaves();

        for (ArrayList<SketchShape> shapesItemList : sketchShapeArrayList) {
            for (SketchShape shape : shapesItemList) {
                shape.deleteShape();

                SketchDesigner.getSketchShapesList().remove(shape);
                CutShape cutShape = shape.getCutShape();
                CutDesigner.getInstance().getCutShapesList().remove(cutShape);
                CutDesigner.getInstance().getCutPane().cutObjectsGroup.getChildren().remove(cutShape);
            }
        }
        sketchShapeArrayList.clear();
        cutShapesCoordinates.clear();
        cutShapesAngles.clear();
    }

    private void btnEditClicked(ActionEvent event){
        //setting change mode to edit
        for(TableDesignerItem item : TableDesigner.getTableDesignerAllItemsList()){
            item.setEditModeProperty(false);
        }
        editModeProperty.setValue(true);
        enterToEditMode(this);
    }

    public void updateItemView(){
        labelRowNumber.setText("");

        labelName.setText(name);
        labelName.setWrapText(true);

        imageView.setImage(material.getImageViewLogo().getImage());

        String shapeTypeImagePath = switch (shapeType) {
            case RECTANGLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_1.png";
            case RECTANGLE_WITH_CORNER -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_corner_1.png";
            case RECTANGLE_WITH_RADIUS -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_circle_1.png";
            case RECTANGLE_WITH_RADIUS_INTO -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_circle_into_1.png";
            case TRIANGLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/triangle_1.png";
            case CIRCLE_HALF -> "/styles/images/TableDesigner/StoneProductItem/shapes/circle_half_1.png";
            case CIRCLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/circle_1.png";
            case TRAPEZE -> "/styles/images/TableDesigner/StoneProductItem/shapes/trapeze_1.png";
            case RHOMBUS -> "/styles/images/TableDesigner/StoneProductItem/shapes/rhombus_1.png";
        };
        imageViewShapeType.setImage(new Image(Project.class.getResource(shapeTypeImagePath).toString()));

        labelMaterial.setText(material.getReceiptName());
        labelDepth.setText("" + depth);
        labelWidth.setText("" + sizeA);
        labelHeight.setText("" + sizeB);
        labelQuantity.setText("" + quantity);

        labelHeaderCard.setText(name);
        tooltipNameCard.setText(name);

        imageViewBackCard.setImage(material.getTextureImage());

        imageViewCardLogo.setImage(material.getImageViewLogo().getImage());

        String frontCardImagePath = switch (shapeType) {
            case RECTANGLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_1.png";
            case RECTANGLE_WITH_CORNER -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_corner_1.png";
            case RECTANGLE_WITH_RADIUS -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_circle_1.png";
            case RECTANGLE_WITH_RADIUS_INTO -> "/styles/images/TableDesigner/StoneProductItem/shapes/rectangle_circle_into_1.png";
            case TRIANGLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/triangle_1.png";
            case TRAPEZE -> "/styles/images/TableDesigner/StoneProductItem/shapes/trapeze_1.png";
            case RHOMBUS -> "/styles/images/TableDesigner/StoneProductItem/shapes/rhombus_1.png";
            case CIRCLE -> "/styles/images/TableDesigner/StoneProductItem/shapes/circle_1.png";
            case CIRCLE_HALF -> "/styles/images/TableDesigner/StoneProductItem/shapes/circle_half_1.png";
        };
        imageViewFrontCard.setImage(new Image(Project.class.getResource(frontCardImagePath).toString()));

        labelQuantityCard.setText("" + quantity);


        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Толщина материала");
        labelValue2Card.setText(depth + " мм");

        if(shapeType == ShapeType.CIRCLE || shapeType == ShapeType.CIRCLE_HALF){
            labelName3Card.setText("Радиус");
            labelValue3Card.setText((int)sizeC + " мм");

            labelName4Card.setText("Высота");
            labelValue4Card.setText("-");
        }else{
            labelName3Card.setText("Ширина");
            labelValue3Card.setText((int)sizeA + " мм");

            labelName4Card.setText("Высота");
            labelValue4Card.setText(" " + (int)sizeB + " мм");
        }

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {
        labelRowPrice.setText("-");
        labelPriceForOneCard.setText("-");
        labelPriceForAllCard.setText("-");
    }

    /**
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK");
    private static Button btnCancel = new Button("Отмена");

    private static ChoiceBox<String> choiceBoxElementType, choiceBoxMaterial, choiceBoxDepth;
    private static ToggleButton toggleButtonShape1, toggleButtonShape2, toggleButtonShape3, toggleButtonShape4;
    private static ToggleButton toggleButtonShape5, toggleButtonShape6, toggleButtonShape7, toggleButtonShape8;
    private static ToggleButton toggleButtonShape9;
    private static ToggleGroup toggleGroupShapes = new ToggleGroup();
    private static TextField textFieldSizeA, textFieldSizeB, textFieldSizeC, textFieldSizeD;

    private static ImageView imageViewShape;
    private static Label labelC_R, labelD;
    private static Label labelPrice;

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StoneProductItem.class.getResource("/fxmls/TableDesigner/TableItems/StoneProductSettings.fxml"));

            try {
                anchorPaneSettingsView = fxmlLoader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            settingsControlElementsInit();
            settingsControlElementsLogicInit();
        }
        settingsControlElementsRefresh();

        return anchorPaneSettingsView;
    }

    private static void settingsControlElementsInit() {
        btnApply.getStyleClass().add("btnBrown");
        btnCancel.getStyleClass().add("btnBrown");

        choiceBoxElementType = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxElementType");
        choiceBoxMaterial = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxMaterial");
        choiceBoxDepth = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxDepth");

        toggleButtonShape1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape1");
        toggleButtonShape2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape2");
        toggleButtonShape3 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape3");
        toggleButtonShape4 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape4");
        toggleButtonShape5 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape5");
        toggleButtonShape6 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape6");
        toggleButtonShape7 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape7");
        toggleButtonShape8 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape8");
        toggleButtonShape9 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonShape9");

        textFieldSizeA = (TextField) anchorPaneSettingsView.lookup("#textFieldSizeA");
        textFieldSizeB = (TextField) anchorPaneSettingsView.lookup("#textFieldSizeB");
        textFieldSizeC = (TextField) anchorPaneSettingsView.lookup("#textFieldSizeC");
        textFieldSizeD = (TextField) anchorPaneSettingsView.lookup("#textFieldSizeD");

        imageViewShape = (ImageView) anchorPaneSettingsView.lookup("#imageViewShape");
        labelC_R = (Label) anchorPaneSettingsView.lookup("#labelC_R");
        labelD = (Label) anchorPaneSettingsView.lookup("#labelD");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");

        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");

        choiceBoxElementType.getItems().addAll(StoneProductType.TABLETOP.getName(),
                StoneProductType.WALL_PANEL.getName(),
                StoneProductType.WINDOWSILL.getName(),
                StoneProductType.FOOT.getName());
        choiceBoxElementType.getSelectionModel().select(0);

        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        System.out.println("\n\nDEFAULT FROM : " + Project.getDefaultMaterial());
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().addAll(Project.getDefaultMaterial().getDepths());
        choiceBoxDepth.getSelectionModel().select(0);

        toggleButtonShape1.setToggleGroup(toggleGroupShapes);
        toggleButtonShape2.setToggleGroup(toggleGroupShapes);
        toggleButtonShape3.setToggleGroup(toggleGroupShapes);
        toggleButtonShape4.setToggleGroup(toggleGroupShapes);
        toggleButtonShape5.setToggleGroup(toggleGroupShapes);
        toggleButtonShape6.setToggleGroup(toggleGroupShapes);
        toggleButtonShape7.setToggleGroup(toggleGroupShapes);
        toggleButtonShape8.setToggleGroup(toggleGroupShapes);
        toggleButtonShape9.setToggleGroup(toggleGroupShapes);

        toggleButtonShape1.setSelected(true);
        labelC_R.setText("C:");
        imageViewShape.setImage(new Image(StoneProductItem.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));

        textFieldSizeA.setText("600");
        textFieldSizeB.setText("600");
        textFieldSizeC.setDisable(true);
        textFieldSizeD.setDisable(true);
    }

    private static void settingsControlElementsLogicInit() {
        btnAdd.setOnAction(event -> addItem(TableDesignerSession.getTableDesignerMainItemsList().size(), 1));

        choiceBoxElementType.setOnAction(event -> {
            if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 0) {
                toggleButtonShape1.setDisable(false);
                toggleButtonShape2.setDisable(false);
                toggleButtonShape3.setDisable(false);
                toggleButtonShape4.setDisable(false);
                toggleButtonShape5.setDisable(false);
                toggleButtonShape6.setDisable(false);
                toggleButtonShape7.setDisable(false);
                toggleButtonShape8.setDisable(false);
                toggleButtonShape9.setDisable(false);

                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));

            } else if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 1) {
                toggleGroupShapes.selectToggle(toggleButtonShape1);
                toggleButtonShape1.setDisable(false);
                toggleButtonShape2.setDisable(true);
                toggleButtonShape3.setDisable(true);
                toggleButtonShape4.setDisable(true);
                toggleButtonShape5.setDisable(true);
                toggleButtonShape6.setDisable(true);
                toggleButtonShape7.setDisable(true);
                toggleButtonShape8.setDisable(true);
                toggleButtonShape9.setDisable(true);

                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));

            } else if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 2) {  //windowsill

                toggleButtonShape1.setDisable(false);
                toggleButtonShape2.setDisable(false);
                toggleButtonShape3.setDisable(false);
                toggleButtonShape4.setDisable(false);
                toggleButtonShape5.setDisable(false);
                toggleButtonShape6.setDisable(false);
                toggleButtonShape7.setDisable(false);
                toggleButtonShape8.setDisable(false);
                toggleButtonShape9.setDisable(false);

                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));

            } else if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 3) {
                toggleGroupShapes.selectToggle(toggleButtonShape1);
                toggleButtonShape1.setDisable(false);
                toggleButtonShape2.setDisable(true);
                toggleButtonShape3.setDisable(true);
                toggleButtonShape4.setDisable(true);
                toggleButtonShape5.setDisable(true);
                toggleButtonShape6.setDisable(true);
                toggleButtonShape7.setDisable(true);
                toggleButtonShape8.setDisable(true);
                toggleButtonShape9.setDisable(true);

                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));
            }

            refreshDepthsField();
            updatePrice();
        });

        choiceBoxMaterial.setOnAction(event -> {
            refreshDepthsField();
            updatePrice();
        });

        choiceBoxDepth.setOnAction(event -> {
            updatePrice();
        });

        toggleGroupShapes.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            labelC_R.setText("C:");
            labelD.setText("D:");

            if (newValue == toggleButtonShape1) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));

                textFieldSizeA.setText("600");
                textFieldSizeB.setText("600");
                textFieldSizeC.setText("0");
                textFieldSizeD.setText("0");

                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(true);
                textFieldSizeD.setDisable(true);
            } else if (newValue == toggleButtonShape2) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle_corner.png").toString()));

                textFieldSizeA.setText("900");
                textFieldSizeB.setText("900");
                textFieldSizeC.setText("600");
                textFieldSizeD.setText("600");
                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(false);
            } else if (newValue == toggleButtonShape3) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle_circle.png").toString()));

                labelC_R.setText("R:");
                textFieldSizeA.setText("600");
                textFieldSizeB.setText("600");
                textFieldSizeC.setText("300");
                textFieldSizeD.setText("0");
                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(true);
            } else if (newValue == toggleButtonShape4) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle_circle_into.png").toString()));

                labelC_R.setText("R:");
                textFieldSizeA.setText("600");
                textFieldSizeB.setText("600");
                textFieldSizeC.setText("300");
                textFieldSizeD.setText("0");

                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(true);
            } else if (newValue == toggleButtonShape5) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/triangle.png").toString()));

                textFieldSizeA.setText("600");
                textFieldSizeB.setText("600");
                textFieldSizeC.setText(String.format(Locale.ENGLISH, "%.0f", Math.pow(Math.pow(600, 2) + Math.pow(600, 2), 0.5)));
                textFieldSizeD.setText("0");

                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(true);
                textFieldSizeD.setDisable(true);
            } else if (newValue == toggleButtonShape6) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/circle_half.png").toString()));

                labelC_R.setText("R:");
                textFieldSizeA.setText("0");
                textFieldSizeB.setText("0");
                textFieldSizeC.setText("300");
                textFieldSizeD.setText("0");

                textFieldSizeA.setDisable(true);
                textFieldSizeB.setDisable(true);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(true);
            } else if (newValue == toggleButtonShape7) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/circle.png").toString()));

                labelC_R.setText("R:");
                textFieldSizeA.setText("0");
                textFieldSizeB.setText("0");
                textFieldSizeC.setText("300");
                textFieldSizeD.setText("0");

                textFieldSizeA.setDisable(true);
                textFieldSizeB.setDisable(true);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(true);
            } else if (newValue == toggleButtonShape8) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/trapeze.png").toString()));

                textFieldSizeA.setText("1000");
                textFieldSizeB.setText("600");
                labelC_R.setText("α:");
                textFieldSizeC.setText("60");//a
                labelD.setText("β:");
                textFieldSizeD.setText("60");//a


                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(false);
            } else if (newValue == toggleButtonShape9) {
                imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rhombus.png").toString()));

                textFieldSizeA.setText("1000");
                textFieldSizeB.setText("600");
                textFieldSizeC.setText("400");
                textFieldSizeD.setText("400");


                textFieldSizeA.setDisable(false);
                textFieldSizeB.setDisable(false);
                textFieldSizeC.setDisable(false);
                textFieldSizeD.setDisable(false);
            }
        });
    }

    private static void addItem(int index, int quantity){
        StoneProductType stoneProductType = StoneProductType.TABLETOP;
        if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 0)
            stoneProductType = StoneProductType.TABLETOP;
        else if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 1)
            stoneProductType = StoneProductType.WALL_PANEL;
        else if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 2)
            stoneProductType = StoneProductType.WINDOWSILL;
        else if (choiceBoxElementType.getSelectionModel().getSelectedIndex() == 3)
            stoneProductType = StoneProductType.FOOT;

        ShapeType shapeType = ShapeType.RECTANGLE;
        if (toggleButtonShape1.isSelected()) shapeType = ShapeType.RECTANGLE;
        else if (toggleButtonShape2.isSelected()) shapeType = ShapeType.RECTANGLE_WITH_CORNER;
        else if (toggleButtonShape3.isSelected()) shapeType = ShapeType.RECTANGLE_WITH_RADIUS;
        else if (toggleButtonShape4.isSelected()) shapeType = ShapeType.RECTANGLE_WITH_RADIUS_INTO;
        else if (toggleButtonShape5.isSelected()) shapeType = ShapeType.TRIANGLE;
        else if (toggleButtonShape6.isSelected()) shapeType = ShapeType.CIRCLE_HALF;
        else if (toggleButtonShape7.isSelected()) shapeType = ShapeType.CIRCLE;
        else if (toggleButtonShape8.isSelected()) shapeType = ShapeType.TRAPEZE;
        else if (toggleButtonShape9.isSelected()) shapeType = ShapeType.RHOMBUS;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }
        int depth = 0;
        double workCoefficient = 0;
        int workCoefficientIndex = 0;
        String productName = "";

        if (material.getMainType().equals("Кварцевый агломерат") || material.getMainType().equals("Натуральный камень")){

            String depthStr = choiceBoxDepth.getValue();
            depth = Integer.parseInt(depthStr.split(" ")[0].replace("d", ""));
            if (stoneProductType == StoneProductType.TABLETOP) {
                if (choiceBoxDepth.getValue().equals("d12 с кромкой 12мм")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой 12мм";
                }
                if (choiceBoxDepth.getValue().equals("d12 с кромкой >12мм")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой >12мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой 20мм")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой 20мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой >20мм")) {
                    workCoefficientIndex = 3;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой >20мм";
                }
                if (choiceBoxDepth.getValue().equals("d30 с кромкой 30мм")) {
                    workCoefficientIndex = 4;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30 с кромкой 30мм";
                }
            } else if (stoneProductType == StoneProductType.WALL_PANEL) {
                if (choiceBoxDepth.getValue().equals("d12")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12";
                }
                if (choiceBoxDepth.getValue().equals("d20")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20";
                }
                if (choiceBoxDepth.getValue().equals("d30")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30";
                }
            } else if (stoneProductType == StoneProductType.WINDOWSILL) {
                if (choiceBoxDepth.getValue().equals("d12 с кромкой 12мм")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой 12мм";
                }
                if (choiceBoxDepth.getValue().equals("d12 с кромкой >12мм")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой >12мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой 20мм")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой 20мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой >20мм")) {
                    workCoefficientIndex = 3;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой >20мм";
                }
                if (choiceBoxDepth.getValue().equals("d30 с кромкой 30мм")) {
                    workCoefficientIndex = 4;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30 с кромкой 30мм";
                }
            } else if (stoneProductType == StoneProductType.FOOT) {
                if (choiceBoxDepth.getValue().equals("d12 с кромкой 12мм")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой 12мм";
                }
                if (choiceBoxDepth.getValue().equals("d12 с кромкой >12мм")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой >12мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой 20мм")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой 20мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой >20мм")) {
                    workCoefficientIndex = 3;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой >20мм";
                }
                if (choiceBoxDepth.getValue().equals("d30 с кромкой 30мм")) {
                    workCoefficientIndex = 4;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30 с кромкой 30мм";
                }
            }
        } else if(
                material.getMainType().equals("Dektone") ||
                material.getMainType().equals("Мраморный агломерат") ||
                material.getName().indexOf("Кварцекерамический камень") != -1){
            String depthStr = choiceBoxDepth.getValue();
            depth = Integer.parseInt(depthStr.split(" ")[0].replace("d", ""));
            if (stoneProductType == StoneProductType.TABLETOP) {
                if (choiceBoxDepth.getValue().equals("d12 с кромкой 12мм")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой 12мм";
                }
                if (choiceBoxDepth.getValue().equals("d12 с кромкой >12мм")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой >12мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой 20мм")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой 20мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой >20мм")) {
                    workCoefficientIndex = 3;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой >20мм";
                }
                if (choiceBoxDepth.getValue().equals("d30 с кромкой 30мм")) {
                    workCoefficientIndex = 4;
                    workCoefficient = material.getTableTopCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30 с кромкой 30мм";
                }
            } else if (stoneProductType == StoneProductType.WALL_PANEL) {
                if (choiceBoxDepth.getValue().equals("d12")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12";
                }
                if (choiceBoxDepth.getValue().equals("d20")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20";
                }
                if (choiceBoxDepth.getValue().equals("d30")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getWallPanelCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30";
                }
            } else if (stoneProductType == StoneProductType.WINDOWSILL) {
                if (choiceBoxDepth.getValue().equals("d12 с кромкой 12мм")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой 12мм";
                }
                if (choiceBoxDepth.getValue().equals("d12 с кромкой >12мм")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой >12мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой 20мм")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой 20мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой >20мм")) {
                    workCoefficientIndex = 3;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой >20мм";
                }
                if (choiceBoxDepth.getValue().equals("d30 с кромкой 30мм")) {
                    workCoefficientIndex = 4;
                    workCoefficient = material.getWindowSillCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30 с кромкой 30мм";
                }
            } else if (stoneProductType == StoneProductType.FOOT) {
                if (choiceBoxDepth.getValue().equals("d12 с кромкой 12мм")) {
                    workCoefficientIndex = 0;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой 12мм";
                }
                if (choiceBoxDepth.getValue().equals("d12 с кромкой >12мм")) {
                    workCoefficientIndex = 1;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d12 с кромкой >12мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой 20мм")) {
                    workCoefficientIndex = 2;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой 20мм";
                }
                if (choiceBoxDepth.getValue().equals("d20 с кромкой >20мм")) {
                    workCoefficientIndex = 3;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d20 с кромкой >20мм";
                }
                if (choiceBoxDepth.getValue().equals("d30 с кромкой 30мм")) {
                    workCoefficientIndex = 4;
                    workCoefficient = material.getFootCoefficientList().get(workCoefficientIndex);
                    productName = stoneProductType.getName() + " d30 с кромкой 30мм";
                }
            }
        } else {
            String depthS = choiceBoxDepth.getValue().replace("d", "");
            depth = Integer.parseInt(depthS);

            productName = stoneProductType.getName() + " d" + depth;
        }

        if(shapeType == ShapeType.RECTANGLE_WITH_RADIUS ||
                shapeType == ShapeType.RECTANGLE_WITH_RADIUS_INTO ||
                shapeType == ShapeType.CIRCLE_HALF ||
                shapeType == ShapeType.CIRCLE){

            productName += ", R = " + textFieldSizeC.getText() + "мм";

        }

        //System.out.println("workCoefficient = " + workCoefficient);

        double sizeA = 0;
        double sizeB = 0;
        double sizeC = 0;
        double sizeD = 0;

        try {
            sizeA = Double.parseDouble(textFieldSizeA.getText());
        } catch (NumberFormatException ex) {
            sizeA = 0;
            return;
        }

        try {
            sizeB = Double.parseDouble(textFieldSizeB.getText());
        } catch (NumberFormatException ex) {
            sizeB = 0;
            return;
        }

        try {
            sizeC = Double.parseDouble(textFieldSizeC.getText());
        } catch (NumberFormatException ex) {
            sizeC = 0;
        }

        try {
            sizeD = Double.parseDouble(textFieldSizeD.getText());
        } catch (NumberFormatException ex) {
            sizeD = 0;
        }



        double materialLength = material.getMaterialWidth();//mm
        double materialHeight = material.getMaterialHeight();//mm
        double shapeLen = sizeA;//mm
        double shapeHeight = sizeB;//mm

        if (shapeType == ShapeType.RECTANGLE || shapeType == ShapeType.RECTANGLE_WITH_CORNER ||
                shapeType == ShapeType.RECTANGLE_WITH_RADIUS_INTO || shapeType == ShapeType.RECTANGLE_WITH_RADIUS ||
                shapeType == ShapeType.TRIANGLE || shapeType == ShapeType.TRAPEZE || shapeType == ShapeType.RHOMBUS) {
            shapeLen = sizeA;
            shapeHeight = sizeB;
        } else if (shapeType == ShapeType.CIRCLE_HALF) {
            shapeLen = sizeC * 2;
            shapeHeight = sizeC;
        } else if (shapeType == ShapeType.CIRCLE) {
            shapeLen = sizeC * 2;
            shapeHeight = sizeC * 2;
        }

        boolean oversize = false;
        if (((shapeLen > materialLength && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeHeight > materialHeight)) ||
                (shapeHeight > materialHeight && shapeLen > materialHeight) || (shapeHeight > materialLength && shapeLen > materialLength)) {
            InfoMessage.showMessage(InfoMessage.MessageType.WARNING, "Размер не соответствует материалу!", null);
            oversize = true;
        }

        double maxPieceXSize = materialHeight;
        double maxPieceYSize = materialHeight;

        int xPieces = (int) Math.floor(shapeLen / maxPieceXSize);
        if (shapeLen % maxPieceXSize != 0) xPieces += 1;

        int yPieces = (int) Math.floor(shapeHeight / maxPieceYSize);
        if (shapeHeight % maxPieceYSize != 0) yPieces += 1;

        ArrayList<ArrayList<Point2D>> cutShapesCoordinates = new ArrayList<>(1);
        ArrayList<ArrayList<Double>> cutShapesAngles = new ArrayList<>(5);
        for(int j=0;j< quantity;j++){
            ArrayList<Point2D> coordList = new ArrayList<>();
            ArrayList<Double> angleList = new ArrayList<>();

            if(!material.isUseMainSheets()){
                xPieces = 1;
                yPieces = 1;
            }

            for (int i = 0; i < xPieces * yPieces; i++) {
                coordList.add(new Point2D(0, 0));
                angleList.add(Double.valueOf(0));
            }

            cutShapesCoordinates.add(coordList);
            cutShapesAngles.add(angleList);
        }

        //String name = stoneProductType.getName() + " " + choiceBoxDepth.getSelectionModel().getSelectedItem();

        TableDesignerSession.getTableDesignerMainItemsList().add(index, new StoneProductItem(cutShapesCoordinates, cutShapesAngles,
                quantity, stoneProductType, shapeType, material, depth, sizeA, sizeB, sizeC, sizeD,
                workCoefficient, workCoefficientIndex, productName));
    }

    public static void refreshDepthsField() {
        choiceBoxDepth.getItems().clear();
        Material selectedMaterial = null;
        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getValue())) {
                selectedMaterial = material;
            }
        }

        if (choiceBoxElementType.getSelectionModel().getSelectedItem() == null) return;
        if (selectedMaterial == null) return;

        if (selectedMaterial.getMainType().equals("Кварцевый агломерат") || selectedMaterial.getMainType().equals("Натуральный камень")) {
            for (String str : selectedMaterial.getDepths()) {
                int depth = Integer.parseInt(str);
                if (depth == 12) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d12");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    }
                }else if (depth == 20) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d20");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    }
                } else if (depth == 30) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d30");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    }
                }
            }
        } else if(
                selectedMaterial.getMainType().equals("Dektone") ||
                selectedMaterial.getMainType().equals("Мраморный агломерат") ||
                selectedMaterial.getMainType().equals("Кварцекерамический камень")){
            for (String str : selectedMaterial.getDepths()) {
                int depth = Integer.parseInt(str);
                if (depth == 12) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d12");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    }
                }else if (depth == 20) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d20");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    }
                } else if (depth == 30) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d30");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    }
                }
            }
        } else {
            for(String s : selectedMaterial.getDepths()){
                choiceBoxDepth.getItems().add("d"+ s);
            }
        }



        choiceBoxDepth.getSelectionModel().select(0);

        //if selected Windowsill it should be selected coefficient = d20 and edge >20mm
        if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
            if(choiceBoxDepth.getItems().contains("d12 с кромкой >12мм")){
                choiceBoxDepth.getSelectionModel().select("d12 с кромкой >12мм");
            }else if(choiceBoxDepth.getItems().contains("d20 с кромкой >20мм")){
                choiceBoxDepth.getSelectionModel().select("d20 с кромкой >20мм");
            }
        }
    }

    public static void settingsControlElementsRefresh() {

        choiceBoxElementType.getItems().clear();
        choiceBoxElementType.getItems().addAll(
                "Столешница",
                "Стеновая панель",
                "Подоконник",
                "Опора");
        choiceBoxElementType.getSelectionModel().select(0);


        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        choiceBoxDepth.getItems().clear();
        Material defaultMaterial = Project.getDefaultMaterial();
        if (defaultMaterial.getMainType().equals("Кварцевый агломерат") || defaultMaterial.getMainType().equals("Натуральный камень")) {
            for (String str : defaultMaterial.getDepths()) {
                int depth = Integer.parseInt(str);
                if (depth == 20) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d20");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    }
                } else if (depth == 30) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d30");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    }
                }
            }
        } else if(
                defaultMaterial.getMainType().equals("Dektone") ||
                defaultMaterial.getMainType().equals("Мраморный агломерат") ||
                defaultMaterial.getMainType().equals("Кварцекерамический камень")){
            for (String str : defaultMaterial.getDepths()) {
                int depth = Integer.parseInt(str);
                if (depth == 12) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d12");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d12 с кромкой 12мм");
                        choiceBoxDepth.getItems().add("d12 с кромкой >12мм");
                    }
                }else if (depth == 20) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d20");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d20 с кромкой 20мм");
                        choiceBoxDepth.getItems().add("d20 с кромкой >20мм");
                    }
                } else if (depth == 30) {
                    if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель")) {
                        choiceBoxDepth.getItems().add("d30");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    } else if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора")) {
                        choiceBoxDepth.getItems().add("d30 с кромкой 30мм");
                    }
                }
            }
        } else {
            for(String s : Project.getDefaultMaterial().getDepths()){
                choiceBoxDepth.getItems().add("d" + s);
            }
        }


        int defDep = Project.getDefaultMaterial().getDefaultDepth();
        for(String s : choiceBoxDepth.getItems()){
            if(s.indexOf("d"+ defDep) != -1) choiceBoxDepth.getSelectionModel().select(s);
        }

        if(choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник")){
            if(choiceBoxDepth.getItems().contains("d20 с кромкой >20мм")){
                choiceBoxDepth.getSelectionModel().select("d20 с кромкой >20мм");
            }
        }

        toggleButtonShape1.setSelected(true);
        imageViewShape.setImage(new Image(Project.class.getResource("/styles/images/TableDesigner/StoneProductItem/sizes/rectangle.png").toString()));

        textFieldSizeA.setText("600");
        textFieldSizeB.setText("600");
        textFieldSizeC.setText("0");
        textFieldSizeC.setDisable(true);
        textFieldSizeD.setDisable(true);

        updatePrice();
    }

    private static void updatePrice() {

        if (choiceBoxElementType.getSelectionModel().getSelectedItem() == null) return;
        if (choiceBoxDepth.getSelectionModel().getSelectedItem() == null) return;

        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                String units = "м^2";
                double priceForOne = -1.0;

                ElementTypes elementType = ElementTypes.TABLETOP;
                if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Столешница"))
                    elementType = ElementTypes.TABLETOP;
                if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Стеновая панель"))
                    elementType = ElementTypes.WALL_PANEL;
                if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Подоконник"))
                    elementType = ElementTypes.WINDOWSILL;
                if (choiceBoxElementType.getSelectionModel().getSelectedItem().equals("Опора"))
                    elementType = ElementTypes.FOOT;

                int depth;
                //System.out.println(choiceBoxDepth.getSelectionModel().getSelectedItem());

                if (material.getMainType().equals("Кварцевый агломерат") ||
                        material.getMainType().equals("Натуральный камень") ||
                        material.getMainType().equals("Dektone") ||
                        material.getMainType().equals("Мраморный агломерат") ||
                        material.getMainType().equals("Кварцекерамический камень")) {

                    depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem().split(" ")[0].replace("d", ""));
                } else {
                    depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem().split(" ")[0].replace("d", ""));
                    //depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());
                }

                priceForOne = material.getPrice(elementType, depth);

                double priceInRUR = 0;
                if(material.getCurrency().equals("RUB")) priceInRUR = priceForOne;
                else if(material.getCurrency().equals("USD")) priceInRUR = priceForOne * MainWindow.getUSDValue().get();
                else if(material.getCurrency().equals("EUR")) priceInRUR = priceForOne * MainWindow.getEURValue().get();
                String symbol = Currency.RUR_SYMBOL;

                //priceForOne /= 100.0;

                labelPrice.setText("Цена: " + String.format(Locale.ENGLISH, "%.0f", priceInRUR) + " " + symbol + "/" + units);
                break;
            }
        }
    }

    private static void enterToEditMode(StoneProductItem stoneProductItem){
        TableDesigner.openSettings(StoneProductItem.class);

        //get row data to settings
        if(stoneProductItem.stoneProductType.equals(StoneProductType.TABLETOP))
            choiceBoxElementType.getSelectionModel().select(0);
        else if(stoneProductItem.stoneProductType.equals(StoneProductType.WALL_PANEL))
            choiceBoxElementType.getSelectionModel().select(1);
        else if(stoneProductItem.stoneProductType.equals(StoneProductType.WINDOWSILL))
        choiceBoxElementType.getSelectionModel().select(2);
        else if(stoneProductItem.stoneProductType.equals(StoneProductType.FOOT))
            choiceBoxElementType.getSelectionModel().select(3);

        choiceBoxMaterial.getSelectionModel().select(stoneProductItem.getMaterial().getReceiptName());

        if(stoneProductItem.shapeType.equals(ShapeType.RECTANGLE))toggleButtonShape1.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.RECTANGLE_WITH_CORNER))toggleButtonShape2.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.RECTANGLE_WITH_RADIUS))toggleButtonShape3.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.RECTANGLE_WITH_RADIUS_INTO))toggleButtonShape4.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.TRIANGLE))toggleButtonShape5.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.CIRCLE_HALF))toggleButtonShape6.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.CIRCLE))toggleButtonShape7.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.TRAPEZE))toggleButtonShape8.setSelected(true);
        if(stoneProductItem.shapeType.equals(ShapeType.RHOMBUS))toggleButtonShape9.setSelected(true);

        String depth = "d" + stoneProductItem.name.split(" d")[1];

        if(stoneProductItem.shapeType == ShapeType.RECTANGLE_WITH_RADIUS ||
                stoneProductItem.shapeType == ShapeType.RECTANGLE_WITH_RADIUS_INTO ||
                stoneProductItem.shapeType == ShapeType.CIRCLE_HALF ||
                stoneProductItem.shapeType == ShapeType.CIRCLE){

            depth = depth.split(",")[0];
        }

        choiceBoxDepth.getSelectionModel().select(depth);

        textFieldSizeA.setText("" + stoneProductItem.sizeA);
        textFieldSizeB.setText("" + stoneProductItem.sizeB);
        textFieldSizeC.setText("" + stoneProductItem.sizeC);
        textFieldSizeD.setText("" + stoneProductItem.sizeD);

        //disable button "add"
        btnAdd.setVisible(false);
        //create buttons "apply" and "cancel"
        anchorPaneSettingsView.getChildren().remove(btnApply);
        anchorPaneSettingsView.getChildren().remove(btnCancel);
        AnchorPane.setBottomAnchor(btnApply, 10.0);
        AnchorPane.setRightAnchor(btnApply, 10.0);
        AnchorPane.setBottomAnchor(btnCancel, 10.0);
        AnchorPane.setRightAnchor(btnCancel, 55.0);
        anchorPaneSettingsView.getChildren().add(btnApply);
        anchorPaneSettingsView.getChildren().add(btnCancel);

        //add listeners to new buttons
        btnApply.setOnAction(event -> {
            int index = TableDesignerSession.getTableDesignerMainItemsList().indexOf(stoneProductItem);
            if(index != -1){//error when materialManager opened in edit mode
                addItem(index, stoneProductItem.quantity);
            }

            exitFromEditMode(stoneProductItem);
            stoneProductItem.removeThisItem();
        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(stoneProductItem);
        });
        //in listeners:
        //"apply". delete old row and create new row
        //"cancel". exit from edit mode
    }

    public static void exitFromEditMode(TableDesignerItem tableDesignerItem){
        btnAdd.setVisible(true);
        //delete buttons "apply" and "cancel"
        anchorPaneSettingsView.getChildren().remove(btnApply);
        anchorPaneSettingsView.getChildren().remove(btnCancel);
        //unselect row
        tableDesignerItem.setEditModeProperty(false);
        settingsControlElementsRefresh();
    }
    /**
     * Dependency part
     */
    private void changeSlaveQuantity(int quantity) {
        for (TableDesignerItem slaveItem : slaveItems) {
            if (slaveItem instanceof RadiusItem) {
                slaveItem.setQuantity(quantity);
            }
        }
    }

    private void deleteSlaves() {
        for (TableDesignerItem slaveItem : slaveItems) {
            if (slaveItem instanceof RadiusItem) {
                RadiusItem radiusItem = (RadiusItem) slaveItem;
                TableDesignerSession.getTableDesignerMainWorkItemsList().remove(radiusItem);
            }
        }
        slaveItems.clear();
    }

    /**
     * JSON SAVING & OPENING PART
     */

    @Override
    public JSONObject getJsonView() {

        CutDesigner.getInstance().getCutPane().setCutPaneScale(1.0);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", "StoneProductItem");

        jsonObject.put("quantity", quantity);

        jsonObject.put("stoneProductType", stoneProductType.name());
        jsonObject.put("shapeType", shapeType.toString());
        jsonObject.put("material", material.getName());
        jsonObject.put("depth", depth);
        jsonObject.put("sizeA", sizeA);
        jsonObject.put("sizeB", sizeB);
        jsonObject.put("sizeC", sizeC);
        jsonObject.put("sizeD", sizeD);

        jsonObject.put("name", name);
        jsonObject.put("workCoefficient", workCoefficient);
        jsonObject.put("workCoefficientIndex", workCoefficientIndex);


        JSONArray coordinatesGlobal = new JSONArray();
        for (ArrayList<SketchShape> shapeList : sketchShapeArrayList) {

            JSONArray coordinatesArray = new JSONArray();
            for (SketchShape shape : shapeList) {
                JSONObject obj = new JSONObject();

                CutShape cutShape = shape.getCutShapeWithoutRefresh();
                obj.put("x", cutShape.getTranslateX());
                obj.put("y", cutShape.getTranslateY());
                coordinatesArray.add(obj);
            }
            coordinatesGlobal.add(coordinatesArray);
        }
        jsonObject.put("cutShapesCoordinates", coordinatesGlobal);

        JSONArray anglesGlobal = new JSONArray();
        for (ArrayList<SketchShape> shapeList : sketchShapeArrayList) {
            JSONArray anglesArray = new JSONArray();
            for (SketchShape shape : shapeList) {
                double angle = shape.getCutShapeWithoutRefresh().getRotateAngle();
                anglesArray.add(angle);
            }
            anglesGlobal.add(anglesArray);
        }
        jsonObject.put("cutShapeAngles", anglesGlobal);

        return jsonObject;
    }

    public static StoneProductItem initFromJSON(JSONObject jsonObject) {
        String materialName = (String) jsonObject.get("material");

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        StoneProductType stoneProductType = StoneProductType.valueOf((String) jsonObject.get("stoneProductType"));
        ShapeType shapeType = ShapeType.valueOf((String) jsonObject.get("shapeType"));

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {
                material = m;
                break;
            }
        }
        if (material == null) return null;

        double workCoefficient = (Double) jsonObject.get("workCoefficient");
        int workCoefficientIndex = 0;
        if(jsonObject.get("workCoefficientIndex") != null){
            workCoefficientIndex = ((Long) jsonObject.get("workCoefficientIndex")).intValue();
        }

        int depth = ((Long) jsonObject.get("depth")).intValue();
        double sizeA = (Double) jsonObject.get("sizeA");
        double sizeB = (Double) jsonObject.get("sizeB");
        double sizeC = (Double) jsonObject.get("sizeC");
        double sizeD = (Double) jsonObject.get("sizeD");

        String name = ((String) jsonObject.get("name"));

        //get data for cutShapes
        ArrayList<ArrayList<Point2D>> cutShapeCoordinatesGlobal = new ArrayList<>(1);
        JSONArray coordinatesArrayGlobal = (JSONArray) jsonObject.get("cutShapesCoordinates");
        for (Object obj : coordinatesArrayGlobal) {

            ArrayList<Point2D> cutShapeCoordinates = new ArrayList<>(5);
            JSONArray coordArray = (JSONArray) obj;
            for (Object obj1 : coordArray) {
                JSONObject point = (JSONObject) obj1;
                cutShapeCoordinates.add(new Point2D((Double) point.get("x"), (Double) point.get("y")));
            }
            cutShapeCoordinatesGlobal.add(cutShapeCoordinates);
        }

        ArrayList<ArrayList<Double>> cutShapeAnglesGlobal = new ArrayList<>(1);
        JSONArray anglesArrayGlobal = (JSONArray) jsonObject.get("cutShapeAngles");
        for (Object obj : anglesArrayGlobal) {

            ArrayList<Double> cutShapeAngles = new ArrayList<>(5);
            JSONArray anglesArray = (JSONArray) obj;
            for (Object obj1 : anglesArray) {
                Double angle = (Double) obj1;
                cutShapeAngles.add(angle);
            }
            cutShapeAnglesGlobal.add(cutShapeAngles);
        }

        //System.out.println(cutShapeCoordinatesGlobal.toString());
        StoneProductItem stoneProductItem = new StoneProductItem(cutShapeCoordinatesGlobal, cutShapeAnglesGlobal,
                quantity, stoneProductType, shapeType, material, depth, sizeA, sizeB, sizeC, sizeD, workCoefficient,
                workCoefficientIndex, name);
        stoneProductItem.updateRowPrice();
        return stoneProductItem;

    }

}

enum StoneProductType {
    TABLETOP("Столешница"),
    WALL_PANEL("Стеновая панель"),
    WINDOWSILL("Подоконник"),
    FOOT("Опора");

    private String name;

    StoneProductType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

enum ShapeType {
    RECTANGLE,
    RECTANGLE_WITH_CORNER,
    RECTANGLE_WITH_RADIUS,
    RECTANGLE_WITH_RADIUS_INTO,
    TRIANGLE,
    CIRCLE_HALF,
    CIRCLE,
    TRAPEZE,
    RHOMBUS;
}
