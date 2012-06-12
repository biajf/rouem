package roue.src;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.wahoofitness.api.WFAntException;
import com.wahoofitness.api.WFAntNotSupportedException;
import com.wahoofitness.api.WFAntServiceNotInstalledException;
import com.wahoofitness.api.WFDisplaySettings;
import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFBikePowerConnection;
import com.wahoofitness.api.comm.WFConnectionParams;
import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;


public class RoueMActivity extends Activity implements WFHardwareConnector.Callback {
	private WFHardwareConnector mHardwareConnector;
	private static final String TAG = "Test";
	private WFConnectionParams connectParams;
	private WFSensorConnection mConnection;
	private static final int DIALOG_ID_SETTINGS = 0;
	private static final int DIALOG_ID_BP_CALIBRATE = 1;
	private CapteurWFBikeSpeed sensor1;
	private TextView distance;
	
	private enum MyMenu {
		MENU_NONE,
		MENU_SETTINGS,
		MENU_BP_CALIBRATION,
		MENU_EXIT,      
	};
	
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        distance = (TextView)findViewById(R.id.textView1);
	        Context context = this.getApplicationContext();
	        int statusVisibility = TextView.INVISIBLE;
	        String statusText = "";
	         
	        // check for ANT hardware support.
	        antConnect(context, savedInstanceState, statusText, statusVisibility);
	        

	   }
	 
	 public void start(View v){
		 
		 
		 
		
		 //distance.setText(""+tutu);
	 }
	 
	 public void stop(View v)
	 {
		 sensor1.disconnectSensor();
	 }
	public void antConnect(Context context,Bundle savedInstanceState,String statusText,int statusVisibility)
	{
		if (WFHardwareConnector.hasAntSupport(context)) {
        	
	        try {
	        	boolean bResumed = false;
	        	
	        	// attempt to retrieve the previously suspended WFHardwareConnector instance.
	        	//
	        	// see the onRetainNonConfigurationInstance method.
	        	mHardwareConnector = (WFHardwareConnector) getLastNonConfigurationInstance();
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
			        mHardwareConnector = WFHardwareConnector.getInstance(this, this);
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
		        
		       statusVisibility = TextView.INVISIBLE;
	        }
	        catch (WFAntNotSupportedException nse) {
	        	// ANT hardware not supported.
	        	statusVisibility = TextView.VISIBLE;
	        	statusText = "ANT not supported.";
	        }
	        catch (WFAntServiceNotInstalledException nie) {

				//Toast installNotification = Toast.makeText(context, this.getResources().getString( R.string.Notify_Service_Required), Toast.LENGTH_LONG);
				//installNotification.show();

				// open the Market Place app, search for the ANT Radio service.
				mHardwareConnector.destroy();
				mHardwareConnector = null;
				WFHardwareConnector.installAntService(this);

				// close this app.
				finish();
	        }
			catch (WFAntException e) {
				statusVisibility = TextView.VISIBLE;
				statusText = "ANT initialization error.";
			}
       }
        else {
        	// ANT hardware not supported.
        	statusVisibility = TextView.VISIBLE;
        	statusText = "ANT not supported.";
        }
        
        if(!this.isFinishing())
        {
            //setContentView(R.layout.landscape_grid);
            initControls();
        	//mAntStateText.setVisibility(statusVisibility);
        	//mAntStateText.setText(statusText);
        }
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

	@Override
	public void hwConnHasData() {
		Log.d(TAG, "hwConnHasData");
		sensor1.updateDisplay();
		WFSensorConnectionStatus tutu = sensor1.connectSensor();
		 
		 distance.setText(""+sensor1.getFootpodConnection().getDeviceNumber());
		 //mBikeCadence.updateDisplay();
	}

	public void hwConnStateChanged(WFHardwareState state) {
		switch (state) {
			case WF_HARDWARE_STATE_DISABLED:
	        	//mAntStateText.setVisibility(TextView.VISIBLE);
	        	if (WFHardwareConnector.hasAntSupport(this)) {
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
		
		sensor1 = new CapteurWFBikeSpeed(getBaseContext());
		 sensor1.initControl(mHardwareConnector);
	}
	    

}