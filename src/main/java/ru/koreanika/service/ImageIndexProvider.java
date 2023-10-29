package ru.koreanika.service;

import ru.koreanika.Common.Material.ImageIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ImageIndexProvider implements Provider<ImageIndex> {

    public static final String INDEX_ZIP_FILE = "materials_resources/image_index.csv.zip";

    @Override
    public ImageIndex get() {
        try (ZipFile zipFile = new ZipFile(INDEX_ZIP_FILE, StandardCharsets.UTF_8)) {
            ImageIndex index = new ImageIndex();
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] chunks = line.split(",");
                            String key = chunks[0];
                            String value = chunks[1].replace("\"", "") + "/" + chunks[2].replace("\"", "");
                            if (!index.containsKey(key)) {
                                index.put(key, new ArrayList<>());
                            }
                            index.get(key).add(value);
                        }
                        inputStreamReader.close();
                        bufferedReader.close();
                    }
                }
            }
            return index;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
