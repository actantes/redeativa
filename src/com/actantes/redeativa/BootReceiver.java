package com.actantes.redeativa;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context contex, Intent intent) {
	 
		  SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contex); 
	      boolean Logado = settings.getBoolean("Login",false);
          int las = settings.getInt("Size",0);
           
	      
		if(Logado){
			Calendar cur_cal = new GregorianCalendar();
			cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
			cal.set(Calendar.HOUR_OF_DAY, cur_cal.get(Calendar.HOUR_OF_DAY));
			if(cur_cal.get(Calendar.MINUTE)+5<60){
          			cal.set(Calendar.MINUTE,cur_cal.get(Calendar.MINUTE)+5);          		
          		}
          		else{
          			cal.set(Calendar.MINUTE,cur_cal.get(Calendar.MINUTE)+5-60);
          			cal.set(Calendar.HOUR_OF_DAY, cur_cal.get(Calendar.HOUR_OF_DAY)+1);
          		}
			cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
			cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
			cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
			
		int minutes = 1; 
		AlarmManager am = (AlarmManager) contex.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(contex, AlarmServ.class);
		PendingIntent pi = PendingIntent.getService(contex, 777, i,  PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi); 
		if (minutes > 0) {
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi); 
			}
		} 
		
	}

}
