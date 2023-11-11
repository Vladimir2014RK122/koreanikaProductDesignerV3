package ru.koreanika.utils.print;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.utils.LoadingProgressDialog;
import ru.koreanika.utils.MainWindow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfSaver {

    private final ProjectHandler projectHandler = ServiceLocator.getService("ProjectHandler", ProjectHandler.class);
    private static final double PIXEL_SCALE = 2.0;

    public void printToPdfBox(List<Node> printNodes, LoadingProgressDialog loadingProgressDialog) {
        loadingProgressDialog.setMessage("Подготовка");
        loadingProgressDialog.getBtnStop().setOnAction(event -> loadingProgressDialog.close());
        loadingProgressDialog.setValue(0);
        loadingProgressDialog.show();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("pdf", "*.pdf"));

        if (projectHandler.getCurrentProjectPath() != null) {
            File projectFile = new File(projectHandler.getCurrentProjectPath());
            String pdfFileName = projectFile.getName().replaceFirst("\\.\\w+$", ".pdf");

            fileChooser.setInitialDirectory(projectFile.getParentFile());
            fileChooser.setInitialFileName(pdfFileName);
        }

        File file = fileChooser.showSaveDialog(MainWindow.getReceiptManager().getSceneReceiptManager().getWindow());
        if (file == null) {
            loadingProgressDialog.close();
            return;
        }

        for (Node node : printNodes) {
            NodeHTMLExporter.export(node);
        }

        PDPage page = new PDPage();
        List<ImageView> preparedPrintImages = prepareImagesForPrinting(
                printNodes,
                page.getMediaBox().getWidth(),
                page.getMediaBox().getHeight()
        );
        double increment = 1.0 / preparedPrintImages.size();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try (PDDocument doc = new PDDocument()) {
                    final int[] pCount = {0};
                    for (ImageView imageView : preparedPrintImages) {
                        Thread.sleep(100);
                        Platform.runLater(() -> {
                            pCount[0]++;
                            loadingProgressDialog.setMessage("Сохранение " + pCount[0] + " страницы из " + preparedPrintImages.size());
                            loadingProgressDialog.setValue(loadingProgressDialog.getValue() + increment);
                        });

                        PDPage myPage = new PDPage();
                        doc.addPage(myPage);

                        BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                        ByteArrayOutputStream s = new ByteArrayOutputStream();

                        ImageIO.write(bImage, "png", s);
                        byte[] res = s.toByteArray();
                        s.close();

                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, res, null);

                        int offset = 10;
                        int iw = (int) myPage.getMediaBox().getWidth() - offset * 2;
                        int ih = (int) myPage.getMediaBox().getHeight() - offset * 2;

                        try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {
                            cont.drawImage(pdImage, offset, offset, iw, ih);
                            cont.setStrokingColor(0f, 0f, 0f, 0f);
                            System.out.println("DRAW IMAGE");
                        }
                    }
                    doc.save(file);

                    Platform.runLater(loadingProgressDialog::close);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private List<ImageView> prepareImagesForPrinting(List<Node> printNodes, double pageWidth, double pageHeight) {
        List<ImageView> preparedPrintImages = new ArrayList<>();

        printNodes.forEach(node -> {
            double nodeWidth = node.getBoundsInParent().getWidth();
            double nodeHeight = node.getBoundsInParent().getHeight();
            double nodeX = node.getBoundsInParent().getMinX();
            double nodeY = node.getBoundsInParent().getMinY();
            double viewPortWidth = nodeWidth;
            double viewPortHeight = pageHeight * viewPortWidth / pageWidth;

            int iterationCount = (int) Math.ceil(nodeHeight / viewPortHeight);

            System.out.println("BOUNDS = " + node.getBoundsInParent());

            try {
                for (int i = 0; i < iterationCount; i++) {
                    Rectangle2D viewPort = new Rectangle2D(0, viewPortHeight * i * PIXEL_SCALE, viewPortWidth, viewPortHeight);
                    SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setFill(Color.TRANSPARENT);
                    snapshotParameters.setViewport(viewPort);
                    snapshotParameters.setTransform(new Scale(PIXEL_SCALE, PIXEL_SCALE));

                    WritableImage printWritableImage = new WritableImage(
                            (int) Math.rint(PIXEL_SCALE * viewPortWidth),
                            (int) Math.rint(PIXEL_SCALE * viewPortHeight)
                    );

                    final int w = (int) printWritableImage.getWidth();
                    final int h = (int) printWritableImage.getHeight();

                    // defines the number of tiles to export (use higher value for bigger resolution)
                    int size = (int) Math.ceil(PIXEL_SCALE);
                    if ((int) Math.ceil(PIXEL_SCALE) < 2) {
                        size = 2;
                    }

                    final int tileWidth = w / size;
                    final int tileHeight = h / size;

                    double startY = (viewPortHeight * i * PIXEL_SCALE) + nodeY;

                    for (int col = 0; col < size; ++col) {
                        for (int row = 0; row < size; ++row) {
                            final int x = row * tileWidth;
                            final int y = col * tileHeight;
                            final SnapshotParameters params = new SnapshotParameters();
                            params.setViewport(new Rectangle2D(nodeX + x, startY + y, tileWidth, tileHeight));
                            params.setTransform(new Scale(PIXEL_SCALE, PIXEL_SCALE));
                            params.setFill(Color.TRANSPARENT);

                            WritableImage writableImage = new WritableImage(tileWidth, tileHeight);
                            writableImage = node.snapshot(params, writableImage);
                            printWritableImage.getPixelWriter().setPixels(x, y, tileWidth, tileHeight, writableImage.getPixelReader(), 0, 0);
                        }
                    }

                    ImageView printImageView = new ImageView(printWritableImage);
                    preparedPrintImages.add(printImageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> MainWindow.getReceiptManager().updateReceiptWidth());

        return preparedPrintImages;
    }

}
