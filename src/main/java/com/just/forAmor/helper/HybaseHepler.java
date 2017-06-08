package com.just.forAmor.helper;

import static com.trs.common.utils.StringUtils.isNullOrEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import com.trs.hybase.client.params.ConnectParams;
import com.trs.hybase.client.params.SearchParams;
import com.trs.search.builder.ITRSSearchBuilder;

/**
 * 2016年8月31日
 * @author liu
 *
 */
@Component
public class HybaseHepler {
	
	@Value(value = "${hybase.database.url}")
	private String hybaseUrl;
	
	@Value(value = "${hybase.database.port}")
	private String hybasePost;
	
	@Value(value = "${hybase.database.user}")
	private String hybaseUser;
	
	@Value(value = "${hybase.database.password}")
	private String hybasePassword;
	
	/**
	 * 简单封装一个hybase原生检索
	 * @param builder hybean提供的检索对象
	 * @param index 表名
	 * @param columns 需要查询的字段名
	 * @param sortMethod 排序字段，如-DOCPUBTIME表示时间倒序
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> hybaseSearch(ITRSSearchBuilder builder, String index,
			String columns, String sortMethod) throws Exception{
		List<Map<String, Object>> result = new ArrayList<>();
		
		TRSConnection conn = new TRSConnection(String.format("http://%s:%s", hybaseUrl,hybasePost), 
				hybaseUser, hybasePassword, new ConnectParams());
		SearchParams param = new SearchParams();
		//为传入colunms则使用默认字段
		if (isNullOrEmpty(columns)) {
			columns = "DOCPUBTIME;DOCAUTHOR;ZB_KEYWORDS5_CHAR;IR_CONTENT;IR_ABSTRACT;MC;ZB_GUID_CHAR;DOCTITLE;CATALOG_AREA;ADDITION_CHAR1;"
			+ "ZB_SOURCE_SITE;PUBURL;DOCCHANNEL;CONTENT;IR_SRCNAME";
		}
		//根据传入的值进行排序
		if (!isNullOrEmpty(sortMethod)) {
			param.setSortMethod(sortMethod);
		}
		param.setReadColumns(columns);
		TRSResultSet resultSet = conn.executeSelect(index, builder.asTRSL(), 0, 1000, param);
		//获取结果集
		for (int i = 0,size= resultSet.size(); i < size; i++) {
	        resultSet.moveNext();
	        TRSRecord re = resultSet.get();
	        Map<String, Object> map = new HashMap<>();
	        String[] keys = columns.split(";");
	        for(String key: keys) {
	        	map.put(key, re.getString(key));
	        }
	        result.add(map);
		}
		//关闭链接
		if (conn != null)
	        conn.close();
		return result;
	}

}
