package roue.src;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CapteurAndroid {

	private SensorManager sensorManager;
	float angle, s1,s2,s3;
	String SENSOR_SERVICE;
	
	CapteurAndroid(String sensor_service)
	{
		SENSOR_SERVICE = sensor_service;
		//gyroscope = null ;
		//bousole = null ;
	}
	
	public void initialiser(Activity activite){
		 sensorManager = (SensorManager) activite.getSystemService(SENSOR_SERVICE);
	        sensorManager.registerListener(boussole, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);  
	        sensorManager.registerListener(gyroscope, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);
	}

	public SensorEventListener boussole = new SensorEventListener(){

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
			angle = arg0.values[0];
		}

    };

    public SensorEventListener gyroscope = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			s1 = event.values[0];
			s2 = event.values[1];
			s3 = event.values[2];
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};

}
