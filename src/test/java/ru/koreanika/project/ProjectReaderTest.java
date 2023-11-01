package ru.koreanika.project;

import org.json.simple.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ProjectReaderTest {

    @Test
    public void testZipProject() {
        String projectPath = getClass().getResource("/fixtures/zip-non-encoded-1.kproj").getPath();

        ProjectReader reader = new ProjectReader();
        JSONObject projectJSONObject = reader.read(projectPath);

        assertNotNull(projectJSONObject);

        System.out.println(projectJSONObject);
        System.out.println();
    }

    @Test
    public void testEncodedZipProject() {
        String projectPath = getClass().getResource("/fixtures/zip-encoded-1.kproj").getPath();

        ProjectReader reader = new ProjectReader();
        JSONObject projectJSONObject = reader.read(projectPath);

        assertNotNull(projectJSONObject);

        System.out.println(projectJSONObject);
        System.out.println();
    }

}
