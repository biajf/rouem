package roue.src;


import java.io.File;
import java.io.IOException;

import com.wahoofitness.api.WFHardwareConnector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class RoueMActivity extends Activity {
	
	private Bundle save;
	
	//Variables Graphiques
	private TextView resultataff;
	private TextView distance;
	private Button poi;
	private Button bstart, bpause, breset, bstop = null;
	private ImageView image = null;
	private MenuItem sm = null; 
	private boolean menucree = false;
	
	//Gestion de la pause et stop
	boolean appstart = false;
	boolean pause;
	
	//Sauvegarde
	public static final String PREFS_NAME = "Preference";
	int donnees;
	private SharedPreferences settings = null ;
	private String varglo;
	
	//POI
	private MediaRecorder mediaRecorder;
	private File fichierEnregistre;
	private static final String LOG_TAG_ENREGISTREUR = null;
	 
	
	// Definition de la pause
	private Odometre roue ;
	float circonference = 1;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        resultataff = (TextView)findViewById(R.id.resultat);
	        distance = (TextView)findViewById(R.id.distance);
	        poi = (Button) findViewById(R.id.POI);
	        bstart = (Button)findViewById(R.id.start);
	        bpause = (Button)findViewById(R.id.pause);
	        breset = (Button)findViewById(R.id.reset);
	        bstop = (Button)findViewById(R.id.stop);
	        image = (ImageView)findViewById(R.id.imageView2);
	        if (!WFHardwareConnector.hasAntSupport(getBaseContext())) {
	        	 alert("Erreur","ANT+ n'est pas supporté par votre matèriel ");
	        	 changedBoutton(false, false ,false ,false);
	        }
	        else{
		        changedBoutton(true, false ,false ,false);
	        }

	        settings = getSharedPreferences(PREFS_NAME, 1);
	        
	        mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC) ;
			mediaRecorder.setOutputFormat( MediaRecorder.OutputFormat.DEFAULT ) ;
			mediaRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.DEFAULT ) ;
	              
	 }
	 
	 protected void onDestroy() {
	        super.onDestroy();
	 }
	 
	 public void poi(View v){
		 if(appstart)
			 roue.savePoi();
	 }
	 
	 public void start(View v){
     	
		 if(!appstart)
		 {
			 double diametre= settings.getInt("diam", 1000) ;
		     circonference =  (float) (diametre/1000 * Math.PI) / settings.getInt("nbcap", 1);
		     roue = new Odometre(this, save,resultataff,distance,circonference);
	     	 changedBoutton(false, true, true, true);
	     	 roue.init(getBaseContext()); 
			 resultataff.setText("");
			 appstart = true ;
			 
			 if(menucree)
				 sm.setEnabled(true);
		 }
		 else
		 {
			 changedBoutton(false, true, true, true);
			 roue.init(getBaseContext()); 
			 resultataff.setText("");
			 appstart = true ;
		 }
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
			//pause = roue.gestpause(pause);
			roue.gestpause();
		}
	 }
	 
	 public void reset(View v){
					
		 if(appstart)
		 {
			 roue.reset();
			 appstart = false ;
			 if(menucree)
				 sm.setEnabled(false);
		 }
		 
	 }

	public void changedBoutton(boolean start, boolean stop, boolean pause, boolean reset){
		bpause.setEnabled(pause);
     	bstop.setEnabled(stop);
     	breset.setEnabled(reset);
     	bstart.setEnabled(start);
	}
	
    public boolean onCreateOptionsMenu(Menu menu) {
    	 
        //Création d'un MenuInflater qui va permettre d'instancier un Menu XML en un objet Menu
        MenuInflater inflater = getMenuInflater();
        //Instanciation du menu XML spécifier en un objet Menu
        inflater.inflate(R.layout.menu, menu);
 
        sm =  menu.getItem(1);
        
        //Il n'est pas possible de modifier l'icône d'entête du sous-menu via le fichier XML on le fait donc en JAVA
    	//menu.getItem(0).getSubMenu().setHeaderIcon(R.drawable.option_white);
 
        if(!appstart)
        	menu.getItem(1).setEnabled(false);
        
        menucree = true ;
        return true;
     }
	
	 public boolean onOptionsItemSelected(MenuItem item) {
         //On regarde quel item a été cliqué grâce à son id et on déclenche une action
         switch (item.getItemId()) {
            case R.id.option:
            	if(appstart)
            	{
            		alert("Notification","Les modifications ne seront valables qu'après un reset ou un redemarrage de l'application.");
            	}
               return true;
            case R.id.nbcaps:
            	recupValeurMenu("Nombre d'aimants", "nbcap");
            	
                return true;
            case R.id.diam:
                recupValeurMenu("Diamètre (mm)","diam");
                return true;
            case R.id.partager :            	
            	return true;
            case R.id.mail :
            	String tmp = roue.export(getBaseContext());
            	sendEmail("", "", "", tmp, getBaseContext());
            	return true;
            case R.id.fichier :
            	roue.export(getBaseContext());
            	return true;
            case R.id.quitter:
               //Pour fermer l'application il suffit de faire finish()
        	   if (appstart) {
        	   roue.stop() ;
        	   }
        	   android.os.Process.killProcess(android.os.Process.myPid());
        	   
               finish();
               return true;
         }
         return false;
	 }
	 
		public void rotation(int rotat){
			//Creation d'une nouvelle image que l'on tournera par la matrice
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fleche);
		    Matrix mat = new Matrix();
		    mat.postRotate(rotat);

		    Bitmap bitmapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
		    image.setImageBitmap(bitmapRotate);
		    
		}
		
		private void recupValeurMenu(String titre, String var){
			//Variables de sauvegarde			
	        donnees = settings.getInt(var, 1);
	        varglo = var; 
	        
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(titre);

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER); 
			input.setText("" + donnees);

			builder.setView(input);

			builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt(varglo, Integer.parseInt(input.getText().toString()));
					// Commit the edits!
					editor.commit();
				}
			});

			builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				// aucune action dans ce cas
				}
			});
			
				builder.create();
				builder.show();
			}
		
		public void alert(String notification,String msg)
		{
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(notification);
			alertDialog.setMessage(msg);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // aucune action dans ce cas générique
			   }
			});
			alertDialog.show();
		}
		

		private void sendEmail(String address,String subject,String emailText, String strFile,Context context){
			
			try
			{
				strFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + strFile;
				final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { address });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
				emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" + strFile));
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailText);
				this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			 
			} catch (Throwable t) {
				Toast.makeText(context, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
			}
		}
		
		public void enregistrer(String nom){
			
			File folder = new File("/mnt/sdcard/Trodomètre/Audio"); 
			if (!folder.exists()) { 
			    folder.mkdir(); 
			} 		
			// Création du fichier de destination.
			try {		   
			fichierEnregistre = File.createTempFile(nom,".mp4", folder);
			} catch (IOException e) {
			    Log.e(LOG_TAG_ENREGISTREUR, "Problème E/S avant l’enregistrement");
			    
			    //Ajouter alerte dialog + traitement en cas de doublons
			    return; 
			}
			
			mediaRecorder.setOutputFile( fichierEnregistre.getAbsolutePath()) ;
			try {
				mediaRecorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mediaRecorder.start();
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Enregistrement");
			alertDialog.setMessage("Appuyer sur OK pour terminer l'enregistrement");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			     mediaRecorder.stop();
			   }
			});
			alertDialog.show();
		}
		
}