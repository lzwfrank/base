package com.just.forAmor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.just.forAmor.helper.FileHepler;

/**
 * 
 * 主要用于云南日报的雷达采集历史数据的问题
 * 云南日报提供url链接，然后由此程序转化成雷达能识别的模板形式
 * 需要传入url的txt和模板路径。最后生成的end.lst就是最终结果
 * 2016年9月5日
 * @author liu
 *
 */
public class AntiMage {
	
	public String replaceUrlForModel(String originPath, String ModelPath) {
		String result = new String();
		FileHepler fileUtil = new FileHepler();
		String path = originPath.substring(0,originPath.lastIndexOf("\\")) + "\\end.lst";
		String model = ModelPath.substring(0,ModelPath.lastIndexOf("\\")) + "\\model.txt";
		try {
			//创建输出文件
			fileUtil.createOrClearFile(path);
			//将传入文件的编码格式改为utf-8并存入输出文件中
//			fileUtil.changeCode(originPath, "GB2312", "UTF-8", path);
			List<String> readContent = fileUtil.readFile(originPath);
			//读取模板信息
			fileUtil.changeCode(ModelPath, "GB2312", "UTF-8", model);
			List<String> modelContent = fileUtil.readFile(model);
			String needReplace = modelContent.get(0).split(";")[0];
			String needReplaceNum = modelContent.get(0).split(";")[4];
			//记录需要重写进文件的数据
			StringBuilder writeContent = new StringBuilder();
			//用于记录网站和栏目
			List<String> channel = new ArrayList<String>();
			int i = 1;
			for(String lineContent : readContent) {
				//对num进行自增操作
				String newNum = String.valueOf(i++);
				//需要进行拼接成需要的content
				String content = modelContent.get(0).replace(needReplace, lineContent)
						.replace(needReplaceNum, newNum);
				writeContent.append(content).append("&-&");
			}
			//清空文件内容
			fileUtil.createOrClearFile(path);
			fileUtil.writeFile(new File(path), writeContent.toString(), "&-&");
			fileUtil.changeCode(path, "UTF-8", "GB2312");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
//	public static void main(String[] args) {
//		String modelPath = "C:\\Users\\liu\\Desktop\\云报客户端.txt";
//		String path = "C:\\Users\\liu\\Desktop\\云报客户端数据.txt";
//		AntiMage am = new AntiMage();
//		String result = am.replaceUrlForModel(path, modelPath);
//		System.out.println(result);
//	}

}
