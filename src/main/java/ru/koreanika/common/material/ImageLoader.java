package ru.koreanika.common.material;

import javafx.scene.image.Image;

public interface ImageLoader {
    Image getImageByPath(String remotePath);
}
