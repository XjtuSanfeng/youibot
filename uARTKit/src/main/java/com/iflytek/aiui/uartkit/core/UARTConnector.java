package com.iflytek.aiui.uartkit.core;

/**
 * 
 *   与底层的串口关联的连接类，通过init send destroy几个方法操作底层串口
 *         底层串口获取到数据之后通过反射调用该类的onReceive方法通知数据到来
 */
public class UARTConnector {
	static {
		System.loadLibrary("uart_helper");
	}

	private static UARTManager sManager;

	// TODO 优化
	public static void setManager(UARTManager uartManager) {
		sManager = uartManager;
	}

	public static void onReceive(byte[] data) {
		sManager.onReceive(data);
	}

	public static native int init(String device, int speed);

	public static native int send(byte[] data);

	public static native void destroy();
}
