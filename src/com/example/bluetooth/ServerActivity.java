package com.example.bluetooth;

import java.util.Date;

import android.app.Activity;
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

public class ServerActivity extends Activity{

	private TextView serverStateText;
	private EditText serverEditText;
	private EditText serverSendEditText;
	private Button serverSendMsgBtn;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			//从后台server传出来的数据将数据显示到画面上
			if(BluetoothTools.ACTION_DATA_TO_GAME.equals(action))
			{
				TransmitBean data = (TransmitBean) intent.getExtras().getSerializable(BluetoothTools.DATA);
				String msg = "from remote "+String.valueOf(new Date())+" \r\n"+data.getMsg()+"\r\n";
				serverEditText.setText(msg);
			} else if(BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action))
			{
				//连接成功
				serverStateText.setText("连接成功");
				serverSendMsgBtn.setEnabled(true);
			} else if(BluetoothTools.ACTION_CONNECT_ERROR.equals(action))
			{
				//连接失败
				serverStateText.setText("连接失败");
			}
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		serverStateText = (TextView) findViewById(R.id.serverstateText);
		serverStateText.setText("正在连接...");
		serverEditText = (EditText) findViewById(R.id.serverEditText);
		serverSendEditText = (EditText) findViewById(R.id.serverSendEditText);
		serverSendMsgBtn = (Button) findViewById(R.id.serverSendMsgBtn);
		serverSendMsgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if("".endsWith(serverSendEditText.getText().toString().trim()))
				{
					Toast.makeText(ServerActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
				} else
				{
					TransmitBean data = new TransmitBean();
					data.setMsg(serverSendEditText.getText().toString());
					Intent intent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
					intent.putExtra(BluetoothTools.DATA, data);
					sendBroadcast(intent);
				}
				
			}
		});
		serverSendMsgBtn.setEnabled(false);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Intent startService = new Intent(ServerActivity.this,BlurtoothServerService.class);
		startService(startService);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);
		registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
		sendBroadcast(intent);
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}
	
	
}
