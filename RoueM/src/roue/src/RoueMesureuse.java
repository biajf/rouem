package roue.src;

import java.util.ArrayList;
import java.util.List;

import com.wahoofitness.api.WFAntException;
import com.wahoofitness.api.WFAntNotSupportedException;
import com.wahoofitness.api.WFAntServiceNotInstalledException;
import com.wahoofitness.api.WFDisplaySettings;
import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;
import com.wahoofitness.api.comm.WFSensorConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;


public class RoueMesureuse implements WFHardwareConnector.Callback {
	
	private RadioGroup sens = null; 
	private TextView resultataff;
	private TextView distance;
	
	//Variables Capteurs
	private WFHardwareConnector mHardwareConnector;
	private static final String TAG = "Roue";
	private CapteurWFBikeCadence sensor2;
	private SensorManager sensorManager;
	
	//Variables de Mesures
	private boolean pris_en_compte=true;
	float distanceparcourue = 0;
	float angle = 0;	
	String mesure ="" ;
	
	// Enregistrement résultat 
	List<String> resultat = new ArrayList<String>() ;
	
	// Calibration 
	
	//Debug
	String cir ="0" ;
	private float circonference = (float)1.5 ;
	
	//Gestion de la pause et stop
	boolean appstart = false;
	boolean pause;
	float distancepause = 0;
	long tourpause;
	
	//Activite 
	private Activity activity;
	private Bundle save;
	
	
	public RoueMesureuse(Activity act,Bundle saved,RadioGroup sen,TextView resultattext,TextView distancetext) {
		activity = act ;
		save = saved ;
		sens = sen ;
		resultataff = resultattext;
		distance = distancetext;
		// TODO Auto-generated constructor stub
	}
	
    ///////////////////////////////////////////////////////////////////////////
	//
	// WFHardwareConnector.Callback Implementation
    @Override
	public void hwConnAntError(WFAntError error) {
		switch (error) {
			case WF_ANT_ERROR_CLAIM_FAILED:
	        	//mAntStateText.setVisibility(TextView.VISIBLE);
	        	//mAntStateText.setText("ANT radio in use.");
				//mHardwareConnector.forceAntConnection(getResources().getString(R.string.app_name));
				break;
		}
	}
	
   @Override
	public void hwConnConnectionRestored() {
    	//mBikeCadence.restoreConnectionState();
    }

	@Override
	public void hwConnConnectedSensor(WFSensorConnection sensorConnection) {
//		mTextDevId.setText(Integer.toString(sensorConnection.getDeviceNumber()));
	}

	@Override
	public void hwConnDisconnectedSensor(WFSensorConnection sensorConnection) {
		
	}

	public void hwConnStateChanged(WFHardwareState state, Context context) {
		switch (state) {
			case WF_HARDWARE_STATE_DISABLED:
	        	//mAntStateText.setVisibility(TextView.VISIBLE);
	        	if (WFHardwareConnector.hasAntSupport(context)) {
	        		//mAntStateText.setText("HW Connector DISABLED.");
	        	}
	        	else {
	        		//mAntStateText.setText("ANT Radio NOT supported.");
	        	}
				break;
				
			case WF_HARDWARE_STATE_SERVICE_NOT_INSTALLED:
	        	//mAntStateText.setVisibility(TextView.VISIBLE);
	        	//mAntStateText.setText("ANT Radio Service NOT installed.");
				break;
				
			case WF_HARDWARE_STATE_SUSPENDED:
	        	//mAntStateText.setVisibility(TextView.VISIBLE);
	        	//mAntStateText.setText("HW Connector SUSPENDED.");
	        	break;
	        	
			case WF_HARDWARE_STATE_READY:
			default:
	        	//mAntStateText.setVisibility(TextView.INVISIBLE);
				break;
		}
	}
    
    ///////////////////////////////////////////////////////////////////////////
	//
	
	public void initControls(){
		
		sensor2 = new CapteurWFBikeCadence();
		sensor2.initControl(mHardwareConnector);
	}
	  
	
	public float distance(long tour)
	{
		if(tour != -1)
		{
		return tour*circonference - distancepause;
		}
		else return 0;
	}
	
	public void directionChange(String direction, boolean pris_en_compte)
	{
		if(pris_en_compte &! pause){
			float tmp = distance(sensor2.getTour()) ;
			if(direction == "Droite")
			{
			 mesure += "\t"+tmp+"m\n"+"	Droite\n";
			}
			else if(direction == "Gauche")
			{
			 mesure += "\t"+tmp+"m\n"+"	Gauche\n";	
			}
			else
			{
			mesure += tmp+"m\n";
			}
		}
		
	}
	

