package utils.MainSettings;

public enum MenuItem {

    USER_SETTINGS("Пользовательские настройки"),
    ENCODE_FILE("Раскодировать файл");

    String name;

    MenuItem(String name){
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
