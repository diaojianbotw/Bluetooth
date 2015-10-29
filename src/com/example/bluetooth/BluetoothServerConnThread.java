package com.example.bluetooth;

import java.io.IOException;

import bluetoothUtil.BluetoothTools;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class BluetoothServerConnThread extends Thread{

	private Handler serviceHandler;
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private BluetoothServerSocket serviceSocket;
	
	public BluetoothServerConnThread(Handler handler)
	{
		this.serviceHandler = handler;
		adapter.getDefaultAdapter();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			serviceSocket = adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			socket = serviceSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
			return;
		}
		finally{
			try {
				serviceSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(socket!=null)
		{
			Message msg = serviceHandler.obtainMessage();
			msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
			msg.obj = socket;
			msg.sendToTarget();
		}
		else
		{
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			return;
		}
		
	}

	
}
