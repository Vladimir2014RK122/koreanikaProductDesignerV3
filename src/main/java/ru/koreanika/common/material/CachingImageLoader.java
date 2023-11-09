package ru.koreanika.common.material;

import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;
import ru.koreanika.PortalClient.PortalURI;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.ImageCachedEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.utils.Main;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class CachingImageLoader implements ImageLoader {

    private static final String CACHE_PATH = "materials_resources/cache/";

    private static final ExecutorService executor = ServiceLocator.getService("ExecutorService", ExecutorService.class);
    private static final EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);

    public CachingImageLoader() {
        File dir = new File(CACHE_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    public Image getImageByPath(String remotePath) {
        String localPath = getLocalPath(remotePath);

        Image image = null;
        try {
            File localFile = new File(CACHE_PATH + localPath);
            if (localFile.exists()) {
                image = new Image(new FileInputStream(localFile));
            } else {
                executor.execute(new ImageDownloadTask(remotePath, localPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return image;
    }

    private static String getLocalPath(String remotePath) {
        return remotePath.substring(remotePath.lastIndexOf("/") + 1);
    }

    private static class ImageDownloadTask implements Runnable {
        private final String remotePath;
        private final String localPath;

        public ImageDownloadTask(String remotePath, String localPath) {
            this.remotePath = remotePath;
            this.localPath = localPath;
        }

        @Override
        public void run() {
            File localFile = new File(CACHE_PATH + localPath);
            if (localFile.exists()) {
                return;
            }

            try (InputStream in = getImageURLEncoded().openStream()) {
                FileUtils.copyInputStreamToFile(in, localFile);
                eventBus.fireEvent(new ImageCachedEvent(remotePath, localPath));
            } catch (URISyntaxException e) {
                System.out.println("Invalid image URI");
            } catch (FileNotFoundException e) {
                System.out.println("NO IMAGE ON SERVER FOR MATERIAL - > " + remotePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private URL getImageURLEncoded() throws URISyntaxException, MalformedURLException {
            String remotePathEncoded = URLEncoder.encode(remotePath, StandardCharsets.UTF_8).replaceAll("%2F", "/");
            String url = Main.getProperty("server.host") + ":8080" + PortalURI.PORTAL_URI_DOWNLOAD_MATERIAL_IMAGE + remotePathEncoded;
            return new URI(url).toURL();
        }
    }

}