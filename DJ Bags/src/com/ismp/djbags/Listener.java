package com.ismp.djbags;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Listener extends Activity{

	private ImageButton play, email;
	private TextView state, welcometext, dj; 
	private ImageView cover; 	
	public MediaPlayer myPlayer1 = new MediaPlayer();
	boolean initial;
	int i = 0;
	boolean bIsPaused;
	int pictute;
	String receivetext;
	String song;
	static int now = 0; //記錄現在播到哪一首
	
	
	public void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);
	 	setContentView(R.layout.musicplay);
	 	findViews();
	 	Bundle bundle = this.getIntent().getExtras();
	 	receivetext = bundle.getString("STATION");
	 	pictute = bundle.getInt("PICTURE");
	 	welcometext.setText(receivetext);
	 	setPicture(pictute);
	 	listener(); 
   }		
	
	private void findViews() {	   		
		play = (ImageButton) findViewById(R.id.play); 
	    email = (ImageButton) findViewById(R.id.email);
	    state = (TextView) findViewById(R.id.state);
	    dj = (TextView) findViewById(R.id.dj);
	    welcometext= (TextView) findViewById(R.id.welcome);
	    cover = (ImageView)findViewById(R.id.showpic);	
	    
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		myPlayer1.pause();
	}	
	
	private void setPicture(int picture){
		switch(picture){
		case 0:
			cover.setBackgroundResource(R.drawable.pic1);
			break;		
		}
		
		dj.setText("DJ簡介:\n各位聽眾們，我是DJ test，歡迎大家一起分享好音樂!");
	}
	private void listener(){
		
		email.setOnClickListener(new ImageButton.OnClickListener() //寄信給DJ
	    { 	      	      
	      public void onClick(View v) 
	      { 	              	    
  			Intent mEmailIntent = new Intent(android.content.Intent.ACTION_SEND);  
		    mEmailIntent.setType("plain/text");	 		              		       
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"lingyu@ismp.csie.ncku.deu.tw"});  		       
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "寄信給DJ");
	        startActivity(Intent.createChooser(mEmailIntent, getResources().getString(R.string.str_message)));  
	      } 
	    });
		
		
		
		play.setOnClickListener(new ImageButton.OnClickListener() { //播放	   
		       
			public void onClick(View v) {
				Global  global = ((Global)getApplicationContext());		
	        try { 	         
	          if(myPlayer1.isPlaying() == true && bIsPaused == false) { //在撥放狀態，暫停
	            
	        	  myPlayer1.pause();  
	              bIsPaused = true; 
	              state.setText("暫停中..."); 
	              play.setBackgroundResource(R.drawable.threebtn);
	              
	          }else if(bIsPaused == true && initial == true) { //在暫停狀態，播放
	        	 
	              myPlayer1.start();   
	              bIsPaused = false;
	              state.setText("正在撥放: " + global.g_select.get(now).toString()); 
	              play.setBackgroundResource(R.drawable.pause);	          
	          }else if (global.g_select != null) {
	        	  myPlayer1.setDataSource( "/sdcard/"+ global.g_select.get(now)); //設定 MediaPlayer讀取SDcard的檔案
		          myPlayer1.prepare();	         
		          myPlayer1.start(); 
		          state.setText("正在撥放: " + global.g_select.get(now).toString()); 
		          play.setBackgroundResource(R.drawable.pause);
		          initial = true;
	          }
	        } catch (Exception e) {  	        
	        	state.setText(e.toString()); 
	            e.printStackTrace(); 
	        }
	        
	      } 
	    });     	     	    	    
	    
        myPlayer1.setOnCompletionListener(new OnCompletionListener() { // 一首歌曲撥放結束
	          
	           public void onCompletion(MediaPlayer arg0) {
	           Global  global = ((Global)getApplicationContext());	
	           
         	  try {
         		  if(now < global.g_select.size() - 1) { //選歌單上尚未播完，繼續播下一首
         			  myPlayer1 = new MediaPlayer();
		            	  myPlayer1.setDataSource( "/sdcard/"+ global.g_select.get(++now)); 
		            	  state.setText("正在撥放: " + global.g_select.get(now).toString()); 
				          myPlayer1.prepare();	         
				          myPlayer1.start(); 
				         
         		  }else {
         			  // 播畢(照理說應該就結束了才對! 電台也要關閉) 除非多一個重複播放功能
         			  myPlayer1.release();
         			  state.setText("播畢!");        			             			  
         		  }
		          
				  } catch (IOException e) {
					  e.printStackTrace();
			      }
         	
               play.setBackgroundResource(R.drawable.pause);
	            
	        } 
	    });    	     	    	    
	}
}

