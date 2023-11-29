package ru.koreanika.utils.print;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Objects;

public class OpenHTML2PDFTest {

    @Test
    public void testBasicExport() throws IOException {
        PDFExporter exporter = new PDFExporter(new File(Objects.requireNonNull(getClass().getResource("/html/index.html")).getFile()));
        exporter.export();
    }

    private static class PDFExporter {

        private final File inputHtmlFile;
        private final File outputPdfFile;

        public PDFExporter(File inputHtmlFile) {
            this.inputHtmlFile = inputHtmlFile;
            this.outputPdfFile = new File("/tmp/koreanika_receipt.pdf");
        }

        public void export() throws IOException {
            try (OutputStream os = new FileOutputStream(outputPdfFile)) {
                // baseUri will be used to resolve future resources (images, etc.)
                String baseUrl = FileSystems.getDefault().getPath("src/test/resources/html").toUri().toURL().toString();
                new PdfRendererBuilder()
                        .useFastMode()
                        .useFont(new File(Objects.requireNonNull(getClass().getResource("/html/font/Segoe-UI-Cyrillic-Normal.ttf")).getFile()), "Segoe UI Normal")
                        .useFont(new File(Objects.requireNonNull(getClass().getResource("/html/font/Segoe-UI-Cyrillic-Bold.ttf")).getFile()), "Segoe UI Bold")
                        .useFont(new File(Objects.requireNonNull(getClass().getResource("/html/font/Segoe-UI-Cyrillic-Light.ttf")).getFile()), "Segoe UI Light")
                        .withW3cDocument(html5ParseDocument(inputHtmlFile), baseUrl)
                        .toStream(os)
                        .run();
            }
        }

        private org.w3c.dom.Document html5ParseDocument(File inputHtmlFile) throws IOException {
            org.jsoup.nodes.Document document = Jsoup.parse(inputHtmlFile, "UTF-8");
            document.outputSettings()
                    .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                    .charset(StandardCharsets.UTF_8);
            return new W3CDom().fromJsoup(document);
        }
    }

}
