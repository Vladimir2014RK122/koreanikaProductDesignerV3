package utils.Receipt.Koreanika;

import utils.Receipt.ReceiptManager;

public class ReceiptManagerKoreanika extends ReceiptManager {

    public ReceiptManagerKoreanika() {
        super();
        rootAnchorPane.getStylesheets().clear();

        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsKoreanika.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerCommon.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerKoreanika.css").toExternalForm());

    }

}
