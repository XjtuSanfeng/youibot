package com.iflytek.aiui.uartkit.core.inter;

import com.iflytek.aiui.uartkit.entity.MsgPacket;

public interface RequestListener {
	void onReqeust(MsgPacket packet);
}
