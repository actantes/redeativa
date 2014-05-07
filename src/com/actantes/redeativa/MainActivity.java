package com.actantes.redeativa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.Util;
import org.brickred.socialauth.util.ProviderSupport;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	SharedPreferences settings ;
	boolean Logado,Valido,Login;
	String Invite;
	ProgressBar pbar,pbar2;
	TabHost tabHost;
	TabWidget widget;
	EditText edtinv,edtmail,edtpsw;
	Switch swf,swt;
	Button btlog,bthid;
	ListView lv1;
	OItem eve = new OItem();
	ArrayList<OItem> Aval = new ArrayList<OItem>();
	MyAdapter adap;
	String url ;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		settings = PreferenceManager.getDefaultSharedPreferences(this); 
        Valido = settings.getBoolean("Valid",false);
        Logado = settings.getBoolean("Login",false);
        Invite = settings.getString("Convite",null);
        url = this.getString(R.string.myurl);
      
		if(Valido){
        	setContentView(R.layout.activity_main);
        	
        		pbar = (ProgressBar)findViewById(R.id.progressBar1);
        	
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
				String Inv = settings.getString("Convite",null); 
        	
				Map<String, Object> params = new HashMap<String, Object>();
	          	params.put("invite",Inv );
	          	AQuery aqr = new AQuery(getApplicationContext());
	          	aqr.progress(pbar).ajax(url,params,  String.class,MainActivity.this, "Listcall");
        	
	          	SetLista(Aval,this);
         
        	
        	
        }else{
        	
        	setContentView(R.layout.activity_tab);
        	//Configura TabHost
        	tabHost = (TabHost)findViewById(android.R.id.tabhost);
        	tabHost.setup();
        	widget = tabHost.getTabWidget();
        	SetTabHost();
        	 	//Resolve primera aba "Convite" pelo Logcall
        		pbar = (ProgressBar)findViewById(R.id.progressBar1);
        		Button btinv =(Button) findViewById(R.id.VldInv);
        		btinv.setOnClickListener(new OnClickListener(){

      			@Override
      			public void onClick(View arg0) {
      				edtinv = (EditText) findViewById(R.id.Invite);

      				if(edtinv.getText().length()==0){ 
      					Toast tst = Toast.makeText(getApplicationContext(), "Preencha seu Convite" , Toast.LENGTH_LONG);
      		        	tst.setGravity(Gravity.CENTER, 0, 0);
      					tst.show();	
      				}else{
      				Map<String, Object> params = new HashMap<String, Object>();
    	          	params.put("invite",edtinv.getText() );
    	          	AQuery aqr = new AQuery(getApplicationContext());
    	          	aqr.progress(pbar).ajax(url+"/invite",params,  String.class,MainActivity.this, "Logcall");
      				}
      			}
              	 
               });
        		//Resolve segunda aba "Email" pelo ASyncRequest	
            	 Button btmail =(Button) findViewById(R.id.VldEmail);
            	 edtmail = (EditText) findViewById(R.id.Email);
            	 edtpsw = (EditText) findViewById(R.id.psw);
   	            
                 btmail.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View arg0) {
        				pbar2 = (ProgressBar)findViewById(R.id.progressBar2);
        			
        				if(edtmail.getText().length()==0 || edtpsw.getText().length()==0){
        					Toast tst =  Toast.makeText(getApplicationContext(), "Preencha ambos campos" , Toast.LENGTH_LONG);
           		        	tst.setGravity(Gravity.CENTER, 0, 40);
           					tst.show();
        				}else{
        				pbar2.setVisibility(View.VISIBLE);
        				ASyncRequest areq = new ASyncRequest(edtmail.getText().toString(),edtpsw.getText().toString());
        				areq.execute(MainActivity.this,pbar2,tabHost,widget);
        				}
        				
        			}
                	 
                 });
                 
               //Resolve terceira aba "Redes Social" pelo ResponseList	

             		;
                 btlog =(Button) findViewById(R.id.VldSocial);
                 bthid =(Button) findViewById(R.id.hidenbt);
                 swf = (Switch)findViewById(R.id.switch1);
                 swt = (Switch)findViewById(R.id.switch2);
                 
                 btlog.setOnClickListener(new OnClickListener(){

         			@Override
         			public void onClick(View arg0) {
         				SocialAuthAdapter adapter = new SocialAuthAdapter(new ResponseList()); 
         				// Inicializada local para garantir apenas os provider selecionados no menu
         				
         				if(swf.isChecked()){
         					adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
                			try {
                				adapter.addConfig(Provider.FACEBOOK, "AppDevID", "AppDevSecret", null);               			

                			} catch (Exception e) {
                				e.printStackTrace();
                			}
         				}
         				
         				
         				if(swt.isChecked()){
         					adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		              		adapter.addCallBack(Provider.TWITTER,"http://www.actantes.org.br"); // Sempre garantir que é o mesmo callbackURL do site do app
		         			try {
		        				adapter.addConfig(Provider.TWITTER, "AppDevKey", "AppDevSecret",null);
		        				
		        			} catch (Exception e) {
		        				e.printStackTrace();
		        			}
         				} 
         				if(swt.isChecked() || swf.isChecked()){
         					adapter.enable(bthid);
         					bthid.performClick();
         				}else{
         				    	 Toast tst = Toast.makeText(getApplicationContext(), "Nenhuma Rede Selecionada", Toast.LENGTH_SHORT);
         				    	 tst.setGravity(Gravity.CENTER, 0, 0);
         				    	 tst.show();
         				}
         			}
                 	 
                  });
                 
                 Button btp1 =(Button) findViewById(R.id.pula1);
                 btp1.setOnClickListener(new OnClickListener(){

         			@Override
         			public void onClick(View arg0) {
         				
         				tabHost.getTabWidget().setCurrentTab(2);
         				tabHost.setCurrentTab(2);
         				widget.getChildAt(1).setBackgroundResource(R.drawable.tabgreen);
         			}
                 	 
                  });
                 
                 Button btp2 =(Button) findViewById(R.id.pula2);
                 btp2.setOnClickListener(new OnClickListener(){

         			@Override
         			public void onClick(View arg0) {
         				
			            SharedPreferences.Editor editor = settings.edit();
			            editor.putBoolean("Valid",true);
			            editor.commit(); 
         			
         	        	Intent i = new Intent(MainActivity.this,MainActivity.class);
         	        	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
         	            startActivity(i);

         			}
                 	 
                  });                 

        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	/***********************************************************************************************/
	
	public void SetLista(final ArrayList <OItem> ALval,Context ctx){
		
        lv1 = (ListView) findViewById(R.id.listView1);
        lv1.setVisibility(View.VISIBLE);
        adap = new MyAdapter(ctx,ALval);
        lv1.setAdapter(adap);
        
        lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				Intent LaunchIntent =  new Intent( getApplicationContext(), CampActivity.class);
            	eve = ALval.get(arg2);
            	LaunchIntent.putExtra("EXTRA_MESSAGE",eve.getbody());
            	LaunchIntent.putExtra("EXTRA_Titulo",eve.gettitle());
            	LaunchIntent.putExtra("EXTRA_day",eve.getday());
            	LaunchIntent.putExtra("EXTRA_month",eve.getmonth());
            	LaunchIntent.putExtra("EXTRA_id",eve.getid());
            	LaunchIntent.putExtra("EXTRA_URL",eve.getimage());
            	
            	startActivityForResult(LaunchIntent,Integer.parseInt(eve.getid()));
				
				return;
			}
        });
        
	}

	/***********************************************************************************************/
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		 Set<String> dumb = new HashSet<String>();
		 Set<String> Sids = new HashSet<String>(settings.getStringSet("Sids",dumb));
		 
		     if(resultCode == RESULT_OK){    
		    	 
		  	 
		    	 
		    String	resID = data.getStringExtra("resID");  
		    String  resTXT = data.getStringExtra("resTXT");
		    
		    	if(!Sids.contains(resID)){
		    		
		    		Intent LaunchInt =  new Intent( getApplicationContext(), AlarmServ.class);
		  		    this.startService(LaunchInt);
		    		
		    		Sids.add(resID);
		            SharedPreferences.Editor editor = settings.edit();
		            editor.putStringSet("Sids",Sids);
		            editor.commit();
		            Notify("RedeAtiva","Campanha Autorizada \n"+resTXT,Integer.parseInt(resID));
		    	   
		    	 } else{
		    		 
		    		Intent LaunchInt =  new Intent( getApplicationContext(), RemoveServ.class);
		    		LaunchInt.putExtra("resID",resID);
		  		    this.startService(LaunchInt);
		    		 
		    		 Sids.remove(resID);
			         SharedPreferences.Editor editor = settings.edit();
			         editor.putStringSet("Sids",Sids);
			         editor.commit();
			         
			         Notify("RedeAtiva","Campanha Desautorizada \n"+resTXT,Integer.parseInt(resID));
		    		 
		    	 }
		    		
		    		adap.notifyDataSetChanged();
		    	  
		    	
		     }
		     if (resultCode == RESULT_CANCELED) {    

		     }
		  
		}//onActivityResult
	
	/***********************************************************************************************/
	
	public void Notify (String Titulo,String Texto,int nID){
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.logo)
		        .setContentTitle(Titulo)
		        .setContentText(Texto);
				
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(nID, mBuilder.build());

	}
	
	/***********************************************************************************************/
	
	public void Listcall(String url, String vallis, AjaxStatus status){

		
    if(vallis != null){
 	   	Gson gson = new Gson();  
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(vallis).getAsJsonArray();
        Aval.clear();

     
        for(int i=0;i<array.size();i++){
        	   eve  = gson.fromJson(array.get(i), OItem.class);
        	   Aval.add(eve);        
          } 
		
        SetLista(Aval,this);

    }else{
        switch (status.getCode())

        {

            case AjaxStatus.TRANSFORM_ERROR:
            Toast.makeText(getApplicationContext(), "TRANSFORM_ERROR: " + status.getCode(), Toast.LENGTH_LONG).show();
            break;
            case AjaxStatus.NETWORK_ERROR:
            Toast.makeText(getApplicationContext(), "NETWORK_ERROR " + status.getCode(), Toast.LENGTH_LONG).show();
            break;
            case AjaxStatus.AUTH_ERROR:
            Toast.makeText(getApplicationContext(), "AUTH_ERROR" + status.getCode(), Toast.LENGTH_LONG).show();
            break;
            case AjaxStatus.NETWORK:
            Toast.makeText(getApplicationContext(), "NETWORK" + status.getCode(), Toast.LENGTH_LONG).show();
            break;
            default:
            Toast.makeText(getApplicationContext(), "OTHER ERROR" + status.getCode(), Toast.LENGTH_LONG).show();
    	
	
            break;

        }
    }    	 
    
}
	
	/***********************************************************************************************/
	
	 public void Logcall(String url, String vallog, AjaxStatus status){    
 		
	    	//	Toast.makeText(getApplicationContext(), vallog, Toast.LENGTH_SHORT).show();
	    		
	    		if(vallog != null){  
	       		   	Gson gson = new Gson();  
	                JsonParser parser = new JsonParser();
	                JsonArray array = parser.parse(vallog).getAsJsonArray();
	                eve.seterror(null);
	               if(array.size()>0)eve  = gson.fromJson(array.get(0), OItem.class);
	     
	               if(eve.geterror()!=null){
	
	            	Login = false;  
	                Toast tst = Toast.makeText(getApplicationContext(), "Convite Invalido", Toast.LENGTH_LONG);
   		        	tst.setGravity(Gravity.CENTER, 0, 0);
   					tst.show();
	               	}
	                 else{
	               	   Login = true;
	               	for(int i=0;i<array.size();i++){
	             	   eve  = gson.fromJson(array.get(i), OItem.class);
	             	   Aval.add(eve);        
	               		} 
	                 }   
	               if(Login){
	            	   
	                   SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	                   SharedPreferences.Editor editor = settings.edit();
	                   editor.putBoolean("Login",true);
	                   editor.putString("Convite",edtinv.getText().toString());
	                   editor.commit();   
	                   
	      				tabHost.getTabWidget().setCurrentTab(1);
	      				tabHost.setCurrentTab(1);
	      				widget.getChildAt(0).setBackgroundResource(R.drawable.tabgreen);
	               }   
	               
	    		} else{          
	    			 switch (status.getCode())

	    	            {
	    	                case AjaxStatus.TRANSFORM_ERROR:
	    	                Toast.makeText(getApplicationContext(), "TRANSFORM_ERROR: " + status.getCode(), Toast.LENGTH_LONG).show();
	    	                break;
	    	                case AjaxStatus.NETWORK_ERROR:
	    	                Toast.makeText(getApplicationContext(), "NETWORK_ERROR " + status.getCode(), Toast.LENGTH_LONG).show();
	    	                break;
	    	                case AjaxStatus.AUTH_ERROR:
	    	                Toast.makeText(getApplicationContext(), "AUTH_ERROR" + status.getCode(), Toast.LENGTH_LONG).show();
	    	                break;
	    	                case AjaxStatus.NETWORK:
	    	                Toast.makeText(getApplicationContext(), "NETWORK" + status.getCode(), Toast.LENGTH_LONG).show();
	    	                break;
	    	                default:
	    	                Toast.makeText(getApplicationContext(), "OTHER ERROR" + status.getCode(), Toast.LENGTH_LONG).show();
	    	                break;
	    	            }
	    		}
	    		
	    	}
	
	/***********************************************************************************************/	
	
	public void SetTabHost(){

		TabSpec spec1=tabHost.newTabSpec("Convite");
		spec1.setIndicator("Convite");
		spec1.setContent(R.id.tab1);


		TabSpec spec2=tabHost.newTabSpec("Email");
		spec2.setIndicator("Email");
		spec2.setContent(R.id.tab2);

		TabSpec spec3=tabHost.newTabSpec("Redes \n Sociais");
		spec3.setIndicator("  Redes \n Sociais");
		spec3.setContent(R.id.tab3);
       	
		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		
		
		for(int i = 0; i < widget.getChildCount(); i++) {
		    View v = widget.getChildAt(i);
		    TextView tv = (TextView)v.findViewById(android.R.id.title);
		   
		    if(tv == null) {
		        continue;
		    }
		    v.setBackgroundResource(R.drawable.tabselect); 
		}
		
		tabHost.getTabWidget().setCurrentTab(0);
		
		tabHost.getTabWidget().getChildTabViewAt(0).setEnabled(false);
		tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);
		tabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
		
	}
