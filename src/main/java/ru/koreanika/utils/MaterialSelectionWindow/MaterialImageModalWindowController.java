package ru.koreanika.utils.MaterialSelectionWindow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.koreanika.Common.Material.ImageIndex;
import ru.koreanika.Common.Material.ImageLoader;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.service.ServiceLocator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MaterialImageModalWindowController {

    private final ImageIndex imageIndex;

    private final ImageLoader imageLoader;

    @FXML
    private Pane slideShowRootPane;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @FXML
    private ImageView imageView;

    private int currentImageIndex;

    private final List<Image> images;

    public MaterialImageModalWindowController() {
        try (InputStream in = getClass().getResourceAsStream("/fxmls/MaterialManager/materialImageModalWindow.fxml")) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setController(this);
            fxmlLoader.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imageIndex = ServiceLocator.getService("ImageIndex", ImageIndex.class);
        imageLoader = ServiceLocator.getService("ImageLoader", ImageLoader.class);

        images = new ArrayList<>();

        this.leftButton.setOnMouseClicked(event -> {
            currentImageIndex = (images.size() + currentImageIndex - 1) % images.size();
            this.imageView.setImage(images.get(currentImageIndex));
        });

        this.rightButton.setOnMouseClicked(event -> {
            currentImageIndex = (images.size() + currentImageIndex + 1) % images.size();
            this.imageView.setImage(images.get(currentImageIndex));
        });
    }

    public void setMaterial(Material material) {
        images.clear();

        List<String> remoteImagePaths = imageIndex.get(material.getId());
        if (remoteImagePaths == null || remoteImagePaths.isEmpty()) {
            try {
                Image image = new Image(new FileInputStream("materials_resources/no_img.png"));
                images.add(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            for (String remoteImagePath : remoteImagePaths) {
                Image image = imageLoader.getImageByPath(remoteImagePath);
                images.add(image);
            }
        }

        this.currentImageIndex = 0;
        this.imageView.setImage(images.get(this.currentImageIndex));
    }

    public Parent getRootElement() {
        return slideShowRootPane;
    }

}
