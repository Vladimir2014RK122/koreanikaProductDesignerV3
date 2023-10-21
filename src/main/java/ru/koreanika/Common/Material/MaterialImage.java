package ru.koreanika.Common.Material;


import ru.koreanika.PortalClient.PortalURI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import ru.koreanika.utils.Main;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MaterialImage {

    private static final String CASH_IMAGES_PATH = "materials_resources/cashed/";

    String mainType;
    String subType;
    String collection;
    String color;

    String imageMaterialPath = "";
    String imageInteriorPath = "";

    private String localImageInteriorName = "";
    private String localImageMaterialName = "";

    Image imageMaterial = null;
    Image imageInterior = null;

    private BooleanProperty cashed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty cashedError = new SimpleBooleanProperty(false);

    public MaterialImage(Image imageMaterial) {
        this.imageMaterial = imageMaterial;
    }

    public MaterialImage(String mainType, String subType, String collection, String color){
        this.mainType = mainType;
        this.subType = subType;
        this.collection = collection;
        this.color = color;

        try {
            imageMaterialPath = URLEncoder.encode("material_images","utf-8") + "/" +
                    URLEncoder.encode(mainType,"utf-8") + "/" +
                    URLEncoder.encode(subType,"utf-8") + "/" +
                    URLEncoder.encode(collection,"utf-8") + "/" +
                    URLEncoder.encode(color,"utf-8") + "/" +
                    URLEncoder.encode("Текстура","utf-8") + "/" +
                    URLEncoder.encode(color + " 200х200.png","utf-8"); // ...200х200.png - x - wrote in cyrillic inn all images !!!!!!!!!

            imageInteriorPath = URLEncoder.encode("material_images","utf-8") + "/" +
                    URLEncoder.encode(mainType,"utf-8") + "/" +
                    URLEncoder.encode(subType,"utf-8") + "/" +
                    URLEncoder.encode(collection,"utf-8") + "/" +
                    URLEncoder.encode(color,"utf-8") + "/" +
                    URLEncoder.encode("Интерьер","utf-8") + "/" +
                    URLEncoder.encode(color + " 800x500 1.png","utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        localImageInteriorName = color + " interior.png";
        localImageMaterialName = color + " material.png";

        File dir = new File(CASH_IMAGES_PATH);
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public String getImageParentPath() {
        return "material_images/" + mainType + "/" + subType + "/" + collection + "/" + color;
    }

    public Image getImageMaterial() {
        return imageMaterial;
    }

    public Image getImageInterior() {
        return imageInterior;
    }

    public BooleanProperty cashedProperty() {
        return cashed;
    }

    public BooleanProperty cashedErrorProperty() {
        return cashedError;
    }

    @Deprecated
    private void downloadMaterialImage(){
        String url = Main.getProperty("server.host") + ":8080" + PortalURI.PORTAL_URI_DOWNLOAD_MATERIAL_IMAGE + imageMaterialPath;

        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(CASH_IMAGES_PATH + localImageMaterialName));
            imageMaterial = new Image(new FileInputStream(CASH_IMAGES_PATH + localImageMaterialName));
            cashed.set(true);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            System.out.println("NO IMAGE ON SERVER FOR MATERIAL - > " + localImageMaterialName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void startDownloadingImages() {
        if (!(new File(CASH_IMAGES_PATH + localImageMaterialName).exists())) {
            Thread thread = new Thread(() -> {
                downloadMaterialImage();
                System.out.println("DOWNLOADING THREAD OUT<-------- cashed = " + cashed.get());
            });
            thread.setDaemon(true);
            thread.start();
        } else {
            try {
                imageMaterial = new Image(new FileInputStream(new File(CASH_IMAGES_PATH + localImageMaterialName)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            cashed.set(true);
        }
    }

}
