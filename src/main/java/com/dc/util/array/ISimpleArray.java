package com.dc.util.array;

/**
 * 简单的数组封装接口
 * 
 * @author Daemon
 *
 * @param <E> 存在在数组中的类型
 */
public interface ISimpleArray<E> {

	/**
	 * 获得当前数组对象的一个快照（其中elementData的长度运行比size大）
	 * 
	 * @return 当前数组对象的一个快照（其中elementData的长度运行比size大）
	 */
	ArraySnapshot getArraySnapshot();
	
	/**
	 * 获得下标为index的元素
	 * 
	 * @param index 元素的下标
	 * @return 下标为index的元素
	 * @throws IndexOutOfBoundsException 当index>=数组长度时
	 */
	E get(int index);
	
	/**
	 * 添加元素
	 * 
	 * @param e 被添加的元素
	 */
	void add(E e);
	
	/**
     * 替换下标为index的元素为element，并返回老的元素
     *
     * @param index 要替换的下标
     * @param element 要替换的元素
     * @return 原来下标为index的元素
     * @throws IndexOutOfBoundsException 当index>=数组长度时
     */
	E set(int index, E element);
	
	/**
     * 删除下标为index的元素，并返回被删除的元素
     *
     * @param index 要删除元素的下标
     * @return the element 被删除的元素
     * @throws IndexOutOfBoundsException 当index>=数组长度时
     */
	E remove(int index);
	
	
	/**
     * 删除元素o，元素o存在于数组中则返回true，否则返回false
     *
     * @param o 要删除的元素
     * @return 元素o存在于数组中则返回true，否则返回false
     */
    boolean remove(Object o);
}
