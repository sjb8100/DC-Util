package com.dc.util.jdk.open.concurrent;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class OpenUnsafe {
	
	public static final Unsafe unsafe;
	
	static {
		
		Unsafe unsafeTemp;
		try {
			// 通过反射获取rt.jar下的Unsafe类
	        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
	        theUnsafeInstance.setAccessible(true);
	        // return (Unsafe) theUnsafeInstance.get(null);是等价的
	        unsafeTemp = (Unsafe) theUnsafeInstance.get(Unsafe.class);
		} catch (Exception e) {
			e.printStackTrace();
			unsafeTemp = null;
		}
		unsafe = unsafeTemp;
	}

	public static Unsafe getUnsafeInstance() {
		return unsafe;
	}
}
