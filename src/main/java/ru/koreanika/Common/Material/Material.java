package ru.koreanika.Common.Material;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.koreanika.catalog.Catalogs;
import ru.koreanika.project.Project;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class Material {

    private String id;
    private String mainType;
    private String subType;
    private String collection;
    private String color;

    private String name;
    private int calculationType;// 1 - m^2, 2 - slabs
    private String currency;//EUR,RUB,USD

    private List<String> depthsList;

    private boolean template = false;

    private String imgPath; // TODO imgPath is deprecated
    private List<String> depths = new ArrayList<>();
    private String depthsString = "";

    private Image textureImage;
    private boolean textureImageUpdatePending = false;

    private double materialWidth = 0;//mm
    private double materialHeight = 0;//mm

    private double minMaterialWidth = 0;//mm

    private double minMaterialHeight = 0;//mm

    private double slabSquare = 0;

    private int minCountSlabs = 1;

    private boolean horizontalCuttingParts = true;

    private int defaultDepth;

    private boolean useAdditionalSheets = false;
    private boolean useMainSheets = true;

    private int availableMainSheetsCount;
    private List<MaterialSheet> availableAdditionalSheets = new ArrayList<>();

    private Map<Integer, Integer> tableTopDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private List<Double> tableTopCoefficientList = new ArrayList<>(3); //<Coefficient>

    private Map<Integer, Integer> wallPanelDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private List<Double> wallPanelCoefficientList = new ArrayList<>(3); //<Coefficient>

    private Map<Integer, Integer> windowSillDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private List<Double> windowSillCoefficientList = new ArrayList<>(3); //<Coefficient>

    private Map<Integer, Integer> footDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private List<Double> footCoefficientList = new ArrayList<>(3); //<Coefficient>

    //additional elements:
    private String sinkCurrency;//EUR,RUB,USD

    private List<Integer> availableSinkTypes = new ArrayList<>();
    private Map<Integer, Integer> sinkInstallTypesAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String sinkInstallTypeCurrency = "";
    private Map<Integer, Integer> sinkEdgeTypesRectangleAndPrices = new LinkedHashMap<>();// <type, price*100>
    private Map<Integer, Integer> sinkEdgeTypesCircleAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String sinkEdgeTypeCurrency = "";

    private Map<Integer, String> sinkCommonCurrency = new LinkedHashMap<>();
    private Map<Integer, Integer> sinkCommonTypesAndPrices = new LinkedHashMap<>();

    private Map<Integer, Integer> cutoutTypesAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String cutoutCurrency = "";

    private Map<Integer, Integer> rodsTypesAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String rodsCurrency = "";

    private Map<Integer, Integer> groovesTypesAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String groovesCurrency = "";

    private Map<Integer, Integer> siphonsTypesAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String siphonsCurrency = "";

    private Map<Integer, Integer> jointsTypesAndPrices = new LinkedHashMap<>();// <type, price*100>
    private String jointsCurrency = "";

    private int radiusElementPrice = 0;
    private String radiusElementCurrency = "";

    private int stoneHemPrice = 0;//подгиб камня()
    private String stoneHemCurrency = "";//подгиб камня

    private int leakGroovePrice = 0;//выборка каплесборника
    private String leakGrooveCurrency = "";//выборка каплесборника

    private int stonePolishingPrice = 0;//полировка поверхности камня
    private String stonePolishingCurrency = "";//полировка поверхности камня

    private int manualLiftingPrice = 0;//ручной подъем

    private String manualLiftingCurrency = "";//ручной подъем

    private int measurerPrice = 0;//замерщик

    private String measurerCurrency = "";//замерщик

    private int measurerKMPrice = 0;//замерщик стоимсть километра при выезде за МКАД

    private String measurerKMCurrency = "";//замерщик стоимсть километра при выезде за МКАД

    private int deliveryInsideMKADPrice = 0;//доставка внутри МКАД

    private String deliveryInsideMKADCurrency = "";//доставка внутри МКАД

    private int deliveryFromManufacture = 0;//доставка от роизводителя

    private String deliveryFromManufactureCurrency = "";//доставка от производителя

    private int sheetCuttingPrice = 0;//стоимость отреза производителя

    private String sheetCuttingCurrency = "";//отреза производителя валюта

    private List<Integer> plywoodPrices = new ArrayList<>();
    private List<String> plywoodCurrency = new ArrayList<>();

    private List<Integer> metalFootingPrices = new ArrayList<>();
    private List<String> metalFootingCurrency = new ArrayList<>();

    private List<Integer> availableGroovesTypes = new ArrayList<>();
    private List<Integer> availableRodsTypes = new ArrayList<>();

    private Map<String, Integer> availableSinkModels = new LinkedHashMap<>();
    private Map<String, Double> availableGroovesModels = new LinkedHashMap<>();
    private Map<String, Double> availableRodsModels = new LinkedHashMap<>();

    //Pallets
    private Map<String, Integer> palletsModelsAndPrices = new LinkedHashMap<>();
    private String palletsCurrency;//EUR,RUB,USD

    //additionalJob:
    private String sinkInstallOperationCurrency;//EUR,RUB,USD
    private Map<String, Double> sinkInstallOperationTypes = new LinkedHashMap<>();

    //edgesHeights:
    private String edgesCurrency;//EUR,RUB,USD
    private Map<Integer, Double> edgesAndPrices = new LinkedHashMap<>();

    //borders prices:
    private String borderCurrency;//EUR,RUB,USD
    private Map<Integer, Integer> borderTypesAndPrices = new LinkedHashMap<>();
    private Map<Integer, Integer> borderTopCutTypesAndPrices = new LinkedHashMap<>();
    private Map<Integer, Integer> borderSideCutTypesAndPrices = new LinkedHashMap<>();

    private int notification1;
    private int notification2;

    private boolean promotion = false;

    // properties for filter materials at MaterialManager
    public static final String VIS_PROP_COLOR = "color"; //0 - index
    public static final String VIS_PROP_TEXTURE = "texture"; //1 - index
    public static final String VIS_PROP_SURFACE = "surface"; //2 - index
    private Map<String, String> visualProperties = new LinkedHashMap<>();

    private List<Material> analogsList = new ArrayList<>();

    public Material(String id, String mainType, String subType, String collection, String color, double width,
                    double height, String imgPath, List<String> depthsList) {
        this.id = id;
        this.mainType = mainType;
        this.subType = subType;
        this.collection = collection;
        this.color = color;
        this.materialWidth = width;
        this.materialHeight = height;
        this.imgPath = imgPath;
        this.depthsList = depthsList;

        if (mainType.equalsIgnoreCase("Акриловый камень")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Полиэфирный камень")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Кварцевый агломерат")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Dektone")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Кварцекерамический камень")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Мраморный агломерат")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Массив")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Массив_шпон")) availableMainSheetsCount = Integer.MAX_VALUE;
        if (mainType.equalsIgnoreCase("Натуральный камень")) availableMainSheetsCount = 0;

        if (depthsList.size() != 0) {
            for (String s : depthsList) {
                this.depths.add(s);
                depthsString += s + ",";
            }
            int l = depthsString.length();
            depthsString = depthsString.substring(0, l - 1);

            defaultDepth = Integer.parseInt(depths.get(0));
        }

        if (color.equalsIgnoreCase("другой") || depthsList.size() == 0) {
            template = true;
            useMainSheets = false;
            availableMainSheetsCount = 0;
        }

        name = mainType + "$" + subType + "$" + collection + "$" + color + "$" + width + "$" + height + "$" + imgPath + "$" + depthsString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image getTextureImage() {
        if (textureImage == null || textureImageUpdatePending) {
            ImageIndex imageIndex = ServiceLocator.getService("ImageIndex", ImageIndex.class);
            List<String> remoteImagePaths = imageIndex.get(this.id);

            if (remoteImagePaths == null || remoteImagePaths.isEmpty()) {
                try {
                    textureImage = new Image(new FileInputStream("materials_resources/no_image.png"));
                    textureImageUpdatePending = false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoader imageLoader = ServiceLocator.getService("ImageLoader", ImageLoader.class);
                textureImage = imageLoader.getImageByPath(remoteImagePaths.get(0));
                if (textureImage == null) {
                    try {
                        textureImage = new Image(new FileInputStream("materials_resources/no_image.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    textureImageUpdatePending = true;
                } else {
                    textureImageUpdatePending = false;
                }
            }
        }
        return textureImage;
    }

    public void setAvailableMainSheetsCount(int availableMainSheetsCount) {
        this.availableMainSheetsCount = availableMainSheetsCount;
    }

    public boolean isUseAdditionalSheets() {
        return useAdditionalSheets;
    }

    public void setUseAdditionalSheets(boolean useAdditionalSheets) {
        this.useAdditionalSheets = useAdditionalSheets;
    }

    public boolean isUseMainSheets() {
        return useMainSheets;
    }

    public void setUseMainSheets(boolean useMainSheets) {
        this.useMainSheets = useMainSheets;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public void setColor(String color) {
        this.color = color;
        name = mainType + "$" + subType + "$" + collection + "$" + color + "$" + materialWidth + "$" + materialHeight + "$" + imgPath + "$" + depthsString;
    }

    private MaterialSheet createMaterialSheet(int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, double customSheetPrice, String sheetCurrency, boolean additionalSheet) {
        MaterialSheet materialSheet = new MaterialSheet(this, depth, sheetWidth, sheetHeight, sheetMinWidth, sheetMinHeight, customSheetPrice, sheetCurrency, additionalSheet);

        //set prices:
        materialSheet.getTableTopDepthsAndPrices().clear();
        materialSheet.getWallPanelDepthsAndPrices().clear();
        materialSheet.getWindowSillDepthsAndPrices().clear();
        materialSheet.getFootDepthsAndPrices().clear();

        for (Map.Entry<Integer, Integer> entry : tableTopDepthsAndPrices.entrySet()) {
            materialSheet.getTableTopDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : wallPanelDepthsAndPrices.entrySet()) {
            materialSheet.getWallPanelDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : windowSillDepthsAndPrices.entrySet()) {
            materialSheet.getWindowSillDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : footDepthsAndPrices.entrySet()) {
            materialSheet.getFootDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }

        //set coefficients:
        materialSheet.getTableTopCoefficientList().clear();
        materialSheet.getWallPanelCoefficientList().clear();
        materialSheet.getWindowSillCoefficientList().clear();
        materialSheet.getFootCoefficientList().clear();

        for (Double coefficient : tableTopCoefficientList) {
            materialSheet.getTableTopCoefficientList().add(coefficient);
        }
        for (Double coefficient : wallPanelCoefficientList) {
            materialSheet.getWallPanelCoefficientList().add(coefficient);
        }
        for (Double coefficient : windowSillCoefficientList) {
            materialSheet.getWindowSillCoefficientList().add(coefficient);
        }
        for (Double coefficient : footCoefficientList) {
            materialSheet.getFootCoefficientList().add(coefficient);
        }

        return materialSheet;
    }

    public double getMinMaterialWidth() {
        return minMaterialWidth;
    }

    public double getMinMaterialHeight() {
        return minMaterialHeight;
    }

    public int getMinCountSlabs() {
        return minCountSlabs;
    }

    public boolean isHorizontalCuttingParts() {
        return horizontalCuttingParts;
    }

    public MaterialSheet createMainMaterialSheet(int depth) {
        return createMaterialSheet(depth, materialWidth, materialHeight, minMaterialWidth, minMaterialHeight, 0, currency, false);
    }

    public MaterialSheet createAdditionalMaterialSheet(int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, double customSheetPrice, String sheetCurrency) {
        MaterialSheet materialSheet = createMaterialSheet(depth, sheetWidth, sheetHeight, sheetMinWidth, sheetMinHeight, customSheetPrice, sheetCurrency, true);
        availableAdditionalSheets.add(materialSheet);
        materialSheet.setAdditionalSheet(true);

        //set coefficients from "ANother material":
        if (!isTemplate()) {
            Material anotherMaterialTemplate = null;
            for (Material m : Catalogs.getMaterialsListAvailable()) {
                if (m.getName().indexOf(getMainType() + "$" + getSubType() + "$" + getCollection() + "$" + "Другой") != -1) {
                    anotherMaterialTemplate = m;
                    break;
                }
            }
            if (anotherMaterialTemplate != null) {
                materialSheet.getTableTopCoefficientList().clear();
                materialSheet.getWallPanelCoefficientList().clear();
                materialSheet.getWindowSillCoefficientList().clear();
                materialSheet.getFootCoefficientList().clear();

                for (Double coefficient : anotherMaterialTemplate.getTableTopCoefficientList()) {
                    materialSheet.getTableTopCoefficientList().add(coefficient);
                }
                for (Double coefficient : anotherMaterialTemplate.getWallPanelCoefficientList()) {
                    materialSheet.getWallPanelCoefficientList().add(coefficient);
                }
                for (Double coefficient : anotherMaterialTemplate.getWindowSillCoefficientList()) {
                    materialSheet.getWindowSillCoefficientList().add(coefficient);
                }
                for (Double coefficient : anotherMaterialTemplate.getFootCoefficientList()) {
                    materialSheet.getFootCoefficientList().add(coefficient);
                }
            }
        }

        return materialSheet;
    }

    public int getAvailableMainSheetsCount() {
        return availableMainSheetsCount;
    }

    public List<MaterialSheet> getAvailableAdditionalSheets() {
        return availableAdditionalSheets;
    }

    public void setMinMaterialSize(int minMaterialWidth, int minMaterialHeight) {
        this.minMaterialWidth = minMaterialWidth;
        this.minMaterialHeight = minMaterialHeight;

        if (minMaterialHeight == materialHeight) {
            horizontalCuttingParts = false;
        } else {
            horizontalCuttingParts = true;
        }

        slabSquare = minMaterialWidth * minMaterialHeight;
        slabSquare = slabSquare / 1000000;//mm^2 to m^2
    }

    public void setMinCountSlabs(int minCountSlabs) {
        this.minCountSlabs = minCountSlabs;
    }

    public void setDefaultDepth(int defaultDepth) {
        this.defaultDepth = defaultDepth;
    }

    public int getDefaultDepth() {
        return defaultDepth;
    }

    public void setImgPath(String imagePath) {
        this.imgPath = imagePath;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCalculationType(int calculationType) {
        this.calculationType = calculationType;
    }

    public void setStoneHemCurrency(String stoneHemCurrency) {
        this.stoneHemCurrency = stoneHemCurrency;
    }

    public void setStoneHemPrice(int stoneHemPrice) {
        this.stoneHemPrice = stoneHemPrice;
    }

    public String getStoneHemCurrency() {
        return stoneHemCurrency;
    }

    public int getStoneHemPrice() {
        return stoneHemPrice;
    }

    public String getLeakGrooveCurrency() {
        return leakGrooveCurrency;
    }

    public int getLeakGroovePrice() {
        return leakGroovePrice;
    }

    public void setLeakGrooveCurrency(String leakGrooveCurrency) {
        this.leakGrooveCurrency = leakGrooveCurrency;
    }

    public void setLeakGroovePrice(int leakGroovePrice) {
        this.leakGroovePrice = leakGroovePrice;
    }

    public void setStonePolishingCurrency(String stonePolishingCurrency) {
        this.stonePolishingCurrency = stonePolishingCurrency;
    }

    public void setStonePolishingPrice(int stonePolishingPrice) {
        this.stonePolishingPrice = stonePolishingPrice;
    }

    public void setManualLiftingCurrency(String manualLiftingCurrency) {
        this.manualLiftingCurrency = manualLiftingCurrency;
    }

    public void setManualLiftingPrice(int manualLiftingPrice) {
        this.manualLiftingPrice = manualLiftingPrice;
    }

    public int getManualLiftingPrice() {
        return manualLiftingPrice;
    }

    public List<Integer> getMetalFootingPrices() {
        return metalFootingPrices;
    }

    public List<String> getMetalFootingCurrency() {
        return metalFootingCurrency;
    }

    public void setMetalFootingCurrency(List<String> metalFootingCurrency) {
        this.metalFootingCurrency = metalFootingCurrency;
    }

    public int getStonePolishingPrice() {
        return stonePolishingPrice;
    }

    public String getStonePolishingCurrency() {
        return stonePolishingCurrency;
    }

    public Map<Integer, Integer> getTableTopDepthsAndPrices() {
        return tableTopDepthsAndPrices;
    }

    public Map<Integer, Integer> getWallPanelDepthsAndPrices() {
        return wallPanelDepthsAndPrices;
    }

    public Map<Integer, Integer> getWindowSillDepthsAndPrices() {
        return windowSillDepthsAndPrices;
    }

    public Map<Integer, Integer> getFootDepthsAndPrices() {
        return footDepthsAndPrices;
    }

    public List<Double> getTableTopCoefficientList() {
        return tableTopCoefficientList;
    }

    public List<Double> getWallPanelCoefficientList() {
        return wallPanelCoefficientList;
    }

    public List<Double> getWindowSillCoefficientList() {
        return windowSillCoefficientList;
    }

    public List<Double> getFootCoefficientList() {
        return footCoefficientList;
    }

    public void setRadiusElementPrice(int radiusElementPrice) {
        this.radiusElementPrice = radiusElementPrice;
    }

    public void setRadiusElementCurrency(String radiusElementCurrency) {
        this.radiusElementCurrency = radiusElementCurrency;
    }

    public Map<Integer, String> getSinkCommonCurrency() {
        return sinkCommonCurrency;
    }

    public Map<Integer, Integer> getSinkCommonTypesAndPrices() {
        return sinkCommonTypesAndPrices;
    }

    public int getRadiusElementPrice() {
        return radiusElementPrice;
    }

    public String getRadiusElementCurrency() {
        return radiusElementCurrency;
    }

    public List<Material> getAnalogsList() {
        analogsList.remove(this);
        return analogsList;
    }

    public void setNotification1(int notification1) {
        this.notification1 = notification1;
    }

    public void setNotification2(int notification2) {
        this.notification2 = notification2;
    }

    public int getNotification1() {
        return notification1;
    }

    public int getNotification2() {
        return notification2;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public Map<String, String> getVisualProperties() {
        return visualProperties;
    }

    public String getManualLiftingCurrency() {
        return manualLiftingCurrency;
    }

    public String getMeasurerCurrency() {
        return measurerCurrency;
    }

    public String getMeasurerKMCurrency() {
        return measurerKMCurrency;
    }

    public String getDeliveryInsideMKADCurrency() {
        return deliveryInsideMKADCurrency;
    }

    public String getDeliveryFromManufactureCurrency() {
        return deliveryFromManufactureCurrency;
    }

    public String getSheetCuttingCurrency() {
        return sheetCuttingCurrency;
    }

    @Deprecated
    public String getImgPath() {
        return imgPath;
    }

    @Deprecated
    public ImageView getImageView() {
        String imgPath = "test.png";
        File f = new File("materials_resources/.");
        for (File imgF : f.listFiles()) {
            if (imgF.isFile() && imgF.getName().toLowerCase(Locale.ROOT).indexOf(subType.toLowerCase()) != -1) {
                imgPath = imgF.getName();
                break;
            }
        }
        try {
            File file;
            System.out.println("-------------------------------materialImage.getImageMaterial()----------------------" + textureImage);

            if (textureImage == null) {
                file = new File("materials_resources/no_img.png");
            } else {
                return new ImageView(textureImage);
            }
            return new ImageView(new Image(file.toURI().toURL().toString()));
        } catch (MalformedURLException ex) {
            System.err.println("Material can't open image file");
            return null;
        }
    }

    // TODO use general image resolution mechanism; don't use ImageView in the model
    public ImageView getImageViewLogo() {
        String imgPath = "test.png";
        File f = new File("materials_resources/.");
        for (File imgF : f.listFiles()) {
            if (imgF.isFile() && imgF.getName().toLowerCase(Locale.ROOT).contains(subType.toLowerCase())) {
                imgPath = imgF.getName();
                break;
            }
        }
        try {
            File file = new File("materials_resources/" + imgPath);
            if (!file.exists()) {
                return new ImageView(getClass().getResource("/styles/images/no_img_white.png").toString());
            }
            return new ImageView(new Image(file.toURI().toURL().toString()));
        } catch (MalformedURLException ex) {
            System.err.println("Material can't open image file");
            return null;
        }
    }

    public String getMainType() {
        return mainType;
    }

    public String getSubType() {
        return subType;
    }

    public String getCollection() {
        return collection;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getReceiptName() {
        return collection + " " + color;
    }

    public double getMaterialWidth() {
        return materialWidth;
    }

    public double getMaterialHeight() {
        return materialHeight;
    }

    public List<String> getDepths() {
        return depths;
    }

    public String getCurrency() {
        return currency;
    }

    public int getCalculationType() {
        return calculationType;
    }


    public double getPrice(ElementTypes elementType, int depth) {
        double price = 0;
        if (elementType == ElementTypes.TABLETOP) {
            price = (tableTopDepthsAndPrices.get(depth).doubleValue()) / 100.0;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            price = wallPanelDepthsAndPrices.get(depth).doubleValue() / 100.0;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            price = windowSillDepthsAndPrices.get(depth).doubleValue() / 100.0;
        } else if (elementType == ElementTypes.FOOT) {
            price = footDepthsAndPrices.get(depth).doubleValue() / 100.0;
        } else {
            price = 0;
        }

        double materialCoefficient = Project.getPriceMaterialCoefficient().doubleValue();
        double commonCoefficient = Project.getPriceMainCoefficient().doubleValue();

        price = price * materialCoefficient * commonCoefficient;
        return price;
    }

    public double getRawPrice(ElementTypes elementType, int depth) {
        double price = 0;
        if (elementType == ElementTypes.TABLETOP) {
            price = (tableTopDepthsAndPrices.get(depth).doubleValue()) / 100.0;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            price = wallPanelDepthsAndPrices.get(depth).doubleValue() / 100.0;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            price = windowSillDepthsAndPrices.get(depth).doubleValue() / 100.0;
        } else if (elementType == ElementTypes.FOOT) {
            price = footDepthsAndPrices.get(depth).doubleValue() / 100.0;
        } else {
            price = 0;
        }
        return price;
    }

    //additional features and types and prices:
    //for SINK:
    public List<Integer> getAvailableSinkTypes() {
        return availableSinkTypes;
    }

    public String getSinkCurrency() {
        return sinkCurrency;
    }

    public Map<Integer, Integer> getSinkInstallTypesAndPrices() {
        return sinkInstallTypesAndPrices;
    }

    public String getSinkInstallTypeCurrency() {
        return sinkInstallTypeCurrency;
    }

    public void setSinkInstallTypesAndPrices(Map<Integer, Integer> sinkInstallTypesAndPrices) {
        this.sinkInstallTypesAndPrices = sinkInstallTypesAndPrices;
    }

    public void setSinkCurrency(String sinkCurrency) {
        this.sinkCurrency = sinkCurrency;
    }

    public void setSinkInstallTypeCurrency(String sinkInstallTypeCurrency) {
        this.sinkInstallTypeCurrency = sinkInstallTypeCurrency;
    }

    public Map<Integer, Integer> getSinkEdgeTypesRectangleAndPrices() {
        return sinkEdgeTypesRectangleAndPrices;
    }

    public Map<Integer, Integer> getSinkEdgeTypesCircleAndPrices() {
        return sinkEdgeTypesCircleAndPrices;
    }

    public String getSinkEdgeTypeCurrency() {
        return sinkEdgeTypeCurrency;
    }

    public void setSinkEdgeTypeCurrency(String sinkEdgeTypeCurrency) {
        this.sinkEdgeTypeCurrency = sinkEdgeTypeCurrency;
    }

    public Map<Integer, Integer> getCutoutTypesAndPrices() {
        return cutoutTypesAndPrices;
    }

    public String getCutoutCurrency() {
        return cutoutCurrency;
    }

    public void setCutoutCurrency(String cutoutCurrency) {
        this.cutoutCurrency = cutoutCurrency;
    }

    public Map<Integer, Integer> getGroovesTypesAndPrices() {
        return groovesTypesAndPrices;
    }

    public String getGroovesCurrency() {
        return groovesCurrency;
    }

    public void setGroovesCurrency(String groovesCurrency) {
        this.groovesCurrency = groovesCurrency;
    }

    public Map<Integer, Integer> getRodsTypesAndPrices() {
        return rodsTypesAndPrices;
    }

    public void setRodsCurrency(String rodsCurrency) {
        this.rodsCurrency = rodsCurrency;
    }

    public String getRodsCurrency() {
        return rodsCurrency;
    }

    public Map<Integer, Integer> getSiphonsTypesAndPrices() {
        return siphonsTypesAndPrices;
    }

    public String getSiphonsCurrency() {
        return siphonsCurrency;
    }

    public void setSiphonsCurrency(String siphonsCurrency) {
        this.siphonsCurrency = siphonsCurrency;
    }

    public Map<Integer, Integer> getJointsTypesAndPrices() {
        return jointsTypesAndPrices;
    }

    public String getJointsCurrency() {
        return jointsCurrency;
    }

    public void setJointsCurrency(String jointsCurrency) {
        this.jointsCurrency = jointsCurrency;
    }

    public List<Integer> getPlywoodPrices() {
        return plywoodPrices;
    }

    public List<String> getPlywoodCurrency() {
        return plywoodCurrency;
    }

    public void setPlywoodCurrency(List<String> plywoodCurrency) {
        this.plywoodCurrency = plywoodCurrency;
    }

    public List<Integer> getAvailableGroovesTypes() {
        return availableGroovesTypes;
    }

    public List<Integer> getAvailableRodsTypes() {
        return availableRodsTypes;
    }

    public Map<String, Integer> getAvailableSinkModels() {
        return availableSinkModels;
    }

    public Map<String, Integer> getPalletsModelsAndPrices() {
        return palletsModelsAndPrices;
    }

    public String getPalletsCurrency() {
        return palletsCurrency;
    }

    public void setPalletsCurrency(String palletsCurrency) {
        this.palletsCurrency = palletsCurrency;
    }

    //edges and borders:
    public Map<Integer, Double> getEdgesAndPrices() {
        return edgesAndPrices;
    }

    public void setEdgesCurrency(String edgesCurrency) {
        this.edgesCurrency = edgesCurrency;
    }

    public String getEdgesCurrency() {
        return edgesCurrency;
    }

    public void setBorderCurrency(String borderCurrency) {
        this.borderCurrency = borderCurrency;
    }

    public String getBorderCurrency() {
        return borderCurrency;
    }

    public Map<Integer, Integer> getBorderTypesAndPrices() {
        return borderTypesAndPrices;
    }

    public Map<Integer, Integer> getBorderTopCutTypesAndPrices() {
        return borderTopCutTypesAndPrices;
    }

    public Map<Integer, Integer> getBorderSideCutTypesAndPrices() {
        return borderSideCutTypesAndPrices;
    }

    public void setMeasurerCurrency(String measurerCurrency) {
        this.measurerCurrency = measurerCurrency;
    }

    public void setMeasurerPrice(int measurerPrice) {
        this.measurerPrice = measurerPrice;
    }

    public int getMeasurerPrice() {
        return measurerPrice;
    }

    public void setMeasurerKMCurrency(String measurerKMCurrency) {
        this.measurerKMCurrency = measurerKMCurrency;
    }

    public void setMeasurerKMPrice(int measurerKMPrice) {
        this.measurerKMPrice = measurerKMPrice;
    }

    public int getMeasurerKMPrice() {
        return measurerKMPrice;
    }

    public void setDeliveryInsideMKADCurrency(String deliveryInsideMKADCurrency) {
        this.deliveryInsideMKADCurrency = deliveryInsideMKADCurrency;
    }

    public void setDeliveryInsideMKADPrice(int deliveryInsideMKADPrice) {
        this.deliveryInsideMKADPrice = deliveryInsideMKADPrice;
    }

    public int getDeliveryInsideMKADPrice() {
        return deliveryInsideMKADPrice;
    }

    public int getDeliveryFromManufacture() {
        return deliveryFromManufacture;
    }

    public void setDeliveryFromManufacture(int deliveryFromManufacture) {
        this.deliveryFromManufacture = deliveryFromManufacture;
    }

    public void setDeliveryFromManufactureCurrency(String deliveryFromManufactureCurrency) {
        this.deliveryFromManufactureCurrency = deliveryFromManufactureCurrency;
    }

    public void setSheetCuttingCurrency(String sheetCuttingCurrency) {
        this.sheetCuttingCurrency = sheetCuttingCurrency;
    }

    public void setSheetCuttingPrice(int sheetCuttingPrice) {
        this.sheetCuttingPrice = sheetCuttingPrice;
    }

    public int getSheetCuttingPrice() {
        return sheetCuttingPrice;
    }

}
