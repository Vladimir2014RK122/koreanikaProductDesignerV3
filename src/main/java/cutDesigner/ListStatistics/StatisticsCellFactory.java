package cutDesigner.ListStatistics;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import utils.MaterialSelectionWindow.ListCellItems.MaterialListCellItem;

public class StatisticsCellFactory  implements Callback<ListView<StatisticCellItem>, ListCell<StatisticCellItem>> {

    @Override
    public ListCell<StatisticCellItem> call(ListView<StatisticCellItem> param) {

        ListCell<StatisticCellItem> cell = new ListCell<StatisticCellItem>() {
            @Override
            protected void updateItem(StatisticCellItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {


                    //setText(item.getName());
                    setGraphic(item.getView());
                }
            }
        };



        cell.setOnMousePressed(event -> {
            //cell.getItem().changeMaximize(false);
        });

        return cell;
    }

    public void onMousePressed(MouseEvent event) {

        System.out.println("event.getClickCount() = " + event.getClickCount());

    }
}
