package com.just.forAmor;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.just.forAmor.util.http.HttpTemplate;


@SpringBootApplication
public class HopelessDream {
    public static void main(String[] args) {
        SpringApplication.run(HopelessDream.class, args);
    }
    
    @PostConstruct
    public void postConstruct() {
		System.out.println("postConstruct 初始化");
	}
    
    @Bean(initMethod = "bean")
    public int	beanInit() {
    	System.out.println("bean init");
		return 0;
    }
    
    @Bean
    public int bean() {
		System.out.println("bean 初始化");
		return 0;
	}
    
    
    @Bean
    public DoSomethingFor initDoSomethingFor() {
		DoSomethingFor doSomethingFor = new DoSomethingFor();
		return doSomethingFor;
	}
    
    @Bean
    public HttpTemplate initHttpTemplate() {
    	HttpTemplate httpTemplate = new HttpTemplate();
		return httpTemplate;
	}
}
