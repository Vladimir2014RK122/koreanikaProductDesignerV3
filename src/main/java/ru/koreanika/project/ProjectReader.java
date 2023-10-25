package ru.koreanika.project;

import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.CheckSheetsPrices;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ProjectReader {

    private static final EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);

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

            ProjectHandler.setCurProjectName(projectName);
            ProjectHandler.setCurProjectPath(projectPath);

            try {
                boolean isZipProject = false;
                boolean isOldProject = false;
                boolean isENCRYPTEDProject = false;

                //check if it .zip type of project:
                {
                    try {
                        ZipFile zipFile = new ZipFile(projectPath);
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
                    System.out.println("Open project: " + projectPath + " (OLD TEXT type)");
                } else if (isZipProject) {
                    //open .zip
                    {
                        FileInputStream fileInputStream = new FileInputStream(projectPath);
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
                                Project.receiptManagerSketchImage = new Image(zis);
                                System.out.println("HAVE IMAGE");
                            }
                        }
                        bis.close();
                    }

                    System.out.println("Open project: " + projectPath + " (ZIP type)");

                } else {
                    /* ADD CUSTOM DECRYPT */
                    {
                        FileInputStream fileInputStream = new FileInputStream(projectPath);

                        byte[] buf = new byte[fileInputStream.available()];
                        fileInputStream.read(buf);
                        fileInputStream.close();

                        for (int i = 0; i < buf.length; i++) {
                            buf[i] -= 76;
                        }

                        FileOutputStream fileOutputStream = new FileOutputStream(projectPath);
                        fileOutputStream.write(buf);

                        fileOutputStream.close();
                    }

                    //open .zip
                    {
                        FileInputStream fileInputStream = new FileInputStream(projectPath);
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
                                Project.receiptManagerSketchImage = new Image(zis);
                            }
                        }
                        bis.close();
                    }

                    System.out.println("parsedproject = " + parsedProject);
                    System.out.println("parsedproject.toString() = " + parsedProject.toString());
                    System.out.println("Open project: " + projectPath + " (ZIP ENCRYPTED type)");
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

            Project.setProjectType(ProjectType.valueOf((String) projectSettings.get("projectType")));

            /** Materials */
            //fill materials in project:
            if (materialsNewList != null) {
                for (Object obj : materialsNewList) {
                    JSONObject materialObject = (JSONObject) obj;

                    Material material = Material.getFromJson(materialObject);
                    if (material != null) {
                        Project.materialsListInProject.add(material);
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
                    Project.materialsListInProject.add(Material.parseMaterial(((String) str)));
                }
            }

            // set default material, if not specified in project
            for (Material material : Project.materialsListInProject) {
                if (((String) materialSettings.get("defaultMaterial")).contains(material.getName())) {
                    Project.defaultMaterial = material;
                }
            }

            //project coefficients for price:
            Double priceMainCoefficientDouble = (Double) projectSettings.get("priceMainCoefficient");
            Double priceMaterialCoefficientDouble = (Double) projectSettings.get("priceMaterialCoefficient");
            if (priceMainCoefficientDouble == null || priceMaterialCoefficientDouble == null) {
                Project.setPriceMainCoefficient(Main.mainCoefficient);
                Project.setPriceMaterialCoefficient(Main.materialCoefficient);
            } else {
                Project.setPriceMainCoefficient(priceMainCoefficientDouble.doubleValue());
                Project.setPriceMaterialCoefficient(priceMaterialCoefficientDouble.doubleValue());
            }


            System.out.println("Default material = " + Project.defaultMaterial.getName());

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

            ProjectHandler.userProject = parsedProject;
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

        ProjectWriter.saveProject(projectPath, projectName);
        return true;
    }

    private static String changeDektoneNameInProject(String project) {
        System.out.println("BEFORE : " + project);
        String res = project.replace("Dektone$D", "Кварцекерамический камень$D");

        res = project.replace("Массив$", "Массив_шпон$");
        //res = project.replace("Agglocemento", "Мраморный агломерат");
        System.out.println("AFTER : " + res);
        return res;
    }


}