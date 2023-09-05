package ru.koreanika.utils.PrinterHandler;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
//import javafx.scene.paint.Color;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import ru.koreanika.utils.LoadingProgressDialog;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PdfSaver {

    private ArrayList<Node> printNodes;

    private ArrayList<ImageView> preparedPrintImages = new ArrayList<>();

    double pixelScale = 2;


    public void printToPdfBox(ArrayList<Node> printNodes, LoadingProgressDialog loadingProgressDialog){

        this.printNodes = printNodes;

        PDPage page = new PDPage();
        prepareImagesForPrinting(page.getMediaBox().getWidth(), page.getMediaBox().getHeight());

        loadingProgressDialog.setMessage("Подготовка");
        loadingProgressDialog.getBtnStop().setOnAction(event -> loadingProgressDialog.close());

        loadingProgressDialog.show();
        //System.out.println("loadingProgressDialog show");




//        double oldNodeWidth = ((AnchorPane)printNodes.get(0).getParent()).getWidth();

        //System.out.println("preparedPrintImages.size() = " + preparedPrintImages.size());

        double increment = 1.0/preparedPrintImages.size();


        //System.out.println("increment = " + increment);
        loadingProgressDialog.setValue(0);


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("pdf", "*.pdf"));

        if (ProjectHandler.getCurProjectPath() != null) {

            String[] pathArr = ProjectHandler.getCurProjectPath().split("\\\\");
            String path1 = "";
            for (int i = 0; i < pathArr.length - 1; i++) {
                path1 += "/" + pathArr[i];
            }
            //System.out.println(ProjectHandler.getCurProjectPath());
            //System.out.println(path1);

            fileChooser.setInitialDirectory(new File(path1));
            fileChooser.setInitialFileName(ProjectHandler.getCurProjectName().split("\\.")[0] + ".pdf");
        }
        File file = fileChooser.showSaveDialog(MainWindow.getReceiptManager().getSceneReceiptManager().getWindow());

        if(file == null){
//            ((AnchorPane)printNodes.get(0).getParent()).setPrefWidth(oldNodeWidth);
            loadingProgressDialog.close();
            return;
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                try (PDDocument doc = new PDDocument()) {

                    final int[] pCount = {0};
                    for(ImageView imageView : preparedPrintImages){

                        //pCount[0] = new Integer(pCount[0].intValue()+1);

                        Thread.sleep(100);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pCount[0]++;
                                loadingProgressDialog.setMessage("Сохранение " + pCount[0] + " страницы из " + preparedPrintImages.size());
                                loadingProgressDialog.setValue(loadingProgressDialog.getValue() + increment);
                            }
                        });





                        PDPage myPage = new PDPage();
                        doc.addPage(myPage);

                        BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                        ByteArrayOutputStream s = new ByteArrayOutputStream();

                        ImageIO.write(bImage, "png", s);
                        byte[] res  = s.toByteArray();
                        s.close();

                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, res, null);



                        int offset = 10;

                        int iw = (int)myPage.getMediaBox().getWidth() - offset*2;
                        int ih = (int)myPage.getMediaBox().getHeight() - offset*2;

                        try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                            cont.drawImage(pdImage, offset, offset, iw, ih);
