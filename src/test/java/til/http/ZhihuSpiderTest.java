package til.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.just.forAmor.HopelessDream;
import com.just.forAmor.zhihu.ZhihuSpider;

/**
 * 2016年10月18日
 * @author liu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=HopelessDream.class)
public class ZhihuSpiderTest {
	
	@Autowired
	private ZhihuSpider zhihu;

	@Test
	public void test() {
		
		String questionNum = "30502941";//问题编号
		try {
			String result = zhihu.Spider(questionNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("success");
		
		
	}
	

}
