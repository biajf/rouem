package roue.src;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wahoofitness.api.WFAntException;
import com.wahoofitness.api.WFAntNotSupportedException;
import com.wahoofitness.api.WFAntServiceNotInstalledException;
import com.wahoofitness.api.WFDisplaySettings;
import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnector.Callback;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFConnectionParams;
import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;



public class CapteurWF extends LinearLayout implements WFSensorConnection.Callback {

	protected WFHardwareConnector mHardwareConnector;
	protected WFSensorConnection mConnection;
	protected short mSensorType;
	
	public CapteurWF(Context context) {
		super(context);
		//defaultSettings(context);
	}

	public CapteurWF(Context context, AttributeSet attrs) {
		super(context, attrs);
		//defaultSettings(context);
	}
	
	public WFSensorConnectionStatus getState() {
		WFSensorConnectionStatus retVal = WFSensorConnectionStatus.WF_SENSOR_CONNECTION_STATUS_IDLE;
		if (mConnection != null) {
			retVal = mConnection.getConnectionStatus();
		}
		return retVal;
	}
	
protected WFSensorConnectionStatus connectSensor() {
		
		boolean retVal = (mHardwareConnector != null);
		WFSensorConnectionStatus Tyty = getState() ;
		if (retVal) {
			
			// set the button state based on the connection state.
			switch ( getState() )
			{
				case WF_SENSOR_CONNECTION_STATUS_IDLE:
				{
					
	        		WFConnectionParams connectionParams = new WFConnectionParams();
	        		connectionParams.sensorType = mSensorType;
	        		//connectionParams.device1 = new WFDeviceParams((short)100, (byte)1);
	        		//connectionParams.device2 = new WFDeviceParams((short)200, (byte)1);
	        		mConnection = mHardwareConnector.initSensorConnection(connectionParams);
	        		if (mConnection != null) {
	        			mConnection.setCallback(this);
	        			
	        		}
					break;
				}
					
				case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
				case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
					// do nothing.
					break;
					
				case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
					retVal = false;
					break;
			}
		}
		
		return getState();
	}
		
protected boolean disconnectSensor() {
	
	boolean retVal = (mConnection != null);
	
	if (retVal) {
		// set the button state based on the connection state.
		switch ( getState() )
		{
			case WF_SENSOR_CONNECTION_STATUS_IDLE:
			{
				if (mConnection != null) {
					mConnection.setCallback(null);
					mConnection = null;
				}
				break;
			}
				
			case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
			case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
			case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
        		mConnection.disconnect();
				break;
		}
	}
	
    return retVal;
}	
	
	public void initControl(WFHardwareConnector hwConn) {
		mHardwareConnector = hwConn;
	}
	
	public void connectionStateChanged(WFSensorConnectionStatus connState) {
		if ( mConnection != null && !mConnection.isValid() ) {
			mConnection.setCallback(null);
			mConnection = null;
		}
	
	
	}



}
