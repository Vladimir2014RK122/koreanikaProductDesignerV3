package ru.koreanika.utils.MaterialSelectionWindow;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.Preferences.UserPreferences;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.MouseClickedEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.MaterialSelectionWindow.TreeViewItems.FolderItem;
import ru.koreanika.utils.MaterialSelectionWindow.TreeViewItems.MaterialItem;
import ru.koreanika.utils.MaterialSelectionWindow.TreeViewItems.MaterialTreeCellItem;
import ru.koreanika.utils.ProjectHandler;
import ru.koreanika.utils.ProjectType;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.*;

public class MaterialSelectionWindow {

    private final MaterialImageModalWindowController modalWindowController;
    private final Stage materialImageModalStage;
    private EventHandler<MouseEvent> currentMaterialImageClickHandler;

    MaterialSelectionEventHandler materialSelectionEventHandler;

    private static AnchorPane rootAnchorPane;

    private static Button btnToProject, btnFromProject, btnApply;
    private static ImageView imageViewSelectedMaterial;
    private Label labelMaterialName, labelMaterialSubName, labelMaterialCollection, labelMaterialColor, labelMaterialCalcType;
    private Label labelMaterialDepths, labelMaterialWidth, labelMaterialHeight;
    private Label labelPrice;

    private static Label labelNotification2;//, labelNotification2;
    private static ChoiceBox<String> choiceBoxDefault;
    private static TreeView<MaterialTreeCellItem> treeViewAvailable;
    private static ListView<MaterialListCellItem> listViewInProject;

    private static Button btnFiltersReset;

    /* Filter fields: */
    private static TextField textFieldFilterName;
    private static TitledPane titledPaneFilterColor;
    private static final ListView<CheckBox> listViewFilterColors = new ListView<>();
    private static TitledPane titledPaneFilterTexture;
    private static final ListView<CheckBox> listViewFilterTexture = new ListView<>();
    private static TitledPane titledPaneFilterSurface;
    private static final ListView<CheckBox> listViewFilterSurface = new ListView<>();
    private static TitledPane titledPaneFilterPromotion;
    private static final ListView<CheckBox> listViewFilterPromotion = new ListView<>();

    private static Label labelSearchInfo;

    List<Material> allAvailableMaterialsList;
    List<Material> filteredAvailableMaterialsList;


    //analogPart:
    private static ListView<MaterialListCellItem> listViewAnalog;
    Label labelAnalog;
    Separator separatorAnalog;

    private static MaterialSelectionWindow materialSelectionWindow;

    AnchorPane materialSettingsView = null;

    boolean correctEdgeHeight = true, correctBorderHeight = true;

    boolean firstStartFlag = false;
    FirstStart firstStart;


    private MaterialSelectionWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(this);
        fxmlLoader.setLocation(getClass().getResource("/fxmls/MaterialManager/materialSelection.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
            // ex.printStackTrace();
        }


        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/materialSelection.css").toExternalForm());

        btnFiltersReset = (Button) rootAnchorPane.lookup("#btnFiltersReset");

        btnToProject = (Button) rootAnchorPane.lookup("#btnToProject");
        btnFromProject = (Button) rootAnchorPane.lookup("#btnFromProject");
        btnApply = (Button) rootAnchorPane.lookup("#btnApply");
        textFieldFilterName = (TextField) rootAnchorPane.lookup("#textFieldFilterName");
        titledPaneFilterColor = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterColor");
        titledPaneFilterTexture = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterTexture");
        titledPaneFilterSurface = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterSurface");
        titledPaneFilterPromotion = (TitledPane) rootAnchorPane.lookup("#titledPaneFilterPromo");

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

        labelNotification2 = (Label) rootAnchorPane.lookup("#labelNotification2");
        labelNotification2.setWrapText(true);
        labelNotification2.setText("");

        choiceBoxDefault = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxDefault");

        treeViewAvailable = (TreeView<MaterialTreeCellItem>) rootAnchorPane.lookup("#treeViewAvailable");
        listViewInProject = (ListView<MaterialListCellItem>) rootAnchorPane.lookup("#listViewInProject");

        treeViewAvailable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //analogPart:
        listViewAnalog = (ListView<MaterialListCellItem>) rootAnchorPane.lookup("#listViewAnalog");

        labelAnalog = (Label) rootAnchorPane.lookup("#labelAnalog");
        separatorAnalog = (Separator) rootAnchorPane.lookup("#separatorAnalog");

        listViewAnalog.setCellFactory(new MaterialAnalogListCellFactory());
        listViewInProject.setCellFactory(new MaterialListCellFactory());

        btnFiltersReset.setOnMouseClicked(event -> {
            listViewFilterColors.getItems().forEach(checkBox -> checkBox.setSelected(false));
            listViewFilterTexture.getItems().forEach(checkBox -> checkBox.setSelected(false));
            listViewFilterSurface.getItems().forEach(checkBox -> checkBox.setSelected(false));
            listViewFilterPromotion.getItems().forEach(checkBox -> checkBox.setSelected(false));
            filterFieldsUpdated();
        });

        initFilterFields();
        initFilterLogic();

        allAvailableMaterialsList = ProjectHandler.getMaterialsListAvailable().stream().filter(material -> {
            if (UserPreferences.getInstance().getSelectedApp() != AppType.KOREANIKAMASTER) {
                if (material.getMainType().equals("Натуральный камень")
                        || material.getMainType().equals("Массив")
                        || material.getMainType().equals("Массив_шпон")
                        || material.isTemplate()
                        || material.getColor().equals("Другой")) {
                    return false;
                }
                return true;
            }
            return true;
        }).toList();

        filterFieldsUpdated();
        initWindowLogic();

        UserPreferences.getInstance().addAppTypeChangeListener(change -> {
            Platform.runLater(() -> {
                initTreeViewAvailable(ProjectHandler.getMaterialsListAvailable());
                initWindowLogic();
                MainWindow.showInfoMessage(InfoMessage.MessageType.INFO, "Тип приложения был изменен");
            });
        });

        // material images slideshow
        modalWindowController = new MaterialImageModalWindowController();
        materialImageModalStage = new Stage();
        materialImageModalStage.setScene(new Scene(modalWindowController.getRootElement()));
        materialImageModalStage.initOwner(null);
        materialImageModalStage.initModality(Modality.APPLICATION_MODAL);
        imageViewSelectedMaterial.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> materialImageModalStage.showAndWait());
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
            if ((!correctEdgeHeight) || (!correctBorderHeight)) {
                return;
            }
            if (listViewInProject.getItems().isEmpty()) {
                return;
            }

