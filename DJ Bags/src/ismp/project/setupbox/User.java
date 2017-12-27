package ismp.project.setupbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String password;
	private int request;
	private String state;
	private ArrayList<HashMap<String,Object>> list;
	
	public User(){}
	
	public User(int urequest, String uid, String upassword) {
		id = uid;
		password = upassword;
		request = urequest;
	}
	
	public User(int urequest, String uid){
		request  = urequest;
		id = uid;
		
	}
	
	public User(int urequest){
		request = urequest;
	}
	
   public void setId(String uid) {
	   id = uid;
   }
   
   public String getId() {
	   return id;
   }
   
   public void setPassword(String upassword) {
	   password = upassword;
   }
   
   public String getPassword() {
	   return password;
   }
   
   public void setRequest(int value) {
	   request = value;
   }
   
   public int getRequest() {
	   return request;
   }
   
   public void setState(String s){
	   state = s ;
   }
   
   public String getState(){
	   return state;
   }
   
   public void setStation(ArrayList<HashMap<String,Object>> listStation){
	   list = listStation;
	   
   }
   
   public ArrayList<HashMap<String,Object>> getStation(){
	   return list;
   }
}
