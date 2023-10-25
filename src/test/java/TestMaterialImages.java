import ru.koreanika.Common.Material.Material;
import ru.koreanika.project.Project;
import ru.koreanika.project.ProjectHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestMaterialImages {

    public static void main(String[] args) {
        ProjectHandler.init();

        Project.getMaterialsListAvailable();

        String resultOut = "";
        int countLostImages = 0;

        for (Material m : Project.getMaterialsListAvailable()) {
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
