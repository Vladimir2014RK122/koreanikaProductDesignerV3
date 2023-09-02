package Common.PlumbingElementForSale;

public enum PlumbingType {

    SINK("Раковина", 1),
    FAUCET("Смеситель", 2),
    DISPENSER("Дозатор", 3),
    SIPHON("Сифон", 4);

    String name;
    int number;

    PlumbingType(String name, int number){
        this.name = name;
        this.number = number;
    }

    public static PlumbingType getByNumber(int number){
        if(number == 1) return SINK;
        else if(number == 2) return FAUCET;
        else if(number == 3) return DISPENSER;
        else if(number == 4) return SIPHON;
        else return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
