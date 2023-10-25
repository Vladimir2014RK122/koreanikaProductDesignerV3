package ru.koreanika.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.time.LocalDateTime;

public class ProjectHandler {

    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";

    private JSONObject projectJSONObject;

    private String currentProjectPath;
    private String currentProjectName;

    private final ProjectWriter projectWriter;
    private final ProjectReader projectReader;

    public ProjectHandler() {
        this.projectWriter = new ProjectWriter(this);
        this.projectReader = new ProjectReader(this);
    }

    public void createProject(String projectName, String projectPath, ProjectType projectType) {
        Project.setProjectType(projectType);

        Project.materialsListInProject.clear();
        Project.defaultMaterial = null;

        projectJSONObject = new JSONObject();

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
        currentProjectName = projectName;
        currentProjectPath = projectPath;

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

        projectJSONObject.put("info", info);

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

        projectJSONObject.put("ProjectSettings", projectSettings);

        /** Sketch designer*/
        JSONObject sketchDesigner = new JSONObject();
        JSONArray sketchDesignerElements = new JSONArray();

        sketchDesigner.put("elements", sketchDesignerElements);
        projectJSONObject.put("SketchDesigner", sketchDesigner);


        /** Cut designer*/
        JSONObject cutDesigner = new JSONObject();
        projectJSONObject.put("cutDesigner", cutDesigner);
    }

    public void saveProjectNewName(String projectPath, String projectName) {
        projectWriter.saveProject(projectPath, projectName);
    }

    public void saveProject() {
        projectWriter.saveProject(currentProjectPath, currentProjectName);
    }

    public boolean openProject(String projectPath, String projectName) {
        boolean success = projectReader.read(projectPath, projectName);
        if (success) {
            projectWriter.saveProject(projectPath, projectName);
            return true;
        } else {
            return false;
        }
    }

    public void closeProject() {
        saveProject();

        if (projectJSONObject != null) {
            projectJSONObject.clear();
        }

        MainWindow.setCutDesigner(null);
        MainWindow.setSketchDesigner(null);
        projectJSONObject = null;

        Project.clearCollections();

        AdditionalFeature.createdFeaturesNumbersList.clear();

        Project.defaultMaterial = null;
    }

    public boolean projectSelected() {
        return projectJSONObject != null;
    }

    JSONObject getProjectJSONObject() {
        return projectJSONObject;
    }

    public void setProjectJSONObject(JSONObject projectJSONObject) {
        this.projectJSONObject = projectJSONObject;
    }

    public String getCurrentProjectName() {
        return currentProjectName;
    }

    public String getCurrentProjectPath() {
        return currentProjectPath;
    }

    public void setCurrentProjectPath(String currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    public void setCurrentProjectName(String currentProjectName) {
        this.currentProjectName = currentProjectName;
    }

}
