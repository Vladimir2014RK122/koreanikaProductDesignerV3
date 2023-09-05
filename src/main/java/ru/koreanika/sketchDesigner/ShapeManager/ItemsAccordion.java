package ru.koreanika.sketchDesigner.ShapeManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import ru.koreanika.sketchDesigner.Features.*;
import ru.koreanika.sketchDesigner.lists.FeatureListElement;
import ru.koreanika.sketchDesigner.lists.FeaturesCellFactory;

import java.io.IOException;

public class ItemsAccordion {

    AnchorPane anchorPaneAccordion;
    Accordion accordionItems;

    TitledPane titledPaneSink, titledPaneGroove, titledPaneRods, titledPaneCutOuts;

    ListView<FeatureListElement> sinkListView;
    ListView<FeatureListElement> groovesListView;
    ListView<FeatureListElement> rodsListView;
    ListView<FeatureListElement> cutoutsListView;

    public ItemsAccordion() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/ShapeManager/ItemsAccordion.fxml"));

        try {
            anchorPaneAccordion = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //anchorPaneAccordion.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        //anchorPaneAccordion.getStylesheets().add(getClass().getResource("/styles/shapeManager.css").toExternalForm());

        accordionItems = (Accordion) anchorPaneAccordion.lookup("#accordionItems");
        titledPaneSink = accordionItems.getPanes().get(0);
        titledPaneGroove = accordionItems.getPanes().get(1);
        titledPaneRods = accordionItems.getPanes().get(2);
        titledPaneCutOuts = accordionItems.getPanes().get(3);

        initSinkList();
        initGrooveList();
        initRodsList();
        initCutoutsList();

    }

    public void initSinkList() {
        sinkListView = new ListView<FeatureListElement>();
        sinkListView.setCellFactory(new FeaturesCellFactory());

        for (int i = 1; i <= 11; i++) {
            sinkListView.getItems().add(new FeatureListElement("Раковина " + i, Sink.getIconImageForList(i),
                    FeatureType.SINK, i, Sink.getTooltipForList(i)));
        }
        sinkListView.getItems().add(new FeatureListElement("Раковина " + 18, Sink.getIconImageForList(18),
                FeatureType.SINK, 18, Sink.getTooltipForList(18)));

        for (int i = 12; i <= 17; i++) {
            if (i == 15) continue;//delete this sink from list
            //Sink sink = new Sink(i);
            sinkListView.getItems().add(new FeatureListElement("Раковина " + i, Sink.getIconImageForList(i), FeatureType.SINK, i, Sink.getTooltipForList(i)));
        }

        ((AnchorPane) (titledPaneSink.getContent())).getChildren().add(sinkListView);
        AnchorPane.setTopAnchor(sinkListView, 0.0);
        AnchorPane.setBottomAnchor(sinkListView, 0.0);
        AnchorPane.setLeftAnchor(sinkListView, 0.0);
        AnchorPane.setRightAnchor(sinkListView, 0.0);
    }

    public void initGrooveList() {
        groovesListView = new ListView<FeatureListElement>();
        groovesListView.setCellFactory(new FeaturesCellFactory());
        for (int i = 1; i <= 4; i++) {
            //Grooves grooves = new Grooves(i);
            groovesListView.getItems().add(new FeatureListElement("Проточка " + i, Grooves.getIconImageForList(i), FeatureType.GROOVES, i, Grooves.getTooltipForList(i)));
        }

        ((AnchorPane) (titledPaneGroove.getContent())).getChildren().add(groovesListView);
        AnchorPane.setTopAnchor(groovesListView, 0.0);
        AnchorPane.setBottomAnchor(groovesListView, 0.0);
        AnchorPane.setLeftAnchor(groovesListView, 0.0);
        AnchorPane.setRightAnchor(groovesListView, 0.0);
    }

    public void initRodsList() {
        rodsListView = new ListView<FeatureListElement>();
        rodsListView.setCellFactory(new FeaturesCellFactory());
        for (int i = 1; i <= 2; i++) {
            //Rods rods = new Rods(i);
            rodsListView.getItems().add(new FeatureListElement("Прутки " + i, Rods.getIconImageForList(i), FeatureType.RODS, i, Rods.getTooltipForList(i)));
        }

        ((AnchorPane) (titledPaneRods.getContent())).getChildren().add(rodsListView);
        AnchorPane.setTopAnchor(rodsListView, 0.0);
        AnchorPane.setBottomAnchor(rodsListView, 0.0);
        AnchorPane.setLeftAnchor(rodsListView, 0.0);
        AnchorPane.setRightAnchor(rodsListView, 0.0);
    }

    public void initCutoutsList() {
        cutoutsListView = new ListView<FeatureListElement>();
        cutoutsListView.setCellFactory(new FeaturesCellFactory());
        for (int i = 1; i <= 14; i++) {
            //Rods rods = new Rods(i);
            if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12) continue;
            cutoutsListView.getItems().add(new FeatureListElement("Вырез " + i, Cutout.getIconImageForList(i), FeatureType.CUTOUTS, i, Cutout.getTooltipForList(i)));

        }

        ((AnchorPane) (titledPaneCutOuts.getContent())).getChildren().add(cutoutsListView);
        AnchorPane.setTopAnchor(cutoutsListView, 0.0);
        AnchorPane.setBottomAnchor(cutoutsListView, 0.0);
        AnchorPane.setLeftAnchor(cutoutsListView, 0.0);
        AnchorPane.setRightAnchor(cutoutsListView, 0.0);
    }

    public AnchorPane getAnchorPaneAccordion() {
        return anchorPaneAccordion;
    }
}
