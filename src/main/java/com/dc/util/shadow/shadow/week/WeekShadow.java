package com.dc.util.shadow.shadow.week;

import com.dc.util.shadow.shadow.IShadow;

/**
 * 
 * 弱可见性的影子（属性isvalid可见性无保证）
 * 
 * @author Daemon
 *
 * @param <Entity> 影子的实体
 */
public class WeekShadow<Entity> implements IShadow<Entity> {

	/**
	 * 影子是否有效
	 */
	protected boolean isvalid;
	
	/**
	 * 影子的实体
	 */
	protected final Entity entity;
	
	public WeekShadow(Entity entity) {
		this.entity = entity;
		isvalid = true;
	}
	
	public WeekShadow(Entity entity, boolean isvalid) {
		this.entity = entity;
		this.isvalid = isvalid;
	}
	
	public void valid() {
		isvalid = true;
	}

	public void invalid() {
		isvalid = false;
	}
	
	public boolean isvalid() {
		return isvalid;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
}
