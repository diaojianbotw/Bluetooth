package bluetoothUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class BluetoothCommunThread extends Thread{

	private Handler serviceHandler;
	private BluetoothSocket socket;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	public volatile boolean isRun = true;
	
	public BluetoothCommunThread(Handler handler,BluetoothSocket socket)
	{
		this.serviceHandler = handler;
		this.socket = socket;
		try {
			this.outStream = new ObjectOutputStream(socket.getOutputStream());
			this.inStream =  new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
			
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			if(!isRun)
			{
				break;
			}
			else
			{
				try {
					Object object= inStream.readObject();
					Message message= serviceHandler.obtainMessage();
					message.what = BluetoothTools.MESSAGE_READ_OBJECT;
					message.obj = object;
					message.sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
					e.printStackTrace();
				} 
			}
		}
		
		if(inStream!=null)
		{
			try {
				inStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(outStream!=null)
		{
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(socket!=null)
		{
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeObject(Object obj)
	{
		try {
			outStream.flush();
			outStream.writeObject(obj);
			outStream.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
