package com.actantes.redeativa;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class TwtServ extends Service {

	 private WakeLock mWakeLock;
	SocialAuthAdapter	adaptert;
	 OItem eve = new OItem();
	ArrayList<OItem> Aval = new ArrayList<OItem>();
 	String url;
 	public String Inv;
 	public int id;
 	AlarmManager am;
 	Calendar cal = new GregorianCalendar();
	Calendar cur_cal = new GregorianCalendar();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	  @Override
	  public void onCreate() {
		  PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE); 
			 mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mTag");
			 mWakeLock.acquire();
			 
			 cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar
			 cal.setTimeInMillis(System.currentTimeMillis());
			 url = this.getString(R.string.myurl)+"/post";
			 am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  id =Integer.parseInt( intent.getStringExtra("PID"));
		  if(id!=0)		  CriaNotSev("RedeAtiva",Integer.toString(id),id);
		  else ;
		  
		  SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
			 Inv = settings.getString("Convite",null);
	         boolean Logado = settings.getBoolean("Login",false); 
		  
		  ConnectivityManager cm =
			        (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
			 
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null &&
			                      activeNetwork.isConnectedOrConnecting();
			if(isConnected&&Logado){
	         adaptert = new SocialAuthAdapter(new ResponseLs());
	
	         adaptert.addProvider(Provider.TWITTER, R.drawable.twitter);
		 	 adaptert.addCallBack(Provider.TWITTER, "http://www.actantes.org.br"); // Sempre garantir que é o mesmo callbackURL do site do app
		 	
	 		try {
	
				adaptert.addConfig(Provider.TWITTER, "AppDevKey", "AppDevSecret",null);
	 		
	 		} catch (Exception e) {
	 			 ReAlarm();
	 		}
	 		adaptert.authorize(getApplicationContext(), Provider.TWITTER);
	 	
			}else{// No Network Connection
				  ReAlarm();
			}
	 	
	      return START_STICKY;
	  }

		private final class ResponseLs implements DialogListener {
			@Override
			public void onComplete(Bundle values) {
				
				final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
				Map<String, Object> params = new HashMap<String, Object>();
	          	params.put("invite",Inv);
	          	params.put("post", id);
	          	AQuery aq = new AQuery(TwtServ.this);	
			 	aq.ajax(url,params,String.class, new AjaxCallback<String>() {

	                @Override
	                public void callback(String url, String val, AjaxStatus status) {
	                	
	                	AjaxStatus sts = status;
	                        if(val != null){
	                        	Gson gson = new Gson();  
	                            JsonParser parser = new JsonParser();
	                            JsonObject obj = parser.parse(val).getAsJsonObject();
	                         	 Aval.clear();
	                         	   eve  = gson.fromJson(obj, OItem.class);
	                         	   adaptert.updateStatus(eve.getbody(), new MessageLs(),false);
	                          //                           } 
	                        }else{ // val null 
	                        	  ReAlarm();
	                        	 
	                        }
	                }
	        }); 
	
			}  

			@Override
			public void onError(SocialAuthError error) { // erro na autorização
				error.printStackTrace();
				  ReAlarm();
				
			}

			@Override
			public void onCancel() {
			}

			@Override
			public void onBack() {

			}
		}
		
		private final class MessageLs implements SocialAuthListener<Integer> {
			@Override
			public void onExecute(String provider, Integer t) {
				Integer status = t;
				if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204){
					CriaNotSev("RedeAtiva","Compartilhou no Twitter "+Integer.toString(id),id);
					
					Map<String, Object> params = new HashMap<String, Object>();
       	          	params.put("invite",Inv);
       	          	params.put("shareds[]", id);
       	          	AQuery aq = new AQuery(TwtServ.this);
          	
       			 	aq.ajax(url,params,String.class, new AjaxCallback<String>() {
       			 		
       	                @Override
       	                public void callback(String url, String val, AjaxStatus status) {
       	                	  if(val != null){
       	                		  stopSelf();
       	                	  }else{ // val null;
       	                		  stopSelf();
       	                	  }
       	                }
       			    }); 
					
				}
				else{ // erro na postagem
					CriaNotSev("RedeAtiva","Não compartilhou no Twitter "+Integer.toString(id),id);
					  ReAlarm();
				}
			}
			
					
			
			@Override
			public void onError(SocialAuthError e) { // erro de comunicação
				  ReAlarm();
			
			
			}
		}
	
	  @Override
	  public void onDestroy() {
		  mWakeLock.release(); 
	  }

	    public void CriaNotSev (String Titulo,String Texto,int nID){
			
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

		  public void ReAlarm(){
			  
		     	 if(cur_cal.get(Calendar.HOUR_OF_DAY)+1<24){
		        			cal.set(Calendar.HOUR_OF_DAY,cur_cal.get(Calendar.HOUR_OF_DAY)+1);
		        		}
		        		else{
		        			cal.set(Calendar.HOUR_OF_DAY,cur_cal.get(Calendar.HOUR_OF_DAY)+1-24);
		        			cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE)+1);
		        		}
				     	Intent ita = new Intent(TwtServ.this, TwtServ.class);
				     	ita.putExtra("PID",Integer.toString(id));
		 				PendingIntent pia = PendingIntent.getService(TwtServ.this,
		 						777, ita,  PendingIntent.FLAG_UPDATE_CURRENT);
		 				am.cancel(pia); 
		 			    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pia); 
		 			    
		     	 stopSelf();
				  
			  }
	    
	    

}

