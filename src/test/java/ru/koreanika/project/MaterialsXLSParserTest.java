package ru.koreanika.project;

import org.junit.Ignore;
import org.junit.Test;
import ru.koreanika.Common.Material.ImageIndex;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;
import ru.koreanika.service.ServiceLocator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MaterialsXLSParserTest {

    public static final String MATERIALS_XLS_PATH = "materials_1_2004.xls";
    public static final String ANALOGS_XLS_PATH = "material_analogs.xls";

    @Test
    @Ignore
    public void readAll() {
        List<Material> materialsListAvailable = new ArrayList<>();
        List<PlumbingElement> plumbingElementsList = new ArrayList<>();
        Set<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
        Map<String, Double> materialsDeliveryFromManufacture = new HashMap<>();

        FacadeXLSParser parser = new FacadeXLSParser(MATERIALS_XLS_PATH, ANALOGS_XLS_PATH);
        parser.populateLists(materialsListAvailable, plumbingElementsList, availablePlumbingTypes, materialsDeliveryFromManufacture);
        System.out.println(materialsListAvailable.size());
    }

    @Ignore
    @Test
    public void matchMaterialsWithImagesFromImageIndex() throws IOException {
        List<Material> materialsListAvailable = new ArrayList<>();
        List<PlumbingElement> plumbingElementsList = new ArrayList<>();
        Set<PlumbingType> availablePlumbingTypes = new LinkedHashSet<>();
        Map<String, Double> materialsDeliveryFromManufacturer = new HashMap<>();

        MaterialsXLSParser materialsXLSParser = new MaterialsXLSParser(MATERIALS_XLS_PATH);
        materialsXLSParser.populateLists(materialsListAvailable, plumbingElementsList, availablePlumbingTypes,
                materialsDeliveryFromManufacturer);

        ImageIndex imageIndex = ServiceLocator.getService("ImageIndex", ImageIndex.class);

        try (PrintWriter writer = new PrintWriter("materials_without_image.csv", "CP1251")) {
            writer.write("id;name\r\n");
            for (Material material : materialsListAvailable) {
                String id = material.getId();
                if (!imageIndex.containsKey(id)) {
                    writer.write(id + ";\"" + material.getName() + "\"\r\n");
                }
            }
        }
    }

}
