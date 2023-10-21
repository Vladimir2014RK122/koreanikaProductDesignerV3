package ru.koreanika.utils.MainSettings;


import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.PortalClient.Authorization.Authorization;
import ru.koreanika.utils.UserPreferences;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ru.koreanika.Main;

import java.io.IOException;
import java.util.List;

public class UserSettingsView {

    private static UserSettingsView userSettingsView;
    private AnchorPane rootAnchorPane;

    private TextField textFieldCompanyAddress;
    private CheckBox checkBoxAutoSaveAfterCut, checkBoxAutoSaveAfterReceipt;

    private Button btnLogout;
    private Label labelUserLogin, labelUserRole;

    private HBox hboxApps;
    private RadioButton radioBtnAppK, radioBtnAppKM, radioBtnAppZ, radioBtnAppPM;
    private Label labelSelectedApp;
    private ToggleGroup toggleGroupApps = new ToggleGroup();
    public UserSettingsView(){

        try {
            rootAnchorPane = FXMLLoader.load(MainSettings.class.getResource("/fxmls/MainSettings/UserSettingsView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        initControls();
        initControlsLogic();

    }

    public static UserSettingsView getInstance(){
        if(userSettingsView == null){
            userSettingsView = new UserSettingsView();
        }
        return userSettingsView;
    }

    private void initControls(){
        textFieldCompanyAddress = (TextField) rootAnchorPane.lookup("#textFieldCompanyAddress");
        checkBoxAutoSaveAfterCut = (CheckBox) rootAnchorPane.lookup("#checkBoxAutoSaveAfterCut");
        checkBoxAutoSaveAfterReceipt = (CheckBox) rootAnchorPane.lookup("#checkBoxAutoSaveAfterReceipt");
        labelUserLogin = (Label) rootAnchorPane.lookup("#labelUserLogin");
        labelUserRole = (Label) rootAnchorPane.lookup("#labelUserRole");
        btnLogout = (Button) rootAnchorPane.lookup("#btnLogout");

        hboxApps = (HBox) rootAnchorPane.lookup("#hboxApps");
        radioBtnAppK = (RadioButton) rootAnchorPane.lookup("#radioBtnAppK");
        radioBtnAppKM = (RadioButton) rootAnchorPane.lookup("#radioBtnAppKM");
        radioBtnAppZ = (RadioButton) rootAnchorPane.lookup("#radioBtnAppZ");
        radioBtnAppPM = (RadioButton) rootAnchorPane.lookup("#radioBtnAppPM");
        labelSelectedApp = (Label) rootAnchorPane.lookup("#labelSelectedApp");

        radioBtnAppK.setToggleGroup(toggleGroupApps);
        radioBtnAppKM.setToggleGroup(toggleGroupApps);
        radioBtnAppZ.setToggleGroup(toggleGroupApps);
        radioBtnAppPM.setToggleGroup(toggleGroupApps);

        textFieldCompanyAddress.setText(UserPreferences.getInstance().getCompanyAddress());

        checkBoxAutoSaveAfterCut.setSelected(Boolean.parseBoolean(Main.getProperty("autosave.afterCut")));
        checkBoxAutoSaveAfterReceipt.setSelected(Boolean.parseBoolean(Main.getProperty("autosave.afterReceipt")));
    }

    public void refreshView(){
        if(Authorization.getInstance().getUser() != null){
            btnLogout.setDisable(false);
            labelUserLogin.setText(Authorization.getInstance().getUser().getLogin());
            labelUserRole.setText(Authorization.getInstance().getUser().getRole());
        }else{
            btnLogout.setDisable(true);
        }

        AppType appType = UserPreferences.getInstance().getSelectedApp();

        labelSelectedApp.setText(appType.getName());

        if (appType == AppType.KOREANIKAMASTER){
            radioBtnAppKM.setSelected(true);
            textFieldCompanyAddress.setDisable(true);
        }
        else if (appType == AppType.KOREANIKA){
            radioBtnAppK.setSelected(true);
            textFieldCompanyAddress.setDisable(true);
        }
        else if (appType == AppType.ZETTA){
            radioBtnAppZ.setSelected(true);
            textFieldCompanyAddress.setDisable(true);
        }
        else if (appType == AppType.PROMEBEL){
            radioBtnAppPM.setSelected(true);
            textFieldCompanyAddress.setDisable(false);
        }

        List<AppType> appTypeList = Authorization.getInstance().getAvailableAppTypes();

        hboxApps.getChildren().clear();
        if(appTypeList.size() == 1){
            hboxApps.getChildren().add(labelSelectedApp);
        }else{
            appTypeList.forEach(appType1 -> {
                if (appType1 == AppType.KOREANIKAMASTER) hboxApps.getChildren().add(radioBtnAppKM);
                else if (appType1 == AppType.KOREANIKA) hboxApps.getChildren().add(radioBtnAppK);
                else if (appType1 == AppType.ZETTA) hboxApps.getChildren().add(radioBtnAppZ);
                else if (appType1 == AppType.PROMEBEL) hboxApps.getChildren().add(radioBtnAppPM);
            });
        }

    }

    private void initControlsLogic(){


        btnLogout.setOnAction((e) ->{
            Authorization.getInstance().logout();
        });

        toggleGroupApps.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if(t1 == radioBtnAppKM) {
                textFieldCompanyAddress.setText("\tООО \"Кореаника\" Балашиха, мкр. Гагарина 13а e-mail: info@koreanika.ru +7(495) 665-82-95");
                textFieldCompanyAddress.setDisable(true);
            }else if(t1 == radioBtnAppK) {
                textFieldCompanyAddress.setText("\tООО \"Кореаника\" Балашиха, мкр. Гагарина 13а e-mail: info@koreanika.ru +7(495) 665-82-95");
                textFieldCompanyAddress.setDisable(true);
            }else if(t1 == radioBtnAppZ) {
                textFieldCompanyAddress.setText("\t123022, Москва, Расторгуевский пер., д. 1 тел.: (495) 510-19-19 email: sek@zetta.ru www.zetta.ru");
                textFieldCompanyAddress.setDisable(true);
            }else if(t1 == radioBtnAppPM) {
//                textFieldCompanyAddress.setText("\tг. Электросталь, ул. Карла Маркса 43/1, e-mail: info@tytpromebel.ru +7(926) 195-20-00");
                textFieldCompanyAddress.setDisable(false);
            }
        });
    }

    public AnchorPane getView(){
        refreshView();
        return rootAnchorPane;
    }

    public void save(){
        Main.updateFieldInProperties("companyAddress", textFieldCompanyAddress.getText());
        Main.updateFieldInProperties("autosave.afterCut", Boolean.valueOf(checkBoxAutoSaveAfterCut.isSelected()).toString());
        Main.updateFieldInProperties("autosave.afterReceipt", Boolean.valueOf(checkBoxAutoSaveAfterReceipt.isSelected()).toString());

        AppType appType = null;
        if(toggleGroupApps.getSelectedToggle().equals(radioBtnAppK)) appType = AppType.KOREANIKA;
        else if(toggleGroupApps.getSelectedToggle().equals(radioBtnAppKM)) appType = AppType.KOREANIKAMASTER;
        else if(toggleGroupApps.getSelectedToggle().equals(radioBtnAppZ)) appType = AppType.ZETTA;
        else if(toggleGroupApps.getSelectedToggle().equals(radioBtnAppPM)) appType = AppType.PROMEBEL;

        UserPreferences.getInstance().saveSelectedApp(appType);
        UserPreferences.getInstance().saveCompanyAddress(textFieldCompanyAddress.getText());
    }

    public void cancel(){

        textFieldCompanyAddress.setText(UserPreferences.getInstance().getCompanyAddress());

        checkBoxAutoSaveAfterCut.setSelected(Boolean.parseBoolean(Main.getProperty("autosave.afterCut")));
        checkBoxAutoSaveAfterReceipt.setSelected(Boolean.parseBoolean(Main.getProperty("autosave.afterReceipt")));
    }
}
