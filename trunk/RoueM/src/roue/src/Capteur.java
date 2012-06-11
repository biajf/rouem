package roue.src;


import android.content.Context;
import android.util.AttributeSet;

import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFBikeSpeedConnection;
import com.wahoofitness.api.comm.WFConnectionParams;
import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.Callback;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;
import com.wahoofitness.api.data.WFBikeSpeedData;
public class Capteur implements  WFSensorConnection.Callback {
	
	protected WFHardwareConnector mHardwareConnector;
	protected WFSensorConnection mConnection;
	protected short mSensorType;
	
	public Capteur()
	{
		mSensorType = WFSensorType.WF_SENSORTYPE_BIKE_CADENCE;
	}
	
	public WFSensorConnectionStatus getState() {
		WFSensorConnectionStatus retVal = WFSensorConnectionStatus.WF_SENSOR_CONNECTION_STATUS_IDLE;
		if (mConnection != null) {
			retVal = mConnection.getConnectionStatus();
		}
		return retVal;
	}
	
	public boolean connect()
	{
		boolean retVal = (mHardwareConnector != null);
		
		if (retVal) {
			// set the button state based on the connection state.
			switch ( getState() )
			{
				case WF_SENSOR_CONNECTION_STATUS_IDLE:
				{
	        		WFConnectionParams connectionParams = new WFConnectionParams();
	        		connectionParams.sensorType = mSensorType;
	        		mConnection = mHardwareConnector.initSensorConnection(connectionParams);
	        		if (mConnection != null) {
	        			mConnection.setCallback(this);
	        		}
					break;
				}
					
				case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
				case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
					break;
					
				case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
					retVal = false;
					break;
			}
		}
		
	return retVal;
	}

	public void connectionStateChanged(WFSensorConnectionStatus arg0) {
		if ( mConnection != null && !mConnection.isValid() ) {
			mConnection.setCallback(null);
			mConnection = null;
		}
		
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
}
