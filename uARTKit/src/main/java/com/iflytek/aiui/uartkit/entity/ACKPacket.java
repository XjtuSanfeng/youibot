package com.iflytek.aiui.uartkit.entity;

public class ACKPacket extends MsgPacket {

	@Override
	public byte getMsgType() {
		return MsgPacket.ACK_TYPE;
	}

	@Override
	protected byte[] getContent() {
		return MsgPacket.RESERVED_DATA;
	}

	@Override
	protected void decodeContent(byte[] data) {

	}

}
