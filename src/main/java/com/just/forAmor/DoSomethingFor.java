package com.just.forAmor;

import static com.trs.common.utils.StringUtils.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.just.forAmor.helper.FileHepler;
import com.just.forAmor.helper.StringHepler;

/**
 * 2016年8月14日
 * @author liu
 *
 */
@Component
public class DoSomethingFor {
	
	public String lineFilter(String originPath) {
		String result = new String();
		FileHepler fileUtil = new FileHepler();
		StringHepler stringHepler = new StringHepler();
		String path = originPath.substring(0,originPath.lastIndexOf("\\")) + "\\end.lst";
		try {
			//创建输出文件
			fileUtil.createOrClearFile(path);
			//将传入文件的编码格式改为utf-8并存入输出文件中
			fileUtil.changeCode(originPath, "GB2312", "UTF-8", path);
			List<String> readContent = fileUtil.readFile(path);
			//记录需要重写进文件的数据
			Map<String, Object> writeContent = new HashMap<String, Object>();
			//用于记录网站和栏目
			List<String> channel = new ArrayList<String>();
			for(String lineContent : readContent) {
				if (!isNullOrEmpty(lineContent) && lineContent.split(";").length > 3) {
					String[] content = lineContent.split(";");
					String url = content[0];
					String web = content[1]+content[2];
					//下面两种情况不会进行后续操作
					if (channel.contains(web)) {
						continue;
					}else if(!channel.contains(web) && !stringHepler.isFind(url, "\\$")) {
						channel.add(web);
						writeContent.put(web, lineContent);
					}else {
						String newUrl = RosesAreRed(url);
						lineContent = lineContent.replace(url, newUrl);
						channel.add(web);
						writeContent.put(web, lineContent);
					}
				}
			}
			//清空文件内容
			fileUtil.createOrClearFile(path);
			fileUtil.writeFile(new File(path), mapToStrig(writeContent), "&-&");
			fileUtil.changeCode(path, "UTF-8", "GB2312");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 将map转换成string
	 * @param map
	 * @return
	 */
	public String mapToStrig(Map<String, Object> map) {
		StringBuilder result = new StringBuilder();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			result.append(entry.getValue()).append("&-&");
		}
		return result.toString();
	}
	
	public String RosesAreRed(String content) {
		if (StringHepler.isFind(content, "\\$")) {
			String wantNum = content.substring(content.lastIndexOf("$[")+2, content.lastIndexOf("-"));
			content = content.replaceAll("\\$\\[(\\d)\\-\\d+\\]", wantNum);
		}
		return content;
	}
	
//	public static void main(String[] args) {
//		SimpleFastDFS fastDFS = new SimpleFastDFS("192.168.7.167:22122");
//		try (FileInputStream input = new FileInputStream(new File("C:\\Users\\liu\\Desktop\\123.jpg"));
//				ByteArrayOutputStream output = new ByteArrayOutputStream()){
//			byte[] fileByte = new byte[1024];
//			int length = 0;
//			while((length = input.read(fileByte)) != -1) {
//				output.write(fileByte, 0, length);
//			}
//			byte[] data = output.toByteArray();
//			String path = fastDFS.uploadResource("jpg", "C:\\Users\\liu\\Desktop\\123.jpg");
//			System.out.println(path);
// 		} catch (Exception e) {
//			e.toString();
//		}
//	}
	
}