            ArrayList<Material> materialsInProject = new ArrayList<>();
            for (MaterialListCellItem item : listViewInProject.getItems()) {
                Material material = item.getMaterial();
                materialsInProject.add(material);
                material.setDefaultDepth(item.getDepth());
            }

            String defaultMaterialName = choiceBoxDefault.getSelectionModel().getSelectedItem();

            for (Material material : materialsInProject) {
                System.out.println(material.getReceiptName());
                if ((material.getSubType() + ", " + material.getCollection() + ", " + material.getColor()).equals(defaultMaterialName)) {
                    ProjectHandler.setDefaultMaterialRAW(material);
                }
            }

            ProjectHandler.setMaterialsListInProject(materialsInProject);
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

            if (!firstStartFlag) {
                materialSelectionEventHandler.apply();
            }
        });

        treeViewAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
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
            if (newValue == null) {
                return;
            }

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
            if (newValue == null) {
                return;
            }
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
        String symbol = ReceiptManager.RUR_SYMBOL;

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

        int notification2 = material.getNotification2();
        String notification2Text = (notification2 == 1) ? "Количество стыков и их расположение в данной " +
                "коллекции выполняется по усмотрению производителя." : "";
        labelNotification2.setText(notification2Text);

        material.updateCashImageView(imageViewSelectedMaterial);
        modalWindowController.setMaterial(material);
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

        initFilterPropertyField(titledPaneFilterColor, listViewFilterColors, availableColors);
        initFilterPropertyField(titledPaneFilterTexture, listViewFilterTexture, availableTextures);
        initFilterPropertyField(titledPaneFilterSurface, listViewFilterSurface, availableSurfaces);

        initFilterPropertyField(titledPaneFilterPromotion, listViewFilterPromotion, new HashSet<>(Arrays.asList("Да", "Нет")));

        // Intercept clicks outside this element
        EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        eventBus.addHandler(MouseClickedEvent.TYPE, e -> {
            if (!isDescendant(e.getSource(), titledPaneFilterColor)) {
                titledPaneFilterColor.setExpanded(false);
            }
            if (!isDescendant(e.getSource(), titledPaneFilterTexture)) {
                titledPaneFilterTexture.setExpanded(false);
            }
            if (!isDescendant(e.getSource(), titledPaneFilterSurface)) {
                titledPaneFilterSurface.setExpanded(false);
            }
            if (!isDescendant(e.getSource(), titledPaneFilterPromotion)) {
                titledPaneFilterPromotion.setExpanded(false);
            }
        });

    }

    private void initFilterPropertyField(TitledPane titledPane, ListView<CheckBox> listView, Set<String> availableProps) {
        final ListView<CheckBox> listViewFilter = listView;

        titledPane.setText("");
        titledPane.setExpanded(false);
        titledPane.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                titledPane.toFront();
            }
        });

        listViewFilter.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CheckBox> call(ListView<CheckBox> filterPropertyItemListView) {
                ListCell<CheckBox> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(CheckBox item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                        }
                    }
                };
                return cell;
            }
        });

        listViewFilter.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableProps.forEach(prop -> listViewFilter.getItems().add(new CheckBox(prop)));
        titledPane.setContent(listViewFilter);
    }

    private void initFilterLogic() {
        textFieldFilterName.textProperty().addListener((observableValue, s, newValue) -> {
            filterFieldsUpdated();
        });

        listViewFilterColors.getItems().forEach(checkBox -> checkBox.setOnAction(e -> filterFieldsUpdated()));
        listViewFilterTexture.getItems().forEach(checkBox -> checkBox.setOnAction(e -> filterFieldsUpdated()));
        listViewFilterSurface.getItems().forEach(checkBox -> checkBox.setOnAction(e -> filterFieldsUpdated()));
        listViewFilterPromotion.getItems().forEach(checkBox -> checkBox.setOnAction(e -> filterFieldsUpdated()));
    }

    private void filterFieldsUpdated() {
        String filterName = textFieldFilterName.getText();
        Set<String> filterColors = new LinkedHashSet<>();
        Set<String> filterTextures = new LinkedHashSet<>();
        Set<String> filterSurfaces = new LinkedHashSet<>();
        Set<String> filterPromotion = new LinkedHashSet<>();

        boolean filterNameClear = false;
        boolean filterColorClear = false;
        boolean filterTextureClear = false;
        boolean filterSurfaceClear = false;
        boolean filterPromotionClear = false;

        filteredAvailableMaterialsList = allAvailableMaterialsList.stream()
                .filter(material -> material.getColor().contains(filterName))
                .toList();

        if (filterName.equals("")) filterNameClear = true;

        System.out.println("filteredAvailableMaterialsList after name = " + filteredAvailableMaterialsList.size());
        //update listViewFilterColors
        listViewFilterColors.getItems().forEach(checkBox -> {
            boolean hasProperty = false;
            for (Material material : filteredAvailableMaterialsList) {
                if (material.getVisualProperties().containsKey(Material.VIS_PROP_COLOR)
                        && material.getVisualProperties().get(Material.VIS_PROP_COLOR).equalsIgnoreCase(checkBox.getText())) {
                    hasProperty = true;
                    break;
                }
            }
            if (!hasProperty) {
                checkBox.setDisable(true);
                checkBox.setSelected(false);
            } else {
                checkBox.setDisable(false);
            }

            if (checkBox.isSelected()) {
                filterColors.add(checkBox.getText());
            }
        });

        //when nothing selected
        if (filterColors.size() == 0) {
            for (Material material : filteredAvailableMaterialsList) {
                filterColors.add(material.getVisualProperties().get(Material.VIS_PROP_COLOR));
            }
            filterColorClear = true;

            titledPaneFilterColor.setText("Все");
        } else {
            titledPaneFilterColor.setText(String.join(", ", filterColors));
        }


        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for (String prop : filterColors) {
                if (material.getVisualProperties().containsValue(prop)) {
                    return true;
                }
            }
            return false;
        }).toList();

        System.out.println("filteredAvailableMaterialsList after color = " + filteredAvailableMaterialsList.size());

        //update listViewFilterTextures
        listViewFilterTexture.getItems().forEach(checkBox -> {
            boolean hasProperty = false;
            for (Material material : filteredAvailableMaterialsList) {
                if (material.getVisualProperties().containsKey(Material.VIS_PROP_TEXTURE)
                        && material.getVisualProperties().get(Material.VIS_PROP_TEXTURE).equalsIgnoreCase(checkBox.getText())) {
                    hasProperty = true;
                    break;
                }
            }
            if (!hasProperty) {
                checkBox.setDisable(true);
                checkBox.setSelected(false);
            } else {
                checkBox.setDisable(false);
            }

            if (checkBox.isSelected()) {
                filterTextures.add(checkBox.getText());
            }
        });

        //when nothing selected
        if (filterTextures.size() == 0) {
            for (Material material : filteredAvailableMaterialsList) {
                filterTextures.add(material.getVisualProperties().get(Material.VIS_PROP_TEXTURE));
            }
            filterTextureClear = true;

            titledPaneFilterTexture.setText("Все");
        } else {
            titledPaneFilterTexture.setText(String.join(", ", filterTextures));
        }

        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for (String prop : filterTextures) {
                if (material.getVisualProperties().containsValue(prop)) {
                    return true;
                }
            }
            return false;
        }).toList();

        System.out.println("filteredAvailableMaterialsList after Texture = " + filteredAvailableMaterialsList.size());

        //update listViewFilterSurfaces
        listViewFilterSurface.getItems().forEach(checkBox -> {
            boolean hasProperty = false;
            for (Material material : filteredAvailableMaterialsList) {
                if (material.getVisualProperties().containsKey(Material.VIS_PROP_SURFACE)
                        && material.getVisualProperties().get(Material.VIS_PROP_SURFACE).equalsIgnoreCase(checkBox.getText())) {
                    hasProperty = true;
                    break;
                }
            }
            if (!hasProperty) {
                checkBox.setDisable(true);
                checkBox.setSelected(false);
            } else {
                checkBox.setDisable(false);
            }

            if (checkBox.isSelected()) {
                filterSurfaces.add(checkBox.getText());
            }
        });

        //when nothing selected
        if (filterSurfaces.isEmpty()) {
            for (Material material : filteredAvailableMaterialsList) {
                filterSurfaces.add(material.getVisualProperties().get(Material.VIS_PROP_SURFACE));
            }
            filterSurfaceClear = true;

            titledPaneFilterSurface.setText("Все");
        } else {
            titledPaneFilterSurface.setText(String.join(", ", filterSurfaces));
        }

        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for (String prop : filterSurfaces) {
                if (material.getVisualProperties().containsValue(prop)) {
                    return true;
                }
            }
            return false;
        }).toList();

        // update listViewFilterPromotion
        listViewFilterPromotion.getItems().forEach(checkBox -> {
            checkBox.setDisable(false);
            if (checkBox.isSelected()) {
                filterPromotion.add(checkBox.getText());
            }
        });

        //when nothing selected
        if (filterPromotion.isEmpty()) {
            filterPromotionClear = true;
            filterPromotion.add("Да");
            filterPromotion.add("Нет");
            titledPaneFilterPromotion.setText("Все");
        } else {
            titledPaneFilterPromotion.setText(String.join(", ", filterPromotion));
        }

        filteredAvailableMaterialsList = filteredAvailableMaterialsList.stream().filter(material -> {
            for (String prop : filterPromotion) {
                if (prop.equals("Да") && material.isPromotion()) {
                    return true;
                }
                if (prop.equals("Нет") && !material.isPromotion()) {
                    return true;
                }
            }
            return false;
        }).toList();

        if (filterNameClear && filterColorClear && filterTextureClear && filterSurfaceClear && filterPromotionClear) {
            filteredAvailableMaterialsList = allAvailableMaterialsList;
        }

        System.out.println("filteredAvailableMaterialsList after Surface = " + filteredAvailableMaterialsList.size());
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

        //create subtypes in treeView
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

            for (TreeItem<MaterialTreeCellItem> mainTypeElement : rootItem.getChildren()) {
                if (mainTypeElement.getValue() instanceof FolderItem && mainTypeElement.getValue().getName().equals(mainType)) {
                    for (TreeItem<MaterialTreeCellItem> subTypeElement : mainTypeElement.getChildren()) {
                        if (subTypeElement.getValue() instanceof FolderItem && subTypeElement.getValue().getName().equals(subType)) {
                            for (TreeItem<MaterialTreeCellItem> collectionElement : subTypeElement.getChildren()) {
                                if (collectionElement.getValue() instanceof FolderItem && collectionElement.getValue().getName().equals(collection)) {
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
        if (choiceBoxDefault.getSelectionModel().getSelectedIndex() == -1) {
            choiceBoxDefault.getSelectionModel().select(0);
        }
    }

    public static void addMaterialToProjectListView(List<Material> materials) {
        for (Material material : materials) {
            if (material.isTemplate()) {
                continue;
            }
            String mainType = material.getMainType();
            String subType = material.getSubType();
            String collection = material.getCollection();
            String color = material.getColor();

            boolean containInProjectList = false;
            for (MaterialListCellItem m : listViewInProject.getItems()) {
                if (m.getMaterial().getName().contains(material.getName())) {
                    containInProjectList = true;
                    break;
                }
            }

            if (!containInProjectList) {
                listViewInProject.getItems().add(new MaterialListCellItem(material));
                choiceBoxDefault.getItems().add(subType + ", " + collection + ", " + color);
            }
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
                if (!choiceBoxDefault.getItems().isEmpty() && choiceBoxDefault.getSelectionModel().getSelectedItem().equals(item.getName())) {
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

    private boolean isDescendant(Node node, Node potentialDescendant) {
        if (potentialDescendant == null) {
            return true;
        }
        while (node != null) {
            if (node == potentialDescendant) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

}
