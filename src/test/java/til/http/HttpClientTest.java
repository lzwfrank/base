package til.http;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.just.forAmor.HopelessDream;
import com.just.forAmor.util.http.HttpTemplate;
import com.just.forAmor.util.http.UrlHeaders;
import com.just.forAmor.util.http.UrlParams;

/**
 * 2016年11月1日
 * @author liu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=HopelessDream.class)
public class HttpClientTest {
	
	@Resource
	private HttpTemplate httpTemplate;
	
	@Test
	public void test() {
		UrlParams params = new UrlParams();
		params.setProprety("modelid", "getSearchMenu");
		params.setProprety("typeid", "zyzx");
		params.setProprety("serviceid", "jtcpg");
		params.setProprety("channelName", "jtcpg");
		params.setProprety("typeName", "jt_lyzh");
		params.setProprety("nodeId", "navigation_004001003");
		params.setProprety("menuName", "menuscriptType");
//		params.setProprety("pageSize", "20");
//		params.setProprety("keyword", "{\"keywords\":,\"time\":}");
		UrlHeaders headers = new UrlHeaders();
		headers.setProprety("Content-Type", "application/x-www-form-urlencoded");
		headers.setProprety("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36");
		headers.setProprety("Cookie", "JSESSIONID=0274EA4913494F9D42BB72901065593D; appId=cpj2xarljgytn; navia99e9b26=120.92.13.22:80,7747; 7747=1481472000000");
		headers.setProprety("formdata", "1");
		try {
			String result = httpTemplate.httpPostRequestAsJson("http://192.168.7.168/wcm/bigdata.do", params, headers);
//			HttpEntity entity = httpTemplate.httpGetRequest("http://cq.cqnews.net/html/2016-11/14/content_39442047.htm");
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
