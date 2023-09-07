package ru.koreanika.utils.Receipt;


import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.ProjectHandler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ReceiptItem {

    private String name;
    private String units;
    private String currency;//EUR,RUB,USD
    private double count;
    private double pseudoCount = -1.0;
    private double priceForOne;
    double additionalPrice = 0;//work part for stone
    private String priceColor = "black";
    private double allPrice;

    private double RUBtoUSD = 0.0;
    private double RUBtoEUR = 0.0;

    private double coefficient = 1.0;

    public ReceiptItem(String name) {

        this.name = name;
        this.units = "";

    }

    public ReceiptItem(String name, String units, double count, String currency, double priceForOne) {

        this.name = name;
        this.units = units;
        this.priceForOne = priceForOne * ProjectHandler.getPriceMainCoefficient().doubleValue();
        this.currency = currency;
        this.count = count;

/*        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();
        if(this.currency.equals("USD")){
            this.priceForOne *= RUBtoUSD;
        }else if(this.currency.equals("EUR")){
            this.priceForOne *= RUBtoEUR;
        }

        this.currency = "RUB";*/

    }

    //for material:
    public ReceiptItem(String name, String units, double count, double pseudoCount, String currency, double priceForOne, double additionalPrice) {

        this.name = name;
        this.units = units;
        this.priceForOne = priceForOne;
        this.currency = currency;
        this.count = count;

        this.pseudoCount = pseudoCount;

        this.additionalPrice = additionalPrice;

        System.out.println("\r\nBLACK name = " + name);
        System.out.println("priceForOne = " + priceForOne);
        System.out.println("coefficient = " + coefficient);
        System.out.println("additionalPrice = " + additionalPrice);


/*        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();
        if(this.currency.equals("USD")){
            this.priceForOne *= RUBtoUSD;
        }else if(this.currency.equals("EUR")){
            this.priceForOne *= RUBtoEUR;
        }

        this.currency = "RUB";*/
    }

    public ReceiptItem(String name, String units, double count, double pseudoCount, String currency, double priceForOne, double additionalPrice, String priceColor) {

        this.name = name;
        this.units = units;
        this.priceForOne = priceForOne;
        this.currency = currency;
        this.count = count;
        this.priceColor = priceColor;

        this.pseudoCount = pseudoCount;

        this.additionalPrice = additionalPrice;


//        System.out.println("\r\nRED name = " + name);
//        System.out.println("priceForOne = " + priceForOne);
//        System.out.println("coefficient = " + coefficient);
//        System.out.println("additionalPrice = " + additionalPrice);

    }


    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public void setRUBtoEUR(double RUBtoEUR) {
        this.RUBtoEUR = RUBtoEUR;
    }

    public void setRUBtoUSD(double RUBtoUSD) {
        this.RUBtoUSD = RUBtoUSD;
    }

    public double getPseudoCountDouble(){
        return pseudoCount;
    }

    public double getAdditionalPrice(){
        return additionalPrice;
    }

    public void setAdditionalPrice(double additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public String getCurrency() {

        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCount() {
        return "" + String.format(Locale.ENGLISH, "%.2f", count);
    }

    public String getPseudoCount() {
        return "" + String.format(Locale.ENGLISH, "%.2f", pseudoCount);
    }

    public String getPriceForOne() {

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
//
        //DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        String result = formatter.format(priceForOne * coefficient);

        return result;

        //return "" + String.format(Locale.ENGLISH, "%.2f", priceForOne*coefficient);
    }

    public String getPriceForOneInRUR() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double priceForOneInRUR = 0;

        

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###", symbols);

        if(this.currency.equals("USD")){
            priceForOneInRUR = this.priceForOne*RUBtoUSD;
        }else if(this.currency.equals("EUR")){
            priceForOneInRUR = this.priceForOne*RUBtoEUR;
        }else{
            priceForOneInRUR = this.priceForOne;
        }

        String result = formatter.format(priceForOneInRUR * coefficient);

        return result;
    }

    public String getAllPrice() {

//        System.out.println("currency = " + currency);
        allPrice = count * (priceForOne * coefficient) + additionalPrice;


        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        String result = formatter.format(allPrice);
        return result;

        //return "" + String.format(Locale.ENGLISH, "%.2f", allPrice);
    }

    public String getAllPriceInRUR() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double allPriceInRUR = 0;

        allPrice = count * (priceForOne * coefficient) + additionalPrice;

        ;

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
//
        if(this.currency.equals("USD")){
            allPriceInRUR = this.allPrice*RUBtoUSD;
        }else if(this.currency.equals("EUR")){
            allPriceInRUR = this.allPrice*RUBtoEUR;
        }else{
            allPriceInRUR = this.allPrice;
        }



        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        String result = formatter.format(allPriceInRUR);
        return result;

    }

    public double getAllPriceInRURDouble() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double allPriceInRUR = 0;

        allPrice = count * (priceForOne * coefficient) + additionalPrice;

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
//
        if(this.currency.equals("USD")){
            allPriceInRUR = this.allPrice*RUBtoUSD;
        }else if(this.currency.equals("EUR")){
            allPriceInRUR = this.allPrice*RUBtoEUR;
        }else{
            allPriceInRUR = this.allPrice;
        }

//        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
//        String result = formatter.format(allPriceInRUR);
        return allPriceInRUR;
    }

    public String getPriceColor() {
        return priceColor;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
