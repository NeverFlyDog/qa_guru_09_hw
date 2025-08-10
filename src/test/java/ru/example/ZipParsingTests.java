package ru.example;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipParsingTests {
    private static final String filename = "archive.zip";
    private final ClassLoader classLoader = getClass().getClassLoader();

    @DisplayName("Проверка содержимого pdf-файла из zip-архива")
    @Test
    void pdfParsingTest() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(classLoader.getResourceAsStream(filename))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    PDF pdf = new PDF(zis);
                    Assertions.assertEquals("NeverFlyDog", pdf.author);
                    Assertions.assertEquals(5, pdf.numberOfPages);
                    Assertions.assertTrue(pdf.text.contains("Lorem ipsum dolor sit amet"));

                    break;
                }
            }
        }
    }

    @DisplayName("Проверка содержимого xlsx-файла из zip-архива")
    @Test
    void xlsxParsingTest() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(classLoader.getResourceAsStream(filename))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xlsx")) {
                    XLS xlsx = new XLS(zis);
                    Assertions.assertEquals(3, xlsx.excel.getNumberOfSheets());

                    Sheet sheet1 = xlsx.excel.getSheetAt(0);
                    Assertions.assertEquals("Example Test", sheet1.getSheetName());
                    Assertions.assertEquals(6, sheet1.getLastRowNum());

                    Sheet sheet2 = xlsx.excel.getSheetAt(1);
                    Assertions.assertEquals("Format Abbr.", sheet2.getSheetName());
                    Assertions.assertEquals(
                            "Question Format Abbreviations",
                            sheet2.getRow(5).getCell(0).getStringCellValue()
                    );

                    Sheet sheet3 = xlsx.excel.getSheetAt(2);
                    Assertions.assertEquals("Readme", sheet3.getSheetName());

                    break;
                }
            }
        }
    }

    @DisplayName("Проверка содержимого csv-файла из zip-архива")
    @Test
    void csvParsingTest() throws IOException, CsvException {
        try (ZipInputStream zis = new ZipInputStream(classLoader.getResourceAsStream(filename))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis))) {
                        List<String[]> data = csvReader.readAll();

                        Assertions.assertEquals(101, data.size());
                        Assertions.assertArrayEquals(
                                new String[] {
                                        "Index", "Name", "Description", "Brand", "Category", "Price", "Currency",
                                        "Stock", "EAN", "Color", "Size", "Availability", "Internal ID"
                                },
                                data.getFirst()
                        );
                    }
                    break;
                }
            }
        }
    }
}
