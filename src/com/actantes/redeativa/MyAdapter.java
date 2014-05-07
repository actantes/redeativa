package com.actantes.redeativa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.androidquery.callback.AjaxStatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


class MyAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<OItem> Items;
  

    public MyAdapter(Context context, ArrayList<OItem> Items) {
        this.context = context;
        this.Items = Items;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	  SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    		 Set<String> dumb = new HashSet<String>();
    		 Set<String> sids= settings.getStringSet("Sids", dumb);
    	 
    	View view;
    	
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (View) inflater.inflate(R.layout.lista, null);
        } else {
            view = (View) convertView;
        }

       LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.Image);
        ASyncBmp ABmp = new ASyncBmp();
        ImageView Bmp = (ImageView) view.findViewById(R.id.imageView1);
        
        if(Items.get(position).getimage()!=null){
    	  Bmp.setTag(Items.get(position).getimage());
    	  ABmp.execute(Bmp);
        }
        
        TextView text1 = (TextView) view.findViewById(R.id.Title);
        TextView text2 = (TextView) view.findViewById(R.id.Conteudo);
        TextView text3 = (TextView) view.findViewById(R.id.Dia);
        TextView text4 = (TextView) view.findViewById(R.id.Mes);
        ImageView img1 = new ImageView(context);
        ImageView img2 = new ImageView(context);
        ImageView img3 = new ImageView(context);
        ImageView imgck = (ImageView) view.findViewById(R.id.imgcheck);
     
        CheckBox cb1 = (CheckBox) view.findViewById(R.id.checkBox1);
        RelativeLayout rl1 = (RelativeLayout) view.findViewById(R.id.Lista);
        RelativeLayout ll1 = (RelativeLayout) view.findViewById(R.id.socbar2);

        
        Map< Integer ,ImageView> Img = new HashMap<Integer, ImageView>();
        Img.put(0, img1);
        Img.put(1, img2);
        Img.put(2, img3);
        
        if(sids.contains(Items.get(position).getid())) {

        	imgck.setImageResource(R.drawable.accepted);	
        }
        else {

        	imgck.setImageResource(R.drawable.not_accepted);	
        }
        
        text1.setText(Items.get(position).gettitle());
        text2.setText("" + Items.get(position).getbody());
        text3.setText(Items.get(position).getday());
        text4.setText(Items.get(position).getmonth());

        return view;
        }
    

}
