package ru.koreanika.utils.Receipt;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class ReceiptImageItem extends Pane {

    String name;
    ImageView imageView;

    public ReceiptImageItem(String name, ImageView imageView) {
        this.name = name;
        this.imageView = imageView;

        this.setId("rootPaneFeatureImg");
        this.setPrefSize(100, 140);

        Label label = new Label("");
        label.setText(name);
        label.setId("labelFeatureImage");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(100, 40);


        this.imageView.setId("imageViewFeature");
        this.imageView.setFitWidth(90);
        this.imageView.setFitHeight(90);

        imageView.setTranslateX(5);
        imageView.setTranslateY(45);
        label.setTranslateX(0);
        label.setTranslateY(0);

        this.getChildren().addAll(label, imageView);
    }

}
