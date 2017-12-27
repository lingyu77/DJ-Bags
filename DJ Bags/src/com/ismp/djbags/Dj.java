package com.ismp.djbags;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ismp.project.setupbox.*;

public class Dj extends Activity {
	
	SeekBar skbVolume; //調整音量
	private ImageButton play, voice, add, record;
	//private ImageButton  mRepeat, mNext, mBefore, mStop; // V1舊有功能，V2目前已淘汰
	//boolean repeat;
	
	private TextView state, djname; 
	
	private boolean bIsPaused = false; //用來判斷是否暫停
	public MediaPlayer myPlayer1 = new MediaPlayer();
	boolean mute = false, initial;
	
	private ArrayAdapter<String> adapter;
	private ListView songList;
	
	List<String> songs = new ArrayList<String>();
	List<String> select = new ArrayList<String>();
	
	static int now = -1; //記錄現在播到哪一首
	
	public void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);

	 	setContentView(R.layout.dj);
	 	findViews();
	 	listener(); 
	 	controlVolume(); 
	 	
	 	Bundle bundle = getIntent().getExtras();
	 	djname.setText(bundle.getString("ID"));  // 從登入介面接收到DJ名稱
   }	
	
	private void findViews() {	   

		skbVolume=(SeekBar)this.findViewById(R.id.skbVolume);
		play = (ImageButton) findViewById(R.id.play);
		songList = (ListView) findViewById(R.id.songList);
	    voice = (ImageButton) findViewById(R.id.voice);
	    add = (ImageButton) findViewById(R.id.add);
	    record = (ImageButton) findViewById(R.id.record);
	    state = (TextView) findViewById(R.id.state); 
	    djname = (TextView) findViewById(R.id.djname);
	    
	    //mStop = (ImageButton) findViewById(R.id.stop);
	    //mRepeat = (ImageButton) findViewById(R.id.repaet); 
	    //mNext = (ImageButton) findViewById(R.id.next);
	    //mBefore = (ImageButton) findViewById(R.id.before);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		myPlayer1.pause();
	}
	
	private void listener(){
		
		record.setOnClickListener(new ImageButton.OnClickListener() { //錄音	    	    
	      public void onClick(View v) {
	     
	    	  Intent intent = new Intent();
	    	  intent.setClass(Dj.this, Record.class);
	    	  startActivity(intent);
	      }
	    });
		
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
	              state.setText("正在撥放: " + select.get(now).toString()); 
	              play.setBackgroundResource(R.drawable.pause);
	              
	          }else if(now == -1 || select == null ) {
	        	    Toast.makeText(Dj.this,"請選擇欲撥放的歌曲!", Toast.LENGTH_SHORT).show();	            
	          }else if (select != null) {
	        	  Toast.makeText(Dj.this," 禁止播放非法音樂，尊重智慧財產權!", Toast.LENGTH_SHORT).show(); 
	        	  myPlayer1.setDataSource( "/sdcard/"+ select.get(now)); //設定 MediaPlayer讀取SDcard的檔案
		          myPlayer1.prepare();	         
		          myPlayer1.start(); 
		          state.setText("正在撥放: " + select.get(now).toString()); 
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
         		  if(now < select.size() - 1) { //選歌單上尚未播完，繼續播下一首
         			  myPlayer1 = new MediaPlayer();
		            	  myPlayer1.setDataSource( "/sdcard/"+ select.get(++now)); 
		            	  state.setText("正在撥放: " + select.get(now).toString()); 
				          myPlayer1.prepare();	         
				          myPlayer1.start(); 
				         
         		  }else {
         			  // 播畢(照理說應該就結束了才對! 電台也要關閉) 除非多一個重複播放功能
         			  myPlayer1.release();
         			  state.setText("播畢!");
         			  //電台關閉
         			  closeStation();
         			  Global  global = ((Global)getApplicationContext());	
         			  global.g_state = "close";
         			  
         			  /*
         			  myPlayer1 = new MediaPlayer();
         			  myPlayer1.setDataSource( "/sdcard/"+ select.get(now = 0)); 
         			  state.setText("正在撥放: " + select.get(now).toString()); //從第一首又開始重複播放
				      myPlayer1.prepare();	         
				      myPlayer1.start(); 
				      */         			  
         		  }
		          
				  } catch (IOException e) {
					  e.printStackTrace();
			      }
         	
               play.setBackgroundResource(R.drawable.pause);
	            
	        } 
	    });
        
	    voice.setOnClickListener(new ImageButton.OnClickListener() { //靜音
	     	      
	      AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
	      
	      public void onClick(View v) {
	       	              	    
	    	  if(mute){	    		  
	    		  mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,5,0);
	    		  skbVolume.setProgress(5);	
	    		  voice.setBackgroundResource(R.drawable.voice); 
	    		  mute = false;
	    	  }else{	    		  
		    	  mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
		    	  voice.setBackgroundResource(R.drawable.voice2); 
		    	  skbVolume.setProgress(0);		    	  
	    		  mute = true;
	    	  } 
	      } 
	    });

	    add.setOnClickListener(new ImageButton.OnClickListener() {   //選擇歌曲

			public void onClick(View v) {
				ShowAlertDialogAndList();  //列出SD card 的mp3音樂檔和amr錄音檔
			}
	    });
	    
        /* V1舊有功能，V2目前已淘汰
	    mRepeat.setOnClickListener(new ImageButton.OnClickListener() //單曲循環播放
	    { 
	      public void onClick(View v) 
	      { 	              
	          if(repeat){
		    	  myPlayer1.setLooping(false); 
		          mRepeat.setBackgroundResource(R.drawable.repeat);
		          repeat = false;
	          }else{
	        	  myPlayer1.setLooping(true); 
		          mRepeat.setBackgroundResource(R.drawable.repeat2);
		          repeat = true;
	          }
	      } 
	    });
	    
	    mNext.setOnClickListener(new ImageButton.OnClickListener()   //下一首
	    {	    
	      public void onClick(View arg0)
	      {    
	        try 
	        { 
	          if(myPlayer1.isPlaying()==true || bIsPaused == true) 
	          {  
	            myPlayer1.reset();            
	          }
	          if(now<select.size()-1) {        	  
	        	  ++now;
	        	  myPlayer1.setDataSource( "/sdcard/"+ select.get(now));
		          songname.setText(select.get(now).toString());
		          myPlayer1.prepare();
		          myPlayer1.start(); 
		          mTextView1.setText("撥放中．．．");
		          mStart.setBackgroundResource(R.drawable.pause);
	          }else{
	        	  Toast popup = Toast.makeText(Dj.this, "已經是最後一首!", Toast.LENGTH_SHORT);
	          	  popup.show();	
	          }
	        } 
	        catch (IllegalStateException e) 
	        { 
	          mTextView1.setText(e.toString()); 
	          e.printStackTrace(); 
	        } 
	        catch (IOException e) 
	        { 
	          mTextView1.setText(e.toString()); 
	          e.printStackTrace(); 
	        } 
	      }      
	    });
	    
	    mBefore.setOnClickListener(new ImageButton.OnClickListener() //上一首
	    {
	      public void onClick(View arg0)
	      {
	    	mStart.setBackgroundResource(R.drawable.pause);	    	 
	        
	        try 
	        { 
	          if(myPlayer1.isPlaying()==true|| bIsPaused == true) 
	          { 
	            myPlayer1.reset();            
	          }
	          if(now != 0) {
	        	  --now;
	        	  myPlayer1.setDataSource( "/sdcard/"+ select.get(now));
		          songname.setText(select.get(now).toString());
		          myPlayer1.prepare();
		          myPlayer1.start(); 
		          mTextView1.setText("撥放中．．．");
		          mStart.setBackgroundResource(R.drawable.pause);   
	          }else{
	        	  Toast popup = Toast.makeText(Dj.this, "已經是第一首!", Toast.LENGTH_SHORT);
	          	  popup.show();	
	          }
		          
	          
	        } 
	        catch (IllegalStateException e) 
	        { 
	          mTextView1.setText(e.toString()); 
	          e.printStackTrace(); 
	        } 
	        catch (IOException e) 
	        { 
	          mTextView1.setText(e.toString()); 
	          e.printStackTrace(); 
	        } 
	      }      
	    });
	   
	    
	    mStop.setOnClickListener(new ImageButton.OnClickListener() //停止按鈕
	    { 
	      public void onClick(View v) 
	      { 	        
	        if(myPlayer1.isPlaying()==true || bIsPaused == false || bIsPaused == true){ 	          
	          myPlayer1.reset(); 
	          mTextView1.setText("stop");	         
	          mStart.setBackgroundResource(R.drawable.play); 
	        } 
	      } 
	    });
	    */
	}
	
    private void controlVolume() {   //調節音量
    	
    	skbVolume.setMax(15);//音量調節極限
        skbVolume.setProgress(5);//設置seekbar的位置        
        skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             
            public void onStopTrackingTouch(SeekBar seekBar) {}
            
            public void onStartTrackingTouch(SeekBar seekBar) {}           
             
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {                    
             
            	AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);            	
            	int setVoice = Integer.parseInt(String.valueOf(progress));
            	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, setVoice, 0);
            	int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
            	
            	if(current == 0) {
            		voice.setBackgroundResource(R.drawable.voice2); //改成靜音圖案
            		mute = true;
            	}else{
            		voice.setBackgroundResource(R.drawable.voice); 
            		mute = false;
            	}
            }
        });
    }
    
	CharSequence[] cs;
	
	private void ShowAlertDialogAndList(){
		boolean[] checked;
		
		getSDcardFile();
		cs = songs.toArray(new CharSequence[songs.size()]);	
	    checked = new boolean[cs.length];
	    
		new AlertDialog.Builder(this)    
    	.setTitle(R.string.select)
    	.setMultiChoiceItems(cs, checked, new DialogInterface.OnMultiChoiceClickListener(){
    		public void onClick(DialogInterface dialog, int which, boolean isCkecked) {
    			select.add(cs[which].toString());	   			 			
			}  	
    	}).setPositiveButton("確定", new DialogInterface.OnClickListener(){
			
			public void onClick(DialogInterface dialog, int which) {
				//上傳選歌單到database
				Global  global = ((Global)getApplicationContext());	
				global.g_state = "open";
				global.g_select = select;
				setSongLit();
				
				now = 0;
			}
    	})   
    	.show();	
    }
	
	private void getSDcardFile(){
		File fileDir = new File("/sdcard/");

        FilenameFilter filter = new FilenameFilter() { 
            public boolean accept(File fileDir, String name) {
            	return (name.endsWith(".mp3")||name.endsWith(".amr")); 
            }
        }; 
        
		File FileAry[] = fileDir.listFiles(filter);        
	    songs.clear();

	    if(FileAry != null){
			for (int i = 0; i < FileAry.length ; i++) songs.add(FileAry[i].getName());      
	    } else {	    
	    	Toast popup = Toast.makeText(Dj.this, "No music file in sdcard!", Toast.LENGTH_SHORT);
        	popup.show();	
	    }
	}
	
	private void setSongLit(){		
		adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item, select);						        	   
		songList.setAdapter(adapter);		
	}
	
	private void closeStation(){

	    	ObjectOutputStream output = null;
	    	Socket clientSocket = null;
	    	
	    	User user = new User(2, djname.getText().toString()); //用來傳送給server的物件
	    	
	    	try{
	    		clientSocket = new Socket("140.116.82.49", 9000);   //create a socket to server
	    		output = new ObjectOutputStream(clientSocket.getOutputStream());  //產生資料送出串流
	    		output.writeObject(user); //傳送物件給server
	    		output.flush();  //沖馬桶   //功用不明?    		    		
	    	}
	    	catch(IOException ioe){
	    		ioe.printStackTrace();
	    	}
	    }
}


