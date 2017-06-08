package com.just.forAmor.helper;

import static com.trs.common.base.PreConditionCheck.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.avro.reflect.Nullable;

/**
 * 2016年8月18日
 * @author liu
 *
 */
public class ReflectHelper<T> {
	
	

	/**
	 * 首字母大写
	 * @param name
	 * @return
	 */
	private String upperCase(String name) {
		checkNotNull(name);
		return name.substring(0,1).toUpperCase()+name.substring(1);
	}
	
	/**
	 * 构建get方法
	 * @param t 
	 * @param fieldName 方法中会进行首字母大写操作
	 * @return
	 * @throws NoSuchMethodException
	 */
	public Method buildGetMethod(T t, String fieldName) throws NoSuchMethodException{
		return t.getClass().getMethod("get" + this.upperCase(checkNotNull(fieldName)));
	}
	
	@Nullable
	public Object getMethod(T t, Field field) {
		Object result = null;
		try {
			Method method = buildGetMethod(t, field.getName());
			result = method.invoke(t);
		} catch (Exception e) {
			throw new IllegalAccessError(e.toString());
		}
		return result;
	}
	
	
	public void SetMethod(T t, Field field, Object value) {
		try {
			Method method =  buildSetMethod(t, field);
			method.invoke(t, value);
		} catch (Exception e ) {
			throw new IllegalAccessError(e.toString());
		}
	}
	
	/**
	 * 构建set方法
	 * @param t
	 * @param fieldName 方法中会进行首字母大写操作
	 * @return
	 * @throws NoSuchMethodException
	 */
	public Method buildSetMethod(T t, Field field) throws NoSuchMethodException{
			
		return t.getClass().getMethod("set"+this.upperCase(field.getName()), field.getType());
	}
	
	/**
	 * 将map转化成对象。
	 * @param map
	 * @return
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	public T mapToBean(Map<String, Object> map) throws NoSuchMethodException,Exception{
		T result = null;
		//获取参数列表
		Field[] fields = result.getClass().getDeclaredFields();
		//以T的参数为基础，去map中查找名字相同的key
		for (Field field : fields) {
			if (map.containsKey(field.getName())) {
				String fieldValue = map.get(field.getName())!=null
						? String.valueOf(map.get(field.getName()))
								:new String();
				//执行set方法
				Method setMethod = buildSetMethod(result, field);
				setMethod.invoke(result, fieldValue);
			}
		}
		return result;
	}
}
