package com.iflytek.aiui.uartkit.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.aiui.uartkit.entity.AIUIConfPacket;
import com.iflytek.aiui.uartkit.entity.AIUIPacket;
import com.iflytek.aiui.uartkit.entity.ControlPacket;
import com.iflytek.aiui.uartkit.entity.CustomPacket;
import com.iflytek.aiui.uartkit.entity.HandShakePacket;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.entity.WIFIConfPacket;
import com.iflytek.aiui.uartkit.entity.WIFIConfPacket.EncryptMethod;
import com.iflytek.aiui.uartkit.entity.WIFIConfPacket.WIFIStatus;

import android.util.Base64;

public class PacketBuilder {
	/**
	 * 构造握手请求数据包
	 * 
	 * @return MsgPacket数据包
	 * 
	 */
	public static MsgPacket obtainHandShakeMsg() {
		return new HandShakePacket();
	}

	/**
	 * 构造AIUI语音识别结果数据包
	 * 
	 * @param data 语音识别数据
	 * @param isNeedCompress 是否需要压缩（压缩模式采用GZIP压缩）
	 * @return MsgPacket数据包
	 */
	public static MsgPacket obtainAIUIPacket(String data, boolean isNeedCompress) {
		AIUIPacket aiuiPacket = new AIUIPacket();
		aiuiPacket.content = data;
		aiuiPacket.isNeedCompress = isNeedCompress;
		return aiuiPacket;
	}
	
	/**
	 * 构造AIUI配置数据包
	 * 
	 * @param appid  MSC APPID
	 * @param key MSC key
	 * @param sence 场景
	 * @param launchDemo  是否启动ProductDemo
	 * @return
	 */
	public static MsgPacket obtainAIUIConfPacket(String appid, String key, String sence, boolean launchDemo){
		try {
			JSONObject config = new JSONObject();
			config.put("appid", appid);
			config.put("key", key);
			config.put("scene", sence);
			config.put("launch_demo", launchDemo);
			
			JSONObject ret = new JSONObject();
			ret.put("type", "aiui_cfg");
			ret.put("content", config);
			
			AIUIConfPacket configPacket = new AIUIConfPacket();
			configPacket.config = ret.toString();
			return configPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	/**
	 * 构造音频保存命令数据包
	 * @return
	 */
	public static MsgPacket obtainAIUIAudioRecordCmdPacket(){
		try {
			JSONObject config = new JSONObject();
			config.put("save_len", 10);
			
			JSONObject ret = new JSONObject();
			ret.put("type", "save_audio");
			ret.put("content", config);
			
			ControlPacket configPacket = new ControlPacket();
			configPacket.controlCMD = ret.toString();
			return configPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	/**
	 * 构造声音控制数据包
	 * @param enable
	 * @return
	 */
	public static MsgPacket obtainVoiceCtrPacket(boolean enable){
		try {
			JSONObject content = new JSONObject();
			content.put("enable_voice", enable);
			
			JSONObject ret = new JSONObject();
			ret.put("type", "voice");
			ret.put("content", content);
			
			ControlPacket controlPacket = new ControlPacket();
			controlPacket.controlCMD = ret.toString();
			
			return controlPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	public static MsgPacket obtainWIFIStatusReqPacket(){
		try {
			JSONObject content = new JSONObject();
			content.put("query", "wifi");
			
			JSONObject ret = new JSONObject();
			ret.put("type", "status");
			ret.put("content", content);
			
			ControlPacket controlPacket = new ControlPacket();
			controlPacket.controlCMD = ret.toString();
			
			return controlPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	/**
	 * 获取AIUI控制包
	 * @param msgType
	 * @param arg1
	 * @param arg2
	 * @param params
	 * @return
	 */
	public static MsgPacket obtainAIUICtrPacket(int msgType, int arg1, int arg2, String params){
		try {
			JSONObject content = new JSONObject();
			content.put("msg_type", msgType);
			content.put("arg1", arg1);
			content.put("arg2", arg2);
			content.put("params", params);
			
			JSONObject ret = new JSONObject();
			ret.put("type", "aiui_msg");
			ret.put("content", content);
			
			ControlPacket controlPacket = new ControlPacket();
			controlPacket.controlCMD = ret.toString();
			
			return controlPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	/**
	 * 获取AIUI控制包
	 * @param msgType
	 * @param arg1
	 * @param arg2
	 * @param params
	 * @return
	 */
	public static MsgPacket obtainAIUICtrPacket(int msgType, int arg1, int arg2, String params, byte[] data){
		try {
			JSONObject content = new JSONObject();
			content.put("msg_type", msgType);
			content.put("arg1", arg1);
			content.put("arg2", arg2);
			content.put("params", params);
			content.put("data", Base64.encodeToString(data, 0));
			
			JSONObject ret = new JSONObject();
			ret.put("type", "aiui_msg");
			ret.put("content", content);
			
			ControlPacket controlPacket = new ControlPacket();
			controlPacket.controlCMD = ret.toString();
			
			return controlPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	
	
	/**
	 * 构造WIFI配置结果数据包
	 * 
	 * @param status 当前WIFI连接状态 CONNECTED, DISCONNECTED, UNKNOWN
	 * @param type WIFI加密类型 OPEN, WEP, WPA
	 * @param ssid WIFI名称
	 * @param passwd WIFI密码
	 * @return MsgPacket数据包
	 */
	public static MsgPacket obtainWIFIConfPacket(WIFIStatus status, EncryptMethod type, String ssid, String passwd) {
		WIFIConfPacket wifiConfPacket = new WIFIConfPacket();
		wifiConfPacket.status = status;

		if (wifiConfPacket.status != WIFIStatus.CONNECTED) {
			wifiConfPacket.encrypt = EncryptMethod.OPEN;
			wifiConfPacket.passwd = "";
			wifiConfPacket.ssid = "";
		} else {
			wifiConfPacket.encrypt = type;
			wifiConfPacket.ssid = ssid;
			wifiConfPacket.passwd = passwd;
		}

		return wifiConfPacket;
	}
	
	
	/**
	 * 构造自定义数据包
	 * @param customData 自定义数据
	 * @return
	 */
	public static MsgPacket obtainCustomPacket(byte[] customData){
		CustomPacket customPacket = new CustomPacket();
		customPacket.customData = customData;
		
		return customPacket;
	}
	
	/**
	 * 构造开始声音合成的请求数据包
	 * @param ttsText
	 * @return
	 */
	public static MsgPacket obtainTTSStartPacket(String ttsText){
		try {
			JSONObject content = new JSONObject();
			content.put("action", "start");
			content.put("text", ttsText);
			
			JSONObject ret = new JSONObject();
			ret.put("type", "tts");
			ret.put("content", content);
			
			ControlPacket controlPacket = new ControlPacket();
			controlPacket.controlCMD = ret.toString();
			
			return controlPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
	
	public static MsgPacket obtainTTSStopPacket(){
		try {
			JSONObject content = new JSONObject();
			content.put("action", "stop");
			
			JSONObject ret = new JSONObject();
			ret.put("type", "tts");
			ret.put("content", content);
			
			ControlPacket controlPacket = new ControlPacket();
			controlPacket.controlCMD = ret.toString();
			
			return controlPacket;
		} catch (JSONException e) {
			//ignore
			return null;
		}
	}
}
