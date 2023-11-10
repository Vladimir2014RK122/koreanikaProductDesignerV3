package ru.koreanika.utils.receipt.domain;

import lombok.Getter;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ReceiptItem {

    double additionalPrice = 0;//work part for stone

    @Getter
    private String name;

    @Getter
    private String units;

    @Getter
    private String currency;//EUR,RUB,USD
    private double count;
    private double pseudoCount = -1.0;
    private double priceForOne;

    @Getter
    private String priceColor = "black";

    private double allPrice;

    private double coefficient = 1.0;

    public ReceiptItem(String name, String units, double count, String currency, double priceForOne) {
        this.name = name;
        this.units = units;
        this.priceForOne = priceForOne * Project.getPriceMainCoefficient().doubleValue();
        this.currency = currency;
        this.count = count;
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
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public double getPseudoCountDouble() {
        return pseudoCount;
    }

    public void setAdditionalPrice(double additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCount() {
        return String.format(Locale.ENGLISH, "%.2f", count);
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getPseudoCount() {
        return String.format(Locale.ENGLISH, "%.2f", pseudoCount);
    }

    public String getPriceForOne() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        String result = formatter.format(priceForOne * coefficient);
        return result;
    }

    public String getAllPrice() {
        allPrice = count * (priceForOne * coefficient) + additionalPrice;

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        String result = formatter.format(allPrice);
        return result;
    }

    public String getAllPriceInRUR() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double allPriceInRUR = 0;

        allPrice = count * (priceForOne * coefficient) + additionalPrice;

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        if (this.currency.equals("USD")) {
            allPriceInRUR = this.allPrice * RUBtoUSD;
        } else if (this.currency.equals("EUR")) {
            allPriceInRUR = this.allPrice * RUBtoEUR;
        } else {
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

        if (this.currency.equals("USD")) {
            allPriceInRUR = this.allPrice * RUBtoUSD;
        } else if (this.currency.equals("EUR")) {
            allPriceInRUR = this.allPrice * RUBtoEUR;
        } else {
            allPriceInRUR = this.allPrice;
        }
        return allPriceInRUR;
    }

}
