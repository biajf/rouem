package roue.src;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CapteurAndroid {

	private SensorManager sensorManager;
	float angle;
	float[] valeurs;
	String SENSOR_SERVICE;
	
	CapteurAndroid(String sensor_service)
	{
		SENSOR_SERVICE = sensor_service;
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
			valeurs = event.values;
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public float[] getgyroscope(){
		return valeurs;
	}
	
	public float getboussole(){
		//Azimut entre l'axe y et le nord
		return angle;
	}
	

}
