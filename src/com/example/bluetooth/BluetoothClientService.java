package com.example.bluetooth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

public class BluetoothClientService extends Service{

	private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothCommunThread communThread;
	
	private BroadcastReceiver controlReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothTools.ACTION_START_DISCOVERY.equals(action))
			{
				discoveredDevices.clear();
				adapter.enable();
				adapter.startDiscovery();
			} else if(BluetoothTools.ACTION_SELECTED_DEVICE.equals(action))
			{
				//选择所选的设备
				BluetoothDevice device = (BluetoothDevice) intent.getExtras().get(BluetoothTools.DEVICE);
				//开启设备连接
				new BluetoothClientConnThread(handler,device);
			} else if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
				if(communThread!=null)
				{
					communThread.isRun=false;
				}
				stopSelf();
			} else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action))
			{
				Object data = intent.getSerializableExtra(BluetoothTools.DATA);
				communThread.writeObject(data);
			}
		}
		
	};
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(adapter.ACTION_DISCOVERY_STARTED.equals(action))
			{
				//donothing
			} else if(BluetoothDevice.ACTION_FOUND.equals(action))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(device);
				Intent deviceIntent = new Intent(BluetoothTools.ACTION_FOUND_SERVER);
				deviceIntent.putExtra(BluetoothTools.DEVICE, deviceIntent);
				sendBroadcast(deviceIntent);
			} else if(adapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{
				if(discoveredDevices.isEmpty())
				{
					Intent fountIntent = new Intent(BluetoothTools.ACTION_NOT_FOUND_SERVER);
					sendBroadcast(fountIntent);
				}
			}
		}
		
	};
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			  	case BluetoothTools.MESSAGE_CONNECT_ERROR:
			  		Intent errIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
			  		sendBroadcast(errIntent);
			  		break;
			  	case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
			  		communThread = new BluetoothCommunThread(handler,(BluetoothSocket)msg.obj);
			  		communThread.start();
			  		
			  		Intent successIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
			  		sendBroadcast(successIntent);
			  		break;
			  	case BluetoothTools.MESSAGE_READ_OBJECT:
			  		Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
			  		dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
			  		sendBroadcast(dataIntent);
			  		break;
			}
			super.handleMessage(msg);
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
		IntentFilter conterFilter = new IntentFilter();
		conterFilter.addAction(BluetoothTools.ACTION_START_DISCOVERY);
		conterFilter.addAction(BluetoothTools.ACTION_SELECTED_DEVICE);
		conterFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		conterFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);
		registerReceiver(controlReceiver, conterFilter);
		
		IntentFilter disFilter = new IntentFilter();
		disFilter.addAction(adapter.ACTION_DISCOVERY_STARTED);
		disFilter.addAction(adapter.ACTION_DISCOVERY_FINISHED);
		disFilter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(broadcastReceiver, disFilter);
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(communThread!=null)
		{
			communThread.isRun=false;
		}
		unregisterReceiver(controlReceiver);
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	
	
}
