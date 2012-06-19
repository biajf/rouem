package roue.src;


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
	        image = (ImageView)findViewById(R.id.imageView2);
	        //sens = (RadioGroup)findViewById(R.id.radioGroup1);

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
			 roue.reset();
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
               Toast.makeText(this, "Option", Toast.LENGTH_SHORT).show();
               return true;
            case R.id.nbcaps:
            	text("Nombre de capteurs", "nbcap");
                return true;
            case R.id.diam:
                text("Diamètre","diam");
                return true;
            case R.id.partager :
            	
            	return true;
           case R.id.quitter:
               //Pour fermer l'application il suffit de faire finish()
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
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	        donnees = settings.getInt(var, 0);
	        
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(titre);

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER); 
			input.setText("" + donnees);

			builder.setView(input);

			builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				      SharedPreferences.Editor editor = settings.edit();
				      editor.putInt("diam", Integer.parseInt(input.getText().toString()));
				      
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
}