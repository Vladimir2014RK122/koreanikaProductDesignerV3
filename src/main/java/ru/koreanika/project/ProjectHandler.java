package ru.koreanika.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.common.material.MaterialSheet;
import ru.koreanika.catalog.Catalogs;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.CheckSheetsPrices;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ProjectHandler {

    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";
    private final EventBus eventBus;
    private final ProjectWriter projectWriter;
    private final ProjectReader projectReader;
    private JSONObject projectJSONObject;
    private String currentProjectPath;
    private String currentProjectName;

    public ProjectHandler() {
        this.eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        this.projectReader = new ProjectReader();
        this.projectWriter = new ProjectWriter(this);
    }

    /**
     * Внешние зависимости, которые нужно устранить:
     * - MainWindow.cutDesigner
     * - MainWindow.tableDesigner
     * - Main.mainCoefficient
     * - Main.materialCoefficient
     * - Project (static), side effects!!!
     * - javafx.scene.image.Image
     */
    private static void processParsedProject(JSONObject parsedProject, List<Material> materialsCatalog) throws ProjectException {
        /** System data: */
        JSONObject info = (JSONObject) parsedProject.get("info");  // TODO not used?

        /** Project settings*/
        JSONObject projectSettings = (JSONObject) parsedProject.get("ProjectSettings");

        //material settings:
        JSONObject materialSettings = (JSONObject) projectSettings.get("materialSettings");
        JSONArray materialsList = (JSONArray) materialSettings.get("materialsList");
        JSONArray materialsNewList = (JSONArray) materialSettings.get("materialsNewList");

        Project.setProjectType(ProjectType.valueOf((String) projectSettings.get("projectType")));

        /** Materials */
        //fill materials in project:
        if (materialsNewList != null) {
            for (Object obj : materialsNewList) {
                JSONObject materialObject = (JSONObject) obj;

                Material material = MaterialFactory.buildFromJSON(materialObject, materialsCatalog);
                if (material != null) {
                    Project.getMaterials().add(material);
                } else {
                    throw new ProjectException("Ошибка распаковки материала " + materialObject.get("name"));
                }
            }
        } else {
            for (Object str : materialsList) {
                String materialName = (String) str;
                System.out.println("materialsList item = " + materialName);

                Material materialTemplate = findMaterialTemplateByName(materialName, materialsCatalog);
                if (materialTemplate == null) {
                    throw new ProjectException("Материал не существует: " + str);
                } else {
                    Project.getMaterials().add(materialTemplate);
                }
            }
        }

        // set default material
        for (Material material : Project.getMaterials()) {
            if (((String) materialSettings.get("defaultMaterial")).contains(material.getName())) {
                Project.setDefaultMaterial(material);
            }
        }

        //project coefficients for price:
        Double priceMainCoefficientDouble = (Double) projectSettings.get("priceMainCoefficient");
        Double priceMaterialCoefficientDouble = (Double) projectSettings.get("priceMaterialCoefficient");
        if (priceMainCoefficientDouble == null || priceMaterialCoefficientDouble == null) {
            Project.setPriceMainCoefficient(Main.mainCoefficient);
            Project.setPriceMaterialCoefficient(Main.materialCoefficient);
        } else {
            Project.setPriceMainCoefficient(priceMainCoefficientDouble);
            Project.setPriceMaterialCoefficient(priceMaterialCoefficientDouble);
        }

        System.out.println("Default material = " + (Project.getDefaultMaterial() != null ? Project.getDefaultMaterial().getName() : "null"));

        if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
            /** Cut designer*/
            JSONObject cutDesigner = (JSONObject) parsedProject.get("CutDesigner");
            MainWindow.getCutDesigner().initFromJson(cutDesigner);

            JSONObject tableDesignerJSONObject = (JSONObject) parsedProject.get("TableDesigner");
            MainWindow.setTableDesigner(TableDesigner.initFromJSON(tableDesignerJSONObject));

            CutDesigner.getInstance().refreshCutView(); //need for correct calculate usesList in Material sheets
        } else if (Project.getProjectType() == ProjectType.SKETCH_TYPE) {
            /** Sketch designer*/
            JSONObject sketchDesigner = (JSONObject) parsedProject.get("SketchDesigner");
            SketchDesigner.setInstanceFromJson(sketchDesigner);

            /** Cut designer*/
            JSONObject cutDesigner = (JSONObject) parsedProject.get("CutDesigner");
            MainWindow.getCutDesigner().initFromJson(cutDesigner);
        }

        /** receiptManager*/
        JSONObject jsonObjectReceiptManager = (JSONObject) parsedProject.get("receiptManager");
        MainWindow.getReceiptManager().initFromJsonObject(jsonObjectReceiptManager);
    }

    private static Material findMaterialTemplateByName(String str, List<Material> materialsCatalog) throws NumberFormatException {
        String[] materialStrArrWithDepth = str.split("#");
        String[] materialStrArr = materialStrArrWithDepth[0].split("\\$");

        String mainType = materialStrArr[0];
        String subType = materialStrArr[1];
        String collection = materialStrArr[2];
        String color = materialStrArr[3];
        double width = Double.parseDouble(materialStrArr[4]);
        double height = Double.parseDouble(materialStrArr[5]);
        String imgPath = (materialStrArr.length == 8 ? materialStrArr[6] : "no_img.png");
        String depthsString = materialStrArr[7];

        String nameForFind = mainType + "$" + subType + "$" + collection + "$" + color + "$" + width + "$" + height + "$" + imgPath + "$" + depthsString;

        for (Material m : materialsCatalog) {
            if (m.getName().contains(nameForFind)) {
                return m;
            }
        }
        return null;
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
            System.out.println("parsedproject = " + parsedProject);

            processParsedProject(parsedProject, Catalogs.getMaterialsListAvailable());

            this.currentProjectName = projectName;
            this.currentProjectPath = projectPath;
            this.projectJSONObject = parsedProject;

            checkMaterialSheetsPrices();

            projectWriter.saveProject(projectPath, projectName);
            return true;
        } catch (ProjectException e) {
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, e.getMessage()));
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

    public boolean projectSelected() {
        return projectJSONObject != null;
    }

    JSONObject getProjectJSONObject() {
        return projectJSONObject;
    }

    public String getCurrentProjectName() {
        return currentProjectName;
    }

    void setCurrentProjectName(String currentProjectName) {
        this.currentProjectName = currentProjectName;
    }

    public String getCurrentProjectPath() {
        return currentProjectPath;
    }

    void setCurrentProjectPath(String currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

}
