package utils;


import Common.PlumbingElementForSale.PlumbingElement;
import Common.PlumbingElementForSale.PlumbingElementXlsReader;
import Common.Material.Material;


import Common.PlumbingElementForSale.PlumbingType;
import Exceptions.ParseXLSFileException;
import cutDesigner.CutDesigner;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sketchDesigner.Edge.Border;
import sketchDesigner.Edge.Edge;
import sketchDesigner.Features.AdditionalFeature;
import sketchDesigner.Shapes.SketchShape;
import sketchDesigner.SketchDesigner;

import tableDesigner.TableDesigner;
import utils.Receipt.ReceiptManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
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

    public static final String MATERIALS_LIST_PATH = "materials_1_2004.xls";
    public static final String ANALOGS_LIST_PATH = "material_analogs.xls";
    public static final String MATERIALS_IMG_PATH = "materials_resources/";
    public static final String DEPTHS_IMG_PATH = "depths_resources/";
    public static final String BORDERS_IMG_PATH = "borders_resources/";
    public static final String EDGES_IMG_PATH = "edges_resources/";

    private static String curProjectPath;
    private static String curProjectName;
    private static JSONObject userProject;

    private static ProjectType projectType = ProjectType.TABLE_TYPE;

    private static BooleanProperty projectOpened = new SimpleBooleanProperty(false);

    private static Map<String, Double> materialsDeliveryFromManufacture = new LinkedHashMap<>();// <Group name, Price in rub>
    private static ArrayList<Material> materialsListAvailable;
    private static ArrayList<Material> materialsListInProject = new ArrayList<>();
    private static Material defaultMaterial;

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


    private static ArrayList<PlumbingElement> plumbingElementsList;
    private static LinkedHashSet<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
