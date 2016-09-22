package com.dc.util.shadow;

/**
 * 
 * 对静态volatile属性的读取和写入
 * （用于特殊目的：1.防止重排序   2.触发 lock前缀 或者 内存屏障）
 * 
 * @author Daemon
 *
 */
public class Volatile {

	protected static volatile boolean value;
	
	private Volatile() {
	}
	
	public static boolean readVolatile() {
		return value;
	}
	
	public static void writeVolatile() {
		value = true;
	}
}
