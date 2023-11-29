package ru.koreanika.utils.print;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.PrintStream;

public class NodeHTMLExporter {

    private final PrintStream out;

    public NodeHTMLExporter(PrintStream out) {
        this.out = out;
    }

    public void export(Node node) {
        out.println("<html lang=\"ru\"><head>" +
                "<meta charset=\"UTF-8\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/sheets-of-paper-a4.css\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/receipt.css\">" +
                "<title></title>" +
                "</head><body class=\"document\">");
        export(node, 0);
        out.println("</body></html>");
    }

    public void export(Node node, int offset) {
        if (node instanceof Pane pane) {
            export(pane, offset);
        } else if (node instanceof Label label) {
            export(label, offset);
        } else {
            out.println("\t".repeat(offset) + node.getClass().getSimpleName());
        }
    }

    public void export(Pane pane, int offset) {
        out.println("\t".repeat(offset) + "<div id=\"" + pane.getId() + "\" class=\"page " + pane.getClass().getSimpleName() + " " + pane.getStyleClass() + "\" contenteditable=\"true\">");

        offset++;

        int columnCount = -1;
        if (pane instanceof GridPane gridPane) {
            columnCount = gridPane.getColumnCount();
            out.println("\t".repeat(offset) + "<div id=\"table\"><table><thead><tr>" + "<th></th>".repeat(columnCount) + "</tr></thead><tbody><tr>");
        }
        int columnCounter = 0;


        for (Node child : pane.getChildren()) {
            if (child instanceof Pane childPane) {
                export(childPane, offset);
            } else if (child instanceof Button button) {
                out.println("\t".repeat(offset) + "<!-- <div class=\"" + button.getClass().getSimpleName() + " " + button.getStyleClass() + "\"><span>" + button.getText() + "</span></div> //-->");
            } else if (child instanceof TextField textField) {
                out.println("\t".repeat(offset) + "<input id=\"" + textField.getId() + "\" type=\"text\" class=\"" + textField.getClass().getSimpleName() + " " + textField.getStyleClass() + "\" value=\"" + textField.getText() + "\"/>");
            } else if (child instanceof Label label) {
                if (pane instanceof GridPane) {
                    int columnSpan = GridPane.getColumnSpan(label);
                    columnCounter += columnSpan;
                    out.println("\t".repeat(offset) + "<td colspan=\"" + columnSpan + "\">");
                }
                String labelText = label.getText() != null ? label.getText() : "";
                out.println("\t".repeat(offset) + "<span id=\"" + label.getId() + "\" class=\"" + label.getClass().getSimpleName() + " " + label.getStyleClass() + "\">" + labelText + "</span>");
                if (pane instanceof GridPane) {
                    out.println("\t".repeat(offset) + "</td>");
                }
            } else if (child instanceof ImageView imageView) {
                String url = imageView.getImage().getUrl() != null ? imageView.getImage().getUrl().replaceFirst("file:", "//") : "no_image.png";
                out.println("\t".repeat(offset) + "<img class=\"" + imageView.getClass().getSimpleName() + " " + imageView.getStyleClass() + "\" src=\"" + url + "\">");
            } else {
                out.println("\t".repeat(offset) + "<div class=\"" + child.getClass().getSimpleName() + " " + child.getStyleClass() + "\"></div>");
            }

            if (pane instanceof GridPane) {
                if (columnCounter % columnCount == 0) {
                    out.println("\t".repeat(offset) + "</tr><tr>");
                }
            }
        }

        if (pane instanceof GridPane) {
            out.println("\t".repeat(offset) + "</tbody></table></div>");
        }

        out.println("\t".repeat(offset - 1) + "</div>");
    }


}
