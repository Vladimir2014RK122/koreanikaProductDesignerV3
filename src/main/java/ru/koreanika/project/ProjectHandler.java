package ru.koreanika.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.time.LocalDateTime;

public class ProjectHandler {

    public static final String MATERIALS_XLS_PATH = "materials_1_2004.xls";
    public static final String ANALOGS_XLS_PATH = "material_analogs.xls";

    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";

    public static void init() {
        FacadeXLSParser parser = new FacadeXLSParser(MATERIALS_XLS_PATH, ANALOGS_XLS_PATH);
        parser.populateLists(Project.materialsListAvailable, Project.plumbingElementsList, Project.availablePlumbingTypes,
                Project.materialsDeliveryFromManufacturer);
    }

    public static void createProject(String projectName, String projectPath, ProjectType projectType) {
        Project.setProjectType(projectType);

        Project.materialsListInProject.clear();
        Project.defaultMaterial = null;

        Project.userProject = new JSONObject();

        Project.curProjectName = projectName;
        Project.curProjectPath = projectPath;

        if (!Project.curProjectName.matches(".+\\.krnkproj$") && !Project.curProjectName.matches(".+\\.kproj$")) {
            Project.curProjectName += ".kproj";
            Project.curProjectPath += ".kproj";
        }

        if (Project.curProjectName.matches(".+\\.krnkproj$")) {
            Project.curProjectName = Project.curProjectName.replace("\\.krnkproj", ".kproj");
        }
        if (Project.curProjectPath.matches(".+\\.krnkproj$")) {
            Project.curProjectPath = Project.curProjectPath.replace("\\.krnkproj", ".kproj");
        }

        JSONObject info = new JSONObject();

        /** System data: */
        //window size:
        double windowWidth = Main.getMainWindow().getRootAnchorPaneMainWindow().getWidth();
        double windowHeight = Main.getMainWindow().getRootAnchorPaneMainWindow().getHeight();
        double windowPosX = Main.getMainScene().getWindow().getX();
        double windowPosY = Main.getMainScene().getWindow().getY();

        //date and time:
        info.put("createDate", LocalDateTime.now().toString());
        info.put("editDate", "1919");
        info.put("name", projectName);

        info.put("windowWidth", windowWidth);
        info.put("windowHeight", windowHeight);
        info.put("windowPosX", windowPosX);
        info.put("windowPosY", windowPosY);

        Project.userProject.put("info", info);

        /** Project settings*/
        JSONObject projectSettings = new JSONObject();

        //project type:
        projectSettings.put("projectType", Project.getProjectType().toString());

        //project coefficients for price:
        Project.setPriceMaterialCoefficient(Main.materialCoefficient);
        Project.setPriceMainCoefficient(Main.mainCoefficient);
        projectSettings.put("priceMainCoefficient", Project.priceMainCoefficient.getValue());
        projectSettings.put("priceMaterialCoefficient", Project.priceMaterialCoefficient.getValue());

        System.out.println("getPriceMaterialCoefficient() = " + Project.getPriceMaterialCoefficient().doubleValue());
        System.out.println("getPriceMainCoefficient() = " + Project.getPriceMainCoefficient().doubleValue());

        //material settings
        JSONObject materialSettings = new JSONObject();
        JSONArray materialsList = new JSONArray();
        for (Material material : Project.materialsListInProject) {
            materialsList.add(material.getName());
        }
        materialSettings.put("materialsList", materialsList);
        materialSettings.put("defaultMaterial", "null");
        materialSettings.put("depthForTableTop", 20);
        materialSettings.put("depthForWallPanel", 20);
        projectSettings.put("materialSettings", materialSettings);

        Project.userProject.put("ProjectSettings", projectSettings);

        /** Sketch designer*/
        JSONObject sketchDesigner = new JSONObject();
        JSONArray sketchDesignerElements = new JSONArray();

        sketchDesigner.put("elements", sketchDesignerElements);
        Project.userProject.put("SketchDesigner", sketchDesigner);


        /** Cut designer*/
        JSONObject cutDesigner = new JSONObject();
        Project.userProject.put("cutDesigner", cutDesigner);
    }

    public static void closeProject() {
        ProjectWriter.saveProject(Project.curProjectPath, Project.curProjectName);
        if (Project.userProject != null) {
            Project.userProject.clear();
        }

        MainWindow.setCutDesigner(null);
        MainWindow.setSketchDesigner(null);
        Project.userProject = null;

        Project.clearCollections();

        AdditionalFeature.createdFeaturesNumbersList.clear();

        Project.defaultMaterial = null;
    }
}
