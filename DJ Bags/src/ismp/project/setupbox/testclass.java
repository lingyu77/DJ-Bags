package ismp.project.setupbox;

import java.io.Serializable;

public class testclass implements Serializable{
	private static final long serialVersionUID = 1L;
	private int x = 0 ;  //�o�W�r�n��
	private String name;
	private String password;
	private String email;
	private String audioTile;
    private String audioDis;

	
	public testclass(){
	    
	}
	
	public void setP(int x){   //�W�r�M��ƦW���X
	    this.x = x;
	}
	
	public int getP(){		//�W�r�M��ƦW���X
	    return x;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return password;
	}
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getEmail(){
		return email;
	}
	public void setAudioTile(String audioTile){
		this.audioTile = audioTile;
	}
	
	public String getAudioTile(){
		return audioTile;
	}
	
	public void setAudioDis(String audioDis){
		this.audioDis = audioDis;
	}
	
	public String getAudioDis(){
		return audioDis;
	}
}