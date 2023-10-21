package ru.koreanika.Common.Material;

import javafx.scene.image.Image;
import ru.koreanika.PortalClient.PortalURI;
import ru.koreanika.Main;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CachingImageLoader implements ImageLoader {

    private static final String CACHE_PATH = "materials_resources/cache/";

    public CachingImageLoader() {
        File dir = new File(CACHE_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    public Image getImageByPath(String remotePath) {
        String localPath = remotePath.substring(remotePath.lastIndexOf("/") + 1);
        File localFile = new File(CACHE_PATH + localPath);

        if (!localFile.exists()) {
            String remotePathEncoded = URLEncoder.encode(remotePath, StandardCharsets.UTF_8).replaceAll("%2F", "/");
            String url = Main.getProperty("server.host") + ":8080" + PortalURI.PORTAL_URI_DOWNLOAD_MATERIAL_IMAGE + remotePathEncoded;

            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, Paths.get(localFile.getPath()));
            } catch (FileNotFoundException ex) {
                System.out.println("NO IMAGE ON SERVER FOR MATERIAL - > " + remotePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Image image = null;
        try {
            image = new Image(new FileInputStream(localFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return image;
    }

}