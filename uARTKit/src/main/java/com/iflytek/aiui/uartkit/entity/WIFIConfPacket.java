package com.iflytek.aiui.uartkit.entity;

public class WIFIConfPacket extends MsgPacket {
	public static enum WIFIStatus {
		CONNECTED(0);

		private byte val;

		private WIFIStatus(int val) {
			this.val = (byte) val;
		}

		public byte toVal() {
			return val;
		}

		public static WIFIStatus parse(byte val) {
			switch (val) {
			case 0:
			default:
				return CONNECTED;
		
			}
		}
	}

	public static enum EncryptMethod {
		OPEN(0), WEP(1), WPA(2);

		private byte val;

		private EncryptMethod(int val) {
			this.val = (byte) val;
		}

		public byte toVal() {
			return val;
		}

		public static EncryptMethod parse(byte val) {
			switch (val) {
			case 0:
				return OPEN;
			case 1:
				return WEP;
			default:
			case 2:
				return WPA;
			}
		}
	}

	/**
	 * | status | encrypt | ssid len |　passwd len |　ssid | password
	 */

	/**
	 * WIFI连接状态
	 * 
	 * @see com.iflytek.aiui.uartkit.entity.WIFIConfPacket.WIFIStatus
	 */
	public WIFIStatus status;

	/**
	 * WIFI加密方式
	 * 
	 * @see com.iflytek.aiui.uartkit.entity.WIFIConfPacket.EncryptMethod
	 */
	public EncryptMethod encrypt;

	/**
	 * WIFI名称
	 */
	public String ssid;

	/**
	 * WIFI密码
	 */
	public String passwd;

	@Override
	public byte getMsgType() {
		return MsgPacket.WIFI_CONF_TYPE;
	}

	@Override
	protected byte[] getContent() {
		byte[] ssidBytes = ssid.getBytes();
		byte[] passwdBytes = passwd.getBytes();
		byte[] ret = new byte[4 + ssidBytes.length + passwdBytes.length];
		ret[0] = status.toVal();
		ret[1] = encrypt.toVal();
		ret[2] = (byte) ssidBytes.length;
		ret[3] = (byte) passwdBytes.length;
		System.arraycopy(ssidBytes, 0, ret, 4, ssidBytes.length);
		System.arraycopy(passwdBytes, 0, ret, 4 + ssidBytes.length, passwdBytes.length);
		return ret;
	}

	@Override
	protected void decodeContent(byte[] data) {
		status = WIFIStatus.parse(data[0]);
		encrypt = EncryptMethod.parse(data[1]);

		byte[] ssidBytes = new byte[data[2]];
		System.arraycopy(data, 4, ssidBytes, 0, ssidBytes.length);
		ssid = new String(ssidBytes);

		byte[] passwdBytes = new byte[data[3]];
		System.arraycopy(data, 4 + ssidBytes.length, passwdBytes, 0, passwdBytes.length);
		passwd = new String(passwdBytes);
	}

}
