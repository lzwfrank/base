package com.just.forAmor.Template;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import lombok.extern.slf4j.Slf4j;

/**
 * 2016年8月26日
 * @author liu
 *
 */
@Slf4j
public class InitHttpTemplate implements TemplateSource<HttpClient>{

	private static CloseableHttpClient httpClient = null;
	
	 /** 
     * 连接池管理对象 
     */  
    private static PoolingHttpClientConnectionManager cm = null;  
    
    /** 
     * 最大连接数400 
     */  
    private static int MAX_CONNECTION_NUM = 400;  
  
  
    /** 
     * 单路由最大连接数80 
     */  
    private static int MAX_PER_ROUTE = 80;  
  
  
    /** 
     * 向服务端请求超时时间设置(单位:毫秒) 
     */  
    private static int SERVER_REQUEST_TIME_OUT = 2000;  
  
  
	private static synchronized void syncInitRestTemplate() {
		if (httpClient == null) {
			init();
			//创建一个HttpClient,怀疑是使用全局cookies，会读取已存的cookies
			RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();//标准Cookie策略
			httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		}
	}
	
	private static void init() {  
        try {  
           SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,  
                           new TrustSelfSignedStrategy())  
                   .build();  
           HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;  
           SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
                   sslcontext,hostnameVerifier);  
           Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
                   .register("http", PlainConnectionSocketFactory.getSocketFactory())  
                   .register("https", sslsf)  
                   .build();  
           cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
           // Increase max total connection to 200  
           cm.setMaxTotal(MAX_CONNECTION_NUM);  
           // Increase default max connection per route to 20  
           cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);  
           SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SERVER_REQUEST_TIME_OUT).build();  
           cm.setDefaultSocketConfig(socketConfig);  
       } catch (Exception e) {  
           log.error("InterfacePhpUtilManager init Exception"+e.toString());  
       }  
   }  
	
	
	@Override
	public HttpClient getInstance() {
		if (httpClient == null) {
			syncInitRestTemplate();
		}
		return httpClient;
	}

}
