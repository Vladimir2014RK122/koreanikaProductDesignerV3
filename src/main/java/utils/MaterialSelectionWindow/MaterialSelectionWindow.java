package utils.MaterialSelectionWindow;

import Common.Material.Material;
import Common.Material.MaterialImage;
import PortalClient.Authorization.AppType;
import Preferences.UserPreferences;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import sketchDesigner.Shapes.ElementTypes;
import sketchDesigner.SketchDesigner;
import tableDesigner.TableDesigner;
import utils.*;
import utils.MaterialSelectionWindow.ListCellItems.MaterialListCellItem;
import utils.MaterialSelectionWindow.TreeViewItems.FolderItem;
import utils.MaterialSelectionWindow.TreeViewItems.MaterialItem;
import utils.MaterialSelectionWindow.TreeViewItems.MaterialTreeCellItem;
import utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.*;

public class MaterialSelectionWindow {

    MaterialSelectionEventHandler materialSelectionEventHandler;
    private static Scene mainScene;
    private static Scene materialScene;

    private static AnchorPane rootAnchorPane;

    private static Button btnToProject, btnFromProject, btnApply;
    private static ImageView imageViewSelectedMaterial;
    //private static Label labelSelectedMaterialPart1, labelSelectedMaterialPart11, labelSelectedMaterialPart2;
    private Label labelMaterialName, labelMaterialSubName, labelMaterialCollection, labelMaterialColor, labelMaterialCalcType;
    private Label labelMaterialDepths, labelMaterialWidth, labelMaterialHeight;
    private Label labelPrice;

    private static Label labelNotification2;//, labelNotification2;
    private static ChoiceBox<String> choiceBoxDefault;
    //TextField textFieldEdgeHeight, textFieldBorderHeight;
    private static TreeView<MaterialTreeCellItem> treeViewAvailable;
    private static ListView<MaterialListCellItem> listViewInProject;

    /* Filter fields: */
    private static TextField textFieldFilterName;
    private static TitledPane titledPaneFilterColor;
    private static ListView<FilterPropertyItem> listViewFilterColors = new ListView<>();;
    private static TitledPane titledPaneFilterTexture;
    private static ListView<FilterPropertyItem> listViewFilterTexture = new ListView<>();;
    private static TitledPane titledPaneFilterSurface;
    private static ListView<FilterPropertyItem> listViewFilterSurface = new ListView<>();;

    private static ImageView imageViewSearchIcon;
//    private static Button btnSearchDown, btnSearchUp;
    private static Label labelSearchInfo;

    List<Material> allAvailableMaterialsList;
    List<Material> filteredAvailableMaterialsList;
    int searchSelectedIndex = -1;


    //analogPart:
//    Button btnAnalogShowHide;
    private static ListView<MaterialListCellItem> listViewAnalog;
    Label labelAnalog;
    Separator separatorAnalog;

    private static MaterialSelectionWindow materialSelectionWindow;

    AnchorPane materialSettingsView = null;

    //    int edgeHeight, borderHeight;
    boolean correctEdgeHeight = true, correctBorderHeight = true;

    boolean firstStartFlag = false;
    FirstStart firstStart;

    private MaterialSelectionWindow() {


        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/MaterialManager/materialSelection.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        rootAnchorPane.getStylesheets().clear();
//
//        if(Main.appOwner == AppOwner.KOREANIKA || Main.appOwner == AppOwner.KOREANIKAMASTER){
//            System.out.println("OWNER = " + Main.appOwner);
//            rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsKoreanika.css").toExternalForm());
//        }else if(Main.appOwner == AppOwner.ZETTA){
//            System.out.println("OWNER = " + Main.appOwner);
//            rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsZetta.css").toExternalForm());
//        }else if(Main.appOwner == AppOwner.PROMEBEL){
//            System.out.println("OWNER = " + Main.appOwner);
//            rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsPromebel.css").toExternalForm());
//        }
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/materialSelection.css").toExternalForm());


//        edgeHeight = ProjectHandler.getDefaultEdgeHeight();
//        borderHeight = ProjectHandler.getDefaultBorderHeight();

        //materialScene = new Scene(rootAnchorPane, rootAnchorPane.getPrefWidth(), rootAnchorPane.getPrefHeight());

        btnToProject = (Button) rootAnchorPane.lookup("#btnToProject");
        btnFromProject = (Button) rootAnchorPane.lookup("#btnFromProject");
        btnApply = (Button) rootAnchorPane.lookup("#btnApply");
        textFieldFilterName = (TextField) rootAnchorPane.lookup("#textFieldFilterName");
        titledPaneFilterColor = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterColor");
        titledPaneFilterTexture = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterTexture");
        titledPaneFilterSurface = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterSurface");


        imageViewSearchIcon = (ImageView) rootAnchorPane.lookup("#imageViewSearchIcon");
//        btnSearchDown = (Button) rootAnchorPane.lookup("#btnSearchDown");
//        btnSearchUp = (Button) rootAnchorPane.lookup("#btnSearchUp");
        labelSearchInfo = (Label) rootAnchorPane.lookup("#labelSearchInfo");

        imageViewSelectedMaterial = (ImageView) rootAnchorPane.lookup("#imageViewSelectedMaterial");

        labelMaterialName = (Label) rootAnchorPane.lookup("#labelMaterialName");
        labelMaterialSubName = (Label) rootAnchorPane.lookup("#labelMaterialSubName");
        labelMaterialCollection = (Label) rootAnchorPane.lookup("#labelMaterialCollection");
        labelMaterialColor = (Label) rootAnchorPane.lookup("#labelMaterialColor");
        labelMaterialCalcType = (Label) rootAnchorPane.lookup("#labelMaterialCalcType");
        labelMaterialDepths = (Label) rootAnchorPane.lookup("#labelMaterialDepths");
        labelMaterialWidth = (Label) rootAnchorPane.lookup("#labelMaterialWidth");
        labelMaterialHeight = (Label) rootAnchorPane.lookup("#labelMaterialHeight");

        labelPrice = (Label) rootAnchorPane.lookup("#labelPrice");

//        labelNotification1 = (Label) rootAnchorPane.lookup("#labelNotification1");
//        labelNotification1.setWrapText(true);
//        labelNotification1.setText("");

        labelNotification2 = (Label) rootAnchorPane.lookup("#labelNotification2");
        labelNotification2.setWrapText(true);
        labelNotification2.setText("");

        choiceBoxDefault = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxDefault");

        treeViewAvailable = (TreeView<MaterialTreeCellItem>) rootAnchorPane.lookup("#treeViewAvailable");
        listViewInProject = (ListView<MaterialListCellItem>) rootAnchorPane.lookup("#listViewInProject");


        treeViewAvailable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //analogPart:
//        btnAnalogShowHide = (Button) rootAnchorPane.lookup("#btnAnalogShowHide");

        listViewAnalog = (ListView<MaterialListCellItem>) rootAnchorPane.lookup("#listViewAnalog");

        labelAnalog = (Label) rootAnchorPane.lookup("#labelAnalog");
        separatorAnalog = (Separator) rootAnchorPane.lookup("#separatorAnalog");

//        btnAnalogShowHide.setText("Скрыть аналоги");

        listViewAnalog.setCellFactory(new MaterialAnalogListCellFactory());

        listViewInProject.setCellFactory(new MaterialListCellFactory());



        initFilterFields();
        initFilterLogic();


        allAvailableMaterialsList = ProjectHandler.getMaterialsListAvailable().stream().filter(material ->{
            if (UserPreferences.getInstance().getSelectedApp() != AppType.KOREANIKAMASTER) {
                if (material.getMainType().equals("Натуральный камень")
                        || material.getMainType().equals("Массив")
                        || material.getMainType().equals("Массив_шпон")
                        || material.isTemplate()
                        || material.getColor().equals("Другой")) {
                    return false;
                }
                return true;
//                if(material.getMainType().equals("Кварцекерамический камень") ) continue;
//                if(material.isTemplate() || material.getColor().equals("Другой") ) continue;
            }
            return true;
        }).toList();

        filterFieldsUpdated();

//        initTreeViewAvailable(allAvailableMaterialsList);

        initWindowLogic();


        UserPreferences.getInstance().addAppTypeChangeListener(change -> {
//            System.out.println("MaterialSelectionWindow.class - APP CHANGED!");
            Platform.runLater(() -> {
                initTreeViewAvailable(ProjectHandler.getMaterialsListAvailable());
                initWindowLogic();

                MainWindow.showInfoMessage(InfoMessage.MessageType.INFO, "Тип приложения был изменен");
            });

        });


    }

