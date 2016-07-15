package com.example.inter;


import com.intercordoba.R;
import com.intercordoba.R.drawable;



import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;


public class Sierras_Chicas extends ActionBarActivity {
	

	//creamos los botones
	private Button b_recorrido;
	private Button b_desde;
	private Button b_hasta;
	private TextView tabla_salida;
	private TextView tabla_llegada;
	private TextView tabla_dias;
	private TextView tabla_tarifa;
	private ListView lista;
	//datos de la lista
	private ArrayList<Lista_entrada> datos = new ArrayList<Lista_entrada>();
	private Lista_adaptador adaptador;

	//toma el valor selecionado
	private String s_origen;
	private String s_destino;
	private int bug=0;
	private int btn;
	private int recorrido=0;
	 
	 //base de datos 

	
	private String seleccion_tabla_horarios;
	private int valor_salida;
	private int valor_llegada;
	private TabHost tabs;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//Obtenemos una referencia a la actionbar y la ocultamos
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.hide();
	    //iniciamos el activity
	    setContentView(R.layout.activity_sc);
	    //tomamos referencia de cada control utilizado
	    b_recorrido= (Button)findViewById(R.id.button_recorrido);
	    b_desde= (Button)findViewById(R.id.button_Desde);
		b_hasta= (Button)findViewById(R.id.button_Hasta);
		// son las cabeceras de la tabla
		tabla_salida = (TextView) findViewById(R.id.tabla_salida);
		tabla_llegada = (TextView) findViewById(R.id.tabla_llegada);
		tabla_dias = (TextView) findViewById(R.id.tabla_dias);
		tabla_tarifa = (TextView) findViewById(R.id.tabla_tarifa);
		lista = (ListView) findViewById(R.id.listView_Horarios);
	    
	    
		//INICIO TABS  //
	    Resources res = getResources();
	    tabs=(TabHost)findViewById(android.R.id.tabhost);
	    tabs.setup();
	    
	    //TAB 1 //
	    TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
	    spec.setContent(R.id.tab1);
	    spec.setIndicator("Horarios");
	    tabs.addTab(spec);
	    //TAB 2 //
	    spec=tabs.newTabSpec("mitab2");
	    spec.setContent(R.id.tab2);
	    spec.setIndicator("Mapa");
	    tabs.addTab(spec);
	    //TAB 4 //
	    spec=tabs.newTabSpec("mitab4");
	    spec.setContent(R.id.tab4);
	    spec.setIndicator("",res.getDrawable(drawable.ic_menu_info_details));
	    tabs.addTab(spec);
	    tabs.setCurrentTab(0);
	    tabs.getTabWidget().setStripEnabled(false);
	    
	    //Color del texto de las pestañas
	  		for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
	  			TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
	  	        tv.setTextColor(Color.parseColor("#000000"));
	          }
	  		//Cambio Color de las pestañas con cada click
	  		setTabColor(tabs);
	          tabs.setOnTabChangedListener(new OnTabChangeListener() {

	              @Override
	              public void onTabChanged(String arg0) {

	                  setTabColor(tabs);
	              }
	               });
	          
			//Asociamos los menús contextuales a los controles
	        registerForContextMenu(b_recorrido);
		    registerForContextMenu(b_desde);
		    registerForContextMenu(b_hasta);
		    
		   //Configuro el adaptador
		    adaptador = new Lista_adaptador(this, R.layout.texto_tabla, datos){
				@Override
				public void onEntrada(Object entrada, View view) {
			        if (entrada != null) {
			        	TextView texto_salida = (TextView) view.findViewById(R.id.textView_Salida); 
			            if (texto_salida != null) 
			            	texto_salida.setText(((Lista_entrada) entrada).getTextoSalida()); 
			              
			            TextView texto_llegada = (TextView) view.findViewById(R.id.textView_llegada); 
			            if (texto_llegada != null)
			            	texto_llegada.setText(((Lista_entrada) entrada).getTextoLlegada());
			            
			            TextView texto_dias = (TextView) view.findViewById(R.id.textView_dias); 
			            if (texto_dias != null)
			            	texto_dias.setText(((Lista_entrada) entrada).getTextoDias()); 
			            
			            TextView texto_tarifa = (TextView) view.findViewById(R.id.textView_tarifa); 
			            if (texto_tarifa != null)
			            	texto_tarifa.setText(((Lista_entrada) entrada).getTextoTarifa()); 
			              
			   
			        }
				}
				
			};
			
	}
	



