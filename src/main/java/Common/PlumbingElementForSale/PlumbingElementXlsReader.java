package Common.PlumbingElementForSale;

import Exceptions.ParseXLSFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

public class PlumbingElementXlsReader {

    private static final String SHEET_NAME = "ExternalElement";

    private static final int CELL_ID = 0;
    private static final int CELL_TYPE = 1;
    private static final int CELL_AVAILABLE = 2;
    private static final int CELL_NAME = 3;
    private static final int CELL_MODELS = 4;
    private static final int CELL_SIZES = 5;
    private static final int CELL_PRICES = 6;
    private static final int CELL_CURRENCY = 7;

    public static ArrayList<PlumbingElement> fillDataFromXls(HSSFWorkbook workBook) throws ParseXLSFileException{

        ArrayList<PlumbingElement> elementsList = new ArrayList<>();
        Sheet sheet = workBook.getSheet(SHEET_NAME);
        Iterator<Row> it = sheet.iterator();
        it.next();

        if(sheet == null) return elementsList;

        while (it.hasNext()) {
            int cell = 0;
            Row row = it.next();

            int id;
            PlumbingType type;
            boolean available;
            String name;
            List<String> models;
            List<String> sizes;
            String currency;
            List<Double> prices = new ArrayList<>();

            if(row.getCell(CELL_ID).getCellType() != CellType.NUMERIC)break;

            if(row.getCell(CELL_ID).getCellType() != CellType.NUMERIC) throw new ParseXLSFileException("ParseXLSFileException ID != NUMERIC, Row = " + row.getRowNum());


            id = (int)row.getCell(CELL_ID).getNumericCellValue();
            type = PlumbingType.getByNumber((int) row.getCell(CELL_TYPE).getNumericCellValue());
            available = ((int)row.getCell(CELL_AVAILABLE).getNumericCellValue() == 0)? false:true;
            name = row.getCell(CELL_NAME).getStringCellValue();
            models = Arrays.asList(row.getCell(CELL_MODELS).getStringCellValue().split(","));
            sizes = Arrays.asList(row.getCell(CELL_SIZES).getStringCellValue().split(","));

            List<String> pricesStr = Arrays.asList(row.getCell(CELL_PRICES).getStringCellValue().split(","));
            pricesStr.forEach(e->{
                prices.add(Double.parseDouble(e));
            });

            currency = row.getCell(CELL_CURRENCY).getStringCellValue().toUpperCase();

            elementsList.add(new PlumbingElement(id, type, available, name, models, sizes, currency, prices));
        }

        return elementsList;
    }

}
