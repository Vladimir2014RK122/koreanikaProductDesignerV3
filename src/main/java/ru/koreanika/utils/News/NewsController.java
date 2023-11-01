package ru.koreanika.utils.News;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.utils.UserPreferences;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import ru.koreanika.utils.Main;

import java.io.*;
import java.util.*;

public class NewsController {

    private static final String STATIC_NEWS_PATH = "news/static/";
    private static final String DYNAMIC_NEWS_PATH = "news/dynamic/";

    private static NewsController newsController;
    NewsAutoClosingThread newsAutoClosingThread;

    private static ArrayList<NewsCard> newsCardsStockStatic = new ArrayList<>();
    private static ArrayList<NewsCard> newsCardsStockStaticForShowing = new ArrayList<>();

    private static ArrayList<NewsCard> newsCardsSystemStatic = new ArrayList<>();
    private static ArrayList<NewsCard> newsCardsSystemStaticForShowing = new ArrayList<>();

    private static ArrayList<NewsCard> newsCardsStockDynamic = new ArrayList<>();
    private static ArrayList<NewsCard> newsCardsStockDynamicForShowing = new ArrayList<>();

    private static ArrayList<NewsCard> newsCardsSystemDynamic = new ArrayList<>();
    private static ArrayList<NewsCard> newsCardsSystemDynamicForShowing = new ArrayList<>();

    private static IntegerProperty newsBtnProperty = new SimpleIntegerProperty();
    AnchorPane mainWindowAnchorPane;


    AnchorPane anchorPaneNewsRoot;
    TabPane tabPaneView;
    Tab tabStock, tabSystem;
    ScrollPane scrollPaneStock;
    ScrollPane scrollPaneSystem;
    VBox vBoxStockStatic;
    VBox vBoxSystemStatic;

    Button btnClose;

    ImageView imageViewSystemIconSeen;
    ImageView imageViewSystemIconNotSeen;

    ImageView imageViewStockIconSeen;
    ImageView imageViewStockIconNotSeen;

    ImageView imageViewSaleIconSeen;
    ImageView imageViewSaleIconNotSeen;

    private static Pane showingTooltip = null;

