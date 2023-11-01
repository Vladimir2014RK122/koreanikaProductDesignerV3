package ru.koreanika.project;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * Поддерживаются три формата файла проекта
 * - plain-text JSON
 * - зазипованный JSON
 * - зазипованный JSON cо сдвигом байтов (~закодированный)
 */
public class ProjectReader {

    private final Deque<String> errorMessages = new ArrayDeque<>();

    public ProjectReader() {
    }

    private static boolean checkZipFile(File projectFile) {
        boolean result;
        try (ZipFile zipFile = new ZipFile(projectFile, StandardCharsets.UTF_8)) {
            zipFile.getName();
            result = true;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private static boolean checkOldProject(InputStream in) {
        boolean isOldProject;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            JSONParser jsonParser = new JSONParser();
            JSONObject parsedProject = (JSONObject) jsonParser.parse(bufferedReader);
            isOldProject = true;
        } catch (ParseException | IOException e) {
            isOldProject = false;
        }
        return isOldProject;
    }

    private static String applyNameSubstitutions(String project) {
        System.out.println("BEFORE NAME SUBSTITUTION: " + project);
        String res = project.replace("Массив$", "Массив_шпон$");
        System.out.println("AFTER NAME SUBSTITUTION:  " + res);
        return res;
    }

    JSONObject read(String projectPath) {
        errorMessages.clear();

        JSONObject parsedProject;
        try {
            boolean isOldProject = checkOldProject(new FileInputStream(projectPath));
            boolean isZipProject = checkZipFile(new File(projectPath));

            FileInputStream in = new FileInputStream(projectPath);
            if (isOldProject) {
                System.out.println("Open project: " + projectPath + " (OLD TEXT type)");
                parsedProject = readOldProject(in);
            } else if (isZipProject) {
                System.out.println("Open project: " + projectPath + " (ZIP type)");
                parsedProject = readZipProject(in);
            } else {
                System.out.println("Open project: " + projectPath + " (ZIP ENCRYPTED type)");
                parsedProject = readZipProject(new CustomDecryptInputStream(in));
            }
        } catch (FileNotFoundException e) {
            errorMessages.push("Файл не найден");
            return null;
        } catch (IOException e) {
            errorMessages.push("Ошибка контента");
            return null;
        } catch (ParseException e) {
            errorMessages.push("Поврежден mainInfo файл");
            return null;
        }

        return parsedProject;
    }

    public String getLastErrorMessage() {
        return errorMessages.peek();
    }

    private JSONObject readOldProject(InputStream in) throws ParseException, IOException {
        return parseProjectData(in, true);
    }

    /**
     * TODO this method has a side-effect (writes to Project)!!! Eliminate it.
     * <p>
     * Note that an encrypted file will not be detected as ZIP.
     */
    private JSONObject readZipProject(InputStream in) throws IOException, ParseException {
        JSONObject parsedProject = null;
        try (ZipInputStream zis = new ZipInputStream(in, StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals("mainInfo.json")) {
                    parsedProject = parseProjectData(zis, false);
                } else if (zipEntry.getName().equals("receiptManagerSketchImage.png")) {
                    Project.setReceiptManagerSketchImage(new Image(zis));
                }
            }
        }
        return parsedProject;
    }

    private JSONObject parseProjectData(InputStream in, boolean close) throws ParseException, IOException {
        JSONParser jsonParser = new JSONParser();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        JSONObject parsedProject = (JSONObject) jsonParser.parse(bufferedReader);
        String s = applyNameSubstitutions(parsedProject.toString());
        parsedProject = (JSONObject) jsonParser.parse(s);

        if (close) {
            bufferedReader.close();
        }
        return parsedProject;
    }

    private static class CustomDecryptInputStream extends FilterInputStream {

        public static final int CUSTOM_BYTE_OFFSET = 76;

        public CustomDecryptInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read(byte[] b) throws IOException {
            int n = super.read(b);
            for (int i = 0; i < b.length; i++) {
                b[i] -= CUSTOM_BYTE_OFFSET;
            }
            return n;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = super.read(b, off, len);
            for (int i = off; i < len; i++) {
                b[off + i] -= CUSTOM_BYTE_OFFSET;
            }
            return n;
        }
    }

}
