package roue.src;

import android.content.Context;

import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFBikeCadenceConnection;
import com.wahoofitness.api.comm.WFBikeSpeedConnection;
import com.wahoofitness.api.comm.WFFootpodConnection;
import com.wahoofitness.api.data.WFBikeCadenceData;
import com.wahoofitness.api.data.WFFootpodData;



public class CapteurWFBikeSpeed extends CapteurWF {

	public CapteurWFBikeSpeed(Context context) {
		super(context);
		mSensorType = WFSensorType.WF_SENSORTYPE_FOOTPOD;
		// TODO Auto-generated constructor stub
	}

	public enum BikeCadenceData {
		DEVICE_ID,
		CADENCE,
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
	
	public void updateDisplay() {
		
		WFFootpodConnection footpodConnection = getFootpodConnection();
		if (footpodConnection != null && footpodConnection.isConnected()) {
			// display connection info.
			//printValue(FootData.DEVICE_ID, "Device ID:  " + footpodConnection.getDeviceNumber()); 
			
            // display standard SDM data.
			WFFootpodData fpData = footpodConnection.getFootpodData();
			if (fpData != null) {
			/*	printValue(FootData.SPEED, "Speed:  " + fpData.getFormattedSpeed(true)); 
				printValue(FootData.PACE, "Pace:  " + fpData.getFormattedPace(true)); 
				printValue(FootData.DISTANCE, "Distance:  " + fpData.getFormattedDistance(true)); 
				printValue(FootData.STRIDE_COUNT, "Strides:  " + fpData.accumulatedStride); 
				printValue(FootData.CADENCE, "Cadence:  " + fpData.getFormattedCadence(true));*/
			}
		}
	}
}
