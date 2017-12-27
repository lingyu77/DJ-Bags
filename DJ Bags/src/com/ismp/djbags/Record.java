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

	    // �P�_SD Card�O�_���J 
	    sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	       
	    // ���oSD Card���|�����������ɮצ�m 
	    if (sdCardExit)myRecAudioDir = Environment.getExternalStorageDirectory();	      

	    //���oSD Card�ؿ��̪��Ҧ�.amr�ɮ� 
	    getRecordFiles();

	    adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item, recordFiles);
	        	   
	    //�NArrayAdapter�[�JListView���� 
	    myListView1.setAdapter(adapter);

	    /* ���� */
	    record.setOnClickListener(new ImageButton.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	        try
	        {
	          if (!sdCardExit)
	          {
	            Toast.makeText(Record.this, "�д��JSD Card", Toast.LENGTH_LONG).show();	               
	            return;
	          }
              
	          // �إ߿����� 
	          myRecAudioFile = File.createTempFile(strTempFile, ".amr",myRecAudioDir);	              	           
	          mMediaRecorder01 = new MediaRecorder();
	         
	          // �]�w�����ӷ������J�� 	        
	          mMediaRecorder01.setAudioSource(MediaRecorder.AudioSource.MIC);	              
	          mMediaRecorder01.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);	              
	          mMediaRecorder01.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);	              
	         
	          mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());
	          mMediaRecorder01.prepare();            
	          mMediaRecorder01.start();

	          status.setText("������");

	          stop.setEnabled(true);
	          play.setEnabled(false);
	          delete.setEnabled(false);

	          isStopRecord = false;

	        } catch (IOException e){
	        
	          e.printStackTrace();
	        }
	      }
	    });
	    
	    /* ���� */
	    stop.setOnClickListener(new ImageButton.OnClickListener() 
	    {	   
	      public void onClick(View arg0)
	      {
	        if (myRecAudioFile != null)
	        {
	          // ������� 
	          mMediaRecorder01.stop();
	          
	          // �N�����ɦW��Adapter 
	          adapter.add(myRecAudioFile.getName());
	          
	          mMediaRecorder01.release();
	          mMediaRecorder01 = null;
	          status.setText("����G" + myRecAudioFile.getName());

	          stop.setEnabled(false);
	          isStopRecord = true;
	        }
	      }
	    });
	    
	    /* ���� */
	    play.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      public void onClick(View arg0)
	      {
	        if (myPlayFile != null && myPlayFile.exists())
	        {
	          /* �}�Ҽ��񪺵{�� */
	          openFile(myPlayFile);
	        }

	      }
	    });
	    /* �R�� */
	    delete.setOnClickListener(new ImageButton.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	        if (myPlayFile != null)
	        {
	          /* ���NAdapter�����ɦW */
	          adapter.remove(myPlayFile.getName());
	          /* �R���ɮ� */
	          if (myPlayFile.exists())
	            myPlayFile.delete();
	          status.setText("�����R��");
	        }
	      }
	    });

	    myListView1
	        .setOnItemClickListener(new AdapterView.OnItemClickListener()
	        {

	          public void onItemClick(AdapterView<?> arg0, View arg1,
	              int arg2, long arg3)
	          {
	            /* ���I���ɦW�ɱN�R���μ�����sEnable */
	            play.setEnabled(true);
	            delete.setEnabled(true);

	            myPlayFile = new File(myRecAudioDir.getAbsolutePath()
	                + File.separator
	                + ((CheckedTextView) arg1).getText());
	            status.setText("�A�諸�O�G"
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
	            //�u��.amr�ɮ� 
	            String fileS = files[i].getName().substring(files[i].getName().indexOf("."));
	                
	            if (fileS.toLowerCase().equals(".amr"))
	              recordFiles.add(files[i].getName());

	          }
	        }
	      }
	    }
	  }

	  /* �}�Ҽ�������ɪ��{�� */
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
