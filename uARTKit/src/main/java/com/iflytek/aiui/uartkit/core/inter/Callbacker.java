package com.iflytek.aiui.uartkit.core.inter;

import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.listener.EventListener;
import com.iflytek.aiui.uartkit.listener.UARTEvent;

public interface Callbacker {

	void notifyRequest(EventListener listener, MsgPacket packet);

	void notifyEvent(EventListener listener, UARTEvent event);

	void destroy();

}