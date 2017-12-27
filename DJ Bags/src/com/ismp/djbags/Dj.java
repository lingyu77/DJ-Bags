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
	
	SeekBar skbVolume; //�վ㭵�q
	private ImageButton play, voice, add, record;
	//private ImageButton  mRepeat, mNext, mBefore, mStop; // V1�¦��\��AV2�ثe�w�^�O
	//boolean repeat;
	
	private TextView state, djname; 
	
	private boolean bIsPaused = false; //�ΨӧP�_�O�_�Ȱ�
	public MediaPlayer myPlayer1 = new MediaPlayer();
	boolean mute = false, initial;
	
	private ArrayAdapter<String> adapter;
	private ListView songList;
	
	List<String> songs = new ArrayList<String>();
	List<String> select = new ArrayList<String>();
	
	static int now = -1; //�O���{�b������@��
	
	public void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);

	 	setContentView(R.layout.dj);
	 	findViews();
	 	listener(); 
	 	controlVolume(); 
	 	
	 	Bundle bundle = getIntent().getExtras();
	 	djname.setText(bundle.getString("ID"));  // �q�n�J����������DJ�W��
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
		
		record.setOnClickListener(new ImageButton.OnClickListener() { //����	    	    
	      public void onClick(View v) {
	     
	    	  Intent intent = new Intent();
	    	  intent.setClass(Dj.this, Record.class);
	    	  startActivity(intent);
	      }
	    });
		
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
	              state.setText("���b����: " + select.get(now).toString()); 
	              play.setBackgroundResource(R.drawable.pause);
	              
	          }else if(now == -1 || select == null ) {
	        	    Toast.makeText(Dj.this,"�п�ܱ����񪺺q��!", Toast.LENGTH_SHORT).show();	            
	          }else if (select != null) {
	        	  Toast.makeText(Dj.this," �T���D�k���֡A�L�����z�]���v!", Toast.LENGTH_SHORT).show(); 
	        	  myPlayer1.setDataSource( "/sdcard/"+ select.get(now)); //�]�w MediaPlayerŪ��SDcard���ɮ�
		          myPlayer1.prepare();	         
		          myPlayer1.start(); 
		          state.setText("���b����: " + select.get(now).toString()); 
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
         		  if(now < select.size() - 1) { //��q��W�|�������A�~�򼽤U�@��
         			  myPlayer1 = new MediaPlayer();
		            	  myPlayer1.setDataSource( "/sdcard/"+ select.get(++now)); 
		            	  state.setText("���b����: " + select.get(now).toString()); 
				          myPlayer1.prepare();	         
				          myPlayer1.start(); 
				         
         		  }else {
         			  // ����(�Ӳz�����ӴN�����F�~��! �q�x�]�n����) ���D�h�@�ӭ��Ƽ���\��
         			  myPlayer1.release();
         			  state.setText("����!");
         			  //�q�x����
         			  closeStation();
         			  Global  global = ((Global)getApplicationContext());	
         			  global.g_state = "close";
         			  
         			  /*
         			  myPlayer1 = new MediaPlayer();
         			  myPlayer1.setDataSource( "/sdcard/"+ select.get(now = 0)); 
         			  state.setText("���b����: " + select.get(now).toString()); //�q�Ĥ@���S�}�l���Ƽ���
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
        
	    voice.setOnClickListener(new ImageButton.OnClickListener() { //�R��
	     	      
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

	    add.setOnClickListener(new ImageButton.OnClickListener() {   //��ܺq��

			public void onClick(View v) {
				ShowAlertDialogAndList();  //�C�XSD card ��mp3�����ɩMamr������
			}
	    });
	    
        /* V1�¦��\��AV2�ثe�w�^�O
	    mRepeat.setOnClickListener(new ImageButton.OnClickListener() //�榱�`������
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
	    
	    mNext.setOnClickListener(new ImageButton.OnClickListener()   //�U�@��
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
		          mTextView1.setText("���񤤡D�D�D");
		          mStart.setBackgroundResource(R.drawable.pause);
	          }else{
	        	  Toast popup = Toast.makeText(Dj.this, "�w�g�O�̫�@��!", Toast.LENGTH_SHORT);
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
	    
	    mBefore.setOnClickListener(new ImageButton.OnClickListener() //�W�@��
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
		          mTextView1.setText("���񤤡D�D�D");
		          mStart.setBackgroundResource(R.drawable.pause);   
	          }else{
	        	  Toast popup = Toast.makeText(Dj.this, "�w�g�O�Ĥ@��!", Toast.LENGTH_SHORT);
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
	   
	    
	    mStop.setOnClickListener(new ImageButton.OnClickListener() //������s
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
	
    private void controlVolume() {   //�ո`���q
    	
    	skbVolume.setMax(15);//���q�ո`����
        skbVolume.setProgress(5);//�]�mseekbar����m        
        skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             
            public void onStopTrackingTouch(SeekBar seekBar) {}
            
            public void onStartTrackingTouch(SeekBar seekBar) {}           
             
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {                    
             
            	AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);            	
            	int setVoice = Integer.parseInt(String.valueOf(progress));
            	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, setVoice, 0);
            	int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
            	
            	if(current == 0) {
            		voice.setBackgroundResource(R.drawable.voice2); //�令�R���Ϯ�
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
    	}).setPositiveButton("�T�w", new DialogInterface.OnClickListener(){
			
			public void onClick(DialogInterface dialog, int which) {
				//�W�ǿ�q���database
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
	    	
	    	User user = new User(2, djname.getText().toString()); //�ΨӶǰe��server������
	    	
	    	try{
	    		clientSocket = new Socket("140.116.82.49", 9000);   //create a socket to server
	    		output = new ObjectOutputStream(clientSocket.getOutputStream());  //���͸�ưe�X��y
	    		output.writeObject(user); //�ǰe����server
	    		output.flush();  //�R����   //�\�Τ���?    		    		
	    	}
	    	catch(IOException ioe){
	    		ioe.printStackTrace();
	    	}
	    }
}


