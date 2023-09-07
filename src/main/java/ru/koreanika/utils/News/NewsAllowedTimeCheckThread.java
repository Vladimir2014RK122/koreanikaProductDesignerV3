package ru.koreanika.utils.News;

import javafx.application.Platform;

public class NewsAllowedTimeCheckThread extends Thread{

    @Override
    public void run() {

        System.out.println("[NewsUpdaterThread] Start");

        while(true){

            Platform.runLater(() ->{
                NewsController.getNewsController().checkCardsAllowedTime();
            });


            try {



                sleep(120000);
            } catch (InterruptedException e) {
                System.out.println("[NewsUpdaterThread] Interrupted");
                break;
            }
        }

        System.out.println("[NewsUpdaterThread] Stopped");
    }
}
