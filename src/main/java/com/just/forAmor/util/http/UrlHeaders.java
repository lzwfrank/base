package com.just.forAmor.util.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.just.forAmor.helper.StringHepler;

/**
 * 2016年8月31日
 * @author liu
 *
 */
public class UrlHeaders implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4642678673772867711L;
	
	private static Map<String, Object> map = new HashMap<String, Object>();
	
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
	
	public void clear() {
		map.clear();
	}
	
	public Header[] buildHeaders() {
		
		List<Header> results = new ArrayList<>();
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			Header header = new BasicHeader(entry.getKey(), new StringHepler().toString(entry.getValue()));
			results.add(header);
		}
		Header[] headers = new BasicHeader[results.size()];
		for (int i = 0; i < results.size(); i++) {
			headers[i] = results.get(i);
		}
		return headers;
	}

}
