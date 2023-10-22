package ru.koreanika.project;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;
import ru.koreanika.Main;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.CheckSheetsPrices;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.PriceCoefficientsWindow;
import ru.koreanika.utils.Receipt.ReceiptManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ProjectHandler {

    /**
     * PROJECT UTILS
     */

    public static final String MATERIALS_XLS_PATH = "materials_1_2004.xls";
    public static final String ANALOGS_XLS_PATH = "material_analogs.xls";
    public static final String MATERIALS_IMG_PATH = "materials_resources/";
    public static final String DEPTHS_IMG_PATH = "depths_resources/";
    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";

    private static String curProjectPath;
    private static String curProjectName;
    private static JSONObject userProject;

    private static ProjectType projectType = ProjectType.TABLE_TYPE;

    private static BooleanProperty projectOpened = new SimpleBooleanProperty(false);

    private static Map<String, Double> materialsDeliveryFromManufacturer = new LinkedHashMap<>();// <Group name, Price in rub>
    private static List<Material> materialsListAvailable = new ArrayList<>();
    private static List<Material> materialsListInProject = new ArrayList<>();
    private static Material defaultMaterial = null;

    private static DoubleProperty priceMainCoefficient = new SimpleDoubleProperty(1);
    private static DoubleProperty priceMaterialCoefficient = new SimpleDoubleProperty(1);

    private static ArrayList<Edge> edgesUsesInProject = new ArrayList<>();
    private static ObservableList<Edge> edgesUsesInProjectObservable = FXCollections.observableList(edgesUsesInProject);

    private static ArrayList<Border> bordersUsesInProject = new ArrayList<>();
    private static ObservableList<Border> bordersUsesInProjectObservable = FXCollections.observableList(bordersUsesInProject);

    private static ArrayList<String> materialsUsesInProject = new ArrayList<>();
    private static ObservableList<String> materialsUsesInProjectObservable = FXCollections.observableList(materialsUsesInProject);

    private static ArrayList<String> depthsTableTopsUsesInProject = new ArrayList<>();
    private static ObservableList<String> depthsTableTopsUsesInProjectObservable = FXCollections.observableList(depthsTableTopsUsesInProject);

    private static ArrayList<String> depthsWallPanelsUsesInProject = new ArrayList<>();
    private static ObservableList<String> depthsWallPanelsUsesInProjectObservable = FXCollections.observableList(depthsWallPanelsUsesInProject);

    private static ArrayList<String> edgesHeightsUsesInProject = new ArrayList<>();
    private static ObservableList<String> edgesHeightsUsesInProjectObservable = FXCollections.observableList(edgesHeightsUsesInProject);

    private static ArrayList<String> bordersHeightsUsesInProject = new ArrayList<>();
    private static ObservableList<String> bordersHeightsUsesInProjectObservable = FXCollections.observableList(bordersHeightsUsesInProject);


    private static List<PlumbingElement> plumbingElementsList = new ArrayList<>();
    private static LinkedHashSet<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
//    private static int defaultEdgeHeight = 20;
//    private static int defaultBorderHeight = 20;

    public static double CUT_AREA_EDGE_WIDTH = 50;
    public static double CUT_AREA_BORDER_WIDTH = 30;

    protected static double commonShapeScale = 0.1;

    private static final EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);

    public static void projectHandlerInit() {
        FacadeXLSParser parser = new FacadeXLSParser(MATERIALS_XLS_PATH, ANALOGS_XLS_PATH);
        parser.populateLists(materialsListAvailable, plumbingElementsList, availablePlumbingTypes,
                materialsDeliveryFromManufacturer);
    }

    public static DoubleProperty getPriceMainCoefficient() {
        return priceMainCoefficient;
    }

    public static void setPriceMainCoefficient(double newCoeff) {
        double minMainCoefficient = PriceCoefficientsWindow.getMinMainCoefficient();
        double maxMainCoefficient = PriceCoefficientsWindow.getMaxMainCoefficient();

        if (newCoeff >= minMainCoefficient && newCoeff <= maxMainCoefficient) {
            ProjectHandler.priceMainCoefficient.set(newCoeff);
        } else {
            ProjectHandler.priceMainCoefficient.set(minMainCoefficient);
        }
    }

    public static DoubleProperty getPriceMaterialCoefficient() {
        return priceMaterialCoefficient;
    }

    public static void setPriceMaterialCoefficient(double newCoeff) {

        double minMaterialCoefficient = PriceCoefficientsWindow.getMinMaterialCoefficient();
        double maxMaterialCoefficient = PriceCoefficientsWindow.getMaxMaterialCoefficient();

        if (newCoeff >= minMaterialCoefficient && newCoeff <= maxMaterialCoefficient) {
            ProjectHandler.priceMaterialCoefficient.set(newCoeff);
        } else {
            ProjectHandler.priceMaterialCoefficient.set(minMaterialCoefficient);
        }

        System.out.println("PROJECTHANDLER SET MATERIAL COEFFICIENT = " + ProjectHandler.priceMaterialCoefficient.get());
    }

    public static double getCommonShapeScale() {
        return commonShapeScale;
    }

    public static String getCurProjectName() {
        return curProjectName;
    }

    public static String getCurProjectPath() {
        return curProjectPath;
    }

    public static JSONObject getUserProject() {
        return userProject;
    }

    public static void setProjectType(ProjectType projectType) {
        ProjectHandler.projectType = projectType;
    }

    public static ProjectType getProjectType() {
        return ProjectHandler.projectType;
    }

    public static void createProject(String projectName, String projectPath, ProjectType projectType) {
        setProjectType(projectType);

        materialsListInProject.clear();
        defaultMaterial = null;

        userProject = new JSONObject();

        curProjectName = projectName;
        curProjectPath = projectPath;

        if (!curProjectName.matches(".+\\.krnkproj$") && !curProjectName.matches(".+\\.kproj$")) {
            curProjectName += ".kproj";
            curProjectPath += ".kproj";
        }

        if (curProjectName.matches(".+\\.krnkproj$")) {
            curProjectName = curProjectName.replace("\\.krnkproj", ".kproj");
        }
        if (curProjectPath.matches(".+\\.krnkproj$")) {
            curProjectPath = curProjectPath.replace("\\.krnkproj", ".kproj");
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

        userProject.put("info", info);

        /** Project settings*/
        JSONObject projectSettings = new JSONObject();

        //project type:
        projectSettings.put("projectType", getProjectType().toString());

        //project coefficients for price:
        setPriceMaterialCoefficient(Main.materialCoefficient);
        setPriceMainCoefficient(Main.mainCoefficient);
        projectSettings.put("priceMainCoefficient", priceMainCoefficient.getValue());
        projectSettings.put("priceMaterialCoefficient", priceMaterialCoefficient.getValue());

        System.out.println("getPriceMaterialCoefficient() = " + getPriceMaterialCoefficient().doubleValue());
        System.out.println("getPriceMainCoefficient() = " + getPriceMainCoefficient().doubleValue());

        //material settings
        JSONObject materialSettings = new JSONObject();
        JSONArray materialsList = new JSONArray();
        for (Material material : materialsListInProject) {
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

        projectOpened.setValue(true);
    }

    public static Image receiptManagerSketchImage = null;

    public static Image getReceiptManagerSketchImage() {
        return receiptManagerSketchImage;
    }

    public static void setReceiptManagerSketchImage(Image receiptManagerSketchImage) {
        ProjectHandler.receiptManagerSketchImage = receiptManagerSketchImage;
    }

    private static String changeDektoneNameInProject(String project) {
        System.out.println("BEFORE : " + project);
        String res = project.replace("Dektone$D", "Кварцекерамический камень$D");

        res = project.replace("Массив$", "Массив_шпон$");
        //res = project.replace("Agglocemento", "Мраморный агломерат");
        System.out.println("AFTER : " + res);
        return res;
    }


    public static boolean openProject(String projectPath, String projectName) {
        double windowWidth = 0;
        double windowHeight = 0;
        double windowPosX = 0;
        double windowPosY = 0;

        String errorMessage = "";
        try {
            if (!projectName.matches(".+\\.krnkproj$") && !projectName.matches(".+\\.kproj$")) {
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Неизвестный тип файла!!!"));
                return false;
            }

            JSONObject parsedProject = null;
            JSONParser jsonParser = new JSONParser();

            curProjectName = projectName;
            curProjectPath = projectPath;

            try {
                boolean isZipProject = false;
                boolean isOldProject = false;
                boolean isENCRYPTEDProject = false;

                //check if it .zip type of project:
                {
                    try {
                        ZipFile zipFile = new ZipFile(curProjectPath);
                        zipFile.getName();
                        isZipProject = true;
                    } catch (IOException e) {
                        isZipProject = false;
                    }
                }

                // check if it old text format
                {
                    try {
                        File file = new File(projectPath);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

                        parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                        bufferedReader.close();
                        isOldProject = true;
                    } catch (ParseException e) {
                        isOldProject = false;
                    } catch (IOException e) {
                        isOldProject = false;
                    }
                }

                if (isOldProject) {
                    //open old type project .json
                    {
                        File file = new File(projectPath);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

                        parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                        String s = changeDektoneNameInProject(parsedProject.toString());
                        parsedProject = (JSONObject) jsonParser.parse(s);

                        bufferedReader.close();
                    }
                    System.out.println("Open project: " + curProjectPath + " (OLD TEXT type)");
                } else if (isZipProject) {
                    //open .zip
                    {
                        FileInputStream fileInputStream = new FileInputStream(curProjectPath);
                        ZipInputStream zis = new ZipInputStream(fileInputStream);
                        BufferedInputStream bis = new BufferedInputStream(zis);
                        ZipEntry zipEntry;
                        while ((zipEntry = zis.getNextEntry()) != null) {

                            if (zipEntry.getName().equals("mainInfo.json")) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                                String s = changeDektoneNameInProject(parsedProject.toString());
                                parsedProject = (JSONObject) jsonParser.parse(s);

                            } else if (zipEntry.getName().equals("receiptManagerSketchImage.png")) {
                                receiptManagerSketchImage = new Image(zis);
                                System.out.println("HAVE IMAGE");
                                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                //parsedProject = (JSONObject) jsonParser.parse(bufferedReader);
                            }
                        }
                        bis.close();
                    }

                    System.out.println("Open project: " + curProjectPath + " (ZIP type)");

                } else {
                    /* ADD CUSTOM DECRYPT */
                    {
                        FileInputStream fileInputStream = new FileInputStream(curProjectPath);

                        byte[] buf = new byte[fileInputStream.available()];
                        fileInputStream.read(buf);
                        fileInputStream.close();

                        for (int i = 0; i < buf.length; i++) {
                            buf[i] -= 76;
                        }

                        FileOutputStream fileOutputStream = new FileOutputStream(curProjectPath);
                        fileOutputStream.write(buf);

                        fileOutputStream.close();
                    }

                    //open .zip
                    {
                        FileInputStream fileInputStream = new FileInputStream(curProjectPath);
                        ZipInputStream zis = new ZipInputStream(fileInputStream);
                        BufferedInputStream bis = new BufferedInputStream(zis);
                        ZipEntry zipEntry;
                        while ((zipEntry = zis.getNextEntry()) != null) {

                            if (zipEntry.getName().equals("mainInfo.json")) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                                String s = changeDektoneNameInProject(parsedProject.toString());
                                parsedProject = (JSONObject) jsonParser.parse(s);

                            } else if (zipEntry.getName().equals("receiptManagerSketchImage.png")) {
                                receiptManagerSketchImage = new Image(zis);
                                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                //parsedProject = (JSONObject) jsonParser.parse(bufferedReader);
                            }
                        }
                        bis.close();
                    }

                    System.out.println("parsedproject = " + parsedProject);
                    System.out.println("parsedproject.toString() = " + parsedProject.toString());
                    System.out.println("Open project: " + curProjectPath + " (ZIP ENCRYPTED type)");
                }
            } catch (ParseException exio) {
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Поврежден mainInfo файл"));
                System.out.println("cant Parse project");
                return false;
            } catch (IOException exio) {
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Ошибка контента"));
                System.out.println("IOException");
                return false;
            }

            /** System data: */
            JSONObject info = (JSONObject) parsedProject.get("info");
            //window size:
            windowWidth = ((Double) info.get("windowWidth")).doubleValue();
            windowHeight = ((Double) info.get("windowHeight")).doubleValue();
            windowPosX = ((Double) info.get("windowPosX")).doubleValue();
            windowPosY = ((Double) info.get("windowPosY")).doubleValue();


            /** Project settings*/
            JSONObject projectSettings = (JSONObject) parsedProject.get("ProjectSettings");

            //material settings:
            JSONObject materialSettings = (JSONObject) projectSettings.get("materialSettings");
            JSONArray materialsList = (JSONArray) materialSettings.get("materialsList");
            JSONArray materialsNewList = (JSONArray) materialSettings.get("materialsNewList");

            setProjectType(ProjectType.valueOf((String) projectSettings.get("projectType")));

            /** Materials */
            //fill materials in project:
            if (materialsNewList != null) {
                for (Object obj : materialsNewList) {
                    JSONObject materialObject = (JSONObject) obj;

                    Material material = Material.getFromJson(materialObject);
                    if (material != null) {
                        materialsListInProject.add(material);
                    } else {
                        errorMessage = "Ошибка распаковки материала " + materialObject.get("name");
                    }
                }
            } else {
                for (Object str : materialsList) {
                    System.out.println("materialsList item = " + str);
                    if (Material.parseMaterial((String) str) == null) {
                        errorMessage = "Материал не существует: " + str;
                    }
                    materialsListInProject.add(Material.parseMaterial(((String) str)));
                }
            }

            // set default material, if not specified in project
            for (Material material : materialsListInProject) {
                if (((String) materialSettings.get("defaultMaterial")).contains(material.getName())) {
                    defaultMaterial = material;
                }
            }

            //project coefficients for price:
            Double priceMainCoefficientDouble = (Double) projectSettings.get("priceMainCoefficient");
            Double priceMaterialCoefficientDouble = (Double) projectSettings.get("priceMaterialCoefficient");
            if (priceMainCoefficientDouble == null || priceMaterialCoefficientDouble == null) {
                setPriceMainCoefficient(Main.mainCoefficient);
                setPriceMaterialCoefficient(Main.materialCoefficient);
            } else {
                setPriceMainCoefficient(priceMainCoefficientDouble.doubleValue());
                setPriceMaterialCoefficient(priceMaterialCoefficientDouble.doubleValue());
            }


            System.out.println("Default material = " + defaultMaterial.getName());

            if (getProjectType() == ProjectType.TABLE_TYPE) {
                /** Cut designer*/
                JSONObject cutDesigner = (JSONObject) parsedProject.get("CutDesigner");
                MainWindow.getCutDesigner().initFromJson(cutDesigner);

                JSONObject tableDesignerJSONObject = (JSONObject) parsedProject.get("TableDesigner");
                MainWindow.setTableDesigner(TableDesigner.initFromJSON(tableDesignerJSONObject));

                CutDesigner.getInstance().refreshCutView(); //need for correct calculate usesList in Material sheets
            } else if (getProjectType() == ProjectType.SKETCH_TYPE) {
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

            userProject = parsedProject;
            projectOpened.setValue(true);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Проект поврежден! (" + errorMessage + ")"));
            return false;
        }

        CutDesigner.getInstance().refreshCutView();

        //check MaterialSheetsPrices:
        boolean haveDifference = false;
        LinkedHashMap<Material, ArrayList<Material.MaterialSheet>> differenceMap = new LinkedHashMap<>();
        for (Material.MaterialSheet materialSheet : CutDesigner.getInstance().getCutPane().getUsedMaterialSheetsList()) {

            boolean theSame = CheckSheetsPrices.checkPrices(materialSheet.getMaterial(), materialSheet);
            //if sheets didn't use set it old price too:
            if (!theSame/* && materialSheet.getUsesList() != 0*/) {
                haveDifference = true;

                ArrayList<Material.MaterialSheet> sheetsList = differenceMap.get(materialSheet.getMaterial());
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

        saveProject(projectPath, projectName);
        return true;
    }


    public static void saveProject(String projectPath, String projectName) {
        if (userProject != null) {
            curProjectName = projectName;
            curProjectPath = projectPath;

            if (curProjectName.matches(".+\\.krnkproj$")) {
                curProjectName = curProjectName.replace("\\.krnkproj", ".kproj");
            }
            if (curProjectPath.matches(".+\\.krnkproj$")) {
                curProjectPath = curProjectPath.replace("\\.krnkproj", ".kproj");
            }
            if (!curProjectPath.matches(".+\\.kproj$")) {
                curProjectPath += ".kproj";
            }

            /** System data: */
            //window size:
            double windowWidth = Main.getMainScene().getWindow().getWidth();
            double windowHeight = Main.getMainScene().getWindow().getHeight();
            double windowPosX = Main.getMainScene().getWindow().getX();
            double windowPosY = Main.getMainScene().getWindow().getY();

            ((JSONObject) userProject.get("info")).put("windowWidth", windowWidth);
            ((JSONObject) userProject.get("info")).put("windowHeight", windowHeight);
            ((JSONObject) userProject.get("info")).put("windowPosX", windowPosX);
            ((JSONObject) userProject.get("info")).put("windowPosY", windowPosY);

            //date and time:
            ((JSONObject) userProject.get("info")).put("editDate", LocalDateTime.now().toString());
            ((JSONObject) userProject.get("info")).put("name", projectName);

            /** Project settings*/
            JSONObject projectSettings = new JSONObject();

            //material settings
            JSONObject materialSettings = new JSONObject();
            JSONArray materialsList = new JSONArray();
            JSONArray materialsNewList = new JSONArray();

            //project type:
            projectSettings.put("projectType", getProjectType().toString());

            //project coefficients for price:
            projectSettings.put("priceMainCoefficient", priceMainCoefficient.getValue());
            projectSettings.put("priceMaterialCoefficient", priceMaterialCoefficient.getValue());

            for (Material material : materialsListInProject) {

                materialsList.add(material.getName() + "#" + material.getDefaultDepth());//deprecated

                JSONObject materialObject = material.getJsonView();
                materialsNewList.add(materialObject);

            }
            materialSettings.put("materialsList", materialsList);
            materialSettings.put("materialsNewList", materialsNewList);
            materialSettings.put("defaultMaterial", defaultMaterial.getName() + "#" + defaultMaterial.getDefaultDepth());
            projectSettings.put("materialSettings", materialSettings);

            userProject.put("ProjectSettings", projectSettings);


            /** Table Designer*/
            if (getProjectType() == ProjectType.TABLE_TYPE) {
                JSONObject tableDesignerJSONObject = MainWindow.getTableDesigner().getJsonView();
                userProject.put("TableDesigner", tableDesignerJSONObject);
            } else if (getProjectType() == ProjectType.SKETCH_TYPE) {
                /** Sketch designer*/
                JSONObject sketchDesigner = new JSONObject();
                JSONArray sketchDesignerElements = new JSONArray();
                JSONArray sketchDesignerUnions = new JSONArray();
                JSONArray sketchDesignerDimensions = new JSONArray();

                for (int i = 0; i < SketchDesigner.getSketchShapesList().size(); i++) {
                    sketchDesignerElements.add(SketchDesigner.getSketchShapesList().get(i).getJsonView());
                }
                for (int i = 0; i < SketchDesigner.getSketchShapeUnionsList().size(); i++) {
                    sketchDesignerUnions.add(SketchDesigner.getSketchShapeUnionsList().get(i).getJsonView());
                }
                for (int i = 0; i < SketchDesigner.getAllDimensions().size(); i++) {
                    sketchDesignerDimensions.add(SketchDesigner.getAllDimensions().get(i).getJsonView());
                }

                sketchDesigner.put("elements", sketchDesignerElements);
                sketchDesigner.put("unions", sketchDesignerUnions);
                sketchDesigner.put("dimensions", sketchDesignerDimensions);
                userProject.put("SketchDesigner", sketchDesigner);
            }

            /** Cut designer*/
            userProject.put("CutDesigner", MainWindow.getCutDesigner().getJsonView());

            /** Receipt manager*/
            userProject.put("receiptManager", MainWindow.getReceiptManager().getJsonViewForSaveData());

            try {
                //create .zip
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(curProjectPath);
                    ZipOutputStream zos = new ZipOutputStream(fileOutputStream);

                    zos.putNextEntry(new ZipEntry("mainInfo.json"));
                    zos.write(userProject.toJSONString().getBytes(StandardCharsets.UTF_8));

                    //save sketchImage from ReceiptImage
                    ReceiptManager receiptManager = MainWindow.getReceiptManager();
                    if (receiptManager != null && receiptManager.getImageViewSketch() != null) {
                        BufferedImage bi = SwingFXUtils.fromFXImage(receiptManager.getImageViewSketch().getImage(), null);
                        zos.putNextEntry(new ZipEntry("receiptManagerSketchImage.png"));
                        ImageIO.write(bi, "png", zos);
                        System.out.println("SAVE IMAGE");
                    }

                    zos.flush();
                    zos.close();
                }

                /* ADD CUSTOM ENCRYPT */
                {
                    FileInputStream fileInputStream = new FileInputStream(curProjectPath);

                    byte[] buf = new byte[fileInputStream.available()];
                    fileInputStream.read(buf);
                    fileInputStream.close();

                    for (int i = 0; i < buf.length; i++) {
                        buf[i] += 76;
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(curProjectPath);
                    fileOutputStream.write(buf);

                    fileOutputStream.close();
                }

                System.out.println("Save project path:" + curProjectPath);
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.SUCCESS, "Проект сохранен"));

            } catch (IOException ex) {
                System.out.println("CAN'T Save project path:" + curProjectPath);
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Ошибка директории"));
            }
        } else {
            System.err.println("Try to save user project. Error: userProject object = null");
            //MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Нельзя сохранить пустой проект");
        }
    }

    public static void closeProject() {
        saveProject(curProjectPath, curProjectName);
        if (userProject != null) {
            userProject.clear();
        }

        MainWindow.setCutDesigner(null);
        MainWindow.setSketchDesigner(null);
        userProject = null;
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

        AdditionalFeature.createdFeaturesNumbersList.clear();

        defaultMaterial = null;
        projectOpened.setValue(false);
    }

    public static boolean isFileZipped(File file) {
        try {
            return new ZipInputStream(new FileInputStream(file)).getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean encodeProjectFile(File srcFile, File dstFile) throws IOException {
        if (!isFileZipped(srcFile)) {
            return false;
        }
        /* ADD CUSTOM ENCRYPT */
        FileInputStream fileInputStream = new FileInputStream(srcFile);

        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();

        for (int i = 0; i < buf.length; i++) {
            buf[i] += 76;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(buf);

        fileOutputStream.close();

        return true;
    }

    public static boolean decodeProjectFile(File srcFile, File dstFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(srcFile);

        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();

        for (int i = 0; i < buf.length; i++) {
            buf[i] -= 76;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(buf);

        fileOutputStream.close();

        if (!isFileZipped(dstFile)) {

            if (!dstFile.getPath().equals(srcFile.getPath())) {
                dstFile.delete();
            } else {
                //copy input file to output file
                FileOutputStream fOutputStream = new FileOutputStream(dstFile);
                FileInputStream fInputStream = new FileInputStream(srcFile);
                for (int i = 0; i < fInputStream.available(); i++) {
                    fOutputStream.write(fInputStream.read());
                }
                fInputStream.close();
                fOutputStream.close();
            }
            return false;
        }
        return true;
    }

    public static BooleanProperty getProjectOpenedProperty() {
        return projectOpened;
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

    public static ArrayList<Border> getBordersInProject() {
        return bordersUsesInProject;
    }

    public static ObservableList<Border> getBordersUsesInProjectObservable() {
        return bordersUsesInProjectObservable;
    }

    public static ArrayList<Edge> getEdgesInProject() {
        return edgesUsesInProject;
    }

    public static ObservableList<Edge> getEdgesUsesInProjectObservable() {
        return edgesUsesInProjectObservable;
    }

    public static ArrayList<String> getMaterialsUsesInProject() {
        return materialsUsesInProject;
    }

    public static ObservableList<String> getMaterialsUsesInProjectObservable() {
        return materialsUsesInProjectObservable;
    }

    public static ArrayList<String> getDepthsTableTopsUsesInProject() {
        return depthsTableTopsUsesInProject;
    }

    public static ObservableList<String> getDepthsTableTopsUsesInProjectObservable() {
        return depthsTableTopsUsesInProjectObservable;
    }

    public static ArrayList<String> getDepthsWallPanelsUsesInProject() {
        return depthsWallPanelsUsesInProject;
    }

    public static ObservableList<String> getDepthsWallPanelsUsesInProjectObservable() {
        return depthsWallPanelsUsesInProjectObservable;
    }

    public static ArrayList<String> getEdgesHeightsUsesInProject() {
        return edgesHeightsUsesInProject;
    }

    public static ObservableList<String> getEdgesHeightsUsesInProjectObservable() {
        return edgesHeightsUsesInProjectObservable;
    }

    public static ArrayList<String> getBordersHeightsUsesInProject() {
        return bordersHeightsUsesInProject;
    }

    public static ObservableList<String> getBordersHeightsUsesInProjectObservable() {
        return bordersHeightsUsesInProjectObservable;
    }

    public static void setMaterialsListInProject(ArrayList<Material> materialsListInProject) {
        ProjectHandler.materialsListInProject = materialsListInProject;
    }

    public static Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public static Map<String, Double> getMaterialsDeliveryFromManufacturer() {
        return materialsDeliveryFromManufacturer;
    }

    public static void setDefaultMaterialRAW(Material defaultMaterial) {
        ProjectHandler.defaultMaterial = defaultMaterial;
    }

    public static void setDefaultMaterial(Material defaultMaterial) {
        ProjectHandler.defaultMaterial = defaultMaterial;

        for (SketchShape sketchShape : SketchDesigner.getSketchShapesList()) {
            if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
                sketchShape.shapeSettingsSaveBtnClicked();//this will update default materials
            }
        }

        if (ProjectHandler.getProjectType() == ProjectType.TABLE_TYPE) {
            TableDesigner.updateMaterialsInProject();
        }
    }

    public static void openProjectFromArguments(String s) {
        File file = new File(s);
        MainWindow.projectOpenedLogic(file);
    }

}