//    private static int defaultEdgeHeight = 20;
//    private static int defaultBorderHeight = 20;

    public static double CUT_AREA_EDGE_WIDTH = 50;
    public static double CUT_AREA_BORDER_WIDTH = 30;

    protected static double commonShapeScale = 0.1;

    public static void  projectHandlerInit() throws ParseXLSFileException {
        materialsListAvailable = fillMaterialsList(MATERIALS_LIST_PATH);

    }



    public static DoubleProperty getPriceMainCoefficient() {
        return priceMainCoefficient;
    }

    public static void setPriceMainCoefficient(double newCoeff) {
//        if(Main.appOwner.toUpperCase().equals("ZETTA")){
        double minMainCoefficient = PriceCoefficientsWindow.getMinMainCoefficient();
        double maxMainCoefficient = PriceCoefficientsWindow.getMaxMainCoefficient();

        if(newCoeff >= minMainCoefficient && newCoeff <= maxMainCoefficient){
            ProjectHandler.priceMainCoefficient.set(newCoeff);
        }else{
            ProjectHandler.priceMainCoefficient.set(minMainCoefficient);
        }

//        if(UserPreferences.getInstance().getSelectedApp() == AppType.ZETTA){
//            ProjectHandler.priceMainCoefficient.set(2);
////        }else if(Main.appOwner.toUpperCase().equals("KOREANIKA")){
//        }else{
//            ProjectHandler.priceMainCoefficient.set(priceMainCoefficient);
//        }



    }

    public static DoubleProperty getPriceMaterialCoefficient() {
        return priceMaterialCoefficient;
    }

    public static void setPriceMaterialCoefficient(double newCoeff) {

        double minMaterialCoefficient = PriceCoefficientsWindow.getMinMaterialCoefficient();
        double maxMaterialCoefficient = PriceCoefficientsWindow.getMaxMaterialCoefficient();

        if(newCoeff >= minMaterialCoefficient && newCoeff <= maxMaterialCoefficient){
            ProjectHandler.priceMaterialCoefficient.set(newCoeff);
        }else{
            ProjectHandler.priceMaterialCoefficient.set(minMaterialCoefficient);
        }

        System.out.println("PROJECTHANDLER SET MATERIAL COEFFICIENT = " + ProjectHandler.priceMaterialCoefficient.get());

//        if(UserPreferences.getInstance().getSelectedApp() == AppType.ZETTA){
//            ProjectHandler.priceMaterialCoefficient.set(1);
//            //ProjectHandler.priceMaterialCoefficient.set(priceMaterialCoefficient);
//
//        }else{
//            ProjectHandler.priceMaterialCoefficient.set(priceMaterialCoefficient);
//        }
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

    public static void setCurProjectName(String curProjectName) {

        ProjectHandler.curProjectName = curProjectName;
        if (!curProjectName.matches(".+\\.krnkproj$")) {
            ProjectHandler.curProjectName += ".krnkproj";

        }

    }

    public static void setCurProjectPath(String curProjectPath) {
        ProjectHandler.curProjectPath = curProjectPath;

        if (curProjectPath.matches(".+\\.krnkproj$")) {
            ProjectHandler.curProjectPath += ".krnkproj";
        }

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
            curProjectName = curProjectName.replace("\\.krnkproj",".kproj");
        }
        if (curProjectPath.matches(".+\\.krnkproj$")) {
            curProjectPath = curProjectPath.replace("\\.krnkproj",".kproj");
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


//        try (FileWriter writer = new FileWriter(curProjectPath)){
//            writer.write(userProject.toJSONString());
//            writer.flush();
//        } catch(IOException ex) {
//
//        }

        projectOpened.setValue(true);

    }

    public static Image receiptManagerSketchImage = null;

    public static Image getReceiptManagerSketchImage() {
        return receiptManagerSketchImage;
    }

    public static void setReceiptManagerSketchImage(Image receiptManagerSketchImage) {
        ProjectHandler.receiptManagerSketchImage = receiptManagerSketchImage;
    }

    private static String changeDektoneNameInProject(String project){
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
                MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Неизвестный тип файла!!!");
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
                    try{
                        File file = new File(projectPath);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

                        parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                        bufferedReader.close();
                        isOldProject = true;
                    }catch(ParseException e){
                        isOldProject = false;
                    }catch(IOException e){
                        isOldProject = false;
                    }
                }

                if(isOldProject){

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
                }else if(isZipProject){

                    //open .zip
                    {
                        FileInputStream fileInputStream = new FileInputStream(curProjectPath);
                        ZipInputStream zis = new ZipInputStream(fileInputStream);
                        BufferedInputStream bis = new BufferedInputStream(zis);
                        ZipEntry zipEntry;
                        while((zipEntry = zis.getNextEntry()) != null){

                            if(zipEntry.getName().equals("mainInfo.json")){
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                                String s = changeDektoneNameInProject(parsedProject.toString());
                                parsedProject = (JSONObject) jsonParser.parse(s);

                            }else if(zipEntry.getName().equals("receiptManagerSketchImage.png")){
                                receiptManagerSketchImage = new Image(zis);
                                System.out.println("HAVE IMAGE");
                                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                //parsedProject = (JSONObject) jsonParser.parse(bufferedReader);
                            }
                        }
                        bis.close();
                    }

                    System.out.println("Open project: " + curProjectPath + " (ZIP type)");

                }else{
                    /* ADD CUSTOM DECRYPT */
                    {
                        FileInputStream fileInputStream = new FileInputStream(curProjectPath);

                        byte[] buf = new byte[fileInputStream.available()];
                        fileInputStream.read(buf);
                        fileInputStream.close();

                        for(int i =0;i< buf.length;i++){
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
                        while((zipEntry = zis.getNextEntry()) != null){

                            if(zipEntry.getName().equals("mainInfo.json")){
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, "UTF8"));
                                parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                                String s = changeDektoneNameInProject(parsedProject.toString());
                                parsedProject = (JSONObject) jsonParser.parse(s);

                            }else if(zipEntry.getName().equals("receiptManagerSketchImage.png")){
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
                MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Поврежден mainInfo файл");
                System.out.println("cant Parse project");
                return false;
            } catch (IOException exio) {
                MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ошибка контента");
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
            if(materialsNewList != null){

                for (Object obj : materialsNewList) {
                    JSONObject materialObject = (JSONObject) obj;

                    Material material = Material.getFromJson(materialObject);
                    if(material == null) errorMessage = "Ошибка распаковки материала " + materialObject.get("name");

                    materialsListInProject.add(material);
                    material.getMaterialImage().startDownloadingImages();

//                    System.out.println("ADDED IN  PROJECT:");
//                    System.out.println(material.getName());
                }

            }else{
                for (Object str : materialsList) {
                    System.out.println("materialsList item = " + str);
                    if(Material.parseMaterial((String) str) == null) errorMessage = "Материал не существует: " + str;
                    materialsListInProject.add(Material.parseMaterial(((String) str)));
                }
            }


            //set default material:

//            System.out.println("materialsListInProject = " + materialsListInProject);
//            System.out.println("DEFAULT MATERIAL:");
//            System.out.println((String) materialSettings.get("defaultMaterial"));

            for (Material material : materialsListInProject) {
//                System.out.println(material);
//                System.out.println(material.getName());
//                System.out.println(materialSettings.get("defaultMaterial"));


                if (((String) materialSettings.get("defaultMaterial")).indexOf(material.getName()) != -1) {
                    defaultMaterial = material;
                }

//                System.out.println(material);
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

//            MainWindow.setDefaultMaterialLabel(defaultMaterial.getName());


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
            MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Проект поврежден! (" +  errorMessage + ")");
            return false;
        }


        if (windowWidth > Screen.getPrimary().getBounds().getWidth() || windowHeight > Screen.getPrimary().getBounds()
                .getHeight()) {

        } else {
//            Main.getMainScene().getWindow().setWidth(windowЦшвер);
//            Main.getMainScene().getWindow().setHeight(windowHeight);
//            Main.getMainScene().getWindow().setX(windowPosX);
//            Main.getMainScene().getWindow().setY(windowPosY);
        }


        CutDesigner.getInstance().refreshCutView();

        //check MaterialSheetsPrices:
        boolean haveDifference = false;
        LinkedHashMap<Material, ArrayList<Material.MaterialSheet>> differenceMap = new LinkedHashMap<>();
        for(Material.MaterialSheet materialSheet : CutDesigner.getInstance().getCutPane().getUsedMaterialSheetsList()){

            boolean theSame = CheckSheetsPrices.checkPrices(materialSheet.getMaterial(), materialSheet);
//
//            if(!theSame && materialSheet.getUsesList() == 0){
//                //set actualPrice:
//                CheckSheetsPrices.setActualPrices(materialSheet.getMaterial(), materialSheet);
//            }
            //if sheets didn't use set it old price too:
            if(!theSame/* && materialSheet.getUsesList() != 0*/){
                haveDifference = true;

                ArrayList<Material.MaterialSheet> sheetsList = differenceMap.get(materialSheet.getMaterial());
                if(sheetsList == null){
                    sheetsList = new ArrayList<>();
                }
                sheetsList.add(materialSheet);
                differenceMap.put(materialSheet.getMaterial(), sheetsList);

            }
            //System.out.println("THE SAME PRICE:" + theSame);
            //System.out.println("materialSheet.getUsesList()" + materialSheet.getUsesList());
        }



        //check coefficients in sketchShapes

        if(haveDifference){
            CheckSheetsPrices.showInfoWindow(Main.getMainScene(), differenceMap);
        }

        saveProject(projectPath,projectName);


        return true;
    }


    public static void saveProject(String projectPath, String projectName) {
        if (userProject != null) {
            curProjectName = projectName;
            curProjectPath = projectPath;


            if (curProjectName.matches(".+\\.krnkproj$")) {
                //System.out.println("curProjectName change .krnkproj to .kproj");
                //curProjectName.replaceAll("\\.krnkproj", ".kproj");

                //curProjectName = curProjectName.split("\\.krnkproj")[0] + ".kproj";
                curProjectName = curProjectName.replace("\\.krnkproj",".kproj");
            }

            if (curProjectPath.matches(".+\\.krnkproj$")) {

                //curProjectPath.replace("krnkproj", "kproj");

                //curProjectPath = curProjectPath.split("\\.krnkproj")[0] + ".kproj";

                curProjectPath = curProjectPath.replace("\\.krnkproj",".kproj");

                //System.out.println("curProjectPath change .krnkproj to .kproj = " + curProjectPath);

            }

            if (!curProjectPath.matches(".+\\.kproj$")) {
                //System.out.println("curProjectPath ADD .kproj");
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
//            materialSettings.put("defaultEdgeHeight", defaultEdgeHeight);
//            materialSettings.put("defaultBorderHeight", defaultBorderHeight);
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
                /*JSONObject cutDesigner = new JSONObject();
                JSONArray cutDesignerElements = new JSONArray();*/
            userProject.put("CutDesigner", MainWindow.getCutDesigner().getJsonView());


            /** Receipt manager*/
            userProject.put("receiptManager", MainWindow.getReceiptManager().getJsonViewForSaveData());

            //          try (FileWriter writer = new FileWriter(curProjectPath)){
            try {


                //File file = new File(curProjectPath);
//                File file = new File("mainInfo.json");
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
//                bufferedWriter.write(userProject.toJSONString());
                //bufferedWriter.flush();
                //bufferedWriter.close();

                //create .zip
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(curProjectPath);
                    ZipOutputStream zos = new ZipOutputStream(fileOutputStream);
                    //BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zos);

                    zos.putNextEntry(new ZipEntry("mainInfo.json"));
                    zos.write(userProject.toJSONString().getBytes(StandardCharsets.UTF_8));


                    //zos.putNextEntry(new ZipEntry("images"));

                    //save sketchImage from ReceiptImage
                    ReceiptManager receiptManager = MainWindow.getReceiptManager();
                    if(receiptManager != null && receiptManager.getImageViewSketch() != null){
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

                    for(int i =0;i< buf.length;i++){
                        buf[i] += 76;
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(curProjectPath);
                    fileOutputStream.write(buf);

                    fileOutputStream.close();
                }


                System.out.println("Save project path:" + curProjectPath);
                MainWindow.showInfoMessage(InfoMessage.MessageType.SUCCESS, "Проект сохранен");

            } catch (IOException ex) {
                System.out.println("CAN'T Save project path:" + curProjectPath);
                MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ошибка дирректории");
            }


        } else {
            System.err.println("Try to save user project. Error: userProject object = null");
            //MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Нельзя сохранить пустой проект");
        }
    }

    public static void closeProject() {
        saveProject(curProjectPath, curProjectName);
        if (userProject != null) userProject.clear();


        /*devicesTreeRootElement.getChildren().clear();
        devicesInProject.clear();
        devicesIds.clear();
		externalAddresses.clear();
		internalAddresses.clear();
        addressesRootElement.getChildren().clear();
        addressesNumbers.clear();*/
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

    public static boolean isFileZipped(File file){
        try {
            return new ZipInputStream(new FileInputStream(file)).getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean encodeProjectFile(File srcFile, File dstFile) throws IOException {

        if(!isFileZipped(srcFile)){
           return false;
        }
        /* ADD CUSTOM ENCRYPT */
        FileInputStream fileInputStream = new FileInputStream(srcFile);

        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();

        for(int i =0;i< buf.length;i++){
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

        for(int i =0;i< buf.length;i++){
            buf[i] -= 76;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(buf);

        fileOutputStream.close();

        if(!isFileZipped(dstFile)){

            if(!dstFile.getPath().equals(srcFile.getPath())){
                dstFile.delete();
            }else{
                //copy input file to output file
                FileOutputStream fOutputStream = new FileOutputStream(dstFile);
                FileInputStream fInputStream = new FileInputStream(srcFile);
                for(int i =0;i< fInputStream.available();i++){
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

    public static ArrayList<PlumbingElement> getPlumbingElementsList() {
        return plumbingElementsList;
    }

    public static LinkedHashSet<PlumbingType> getAvailablePlumbingTypes() {
        return availablePlumbingTypes;
    }

    public static ArrayList<Material> fillMaterialsList(String name) throws ParseXLSFileException {

        ArrayList<Material> list = new ArrayList<Material>();

        String result = "";
        InputStream in = null;
        HSSFWorkbook wb = null;

        File xlsFile = new File(name);
        Biff8EncryptionKey.setCurrentUserPassword("9031713970");

        try {
            in = new FileInputStream(xlsFile);

            //in = new InputStreamReader(new FileInputStream(name), "UTF8");
            wb = new HSSFWorkbook(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        plumbingElementsList = PlumbingElementXlsReader.fillDataFromXls(wb);

        availablePlumbingTypes.clear();
        for(PlumbingElement pe : plumbingElementsList){
            if (pe.isAvailable()) availablePlumbingTypes.add(pe.getPlumbingType());
        }

        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();
        it.next();
        it.next();
        it.next();
        //it.next();
        int position = 2;

        int cell = 0;
        try {

            while (it.hasNext()) {
                position++;
                Row row = it.next();
                if (row.getCell(0).getStringCellValue().equals("") || row.getCell(5).getStringCellValue().equals(""))
                    break;
                int width = 0;
                int height = 0;

                if(!row.getCell(5).getStringCellValue().equals("-")){
                    String[] size = row.getCell(5).getStringCellValue().split("-");
                    width = Integer.parseInt(size[0]);
                    height = Integer.parseInt(size[1]);
                }

                int minWidth = 0;
                int minHeight = 0;
                if(!row.getCell(6).getStringCellValue().equals("-")){
                    String[] minSize = row.getCell(6).getStringCellValue().split("-");
                    minWidth = Integer.parseInt(minSize[0]);
                    minHeight = Integer.parseInt(minSize[1]);
                }


                String imgPath = "no_img.png";


                if (row.getCell(8) != null && !row.getCell(8).getStringCellValue().equals("")) {
                    imgPath = row.getCell(8).getStringCellValue();

                } else {
                    imgPath = "no_img.png";
                }

                //depths:
                ArrayList<String> depths = new ArrayList<>();
                //if(!row.getCell(9).getStringCellValue().equals("-")){

                    if (row.getCell(0).getStringCellValue().indexOf("Кварцевый агломерат") != -1 ||
                            row.getCell(0).getStringCellValue().indexOf("Натуральный камень") != -1 ||
                            row.getCell(0).getStringCellValue().indexOf("Dektone") != -1 ||
                            row.getCell(0).getStringCellValue().indexOf("Мраморный агломерат") != -1 ||
                            row.getCell(0).getStringCellValue().indexOf("Кварцекерамический камень") != -1) {

                        if(!row.getCell(10).getStringCellValue().equals("-")) {

                            String[] depthAndPriceArr1 = row.getCell(10).getStringCellValue().split("/");

                            for (String depthAndPrice : depthAndPriceArr1) {
                                int depth = 0;
                                int price = 0;


                                String[] arr = depthAndPrice.split("=");

                                price = (int) (Double.parseDouble(arr[1].replace(",", ".")) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));
                                depths.add("" + depth);
                            }
                        }else{

                        }
                    }else if(row.getCell(0).getStringCellValue().indexOf("Массив") != -1 ||
                            row.getCell(0).getStringCellValue().indexOf("Массив_шпон") != -1){
                        if(!row.getCell(11).getStringCellValue().equals("-")) {

                            String[] depthAndPriceArr1 = row.getCell(11).getStringCellValue().split("/");

                            for (String depthAndPrice : depthAndPriceArr1) {
                                int depth = 0;
                                int price = 0;


                                String[] arr = depthAndPrice.split("=");
                                if(arr[1].contains(",")){
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));
                                depths.add("" + depth);
                            }
//                            System.out.println(depths);
                        }
                    } else {
                        depths.add("12");
                    }

                //}



//                if (depths.size() == 0) continue;
                Material material = new Material(row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(2).getStringCellValue(),
                        row.getCell(3).getStringCellValue(), width, height, imgPath, depths);


                material.setCurrency(row.getCell(9).getStringCellValue().toUpperCase());




                //prices:

                Map<Integer, Integer> tableTopDepthsAndPricesMap = material.getTableTopDepthsAndPrices();
                Map<Integer, Integer> wallPanelDepthsAndPricesMap = material.getWallPanelDepthsAndPrices();
                Map<Integer, Integer> windowSillDepthsAndPricesMap = material.getWindowSillDepthsAndPrices();
                Map<Integer, Integer> footDepthsAndPricesMap = material.getFootDepthsAndPrices();

                tableTopDepthsAndPricesMap.clear();
                wallPanelDepthsAndPricesMap.clear();
                windowSillDepthsAndPricesMap.clear();
                footDepthsAndPricesMap.clear();

                ArrayList<Double> tableTopCoefficientList = material.getTableTopCoefficientList();
                ArrayList<Double> wallPanelCoefficientList = material.getWallPanelCoefficientList();
                ArrayList<Double> windowSillCoefficientList = material.getWindowSillCoefficientList();
                ArrayList<Double> footCoefficientList = material.getFootCoefficientList();

                tableTopCoefficientList.clear();
                wallPanelCoefficientList.clear();
                windowSillCoefficientList.clear();
                footCoefficientList.clear();
                // coefficient for TableTop:
                if (row.getCell(11).getCellType() != CellType.NUMERIC) tableTopCoefficientList.add(Double.valueOf(1.0));
                else tableTopCoefficientList.add(Double.valueOf(row.getCell(11).getNumericCellValue()));

                if (row.getCell(12).getCellType() != CellType.NUMERIC) tableTopCoefficientList.add(Double.valueOf(1.0));
                else tableTopCoefficientList.add(Double.valueOf(row.getCell(12).getNumericCellValue()));

                if (row.getCell(13).getCellType() != CellType.NUMERIC) tableTopCoefficientList.add(Double.valueOf(1.0));
                else tableTopCoefficientList.add(Double.valueOf(row.getCell(13).getNumericCellValue()));

                if (row.getCell(14).getCellType() != CellType.NUMERIC) tableTopCoefficientList.add(Double.valueOf(1.0));
                else tableTopCoefficientList.add(Double.valueOf(row.getCell(14).getNumericCellValue()));

                if (row.getCell(15).getCellType() != CellType.NUMERIC) tableTopCoefficientList.add(Double.valueOf(1.0));
                else tableTopCoefficientList.add(Double.valueOf(row.getCell(15).getNumericCellValue()));

                // coefficient for WallPanel:

                if (row.getCell(16).getCellType() != CellType.NUMERIC) wallPanelCoefficientList.add(Double.valueOf(1.0));
                else wallPanelCoefficientList.add(Double.valueOf(row.getCell(16).getNumericCellValue()));

                if (row.getCell(17).getCellType() != CellType.NUMERIC) wallPanelCoefficientList.add(Double.valueOf(1.0));
                else wallPanelCoefficientList.add(Double.valueOf(row.getCell(17).getNumericCellValue()));

                if (row.getCell(18).getCellType() != CellType.NUMERIC) wallPanelCoefficientList.add(Double.valueOf(1.0));
                else wallPanelCoefficientList.add(Double.valueOf(row.getCell(18).getNumericCellValue()));

                // coefficient for WindowSill:
                if (row.getCell(19).getCellType() != CellType.NUMERIC) windowSillCoefficientList.add(Double.valueOf(1.0));
                else windowSillCoefficientList.add(Double.valueOf(row.getCell(19).getNumericCellValue()));

                if (row.getCell(20).getCellType() != CellType.NUMERIC) windowSillCoefficientList.add(Double.valueOf(1.0));
                else windowSillCoefficientList.add(Double.valueOf(row.getCell(20).getNumericCellValue()));

                if (row.getCell(21).getCellType() != CellType.NUMERIC) windowSillCoefficientList.add(Double.valueOf(1.0));
                else windowSillCoefficientList.add(Double.valueOf(row.getCell(21).getNumericCellValue()));

                if (row.getCell(22).getCellType() != CellType.NUMERIC) windowSillCoefficientList.add(Double.valueOf(1.0));
                else windowSillCoefficientList.add(Double.valueOf(row.getCell(22).getNumericCellValue()));

                if (row.getCell(23).getCellType() != CellType.NUMERIC) windowSillCoefficientList.add(Double.valueOf(1.0));
                else windowSillCoefficientList.add(Double.valueOf(row.getCell(23).getNumericCellValue()));

                // coefficient for Foot:
                if (row.getCell(24).getCellType() != CellType.NUMERIC) footCoefficientList.add(Double.valueOf(1.0));
                else footCoefficientList.add(Double.valueOf(row.getCell(24).getNumericCellValue()));

                if (row.getCell(25).getCellType() != CellType.NUMERIC) footCoefficientList.add(Double.valueOf(1.0));
                else footCoefficientList.add(Double.valueOf(row.getCell(25).getNumericCellValue()));

                if (row.getCell(26).getCellType() != CellType.NUMERIC) footCoefficientList.add(Double.valueOf(1.0));
                else footCoefficientList.add(Double.valueOf(row.getCell(26).getNumericCellValue()));

                if (row.getCell(27).getCellType() != CellType.NUMERIC) footCoefficientList.add(Double.valueOf(1.0));
                else footCoefficientList.add(Double.valueOf(row.getCell(27).getNumericCellValue()));

                if (row.getCell(28).getCellType() != CellType.NUMERIC) footCoefficientList.add(Double.valueOf(1.0));
                else footCoefficientList.add(Double.valueOf(row.getCell(28).getNumericCellValue()));

//                System.out.println(material.getName() + " tableTopCoefficientList = " + tableTopCoefficientList
//                        + " row.getCell(10).getCellType() = " + row.getCell(10).getCellType()
//                        + ((row.getCell(10).getCellType() == CellType.FORMULA)? row.getCell(10).getNumericCellValue(): ""));

//                System.out.println(material.getMainType());
                if(!row.getCell(5).getStringCellValue().equals("-")){
                    if (material.getMainType().indexOf("Кварцевый агломерат") != -1 ||
                            material.getMainType().indexOf("Натуральный камень") != -1 ||
                            material.getMainType().indexOf("Dektone") != -1 ||
                            material.getMainType().indexOf("Кварцекерамический камень") != -1 ||
                            material.getMainType().indexOf("Мраморный агломерат") != -1) {

                        //depths and prices:
                        String[] depthAndPriceArr = row.getCell(10).getStringCellValue().split("/");

                        for (String depthAndPrice : depthAndPriceArr) {
                            int depth = 0;
                            int price = 0;



                            String[] arr = depthAndPrice.split("=");
                            //System.out.println(arr[1]);
                            price = (int) (Double.parseDouble(arr[1].replace(",", ".")) * 100);
                            depth = Integer.parseInt(arr[0].substring(1));
//                            System.out.print("price = " + price);
                            price = (int) (price / ((width * height) / 1000000.0));
//                            System.out.print("((width * height) / 1000000) = " + ((width * height) / 1000000.0));
//                            System.out.print("(price / ((width * height) / 1000000)) = " + (price / ((width * height) / 1000000)));
//                            System.out.print("price = " + price);

                            tableTopDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            wallPanelDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            windowSillDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            footDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));

                        }

                    }else if(material.getMainType().indexOf("Массив") != -1 ||
                            material.getMainType().indexOf("Массив_шпон") != -1){

                        //for TableTop:
                        if(!row.getCell(11).getStringCellValue().equals("-")){
                            String[] depthAndPriceArr = row.getCell(11).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;

                                String[] arr = depthAndPrice.split("=");
                                if(arr[1].contains(",")){
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));

                                tableTopDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            }
                        }

                        //for WallPanel:
                        if(!row.getCell(16).getStringCellValue().equals("-")){
                            String[] depthAndPriceArr = row.getCell(16).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;

                                String[] arr = depthAndPrice.split("=");
                                if(arr[1].contains(",")){
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));

                                wallPanelDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            }
                        }

                        //for WindowSill:
                        if(!row.getCell(19).getStringCellValue().equals("-")){
                            String[] depthAndPriceArr = row.getCell(19).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;

                                String[] arr = depthAndPrice.split("=");
                                if(arr[1].contains(",")){
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));

                                windowSillDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            }
                        }

                        //for Foot:
                        if(!row.getCell(24).getStringCellValue().equals("-")){

                            String[] depthAndPriceArr = row.getCell(24).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;
                                String[] arr = depthAndPrice.split("=");
                                if(arr[1].contains(",")){
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));
                                footDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));
                            }


                        }

                    } else {

                        //for TableTop:
                        int price = (int) (((double) row.getCell(11).getNumericCellValue()) * 100);
                        int depth = 12;
                        tableTopDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));


                        //for WallPanel:
                        price = (int) (((double) row.getCell(16).getNumericCellValue()) * 100);
                        depth = 12;
                        wallPanelDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));

                        //for WindowSill:
                        price = (int) (((double) row.getCell(19).getNumericCellValue()) * 100);
                        depth = 12;
                        windowSillDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));

                        //for Foot:
                        price = (int) (((double) row.getCell(24).getNumericCellValue()) * 100);
                        depth = 12;
                        footDepthsAndPricesMap.put(Integer.valueOf(depth), Integer.valueOf(price));

                    }
                }



