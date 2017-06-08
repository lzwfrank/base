package com.just.forAmor.util.http;

import static com.trs.common.base.PreConditionCheck.checkNotNull;
import static com.trs.common.utils.StringUtils.isNullOrEmpty;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.just.forAmor.Template.InitHttpTemplate;
import com.just.forAmor.Template.TemplateStraegy;
import com.just.forAmor.helper.FileHepler;

/**
 * 2016年8月19日
 * @author liu
 *
 */
@Component
public class HttpTemplate extends HttpSource{
	
	private static HttpClient httpClient = new TemplateStraegy<>(new InitHttpTemplate()).getInstance();
	
	
	/**
	 * 主要是基于java自带的urlConnection来实现的
	 * @param url
	 * @param regex
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String readUrl(String url,String regex) throws MalformedURLException,IOException{
		String result = new String();
		checkNotNull(result);
		BufferedReader reader = null;
		try {
			//将url转成url对象
			URL urlRead = new URL(url);
			//初始化链接
			URLConnection connection = urlRead.openConnection();
			//链接超时设置10秒
			connection.setConnectTimeout(10000);
			connection.connect();
			// 初始化 BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			//读取BufferedReader
			result = bufferedRead(reader, regex);
		} finally {
			if (reader != null)
				reader.close();
		}
		return result;
	}
	
//	public static void main(String[] args) {
//		HttpTemplate template = new HttpTemplate();
//		String url = "http://s.cimg.163.com/i/cms-bucket.nosdn.127.net/catchpic/a/ab/ab77ae334abe81b71ee95a476610bb2f.jpg.170x220.auto.jpg";
//		try {
//			template.readUrl(url, "utf-8");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 帮助读取BufferedReader
	 * @param read 需要被读取的BufferedReader
	 * @param regex 每行分割符
	 * @return result
	 */
	private String bufferedRead(BufferedReader read, String regex) throws IOException{
		checkNotNull(read);
		String line = new String();
		String result = new String();
		while((line = read.readLine()) != null) {
			result += isNullOrEmpty(regex)? line: (line+regex);
		}
		return result;
		
	}
	
	
	/**
	 * 模拟get请求,返回json形式的数据
	 * @param httpClient
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String httpGetRequestAsJson(String url) throws ClientProtocolException, IOException{
		
		return httpGetRequestAsJson(url, null, null);
	}
	
	public String httpGetRequestAsJson(String url, UrlParams params) throws ClientProtocolException, IOException{
		
		return httpGetRequestAsJson(url, params, null);
	}
	
	public String httpGetRequestAsJson(String url, UrlParams params, UrlHeaders header) throws ClientProtocolException, IOException{
		String result = new String();
		//获取最终请求链接
		url = (params == null)? url: params.buildGetUrl(url); 
		HttpEntity entity = readUrlByhttpClient(
						httpClient, url, RequestMethod.GET, null, null, 
							header!=null? header.buildHeaders(): null
						);
		//获取结果json
		result = EntityUtils.toString(entity);
		//关闭流,释放资源
		EntityUtils.consume(entity);
		return result;
	}
	
	public String httpPostRequestAsJson(String url, UrlParams params) throws ClientProtocolException, IOException {
		
			return httpPostRequestAsJson(url, params, null);
	}
	
	
	/**
	 * 模拟post请求,返回json形式的数据
	 * @param httpClient
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String httpPostRequestAsJson(String url, UrlParams params, UrlHeaders header) throws ClientProtocolException, IOException{
		String result = new String();
		
		HttpEntity entity = readUrlByhttpClient(
				httpClient, url, RequestMethod.POST, null, params.buildFormEntity(), header != null? header.buildHeaders(): null
				);
//		InputStream in=entity.getContent();
//        String json=IOUtils.toString(in);
//        in.close();
		//获取结果json
		result = EntityUtils.toString(entity);
		//关闭流,释放资源
		EntityUtils.consume(entity);
		return result;
	}
	
	
	
	/**
	 * 模拟post请求,返回未进行处理的httpEntity
	 * @param httpClient
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpEntity httpPostRequest(String url, UrlParams params, UrlHeaders header) throws ClientProtocolException, IOException{
		
		return readUrlByhttpClient(httpClient, url, RequestMethod.POST, null, params.buildFormEntity(), 
				header != null? header.buildHeaders(): null);
	}
	
	/**
	 * 模拟post请求,返回未进行处理的httpEntity
	 * @param httpClient
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpEntity httpPostRequest(String url, UrlParams params) throws ClientProtocolException, IOException{
		
		return readUrlByhttpClient(httpClient, url, RequestMethod.POST, null, params.buildFormEntity());
	}
	
	/**
	 * 模拟post请求,返回未进行处理的httpEntity
	 * @param httpClient
	 * @param url
	 * @param params 传入的是json形式的数据
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpEntity httpPostRequestWithJson(String url, UrlParams params) throws ClientProtocolException, IOException{
		return httpPostRequestWithJson(url, params, null);
	}
	
	/**
	 * 模拟post请求,返回未进行处理的httpEntity
	 * @param httpClient
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpEntity httpPostRequestWithJson(String url, UrlParams params, UrlHeaders header) throws ClientProtocolException, IOException{
//		header.setProprety("connection", "keep-alive");
//		header.setProprety("Charsert", "UTF-8");
//		header.setProprety("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
//		header.setProprety("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
		return readUrlByhttpClient(httpClient, url, RequestMethod.POST, null, params.buildJsonEntity(), 
				header != null? header.buildHeaders(): null);
	}
	
	
	/**
	 * 模拟get请求,返回未进行处理的httpEntity
	 * @param httpClient
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpEntity httpGetRequest(String url) throws ClientProtocolException, IOException{
		
		return readUrlByhttpClient(httpClient, url, RequestMethod.GET, null, null);
	}
	
	public HttpEntity httpGetRequest(String url, UrlHeaders headers) throws ClientProtocolException, IOException{
			
		return readUrlByhttpClient(httpClient, url, RequestMethod.GET, null, null, headers.buildHeaders());
	}
	
	public HttpEntity httpGetRequest(String url, UrlParams params, UrlHeaders header) throws ClientProtocolException, IOException{
		//获取最终请求链接
		url = (params == null)? url: params.buildGetUrl(url); 
		HttpEntity entity = readUrlByhttpClient(
						httpClient, url, RequestMethod.GET, null, null, 
							header!=null? header.buildHeaders(): null
						);
		return entity;
	}
	
	
	/**
	 * 使用httpClient来进行请求
	 * @param url
	 * @param savePath
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public void downloadPic(String url, String savePath)  throws IOException,ClientProtocolException{
	    FileHepler fileUtil = new FileHepler();
	    HttpEntity entity = null;
		try {
			entity = httpGetRequest(url);
	        FileOutputStream outputStream = new FileOutputStream(fileUtil.createOrClearFile(savePath));
		    byte[] bytes = new byte[1024];
		    int i = 0 ;
		    while((i = entity.getContent().read(bytes)) != -1) {
		    	outputStream.write(bytes, 0, i);
		    }
		    outputStream.flush();
		    outputStream.close();
		} finally {
			if (entity != null) 
			   entity.getContent().close();
			EntityUtils.consume(entity);
		}
	}
	
	public static void main(String[] args) {
		try {
			HttpTemplate template = new HttpTemplate();
			template.downloadPic("http://ycrb.ycen.com.cn/epaper/ycwb/res/2017-05/16/20/ycwbp59_b.jpg", "D:\\123.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String convertStreamToString(InputStream is) {   

	   	BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
        StringBuilder sb = new StringBuilder();   
        String line = null;   
        try {   
            while ((line = reader.readLine()) != null) {   
                sb.append(line + "/n");   
            }   
        } catch (IOException e) {   
            e.printStackTrace();   
        } 
        return sb.toString();   
    } 
	

}
