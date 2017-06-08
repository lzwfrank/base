package com.just.forAmor.dataExport.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2016年8月31日
 * @author liu
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "export_data")
public class HybaseExportResult {
	
	@Column(name = "sourceSite")
	private String ZB_SOURCE_SITE;
	
	@Column(name = "ADDITION_CHAR2")
	private String ADDITION_CHAR2;
	
	private String PUBURL;
	
	@Column(name = "ZB_ZYZXFIELD_LIST")
	private String ZB_ZYZXFIELD_LIST;
	
	@Lob
	@Column(name = "CONTENT")
	private String CONTENT;
	
//	private String ZB_ORIGINAL;
	@Column(name = "DOCAUTHOR")
	private String DOCAUTHOR;
	
	@Id
	private String ZB_GUID;
	
	@Column(name = "DOCPUBTIME")
	private String DOCPUBTIME;
	
	@Column(name = "DOCTITLE")
	private String DOCTITLE;
	
	@Column(name = "IR_RDCOUNT")
	private String IR_RDCOUNT = "";
	
	@Column(name = "IR_PRCOUNT")
	private String IR_PRCOUNT = "";
	
	@Column(name = "centralFirst")
	private String centralFirst;
	
	@Column(name = "central")
	private String central;
	
	@Column(name = "one")
	private String one;
	
	@Column(name = "two")
	private String two;
	
	@Column(name = "cb")
	private String cb;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "weixinSource")
	private String weixinSource;
	
	@Column(name = "weixinLevel")
	private String weixinLevel;
	
	@Column(name = "excelId")
	private String excelId;
	
}
