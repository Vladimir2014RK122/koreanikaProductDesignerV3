package ru.koreanika.Common.Material;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.Common.Connectible;
//import ru.koreanika.cutDesigner.CutSheet;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.utils.ProjectHandler;


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
    private String tableTopPrice;
    private String wallPanelPrice;
    private String windowsillPrice;
    private String currency;//EUR,RUB,USD

    ArrayList<String> depthsList;

    boolean template = false;

    private String imgPath;
    ArrayList<String> depths = new ArrayList<>();
    String depthsString = "";

//    ImageView materialImage;

    private MaterialImage materialImage;
    private boolean materialImageUpdatePending = false;

    double materialWidth = 0;//mm
    double materialHeight = 0;//mm

    double minMaterialWidth = 0;//mm
    double minMaterialHeight = 0;//mm

    double slabSquare = 0;
    int minCountSlabs = 1;
    boolean horizontalCuttingParts = true;

    int defaultDepth;

    boolean useAdditionalSheets = false;
    boolean useMainSheets = true;



    int availableMainSheetsCount;
    ArrayList<MaterialSheet> availableAdditionalSheets = new ArrayList<>();

    //private Map<Integer, Integer> depthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>

    private Map<Integer, Integer> tableTopDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private ArrayList<Double> tableTopCoefficientList = new ArrayList<>(3); //<Coefficient>

    private Map<Integer, Integer> wallPanelDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private ArrayList<Double> wallPanelCoefficientList = new ArrayList<>(3); //<Coefficient>

    private Map<Integer, Integer> windowSillDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private ArrayList<Double> windowSillCoefficientList = new ArrayList<>(3); //<Coefficient>

    private Map<Integer, Integer> footDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
    private ArrayList<Double> footCoefficientList = new ArrayList<>(3); //<Coefficient>

    //additional elements:
    private String sinkCurrency;//EUR,RUB,USD

    private ArrayList<Integer> availableSinkTypes = new ArrayList<>();
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

    private ArrayList<Integer> plywoodPrices = new ArrayList<>();
    private ArrayList<String> plywoodCurrency = new ArrayList<>();

    private ArrayList<Integer> metalFootingPrices = new ArrayList<>();
    private ArrayList<String> metalFootingCurrency = new ArrayList<>();

    private ArrayList<Integer> availableGroovesTypes = new ArrayList<>();
    private ArrayList<Integer> availableRodsTypes = new ArrayList<>();

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

    /** properties for filter materials at MaterialManager
     * --  **/

    public static final String VIS_PROP_COLOR = "color"; //0 - index
    public static final String VIS_PROP_TEXTURE = "texture"; //1 - index
    public static final String VIS_PROP_SURFACE = "surface"; //2 - index
    private LinkedHashMap<String, String> visualProperties = new LinkedHashMap<>();


    private ArrayList<Material> analogsList = new ArrayList<>();

    public Material(String id, String mainType, String subType, String collection, String color, double width, double height, String imgPath, ArrayList<String> depthsList) {
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

        if (this.id == null || this.id.isEmpty()) {
            // Old approach (no material IDs, resolving images by names, crazy caching logic)
            materialImage = new MaterialImage(this.mainType, this.subType, this.collection, this.color);
        } else {
            // New approach (using material IDs, pre-collected image index, caching image loader and lazy initialization)
            materialImage = null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MaterialImage getMaterialImage() {
        if (materialImage == null || materialImageUpdatePending) {
            ImageIndex imageIndex = ServiceLocator.getService("ImageIndex", ImageIndex.class);
            List<String> remoteImagePaths = imageIndex.get(this.id);

            if (remoteImagePaths == null || remoteImagePaths.isEmpty()) {
                try {
                    Image image = new Image(new FileInputStream("materials_resources/no_image.png"));
                    materialImage = new MaterialImage(image);
                    materialImageUpdatePending = false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoader imageLoader = ServiceLocator.getService("ImageLoader", ImageLoader.class);
                Image image = imageLoader.getImageByPath(remoteImagePaths.get(0));
                if (image == null) {
                    try {
                        image = new Image(new FileInputStream("materials_resources/no_image.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    materialImageUpdatePending = true;
                } else {
                    materialImageUpdatePending = false;
                }
                materialImage = new MaterialImage(image);
            }
        }
        return materialImage;
    }

    public void setAvailableMainSheetsCount(int availableMainSheetsCount) {
        this.availableMainSheetsCount = availableMainSheetsCount;
    }

    public void setDepthsList(ArrayList<String> depthsList) {

        this.depthsList = depthsList;

        if(depthsList.size()!=0){
            for (String s : depthsList) {
                this.depths.add(s);
                depthsString += s + ",";
            }
            int l = depthsString.length();
            depthsString = depthsString.substring(0, l - 1);

            defaultDepth = Integer.parseInt(depths.get(0));
        }

        name = mainType + "$" + subType + "$" + collection + "$" + color + "$" + materialWidth + "$" + materialHeight + "$" + imgPath + "$" + depthsString;

    }

    public void setDepths(ArrayList<String> depths) {
        this.depths = depths;
    }

    public void setDepthsString(String depthsString) {
        this.depthsString = depthsString;
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

    private Material.MaterialSheet createMaterialSheet(int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, double customSheetPrice, String sheetCurrency, boolean additionalSheet) {
        Material.MaterialSheet materialSheet = new MaterialSheet(depth, sheetWidth, sheetHeight, sheetMinWidth, sheetMinHeight, customSheetPrice, sheetCurrency, additionalSheet);


        //set prices:
        materialSheet.getTableTopDepthsAndPrices().clear();
        materialSheet.getWallPanelDepthsAndPrices().clear();
        materialSheet.getWindowSillDepthsAndPrices().clear();
        materialSheet.getFootDepthsAndPrices().clear();

        for(Map.Entry<Integer, Integer> entry : tableTopDepthsAndPrices.entrySet()){
            materialSheet.getTableTopDepthsAndPrices().put(new Integer(entry.getKey()), new Integer(entry.getValue()));
        }
        for(Map.Entry<Integer, Integer> entry : wallPanelDepthsAndPrices.entrySet()){
            materialSheet.getWallPanelDepthsAndPrices().put(new Integer(entry.getKey()), new Integer(entry.getValue()));
        }
        for(Map.Entry<Integer, Integer> entry : windowSillDepthsAndPrices.entrySet()){
            materialSheet.getWindowSillDepthsAndPrices().put(new Integer(entry.getKey()), new Integer(entry.getValue()));
        }
        for(Map.Entry<Integer, Integer> entry : footDepthsAndPrices.entrySet()){
            materialSheet.getFootDepthsAndPrices().put(new Integer(entry.getKey()), new Integer(entry.getValue()));
        }

        //set coefficients:
        materialSheet.getTableTopCoefficientList().clear();
        materialSheet.getWallPanelCoefficientList().clear();
        materialSheet.getWindowSillCoefficientList().clear();
        materialSheet.getFootCoefficientList().clear();

        for(Double coefficient : tableTopCoefficientList){
            materialSheet.getTableTopCoefficientList().add(new Double(coefficient));
        }
        for(Double coefficient : wallPanelCoefficientList){
            materialSheet.getWallPanelCoefficientList().add(new Double(coefficient));
        }
        for(Double coefficient : windowSillCoefficientList){
            materialSheet.getWindowSillCoefficientList().add(new Double(coefficient));
        }
        for(Double coefficient : footCoefficientList){
            materialSheet.getFootCoefficientList().add(new Double(coefficient));
        }

        return materialSheet;
    }

    public Material.MaterialSheet createMainMaterialSheet(int depth){

        return createMaterialSheet(depth,materialWidth,materialHeight, minMaterialWidth, minMaterialHeight, 0, currency, false);
    }

    public Material.MaterialSheet createAdditionalMaterialSheet(int depth, double sheetWidth,double sheetHeight, double sheetMinWidth, double sheetMinHeight, double customSheetPrice, String sheetCurrency){
        MaterialSheet materialSheet = createMaterialSheet(depth,sheetWidth,sheetHeight, sheetMinWidth, sheetMinHeight, customSheetPrice, sheetCurrency, true);
        availableAdditionalSheets.add(materialSheet);
        materialSheet.setAdditionalSheet(true);

        //set coefficients from "ANother material":
        if(!isTemplate()){
            Material anotherMaterialTemplate = null;
            for(Material m : ProjectHandler.getMaterialsListAvailable()){
                if(m.getName().indexOf(getMainType() + "$" + getSubType() + "$" + getCollection() + "$" + "Другой") != -1){
                    anotherMaterialTemplate = m;
                    break;
                }
            }
            if(anotherMaterialTemplate != null){
                materialSheet.getTableTopCoefficientList().clear();
                materialSheet.getWallPanelCoefficientList().clear();
                materialSheet.getWindowSillCoefficientList().clear();
                materialSheet.getFootCoefficientList().clear();

                for(Double coefficient : anotherMaterialTemplate.getTableTopCoefficientList()){
                    materialSheet.getTableTopCoefficientList().add(new Double(coefficient));
                }
                for(Double coefficient : anotherMaterialTemplate.getWallPanelCoefficientList()){
                    materialSheet.getWallPanelCoefficientList().add(new Double(coefficient));
                }
                for(Double coefficient : anotherMaterialTemplate.getWindowSillCoefficientList()){
                    materialSheet.getWindowSillCoefficientList().add(new Double(coefficient));
                }
                for(Double coefficient : anotherMaterialTemplate.getFootCoefficientList()){
                    materialSheet.getFootCoefficientList().add(new Double(coefficient));
                }
            }
        }





        return materialSheet;
    }

    public int getAvailableMainSheetsCount() {
        return availableMainSheetsCount;
    }

    public ArrayList<MaterialSheet> getAvailableAdditionalSheets() {
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

    public double getMinMaterialHeight() {
        return minMaterialHeight;
    }

    public double getMinMaterialWidth() {
        return minMaterialWidth;
    }

    public void setMinCountSlabs(int minCountSlabs) {
        this.minCountSlabs = minCountSlabs;
    }

    public int getMinCountSlabs() {
        return minCountSlabs;
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

    public String getManualLiftingCurrency() {
        return manualLiftingCurrency;
    }


    public ArrayList<Integer> getMetalFootingPrices() {
        return metalFootingPrices;
    }

    public ArrayList<String> getMetalFootingCurrency() {
        return metalFootingCurrency;
    }

    public void setMetalFootingPrices(ArrayList<Integer> metalFootingPrices) {
        this.metalFootingPrices = metalFootingPrices;
    }

    public void setMetalFootingCurrency(ArrayList<String> metalFootingCurrency) {
        this.metalFootingCurrency = metalFootingCurrency;
    }

    public void setTableTopDepthsAndPrices(Map<Integer, Integer> tableTopDepthsAndPrices) {
        this.tableTopDepthsAndPrices = tableTopDepthsAndPrices;
    }

    public int getStonePolishingPrice() {
        return stonePolishingPrice;
    }

    public String getStonePolishingCurrency() {
        return stonePolishingCurrency;
    }

    public void setWallPanelDepthsAndPrices(Map<Integer, Integer> wallPanelDepthsAndPrices) {
        this.wallPanelDepthsAndPrices = wallPanelDepthsAndPrices;
    }

    public void setWindowSillDepthsAndPrices(Map<Integer, Integer> windowSillDepthsAndPrices) {
        this.windowSillDepthsAndPrices = windowSillDepthsAndPrices;
    }

    public void setFootDepthsAndPrices(Map<Integer, Integer> footDepthsAndPrices) {
        this.footDepthsAndPrices = footDepthsAndPrices;
    }

    public void setTableTopCoefficientList(ArrayList<Double> tableTopCoefficientList) {
        this.tableTopCoefficientList = tableTopCoefficientList;
    }

    public void setWindowSillCoefficientList(ArrayList<Double> windowSillCoefficientList) {
        this.windowSillCoefficientList = windowSillCoefficientList;
    }

    public void setWallPanelCoefficientList(ArrayList<Double> wallPanelCoefficientList) {
        this.wallPanelCoefficientList = wallPanelCoefficientList;
    }

    public void setFootCoefficientList(ArrayList<Double> footCoefficientList) {
        this.footCoefficientList = footCoefficientList;
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

    public ArrayList<Double> getTableTopCoefficientList() {
        return tableTopCoefficientList;
    }

    public ArrayList<Double> getWallPanelCoefficientList() {
        return wallPanelCoefficientList;
    }

    public ArrayList<Double> getWindowSillCoefficientList() {
        return windowSillCoefficientList;
    }

    public ArrayList<Double> getFootCoefficientList() {
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

    public ArrayList<Material> getAnalogsList() {
        analogsList.remove(this);
        return analogsList;
    }

    public void setAnalogsList(ArrayList<Material> analogsList) {
        this.analogsList = analogsList;
    }

    public void setPrice(String price, ElementTypes elementType) {
        if (elementType == ElementTypes.TABLETOP) {
            this.tableTopPrice = price;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            this.wallPanelPrice = price;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            this.windowsillPrice = price;
        }

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

    public LinkedHashMap<String, String> getVisualProperties() {
        return visualProperties;
    }

    public String getImgPath() {
        return imgPath;
    }

    public ImageView getImageView() {

        String imgPath = "test.png";
//        String subType = row.getCell(1).getStringCellValue();
        File f = new File("materials_resources/.");
        for(File imgF : f.listFiles()){
            if(imgF.isFile() && imgF.getName().toLowerCase(Locale.ROOT).indexOf(subType.toLowerCase()) != -1){
                imgPath = imgF.getName();
                break;
            }
        }
         //System.out.println("name = " + name + " imgPath = " + imgPath);
        try {
            File file;
            System.out.println("-------------------------------materialImage.getImageMaterial()----------------------" + materialImage.getImageMaterial());

            if(materialImage.getImageMaterial() == null){
                file = new File("materials_resources/no_img.png");
            }else{
//                file = new File(materialImage.getImageMaterial());
                return new ImageView(materialImage.getImageMaterial());
            }


            //System.out.println("IMAGE MATERIAL = " + imgPath);
            return new ImageView(new Image(file.toURI().toURL().toString()));
        } catch (MalformedURLException ex) {
            System.err.println("Material can't open image file");
            return null;
        }


    }

    public ImageView getImageViewLogo() {


        String imgPath = "test.png";
//        String subType = row.getCell(1).getStringCellValue();
        File f = new File("materials_resources/.");
        for(File imgF : f.listFiles()){
            if(imgF.isFile() && imgF.getName().toLowerCase(Locale.ROOT).indexOf(subType.toLowerCase()) != -1){
                imgPath = imgF.getName();
                break;
            }
        }
        //System.out.println("name = " + name + " imgPath = " + imgPath);
        try {
            File file = new File("materials_resources/" + imgPath);
            if (!file.exists()) {
                file = new File("materials_resources/no_img.png");
                return new ImageView(getClass().getResource("/styles/images/no_img_white.png").toString());
            }
            //System.out.println("IMAGE MATERIAL = " + imgPath);
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


    public double getSlabSquare() {
        return slabSquare;
    }

    public double getMaterialWidth() {
        return materialWidth;
    }

    public double getMaterialHeight() {
        return materialHeight;
    }

    public ArrayList<String> getDepths() {


        return depths;
    }

    public String getCurrency() {
        return currency;
    }

    public int getCalculationType() {
        return calculationType;
    }


    public double getPrice(ElementTypes elementType, int depth) {

//        System.out.println("tableTopDepthsAndPrices = " + tableTopDepthsAndPrices);
//        System.out.println("wallPanelDepthsAndPrices = " + wallPanelDepthsAndPrices);
//        System.out.println("windowSillDepthsAndPrices = " + windowSillDepthsAndPrices);
//        System.out.println("footDepthsAndPrices = " + footDepthsAndPrices);

        double price = 0;
        if (elementType == ElementTypes.TABLETOP) {

            //System.out.println(tableTopDepthsAndPrices);
            //System.out.println(depth);
            //System.out.println(this);
            price = (tableTopDepthsAndPrices.get(new Integer(depth)).doubleValue()) / 100.0;


        } else if (elementType == ElementTypes.WALL_PANEL) {

            price = wallPanelDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;


        } else if (elementType == ElementTypes.WINDOWSILL) {

            price = windowSillDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;
        } else if (elementType == ElementTypes.FOOT) {

            price = footDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;

        } else {
            price = 0;
        }

        //price = depthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;

        double materialCoefficient = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
        double commonCoefficient = ProjectHandler.getPriceMainCoefficient().doubleValue();

        price = price * materialCoefficient * commonCoefficient;
        return price;
    }

    public double getRawPrice(ElementTypes elementType, int depth) {

//        System.out.println("tableTopDepthsAndPrices = " + tableTopDepthsAndPrices);
//        System.out.println("wallPanelDepthsAndPrices = " + wallPanelDepthsAndPrices);
//        System.out.println("windowSillDepthsAndPrices = " + windowSillDepthsAndPrices);
//        System.out.println("footDepthsAndPrices = " + footDepthsAndPrices);

        double price = 0;
        if (elementType == ElementTypes.TABLETOP) {

            //System.out.println(tableTopDepthsAndPrices);
            //System.out.println(depth);
            //System.out.println(this);
            price = (tableTopDepthsAndPrices.get(new Integer(depth)).doubleValue()) / 100.0;


        } else if (elementType == ElementTypes.WALL_PANEL) {

            price = wallPanelDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;


        } else if (elementType == ElementTypes.WINDOWSILL) {

            price = windowSillDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;
        } else if (elementType == ElementTypes.FOOT) {

            price = footDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;

        } else {
            price = 0;
        }

        //price = depthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;

        double materialCoefficient = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
        double commonCoefficient = ProjectHandler.getPriceMainCoefficient().doubleValue();

        //price = price * materialCoefficient * commonCoefficient;
        return price;
    }


    //additional features and types and prices:
    //for SINK:
    public ArrayList<Integer> getAvailableSinkTypes() {
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

    public ArrayList<Integer> getPlywoodPrices() {
        return plywoodPrices;
    }

    public ArrayList<String> getPlywoodCurrency() {
        return plywoodCurrency;
    }

    public void setPlywoodPrices(ArrayList<Integer> plywoodPrices) {
        this.plywoodPrices = plywoodPrices;
    }

    public void setPlywoodCurrency(ArrayList<String> plywoodCurrency) {
        this.plywoodCurrency = plywoodCurrency;
    }

    public ArrayList<Integer> getAvailableGroovesTypes() {
        return availableGroovesTypes;
    }

    public ArrayList<Integer> getAvailableRodsTypes() {
        return availableRodsTypes;
    }

    public Map<String, Integer> getAvailableSinkModels() {
        return availableSinkModels;
    }

    public Map<String, Double> getAvailableGroovesModels() {
        return availableGroovesModels;
    }

    public Map<String, Double> getAvailableRodsModels() {
        return availableRodsModels;
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

    //additional features install operations and prices:
    public void setSinkInstallOperationCurrency(String sinkInstallOperationCurrency) {
        this.sinkInstallOperationCurrency = sinkInstallOperationCurrency;
    }

    public String getSinkInstallOperationCurrency() {
        return sinkInstallOperationCurrency;
    }

    public Map<String, Double> getSinkInstallOperationTypes() {
        return sinkInstallOperationTypes;
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

    public void setBorderTypesAndPrices(Map<Integer, Integer> borderTypesAndPrices) {
        this.borderTypesAndPrices = borderTypesAndPrices;
    }

    public void setBorderTopCutTypesAndPrices(Map<Integer, Integer> borderTopCutTypesAndPrices) {
        this.borderTopCutTypesAndPrices = borderTopCutTypesAndPrices;
    }

    public void setBorderSideCutTypesAndPrices(Map<Integer, Integer> borderSideCutTypesAndPrices) {
        this.borderSideCutTypesAndPrices = borderSideCutTypesAndPrices;
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

    public static Material parseMaterial(String str) throws NumberFormatException {
        String[] materialStrArrWithDepth = str.split("#");
        int depth = Integer.parseInt(materialStrArrWithDepth[1]);

        //System.out.println();
        String[] materialStrArr = materialStrArrWithDepth[0].split("\\$");
        String mainType = materialStrArr[0];
        String subType = materialStrArr[1];
        String collection = materialStrArr[2];
        String color = materialStrArr[3];
        double width = Double.parseDouble(materialStrArr[4]);
        double height = Double.parseDouble(materialStrArr[5]);

        String imgPath;
        if (materialStrArr.length == 8) imgPath = materialStrArr[6];
        else imgPath = "no_img.png";

        String depthsString = materialStrArr[7];
        String[] depthsArr = depthsString.split(",");
        ArrayList<String> depths = new ArrayList<>();
        for (int i = 0; i < depthsArr.length; i++) {
            depths.add(depthsArr[i]);
        }

        //Material material = new Material(mainType, subType, collection, color, width, height, imgPath, depths);
        //material.setDefaultDepth(depth);

        String nameForFind = mainType + "$" + subType + "$" + collection + "$" + color + "$" + width + "$" + height + "$" + imgPath + "$" + depthsString;
        //try to find material in available list:
        int index =-1;

        //System.out.println("ProjectHandler.getMaterialsListAvailable().size() = " + ProjectHandler.getMaterialsListAvailable().size());

        for(Material m : ProjectHandler.getMaterialsListAvailable()){


            if(m.getName().indexOf(nameForFind) != -1){
                index = ProjectHandler.getMaterialsListAvailable().indexOf(m);


            }
        }
        System.out.println();
        //int index = ProjectHandler.getMaterialsListAvailable().indexOf(material);
        //System.out.println(index);
        if(index == -1) return null;
        return ProjectHandler.getMaterialsListAvailable().get(index);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Material material = (Material) o;
//        return Double.compare(material.materialWidth, materialWidth) == 0 &&
//                Double.compare(material.materialHeight, materialHeight) == 0 &&
//                Objects.equals(mainType, material.mainType) &&
//                Objects.equals(subType, material.subType) &&
//                Objects.equals(collection, material.collection) &&
//                Objects.equals(color, material.color) &&
//                Objects.equals(imgPath, material.imgPath) &&
//                Objects.equals(depths, material.depths);
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(mainType, subType, collection, color, imgPath, depths, materialWidth, materialHeight);
//    }

    public void setMeasurerCurrency(String measurerCurrency) {
        this.measurerCurrency = measurerCurrency;
    }

    public String getMeasurerCurrency() {
        return measurerCurrency;
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

    public String getMeasurerKMCurrency() {
        return measurerKMCurrency;
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

    public String getDeliveryInsideMKADCurrency() {
        return deliveryInsideMKADCurrency;
    }

    public int getDeliveryFromManufacture() {
        return deliveryFromManufacture;
    }

    public String getDeliveryFromManufactureCurrency() {
        return deliveryFromManufactureCurrency;
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

    public String getSheetCuttingCurrency() {
        return sheetCuttingCurrency;
    }


    public Material copyMaterial(String color, double width, double height, String imgPath, ArrayList<String> depthsList){
        return copyMaterial(this.id, this.mainType, this.subType, this.collection, color, width, height, imgPath, depthsList);
    }

    private Material copyMaterial(String id, String mainType, String subType, String collection, String color, double width, double height, String imgPath, ArrayList<String> depthsList){
        ArrayList<String> depthsCopy = new ArrayList<>();
        for(String s : depthsList){
            depthsCopy.add(new String(s));
        }
        //System.out.println("MATERIAL = " + this);
        Material materialCopy = new Material(id, mainType, subType, collection, color, width, height, imgPath, depthsCopy);
        //System.out.println("materialCopy = " + materialCopy);
        materialCopy.setCurrency("RUB");


        //depths and prices:
        for(Map.Entry<Integer, Integer> entry : tableTopDepthsAndPrices.entrySet()){
            materialCopy.getTableTopDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : wallPanelDepthsAndPrices.entrySet()){
            materialCopy.getWallPanelDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : windowSillDepthsAndPrices.entrySet()){
            materialCopy.getWindowSillDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : footDepthsAndPrices.entrySet()){
            materialCopy.getFootDepthsAndPrices().put(entry.getKey(), entry.getValue());
        }

        //coefficients:
        for(Double coefficient : tableTopCoefficientList){
            materialCopy.getTableTopCoefficientList().add(new Double(coefficient));
        }
        for(Double coefficient : wallPanelCoefficientList){
            materialCopy.getWallPanelCoefficientList().add(new Double(coefficient));
        }
        for(Double coefficient : windowSillCoefficientList){
            materialCopy.getWindowSillCoefficientList().add(new Double(coefficient));
        }
        for(Double coefficient : footCoefficientList){
            materialCopy.getFootCoefficientList().add(new Double(coefficient));
        }

        materialCopy.setCalculationType(calculationType);
        materialCopy.setMinMaterialSize((int)minMaterialWidth, (int)minMaterialHeight);
        materialCopy.setMinCountSlabs(minCountSlabs);

        //sink:
        for(Integer type : availableSinkTypes){
            materialCopy.getAvailableSinkTypes().add(new Integer(type));
        }
        for(Map.Entry<String, Integer> entry : availableSinkModels.entrySet()){
            materialCopy.getAvailableSinkModels().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSinkCurrency(sinkCurrency);

        for(Map.Entry<Integer, Integer> entry : sinkCommonTypesAndPrices.entrySet()){
            materialCopy.getSinkCommonTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, String> entry : sinkCommonCurrency.entrySet()){
            materialCopy.getSinkCommonCurrency().put(entry.getKey(), entry.getValue());
        }

        //sinks installTypes:
        for(Map.Entry<Integer, Integer> entry : sinkInstallTypesAndPrices.entrySet()){
            materialCopy.getSinkInstallTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSinkInstallTypeCurrency(sinkInstallTypeCurrency);

        //sink edges:
        for(Map.Entry<Integer, Integer> entry : sinkEdgeTypesRectangleAndPrices.entrySet()){
            materialCopy.getSinkEdgeTypesRectangleAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : sinkEdgeTypesCircleAndPrices.entrySet()){
            materialCopy.getSinkEdgeTypesCircleAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSinkEdgeTypeCurrency(sinkEdgeTypeCurrency);

        //grooves:
        for(Map.Entry<Integer, Integer> entry : groovesTypesAndPrices.entrySet()){
            materialCopy.getGroovesTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Integer type : availableGroovesTypes){
            materialCopy.getAvailableGroovesTypes().add(new Integer(type));
        }
        materialCopy.setGroovesCurrency(groovesCurrency);

        //rods
        for(Map.Entry<Integer, Integer> entry : rodsTypesAndPrices.entrySet()){
            materialCopy.getRodsTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Integer type : availableRodsTypes){
            materialCopy.getAvailableRodsTypes().add(new Integer(type));
        }
        materialCopy.setRodsCurrency(rodsCurrency);

        //edges
        for(Map.Entry<Integer, Double> entry : edgesAndPrices.entrySet()){
            materialCopy.getEdgesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setEdgesCurrency(edgesCurrency);


        //borders
        for(Map.Entry<Integer, Integer> entry : borderTypesAndPrices.entrySet()){
            materialCopy.getBorderTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : borderTopCutTypesAndPrices.entrySet()){
            materialCopy.getBorderTopCutTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Integer, Integer> entry : borderSideCutTypesAndPrices.entrySet()){
            materialCopy.getBorderSideCutTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setBorderCurrency(borderCurrency);

        //cutout:
        for(Map.Entry<Integer, Integer> entry : cutoutTypesAndPrices.entrySet()){
            materialCopy.getCutoutTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setCutoutCurrency(cutoutCurrency);

        //siphons:
        for(Map.Entry<Integer, Integer> entry : siphonsTypesAndPrices.entrySet()){
            materialCopy.getSiphonsTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setSiphonsCurrency(siphonsCurrency);

        //Joints:
        for(Map.Entry<Integer, Integer> entry : jointsTypesAndPrices.entrySet()){
            materialCopy.getJointsTypesAndPrices().put(entry.getKey(), entry.getValue());
        }
        materialCopy.setJointsCurrency(jointsCurrency);

        //plywoods:
        for(Integer price : plywoodPrices){
            materialCopy.getPlywoodPrices().add(new Integer(price));
        }
        materialCopy.setPlywoodCurrency(plywoodCurrency);

        //stone polishing
        materialCopy.setStonePolishingPrice(stonePolishingPrice);
        materialCopy.setStonePolishingCurrency(stonePolishingCurrency);

        //metalFooting
        for(Integer price : metalFootingPrices){
            materialCopy.getMetalFootingPrices().add(new Integer(price));
        }
        materialCopy.setMetalFootingCurrency(metalFootingCurrency);

        //radius element
        materialCopy.setStoneHemPrice(stoneHemPrice);
        materialCopy.setStoneHemCurrency(stoneHemCurrency);

        //leak groove
        materialCopy.setLeakGroovePrice(leakGroovePrice);
        materialCopy.setLeakGrooveCurrency(leakGrooveCurrency);

        //manual lifting
        materialCopy.setManualLiftingCurrency(manualLiftingCurrency);
        materialCopy.setManualLiftingPrice(manualLiftingPrice);

        //delivery price for inside MKAD
        materialCopy.setDeliveryInsideMKADCurrency(deliveryInsideMKADCurrency);
        materialCopy.setDeliveryInsideMKADPrice(deliveryInsideMKADPrice);

        //measurer
        materialCopy.setMeasurerCurrency(measurerCurrency);
        materialCopy.setMeasurerPrice(measurerPrice);

        //measurer price for km  outside MKAD:
        materialCopy.setMeasurerKMCurrency(measurerKMCurrency);
        materialCopy.setMeasurerKMPrice(measurerKMPrice);

        //delivery price from manufacture
        materialCopy.setDeliveryFromManufactureCurrency(deliveryFromManufactureCurrency);
        materialCopy.setDeliveryFromManufacture(deliveryFromManufacture);

        //sheet cutting price from manufacture
        materialCopy.setSheetCuttingCurrency(sheetCuttingCurrency);
        materialCopy.setSheetCuttingPrice(sheetCuttingPrice);

        materialCopy.setRadiusElementPrice(radiusElementPrice);
        materialCopy.setRadiusElementCurrency(radiusElementCurrency);

        // add notification 1:
        {
            materialCopy.setNotification1(notification1);
        }

        // add notification 2:
        {
            materialCopy.setNotification2(notification2);
        }

        return materialCopy;
    }


    public JSONObject getJsonView(){

        JSONObject materialObject = new JSONObject();
        materialObject.put("id", id);
        materialObject.put("name", name);
        materialObject.put("defaultDepth", getDefaultDepth());
        materialObject.put("useMainSheets", isUseMainSheets());
        materialObject.put("useAdditionalSheets", isUseAdditionalSheets());
        materialObject.put("availableMainSheetsCount", getAvailableMainSheetsCount());

        JSONArray additionalSheetsJsonArray = new JSONArray();

        for(MaterialSheet materialSheet : availableAdditionalSheets){
            JSONObject sheetObject = new JSONObject();

            sheetObject.put("width", materialSheet.getSheetWidth());
            sheetObject.put("height", materialSheet.getSheetHeight());
            sheetObject.put("depth", materialSheet.getSheetDepth());
            sheetObject.put("priceForMeter", materialSheet.sheetCustomPriceForMeter);
            sheetObject.put("currency", materialSheet.sheetCurrency);

            additionalSheetsJsonArray.add(sheetObject);
        }
        materialObject.put("additionalSheets", additionalSheetsJsonArray);

        return  materialObject;
    }

    public static Material getFromJson(JSONObject materialObject) {
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
            for (Material m : ProjectHandler.getMaterialsListAvailable()) {
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
                for (Material m : ProjectHandler.getMaterialsListAvailable()) {
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
            material = templateMaterial.copyMaterial(name.split("\\$")[3], 1000, 1000, templateMaterial.imgPath, depthsList);
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

            for (Material m : ProjectHandler.getMaterialsListAvailable()) {
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
//                System.out.println(m.getName());
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

            double sheetWidth = ((Double) (sheetObject.get("width"))).doubleValue();
            double sheetHeight = ((Double) (sheetObject.get("height"))).doubleValue();
            int sheetDepth = ((Long) (sheetObject.get("depth"))).intValue();
            double sheetCustomPriceForMeter = ((Double) (sheetObject.get("priceForMeter"))).doubleValue();
            String currency = (String) (sheetObject.get("currency"));

            material.createAdditionalMaterialSheet(sheetDepth, sheetWidth, sheetHeight, sheetWidth, sheetHeight, sheetCustomPriceForMeter, currency);

        }

        material.setUseMainSheets(useMainSheets);
        material.setUseAdditionalSheets(useAdditionalSheets);

        return material;
    }



    public class MaterialSheet extends Pane implements Connectible {

        int partsOfSheet = 1;
        int usesList = 0; //0- not uses, 1 part, 2 - parts;
        double materialScale = 0.1;

        double sheetWidth;//mm
        double sheetHeight;//mm

        double sheetMinWidth;//mm
        double sheetMinHeight;//mm
        int sheetDepth = defaultDepth;


        double sheetCustomPriceForMeter = 0;
        String sheetCurrency = "RUB";

        double sheetSquare = 0;
        double minSheetSquare = 0;

        boolean additionalSheet = false;

        Label labelSize = new Label();

        /* FIXED prices and coefficients START */

        boolean actualPrice = true;

        private Map<Integer, Integer> tableTopDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
        private Map<Integer, Integer> wallPanelDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
        private Map<Integer, Integer> windowSillDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>
        private Map<Integer, Integer> footDepthsAndPrices = new LinkedHashMap<>(); //<Depth, Price*100>

        private ArrayList<Double> tableTopCoefficientList = new ArrayList<>(3); //<Coefficient>
        private ArrayList<Double> wallPanelCoefficientList = new ArrayList<>(3); //<Coefficient>
        private ArrayList<Double> windowSillCoefficientList = new ArrayList<>(3); //<Coefficient>
        private ArrayList<Double> footCoefficientList = new ArrayList<>(3); //<Coefficient>

        /* FIXED prices and coefficients END */



        Polygon polygon = new Polygon();
        Polygon hideHorizontalPolygon1 = new Polygon();
        Polygon hideHorizontalPolygon2 = new Polygon();

        Polygon hideVerticalPolygon1 = new Polygon();
        Polygon hideVerticalPolygon2 = new Polygon();
        Polygon hideVerticalPolygon3 = new Polygon();
        Polygon hideVerticalPolygon4 = new Polygon();


        //connect points:
        CornerConnectPoint leftUpConnectPoint = new CornerConnectPoint(this);
        CornerConnectPoint leftDownConnectPoint = new CornerConnectPoint(this);
        CornerConnectPoint rightUpConnectPoint = new CornerConnectPoint(this);
        CornerConnectPoint rightDownConnectPoint = new CornerConnectPoint(this);

        ArrayList<ConnectPoint> connectPointArrayList = new ArrayList<>();

        public MaterialSheet(int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, boolean additionalSheet) {

            this(depth, sheetWidth, sheetHeight,sheetMinWidth, sheetMinHeight, 0.0, currency, additionalSheet);
        }

        public MaterialSheet(int depth, double sheetWidth, double sheetHeight, double sheetMinWidth, double sheetMinHeight, double sheetCustomPriceForMeter, String currency, boolean additionalSheet) {
            this.sheetDepth = depth;
            this.sheetWidth = sheetWidth;
            this.sheetHeight = sheetHeight;
            this.sheetMinWidth = sheetMinWidth;
            this.sheetMinHeight = sheetMinHeight;
            setPrefWidth(sheetWidth * materialScale);
            setPrefHeight(sheetHeight * materialScale);

            this.sheetCustomPriceForMeter = sheetCustomPriceForMeter;
            this.sheetCurrency = currency;
            this.additionalSheet = additionalSheet;


//            this.setOnContextMenuRequested(event -> {
//                ContextMenu contextMenu = new ContextMenu();
//                MenuItem menuItem1 = new MenuItem("menu1");
//                MenuItem menuItem2 = new MenuItem("menu2");
//                MenuItem menuItem3 = new MenuItem("menu3");
//                contextMenu.getItems().add(menuItem1);
//                contextMenu.getItems().add(menuItem2);
//                contextMenu.getItems().add(menuItem3);
//                contextMenu.show(this, event.getX(), event.getY());
//            });





            sheetSquare = (this.sheetWidth * this.sheetHeight);
            minSheetSquare = (sheetMinWidth * sheetMinHeight);

            partsOfSheet = (int)(((sheetHeight / 1000) * (sheetWidth / 1000)) / ((sheetMinWidth / 1000) * (sheetMinHeight / 1000)));
            System.out.println("partsOfSheet = " + partsOfSheet);
            //System.out.println("materialSize = " + (materialWidth / 1000) + " x " + (materialHeight / 1000));
            //System.out.println("minMaterialSize = " + (minMaterialWidth / 1000) + " x " + (minMaterialHeight / 1000));


            polygon.getPoints().addAll(
                    0.0, 0.0,
                    sheetWidth * materialScale, 0.0,
                    sheetWidth * materialScale, sheetHeight * materialScale,
                    0.0, sheetHeight * materialScale
            );
            polygon.setFill(Color.web("0xE1DFDD"));
//            polygon.setStroke(Color.GREEN);
//            polygon.setStrokeType(StrokeType.INSIDE);
            this.getChildren().add(polygon);

            labelSize.setPrefWidth(polygon.getBoundsInLocal().getWidth());
            labelSize.setPrefHeight(polygon.getBoundsInLocal().getHeight());
            labelSize.setAlignment(Pos.CENTER);
            labelSize.setText("" + ((int)sheetWidth) + " x " + ((int)sheetHeight));
            labelSize.setFont(Font.font(19.0));
            labelSize.setStyle("-fx-text-fill: white");
            this.getChildren().remove(labelSize);
            this.getChildren().add(labelSize);

            createHidingPolygons();


            initConnectionPoints();
        }

        public boolean isAdditionalSheet() {
            return additionalSheet;
        }


        public void setAdditionalSheet(boolean additionalSheet) {
            this.additionalSheet = additionalSheet;
        }

        public double getSheetSquare() {
            return sheetSquare;
        }

        public double getMinSheetSquare() {
            return minSheetSquare;
        }

        public double getSheetWidth() {
            return sheetWidth;
        }

        public void setSheetWidth(double sheetWidth) {
            this.sheetWidth = sheetWidth;
        }

        public double getSheetHeight() {
            return sheetHeight;
        }

        public void setSheetHeight(double sheetHeight) {
            this.sheetHeight = sheetHeight;
        }

        public int getSheetDepth() {
            return sheetDepth;
        }

        public void setSheetDepth(int sheetDepth) {
            this.sheetDepth = sheetDepth;
        }

        public boolean isActualPrice() {
            return actualPrice;
        }

        public void setActualPrice(boolean actualPrice) {
            this.actualPrice = actualPrice;
        }

        public int getDepth() {
            return sheetDepth;
        }

        public double getPartsOfSheet() {
            return partsOfSheet;
        }

        public String getCuttingDirection() {
            if (horizontalCuttingParts) {
                return "h";
            } else {
                return "v";
            }
        }

        public int getCountOfAvailableParts(){

            if(additionalSheet) return 1;

            if (getCuttingDirection().equals("h")) {
                return 2;
            } else if (getCuttingDirection().equals("v")) {
                return 4;
            }

            return 1;
        }

        public String getSheetCurrency() {
            return sheetCurrency;
        }

        public ArrayList<ConnectPoint> getConnectPointArrayList() {
            return connectPointArrayList;
        }

        public Material getMaterial() {
            return Material.this;
        }

        private void createHidingPolygons() {

            Polygon templatePolygon = new Polygon(
                    0.0, 0.0,
                    sheetMinWidth * materialScale, 0.0,
                    sheetMinWidth * materialScale, sheetMinHeight * materialScale,
                    0.0, sheetMinHeight * materialScale
            );

            //System.out.println("additionalSheet = " + additionalSheet);
            if(additionalSheet){

                hideHorizontalPolygon1.getPoints().addAll(templatePolygon.getPoints());
                this.getChildren().addAll(hideHorizontalPolygon1);
                hideHorizontalPolygon1.setMouseTransparent(true);
                hideHorizontalPolygon1.setFill(new Color(0, 0, 0, 0.5));
                hideHorizontalPolygon1.setTranslateX(0.0);

            }else{

                if (horizontalCuttingParts) {
                    hideHorizontalPolygon1.getPoints().addAll(templatePolygon.getPoints());
                    hideHorizontalPolygon2.getPoints().addAll(templatePolygon.getPoints());

                    if(partsOfSheet == 1){
                        this.getChildren().addAll(hideHorizontalPolygon1);
                    }else{
                        this.getChildren().addAll(hideHorizontalPolygon1, hideHorizontalPolygon2);
                    }


                    hideHorizontalPolygon1.setMouseTransparent(true);
                    hideHorizontalPolygon2.setMouseTransparent(true);

                    hideHorizontalPolygon1.setFill(new Color(1, 1, 1, 0.4));
                    hideHorizontalPolygon2.setFill(new Color(1, 1, 1, 0.4));

                    hideHorizontalPolygon1.setTranslateX(0.0);
                    hideHorizontalPolygon1.setTranslateY(0.0);
                    hideHorizontalPolygon2.setTranslateX(0.0);
                    hideHorizontalPolygon2.setTranslateY((sheetHeight / 2) * materialScale);
                } else {

                    hideVerticalPolygon1.getPoints().addAll(templatePolygon.getPoints());
                    hideVerticalPolygon2.getPoints().addAll(templatePolygon.getPoints());
                    hideVerticalPolygon3.getPoints().addAll(templatePolygon.getPoints());
                    hideVerticalPolygon4.getPoints().addAll(templatePolygon.getPoints());

                    if (partsOfSheet == 1) {
                        this.getChildren().addAll(hideVerticalPolygon1);
                    }else if (partsOfSheet == 2) {
                        this.getChildren().addAll(hideVerticalPolygon1, hideVerticalPolygon2);
                    } else if (partsOfSheet == 4) {
                        this.getChildren().addAll(hideVerticalPolygon1, hideVerticalPolygon2);
                        this.getChildren().addAll(hideVerticalPolygon3, hideVerticalPolygon4);
                    }


                    hideVerticalPolygon1.setMouseTransparent(true);
                    hideVerticalPolygon2.setMouseTransparent(true);
                    hideVerticalPolygon3.setMouseTransparent(true);
                    hideVerticalPolygon4.setMouseTransparent(true);

                    hideVerticalPolygon1.setFill(new Color(1, 1, 1, 0.4));
                    hideVerticalPolygon2.setFill(new Color(1, 1, 1, 0.4));
                    hideVerticalPolygon3.setFill(new Color(1, 1, 1, 0.4));
                    hideVerticalPolygon4.setFill(new Color(1, 1, 1, 0.4));

                    hideVerticalPolygon1.setTranslateX(0.0);
                    hideVerticalPolygon1.setTranslateY(0.0);
                    hideVerticalPolygon2.setTranslateX((sheetMinWidth * 1) * materialScale);
                    hideVerticalPolygon2.setTranslateY(0.0);
                    hideVerticalPolygon3.setTranslateX((sheetMinWidth * 2) * materialScale);
                    hideVerticalPolygon3.setTranslateY(0.0);
                    hideVerticalPolygon4.setTranslateX((sheetMinWidth * 3) * materialScale);
                    hideVerticalPolygon4.setTranslateY(0.0);

                }
            }




        }

        public Polygon getPolygon() {
            return polygon;
        }

        public double getMaterialScale() {
            return materialScale;
        }

        public void initConnectionPoints() {
            getChildren().removeAll(leftUpConnectPoint, leftDownConnectPoint, rightUpConnectPoint, rightDownConnectPoint);
            //leftUpConnectPoint.relocate(-5,-5);
            leftUpConnectPoint.changeSetPoint(new Point2D(0, 0));
//            leftUpConnectPoint.setTranslateX(-5);
//            leftUpConnectPoint.setTranslateY(-5);
            leftUpConnectPoint.hide();
            //leftDownConnectPoint.relocate(-5,heightRectangle-5);
            leftDownConnectPoint.changeSetPoint(new Point2D(0, sheetHeight * materialScale));
//            leftDownConnectPoint.setTranslateX(-5);
//            leftDownConnectPoint.setTranslateY(materialHeight*materialScale-5);
            leftDownConnectPoint.hide();
            //rightUpConnectPoint.relocate(widthRectangle-5,-5);
            rightUpConnectPoint.changeSetPoint(new Point2D(sheetWidth * materialScale, 0));
//            rightUpConnectPoint.setTranslateX(materialWidth*materialScale-5);
//            rightUpConnectPoint.setTranslateY(-5);
            rightUpConnectPoint.hide();
            //rightDownConnectPoint.relocate(widthRectangle-5,heightRectangle-5);
            rightDownConnectPoint.changeSetPoint(new Point2D(sheetWidth * materialScale, sheetHeight * materialScale));
//            rightDownConnectPoint.setTranslateX(materialWidth*materialScale-5);
//            rightDownConnectPoint.setTranslateY(materialHeight*materialScale-5);
            rightDownConnectPoint.hide();
            getChildren().addAll(leftUpConnectPoint, leftDownConnectPoint, rightUpConnectPoint, rightDownConnectPoint);


            connectPointArrayList.clear();
            connectPointArrayList.add(leftUpConnectPoint);
            connectPointArrayList.add(leftDownConnectPoint);
            connectPointArrayList.add(rightUpConnectPoint);
            connectPointArrayList.add(rightDownConnectPoint);


            this.scaleXProperty().addListener((observable, oldValue, newValue) -> {
               // System.out.println("SCALE X CHANGED");
                //leftUpConnectPoint.setScaleX(1.0/newValue.doubleValue());
                //leftDownConnectPoint.setScaleX(1.0/newValue.doubleValue());
                //rightUpConnectPoint.setScaleX(1.0/newValue.doubleValue());
                //rightDownConnectPoint.setScaleX(1.0/newValue.doubleValue());
            });

            this.scaleYProperty().addListener((observable, oldValue, newValue) -> {
//                leftUpConnectPoint.setScaleY(1.0/newValue.doubleValue());
//                leftDownConnectPoint.setScaleY(1.0/newValue.doubleValue());
//                rightUpConnectPoint.setScaleY(1.0/newValue.doubleValue());
//                rightDownConnectPoint.setScaleY(1.0/newValue.doubleValue());
            });
        }

        public void hideHorizontalHalf(int part, boolean hide) {
            usesList = 0;

//            System.out.println("customSheet = " + additionalSheet);
//            System.out.println("this.getChildren().contains(hideHorizontalPolygon1) = " + this.getChildren().contains(hideHorizontalPolygon1));
//            System.out.println("this.getChildren().contains(hideHorizontalPolygon2) = " + this.getChildren().contains(hideHorizontalPolygon2));
//            System.out.println("part = " + part);
//            System.out.println("hide = " + hide);


            if (part == 1) {
                hideHorizontalPolygon1.setVisible(!hide);
                //if(!hide) usesList = 1;
            } else {
                hideHorizontalPolygon2.setVisible(!hide);
                //if(!hide) usesList = 2;
            }

        }

//        public double getMinMaterialWidth() {
//            return minMaterialWidth;
//        }
//
//        public double getMinMaterialHeight() {
//            return minMaterialHeight;
//        }


        public double getSheetMinHeight() {
            return sheetMinHeight;
        }

        public double getSheetMinWidth() {
            return sheetMinWidth;
        }

        public void hideVerticalHalf(int part, boolean hide) {
            usesList = 0;
            if (part == 1) {
                hideVerticalPolygon1.setVisible(!hide);
                //if(!hide) usesList = 1;
            } else if (part == 2) {
                hideVerticalPolygon2.setVisible(!hide);
                //if(!hide) usesList = 2;
            } else if (part == 3) {
                hideVerticalPolygon3.setVisible(!hide);
                //if(!hide) usesList = 3;
            } else if (part == 4) {
                hideVerticalPolygon4.setVisible(!hide);
                //if(!hide) usesList = 4;
            }
        }

        public int getUsesList() {

            usesList = 0;

            if(additionalSheet) return 1;
            if (getCuttingDirection().equals("h")) {
                if (hideHorizontalPolygon1.isVisible()) usesList++;
                if (hideHorizontalPolygon2.isVisible()) usesList++;
            } else if (getCuttingDirection().equals("v")) {
                if (hideVerticalPolygon1.isVisible()) usesList++;
                if (hideVerticalPolygon2.isVisible()) usesList++;
                if (hideVerticalPolygon3.isVisible()) usesList++;
                if (hideVerticalPolygon4.isVisible()) usesList++;
            }
            return usesList;
        }

        public double getUsesSlabs(){
            double slabs = 0;
            if(additionalSheet){

                if(hideHorizontalPolygon1.isVisible()){
                    slabs = 1;
                }

                return slabs;
            }

            if (getCuttingDirection().equals("h")) {
                if (hideHorizontalPolygon1.isVisible()) slabs +=0.5;
                if (hideHorizontalPolygon2.isVisible()) slabs +=0.5;
            } else if (getCuttingDirection().equals("v")) {
                if (hideVerticalPolygon1.isVisible()) slabs +=0.25;
                if (hideVerticalPolygon2.isVisible()) slabs +=0.25;
                if (hideVerticalPolygon3.isVisible()) slabs +=0.25;
                if (hideVerticalPolygon4.isVisible()) slabs +=0.25;
            }

            return slabs;
        }

        public int getRawUsesList() {
            return usesList;
        }

        public void setUsesList(int usesList) {
            this.usesList = usesList;
        }

        /* FIXED prices and coefficients START */

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

        public ArrayList<Double> getTableTopCoefficientList() {
            return tableTopCoefficientList;
        }
        public ArrayList<Double> getWallPanelCoefficientList() {
            return wallPanelCoefficientList;
        }
        public ArrayList<Double> getFootCoefficientList() {
            return footCoefficientList;
        }
        public ArrayList<Double> getWindowSillCoefficientList() {
            return windowSillCoefficientList;
        }

//        public void setTableTopDepthsAndPrices(Map<Integer, Integer> tableTopDepthsAndPrices) {
//            tableTopDepthsAndPrices = tableTopDepthsAndPrices;
//        }
//        public void setWallPanelDepthsAndPrices(Map<Integer, Integer> wallPanelDepthsAndPrices) {
//            this.wallPanelDepthsAndPrices = wallPanelDepthsAndPrices;
//        }
//        public void setWindowSillDepthsAndPrices(Map<Integer, Integer> windowSillDepthsAndPrices) {
//            this.windowSillDepthsAndPrices = windowSillDepthsAndPrices;
//        }
//        public void setFootDepthsAndPrices(Map<Integer, Integer> footDepthsAndPrices) {
//            this.footDepthsAndPrices = footDepthsAndPrices;
//        }
//
//        public void setTableTopCoefficientList(ArrayList<Double> tableTopCoefficientList) {
//            this.tableTopCoefficientList = tableTopCoefficientList;
//        }
//        public void setWallPanelCoefficientList(ArrayList<Double> wallPanelCoefficientList) {
//            this.wallPanelCoefficientList = wallPanelCoefficientList;
//        }
//        public void setFootCoefficientList(ArrayList<Double> footCoefficientList) {
//            this.footCoefficientList = footCoefficientList;
//        }
//        public void setWindowSillCoefficientList(ArrayList<Double> windowSillCoefficientList) {
//            this.windowSillCoefficientList = windowSillCoefficientList;
//        }

        public double getPrice(ElementTypes elementType, int depth) {

            double price = getPriceRaw(elementType, depth);

            double materialCoefficient = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
            double commonCoefficient = ProjectHandler.getPriceMainCoefficient().doubleValue();

            price = price * materialCoefficient * commonCoefficient;
            return price;
        }

        public double getPriceRaw(ElementTypes elementType, int depth) {

            double price = 0;
            if(additionalSheet){
                price = sheetCustomPriceForMeter;
            }else{

                //System.out.println(name);
                System.out.println("INTO SHEET tableTopDepthsAndPrices = " + tableTopDepthsAndPrices);

                if (elementType == ElementTypes.TABLETOP) {
                    price = (tableTopDepthsAndPrices.get(new Integer(depth)).doubleValue()) / 100.0;
                } else if (elementType == ElementTypes.WALL_PANEL) {
                    price = wallPanelDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;
                } else if (elementType == ElementTypes.WINDOWSILL) {
                    price = windowSillDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;
                } else if (elementType == ElementTypes.FOOT) {
                    price = footDepthsAndPrices.get(new Integer(depth)).doubleValue() / 100.0;
                } else {
                    price = 0;
                }
            }

            return price;
        }
        /* FIXED prices and coefficients END */


        @Override
        public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint) {
            CutShape draggableShape = (CutShape) draggablePoint.getPointOwner();
            MaterialSheet staticShape;

            staticShape = (Material.MaterialSheet) staticPoint.getPointOwner();

            double oldX = draggableShape.getTranslateX();
            double oldY = draggableShape.getTranslateY();
            double newX = staticPoint.getTranslateX() + 5 + staticShape.getTranslateX() - (draggablePoint.getTranslateX() + 5);
            double newY = staticPoint.getTranslateY() + 5 + staticShape.getTranslateY() - (draggablePoint.getTranslateY() + 5);


            draggableShape.setTranslateX(((staticPoint.getTranslateX() + 5 + staticShape.getTranslateX()) - (draggablePoint.getTranslateX() + 5)));
            draggableShape.setTranslateY((staticPoint.getTranslateY() + 5 + staticShape.getTranslateY()) - (draggablePoint.getTranslateY() + 5));
        }

        @Override
        public void showConnectionPoints() {
            for (ConnectPoint p : connectPointArrayList) {
                p.show();
            }
        }

        @Override
        public void hideConnectionPoints() {
            for (ConnectPoint p : connectPointArrayList) {
                p.hide();
            }
        }


    }
}
