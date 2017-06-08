package com.just.forAmor.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trs.common.utils.StringUtils;
import static com.trs.common.base.PreConditionCheck.*;

/**
 * 2016年8月18日
 * @author liu
 *
 */
public class StringHepler {
	
	public static String toString(Object object) {
		
		return toString(object, false); 
	}

	public static String toString(Object object, boolean needNullToString) {
		
		return needNullToString? 
				String.valueOf(object): 
			object == null? new String(): String.valueOf(object); 
	}
	
	/**
	 * 判断字符串中是否是str
	 * @param content
	 * @param str
	 * @return
	 */
	public static boolean isFind(String content, String str) {
		return Pattern.compile(str).matcher(content).find();
	}
	
	public static boolean isNullOrEmpty(Object object) {
		String input = toString(object);
		return input.equals("")? false: true;
		
	}
	
	public static String deleteLastTag(String content, String tag) {
		String result = new String();
		checkNotNull(tag);
		if (!StringUtils.isNullOrEmpty(content)) {
			result = content.substring(0, content.lastIndexOf(tag));
		}
		return result;
	}
	
	public static String find(String content, String regex) {
		
		return find(content, regex, null);
	}
	
	public static String find(String content, String regex, String sign) {
		if (StringUtils.isNullOrEmpty(content)) {
			return content;
		}
		StringBuffer result = new StringBuffer();
		//默认逗号分隔
		if (isNullOrEmpty(sign)) {
			sign = ";";
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		boolean isFirst = true;
		while (matcher.find()) {
			if (isFirst) {
				result.append(matcher.group());
			}
			else {
				result.append(sign).append(matcher.group());
				isFirst = false;
			}
		}
		return result.toString();
	}
	
//	public List<Object> sortList(List<Object> list, SORT_TYPE type, final boolean isAsc) {
//    	switch (type) {
//			case time:
//				 Collections.sort(list,new Comparator<Object>() {
//			            //降序排序（升序只需要调换o1和o2的位置）
//						public int compare(Object o1,
//			            		Object o2) {
//			            	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd"); // 格式化日期
//			            	//对时间进行排序
//			            	int value1 = 0;
//			            	int value2 = 0;
//							try {
//								value1 = Integer.parseInt(String.valueOf(simpleDateFormat.format(new Date(String.valueOf(o1)))));
//								value2 = Integer.parseInt(String.valueOf(simpleDateFormat.format(new Date(String.valueOf(o2)))));
//							} catch (NumberFormatException e) {
//								// TODO 不处理异常
//							}
//							return isAsc? (value2 - value1): (value1 - value2);
//			            }
//			        });
//				break;
//			case mix:
//				 Collections.sort(list,new Comparator<Object>() {
//			            //降序排序（升序只需要调换o1和o2的位置）
//			            public int compare(Object o1,
//			                   Object o2) {
//			            	//获取纯英文字符
//			            	String alphabet1 = String.valueOf(o1).replaceAll("[0-9]", "");
//			            	String alphabet2 = String.valueOf(o2).replaceAll("[0-9]", "");
//			            	//先对字符进行排序
//			            	int compareResult = isAsc? alphabet2.compareTo(alphabet1): alphabet1.compareTo(alphabet2);
//			            	if(compareResult != 0) {
//			            		return compareResult;
//			            	}
//			            	//过滤掉所有英文字符，然后对纯数字的版次进行排序
//			            	int value1 = Integer.parseInt(String.valueOf(o1).replaceAll("[a-zA-Z]", ""));
//			            	int value2 = Integer.parseInt(String.valueOf(o2).replaceAll("[a-zA-Z]", ""));
//			                return isAsc? (value2 - value1): (value1 - value2);
//			            }
//			            
//			        });
//				break;
//			case number:
//				Collections.sort(list,new Comparator<Object>() {
//		            //降序排序（升序只需要调换o1和o2的位置）
//		            public int compare(Object o1,
//		                   Object o2) {
//		            	//过滤掉所有英文字符，然后对纯数字的版次进行排序
//		            	int value1 = Integer.parseInt(String.valueOf(o1));
//		            	int value2 = Integer.parseInt(String.valueOf(o2));
//		                return isAsc? (value2 - value1): (value1 - value2);
//		            }
//		            
//		        });
//			break;
//		}
//		return list;
//		
//	}

}
