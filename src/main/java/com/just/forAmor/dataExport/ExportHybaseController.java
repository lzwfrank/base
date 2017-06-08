package com.just.forAmor.dataExport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trs.common.base.Reports;

/**
 * 2016年11月4日
 * @author liu
 *
 */
@RestController
public class ExportHybaseController {
	
	@Autowired
	private ExportHybaseService exportHybaseService;
	
	@RequestMapping(value = "til/export")
	public String exportHybase() {
		Reports repost = Reports.createNewReports("exportHybase");
		exportHybaseService.exportHybase();
		return repost.reportSuccessInfoAsJson(false);
	}
	
	@RequestMapping(value = "til/weibo")
	public String exportHybaseWeibo() {
		Reports repost = Reports.createNewReports("exportHybase");
		exportHybaseService.test();;
		return repost.reportSuccessInfoAsJson(false);
	}

}
