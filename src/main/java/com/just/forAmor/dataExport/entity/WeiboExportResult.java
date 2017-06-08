package com.just.forAmor.dataExport.entity;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

/**
 * 2016年11月22日
 * @author liu
 *
 */
@Data
public class WeiboExportResult {
	
	private String ZB_SOURCE_SITE;
	
	private String PUBURL;
	
	private String ZB_ZYZXFIELD_LIST;
	
	private String CONTENT;
	
	private String ZB_GUID;
	
	private String DOCPUBTIME;
	
	private String rttcount ;
	
	private String commtcount ;
	
	private String hitcount;
	
	private String score;
	
	private String excelId;

}