// es el metodo mas importante porque realiza la bsuqeda
public void onClickBuscarSC(View v) {
	
	//Creo una base de datos y la asicio al archivo
	BaseDatos db2 = new BaseDatos(this, "DBexterna3.db", null, 2);
	//creo un flujo para comunicarme con la BD
	SQLiteDatabase db= db2.getWritableDatabase();
    //Un toast para dar msj
	Toast toast1 = Toast.makeText(getApplicationContext(),
            "Error:Selecione un Origen/Destino", Toast.LENGTH_SHORT);
	//si no sleciono nada no puedo buscar
	  if(s_origen==null||s_destino==null||s_destino.equalsIgnoreCase(s_origen)){ 
			  toast1.show();
			  return;}
	  // limpio los datos del listview antes de la busqueda 
	  datos.clear();
	  
	  // los titulos del menu, deben coincidir con el nombre del los campos(borar espacios)
	    s_destino=eliminarEspacios(s_destino);
	    s_origen=eliminarEspacios(s_origen);
	    //Guardo el vallor de la selecion de la tabla para luego resetearlo
	    String recorrido = seleccion_tabla_horarios;
	    //Si es ida seleciono una tabla y sino seleciono otra
	    if (valor_salida<valor_llegada) {
	    	seleccion_tabla_horarios="IDA_"+seleccion_tabla_horarios;	
		}else{
			seleccion_tabla_horarios="REGRESO_"+seleccion_tabla_horarios;
		}
	    //Muestro la tabla selecionada para algo personal no es info para el usuario
	    //toast1.setText(seleccion_tabla_horarios);
	    //toast1.show();

	    //Busco los horarios
		String[] campos = new String[] {s_origen,s_destino,"Dias","SERVICIO"};
		//Utilizo la tabla selecionada y busco los campos requeridos
		Cursor c = db.query(seleccion_tabla_horarios, campos, null, null, null, null, null);
		
		//Creo los Strign que se van a mostrar
		String sal;
        String lle;
        String dias;
        String servicio;
      //Recorremos los resultados para mostrarlos en pantalla
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do { 
		    	 //tomo los valores leido del cursor
		    	  sal = c.getString(0);
		          lle = c.getString(1);
		          dias = c.getString(2);
		          servicio = c.getString(3);
		          
		          //Cuando no estan vacios ninguna de las lineas las agrego
		          if(sal!=null && lle!=null && (!sal.equals(""))&& (!lle.equals(""))){

		        	  datos.add(new Lista_entrada(sal,lle,dias,servicio));
		        	  
		          }
		          
		          
		     } while(c.moveToNext());
		}
	
	 
	       
