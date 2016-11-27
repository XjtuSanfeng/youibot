package com.iflytek.aiui.uartkit.entity;

import com.iflytek.aiui.uartkit.core.util.GzipUtil;

public class ControlPacket extends MsgPacket{
	public String controlCMD = "";
	
	
	@Override
	public byte getMsgType() {
		return CTR_PACKET_TYPE;
	}

	@Override
	protected byte[] getContent() {
//		return GzipUtil.compress(controlCMD.getBytes());
		return controlCMD.getBytes();
	}

	@Override
	protected void decodeContent(byte[] data) {
		if(GzipUtil.isGZIPData(data)){
			controlCMD = new String(GzipUtil.unCompress(data));
		}else{
			controlCMD = new String(data);
		}
		
	}

}
