package utils.Receipt.CustomReceiptItems;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public abstract class CustomReceiptItem {


    public abstract String getName();

    public abstract ImageView getImageView();

    public abstract Tooltip getTooltip();

    public abstract AnchorPane getRootAnchorPane();

}
