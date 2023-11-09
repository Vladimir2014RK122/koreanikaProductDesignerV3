package ru.koreanika.tableDesigner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import ru.koreanika.tableDesigner.Items.TableDesignerItem;

import java.util.ArrayList;

public class TableDesignerSession {
    @Getter
    public static ObservableList<TableDesignerItem> tableDesignerMainItemsList = FXCollections.observableList(new ArrayList<>());
    @Getter
    public static ObservableList<TableDesignerItem> tableDesignerAdditionalItemsList = FXCollections.observableList(new ArrayList<>());
    @Getter
    public static ObservableList<TableDesignerItem> tableDesignerMainWorkItemsList = FXCollections.observableList(new ArrayList<>());
    @Getter
    public static ObservableList<TableDesignerItem> tableDesignerAdditionalWorkItemsList = FXCollections.observableList(new ArrayList<>());
}
