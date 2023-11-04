package ru.koreanika.utils.Receipt.Promebel;

import ru.koreanika.utils.Receipt.ReceiptManager;

public class ReceiptManagerPromebel extends ReceiptManager {

    public ReceiptManagerPromebel() {
        super();
        rootAnchorPane.getStylesheets().clear();

        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsPromebel.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerCommon.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerPromebel.css").toExternalForm());
    }

    @Override
    protected void createTopPartGridPane() {
        super.createTopPartGridPane();
        topPartChildrenCount = gridPaneTop.getChildren().size();
    }

}
