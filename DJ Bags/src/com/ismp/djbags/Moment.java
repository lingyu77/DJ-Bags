package com.ismp.djbags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Moment extends Activity implements LocationListener {
	
	private ImageButton play, email, forward, next;
	TextView time, location, season, song, state, year, month, day, week;
	LocationManager status, mgr;
	String best;
	public MediaPlayer myPlayer1 = new MediaPlayer();
	private boolean bIsPaused = false; //用來判斷是否暫停
	boolean initial;
	List<String> select = new ArrayList<String>();
	int now = -1;
	ImageView weather;
	
	public void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);
	 	setContentView(R.layout.moment);
	 	findViews();
	 	context();
	 	
	 	
        status = (LocationManager)(this.getSystemService(Context.LOCATION_SERVICE));
        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)||status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            updateStat();        
        }else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }  
        
        //要播的歌(假資料)
        select.add("國歌.mp3");
        select.add("成大校歌.mp3");
        now = (int)(Math.random() * 2); 
        
        setListener();
	}
			
    private void updateStat() {
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);
        Location place = mgr.getLastKnownLocation(best);
        
        if (place != null) {
	        location.setText("目前地點: 國立成功大學\n" + "經度:" + place.getLatitude() + "\n緯度:" + place.getLongitude());
	    } else {
	        Toast.makeText(this, "No location found", Toast.LENGTH_LONG).show();
	    }
    }

	@Override
	protected void onResume() {
		super.onResume();
		mgr.requestLocationUpdates(best, 60000, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mgr.removeUpdates(this);
		myPlayer1.pause();
	}	
	
	
	private void setListener() {
		email.setOnTouchListener(mail);
		
		play.setOnClickListener(new ImageButton.OnClickListener() { //播放	   
		      
			public void onClick(View v) {	       
	        try { 
	         
	          if(myPlayer1.isPlaying() == true && bIsPaused == false) { //在撥放狀態，暫停
	            
	        	  myPlayer1.pause();  
	              bIsPaused = true; 
	              state.setText("暫停中..."); 
	              play.setBackgroundResource(R.drawable.halfbutton);
	              
	          }else if(bIsPaused == true && initial == true) { //在暫停狀態，播放
	        	 
	              myPlayer1.start();   
	              bIsPaused = false;
	              state.setText(select.get(now).toString()); 
	              play.setBackgroundResource(R.drawable.pause);	              	         
	          }else if (select != null) {
	        	  myPlayer1.setDataSource( "/sdcard/context/"+ select.get(now)); //設定 MediaPlayer讀取SDcard的檔案
		          myPlayer1.prepare();	         
		          myPlayer1.start(); 
		          state.setText(select.get(now).toString()); 
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
	           
         	  try {
         		  if(now < select.size() - 1) { //選歌單尚未播完，繼續播下一首
         			  myPlayer1 = new MediaPlayer();
		            	  myPlayer1.setDataSource( "/sdcard/"+ select.get(++now)); 
		            	  state.setText(select.get(now).toString()); 
				          myPlayer1.prepare();	         
				          myPlayer1.start(); 
				         
         		  }else {
         			  // 播畢
         			  myPlayer1 = new MediaPlayer();
         			  myPlayer1.setDataSource( "/sdcard/"+ select.get(now = 0));  //從第一首又開始重複播放
         			  state.setText(select.get(now).toString()); 
				      myPlayer1.prepare();	         
				      myPlayer1.start();         			  
         		  }
		          
				  } catch (IOException e) {
					  e.printStackTrace();
			      }
         	
               play.setBackgroundResource(R.drawable.pause);
	            
	        } 
	    });
               
	    next.setOnClickListener(new ImageButton.OnClickListener() {  //下一首 
	   	    
	      public void onClick(View arg0) {
	        try {  
	        
	          if(myPlayer1.isPlaying() == true || bIsPaused == true) { 	           
	             myPlayer1.reset();            
	          }
	          
	          if(now < select.size() - 1) {        	  
	        	  ++now;
	        	  myPlayer1.setDataSource( "/sdcard/"+ select.get(now));
	        	  state.setText(select.get(now).toString()); 
		          myPlayer1.prepare();
		          myPlayer1.start(); 
		          play.setBackgroundResource(R.drawable.pause);
	          }else {
	        	  //Toast popup = Toast.makeText(Moment.this, "已經是最後一首!", Toast.LENGTH_SHORT);
	          	  //popup.show();	
	          }
	        } catch (Exception e) {
	            state.setText("error");  	        
	            e.printStackTrace(); 
	        } 	       
	      }      
	    });
	    
	    forward.setOnClickListener(new ImageButton.OnClickListener() { //上一首	    
	      public void onClick(View arg0) {
	      
	    	play.setBackgroundResource(R.drawable.pause);	    	 
	        
	        try { 	        
	         
	          if(myPlayer1.isPlaying()==true|| bIsPaused == true) {  	          
	            myPlayer1.reset();            
	          }
	          
	          if(now != 0) {
	        	  --now;
	        	  myPlayer1.setDataSource( "/sdcard/"+ select.get(now));
		          state.setText(select.get(now).toString());
		          myPlayer1.prepare();
		          myPlayer1.start(); 
		          play.setBackgroundResource(R.drawable.pause);   
	          }else {
	        	  Toast popup = Toast.makeText(Moment.this, "已經是第一首!", Toast.LENGTH_SHORT);
	          	  popup.show();	
	          }
		          	          
	        } catch (Exception e) {
	        	state.setText("error"); 
	            e.printStackTrace(); 
	        } 
	      }      
	    });
	}
	
	private ImageButton.OnTouchListener mail = new ImageButton.OnTouchListener() {
          
    	public boolean onTouch(View v, MotionEvent event) {   
            
			Intent mEmailIntent = new Intent(android.content.Intent.ACTION_SEND);  
		    mEmailIntent.setType("plain/text");	 		              		       
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"lingyu@ismp.csie.ncku.deu.tw"});  		       
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DJ Bags 意見回饋");
	        startActivity(Intent.createChooser(mEmailIntent, getResources().getString(R.string.str_message)));  
    		
            return false;   
    	}   
	};
	
	private void findViews() {	 
			   
	    email = (ImageButton) findViewById(R.id.email);
	    forward = (ImageButton) findViewById(R.id.forward);
	    next = (ImageButton) findViewById(R.id.next);
	    time = (TextView) findViewById(R.id.time);	
	    year = (TextView) findViewById(R.id.year);
	    month = (TextView) findViewById(R.id.month);
	    day = (TextView) findViewById(R.id.day);
	    week = (TextView) findViewById(R.id.week);
	    season = (TextView) findViewById(R.id.season);
	    location = (TextView) findViewById(R.id.location);
	    song = (TextView) findViewById(R.id.song);
	    play = (ImageButton) findViewById(R.id.play);
	    state = (TextView) findViewById(R.id.state); 
	    weather = (ImageView) findViewById(R.id.weather);
	}
	
	private void context(){
	    
		Calendar rightNow = Calendar.getInstance();
	    year.setText("" + rightNow.get(Calendar.YEAR));
	    month.setText("" + rightNow.get(Calendar.MONTH));
	    day.setText("" + rightNow.get(Calendar.DAY_OF_MONTH));
	    week.setText("星期" + getChineseDayOfWeek(rightNow));	    
		time.setText("" + getSection(rightNow));

		season.setText("" + getSeason(rightNow));
		weather.setBackgroundResource(R.drawable.cloud); //目前寫死，之後要動態抓天氣
	}
	
	//判斷早午晚
	public String getSection(Calendar rightNow){
		
		String section = null;
		
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		
		  if (hour >= 6 && hour < 12) {		 
			  section = "上午";
		  } else if (hour >= 12 && hour < 18) {
			  section = "下午";
		  } else {
			  section = "晚上";
		  }
		  
		  return section;
	}
	
	//判斷季節
    public String getSeason(Calendar rightNow) {
        String seasonKind = null;
        
        switch(rightNow.get(Calendar.MONTH)) {
          
            case Calendar.MARCH:
            case Calendar.APRIL:
            case Calendar.MAY:	
               
                seasonKind = "春季";
                break;
                
            case Calendar.JUNE:
            case Calendar.JULY:
            case Calendar.AUGUST:

                seasonKind = "夏季";
                break;
                
            case Calendar.SEPTEMBER:
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:	
 
                seasonKind = "秋季";
                break;
                
            case Calendar.DECEMBER:
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
 
                seasonKind = "冬季";
                break;                
        }
        
        return seasonKind;
    }
    
    //判斷星期
    public static String getChineseDayOfWeek(Calendar rightNow) {
    	String chineseDayOfWeek = null;
			
		switch(rightNow.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
				chineseDayOfWeek = "日";
				break;
			case Calendar.MONDAY:
				chineseDayOfWeek = "一";
				break;
			case Calendar.TUESDAY:
				chineseDayOfWeek = "二";
				break;
			case Calendar.WEDNESDAY:
				chineseDayOfWeek = "三";
				break;
			case Calendar.THURSDAY:
				chineseDayOfWeek = "四";
				break;
			case Calendar.FRIDAY:
				chineseDayOfWeek = "五";
				break;
			case Calendar.SATURDAY:
				chineseDayOfWeek = "六";
				break;           
			}
			
		return chineseDayOfWeek;
    }


	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	} 
	
}
