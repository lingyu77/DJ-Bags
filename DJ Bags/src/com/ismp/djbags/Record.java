package com.ismp.djbags;

import android.app.Activity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Record extends Activity {
	
	  private ImageButton record, stop, play, delete;
	  private ListView myListView1;
	  private String strTempFile = "DjRecord_";
	  private File myRecAudioFile;
	  private File myRecAudioDir;
	  private File myPlayFile;
	  private MediaRecorder mMediaRecorder01;

	  private ArrayList<String> recordFiles;
	  private ArrayAdapter<String> adapter;
	  private TextView status;
	  private boolean sdCardExit;
	  private boolean isStopRecord;

	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.record);
	    findViews();

	    stop.setEnabled(false);
	    play.setEnabled(false);
	    delete.setEnabled(false);

	    // 判斷SD Card是否插入 
	    sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	       
	    // 取得SD Card路徑做為錄音的檔案位置 
	    if (sdCardExit)myRecAudioDir = Environment.getExternalStorageDirectory();	      

	    //取得SD Card目錄裡的所有.amr檔案 
	    getRecordFiles();

	    adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item, recordFiles);
	        	   
	    //將ArrayAdapter加入ListView物件中 
	    myListView1.setAdapter(adapter);

	    /* 錄音 */
	    record.setOnClickListener(new ImageButton.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	        try
	        {
	          if (!sdCardExit)
	          {
	            Toast.makeText(Record.this, "請插入SD Card", Toast.LENGTH_LONG).show();	               
	            return;
	          }
              
	          // 建立錄音檔 
	          myRecAudioFile = File.createTempFile(strTempFile, ".amr",myRecAudioDir);	              	           
	          mMediaRecorder01 = new MediaRecorder();
	         
	          // 設定錄音來源為麥克風 	        
	          mMediaRecorder01.setAudioSource(MediaRecorder.AudioSource.MIC);	              
	          mMediaRecorder01.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);	              
	          mMediaRecorder01.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);	              
	         
	          mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());
	          mMediaRecorder01.prepare();            
	          mMediaRecorder01.start();

	          status.setText("錄音中");

	          stop.setEnabled(true);
	          play.setEnabled(false);
	          delete.setEnabled(false);

	          isStopRecord = false;

	        } catch (IOException e){
	        
	          e.printStackTrace();
	        }
	      }
	    });
	    
	    /* 停止 */
	    stop.setOnClickListener(new ImageButton.OnClickListener() 
	    {	   
	      public void onClick(View arg0)
	      {
	        if (myRecAudioFile != null)
	        {
	          // 停止錄音 
	          mMediaRecorder01.stop();
	          
	          // 將錄音檔名給Adapter 
	          adapter.add(myRecAudioFile.getName());
	          
	          mMediaRecorder01.release();
	          mMediaRecorder01 = null;
	          status.setText("停止：" + myRecAudioFile.getName());

	          stop.setEnabled(false);
	          isStopRecord = true;
	        }
	      }
	    });
	    
	    /* 播放 */
	    play.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      public void onClick(View arg0)
	      {
	        if (myPlayFile != null && myPlayFile.exists())
	        {
	          /* 開啟播放的程式 */
	          openFile(myPlayFile);
	        }

	      }
	    });
	    /* 刪除 */
	    delete.setOnClickListener(new ImageButton.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	        if (myPlayFile != null)
	        {
	          /* 先將Adapter移除檔名 */
	          adapter.remove(myPlayFile.getName());
	          /* 刪除檔案 */
	          if (myPlayFile.exists())
	            myPlayFile.delete();
	          status.setText("完成刪除");
	        }
	      }
	    });

	    myListView1
	        .setOnItemClickListener(new AdapterView.OnItemClickListener()
	        {

	          public void onItemClick(AdapterView<?> arg0, View arg1,
	              int arg2, long arg3)
	          {
	            /* 當有點選檔名時將刪除及播放按鈕Enable */
	            play.setEnabled(true);
	            delete.setEnabled(true);

	            myPlayFile = new File(myRecAudioDir.getAbsolutePath()
	                + File.separator
	                + ((CheckedTextView) arg1).getText());
	            status.setText("你選的是："
	                + ((CheckedTextView) arg1).getText());
	          }
	        });

	  }

	  @Override
	  protected void onStop()
	  {
	    if (mMediaRecorder01 != null && !isStopRecord)
	    {
	      mMediaRecorder01.stop();
	      mMediaRecorder01.release();
	      mMediaRecorder01 = null;
	    }
	    super.onStop();
	  }

	  private void getRecordFiles()
	  {
	    recordFiles = new ArrayList<String>();
	    if (sdCardExit)
	    {
	      File files[] = myRecAudioDir.listFiles();
	      if (files != null)
	      {

	        for (int i = 0; i < files.length; i++)
	        {
	          if (files[i].getName().indexOf(".") >= 0)
	          {
	            //只取.amr檔案 
	            String fileS = files[i].getName().substring(files[i].getName().indexOf("."));
	                
	            if (fileS.toLowerCase().equals(".amr"))
	              recordFiles.add(files[i].getName());

	          }
	        }
	      }
	    }
	  }

	  /* 開啟播放錄音檔的程式 */
	  private void openFile(File f)
	  {
	    Intent intent = new Intent();
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(android.content.Intent.ACTION_VIEW);

	    String type = getMIMEType(f);
	    intent.setDataAndType(Uri.fromFile(f), type);
	    startActivity(intent);
	  }

	  private String getMIMEType(File f)
	  {
	    String end = f.getName().substring(
	        f.getName().lastIndexOf(".") + 1, f.getName().length())
	        .toLowerCase();
	    String type = "";
	    if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
	        || end.equals("amr") || end.equals("mpeg")
	        || end.equals("mp4"))
	    {
	      type = "audio";
	    } else if (end.equals("jpg") || end.equals("gif")
	        || end.equals("png") || end.equals("jpeg"))
	    {
	      type = "image";
	    } else
	    {
	      type = "*";
	    }
	    type += "/*";
	    return type;
	  }
	  
	  private void findViews() {	   

	    record = (ImageButton) findViewById(R.id.record);
	    stop = (ImageButton) findViewById(R.id.stop);
	    play = (ImageButton) findViewById(R.id.play);
	    delete = (ImageButton) findViewById(R.id.delete);
	    myListView1 = (ListView) findViewById(R.id.ListView01);
	    status = (TextView) findViewById(R.id.status);
	  }

}
