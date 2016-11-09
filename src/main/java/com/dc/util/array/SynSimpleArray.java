package com.dc.util.array;


/**
 * 封装SimpleArray，对外提供线程安全的方法
 * 
 * @author Daemon
 *
 * @param <E> 存在在数组中的类型
 */
public class SynSimpleArray<E> extends SimpleArray<E> {
	
	/**
     * 默认申请长度为10的数组
     */
	public SynSimpleArray() {
    }
	
	/**
	 * 数组初始化长度=initialCapacity
	 * 
	 * @param initialCapacity 初始化长度
	 */
	public SynSimpleArray(int initialCapacity) {
		super(initialCapacity);
	}
	
	public synchronized ArraySnapshot getArraySnapshot() {
		return super.getArraySnapshot();
	}
	
	@Override
	public synchronized E get(int index) {
		return super.get(index);
	}

	public synchronized void add(E e) {
		super.add(e);
	}
	
	public synchronized E set(int index, E element) {
		return super.set(index, element);
	}

	@Override
	public synchronized E remove(int index) {
		return super.remove(index);
	}

	@Override
	public synchronized boolean remove(Object o) {
		return super.remove(o);
	}
	
	
}
