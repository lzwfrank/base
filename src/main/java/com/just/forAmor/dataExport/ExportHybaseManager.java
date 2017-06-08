package com.just.forAmor.dataExport;

import static com.trs.common.utils.StringUtils.isNullOrEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

import com.just.forAmor.dataExport.entity.HybaseExportResult;
import com.just.forAmor.dataExport.entity.HybaseExportResultRepository;
import com.just.forAmor.dataExport.entity.WeiboExportResult;
import com.just.forAmor.helper.ExcelHepler;
import com.just.forAmor.helper.FileHepler;
import com.just.forAmor.helper.ReflectHelper;
import com.just.forAmor.helper.StringHepler;
import com.just.forAmor.util.http.HttpTemplate;
import com.just.forAmor.util.http.UrlParams;
import com.trs.common.utils.JsonUtils;
import com.trs.common.utils.StringUtils;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import com.trs.hybase.client.params.ConnectParams;
import com.trs.hybase.client.params.SearchParams;
import com.trs.search.builder.ITRSSearchBuilder;
import com.trs.search.builder.SearchBuilderFactory;

/**
 * 2016年8月31日
 * @author liu
 *
 */
@Component
public class ExportHybaseManager {
	
	@Value(value = "${hybase.database.url}")
	private String hybaseUrl;
	
	@Value(value = "${hybase.database.port}")
	private String hybasePost;
	
	@Value(value = "${hybase.database.user}")
	private String hybaseUser;
	
	@Value(value = "${hybase.database.password}")
	private String hybasePassword;
	
	@Value(value = "${export.filePath}")
	private String filePath;
	
	@Value(value = "${weixinBelong.url}")
	private String weixinBelong;
	
	@Value(value = "${cbzs.url}")
	private String cbzsUrl;
	
	@Value(value = "${cbValue.url}")
	private String cbVauleUrl;
	
	@Autowired
	private HybaseExportResultRepository hybaseExportResultRepository;
	
	private ExcelHepler excelUtil = new ExcelHepler();;
	
	@Autowired
	private HttpTemplate httpTemplate;
	
	private static JdbcTemplate jdbcTemplate = null;
	
	private static Map<String, Object> ID_POOL = new HashMap<>();
	
	static {
		ID_POOL.put("重庆日报", "CQRB");
		ID_POOL.put("重庆晚报", "CQWB");
		ID_POOL.put("重庆商报", "CQSB");
		ID_POOL.put("重庆晨报", "CQCB");
		ID_POOL.put("华龙网", "HLW");
	}
	
	private static List<String> navigation = new ArrayList<>();
	private static List<String> weiboavigation = new ArrayList<>();
	static {
		//params也需要转成list形式
		navigation.add("来源");
		navigation.add("微信id");
		navigation.add("URL");
		navigation.add("领域分类");
		navigation.add("内容");
		navigation.add("作者");
		navigation.add("ZB_GUID");
		navigation.add("发布时间");
		navigation.add("标题");
		navigation.add("阅读数");
		navigation.add("点赞数");
		navigation.add("核心头条");
		navigation.add("核心");
		navigation.add("一级");
		navigation.add("二级");
		navigation.add("传播指数");
		navigation.add("类型");
		navigation.add("微信归属");
		navigation.add("微信等级");
		navigation.add("ID");
		navigation.add("年");
		navigation.add("月");
		navigation.add("日");
		navigation.add("时");
		navigation.add("分");
		navigation.add("秒");
		navigation.add("领域分类");
		navigation.add("领域分类");
		navigation.add("领域分类");
		navigation.add("领域分类");
		
		weiboavigation.add("来源");
		weiboavigation.add("URL");
		weiboavigation.add("领域分类");
		weiboavigation.add("内容");
		weiboavigation.add("ZB_GUID");
		weiboavigation.add("发布时间");
		weiboavigation.add("转发数");
		weiboavigation.add("评论数");
		weiboavigation.add("点赞数");
		weiboavigation.add("传播指数");
		weiboavigation.add("ID");
		weiboavigation.add("年");
		weiboavigation.add("月");
		weiboavigation.add("日");
		weiboavigation.add("时");
		weiboavigation.add("分");
		weiboavigation.add("秒");
		weiboavigation.add("领域分类");
		weiboavigation.add("领域分类");
		weiboavigation.add("领域分类");
		weiboavigation.add("领域分类");
	}
	
