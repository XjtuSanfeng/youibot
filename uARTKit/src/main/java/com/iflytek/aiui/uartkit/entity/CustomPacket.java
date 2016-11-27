package com.iflytek.aiui.uartkit.entity;

public class CustomPacket extends MsgPacket {

	public byte[] customData;
	
	@Override
	public byte getMsgType() {
		return MsgPacket.CUSTOM_PACKET_TYPE;
	}

	@Override
	protected byte[] getContent() {
		return customData;
	}

	@Override
	protected void decodeContent(byte[] data) {
		customData = data;
	}

}
