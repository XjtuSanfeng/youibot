package com.iflytek.aiui.uartkit.listener;

public class UARTEvent {
	/**
	 * 事件类型
	 * 
	 * @see com.iflytek.aiui.uartkit.constant.UARTConstant
	 */
	public int eventType;
	/**
	 * 事件数据
	 * 
	 * 对于不同的eventType取值，data有不同的类型
	 */
	public Object data;

	public UARTEvent(int eventType, Object data) {
		this.eventType = eventType;
		this.data = data;
	}

	public UARTEvent(int eventType) {
		this.eventType = eventType;
		this.data = null;
	}

}
