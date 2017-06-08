package com.just.forAmor.util.http;

import static com.trs.common.base.PreConditionCheck.checkNotNull;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.just.forAmor.helper.StringHepler;

import lombok.Data;

/**
 * 2016年8月31日
 * @author liu
 *
 */
public class UrlParams implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1878640544538082722L;
	
	
	private static Map<String, Object> map = new HashMap<String, Object>();
	
	private static String json = null;
	
	public String getJson() {
		return json;
	}
	
	public void setJson(String json) {
		this.json = json;
	}
	
	public Map<String, Object> setProprety(String key,Object value) {
		map.put(key, value);
		return map;
	}
	
	public String getPropretyAsString(String key) {
		return new StringHepler().toString(map.get(key));
	}
	
	public Object getProprety(String key) {
		return map.get(key);
	}
	
	/**
	 * 将参数转化成httpClient的表单参数
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public UrlEncodedFormEntity buildFormEntity() throws UnsupportedEncodingException {
		UrlEncodedFormEntity result = null;
		
		List<NameValuePair> pair = new ArrayList<>();
		//封装请求参数
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			pair.add(new BasicNameValuePair(entry.getKey(), new StringHepler().toString(entry.getValue(), false)));
		}
		result = new UrlEncodedFormEntity(pair, "utf-8");
		return result;
	}
	
	public StringEntity buildJsonEntity() {
		StringEntity entity = new StringEntity(json,"utf-8");
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");
		return entity;  
	}
	
	public String buildGetUrl(String url) {
		String result = new String();
		
		//非空校验
		checkNotNull(url);
		boolean isFirst = true;
		for(Map.Entry<String, Object> entry: map.entrySet()) {
			if (isFirst) {
				result = String.format("%s?%s=%s", url, entry.getKey(), entry.getValue());
				isFirst = false;
			}else {
				result = String.format("%s&%s=%s", result, entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
}
