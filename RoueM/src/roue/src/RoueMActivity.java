package roue.src;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class RoueMActivity extends Activity {
	
	private Bundle save;
	
	//Variables Graphiques
	private TextView resultataff;
	private TextView distance;
	private Button bstart, bpause, breset, bstop = null;
	private RadioGroup sens = null; 
	
	//Gestion de la pause et stop
	boolean appstart = false;
	boolean pause;
	
	// Definition de la pause
	RoueMesureuse roue ;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        resultataff = (TextView)findViewById(R.id.resultat);
	        distance = (TextView)findViewById(R.id.distance);
	        bstart = (Button)findViewById(R.id.start);
	        bpause = (Button)findViewById(R.id.pause);
	        breset = (Button)findViewById(R.id.reset);
	        bstop = (Button)findViewById(R.id.stop);
	        sens = (RadioGroup)findViewById(R.id.radioGroup1);

	        // Version avec Roue Mesureuse
	        
	        roue = new RoueMesureuse(this, save, sens,resultataff,distance);
	       
	 }
	 
	 public void start(View v){
     	
     	changedBoutton(false, true, true, true);
		 roue.init(getBaseContext()); 
		 resultataff.setText("");
		 appstart = true ;
	 }
	 
	 public void stop(View v)
	 {
		changedBoutton(true, false, false, true);
	     	
		 if(appstart)
		 {
		
		 roue.result();
		 }
		 
	 }
	 
	 public void pause(View v){
		 
		if(appstart){
			pause = roue.gestpause(pause);
		}
	 }
	 
	 public void reset(View v){
					
		 if(appstart)
		 {
			 roue.reset(this);
		 }
		 
	 }

	public void changedBoutton(boolean start, boolean stop, boolean pause, boolean reset){
		bpause.setEnabled(pause);
     	bstop.setEnabled(stop);
     	breset.setEnabled(reset);
     	bstart.setEnabled(start);
	}
}