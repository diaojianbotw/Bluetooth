package bluetoothUtil;

import java.util.UUID;

public class BluetoothTools {
	
	/**
	 * 本程序所使用的UUID
	 */
	public static final UUID PRIVATE_UUID = UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
	
	public static final String ACTION_DATA_TO_GAME = "ACTION_DATA_TO_GAME";
	
	public static final String DATA = "DATA";
	
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";
	
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";
	
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
	
	//点击事发送数据
	public static final String ACTION_DATA_TO_SERVICE = "ACTION_DATA_TO_SERVICE";
	
	public static final String ACTION_NOT_FOUND_SERVER = "ACTION_NOT_FOUND_DEVICE";
	
	public static final String ACTION_FOUND_SERVER = "ACTION_FOUND_SERVER";
	
	public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";
	
	public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";
	
	public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";
	
	public static final String DEVICE = "DEVICE";
	
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;
	
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;
	
	public static final int MESSAGE_READ_OBJECT = 0x00000004;
}