//NOTA: En caso de necesitar mostrar en la lista datos procedentes de una base de datos la mejor práctica 
//es utilizar un Loader (concretamente un CursorLoader), que cargará los datos de forma asíncrona de forma
//que la aplicación no se bloquee durante la carga. Esto lo veremos más adelante en el curso, ahora nos conç
//formaremos con cargar datos estáticos procedentes de un array.
			//Caundo termina la busqueda envio el teto a cada cabecera
			tabla_salida.setText(R.string.salida);
			tabla_llegada.setText(R.string.llegada);
			tabla_dias.setText(R.string.dias);
			tabla_tarifa.setText("Servicio");
			//envio el adaptador con todos los datos a la lista 
	        lista.setAdapter(adaptador);
	        //le doy formato a la lista
	        lista.setBackgroundResource(R.drawable.red_theme);
	        tabla_salida.setBackgroundResource(R.drawable.red_theme);
	        tabla_llegada.setBackgroundResource(R.drawable.red_theme);
	        tabla_dias.setBackgroundResource(R.drawable.red_theme);
	        tabla_tarifa.setBackgroundResource(R.drawable.red_theme);
	        //Libero Ram
	        db.close();
	        db2.close();
	        c.close();
	        //reseteo el valor por si vuelve apretar el boton de busqueda
	        seleccion_tabla_horarios=recorrido;
			            }

	
			
			/**
		     * Copies your database from your local downloaded database that is copied from the server 
		     * into the just created empty database in the
		     * system folder, from where it can be accessed and handled.
		     * This is done by transfering bytestream.
		     * */



		     
			public static void setTabColor(TabHost tabhost) {

	            for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
	                tabhost.getTabWidget().getChildAt(i)
	                        .setBackgroundColor(Color.parseColor("#FE2E2E")); // unselected
	            }
	            tabhost.getTabWidget().setCurrentTab(0);
	            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
	                    .setBackgroundColor(Color.parseColor("#D8D8D8")); // selected
	                                                                            // //have
	                                                                            // to
	                                                                            // change
	        }
			
			public void onClicklMapa(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/ms?msid=217036447620627900275.0004ffc3d002e652134eb&msa=0&ll=-31.294394,-64.170456&spn=0.3409,0.676346&dg=feature"));
				startActivity(browserIntent);
			}
			
			public void onClicklPlayStoreCuando(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=efisat.cuandollega2&hl=es"));
				startActivity(browserIntent);
			}
			
			public void onClickEnviarMail(View v) {
				// Reemplazamos el email por algun otro real
			    String[] to = { "info@intercordoba.com.ar"};
			    enviar(to, "Sugerencias Intercordoba - APLICACION ANDROID",
			            "Escriba Aqui su sugerencia, reporte de error o cualquier informacion que nos quiera trasnmitir");
			}

			public void onClickLlamar1(View arg0) {
				 
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:03514580030"));
				startActivity(callIntent);
 
			}
			
			public void onClickLlamar2(View arg0) {
				 
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:03514580050"));
				startActivity(callIntent);
 
			}
			
			public void onClickLlamar3(View arg0) {
				 
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:08003450505"));
				startActivity(callIntent);
 
			}
			
			public void onClickCompartir (View v) {
				Intent intent=new Intent(android.content.Intent.ACTION_SEND);
 	        	intent.setType("text/plain");
 	        	// Add data to the intent, the receiving app will decide what to do with it.
 	        	intent.putExtra(Intent.EXTRA_SUBJECT, "");
 	        	intent.putExtra(Intent.EXTRA_TEXT, "Descarga la nueva aplicacion #ANDROID con los horarios de #INTERCORDOBA @Intercba  \n → http://bit.ly/interCBA ←");
 	        	startActivity(Intent.createChooser(intent, "Comapartir"));
			}

			 private void enviar(String[] to,
			        String asunto, String mensaje) {

			        Intent emailIntent = new Intent(Intent.ACTION_SEND);
			        emailIntent.setData(Uri.parse("mailto:"));
			        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
			        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
			        emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
			        emailIntent.setType("message/rfc822");
			        startActivity(Intent.createChooser(emailIntent, "Email "));
			    }
			 
			 private static String eliminarEspacios(String cadenaConEspacios) {
			        StringTokenizer tokenizer = new StringTokenizer(cadenaConEspacios);
			        StringBuilder cadenaSinEspacios = new StringBuilder();
			 
			        while(tokenizer.hasMoreTokens()){
			            cadenaSinEspacios.append(tokenizer.nextToken());
			        }
			 
			        return cadenaSinEspacios.toString();
			    }
			 
			 
			//Metodo que infla el context menu y se activa con el click del boton
			 public void IniciarContext(View v) {
			      registerForContextMenu(v);
			      openContextMenu(v);
			   }
			 
			//Se crea un solo cotxt menu y dependiedno de la selecion deplegamos un men
			 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
			     super.onCreateContextMenu(menu, v, menuInfo);
			     
			     if(v.getId()==R.id.button_recorrido){
			    	 menu.setHeaderTitle("Recorridos");
			         getMenuInflater().inflate(R.menu.recorridos_sc, menu);
			         btn=3;
			         s_destino=null;
			         s_origen=null;
			         b_desde.setText("Desde");
			         b_hasta.setText("Hasta");
			         return;}
			     
			     if(recorrido!=0){
			    	 if(v.getId()==R.id.button_Desde){
				         getMenuInflater().inflate(recorrido, menu);
				         btn=1;}
				     if(v.getId()==R.id.button_Hasta){
				         getMenuInflater().inflate(recorrido, menu);
				     	btn=2;}	
			     } 
			     else{
			    	 Toast toast1 = Toast.makeText(getApplicationContext(),
					            "Error:Selecione un RECORRIDO", Toast.LENGTH_SHORT);
			    	 toast1.show();}
			 }
			
			 
			//ejecuta Distintas Acciones dependiendo de la opccion pulsada en el contexct menu
			 public boolean onContextItemSelected(MenuItem item) {
				 if(btn==3){ 
					 b_recorrido.setText("Recorrido: "+item.getTitle());
					 //logica segun lo elecionado.
					 switch (item.getItemId()) {
				        case R.id.InterUrbano:
				            recorrido=R.menu.destinos_sc;
				            seleccion_tabla_horarios="SC";
				            return true;
				        case R.id.RioCeballos:
				            recorrido=R.menu.destinos_rioceballos;
				            seleccion_tabla_horarios="RioCeballos";
				            return true;
				        case R.id.Salsipuedes:
				            recorrido=R.menu.destinos_salsipuedes;
				            seleccion_tabla_horarios="Salsipuedes";
				            return true;
				        case R.id.Mendiolaza:
				            recorrido=R.menu.destinos_mendiolaza;
				            seleccion_tabla_horarios="Mendiolaza";
				            return true;
				        case R.id.unquillo_1_2:
				            recorrido=R.menu.destinos_u1;
				            seleccion_tabla_horarios="U1";
				            return true;
				        case R.id.unquillo_3:
				            recorrido=R.menu.destinos_u3;
				            seleccion_tabla_horarios="U3";
				            return true;
				        case R.id.unquillo_4:
				            recorrido=R.menu.destinos_u4;
				            seleccion_tabla_horarios="U4";
				            return true;}
				 }
				 //diferencio si el boton apretado previamente
				 if(btn==1){
					 b_desde.setText("Desde: "+item.getTitle()); //le envio el texto selecionado al boton
				 	 s_origen=(String) item.getTitle();//guardo el valor del origen

				 	 switch (item.getItemId()) {
				        case R.id.CORDOBA:
				            valor_salida=1;
				            return true;
				        case R.id.CR_Bower:
				            valor_salida=2;
				            return true;
				        case R.id.CR_R_Garcia:
				            valor_salida=3;
				            return true;
				        case R.id.AltoFierro:
				            valor_salida=4;
				            return true;
				        case R.id.BajoChico:
				            valor_salida=5;
				            return true;
				        case R.id.CRDespenaderos:
				            valor_salida=6;
				            return true;
				        case R.id.Despenaderos:
				            valor_salida=7;
				            return true;
				        case R.id.MonteRalo:
				            valor_salida=8;
				            bug=1;
				            return true;
				        case R.id.Corralito:
				            valor_salida=9;
				            bug=1;
				            return true;
				        case R.id.BajodelCarmen:
				            valor_salida=10;
				            return true;
				        case R.id.CRSanAgustin:
				            valor_salida=11;
				            return true;
				        case R.id.CR_Soconcho:
				            valor_salida=13;
				            return true;
				        case R.id.LasBajadas:
				            valor_salida=14;
				            return true;
				        case R.id.CRAlmafuerte:
				            valor_salida=15;
				            return true;
				        case R.id.RIOTERCEROllega:
				            valor_salida=16;
				            return true;
				        case R.id.RIOTERCEROsale:
				            valor_salida=17;
				            return true;
				        case R.id.Tancacha:
				            valor_salida=18;
				            return true;
				        case R.id.Gral_Fotheringam:
				            valor_salida=19;
				            return true;
				        case R.id.HERNANDO:
				            valor_salida=20;
				            return true;
				        case R.id.Pampayasta:
				            valor_salida=21;
				            bug=1;
				            return true;
				        case R.id.Oliva:
				            valor_salida=22;
				            bug=1;
				            return true;
				        case R.id.D_V_Sarsfield:
				            valor_salida=23;
				            return true;
				        case R.id.LasPerdices:
				            valor_salida=24;
				            return true;
				        case R.id.Gral_Deheza:
				            valor_salida=25;
				            return true;
				        case R.id.Gral_Cabrera:
				            valor_salida=26;
				            return true;
				        case R.id.Carnerillo:
				            valor_salida=27;
				            return true;
				        case R.id.Chucul:
				            valor_salida=28;
				            return true;
				        case R.id.LasHigueras:
				            valor_salida=29;
				            return true;
				        case R.id.RioCuarto:
				            valor_salida=30;
				            return true;
				        case R.id.Ticino:
				            valor_salida=31;
				            bug=1;
				            return true;
				        case R.id.Pasco:
				            valor_salida=32;
				            bug=1;
				            return true;
					case R.id.LaLaguna:
				            valor_salida=33;
				            bug=1;
				            return true;
				            }
				 	 
				 	 } 

				 if(btn==2){
					 b_hasta.setText("Hasta: "+item.getTitle());
					 s_destino=(String) item.getTitle();//guardo el valor del destino
					 switch (item.getItemId()) {
				        case R.id.CORDOBA:
				            valor_llegada=1;
				            return true;
				        case R.id.CR_Bower:
				            valor_llegada=2;
				            return true;
				        case R.id.CR_R_Garcia:
				            valor_llegada=3;
				            return true;
				        case R.id.AltoFierro:
				            valor_llegada=4;
				            return true;
				        case R.id.BajoChico:
				            valor_llegada=5;
				            return true;
				        case R.id.CRDespenaderos:
				            valor_llegada=6;
				            return true;
				        case R.id.Despenaderos:
				            valor_llegada=7;
				            return true;
				        case R.id.MonteRalo:
				            valor_llegada=8;
				            bug=1;
				            return true;
				        case R.id.Corralito:
				            valor_llegada=9;
				            bug=1;
				            return true;
				        case R.id.BajodelCarmen:
				            valor_llegada=10;
				            return true;
				        case R.id.CRSanAgustin:
				            valor_llegada=11;
				            return true;
				        case R.id.CR_Soconcho:
				            valor_llegada=13;
				            return true;
				        case R.id.LasBajadas:
				            valor_llegada=14;
				            return true;
				        case R.id.CRAlmafuerte:
				            valor_llegada=15;
				            return true;
				        case R.id.RIOTERCEROllega:
				            valor_llegada=16;
				            return true;
				        case R.id.RIOTERCEROsale:
				            valor_llegada=17;
				            return true;
				        case R.id.Tancacha:
				            valor_llegada=18;
				            return true;
				        case R.id.Gral_Fotheringam:
				            valor_llegada=19;
				            return true;
				        case R.id.HERNANDO:
				            valor_llegada=20;
				            return true;
				        case R.id.Pampayasta:
				            valor_llegada=21;
				            bug=1;
				            return true;
				        case R.id.Oliva:
				            valor_llegada=22;
				            bug=1;
				            return true;
				        case R.id.D_V_Sarsfield:
				            valor_llegada=23;
				            return true;
				        case R.id.LasPerdices:
				            valor_llegada=24;
				            return true;
				        case R.id.Gral_Deheza:
				            valor_llegada=25;
				            return true;
				        case R.id.Gral_Cabrera:
				            valor_llegada=26;
				            return true;
				        case R.id.Carnerillo:
				            valor_llegada=27;
				            return true;
				        case R.id.Chucul:
				            valor_llegada=28;
				            return true;
				        case R.id.LasHigueras:
				            valor_llegada=29;
				            return true;
				        case R.id.RioCuarto:
				            valor_llegada=30;
				            return true;
				        case R.id.Ticino:
				            valor_llegada=31;
				            bug=1;
				            return true;
				        case R.id.Pasco:
				            valor_llegada=32;
				            bug=1;
				            return true;
				        case R.id.LaLaguna:
				            valor_llegada=33;
				            bug=1;
				            return true;
				            }
					 }
				 return true;
			     }

			 
			 
			
			 
}
