/** 
 * This is Login page.
 */
package com.ismp.djbags;

import ismp.project.setupbox.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;

public class Login extends Activity {
	   
   private EditText id, password;
   ImageButton login;
   TextView register;		

   @Override
   public void onCreate(Bundle savedInstanceState) {
	   
	 	super.onCreate(savedInstanceState);
	 	setContentView(R.layout.login);
	 	findViews();
	 	listener();
   }

   private void findViews() {
	   
	   id = (EditText) findViewById(R.id.id);
	   password = (EditText) findViewById(R.id.password);
	   login = (ImageButton)findViewById(R.id.login);  //login 
	   register = (TextView)findViewById(R.id.register);  //register 
	   
   }
   	
   private ImageButton.OnTouchListener match = new ImageButton.OnTouchListener() { // check id and password
       
    	public boolean onTouch(View v, MotionEvent event) {     		
    	   //if(!id.getText().equals("test") && !password.getText().equals("test")){   		
    		if(check()){   	
    		    Intent intent = new Intent(); //Switch to DJ page    		    
    		    Bundle bundle = new Bundle(); // Send id to DJ page
    			bundle.putString("ID", id.getText().toString()); 
    			intent.putExtras(bundle);			    
                intent.setClass(Login.this, Dj.class);
                startActivity(intent);  
                
    	   }else{	 		        
 				Toast popup = Toast.makeText(Login.this, R.string.error_login, Toast.LENGTH_SHORT); //Show error message 
            	popup.show();
 		   }    	   
            return false;   
    	}   
    };
    
    private void listener(){
    	password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //mask password 	
    	login.setOnTouchListener(match);                 // check id and password
    	/*register.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			    Intent intent = new Intent(Login.this, Register.class);  //Switch to Register page
                startActivity(intent);
			}
		});*/
    }
    /*
    private int connect(){         //Connect to Database & Check id and password   //�o���ӥt�g�@�Ӷǰe class
    	ObjectOutputStream output = null;
    	ObjectInputStream input = null;
    	Socket clientSocket = null;
    	
    	testclass tc = new testclass(); //�ΨӶǰe��server������
    	testclass tced = null; //�Ψ��x�s���쪺���ժ���
    	
    	tc.setP(1);
    	tc.setName(id.getText().toString());
    	tc.setPassword(password.getText().toString());
    	
    	try{
    		clientSocket = new Socket("140.116.82.63", 9000);   //create a socket to server
    		output = new ObjectOutputStream(clientSocket.getOutputStream());  //���͸�ưe�X��y
    		output.writeObject(tc); //�ǰe����server
    		output.flush();  //�R����   //�\�Τ���?    		    		
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    	}
    	
    	while(true){
			try{ 
				input = new ObjectInputStream(clientSocket.getInputStream()); 	  //���͸�Ʊ�����y    
        		tced = (testclass) input.readObject(); //����server�ǨӪ�����        		
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
    }*/ 
    
    private boolean check(){

    	ObjectOutputStream output = null;
    	ObjectInputStream input = null;
    	Socket clientSocket = null;
    	
    	User user = new User(1, id.getText().toString(), password.getText().toString()); //�ΨӶǰe��server������
    	User backUser = new User(); //�Ψ��x�s���쪺���ժ���
    	
    	try{
    		clientSocket = new Socket("140.116.82.49", 9000);   //create a socket to server
    		output = new ObjectOutputStream(clientSocket.getOutputStream());  //���͸�ưe�X��y
    		output.writeObject(user); //�ǰe����server
    		output.flush();  //�R����   //�\�Τ���?    		    		
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    	}
    	
    	while(true){
			try{ 
				input = new ObjectInputStream(clientSocket.getInputStream()); 	  //���͸�Ʊ�����y    
				backUser = (User) input.readObject(); //����server�ǨӪ�����        		
        		input.close();
        		output.close();
        		clientSocket.close();
        		if(backUser.getRequest() != 0) return true;	  
        		else return false;      		
			}catch(Exception e){        				
				e.printStackTrace();
			}  
		}
    }
}	   
	   
	   
	   

