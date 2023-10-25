package ru.koreanika.project;

import javafx.embed.swing.SwingFXUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.Receipt.ReceiptManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProjectWriter {

    private static final EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);

    static void saveProject(String projectPath, String projectName) {
        if (ProjectHandler.userProject != null) {
            if (projectName.matches(".+\\.krnkproj$")) {
                projectName = projectName.replace("\\.krnkproj", ".kproj");
            }
            if (projectPath.matches(".+\\.krnkproj$")) {
                projectPath = projectPath.replace("\\.krnkproj", ".kproj");
            }
            if (!projectPath.matches(".+\\.kproj$")) {
                projectPath += ".kproj";
            }
            ProjectHandler.setCurProjectName(projectName);
            ProjectHandler.setCurProjectPath(projectPath);

            /** System data: */
            //window size:
            double windowWidth = Main.getMainScene().getWindow().getWidth();
            double windowHeight = Main.getMainScene().getWindow().getHeight();
            double windowPosX = Main.getMainScene().getWindow().getX();
            double windowPosY = Main.getMainScene().getWindow().getY();

            ((JSONObject) ProjectHandler.userProject.get("info")).put("windowWidth", windowWidth);
            ((JSONObject) ProjectHandler.userProject.get("info")).put("windowHeight", windowHeight);
            ((JSONObject) ProjectHandler.userProject.get("info")).put("windowPosX", windowPosX);
            ((JSONObject) ProjectHandler.userProject.get("info")).put("windowPosY", windowPosY);

            //date and time:
            ((JSONObject) ProjectHandler.userProject.get("info")).put("editDate", LocalDateTime.now().toString());
            ((JSONObject) ProjectHandler.userProject.get("info")).put("name", projectName);

            /** Project settings*/
            JSONObject projectSettings = new JSONObject();

            //material settings
            JSONObject materialSettings = new JSONObject();
            JSONArray materialsList = new JSONArray();
            JSONArray materialsNewList = new JSONArray();

            //project type:
            projectSettings.put("projectType", Project.getProjectType().toString());

            //project coefficients for price:
            projectSettings.put("priceMainCoefficient", Project.priceMainCoefficient.getValue());
            projectSettings.put("priceMaterialCoefficient", Project.priceMaterialCoefficient.getValue());

            for (Material material : Project.materialsListInProject) {
                materialsList.add(material.getName() + "#" + material.getDefaultDepth());//deprecated

                JSONObject materialObject = material.getJsonView();
                materialsNewList.add(materialObject);
            }

            materialSettings.put("materialsList", materialsList);
            materialSettings.put("materialsNewList", materialsNewList);
            materialSettings.put("defaultMaterial", Project.defaultMaterial.getName() + "#" + Project.defaultMaterial.getDefaultDepth());
            projectSettings.put("materialSettings", materialSettings);

            ProjectHandler.userProject.put("ProjectSettings", projectSettings);


            /** Table Designer*/
            if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
                JSONObject tableDesignerJSONObject = MainWindow.getTableDesigner().getJsonView();
                ProjectHandler.userProject.put("TableDesigner", tableDesignerJSONObject);
            } else if (Project.getProjectType() == ProjectType.SKETCH_TYPE) {
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
                ProjectHandler.userProject.put("SketchDesigner", sketchDesigner);
            }

            /** Cut designer*/
            ProjectHandler.userProject.put("CutDesigner", MainWindow.getCutDesigner().getJsonView());

            /** Receipt manager*/
            ProjectHandler.userProject.put("receiptManager", MainWindow.getReceiptManager().getJsonViewForSaveData());

            try {
                //create .zip
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(ProjectHandler.getCurProjectPath());
                    ZipOutputStream zos = new ZipOutputStream(fileOutputStream);

                    zos.putNextEntry(new ZipEntry("mainInfo.json"));
                    zos.write(ProjectHandler.userProject.toJSONString().getBytes(StandardCharsets.UTF_8));

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
                    FileInputStream fileInputStream = new FileInputStream(ProjectHandler.getCurProjectPath());

                    byte[] buf = new byte[fileInputStream.available()];
                    fileInputStream.read(buf);
                    fileInputStream.close();

                    for (int i = 0; i < buf.length; i++) {
                        buf[i] += 76;
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(ProjectHandler.getCurProjectPath());
                    fileOutputStream.write(buf);

                    fileOutputStream.close();
                }

                System.out.println("Save project path:" + ProjectHandler.getCurProjectPath());
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.SUCCESS, "Проект сохранен"));

            } catch (IOException ex) {
                System.out.println("CAN'T Save project path:" + ProjectHandler.getCurProjectPath());
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Ошибка директории"));
            }
        } else {
            System.err.println("Try to save user project. Error: userProject object = null");
        }
    }

}