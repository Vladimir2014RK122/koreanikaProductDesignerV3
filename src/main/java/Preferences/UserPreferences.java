package Preferences;

import PortalClient.Authorization.AppType;
import PortalClient.Authorization.Authorization;
import utils.Main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;

public class UserPreferences {

    private  static UserPreferences userPreferences;

    private static String PREFERENCES_NAME = "koreanika_designer";

    private static String ACCESS_TOKEN_NAME = "accessToken";
    private static String REFRESH_TOKEN_NAME = "refreshToken";
    private static String SELECTED_APP = "selectedApp";
    private static String COMPANY_ADDRESS = "companyAddress";

    Preferences prefs = Preferences.userRoot().node(PREFERENCES_NAME);
    String accessToken = prefs.get(ACCESS_TOKEN_NAME, null);
    String refreshToken = prefs.get(REFRESH_TOKEN_NAME, null);
    String selectedApp = prefs.get(SELECTED_APP, "k");
    String companyAddress = prefs.get(COMPANY_ADDRESS, null);


    Set<AppTypeChangeListener> appTypeChangeListeners = new LinkedHashSet<>();

    private UserPreferences(){

        if(companyAddress == null){

            if(getSelectedApp() == AppType.KOREANIKAMASTER)
                companyAddress = "\tООО \"Кореаника\" Балашиха, мкр. Гагарина 13а e-mail: info@koreanika.ru +7(495) 665-82-95";
            else if(getSelectedApp() == AppType.KOREANIKA)
                companyAddress = "\tООО \"Кореаника\" Балашиха, мкр. Гагарина 13а e-mail: info@koreanika.ru +7(495) 665-82-95";
            else if(getSelectedApp() == AppType.ZETTA)
                companyAddress = "\t123022, Москва, Расторгуевский пер., д. 1 тел.: (495) 510-19-19 email: sek@zetta.ru www.zetta.ru";
            else if(getSelectedApp() == AppType.PROMEBEL){
                companyAddress = "\tг. Электросталь, ул. Карла Маркса 43/1, e-mail: info@tytpromebel.ru +7(926) 195-20-00";
            }else
                companyAddress = "\t УСТАНОВИТЕ АДРЕС КОМАНИИ";

            saveCompanyAddress(companyAddress);
        }
    }

    public synchronized static UserPreferences getInstance(){
        if(userPreferences == null){
            userPreferences = new UserPreferences();
        }
        return userPreferences;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void saveAccessToken(String token) {
        prefs.put(ACCESS_TOKEN_NAME, token);
        this.accessToken = token;
    }

    public void saveRefreshToken(String token) {
        prefs.put(REFRESH_TOKEN_NAME, token);
        this.refreshToken = token;
    }

    public void saveSelectedApp(AppType selectedApp) {

        String oldValue = this.selectedApp;

        prefs.put(SELECTED_APP, selectedApp.getShortName());
        this.selectedApp = selectedApp.getShortName();

        if(!oldValue.equals(this.selectedApp)){
//            System.out.println("appTypeChangeListeners.size() = " + appTypeChangeListeners.size());
            for(AppTypeChangeListener listener : appTypeChangeListeners){
//                System.out.println(listener);
                listener.changed(AppType.getByShortName(this.selectedApp));
            }
//            appTypeChangeListeners.forEach(listener -> {
//
//            });
        }
    }

    public void saveCompanyAddress(String companyAddress) {
        prefs.put(COMPANY_ADDRESS, companyAddress);
        this.companyAddress = companyAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public AppType getSelectedApp() {
        return AppType.getByShortName(selectedApp);
    }

    public void removeAccessToken(){
        prefs.remove("accessToken");
        this.accessToken = null;
    }

    public void removeRefreshToken(){
        prefs.remove("refreshToken");
        this.refreshToken = null;
    }

    public void addAppTypeChangeListener(AppTypeChangeListener listener){
        appTypeChangeListeners.add(listener);
    }
}
