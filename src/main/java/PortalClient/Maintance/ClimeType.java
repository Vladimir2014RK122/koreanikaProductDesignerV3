package PortalClient.Maintance;

public enum ClimeType {

    ADVICE("ADVICE"),
    ERROR("ERROR");

    String name;
    ClimeType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
