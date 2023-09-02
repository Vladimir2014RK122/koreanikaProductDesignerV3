package utils.MainSettings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class MainSettings {

    public static MainSettings mainSettings;

    private Stage mainSettingsStage;


    public static MainSettings getInstance(){
        if(mainSettings ==  null){
            mainSettings = new MainSettings();
        }
        return mainSettings;
    }


    public void decodeFile(){

    }

    public void encodeFile(){

    }


    public static void showWindow(Scene parentScene){

        AnchorPane root = MainSettingsView.getInstance().getView();

        if(getInstance().mainSettingsStage != null){
            getInstance().mainSettingsStage.show();
            return;
        }
        getInstance().mainSettingsStage = new Stage();
        getInstance().mainSettingsStage.initStyle(StageStyle.TRANSPARENT);
        getInstance().mainSettingsStage.setTitle("Настройки и опции");
        getInstance().mainSettingsStage.initOwner(parentScene.getWindow());
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        getInstance().mainSettingsStage.setScene(scene);
        //materialStage.setX(mainScene.getWindow().getX() + mainScene.getWindow().getWidth() / 2 - materialScene.getWidth() / 2);
        //materialStage.setY(mainScene.getWindow().getY() + mainScene.getWindow().getHeight() / 2 - materialScene.getHeight() / 2);
        getInstance().mainSettingsStage.initModality(Modality.APPLICATION_MODAL);
        getInstance().mainSettingsStage.setResizable(false);
        //getInstance().mainSettingsStage.setMinWidth(602);
        getInstance().mainSettingsStage.show();
    }

    public void closeWindow(){
        mainSettingsStage.close();
    }

    protected File showSrcFileChooser(){

        if(mainSettingsStage == null) return null;

        File file = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Koreanika projects", "*.krnkproj", "*.kproj"));
        file = fileChooser.showOpenDialog(mainSettingsStage);

        return file;
    }

    protected File showDstFileChooser(){

        if(mainSettingsStage == null) return null;

        File file = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Koreanika projects", "*.krnkproj", "*.kproj"));
//        String path = System.getProperty("user.home");
//
//        if()fileChooser.setInitialDirectory(new File(ProjectHandler.getCurProjectPath()));
        file = fileChooser.showOpenDialog(mainSettingsStage);

        return file;
    }


}
