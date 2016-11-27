package com.iflytek.aiui.uartkit.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.iflytek.aiui.uartkit.core.inter.ActionListener;
import com.iflytek.aiui.uartkit.core.inter.RequestListener;
import com.iflytek.aiui.uartkit.core.util.PacketUtil;
import com.iflytek.aiui.uartkit.core.util.UARTLogger;
import com.iflytek.aiui.uartkit.entity.DataPacket;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.entity.Packet;
import com.iflytek.aiui.uartkit.entity.UARTException;

public class UARTManager {
	private static final String TAG = "UART_Manager";

	private static final int SEND_RETRY_COUNT = 3;
	private static final long SEND_TIME_OUT = 300;

	private static UARTManager sManager;

	public static UARTManager getManager() {
		if (null == sManager) {
			sManager = new UARTManager();
		}
		return sManager;
	}


	private Queue<Integer> mReceivePool = new LinkedList<Integer>();
	private List<PacketEntity> mSendPool = new ArrayList<PacketEntity>();
	private ExecutorService mMainExecutor;
	private Thread mSendThread;
	private RequestListener mRequestListener;
	private boolean mIsRunning = false;

	private UARTManager() {
		
	}
	
	public void sendResponse(final DataPacket packet) {
		mMainExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				PacketEntity entity = new PacketEntity(packet);
				addToSendPool(entity);
			}
		});
	}
	
	public void sendRequest(final DataPacket packet, final ActionListener listener){
		mMainExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				PacketEntity entity = new PacketEntity(packet, listener);
				addToSendPool(entity);
			}
		});
	}
	
	
	

	public void init(final String deviceName, final int speed, final ActionListener listener) {
		mIsRunning = true;
		UARTConnector.setManager(this);
		mSendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				processSendPool();
			}
		}, "UART_Manager_Write_Thread");
		mSendThread.setDaemon(true);
		mSendThread.start();
		
		
		mMainExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread ret = new Thread(r, "UART_Manager_Main_Thread");
				ret.setDaemon(true);
				return ret;
			}
		});
		
		
		mMainExecutor.submit(new Runnable() {
			@Override
			public void run() {
				int init_status = UARTConnector.init(deviceName, speed);
				if (init_status != -1) {
					listener.onSuccess();
				} else {
					listener.onFailed(Error.OPEN_FAILED);
				}
			}
		});
	}

	public void setRequestListener(RequestListener listener) {
		mRequestListener = listener;
	}
	
	
	public void reset(){
		synchronized (mSendPool) {
			for(PacketEntity entity : mSendPool){
				if(entity.isHasAcked()){
					entity.onSuccess();
					continue;
				}
				
				entity.onFailed(Error.NO_ACK);
			}
			mSendPool.clear();
		}
		
		synchronized (mReceivePool) {
			mReceivePool.clear();
		}
	}

	public void destroy() {
		mMainExecutor.shutdownNow();
		mIsRunning = false;
		reset();
		// FIXME 线程取消未实现
		// UARTConnector.destroy();
	}

	public void onReceive(byte[] data) {
		if (data == null)
			return;
		
		UARTLogger.log(TAG, "recv data" + Arrays.toString(data));
		if (!DataPacket.isValid(data)) {
			UARTLogger.warn(TAG, "recv data is not valid, drop it !!!");
			return;
		}

		DataPacket dataPacket = new DataPacket();
		try {
			dataPacket.decodeBytes(data);
		} catch (UARTException e) {
			UARTLogger.err(TAG, "recv packet decode error" + e.getMessage());
			return;
		}
		final MsgPacket msgPacket = dataPacket.data;
		if (msgPacket.isReqType() && msgPacket.getMsgType() != MsgPacket.HANDSHAKE_REQ_TYPE && mReceivePool.contains(msgPacket.getSeqID())) {
			UARTLogger.warn(TAG, "recv same data, send ack drop it !!!");
			DataPacket ackPacket = PacketUtil.getAckMsg(msgPacket);
			sendResponse(ackPacket);
			return;
		}

		if (msgPacket.isReqType()) {
			addReceivePool(msgPacket.getSeqID());
			processReqPacket(msgPacket);
		} else {
			processAckPacket(msgPacket);
		}
		
	}

	private void processAckPacket(final MsgPacket msgPacket) {
		mMainExecutor.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (mSendPool) {
					Iterator<PacketEntity> iterator = mSendPool.iterator();
					while(iterator.hasNext()){
						PacketEntity entity = iterator.next();
						if(entity.getPacket().data.getSeqID() == msgPacket.getSeqID()){
							entity.setAcked();
							break;
						}
					}
				}
			}
		});
	}

	private void processReqPacket(final MsgPacket packet) {
		mMainExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (mRequestListener == null)
					return;
				DataPacket ackPacket = PacketUtil.getAckMsg(packet);
				sendResponse(ackPacket);
				mRequestListener.onReqeust(packet);
			}
		});
	}
	
	private void processSendPool(){
		while(mIsRunning){
			synchronized (mSendPool) {
				long minSleepTime = SEND_TIME_OUT;
				long currentTime = System.currentTimeMillis();
				Iterator<PacketEntity> iterator = mSendPool.iterator();
				while(iterator.hasNext()){
					PacketEntity entity = iterator.next();
					//已收到确认的消息
					if(entity.isHasAcked()){
						entity.onSuccess();
						iterator.remove();
						continue;
					}
					
					//重试次数超过的消息
					if(entity.getRetryCount() >= SEND_RETRY_COUNT){
						entity.onFailed(Error.NO_ACK);
						iterator.remove();
						continue;
					}
					
					//未收到确认重试次数也未超过的消息
					if(currentTime - entity.getLastSendTime() >= SEND_TIME_OUT){
						sendPacket(entity.getPacket());
						if(entity.isNeedAck()){
							entity.increaseRetryCoutn();;
							entity.setLastSendTime(currentTime);
						}else{
							iterator.remove();
						}
					}else{
						long sendInterval = SEND_TIME_OUT - (currentTime - entity.getLastSendTime());
						if(sendInterval < minSleepTime){
							minSleepTime = sendInterval;
						}
					}
				}
				
				try {
					mSendPool.wait(minSleepTime);
				} catch (InterruptedException e) {
					//ignore
				}
			}
		}
		
	}
	
	private void sendPacket(Packet packet){
		int retry_count = 0;
		do {
			UARTLogger.log(TAG, "send packet start");
			
			try {
				byte[] encodeBytes = packet.encodeBytes();
				int status = UARTConnector.send(encodeBytes);
				UARTLogger.log(TAG, "send packet bytes" + Arrays.toString(encodeBytes) + " status: " + status);
				if (status == 0) {
					break;
				}
			} catch (UARTException e) {
				UARTLogger.err(TAG, "send packet encode error " + e.getMessage());
				break;
			}
		} while (retry_count++ < 3);
	}
	
	private void addToSendPool(PacketEntity packetWrapper){
		synchronized (mSendPool) {
			mSendPool.add(packetWrapper);
			mSendPool.notify();
		}
	}
	
	private void addReceivePool(int seqID) {
		if (mReceivePool.size() > 200) {
			mReceivePool.remove();
		}
		mReceivePool.add(seqID);
	}

}


