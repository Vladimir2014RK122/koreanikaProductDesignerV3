package ru.koreanika.catalog;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.koreanika.Common.Material.Material;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnalogsXLSParser {

    private final String analogsXLSPath;

    public AnalogsXLSParser(String analogsXLSPath) {
        this.analogsXLSPath = analogsXLSPath;
    }

    void populateCatalogs(List<Material> materialsListAvailable) throws IOException {
        try (InputStream inputStream = new FileInputStream(analogsXLSPath)) {
            try (HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
                fillAcrylicStoneAnalogs(materialsListAvailable, workbook);
                fillQuartzStoneAnalogs(materialsListAvailable, workbook);
            }
        }
    }

    private void fillAcrylicStoneAnalogs(List<Material> materialsListAvailable, HSSFWorkbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        iterator.next();

        while (iterator.hasNext()) {
            Row row = iterator.next();

            ArrayList<String> localListAnalogs = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                if (row.getCell(i * 5 + 1) == null || row.getCell(i * 5 + 2) == null || row.getCell(i * 5 + 3) == null || row.getCell(i * 5 + 4) == null) {
                    continue;
                }
                String materialName = row.getCell(i * 5 + 1).getStringCellValue() + "$" + row.getCell(i * 5 + 2).getStringCellValue() + "$" +
                        row.getCell(i * 5 + 3).getStringCellValue() + "$" + row.getCell(i * 5 + 4).getStringCellValue() + "$";
                localListAnalogs.add(materialName);
            }

            //add Analogs to materials instances:
            for (String analogName : localListAnalogs) {
                for (Material m : materialsListAvailable) {
                    if (m.getName().contains(analogName)) {
                        //add analogs to material:
                        m.getAnalogsList().clear();
                        for (String analogNameForAdd : localListAnalogs) {
                            //getMaterial by name:
                            for (Material mForAdd : materialsListAvailable) {
                                if (mForAdd.getName().contains(analogNameForAdd)) {
                                    //add analog material:
                                    m.getAnalogsList().add(mForAdd);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void fillQuartzStoneAnalogs(List<Material> materialsListAvailable, HSSFWorkbook workbook) {
        Sheet sheet = workbook.getSheetAt(1);
        Iterator<Row> it = sheet.iterator();
        it.next();

        while (it.hasNext()) {
            Row row = it.next();

            ArrayList<String> localListAnalogs = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                if (row.getCell(i * 5 + 1) == null || row.getCell(i * 5 + 2) == null || row.getCell(i * 5 + 3) == null || row.getCell(i * 5 + 4) == null) {
                    continue;
                }
                String materialName = row.getCell(i * 5 + 1).getStringCellValue() + "$" + row.getCell(i * 5 + 2).getStringCellValue() + "$" +
                        row.getCell(i * 5 + 3).getStringCellValue() + "$" + row.getCell(i * 5 + 4).getStringCellValue() + "$";
                localListAnalogs.add(materialName);
            }

            // add Analogs to materials instances:
            for (String analogName : localListAnalogs) {
                for (Material m : materialsListAvailable) {
                    if (m.getName().contains(analogName)) {
                        //add analogs to material:
                        m.getAnalogsList().clear();
                        for (String analogNameForAdd : localListAnalogs) {
                            //getMaterial by name:
                            for (Material mForAdd : materialsListAvailable) {
                                if (mForAdd.getName().contains(analogNameForAdd)) {
                                    //add analog material:
                                    m.getAnalogsList().add(mForAdd);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}
