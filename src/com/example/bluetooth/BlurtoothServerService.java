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
	
	//��action��ȡ���رշ���  �Լ����Ͱ�ť����Ϣ
	//hander ��ȡ���������߳�-�����߳�  ���ӳɹ�����ʧ��  �Լ� ͨ���ֳɵĶ�ȡ������
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
	
	//���������ֳɵ���Ϣ
	private Handler serviceHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
					//������������ͨ���³�
					communThread = new BluetoothCommunThread(serviceHandler,(BluetoothSocket)msg.obj);
					communThread.start();
					//�������ӳɹ���Ϣ
					Intent connSuccess = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
					sendBroadcast(connSuccess);
					break;
				case BluetoothTools.MESSAGE_CONNECT_ERROR:
					Intent connError = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
					sendBroadcast(connError);
					break;
				case BluetoothTools.MESSAGE_READ_OBJECT:
					//���Ͷ�ȡ������ ��������ʾ��ǰ̨
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
		//��������
		bluetoothAdapter.enable();
		//��������������30��
		Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
		discoverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(discoverIntent);
		//������̨�����߳�
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
