package ru.koreanika.project;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.koreanika.Common.Material.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProjectReaderTest {

    private static final String projectPath = "/home/transcend/code/KOREANIKA/koreanikaProductDesignerV3/src/test/resources/fixtures/test4.kproj";

    private List<Material> dummyMaterialsCatalog;

    @Before
    public void setUp() {
        dummyMaterialsCatalog = Arrays.asList(
          new Material("id1", "mainType", "subType", "collection", "white", 100.0, 20.0, null, Collections.emptyList()),
          new Material("id2", "mainType", "subType", "collection", "white", 100.0, 20.0, null, Collections.emptyList()),
          new Material("id3", "mainType", "subType", "collection", "white", 100.0, 20.0, null, Collections.emptyList())
        );
    }

    @After
    public void tearDown() {
        dummyMaterialsCatalog = null;
    }

    /**
     * Внешние зависимости в ProjectReader, которые нужно устранить:
     *   - MainWindow.cutDesigner
     *   - MainWindow.tableDesigner
     *   - Main.mainCoefficient
     *   - Main.materialCoefficient
     *   - Project (static), side-effects!!!
     *   - javafx.scene.image.Image
     */
    @Ignore
    @Test
    public void testProjectReader() {
        ProjectReader reader = new ProjectReader(dummyMaterialsCatalog);
        JSONObject projectJSONObject = reader.read(projectPath);
        System.out.println();
    }

}
