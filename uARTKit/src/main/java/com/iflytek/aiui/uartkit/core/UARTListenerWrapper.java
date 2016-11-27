package com.iflytek.aiui.uartkit.core;

import com.iflytek.aiui.uartkit.core.inter.Callbacker;
import com.iflytek.aiui.uartkit.core.inter.RequestListener;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.listener.EventListener;

public class UARTListenerWrapper implements RequestListener {
	private EventListener mListener;
	private UARTManager mUartManager;
	private Callbacker mCallbacker;

	public UARTListenerWrapper(UARTManager uartManager, Callbacker threadHelper, EventListener listener) {
		this.mListener = listener;
		this.mUartManager = uartManager;
		this.mCallbacker = threadHelper;
	}

	@Override
	public void onReqeust(MsgPacket packet) {
		if(packet.getMsgType() != MsgPacket.HANDSHAKE_REQ_TYPE){
			mCallbacker.notifyRequest(mListener, packet);
		}else{
			mUartManager.reset();
		}
	}

}