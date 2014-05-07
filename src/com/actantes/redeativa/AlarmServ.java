package com.actantes.redeativa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmServ extends Service {
	
	 private WakeLock mWakeLock;
	 OItem eve = new OItem();
	 ArrayList<OItem> Aval = new ArrayList<OItem>();
 	 String url;
 	 Calendar cal = new GregorianCalendar();
	 Calendar cur_cal = new GregorianCalendar();
	 AlarmManager am;
	 String t,f,user;
	 	
	@Override
	public IBinder onBind(Intent intent) {
	
		return null;
	}
	
	  @Override
	  public void onCreate() {
	
		 
			 PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE); 
			 mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mTag");
			 mWakeLock.acquire();
			 url = this.getString(R.string.myurl);
			 am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			 
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	 
	
		  cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar
		  cal.setTimeInMillis(System.currentTimeMillis());
       		
       	 ConnectivityManager cm =
			        (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
			 
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null &&
			                      activeNetwork.isConnectedOrConnecting();
			if(isConnected){
				LidaAlarm();
			}else{
				ReAlarm();
			}	
       	
	      return START_STICKY;
	  }

	
	
	  @Override
	  public void onDestroy() {
		 
		  mWakeLock.release(); 	 
	  }
	  
	  public void LidaAlarm() {
		  
			 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
			 String Inv = settings.getString("Convite",null);
	         boolean Logado = settings.getBoolean("Valid",false); 
	 		 t =settings.getString("twitter key", null);
			 f =settings.getString("facebook key", null);
			 user = settings.getString("User",null);
	         
	         if (Logado){
	 	        Map<String, Object> params = new HashMap<String, Object>();
	           	params.put("invite",Inv);
	           	params.put("post", "post");	           
	           	AQuery aq = new AQuery(this);
	 		 	aq.ajax(url,params,String.class, new AjaxCallback<String>() {
	 		 

	                 @Override
	                 public void callback(String url, String val, AjaxStatus status) {
	                 	
	                 	AjaxStatus sts = status;
	                         if(val != null){

	                         	Gson gson = new Gson();  
	                         	JsonParser parser = new JsonParser();
	                         	JsonArray array = parser.parse(val).getAsJsonArray();
	                         	Aval.clear();
	                           for(int i=0;i<array.size();i++){
	                          	   eve  = gson.fromJson(array.get(i), OItem.class);
	                          	   if(eve.geterror()!=null){
	                          		 stopSelf();
	                        	 
	                          	   }else{	                        	
	                          		   cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(eve.gethour()));
	                          		   cal.set(Calendar.DATE, Integer.parseInt(eve.getday()));
	                          		   cal.set(Calendar.MONTH, Integer.parseInt(eve.getmonth())-1);
	                          		   cal.set(Calendar.MINUTE,Integer.parseInt(eve.getmin()));                          

	                          		   switch (eve.getprovider(0))

	                          		   {
	                          		   case 1:
	                          			   if(t!=null){
	                          			  
	                          			   Intent itt = new Intent(AlarmServ.this, TwtServ.class);
	                          			   itt.putExtra("PID",eve.getid());
	                          			   PendingIntent pit = PendingIntent.getService(AlarmServ.this,
	   	             					   Integer.parseInt(eve.getid()), itt,  PendingIntent.FLAG_UPDATE_CURRENT);
	                          			   am.cancel(pit); 
	                          			   am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pit);
	                          			   }
	                          			   break;
	                          		   case 2: 
	                          			   if(f!=null){
	                          			  
	                          			   Intent itf = new Intent(AlarmServ.this, FBServ.class);
	                          			   itf.putExtra("PID",eve.getid());
	                          			   PendingIntent pif = PendingIntent.getService(AlarmServ.this, 
	                          			   Integer.parseInt(eve.getid()), itf,  PendingIntent.FLAG_UPDATE_CURRENT);
	                          			   am.cancel(pif); 
	                          			   am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pif);
	                          			   }
	                          			   break;
	                          		   case 3:
	                          			   if(user!=null){
	                          			 
	                          			   Intent itm = new Intent(AlarmServ.this, MailServ.class);
	                          			   itm.putExtra("PID",eve.getid());
	                          			   PendingIntent pim = PendingIntent.getService(AlarmServ.this,
	   	             					   Integer.parseInt(eve.getid()), itm,  PendingIntent.FLAG_UPDATE_CURRENT);
	                          			   am.cancel(pim); 
	                          			   am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pim);
	                          			   }
	                          			   break;
	                          			default:
	                          			 
	                          			break;
	   	             			
	                          		   }
	                          		  
	                          	   	} 
	                           	}
	                           stopSelf();
	                         }else{//val==null	 
	                         	ReAlarm();
	                         }
	                 	}
	 		 		}); 
	 		 	
	         	}else{//Logado == False
	         		stopSelf();
	         	}
	  }  

	  public void ReAlarm(){
		  
     	 if(cur_cal.get(Calendar.MINUTE)+5<60){
        			cal.set(Calendar.MINUTE,cur_cal.get(Calendar.MINUTE)+5);
        		}
        		else{
        			cal.set(Calendar.MINUTE,cur_cal.get(Calendar.MINUTE)+5-60);
        			cal.set(Calendar.HOUR_OF_DAY, cur_cal.get(Calendar.HOUR_OF_DAY)+1);
        		
        		}
     	 Intent ita = new Intent(AlarmServ.this, AlarmServ.class);
 				PendingIntent pia = PendingIntent.getService(AlarmServ.this,
 						777, ita,  PendingIntent.FLAG_UPDATE_CURRENT);
 				am.cancel(pia); 
 			    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pia); 
 			    
     	 stopSelf();
		  
	  }
}
