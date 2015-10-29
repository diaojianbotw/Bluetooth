package com.example.bluetooth;

import java.io.Serializable;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import bluetoothUtil.BluetoothCommunThread;
import bluetoothUtil.BluetoothTools;

public class BlurtoothServerService extends Service{

	private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	private BluetoothCommunThread communThread;
	
	//从action获取到关闭服务  以及发送按钮的消息
	//hander 获取的是其他线程-连接线程  连接成功连接失败  以及 通信现成的读取到数据
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothTools.ACTION_STOP_SERVICE.equals(action))
			{
				if(communThread!=null)
				{
					communThread.isRun=false;
				}
				stopSelf();
			}else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action))
			{
				Object data = intent.getSerializableExtra(BluetoothTools.DATA);
				if(communThread!=null)
				{
					communThread.writeObject(data);
				}
			}
		}
	};
	
	//接受其他现成的消息
	private Handler serviceHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
					//连接启东开启通信新城
					communThread = new BluetoothCommunThread(serviceHandler,(BluetoothSocket)msg.obj);
					communThread.start();
					//发送连接成功消息
					Intent connSuccess = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
					sendBroadcast(connSuccess);
					break;
				case BluetoothTools.MESSAGE_CONNECT_ERROR:
					Intent connError = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
					sendBroadcast(connError);
					break;
				case BluetoothTools.MESSAGE_READ_OBJECT:
					//发送读取到数据 将数据显示到前台
					Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
					dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(dataIntent);
			}
		}
		
		
	};
			
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);
		registerReceiver(broadcastReceiver, intentFilter);
		//开启蓝牙
		bluetoothAdapter.enable();
		//开启蓝牙被发现30秒
		Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
		discoverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(discoverIntent);
		//开启后台连接线程
		new BluetoothServerConnThread(serviceHandler).start();
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(communThread!=null)
		{
			communThread.isRun=false;
		}
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	
	
}
