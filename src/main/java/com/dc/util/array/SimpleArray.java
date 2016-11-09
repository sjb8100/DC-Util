package com.dc.util.array;

import java.util.Arrays;

/**
 * 简单的数组封装类（类似于ArrayList，但其中的属性对子类开放）
 * 
 * @author Daemon
 *
 * @param <E> 存在在数组中的类型
 */
public class SimpleArray<E> implements ISimpleArray<E> {
	
	/**
     * Default initial capacity.
     */
	public static final int DEFAULT_CAPACITY = 10;
    
    protected int size = 0;
    protected Object[] elementData;
    
    /**
     * 默认申请长度为10的数组
     */
    public SimpleArray() {
        init();
    }
    
    /**
     * 无参数构造方法 调用的 初始化方法（方便子类覆盖重写构造方法）
     */
    protected void init() {
    	this.elementData = new Object[DEFAULT_CAPACITY];
    }
    
	/**
	 * 数组初始化长度=initialCapacity
	 * 
	 * @param initialCapacity 初始化长度
	 */
	public SimpleArray(int initialCapacity) {
		
		if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
		
		this.elementData = new Object[initialCapacity];
	}
	
	public ArraySnapshot getArraySnapshot() {
		
		return new ArraySnapshot(size, elementData);
	}
	
	@SuppressWarnings("unchecked")
	public E get(int index) {
		
		if (index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
		
		return (E) elementData[index];
	}
	
	public void add(E e) {
		ensureExplicitCapacity(size + 1);
		elementData[size++] = e;
	}
	
    public E set(int index, E element) {
    	
    	if (index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);

        @SuppressWarnings("unchecked")
		E oldValue = (E) elementData[index];
        elementData[index] = element;
        return oldValue;
    }
	
    public E remove(int index) {
        
    	if (index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);

        @SuppressWarnings("unchecked")
		E oldValue = (E) elementData[index];

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }
    
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
    
    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    protected void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }
    
	protected void ensureExplicitCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
	
	protected void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity == 0 ? DEFAULT_CAPACITY : oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
	
	/**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    protected static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
	protected static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
	
}