    public void setFirstStart(FirstStart firstStart) {
        this.firstStart = firstStart;
    }

    public boolean isFirstStartFlag() {
        return firstStartFlag;
    }

    public static synchronized MaterialSelectionWindow getInstance() {
        if (materialSelectionWindow == null) {
            materialSelectionWindow = new MaterialSelectionWindow();
        }
        return materialSelectionWindow;
    }

    private void initWindowLogic() {
        btnToProject.setOnAction(event -> {

            List<Material> materials = getSelectedMaterials();
            if (materials.size() == 1 && materials.get(0).isTemplate()) {
                MaterialSettings materialSettings = new MaterialSettings(materials.get(0));
                showMaterialSettings(materialSettings);
//                materialSettings.show(Main.getMainScene());
            } else {
                addMaterialToProjectListView(getSelectedMaterials());
            }

        });
        btnFromProject.setOnMouseClicked(event -> {
            ArrayList<Material> materials = new ArrayList<>();
            for (MaterialListCellItem item : listViewInProject.getSelectionModel().getSelectedItems()) {
                materials.add(item.getMaterial());
            }
            removeMaterialFromProjectListView(materials);
        });

        btnApply.setOnMouseClicked(event -> {

//            System.out.println("correctEdgeHeight = " + correctEdgeHeight);
//            System.out.println("correctBorderHeight = " + correctBorderHeight);
//            System.out.println("listViewInProject.getItems().size() = " + listViewInProject.getItems().size());
//            System.out.println("(!correctEdgeHeight) || (!correctBorderHeight) = " + ((!correctEdgeHeight) || (!correctBorderHeight)));

            if ((!correctEdgeHeight) || (!correctBorderHeight)) return;

            if (listViewInProject.getItems().size() == 0) return;

            //ProjectHandler.getMaterialsListInProject().clear();
            ArrayList<Material> materialsInProject = new ArrayList<>();
            for (MaterialListCellItem item : listViewInProject.getItems()) {
                String[] materialNameArr = item.getName().split("\\$");

                Material material = item.getMaterial();
                materialsInProject.add(material);
                material.setDefaultDepth(item.getDepth());
            }


            String defaultMaterialName = choiceBoxDefault.getSelectionModel().getSelectedItem();

//            System.out.println("\n\n******DEFAULT FROM CHOICE BOX: " + defaultMaterialName);


            for (Material material : materialsInProject) {
                System.out.println(material.getReceiptName());
                if ((material.getSubType() + ", " + material.getCollection() + ", " + material.getColor()).equals(defaultMaterialName)) {

                    ProjectHandler.setDefaultMaterialRAW(material);
                    //break;
                }

            }

            ProjectHandler.setMaterialsListInProject(materialsInProject);


            //((Stage) (materialScene.getWindow())).close();

            ProjectHandler.getMaterialsUsesInProjectObservable().clear();
            SketchDesigner.getSketchShapesList().forEach(sketchShape -> {
                ProjectHandler.getMaterialsUsesInProjectObservable().add(sketchShape.getMaterial().getName() + "#" + sketchShape.getShapeDepth());
            });

            System.out.println("\n\n******DEFAULT FROM CHOICE BOX: " + defaultMaterialName);

            if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
                if (MainWindow.getSketchDesigner() != null) SketchDesigner.updateMaterialsInProject();
            } else if (ProjectHandler.getProjectType() == ProjectType.TABLE_TYPE) {
                if (MainWindow.getTableDesigner() != null) TableDesigner.updateMaterialsInProject();
            }

            if (firstStartFlag) {
                firstStart.firstMaterialSelected();
            }

            if (!firstStartFlag) materialSelectionEventHandler.apply();
        });

//        btnAnalogShowHide.setOnMouseClicked(event -> {
//            if (rootAnchorPane.getChildren().contains(labelAnalog)) {
//                btnAnalogShowHide.setText("Показать аналоги");
//                rootAnchorPane.getChildren().remove(labelAnalog);
//                rootAnchorPane.getChildren().remove(listViewAnalog);
//                rootAnchorPane.getChildren().remove(separatorAnalog);
//                rootAnchorPane.getScene().getWindow().setWidth(815);
//            } else {
//                btnAnalogShowHide.setText("Скрыть аналоги");
//                rootAnchorPane.getChildren().add(labelAnalog);
//                rootAnchorPane.getChildren().add(listViewAnalog);
//                rootAnchorPane.getChildren().add(separatorAnalog);
//                rootAnchorPane.getScene().getWindow().setWidth(1000);
//            }
//        });


        treeViewAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null) return;
            TreeItem<MaterialTreeCellItem> item = newValue;
            if (item.isLeaf()) {
                String name = item.getValue().getFullName();
                for (Material m : ProjectHandler.getMaterialsListAvailable()) {
                    if (m.getName().equals(name)) {
                        showInfo(m);
                        break;
                    }
                }
            }
            listViewInProject.getSelectionModel().select(null);
            listViewAnalog.getSelectionModel().select(null);

            //refill analog list:
            listViewAnalog.getItems().clear();
            if (item.getValue() instanceof MaterialItem) {
                Material selectedMaterial = ((MaterialItem) item.getValue()).getMaterial();

                System.out.println("REFILL ANALOG LIST = " + selectedMaterial.getAnalogsList());

                for (Material analog : selectedMaterial.getAnalogsList()) {
                    MaterialListCellItem cellItem = new MaterialListCellItem(analog, true);
                    listViewAnalog.getItems().add(cellItem);
                }
            }

        });

        listViewInProject.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue == null) return;

            showInfo(newValue.getMaterial());

            treeViewAvailable.getSelectionModel().select(null);
            listViewAnalog.getSelectionModel().select(null);

            //refill analog list:
            listViewAnalog.getItems().clear();
            for (Material analog : newValue.getMaterial().getAnalogsList()) {
                MaterialListCellItem cellItem = new MaterialListCellItem(analog);
                listViewAnalog.getItems().add(cellItem);
            }
        });

        listViewAnalog.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            treeViewAvailable.getSelectionModel().select(null);
            listViewInProject.getSelectionModel().select(null);

            MaterialListCellItem cellItem = newValue;

            for (Material m : ProjectHandler.getMaterialsListAvailable()) {
                if (m.getReceiptName().equals(cellItem.getMaterial().getReceiptName())) {
                    showInfo(m);
                    break;
                }
            }
        });


//        textFieldFilterName.textProperty().addListener((observable, oldValue, newValue) -> {
//
//            searchFoundedList.clear();
//            searchSelectedIndex = -1;
//
//            String searchStr = textFieldFilterName.getText();
//            System.out.println("search: " + searchStr);
//            if (searchStr.equals("")) {
//                //initTreeViewAvailable(ProjectHandler.getMaterialsListAvailable());
//                return;
//            }
//
//            for (Material material : ProjectHandler.getMaterialsListAvailable()) {
//                if (material.getColor().equals(searchStr) || material.getColor().indexOf(searchStr) != -1) {
//                    searchFoundedList.add(material);
//                }
//            }
//
//            //initTreeViewAvailable(searchFoundedList);
//
//
//            searchSelectNext();
//        });
//
//        textFieldFilterName.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                if (searchFoundedList.size() != 0) {
//                    searchSelectNext();
//                }
//            }
//        });

//        btnSearchUp.setOnAction(event -> {
//            searchSelectPrevious();
//        });
//
//        btnSearchDown.setOnAction(event -> {
//            searchSelectNext();
//        });


    }

    public void setMaterialSelectionEventHandler(MaterialSelectionEventHandler materialSelectionEventHandler) {
        this.materialSelectionEventHandler = materialSelectionEventHandler;
    }

    public static ListView<MaterialListCellItem> getListViewInProject() {
        return listViewInProject;
    }

    public static Button getBtnToProject() {
        return btnToProject;
    }

//    public void searchSelectNext() {
//
//        if (searchFoundedList.size() == 0) return;
//
//        if (searchSelectedIndex + 1 > searchFoundedList.size() - 1) {
//            searchSelectedIndex = 0;
//        } else {
//            searchSelectedIndex++;
//        }
//
//        searchUpdateInfo();
//
//        for (TreeItem<MaterialTreeCellItem> it0 : treeViewAvailable.getRoot().getChildren()) {
//            it0.setExpanded(false);
//            for (TreeItem<MaterialTreeCellItem> item1 : it0.getChildren()) {
//                item1.setExpanded(false);
//                for (TreeItem<MaterialTreeCellItem> item2 : item1.getChildren()) {
//                    item2.setExpanded(false);
//                }
//            }
//        }
//
//        Material material = searchFoundedList.get(searchSelectedIndex);
//
//        for (TreeItem<MaterialTreeCellItem> item0 : treeViewAvailable.getRoot().getChildren()) {
//            if (item0.getValue().getName().equals(material.getMainType())) {
//                item0.setExpanded(true);
//                for (TreeItem<MaterialTreeCellItem> item1 : item0.getChildren()) {
//                    if (item1.getValue().getName().equals(material.getSubType())) {
//                        item1.setExpanded(true);
//                        for (TreeItem<MaterialTreeCellItem> item2 : item1.getChildren()) {
//                            if (item2.getValue().getName().equals(material.getCollection())) {
//                                item2.setExpanded(true);
//                                for (TreeItem<MaterialTreeCellItem> item3 : item2.getChildren()) {
//                                    if (item3.getValue().getName().indexOf(material.getColor()) != -1) {
//                                        treeViewAvailable.getSelectionModel().select(item3);
//                                        int itemIndex = treeViewAvailable.getSelectionModel().getSelectedIndex();
//                                        treeViewAvailable.scrollTo(itemIndex - 10);
//                                        return;
//                                    }
//                                }
//                                //return;
//                            }
//                        }
//                        //return;
//                    }
//                }
//                // return;
//            }
//        }
//
//    }

