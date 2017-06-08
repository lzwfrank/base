package til.http;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.just.forAmor.HopelessDream;
import com.just.forAmor.helper.ExcelHepler;
import com.just.forAmor.helper.FileHepler;
import com.just.forAmor.luv.LuvLetter;

/**
 * 2016年12月2日
 * @author liu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=HopelessDream.class)
public class StartToExcel {
	
	private static ExcelHepler excelHepler = new ExcelHepler();
	
	private static FileHepler fileUtil = new FileHepler();
	
	@Test
	public void test() {
		String txtPath = "C:\\Users\\liu\\Desktop\\start.lst";
		List<String> readers = fileUtil.readFile(txtPath);
		List<LuvLetter> letters = new ArrayList<>();
		for (String reader : readers) {
			LuvLetter luv = new LuvLetter();
			luv.setAddress(reader.split(";")[0].replace("\"", ""));
		}
	}

}
