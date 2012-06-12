package roue.src;

import android.content.Context;


import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFFootpodConnection;
import com.wahoofitness.api.data.WFFootpodData;



public class CapteurWFFoot extends CapteurWF {

	public CapteurWFFoot(Context context) {
		super(context);
		mSensorType = WFSensorType.WF_SENSORTYPE_FOOTPOD;
	}

	public enum FootData {
		DEVICE_ID,
		SPEED,
		PACE,
		DISTANCE,
		STRIDE_COUNT,
		CADENCE
	};
	
	public WFFootpodConnection getFootpodConnection() {
		WFFootpodConnection retVal = null;
		if ( mConnection instanceof WFFootpodConnection ) {
			retVal = (WFFootpodConnection)mConnection;
		}
		
		return retVal;
	}
	
	public String updateDisplay() {
		
		WFFootpodConnection footpodConnection = getFootpodConnection();
		String val = null ;
		if (footpodConnection != null && footpodConnection.isConnected()) {
			// display connection info.
			val = "Device ID:  " + footpodConnection.getDeviceNumber() ; 
			
            // display standard SDM data.
			WFFootpodData fpData = footpodConnection.getFootpodData();
			if (fpData != null) {
				val = val + "Speed:  " + fpData.getFormattedSpeed(true);
			    val = val + "Pace:  " + fpData.getFormattedPace(true); 
				val = val + "Distance:  " + fpData.getFormattedDistance(true); 
				val = val + "Strides:  " + fpData.accumulatedStride; 
				val = val + "Cadence:  " + fpData.getFormattedCadence(true);
			}
		}
		return val;
	}
}
