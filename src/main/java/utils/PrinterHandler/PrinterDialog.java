package utils.PrinterHandler;

import javafx.application.Platform;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import utils.InfoMessage;
import utils.Main;
import utils.MainWindow;
import utils.ProjectHandler;
import utils.Receipt.ReceiptManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PrinterDialog {

    private boolean withFrame = true;

    AnchorPane rootAnchorPane;

    ChoiceBox<String> choiceBoxPrinter;
    TextField textFieldCopies;
    Button btnCopiesUp, btnCopiesDown;
    boolean TFCopiesValidOK = true;
    int copies = 1;

    ToggleButton toggleBtnAllPages, toggleBtnCurrentPage, toggleBtnCustomPages;
    TextField textFieldPages;
    ToggleGroup toggleGroupPagesPrint;
    boolean TFPagesValidOK = true;
    //ArrayList<Integer> pagesForPrint

    ChoiceBox<String> choiceBoxPageSize;
    ChoiceBox<String> choiceBoxPixelScale;
    TextField textFieldScale;
    ToggleButton toggleBtnPortrait, toggleBtnLandscape;
    ToggleGroup toggleGroupPageOrientation;


    TextField textFieldMarginLeft, textFieldMarginRight, textFieldMarginTop, textFieldMarginBottom;
    boolean TFMarginLeftValidOK = true, TFMarginRightValidOK = true, TFMarginTopValidOK = true, TFMarginBottomValidOK = true;

    Button btnPrint, btnCancel;

    Pane panePreviewRoot;
    Pane panePreview;
    Slider sliderPreview;
    Label labelPreview;

    private Stage printerStage;
    private Scene printerDialogScene;
    private Window windowOwner;
    private ArrayList<Node> rawPrintNodes;
    private ArrayList<Node> preparedPrintNodes = new ArrayList<>();
    private ArrayList<Node> previewPrintNodes = new ArrayList<>();


    PrinterJob printerJob;
    Printer printer;
    PageLayout pageLayout;
    Paper paper;

    PageOrientation pageOrientation;

    double pixelScale = 2;

    private PrinterDialog(Window windowOwner, ArrayList<Node> printNodes, boolean withFrame) {

        this.windowOwner = windowOwner;
        this.rawPrintNodes = printNodes;
        this.withFrame = withFrame;


        if(windowOwner != null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(PrinterDialog.class.getResource("/fxmls/PrinterDialog.fxml"));
            try {
                rootAnchorPane = fxmlLoader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            printerDialogScene = new Scene(rootAnchorPane);

            printerStage = new Stage();
            printerStage.setTitle("Печать");
            printerStage.initOwner(windowOwner);
            printerStage.setScene(printerDialogScene);
            printerStage.setX(windowOwner.getX() + windowOwner.getWidth() / 2 - printerDialogScene.getWidth() / 2);
            printerStage.setY(windowOwner.getY() + windowOwner.getHeight() / 2 - printerDialogScene.getHeight() / 2);
            printerStage.initModality(Modality.APPLICATION_MODAL);
            printerStage.centerOnScreen();
            printerStage.setResizable(false);

            printerStage.setOnCloseRequest(windowEvent -> {
                MainWindow.getReceiptManager().updateReceiptWidth();
            });



            initControlElements();
            initControlElementsLogic();

            prepareNodesForPreview();
        }



    }

    public static void showPrinterDialog(Window windowOwner, ArrayList<Node> printNodes, boolean withFrame) {


        PrinterDialog printerDialog = new PrinterDialog(windowOwner, printNodes, withFrame);
        printerDialog.printerStage.show();


    }

    public static void printToPdf(ArrayList<Node> printNodes){

        Printer printer1 =  null;
        Paper paper1 = null;

        for(Printer p : Printer.getAllPrinters()){
            if(p.getName().equalsIgnoreCase("MICROSOFT PRINT TO PDF")){
                printer1 = p;
                break;
            }
        }

        if(printer1 == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка принтера");
            alert.setHeaderText("Принтер не найден");
            alert.setContentText("Принтер с именем \"Microsoft Print to PDF\" не найден.");
            alert.show();
            return;
        }

        for(Paper p : printer1.getPrinterAttributes().getSupportedPapers()){
            if (p.getName().equalsIgnoreCase("A4")) {
                paper1 = p;
                break;
            }
        }

        if(paper1 == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка принтера");
            alert.setHeaderText("Требуемый формат бумаги не найден.");
            alert.setContentText("Формат бумаги \"А4\" не найден, попробуйте выполнить печать вручную.");
            alert.show();
            return;
        }


        PrinterDialog printerDialog = new PrinterDialog(null, printNodes, false);

        printerDialog.printer = printer1;
        printerDialog.printerJob = PrinterJob.createPrinterJob(printerDialog.printer);

        printerDialog.pageOrientation = PageOrientation.PORTRAIT;

        //double margin = (5 / 25.4) * 72;

        printerDialog.pageLayout = printerDialog.printer.createPageLayout(paper1, PageOrientation.PORTRAIT, 5, 5, 5, 5);

        printerDialog.prepareNodesForPrinting();



        printerDialog.preparedPrintNodes.forEach(node -> {
            printerDialog.printerJob.printPage(printerDialog.pageLayout, node);
        });


        printerDialog.printerJob.endJob();
        //printerDialog.printerStage.close();

    }



    private void prepareNodesForPreview() {
        previewPrintNodes.clear();

        rawPrintNodes.forEach(node -> {

            double nodeWidth = node.getBoundsInParent().getWidth();

            double nodeHeight = node.getBoundsInParent().getHeight();
            double nodeX = node.getBoundsInParent().getMinX();
            double nodeY = node.getBoundsInParent().getHeight();

            double pageWidth = pageLayout.getPrintableWidth();
            double pageHeight = pageLayout.getPrintableHeight();

            double viewPortWidth = nodeWidth;
            double viewPortHeight = pageHeight * viewPortWidth / pageWidth;

            int iterationCount = (int) Math.ceil(nodeHeight / viewPortHeight);

            for (int i = 0; i < iterationCount; i++) {

//                Rectangle2D viewPort = new Rectangle2D(0, viewPortHeight * i, viewPortWidth, viewPortHeight);
                Rectangle2D viewPort = new Rectangle2D(nodeX, viewPortHeight * i, viewPortWidth, viewPortHeight);

                SnapshotParameters snapshotParameters = new SnapshotParameters();
                snapshotParameters.setFill(Color.TRANSPARENT);
                snapshotParameters.setViewport(viewPort);

                WritableImage previewWritableImage = node.snapshot(snapshotParameters, null);
                ImageView previewImageView = new ImageView(previewWritableImage);
//                previewImageView.setFitWidth((pageWidth/72*25.4) / 2);
//                previewImageView.setFitHeight((pageHeight/72*25.4) / 2);

                previewImageView.setFitWidth(pageWidth);
                previewImageView.setFitHeight(pageHeight);


                Pane imgPane = new Pane();
                Pane panePreview = null;
                if (pageOrientation == PageOrientation.PORTRAIT) {
                    panePreview = (Pane) addFrameForPreview(previewImageView, pageLayout.getPaper().getWidth(), pageLayout.getPaper().getHeight(), imgPane);
                } else {
                    panePreview = (Pane) addFrameForPreview(previewImageView, pageLayout.getPaper().getHeight(), pageLayout.getPaper().getWidth(), imgPane);
                }
                previewPrintNodes.add(panePreview);

                previewImageView.setTranslateX(pageLayout.getLeftMargin());
                previewImageView.setTranslateY(pageLayout.getTopMargin());

            }


        });

        panePreview.getChildren().clear();
        panePreview.getChildren().add(previewPrintNodes.get(0));

        //previewPrintNodes.get(0).setTranslateX((pageLayout.getLeftMargin()/72)*25.4);


        sliderPreview.setMin(1);
        sliderPreview.setMax(previewPrintNodes.size());
        labelPreview.setText("Стр. 1 из " + previewPrintNodes.size());
        sliderPreview.setValue(1);

        if (pageOrientation == PageOrientation.PORTRAIT) {
            panePreview.setPrefWidth(pageLayout.getPaper().getWidth());
            panePreview.setPrefHeight(pageLayout.getPaper().getHeight());
        } else {
            panePreview.setPrefWidth(pageLayout.getPaper().getHeight());
            panePreview.setPrefHeight(pageLayout.getPaper().getWidth());
        }


        Iterator<Transform> it = panePreview.getTransforms().iterator();
        while (it.hasNext()) {
            Transform t = it.next();
            if (t instanceof Scale) it.remove();
        }

        double scaleX = (((panePreviewRoot.getPrefWidth() - 50) / panePreview.getPrefWidth()));
        double scaleY = (((panePreviewRoot.getPrefWidth() - 50) / panePreview.getPrefWidth()));
        Scale scale = new Scale(scaleX, scaleY);

        panePreview.getTransforms().add(scale);

        prepareNodesForPrinting();
    }

    private void prepareNodesForPrinting() {
        preparedPrintNodes.clear();


        rawPrintNodes.forEach(node -> {

            //Transform transformScale = new Scale(2,2);
            //node.getTransforms().add(transformScale);
            double nodeWidth = node.getBoundsInParent().getWidth();
            double nodeHeight = node.getBoundsInParent().getHeight();
            double nodeX = node.getBoundsInParent().getMinX();
            double nodeY = node.getBoundsInParent().getMinY();

            System.out.println("GRID PANE WIDTH = " + nodeWidth);

            double pageWidth = pageLayout.getPrintableWidth();
            double pageHeight = pageLayout.getPrintableHeight();

            double viewPortWidth = nodeWidth;
            double viewPortHeight = pageHeight * viewPortWidth / pageWidth;

            int iterationCount = (int) Math.ceil(nodeHeight / viewPortHeight);


            System.out.println("iterationCount = " + iterationCount);

            System.out.println("nodeWidth = " + nodeWidth);
            System.out.println("nodeHeight = " + nodeHeight);
            System.out.println("pageWidth = " + pageWidth);
            System.out.println("pageHeight = " + pageHeight);
            System.out.println("viewPortWidth = " + viewPortWidth);
            System.out.println("viewPortHeight = " + viewPortHeight);
            System.out.println("DPI = " + Screen.getPrimary().getDpi());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setHeaderText("Printing log: ");

            String finalStackTrace = "";
            finalStackTrace +="iterationCount = " + iterationCount + "\n";
            finalStackTrace +="nodeWidth = " + nodeWidth + "\n";
            finalStackTrace +="nodeHeight = " + nodeHeight + "\n";
            finalStackTrace +="pageWidth = " + pageWidth + "\n";
            finalStackTrace +="pageHeight = " + pageHeight + "\n";
            finalStackTrace +="viewPortWidth = " + viewPortWidth + "\n";
            finalStackTrace +="viewPortHeight = " + viewPortHeight + "\n";
            finalStackTrace +="DPI = " + Screen.getPrimary().getDpi() + "\n";


            if(choiceBoxPixelScale == null){
                pixelScale = 2;
            }else{
                pixelScale = Double.parseDouble(choiceBoxPixelScale.getSelectionModel().getSelectedItem());
            }

            //double pixelScale = 1.5;
            try{
                for (int i = 0; i < iterationCount; i++) {
                    Rectangle2D viewPort = new Rectangle2D(0, viewPortHeight * i * pixelScale, viewPortWidth, viewPortHeight);
                    SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setFill(Color.TRANSPARENT);
                    snapshotParameters.setViewport(viewPort);
                    snapshotParameters.setTransform(new Scale(pixelScale, pixelScale));


                    //WritableImage printWritableImage = new WritableImage((int) Math.rint(pixelScale * viewPortWidth), (int) Math.rint(pixelScale * viewPortHeight));
                    WritableImage printWritableImage = new WritableImage((int) Math.rint(pixelScale * viewPortWidth), (int) Math.rint(pixelScale * viewPortHeight));





                    final int w = (int)printWritableImage.getWidth();
                    final int h = (int) printWritableImage.getHeight();

                    // defines the number of tiles to export (use higher value for bigger resolution)
                    int size = (int)Math.ceil(pixelScale);
                    if((int)Math.ceil(pixelScale) < 2) size = 2;

                    final int tileWidth = w / size;
                    final int tileHeight = h / size;


                    double startY = (viewPortHeight * i*pixelScale) + nodeY;

                    for (int col = 0; col < size; ++col) {
                        for (int row = 0; row < size; ++row) {
                            final int x = row * tileWidth;
                            final int y = col * tileHeight;
                            final SnapshotParameters params = new SnapshotParameters();
                            params.setViewport(new Rectangle2D(nodeX+ x, startY + y, tileWidth, tileHeight));
                            params.setTransform(new Scale(pixelScale, pixelScale));
                            params.setFill(Color.TRANSPARENT);
                            //final CompletableFuture<Image> future = new CompletableFuture<>();
                            // keeps fx application thread unblocked
                            //Platform.runLater(() -> future.complete(node.snapshot(params, null)));

                            WritableImage writableImage = new WritableImage(tileWidth, tileHeight);
                            writableImage = node.snapshot(params, writableImage);
                            printWritableImage.getPixelWriter().setPixels(x, y, tileWidth, tileHeight, writableImage.getPixelReader(), 0, 0);
                        }
                    }


                    {
                        finalStackTrace += "(int) Math.rint(pixelScale * viewPortWidth) = " + ((int) Math.rint(pixelScale * viewPortWidth)) + "\n";
                        finalStackTrace += "(int) Math.rint(pixelScale * viewPortHeight) = " + ((int) Math.rint(pixelScale * viewPortHeight)) + "\n";
                        finalStackTrace += "printWritableImage = " + printWritableImage + "\n";

                        finalStackTrace += "node = " + node + "\n";
                        finalStackTrace += "snapshotParameters = " + snapshotParameters + "\n";
                    }

                    //printWritableImage = node.snapshot(snapshotParameters, printWritableImage);



                    ImageView printImageView = new ImageView(printWritableImage);
                    ImageView printImageView1 = new ImageView(printWritableImage);
                    printImageView.setFitWidth((pageWidth));
                    printImageView.setFitHeight((pageHeight));
                    Pane imgPane = new Pane();
                    Pane panePreview = null;
                    if (pageOrientation == PageOrientation.PORTRAIT) {
                        panePreview = (Pane) addFrameForPreview(printImageView, pageLayout.getPaper().getWidth(), pageLayout.getPaper().getHeight(), imgPane);
                    } else {
                        panePreview = (Pane) addFrameForPreview(printImageView, pageLayout.getPaper().getHeight(), pageLayout.getPaper().getWidth(), imgPane);
                    }
                    preparedPrintNodes.add(panePreview);

                    printImageView.setTranslateX(pageLayout.getLeftMargin());
                    printImageView.setTranslateY(pageLayout.getTopMargin());
                    //printImageView.setTranslateX(pageLayout.getLeftMargin());
                    //printImageView.setTranslateY(pageLayout.getTopMargin());
                    //printImageView.setTranslateX(((pageLayout.getLeftMargin()/72)*25.4)/2);
                    //printImageView.setTranslateY(((pageLayout.getTopMargin()/72)*25.4)/2);
                }

                //alert.getDialogPane().setContent(new TextArea(finalStackTrace));
                //alert.show();

            }catch(Exception e){
                finalStackTrace += e + "\n";
                for (StackTraceElement s : e.getStackTrace()) {
                    //System.err.println(s.toString());
                    finalStackTrace += s.toString() + "\n";

                }
                alert.getDialogPane().setContent(new TextArea(finalStackTrace));
                alert.show();
            }
            //node.getTransforms().remove(transformScale);
        });


        // double scale = panePreview.getWidth()/preparedPrintNodes.get(0).getBoundsInParent().getWidth();
        //preparedPrintNodes.get(0).setScaleX(scale);
        //preparedPrintNodes.get(0).setScaleY(scale);

       Platform.runLater(()->{MainWindow.getReceiptManager().updateReceiptWidth();});
    }


    private Node addFrameForPreview(ImageView imgView, double pageWidth, double pageHeight, Pane imgPane) {
        Pane pane = new AnchorPane();
        //pane.setStyle("-fx-background-color: red;");

        //imgPane.setStyle("-fx-background-color: green;");

        pane.setPrefSize(pageWidth, pageHeight);

//        if(imgView.getFitWidth() > pageWidth-(mmToPoints(25))){
//            imgView.setFitWidth(imgView.getFitWidth()-(mmToPoints(25)));
//        }
//        if(imgView.getFitHeight() > pageHeight-(mmToPoints(10))){
//            imgView.setFitHeight(imgView.getFitHeight()-(mmToPoints(10)));
//        }
//        imgView.setFitWidth(imgView.getFitWidth()-(mmToPoints(25)));
//        imgView.setFitHeight(imgView.getFitHeight()-(mmToPoints(10)));


        imgPane.getChildren().clear();
        imgPane.getChildren().add(imgView);
        imgPane.setPrefSize(pageWidth, pageHeight);

        pane.getChildren().add(imgPane);
        imgPane.setTranslateX(0);
        imgPane.setTranslateY(0);
//        AnchorPane.setTopAnchor(imgPane, (mmToPoints(5)));
//        AnchorPane.setLeftAnchor(imgPane, (mmToPoints(20)));


        if (withFrame) {
            //create frame:
            {
                Line topLine = new Line(mmToPoints(20), mmToPoints(5), pageWidth - mmToPoints(5), mmToPoints(5));
                Line bottomLine = new Line(mmToPoints(20), pageHeight - mmToPoints(5), pageWidth - mmToPoints(5), pageHeight - mmToPoints(5));
                Line leftLine = new Line(mmToPoints(20), mmToPoints(5), mmToPoints(20), pageHeight - mmToPoints(5));
                Line rightLine = new Line(pageWidth - mmToPoints(5), mmToPoints(5), pageWidth - mmToPoints(5), pageHeight - mmToPoints(5));
                topLine.setStrokeWidth(1);
                bottomLine.setStrokeWidth(1);
                leftLine.setStrokeWidth(1);
                rightLine.setStrokeWidth(1);


                pane.getChildren().add(topLine);
                pane.getChildren().add(bottomLine);
                pane.getChildren().add(leftLine);
                pane.getChildren().add(rightLine);
            }

            //create stamp:
            {
                Line topLine = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(60), pageWidth - mmToPoints(5), pageHeight - mmToPoints(60));
                Line leftLine = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(60), pageWidth - mmToPoints(190), pageHeight - mmToPoints(5));

                Line lineHorizontal1 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(55), pageWidth - mmToPoints(125), pageHeight - mmToPoints(55));
                Line lineHorizontal2 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(50), pageWidth - mmToPoints(125), pageHeight - mmToPoints(50));
                Line lineHorizontal3 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(45), pageWidth - mmToPoints(5), pageHeight - mmToPoints(45));
                Line lineHorizontal4 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(40), pageWidth - mmToPoints(125), pageHeight - mmToPoints(40));
                Line lineHorizontal5 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(35), pageWidth - mmToPoints(125), pageHeight - mmToPoints(35));
                Line lineHorizontal6 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(30), pageWidth - mmToPoints(125), pageHeight - mmToPoints(30));
                Line lineHorizontal7 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(25), pageWidth - mmToPoints(125), pageHeight - mmToPoints(25));
                Line lineHorizontal8 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(20), pageWidth - mmToPoints(5), pageHeight - mmToPoints(20));
                Line lineHorizontal9 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(15), pageWidth - mmToPoints(125), pageHeight - mmToPoints(15));
                Line lineHorizontal10 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(10), pageWidth - mmToPoints(125), pageHeight - mmToPoints(10));
                Line lineHorizontal11 = new Line(pageWidth - mmToPoints(55), pageHeight - mmToPoints(40), pageWidth - mmToPoints(5), pageHeight - mmToPoints(40));
                Line lineHorizontal12 = new Line(pageWidth - mmToPoints(55), pageHeight - mmToPoints(25), pageWidth - mmToPoints(5), pageHeight - mmToPoints(25));

                Line lineVertical1 = new Line(pageWidth - mmToPoints(183), pageHeight - mmToPoints(60), pageWidth - mmToPoints(183), pageHeight - mmToPoints(60));
                Line lineVertical2 = new Line(pageWidth - mmToPoints(173), pageHeight - mmToPoints(60), pageWidth - mmToPoints(173), pageHeight - mmToPoints(5));
                Line lineVertical3 = new Line(pageWidth - mmToPoints(150), pageHeight - mmToPoints(60), pageWidth - mmToPoints(150), pageHeight - mmToPoints(5));
                Line lineVertical4 = new Line(pageWidth - mmToPoints(135), pageHeight - mmToPoints(60), pageWidth - mmToPoints(135), pageHeight - mmToPoints(5));
                Line lineVertical5 = new Line(pageWidth - mmToPoints(125), pageHeight - mmToPoints(60), pageWidth - mmToPoints(125), pageHeight - mmToPoints(5));
                Line lineVertical6 = new Line(pageWidth - mmToPoints(55), pageHeight - mmToPoints(45), pageWidth - mmToPoints(55), pageHeight - mmToPoints(5));
                Line lineVertical7 = new Line(pageWidth - mmToPoints(40), pageHeight - mmToPoints(45), pageWidth - mmToPoints(40), pageHeight - mmToPoints(25));
                Line lineVertical8 = new Line(pageWidth - mmToPoints(23), pageHeight - mmToPoints(45), pageWidth - mmToPoints(23), pageHeight - mmToPoints(25));
                Line lineVertical9 = new Line(pageWidth - mmToPoints(35), pageHeight - mmToPoints(25), pageWidth - mmToPoints(35), pageHeight - mmToPoints(20));

                pane.getChildren().add(topLine);
                pane.getChildren().add(leftLine);

                pane.getChildren().add(lineHorizontal1);
                pane.getChildren().add(lineHorizontal2);
                pane.getChildren().add(lineHorizontal3);
                pane.getChildren().add(lineHorizontal4);
                pane.getChildren().add(lineHorizontal5);
                pane.getChildren().add(lineHorizontal6);
                pane.getChildren().add(lineHorizontal7);
                pane.getChildren().add(lineHorizontal8);
                pane.getChildren().add(lineHorizontal9);
                pane.getChildren().add(lineHorizontal10);
                pane.getChildren().add(lineHorizontal11);
                pane.getChildren().add(lineHorizontal12);

                pane.getChildren().add(lineVertical1);
                pane.getChildren().add(lineVertical2);
                pane.getChildren().add(lineVertical3);
                pane.getChildren().add(lineVertical4);
                pane.getChildren().add(lineVertical5);
                pane.getChildren().add(lineVertical6);
                pane.getChildren().add(lineVertical7);
                pane.getChildren().add(lineVertical8);
                pane.getChildren().add(lineVertical9);
            }
        }

        return pane;

    }

    private Node addFrameForPrinting(ImageView imgView, double pageWidth, double pageHeight) {
        AnchorPane anchorPane = new AnchorPane();

        anchorPane.setPrefSize(pageWidth, pageHeight);

        imgView.setFitWidth(imgView.getFitWidth() - (mmToPoints(25)));
        imgView.setFitHeight(imgView.getFitHeight() - (mmToPoints(10)));
        anchorPane.getChildren().add(imgView);
        AnchorPane.setTopAnchor(imgView, (mmToPoints(5)));
        AnchorPane.setLeftAnchor(imgView, (mmToPoints(20)));


        //create frame:
        {
            Line topLine = new Line(mmToPoints(20), mmToPoints(5), pageWidth - mmToPoints(5), mmToPoints(5));
            Line bottomLine = new Line(mmToPoints(20), pageHeight - mmToPoints(5), pageWidth - mmToPoints(5), pageHeight - mmToPoints(5));
            Line leftLine = new Line(mmToPoints(20), mmToPoints(5), mmToPoints(20), pageHeight - mmToPoints(5));
            Line rightLine = new Line(pageWidth - mmToPoints(5), mmToPoints(5), pageWidth - mmToPoints(5), pageHeight - mmToPoints(5));
            topLine.setStrokeWidth(1);
            bottomLine.setStrokeWidth(1);
            leftLine.setStrokeWidth(1);
            rightLine.setStrokeWidth(1);


            anchorPane.getChildren().add(topLine);
            anchorPane.getChildren().add(bottomLine);
            anchorPane.getChildren().add(leftLine);
            anchorPane.getChildren().add(rightLine);
        }

        //create stamp:
        {
            Line topLine = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(60), pageWidth - mmToPoints(5), pageHeight - mmToPoints(60));
            Line leftLine = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(60), pageWidth - mmToPoints(190), pageHeight - mmToPoints(5));

            Line lineHorizontal1 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(55), pageWidth - mmToPoints(125), pageHeight - mmToPoints(55));
            Line lineHorizontal2 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(50), pageWidth - mmToPoints(125), pageHeight - mmToPoints(50));
            Line lineHorizontal3 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(45), pageWidth - mmToPoints(5), pageHeight - mmToPoints(45));
            Line lineHorizontal4 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(40), pageWidth - mmToPoints(125), pageHeight - mmToPoints(40));
            Line lineHorizontal5 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(35), pageWidth - mmToPoints(125), pageHeight - mmToPoints(35));
            Line lineHorizontal6 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(30), pageWidth - mmToPoints(125), pageHeight - mmToPoints(30));
            Line lineHorizontal7 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(25), pageWidth - mmToPoints(125), pageHeight - mmToPoints(25));
            Line lineHorizontal8 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(20), pageWidth - mmToPoints(5), pageHeight - mmToPoints(20));
            Line lineHorizontal9 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(15), pageWidth - mmToPoints(125), pageHeight - mmToPoints(15));
            Line lineHorizontal10 = new Line(pageWidth - mmToPoints(190), pageHeight - mmToPoints(10), pageWidth - mmToPoints(125), pageHeight - mmToPoints(10));
            Line lineHorizontal11 = new Line(pageWidth - mmToPoints(55), pageHeight - mmToPoints(40), pageWidth - mmToPoints(5), pageHeight - mmToPoints(40));
            Line lineHorizontal12 = new Line(pageWidth - mmToPoints(55), pageHeight - mmToPoints(25), pageWidth - mmToPoints(5), pageHeight - mmToPoints(25));

            Line lineVertical1 = new Line(pageWidth - mmToPoints(183), pageHeight - mmToPoints(60), pageWidth - mmToPoints(183), pageHeight - mmToPoints(60));
            Line lineVertical2 = new Line(pageWidth - mmToPoints(173), pageHeight - mmToPoints(60), pageWidth - mmToPoints(173), pageHeight - mmToPoints(5));
            Line lineVertical3 = new Line(pageWidth - mmToPoints(150), pageHeight - mmToPoints(60), pageWidth - mmToPoints(150), pageHeight - mmToPoints(5));
            Line lineVertical4 = new Line(pageWidth - mmToPoints(135), pageHeight - mmToPoints(60), pageWidth - mmToPoints(135), pageHeight - mmToPoints(5));
            Line lineVertical5 = new Line(pageWidth - mmToPoints(125), pageHeight - mmToPoints(60), pageWidth - mmToPoints(125), pageHeight - mmToPoints(5));
            Line lineVertical6 = new Line(pageWidth - mmToPoints(55), pageHeight - mmToPoints(45), pageWidth - mmToPoints(55), pageHeight - mmToPoints(5));
            Line lineVertical7 = new Line(pageWidth - mmToPoints(40), pageHeight - mmToPoints(45), pageWidth - mmToPoints(40), pageHeight - mmToPoints(25));
            Line lineVertical8 = new Line(pageWidth - mmToPoints(23), pageHeight - mmToPoints(45), pageWidth - mmToPoints(23), pageHeight - mmToPoints(25));
            Line lineVertical9 = new Line(pageWidth - mmToPoints(35), pageHeight - mmToPoints(25), pageWidth - mmToPoints(35), pageHeight - mmToPoints(20));

            anchorPane.getChildren().add(topLine);
            anchorPane.getChildren().add(leftLine);

            anchorPane.getChildren().add(lineHorizontal1);
            anchorPane.getChildren().add(lineHorizontal2);
            anchorPane.getChildren().add(lineHorizontal3);
            anchorPane.getChildren().add(lineHorizontal4);
            anchorPane.getChildren().add(lineHorizontal5);
            anchorPane.getChildren().add(lineHorizontal6);
            anchorPane.getChildren().add(lineHorizontal7);
            anchorPane.getChildren().add(lineHorizontal8);
            anchorPane.getChildren().add(lineHorizontal9);
            anchorPane.getChildren().add(lineHorizontal10);
            anchorPane.getChildren().add(lineHorizontal11);
            anchorPane.getChildren().add(lineHorizontal12);

            anchorPane.getChildren().add(lineVertical1);
            anchorPane.getChildren().add(lineVertical2);
            anchorPane.getChildren().add(lineVertical3);
            anchorPane.getChildren().add(lineVertical4);
            anchorPane.getChildren().add(lineVertical5);
            anchorPane.getChildren().add(lineVertical6);
            anchorPane.getChildren().add(lineVertical7);
            anchorPane.getChildren().add(lineVertical8);
            anchorPane.getChildren().add(lineVertical9);
        }

        return anchorPane;

    }

    public double mmToPoints(double mm) {
        return (mm * 72) / 25.4;
    }

    public double pointsToMm(double points) {
        return (points / 72) * 25.4;
    }


    private void initControlElements() {

        choiceBoxPrinter = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxPrinter");
        textFieldCopies = (TextField) rootAnchorPane.lookup("#textFieldCopies");
        btnCopiesUp = (Button) rootAnchorPane.lookup("#btnCopiesUp");
        btnCopiesDown = (Button) rootAnchorPane.lookup("#btnCopiesDown");

        toggleBtnAllPages = (ToggleButton) rootAnchorPane.lookup("#toggleBtnAllPages");
        toggleBtnCurrentPage = (ToggleButton) rootAnchorPane.lookup("#toggleBtnCurrentPage");
        toggleBtnCustomPages = (ToggleButton) rootAnchorPane.lookup("#toggleBtnCustomPages");
        textFieldPages = (TextField) rootAnchorPane.lookup("#textFieldPages");
        toggleGroupPagesPrint = new ToggleGroup();

        choiceBoxPageSize = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxPageSize");
        choiceBoxPixelScale = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxPixelScale");

        textFieldScale = (TextField) rootAnchorPane.lookup("#textFieldScale");
        toggleBtnPortrait = (ToggleButton) rootAnchorPane.lookup("#toggleBtnPortrait");
        toggleBtnLandscape = (ToggleButton) rootAnchorPane.lookup("#toggleBtnLandscape");
        toggleGroupPageOrientation = new ToggleGroup();

        textFieldMarginLeft = (TextField) rootAnchorPane.lookup("#textFieldMarginLeft");
        textFieldMarginRight = (TextField) rootAnchorPane.lookup("#textFieldMarginRight");
        textFieldMarginTop = (TextField) rootAnchorPane.lookup("#textFieldMarginTop");
        textFieldMarginBottom = (TextField) rootAnchorPane.lookup("#textFieldMarginBottom");

        btnPrint = (Button) rootAnchorPane.lookup("#btnPrint");
        btnCancel = (Button) rootAnchorPane.lookup("#btnCancel");

        panePreviewRoot = (Pane) rootAnchorPane.lookup("#panePreviewRoot");
        panePreview = (Pane) panePreviewRoot.lookup("#panePreview");
        sliderPreview = (Slider) rootAnchorPane.lookup("#sliderPreview");
        labelPreview = (Label) rootAnchorPane.lookup("#labelPreview");


        Printer.getAllPrinters().forEach(printer -> {
            choiceBoxPrinter.getItems().add(printer.getName());
        });
        choiceBoxPrinter.getSelectionModel().select(Printer.getDefaultPrinter().getName());
        printer = Printer.getDefaultPrinter();

        textFieldCopies.setText("1");

        toggleBtnAllPages.setToggleGroup(toggleGroupPagesPrint);
        toggleBtnCurrentPage.setToggleGroup(toggleGroupPagesPrint);
        toggleBtnCustomPages.setToggleGroup(toggleGroupPagesPrint);
        toggleBtnAllPages.setSelected(true);

        textFieldPages.setText("1");

        Printer.getDefaultPrinter().getPrinterAttributes().getSupportedPapers().forEach(paper -> {
            choiceBoxPageSize.getItems().add(paper.getName());
        });
        choiceBoxPageSize.getSelectionModel().select(Printer.getDefaultPrinter().getPrinterAttributes().getDefaultPaper().getName());

        choiceBoxPixelScale.getItems().add("1");
        choiceBoxPixelScale.getItems().add("2");
//        choiceBoxPixelScale.getItems().add("3");
//        choiceBoxPixelScale.getItems().add("4");
//        choiceBoxPixelScale.getItems().add("5");
//        choiceBoxPixelScale.getItems().add("6");
//        choiceBoxPixelScale.getItems().add("7");
//        choiceBoxPixelScale.getItems().add("8");
//        choiceBoxPixelScale.getItems().add("9");
//        choiceBoxPixelScale.getItems().add("10");
//        choiceBoxPixelScale.getItems().add("11");

        choiceBoxPixelScale.getSelectionModel().select("2");

        paper = Printer.getDefaultPrinter().getPrinterAttributes().getDefaultPaper();

        if (withFrame)
            pageLayout = printer.createPageLayout(paper, PageOrientation.PORTRAIT, mmToPoints(20), mmToPoints(5), mmToPoints(5), mmToPoints(60));
        else
            pageLayout = printer.createPageLayout(paper, PageOrientation.PORTRAIT, mmToPoints(5), mmToPoints(5), mmToPoints(5), mmToPoints(5));


        pageOrientation = pageLayout.getPageOrientation();

        textFieldScale.setText("100");


        textFieldMarginLeft.setText(String.format(Locale.ENGLISH, "%.1f", (pageLayout.getLeftMargin() / 72) * 25.4));
        textFieldMarginRight.setText(String.format(Locale.ENGLISH, "%.1f", (pageLayout.getRightMargin() / 72) * 25.4));
        textFieldMarginTop.setText(String.format(Locale.ENGLISH, "%.1f", (pageLayout.getTopMargin() / 72) * 25.4));
        textFieldMarginBottom.setText(String.format(Locale.ENGLISH, "%.1f", (pageLayout.getBottomMargin() / 72) * 25.4));

        toggleBtnPortrait.setToggleGroup(toggleGroupPageOrientation);
        toggleBtnLandscape.setToggleGroup(toggleGroupPageOrientation);
        toggleBtnPortrait.setSelected(true);


    }

    private void initControlElementsLogic() {

        choiceBoxPrinter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            Printer.getAllPrinters().forEach(printer -> {
                if (printer.getName().equals(newValue)) {
                    this.printer = printer;

                    choiceBoxPageSize.getItems().clear();

                    printer.getPrinterAttributes().getSupportedPapers().forEach(paper -> {
                        choiceBoxPageSize.getItems().add(paper.getName());
                    });
                    choiceBoxPageSize.getSelectionModel().select(Printer.getDefaultPrinter().getPrinterAttributes().getDefaultPaper().getName());


                    return;
                }
            });
            choiceBoxPageSize.getSelectionModel().select(Printer.getDefaultPrinter().getPrinterAttributes().getDefaultPaper().getName());

        });

        choiceBoxPageSize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            printer.getPrinterAttributes().getSupportedPapers().forEach(paper -> {
                if (paper.getName().equals(newValue)) {
                    this.paper = paper;
                    pageOrientation = PageOrientation.PORTRAIT;
                    pageLayout = printer.createPageLayout(paper, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

                    System.out.println("Select new paper: " + paper.getName() + " " + paper.getWidth() + "x" + paper.getHeight());


                    prepareNodesForPreview();


                }
            });

        });

        choiceBoxPixelScale.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            prepareNodesForPreview();
        });

        sliderPreview.setOnMouseDragged(event -> {
            if (previewPrintNodes.size() < 2) return;
            panePreview.getChildren().clear();
            panePreview.getChildren().add(previewPrintNodes.get((int) sliderPreview.getValue() - 1));

            labelPreview.setText("Стр. " + ((int) sliderPreview.getValue()) + " из " + previewPrintNodes.size());
        });

        textFieldMarginLeft.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                textFieldMarginLeft.setStyle("-fx-text-fill: black;");
                TFMarginLeftValidOK = true;

                value = (value / 25.4) * 72;
                pageLayout = printer.createPageLayout(paper, pageOrientation, value, pageLayout.getRightMargin(), pageLayout.getTopMargin(), pageLayout.getBottomMargin());
                prepareNodesForPreview();

            } catch (NumberFormatException ex) {
                textFieldMarginLeft.setStyle("-fx-text-fill: red;");
                TFMarginLeftValidOK = false;
            }
        });

        textFieldMarginRight.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                textFieldMarginRight.setStyle("-fx-text-fill: black;");
                TFMarginRightValidOK = true;

                value = (value / 25.4) * 72;
                pageLayout = printer.createPageLayout(paper, pageOrientation, pageLayout.getLeftMargin(), value, pageLayout.getTopMargin(), pageLayout.getBottomMargin());
                prepareNodesForPreview();

            } catch (NumberFormatException ex) {
                textFieldMarginRight.setStyle("-fx-text-fill: red;");
                TFMarginRightValidOK = false;
            }
        });

        textFieldMarginTop.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                textFieldMarginTop.setStyle("-fx-text-fill: black;");
                TFMarginTopValidOK = true;

                value = (value / 25.4) * 72;
                pageLayout = printer.createPageLayout(paper, pageOrientation, pageLayout.getLeftMargin(), pageLayout.getRightMargin(), value, pageLayout.getBottomMargin());
                prepareNodesForPreview();

            } catch (NumberFormatException ex) {
                textFieldMarginTop.setStyle("-fx-text-fill: red;");
                TFMarginTopValidOK = false;
            }
        });

        textFieldMarginBottom.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                textFieldMarginBottom.setStyle("-fx-text-fill: black;");
                TFMarginBottomValidOK = true;

                value = (value / 25.4) * 72;
                pageLayout = printer.createPageLayout(paper, pageOrientation, pageLayout.getLeftMargin(), pageLayout.getRightMargin(), pageLayout.getTopMargin(), value);
                prepareNodesForPreview();

            } catch (NumberFormatException ex) {
                textFieldMarginBottom.setStyle("-fx-text-fill: red;");

                TFMarginBottomValidOK = false;
            }
        });

        textFieldPages.textProperty().addListener((observable, oldValue, newValue) -> {

            String[] arr = newValue.split(",");
            for (int i = 0; i < arr.length; i++) {
                if ((arr[i].matches("\\d+-\\d+") || arr[i].matches("\\d+")) && !arr[i].equals("")) {
                    textFieldPages.setStyle("-fx-text-fill: black;");
                    TFPagesValidOK = true;
                } else {
                    textFieldPages.setStyle("-fx-text-fill: red;");
                    TFPagesValidOK = false;
                }
            }


        });

        toggleGroupPageOrientation.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == toggleBtnPortrait) {
                pageOrientation = PageOrientation.PORTRAIT;
            } else {
                pageOrientation = PageOrientation.LANDSCAPE;
            }

            pageLayout = printer.createPageLayout(paper, pageOrientation, pageLayout.getLeftMargin(), pageLayout.getRightMargin(), pageLayout.getTopMargin(), pageLayout.getBottomMargin());
            prepareNodesForPreview();
        });

        btnPrint.setOnMouseClicked(event -> {
            if (!(TFPagesValidOK && TFMarginLeftValidOK && TFMarginRightValidOK && TFMarginTopValidOK && TFMarginBottomValidOK && TFCopiesValidOK))
                return;

            pageLayout = printer.createPageLayout(paper, pageLayout.getPageOrientation(), Printer.MarginType.HARDWARE_MINIMUM);

            printerJob = PrinterJob.createPrinterJob(printer);

            if (toggleBtnAllPages.isSelected()) {
                preparedPrintNodes.forEach(node -> {
                    printerJob.printPage(pageLayout, node);
                });
            } else if (toggleBtnCurrentPage.isSelected()) {
                int pageIndex = previewPrintNodes.indexOf(panePreview.getChildren().get(0));
                printerJob.printPage(pageLayout, preparedPrintNodes.get(pageIndex));
            } else if (toggleBtnCustomPages.isSelected()) {

            }


            printerJob.endJob();
            printerStage.close();

        });

        btnCancel.setOnMouseClicked(event -> {
            if (printerJob != null) {
                printerJob.endJob();
            }
            printerStage.close();
        });

        printerDialogScene.getWindow().setOnCloseRequest(event -> {
            if (printerJob != null) {
                printerJob.endJob();
            }
        });
    }

}
