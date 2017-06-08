package til.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.just.forAmor.HopelessDream;
import com.just.forAmor.dataExport.ExportHybaseManager;
import com.just.forAmor.dataExport.ExportHybaseManager.Model;

/**
 * 2016年11月4日
 * @author liu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=HopelessDream.class)
public class HybaseExportTest {
	
	@Autowired
	private ExportHybaseManager exportDataToExcel;
	
	@Test
	public void test(){
		String result;
		try {
			exportDataToExcel.exportTest();
//			System.out.println(result);
//			exportDataToExcel.simpleExportMenu(Model.rb);
//			exportDataToExcel.simpleExportMenu(Model.hlw);
//			exportDataToExcel.simpleExportMenu(Model.wb);
//			exportDataToExcel.simpleExportMenu(Model.sb);
//			exportDataToExcel.simpleExportMenu(Model.cb);
			System.out.println("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