    private NewsController(AnchorPane mainWindowAnchorPane){

        this.mainWindowAnchorPane = mainWindowAnchorPane;

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxmls/News/newsBar.fxml"));
        //fxmlLoader.setLocation(this.getClass().getResource("/fxmls/TableDesigner/ru.koreanika.tableDesigner.fxml"));
        try {
            anchorPaneNewsRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        initView();
        readStaticNewsCards();
        checkCardsAllowedTime();
        updateView();

        NewsAllowedTimeCheckThread newsAllowedTimeCheckThread = new NewsAllowedTimeCheckThread();
        newsAllowedTimeCheckThread.setDaemon(true);
        newsAllowedTimeCheckThread.start();

        NewsSeenCheckThread newsSeenCheckThread = new NewsSeenCheckThread();
        newsSeenCheckThread.setDaemon(true);
        newsSeenCheckThread.start();
    }

    private void initView(){
//        imageViewStockIconNotSeen = new ImageView(new Image(String.valueOf(getClass().getResource("/styles/icons/news/icons8_sale_notSeen_100px.png"))));
//        imageViewStockIconSeen = new ImageView(new Image(String.valueOf(getClass().getResource("/styles/icons/news/icons8_sale_100px.png"))));

//        imageViewStockIconSeen.setFitHeight(16);
//        imageViewStockIconSeen.setFitWidth(16);

//        imageViewStockIconNotSeen.setFitHeight(16);
//        imageViewStockIconNotSeen.setFitWidth(16);

        tabPaneView = (TabPane) anchorPaneNewsRoot.lookup("#tabPaneView");
        tabStock = tabPaneView.getTabs().get(0);
        tabSystem = tabPaneView.getTabs().get(1);


        btnClose = (Button) anchorPaneNewsRoot.lookup("#btnClose");
//        tabStock.setGraphic(imageViewStockIconNotSeen);
//        tabStock.setClosable(false);
//        tabPaneView.getTabs().add(tabStock);

//        imageViewSystemIconNotSeen = new ImageView(new Image(String.valueOf(getClass().getResource("/styles/icons/news/icons8_laptop_not_seen_100px.png"))));
//        imageViewSystemIconSeen = new ImageView(new Image(String.valueOf(getClass().getResource("/styles/icons/news/icons8_laptop_seen_100px_1.png"))));
//
//        imageViewSystemIconSeen.setFitHeight(16);
//        imageViewSystemIconSeen.setFitWidth(16);
//
//        imageViewSystemIconNotSeen.setFitHeight(16);
//        imageViewSystemIconNotSeen.setFitWidth(16);

//        Tab tabSystem = new Tab();
//        tabSystem.setGraphic(imageViewSystemIconNotSeen);
//        tabSystem.setClosable(false);
//        tabPaneView.getTabs().add(tabSystem);

//        imageViewSaleIconNotSeen = new ImageView(new Image(String.valueOf(getClass().getResource("/styles/icons/news/icons8_free_shipping_100px.png"))));
//        imageViewSaleIconSeen = new ImageView(new Image(String.valueOf(getClass().getResource("/styles/icons/news/icons8_free_shipping_100px.png"))));

//        imageViewSaleIconSeen.setFitHeight(16);
//        imageViewSaleIconSeen.setFitWidth(16);
//        Tab tabSale = new Tab();
//        tabSale.setGraphic(imageViewSaleIconNotSeen);
//        tabSale.setClosable(false);
//        tabPaneView.getTabs().add(tabSale);
//
//        tabSale.setDisable(true);
//        tabSale.setVisible(false);

        scrollPaneStock = (ScrollPane)tabStock.getContent();
        vBoxStockStatic = (VBox) scrollPaneStock.getContent();
        vBoxStockStatic.setPrefWidth(230);

//        scrollPaneStock.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneStock.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPaneSystem = (ScrollPane)tabSystem.getContent();
        vBoxSystemStatic = (VBox) scrollPaneSystem.getContent();
        vBoxSystemStatic.setPrefWidth(230);
//        scrollPaneSystem.setContent(vBoxSystemStatic);
//        tabPaneView.getTabs().get(1).setContent(scrollPaneSystem);
//        scrollPaneSystem.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneSystem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainWindowAnchorPane.heightProperty().addListener((observableValue, number, t1) -> {
            anchorPaneNewsRoot.setPrefHeight(t1.doubleValue() - 32 - 30);
        });

        mainWindowAnchorPane.widthProperty().addListener((observableValue, number, t1) -> {
            anchorPaneNewsRoot.setTranslateX(mainWindowAnchorPane.getWidth() - newsController.tabPaneView.getWidth());
        });

//        ((SplitPane)mainWindowAnchorPane.getChildren().get(1)).setOnMouseClicked(mouseEvent -> {
//            hide();
//        });
        AnchorPane.setTopAnchor(anchorPaneNewsRoot, 0.0);
        AnchorPane.setBottomAnchor(anchorPaneNewsRoot, 0.0);

