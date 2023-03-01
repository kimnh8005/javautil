package etc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.encryption.CinoHash;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class TestExcel3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TestExcel3 sample = new TestExcel3();

		try {
			sample.excelRead(new File("C:/Users/admin/Desktop/test.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void excelRead(File file) throws IOException {

		CinoHash ch = new CinoHash();
		ExcelExport ex = new ExcelExport();
		// map의 key정의
		String[] keys = { "name", "gender", "phoneNo", "birthDay", "sumData",
				"cicode", "cicode_compare", "chk" };

		Workbook w = null;
		try {
			w = Workbook.getWorkbook(file);
			// 첫번째 엑셀 시트를 가져옴.
			Sheet sheet = w.getSheet(0);

			// 읽어들인 데이터를 저장
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

			// 한 행을 저장
			Map<String, Object> map = null;

			// 시트의 행 사이즈 만큼 읽어옴.
			for (int j = 0; j < sheet.getRows(); j++) {

				map = new HashMap<String, Object>();

				// 열 사이즈 만큼 읽어옴.
				for (int i = 0; i < sheet.getColumns(); i++) {

					// 한셀씩 읽어온다
					Cell cell = sheet.getCell(i, j);

					map.put(keys[i], cell.getContents());

					if ((String) map.get("sumData") == map.get(keys[i])) {
						String SumData = (String) map.get("name")
								+ map.get("gender") + map.get("phoneNo")
								+ map.get("birthDay");
						map.put(keys[i], SumData);
						// ex.exportExcel(SumData);
					}

					if ((String) map.get("cicode_compare") == map.get(keys[i])) {
						String SumDataTemp = (String) map.get("name")
								+ map.get("gender") + map.get("phoneNo")
								+ map.get("birthDay");

						int defauleSize = 40;
						int sumDataTempSize = SumDataTemp.length();
						int emptySpaceSize = 0;
					

						if (sumDataTempSize != defauleSize) {
							emptySpaceSize = defauleSize - sumDataTempSize;
							for (int t = 0; t < emptySpaceSize; t++) {
								SumDataTemp = SumDataTemp.concat(" ");
							}
							// System.out.println("222222222222"+SumDataTemp.length());
							// System.out.println("================="+SumDataTemp);
							String CICode = ch.codeConversion(SumDataTemp);
							map.put(keys[i], CICode);

						} else {
							String CICode = ch.codeConversion(SumDataTemp);
							map.put(keys[i], CICode);
						}
					}

					if ((String) map.get("chk") == map.get(keys[i])) {
						String CI = (String) map.get("cicode");
						String CI2 = (String) map.get("cicode_compare");
						if (CI.equals(CI2) == true) {
							map.put(keys[i], "");
						} else {
							map.put(keys[i], "수정하세요");
						}
					}
				}
				data.add(map);
			}

			ex.exportExcel(data);

			// 읽어 들인 데이터 출력
			for (Map<String, Object> temp : data) {

				// ex.exportExcel();
				System.out.println("temp : " + temp);
			}
		} catch (BiffException e) {
			e.printStackTrace();
		}
	}
}