//    public void searchSelectPrevious() {
//
//        if (searchFoundedList.size() == 0) return;
//
//        if (searchSelectedIndex - 1 < 0) {
//            searchSelectedIndex = searchFoundedList.size() - 1;
//        } else {
//            searchSelectedIndex--;
//        }
//
//        searchUpdateInfo();
//
////        for(TreeItem<MaterialTreeCellItem> it0 : treeViewAvailable.getRoot().getChildren()){
////            it0.setExpanded(false);
////            for(TreeItem<MaterialTreeCellItem> item1 : it0.getChildren()){
////                item1.setExpanded(false);
////                for(TreeItem<MaterialTreeCellItem> item2 : item1.getChildren()){
////                    item2.setExpanded(false);
////                }
////            }
////        }
//
//        Material material = searchFoundedList.get(searchSelectedIndex);
//
//        for (TreeItem<MaterialTreeCellItem> item0 : treeViewAvailable.getRoot().getChildren()) {
//            if (item0.getValue().getName().equals(material.getMainType())) {
//                item0.setExpanded(true);
//                for (TreeItem<MaterialTreeCellItem> item1 : item0.getChildren()) {
//                    if (item1.getValue().getName().equals(material.getSubType())) {
//                        item1.setExpanded(true);
//                        for (TreeItem<MaterialTreeCellItem> item2 : item1.getChildren()) {
//                            if (item2.getValue().getName().equals(material.getCollection())) {
//                                item2.setExpanded(true);
//                                for (TreeItem<MaterialTreeCellItem> item3 : item2.getChildren()) {
//                                    if (item3.getValue().getName().indexOf(material.getColor()) != -1) {
//                                        treeViewAvailable.getSelectionModel().select(item3);
//                                        int itemIndex = treeViewAvailable.getSelectionModel().getSelectedIndex();
//                                        treeViewAvailable.scrollTo(itemIndex - 10);
//                                        return;
//                                    }
//                                }
//                                //return;
//                            }
//                        }
//                        //return;
//                    }
//                }
//                // return;
//            }
//        }
//    }
//
//    public void searchUpdateInfo() {
//
//        if (textFieldFilterName.getText().equals("")) {
//            labelSearchInfo.setText("-");
//        }
//        labelSearchInfo.setText("" + (searchSelectedIndex + 1) + " из " + searchFoundedList.size());
//
//    }

    public void setFirstStartFlag(boolean firstStartFlag) {
        this.firstStartFlag = firstStartFlag;
    }

    private void showInfo(Material material) {


        String mainType = material.getMainType() + "\n";
        String subType = material.getSubType() + "\n";
        String collection = material.getCollection() + "\n";
        String color = material.getColor() + "\n";


        String calculationType = ((material.getCalculationType() == 1) ? "по площади" : "по раскрою") + "\n";// 1 - m^2, 2 - slabs

        String depths = "";
        for (String s : material.getDepths()) {
            depths += s + "мм ";
        }
        depths += "\n";

        String materialWidth = material.getMaterialWidth() + " мм\n";
        String materialHeight = material.getMaterialHeight() + " мм\n";

        String symbol = (material.getCurrency().equals("RUB")) ? ReceiptManager.RUR_SYMBOL : ((material.getCurrency().equals("USD")) ? ReceiptManager.USD_SYMBOL : ReceiptManager.EUR_SYMBOL);


        System.out.println(material.getName());
        System.out.println(material.getDepths());
        System.out.println(depths);

        double priceInRUR = 0;
        if (material.getCurrency().equals("RUB"))
            priceInRUR = material.getPrice(ElementTypes.TABLETOP, material.getDefaultDepth());
        else if (material.getCurrency().equals("USD"))
            priceInRUR = material.getPrice(ElementTypes.TABLETOP, material.getDefaultDepth()) * MainWindow.getUSDValue().get();
        else if (material.getCurrency().equals("EUR"))
            priceInRUR = material.getPrice(ElementTypes.TABLETOP, material.getDefaultDepth()) * MainWindow.getEURValue().get();
        symbol = ReceiptManager.RUR_SYMBOL;

        String materialPrice = String.format(Locale.ENGLISH, "Цена: %.0f", priceInRUR) + symbol + " м^2";

        System.out.println(depths);


        labelMaterialName.setText(mainType);
        labelMaterialSubName.setText(subType);
        labelMaterialCollection.setText(collection);
        labelMaterialColor.setText(color);
        labelMaterialCalcType.setText(calculationType);
        labelMaterialDepths.setText(depths);
        labelMaterialWidth.setText(materialWidth);
        labelMaterialHeight.setText(materialHeight);

        labelPrice.setText(materialPrice);

//        labelSelectedMaterialPart1.setText("Вид:\nМатериал:\nКоллекция:\nЦвет:\nТип раскроя:\nДоступные толщины:\nШирина:\nВысота:");
//        labelSelectedMaterialPart11.setText(mainType + subType + collection + color + calculationType + depths + materialWidth + materialHeight);
//        //labelSelectedMaterialPart1.setText(mainType + subType + collection + color + calculationType + depths + materialWidth + materialHeight + materialPrice);
//        labelSelectedMaterialPart2.setText(materialPrice);


        int notification1 = material.getNotification1();
        String notification1Text = (notification1 == 1) ? "Выбранный цвет требует " +
                "обязательного уточнения по наличию." : "";
//        labelNotification1.setText(notification1Text);

        int notification2 = material.getNotification2();
        String notification2Text = (notification2 == 1) ? "Количество стыков и их расположение в данной " +
                "коллекции выполняется по усмотрению производителя." : "";
        labelNotification2.setText(notification2Text);

        String imagePath = material.getImgPath();
        if (imagePath == null || imagePath.equals("")) {
            imagePath = ProjectHandler.MATERIALS_IMG_PATH + "no_img.png";
        } else {
            imagePath = ProjectHandler.MATERIALS_IMG_PATH + material.getImgPath();
        }


        material.updateCashImageView(imageViewSelectedMaterial);
//        imageViewSelectedMaterial.setImage(material.getImageView().getImage());
//
//
//
//        material.getMaterialImage().cashedProperty().addListener((observableValue, aBoolean, t1) -> {
//            System.out.println("CASHED");
//            imageViewSelectedMaterial.setImage(material.getImageView().getImage());
//        });
//        System.out.println("START DOWNLOADING");
//        material.getMaterialImage().startDownloadingImages();
    }

    private void initFilterFields() {
        textFieldFilterName.setText("");

        Set<String> availableColors = new LinkedHashSet<>();
        Set<String> availableTextures = new LinkedHashSet<>();
        Set<String> availableSurfaces = new LinkedHashSet<>();

        for (Material m : ProjectHandler.getMaterialsListAvailable()) {
            String color = m.getVisualProperties().get(Material.VIS_PROP_COLOR);
            String texture = m.getVisualProperties().get(Material.VIS_PROP_TEXTURE);
            String surface = m.getVisualProperties().get(Material.VIS_PROP_SURFACE);
            if (color != null) availableColors.add(color);
            if (texture != null) availableTextures.add(texture);
            if (surface != null) availableSurfaces.add(surface);
        }

        initFilterPropertyField(titledPaneFilterColor, listViewFilterColors, availableColors, Material.VIS_PROP_COLOR);
        initFilterPropertyField(titledPaneFilterTexture, listViewFilterTexture, availableTextures, Material.VIS_PROP_TEXTURE);
        initFilterPropertyField(titledPaneFilterSurface, listViewFilterSurface, availableSurfaces, Material.VIS_PROP_SURFACE);

    }

    private void initFilterPropertyField(TitledPane titledPane, ListView<FilterPropertyItem> listView, Set<String> availableProps, String property){

        final TitledPane titledPaneFilter = titledPane;
        final ListView<FilterPropertyItem> listViewFilter = listView;


//        System.out.println("ListView H=" + listViewFilter.getHeight());
//        System.out.println("titledPaneFilter H=" + titledPaneFilter.getHeight());

        titledPaneFilter.setText("");
        titledPaneFilter.setExpanded(false);
        titledPaneFilter.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1)titledPane.toFront();
        });




        //titled pane color:
        listViewFilter.setCellFactory(new Callback<ListView<FilterPropertyItem>, ListCell<FilterPropertyItem>>() {
            @Override
            public ListCell<FilterPropertyItem> call(ListView<FilterPropertyItem> filterPropertyItemListView) {
                ListCell<FilterPropertyItem> cell = new ListCell<FilterPropertyItem>() {
                    @Override
                    protected void updateItem(FilterPropertyItem item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            //setText(item.getName());
                            setGraphic(item.getCheckBox());
                        }
                    }
                };
                return cell;
            }
        });

        listViewFilter.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableProps.forEach(prop -> {
            FilterPropertyItem filterPropertyItem = new FilterPropertyItem(prop);
//            filterPropertyItem.getCheckBox().setOnAction(e->{
//
//                titledPaneFilter.setText("");
//                listViewFilter.getItems().forEach(item->{
//                    if(item.getCheckBox().isSelected()){
//                        titledPaneFilter.setText(titledPaneFilter.getText() + ((titledPaneFilter.getText().equals(""))?"":",") + item.getName());
//                    }
//                });
//            });
            listViewFilter.getItems().add(filterPropertyItem);
        });

        titledPaneFilter.setContent(listViewFilter);
    }

    private void initFilterLogic(){
        textFieldFilterName.textProperty().addListener((observableValue, s, newValue) -> {
            filterFieldsUpdated();
        });

        listViewFilterColors.getItems().forEach(filterPropertyItem ->{

            filterPropertyItem.getCheckBox().setOnAction(e ->{
                filterFieldsUpdated();
            });
        });

        listViewFilterTexture.getItems().forEach(filterPropertyItem ->{

            filterPropertyItem.getCheckBox().setOnAction(e ->{
                filterFieldsUpdated();
            });
        });

        listViewFilterSurface.getItems().forEach(filterPropertyItem ->{

            filterPropertyItem.getCheckBox().setOnAction(e ->{
                filterFieldsUpdated();
            });
        });

    }

    private void filterFieldsUpdated(){
        String filterName = textFieldFilterName.getText();
        LinkedHashSet<String> filterColors = new LinkedHashSet<>();
        LinkedHashSet<String> filterTextures = new LinkedHashSet<>();
        LinkedHashSet<String> filterSurfaces = new LinkedHashSet<>();

        boolean filterNameClear = false;
        boolean filterColorClear = false;
        boolean filterTextureClear = false;
        boolean filterSurfaceClear = false;

        filteredAvailableMaterialsList = allAvailableMaterialsList.stream()
                .filter(material -> {
                    return (material.getColor().contains(filterName));
                }).toList();


        if(filterName.equals("")) filterNameClear = true;

        System.out.println("filteredAvailableMaterialsList after name = " + filteredAvailableMaterialsList.size());
        //update listViewFilterColors
        listViewFilterColors.getItems().forEach(filterPropertyItem -> {
            boolean haveProperty = false;
            for(Material material: filteredAvailableMaterialsList){
                if(material.getVisualProperties().containsKey(Material.VIS_PROP_COLOR)
                        && material.getVisualProperties().get(Material.VIS_PROP_COLOR).equalsIgnoreCase(filterPropertyItem.getName())){
                    haveProperty = true;
                }
            }
            if(!haveProperty){
                filterPropertyItem.getCheckBox().setDisable(true);
                filterPropertyItem.getCheckBox().setSelected(false);
            }else{
                filterPropertyItem.getCheckBox().setDisable(false);
            }

            if(filterPropertyItem.getCheckBox().isSelected()){
                filterColors.add(filterPropertyItem.getName());
            }
        });

        //when nothing selected
        if(filterColors.size() == 0){
            for(Material material: filteredAvailableMaterialsList){
                filterColors.add(material.getVisualProperties().get(Material.VIS_PROP_COLOR));
            }
            filterColorClear = true;

            titledPaneFilterColor.setText("Все");
        }else{
            titledPaneFilterColor.setText(String.join(", ", filterColors));
        }




        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for(String prop : filterColors){
                if(material.getVisualProperties().containsValue(prop)){
                    return true;
                }
            }
            return false;
        }).toList();

        System.out.println("filteredAvailableMaterialsList after color = " + filteredAvailableMaterialsList.size());

        //update listViewFilterTextures
        listViewFilterTexture.getItems().forEach(filterPropertyItem -> {
            boolean haveProperty = false;
            for(Material material: filteredAvailableMaterialsList){
                if(material.getVisualProperties().containsKey(Material.VIS_PROP_TEXTURE)
                        && material.getVisualProperties().get(Material.VIS_PROP_TEXTURE).equalsIgnoreCase(filterPropertyItem.getName())){
                    haveProperty = true;
                }
            }
            if(!haveProperty){
                filterPropertyItem.getCheckBox().setDisable(true);
                filterPropertyItem.getCheckBox().setSelected(false);
            }else{
                filterPropertyItem.getCheckBox().setDisable(false);
            }

            if(filterPropertyItem.getCheckBox().isSelected()){
                filterTextures.add(filterPropertyItem.getName());
            }
        });

        //when nothing selected
        if(filterTextures.size() == 0){
            for(Material material: filteredAvailableMaterialsList){
                filterTextures.add(material.getVisualProperties().get(Material.VIS_PROP_TEXTURE));
            }
            filterTextureClear = true;

            titledPaneFilterTexture.setText("Все");
        }else{
            titledPaneFilterTexture.setText(String.join(", ", filterTextures));
        }


        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for(String prop : filterTextures){
                if(material.getVisualProperties().containsValue(prop)){
                    return true;
                }
            }
            return false;
        }).toList();

        System.out.println("filteredAvailableMaterialsList after Texture = " + filteredAvailableMaterialsList.size());

        //update listViewFilterSurfaces
        listViewFilterSurface.getItems().forEach(filterPropertyItem -> {
            boolean haveProperty = false;
            for(Material material: filteredAvailableMaterialsList){
                if(material.getVisualProperties().containsKey(Material.VIS_PROP_SURFACE)
                        && material.getVisualProperties().get(Material.VIS_PROP_SURFACE).equalsIgnoreCase(filterPropertyItem.getName())){
                    haveProperty = true;
                }
            }
            if(!haveProperty){
                filterPropertyItem.getCheckBox().setDisable(true);
                filterPropertyItem.getCheckBox().setSelected(false);
            }else{
                filterPropertyItem.getCheckBox().setDisable(false);
            }

            if(filterPropertyItem.getCheckBox().isSelected()){
                filterSurfaces.add(filterPropertyItem.getName());
            }
        });

        //when nothing selected
        if(filterSurfaces.size() == 0){
            for(Material material: filteredAvailableMaterialsList){
                filterSurfaces.add(material.getVisualProperties().get(Material.VIS_PROP_SURFACE));
            }
            filterSurfaceClear = true;

            titledPaneFilterSurface.setText("Все");
        }else{
            titledPaneFilterSurface.setText(String.join(", ", filterSurfaces));
        }



        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for(String prop : filterSurfaces){
                if(material.getVisualProperties().containsValue(prop)){
                    return true;
                }
            }
            return false;
        }).toList();

        if(filterNameClear && filterColorClear && filterTextureClear && filterSurfaceClear){
            filteredAvailableMaterialsList = allAvailableMaterialsList;
        }

        System.out.println("filteredAvailableMaterialsList after Surface = " + filteredAvailableMaterialsList.size());