//            System.out.println("windowSillDepthsAndPrices = " + windowSillDepthsAndPrices);
//            System.out.println("footDepthsAndPrices = " + footDepthsAndPrices);


                material.setCalculationType((int) row.getCell(29).getNumericCellValue());
                material.setMinMaterialSize(minWidth, minHeight);

                material.setMinCountSlabs((int) row.getCell(30).getNumericCellValue());

                /** add sinks: */
                material.getAvailableSinkTypes().clear();
                material.getAvailableSinkModels().clear();


                if(!row.getCell(31).getStringCellValue().equals("-")){
                    String availableSinks = row.getCell(31).getStringCellValue();
                    String[] availableSinksArr = availableSinks.split("/");
                    for (String s : availableSinksArr) {
                        material.getAvailableSinkTypes().add(Integer.valueOf(s));
                    }
                }

                //System.out.println(availableSinks);

                for (int i = 32; i <= 88; i++) {
                    String model = sheet.getRow(2).getCell(i).getStringCellValue();

                    if (row.getCell(i).getStringCellValue().equals("-") || row.getCell(i).getStringCellValue().equals("")) continue;

                    String priceStr = row.getCell(i).getStringCellValue();
                    priceStr = priceStr.replaceAll(",", ".");
                    double price = Double.parseDouble(priceStr.split("=")[1]);
                    String currency = priceStr.split("=")[0];
                    material.setSinkCurrency(currency.toUpperCase());
                    material.getAvailableSinkModels().put(model, (int)(price * 100));
                }

                {
                    int type = 16;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int)price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 17;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int)price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 19;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int)price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 20;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int)price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 21;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int)price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                //System.out.println(material.getSinkCommonTypesAndPrices());

                /** Pallets: */
                for (int i = 89; i <= 96; i++) {
                    String model = sheet.getRow(2).getCell(i).getStringCellValue();

                    if (row.getCell(i).getStringCellValue().equals("-") || row.getCell(i).getStringCellValue().equals("")) continue;

                    String priceStr = row.getCell(i).getStringCellValue();
                    priceStr = priceStr.replaceAll(",", ".");
                    double price = Double.parseDouble(priceStr.split("=")[1]);
                    String currency = priceStr.split("=")[0];
                    material.setPalletsCurrency(currency.toUpperCase());
                    material.getPalletsModelsAndPrices().put(model, (int)(price * 100));


                }
