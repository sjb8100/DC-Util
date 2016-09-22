package com.dc.util.shadow.week;

import java.util.concurrent.TimeUnit;

import com.dc.util.shadow.Volatile;
import com.dc.util.shadow.shadow.week.WeekShadow;

/**
 * 
 * 对WeekShadow可行性的测试，建议可以断点看具体效果
 * 
 * @author Daemon
 *
 */
public class WeekShadowTest {

	public static void main(String[] args) {
		
		final WeekShadow<Integer> shadow = new WeekShadow<Integer>(10);
		
		//随机设置影子 有效 和 失效
		new Thread(new Runnable() {
			public void run() {
				
				long c = 0;
				for(;;) {
					
					if( Math.random() > 0.5 ) {
						shadow.invalid();
					} else {
						shadow.valid();
					}
					
					for(int i=0; i<100000000; i++)
						c = c +i;
					
					if( c % 100000001 == 0 )
						System.out.println();
				}
			}
		}).start();
		
		//每隔0.5秒打印一次当前线程见到的影子的有效性
		new Thread(new Runnable() {
			public void run() {
				
				for(;;) {
					
					Volatile.readVolatile();
					
					System.out.println(shadow.isvalid());
					
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
