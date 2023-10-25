package ru.koreanika.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.time.LocalDateTime;

public class ProjectHandler {

    private static final String MATERIALS_XLS_PATH = "materials_1_2004.xls";
    private static final String ANALOGS_XLS_PATH = "material_analogs.xls";

    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";

    static JSONObject userProject;
    static String curProjectPath;
    static String curProjectName;

    public static void init() {
        FacadeXLSParser parser = new FacadeXLSParser(MATERIALS_XLS_PATH, ANALOGS_XLS_PATH);
        parser.populateLists(Project.materialsListAvailable, Project.plumbingElementsList, Project.availablePlumbingTypes,
                Project.materialsDeliveryFromManufacturer);
    }

    public static void createProject(String projectName, String projectPath, ProjectType projectType) {
        Project.setProjectType(projectType);

        Project.materialsListInProject.clear();
        Project.defaultMaterial = null;

        userProject = new JSONObject();

        if (!projectName.matches(".+\\.krnkproj$") && !projectName.matches(".+\\.kproj$")) {
            projectName += ".kproj";
            projectPath += ".kproj";
        }
        if (projectName.matches(".+\\.krnkproj$")) {
            projectName = projectName.replace("\\.krnkproj", ".kproj");
        }
        if (projectPath.matches(".+\\.krnkproj$")) {
            projectPath = projectPath.replace("\\.krnkproj", ".kproj");
        }
        curProjectName = projectName;
        curProjectPath = projectPath;

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

        userProject.put("info", info);

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

        userProject.put("ProjectSettings", projectSettings);

        /** Sketch designer*/
        JSONObject sketchDesigner = new JSONObject();
        JSONArray sketchDesignerElements = new JSONArray();

        sketchDesigner.put("elements", sketchDesignerElements);
        userProject.put("SketchDesigner", sketchDesigner);


        /** Cut designer*/
        JSONObject cutDesigner = new JSONObject();
        userProject.put("cutDesigner", cutDesigner);
    }

    public static void saveProjectNewName(String projectPath, String projectName) {
        ProjectWriter.saveProject(projectPath, projectName);
    }

    public static void saveProject() {
        ProjectWriter.saveProject(curProjectPath, curProjectName);
    }

    public static void closeProject() {
        saveProject();

        if (userProject != null) {
            userProject.clear();
        }

        MainWindow.setCutDesigner(null);
        MainWindow.setSketchDesigner(null);
        userProject = null;

        Project.clearCollections();

        AdditionalFeature.createdFeaturesNumbersList.clear();

        Project.defaultMaterial = null;
    }

    public static JSONObject getUserProject() {
        return userProject;
    }

    public static String getCurProjectName() {
        return curProjectName;
    }

    public static String getCurProjectPath() {
        return curProjectPath;
    }

    public static void setCurProjectPath(String curProjectPath) {
        ProjectHandler.curProjectPath = curProjectPath;
    }

    public static void setCurProjectName(String curProjectName) {
        ProjectHandler.curProjectName = curProjectName;
    }
}
