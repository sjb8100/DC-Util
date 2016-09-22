package com.dc.util.shadow.delayPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.dc.util.shadow.exception.IExceptionListener;
import com.dc.util.shadow.shadow.IShadow;
import com.dc.util.shadow.shadow.delayPush.DelayPushRegister;
import com.dc.util.shadow.shadow.delayPush.DelayPushRunnable;
import com.dc.util.shadow.shadow.nomal.Shadow;
import com.dc.util.shadow.shadow.week.WeekShadow;

/**
 * 
 * 延迟推送的测试
 * 
 * 这里假设：
 * 用户可以一次订阅多个资源（现有的资源id为1-8），订阅信息是int[]（里面是资源id），
 * 资源会在大概1.5s后推送给用户，
 * 当用户订阅新的资源时，上一次订阅的资源就不需要推送了
 * 
 * 所以这里的订阅信息是 int[]，订阅的实体是User
 * 
 * @author Daemon
 *
 */
public class DelayPushTest extends DelayPushRunnable<int[], User> {
	
	/**
	 * 资源Id 对应 等待推送该资源的用户影子队列
	 */
	protected HashMap<Integer, UserShadowList> resourceIdMapUserQueue;

	public DelayPushTest(int pollingTime, IExceptionListener exceptionListener) {
		super(pollingTime, exceptionListener);
		
		resourceIdMapUserQueue = new HashMap<Integer, UserShadowList>();
		resourceIdMapUserQueue.put( 1, new UserShadowList() );
		resourceIdMapUserQueue.put( 2, new UserShadowList() );
		resourceIdMapUserQueue.put( 3, new UserShadowList() );
		resourceIdMapUserQueue.put( 4, new UserShadowList() );
		resourceIdMapUserQueue.put( 5, new UserShadowList() );
		resourceIdMapUserQueue.put( 6, new UserShadowList() );
		resourceIdMapUserQueue.put( 7, new UserShadowList() );
		resourceIdMapUserQueue.put( 8, new UserShadowList() );
	}

	@Override
	public void needPush(int[] resourceIdArray, IShadow<User> userShadow) {
		
		//添加用户影子到资源等待推送队列
		for( int i=0; i<resourceIdArray.length; i++ ) {
			
			UserShadowList list = resourceIdMapUserQueue.get( resourceIdArray[i] );
			list.addUserShadow(userShadow);
		}
	}

	@Override
	public void push() {
		
		//推送资源给等待推送的用户 （这里仅仅打印数据）
		
		System.out.println("--------------------------------");
		for( Entry<Integer, UserShadowList> entry : resourceIdMapUserQueue.entrySet() ) {
			
			Integer resourceId = entry.getKey();
			UserShadowList list = resourceIdMapUserQueue.get( resourceId );
			
			ArrayList<IShadow<User>> needPushUserList = list.returnListAndClear();
			
			if( needPushUserList.size() > 0 )
				System.out.println("推送资源：" + resourceId + "--------");
			
			for( IShadow<User> userShadow : needPushUserList ) {
				
				if( userShadow.isvalid() ) {
					 //如果该影子有效 则推送
					System.out.println( "	用户:" + userShadow.getEntity().userName);
				}
			}
			
			if( needPushUserList.size() > 0 )
				System.out.println("-----------------\n");
		}
		System.out.println("--------------------------------\n");
		
	}

	public static void main(String[] args) {
		
		IExceptionListener exceptionListener = new IExceptionListener() {
			public void exception(Exception e) {
				e.printStackTrace();
			}
			public void erroState(String erroInfo) {
			}
		};
		
		final DelayPushRunnable<int[], User> delayPushRunnable = new DelayPushTest(1500, exceptionListener);
		final DelayPushRegister<int[], User> delayPushRegister = new DelayPushRegister<int[], User>(delayPushRunnable, exceptionListener);
		
		//运行delayPushRunnable 和 delayPushRegister
		new Thread(delayPushRunnable).start();
		new Thread(delayPushRegister).start();
		
		//启动Shadow测试线程
		new Thread(new Runnable() {
			public void run() {
				Shadow<User> oldUserShadow = new Shadow<User>(new User("b0001"));
				for(;;) {
					
					//将旧的影子失效，并创建新的影子，并订阅资源1的推送
					oldUserShadow.invalid();
					oldUserShadow = new Shadow<User>(new User("b0001"));
					delayPushRegister.addRegister(new int[]{1}, oldUserShadow);
					
					//将旧的影子失效，并创建新的影子，并订阅资源3,5,6的推送
					oldUserShadow.invalid();
					oldUserShadow = new Shadow<User>(new User("b0001"));
					delayPushRegister.addRegister(new int[]{3,5,6}, oldUserShadow);
					
					try {
						TimeUnit.MILLISECONDS.sleep(600);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
		//启动WeekShadow测试线程
		new Thread(new Runnable() {
			public void run() {
				WeekShadow<User> oldUserShadow = new WeekShadow<User>(new User("cccccc"));
				for(;;) {
					
					//将旧的影子失效，并创建新的影子，并订阅资源1的推送
					oldUserShadow.invalid();
					oldUserShadow = new WeekShadow<User>(new User("cccccc"));
					delayPushRegister.addRegister(new int[]{1}, oldUserShadow);
					
					//将旧的影子失效，并创建新的影子，并订阅资源3,5,6的推送
					oldUserShadow.invalid();
					oldUserShadow = new WeekShadow<User>(new User("cccccc"));
					delayPushRegister.addRegister(new int[]{3,5,6}, oldUserShadow);
					
					try {
						TimeUnit.MILLISECONDS.sleep(600);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
}

/**
 * 
 * 用户类
 * 
 * @author Daemon
 *
 */
class User {
	
	final String userName;
	
	User(String userName) {
		this.userName = userName;
	}
}

/**
 * 
 * 用户影子列表（对ArrayList进行封装，对外提供并发安全的方法）
 * 
 * @author Daemon
 *
 */
class UserShadowList {
	
	ArrayList<IShadow<User>> userShadowList = new ArrayList<IShadow<User>>(512);

	/**
	 * 添加用户影子到列表中(并发安全)
	 * 
	 * @param userShadow 添加用户影子到列表中
	 */
	public synchronized void addUserShadow(IShadow<User> userShadow) {
		userShadowList.add(userShadow);
	}
	
	/**
	 * 把列表清空，并返回之前列表中的所有数据(并发安全)
	 * 
	 * @return 之前列表中的所有数据
	 */
	public synchronized ArrayList<IShadow<User>> returnListAndClear() {
		ArrayList<IShadow<User>> oldList = userShadowList;
		userShadowList = new ArrayList<IShadow<User>>(512);
		return oldList;
	}
	
}