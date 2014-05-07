package com.actantes.redeativa;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

class ASyncRequest extends AsyncTask<Object,Integer,Boolean> {
	

	private Session session;
    private String mailhost = "smtp.gmail.com"; 
    private String username;
    private String password;
	TabHost tabHost;
	TabWidget widget;
	Context ctx;
	ProgressBar pbar;
    
    ASyncRequest(String user,String psw) {
        this.username = user;
        this.password = psw;
    }

	@Override
	protected Boolean doInBackground(Object... params) {
		
		ctx = (Context) params[0];
		pbar = (ProgressBar) params[1];
		tabHost = (TabHost) params[2];
		widget  = (TabWidget) params[3];
		
		
        Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");   
        props.setProperty("mail.host", mailhost);   
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", "465");   
        props.put("mail.smtp.socketFactory.port", "465");   
        props.put("mail.smtp.socketFactory.class",   
                "javax.net.ssl.SSLSocketFactory"); 
        props.put("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.quitwait", "false");   
      

        session = Session.getDefaultInstance(props);  
       

        try {
        	Transport tp = session.getTransport();
        	try {
				tp.connect(username+"@gmail.com", password);
	
			} catch (MessagingException e) {
				e.printStackTrace();			
			}
        	boolean conc =	tp.isConnected();
		if (conc) {
			return true;
		
		}
		else {
			return false;
		
		}
	
		} catch (NoSuchProviderException e) {
			e.printStackTrace();			
			return null;
		}
 }
	
	
	protected void onPostExecute(Boolean result){

		if (result){
			pbar.setVisibility(View.GONE);
			
			 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
             SharedPreferences.Editor editor = settings.edit();
             editor.putString("User",username);
             editor.putString("PSW",password);
             editor.commit();   
			
			tabHost.getTabWidget().setCurrentTab(2);
			tabHost.setCurrentTab(2);
			widget.getChildAt(1).setBackgroundResource(R.drawable.tabgreen);
			
			}else{
				pbar.setVisibility(View.GONE);
	        	Toast tst =  Toast.makeText(ctx, "Email ou Senha Invalido", Toast.LENGTH_LONG);
	        	tst.setGravity(Gravity.CENTER, 0, 0);
				tst.show();	
			}
		}



}