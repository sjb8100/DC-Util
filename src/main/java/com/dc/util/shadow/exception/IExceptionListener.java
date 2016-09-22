package com.dc.util.shadow.exception;

/**
 * 
 * 异常回调接口(用于记录异常信息等)
 * 
 * @author Daemon
 *
 */
public interface IExceptionListener {

	
	/**
	 * 异常的状态
	 * 
	 * @param erroInfo 异常信息
	 */
	void erroState( String erroInfo );
	
	/**
	 * 
	 * 异常
	 * 
	 * @param e 异常
	 */
	void exception( Exception e );
	
	
}
