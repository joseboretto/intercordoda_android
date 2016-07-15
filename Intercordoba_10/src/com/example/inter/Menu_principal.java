package com.example.inter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.Calendar;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.intercordoba.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony.Sms.Conversations;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
 
public class Menu_principal extends ActionBarActivity {
	
	private WebView mWebView;
	ProgressDialog pDialog;
	private MiTareaAsincronaDialog actualizar;
	static Context context;
	private long fechaBDOnline=0;
	//dropbox "https://dl.dropboxusercontent.com/s/983miucd1xz1mra/BaseDeDatos_Android_PERMANENTE.db";
	// CON MEDIA FIRE PUEDO SABER EL TAMAÑO DEL ARCHIVO ANTES DE DESCARGARLO
	//Media = http://download1488.mediafire.com/4jxj2jof4nlg/bhfzthtfgakth5r/BaseDeDatos_Android_PERMANENTE.db
	//dropbox "https://dl.dropboxusercontent.com/s/983miucd1xz1mra/BaseDeDatos_Android_PERMANENTE.db";
	// CON MEDIA FIRE PUEDO SABER EL TAMAÑO DEL ARCHIVO ANTES DE DESCARGARLO
	//Media = http://download1488.mediafire.com/4jxj2jof4nlg/bhfzthtfgakth5r/BaseDeDatos_Android_PERMANENTE.db
	//GoogleDrive http://goo.gl/Xt99Vp?gdriveurl
	//cubby.com https://www.cubbyusercontent.com/pl/BaseDeDatos_Android_PERMANENTE.db/_0cbdc37141b146f79a8d68b7171eba1d
	String dwnload_file_path = "http://www.intercordoba.com.ar/BaseDeDatos_Android_PERMANENTE.db";
    String dest_file_path = "/data/data/com.intercordoba/databases/DBexterna3.db";
    
 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Obtenemos una referencia a la actionbar
	    ActionBar actionBar = getSupportActionBar();
	    //Ocultamos el action bar
	    actionBar.hide();
	    setContentView(R.layout.menu_principal);


	    //Obtenemos referencia al webview
	    mWebView = (WebView) findViewById(R.id.webTwitter);
	    
	    if (isOnline()) {
			// Activamos Javascript
			   WebSettings webSettings = mWebView.getSettings();
			   webSettings.setJavaScriptEnabled(true);
			// Url que carga la app (webview)
			   mWebView.loadUrl("file:///android_asset/widget_twitter.html");
			// Forzamos el webview para que abra los enlaces internos dentro de la la APP
			   mWebView.setWebViewClient(new MyWebViewClient());
		} else {
			String customHtml = "Necesita Internet Para Ver Twitter";
			mWebView.loadData(customHtml, "text/html", "UTF-8");

		}

			File file = new File(dest_file_path);
			//si no existe el achivo
			if(!file.exists()){
				//si tiene internet actualizo
				if (isOnline()) {
		        	//Creo el directorio dia para que se arme la carpeta databases
		        	//Es un bug de 4.4 nose porque no me da los permisos
		        	File dir = getDatabasePath("Dir");
		        	dir.mkdirs();
		        	//Actualizo
		        	actuliazar();
				}
				//En caso de no tener internet cierra la aplicacion porque no esta la base de datos descargada
				else {
					Toast.makeText(Menu_principal.this, "Necesita Internet para Descargar los Horarios",
				            Toast.LENGTH_LONG).show();
					this.finish();
					}
	        }
			
			controlDeVeriones control = new controlDeVeriones();
			control.execute();

			
			}
	
				
	
			
			
			
					
			//Inicia la activity de los horarios de Tercero Arriba
			public void activity_TA(View v) {
				Intent act = new Intent(this, Tercero_Arriba.class);
				startActivity(act);
			}
			//Inicia la activity de los horarios de Sierras Chicas
			public void activity_SC(View v) {
				 
				Intent act = new Intent(this, Sierras_Chicas.class);
				startActivity(act);
				
			}
			//Actualizo, descargando desde algun servidor el archivo de la base de datos
			public void actuliazar(){
				// TODO Auto-generated method stub
				pDialog = new ProgressDialog(Menu_principal.this);
		        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pDialog.setMessage("Descargando Nuevos Horarios...Espere");
		        pDialog.setCancelable(false);
		        //Tare Asincronica
		        actualizar = new MiTareaAsincronaDialog();
		        actualizar.execute();}
			//Comprueba la conexion
			public boolean isOnline() {
			    ConnectivityManager cm =
			        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo netInfo = cm.getActiveNetworkInfo();
			    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			        return true;
			    }
			    return false;
			}
			
			public static Context getAppContext() {
			        return Menu_principal.context;
			    }

//ES una clse privada para poder pasarle parametro cuando hace un Click en twitter, sin eso no hace nada.			
private class MyWebViewClient extends WebViewClient {
			    @Override
			    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    		String tweetUrl = 
			    			    String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
			    			        urlEncode("Descarga la nueva aplicacion #ANDROID con los horarios de #INTERCORDOBA @Intercba \n"), urlEncode("→ http://bit.ly/interCBA ←"));
			    			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
			    			startActivity(intent);
			    			return true;
			    }
			    //Le doy un formato estanadar el string para poder usarlo en la URL
			    public String urlEncode(String s) {
			        try {
			            return URLEncoder.encode(s, "UTF-8");
			        }
			        catch (UnsupportedEncodingException e) {
			            Log.wtf("TAG", "UTF-8 should always be supported", e);
			            throw new RuntimeException("URLEncoder.encode() failed for " + s);
			        }
			    }  
			}			
	
			
			
