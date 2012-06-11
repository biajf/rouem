package roue.src;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RoueMActivity extends Activity implements SensorEventListener {
	
	boolean compte = true;
	private SensorManager sensorManager;

	float distparcouru = 0;
	float s1, s2, s3;
	EditText distance = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);  
        distance = (EditText) findViewById(R.id.distance);
        sensorManager.getOrientation(R, values);
    }

    public void start(View v) throws InterruptedException {
    	compte = true;
    	while(compte){
    		distparcouru = distance(2*(float)(Math.PI*0.4)) + distparcouru;
    		distance.setText(Float.toString(distparcouru));
    		Thread.sleep(10000);
    		
    	}
    	//tToast("Start");
    }
    
    public void pause(View v) {
    	tToast("Pause");
    }
    
    public void stop(View v) {
    	compte = false;
    	tToast("Stop");
    
    }
    
    public void reset(View v) {
    	tToast("Reset");
    }

    private void tToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG/2;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
    
    public float distance(float circonference){
    	double RPM = Math.random()*10; 
    	
    	return (Math.abs((int)RPM)*circonference);
    }

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		s1 = event.values[0];
		s2 = event.values[1];
		s3 = event.values[2];
	}

}