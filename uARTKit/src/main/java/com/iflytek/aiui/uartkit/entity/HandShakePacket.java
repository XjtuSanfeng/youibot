package com.iflytek.aiui.uartkit.entity;

public class HandShakePacket extends MsgPacket {

	@Override
	public byte getMsgType() {
		return MsgPacket.HANDSHAKE_REQ_TYPE;
	}

	@Override
	protected byte[] getContent() {
		return MsgPacket.RESERVED_DATA;
	}

	@Override
	protected void decodeContent(byte[] data) {

	}

}
