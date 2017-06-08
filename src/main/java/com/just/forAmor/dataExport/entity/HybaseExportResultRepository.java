package com.just.forAmor.dataExport.entity;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 2016年11月4日
 * @author liu
 *
 */

@Transactional
public interface HybaseExportResultRepository extends JpaSpecificationExecutor<HybaseExportResult>,PagingAndSortingRepository<HybaseExportResult, String>{
	
	@Query(value = "select h from HybaseExportResult h where h.source = ?1 and h.DOCPUBTIME >= ?2 and h.DOCPUBTIME < ?3 ")
	public List<HybaseExportResult> findExcelWithTimeAndWeixin(String model, String minTime, String maxTime);
	
	@Query(value = "select h from HybaseExportResult h where h.source = ?1 or h.source = ?2 and h.DOCPUBTIME >= ?3 and h.DOCPUBTIME < ?4 ")
	public List<HybaseExportResult> findExcelWithTimeAndSzb(String model, String type, String minTime, String maxTime);

}