//                System.out.println("PALLETS MODEL AND PRICES:" +material.getPalletsModelsAndPrices());

                /** add grooves: */
                material.getGroovesTypesAndPrices().clear();
                material.getAvailableGroovesTypes().clear();
                for (int i = 97; i <= 100; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";

                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];

                        material.getAvailableGroovesTypes().add(i - 97 + 1);
                    }
                    material.setGroovesCurrency(currency.toUpperCase());
                    material.getGroovesTypesAndPrices().put(i - 97, (int) (price * 100));
                }


                /** add rods: */
                material.getRodsTypesAndPrices().clear();
                material.getAvailableRodsTypes().clear();
                for (int i = 101; i <= 102; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";

                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                        material.getAvailableRodsTypes().add(i - 101 + 1);
                    }
                    material.setRodsCurrency(currency.toUpperCase());
                    material.getRodsTypesAndPrices().put(i - 101, (int) (price * 100));
                }


                /** edges heights and prices: */
                if (material.getMainType().indexOf("Кварцевый агломерат") != -1 ||
                        material.getMainType().indexOf("Натуральный камень") != -1 ||
                        material.getMainType().indexOf("Dektone") != -1 ||
                        material.getMainType().indexOf("Мраморный агломерат") != -1 ||
                        material.getMainType().indexOf("Кварцекерамический камень") != -1) {
                    for (int i = 122; i <= 148; i++) {
                        if (!row.getCell(i).getStringCellValue().equals("-")) {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            String currency = priceStr.split("=")[0];
                            double price = Double.parseDouble(priceStr.split("=")[1]);
                            Integer edgeNumber = (int) sheet.getRow(2).getCell(i).getNumericCellValue();
                            // System.out.println(edgeNumber);
                            material.setEdgesCurrency(currency.toUpperCase());
                            material.getEdgesAndPrices().put(edgeNumber, price);
                        }
                    }
                } else {
                    for (int i = 105; i <= 121; i++) {
                        if (!row.getCell(i).getStringCellValue().equals("-")) {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            String currency = priceStr.split("=")[0];
                            double price = Double.parseDouble(priceStr.split("=")[1]);

                            Integer edgeNumber = (int) sheet.getRow(2).getCell(i).getNumericCellValue();
                            // System.out.println(edgeNumber);
                            material.setEdgesCurrency(currency.toUpperCase());
                            material.getEdgesAndPrices().put(edgeNumber, price);
                        }
                    }
                }

                /** borders heights and prices: */
                Map<Integer, Integer> borderTypesAndPrices = material.getBorderTypesAndPrices();
                Map<Integer, Integer> borderTopCutTypesAndPrices = material.getBorderTopCutTypesAndPrices();
                Map<Integer, Integer> borderSideCutTypesAndPrices = material.getBorderSideCutTypesAndPrices();

                if (material.getMainType().indexOf("Кварцевый агломерат") != -1 ||
                        material.getMainType().indexOf("Натуральный камень") != -1 ||
                        material.getMainType().indexOf("Dektone") != -1 ||
                        material.getMainType().indexOf("Мраморный агломерат") != -1 ||
                        material.getMainType().indexOf("Кварцекерамический камень") != -1) {

                    String currency;
                    double price;

                    if (!row.getCell(149).getStringCellValue().equals("-")) {
                        String priceStr = row.getCell(149).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        currency = priceStr.split("=")[0].toUpperCase();
                        price = Double.parseDouble(priceStr.split("=")[1]);
                    } else {
                        currency = "RUB";
                        price = 0;
                    }

                    material.setBorderCurrency(currency);
                    borderTypesAndPrices.put(Integer.valueOf(0), (int) (price * 100));




                } else {


                    for (int i = 150; i <= 153; i++) {
                        String currency;
                        double price;
                        if (!row.getCell(i).getStringCellValue().equals("-")) {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            currency = (priceStr.split("=")[0]).toUpperCase();
                            price = Double.parseDouble(priceStr.split("=")[1]);
                        } else {
                            currency = "RUB";
                            price = 0;
                        }

                        material.setBorderCurrency(currency);
                        borderTypesAndPrices.put(Integer.valueOf(i - 150), Integer.valueOf((int) (price * 100)));

                    }

                    //cutout for radiator
                    {
                        double price = 0;
                        String currency = "RUB";
                        if (row.getCell(174).getStringCellValue().equals("-")) {
                            price = 0;
                            currency = "RUB";
                        } else {
                            String priceStr = row.getCell(174).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            price = Double.parseDouble(priceStr.split("=")[1]);
                            currency = priceStr.split("=")[0];
                        }

                        material.setCutoutCurrency(currency.toUpperCase());
                        material.getCutoutTypesAndPrices().put(7, (int) (price * 100));
                    }
                }

                for (int i = 154; i <= 157; i++) {
                    double price;

                    if (!row.getCell(i).getStringCellValue().equals("-")) {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);

                    } else {
                        price = 0;
                    }
                    borderTopCutTypesAndPrices.put(Integer.valueOf(i - 154), Integer.valueOf((int) (price * 100)));
                }

                for (int i = 158; i <= 159; i++) {

                    double price;
                    if (!row.getCell(i).getStringCellValue().equals("-")) {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);

                    } else {
                        price = 0;
                    }
                    borderSideCutTypesAndPrices.put(Integer.valueOf(i - 158), Integer.valueOf((int) (price * 100)));
                }


                //add sinks installTypes:
                material.getSinkInstallTypesAndPrices().clear();
                for (int i = 180; i <= 181; i++) {

                    if (row.getCell(i).getStringCellValue().equals("-")) continue;

                    String priceStr = row.getCell(i).getStringCellValue();
                    priceStr = priceStr.replaceAll(",", ".");
                    double price = Double.parseDouble(priceStr.split("=")[1]);
                    String currency = priceStr.split("=")[0];
                    material.setSinkInstallTypeCurrency(currency.toUpperCase());
                    material.getSinkInstallTypesAndPrices().put(Integer.valueOf(i - 180), Integer.valueOf((int) (price * 100)));
                }

                /** add sinks edge Types: */
                material.getSinkEdgeTypesRectangleAndPrices().clear();
                for (int i = 169; i <= 170; i++) {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setSinkEdgeTypeCurrency(currency.toUpperCase());
                    material.getSinkEdgeTypesRectangleAndPrices().put(Integer.valueOf(i - 169), Integer.valueOf((int) (price * 100)));
                }

                material.getSinkEdgeTypesCircleAndPrices().clear();
                for (int i = 171; i <= 172; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";

                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setSinkEdgeTypeCurrency(currency.toUpperCase());
                    material.getSinkEdgeTypesCircleAndPrices().put(Integer.valueOf(i - 171), Integer.valueOf((int) (price * 100)));
                }

                /** add cutout Types: */
                material.getCutoutTypesAndPrices().clear();

                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(161).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(161).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(1), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(160).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(160).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(2), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(167).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(167).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(3), Integer.valueOf((int) (price * 100)));
                }

                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(162).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(162).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(4), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(168).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(168).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(5), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(173).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(173).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(6), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(174).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(174).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(7), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(167).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(167).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(8), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(186).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(186).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(13), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(187).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(187).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(14), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(193).getStringCellValue().equals("-") || row.getCell(193).getStringCellValue().equals("")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(193).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(15), Integer.valueOf((int) (price * 100)));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(194).getStringCellValue().equals("-") || row.getCell(194).getStringCellValue().equals("")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(194).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(Integer.valueOf(16), Integer.valueOf((int) (price * 100)));
                }


                /** add siphons: (unavailable in this tab)*/
                material.getSiphonsTypesAndPrices().clear();
                material.setSiphonsCurrency("RUB");
                material.getSiphonsTypesAndPrices().put(0, 180000);
                material.getSiphonsTypesAndPrices().put(1, 360000);


                /** add joints: */
                material.getJointsTypesAndPrices().clear();
                for (int i = 163; i <= 164; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";

                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];

                    }
                    material.setJointsCurrency(currency.toUpperCase());
                    material.getJointsTypesAndPrices().put(Integer.valueOf(i - 163), Integer.valueOf((int) (price * 100)));
                }

                /** add plywoods: */
                material.getPlywoodPrices().clear();
                for (int i = 165; i <= 166; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";

                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];

                    }
                    material.getPlywoodCurrency().add(currency.toUpperCase());
                    material.getPlywoodPrices().add(Integer.valueOf((int) (price * 100)));
                }


                //add stonePolishing element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(178).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(178).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setStonePolishingCurrency(currency.toUpperCase());
                    material.setStonePolishingPrice((int) (price * 100));
                }

                //add metalFooting element:
                {
                    material.getMetalFootingPrices().clear();
                    for (int i = 176; i <= 177; i++) {

                        double price = 0;
                        String currency = "RUB";
                        if (row.getCell(i).getStringCellValue().equals("-")) {
                            price = 0;
                            currency = "RUB";

                        } else {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            price = Double.parseDouble(priceStr.split("=")[1]);
                            currency = priceStr.split("=")[0];

                        }
                        material.getMetalFootingCurrency().add(currency.toUpperCase());
                        material.getMetalFootingPrices().add(Integer.valueOf((int) (price * 100)));
                    }
                }


                //add radius element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(179).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(179).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setRadiusElementCurrency(currency.toUpperCase());
                    material.setRadiusElementPrice((int) (price * 100));
                }

                //add stone hem element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(182).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(182).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setStoneHemCurrency(currency.toUpperCase());
                    material.setStoneHemPrice((int) (price * 100));
                }

                //add leakGroove element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(175).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(175).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setLeakGrooveCurrency(currency.toUpperCase());
                    material.setLeakGroovePrice((int) (price * 100));
                }

                //add manual lifting:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(183).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(183).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setManualLiftingCurrency(currency.toUpperCase());
                    material.setManualLiftingPrice((int) (price * 100));
                }

                //add delivery price for inside MKAD:
                {
                    //Row row = sheet.getRow(2);

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(190).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {


                        String priceStr = row.getCell(190).getStringCellValue();

                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setDeliveryInsideMKADCurrency(currency.toUpperCase());
                    material.setDeliveryInsideMKADPrice((int) (price));
                }

                //add measurer:
                {
                    //Row row = sheet.getRow(2);
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(188).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(188).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setMeasurerCurrency(currency.toUpperCase());
                    material.setMeasurerPrice((int) (price));
                }

                //add measurer price for km  outside MKAD:
                {
                    //Row row = sheet.getRow(2);

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(189).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(189).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setMeasurerKMCurrency(currency.toUpperCase());
                    material.setMeasurerKMPrice((int) (price));
                }

                //add delivery price from manufacture
                {
                    //Row row = sheet.getRow(2);

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(191).getStringCellValue().equals("-") ||
                            row.getCell(191).getStringCellValue().equals("")) {
                        price = 0;
                        currency = "RUB";
                    } else {


                        String priceStr = row.getCell(191).getStringCellValue();

                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setDeliveryFromManufactureCurrency(currency.toUpperCase());
                    material.setDeliveryFromManufacture((int) (price));
                }

                //add sheet cutting price from manufacture
                {

                    material.setSheetCuttingCurrency("RUB");
                    material.setSheetCuttingPrice((int) (1000.0));
                }


                /** add notification 1: */
                {
                    int notification1 = (int) row.getCell(184).getNumericCellValue();
                    material.setNotification1(notification1);
                }

                /** add notification 2: */
                {
                    int notification2 = (int) row.getCell(185).getNumericCellValue();
                    material.setNotification2(notification2);
                }

                /** add promotion info: */
                cell = 195;
                if (row.getCell(cell).getCellType() == CellType.STRING) {
                    String value = row.getCell(cell).getStringCellValue();
                    material.setPromotion(value.equalsIgnoreCase("yes"));
                }else{
                    material.setPromotion(false);
                }

                /** add visual properties: */
                cell = 196;
                if (row.getCell(cell).getCellType() == CellType.STRING &&
                        !row.getCell(cell).getStringCellValue().equals("-") &&
                        !row.getCell(cell).getStringCellValue().equalsIgnoreCase("н/д")) {

                    String[] values = row.getCell(cell).getStringCellValue().split("##");
                    material.getVisualProperties().clear();

                    if (values.length >= 1) {
                        material.getVisualProperties().put(Material.VIS_PROP_COLOR, values[0]);
                    }
                    if (values.length >= 2) {
                        material.getVisualProperties().put(Material.VIS_PROP_TEXTURE, values[1]);
                    }
                    if (values.length >= 3) {
                        material.getVisualProperties().put(Material.VIS_PROP_SURFACE, values[2]);
                    }

                }else{
                    material.getVisualProperties().clear();
                }

                list.add(material);


            }



        }catch(Exception e){
            System.err.println("Error in Materials File pos:" + position);

            e.printStackTrace();
        }
        /** set prices without depency of MATERIAL */


        /** fill delivery from manufacture*/

        sheet = wb.getSheet("delivery");
        it = sheet.iterator();
        it.next();
        while (it.hasNext()) {
            Row row = it.next();
            if (row.getCell(1).getStringCellValue().equals("")) break;
            String groupName = row.getCell(1).getStringCellValue();
            Double price = Double.valueOf(row.getCell(2).getNumericCellValue());
            materialsDeliveryFromManufacture.put(groupName, price);

        }

        /** fill analogs for materials: */
        {


            HSSFWorkbook hssfWorkbook = null;
            try {
                InputStream inputStream = new FileInputStream(ANALOGS_LIST_PATH);

                //in = new InputStreamReader(new FileInputStream(name), "UTF8");
                hssfWorkbook = new HSSFWorkbook(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //for acrylic stone:
            Sheet sheetAnalogs = hssfWorkbook.getSheetAt(0);
            Iterator<Row> iterator = sheetAnalogs.iterator();
            iterator.next();

            while (iterator.hasNext()) {
                Row row = iterator.next();

                ArrayList<String> localListAnalogs = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    if (row.getCell(i * 5 + 1) == null || row.getCell(i * 5 + 2) == null || row.getCell(i * 5 + 3) == null || row.getCell(i * 5 + 4) == null) {
                        continue;
                    }
                    String materialName = row.getCell(i * 5 + 1).getStringCellValue() + "$" + row.getCell(i * 5 + 2).getStringCellValue() + "$" +
                            row.getCell(i * 5 + 3).getStringCellValue() + "$" + row.getCell(i * 5 + 4).getStringCellValue() + "$";
                    localListAnalogs.add(materialName);
                }
                //System.out.println(localListAnalogs);


                //add Analogs to materials instances:
                for (String analogName : localListAnalogs) {

                    for (Material m : list) {
                        if (m.getName().indexOf(analogName) != -1) {

                            //add analogs to material:
                            m.getAnalogsList().clear();
                            for (String analogNameForAdd : localListAnalogs) {

                                //if(analogNameForAdd.equals(analogName)) continue;//not to add himself
                                //getMaterial by name:
                                for (Material mForAdd : list) {
                                    if (mForAdd.getName().indexOf(analogNameForAdd) != -1) {
                                        //add analog material:
                                        m.getAnalogsList().add(mForAdd);
                                    }
                                }
                            }
                        }
                    }
                }


            }


            //for quarz stone:
            Sheet sheetAnalogs1 = hssfWorkbook.getSheetAt(1);
            Iterator<Row> iterator1 = sheetAnalogs1.iterator();
            iterator1.next();

            while (iterator1.hasNext()) {
                Row row = iterator1.next();

                ArrayList<String> localListAnalogs = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    if (row.getCell(i * 5 + 1) == null || row.getCell(i * 5 + 2) == null || row.getCell(i * 5 + 3) == null || row.getCell(i * 5 + 4) == null) {
                        continue;
                    }
                    String materialName = row.getCell(i * 5 + 1).getStringCellValue() + "$" + row.getCell(i * 5 + 2).getStringCellValue() + "$" +
                            row.getCell(i * 5 + 3).getStringCellValue() + "$" + row.getCell(i * 5 + 4).getStringCellValue() + "$";
                    localListAnalogs.add(materialName);
                }
                //System.out.println(localListAnalogs);


                //add Analogs to materials instances:
                for (String analogName : localListAnalogs) {

                    for (Material m : list) {
                        if (m.getName().indexOf(analogName) != -1) {

                            //add analogs to material:
                            m.getAnalogsList().clear();
                            for (String analogNameForAdd : localListAnalogs) {

                                //if(analogNameForAdd.equals(analogName)) continue;//not to add himself
                                //getMaterial by name:
                                for (Material mForAdd : list) {
                                    if (mForAdd.getName().indexOf(analogNameForAdd) != -1) {
                                        //add analog material:
                                        m.getAnalogsList().add(mForAdd);
                                    }
                                }
                            }
                        }
                    }
                }


            }


            sheet = wb.getSheetAt(0);

        }


