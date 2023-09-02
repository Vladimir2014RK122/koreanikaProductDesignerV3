package utils.MaterialSelectionWindow;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;

public class FilterPropertyItem {

    String name;

    CheckBox checkBox = new CheckBox();

    Pane pane = new Pane();

    public FilterPropertyItem(String name){
        this.name = name;
        checkBox.setText(name);
    }

    public String getName() {
        return name;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
