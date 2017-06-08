package com.just.forAmor.helper;

import static com.trs.common.base.PreConditionCheck.checkNotNull;
import static com.trs.common.utils.StringUtils.isNullOrEmpty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.trs.common.base.Defaults;
import com.trs.common.io.Closer;
import com.trs.common.utils.StringUtils;

/**
 * 2016年8月11日
 * @author liu
 *
 */
public class FileHepler {
	
	/**
	 * 读取文件内容
	 * @param path
	 * @return
	 */
	public List<String> readFile(String path) {
		List<String> result = new ArrayList<String>();
 		File file = new File(path);  
	    BufferedReader reader = null;  
	    try {  
	        System.out.println("以行为单位读取文件内容，一次读一整行：");  
	        reader = new BufferedReader(new FileReader(file));  
	        String tempString = null;  
	        int line = 1;  
	        // 一次读入一行，直到读入null为文件结束  
	        while ((tempString = reader.readLine()) != null) {  
	            // 显示行号  
	            System.out.println("line " + line );  
	            if(!isNullOrEmpty(tempString)) {
	            	String input = tempString;
	            	result.add(input);
	            }
	            line++;
	        }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return result;
	}
	
    /**
     * 将内容写入指定的文件
     * @param file
     * @param content
     * @param regex 每行分割符
     * @throws IOException
     */
    public void writeFile(File file, String content, String regex) throws IOException{
		BufferedWriter bw = Defaults.defaultValue(BufferedWriter.class);
		try (Closer closer = Closer.create()) {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			closer.regist(bw);
			int line = 0;
			if(!StringUtils.isNullOrEmpty(regex) && !StringUtils.isNullOrEmpty(content)){
				String contentArrs[] = content.split(regex);
				for(String section : contentArrs){
					bw.write(section);
					bw.newLine();
					line++;
					System.out.println("已写入 line " +line);
				}
			}else if(StringUtils.isNullOrEmpty(content)){
				bw.write(content);
			}
		} catch (Exception e) {
			throw new IOException(String.format("写入文件失败:[%s]", e));
		}
	}
    
    /**
     * 直接修改该文件的文件编码格式
     * @param filepath
     * @param originalCode
     * @param needCode
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public void changeCode(String filepath, String originalCode, String needCode) throws UnsupportedEncodingException, IOException {
    	changeCode( filepath, originalCode, needCode, filepath);
    }
    
    /**
     * 修改文件的编码格式，修改后的内容存入指定的文件中
     * @param filepath
     * @param originalCode
     * @param needCode
     * @param outFile
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public void changeCode(String filepath, String originalCode, String needCode, String outFile) throws UnsupportedEncodingException, IOException{  
//        BufferedReader buf = null;  
//        OutputStreamWriter pw=null;       
        String str = null;  
        String allstr="";  
          
        //用于输入换行符的字节码  
        byte[] c=new byte[2];  
        c[0]=0x0d;  
        c[1]=0x0a;  
        String t=new String(c);  
        try (BufferedReader buf=new BufferedReader(new InputStreamReader(new FileInputStream(filepath), originalCode)); 
        	 OutputStreamWriter	pw =new OutputStreamWriter(new FileOutputStream(outFile),needCode);){
        	
        	 while((str = buf.readLine()) != null){  
                 allstr=allstr+str+t;  
             }  
             pw.write(allstr);   
             
		}
    }  
    
    /**
     * 如果该路径下没有该文件则新建，有则清空里面的内容
     * @param path
     */
    public static File createOrClearFile(String path) throws IOException{
		checkNotNull(path);
		File file = new File(path);
		try (FileWriter fw =  new FileWriter(file);) {
			if (!file.exists()) {
				file.createNewFile();
			}else {
				fw.write("");//实现内容清空
			}
		}
		return file;
	}
    
    /**
     * 如果该路径下没有该文件则新建，有则情况里面的内容
     * @param path
     */
    public static void createDir(String path) throws IOException{
		checkNotNull(path);
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
    }

}
