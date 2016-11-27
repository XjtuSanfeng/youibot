package com.iflytek.aiui.uartkit.entity;

public class AIUIConfPacket extends MsgPacket {
	public String config = "";
	
	
	@Override
	public byte getMsgType() {
		return AIUI_CONF_TYPE;
	}

	@Override
	protected byte[] getContent() {
		return config.getBytes();
	}

	@Override
	protected void decodeContent(byte[] data) {
		config = new String(data);
	}

}
