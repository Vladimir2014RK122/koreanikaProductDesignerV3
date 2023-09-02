package utils.News;

import javafx.application.Platform;

public class NewsSeenCheckThread extends Thread{

    @Override
    public void run() {

        System.out.println("[NewsSeenCheckThread] Start");

        while(true){

            Platform.runLater(() ->{
                NewsController.getNewsController().checkSeenCards();
            });


            try {



                sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("[NewsUpdaterThread] Interrupted");
                break;
            }
        }

        System.out.println("[NewsSeenCheckThread] Stopped");
    }
}
