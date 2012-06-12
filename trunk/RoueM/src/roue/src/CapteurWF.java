package roue.src;


import android.content.Context;
import android.util.AttributeSet;

import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFConnectionParams;
import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;



public class CapteurWF implements  WFSensorConnection.Callback {

	private WFConnectionParams connectParams;
	private WFSensorConnection mConnection;
	private WFHardwareConnector mHardwareConnector;
	
	public CapteurWF()
	{
		connectParams = null ;
		mConnection = null ;
		mHardwareConnector = null ;
		
	}
	
	public void connectionStateChanged(WFSensorConnectionStatus arg0) {
		
		
	}
	
	public void connectCapteur(){
		
		connectParams = new WFConnectionParams();
		connectParams.sensorType = WFSensorType.WF_SENSORTYPE_BIKE_CADENCE;
		mConnection = mHardwareConnector.initSensorConnection(connectParams);
		if (mConnection != null) {
			mConnection.setCallback(this);
		}
		boolean retVal = mConnection.isConnected();
		if (!retVal)
		{
			new Exception("Connection Echouée");
		}
	}
}
