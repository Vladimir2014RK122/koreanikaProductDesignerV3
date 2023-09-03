package PortalClient.Authorization;

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
        if (shortName.equals("z")) return AppType.ZETTA;
        else if (shortName.equals("k")) return AppType.KOREANIKA;
        else if (shortName.equals("km")) return AppType.KOREANIKAMASTER;
        else if (shortName.equals("pm")) return AppType.PROMEBEL;
        return null;
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
