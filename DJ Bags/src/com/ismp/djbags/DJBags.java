/**
 * @(#)DataCollection.java        1.0.1 11/11/30
 * All rights reserved.
 * 
 * This software is social music broadcasting platform.
 */
package com.ismp.djbags;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * @version	1.0.1 30 Sep. 2011
 * @author	lingyu
 *
 */

public class DJBags extends Activity {
    
	private ImageButton option1, option2; //主頁面的二個功能: 電台聽聽和DJ專區   
	protected static final int MENU_ABOUT = Menu.FIRST;
    protected static final int MENU_FEEDBACK = Menu.FIRST + 1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViews();     // 背景顯示物件
        setListeners();  // 監聽按鍵是否被觸發
        
        Global  global = ((Global)getApplicationContext());
    	global.g_state = "close";
    }
    
    private void findViews() {  	
        option1 = (ImageButton) findViewById(R.id.option1); // 電台聽聽
        option2 = (ImageButton) findViewById(R.id.option2); // DJ 專區
    }
    
    private void setListeners() {        	
    	option1.setOnTouchListener(audio);
    	option2.setOnTouchListener(dj);   	
    }
    
    private ImageButton.OnTouchListener audio = new ImageButton.OnTouchListener() {
            
    	public boolean onTouch(View v, MotionEvent event) {   
            
    		if(event.getAction() == MotionEvent.ACTION_DOWN) {                                     
                    Intent intent = new Intent();                
                    intent.setClass(DJBags.this, Station.class); //Switch to Station page
                    startActivity(intent);
            }
    		
            return false;   
    	}   
    };
    
    private ImageButton.OnTouchListener dj = new ImageButton.OnTouchListener() {  	
   
    	public boolean onTouch(View v, MotionEvent event) {   
           
    		if(event.getAction() == MotionEvent.ACTION_DOWN){                                      
                    Intent intent = new Intent();                
                    intent.setClass(DJBags.this, Login.class); //Switch to Login page
                    startActivity(intent);
            }
    		
            return false;   
    	}   
    };   
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, MENU_ABOUT, 0, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details); 
    	menu.add(0, MENU_FEEDBACK, 0, R.string.menu_feedback).setIcon(android.R.drawable.ic_menu_send); 
    
    	return true;   
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	super.onOptionsItemSelected(item);
    	
    	switch(item.getItemId()){
    		
    		case MENU_ABOUT:
    			openOptionsDialog();  //系統簡介
    			break;
    		case MENU_FEEDBACK:       //意見回饋
    			Intent mEmailIntent = new Intent(android.content.Intent.ACTION_SEND);  
 		        mEmailIntent.setType("plain/text");	 		              		       
		        mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"lingyu@ismp.csie.ncku.deu.tw"});  		       
		        mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DJ Bags 意見回饋");
		        startActivity(Intent.createChooser(mEmailIntent, getResources().getString(R.string.str_message)));  
    			break;	
    	}
    	
    	return true;
    }
    
    private void openOptionsDialog(){
    	new AlertDialog.Builder(DJBags.this)
    	.setTitle(R.string.about)
    	.setMessage(R.string.introduction)
    	.setPositiveButton(android.R.string.ok,
    			new DialogInterface.OnClickListener(){
    				public void onClick( DialogInterface dialoginterface, int i){}	
    	}).show();
    }
    

}