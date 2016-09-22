package com.dc.util.shadow.shadow.nomal;

import com.dc.util.shadow.shadow.IShadow;

/**
 * 
 * 影子（通过将isvalid声明为volatile确保其可见性）
 * 
 * @author Daemon
 *
 * @param <Entity> 影子的实体
 */
public class Shadow<Entity> implements IShadow<Entity> {

	/**
	 * 影子是否有效
	 */
	protected volatile boolean isvalid;
	
	/**
	 * 影子的实体
	 */
	protected final Entity entity;
	
	public Shadow(Entity entity) {
		this.entity = entity;
		isvalid = true;
	}
	
	public Shadow(Entity entity, boolean isvalid) {
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