/***********************************************************************************************/
	
	public class ResponseList implements DialogListener {

		private Context ctx;
		@Override
		public void onComplete(Bundle values) {
		
		ctx = getApplicationContext();
		final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		String t =settings.getString("twitter key", null);
		String f =settings.getString("facebook key", null);
		
				if(swf.isChecked()&& !swt.isChecked()){
					if(f!=null){
			            SharedPreferences.Editor editor = settings.edit();
			            editor.putBoolean("Valid",true);
			            editor.commit(); 
			            
			        	Intent i = new Intent(MainActivity.this,MainActivity.class);
			        	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(i);	
					}
				
				}
				if(swt.isChecked()&& !swf.isChecked()){
					if(t!=null){
			            SharedPreferences.Editor editor = settings.edit();
			            editor.putBoolean("Valid",true);
			            editor.commit(); 
			            
			        	Intent i = new Intent(MainActivity.this,MainActivity.class);
			        	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(i);	
					}
				
				} 
				if(swt.isChecked() && swf.isChecked()){
					if(f!=null&&t!=null){

			            SharedPreferences.Editor editor = settings.edit();
			            editor.putBoolean("Valid",true);
			            editor.commit(); 
			            
			        	Intent i = new Intent(MainActivity.this,MainActivity.class);
			        	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(i);	
					}
					
				}
		

		
		
		Toast tst =  Toast.makeText(getApplicationContext(), providerName + " Conectado", Toast.LENGTH_SHORT);
       	tst.setGravity(Gravity.CENTER, 0, 0);
		tst.show();
	
		}
		
		@Override
		public void onBack() {
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onError(SocialAuthError arg0) {
			arg0.printStackTrace();
			
		}
		
	  }	
}

		


