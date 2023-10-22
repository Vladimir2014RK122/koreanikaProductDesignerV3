package ru.koreanika.project;

import org.junit.Ignore;
import org.junit.Test;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;

import java.util.*;

public class MaterialsXLSParserTest {

    public static final String MATERIALS_LIST_PATH = "/home/transcend/code/KOREANIKA/DATA/materials_1_2004.xls";
    public static final String ANALOGS_LIST_PATH = "material_analogs.xls";

    @Test
    @Ignore
    public void readAll() {
        List<Material> materialsListAvailable = new ArrayList<>();
        List<PlumbingElement> plumbingElementsList = new ArrayList<>();
        Set<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
        Map<String, Double> materialsDeliveryFromManufacture = new HashMap<>();

        FacadeXLSParser parser = new FacadeXLSParser(MATERIALS_LIST_PATH, ANALOGS_LIST_PATH);
        parser.populateLists(materialsListAvailable, plumbingElementsList, availablePlumbingTypes, materialsDeliveryFromManufacture);
        System.out.println(materialsListAvailable.size());
    }

}