//        for(Material material: filteredAvailableMaterialsList){
//            System.out.println("Name = " + material.getName());
//        }

        labelSearchInfo.setText("Доступно: " + filteredAvailableMaterialsList.size());


        initTreeViewAvailable(filteredAvailableMaterialsList);
    }



    private void initTreeViewAvailable(List<Material> materialsList) {


        TreeItem<MaterialTreeCellItem> rootItem = new TreeItem<>(new FolderItem("root"));
        treeViewAvailable.setCellFactory(new MaterialTreeCellFactory());
        treeViewAvailable.setRoot(rootItem);
        treeViewAvailable.setShowRoot(false);


        //create main types in treeView
        for (Material material : materialsList) {

//            if (UserPreferences.getInstance().getSelectedApp() != AppType.KOREANIKAMASTER) {
//                if (material.getMainType().equals("Натуральный камень")) continue;
//                if (material.getMainType().equals("Массив")) continue;
//                if (material.getMainType().equals("Массив_шпон")) continue;
////                if(material.getMainType().equals("Кварцекерамический камень") ) continue;
////                if(material.isTemplate() || material.getColor().equals("Другой") ) continue;
//            }


            String mainType = material.getMainType();
            boolean mainTypeContain = false;

            for (TreeItem<MaterialTreeCellItem> mainTypeElement : rootItem.getChildren()) {
                if (mainTypeElement.getValue() instanceof FolderItem && mainTypeElement.getValue().getName().equals(mainType)) {
                    mainTypeContain = true;
                }
            }

            if (!mainTypeContain) {
                TreeItem<MaterialTreeCellItem> newMainTypeElement = new TreeItem<>(new FolderItem(mainType));
                rootItem.getChildren().add(newMainTypeElement);
            }
        }

        //create sub types in treeView
        for (Material material : materialsList) {
            String mainType = material.getMainType();
            String subType = material.getSubType();
            boolean subTypeContain = false;

            for (TreeItem<MaterialTreeCellItem> mainTypeElement : rootItem.getChildren()) {

                if (mainTypeElement.getValue() instanceof FolderItem && mainTypeElement.getValue().getName().equals(mainType)) {
                    for (TreeItem<MaterialTreeCellItem> subTypeElement : mainTypeElement.getChildren()) {
                        if (subTypeElement.getValue() instanceof FolderItem && subTypeElement.getValue().getName().equals(subType)) {
                            subTypeContain = true;
                        }
                    }

                    if (!subTypeContain) {
                        TreeItem<MaterialTreeCellItem> newSubTypeElement = new TreeItem<>(new FolderItem(subType));
                        mainTypeElement.getChildren().add(newSubTypeElement);
                    }
                }

            }
        }

        //create collections in treeView
        for (Material material : materialsList) {
            String mainType = material.getMainType();
            String subType = material.getSubType();
            String collection = material.getCollection();
            boolean collectionContain = false;

            for (TreeItem<MaterialTreeCellItem> mainTypeElement : rootItem.getChildren()) {

                if (mainTypeElement.getValue() instanceof FolderItem && mainTypeElement.getValue().getName().equals(mainType)) {
                    for (TreeItem<MaterialTreeCellItem> subTypeElement : mainTypeElement.getChildren()) {

                        if (subTypeElement.getValue() instanceof FolderItem && subTypeElement.getValue().getName().equals(subType)) {
                            for (TreeItem<MaterialTreeCellItem> collectionElement : subTypeElement.getChildren()) {
                                if (collectionElement.getValue() instanceof FolderItem && collectionElement.getValue().getName().equals(collection)) {
                                    collectionContain = true;
                                }
                            }

                            if (!collectionContain) {
                                TreeItem<MaterialTreeCellItem> newCollectionElement = new TreeItem<>(new FolderItem(collection));
                                subTypeElement.getChildren().add(newCollectionElement);
                            }
                        }
                    }
                }

            }
        }

        //create materials in treeView
        for (Material material : materialsList) {
            String mainType = material.getMainType();
            String subType = material.getSubType();
            String collection = material.getCollection();
            String color = material.getColor();

            for (TreeItem<MaterialTreeCellItem> mainTypeElement : rootItem.getChildren()) {

                if (mainTypeElement.getValue() instanceof FolderItem && mainTypeElement.getValue().getName().equals(mainType)) {
                    for (TreeItem<MaterialTreeCellItem> subTypeElement : mainTypeElement.getChildren()) {

                        if (subTypeElement.getValue() instanceof FolderItem && subTypeElement.getValue().getName().equals(subType)) {
                            for (TreeItem<MaterialTreeCellItem> collectionElement : subTypeElement.getChildren()) {

                                if (collectionElement.getValue() instanceof FolderItem && collectionElement.getValue().getName().equals(collection)) {

//                                    if (UserPreferences.getInstance().getSelectedApp() != AppType.KOREANIKAMASTER) {
//                                        if (material.isTemplate() || material.getColor().equals("Другой")) continue;
//                                    }

                                    TreeItem<MaterialTreeCellItem> newMaterialElement = new TreeItem<>(new MaterialItem(material));
                                    collectionElement.getChildren().add(newMaterialElement);
                                }
                            }
                        }
                    }
                }
            }

        }


    }

    private void initTreeViewAvailableLogic() {

        treeViewAvailable.getSelectionModel().selectionModeProperty().setValue(SelectionMode.SINGLE);

    }

    public static List<Material> getSelectedMaterials() {
        List<Material> materials = new ArrayList<>();

        for (TreeItem<MaterialTreeCellItem> item : treeViewAvailable.getSelectionModel().getSelectedItems()) {
            if (item.isLeaf() && item.getValue() instanceof MaterialItem) {
                Material m = ((MaterialItem) item.getValue()).getMaterial();
                materials.add(m);
            }
        }

        return materials;
    }

    public static void addAnalogMaterialToProjectListView(Material selectedMaterial) {
        //Material material = listViewAnalog.getSelectionModel().getSelectedItem().getMaterial();
        String subtype = selectedMaterial.getSubType();
        String collection = selectedMaterial.getCollection();
        String color = selectedMaterial.getColor();

        if (selectedMaterial == null) return;

        boolean containInProjectList = false;
        for (MaterialListCellItem m : listViewInProject.getItems()) {

            if (m.getMaterial().getName().indexOf(selectedMaterial.getName()) != -1) {
                containInProjectList = true;
                return;
            }
        }

        listViewInProject.getItems().add(new MaterialListCellItem(selectedMaterial));
        choiceBoxDefault.getItems().add(subtype + ", " + collection + ", " + color);
        if (choiceBoxDefault.getSelectionModel().getSelectedIndex() == -1)
            choiceBoxDefault.getSelectionModel().select(0);

    }

    public static void addMaterialToProjectListView(List<Material> materials) {

//        ObservableList<TreeItem<MaterialTreeCellItem>> selectedMaterials = treeViewAvailable.getSelectionModel().getSelectedItems();

        //choiceBoxDefault.getItems().clear();

        for (Material material : materials) {
            if (material.isTemplate()) {
                continue;
            }
//            if (item.isLeaf() && item.getValue() instanceof MaterialItem) {
//                material = ((MaterialItem) item.getValue()).getMaterial();
            String mainType = material.getMainType();
            String subType = material.getSubType();
            String collection = material.getCollection();
            String color = material.getColor();

            String name = mainType + "$" + subType + "$" + collection + "$" + color;
            String materialName = "---";

//                for (Material m : ProjectHandler.getMaterialsListAvailable()) {
//                    if (m.getName().indexOf(name) != -1) {
//
//                        materialName = m.getName();
//                        break;
//                    }
//                }

            boolean containInProjectList = false;
            for (MaterialListCellItem m : listViewInProject.getItems()) {


                if (m.getMaterial().getName().indexOf(material.getName()) != -1) {
                    containInProjectList = true;
                    break;
                }
            }


            if (!containInProjectList) {

                listViewInProject.getItems().add(new MaterialListCellItem(material));
                choiceBoxDefault.getItems().add(subType + ", " + collection + ", " + color);
            }
//            }
        }


        choiceBoxDefault.getSelectionModel().select(0);

    }

    public static void removeMaterialFromProjectListView(List<Material> materials) {


        listViewInProject.getSelectionModel().selectionModeProperty().setValue(SelectionMode.SINGLE);


        ArrayList<MaterialListCellItem> itemsForDelete = new ArrayList<>();
        for (Material m : materials) {


            for (MaterialListCellItem item : listViewInProject.getItems()) {
                if (item.getMaterial().getName().equals(m.getName())) {
                    itemsForDelete.add(item);

                }

            }

            for (MaterialListCellItem item : itemsForDelete) {
                listViewInProject.getItems().remove(item);

                choiceBoxDefault.getItems().remove(item.getName());

                if (choiceBoxDefault.getItems().size() > 0 && choiceBoxDefault.getSelectionModel().getSelectedItem().equals(item.getName())) {
                    choiceBoxDefault.getSelectionModel().select(0);
                }
            }
        }
    }

    public AnchorPane getView() {

        refreshView();
        return rootAnchorPane;
    }

    private void refreshView() {

        if (ProjectHandler.getDefaultMaterial() != null) {
            listViewInProject.getItems().clear();
            choiceBoxDefault.getItems().clear();
            for (Material material : ProjectHandler.getMaterialsListInProject()) {
                listViewInProject.getItems().add(new MaterialListCellItem(material));
                choiceBoxDefault.getItems().add(material.getSubType() + ", " + material.getCollection() + ", " + material.getColor());
            }
            choiceBoxDefault.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getSubType() + ", " + ProjectHandler.getDefaultMaterial().getCollection() + ", " + ProjectHandler.getDefaultMaterial().getColor());
        }
    }


