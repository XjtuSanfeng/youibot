package com.iflytek.aiui.uartkit.entity;

import com.iflytek.aiui.uartkit.core.util.PacketUtil;

public class DataPacket implements Packet {
	protected final static byte SYNC_BYTE = (byte) 0xa5;
	protected final byte USER_BYTE = (byte) 0x01;

	/**
	 * 格式 | SYNC | USER | DATA | CheckCode
	 */

	private byte sync = DataPacket.SYNC_BYTE;
	private byte user = USER_BYTE;
	public MsgPacket data;
	private byte checkCode;

	@Override
	public byte[] encodeBytes() {
		byte[] dataBytes = data.encodeBytes();
		byte[] ret = new byte[dataBytes.length + 3];
		ret[0] = sync;
		ret[1] = user;
		System.arraycopy(dataBytes, 0, ret, 2, dataBytes.length);
		checkCode = checkCode(ret, 0, ret.length - 2);
		ret[ret.length - 1] = checkCode;
		return ret;
	}

	@Override
	public Packet decodeBytes(byte[] rawData) throws UARTException{
		sync = rawData[0];
		user = rawData[1];
		byte[] dataBytes = new byte[rawData.length - 3];
		System.arraycopy(rawData, 2, dataBytes, 0, dataBytes.length);
		data = PacketUtil.parse(dataBytes);
		if(data == null){
			throw new UARTException("Decode Data Error");
		}
		checkCode = rawData[rawData.length - 1];
		return this;
	}

	public static boolean isValid(byte[] data) {
		return checkCode(data, 0, data.length - 2) == data[data.length - 1];
	}

	private static byte checkCode(byte[] data, int start, int end) {
		byte sum = 0;
		for (int index = 0; index <= end; index++) {
			sum += data[index];
		}

		return (byte) ((~(sum & 0xff) + 1) & 0xff);
	}

	public static DataPacket buildDataPacket(MsgPacket data) {
		DataPacket ret = new DataPacket();
		ret.data = data;
		return ret;
	}
}
