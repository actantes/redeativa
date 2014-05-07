package com.actantes.redeativa;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CampActivity extends Activity {
	
	public String resposta,idCamp,message,date, month, uri,Inv;
	ProgressBar pbarc;
	String url;
	OItem eve = new OItem();
	ArrayList<OItem> Aval = new ArrayList<OItem>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camp);
		
		url = this.getString(R.string.myurl);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		Set<String> dumb = new HashSet<String>();
		Set<String> Sids = new HashSet<String>(settings.getStringSet("Sids",dumb));
		Inv = settings.getString("Convite",null); 

		Intent intent = getIntent();
	     
		 message = intent.getStringExtra("EXTRA_MESSAGE");
		 resposta = intent.getStringExtra("EXTRA_Titulo");
	     idCamp = intent.getStringExtra("EXTRA_id");
		 date =intent.getStringExtra("EXTRA_day");
		 month =intent.getStringExtra("EXTRA_month");
		 uri =intent.getStringExtra("EXTRA_URL");
		 
		 ImageView img = (ImageView) findViewById(R.id.ImgCamp);
		 if(uri.length()==0){
			img.setVisibility(View.GONE);
			
		 }else{
		 
		 ASyncBmp cache = new ASyncBmp();
		 Bitmap bmp = (Bitmap) cache.getcache().get(uri);
		 img.setImageBitmap(bmp);
		 }
		 
		 TextView edt = (TextView) findViewById(R.id.Texto);
		 edt.setText(message);
		 TextView tv1 = (TextView) findViewById(R.id.Date);
		 tv1.setText(date+" "+month);
		 TextView tv2 = (TextView) findViewById(R.id.Titulo);
		 tv2.setText(resposta);
		 

		 pbarc = (ProgressBar)findViewById(R.id.progressBar3);
		 Button btn = (Button) findViewById(R.id.update1);
		 
		 if(Sids.contains(idCamp)){
	    		
			btn.setText("Desautoriza");
			btn.setBackgroundResource(R.drawable.btnunauth);
	    	   
	    	 }
		 
		 btn.setOnClickListener(new OnClickListener(){

   			@Override
   			public void onClick(View arg0) {
   				
  				Map<String, Object> params = new HashMap<String, Object>();
	          	params.put("invite",Inv );
	          	params.put("campaigns",idCamp );	          	
	          	AQuery aqr = new AQuery(getApplicationContext());
	          	aqr.progress(pbarc).ajax(url,params,  String.class,CampActivity.this, "Sharecall");
	          	
   				}
            });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.camp, menu);
		return true;
	}

	
	/*******************************************************************************************/
	
	 public void Sharecall(String url, String vallog, AjaxStatus status){    
		 
	    		if(vallog != null){  
	       		   	
	    			Gson gson = new Gson();  
	                JsonParser parser = new JsonParser();
	                JsonArray array = parser.parse(vallog).getAsJsonArray();
	                eve.seterror(null);
	                             
	               if(array.size()>0)eve  = gson.fromJson(array.get(0), OItem.class);
	     
	               if(eve.geterror()!=null){
 
	                Toast tst = Toast.makeText(getApplicationContext(), "Erro de Comunicação \n Tente Novamente", Toast.LENGTH_LONG);
   		        	tst.setGravity(Gravity.CENTER, 0, 0);
   					tst.show();
	               	
	               	}else{
	    			
	      		   	 Intent returnIntent = new Intent();
	       			 returnIntent.putExtra("resID",idCamp);
	       			 returnIntent.putExtra("resTXT",resposta);
	       			 setResult(RESULT_OK,returnIntent);  
	       			 finish();
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
	    	                break;
	    	            }
	    		}
	    		
	    	}
	
}
