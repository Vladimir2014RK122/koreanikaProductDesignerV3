package ru.koreanika.utils.Currency;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.List;

public class BankCurrencyThread extends Thread{

    boolean firstAnswer = false;
    FirstCurrencyServerAnswer firstCurrencyServerAnswer;

    private SimpleBooleanProperty bankServerAvailable = null;
    private SimpleDoubleProperty bankUSDValue = new SimpleDoubleProperty(-1.0);
    private SimpleDoubleProperty bankEURValue = new SimpleDoubleProperty(-1.0);

    protected BankCurrencyThread(SimpleBooleanProperty bankServerAvailable,
                                 SimpleDoubleProperty bankUSDValue,
                                 SimpleDoubleProperty bankEURValue){

        this.bankServerAvailable = bankServerAvailable;
        this.bankUSDValue = bankUSDValue;
        this.bankEURValue = bankEURValue;

        firstCurrencyServerAnswer = () ->{};
    }

    @Override
    public void run() {

        while(true){

            try{
                sendRequest();

                Thread.sleep(5000);
            }catch(InterruptedException ex){
                System.out.println("THREAD BankCurrencyThread Interrupted.");
                break;
            }
        }
    }

    private void sendRequest(){

        double EURvalue = -1.0;
        double USDvalue = -1.0;

        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read ("https://www.cbr.ru/scripts/XML_daily.asp");

            if(!bankServerAvailable.get()){
                //InfoMessage.showMessage(InfoMessage.MessageType.SUCCESS, "Удалось подключится к серверу валют.", null);
            }
            bankServerAvailable.set(true);
        } catch (Exception ex) {
            bankServerAvailable.set(false);
            //InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Не удается подключится к серверу валют.", null);
            return;
        }

        Element root = document.getRootElement();
        List<Element> list = root.elements();

        for(Element el : list){
            List<Element> list1 = el.elements();
            for(Element el1 : list1){
                if(el1.getName().equals("CharCode") && el1.getText().equals("USD")){
                    USDvalue = Double.parseDouble(el.element("Value").getText().replace(",", "."));
                }

                if(el1.getName().equals("CharCode") && el1.getText().equals("EUR")){
                    EURvalue = Double.parseDouble(el.element("Value").getText().replace(",", "."));
                }
            }
        }

        bankUSDValue.set(USDvalue);
        bankEURValue.set(EURvalue);

        System.out.println("BANK CURRENCY ANSWER: USD = " + USDvalue + ", EUR = " + EURvalue);
//        if(EURvalue > 0) MainWindow.getEURValue().set(EURvalue);
//        if(USDvalue > 0) MainWindow.getUSDValue().set(USDvalue);
//

//        MainWindow.checkCurrency();

        if(!firstAnswer){
            firstAnswer = true;
            System.out.println("FIRST CURRENCY SERVER ANSWER");
            //
            firstCurrencyServerAnswer.firstAnswer();
        }

    }

    protected void setFirstCurrencyServerAnswer(FirstCurrencyServerAnswer firstCurrencyServerAnswer) {
        this.firstCurrencyServerAnswer = firstCurrencyServerAnswer;
    }
}
