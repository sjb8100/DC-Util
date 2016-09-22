package com.dc.util.shadow.shadow.delayPush;

import java.util.concurrent.LinkedBlockingQueue;

import com.dc.util.shadow.exception.IExceptionListener;
import com.dc.util.shadow.shadow.IShadow;

/**
 * 
 * 延迟推送注册器
 * 
 * 注册器把addRegister的消息放入队列中，由单独线程去调用delayPushRunnable.needPush处理具体业务
 * 
 * DelayPushRegister是搭配DelayPushRunnable使用的，关于是否使用DelayPushRegister可以看DelayPushRunnable说明
 * 
 * 使用DelayPushRegister可以达到以下效果（建议先阅读DelayPushRunnable中说明的前置条件）：
 * 1. 避免调用addRegister的线程的阻塞（DelayPushRunnable.needPush和DelayPushRunnable.push的阻塞只会影响DelayPushRegister线程 和  DelayPushRunnable线程）
 * 2. 避免了并发的调用delayPushRunnable.needPush，降低了阻塞的发送（DelayPushRunnable.needPush和DelayPushRunnable.needPush可能也会导致阻塞）
 * 
 * @author Daemon
 *
 * @param <RegisterInfo> 注册信息
 * @param <EntryType> 影子的实体
 */
public class DelayPushRegister<RegisterInfo, EntryType> implements Runnable {

	protected final IExceptionListener exceptionListener;
	
	protected final DelayPushRunnable<RegisterInfo, EntryType> delayPushRunnable;
	protected final LinkedBlockingQueue<Object[]> registerQueue;
	
	public DelayPushRegister(DelayPushRunnable<RegisterInfo, EntryType> delayPushRunnable, IExceptionListener exceptionListener) {
		
		this.exceptionListener = exceptionListener;
		
		this.delayPushRunnable = delayPushRunnable;
		this.registerQueue = new LinkedBlockingQueue<Object[]>();
	}
	
	/**
	 * 需要推送registerInfo给shadow
	 * （会触发异步调用 delayPushRunnable.needPush方法处理具体业务）
	 * 
	 * @param registerInfo
	 * @param shadow
	 */
	public void addRegister(RegisterInfo registerInfo, IShadow<EntryType> shadow) {
		registerQueue.add(new Object[]{registerInfo, shadow});
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		
		Object[] params; 
		for(;;) {
			
			try {
				
				params = registerQueue.take();
				//调用 delayPushRunnable.needPush方法处理具体注册信息
				delayPushRunnable.needPush( (RegisterInfo)params[0], (IShadow<EntryType>)params[1] );
				
			} catch (Exception e) {
				exceptionListener.exception(e);
			}
		}
	}
	
}
