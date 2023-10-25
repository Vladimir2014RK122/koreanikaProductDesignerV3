package ru.koreanika.catalog;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FacadeXLSParser {

    public static final String CURRENT_USER_PASSWORD = "9031713970";

    private final String materialsXLSPath;
    private final String analogsXLSPath;

    public FacadeXLSParser(String materialsXLSPath, String analogsXLSPath) {
        this.materialsXLSPath = materialsXLSPath;
        this.analogsXLSPath = analogsXLSPath;

        Biff8EncryptionKey.setCurrentUserPassword(CURRENT_USER_PASSWORD);
    }

    public void populateCatalogs(List<Material> materialsListAvailable, List<PlumbingElement> plumbingElementsList,
                                 Set<PlumbingType> availablePlumbingTypes,
                                 Map<String, Double> materialsDeliveryFromManufacturer) throws IOException {
        MaterialsXLSParser materialsXLSParser = new MaterialsXLSParser(materialsXLSPath);
        materialsXLSParser.populateCatalogs(materialsListAvailable, plumbingElementsList, availablePlumbingTypes,
                materialsDeliveryFromManufacturer);

        AnalogsXLSParser analogsXLSParser = new AnalogsXLSParser(analogsXLSPath);
        analogsXLSParser.populateCatalogs(materialsListAvailable);
    }
}
