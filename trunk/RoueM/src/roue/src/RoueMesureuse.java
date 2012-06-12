package roue.src;

import android.app.Activity;
import roue.src.CapteurWF;

public class RoueMesureuse {
	
	private Activity activite ;
	private CapteurWF sensor1;
	private CapteurAndroid sensor2;
	
	RoueMesureuse(Activity activity){
		activite = activity;
	}
	
	
	public void initialiser(String sensor_service){
		
		sensor2 = new CapteurAndroid(sensor_service);
		//sensor1 = new CapteurWF();
		//sensor1.connectCapteur();
		
	}
	
}
