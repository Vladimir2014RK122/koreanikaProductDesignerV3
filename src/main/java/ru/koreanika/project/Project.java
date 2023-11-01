package ru.koreanika.project;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.utils.PriceCoefficientsWindow;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private static ProjectType projectType = ProjectType.TABLE_TYPE;

    /**
     * Materials selected from catalog for use in project
     */
    private static final List<Material> materials = new ArrayList<>();

    private static Material defaultMaterial = null;
    private static Image receiptManagerSketchImage = null;

    private static final DoubleProperty priceMainCoefficient = new SimpleDoubleProperty(1.0);
    private static final DoubleProperty priceMaterialCoefficient = new SimpleDoubleProperty(1.0);

    private static final List<Edge> edgesInUse = new ArrayList<>();
    private static final List<Border> bordersInUse = new ArrayList<>();
    private static final List<String> materialsInUse = new ArrayList<>();
    private static final List<String> depthsTableTopsInUse = new ArrayList<>();
    private static final List<String> depthsWallPanelsInUse = new ArrayList<>();
    private static final List<String> edgesHeightsInUse = new ArrayList<>();
    private static final List<String> bordersHeightsInUse = new ArrayList<>();

    private static final double commonShapeScale = 0.1;

    public static DoubleProperty getPriceMainCoefficient() {
        return priceMainCoefficient;
    }

    public static void setPriceMainCoefficient(double newCoeff) {
        // TODO refac: no access to UI controllers from domain models is allowed
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
        // TODO refac: no access to UI controllers from domain models is allowed
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

    public static Image getReceiptManagerSketchImage() {
        return receiptManagerSketchImage;
    }

    public static void setReceiptManagerSketchImage(Image receiptManagerSketchImage) {
        Project.receiptManagerSketchImage = receiptManagerSketchImage;
    }

    public static List<Material> getMaterials() {
        return materials;
    }

    public static List<Border> getBordersInUse() {
        return bordersInUse;
    }

    public static List<Edge> getEdgesInUse() {
        return edgesInUse;
    }

    public static List<String> getMaterialsInUse() {
        return materialsInUse;
    }

    public static List<String> getDepthsTableTopsInUse() {
        return depthsTableTopsInUse;
    }

    public static List<String> getDepthsWallPanelsInUse() {
        return depthsWallPanelsInUse;
    }

    public static List<String> getEdgesHeightsInUse() {
        return edgesHeightsInUse;
    }

    public static List<String> getBordersHeightsInUse() {
        return bordersHeightsInUse;
    }

    public static void setMaterials(List<Material> materials) {
        Project.materials.clear();
        Project.materials.addAll(materials);
    }

    public static Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public static void setDefaultMaterial(Material defaultMaterial) {
        Project.defaultMaterial = defaultMaterial;
    }

    static void clearCollections() {
        materials.clear();
        edgesInUse.clear();
        bordersInUse.clear();
        materialsInUse.clear();
        depthsTableTopsInUse.clear();
        depthsWallPanelsInUse.clear();
        edgesHeightsInUse.clear();
        bordersHeightsInUse.clear();
    }

}
