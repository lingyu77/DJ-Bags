package com.ismp.djbags;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import ismp.project.setupbox.*;

public class Station extends Activity {
	
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	ListView radiolist;
	private SimpleAdapter adapter;
	private ImageButton rank, moment;
	
	private static final int[] mPics=new int[]{
		R.drawable.pic1
	 };
	 
	 private static final String[] station = new String[] {
		 "音樂無界限"
	 };
		 
	 private static final String[] dj= new String[] {
		 "test"
	 };
		 
	 private static final String[] numListener = new String[] {
		 "0"
	 };
	 
	public void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.stationbackground);
		 findViews();
		 loadData();
		 setListeners();
	 }
	
	private void findViews() {	
		
		 radiolist = (ListView) findViewById(R.id.radioList);
		 rank = (ImageButton) findViewById(R.id.rank);
		 moment = (ImageButton) findViewById(R.id.moment);
	}
	
	private void loadData() {
		 
		//connect to database && crawling data
        
		Global  global = ((Global)getApplicationContext());	
		
		if(global.g_state.equals("open")) {
			for(int i = 0; i < station .length; i++){
				 HashMap<String,Object> item = new HashMap<String, Object>();
				 item.put("pic", mPics[i]);
				 item.put( "station", station[i]);
				 item.put( "dj","DJ：" + dj[i]);
				 item.put("rating", "聽眾：" + numListener[i]);
				 list.add(item); 
			 }
		}
		 
		//if(list != null) {
			 adapter = new SimpleAdapter( 
					 this,
					 list,
					 R.layout.station,
					 new String[] { "pic","station","dj","rating" },
					 new int[] { R.id.imageView1, R.id.textView1, R.id.textView2, R.id.textView3 } );		 
			 
			 radiolist.setAdapter(adapter);
		//}
		 		
	}
	
    private void setListeners() {        	
    	rank.setOnTouchListener(audio);
    	moment.setOnTouchListener(context); 
    	radiolist.setOnItemClickListener(transformation);
    }
    
    private ImageButton.OnTouchListener audio = new ImageButton.OnTouchListener() {
            
    	public boolean onTouch(View v, MotionEvent event) {   
               		
            return false;   
    	}   
    };
    
    private ImageButton.OnTouchListener context = new ImageButton.OnTouchListener() {  	
   
    	public boolean onTouch(View v, MotionEvent event) {   
    		
                Intent intent = new Intent();                
                intent.setClass(Station.this, Moment.class); 
                startActivity(intent);
                
            return false;   
    	}   
    };
    
    private ListView.OnItemClickListener transformation = new ListView.OnItemClickListener() {  	
    	   
		 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		    	Intent intent = new Intent();
				intent.setClass(Station.this, Listener.class);			
				Bundle bundle = new Bundle();  //設定要傳遞的資料
				bundle.putString("STATION", station[position]); // 怎麼樣只取到電台名稱?
				bundle.putInt("PICTURE", position);
				intent.putExtras(bundle);
				startActivity(intent);
		 }
    };
    
    /*
    private boolean download(){

    	ObjectOutputStream output = null;
    	ObjectInputStream input = null;
    	Socket clientSocket = null;
    	
    	User user = new User(3); //用來傳送給server的物件
    	User backUser = new User(); //用來儲存收到的測試物件
    	
    	try{
    		clientSocket = new Socket("140.116.82.49", 9000);   //create a socket to server
    		output = new ObjectOutputStream(clientSocket.getOutputStream());  //產生資料送出串流
    		output.writeObject(user); //傳送物件給server
    		output.flush();  //沖馬桶   //功用不明?    		    		
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    	}
    	
    	while(true){
			try{ 
				input = new ObjectInputStream(clientSocket.getInputStream()); 	  //產生資料接收串流    
				backUser = (User) input.readObject(); //接收server傳來的物件   
				
        		input.close();
        		output.close();
        		clientSocket.close();
        		
        		if(backUser.getRequest() != 0) {       			
        			return true;	  
        		}
        		else {
        			list = backUser.getStation();
        			Toast.makeText(this, list + "", Toast.LENGTH_LONG).show();
        			return false;      		
        		}
			}catch(Exception e){        				
				e.printStackTrace();
			}  
		}
		
    }*/
}
