package com.just.forAmor.Template;

import static com.trs.common.base.PreConditionCheck.checkNotNull;

/**
 * 策略类，用于生成调用生成想要的链接体
 * @author liu
 *
 * @param <T>
 */
public class TemplateStraegy<T> {
	
	private TemplateSource<T> source;
	
	
	public TemplateStraegy (TemplateSource<T> baseTemplate) {
		source = checkNotNull(baseTemplate);
	}
	
	public T getInstance() {
		return source.getInstance();
	}
}
