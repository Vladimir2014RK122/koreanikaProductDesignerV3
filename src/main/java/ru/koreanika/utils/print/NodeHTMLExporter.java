package ru.koreanika.utils.print;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class NodeHTMLExporter {

    private static int id = 0;

    public static void export(Node node) {
        System.err.println("<html lang=\"ru\"><head></head><body>");
        export(node, 0);
        System.err.println("</body></html>");
    }

    public static void export(Node node, int offset) {
        if (node instanceof Pane pane) {
            export(pane, offset);
        } else if (node instanceof Label label) {
            export(label, offset);
        } else {
            System.err.println("\t".repeat(offset) + node.getClass().getName());
        }
    }

    public static void export(Pane pane, int offset) {
        System.err.println("\t".repeat(offset) + "<div id=\"" + pane.getId() + "-" + id++ + "\" class=\"" + pane.getClass().getName() + " " + pane.getStyleClass() + "\">");

        offset++;

        int columnCount = -1;
        if (pane instanceof GridPane gridPane) {
            columnCount = gridPane.getColumnCount();
            System.err.println("\t".repeat(offset) + "<table><thead><tr>" + "<th></th>".repeat(columnCount) + "</tr></thead><tbody><tr>");
        }
        int columnCounter = 0;


        for (Node child : pane.getChildren()) {
            if (child instanceof Pane childPane) {
                export(childPane, offset);
            } else if (child instanceof Button button) {
                System.err.println("\t".repeat(offset) + "<div class=\"" + button.getClass().getName() + " " + button.getStyleClass() + "\"><span>" + button.getText() + "</span></div>");
            } else if (child instanceof TextField textField) {
                System.err.println("\t".repeat(offset) + "<input type=\"text\" class=\"" + textField.getClass().getName() + " " + textField.getStyleClass() + "\" value=\"" + textField.getText() + "\"/>");
            } else if (child instanceof Label label) {
                if (pane instanceof GridPane) {
                    int columnSpan = GridPane.getColumnSpan(label);
                    columnCounter += columnSpan;
                    System.err.println("\t".repeat(offset) + "<td colspan=\"" + columnSpan + "\">");
                }
                System.err.println("\t".repeat(offset) + "<span class=\"" + label.getClass().getName() + " " + label.getStyleClass() + "\">" + label.getText() + "</span>");
                if (pane instanceof GridPane) {
                    System.err.println("\t".repeat(offset) + "</td>");
                }
            } else if (child instanceof ImageView imageView) {
                String url = imageView.getImage().getUrl() != null ? imageView.getImage().getUrl().replaceFirst("file:", "//") : "no_image.png";
                System.err.println("\t".repeat(offset) + "<img class=\"" + imageView.getClass().getName() + " " + imageView.getStyleClass() + "\" src=\"" + url + "\">");
            } else {
                System.err.println("\t".repeat(offset) + "<div class=\"" + child.getClass().getName() + " " + child.getStyleClass() + "\"></div>");
            }

            if (pane instanceof GridPane) {
                if (columnCounter % columnCount == 0) {
                    System.err.println("\t".repeat(offset) + "</tr><tr>");
                }
            }
        }

        if (pane instanceof GridPane) {
            System.err.println("\t".repeat(offset) + "</tbody></table>");
        }

        System.err.println("\t".repeat(offset - 1) + "</div>");
    }


}
