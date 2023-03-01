package etc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
public class ExcelExport {
 
    public  void exportExcel(List<Map<String, Object>> data) {
         
        // Workbook 생성
        Workbook xlsWb = new HSSFWorkbook(); // Excel 2007 이전 버전
        Workbook xlsxWb = new XSSFWorkbook(); // Excel 2007 이상
 
        // *** Sheet-------------------------------------------------
        // Sheet 생성
        Sheet sheet1 = xlsWb.createSheet("excel");
 
        // 컬럼 너비 설정
        sheet1.setColumnWidth(0, 2000);
        sheet1.setColumnWidth(1, 1000);
        sheet1.setColumnWidth(2, 3000);
        sheet1.setColumnWidth(3, 3000);
        sheet1.setColumnWidth(4, 5000);
        sheet1.setColumnWidth(5, 17000);
        sheet1.setColumnWidth(6, 17000);
        sheet1.setColumnWidth(7, 3000);

        // ----------------------------------------------------------
         
        // *** Style--------------------------------------------------
        // Cell 스타일 생성
        CellStyle cellStyle = xlsWb.createCellStyle();
         
        // 줄 바꿈
        cellStyle.setWrapText(true);
         
        // Cell 색깔, 무늬 채우기
        cellStyle.setFillForegroundColor(HSSFColor.LIME.index);
        cellStyle.setFillPattern(CellStyle.BIG_SPOTS);
         
        Row row = null;
        Cell cell = null;
        //----------------------------------------------------------
        
        int dataRowSize = data.size();
        int dataCellSize =  data.get(1).size();
        for(int j = 1 ; j < dataRowSize ; j++){
        	 row = sheet1.createRow(j);
        	 
             cell = row.createCell(0);
             cell.setCellValue((String) data.get(j).get("name"));
                           
             cell = row.createCell(1);
             cell.setCellValue((String) data.get(j).get("gender"));  
             
             cell = row.createCell(2);
             cell.setCellValue((String) data.get(j).get("phoneNo"));  
             
             cell = row.createCell(3);
             cell.setCellValue((String) data.get(j).get("birthDay"));  
             
             cell = row.createCell(4);
             cell.setCellValue((String) data.get(j).get("sumData"));  
             
             cell = row.createCell(5);
             cell.setCellValue((String) data.get(j).get("cicode"));  
             
             cell = row.createCell(6);
             cell.setCellValue((String) data.get(j).get("cicode_compare"));  
             
             cell = row.createCell(7);
             cell.setCellValue((String) data.get(j).get("chk"));  
        }
        
       

        // excel 파일 저장
        try {
            File xlsFile = new File("D:/testExcel.xls");
            FileOutputStream fileOut = new FileOutputStream(xlsFile);
            xlsWb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
    }
 
}