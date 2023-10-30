package ru.koreanika.project;

import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.catalog.Catalogs;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ProjectReader {

    private String errorMessage;

    ProjectReader() {
    }

    JSONObject read(String projectPath) throws NullPointerException {
        errorMessage = null;

        JSONObject parsedProject = null;
        JSONParser jsonParser = new JSONParser();

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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

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
                    ZipInputStream zis = new ZipInputStream(fileInputStream, StandardCharsets.UTF_8);
                    BufferedInputStream bis = new BufferedInputStream(zis);
                    ZipEntry zipEntry;
                    while ((zipEntry = zis.getNextEntry()) != null) {

                        if (zipEntry.getName().equals("mainInfo.json")) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
                            parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                            String s = changeDektoneNameInProject(parsedProject.toString());
                            parsedProject = (JSONObject) jsonParser.parse(s);

                        } else if (zipEntry.getName().equals("receiptManagerSketchImage.png")) {
                            Project.setReceiptManagerSketchImage(new Image(zis));
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
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
                            parsedProject = (JSONObject) jsonParser.parse(bufferedReader);

                            String s = changeDektoneNameInProject(parsedProject.toString());
                            parsedProject = (JSONObject) jsonParser.parse(s);

                        } else if (zipEntry.getName().equals("receiptManagerSketchImage.png")) {
                            Project.setReceiptManagerSketchImage(new Image(zis));
                        }
                    }
                    bis.close();
                }

                System.out.println("parsedproject = " + parsedProject);
                System.out.println("parsedproject.toString() = " + parsedProject.toString());
                System.out.println("Open project: " + projectPath + " (ZIP ENCRYPTED type)");
            }

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

                    Material material = MaterialFactory.getFromJson(materialObject);
                    if (material != null) {
                        Project.getMaterials().add(material);
                    } else {
                        errorMessage = "Ошибка распаковки материала " + materialObject.get("name");
                    }
                }
            } else {
                for (Object str : materialsList) {
                    String materialName = (String) str;
                    System.out.println("materialsList item = " + materialName);

                    Material materialTemplate = findMaterialTemplateByName(materialName);
                    if (materialTemplate == null) {
                        errorMessage = "Материал не существует: " + str;
                    } else {
                        Project.getMaterials().add(materialTemplate);
                    }
                }
            }

            // set default material, if not specified in project
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

            System.out.println("Default material = " + Project.getDefaultMaterial().getName());

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

            return parsedProject;

        } catch (ParseException exio) {
            System.out.println("cant Parse project");
            errorMessage = "Поврежден mainInfo файл";
            return null;
        } catch (IOException exio) {
            System.out.println("IOException");
            errorMessage = "Ошибка контента";
            return null;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static Material findMaterialTemplateByName(String str) throws NumberFormatException {
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

        for (Material m : Catalogs.getMaterialsListAvailable()) {
            if (m.getName().contains(nameForFind)) {
                return m;
            }
        }
        return null;
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
