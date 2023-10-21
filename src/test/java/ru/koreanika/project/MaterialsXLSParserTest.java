package ru.koreanika.project;

import org.junit.Ignore;
import org.junit.Test;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.Material.MaterialImage;
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

        try {
            MaterialsXLSParser parser = new MaterialsXLSParser(MATERIALS_LIST_PATH, ANALOGS_LIST_PATH);
            parser.fillMaterialsList(materialsListAvailable, plumbingElementsList, availablePlumbingTypes,
                    materialsDeliveryFromManufacture);
            System.out.println(materialsListAvailable.size());

            System.out.println("id,path");
            for (Material material : materialsListAvailable) {
                MaterialImage materialImage = material.getMaterialImage();
                System.out.println(material.getId() + ",\"" + materialImage.getImageParentPath() + "\"");
            }

        } catch (ParseXLSFileException e) {
            e.printStackTrace();
        }
    }

}
