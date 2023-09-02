package utils.News;

import PortalClient.Authorization.AppType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
//import utils.AppOwner1;
import utils.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NewsCard{

    String header = "";
    Image image;
    Image imageTooltip;
    String text = "";
    Date startDate;
    Date endDate;

    AnchorPane rootAnchorPane;
    VBox vBox;
    Label labelHeader;
    ImageView imageView;
    HBox hBoxControls;
    Hyperlink hyperlink;
    Button btnMaxMin;
    Label labelText;

//    Tooltip tooltip;
    AnchorPane anchorPaneTooltip;
    ImageView tooltipImageView;

    BooleanProperty expandProperty = new SimpleBooleanProperty(false);

    BooleanProperty seen = new SimpleBooleanProperty(false);
    boolean haveHeader = false;
    boolean haveImage = false;
    boolean haveTooltipImage = false;
    boolean haveText = false;

    Properties properties = new Properties();
    String cardDirPath;

    Set<AppType> cardOwners;
    NewsCardType cardType;

    NewsCardStockType stockType;
    NewsCardStockCondition stockCondition;
    NewsCardStockItem stockItem;
    ArrayList<String> stockItemModels = new ArrayList<>();
    double stockConditionCount = 0;
    ArrayList<String> stockConditionMaterialTypes = new ArrayList<>();

    LinkedHashMap<String, ArrayList<String>> stockMaterials = null;//<MainName, ArrayList<materialCodes>>

    TooltipShowingHandler tooltipShowingHandler;



    double stockSize = 0;

    public NewsCard(File cardDir) {

        this.cardDirPath = cardDir.getPath();

        try {
            //get properties:
            readProperties(cardDirPath);

            //get header:
            if(Arrays.asList(cardDir.list()).contains("header.txt")){
                haveHeader = true;

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(cardDir.getPath() + "/header.txt"), "UTF8")
                );
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    header += line;
                }
                reader.close();
            }

            //get text:
            if(Arrays.asList(cardDir.list()).contains("text.txt")){
                haveText = true;

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(cardDir.getPath() + "/text.txt"), "UTF8")
                );
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    text += line + "\n";
                }
                reader.close();
            }

            //get image:
            if(Arrays.asList(cardDir.list()).contains("image.png")){
                haveImage = true;
                FileInputStream input = new FileInputStream(cardDir.getPath() + "/image.png");
                image = new Image(input);
                input.close();
            }

            //get imageTooltip:
            if(Arrays.asList(cardDir.list()).contains("imageTooltip.png")){
                haveTooltipImage = true;
                FileInputStream input = new FileInputStream(cardDir.getPath() + "/imageTooltip.png");
                imageTooltip = new Image(input);
                input.close();

                tooltipImageView = new ImageView(imageTooltip);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/News/newsCard.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        //rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/News/newsCard.css").toExternalForm());

        initControls();
        updateView();
        initControlsLogic();
        createTooltip();
        expand(false);

    }

    public NewsCard(String header, Image image, String text, Date startDate, Date endDate) {
        this.header = header;
        this.image = image;
        this.text = text;
        this.startDate = startDate;
        this.endDate = endDate;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/News/newsCard.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/News/newsCard.css").toExternalForm());

        initControls();
        updateView();
        initControlsLogic();
        expand(false);
    }

    public void setTooltipShowingHandler(TooltipShowingHandler tooltipShowingHandler) {
        this.tooltipShowingHandler = tooltipShowingHandler;
    }

    public void readProperties(){
        readProperties(cardDirPath);
    }
    private void readProperties(String cardDirPath){

        FileInputStream fis = null;

        try {
            //fis = new FileInputStream(cardDirPath + "/card.properties");
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(cardDirPath + "/card.properties"), StandardCharsets.UTF_8);
            properties.load(inputStreamReader);
            inputStreamReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        //get start date and endDate:
        {
            String time = (String) properties.get("time.startTime");
            String date = (String) properties.get("time.startDate");
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(
                    Integer.valueOf(date.split("\\.")[2]),
                    Integer.valueOf(date.split("\\.")[1]) - 1,
                    Integer.valueOf(date.split("\\.")[0]),
                    Integer.valueOf(time.split(":")[0]),
                    Integer.valueOf(time.split(":")[1])
            );
            startDate = calendar.getTime();

            time = (String) properties.get("time.endTime");
            date = (String) properties.get("time.endDate");
            calendar = GregorianCalendar.getInstance();
            calendar.set(
                    Integer.valueOf(date.split("\\.")[2]),
                    Integer.valueOf(date.split("\\.")[1]) - 1,
                    Integer.valueOf(date.split("\\.")[0]),
                    Integer.valueOf(time.split(":")[0]),
                    Integer.valueOf(time.split(":")[1])
            );
            endDate = calendar.getTime();

           // System.out.println("startDate = " + startDate);
            //System.out.println(endDate);
        }

        //get cardOwners
        {
            if(properties.get("common.owner") != null){
                cardOwners = new HashSet<>();

                String[] cardOwnersStr = ((String)properties.get("common.owner"))
                        .toUpperCase()
                        .replaceAll(" ", "")
                        .split(",");

                for(String s : cardOwnersStr){
                    cardOwners.add(AppType.valueOf(s));
                }
            }
        }

        seen.set(Boolean.valueOf((String)properties.get("common.seen")));

        if(properties.get("common.cardType") == null){
            cardType = NewsCardType.NEWS;
        }else{
            cardType = NewsCardType.valueOf(((String)properties.get("common.cardType")).toUpperCase());

        }

        if(properties.get("stock.stockType") == null){
            stockType = NewsCardStockType.NO;
        }else{
            stockType = NewsCardStockType.valueOf(((String)properties.get("stock.stockType")).toUpperCase());

        }

        if(properties.get("stock.stockItem") == null){
            stockItem = NewsCardStockItem.NO;
        }else{
            stockItem = NewsCardStockItem.valueOf(((String)properties.get("stock.stockItem")).toUpperCase());
        }

        if(properties.get("stock.condition") == null){
            stockCondition = NewsCardStockCondition.NO;
        }else{
            stockCondition = NewsCardStockCondition.valueOf(((String)properties.get("stock.condition")).toUpperCase());
        }

        if(properties.get("stock.conditionCount") == null){
            stockConditionCount = 0;
        }else{
            stockConditionCount = Double.valueOf((String)properties.get("stock.conditionCount"));
        }

        stockItemModels.clear();
        if(properties.get("stock.stockItemModels") == null){

        }else{
            String[] arr = ((String)properties.get("stock.stockItemModels")).split(" ");
            for (String s : arr){
                String model = s.replace("_", " ");
                stockItemModels.add(model);
            }
        }

        if(cardType == NewsCardType.STOCK && stockType == NewsCardStockType.ITEM){
            stockConditionMaterialTypes.clear();
            if(properties.get("stock.conditionMaterialTypes") != null){
                String[] arr = ((String)properties.get("stock.conditionMaterialTypes")).split(" ");
                for (String s : arr){
                    String typeM = s.replace("_", " ");
                    if(typeM.equals("acrylic")){
                        stockConditionMaterialTypes.add("Акриловый камень");
                    }else if(typeM.equals("polyester")){
                        stockConditionMaterialTypes.add("Полиэфирный камень");
                    }else if(typeM.equals("wood")){
                        stockConditionMaterialTypes.add("Массив_шпон");
                    }

                }

                //System.out.println(stockConditionMaterialTypes.toString());
            }
        }

        //get stock materials if exists:
        if(cardType == NewsCardType.STOCK){

            stockSize = Double.parseDouble((String)properties.get("stock.stockSize"));

            if(stockType == NewsCardStockType.MATERIAL){
                stockMaterials = new LinkedHashMap<>();

                for(int i=0; properties.get("stock.materialName." + i) != null;i++){
                    String name[] = ((String)properties.get("stock.materialName." + i)).split(" ");
                    ArrayList<String> codes = stockMaterials.get(name[0].replaceAll("_", " "));
                    if(codes == null){
                        codes = new ArrayList<>();
                    }
                    codes.add(name[1].replaceAll("_", " "));
                    stockMaterials.put(name[0].replaceAll("_", " "), codes);
                }
            }

        }

    }
    private void initControls(){

        vBox = (VBox) rootAnchorPane.lookup("#vBox");
        imageView = (ImageView) vBox.getChildren().get(0);
        labelHeader = (Label) vBox.getChildren().get(1);
        labelText = (Label) vBox.getChildren().get(2);
        hBoxControls = (HBox) vBox.getChildren().get(3);

        hyperlink = (Hyperlink) hBoxControls.getChildren().get(0);
        btnMaxMin = (Button) hBoxControls.getChildren().get(1);


//        vBox.getChildren().remove(hBoxControls);
    }

    private void initControlsLogic(){
        btnMaxMin.setOnAction(actionEvent -> {
            seen.set(true);
            if(expandProperty.get()){
                expand(false);
                //hyperlink.setText("подробнее...");

                //vBox.getChildren().remove(hyperlinkMore);

                properties.setProperty("common.seen", Boolean.valueOf(seen.get()).toString());
                saveProperties();
            }else{
                expand(true);
                //hyperlink.setText("свернуть");
                //vBox.getChildren().remove(hyperlink);
                //if(haveTooltipImage)vBox.getChildren().add(hyperlinkMore);
                //vBox.getChildren().add(hyperlink);
            }

            //hyperlink.setVisited(false);
            rootAnchorPane.requestFocus();
        });

        hyperlink.setOnAction(actionEvent -> {
            Window window = Main.getMainWindow().getRootAnchorPaneMainWindow().getScene().getWindow();
            showTooltip();

            rootAnchorPane.requestFocus();
        });
        seen.addListener((observableValue, aBoolean, t1) -> {
            if(seen.get()){
                //hyperlink.setVisited(true);
                //hyperlinkMore.setVisited(true);
            }
            properties.setProperty("common.seen", Boolean.valueOf(seen.get()).toString());
            saveProperties();
        });
    }


    private void updateView(){

        vBox.getChildren().clear();


//            Media media = new Media("https://www.youtube.com/embed/P_tAU3GM9XI?autoplay=1");
//            MediaPlayer mediaPlayer = new MediaPlayer(media);
//            mediaPlayer.play();
//            MediaView mediaView = new MediaView(mediaPlayer);
//            mediaView.setFitHeight(100);
//            mediaView.setFitWidth(100);
//            vBox.getChildren().add(mediaView);


//        WebView webview = new WebView();
//        webview.getEngine().load(
//                "https://www.youtube.com/embed/P_tAU3GM9XI?autoplay=1"
//        );
//        webview.setPrefSize(200, 200);


//        vBox.getChildren().add(webview);

        if(haveImage){
            imageView.setImage(image);
            vBox.getChildren().add(imageView);
        }else{
            hyperlink.setVisible(false);
//            try {
//                FileInputStream input = new FileInputStream(cardDirPath + "/giphy.gif");
//
//                image = new Image(input);
//                imageView.setImage(image);
//
//                input.close();
//                vBox.getChildren().add(imageView);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        if(haveHeader){
            labelHeader.setText(header);
            vBox.getChildren().add(labelHeader);
        }

        if(haveText){
            labelText.setText(text);

//            hyperlink.setText("еще");

            //vBox.getChildren().add(hyperlink);
            vBox.getChildren().add(hBoxControls);
        }


        if(seen.get()) hyperlink.setVisited(true);
    }

    private void expand(boolean value){

        if(value){
            if(!vBox.getChildren().contains(labelText)){
                vBox.getChildren().remove(hBoxControls);
                vBox.getChildren().add(labelText);
                vBox.getChildren().add( hBoxControls);
            }
            btnMaxMin.setStyle("-fx-background-size: 22px, 0px;");

        }else{
            vBox.getChildren().remove(labelText);
            btnMaxMin.setStyle("-fx-background-size: 0px, 22px;");
        }

        expandProperty.set(value);

    }

    private void saveProperties(){
        try {
            properties.store(new OutputStreamWriter( new FileOutputStream(cardDirPath + "/card.properties"), StandardCharsets.UTF_8),null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAllowed(){
        boolean result;

        Date currentTime = GregorianCalendar.getInstance().getTime();

        if(currentTime.after(startDate) && currentTime.before(endDate)){
            result = true;
        }else{
            result = false;
        }

        //System.out.println("current: " + currentTime.toString() + ", start: " + startDate.toString() + ", end: " + endDate.toString() + "result = " + result);

        return result;
    }

    public boolean isSeen() {
        return seen.get();
    }

    private void createTooltip(){

        if(!haveTooltipImage) return;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/News/newsCardTooltip.fxml"));
        try {
            anchorPaneTooltip = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ScrollPane scrollPane = (ScrollPane) anchorPaneTooltip.lookup("#scrollPane");
        VBox vBoxTooltip = (VBox)scrollPane.getContent();
        ImageView tooltipImageView = (ImageView) vBoxTooltip.getChildren().get(0);
        Label labelTooltipHeader = (Label)vBoxTooltip.getChildren().get(1);
        Label labelTooltipText = (Label)vBoxTooltip.getChildren().get(2);

        labelTooltipHeader.setText(header);
        labelTooltipText.setText(text);

        Button btnCloseTooltip = (Button) anchorPaneTooltip.lookup("#btnCloseTooltip");
        tooltipImageView.setImage(imageTooltip);

        scrollPane.widthProperty().addListener((observableValue, number, t1) -> {
            vBoxTooltip.setPrefWidth(t1.doubleValue());
        });
        scrollPane.heightProperty().addListener((observableValue, number, t1) -> {
            vBoxTooltip.setPrefHeight(t1.doubleValue());
        });

//        tooltip = new Tooltip();


////        anchorPaneTooltip = new AnchorPane();
////        anchorPaneTooltip.getStylesheets().add(getClass().getResource("/styles/News/newsCard.css").toExternalForm());
//
//
//        Hyperlink hyperlinkClose = new Hyperlink("Закрыть");
//
//        labelTooltipText.setWrapText(true);
//
//
//        labelTooltipHeader.setId("labelTooltipHeader");
//        labelTooltipText.setId("labelTooltipText");
//        hyperlinkClose.setId("hyperlinkClose");
//
//        anchorPaneTooltip.getChildren().add(vBoxTooltip);
//
//        tooltipImageView.setFitHeight(450);
//        tooltipImageView.setFitWidth(450);
//
//        vBoxTooltip.getChildren().add(tooltipImageView);
//        vBoxTooltip.getChildren().add(labelTooltipHeader);
//        vBoxTooltip.getChildren().add(labelTooltipText);
//        vBoxTooltip.getChildren().add(hyperlinkClose);
//
//
//
//        vBoxTooltip.setPrefWidth(tooltipImageView.getFitWidth());
//
//
//        AnchorPane.setTopAnchor(vBoxTooltip, 5.0);
//        AnchorPane.setLeftAnchor(vBoxTooltip, 5.0);
//        AnchorPane.setRightAnchor(vBoxTooltip, 5.0);
//
//        tooltip.setGraphic(anchorPaneTooltip);

        //Tooltip.install(imageView, tooltip);


        imageView.setOnMouseClicked(mouseEvent -> {
            showTooltip();
        });


        btnCloseTooltip.setOnAction(actionEvent -> {
            hyperlink.setVisited(false);
            closeTooltip();
        });
    }

    private void showTooltip(){
        seen.set(true);
        tooltipShowingHandler.onShowTooltip(anchorPaneTooltip);

//        if(showingTooltip != null){
//            Main.getMainWindow().getRootAnchorPaneMainWindow().getChildren().remove(showingTooltip);
//        }
//        showingTooltip = anchorPaneTooltip;
//        Main.getMainWindow().getRootAnchorPaneMainWindow().getChildren().add(anchorPaneTooltip);
    }
    private void closeTooltip(){
        tooltipShowingHandler.onHideTooltip(anchorPaneTooltip);
//        showingTooltip = null;
//        Main.getMainWindow().getRootAnchorPaneMainWindow().getChildren().remove(anchorPaneTooltip);
    }



    public AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public BooleanProperty getExpandProperty() {
        return expandProperty;
    }

    public Set<AppType> getCardOwners() {
        return cardOwners;
    }

    public NewsCardType getCardType() {
        return cardType;
    }

    public NewsCardStockType getStockType() {
        return stockType;
    }

    public NewsCardStockItem getStockItem() {
        return stockItem;
    }

    public ArrayList<String> getStockItemModel() {
        return stockItemModels;
    }

    public LinkedHashMap<String, ArrayList<String>> getStockMaterials() {
        return stockMaterials;
    }

    public ArrayList<String> getStockConditionMaterialTypes() {
        return stockConditionMaterialTypes;
    }

    public double getStockConditionCount() {
        return stockConditionCount;
    }

    public NewsCardStockCondition getStockCondition() {
        return stockCondition;
    }

    public double getStockSize() {
        return stockSize;
    }

    public String getHeader() {
        return header;
    }
}

interface TooltipShowingHandler{
    void onShowTooltip(AnchorPane anchorPaneTooltip);
    void onHideTooltip(AnchorPane anchorPaneTooltip);
}