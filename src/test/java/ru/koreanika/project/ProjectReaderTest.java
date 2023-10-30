package ru.koreanika.project;

import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

public class ProjectReaderTest {

    private static final String projectPath = "/home/transcend/code/KOREANIKA/koreanikaProductDesignerV3/src/test/resources/fixtures/test4.kproj";

    /**
     * У ProjectReader есть неявная зависимость от каталогов (т.е. от данных, прочитанных из XLS), которые необходимы
     * для разрешения материалов по названию и/или id. По факту же этот функционал относится к инстанцированию
     * объектов типа Material, а не к чтению файла проекта. Т.е. потенциально это задача некоторого менеджера
     * материалов (и уж точно это не должно быть в классе Material, как сейчас).
     */
    @Ignore
    @Test
    public void testProjectReader() {
        ProjectReader reader = new ProjectReader();
        JSONObject projectJSONObject = reader.read(projectPath);
        System.out.println();
    }

}
