package com.actantes.redeativa;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class OItem {
	
	private String id;

	private String title;
	
	private String body;
	
	private String error;
	
	private String day;
	
	private String month;
	
	private String year;
	
	private String hour;
	
	private String minute;
	
	private  ArrayList<Integer> providers;
	
	private String image;
	
	private String recipients;
	
	OItem(){
		
	}
	

			public String getid() {
				return id;
			}

			public void setid(String id) {
				this.id = id;
			}
			public String gettitle() {
				return title;
			}

			public void settitle(String title) {
				this.title = title;
			}
			public String getbody() {
				return body;
			}

			public void setbody(String body ) {
				this.body = body;
			}
			public String geterror() {
				return error;
			}

			public void seterror(String error) {
				this.error = error;
			}
			public String getday() {
				return day;
			}

			public void setday(String day) {
				this.day = day;
			}
			public String getmonth() {
				return month;
			}

			public void semonth(String month ) {
				this.month = month;
			}
			public String getyear() {
				return year;
			}

			public void setyear(String year) {
				this.year = year;
			}
			public String gethour() {
				return hour;
			}

			public void sethour(String hour) {
				this.hour = hour;
			}
			public String getmin() {
				return minute;
			}

			public void setmin(String min) {
				this.minute = min;
			}
			public String getimage() {
				return image;
			}

			public void setimage(String image) {
				this.image = image;
			}
			public String getrecipients() {
				return recipients;
			}

			public void setrecipients(String recipient) {
				this.recipients = recipient;
			}
			
			public int getprovider(int pos) {
				return providers.get(pos);
			}
			public  ArrayList<Integer> getproviderall() {
				return providers;
			}

			public void setprovider(int provider) {
				this.providers.add(provider);
			}
			public  int getprovidersize() {
				return providers.size();
			}
}

