package utils.MainSettings;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainSettingsView {

    private static MainSettingsView mainSettingsView;


    private UserSettingsView userSettingsView;

    private AnchorPane anchorPaneShadow;
    private AnchorPane rootAnchorPane;
    private SplitPane splitPane;

    private ListView<MenuItem> listViewMenu;

    private ScrollPane scrollPaneSettingsView;

    Button btnSave, btnSaveAndExit, btnCancel;

    private Button btnWindowSubtract, btnWindowMaxMin, btnWindowClose;

    private double xOffset = 0;
    private double yOffset = 0;

    public MainSettingsView(){

        try {
            anchorPaneShadow = FXMLLoader.load(MainSettings.class.getResource("/fxmls/MainSettings/MainSettings.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        userSettingsView = UserSettingsView.getInstance();

        initControls();

        initMenu();
        initControlLogic();
    }

    public static MainSettingsView getInstance(){
        if(mainSettingsView == null){
            mainSettingsView = new MainSettingsView();
        }
        return mainSettingsView;
    }

    private void initControls(){
        rootAnchorPane = (AnchorPane) anchorPaneShadow.lookup("#rootAnchorPane");
        splitPane = (SplitPane) rootAnchorPane.lookup("#splitPane");
        listViewMenu = (ListView<MenuItem>) splitPane.getItems().get(0);
        scrollPaneSettingsView = (ScrollPane) splitPane.getItems().get(1);

        btnSaveAndExit = (Button) rootAnchorPane.lookup("#btnSaveAndExit");
        btnSave = (Button) rootAnchorPane.lookup("#btnSave");
        btnCancel = (Button) rootAnchorPane.lookup("#btnCancel");

        btnWindowSubtract = (Button) rootAnchorPane.lookup("#btnWindowSubtract");
        btnWindowMaxMin = (Button) rootAnchorPane.lookup("#btnWindowMaxMin");
        btnWindowClose = (Button) rootAnchorPane.lookup("#btnWindowClose");
    }

    private void initMenu(){

        listViewMenu.getItems().clear();
        for(MenuItem menuItem : MenuItem.values()){
            listViewMenu.getItems().add(menuItem);
        }

        listViewMenu.getItems().remove(MenuItem.ENCODE_FILE);

        if(listViewMenu.getItems().size() != 0){
            listViewMenu.getSelectionModel().select(0);
            selectSettingsTab(listViewMenu.getSelectionModel().getSelectedItem());
        }



    }

    private void initControlLogic(){

        btnWindowClose.setOnAction(actionEvent -> {
            ((Stage)(rootAnchorPane.getScene().getWindow())).close();
        });

        rootAnchorPane.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        rootAnchorPane.setOnMouseDragged((MouseEvent event) -> {

            Stage stage = ((Stage)(rootAnchorPane.getScene().getWindow()));
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        listViewMenu.getSelectionModel().selectedItemProperty().addListener((observableValue, menuItem, newValue) -> {
            selectSettingsTab(newValue);
        });

        btnSave.setOnMouseClicked(mouseEvent -> {
            userSettingsView.save();
        });

        btnSaveAndExit.setOnMouseClicked(mouseEvent -> {
            userSettingsView.save();
            MainSettings.getInstance().closeWindow();
        });

        btnCancel.setOnMouseClicked(mouseEvent -> {
            userSettingsView.cancel();
        });

        scrollPaneSettingsView.widthProperty().addListener((observableValue, number, t1) -> {
            ((AnchorPane)scrollPaneSettingsView.getContent()).setPrefWidth(t1.doubleValue());
        });


        KeyCodeCombination ctrlU = new KeyCodeCombination(KeyCode.U, KeyCodeCombination.CONTROL_DOWN);
        splitPane.setOnKeyPressed(keyEvent -> {
            if(ctrlU.match(keyEvent)){
                System.out.println("CTRL + U");
                if(listViewMenu.getItems().contains(MenuItem.ENCODE_FILE)){
                    listViewMenu.getItems().remove(MenuItem.ENCODE_FILE);
                }else{
                    listViewMenu.getItems().add(MenuItem.ENCODE_FILE);
                }
            }
        });
    }

    private void selectSettingsTab(MenuItem menuItem){

        if(menuItem == MenuItem.USER_SETTINGS){
            AnchorPane anchorPane = userSettingsView.getView();
            anchorPane.setPrefWidth(scrollPaneSettingsView.widthProperty().doubleValue());
            scrollPaneSettingsView.setContent(anchorPane);

        }else if(menuItem == MenuItem.ENCODE_FILE){

            try {
                AnchorPane anchorPane = FXMLLoader.load(MainSettings.class.getResource("/fxmls/MainSettings/MainSettingsEncodeFile.fxml"));
                anchorPane.setPrefWidth(scrollPaneSettingsView.widthProperty().doubleValue());
                scrollPaneSettingsView.setContent(anchorPane);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public AnchorPane getView(){
        userSettingsView.refreshView();
        return anchorPaneShadow;
    }





}
