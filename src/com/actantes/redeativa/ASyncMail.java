package com.actantes.redeativa;

import android.os.AsyncTask;
import android.util.Log;

class ASyncMail extends AsyncTask<String,Void,Boolean> {
	
	private String username;
	
	private String psw;
	
	
	ASyncMail(String user,String pass){
		this.username = user+"@gmail.com";
		this.psw = pass;	
	}

	@Override
	protected Boolean doInBackground(String... params) {

		GMailSender sender = new GMailSender(username,psw);
	
	try {
		sender.sendMail(params[0],params[1],username,params[3]);
	} catch (Exception e) {
		e.printStackTrace();			
		}
	return null;
			
	}	
}