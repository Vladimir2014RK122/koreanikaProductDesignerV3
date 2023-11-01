package ru.koreanika.catalog;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;

import java.util.*;

public class Catalogs {

    public static List<Material> materialsListAvailable = new ArrayList<>();
    public static List<PlumbingElement> plumbingElementsList = new ArrayList<>();
    public static LinkedHashSet<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
    public static Map<String, Double> materialsDeliveryFromManufacturer = new LinkedHashMap<>();// <Group name, Price in rub>

    public static List<Material> getMaterialsListAvailable() {
        return materialsListAvailable;
    }

    public static List<PlumbingElement> getPlumbingElementsList() {
        return plumbingElementsList;
    }

    public static LinkedHashSet<PlumbingType> getAvailablePlumbingTypes() {
        return availablePlumbingTypes;
    }

    public static Map<String, Double> getMaterialsDeliveryFromManufacturer() {
        return materialsDeliveryFromManufacturer;
    }

}