//        for(Material material : list){
//            System.out.println("tableTopDepthsAndPrices = " + material.getTableTopDepthsAndPrices());
//        }





        return list;
    }

    public static ArrayList<Material> getMaterialsListAvailable() {
        return materialsListAvailable;
    }

    public static ArrayList<Material> getMaterialsListInProject() {
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
        //CutDesigner.updateMaterialsInProject();
//        if (getProjectType() == ProjectType.SKETCH_TYPE) {
//            if (MainWindow.getSketchDesigner() != null) SketchDesigner.updateMaterialsInProject();
//        } else if (getProjectType() == ProjectType.TABLE_TYPE) {
//            if (MainWindow.getTableDesigner() != null) TableDesigner.updateMaterialsInProject();
//        }

    }


    public static Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public static Map<String, Double> getMaterialsDeliveryFromManufacture() {
        return materialsDeliveryFromManufacture;
    }

    public static void setDefaultMaterialRAW(Material defaultMaterial) {
        ProjectHandler.defaultMaterial = defaultMaterial;
//        MainWindow.setDefaultMaterialLabel(defaultMaterial.getName());
    }

    public static void setDefaultMaterial(Material defaultMaterial) {
        ProjectHandler.defaultMaterial = defaultMaterial;
//        MainWindow.setDefaultMaterialLabel(defaultMaterial.getName());

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
