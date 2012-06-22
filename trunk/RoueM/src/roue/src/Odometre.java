package roue.src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

import android.R.xml;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class Odometre implements WFHardwareConnector.Callback {
	
	private RadioGroup sens = null; 
	private TextView resultataff;
	private TextView distance;
	
	//Variables Capteurs
	private WFHardwareConnector mHardwareConnector;
	private static final String TAG = "Roue";
	private CapteurWFBikeCadence sensor2;
	private Gyroscope gyroscope;
	
	//Variables de Mesures
	float distanceparcourue = 0;
	float angle = 0;	
	String mesure ="" ;
	String xmlString = "";
	
	// Enregistrement résultat 
	List<String> resultat = new ArrayList<String>() ;
	List<String> resultatxml = new ArrayList<String>() ;

	// Calibration 
	
	//Debug
	String cir ="1" ;
	private float circonference = 1;
	
	//Gestion de la pause et stop
	boolean appstart = false;
	boolean pause;
	float distancepause = 0;
	long tourpause = 0;
	
	//Activite 
	private RoueMActivity activity;
	private Bundle save;
	
	//Version Structure 
	Mesure mesurecourante ;
	List<Mesure> resultatstruc = new ArrayList<Mesure>() ;

	
	public Odometre(RoueMActivity act,Bundle saved,RadioGroup sen,TextView resultattext,TextView distancetext, float circonf) {
		activity = act ;
		save = saved ;
		sens = sen ;
		resultataff = resultattext;
		distance = distancetext;
		circonference = circonf;
		
		
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
			
			float tmpdistance = distance(sensor2.getTour()) ;
			distanceparcourue += tmpdistance ;
			if(direction == "Droite")
			{
			 //mesure += "\t"+tmp+"m\n"+"	Droite\n"
			 mesurecourante.getListAction().add(new Action("Droite",tmpdistance));
			 xmlString += "\t\t<distance="+tmpdistance+"/>\n"+"\t<Droite  = "+distanceparcourue+"/>\n";
			}
			else if(direction == "Gauche")
			{
			 //mesure += "\t"+tmp+"m\n"+"	Gauche\n";
				mesurecourante.getListAction().add(new Action("Gauche",tmpdistance));
			 xmlString += "\t\t<distance="+tmpdistance+"/>\n"+"\t<Gauche = "+distanceparcourue+"/>\n";
			}
			else
			{
			//mesure += "\t" + tmp+"m\n";
			//mesurecourante.getListAction().add(new Action("Droite",tmpdistance));
			xmlString += "\t<distance="+tmpdistance+"/>\n";
			}
		}
		
	}
	
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
		        	
		        	alert("Erreur","ANT not supported.");
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
					
					alert( "Erreur" ,"ANT initialization error.");
				}
	       }
	        else {
	        	// ANT hardware not supported.
	        	
	        	alert("Erreur" ,"ANT not supported.");
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
		 gyroscope = new Gyroscope(Sensor.TYPE_GYROSCOPE, activity, this);
		 gyroscope.initialiser();
		 antConnect(context, save);
		 mesurecourante = new Mesure(Integer.toString(resultatstruc.size())) ;
		 //distanceparcourue =0 ;
		 distancepause = 0 ;
		 tourpause = 0;
		 Log.d(TAG, "init");
	}

	@Override
	public void hwConnHasData() {
		Log.d(TAG, "hwConnHasData");
		//sensor1.connectSensor();
		sensor2.connectSensor();
		distance.setText( "Distance totale :"+ distance(sensor2.getTour())+"m\n");
		String tmp =""  ;
		Action tmpAction ;
		Action precAction = null;
		int size = mesurecourante.getListAction().size() ;
		for(int j=0; j<size; j++)
		 	{
		 		tmpAction = mesurecourante.getListAction().get(j) ;
		 		if(j>0)
		 		{
		 		precAction = mesurecourante.getListAction().get(j-1);
		 		}
		 		
		 		if(precAction == null)
		 		{
		 		tmp += tmpAction.getDistance() +"\n";
		 		}
		 		else
		 		{
		 		tmp += tmpAction.getDistance() - precAction.getDistance() +"\n";
		 		}
		 		tmp += tmpAction.getNom() + "\n";
		 	}
		
		float  tmpDistance = 0 ;
		if(size>0) {
			tmpDistance = distance(sensor2.getTour()) - mesurecourante.getListAction().get(size-1).getDistance();
		}
		else
		{
			tmpDistance = distance(sensor2.getTour()) ;
		}
		resultataff.setText(tmp+"\n"+tmpDistance);
		
	}
	
	public void alert(String notification,String msg)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setTitle(notification);
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
		/* 
		float dist = distance(sensor2.getTour());
		 mesure += dist +"m\n" ;
		 distancepause = 0 ;
		 resultat.add(mesure);
		 resultatxml.add("\t<distance_parcourue="+ dist +"/>\n\t"+xmlString+"\t\t<distance="+(dist-distanceparcourue)+"/>");
		 sensor2.disconnectSensor();
		 mHardwareConnector.destroy();
		 String tmp = "" ;
		 for(int i=0; i<resultat.size(); i++)
		 {
		 tmp += "Mesure "+i + " : " + dist + "m\n" +"\t"+resultat.get(i).toString();
		 }
		 */
		float tmpDistance = 0 ;
		tmpDistance = distance(sensor2.getTour()) ;


		mesurecourante.setDistancetotale(distance(sensor2.getTour()));
		mesurecourante.getListAction().add(new Action("Fin mesure",tmpDistance));
		resultatstruc.add(mesurecourante);
		sensor2.disconnectSensor();
		mHardwareConnector.destroy();
		 String tmp =""  ;
			Action tmpAction ;
			Action precAction = null;
			 for(int i=0; i<resultatstruc.size(); i++)
			 {
			 tmp += "Mesure " + resultatstruc.get(i).getNom() + " : " +  resultatstruc.get(i).getDistancetotale() +"\n";
			 	for(int j=0; j<resultatstruc.get(i).getListAction().size(); j++)
			 	{
			 		tmpAction = resultatstruc.get(i).getListAction().get(j) ;
			 		if(j>0)
			 		{
			 		precAction = resultatstruc.get(i).getListAction().get(j-1);
			 		}
			 		
			 		if(precAction == null)
			 		{
			 		tmp += tmpAction.getDistance() +"\n";
			 		}
			 		else
			 		{
			 		tmp += tmpAction.getDistance() - precAction.getDistance() +"\n";
			 		}
			 		tmp += tmpAction.getNom() + "\n";
			 	}
			 }
		 		 
		 
		 
		 
		 //distanceparcourue = dist ;
		 distance.setText("Appuyer sur Start");
		 resultataff.setText(tmp);
		// mesure = "" ;
		 mesurecourante = null ;
		 xmlString = "";
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
	
	public boolean reset(){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		 builder.setMessage("Voulez-vous supprimer l'ensemble des données ?")
		        .setCancelable(false)
		        .setPositiveButton("Oui je le veux", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	sensor2.disconnectSensor();
		       		 	mHardwareConnector.destroy();
		       		 	distancepause = 0 ;
		       		 	resultat.clear();
		       		 	resultatxml.clear();
		       		 	distance.setText("Appuyer sur Start");
		       		 	resultataff.setText("");
		       		 	mesure = "" ;
		       		 	xmlString = "";		       		 	
		       		 	activity.changedBoutton(true, false, false, false);
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
	
	private void createFile(String strFile, String data , Context context) {
		try {
			FileOutputStream fos = new FileOutputStream(strFile);
			Writer out = new OutputStreamWriter(fos, "UTF-8");
			out.write(data);
			out.flush();
			out.close();
		 
		} catch (Throwable t) {
			Toast.makeText(context, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
		}
	}
	
	public String export(Context context){
		String entete = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String xmlString = "";
		Action tmpAction ;
		Action precAction = null;
		 for(int i=0; i<resultatstruc.size(); i++)
		 {
		 xmlString += "<mesure id='"+ resultatstruc.get(i).getNom() + "'>" + "<distance_totale>" + resultatstruc.get(i).getDistancetotale() +"<distance_totale/>";
		 	for(int j=0; j<resultatstruc.get(i).getListAction().size(); j++)
		 	{
		 			tmpAction = resultatstruc.get(i).getListAction().get(j) ;
		 		if(j>0)
		 		{
		 			precAction = resultatstruc.get(i).getListAction().get(j-1);
		 		}
		 		
		 		if(precAction == null)
		 		{
		 			xmlString += "<distance id='"+j+"'>"+tmpAction.getDistance() +"<distance/>";
		 		}
		 		else
		 		{
		 			xmlString += "<distance id='"+j+ "'>" + (tmpAction.getDistance() - precAction.getDistance()) +"<distance/>";
		 		}
		 		if (tmpAction.getNom() == "Droite" || tmpAction.getNom() == "Gauche")
		 			xmlString += "<" + tmpAction.getNom() + "/>";
		 		else
		 			xmlString += "<POI='" + tmpAction.getNom() + "'/>" + "<file>"+tmpAction.getNom()+"_"+i+"_"+tmpAction.getDistance()+"<file/>";
		 	}
		 	xmlString += "<mesure/>";
		 }
		File folder = new File("/mnt/sdcard/RoueM/"); 
		if (!folder.exists()) { 
		    folder.mkdir(); 
		} 		
		Time date = new Time() ;
		createFile(folder + "/" + date.format2445()+".xml", entete+xmlString, context);
		return folder + "/" + date.format2445()+"xml";
	}
	

	public void stop() {
		if(sensor2 != null && mHardwareConnector != null)
		{
		sensor2.disconnectSensor();
		mHardwareConnector.destroy();
		//gyroscope.disconnect();
		
		}
		
	}
	
}