	 public SensorEventListener gyroscope = new SensorEventListener(){

			public void onAccuracyChanged(Sensor arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			public void onSensorChanged(SensorEvent arg0) {
				// TODO Auto-generated method stub
		    	float degree = arg0.values[2];

		    	if(degree > 2){
					sens.check(R.id.ouest);	
					directionChange("Gauche", pris_en_compte);
					pris_en_compte = false;
				}					
				else if(degree < -2){
					sens.check(R.id.est);
					directionChange("Droite", pris_en_compte);
					pris_en_compte = false;
				}								
				else{
					sens.check(R.id.nord);
					pris_en_compte = true;
				}				
			}

	    };
	    
	    public void antConnect(Context context,Bundle savedInstanceState)
		{
			if (WFHardwareConnector.hasAntSupport(context)) {
	        	
		        try {
		        	boolean bResumed = false;
		        	
		        	// attempt to retrieve the previously suspended WFHardwareConnector instance.
		        	//
		        	// see the onRetainNonConfigurationInstance method.
		        	mHardwareConnector = (WFHardwareConnector)activity.getLastNonConfigurationInstance();
		        	if (mHardwareConnector != null) {
		        		// attempt to resume the WFHardwareConnector instance.
		        		if (!(bResumed = mHardwareConnector.resume(this))) {
		        			// if the WFHardwareConnector instance failed to resume,
		        			// it must be re-initialized.
		        			mHardwareConnector.connectAnt();
		        		}
		        	}
		        	
		        	// if there is no suspended WFHardwareConnector instance,
		        	// configure the singleton instance.
		        	else {
				         // get the hardware connector singleton instance.
				        mHardwareConnector = WFHardwareConnector.getInstance(activity, this);
						mHardwareConnector.connectAnt();
		        	}
			        
			        // restore connection state only if the previous
			        // WFHardwareConnector instance was not resumed.
			        if (!bResumed) {
				        // the connection state is cached in the state
				        // bundle (onSaveInstanceState).  this is used to
				        // restore previous connections.  if the Bundle
				        // is null, no connections are configured.
				        mHardwareConnector.restoreInstanceState(savedInstanceState);
			        }
			        
			        // configure the display settings.
			        //
			        // this demonstrates how to use the display
			        // settings.  if this step is skipped, the
			        // default settings will be used.
			        WFDisplaySettings settings = mHardwareConnector.getDisplaySettings();
			        settings.staleDataTimeout = 5.0f;          // seconds, default = 5
			        settings.staleDataString = "--";           // string to display when data is stale, default = "--"
			        settings.useMetricUnits = true;            // display metric units, default = false
			        settings.bikeWheelCircumference = 2.07f;   // meters, default = 2.07
			        settings.bikeCoastingTimeout = 3.0f;       // seconds, default = 3    
			        mHardwareConnector.setDisplaySettings(settings);
			        
			       
		        }
		        catch (WFAntNotSupportedException nse) {
		        	// ANT hardware not supported.
		        	
		        	alert("ANT not supported.");
		        }
		        catch (WFAntServiceNotInstalledException nie) {

					//Toast installNotification = Toast.makeText(context, this.getResources().getString( R.string.Notify_Service_Required), Toast.LENGTH_LONG);
					//installNotification.show();

					// open the Market Place app, search for the ANT Radio service.
					mHardwareConnector.destroy();
					mHardwareConnector = null;
					WFHardwareConnector.installAntService(context);

					// close this app.
					activity.finish();
		        }
				catch (WFAntException e) {
					
					alert( "ANT initialization error.");
				}
	       }
	        else {
	        	// ANT hardware not supported.
	        	
	        	alert("ANT not supported.");
	        }
	        
	        if(!activity.isFinishing())
	        {
	            //setContentView(R.layout.landscape_grid);
	            initControls();
	        	//mAntStateText.setVisibility(statusVisibility);
	        	//mAntStateText.setText(statusText);
	        }
		}
	    
	    
	void init(Context context){
		 sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
	     sensorManager.registerListener(gyroscope, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);  
		 antConnect(context, save);
		 Log.d(TAG, "init");
	}

	@Override
	public void hwConnHasData() {
		Log.d(TAG, "hwConnHasData");
		//sensor1.connectSensor();
		sensor2.connectSensor();
		distance.setText( "Distance courante :"+ distance(sensor2.getTour())+"m\n");
		resultataff.setText(mesure);
		
	}
	
	public void alert(String msg)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setTitle("Erreur");
		alertDialog.setMessage(msg);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
		      // here you can add functions
		   }
		});
		alertDialog.show();
	}

	@Override
	public void hwConnStateChanged(WFHardwareState arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void result()
	{
		 mesure += distance(sensor2.getTour())+"m\n" ;
		 sensor2.disconnectSensor();
		 mHardwareConnector.destroy();
		 distancepause = 0 ;
		 resultat.add(mesure);
		 String tmp = "" ;
		 for(int i=0; i<resultat.size(); i++)
		 {
		 tmp += "Mesure "+i + " :\n" +"\t"+resultat.get(i).toString();
		 }
		 distance.setText("Appuyer sur Start");
		 resultataff.setText(tmp);
		 mesure = "" ;
	}
	public boolean gestpause(Boolean pause){
		if(pause)
		 {
			 pause = false ;
			 long toursortie = sensor2.getTour();
			 distancepause = distance(toursortie-tourpause);
		 }
		 else
		 {
			 tourpause = sensor2.getTour();
			 pause = true;
		 }
		return pause;
	}
	
	public boolean reset(final RoueMActivity roueactivity){
		AlertDialog.Builder builder = new AlertDialog.Builder(roueactivity);
		 builder.setMessage("Voulez-vous supprimer l'ensemble des données ?")
		        .setCancelable(false)
		        .setPositiveButton("Oui je le veux", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	sensor2.disconnectSensor();
		       		 	mHardwareConnector.destroy();
		       		 	distancepause = 0 ;
		       		 	resultat.clear();
		       		 	distance.setText("Appuyer sur Start");
		       		 	resultataff.setText("");
		       		 	mesure = "" ;
		       		 	roueactivity.changedBoutton(true, false, false, false);
		                activity.getApplication();
		            }

		        })
		        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                 dialog.cancel();
		            }
		        });
		 AlertDialog alert = builder.create();
		 alert.show();
		return appstart;
		 
	}
}
