package com.iflytek.aiui.uartkit.core.util;

import com.iflytek.aiui.uartkit.entity.ACKPacket;
import com.iflytek.aiui.uartkit.entity.AIUIConfPacket;
import com.iflytek.aiui.uartkit.entity.AIUIPacket;
import com.iflytek.aiui.uartkit.entity.ControlPacket;
import com.iflytek.aiui.uartkit.entity.CustomPacket;
import com.iflytek.aiui.uartkit.entity.DataPacket;
import com.iflytek.aiui.uartkit.entity.HandShakePacket;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.entity.WIFIConfPacket;

public class PacketUtil {
	public static MsgPacket parse(byte[] rawData){
		byte packetType = rawData[0];
		switch (packetType) {
		case MsgPacket.AIUI_PACKET_TYPE:
			return (MsgPacket)new AIUIPacket().decodeBytes(rawData);
		case MsgPacket.HANDSHAKE_REQ_TYPE:
			return (MsgPacket)new HandShakePacket().decodeBytes(rawData);
		case MsgPacket.WIFI_CONF_TYPE:
			return (MsgPacket)new WIFIConfPacket().decodeBytes(rawData);
		case MsgPacket.AIUI_CONF_TYPE:
			return (MsgPacket)new AIUIConfPacket().decodeBytes(rawData);
		case MsgPacket.CTR_PACKET_TYPE:
		return (MsgPacket)new ControlPacket().decodeBytes(rawData); 
		case MsgPacket.ACK_TYPE:
			return (MsgPacket)new ACKPacket().decodeBytes(rawData);
		case MsgPacket.CUSTOM_PACKET_TYPE:
			return (MsgPacket)new CustomPacket().decodeBytes(rawData);
		default:
			return null;
		}
	}
	
	//FIXME 只返回对应类型 msgPacket
	public static DataPacket getAckMsg(MsgPacket packet){
		MsgPacket ackPacket = new ACKPacket();
		ackPacket.setSeqID(packet.getSeqID());
		
		return DataPacket.buildDataPacket(ackPacket);
	}
	
	public static boolean isMatchAck(MsgPacket req, MsgPacket ack){
		return req.getSeqID() == ack.getSeqID();
	}

}