//    public void show(Scene mainScene) {
//        this.mainScene = mainScene;
//
//        Stage materialStage = new Stage();
//        materialStage.setTitle("Выберите материал");
//        materialStage.initOwner(mainScene.getWindow());
//        materialStage.setScene(materialScene);
//        materialStage.setX(mainScene.getWindow().getX() + mainScene.getWindow().getWidth() / 2 - materialScene.getWidth() / 2);
//        materialStage.setY(mainScene.getWindow().getY() + mainScene.getWindow().getHeight() / 2 - materialScene.getHeight() / 2);
//        materialStage.initModality(Modality.APPLICATION_MODAL);
//        materialStage.setResizable(false);
//        materialStage.show();
//
//
//
////        edgeHeight = ProjectHandler.getDefaultEdgeHeight();
////        borderHeight = ProjectHandler.getDefaultBorderHeight();
////
////        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
////        textFieldBorderHeight.setText(String.valueOf(borderHeight));
//
//        if (rootAnchorPane.getChildren().contains(labelAnalog)) {
//
//        } else {
//            btnAnalogShowHide.setText("Скрыть аналоги");
//            rootAnchorPane.getChildren().add(labelAnalog);
//            rootAnchorPane.getChildren().add(listViewAnalog);
//            rootAnchorPane.getChildren().add(separatorAnalog);
//            rootAnchorPane.getScene().getWindow().setWidth(1000);
//        }
//
//        //((Stage)(materialScene.getWindow())).initStyle(StageStyle.UNDECORATED);
//        ((Stage) (materialScene.getWindow())).setOnCloseRequest(event -> {
//
//
//            System.out.println("close");
//
//            //This mean that it was creating project
//            if (ProjectHandler.getDefaultMaterial() == null) {
//                event.consume();
//                //show(mainScene);
//
//                Alert alert = new Alert(Alert.AlertType.WARNING, "Закрыть конфигуратор?", ButtonType.NO, ButtonType.YES);
//                // alert.setTitle("Материал не выбран!");
//                alert.setContentText("Материал не выбран!\nВыберите материал или закройте приложение.\nЗакрыть приложение?");
//
//                alert.showAndWait().ifPresent(response -> {
//                    if (response == ButtonType.YES) {
//                        ((Stage) (Main.getMainScene().getWindow())).close();
//                    }
//                });
//
//            }
//        });
//    }

    public static Scene getMaterialScene() {
        return materialScene;
    }

    public static void clear() {
        listViewInProject.getItems().clear();
        choiceBoxDefault.getItems().clear();
        for (TreeItem<MaterialTreeCellItem> item : treeViewAvailable.getRoot().getChildren()) {
            item.setExpanded(false);
        }
    }


    public void showMaterialSettings(MaterialSettings materialSettings) {

        refreshView();

        materialSettingsView = materialSettings.getView();
        rootAnchorPane.getChildren().add(materialSettingsView);

        AnchorPane.setTopAnchor(materialSettingsView, 0.0);
        AnchorPane.setBottomAnchor(materialSettingsView, 0.0);
        AnchorPane.setLeftAnchor(materialSettingsView, 0.0);
        AnchorPane.setRightAnchor(materialSettingsView, 0.0);
    }

    public void hideMaterialSettings() {
        if (materialSettingsView != null) {
            rootAnchorPane.getChildren().remove(materialSettingsView);
        }
    }

    public interface MaterialSelectionEventHandler {
        void apply();

        void cancel();
    }


}
