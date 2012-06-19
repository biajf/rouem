package roue.src;


import com.wahoofitness.api.WFHardwareConnector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class RoueMActivity extends Activity {
	
	private Bundle save;
	
	//Variables Graphiques
	private TextView resultataff;
	private TextView distance;
	private Button bstart, bpause, breset, bstop = null;
	private RadioGroup sens = null; 
	private ImageView image = null;
	
	//Gestion de la pause et stop
	boolean appstart = false;
	boolean pause;
	
	//Sauvegarde
	public static final String PREFS_NAME = "Preference";
	int donnees;
	SharedPreferences settings = null ;
	String varglo;
	
	// Definition de la pause
	RoueMesureuse roue ;
	float circonference = 1;
	
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
	        image = (ImageView)findViewById(R.id.imageView2);
	        //sens = (RadioGroup)findViewById(R.id.radioGroup1);
	        if (!WFHardwareConnector.hasAntSupport(getBaseContext())) {
	        	 alert("Erreur","ANT+ n'est pas supporté par votre matèriel ");
	        	 changedBoutton(false, false ,false ,false);
	        }
	        else
	        {
		        changedBoutton(true, false ,false ,false);
	        }

	        settings = getSharedPreferences(PREFS_NAME, 0);
	        
	        // Version avec Roue Mesureuse
	       
	       
	 }
	 
	 protected void onDestroy() {
	        super.onDestroy();
	 }
	 public void start(View v){
     	
		 if(!appstart)
		 {
			 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		 double diametre= settings.getInt("diam", 0) ;
	     circonference =  (float) (diametre/1000 * Math.PI);
	     roue = new RoueMesureuse(this, save, sens,resultataff,distance,circonference);
     	 changedBoutton(false, true, true, true);
		 roue.init(getBaseContext()); 
		 resultataff.setText("");
		 appstart = true ;
		 
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
			pause = roue.gestpause(pause);
		}
	 }
	 
	 public void reset(View v){
					
		 if(appstart)
		 {
			 roue.reset();
			 appstart = false ;
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
 
        //Il n'est pas possible de modifier l'icône d'entête du sous-menu via le fichier XML on le fait donc en JAVA
    	//menu.getItem(0).getSubMenu().setHeaderIcon(R.drawable.option_white);
 
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
            	text("Nombre d'aimants", "nbcap");
            	
                return true;
            case R.id.diam:
                text("Diamètre","diam");
                return true;
            case R.id.partager :            	
            	return true;
            case R.id.mail :
            	roue.sendEmail("", "", "", "", getBaseContext());
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
		
		public void text(String titre, String var){
			//Variables de sauvegarde			
	        donnees = settings.getInt(var, 0);
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
				// do stuff ////////////////////////////////////////////
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
			      // here you can add functions
			   }
			});
			alertDialog.show();
		}
}