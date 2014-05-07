package com.actantes.redeativa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;

public class RemoveServ extends Service {
	
	private WakeLock mWakeLock;
	 OItem eve = new OItem();
	 ArrayList<OItem> Aval = new ArrayList<OItem>();
	 String url;
	 Calendar cal = new GregorianCalendar();
	 Calendar cur_cal = new GregorianCalendar();
	 AlarmManager am;
	 String	resID;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	 @Override
	  public void onCreate() {
			 PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE); 
			 mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mTag");
			 mWakeLock.acquire();
			 url = this.getString(R.string.myurl)+"/campaign";
			 am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			 
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar
		  cal.setTimeInMillis(System.currentTimeMillis());
      		resID = intent.getStringExtra("resID"); 
      		
          	 ConnectivityManager cm =
 			        (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
 			 
 			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
 			boolean isConnected = activeNetwork != null &&
 			                      activeNetwork.isConnectedOrConnecting();
 			if(isConnected){
 				LidaRemove();
 			}else{
 				ReAlarm();
 			}

	      return START_STICKY;
	  }

	
	
	  @Override
	  public void onDestroy() {
		  mWakeLock.release(); 	 
	  }
	  
	  public void LidaRemove(){
		  
		  	 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
			 String Inv = settings.getString("Convite",null);
	         boolean Logado = settings.getBoolean("Valid",false); 
	         
	         if (Logado){
	 	        Map<String, Object> params = new HashMap<String, Object>();
	           	params.put("invite",Inv);
	           	params.put("campaign", resID);
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
	                          		   switch (eve.getprovider(0))

	                          		   {
	                          		   case 1:
	                          			   Intent itt = new Intent(RemoveServ.this, TwtServ.class);
	                          			   itt.putExtra("PID",eve.getid());
	                          			   PendingIntent pit = PendingIntent.getService(RemoveServ.this,
	   	             					   Integer.parseInt(eve.getid()), itt,  PendingIntent.FLAG_UPDATE_CURRENT);
	                          			   am.cancel(pit); 
	                          			   
	                          			   break;
	                          		   case 2: 
	                          			   Intent itf = new Intent(RemoveServ.this, FBServ.class);
	                          			   itf.putExtra("PID",eve.getid());
	                          			   PendingIntent pif = PendingIntent.getService(RemoveServ.this, 
	                          			   Integer.parseInt(eve.getid()), itf,  PendingIntent.FLAG_UPDATE_CURRENT);
	                          			   am.cancel(pif); 
	                          		
	                          			   break;
	                          		   case 3: 
	                          			   Intent itm = new Intent(RemoveServ.this, MailServ.class);
	                          			   itm.putExtra("PID",eve.getid());
	                          			   PendingIntent pim = PendingIntent.getService(RemoveServ.this,
	   	             					   Integer.parseInt(eve.getid()), itm,  PendingIntent.FLAG_UPDATE_CURRENT);
	                          			   am.cancel(pim); 
	                          			 
	                          			   break;
	                          			default:
	                          			break;
	   	             			
	                          		   }
	                          	   	} 
	                           	}
	                           stopSelf();
	                         }else{	 
	                         	ReAlarm();
	                         }
	                 	}
	 		 		}); 
	 		 	
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
	     	 Intent ita = new Intent(RemoveServ.this, RemoveServ.class);
	 				PendingIntent pia = PendingIntent.getService(RemoveServ.this,
	 						777, ita,  PendingIntent.FLAG_UPDATE_CURRENT);
	 				am.cancel(pia); 
	 			    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pia); 
	 			    
	     	 stopSelf();
			  
		  }

}
