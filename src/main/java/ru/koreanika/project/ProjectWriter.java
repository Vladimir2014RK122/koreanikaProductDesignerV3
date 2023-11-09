package ru.koreanika.project;

import javafx.embed.swing.SwingFXUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.common.material.MaterialSheet;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.receipt.ui.controller.ReceiptManager;

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

    private final ProjectHandler projectHandler;
    private final EventBus eventBus;

    // TODO get rid of ProjectHandler dependency
    ProjectWriter(ProjectHandler projectHandler) {
        this.projectHandler = projectHandler;
        this.eventBus = ServiceLocator.getService("EventBus", EventBus.class);
    }

    void saveProject(String projectPath, String projectName) {
        if (projectHandler.getProjectJSONObject() != null) {
            if (projectName.matches(".+\\.krnkproj$")) {
                projectName = projectName.replace("\\.krnkproj", ".kproj");
            }
            if (projectPath.matches(".+\\.krnkproj$")) {
                projectPath = projectPath.replace("\\.krnkproj", ".kproj");
            }
            if (!projectPath.matches(".+\\.kproj$")) {
                projectPath += ".kproj";
            }
            projectHandler.setCurrentProjectName(projectName);
            projectHandler.setCurrentProjectPath(projectPath);

            /** System data: */
            //window size:
            double windowWidth = Main.getMainScene().getWindow().getWidth();
            double windowHeight = Main.getMainScene().getWindow().getHeight();
            double windowPosX = Main.getMainScene().getWindow().getX();
            double windowPosY = Main.getMainScene().getWindow().getY();

            ((JSONObject) projectHandler.getProjectJSONObject().get("info")).put("windowWidth", windowWidth);
            ((JSONObject) projectHandler.getProjectJSONObject().get("info")).put("windowHeight", windowHeight);
            ((JSONObject) projectHandler.getProjectJSONObject().get("info")).put("windowPosX", windowPosX);
            ((JSONObject) projectHandler.getProjectJSONObject().get("info")).put("windowPosY", windowPosY);

            //date and time:
            ((JSONObject) projectHandler.getProjectJSONObject().get("info")).put("editDate", LocalDateTime.now().toString());
            ((JSONObject) projectHandler.getProjectJSONObject().get("info")).put("name", projectName);

            /** Project settings*/
            JSONObject projectSettings = new JSONObject();

            //material settings
            JSONObject materialSettings = new JSONObject();
            JSONArray materialsList = new JSONArray();
            JSONArray materialsNewList = new JSONArray();

            //project type:
            projectSettings.put("projectType", Project.getProjectType().toString());

            //project coefficients for price:
            projectSettings.put("priceMainCoefficient", Project.getPriceMainCoefficient().getValue());
            projectSettings.put("priceMaterialCoefficient", Project.getPriceMaterialCoefficient().getValue());

            for (Material material : Project.getMaterials()) {
                materialsList.add(material.getName() + "#" + material.getDefaultDepth());//deprecated

                JSONObject materialObject = getJsonView(material);
                materialsNewList.add(materialObject);
            }

            materialSettings.put("materialsList", materialsList);
            materialSettings.put("materialsNewList", materialsNewList);
            materialSettings.put("defaultMaterial", Project.getDefaultMaterial().getName() + "#" + Project.getDefaultMaterial().getDefaultDepth());
            projectSettings.put("materialSettings", materialSettings);

            projectHandler.getProjectJSONObject().put("ProjectSettings", projectSettings);


            /** Table Designer*/
            if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
                JSONObject tableDesignerJSONObject = MainWindow.getTableDesigner().getJsonView();
                projectHandler.getProjectJSONObject().put("TableDesigner", tableDesignerJSONObject);
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
                projectHandler.getProjectJSONObject().put("SketchDesigner", sketchDesigner);
            }

            /** Cut designer*/
            projectHandler.getProjectJSONObject().put("CutDesigner", MainWindow.getCutDesigner().getJsonView());

            /** Receipt manager*/
            projectHandler.getProjectJSONObject().put("receiptManager", MainWindow.getReceiptManager().getJsonViewForSaveData());

            try {
                //create .zip
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(projectHandler.getCurrentProjectPath());
                    ZipOutputStream zos = new ZipOutputStream(fileOutputStream);

                    zos.putNextEntry(new ZipEntry("mainInfo.json"));
                    zos.write(projectHandler.getProjectJSONObject().toJSONString().getBytes(StandardCharsets.UTF_8));

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
                    FileInputStream fileInputStream = new FileInputStream(projectHandler.getCurrentProjectPath());

                    byte[] buf = new byte[fileInputStream.available()];
                    fileInputStream.read(buf);
                    fileInputStream.close();

                    for (int i = 0; i < buf.length; i++) {
                        buf[i] += 76;
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(projectHandler.getCurrentProjectPath());
                    fileOutputStream.write(buf);

                    fileOutputStream.close();
                }

                System.out.println("Save project path:" + projectHandler.getCurrentProjectPath());
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.SUCCESS, "Проект сохранен"));

            } catch (IOException ex) {
                System.out.println("CAN'T Save project path:" + projectHandler.getCurrentProjectPath());
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Ошибка директории"));
            }
        } else {
            System.err.println("Try to save user project. Error: userProject object = null");
        }
    }

    private JSONObject getJsonView(Material material) {
        JSONObject materialObject = new JSONObject();
        materialObject.put("id", material.getId());
        materialObject.put("name", material.getName());
        materialObject.put("defaultDepth", material.getDefaultDepth());
        materialObject.put("useMainSheets", material.isUseMainSheets());
        materialObject.put("useAdditionalSheets", material.isUseAdditionalSheets());
        materialObject.put("availableMainSheetsCount", material.getAvailableMainSheetsCount());

        JSONArray additionalSheetsJsonArray = new JSONArray();

        for (MaterialSheet materialSheet : material.getAvailableAdditionalSheets()) {
            JSONObject sheetObject = new JSONObject();

            sheetObject.put("width", materialSheet.getSheetWidth());
            sheetObject.put("height", materialSheet.getSheetHeight());
            sheetObject.put("depth", materialSheet.getSheetDepth());
            sheetObject.put("priceForMeter", materialSheet.getSheetCustomPriceForMeter());
            sheetObject.put("currency", materialSheet.getSheetCurrency());

            additionalSheetsJsonArray.add(sheetObject);
        }
        materialObject.put("additionalSheets", additionalSheetsJsonArray);

        return materialObject;
    }

}
