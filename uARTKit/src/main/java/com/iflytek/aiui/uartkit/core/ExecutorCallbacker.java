package com.iflytek.aiui.uartkit.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.iflytek.aiui.uartkit.constant.UARTConstant;
import com.iflytek.aiui.uartkit.core.inter.Callbacker;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.listener.EventListener;
import com.iflytek.aiui.uartkit.listener.UARTEvent;

public class ExecutorCallbacker implements Callbacker {
	private ExecutorService mCallbackWorker;

	public ExecutorCallbacker(final String workerName) {
		mCallbackWorker = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, "callback_thread");
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	@Override
	public void notifyRequest(final EventListener listener, final MsgPacket packet) {
		postCallback(new Runnable() {

			@Override
			public void run() {
				listener.onEvent(new UARTEvent(UARTConstant.EVENT_MSG, packet));
			}
		});
	}

	@Override
	public void notifyEvent(final EventListener listener, final UARTEvent event) {
		postCallback(new Runnable() {

			@Override
			public void run() {
				listener.onEvent(event);
			}
		});
	}

	private void postCallback(Runnable r) {
		mCallbackWorker.execute(r);
	}

	@Override
	public void destroy() {
		mCallbackWorker.shutdownNow();
	}
}
