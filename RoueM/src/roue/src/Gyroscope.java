package roue.src;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Gyroscope {

	private Odometre roue;
	private boolean pris_en_compte=true;
	private SensorManager sensorManager;
	private int type_capteur;
	private RoueMActivity activity;
	
	Gyroscope(int service, RoueMActivity act, Odometre r)
	{
		type_capteur = service;
		activity = act;
		roue = r;
	}
	
	public void initialiser(){
	 	sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        sensorManager.registerListener(capteur, sensorManager.getDefaultSensor(type_capteur),SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("gyro1", "cc");
	}
	
    public SensorEventListener capteur = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			Log.d("gyro", "cc");
			float degree = event.values[2];

	    	if(degree > 2){
				activity.rotation(-90);
				roue.directionChange("Gauche", pris_en_compte);
				pris_en_compte = false;
			}					
			else if(degree < -2){
				activity.rotation(90);
				roue.directionChange("Droite", pris_en_compte);
				pris_en_compte = false;
			}								
			else{
				activity.rotation(0);
				pris_en_compte = true;
			}			
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
    public void disconnect(){
		sensorManager.unregisterListener(capteur, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
	}
	
}
