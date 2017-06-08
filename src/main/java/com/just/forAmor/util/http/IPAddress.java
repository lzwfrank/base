package com.just.forAmor.util.http;

import static com.trs.common.base.PreConditionCheck.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.just.forAmor.helper.AddressHepler;
import com.trs.common.utils.JsonUtils;

/**
 * 2016年9月12日
 * 
 * @author liu
 *
 */
public class IPAddress {

	public static String getIP(String name) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(name);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("获取失败");
		}
		return address.getHostAddress().toString();
	}
	
	/**
	 * 获取map中对应key键的值
	 * @param map
	 * @param key
	 * @return
	 */
	private static String getValueFromMap(Map<String, Object> map, String key) {
		checkNotNull(key);
		String result = new String();
		if (map.containsKey(key)) {
			result = map.get(key) !=null?
					String.valueOf(map.get(key))
						:new String();
		}
		return result;
	}
	
	public static void getAddressByIp(String ip) throws Exception {  
	    // json_result用于接收返回的json数据  
	    String json_result = null;  
	    try {  
	        json_result = AddressHepler.getAddresses("ip=" + ip, "utf-8");  
	    } catch (UnsupportedEncodingException e) {  
	        e.printStackTrace();  
	    }  
	    Map<String, Map<String, Object>> json = JsonUtils.toObject(json_result, HashMap.class);
	    Map<String, Object> data = json.containsKey("data")? json.get("data"): null;
	    String country = getValueFromMap(data, "country");  
	    String region = getValueFromMap(data, "region");;  
	    String city = getValueFromMap(data, "city");;  
//	    String county = getValueFromMap(data, "county");;  
//	    String isp = getValueFromMap(data, "isp");;  
//	    String area = getValueFromMap(data, "area");;  
//	    System.out.println("国家： " + country);  
//	    System.out.println("地区： " + area);  
//	    System.out.println("省份: " + region);  
//	    System.out.println("城市： " + city);  
//	    System.out.println("区/县： " + county);  
//	    System.out.println("互联网服务提供商： " + isp);  
	      
	    String address = country + "/";  
	    address += region + "/";  
	    address += city + "/";  
//	    address += county;  
	    System.out.println(address);  
	}  
	
	public static String getDomainName (String ip) {
		String result = new String();
	    //去掉域名前面的http(s)://
		result = ip.substring(ip.indexOf("//")+2);
		return result = result.substring(0, result.indexOf("/"));
	}
}
