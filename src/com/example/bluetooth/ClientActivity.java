package com.example.bluetooth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bluetoothUtil.BluetoothTools;
import bluetoothUtil.TransmitBean;

public class ClientActivity extends Activity {

	private TextView serversText;
	private EditText chatEditText;
	private EditText sendEdittext;
	private Button sendBtn;
	private Button startDiscover;
	private Button selectDevice;
	
	private List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothTools.ACTION_NOT_FOUND_SERVER.equals(action))
			{
				serversText.setText("未发现设备");
			} else if(BluetoothTools.ACTION_FOUND_DEVICE.equals(action))
			{
				BluetoothDevice device = (BluetoothDevice) intent.getExtras().get(BluetoothTools.DEVICE);
				devicelist.add(device);
				serversText.append(device.getName()+"\r\n");
			} else if(BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action))
			{
				serversText.setText("连接成功");
				sendBtn.setEnabled(true);
			} else if(BluetoothTools.ACTION_CONNECT_ERROR.equals(action))
			{
				serversText.setText("连接失败");
				sendBtn.setEnabled(false);
			} else if(BluetoothTools.ACTION_DATA_TO_GAME.equals(action))
			{
				TransmitBean data = new TransmitBean();
				data = (TransmitBean) intent.getExtras().getSerializable(BluetoothTools.DATA);
				String msg = "from remote "+String.valueOf(new Date())+"\r\n"+data.getMsg()+"\r\n";
				chatEditText.append(msg);
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.client);
		 serversText = (TextView) findViewById(R.id.clientServersText);
		 chatEditText = (EditText) findViewById(R.id.clientChatEditText);
		 sendEdittext = (EditText) findViewById(R.id.clientSendEditText);
		 sendBtn = (Button) findViewById(R.id.clientSendMsgBtn);
		 startDiscover = (Button) findViewById(R.id.startSearchBtn);
		 selectDevice = (Button) findViewById(R.id.selectDeviceBtn);
		 
		 sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if("".equals(sendEdittext.getText().toString().trim()))
				{
					Toast.makeText(ClientActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
				}else
				{
					TransmitBean data = new TransmitBean();
					data.setMsg(sendEdittext.getText().toString().trim());
					Intent sendIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
					sendIntent.putExtra(BluetoothTools.DATA, data);
					sendBroadcast(sendIntent);
				}
				
			}
		});
		 startDiscover.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(BluetoothTools.ACTION_START_DISCOVERY);
				sendBroadcast(startIntent);
				
			}
		});
		 selectDevice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent selectIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
				selectIntent.putExtra(BluetoothTools.DEVICE, devicelist.get(0));
				sendBroadcast(selectIntent);
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		devicelist.clear();
		Intent serverIntent = new Intent(ClientActivity.this,BluetoothClientService.class);
		startService(serverIntent);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_NOT_FOUND_SERVER);
		intentFilter.addAction(BluetoothTools.ACTION_FOUND_DEVICE);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Intent stopIntent = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
		sendBroadcast(stopIntent);
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	
}
