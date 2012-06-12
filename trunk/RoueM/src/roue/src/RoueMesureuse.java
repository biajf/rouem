package roue.src;

import android.app.Activity;
import roue.src.CapteurWF;

public class RoueMesureuse {
	
	private Activity activite ;
	private CapteurWF sensor1;
	
	RoueMesureuse(Activity activity){
		activite = activity;
	}
	
	
	public void initialiser(){
		
		sensor1 = new CapteurWF();
		//sensor1.connectCapteur();
		
	}
	
}
