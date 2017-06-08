package til.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.just.forAmor.HopelessDream;
import com.just.forAmor.util.http.HttpTemplate;
import com.just.forAmor.util.http.UrlParams;
import com.trs.common.utils.JsonUtils;

/**
 * 2016年9月13日
 * @author liu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=HopelessDream.class)
public class HttpTest {
	
	@Resource
	private HttpTemplate httpTemplate;
	
	
	@Test
	public void test(){
//		try {
//			System.out.println(httpTemplate.httpGetRequestAsJson("https://kyfw.12306.cn/otn/leftTicket/queryT?leftTicketDTO.train_date=2016-10-21&leftTicketDTO.from_station=CUW&leftTicketDTO.to_station=NKW&purpose_codes=ADULT"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		File file = new File("C:\\Users\\liu\\Desktop\\123.jpg");
		String picResult = new String();
		try ( InputStream input = new FileInputStream(file);
				ByteArrayOutputStream output = new ByteArrayOutputStream((int)file.length());){
		      byte[] buf = new byte[1024];
		      int numBytesRead = 0;
		      while ((numBytesRead = input.read(buf)) != -1) {
		    	  output.write(buf, 0, numBytesRead);
		      }
		      byte[] data = output.toByteArray();
		      picResult = new String(data, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		拼装json集
		Map<String, Map<String, Object>> json = new HashMap<>();
		Map<String, Object> doc = new HashMap<>();
		List<Map<String, Object>> pictures = new ArrayList<>();
		Map<String, Object> pic = new HashMap<>();
		pic.put("camerist", "测试");
		pic.put("catagory", "测试");
		pic.put("area", "测试");
		pic.put("createtime", "测试");
		pic.put("picpubtime", "测试");
		pic.put("is_publish", "测试");
		pic.put("content", "测试");
		pic.put("docchannle", "测试");
		pic.put("picture", picResult);
		pictures.add(pic);
		doc.put("doctitle", "测试");
		doc.put("puburl", "测试");
		doc.put("docauthor", "测试");
		doc.put("token", "8e7da16226ca8630640835f3977ec97e");
		doc.put("pictures", pictures);
		json.put("doc", doc);
		String url = "http://localhost:8091/dataAccess/newPictures";
		UrlParams params = new UrlParams();
		String sada = JsonUtils.toJson(json);
//		params.setProprety("json", sada);
		params.setJson(sada);
		HttpEntity result;
		try {
//				String result = util.httpPostRequestAsJson(url, params, hearders);
			result = httpTemplate.httpPostRequestWithJson(url, params);
			InputStream inputSteam = result.getContent();
			String str = httpTemplate.convertStreamToString(inputSteam);
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String compress(byte[] datas){
	    String finalData=null;
	      try{
	    	 String data = new String(datas,"ISO-8859-1");
	         //打开字节输出流
	         ByteArrayOutputStream bout=new ByteArrayOutputStream();
	         //打开压缩用的输出流,压缩后的结果放在bout中
	         GZIPOutputStream gout=new GZIPOutputStream(bout);
	         //写入待压缩的字节数组
	         gout.write(data.getBytes("ISO-8859-1"));
	         //完成压缩写入
	         gout.finish();
	         //关闭输出流
	         gout.close();
	         finalData=bout.toString("ISO-8859-1");
	     }catch(Exception e){
	         e.printStackTrace();
	    }
	    return finalData;
		}
	
}
