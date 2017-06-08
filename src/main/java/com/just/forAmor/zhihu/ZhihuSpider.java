package com.just.forAmor.zhihu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.just.forAmor.helper.StringHepler;
import com.just.forAmor.util.http.HttpTemplate;
import com.just.forAmor.util.http.UrlHeaders;
import com.just.forAmor.util.http.UrlParams;
import com.trs.common.base.PreConditionCheck;
import com.trs.common.utils.JsonUtils;
import com.trs.common.utils.StringUtils;

/**
 * 2016年9月30日
 * @author liu
 *
 */
@Component
public class ZhihuSpider {
	
	public static UrlHeaders urlHeaders = new UrlHeaders();
	
	public static UrlParams urlParams = new UrlParams();
	
	public static String defaultPath = "D:\\bgp\\知乎图片\\"; 
	
	public static HttpTemplate httpTemplate = new HttpTemplate();
	
	public static int j = 0;
	
	/**
	 * 对知乎爬虫继续一个完整封装
	 * @param questionNum
	 * @return
	 */
	public String Spider(String questionNum) throws Exception{
		int originalOffset = 300;
		String result = "success";
		String url = "https://www.zhihu.com/question/"+questionNum;
		String html = httpTemplate.httpGetRequestAsJson(url);
//		int originalOffset = Integer.valueOf(this.readWebById(html, "zh-question-answer-num", "[1-9]\\d*"));//展示第多少数据
//		String title = this.readWebByClass(html, "zm-editable-content", "");
		int offset = 10;
		while(originalOffset >= (offset+10)){
			zhihuPicDown(questionNum, "10", String.valueOf(offset));
			offset+=10;
		}
		return result;
	}
	
	private String QuestionAnswer(String questionNum, String pageSize, String offset) throws IOException{
		String result = new String();
		//创建栏目对应文件夹
		buildDefaultPath(questionNum);
		String url = "https://www.zhihu.com/node/QuestionAnswerListV2";
		urlParams.setProprety("method", "next");
		urlParams.setProprety("params", "{\"url_token\":"+questionNum+",\"pagesize\":"+pageSize+",\"offset\":"+offset+"}");
		HttpTemplate httpTemplate = new HttpTemplate();
		result = httpTemplate.httpPostRequestAsJson(url, urlParams);
		return result;
	}
	
	/**
	 * 知乎图片下载
	 * @param questionNum 问题编号
	 * @param pageSize 每页展示数
	 * @param offset 展示第多少数据
	 */
	private void zhihuPicDown(String questionNum, String pageSize, String offset) {
		StringHepler stringUtil = new StringHepler();
		//用于防止下载重复图片
        List<String> picture = new ArrayList<>();
		try {
			String zhihuResult = QuestionAnswer(questionNum, pageSize, offset);
			//解析json，获取返回的答案的html
			List<String> htmls = getHtml(zhihuResult);
			//解析html，抓取其中的图片
			for(String html : htmls) {
				Document doc = Jsoup.parse(html);
				Elements elements = doc.getElementsByClass("zm-item-answer");
				for(Element elemet : elements) {
					//获取正文内容的class
					Elements contentElements = elemet.getElementsByClass("zm-editable-content");
					for(Element contentElement : contentElements) {
						//获取img标签内容
						Elements links = contentElement.getElementsByTag("img");
						for(Element link : links) {
							//获取图片链接
							String pictureLink = link.attr("src");
							//该图不在重复列表中，且不是空字符串并且是一个可用链接
							if (!picture.contains(pictureLink) && pictureLink != "" && stringUtil.isFind(pictureLink, "http")) {
								//获取图片格式
								String format = pictureLink.substring(pictureLink.lastIndexOf("."), pictureLink.length());
								System.out.println(pictureLink.replace("/", "").replace(":", ""));
//								下载图片
								httpTemplate.downloadPic(pictureLink, defaultPath+questionNum+"//"+questionNum+"_"+j+format);
								j++;
								picture.add(pictureLink);
							}
							
						}
					}
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//主要为了获取当前问题一共有多少个答案
	private String readWebById(String html, String cssId, String reg) {
		String result = new String();
		Document doc = Jsoup.parse(html);
		Element element = doc.getElementById(cssId);
		result = StringHepler.find(
				element != null? element.text(): new String(), 
				reg);
		return result;
	}
	
	//主要为了获取当前问题一共有多少个答案
	private String readWebByClass(String html, String cssClass, String reg) {
		StringBuffer result = new StringBuffer();
		Document doc = Jsoup.parse(html);
		List<Element> elements = doc.getElementsByClass(cssClass);
//		if (allNeed) {
//			for (Element element : elements) {
				String elementValue = StringHepler.find(
						elements.get(0) != null? elements.get(0).text(): new String(), 
						reg);
				if (!StringUtils.isNullOrEmpty(elementValue)) {
					result.append(elementValue).append(";");
				}
//			}
//		}
		return StringHepler.deleteLastTag(result.toString(), ";");
	}
	
	//主要为了获取当前问题一共有多少个答案
	private String spiderWeb(String url, String cssId, String reg) {
		String result = new String();
		try {
			String html = httpTemplate.httpGetRequestAsJson(url);
			this.readWebById(html, cssId, reg);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	private void buildDefaultPath(String questionNum) {
		File file = new File(defaultPath+questionNum);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	//解析知乎请求的返回json数组
	private List<String> getHtml(String json) {
		List<String> result = new ArrayList<>();
		Map<String, Object> jsonMap = JsonUtils.toObject(json, HashMap.class);
		result = (List<String>) jsonMap.get("msg");
		return result;
		
	}
	
//	public void getUrl() {
//		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build(); 
//		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build(); 
//		int page = 1;
//		HttpPost post = new HttpPost("https://www.zhihu.com/node/QuestionAnswerListV2?method=next&params=%7B%22url_token%22%3A"+questionNum+"%2C%22pagesize%22%3A20%2C%22offset%22%3A"+page*20+"%7D"); 
//		HttpResponse httpResponse = httpClient.execute(post); 
//		HttpEntity he = httpResponse.getEntity(); 
//		if (he != null) { 
//			String responseString = EntityUtils.toString(he); 
//			// System.out.println("response length:" + responseString.length()); 
//			System.out.println(responseString); 
//		}
//	}
	
}