        btnClose.setOnAction(actionEvent -> hide());
    }


    public void readStaticNewsCards(){

        newsCardsStockStatic.clear();
        newsCardsSystemStatic.clear();


        File dir = new File(STATIC_NEWS_PATH);

        if(dir.isFile() || dir.listFiles() == null){
            System.err.println("Update News cards ERROR. Static dir is file");
            return;
        }

        for(File cardDir : dir.listFiles()){

            NewsCard  newsCard = new NewsCard(cardDir);

            newsCard.setTooltipShowingHandler(new TooltipShowingHandler() {
                @Override
                public void onShowTooltip(AnchorPane anchorPaneTooltip) {
                    AnchorPane.setTopAnchor(anchorPaneTooltip, 0.0);
                    AnchorPane.setBottomAnchor(anchorPaneTooltip, 0.0);
                    AnchorPane.setLeftAnchor(anchorPaneTooltip, 0.0);
                    AnchorPane.setRightAnchor(anchorPaneTooltip, 230.0);

                    if(showingTooltip != null){
                        Main.getMainWindow().getRootAnchorPaneMainWindow().getChildren().remove(showingTooltip);
                    }
                    showingTooltip = anchorPaneTooltip;
                    Main.getMainWindow().getRootAnchorPaneMainWindow().getChildren().add(anchorPaneTooltip);
                    pauseAutoClosingThread(true);
                }

                @Override
                public void onHideTooltip(AnchorPane anchorPaneTooltip) {
                    showingTooltip = null;
                    Main.getMainWindow().getRootAnchorPaneMainWindow().getChildren().remove(anchorPaneTooltip);
                    pauseAutoClosingThread(false);
                }
            });

//            if(newsCard.getCardOwners() != null && !newsCard.getCardOwners().contains(Main.appType)) continue;

            if(newsCard.getCardType() == NewsCardType.STOCK || newsCard.getCardType() == NewsCardType.NEWS){
                newsCardsStockStatic.add(newsCard);
            }else if(newsCard.getCardType() == NewsCardType.SYSTEM){
                newsCardsSystemStatic.add(newsCard);
            }

        }
    }

    public static NewsController createNewsController(AnchorPane mainWindowAnchorPane){

        newsController = new NewsController(mainWindowAnchorPane);

        return newsController;

    }

    public static NewsController getNewsController() {
        return newsController;
    }



    private void updateView(){

        vBoxStockStatic.getChildren().clear();
        vBoxSystemStatic.getChildren().clear();

        for(NewsCard newsCard : newsCardsStockStaticForShowing){
            vBoxStockStatic.getChildren().add(newsCard.getRootAnchorPane());
        }
        for(NewsCard newsCard : newsCardsSystemStaticForShowing){
            vBoxSystemStatic.getChildren().add(newsCard.getRootAnchorPane());
        }

    }

    protected void checkCardsAllowedTime(){
        newsCardsStockStaticForShowing.clear();



        AppType appType = UserPreferences.getInstance().getSelectedApp();

        System.out.println("newsCardsStockStatic = " + newsCardsStockStatic);

        for(NewsCard card : newsCardsStockStatic){

            System.out.println("\r\ncard.getHeader() = " + card.getHeader());
            System.out.println("card.isAllowed() = " + card.isAllowed());
            System.out.println("card.getCardOwners() = " + card.getCardOwners());
            System.out.println("card.getCardOwners() = " + card.getCardOwners());
            if(card.isAllowed() && card.getCardOwners() != null && card.getCardOwners().contains(appType)){
                newsCardsStockStaticForShowing.add(card);
            }
        }

        newsCardsSystemStaticForShowing.clear();
        for(NewsCard card : newsCardsSystemStatic){
            if(card.isAllowed() && card.getCardOwners() != null && card.getCardOwners().contains(appType)){
                newsCardsSystemStaticForShowing.add(card);
            }
        }


        System.out.println("newsCardsStockStaticForShowing = " + newsCardsStockStaticForShowing);

        updateView();


    }


    public static IntegerProperty newsBtnProperty() {
        return newsBtnProperty;
    }

    protected void checkSeenCards(){
        boolean allSeen = true;
        boolean stockSeen = true;
        boolean systemSeen = true;

        int stockUnseenCount = 0;
        int systemUnseenCount = 0;

        for(NewsCard card : newsCardsStockStaticForShowing){
            if(card.isSeen() == false){
                stockUnseenCount++;
                allSeen = false;
                stockSeen = false;
               // break;
            }

        }

        for(NewsCard card : newsCardsSystemStaticForShowing){
            if(card.isSeen() == false){
                systemUnseenCount++;
                allSeen = false;
                systemSeen = false;
                //break;
            }

        }

        //Button btnNews = MainWindow.getBtnNews();

        if(allSeen){
            //set white icon
            newsBtnProperty.set(0);

        }else{
            //set orange color of icon
            newsBtnProperty.set(1);

        }

        //stock icon
        {
            if (stockUnseenCount != 0) {
                tabStock.setStyle("-fx-background-size: 8px;");
            }else{
                tabStock.setStyle("-fx-background-size: 0px;");

            }
        }
        //system icon
        {
            if (systemUnseenCount != 0) {
                tabSystem.setStyle("-fx-background-size: 8px;");
            }else{
                tabSystem.setStyle("-fx-background-size: 0px;");
            }
        }
    }

    public AnchorPane getView(){
        return anchorPaneNewsRoot;
    }


    public boolean isHaveNotSeenCards(){

        boolean haveStock = false;
        boolean haveSystem = false;
        for(NewsCard card : newsCardsStockStaticForShowing){
            if(card.isSeen() == false){
                haveStock = true;
                break;
            }
        }

        for(NewsCard card : newsCardsSystemStaticForShowing){
            if(card.isSeen() == false){
                haveSystem = true;
                break;
            }
        }



        System.out.println("isHaveNotSeenCards() = " + (haveStock || haveSystem));
        return haveStock || haveSystem;
    }



    public String getMaterialStockSize(Material material){
        double stockSize = 0;
        String stockName = "No stock name";
        for(NewsCard newsCard : newsCardsStockStaticForShowing){
            if(newsCard.cardType == NewsCardType.STOCK && newsCard.stockType == NewsCardStockType.MATERIAL){

                System.out.println(material.getSubType());
                System.out.println(newsCard.getStockMaterials());
                for(Map.Entry<String, ArrayList<String>> entry : newsCard.getStockMaterials().entrySet()){

                    if(material.getSubType().toLowerCase().equals(entry.getKey().toLowerCase())){

//                        System.out.println(material.getSubType().toLowerCase());
//                        System.out.println(entry.getKey().toLowerCase());

                        for(String materialCode : entry.getValue()){

//                            System.out.println(materialCode);
                            if(material.getColor().toLowerCase().indexOf(materialCode.toLowerCase()) != -1){
                                stockSize += newsCard.getStockSize();
                                stockName = newsCard.getHeader();
//                                System.out.println("STOCK NAME :" + stockName + "STOCK SIZE = " + stockSize);
                                break;
                            }
                        }

                    }

                }
            }
        }

        System.out.println("STOCK SIZE = " + stockSize);

        return stockName + "##" + stockSize;
    }

    public ArrayList<NewsCard> getStockItemCards(){

        ArrayList<NewsCard> list = new ArrayList<>();
        for(NewsCard newsCard : newsCardsStockStaticForShowing){
            if(newsCard.getStockType() == NewsCardStockType.ITEM){
                list.add(newsCard);
            }
        }

        return list;
    }

    private void initAndStartAutoClosingThread(){
        newsAutoClosingThread = new NewsAutoClosingThread();
        newsAutoClosingThread.setDaemon(true);
        newsAutoClosingThread.start();
    }

    private void pauseAutoClosingThread(boolean val){
        newsAutoClosingThread.pause(val);
    }

    public static void show(){

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setByX(1);
        translateTransition.setFromX(newsController.mainWindowAnchorPane.getWidth());
        translateTransition.setToX(newsController.mainWindowAnchorPane.getWidth() - 230);
        translateTransition.setDuration(new Duration(400));
        translateTransition.setNode(newsController.anchorPaneNewsRoot);

        newsController.mainWindowAnchorPane.getChildren().add(newsController.anchorPaneNewsRoot);

        newsController.anchorPaneNewsRoot.requestFocus();

        translateTransition.play();


        newsController.initAndStartAutoClosingThread();
        newsController.tabPaneView.setOnMouseMoved(mouseEvent -> {
            newsController.newsAutoClosingThread.resetCounter();
        });
    }

    public static void hide(){

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setByX(1);
        translateTransition.setFromX(newsController.mainWindowAnchorPane.getWidth() - 230);
        translateTransition.setToX(newsController.mainWindowAnchorPane.getWidth());
        translateTransition.setDuration(new Duration(400));
        translateTransition.setNode(newsController.anchorPaneNewsRoot);

        translateTransition.play();

        translateTransition.setOnFinished(actionEvent -> {
            newsController.mainWindowAnchorPane.getChildren().remove(newsController.anchorPaneNewsRoot);
        });
    }
}
