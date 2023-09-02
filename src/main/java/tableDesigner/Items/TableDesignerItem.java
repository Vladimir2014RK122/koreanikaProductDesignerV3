package tableDesigner.Items;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.json.simple.JSONObject;
import tableDesigner.TableDesigner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public abstract class TableDesignerItem implements TableView {

    AnchorPane anchorPaneTableRow = null;

    protected int quantity = 1;

    BooleanProperty editModeProperty = new SimpleBooleanProperty(false);


    // card controls:
    AnchorPane anchorPaneCardView = null;

    Label labelHeaderCard;
    Label labelPriceForOneCard;
    Label labelQuantityCard;
    Label labelName1Card, labelName2Card, labelName3Card, labelName4Card;
    Label labelValue1Card, labelValue2Card, labelValue3Card, labelValue4Card;
    Label labelPriceForAllCard;
    Button btnMinusCard, btnPlusCard;
    Button btnDeleteCard, btnEditCard;
    ImageView imageViewBackCard, imageViewFrontCard;
    Tooltip tooltipNameCard;


    /** SETTINGS PANE START */



    /** SETTINGS PANE END */

    protected TableDesignerItem(){

        FXMLLoader fxmlLoaderCard = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/ItemCardViewTemplate.fxml")
        );

        try {
            anchorPaneCardView = fxmlLoaderCard.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        editModeProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue.booleanValue()){
                anchorPaneTableRow.setStyle("-fx-background-color: #73977a;");//selected color
                anchorPaneCardView.setStyle("-fx-background-color: BACKGROUND_COLOR_SELECTED;");//selected color
            }else{
                anchorPaneTableRow.setStyle("-fx-background-color: MAIN_COLOR_LIGHT;");//MAIN_COLOR_LIGHT
                anchorPaneCardView.setStyle("-fx-background-color: BACKGROUND_COLOR_1;");//MAIN_COLOR_LIGHT
            }
        });


    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public abstract void setRowNumber(int number);

    public abstract void updateRowPrice();

    public abstract Map<String, ImageView> getMainImageView();

    public abstract void removeThisItem();

    public BooleanProperty editModePropertyProperty() {
        return editModeProperty;
    }

    public void setEditModeProperty(boolean editModeProperty) {
        this.editModeProperty.set(editModeProperty);
    }


    public AnchorPane getCardView() {
        return anchorPaneCardView;
    }

    protected void cardControlElementsInit() {

        labelHeaderCard = (Label) anchorPaneCardView.lookup("#labelHeader");
        tooltipNameCard = labelHeaderCard.getTooltip();

        labelPriceForOneCard = (Label) anchorPaneCardView.lookup("#labelPriceForOne");
        labelQuantityCard = (Label) anchorPaneCardView.lookup("#labelQuantity");

        labelName1Card = (Label) anchorPaneCardView.lookup("#labelName1");
        labelName2Card = (Label) anchorPaneCardView.lookup("#labelName2");
        labelName3Card = (Label) anchorPaneCardView.lookup("#labelName3");
        labelName4Card = (Label) anchorPaneCardView.lookup("#labelName4");

        labelValue1Card = (Label) anchorPaneCardView.lookup("#labelValue1");
        labelValue2Card = (Label) anchorPaneCardView.lookup("#labelValue2");
        labelValue3Card = (Label) anchorPaneCardView.lookup("#labelValue3");
        labelValue4Card = (Label) anchorPaneCardView.lookup("#labelValue4");

        labelPriceForAllCard = (Label) anchorPaneCardView.lookup("#labelPriceForAll");

        btnMinusCard = (Button) anchorPaneCardView.lookup("#btnMinus");
        btnPlusCard = (Button) anchorPaneCardView.lookup("#btnPlus");

        btnDeleteCard = (Button) anchorPaneCardView.lookup("#btnDelete");
        btnEditCard = (Button) anchorPaneCardView.lookup("#btnEdit");

        imageViewBackCard = (ImageView) anchorPaneCardView.lookup("#imageViewBack");
        imageViewFrontCard = (ImageView) anchorPaneCardView.lookup("#imageViewFront");



    }



    public void updateItemView(){}



    public abstract void exitEditMode();
    /**
     * JSON part
     */

    public abstract JSONObject getJsonView();

    public static TableDesignerItem initFromJSON(JSONObject object) {
        //get type of object
        return null;
    }
}
