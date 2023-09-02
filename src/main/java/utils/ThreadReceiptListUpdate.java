package utils;

import javafx.application.Platform;

public class ThreadReceiptListUpdate extends Thread {

    boolean stopThread = false;

    public boolean isStopThread() {
        return stopThread;
    }


    @Override
    public void run() {

//        while (true) {
//            try {
//
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        //update list
//                        MainWindow.refreshReceiptImagesItemsList();
//                    }
//                });
//
//
//                sleep(4000);
//            } catch (InterruptedException e) {
//                System.err.println("ThreadReceiptListUpdate interrupted");
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        //update list
//                        MainWindow.clearReceiptImagesList();
//                    }
//                });
//
//                break;
//            }
//        }
    }
}
