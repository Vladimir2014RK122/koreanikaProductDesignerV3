package ru.koreanika.utils.Updater;

import org.apache.commons.io.FileUtils;
import ru.koreanika.utils.InfoMessage;

import java.io.*;
import java.net.URL;

public class UpdaterMaterialsList {

    public static void downloadNewMaterials(String urlStr) {

        //String fileJson = "";
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(urlStr);

            File fileNew = new File("materials_1_2004.xls");
            FileUtils.copyURLToFile(url, fileNew);
            InfoMessage.showMessage(InfoMessage.MessageType.SUCCESS, "Файл материалов обновлен!.", null);
        } catch (IOException ex) {

            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Не удается подключится к серверу обновлений.", null);
            return;
        }
    }

}
