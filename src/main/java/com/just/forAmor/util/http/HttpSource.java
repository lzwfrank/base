package com.just.forAmor.util.http;

import static com.trs.common.base.PreConditionCheck.checkArgument;
import static com.trs.common.base.PreConditionCheck.checkNotNull;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Component;

/**
 * 2016年8月29日
 * @author liu
 *
 */
@Component
public class HttpSource {
	
	private static String HEADER_NAME = "User-Agent";
	
	private static String HEADER_VALUE = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
	
	public static enum RequestMethod {
		POST,GET
	}
	
	/**
	 * 初始默认链接属性
	 * @return
	 */
	public RequestConfig buildRequestConfig() {
		
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(5000)       //设置连接超时时间
				.setConnectionRequestTimeout(5000) //设置从connect Manager获取Connection 超时时间，单位毫秒
				.setSocketTimeout(5000)
				.build();
		return config;
	}
	
	public HttpEntity readUrlByhttpClient(HttpClient httpClient, String url, RequestMethod request, 
			BasicHeader basicHeader, StringEntity paramEntity) throws ClientProtocolException, IOException{
		
		return readUrlByhttpClient(httpClient, url, request, basicHeader, paramEntity, null);
	}
	
	
	/**
	 * 构造一个httpClient的请求
	 * @param httpClient 传入httpClient对象
	 * @param url  请求链接
	 * @param request 请求方式,提供get和post两种
	 * @param params 请求参数
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpEntity readUrlByhttpClient(HttpClient httpClient, String url, RequestMethod request, 
			BasicHeader basicHeader, StringEntity paramEntity, Header[] headers) throws ClientProtocolException, IOException{
		HttpEntity result = null;
		HttpResponse response = null;
		switch (request) {
			case GET:
				//初始化get请求
			    HttpGet httpGet = new HttpGet(url);
			    httpGet.setConfig(buildRequestConfig());
			    //设置头信息，进行伪装
			    if (headers == null) {
			    	httpGet.setHeader(HEADER_NAME,HEADER_VALUE);
				}else {
					httpGet.setHeaders(headers);
				}
			    response = httpClient.execute(httpGet);
			    result = response.getEntity();
				break;

			case POST:
				checkNotNull(paramEntity);
				HttpPost httpPost = new HttpPost(url);
				if (headers == null) {
					httpPost.setHeader(HEADER_NAME,HEADER_VALUE);
				}else {
					httpPost.setHeaders(headers);
				}
				httpPost.setConfig(buildRequestConfig());
				httpPost.setEntity(paramEntity);
				response = httpClient.execute(httpPost);
				result = response.getEntity();
				break;
				
			default:
				break;
			}
		checkArgument(result != null, "get nothing by this url" + url);
		return result;
	}
	
}
