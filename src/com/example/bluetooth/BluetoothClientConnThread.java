package com.example.bluetooth;

import java.io.IOException;

import bluetoothUtil.BluetoothTools;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class BluetoothClientConnThread extends Thread{

	private Handler handler;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	
	public BluetoothClientConnThread(Handler handler,BluetoothDevice deviceBlue)
	{
		this.handler = handler;
		this.device = deviceBlue;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		try {
			socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
			socket.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			handler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			return;
		}
		Message msg = handler.obtainMessage();
		msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
		msg.obj = socket; 
		msg.sendToTarget();
	}

	
}
