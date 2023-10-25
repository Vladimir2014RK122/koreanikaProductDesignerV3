import ru.koreanika.Common.Material.Material;
import ru.koreanika.catalog.Catalogs;
import ru.koreanika.catalog.FacadeXLSParser;
import ru.koreanika.project.ProjectHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Deprecated
public class TestMaterialImages {

    private static final String MATERIALS_XLS_PATH = "materials_1_2004.xls";
    private static final String ANALOGS_XLS_PATH = "material_analogs.xls";

    public static void main(String[] args) {
        try {
            FacadeXLSParser parser = new FacadeXLSParser(MATERIALS_XLS_PATH, ANALOGS_XLS_PATH);
            parser.populateCatalogs(
                    Catalogs.materialsListAvailable,
                    Catalogs.plumbingElementsList,
                    Catalogs.availablePlumbingTypes,
                    Catalogs.materialsDeliveryFromManufacturer
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        String resultOut = "";
        int countLostImages = 0;

        for (Material m : Catalogs.getMaterialsListAvailable()) {
            String name = m.getMainType() + "/" +
                    m.getSubType() + "/" +
                    m.getCollection() + "/" +
                    m.getColor() + "/" +
                    "Текстура" + "/" +
                    m.getColor() + " 200х200.png";

            String imageMaterialPath = "../Камни для калькулятора/Камни для калькулятора/" +
                    m.getMainType() + "/" +
                    m.getSubType() + "/" +
                    m.getCollection() + "/" +
                    m.getColor() + "/" +
                    "Текстура" + "/" +
                    m.getColor() + " 200х200.png";

            File file = new File(imageMaterialPath);
            if (file.exists()) {
//                System.out.println(imageMaterialPath + " - exist");
            } else {
                countLostImages++;
                resultOut += name + " - NO\r\n";
                System.out.println(name + " - NO");
            }
        }

        resultOut += "count = " + countLostImages;
        System.out.println("count = " + countLostImages);

        try {
            FileWriter writer = new FileWriter("../Камни для калькулятора/LostMaterialImages.txt", false);
            writer.write(resultOut);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
