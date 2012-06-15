package roue.src;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.content.Context;

import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFBikeSpeedCadenceConnection;
import com.wahoofitness.api.data.WFBikeSpeedCadenceData;




public class CapteurWFBikeCadence extends CapteurWF {

	public CapteurWFBikeCadence() {
		mSensorType = WFSensorType.WF_SENSORTYPE_BIKE_SPEED_CADENCE;
		// TODO Auto-generated constructor stub
	}


	
	public enum BikeSpeedCadenceData {
		DEVICE_ID,
		SPEED,
		DISTANCE,
		CADENCE,
		CRANK_REVS,
		ACCUM_CRANK_REVS,
		CADENCE_TIME,
		ACCUM_CADENCE_TIME,
		WHEEL_REVS,
		ACCUM_WHEEL_REVS,
		SPEED_TIME,
		ACCUM_SPEED_TIME,
		SPEED_TIMESTAMP
	}

	private WFBikeSpeedCadenceConnection getBikeSpeedCadenceConnection() {
		WFBikeSpeedCadenceConnection retVal = null;
		if ( mConnection instanceof WFBikeSpeedCadenceConnection ) {
			retVal = (WFBikeSpeedCadenceConnection)mConnection;
		}
		
		return retVal;
	}
	
	public long getTour(){
		
		long tour = 0 ;
		WFBikeSpeedCadenceConnection bscConnection = getBikeSpeedCadenceConnection();
		if (bscConnection != null && bscConnection.isConnected()) {
		WFBikeSpeedCadenceData bscData = bscConnection.getBikeSpeedCadenceData();
		tour = bscData.accumWheelRevolutions;
		//tour = -1;
		}
		else 
		{
			tour = -1;
		}
		return tour;
	}
	public String updateDisplay() {
		
		String val = null ;
		WFBikeSpeedCadenceConnection bscConnection = getBikeSpeedCadenceConnection();
		if (bscConnection != null && bscConnection.isConnected()) {
			// display connection info.
			 val = "Device ID:  " + bscConnection.getDeviceNumber();
			
            // display standard CBSC data.
			WFBikeSpeedCadenceData bscData = bscConnection.getBikeSpeedCadenceData();
			if (bscData != null) {
				DecimalFormat df = new DecimalFormat("#.00");
				val = val +" Speed:  " + bscData.getFormattedSpeed(true); 
				val = val +" Dist:  " + bscData.getFormattedDistance(true); 
				val = val+" Cadence:  " + bscData.getFormattedCadence(true);
				
				val = val + "Cadence:  " + bscData.getFormattedCadence(true);
				/*printValue(BikeSpeedCadenceData.ACCUM_CRANK_REVS, "Accum C Revs:  " + bscData.accumCrankRevolutions);
				printValue(BikeSpeedCadenceData.ACCUM_CADENCE_TIME, "Accum C Time:  " + df.format(bscData.accumCadenceTime);*/
				val = val + "Accum W Revs:  " + bscData.accumWheelRevolutions;
				val = val + "Accum S Time:  " + df.format(bscData.accumSpeedTime);
				
				Date timestamp = new Date(bscData.speedTimestamp);
				SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
				val = val+" Time:  " + timeFormatter.format(timestamp);
			}
			else {
				/*
				printValue(BikeSpeedCadenceData.SPEED, "Speed:  n/a"); 
				printValue(BikeSpeedCadenceData.DISTANCE, "Dist:  n/a"); 
				printValue(BikeSpeedCadenceData.CADENCE, "Cadence:  n/a");
				printValue(BikeSpeedCadenceData.ACCUM_CRANK_REVS, "Accum C Revs:  n/a");
				printValue(BikeSpeedCadenceData.ACCUM_CADENCE_TIME, "Accum C Time:  n/a");
				printValue(BikeSpeedCadenceData.ACCUM_WHEEL_REVS, "Accum W Revs:  n/a");
				printValue(BikeSpeedCadenceData.ACCUM_SPEED_TIME, "Accum S Time:  n/a");
				*/
				val = "n/a";
			}
			
		}
		return val;
	}
}
