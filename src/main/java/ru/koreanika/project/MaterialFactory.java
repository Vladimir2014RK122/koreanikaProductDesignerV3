package ru.koreanika.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaterialFactory {

    public static Material buildFromJSON(JSONObject materialObject, List<Material> materialsCatalog) {
        Material material = null;

        String id = (String) materialObject.get("id");

        //check material from template or not
        String name = (String) materialObject.get("name");
        int defaultDepth = ((Long) materialObject.get("defaultDepth")).intValue();
        int availableMainSheetsCount = ((Long) materialObject.get("availableMainSheetsCount")).intValue();
        boolean useMainSheets = (Boolean) materialObject.get("useMainSheets");
        boolean useAdditionalSheets = (Boolean) materialObject.get("useAdditionalSheets");

        ArrayList<String> depthsList = new ArrayList<>();
        JSONArray additionalSheetsJsonArray = (JSONArray) materialObject.get("additionalSheets");
        for (Object obj : additionalSheetsJsonArray) {
            JSONObject sheetObject = (JSONObject) obj;
            int sheetDepth = ((Long) (sheetObject.get("depth"))).intValue();
            if (!depthsList.contains("" + sheetDepth)) depthsList.add("" + sheetDepth);
        }

        if (availableMainSheetsCount == 0) {
            //created from template
            Material templateMaterial = null;
            boolean foundTemplate = false;
            for (Material m : materialsCatalog) {
                // try to resolve material template based on Material ID
                if (m.getId() != null && id != null && !id.isEmpty() && m.getId().equals(id)) {
                    System.out.println("DEBUG: [1] Material template resolved by ID, id = " + id);
                    templateMaterial = m;
                    foundTemplate = true;
                    break;
                }

                // otherwise attempt name-based resolution
                String condName = name.split("\\$")[0] + "$" + name.split("\\$")[1] + "$" + name.split("\\$")[2] + "$" + name.split("\\$")[3] + "$" + name.split("\\$")[4] + "$" + name.split("\\$")[5];
                if ((m.getMainType() + "$" + m.getSubType() + "$" + m.getCollection() + "$" + m.getColor()).equals(condName)) {
                    System.out.println("DEBUG: [1] Material template resolved by name, name = " + condName);
                    templateMaterial = m;
                    foundTemplate = true;
                    break;
                }
            }
            if (!foundTemplate) {
                for (Material m : materialsCatalog) {
                    String condName = name.split("\\$")[0] + "$" + name.split("\\$")[1] + "$" + name.split("\\$")[2] + "$" + "Другой";
                    if ((m.getMainType() + "$" + m.getSubType() + "$" + m.getCollection() + "$" + m.getColor()).equalsIgnoreCase(condName)) {
                        templateMaterial = m;
                        foundTemplate = true;
                        break;
                    }
                }
            }

            if (!foundTemplate) {
                System.out.println("MATERIAL DOESNT PARSED FROM JSON!!!! (" + name + ")");
                return null;
            }

            //depthsList
            material = deriveFrom(templateMaterial, name.split("\\$")[3], 1000, 1000, templateMaterial.getImgPath(), depthsList);
            material.setTemplate(false);

            material.setUseMainSheets(false);
            material.setAvailableMainSheetsCount(0);

            material.getTableTopDepthsAndPrices().clear();
            material.getWallPanelDepthsAndPrices().clear();
            material.getWindowSillDepthsAndPrices().clear();
            material.getFootDepthsAndPrices().clear();

            for (String d : material.getDepths()) {

                material.getTableTopDepthsAndPrices().put(Integer.parseInt(d), 100000);
                material.getWallPanelDepthsAndPrices().put(Integer.parseInt(d), 100000);
                material.getWindowSillDepthsAndPrices().put(Integer.parseInt(d), 100000);
                material.getFootDepthsAndPrices().put(Integer.parseInt(d), 100000);
            }

        } else {
            //from availableList
            boolean foundTemplate = false;

            for (Material m : materialsCatalog) {
                // try to resolve material template based on Material ID
                if (m.getId() != null && id != null && !id.isEmpty() && m.getId().equals(id)) {
                    System.out.println("DEBUG: [2] Material template resolved by ID, id = " + id);
                    material = m;
                    foundTemplate = true;
                    break;
                }

                // otherwise attempt name-based resolution
                if (m.getName().equals(name)) {
                    System.out.println("DEBUG: [2] Material template resolved by name, name = " + name);
                    material = m;
                    foundTemplate = true;
                    break;
                }
            }

            if (!foundTemplate) {
                System.out.println("MATERIAL DOESNT PARSED FROM JSON!!!! (" + name + ")");
                return null;
            }
        }

        System.out.println(material);

        material.getAvailableAdditionalSheets().clear();

        for (Object obj : additionalSheetsJsonArray) {
            JSONObject sheetObject = (JSONObject) obj;

            double sheetWidth = (Double) (sheetObject.get("width"));
            double sheetHeight = (Double) (sheetObject.get("height"));
            int sheetDepth = ((Long) (sheetObject.get("depth"))).intValue();
            double sheetCustomPriceForMeter = (Double) (sheetObject.get("priceForMeter"));
            String currency = (String) (sheetObject.get("currency"));

            material.createAdditionalMaterialSheet(sheetDepth, sheetWidth, sheetHeight, sheetWidth, sheetHeight, sheetCustomPriceForMeter, currency);

        }

        material.setUseMainSheets(useMainSheets);
        material.setUseAdditionalSheets(useAdditionalSheets);

        return material;
    }

    public static Material deriveFrom(Material material, String color, double width, double height, String imgPath, List<String> depthsList) {
        List<String> depthsCopy = new ArrayList<>(depthsList);

        Material materialCopy = new Material(material.getId(), material.getMainType(), material.getSubType(),
                material.getCollection(), color, width, height, imgPath, depthsCopy);
        materialCopy.setCurrency("RUB");

        //depths and prices:
        for (Map.Entry<Integer, Integer> entry : material.getTableTopDepthsAndPrices().entrySet()) {
            materialCopy.getTableTopDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : material.getWallPanelDepthsAndPrices().entrySet()) {
            materialCopy.getWallPanelDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : material.getWindowSillDepthsAndPrices().entrySet()) {
            materialCopy.getWindowSillDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : material.getFootDepthsAndPrices().entrySet()) {
            materialCopy.getFootDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }

        //coefficients:
        for (Double coefficient : material.getTableTopCoefficientList()) {
            materialCopy.getTableTopCoefficientList().add(coefficient);
        }
        for (Double coefficient : material.getWallPanelCoefficientList()) {
            materialCopy.getWallPanelCoefficientList().add(coefficient);
        }
        for (Double coefficient : material.getWindowSillCoefficientList()) {
            materialCopy.getWindowSillCoefficientList().add(coefficient);
        }
        for (Double coefficient : material.getFootCoefficientList()) {
            materialCopy.getFootCoefficientList().add(coefficient);
        }

        materialCopy.setCalculationType(material.getCalculationType());
        materialCopy.setMinMaterialSize((int) material.getMinMaterialWidth(), (int) material.getMinMaterialHeight());
        materialCopy.setMinCountSlabs(material.getMinCountSlabs());

        //sink:
        for (Integer type : material.getAvailableSinkTypes()) {
            materialCopy.getAvailableSinkTypes().add(type);
        }
        for (Map.Entry<String, Integer> entry : material.getAvailableSinkModels().entrySet()) {
            materialCopy.getAvailableSinkModels().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSinkCurrency(material.getSinkCurrency());

        for (Map.Entry<Integer, Integer> entry : material.getSinkCommonTypesAndPrices().entrySet()) {
            materialCopy.getSinkCommonTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, String> entry : material.getSinkCommonCurrency().entrySet()) {
            materialCopy.getSinkCommonCurrency().put(entry.getKey(), entry.getValue());
        }

        //sinks installTypes:
        for (Map.Entry<Integer, Integer> entry : material.getSinkInstallTypesAndPrices().entrySet()) {
            materialCopy.getSinkInstallTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSinkInstallTypeCurrency(material.getSinkInstallTypeCurrency());

        //sink edges:
        for (Map.Entry<Integer, Integer> entry : material.getSinkEdgeTypesRectangleAndPrices().entrySet()) {
            materialCopy.getSinkEdgeTypesRectangleAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : material.getSinkEdgeTypesCircleAndPrices().entrySet()) {
            materialCopy.getSinkEdgeTypesCircleAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSinkEdgeTypeCurrency(material.getSinkEdgeTypeCurrency());

        //grooves:
        for (Map.Entry<Integer, Integer> entry : material.getGroovesTypesAndPrices().entrySet()) {
            materialCopy.getGroovesTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Integer type : material.getAvailableGroovesTypes()) {
            materialCopy.getAvailableGroovesTypes().add(type);
        }
        materialCopy.setGroovesCurrency(material.getGroovesCurrency());

        //rods
        for (Map.Entry<Integer, Integer> entry : material.getRodsTypesAndPrices().entrySet()) {
            materialCopy.getRodsTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Integer type : material.getAvailableRodsTypes()) {
            materialCopy.getAvailableRodsTypes().add(type);
        }
        materialCopy.setRodsCurrency(material.getRodsCurrency());

        //edges
        for (Map.Entry<Integer, Double> entry : material.getEdgesAndPrices().entrySet()) {
            materialCopy.getEdgesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setEdgesCurrency(material.getEdgesCurrency());

        //borders
        for (Map.Entry<Integer, Integer> entry : material.getBorderTypesAndPrices().entrySet()) {
            materialCopy.getBorderTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : material.getBorderTopCutTypesAndPrices().entrySet()) {
            materialCopy.getBorderTopCutTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : material.getBorderSideCutTypesAndPrices().entrySet()) {
            materialCopy.getBorderSideCutTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setBorderCurrency(material.getBorderCurrency());

        //cutout:
        for (Map.Entry<Integer, Integer> entry : material.getCutoutTypesAndPrices().entrySet()) {
            materialCopy.getCutoutTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setCutoutCurrency(material.getCutoutCurrency());

        //siphons:
        for (Map.Entry<Integer, Integer> entry : material.getSiphonsTypesAndPrices().entrySet()) {
            materialCopy.getSiphonsTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSiphonsCurrency(material.getSiphonsCurrency());

        //Joints:
        for (Map.Entry<Integer, Integer> entry : material.getJointsTypesAndPrices().entrySet()) {
            materialCopy.getJointsTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setJointsCurrency(material.getJointsCurrency());

        //plywoods:
        for (Integer price : material.getPlywoodPrices()) {
            materialCopy.getPlywoodPrices().add(price);
        }
        materialCopy.setPlywoodCurrency(material.getPlywoodCurrency());

        //stone polishing
        materialCopy.setStonePolishingPrice(material.getStonePolishingPrice());
        materialCopy.setStonePolishingCurrency(material.getStonePolishingCurrency());

        //metalFooting
        for (Integer price : material.getMetalFootingPrices()) {
            materialCopy.getMetalFootingPrices().add(price);
        }
        materialCopy.setMetalFootingCurrency(material.getMetalFootingCurrency());

        //radius element
        materialCopy.setStoneHemPrice(material.getStoneHemPrice());
        materialCopy.setStoneHemCurrency(material.getStoneHemCurrency());

        //leak groove
        materialCopy.setLeakGroovePrice(material.getLeakGroovePrice());
        materialCopy.setLeakGrooveCurrency(material.getLeakGrooveCurrency());

        //manual lifting
        materialCopy.setManualLiftingCurrency(material.getManualLiftingCurrency());
        materialCopy.setManualLiftingPrice(material.getManualLiftingPrice());

        //delivery price for inside MKAD
        materialCopy.setDeliveryInsideMKADCurrency(material.getDeliveryInsideMKADCurrency());
        materialCopy.setDeliveryInsideMKADPrice(material.getDeliveryInsideMKADPrice());

        //measurer
        materialCopy.setMeasurerCurrency(material.getMeasurerCurrency());
        materialCopy.setMeasurerPrice(material.getMeasurerPrice());

        //measurer price for km  outside MKAD:
        materialCopy.setMeasurerKMCurrency(material.getMeasurerKMCurrency());
        materialCopy.setMeasurerKMPrice(material.getMeasurerKMPrice());

        //delivery price from manufacture
        materialCopy.setDeliveryFromManufactureCurrency(material.getDeliveryFromManufactureCurrency());
        materialCopy.setDeliveryFromManufacture(material.getDeliveryFromManufacture());

        //sheet cutting price from manufacture
        materialCopy.setSheetCuttingCurrency(material.getSheetCuttingCurrency());
        materialCopy.setSheetCuttingPrice(material.getSheetCuttingPrice());

        materialCopy.setRadiusElementPrice(material.getRadiusElementPrice());
        materialCopy.setRadiusElementCurrency(material.getRadiusElementCurrency());

        // add notifications
        materialCopy.setNotification1(material.getNotification1());
        materialCopy.setNotification2(material.getNotification2());

        return materialCopy;
    }
}
