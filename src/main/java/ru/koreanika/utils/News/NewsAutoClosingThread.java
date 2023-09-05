package ru.koreanika.utils.News;

public class NewsAutoClosingThread extends Thread{

    int secCounter = 0;
    private final int SHOW_TIME = 20;//sec
    private boolean paused = false;

    @Override
    public void run() {

        while(secCounter <= SHOW_TIME){
            try {
                sleep(1000);
                if(!paused)secCounter++;
            } catch (InterruptedException e) {
                System.out.println("NewsAutoClosingThread INTERRUPTED");
            }
        }
        NewsController.hide();
    }

    public void resetCounter(){
        secCounter = 0;
    }

    public void pause(boolean val) {
        paused = val;
    }
}
