package com.iflytek.aiui.uartkit.entity;

public abstract class MsgPacket implements Packet {
	public final static byte HANDSHAKE_REQ_TYPE = 0x01;
	public final static byte WIFI_CONF_TYPE = 0x02;
	public final static byte AIUI_CONF_TYPE = 0x03;
	public final static byte AIUI_PACKET_TYPE = 0x04;
	public final static byte CTR_PACKET_TYPE = 0x05;
	public final static byte CUSTOM_PACKET_TYPE = 0x2A;
	
	public final static byte ACK_TYPE = (byte) 0xff;
	

	protected final static byte[] RESERVED_DATA = new byte[] { DataPacket.SYNC_BYTE, 0x00, 0x00, 0x00 };
	protected static final int TYPE_BIT = 2;

	/**
	 * 获取消息类型
	 * 
	 * @return 消息类型
	 * 
	 */
	public abstract byte getMsgType();

	protected abstract byte[] getContent();

	protected abstract void decodeContent(byte[] data);

	private static int seqIDAchor = 0;

	private int seqID;

	public MsgPacket() {
		seqID = seqIDAchor;
		seqIDAchor += 1;
		if (seqIDAchor > 65536) {
			seqIDAchor = 0;
		}
	}

	@Override
	public byte[] encodeBytes() {
		byte[] content = getContent();
		byte[] ret = new byte[3 + 2 + content.length];
		ret[0] = getMsgType();
		ret[1] = (byte) ((content.length) & 0xff);
		ret[2] = (byte) ((content.length) >> 8 & 0xff);
		ret[3] = (byte) (seqID & 0xff);
		ret[4] = (byte) (seqID >> 8 & 0xff);
		System.arraycopy(content, 0, ret, 5, content.length);
		return ret;
	}

	@Override
	public Packet decodeBytes(byte[] rawData) {
		seqID = (rawData[4] & 0xff) << 8 | rawData[3] & 0xff;
		int lengh = (rawData[2] & 0xff) << 8 | rawData[1] & 0xff;
		byte[] content = new byte[lengh];
		System.arraycopy(rawData, 5, content, 0, lengh);
		decodeContent(content);
		return this;
	}

	/**
	 * 获取消息ID
	 * 
	 * @return
	 */
	public int getSeqID() {
		return seqID;
	}
	
	
	/**
	 * 设置消息ID
	 * @return
	 */
	public void setSeqID(int seqID){
		this.seqID = seqID;
	}
	
	
	public boolean isReqType() {
		if (getMsgType() != MsgPacket.ACK_TYPE) {
			return true;
		} else {
			return false;
		}

	}

}
