package com.ismp.djbags;

import ismp.project.setupbox.testclass;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterStation extends Activity {
	EditText e_station,e_statement;
	Button selectImg;
	ImageView thumbnail;
	ImageButton send;
	private static final int ACTIVITY_SELECT_IMAGE = 3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_station);
        findViews();
        listener();  
	}

	private void findViews(){
		 e_station = (EditText)findViewById(R.id.stationtext);
		 e_statement = (EditText)findViewById(R.id.statementtext);
		 selectImg = (Button)findViewById(R.id.button_select);
		 send = (ImageButton)findViewById(R.id.button_submit);	
		 send.setClickable(false);
	}	
	
	private void listener(){
        selectImg.setOnClickListener(new View.OnClickListener() { //點擊選擇圖片的按鈕
		    public void onClick(View v) {
		    	Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		    	startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
		    }
        });
	        
        send.setOnClickListener(new View.OnClickListener() {    //點擊傳送的按鈕
            public void onClick(View v) {
            	if(check() && connect() == 1){        		            	
	            	Toast.makeText(RegisterStation.this, R.string.register_ok, Toast.LENGTH_SHORT).show();
	                Intent intent = new Intent();                //Switch to login page
                    intent.setClass(RegisterStation.this, Login.class);  
                    startActivity(intent);
            	}   
            }
        });		 
	 } 
	 
	 private boolean check(){
		 
		 if(e_station.getText().toString().equals("")){
			 Toast popup = Toast.makeText(RegisterStation.this, R.string.error_null, Toast.LENGTH_SHORT); //Show error message 
         	 popup.show(); 
         	 
         	 return false; 
		 }else{
			 return true;
		 }		 
	 }
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data){ //這呼叫圖檔的地方要再研究，根本不知道他在寫什麼，也不能上傳到server

	        super.onActivityResult(requestCode, resultCode, data);
	        
	        // 有選擇檔案
	        if ( resultCode == RESULT_OK ){
	            // 取得檔案的 Uri
	            Uri uri = data.getData();
	            if( uri != null ){
	                ImageView iv = (ImageView)this.findViewById(R.id.thumbnail_show);  // 利用 Uri 顯示 ImageView 圖片
	                iv.setImageURI(uri);
	                //Toast.makeText(EditInfo.this, uri.toString(), Toast.LENGTH_SHORT).show(); 
	                setTitle( uri.toString());   
	                send.setClickable(true);
	                /*
	                try{
	                	File file = new File(uri.toString()); 
		                int length = (int) file.length(); 
		                InputStream fin = new FileInputStream(file); 
		                InputStream is = null;
		                byte[] buffer = new byte[4096*100];
		                int size = is.read(buffer);
                        is.read(buffer);
	                }catch(IOException e) { 
	                    e.printStackTrace(); 
	                }*/  
	            }	           
	        }	            
	        else{
	            Toast.makeText(RegisterStation.this, "fail", Toast.LENGTH_SHORT).show();
	        }
	 }	       
 
	 private int connect(){
		    ObjectOutputStream output = null;
	     	ObjectInputStream input = null;
	     	Socket clientSocket = null;

	     	testclass tc = new testclass(); //用來傳送給server的測試物件
	     	testclass tced = null; //用來儲存收到的測試物件
	     	
	     	Bundle bundle = getIntent().getExtras();
	    	String name = bundle.getString("NAME");
	    	String password = bundle.getString("PASSWORD");
	    	String email = bundle.getString("EMAIL");
	    	
	     	tc.setP(2);	     	
	     	tc.setName(name);
	     	tc.setPassword(password);
	     	tc.setEmail(email);
	     	tc.setAudioTile(e_station.getText().toString());
	     	tc.setAudioDis(e_statement.getText().toString());	     	

	    	try{
	    		clientSocket = new Socket("140.116.82.63", 9000);   //create a socket to server
	    		output = new ObjectOutputStream(clientSocket.getOutputStream());  //產生資料送出串流
	    		output.writeObject(tc); //傳送物件給server
	    		output.flush();  //沖馬桶   //功用不明?    		    		
	    	}
	    	catch(IOException ioe){
	    		ioe.printStackTrace();
	    	}
	    	
	    	while(true){
				try{ 
					input = new ObjectInputStream(clientSocket.getInputStream()); 	  //產生資料接收串流    
	        		tced = (testclass) input.readObject(); //接收server傳來的物件        		
	        		input.close();
	        		output.close();
	        		clientSocket.close();
	        		if(tced.getP() == 1) return 1;	  //if id and password match, tecd.x == 1
		            
	        		return 0;
				}catch(IOException ioe){
	        		ioe.printStackTrace();
	        	}catch (ClassNotFoundException e) {			
					e.printStackTrace();
				}  
			}
	 } 
}
