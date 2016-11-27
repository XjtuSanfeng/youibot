package com.iflytek.aiui.uartkit.core.util;

import android.util.Log;

public class UARTLogger {
	public static final boolean DEBUG = true;
	public static final void log(String tag, String msg){
		if(DEBUG){
//			Log.d(tag, System.currentTimeMillis() + " " + msg);
			Log.d(tag, msg);
		}
	}
	
	public static final void warn(String tag, String msg){
		if(DEBUG){
//			Log.w(tag, System.currentTimeMillis() + " " + msg);
			Log.w(tag, msg);
		}
	}
	
	public static final void err(String tag, String msg){
		if(DEBUG){
//			Log.e(tag, System.currentTimeMillis() + " " + msg);
			Log.e(tag, msg);
		}
	}
}
