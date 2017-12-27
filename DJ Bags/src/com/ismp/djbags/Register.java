package com.ismp.djbags;

import ismp.project.setupbox.testclass;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Register extends Activity{
	
	EditText id, pw, pwagain, email;
	ImageButton next;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.register);
	    findViews();
	    listener();  
	}
	 
	private void findViews(){
		 
		id = (EditText)findViewById(R.id.useridtext);         //id
		pw = (EditText)findViewById(R.id.pwtext);             //password
		pwagain = (EditText)findViewById(R.id.pwagaintext);   //enter password again
		email = (EditText)findViewById(R.id.emailtext);       //email
		next = (ImageButton)findViewById(R.id.nextstep);      //next stop    
		
		pw.setTransformationMethod(PasswordTransformationMethod.getInstance()); //mask password
		pwagain.setTransformationMethod(PasswordTransformationMethod.getInstance()); //mask password 
		next.setClickable(false); 
	}
	 
    private void listener(){
    	/*
    	pw.addTextChangedListener(new TextWatcher(){  //EditBox 監聽
    		@Override
    		public void afterTextChanged(Editable s) {
    		
    		}
    		@Override
    		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
    	
    		}
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
    			if(connect() == 1){ // 有人註冊過   //為什麼要按了才通知?要及時檢查才對!
            		Toast.makeText(Register.this, R.string.id_repeat, Toast.LENGTH_SHORT).show(); //Show error message 
            		next.setClickable(false);		 
                }
    	}});*/
		 
		next.setOnClickListener(new View.OnClickListener() {			 
            public void onClick(View v) {   
            	 
            	//檢查欄位是否填寫完整
            	if(check()){            	
            		Intent intent = new Intent();
	    			intent.setClass(Register.this, RegisterStation.class);  
	    			Bundle bundle = new Bundle();
	    			bundle.putString("NAME", id.getText().toString());
	    			bundle.putString("PASSWORD", pw.getText().toString());
	    			bundle.putString("EMAIL", email.getText().toString());
	    			intent.putExtras(bundle);
	    			startActivity(intent);                 
                }
            }
	     });
	 }
	 
	 private boolean check(){
		 
		 if(id.getText().toString().equals("") || pw.getText().toString().equals("") || pwagain.getText().toString().equals("")||email.getText().toString().equals("")){
			 Toast popup = Toast.makeText(Register.this, R.string.error_null, Toast.LENGTH_SHORT); //Show error message 
         	 popup.show();
         	 return false;
		 }else if (!(pwagain.getText().toString().equals(pw.getText().toString()))){
			 Toast popup = Toast.makeText(Register.this, R.string.error_pw + pw.getText().toString() + pwagain.getText().toString(), Toast.LENGTH_SHORT); //Show error message 
         	 popup.show();
         	 return false; 
		 }else if(isEmail(email.getText().toString())){
			 Toast popup = Toast.makeText(Register.this, R.string.error_email, Toast.LENGTH_SHORT); //Show error message 
         	 popup.show();
         	 return false; 
		 }else{
			 next.setClickable(true); //enable to next step button 
			 return true;
		 }
	 }
	 
	 public static boolean isEmail(String strEmail){  
	 
	    String strPattern = "^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$"; 
	    Pattern p = Pattern.compile(strPattern); 
	    Matcher m = p.matcher(strEmail); 
	    
	    return m.matches();
	    
	 } 
	 
     private int connect(){         //Connect to Database & check//這應該另寫一個傳送 class
    	ObjectOutputStream output = null;
    	ObjectInputStream input = null;
    	Socket clientSocket = null;
    	
    	testclass tc = new testclass(); //用來傳送給server的物件
    	testclass tced = null; //用來儲存收到的測試物件
     	
     	tc.setP(1);
     	tc.setName( id.getText().toString());
     	tc.setPassword(pw.getText().toString());
     	
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
        		if(tced.getP() == 1) return 1;	  
	            
        		return 0;
			}catch(IOException ioe){
        		ioe.printStackTrace();
        	}catch (ClassNotFoundException e) {			
				e.printStackTrace();
			}  
		}
	 } 
}	
