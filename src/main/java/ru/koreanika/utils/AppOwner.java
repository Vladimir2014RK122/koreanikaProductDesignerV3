package ru.koreanika.utils;

public enum AppOwner {

    ZETTA("zetta"),
    KOREANIKA("koreanika"),
    KOREANIKAMASTER("koreanika master"),
    PROMEBEL("promebel");

    private String name;

    AppOwner(String name) {
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
