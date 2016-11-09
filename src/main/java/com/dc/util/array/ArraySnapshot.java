package com.dc.util.array;

/**
 * 
 * 当前数组对象的一个快照（其中elementData的长度运行比size大）
 * 
 * @author Daemon
 *
 */
public class ArraySnapshot {

	public final int size;
	public final Object[] elementData;
	
	/**
	 * @param size 数组内有效元素的长度
	 * @param elementData 数组
	 */
	public ArraySnapshot(int size, Object[] elementData) {
		super();
		this.size = size;
		this.elementData = elementData;
	}
	
	
}
