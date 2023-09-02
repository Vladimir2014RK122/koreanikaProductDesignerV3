package Common.Material;


import PortalClient.Authorization.Authorization;
import PortalClient.PortalURI;
import PortalClient.Status.PortalStatus;
import Preferences.UserPreferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import utils.Main;
import utils.Updater.UpdateManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MaterialImage {

    private static final String CASH_IMAGES_PATH = "materials_resources/cashed/";

    String mainType;
    String subType;
    String collection;
    String color;

    String imageMaterialPath = "";
    String imageInteriorPath = "";

    String localImageInteriorName = "";
    String localImageMaterialName = "";

    Image imageMaterial = null;
    Image imageInterior = null;

    private BooleanProperty cashed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty cashedError = new SimpleBooleanProperty(false);

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

    public void downloadMaterialImage(){

        String url = Main.getProperty("server.host") + ":8080" + PortalURI.PORTAL_URI_DOWNLOAD_MATERIAL_IMAGE + imageMaterialPath;

        System.out.println(url);

        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(CASH_IMAGES_PATH + localImageMaterialName));
            imageMaterial = new Image(new FileInputStream(CASH_IMAGES_PATH + localImageMaterialName));
            cashed.set(true);
        }catch (MalformedURLException ex){
            ex.printStackTrace();
        }catch (FileNotFoundException ex){
            System.out.println("NO IMAGE ON SERVER FOR MATERIAL - > " + localImageMaterialName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void startDownloadingImages(){


        if(!(new File(CASH_IMAGES_PATH + localImageMaterialName).exists())){
            Thread thread = new Thread(() ->{

                downloadMaterialImage();

                System.out.println("DOWNLOADING THREAD OUT<-------- cashed = " + cashed.get());
            });

            thread.setDaemon(true);
            thread.start();
        }else{
//            System.out.println(CASH_IMAGES_PATH + localImageMaterialName);
            try {
                imageMaterial = new Image(new FileInputStream(new File(CASH_IMAGES_PATH + localImageMaterialName)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            cashed.set(true);
        }

    }





}
