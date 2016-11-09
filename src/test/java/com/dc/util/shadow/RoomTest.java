package com.dc.util.shadow;

import com.dc.util.array.ArraySnapshot;
import com.dc.util.array.SynSimpleArray;
import com.dc.util.hash.HashSegmentTools;
import com.dc.util.shadow.shadow.IShadow;
import com.dc.util.shadow.shadow.nomal.Shadow;


/**
 * 房间测试类
 * 
 * @author Daemon
 *
 */
public class RoomTest {
	
	public static void main(String[] args) {
		
		//创建房间
		Room room = new Room();
		
		//创建测试用户：daemon, dc
		User user1 = new User("daemon");
		User user2 = new User("dc");
		
		//添加用户daemon, dc的影子到房间中
		Shadow<User> daemonShadow = new Shadow<User>(user1);
		room.addUser(daemonShadow);
		Shadow<User> dcShadow = new Shadow<User>(user2);
		room.addUser(dcShadow);
		
		//推送消息1
		room.pushMsg("test1 test1 test1");
		
		//用户daemon退出房间
		System.out.println("\n~~~~~~~daemonShadow\n");
		daemonShadow.invalid();
		
		//推送消息2
		room.pushMsg("test2 test2 test2");
		
		//清理无效的用户影子
		room.clearInValidShadow();
		System.out.println("\n~~~~~~~clearInValidShadow\n");
		
		//推送消息3
		room.pushMsg("test3 test3 test3");
		
		
	}
}


/**
 * 房间
 * 
 * 
 * @author Daemon
 *
 */
class Room {
	
	/**
	 * 用户影子容器
	 */
	ShadowContext<User> userContext = new ShadowContext<User>(4);
	
	/**
	 * 用户加入房间
	 * 
	 * @param userShadow 用户影子
	 */
	void addUser(IShadow<User> userShadow) {
		userContext.add(userShadow);
	}
	
	/**
	 * 推送消息给房间内的所有用户
	 * 
	 * @param msg 推送的消息
	 */
	@SuppressWarnings("unchecked")
	void pushMsg(String msg) {
		
		System.out.println("----pushMsg:" + msg);
		
		//循环容器内的所有对象
		for( ShadowArray<User> shadowArray : userContext.shadowArrays ) {
			
			//获得当前用户队列的一个快照
			ArraySnapshot arraySnapshot = shadowArray.getArraySnapshot();
			int size = arraySnapshot.size;
			Object[] elementData = arraySnapshot.elementData;
			
//			System.out.println(size + " " + elementData.length);
			
			//发送给每个有效的用户
			for( int i=0; i<size; i++ ) {
				
				IShadow<User> userShadow = (IShadow<User>) elementData[i];
				if( userShadow.isvalid() ) {
					
					send(userShadow.getEntity(), msg);
					
				} else {
					
					//失效的影子不予处理，打印信息方便测试
					System.out.println( "*****invalid user:'" + userShadow.getEntity().userName + "'" );
				}
			}
		}
		
		System.out.println("----pushMsg over\n");
	}
	
	/**
	 * 推送消息给有用户
	 * 
	 * @param user 用户
	 * @param msg 推送的消息
	 */
	void send(User user, String msg) {
		
		System.out.println( ">>>>>send to user:'" + user.userName );
	}
	
	/**
	 * 清除掉失效的影子
	 */
	void clearInValidShadow() {
		
		userContext.clearInValidShadow();
	}
}

/**
 * 用户影子容器
 * 
 * @author Daemon
 *
 * @param <EntityType> 用户
 */
class ShadowContext<EntityType> extends HashSegmentTools {
	
    /**
     * 将用户存放在不同的桶内，降低添加时候的锁竞争
     */
    final ShadowArray<EntityType>[] shadowArrays;
    
	@SuppressWarnings("unchecked")
	ShadowContext(int concurrencyLevel) {
		
		super(concurrencyLevel);
		
		int ssize = segmentMask + 1;
		
		shadowArrays = new ShadowArray[ssize];
        for( int i=0; i<ssize; i++ ) {
        	shadowArrays[i] = new ShadowArray<EntityType>();
        }
	}
	
	/**
	 * 添加用户
	 * 
	 * @param shadow 用户影子
	 */
	void add(IShadow<EntityType> shadow) {
		
		shadowArrays[getSegmentIndex(shadow)].add(shadow);
	}
	
	/**
	 * 清除掉失效的影子
	 */
	void clearInValidShadow() {
		
		for( ShadowArray<EntityType> array : shadowArrays ) {
			array.clearInValidShadow();
		}
	}
    
}

/**
 * 用户数组封装类，对外提供线程安全的方法
 * 
 * @author Daemon
 *
 * @param <EntityType> 用户
 */
class ShadowArray<EntityType> extends SynSimpleArray<IShadow<EntityType>> {
	
	ShadowArray() {
		super(0);
	}
	
	/**
	 * 清除掉失效的影子
	 */
	@SuppressWarnings("unchecked")
	synchronized void clearInValidShadow() {
		
		int size = this.size;
		Object[] array = this.elementData;

		//循环所有的元素，查找是否有存在失效的影子
		IShadow<EntityType> shadow;
		for( int i=0; i<size; i++ ) {
			
			shadow = (IShadow<EntityType>) array[i];
			if( ! shadow.isvalid() ) {
				
				//发现有失效的影子，更新队列信息并返回
				
				int newSize = 0;
				Object[] newArray = new Object[size+6];
				
				for( int j=0; j<i; j++ ) {
					newArray[newSize++] = (IShadow<EntityType>) array[j];
				}
				
				for( int j=i+1; j<size; j++ ) {
					shadow = (IShadow<EntityType>) array[j];
					if( shadow.isvalid() )
						newArray[newSize++] = shadow;
				}
				
				this.size = newSize;
				this.elementData = newArray;
				
				return;
			}
		}
		
		//没有发现失效的影子
	}
}

/**
 * 用户
 * 
 * @author Daemon
 *
 */
class User {
	
	String userName;
	
	User(String userName) {
		this.userName = userName;
	}
}


