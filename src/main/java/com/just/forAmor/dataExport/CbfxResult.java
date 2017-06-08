package com.just.forAmor.dataExport;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CbfxResult {
	
	private Integer EXCOREMEDIATOPS; //核心头条转载
	
	private Integer EXCOREMEDIAS; //核心媒体转载
	
	private Integer EXLV1MEDIAS;//一级媒体
	
	private Integer EXLV2MEDIAS;//二级媒体
	
	private Long CEIINDEX;//传播指数
	
	private Integer UV;//阅读数
	
	private Integer praises;//点赞数
	
}
