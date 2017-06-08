package com.just.forAmor.zhihu;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrAnalyzer {

    public static String targetUrl;

    private StrAnalyzer(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     *根据正则提取{@code ip}
     * @return {@code ip}
     */
    public String getIp() {
        String ipReg="://(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5]):";
        Matcher matcher = Pattern.compile(ipReg).matcher(targetUrl);
        if(matcher.find()){
            return targetUrl.substring(matcher.start()+3,matcher.end()-1);
        }
        throw new IllegalArgumentException("ip is error format");
    }

    /**
     *根据正则匹配{@code dbName},它将会匹配在://以前的所有内容
     * @return 数据库名字
     */
    public String getDbName() {
        String dbName="\\w{2,}(:){0,}\\w{2,}://";
        Matcher matcher = Pattern.compile(dbName).matcher(targetUrl);
        if(matcher.find()){
            return targetUrl.substring(matcher.start(),matcher.end()-3);
        }
        throw new IllegalArgumentException("dbName is error format");

    }

    /**
     *根据正则提取端口
     * @return {@code port}
     */
    public String getPort() {
        String portReg=":([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5]){2,5}/?";

        Matcher matcher = Pattern.compile(portReg).matcher(targetUrl);
        if(matcher.find()){
            String result = targetUrl.substring(matcher.start() + 1, matcher.end());
            if(result.indexOf("/")>0){
                result=result.substring(0,result.length()-1);
            }
            return result;
        }
          throw new IllegalArgumentException("port is error format");
    }


    private static class LazyHolder {

        private static StrAnalyzer INSTANCE = new StrAnalyzer(targetUrl);
    }

    /**
     * 获得对象实例
     * @param targetUrl  需要解析的字符串地址.必须包含数据库名必须以字母或_开头例如:
     *                   <p>{@code mysql://127.0.0.1:3306}</p>
     * @return 实例对象
     * @throws IllegalArgumentException 当所传入的参数不符合要求时,抛出异常
     */
    public static final StrAnalyzer getInstance(String targetUrl)  throws IllegalArgumentException{
        StrAnalyzer.targetUrl=targetUrl;
        //正则
       String reg="([_|A-Za-z0-9]+)://(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5]):([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5]){2,6}(/|$)";
        targetUrl=targetUrl.trim();
        Matcher matcher=Pattern.compile(reg).matcher(targetUrl);
        if (matcher.find()) {
            return LazyHolder.INSTANCE;
        } else {
            throw new IllegalArgumentException("target format is error");
        }
    }
}