private class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean> {
				private long fechaDBOnlineAsync;
				//private String contenido ="";
				//metodo principal del asynctack 
				@Override
				protected Boolean doInBackground(Void... params) {
					//android.os.Debug.waitForDebugger();
					 try {
	                       // Log.d(TAG, "downloading database");
	                        URL url = new URL(dwnload_file_path);
	                        /* Open a connection to that URL. */
	                        URLConnection ucon = url.openConnection();
	                        //HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
	                        
	                        /*
	                         * Define InputStreams to read from the URLConnection.
	                         */
	                        fechaDBOnlineAsync = ucon.getLastModified();
	                        //contenido = ucon.getContent().toString();
	                        InputStream is = ucon.getInputStream();
	                        BufferedInputStream bis = new BufferedInputStream(is);
	                        /*
	                         * Read bytes to the Buffer until there is nothing more to read(-1).
	                         */
	                        ByteArrayBuffer baf = new ByteArrayBuffer(50);
	                        int current = 0;
	                        while ((current = bis.read()) != -1) {
	                                baf.append((byte) current);
	                        }
	                        /* Convert the Bytes read to a String. */
	                        FileOutputStream fos = null;
	                        // Select storage location
	                        File dest_file = new File(dest_file_path);
				            dest_file.createNewFile();
				            dest_file.mkdir();
				            // Escritura del archivo
	                        fos = new FileOutputStream(dest_file);
	                        fos.write(baf.toByteArray());
	                        fos.close();
	                } catch (IOException e) {
	                        Log.e("E", "downloadDatabase Error: " , e);
	                        return false;
	                }  catch (NullPointerException e) {
	                        Log.e("E", "downloadDatabase Error: " , e);
	                        return false;
	                } catch (Exception e){
	                        Log.e("E", "downloadDatabase Error: " , e);
	                        return false;
	                }
	                return true;
		    	}
		    	//Se ejecuta mientras se ejecuta el principal
		    	 @Override
				    protected void onProgressUpdate(Integer... values) {
				    }
				//Corre antes del ejecutar el principal
				    @Override
				    protected void onPreExecute() {
				
				        pDialog.setOnCancelListener(new OnCancelListener() {
				        @Override
				        public void onCancel(DialogInterface dialog) {
				            MiTareaAsincronaDialog.this.cancel(true);
				        }
				    });
				        pDialog.show();
				        
				    }
				//corre luego que termina el principal
				    @Override
				    protected void onPostExecute(Boolean result) {
				        if(result)
				        {
				            pDialog.dismiss();
//				            Toast.makeText(Menu_principal.this, "Descarga finalizada! ,fecha: "+ fechaDBOnlineAsync,
//				                Toast.LENGTH_LONG).show();
				          //Creo un sahred prefrences que toma un modelo de clave = valor
				 		  SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
				 		  SharedPreferences.Editor editor = prefs.edit();
				 		  //Actualizo la fecha de BD Local
				 		  editor.putLong("longFechaDBLocal", fechaDBOnlineAsync);
				 		 editor.commit();
				        }
				    }
				
				    @Override
				    protected void onCancelled() {
				        Toast.makeText(Menu_principal.this, "Descarga Cancelada",
				            Toast.LENGTH_SHORT).show();
				    }
				    
		    
}


private class controlDeVeriones extends AsyncTask<Void, Integer, Boolean> {
	
	//private String contenido ="";
	//metodo principal del asynctack 
	@Override
	protected Boolean doInBackground(Void... params) {
		//android.os.Debug.waitForDebugger();
		 try {
               // Log.d(TAG, "downloading database");
                URL url = new URL(dwnload_file_path);
                /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();
                fechaBDOnline = ucon.getLastModified();
                
        } catch (IOException e) {
                Log.e("E", "downloadDatabase Error: " , e);
                return false;
        }  catch (NullPointerException e) {
                Log.e("E", "downloadDatabase Error: " , e);
                return false;
        } catch (Exception e){
                Log.e("E", "downloadDatabase Error: " , e);
                return false;
        }
        return true;
	}
	
	@Override
    protected void onPostExecute(Boolean result) {
        if(result)
        {
            if (isOnline()) {
//            	Toast.makeText(Menu_principal.this, "Debo actualizar? "+ fechaBDOnline,
//                        Toast.LENGTH_LONG).show();
                  //Creo un sahred prefrences que toma un modelo de clave = valor
        	 		   SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        	 		   long fechaLocal = prefs.getLong("longFechaDBLocal", -1);
        	 		   //si lo que esta guardado es diferente de lo que esta online descargo
        	 		   if (fechaLocal != fechaBDOnline) {
//        	 			  Toast.makeText(Menu_principal.this, "Si, actualizo ",
//        	                        Toast.LENGTH_LONG).show();
        	 			   actuliazar();
        	 		   }
        	 		 
			}
            
	 		  
            
        }
    }
	
	    

}
    
}