class PacketEntity {
	private DataPacket sendPacket = null;
	private boolean isNeedAck = false;
	private long lastSendTime = 0;
	private int retryCount = 0;
	private ActionListener callback = null;
	private boolean isHasAcked = false;

	public PacketEntity(DataPacket packet, ActionListener callback){
		this.sendPacket = packet;
		this.callback = callback;
		this.isNeedAck = true;
	}
	
	public PacketEntity(DataPacket packet){
		this.sendPacket = packet;
		this.isNeedAck = false;
	}
	
	public DataPacket getPacket(){
		return sendPacket;
	}
	
	public boolean isNeedAck(){
		return isNeedAck;
	}
	
	public boolean isHasAcked(){
		return isHasAcked;
	}
	
	public void setAcked(){
		isHasAcked = true;
	}
	
	public long getLastSendTime(){
		return lastSendTime;
	}
	
	public void setLastSendTime(long lastSendTime){
		this.lastSendTime = lastSendTime;
	}
	
	public void increaseRetryCoutn(){
		retryCount += 1;
	}
	
	public void resetRetryCount(){
		retryCount = 0;
	}
	
	public int getRetryCount(){
		return retryCount;
	}
	
	
	public void onSuccess(){
		if(callback != null){
			callback.onSuccess();
		}
	}
	
	public void onFailed(int error){
		if(callback != null) {
			callback.onFailed(error);
		}
	}
	
}
