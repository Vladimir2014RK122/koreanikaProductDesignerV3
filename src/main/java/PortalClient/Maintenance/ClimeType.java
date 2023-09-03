package PortalClient.Maintenance;

public enum ClimeType {

    ADVICE("ADVICE"),
    ERROR("ERROR");

    private final String name;

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
