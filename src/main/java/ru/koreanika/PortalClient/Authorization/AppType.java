package ru.koreanika.PortalClient.Authorization;

public enum AppType {

    ZETTA("zetta", "z"),
    KOREANIKA("koreanika", "k"),
    KOREANIKAMASTER("koreanika master", "km"),
    PROMEBEL("promebel", "pm");

    private final String name;
    private final String shortName;

    AppType(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public static AppType getByShortName(String shortName) {
        return switch (shortName) {
            case "z" -> AppType.ZETTA;
            case "k" -> AppType.KOREANIKA;
            case "km" -> AppType.KOREANIKAMASTER;
            case "pm" -> AppType.PROMEBEL;
            default -> null;
        };
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return name + "/" + shortName;
    }

}
