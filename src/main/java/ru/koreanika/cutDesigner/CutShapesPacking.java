package ru.koreanika.cutDesigner;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import javafx.geometry.Bounds;
import ru.koreanika.project.ProjectHandler;

import java.util.*;

public class CutShapesPacking {


    //- right all shapes
    //- down all shapes
    //-right with rotate
    //-down with rotate
    public static ArrayList<CutShape> packTypeOne(Material.MaterialSheet materialSheet, ArrayList<CutShape> shapesList, Comparator<CutShape> sortType){

        ArrayList<CutShape> remainderList = new ArrayList<>(shapesList);

        double sheetX = materialSheet.getTranslateX();
        double sheetY = materialSheet.getTranslateY();

        double sheetWidth = materialSheet.getSheetWidth() * ProjectHandler.getCommonShapeScale();
        double sheetHeight = materialSheet.getSheetHeight() * ProjectHandler.getCommonShapeScale();



        //rotate all shapes:
        for(CutShape cutShape : remainderList){
            Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

            double width = cutShapeBounds.getWidth();
            double height = cutShapeBounds.getHeight();

//            if(height > sheetHeight || (width < height && height < sheetHeight)){
//                cutShape.rotateShapeLocal(90);
//            }

            if(height > sheetHeight || width < height ){
                cutShape.rotateShapeLocal(90);
            }

        }

        //sorting from large Height to small

        //remainderList.sort(new SortCutShapesByWidth());
        remainderList.sort(sortType);
        Collections.reverse(remainderList);


        //start placing horizontal:
        double positionX = 0;
        double positionY = 0;

//        double maxY = 0;



        ArrayList<CutShape> placedCutShapes = new ArrayList<>();
        boolean onlyOneSuccess = true;



//        boolean vertical = true;
        while (onlyOneSuccess) {
            onlyOneSuccess = false;

            {
                Iterator<CutShape> it = remainderList.iterator();
                while (it.hasNext()) {

                    boolean successForShape = false;

                    CutShape cutShape = it.next();

                    cutShape.setTranslateX(0);
                    cutShape.setTranslateY(0);

                    Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                    Bounds cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                    double cutShift = CutDesigner.CUT_SHAPES_CUTSHIFT;





                    if(placedCutShapes.size() == 0){
                        cutShape.setTranslateX(positionX + sheetX - cutShapeBounds.getMinX());
                        cutShape.setTranslateY(positionY + sheetY - cutShapeBounds.getMinY());
//                        System.out.println("START PLACED:" + cutShape.getShapeNumber());
                        onlyOneSuccess = true;
                        placedCutShapes.add(cutShape);
                        it.remove();
                    }else {

//                        System.out.println("\r\n");
                        //first place to right for all placed shapes:
                        for(CutShape placedCutShape : placedCutShapes){

                            cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                            cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                            Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                            Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());


                            //right place
                            //without rotate
                            {
                                if (cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2 * cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX) {

                                    cutShape.setTranslateX(0);
                                    cutShape.setTranslateY(0);
                                    cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                    cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                    boolean success = true;

                                    cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                    cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());

                                    cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                    cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                    for (CutShape placedCutShapeForCheck : placedCutShapes) {

                                        if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                            success = false;
                                            break;
                                        }
                                    }

                                    if (success) {
//                                        System.out.println("RIGHT without rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());

                                        onlyOneSuccess = true;
                                        successForShape = true;
                                        placedCutShapes.add(0,cutShape);
                                        it.remove();
                                        break;
                                    } else {
//                                        System.out.println("RIGHT without rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }

                                }else{
//                                    System.out.println("RIGHT without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                }
                            }

                        }

//                        System.out.println("\r\n");

                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                //down place
                                {
                                    if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                        boolean success = true;
                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                            Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        }else{
//                                            System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }
                                    }else{
//                                        System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }
                            }

//                        System.out.println("\r\n");
                        //second try to put right with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {
                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight){
                                    cutShape.rotateShapeLocal(90);
                                    //try to place
                                    {
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        //right place
                                        {
                                            if(cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2*cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX){
                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                        success = false;
                                                        break;
                                                    }
                                                }
                                                if (success) {
//                                                    System.out.println("RIGHT with rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
//                                                    System.out.println("RIGHT with rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    cutShape.rotateShapeLocal(-90);
                                                }
                                            }else{
//                                                System.out.println("RIGHT with rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }
                                    }
                                }
                            }

//                        //sort
//                        placedCutShapes.sort(new SortCutShapesByMinY());
//                        //second try to put down with rotate:
//                        if(!successForShape)
//                            for(CutShape placedCutShape : placedCutShapes) {
//
//                                //check able rotate or not:
//                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
//                                if(cutShapeBounds.getWidth() <= sheetHeight) {
//                                    cutShape.rotateShapeLocal(90);
//
//                                    //try to place:
//                                    {
//
//                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
//                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
//
//                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
//                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
//                                        cutShape.setTranslateX(0);
//                                        cutShape.setTranslateY(0);
//                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
//                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
//
//                                        //down place
//                                        {
//                                            if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() < sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() < sheetWidth + sheetX){
//
//                                                cutShape.setTranslateX(0);
//                                                cutShape.setTranslateY(0);
//                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
//                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
//
//                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
//                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());
//
//
//                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
//                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
//
//
//                                                boolean success = true;
//                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
//                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
//                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
//
//                                                        success = false;
//                                                        break;
//                                                    }
//                                                }
//
//                                                if (success) {
////                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
//                                                    onlyOneSuccess = true;
//                                                    successForShape = true;
//                                                    placedCutShapes.add(0,cutShape);
//                                                    it.remove();
//                                                    break;
//                                                }else{
//                                                    cutShape.rotateShapeLocal(-90);
////                                                  System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
//                                                }
//                                            }else{
////                                                System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
//                                            }
//                                        }
//
//                                    }
//                                }
//
//
//                            }
                    }

                }
            }

        }

        //if shapes don't fit for empty sheet:
        if(shapesList.size() == remainderList.size()){

//            System.out.println("CANT PLACED SHAPES");
            for(CutShape cutShape : remainderList){

                Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

                cutShape.setTranslateX(sheetWidth + sheetX + cutShape.getTranslateX() - cutShapeBounds.getMinX());
                cutShape.setTranslateY(50 + sheetY + cutShape.getTranslateY() - cutShapeBounds.getMinY());

            }
            remainderList.clear();
        }
        //remainderList.clear();
        return remainderList;
    }


    //- right all shapes
    //-right with rotate
    //- down all shapes
    //-down with rotate
    public static ArrayList<CutShape> packTypeTwo(Material.MaterialSheet materialSheet, ArrayList<CutShape> shapesList, Comparator<CutShape> sortType){

        ArrayList<CutShape> remainderList = new ArrayList<>(shapesList);

        double sheetX = materialSheet.getTranslateX();
        double sheetY = materialSheet.getTranslateY();

        double sheetWidth = materialSheet.getSheetWidth() * ProjectHandler.getCommonShapeScale();
        double sheetHeight = materialSheet.getSheetHeight() * ProjectHandler.getCommonShapeScale();



        //rotate all shapes:
        for(CutShape cutShape : remainderList){
            Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

            double width = cutShapeBounds.getWidth();
            double height = cutShapeBounds.getHeight();

//            if(height > sheetHeight || (width < height && height < sheetHeight)){
//                cutShape.rotateShapeLocal(90);
//            }

            if(height > sheetHeight || width < height ){
                cutShape.rotateShapeLocal(90);
            }

        }

        //sorting from large Height to small

        //remainderList.sort(new SortCutShapesByWidth());
        remainderList.sort(sortType);
        Collections.reverse(remainderList);


        //start placing horizontal:
        double positionX = 0;
        double positionY = 0;

//        double maxY = 0;



        ArrayList<CutShape> placedCutShapes = new ArrayList<>();
        boolean onlyOneSuccess = true;



//        boolean vertical = true;
        while (onlyOneSuccess) {
            onlyOneSuccess = false;

            {
                Iterator<CutShape> it = remainderList.iterator();
                while (it.hasNext()) {

                    boolean successForShape = false;

                    CutShape cutShape = it.next();

                    cutShape.setTranslateX(0);
                    cutShape.setTranslateY(0);

                    Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                    Bounds cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                    double cutShift = CutDesigner.CUT_SHAPES_CUTSHIFT;





                    if(placedCutShapes.size() == 0){
                        cutShape.setTranslateX(positionX + sheetX - cutShapeBounds.getMinX());
                        cutShape.setTranslateY(positionY + sheetY - cutShapeBounds.getMinY());
//                        System.out.println("START PLACED:" + cutShape.getShapeNumber());
                        onlyOneSuccess = true;
                        placedCutShapes.add(cutShape);
                        it.remove();
                    }else {

//                        System.out.println("\r\n");
                        //first place to right for all placed shapes:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes){


                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());


                                //right place
                                //without rotate
                                {
                                    if (cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2 * cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX) {

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        boolean success = true;

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {

                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
    //                                        System.out.println("RIGHT without rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());

                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        } else {
    //                                        System.out.println("RIGHT without rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }

                                    }else{
    //                                    System.out.println("RIGHT without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }

                            }

                        //second try to put right with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {
                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight){
                                    cutShape.rotateShapeLocal(90);
                                    //try to place
                                    {
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        //right place
                                        {
                                            if(cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2*cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX){
                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                        success = false;
                                                        break;
                                                    }
                                                }
                                                if (success) {
//                                                    System.out.println("RIGHT with rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
//                                                    System.out.println("RIGHT with rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    cutShape.rotateShapeLocal(-90);
                                                }
                                            }else{
//                                                System.out.println("RIGHT with rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }
                                    }
                                }
                            }

//                        System.out.println("\r\n");

                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                //down place
                                {
                                    if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                        boolean success = true;
                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                            Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        }else{
//                                            System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }
                                    }else{
//                                        System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }
                            }

//                        System.out.println("\r\n");


                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight) {
                                    cutShape.rotateShapeLocal(90);

                                    //try to place:
                                    {

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        //down place
                                        {
                                            if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                        success = false;
                                                        break;
                                                    }
                                                }

                                                if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
                                                    cutShape.rotateShapeLocal(-90);
//                                                  System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                }
                                            }else{
//                                                System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }

                                    }
                                }


                            }
                    }

                }
            }

        }

        //if shapes don't fit for empty sheet:
        if(shapesList.size() == remainderList.size()){

//            System.out.println("CANT PLACED SHAPES");
            for(CutShape cutShape : remainderList){

                Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

                cutShape.setTranslateX(sheetWidth + sheetX + cutShape.getTranslateX() - cutShapeBounds.getMinX());
                cutShape.setTranslateY(50 + sheetY + cutShape.getTranslateY() - cutShapeBounds.getMinY());

            }
            remainderList.clear();
        }
        //remainderList.clear();
        return remainderList;
    }

    //- down with rotate
    //- right all shapes
    //- right with rotate
    //- down all shapes

    public static ArrayList<CutShape> packTypeThree(Material.MaterialSheet materialSheet, ArrayList<CutShape> shapesList, Comparator<CutShape> sortType){

        ArrayList<CutShape> remainderList = new ArrayList<>(shapesList);

        double sheetX = materialSheet.getTranslateX();
        double sheetY = materialSheet.getTranslateY();

        double sheetWidth = materialSheet.getSheetWidth() * ProjectHandler.getCommonShapeScale();
        double sheetHeight = materialSheet.getSheetHeight() * ProjectHandler.getCommonShapeScale();



        //rotate all shapes:
        for(CutShape cutShape : remainderList){
            Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

            double width = cutShapeBounds.getWidth();
            double height = cutShapeBounds.getHeight();

//            if(height > sheetHeight || (width < height && height < sheetHeight)){
//                cutShape.rotateShapeLocal(90);
//            }

            if(height > sheetHeight || width < height ){
                cutShape.rotateShapeLocal(90);
            }

        }

        //sorting from large Height to small

        //remainderList.sort(new SortCutShapesByWidth());
        remainderList.sort(sortType);
        Collections.reverse(remainderList);


        //start placing horizontal:
        double positionX = 0;
        double positionY = 0;

//        double maxY = 0;



        ArrayList<CutShape> placedCutShapes = new ArrayList<>();
        boolean onlyOneSuccess = true;



//        boolean vertical = true;
        while (onlyOneSuccess) {
            onlyOneSuccess = false;

            {
                Iterator<CutShape> it = remainderList.iterator();
                while (it.hasNext()) {

                    boolean successForShape = false;

                    CutShape cutShape = it.next();

                    cutShape.setTranslateX(0);
                    cutShape.setTranslateY(0);

                    Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                    Bounds cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                    double cutShift = CutDesigner.CUT_SHAPES_CUTSHIFT;





                    if(placedCutShapes.size() == 0){
                        cutShape.setTranslateX(positionX + sheetX - cutShapeBounds.getMinX());
                        cutShape.setTranslateY(positionY + sheetY - cutShapeBounds.getMinY());
//                        System.out.println("START PLACED:" + cutShape.getShapeNumber());
                        onlyOneSuccess = true;
                        placedCutShapes.add(cutShape);
                        it.remove();
                    }else {

                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                //down place
                                {
                                    if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                        boolean success = true;
                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                            Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        }else{
//                                            System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }
                                    }else{
//                                        System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }
                            }

//                        System.out.println("\r\n");
                        //first place to right:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes){


                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());


                                //right place
                                //without rotate
                                {
                                    if (cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2 * cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX) {

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        boolean success = true;

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {

                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
                                            //                                        System.out.println("RIGHT without rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());

                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        } else {
                                            //                                        System.out.println("RIGHT without rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }

                                    }else{
                                        //                                    System.out.println("RIGHT without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }

                            }

                        //second try to put right with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {
                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight){
                                    cutShape.rotateShapeLocal(90);
                                    //try to place
                                    {
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        //right place
                                        {
                                            if(cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2*cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX){
                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                        success = false;
                                                        break;
                                                    }
                                                }
                                                if (success) {
//                                                    System.out.println("RIGHT with rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
//                                                    System.out.println("RIGHT with rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    cutShape.rotateShapeLocal(-90);
                                                }
                                            }else{
//                                                System.out.println("RIGHT with rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }
                                    }
                                }
                            }

//                        System.out.println("\r\n");



//                        System.out.println("\r\n");


                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight) {
                                    cutShape.rotateShapeLocal(90);

                                    //try to place:
                                    {

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        //down place
                                        {
                                            if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                        success = false;
                                                        break;
                                                    }
                                                }

                                                if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
                                                    cutShape.rotateShapeLocal(-90);
//                                                  System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                }
                                            }else{
//                                                System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }

                                    }
                                }


                            }
                    }

                }
            }

        }

        //if shapes don't fit for empty sheet:
        if(shapesList.size() == remainderList.size()){

//            System.out.println("CANT PLACED SHAPES");
            for(CutShape cutShape : remainderList){

                Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

                cutShape.setTranslateX(sheetWidth + sheetX + cutShape.getTranslateX() - cutShapeBounds.getMinX());
                cutShape.setTranslateY(50 + sheetY + cutShape.getTranslateY() - cutShapeBounds.getMinY());

            }
            remainderList.clear();
        }
        //remainderList.clear();
        return remainderList;
    }


    //- right all shapes
    //- down all shapes
    //- down with rotate
    //- right with rotate


    public static ArrayList<CutShape> packTypeFour(Material.MaterialSheet materialSheet, ArrayList<CutShape> shapesList, Comparator<CutShape> sortType){

        ArrayList<CutShape> remainderList = new ArrayList<>(shapesList);

        double sheetX = materialSheet.getTranslateX();
        double sheetY = materialSheet.getTranslateY();

        double sheetWidth = materialSheet.getSheetWidth() * ProjectHandler.getCommonShapeScale();
        double sheetHeight = materialSheet.getSheetHeight() * ProjectHandler.getCommonShapeScale();



        //rotate all shapes:
        for(CutShape cutShape : remainderList){
            Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

            double width = cutShapeBounds.getWidth();
            double height = cutShapeBounds.getHeight();

//            if(height > sheetHeight || (width < height && height < sheetHeight)){
//                cutShape.rotateShapeLocal(90);
//            }

            if(height > sheetHeight || width < height ){
                cutShape.rotateShapeLocal(90);
            }

        }

        //sorting from large Height to small

        //remainderList.sort(new SortCutShapesByWidth());
        remainderList.sort(sortType);
        Collections.reverse(remainderList);


        //start placing horizontal:
        double positionX = 0;
        double positionY = 0;

//        double maxY = 0;



        ArrayList<CutShape> placedCutShapes = new ArrayList<>();
        boolean onlyOneSuccess = true;



//        boolean vertical = true;
        while (onlyOneSuccess) {
            onlyOneSuccess = false;

            {
                Iterator<CutShape> it = remainderList.iterator();
                while (it.hasNext()) {

                    boolean successForShape = false;

                    CutShape cutShape = it.next();

                    cutShape.setTranslateX(0);
                    cutShape.setTranslateY(0);

                    Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                    Bounds cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                    double cutShift = CutDesigner.CUT_SHAPES_CUTSHIFT;





                    if(placedCutShapes.size() == 0){
                        cutShape.setTranslateX(positionX + sheetX - cutShapeBounds.getMinX());
                        cutShape.setTranslateY(positionY + sheetY - cutShapeBounds.getMinY());
//                        System.out.println("START PLACED:" + cutShape.getShapeNumber());
                        onlyOneSuccess = true;
                        placedCutShapes.add(cutShape);
                        it.remove();
                    }else {

                        //                        System.out.println("\r\n");
                        //first place to right:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes){


                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());


                                //right place
                                //without rotate
                                {
                                    if (cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2 * cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX) {

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        boolean success = true;

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {

                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
                                            //                                        System.out.println("RIGHT without rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());

                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        } else {
                                            //                                        System.out.println("RIGHT without rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }

                                    }else{
                                        //                                    System.out.println("RIGHT without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }

                            }

                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                cutShape.setTranslateX(0);
                                cutShape.setTranslateY(0);
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                //down place
                                {
                                    if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                        cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                        boolean success = true;
                                        for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                            Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                            if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                success = false;
                                                break;
                                            }
                                        }

                                        if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            onlyOneSuccess = true;
                                            successForShape = true;
                                            placedCutShapes.add(0,cutShape);
                                            it.remove();
                                            break;
                                        }else{
//                                            System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                        }
                                    }else{
//                                        System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                    }
                                }
                            }

                        //sort
                        placedCutShapes.sort(new SortCutShapesByMinY());
                        //second try to put down with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {

                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight) {
                                    cutShape.rotateShapeLocal(90);

                                    //try to place:
                                    {

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);
                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                        //down place
                                        {
                                            if(cutShapeBounds.getHeight() + cutShift + placedCutShapeCutZoneBounds.getMaxY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + cutShift + placedCutShapeCutZoneBounds.getMinX() <= sheetWidth + sheetX){

                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());

                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMinX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMaxY() - cutShapeCutZoneBounds.getMinY());


                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());


                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {

                                                        success = false;
                                                        break;
                                                    }
                                                }

                                                if (success) {
//                                            System.out.println("DOWN PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
                                                    cutShape.rotateShapeLocal(-90);
//                                                  System.out.println("DOWN NOT PLACED without rotate:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                }
                                            }else{
//                                                System.out.println("DOWN without rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }

                                    }
                                }


                            }

                        //second try to put right with rotate:
                        if(!successForShape)
                            for(CutShape placedCutShape : placedCutShapes) {
                                //check able rotate or not:
                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                if(cutShapeBounds.getWidth() <= sheetHeight){
                                    cutShape.rotateShapeLocal(90);
                                    //try to place
                                    {
                                        cutShape.setTranslateX(0);
                                        cutShape.setTranslateY(0);

                                        cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                        cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                        Bounds placedCutShapeBounds = placedCutShape.localToParent(placedCutShape.getPolygon().getBoundsInParent());
                                        Bounds placedCutShapeCutZoneBounds = placedCutShape.localToParent(placedCutShape.getCutZonePolygon().getBoundsInParent());
                                        //right place
                                        {
                                            if(cutShapeBounds.getHeight() + placedCutShapeBounds.getMinY() <= sheetHeight + sheetY && cutShapeBounds.getWidth() + 2*cutShift + placedCutShapeBounds.getMaxX() <= sheetWidth + sheetX){
                                                cutShape.setTranslateX(0);
                                                cutShape.setTranslateY(0);
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                cutShape.setTranslateX(placedCutShapeCutZoneBounds.getMaxX() - cutShapeCutZoneBounds.getMinX());
                                                cutShape.setTranslateY(placedCutShapeCutZoneBounds.getMinY() - cutShapeCutZoneBounds.getMinY());
                                                cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());
                                                cutShapeCutZoneBounds = cutShape.localToParent(cutShape.getCutZonePolygon().getBoundsInParent());
                                                boolean success = true;
                                                for (CutShape placedCutShapeForCheck : placedCutShapes) {
                                                    Bounds placedCutShapeCutZoneForCheckBounds = placedCutShapeForCheck.localToParent(placedCutShapeForCheck.getCutZonePolygon().getBoundsInParent());
                                                    if (CutPane.isCutShapeOverCutShape(placedCutShapeForCheck, cutShape)) {
                                                        success = false;
                                                        break;
                                                    }
                                                }
                                                if (success) {
//                                                    System.out.println("RIGHT with rotate PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    onlyOneSuccess = true;
                                                    successForShape = true;
                                                    placedCutShapes.add(0,cutShape);
                                                    it.remove();
                                                    break;
                                                }else{
//                                                    System.out.println("RIGHT with rotate NOT PLACED:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                                    cutShape.rotateShapeLocal(-90);
                                                }
                                            }else{
//                                                System.out.println("RIGHT with rotate TOO LARGE:" + cutShape.getShapeNumber() + "PlacedShape:" + placedCutShape.getShapeNumber());
                                            }
                                        }
                                    }
                                }
                            }

//                        System.out.println("\r\n");



//                        System.out.println("\r\n");



                    }

                }
            }

        }

        //if shapes don't fit for empty sheet:
        if(shapesList.size() == remainderList.size()){

//            System.out.println("CANT PLACED SHAPES");
            for(CutShape cutShape : remainderList){

                Bounds cutShapeBounds = cutShape.localToParent(cutShape.getPolygon().getBoundsInParent());

                cutShape.setTranslateX(sheetWidth + sheetX + cutShape.getTranslateX() - cutShapeBounds.getMinX());
                cutShape.setTranslateY(50 + sheetY + cutShape.getTranslateY() - cutShapeBounds.getMinY());

            }
            remainderList.clear();
        }
        //remainderList.clear();
        return remainderList;
    }



}