	public static enum Model {
		weixin,szb,weibo,rb,wb,cb,sb,hlw
	}
	
	
	/**
	 * 简单封装一个hybase原生检索
	 * @param builder hybean提供的检索对象
	 * @param index 表名
	 * @param columns 需要查询的字段名
	 * @param sortMethod 排序字段，如-DOCPUBTIME表示时间倒序
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> hybaseSearch(ITRSSearchBuilder builder, String index,
			String columns, String sortMethod) throws Exception{
		List<Map<String, Object>> result = new ArrayList<>();
		
		TRSConnection conn = new TRSConnection(String.format("http://%s:%s", "192.168.7.151","5555"), 
				"admin", "trsadmin2016", new ConnectParams());
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
		TRSResultSet resultSet = conn.executeSelect(index, builder.asTRSL(), 0, 10000, param);
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
	
	/**
	 * 将map的value转化成string，用&-&分割
	 * @param map
	 * @return
	 */
	private String mapToStrig(Map<String, Object> map) {
		StringBuilder result = new StringBuilder();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			
			result.append(entry.getValue()).append("&-&");
		}
		return result.toString();
	}
	
	private String hybaseExportResultsToStrig(HybaseExportResult hybaseResult) throws NoSuchMethodException,InvocationTargetException, IllegalAccessException{
		StringBuilder result = new StringBuilder();

		ReflectHelper<HybaseExportResult> reflectUtil = new ReflectHelper<>();
		Field[] fields = hybaseResult.getClass().getDeclaredFields();
		for(Field field : fields){
			Method method = reflectUtil.buildGetMethod(hybaseResult, field.getName());
			Object value = method.invoke(hybaseResult);
			result.append(value).append("&-&");
		}
		return result.toString();
	}
	
	private Map<String, Object> hybaseExportResultsToMap(HybaseExportResult hybaseResult) throws NoSuchMethodException,InvocationTargetException, IllegalAccessException{
		Map<String, Object> result = new HashMap<>();
		result = JsonUtils.toObject(JsonUtils.toJson(hybaseResult), HashMap.class);
		return result;
	}
	
	/**
	 * 过滤掉数据来源末尾的网或数字报
	 * @param source
	 * @return
	 */
	public String deleteEndWith(String source) {
		if (source.endsWith("网") && !"华龙网".equals(source)) {
			source = source.substring(0, source.lastIndexOf("网"));
		}else if(source.endsWith("数字报")){
			source = source.substring(0, source.lastIndexOf("数字报"));
		}
		return source;
	}
	
	private List<HybaseExportResult> mergeCbfxResultToHybaseResult(List<HybaseExportResult> hybaseResults) throws Exception{
		ReflectHelper<CbfxResult> reflectUtil = new ReflectHelper<>();
		int i= 1;
		for (HybaseExportResult hybaseExportResult : hybaseResults) {
			//发送post请求
//			String url = "cbzsUrl";
			UrlParams params = new UrlParams();
			params.setProprety("guid", hybaseExportResult.getZB_GUID());
			String cbfxJson = httpTemplate.httpPostRequestAsJson(cbzsUrl, params);
			//获取传播力指数
			if (!isNullOrEmpty(cbfxJson)) {
				Map<String, Object> cbfxResult = JsonUtils.toObject(cbfxJson, HashMap.class);
				for(Entry<String, Object> entry: cbfxResult.entrySet()) {
					//对不同属性进行不同处理
					switch (entry.getKey()) {
						case "EXCOREMEDIATOPS": //核心头条
							hybaseExportResult.setCentralFirst(
									String.valueOf(entry.getValue())
									);
							break;
						case "EXCOREMEDIAS"://核心
							hybaseExportResult.setCentral(
									String.valueOf(entry.getValue())
									);
							break;
						case "EXLV1MEDIAS"://一级
							hybaseExportResult.setOne(
									String.valueOf(entry.getValue())
									);
							break;
						
						case "EXLV2MEDIAS"://二级
							hybaseExportResult.setTwo(
									String.valueOf(entry.getValue())
									);
							break;
						
						case "CEIINDEX"://传播指数
							hybaseExportResult.setCb(
									String.valueOf(entry.getValue())
									);
							break;
							
						case "UV"://阅读数
							hybaseExportResult.setIR_RDCOUNT(
									String.valueOf(entry.getValue())
									);
							break;
							
						case "praises"://点赞数
							hybaseExportResult.setIR_PRCOUNT(
									String.valueOf(entry.getValue())
									);
							break;
			
						default:
							break;
					}
				}
			}
			String source = hybaseExportResult.getZB_SOURCE_SITE();
			source = source.contains("数字报")? "szb":
				source.contains("微信")? "weixin": "website";
			hybaseExportResult.setSource(source);
			//处理微信id归属
			if (source.equals("weixin")) {
				String weixinUrl = String.format("http://%s/weixinBelong/%s", weixinBelong, hybaseExportResult.getZB_SOURCE_SITE());
				String belongWeixin = httpTemplate.httpGetRequestAsJson(weixinUrl);
				if (!belongWeixin.equals("[]")) {
					List<Map<String, String>> belongs = JsonUtils.toObject(belongWeixin, ArrayList.class);
					hybaseExportResult.setWeixinSource(belongs.get(0).containsKey("belong_to")?
							!StringUtils.isNullOrEmpty(belongs.get(0).get("belong_to"))? belongs.get(0).get("belong_to"): new String()
									:new String());
					hybaseExportResult.setWeixinLevel(belongs.get(0).containsKey("inner_level")?
							!StringUtils.isNullOrEmpty(belongs.get(0).get("inner_level"))? belongs.get(0).get("inner_level"): new String()
									:new String());
				}
			}
			//生成id
			String name = getSourceId(hybaseExportResult.getZB_SOURCE_SITE());
			String time = hybaseExportResult.getDOCPUBTIME().replace("/", "").replace(":", "").replace(" ", "");
			hybaseExportResult.setExcelId(name+source+time+"_"+i);
			i++;
			//处理数据来源，清除后缀
			if (!StringUtils.isNullOrEmpty(hybaseExportResult.getZB_SOURCE_SITE())) {
				hybaseExportResult.setZB_SOURCE_SITE(
						this.deleteEndWith(hybaseExportResult.getZB_SOURCE_SITE())
						);
			}
		}
		return hybaseResults;
	}
	
	
	
	private List<HybaseExportResult> spilitMysqlResultToHybaseResult(List<HybaseExportResult> hybaseResults) {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUsername("cas");
        dataSource.setUrl("jdbc:mysql://192.168.7.175:3306/CAS");
        dataSource.setPassword("trs123");
		JdbcTemplate JDBC = new JdbcTemplate(dataSource);
		
		for(HybaseExportResult hybaseResult : hybaseResults) {
			if (hybaseResult.getZB_GUID() != null) {
				String guid = StringHepler.toString(hybaseResult.getZB_GUID());
				String sql = String.format("select MEDIALEVEL,COUNT(*) from CAS_EXMEDIAREPRINT where ZBGUID='%s' GROUP BY MEDIALEVEL", guid);
				List<Map<String, Object>> maps = JDBC.queryForList(sql);
				if (maps != null && maps.size() > 0) {
					for (Map<String, Object> map: maps) {
						switch (StringHepler.toString(map.get("MEDIALEVEL"))) {
						case "核心头条":
							hybaseResult.setCentralFirst(StringHepler.toString(map.get("COUNT(*)")));
							break;

						case "核心":
							hybaseResult.setCentral(StringHepler.toString(map.get("COUNT(*)")));
							break;
						case "一级":
							hybaseResult.setOne(StringHepler.toString(map.get("COUNT(*)")));
							break;
						case "二级":
							hybaseResult.setTwo(StringHepler.toString(map.get("COUNT(*)")));
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return hybaseResults;
	}
	
	private List<HybaseExportResult> listToHybaseExportResult(List<Map<String, Object>> maps) 
			throws NoSuchMethodException,InvocationTargetException, IllegalAccessException{
		List<HybaseExportResult> result = new ArrayList<>();
		//用于排重
		List<String> repeatData = new ArrayList<>();
		for(Map<String, Object> map : maps) {
			ReflectHelper<HybaseExportResult> reflectUtil = new ReflectHelper<>();
			HybaseExportResult hybaseResult = new HybaseExportResult();
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				Field[] fields = hybaseResult.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getName().equals(entry.getKey())) {
						reflectUtil.SetMethod(hybaseResult, field, entry.getValue()!=null? entry.getValue(): "0");
					}
				}
			}
			//排除来源且标题相同的数据
			if (!repeatData.contains(hybaseResult.getDOCTITLE())) {
				result.add(hybaseResult);
				repeatData.add(hybaseResult.getDOCTITLE());
			}
		}
		return result;
	}
	
	private String getSourceId(String name) {
		String result = "other";
		for (Map.Entry<String, Object> entry : ID_POOL.entrySet()) {
			if (name.contains(entry.getKey())) {
				result = String.valueOf(entry.getValue());
				break;
			}
		}
		return result;
		
	}
	
	
	
	private String exprotHybase(ITRSSearchBuilder builder, String model) {
		String index = "mlf_product_formal_20160321";
		String params= "ZB_SOURCE_SITE;ADDITION_CHAR2;PUBURL;ZB_ZYZXFIELD_LIST;CONTENT;DOCAUTHOR;ZB_GUID;DOCPUBTIME;DOCTITLE;IR_RDCOUNT;IR_PRCOUNT";
		//目前实现的excel方法只支持list形式
		//查找hybase结果
		try {
			List<Map<String, Object>> hybaseResults = hybaseSearch(builder, index, params, "-IR_RDCOUNT");
			//把结果集放进HybaseExportResult
			List<HybaseExportResult> resultAsHybase = listToHybaseExportResult(hybaseResults);
			//将需要的mysql结果拼接进HybaseExportResult
			resultAsHybase = mergeCbfxResultToHybaseResult(resultAsHybase);
			//存进mysql
//			hybaseExportResultRepository.save(resultAsHybase);
			createExcel(resultAsHybase, model);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	private String simpleExprotHybase(ITRSSearchBuilder builder, String model) {
		String index = "mlf_product_formal_20160321";
		String params= "ZB_SOURCE_SITE;ADDITION_CHAR2;PUBURL;ZB_ZYZXFIELD_LIST;CONTENT;DOCAUTHOR;ZB_GUID;DOCPUBTIME;DOCTITLE;IR_RDCOUNT;IR_PRCOUNT";
		//目前实现的excel方法只支持list形式
		//查找hybase结果
		try {
			List<Map<String, Object>> hybaseResults = hybaseSearch(builder, index, params, "-IR_RDCOUNT");
			//把结果集放进HybaseExportResult
			List<HybaseExportResult> resultAsHybase = listToHybaseExportResult(hybaseResults);
			createExcel(resultAsHybase, model);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	private void exportMysql(String minTime, String maxTime, Model model) throws Exception{
		List<HybaseExportResult> records = new ArrayList<>();
		String type =new String();
		switch (model) {
			case weixin:
				type = "weixin";
				records = hybaseExportResultRepository.findExcelWithTimeAndWeixin("weixin", minTime, maxTime);
				break;
			case szb:
				type = "szb";
				records = hybaseExportResultRepository.findExcelWithTimeAndSzb("szb", "website", minTime, maxTime);
				break;
			default:
				break;
			}
		//目前实现的excel方法只支持list形式
		createExcel(records, type);
	}
	
	private void createExcel(List<HybaseExportResult> resultAsHybases, String model) throws Exception{
		List<String> reader = new ArrayList<>();
		//转化成list<string>，方便进行excel写入
		for(HybaseExportResult hybaseResult : resultAsHybases) {
			reader.add(hybaseExportResultsToStrig(hybaseResult));
		}
		String typePath = String.format("%s\\%s", filePath, nowDate().replace("/", ""));
		FileHepler.createDir(typePath);
		String fileName = String.format("%s//%s_%s.xls", typePath, model,nowDate().replace("/", ""));
		//写入excel
		excelUtil.exportExcel(fileName, reader, navigation, "&-&");
	}
	
    
	public String nowDate() {
    	Date date=new Date();
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    	String time=formatter.format(date);
    	return time;
	}
	
	
	/**
	 * 微博导出工具
	 * @return
	 */
	public String weiboExportMenu() {
		//创建一个日历对象
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat dft = new SimpleDateFormat("yyyy/MM/dd");
    	String startDay = new String(); //开始时间
    	String endDay = dft.format(getDateBefore(new Date(), 1)).replace("/", ""); //结束时间，由于都是到处前一天数据，所以endDay昨天
    	if (calendar.get(Calendar.DAY_OF_MONTH) == 1) { //判断今天是不是当月第一天
//    		获取上个月第一天
    		calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startDay = dft.format(calendar.getTime()).replace("/", "");
		}else if (calendar.get(Calendar.DAY_OF_WEEK)  ==  2) { //判断今天是不是周一
    		calendar.add(Calendar.DATE, (-1)*7);
    		calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
    		startDay = dft.format(calendar.getTime()).replace("/", "");
		}else {
//			获取昨天日期
			startDay = dft.format(getDateBefore(new Date(), 1)).replace("/", "");
		}
    	ITRSSearchBuilder builder = SearchBuilderFactory.createNoPagedBuilder()
				.filterField("DOCPUBTIME", String.format("[%s TO %s]", startDay, endDay), ITRSSearchBuilder.Operator.Equal, false)
				.filterField("ZB_SOURCE_SITE", "互联网微博", ITRSSearchBuilder.Operator.NotEqual, false)
				.filterField("IR_FORWARD_MID", "0", ITRSSearchBuilder.Operator.Equal, false)
				.filterField("IR_RETWEETED_MID", "0", ITRSSearchBuilder.Operator.Equal, false)
				.filterChildField("IR_SCREEN_NAME", "重庆晚报", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("IR_SCREEN_NAME", "重庆日报", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("IR_SCREEN_NAME", "重庆晨报", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("IR_SCREEN_NAME", "今日重庆", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("IR_SCREEN_NAME", "新女报官方微博", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("IR_SCREEN_NAME", "华龙网", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("IR_SCREEN_NAME", "重庆商报", ITRSSearchBuilder.Operator.Equal);
		weiboExprotHybase(builder, "weibo");
		return "success";
		
	}
	
	/**
	 * 报纸微信导出工具
	 * @return
	 */
    public String exportMenu(Model model) throws Exception{
    	//创建一个日历对象
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat dft = new SimpleDateFormat("yyyy/MM/dd");
    	String startDay = new String(); //开始时间
    	String endDay = dft.format(getDateBefore(new Date(), 1)).replace("/", ""); //结束时间，由于都是到处前一天数据，所以endDay昨天
    	if (calendar.get(Calendar.DAY_OF_MONTH) == 1) { //判断今天是不是当月第一天
//    		获取上个月第一天
    		calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startDay = dft.format(calendar.getTime()).replace("/", "");
		}else if (calendar.get(Calendar.DAY_OF_WEEK)  ==  2) { //判断今天是不是周一
    		calendar.add(Calendar.DATE, (-1)*7);
    		calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
    		startDay = dft.format(calendar.getTime()).replace("/", "");
		}else {
//			获取昨天日期
			startDay = dft.format(getDateBefore(new Date(), 1)).replace("/", "");
		}
//			获取昨天日期
		ITRSSearchBuilder builder = SearchBuilderFactory.createNoPagedBuilder()
				.filterField("ZB_ORIGINAL", "1", ITRSSearchBuilder.Operator.GreaterThanOrEqual, false)
				.filterField("ZB_DROP", "0", ITRSSearchBuilder.Operator.Equal, false)
				.filterField("DOCPUBTIME", String.format("[%s TO %s]", startDay, endDay), ITRSSearchBuilder.Operator.Equal, false);
		switch (model) {
			case weixin:
				builder.filterField("ADDITION_CHAR1", "weixin", ITRSSearchBuilder.Operator.Equal, false);
				exprotHybase(builder, "weixin");
				break;
			case szb:
				builder.filterChildField("ADDITION_CHAR1", "website", ITRSSearchBuilder.Operator.Equal)
					   .filterChildField("ADDITION_CHAR1", "szb", ITRSSearchBuilder.Operator.Equal);
				exprotHybase(builder, "szb");
				break;
			default:
				break;
			}
		return "success";
	}
    
    
    public String simpleExportMenu(Model model) throws Exception{
    	SimpleDateFormat dft = new SimpleDateFormat("yyyy/MM/dd");
//			获取昨天日期
		String	startTime = dft.format(getDateBefore(new Date(), 1)).replace("/", "");
		String	endTime = startTime;
		ITRSSearchBuilder builder = SearchBuilderFactory.createNoPagedBuilder()
				.filterField("ZB_ORIGINAL", "1", ITRSSearchBuilder.Operator.GreaterThanOrEqual, false)
				.filterField("ZB_DROP", "0", ITRSSearchBuilder.Operator.Equal, false)
				.filterField("DOCPUBTIME", String.format("[%s TO %s]", startTime, endTime), ITRSSearchBuilder.Operator.Equal, false);
//				.filterField("DOCPUBTIME", String.format("[20161224 TO 20161224]"), ITRSSearchBuilder.Operator.Equal, false);
		switch (model) {
			case rb:
				builder.filterChildField("ZB_SOURCE_SITE", "重庆日报网", ITRSSearchBuilder.Operator.Equal)
					   .filterChildField("ZB_SOURCE_SITE", "重庆日报数字报", ITRSSearchBuilder.Operator.Equal);
				simpleExprotHybase(builder, "重庆日报");
				break;
			case wb:
				builder.filterChildField("ZB_SOURCE_SITE", "重庆晚报数字报", ITRSSearchBuilder.Operator.Equal)
					   .filterChildField("ZB_SOURCE_SITE", "重庆晚报网", ITRSSearchBuilder.Operator.Equal);
				System.out.println(builder.asTRSL());
				simpleExprotHybase(builder, "重庆晚报");
				break;
			case cb:
				builder.filterChildField("ZB_SOURCE_SITE", "重庆晨报网", ITRSSearchBuilder.Operator.Equal)
					   .filterChildField("ZB_SOURCE_SITE", "重庆晨报数字报", ITRSSearchBuilder.Operator.Equal);
				simpleExprotHybase(builder, "重庆晨报");
				break;
			case sb:
				builder.filterChildField("ZB_SOURCE_SITE", "重庆商报数字报", ITRSSearchBuilder.Operator.Equal)
				       .filterChildField("ZB_SOURCE_SITE", "重庆商报网", ITRSSearchBuilder.Operator.Equal);
				simpleExprotHybase(builder, "重庆商报");
				break;
			case hlw:
				builder.filterField("ZB_SOURCE_SITE", "华龙网", ITRSSearchBuilder.Operator.Equal, false);
				simpleExprotHybase(builder, "华龙网");
				break;
			default:
				break;
			}
    	return "success";
	}
    
    @PostConstruct
    private void init() {
    	if (jdbcTemplate == null) {
			SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
			dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
			dataSource.setUrl("jdbc:mysql://192.168.7.157:3306/new_mlf");
			dataSource.setUsername("root");
			dataSource.setPassword("trsadmin");
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
    	
    }
    
    protected static Date getDateBefore(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }
    
	
	private String weiboExprotHybase(ITRSSearchBuilder builder, String model) {
		String index = "weibo_formal_20160219";
		String params= "ZB_SOURCE_SITE;PUBURL;ZB_ZYZXFIELD_LIST;CONTENT;ZB_GUID;DOCPUBTIME;";
		//目前实现的excel方法只支持list形式
		//查找hybase结果
		try {
			List<Map<String, Object>> hybaseResults = hybaseSearch(builder, index, params, "-DOCPUBTIME");
			//把结果集放进HybaseExportResult
			List<WeiboExportResult> resultAsHybase = listToWeiboExportResult(hybaseResults);
			//将需要的mysql结果拼接进HybaseExportResult
			resultAsHybase = weiboMergeCbfxResultToHybaseResult(resultAsHybase);
			//存进mysql
//			hybaseExportResultRepository.save(resultAsHybase);
			createWeiboExcel(resultAsHybase, model);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	private List<WeiboExportResult> weiboMergeCbfxResultToHybaseResult(List<WeiboExportResult> hybaseResults) throws Exception{
		ReflectHelper<CbfxResult> reflectUtil = new ReflectHelper<>();
		int i= 1;
		for (WeiboExportResult hybaseExportResult : hybaseResults) {
			List<Map<String, Object>> mapResult = jdbcTemplate.queryForList("select * from weiboranking where zb_guid_char='"+hybaseExportResult.getZB_GUID()+"'");
			if (mapResult.size() > 0 && mapResult.get(0).containsKey("score")) {
				String score = String.valueOf(
						mapResult.get(0).get("score")
						);
				//转发数
				String rttcount = String.valueOf(
						mapResult.get(0).get("rttcount")
						);
				//评论数
				String commtcount = String.valueOf(
						mapResult.get(0).get("commtcount")
						);
				//点赞数
				String hitcount = String.valueOf(
						mapResult.get(0).get("hitcount")
						);
				hybaseExportResult.setRttcount(rttcount);//转发数
				hybaseExportResult.setCommtcount(commtcount);;//评论数
				hybaseExportResult.setHitcount(hitcount);;//点赞数
				hybaseExportResult.setScore(score);
			}
			//生成id
			String time = hybaseExportResult.getDOCPUBTIME().replace("/", "").replace(":", "").replace(" ", "");
			hybaseExportResult.setExcelId("WEIBO"+time+"_"+i);
			i++;
		}
		return hybaseResults;
	}
	
	private List<WeiboExportResult> listToWeiboExportResult(List<Map<String, Object>> maps) 
			throws NoSuchMethodException,InvocationTargetException, IllegalAccessException{
		List<WeiboExportResult> result = new ArrayList<>();
		//用于排重
		List<String> repeatData = new ArrayList<>();
		ReflectHelper<WeiboExportResult> reflectUtil = new ReflectHelper<>();
		for(Map<String, Object> map : maps) {
			WeiboExportResult hybaseResult = new WeiboExportResult();
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				Field[] fields = hybaseResult.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getName().equals(entry.getKey()) && 
							(field.getName().equals("IR_PRCOUNT") || field.getName().equals("IR_RDCOUNT"))) {
						reflectUtil.SetMethod(hybaseResult, field, entry.getValue()!=null? entry.getValue(): "0");
						break;
					}else if (field.getName().equals(entry.getKey())) {
						reflectUtil.SetMethod(hybaseResult, field, entry.getValue()!=null? entry.getValue(): new String());
						break;
					}
				}
			}
			//排除来源且标题相同的数据
			if (!repeatData.contains(hybaseResult.getCONTENT())) {
				result.add(hybaseResult);
				repeatData.add(hybaseResult.getCONTENT());
			}
		}
		return result;
	}
	
	private void createWeiboExcel(List<WeiboExportResult> resultAsHybases, String model) throws Exception{
		List<String> reader = new ArrayList<>();
		//转化成list<string>，方便进行excel写入
		for(WeiboExportResult hybaseResult : resultAsHybases) {
			reader.add(weiboExportResultsToStrig(hybaseResult));
		}
		String typePath = String.format("%s\\%s", filePath, nowDate().replace("/", ""));
		FileHepler.createDir(typePath);
		String fileName = String.format("%s//%s_%s.xls", typePath, model,nowDate().replace("/", ""));
		//写入excel
		excelUtil.exportExcel(fileName, reader, weiboavigation, "&-&");
	}
	
	private String weiboExportResultsToStrig(WeiboExportResult hybaseResult) throws NoSuchMethodException,InvocationTargetException, IllegalAccessException{
		StringBuilder result = new StringBuilder();

		ReflectHelper<WeiboExportResult> reflectUtil = new ReflectHelper<>();
		Field[] fields = hybaseResult.getClass().getDeclaredFields();
		for(Field field : fields){
			Method method = reflectUtil.buildGetMethod(hybaseResult, field.getName());
			Object value = method.invoke(hybaseResult);
			result.append(value).append("&-&");
		}
		return result.toString();
	}
	
	public void exportTest() {
		ITRSSearchBuilder builder = SearchBuilderFactory.createNoPagedBuilder()
				.filterField("ZB_ORIGINAL", "1", ITRSSearchBuilder.Operator.GreaterThanOrEqual, false)
				.filterField("ZB_DROP", "0", ITRSSearchBuilder.Operator.Equal, false)
				.filterField("DOCPUBTIME", "[20170213 TO 20170219]", ITRSSearchBuilder.Operator.Equal, false)
//				.filterField("ADDITION_CHAR1", "weixin", ITRSSearchBuilder.Operator.Equal, false);
				.filterChildField("ADDITION_CHAR1", "website", ITRSSearchBuilder.Operator.Equal)
				.filterChildField("ADDITION_CHAR1", "szb", ITRSSearchBuilder.Operator.Equal);
		exprotHybase(builder, "szb");
//		ITRSSearchBuilder builder = SearchBuilderFactory.createNoPagedBuilder()
//				.filterField("DOCPUBTIME", "[20170102 TO 20170108]", ITRSSearchBuilder.Operator.Equal, false)
//				.filterField("ZB_SOURCE_SITE", "互联网微博", ITRSSearchBuilder.Operator.NotEqual, false)
//				.filterField("IR_FORWARD_MID", "0", ITRSSearchBuilder.Operator.Equal, false)
//				.filterField("ZB_DROP", "0", ITRSSearchBuilder.Operator.Equal, false)
//				.filterField("IR_RETWEETED_MID", "0", ITRSSearchBuilder.Operator.Equal, false)
//				.filterChildField("IR_SCREEN_NAME", "重庆晚报", ITRSSearchBuilder.Operator.Equal)
//				.filterChildField("IR_SCREEN_NAME", "重庆日报", ITRSSearchBuilder.Operator.Equal)
//				.filterChildField("IR_SCREEN_NAME", "重庆晨报", ITRSSearchBuilder.Operator.Equal)
//				.filterChildField("IR_SCREEN_NAME", "今日重庆", ITRSSearchBuilder.Operator.Equal)
//				.filterChildField("IR_SCREEN_NAME", "新女报官方微博", ITRSSearchBuilder.Operator.Equal)
//				.filterChildField("IR_SCREEN_NAME", "华龙网", ITRSSearchBuilder.Operator.Equal)
//				.filterChildField("IR_SCREEN_NAME", "重庆商报", ITRSSearchBuilder.Operator.Equal);
//		weiboExprotHybase(builder, "weibo全月");
	}
	
//	public static void main(String[] args) {
//		WM_RequestJson  json = new WM_RequestJson();
//		json.setBeginDay("20161024");
//		json.setEndDay("20161030");
//		List<String> urls = new ArrayList<>();
//		urls.add("http://cdsbrss.cdsb.com/detail/080808_7385610.html?newsId=7~1892635193655296");
//		urls.add("http://rss1.qjwb.com.cn/news/wkarticle/367823?newsId=7~1892832000327680");
//		urls.add("http://rss1.qjwb.com.cn/news/wkarticle/368474?newsId=7~1897111816519680");
//		json.setUrls(urls);
//		Connection con = Jsoup.connect("http://mg.ta.trs.cn/bas/api/retrieveWebUV");
//		con.data("json", JsonUtils.toJson(json));
//		Document doc;
//		try {
//			doc = con.post();
//			System.out.println(doc.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
        
//		HttpTemplate template = new HttpTemplate();
//		UrlParams params = new UrlParams();
//		params.setJson(JsonUtils.toJson(json));
//		try {
//			String result = template.httpPostRequestAsJson("http://mg.ta.trs.cn/bas/api/retrieveWebUV", params);
//			System.err.println(JsonUtils.toJson(json)+"\n");
//			System.out.println(result);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
