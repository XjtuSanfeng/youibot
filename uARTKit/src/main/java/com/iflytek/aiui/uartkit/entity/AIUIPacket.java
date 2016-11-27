package com.iflytek.aiui.uartkit.entity;

import com.iflytek.aiui.uartkit.core.util.GzipUtil;

public class AIUIPacket extends MsgPacket {
	/**
	 * AIUI识别结果（未压缩数据）
	 */
	public String content;
	/**
	 * 是否需要压缩（只在构造数据包的时候需要指明）
	 */
	public boolean isNeedCompress;

	@Override
	public byte getMsgType() {
		return MsgPacket.AIUI_PACKET_TYPE;
	}

	@Override
	protected byte[] getContent() {
		if (isNeedCompress) {
			return GzipUtil.compress(content.getBytes());
		} else {
			return content.getBytes();
		}
	}

	@Override
	protected void decodeContent(byte[] data) {
		if (GzipUtil.isGZIPData(data)) {
			isNeedCompress = true;
			this.content = new String(GzipUtil.unCompress(data));
		} else {
			this.content = new String(data);
		}
	}

}
