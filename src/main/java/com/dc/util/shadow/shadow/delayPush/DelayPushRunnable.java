package com.dc.util.shadow.shadow.delayPush;

import java.util.concurrent.TimeUnit;

import com.dc.util.shadow.exception.IExceptionListener;
import com.dc.util.shadow.shadow.IShadow;

/**
 * 
 * 延迟推送线程
 * 
 * 每隔 pollingTime 毫秒调用一次 push方法
 * 
 * 当出现以下情况：
 * 1. needPush 和  push 可能会相互阻塞（needPush需要往队列里面添加shadow，push需要读取并操作该队列）
 * 2. needPush 和 needPush 可能会相互阻塞（两个needPush可能需要往同个队列里面添加shadow）
 * 
 * 则建议使用DelayPushRegister，如果没有出现任一种情况，则建议可直接调用needPush
 * 
 * @author Daemon
 *
 * @param <RegisterInfo> 注册信息
 * @param <EntryType> 影子的实体
 */
public abstract class DelayPushRunnable<RegisterInfo, EntryType> implements Runnable {

	protected final IExceptionListener exceptionListener;
	
	protected final int pollingTime;
	
	public DelayPushRunnable(int pollingTime, IExceptionListener exceptionListener) {
		
		this.exceptionListener = exceptionListener;
		
		this.pollingTime = pollingTime;
	}
	
	/**
	 * 注册需要推送的信息
	 * 
	 * @param registerInfo 注册信息
	 * @param shadow 影子的实体
	 */
	public abstract void needPush(RegisterInfo registerInfo, IShadow<EntryType> shadow);
	
	/**
	 * 推送数据 业务处理
	 */
	public abstract void push();
	
	public void run() {
		
		long pollingTime = this.pollingTime;
		
		long timeBegin = System.currentTimeMillis();
		long timeSleep = pollingTime;
		for(;;) {
			
			try {
				
				if( timeSleep > 0 )
					TimeUnit.MILLISECONDS.sleep(timeSleep);
				
				timeBegin = System.currentTimeMillis();
				
				push();
				
				//timeSleep = pollingTime - ( System.currentTimeMillis()-timeBegin );
				timeSleep = timeBegin - System.currentTimeMillis() + pollingTime;
				
			} catch (Exception e) {
				exceptionListener.exception(e);
				
				timeSleep = timeBegin - System.currentTimeMillis() + pollingTime;
			}
		}
	}
	
}
