package com.actantes.redeativa;

import java.io.File;
import java.io.InputStream;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ASyncBmp extends AsyncTask<Object, Void, Bitmap> {

	ImageView imv = null;
	 private static LruCache<String, Bitmap>     mMemoryCache    = null;
	    private static int                          cacheSize       = 1024 * 1024 * 20;

	    public LruCache getcache(){
	    	return mMemoryCache;
	    }

	@Override
		protected Bitmap doInBackground(Object... imageViews) {
	    	this.imv = (ImageView) imageViews[0];
	    	
	    	  Bitmap bitmap = null;
	    	 String url = (String)imv.getTag();
	    	 
	    	 if (url == null || url.length() == 0) return null;
	         if (mMemoryCache == null) {
	             mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	                 @Override
	                 protected int sizeOf(String key, Bitmap bitmap) {
	                     return (bitmap.getRowBytes() * bitmap.getHeight());
	                 }
	             };
	         }

	          bitmap = mMemoryCache.get(url);
	         if (bitmap == null) {
	        	 try {
	        		 InputStream in = new java.net.URL(url).openStream();
	        		 bitmap = BitmapFactory.decodeStream(in);
	        		 mMemoryCache.put(url, bitmap);
	        		 
	        	 } catch (Exception e) {
	        		 e.printStackTrace();
	        	 	}
	         } 
	       
	         return bitmap;
	    	
	    	
		}

	@Override
		protected void onPostExecute(Bitmap result) {

		Bitmap bmp = mMemoryCache.get(imv.getTag().toString());
		
         if ( bmp != null && bmp == result ) {

	    	imv.setImageBitmap(bmp);
	    	imv.setVisibility(View.VISIBLE);
	    	
	    }else{
	    	imv.setVisibility(View.GONE);
	    	}
		}	
		

	}

