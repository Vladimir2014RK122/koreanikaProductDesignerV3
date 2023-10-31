package ru.koreanika.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.Material.MaterialSheet;
import ru.koreanika.catalog.Catalogs;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.utils.CheckSheetsPrices;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProjectHandler {

    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";

    private JSONObject projectJSONObject;

    private String currentProjectPath;
    private String currentProjectName;

    private final EventBus eventBus;

    private final ProjectWriter projectWriter;
    private final ProjectReader projectReader;

    public ProjectHandler() {
        this.eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        this.projectReader = new ProjectReader(Catalogs.getMaterialsListAvailable());
        this.projectWriter = new ProjectWriter(this);
    }

    public void createProject(String projectName, String projectPath, ProjectType projectType) {
        Project.setProjectType(projectType);

        Project.getMaterials().clear();
        Project.setDefaultMaterial(null);

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
        projectSettings.put("priceMainCoefficient", Project.getPriceMainCoefficient().getValue());
        projectSettings.put("priceMaterialCoefficient", Project.getPriceMaterialCoefficient().getValue());

        System.out.println("getPriceMaterialCoefficient() = " + Project.getPriceMaterialCoefficient().doubleValue());
        System.out.println("getPriceMainCoefficient() = " + Project.getPriceMainCoefficient().doubleValue());

        //material settings
        JSONObject materialSettings = new JSONObject();
        JSONArray materialsList = new JSONArray();
        for (Material material : Project.getMaterials()) {
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
        if (!projectName.matches(".+\\.krnkproj$") && !projectName.matches(".+\\.kproj$")) {
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Неизвестный тип файла!!!"));
            return false;
        }

        try {
            JSONObject parsedProject = projectReader.read(projectPath);
            if (parsedProject != null) {
                this.currentProjectName = projectName;
                this.currentProjectPath = projectPath;
                this.projectJSONObject = parsedProject;

                checkMaterialSheetsPrices();
                projectWriter.saveProject(projectPath, projectName);
                return true;
            } else {
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, projectReader.getErrorMessage()));
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Проект поврежден! (" + projectReader.getErrorMessage() + ")"));
            return false;
        }
    }

    public void closeProject() {
        saveProject();

        if (projectJSONObject != null) {
            projectJSONObject.clear();
        }
        projectJSONObject = null;

        MainWindow.setCutDesigner(null);
        MainWindow.setSketchDesigner(null);

        Project.clearCollections();
        Project.setDefaultMaterial(null);

        AdditionalFeature.createdFeaturesNumbersList.clear();
    }

    private static void checkMaterialSheetsPrices() {
        CutDesigner.getInstance().refreshCutView();

        //check MaterialSheetsPrices:
        boolean haveDifference = false;
        LinkedHashMap<Material, ArrayList<MaterialSheet>> differenceMap = new LinkedHashMap<>();
        for (MaterialSheet materialSheet : CutDesigner.getInstance().getCutPane().getUsedMaterialSheetsList()) {

            boolean theSame = CheckSheetsPrices.checkPrices(materialSheet.getMaterial(), materialSheet);
            //if sheets didn't use set it old price too:
            if (!theSame/* && materialSheet.getUsesList() != 0*/) {
                haveDifference = true;

                ArrayList<MaterialSheet> sheetsList = differenceMap.get(materialSheet.getMaterial());
                if (sheetsList == null) {
                    sheetsList = new ArrayList<>();
                }
                sheetsList.add(materialSheet);
                differenceMap.put(materialSheet.getMaterial(), sheetsList);
            }
        }

        //check coefficients in sketchShapes
        if (haveDifference) {
            CheckSheetsPrices.showInfoWindow(Main.getMainScene(), differenceMap);
        }
    }

    public boolean projectSelected() {
        return projectJSONObject != null;
    }

    JSONObject getProjectJSONObject() {
        return projectJSONObject;
    }

    public String getCurrentProjectName() {
        return currentProjectName;
    }

    public String getCurrentProjectPath() {
        return currentProjectPath;
    }

    void setCurrentProjectPath(String currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    void setCurrentProjectName(String currentProjectName) {
        this.currentProjectName = currentProjectName;
    }

}
