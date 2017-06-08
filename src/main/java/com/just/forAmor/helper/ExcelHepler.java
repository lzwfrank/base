package com.just.forAmor.helper;

import static com.trs.common.utils.StringUtils.isNullOrEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.just.forAmor.util.http.HttpTemplate;
import com.trs.common.utils.StringUtils;

/**
 * 2016年8月17日
 * @author liu
 *
 */
public class ExcelHepler {
	
	private static FileHepler fileUtil = new FileHepler();
	
	
	/**
	 * 
	 * @param excelPath excel路径
	 * @param txtPath txt路径
	 * @param navigation 需要生成的excel的行信息
	 * @param regex txt中每行数据的分割符
	 */
	public void txtToExcel(String excelPath, String txtPath, List<String> navigation, String regex) {
			List<String> reader = fileUtil.readFile(txtPath);
			exportExcel(excelPath, reader, navigation, regex);
    }
	
	public void exportExcel(String excelPath,List<String> reader,  List<String> navigation, String regex) {
		HttpTemplate httpTemplate = new HttpTemplate();
		HSSFWorkbook workbook = new HSSFWorkbook();
		//设置数字格式
		HSSFCellStyle cellStyle = workbook.createCellStyle(); 
		HSSFDataFormat format = workbook.createDataFormat(); 
		cellStyle.setDataFormat(format.getFormat("#,##0.00")); 
		HSSFCellStyle timeStyle = workbook.createCellStyle(); 
		HSSFDataFormat formatTime = workbook.createDataFormat(); 
		timeStyle.setDataFormat(formatTime.getFormat("#,##0")); 
		//创建sheet页
		HSSFSheet sheet = workbook.createSheet("成拓大数据");
		HSSFRow row = sheet.createRow(0);
		int size = navigation.size();
		//生成头
		for (int i = 0 ; i < size; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(navigation.get(i));
		}
		try {
			int i = 1;
			for(String read: reader){
				 row = sheet.createRow(i);
				 if(read.split(regex).length > 1) {
					 String[] data = read.split(regex);
					 String zyzxfield = new String();
					 String docputime = new String();
					 for (int j = 0; j < data.length; j++) {
						 if (new StringHepler().isFind(data[j], "zyzxfield")) 
							 zyzxfield = data[j];
						 if (new StringHepler().isFind(data[j], "\\d{4}(\\/)\\d{1,2}\\1\\d{1,2}")) 
							 docputime = data[j];
						 HSSFCell cell = row.createCell(j);
						 if (!isNullOrEmpty(data[j])  && 
								 StringHepler.isFind(data[j], "(^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$)|(^[0-9]*$)")) {
							cell.setCellStyle(cellStyle);
							cell.setCellValue(!isNullOrEmpty(data[j])? Double.valueOf(data[j]): 0);
						}else {
							row.createCell(j).setCellValue(data[j]);
						}
					 }
						//处理时间
						 List<String> times = new ArrayList<>();
						 String[] dates = docputime.split(" ");
						 String[] date = dates[0].split("/");
						 String[] time = dates[1].split(":");
						 for (String da : date) {
							 times.add(da);
						}
						for (String ti : time) {
							 times.add(ti);
						}
						for (int t = data.length; t < (data.length + 6); t++) {
							 HSSFCell timeCell = row.createCell(t);
							 timeCell.setCellStyle(timeStyle);
							 timeCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							 timeCell.setCellValue(Double.valueOf(times.get(t - data.length)));
						 }
						if (!StringUtils.isNullOrEmpty(zyzxfield)) {
							 List<String> parentId = new ArrayList<>();
							 String[] strings = zyzxfield.split(";");
							 for (String str : strings) {
								if (str.length() < 14) {
									parentId.add(str.replace("zyzxfield_", ""));
								}
							}
						 for (int t = data.length+6; t < (data.length+6 + parentId.size()); t++) {
							 HSSFCell zyzxCell = row.createCell(t);
							 zyzxCell.setCellStyle(timeStyle);
							 zyzxCell.setCellValue(Double.valueOf(parentId.get(t - data.length-6)));
						 }
					 }
					}
		         i++;
			}
	        File file = fileUtil.createOrClearFile(excelPath);
	        FileOutputStream out = new FileOutputStream(file);
	        try {
		        workbook.write(out);
			} catch (Exception e) {
				
			}finally {
				out.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
     * 导入excel
     */
    public List<String> readXls(String path) throws IOException {
    	List<String> result = new ArrayList<>();
    	
       SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
       dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
       dataSource.setUsername("root");
       dataSource.setUrl("jdbc:mysql://192.168.7.157:3306/new_mlf");
       dataSource.setPassword("trsadmin");
       JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	//只能读取xls
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        
        int t = 0;
       // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
           HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
           if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
           for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
               HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    HSSFCell website = hssfRow.getCell(0);
                    HSSFCell channel = hssfRow.getCell(1);
                    HSSFCell address = hssfRow.getCell(2);
                   //需要自己定制
                    String sql = String.format("select * from website w where w.website = '%s' and w.channel = '%s'", 
                    		website.getStringCellValue(), channel.getStringCellValue());
                    List<Map<String,Object>> count = jdbcTemplate.queryForList(sql);
                    StringBuilder sb = new StringBuilder();
                    if (count.size() < 1) {
                    	sb.append(website).append("&-&")
                    	  .append(channel).append("&-&")
                    	  .append(address).append("&-&")
                    	  .append("sourcelevel_001").append("&-&")
                    	  .append("ranknature_001").append("&-&")
                    	  .append("").append("&-&")
                    	  .append("中国\\重庆").append("&-&");
                    	result.add(sb.toString());
                    }
                }
           }
        }
        return result;
	}

}
