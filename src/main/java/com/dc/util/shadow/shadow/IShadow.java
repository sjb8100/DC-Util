package com.dc.util.shadow.shadow;

/**
 * 
 * 影子对象接口
 * 
 * @author Daemon
 *
 * @param <Entity> 影子的实体
 */
public interface IShadow<Entity> {
	
	/**
	 * 影子有效
	 */
	void valid();
	
	/**
	 * 影子失效
	 */
	void invalid();
	
	/**
	 * 影子是否失效
	 * 
	 * @return 影子是否失效
	 */
	boolean isvalid();
	
	/**
	 * 获得影子的实体
	 * 
	 * @return 影子的实体
	 */
	Entity getEntity();
}
