package ru.koreanika.project;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.PriceCoefficientsWindow;

import java.util.*;

public class Project {

    private static ProjectType projectType = ProjectType.TABLE_TYPE;

    static Map<String, Double> materialsDeliveryFromManufacturer = new LinkedHashMap<>();// <Group name, Price in rub>
    static List<Material> materialsListAvailable = new ArrayList<>();
    static List<Material> materialsListInProject = new ArrayList<>();
    static Material defaultMaterial = null;

    static DoubleProperty priceMainCoefficient = new SimpleDoubleProperty(1);
    static DoubleProperty priceMaterialCoefficient = new SimpleDoubleProperty(1);

    private static List<Edge> edgesUsesInProject = new ArrayList<>();
    private static ObservableList<Edge> edgesUsesInProjectObservable = FXCollections.observableList(edgesUsesInProject);

    private static List<Border> bordersUsesInProject = new ArrayList<>();
    private static ObservableList<Border> bordersUsesInProjectObservable = FXCollections.observableList(bordersUsesInProject);

    private static List<String> materialsUsesInProject = new ArrayList<>();
    private static ObservableList<String> materialsUsesInProjectObservable = FXCollections.observableList(materialsUsesInProject);

    private static List<String> depthsTableTopsUsesInProject = new ArrayList<>();
    private static ObservableList<String> depthsTableTopsUsesInProjectObservable = FXCollections.observableList(depthsTableTopsUsesInProject);

    private static List<String> depthsWallPanelsUsesInProject = new ArrayList<>();
    private static ObservableList<String> depthsWallPanelsUsesInProjectObservable = FXCollections.observableList(depthsWallPanelsUsesInProject);

    private static List<String> edgesHeightsUsesInProject = new ArrayList<>();
    private static ObservableList<String> edgesHeightsUsesInProjectObservable = FXCollections.observableList(edgesHeightsUsesInProject);

    private static List<String> bordersHeightsUsesInProject = new ArrayList<>();
    private static ObservableList<String> bordersHeightsUsesInProjectObservable = FXCollections.observableList(bordersHeightsUsesInProject);


    static List<PlumbingElement> plumbingElementsList = new ArrayList<>();
    static LinkedHashSet<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
//    private static int defaultEdgeHeight = 20;
//    private static int defaultBorderHeight = 20;

    public static double CUT_AREA_EDGE_WIDTH = 50;
    public static double CUT_AREA_BORDER_WIDTH = 30;

    protected static double commonShapeScale = 0.1;

    public static DoubleProperty getPriceMainCoefficient() {
        return priceMainCoefficient;
    }

    public static void setPriceMainCoefficient(double newCoeff) {
        double minMainCoefficient = PriceCoefficientsWindow.getMinMainCoefficient();
        double maxMainCoefficient = PriceCoefficientsWindow.getMaxMainCoefficient();

        if (newCoeff >= minMainCoefficient && newCoeff <= maxMainCoefficient) {
            Project.priceMainCoefficient.set(newCoeff);
        } else {
            Project.priceMainCoefficient.set(minMainCoefficient);
        }
    }

    public static DoubleProperty getPriceMaterialCoefficient() {
        return priceMaterialCoefficient;
    }

    public static void setPriceMaterialCoefficient(double newCoeff) {
        double minMaterialCoefficient = PriceCoefficientsWindow.getMinMaterialCoefficient();
        double maxMaterialCoefficient = PriceCoefficientsWindow.getMaxMaterialCoefficient();

        if (newCoeff >= minMaterialCoefficient && newCoeff <= maxMaterialCoefficient) {
            Project.priceMaterialCoefficient.set(newCoeff);
        } else {
            Project.priceMaterialCoefficient.set(minMaterialCoefficient);
        }

        System.out.println("PROJECTHANDLER SET MATERIAL COEFFICIENT = " + Project.priceMaterialCoefficient.get());
    }

    public static double getCommonShapeScale() {
        return commonShapeScale;
    }

    public static void setProjectType(ProjectType projectType) {
        Project.projectType = projectType;
    }

    public static ProjectType getProjectType() {
        return Project.projectType;
    }

    public static Image receiptManagerSketchImage = null;

    public static Image getReceiptManagerSketchImage() {
        return receiptManagerSketchImage;
    }

    public static void setReceiptManagerSketchImage(Image receiptManagerSketchImage) {
        Project.receiptManagerSketchImage = receiptManagerSketchImage;
    }

    public static List<PlumbingElement> getPlumbingElementsList() {
        return plumbingElementsList;
    }

    public static LinkedHashSet<PlumbingType> getAvailablePlumbingTypes() {
        return availablePlumbingTypes;
    }

    public static List<Material> getMaterialsListAvailable() {
        return materialsListAvailable;
    }

    public static List<Material> getMaterialsListInProject() {
        return materialsListInProject;
    }

    public static ObservableList<Border> getBordersUsesInProjectObservable() {
        return bordersUsesInProjectObservable;
    }

    public static ObservableList<Edge> getEdgesUsesInProjectObservable() {
        return edgesUsesInProjectObservable;
    }

    public static ObservableList<String> getMaterialsUsesInProjectObservable() {
        return materialsUsesInProjectObservable;
    }

    public static ObservableList<String> getDepthsTableTopsUsesInProjectObservable() {
        return depthsTableTopsUsesInProjectObservable;
    }

    public static ObservableList<String> getDepthsWallPanelsUsesInProjectObservable() {
        return depthsWallPanelsUsesInProjectObservable;
    }

    public static ObservableList<String> getEdgesHeightsUsesInProjectObservable() {
        return edgesHeightsUsesInProjectObservable;
    }

    public static ObservableList<String> getBordersHeightsUsesInProjectObservable() {
        return bordersHeightsUsesInProjectObservable;
    }

    public static void setMaterialsListInProject(List<Material> materialsListInProject) {
        Project.materialsListInProject = materialsListInProject;
    }

    public static Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public static Map<String, Double> getMaterialsDeliveryFromManufacturer() {
        return materialsDeliveryFromManufacturer;
    }

    public static void setDefaultMaterialRAW(Material defaultMaterial) {
        Project.defaultMaterial = defaultMaterial;
    }

    public static void setDefaultMaterial(Material defaultMaterial) {
        Project.defaultMaterial = defaultMaterial;

        for (SketchShape sketchShape : SketchDesigner.getSketchShapesList()) {
            if (Project.getProjectType() == ProjectType.SKETCH_TYPE) {
                sketchShape.shapeSettingsSaveBtnClicked();//this will update default materials
            }
        }

        if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
            TableDesigner.updateMaterialsInProject();
        }
    }

    static void clearCollections() {
        materialsListInProject.clear();

        edgesUsesInProject.clear();
        edgesUsesInProjectObservable.clear();

        bordersUsesInProject.clear();
        bordersUsesInProjectObservable.clear();

        materialsUsesInProject.clear();
        materialsUsesInProjectObservable.clear();

        depthsTableTopsUsesInProject.clear();
        depthsTableTopsUsesInProjectObservable.clear();

        depthsWallPanelsUsesInProject.clear();
        depthsWallPanelsUsesInProjectObservable.clear();

        edgesHeightsUsesInProject.clear();
        edgesHeightsUsesInProjectObservable.clear();

        bordersHeightsUsesInProject.clear();
        bordersHeightsUsesInProjectObservable.clear();
    }

}
