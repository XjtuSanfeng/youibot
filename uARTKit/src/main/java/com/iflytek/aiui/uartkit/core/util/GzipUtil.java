package com.iflytek.aiui.uartkit.core.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GzipUtil {
	public static boolean isGZIPData(byte[] data){
		if(data.length >3){
			if(data[0] == (byte)0x1f && data[1] == (byte)0x8b && data[2] == (byte)0x08){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 压缩数据
	 * @param packet 要压缩的数据
	 * @return 返回压缩后的数据
	 */
	public static byte[] compress(byte[] packet) {
		if (null != packet) {
			ByteArrayInputStream byteArrayInputStream = null;
			ByteArrayOutputStream byteArrayOutputStream = null;
			GZIPOutputStream gzipOut = null;
			try {
				byteArrayInputStream = new ByteArrayInputStream(packet);
				byteArrayOutputStream = new ByteArrayOutputStream();
				gzipOut = new GZIPOutputStream(byteArrayOutputStream);
				byte[] buffer = new byte[2048];
				int length = -1;
				while (-1 != (length = byteArrayInputStream.read(buffer, 0, buffer.length))) {
					gzipOut.write(buffer, 0, length);
				}
				gzipOut.finish();
				return byteArrayOutputStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					byteArrayInputStream.close();
					byteArrayOutputStream.close();
					gzipOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 解压数据
	 * @param packet 待解压数据
	 * @return 解压后数据
	 */
	public static byte[] unCompress(byte[] packet) {
		if (null != packet) {
			GZIPInputStream gzipIn = null;
			ByteArrayInputStream byteArrayInputStream = null;
			ByteArrayOutputStream byteArrayOutputStream = null;
			try {
				byteArrayInputStream = new ByteArrayInputStream(packet);
				gzipIn = new GZIPInputStream(byteArrayInputStream);
				byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[2048];
				int length = -1;
				while (-1 !=(length = gzipIn.read(buffer, 0, buffer.length))) {
					byteArrayOutputStream.write(buffer, 0, length);
				}
				return byteArrayOutputStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					byteArrayInputStream.close();
					byteArrayOutputStream.close();
					gzipIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
