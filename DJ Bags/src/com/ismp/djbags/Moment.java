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
	private boolean bIsPaused = false; //�ΨӧP�_�O�_�Ȱ�
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
        
        //�n�����q(�����)
        select.add("��q.mp3");
        select.add("���j�պq.mp3");
        now = (int)(Math.random() * 2); 
        
        setListener();
	}
			
    private void updateStat() {
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);
        Location place = mgr.getLastKnownLocation(best);
        
        if (place != null) {
	        location.setText("�ثe�a�I: ��ߦ��\�j��\n" + "�g��:" + place.getLatitude() + "\n�n��:" + place.getLongitude());
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
		
		play.setOnClickListener(new ImageButton.OnClickListener() { //����	   
		      
			public void onClick(View v) {	       
	        try { 
	         
	          if(myPlayer1.isPlaying() == true && bIsPaused == false) { //�b���񪬺A�A�Ȱ�
	            
	        	  myPlayer1.pause();  
	              bIsPaused = true; 
	              state.setText("�Ȱ���..."); 
	              play.setBackgroundResource(R.drawable.halfbutton);
	              
	          }else if(bIsPaused == true && initial == true) { //�b�Ȱ����A�A����
	        	 
	              myPlayer1.start();   
	              bIsPaused = false;
	              state.setText(select.get(now).toString()); 
	              play.setBackgroundResource(R.drawable.pause);	              	         
	          }else if (select != null) {
	        	  myPlayer1.setDataSource( "/sdcard/context/"+ select.get(now)); //�]�w MediaPlayerŪ��SDcard���ɮ�
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
	    
        myPlayer1.setOnCompletionListener(new OnCompletionListener() { // �@���q�����񵲧�
	          
	          public void onCompletion(MediaPlayer arg0) {
	           
         	  try {
         		  if(now < select.size() - 1) { //��q��|�������A�~�򼽤U�@��
         			  myPlayer1 = new MediaPlayer();
		            	  myPlayer1.setDataSource( "/sdcard/"+ select.get(++now)); 
		            	  state.setText(select.get(now).toString()); 
				          myPlayer1.prepare();	         
				          myPlayer1.start(); 
				         
         		  }else {
         			  // ����
         			  myPlayer1 = new MediaPlayer();
         			  myPlayer1.setDataSource( "/sdcard/"+ select.get(now = 0));  //�q�Ĥ@���S�}�l���Ƽ���
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
               
	    next.setOnClickListener(new ImageButton.OnClickListener() {  //�U�@�� 
	   	    
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
	        	  //Toast popup = Toast.makeText(Moment.this, "�w�g�O�̫�@��!", Toast.LENGTH_SHORT);
	          	  //popup.show();	
	          }
	        } catch (Exception e) {
	            state.setText("error");  	        
	            e.printStackTrace(); 
	        } 	       
	      }      
	    });
	    
	    forward.setOnClickListener(new ImageButton.OnClickListener() { //�W�@��	    
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
	        	  Toast popup = Toast.makeText(Moment.this, "�w�g�O�Ĥ@��!", Toast.LENGTH_SHORT);
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
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DJ Bags �N���^�X");
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
	    week.setText("�P��" + getChineseDayOfWeek(rightNow));	    
		time.setText("" + getSection(rightNow));

		season.setText("" + getSeason(rightNow));
		weather.setBackgroundResource(R.drawable.cloud); //�ثe�g���A����n�ʺA��Ѯ�
	}
	
	//�P�_���ȱ�
	public String getSection(Calendar rightNow){
		
		String section = null;
		
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		
		  if (hour >= 6 && hour < 12) {		 
			  section = "�W��";
		  } else if (hour >= 12 && hour < 18) {
			  section = "�U��";
		  } else {
			  section = "�ߤW";
		  }
		  
		  return section;
	}
	
	//�P�_�u�`
    public String getSeason(Calendar rightNow) {
        String seasonKind = null;
        
        switch(rightNow.get(Calendar.MONTH)) {
          
            case Calendar.MARCH:
            case Calendar.APRIL:
            case Calendar.MAY:	
               
                seasonKind = "�K�u";
                break;
                
            case Calendar.JUNE:
            case Calendar.JULY:
            case Calendar.AUGUST:

                seasonKind = "�L�u";
                break;
                
            case Calendar.SEPTEMBER:
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:	
 
                seasonKind = "��u";
                break;
                
            case Calendar.DECEMBER:
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
 
                seasonKind = "�V�u";
                break;                
        }
        
        return seasonKind;
    }
    
    //�P�_�P��
    public static String getChineseDayOfWeek(Calendar rightNow) {
    	String chineseDayOfWeek = null;
			
		switch(rightNow.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
				chineseDayOfWeek = "��";
				break;
			case Calendar.MONDAY:
				chineseDayOfWeek = "�@";
				break;
			case Calendar.TUESDAY:
				chineseDayOfWeek = "�G";
				break;
			case Calendar.WEDNESDAY:
				chineseDayOfWeek = "�T";
				break;
			case Calendar.THURSDAY:
				chineseDayOfWeek = "�|";
				break;
			case Calendar.FRIDAY:
				chineseDayOfWeek = "��";
				break;
			case Calendar.SATURDAY:
				chineseDayOfWeek = "��";
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
