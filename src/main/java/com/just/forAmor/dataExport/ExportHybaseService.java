package com.just.forAmor.dataExport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.just.forAmor.dataExport.ExportHybaseManager.Model;

import lombok.extern.slf4j.Slf4j;

/**
 * 2016年11月3日
 * @author liu
 *
 */
@Slf4j
@Service
@EnableScheduling
public class ExportHybaseService {
	
	@Autowired
	private ExportHybaseManager exportHybaseManager;
	
	private static List<Model> models = new ArrayList<>();
	
	private static final ExecutorService service = Executors.newCachedThreadPool();
	
	static {
		models.add(Model.szb);
		models.add(Model.weixin);
		models.add(Model.weibo);
		models.add(Model.cb);//晨报
		models.add(Model.hlw);//华龙网
		models.add(Model.rb);//日报
		models.add(Model.sb);//商报
		models.add(Model.wb);//晚报
	}
	
	public void test() {
		exportHybaseManager.exportTest();
	}
	
	public void exportHybase() {
		for (final Model model : models) {
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						if (model.equals(Model.weixin) || model.equals(Model.szb)) {
							exportHybaseManager.exportMenu(model);
						}else if (model.equals(Model.weibo)) {
							exportHybaseManager.weiboExportMenu();
						}else {
							exportHybaseManager.simpleExportMenu(model);
						}
					} catch (Exception e) {
						log.error("export error,the time is "+ exportHybaseManager.nowDate()+ ", and model is " 
								+model.toString());
					}
					
				}
			});
		}
		
	}

}
