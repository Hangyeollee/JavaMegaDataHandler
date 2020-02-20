
package Functions;
import Model.Info;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import sun.text.normalizer.UCharacter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExcelBuilder {

    private static String[] numColumns = {};


    public void ExcelWriter(List<String> datas, Info info){

        String [] headerColumns = info.getTargetColumns().split(",");
        XSSFWorkbook  workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file


/* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */

        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Employee");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        //headerFont.setBold(boolean);
        //headerFont.setFontHeightInPoints((short) 14);
        //headerFont.setColor(IndexedColors.RED.getIndex());
        headerFont.setBold(true);

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < headerColumns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerColumns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        CellStyle numCellStyle = workbook.createCellStyle();
        //numCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.000"));
        numCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        // Create Other rows and cells with employees data
        int rowNum = 1;

/*   for(Employee employee: employees) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(employee.getName());

            row.createCell(1)
                    .setCellValue(employee.getEmail());

            Cell dateOfBirthCell = row.createCell(2);
            dateOfBirthCell.setCellValue(employee.getDateOfBirth());
            dateOfBirthCell.setCellStyle(dateCellStyle);

            row.createCell(3)
                    .setCellValue(employee.getSalary());
        }*/

        List<String> list = Arrays.asList(numColumns);
        for(String data : datas){
            Row row = sheet.createRow(rowNum++);
            String [] dataArr = data.split("@,");
            for(int i = 0; i <  dataArr.length; i++){
                if(dataArr[i].equals("null")) continue;
                if(list.contains(headerColumns[i])){
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataArr[i]);
                    cell.setCellStyle(numCellStyle);
                }else{
                    row.createCell(i).setCellValue(dataArr[i]);
                }
            }
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < headerColumns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("poi-generated-file.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        // Closing the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
