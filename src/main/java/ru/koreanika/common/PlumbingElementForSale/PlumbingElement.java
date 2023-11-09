package ru.koreanika.common.PlumbingElementForSale;

import java.util.List;

public class PlumbingElement {

    int id;
    PlumbingType plumbingType;
    boolean available;
    String name;
    List<String> models;
    List<String> sizes;
    String currency;
    List<Double> prices;

    public PlumbingElement(int id, PlumbingType plumbingType, boolean available, String name, List<String> models, List<String> sizes, String currency, List<Double> prices) {
        this.id = id;
        this.plumbingType = plumbingType;
        this.available = available;
        this.name = name;
        this.models = models;
        this.sizes = sizes;
        this.currency = currency;
        this.prices = prices;

    }

    public String getSize(String model){

        int index = models.indexOf(model);
        if(index == -1) return "NOT FOUND";

        return sizes.get(index);
    }

    public double getPrice(String model){

        int index = models.indexOf(model);
        if(index == -1) return 0;

        return prices.get(index);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public void setPrices(List<Double> prices) {
        this.prices = prices;
    }

    public PlumbingType getPlumbingType() {
        return plumbingType;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "ExternalElement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", models=" + models +
                ", sizes=" + sizes +
                ", currency='" + currency + '\'' +
                ", prices=" + prices +
                '}';
    }
}
