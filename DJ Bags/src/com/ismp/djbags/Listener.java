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
	static int now = 0; //�O���{�b������@��
	
	
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
		
		dj.setText("DJ²��:\n�U��ť���̡A�ڬODJ test�A�w��j�a�@�_���ɦn����!");
	}
	private void listener(){
		
		email.setOnClickListener(new ImageButton.OnClickListener() //�H�H��DJ
	    { 	      	      
	      public void onClick(View v) 
	      { 	              	    
  			Intent mEmailIntent = new Intent(android.content.Intent.ACTION_SEND);  
		    mEmailIntent.setType("plain/text");	 		              		       
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"lingyu@ismp.csie.ncku.deu.tw"});  		       
	        mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "�H�H��DJ");
	        startActivity(Intent.createChooser(mEmailIntent, getResources().getString(R.string.str_message)));  
	      } 
	    });
		
		
		
		play.setOnClickListener(new ImageButton.OnClickListener() { //����	   
		       
			public void onClick(View v) {
				Global  global = ((Global)getApplicationContext());		
	        try { 	         
	          if(myPlayer1.isPlaying() == true && bIsPaused == false) { //�b���񪬺A�A�Ȱ�
	            
	        	  myPlayer1.pause();  
	              bIsPaused = true; 
	              state.setText("�Ȱ���..."); 
	              play.setBackgroundResource(R.drawable.threebtn);
	              
	          }else if(bIsPaused == true && initial == true) { //�b�Ȱ����A�A����
	        	 
	              myPlayer1.start();   
	              bIsPaused = false;
	              state.setText("���b����: " + global.g_select.get(now).toString()); 
	              play.setBackgroundResource(R.drawable.pause);	          
	          }else if (global.g_select != null) {
	        	  myPlayer1.setDataSource( "/sdcard/"+ global.g_select.get(now)); //�]�w MediaPlayerŪ��SDcard���ɮ�
		          myPlayer1.prepare();	         
		          myPlayer1.start(); 
		          state.setText("���b����: " + global.g_select.get(now).toString()); 
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
	           Global  global = ((Global)getApplicationContext());	
	           
         	  try {
         		  if(now < global.g_select.size() - 1) { //��q��W�|�������A�~�򼽤U�@��
         			  myPlayer1 = new MediaPlayer();
		            	  myPlayer1.setDataSource( "/sdcard/"+ global.g_select.get(++now)); 
		            	  state.setText("���b����: " + global.g_select.get(now).toString()); 
				          myPlayer1.prepare();	         
				          myPlayer1.start(); 
				         
         		  }else {
         			  // ����(�Ӳz�����ӴN�����F�~��! �q�x�]�n����) ���D�h�@�ӭ��Ƽ���\��
         			  myPlayer1.release();
         			  state.setText("����!");        			             			  
         		  }
		          
				  } catch (IOException e) {
					  e.printStackTrace();
			      }
         	
               play.setBackgroundResource(R.drawable.pause);
	            
	        } 
	    });    	     	    	    
	}
}