//                            cont.setStrokingColor(Color.red);
                            cont.setStrokingColor(0f,0f,0f,0f);

                            System.out.println("DRAW IMAGE");
                        }


                    }

                    if (file != null){
                        doc.save(file);
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

//                            ((AnchorPane)printNodes.get(0).getParent()).setPrefWidth(oldNodeWidth);
                            loadingProgressDialog.close();
                        }
                    });

                    doc.close();

                }catch(IOException e){
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }

    private ArrayList<ImageView> prepareImagesForPrinting(double pageWidth, double pageHeight) {

        preparedPrintImages.clear();

        printNodes.forEach(node -> {

            //Transform transformScale = new Scale(2,2);
            //node.getTransforms().add(transformScale);

//            ((AnchorPane)node.getParent()).setPrefWidth(1000);
//            ((GridPane)node).setPrefWidth(1000);

            double nodeWidth = node.getBoundsInParent().getWidth();
            double nodeHeight = node.getBoundsInParent().getHeight();
            double nodeX = node.getBoundsInParent().getMinX();
            double nodeY = node.getBoundsInParent().getMinY();
            double viewPortWidth = nodeWidth;
            double viewPortHeight = pageHeight * viewPortWidth / pageWidth;

            int iterationCount = (int) Math.ceil(nodeHeight / viewPortHeight);

            System.out.println("BOUNDS = " + node.getBoundsInParent());

//            System.out.println("iterationCount = " + iterationCount);
//
//            System.out.println("nodeWidth = " + nodeWidth);
//            System.out.println("nodeHeight = " + nodeHeight);
//            System.out.println("pageWidth = " + pageWidth);
//            System.out.println("pageHeight = " + pageHeight);
//            System.out.println("viewPortWidth = " + viewPortWidth);
//            System.out.println("viewPortHeight = " + viewPortHeight);
//            System.out.println("DPI = " + Screen.getPrimary().getDpi());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

//            alert.setHeaderText("Printing log: ");
//
//            String finalStackTrace = "";
//            finalStackTrace +="iterationCount = " + iterationCount + "\n";
//            finalStackTrace +="nodeWidth = " + nodeWidth + "\n";
//            finalStackTrace +="nodeHeight = " + nodeHeight + "\n";
//            finalStackTrace +="pageWidth = " + pageWidth + "\n";
//            finalStackTrace +="pageHeight = " + pageHeight + "\n";
//            finalStackTrace +="viewPortWidth = " + viewPortWidth + "\n";
//            finalStackTrace +="viewPortHeight = " + viewPortHeight + "\n";
//            finalStackTrace +="DPI = " + Screen.getPrimary().getDpi() + "\n";



            //double pixelScale = 1.5;
            try{
                for (int i = 0; i < iterationCount; i++) {
//                    Rectangle2D viewPort = new Rectangle2D(0, viewPortHeight * i * pixelScale, viewPortWidth, viewPortHeight);
                    Rectangle2D viewPort = new Rectangle2D(0, viewPortHeight * i * pixelScale, viewPortWidth, viewPortHeight);
                    SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setFill(Color.TRANSPARENT);
//                    snapshotParameters.setFill(Color.WHITE);
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
//                    double startX = (viewPortWidth * i*pixelScale) + nodeX;

                    for (int col = 0; col < size; ++col) {
                        for (int row = 0; row < size; ++row) {
                            final int x = row * tileWidth;
                            final int y = col * tileHeight;
                            final SnapshotParameters params = new SnapshotParameters();
                            params.setViewport(new Rectangle2D(nodeX + x, startY + y, tileWidth, tileHeight));
                            params.setTransform(new Scale(pixelScale, pixelScale));
//                            params.setFill(Color.WHITE);
                            params.setFill(Color.TRANSPARENT);
                            //final CompletableFuture<Image> future = new CompletableFuture<>();
                            // keeps fx application thread unblocked
                            //Platform.runLater(() -> future.complete(node.snapshot(params, null)));

                            WritableImage writableImage = new WritableImage(tileWidth, tileHeight);
                            writableImage = node.snapshot(params, writableImage);
                            printWritableImage.getPixelWriter().setPixels(x, y, tileWidth, tileHeight, writableImage.getPixelReader(), 0, 0);
                        }
                    }






//                    {
//                        finalStackTrace += "(int) Math.rint(pixelScale * viewPortWidth) = " + ((int) Math.rint(pixelScale * viewPortWidth)) + "\n";
//                        finalStackTrace += "(int) Math.rint(pixelScale * viewPortHeight) = " + ((int) Math.rint(pixelScale * viewPortHeight)) + "\n";
//                        finalStackTrace += "printWritableImage = " + printWritableImage + "\n";
//
//                        finalStackTrace += "node = " + node + "\n";
//                        finalStackTrace += "snapshotParameters = " + snapshotParameters + "\n";
//                    }

                    //printWritableImage = node.snapshot(snapshotParameters, printWritableImage);



                    ImageView printImageView = new ImageView(printWritableImage);
//                    ImageView printImageView1 = new ImageView(printWritableImage);
//                    printImageView.setFitWidth((pageWidth));
//                    printImageView.setFitHeight((pageHeight));
//                    Pane imgPane = new Pane();
//                    Pane panePreview = null;
//                    if (pageOrientation == PageOrientation.PORTRAIT) {
//                        panePreview = (Pane) addFrameForPreview(printImageView, pageLayout.getPaper().getWidth(), pageLayout.getPaper().getHeight(), imgPane);
//                    } else {
//                        panePreview = (Pane) addFrameForPreview(printImageView, pageLayout.getPaper().getHeight(), pageLayout.getPaper().getWidth(), imgPane);
//                    }
                    //preparedPrintNodes.add(panePreview);
                    preparedPrintImages.add(printImageView);

                }

                //alert.getDialogPane().setContent(new TextArea(finalStackTrace));
                //alert.show();

            }catch(Exception e){
                //finalStackTrace += e + "\n";
                for (StackTraceElement s : e.getStackTrace()) {
                    //System.err.println(s.toString());
                    //finalStackTrace += s.toString() + "\n";

                }
                //alert.getDialogPane().setContent(new TextArea(finalStackTrace));
                //alert.show();
            }




            //node.getTransforms().remove(transformScale);

        });

        Platform.runLater(()->{MainWindow.getReceiptManager().updateReceiptWidth();});


        return preparedPrintImages;

        // double scale = panePreview.getWidth()/preparedPrintNodes.get(0).getBoundsInParent().getWidth();
        //preparedPrintNodes.get(0).setScaleX(scale);
        //preparedPrintNodes.get(0).setScaleY(scale);


    }

}
