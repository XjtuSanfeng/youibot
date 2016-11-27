package com.iflytek.aiui.uartkit;

import com.iflytek.aiui.uartkit.constant.UARTConstant;
import com.iflytek.aiui.uartkit.core.ExecutorCallbacker;
import com.iflytek.aiui.uartkit.core.UARTListenerWrapper;
import com.iflytek.aiui.uartkit.core.UARTManager;
import com.iflytek.aiui.uartkit.core.inter.ActionListener;
import com.iflytek.aiui.uartkit.core.inter.Callbacker;
import com.iflytek.aiui.uartkit.entity.DataPacket;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.listener.EventListener;
import com.iflytek.aiui.uartkit.listener.UARTEvent;
import com.iflytek.aiui.uartkit.util.PacketBuilder;

public class UARTAgent {
	private static final int MAX_FAILED_COUNT = 5;
	
	private static final int STATUS_OK = 0;
	private static final int STATUS_HANDSHAKING = 1;
	private static final int STATUS_FAILED = 2;
	
	
	private static UARTAgent sInstance;

	private UARTManager mUARTManager;
	private Callbacker mCallbacker;
	private EventListener mEventListener;
	private int mStatus = STATUS_FAILED;
	private int mFailedCount = 0;
	
	

	/**
	 * 创建UARTAgent
	 * 
	 * @param device
	 *            串口设备名(需要设备可读写，如可以通过chmod 777 /dev/ttyS2)
	 * @param speed
	 *            串口速率
	 * @param listener
	 *            串口事件监听器
	 * 
	 * @return 返回创建的UARTAgent
	 */
	public static UARTAgent createAgent(String device, int speed, EventListener listener) {
		if (sInstance == null) {
			sInstance = new UARTAgent(device, speed, listener);
		}

		return sInstance;
	}

	/**
	 * 返回createAgent创建的唯一实例
	 * 
	 * @return UARTAgent
	 */
	public static UARTAgent getUARTAgent() {
		if (sInstance == null) {
			throw new IllegalStateException("Please invoke createAgent firstly");
		}

		return sInstance;
	}

	private UARTAgent(String device, int speed, EventListener listener) {
		mUARTManager = UARTManager.getManager();
		mCallbacker = new ExecutorCallbacker("Uart_AIUI_Thread");
		mEventListener = listener;
		mUARTManager.setRequestListener(new UARTListenerWrapper(mUARTManager, mCallbacker, mEventListener));

		mUARTManager.init(device, speed, new ActionListener() {
			@Override
			public void onSuccess() {
				handShake(new ActionListener() {
					
					@Override
					public void onSuccess() {
						notifyEvent(new UARTEvent(UARTConstant.EVENT_INIT_SUCCESS));
					}
					
					@Override
					public void onFailed(int errorCode) {
						notifyEvent(new UARTEvent(UARTConstant.EVENT_INIT_FAILED));
					}
				});	
			}

			@Override
			public void onFailed(int errorCode) {
				notifyEvent(new UARTEvent(UARTConstant.EVENT_INIT_FAILED));
			}
		});
		
	}
		

	/**
	 * 发送串口消息
	 * 
	 * @param reqPacket
	 *            要发送的串口数据包
	 */
	public boolean sendMessage(final MsgPacket reqPacket) {
		if(mStatus != STATUS_OK){
			return false;
		}
		mUARTManager.sendRequest(DataPacket.buildDataPacket(reqPacket), new ActionListener() {

			@Override
			public void onSuccess() {
				mFailedCount = 0;
			}

			@Override
			public void onFailed(int errorCode) {
				notifyEvent(new UARTEvent(UARTConstant.EVENT_SEND_FAILED, reqPacket));
				mFailedCount++;
				//发送失败超过一定次数，重新握手
				if(mFailedCount > MAX_FAILED_COUNT && mStatus != STATUS_HANDSHAKING){
					handShake(null);
				}
			}
		});
		return true;
	}

	/**
	 * 销毁
	 * 
	 * 关闭串口，取消接收线程
	 */
	public void destroy() {
		mUARTManager.destroy();
		mCallbacker.destroy();

		mUARTManager = null;
		mCallbacker = null;
	}
	
	
	private void handShake(final ActionListener listener) {
		mStatus = STATUS_HANDSHAKING;
		final DataPacket handshakePacket = DataPacket.buildDataPacket(PacketBuilder.obtainHandShakeMsg());
		sendHandshake(new ActionListener() {
			@Override
			public void onSuccess() {
				if(listener != null){
					listener.onSuccess();
				}
				mStatus = STATUS_OK;
			}

			@Override
			public void onFailed(int errorCode) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
				sendHandshake(this, handshakePacket);
			}
		}, handshakePacket);
	}

	private void sendHandshake(final ActionListener listener, DataPacket handshakePacket) {
		mUARTManager.sendRequest(handshakePacket, new ActionListener() {
			
			@Override
			public void onSuccess() {
				listener.onSuccess();
			}
			
			@Override
			public void onFailed(int errorCode) {
				listener.onFailed(errorCode);
			}
		});
	}
	
	private void notifyEvent(UARTEvent event) {
		mCallbacker.notifyEvent(mEventListener, event);
	}

}
