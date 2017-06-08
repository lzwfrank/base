package com.just.forAmor.dataExport.entity;

import java.util.List;

import lombok.Data;

/**
 * 2017年1月20日
 * @author liu
 *
 */
@Data
public class WM_RequestJson {
	
	String beginDay;
	
	String endDay;
	
	List<String> urls;

}
