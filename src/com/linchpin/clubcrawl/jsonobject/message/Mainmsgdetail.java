
package com.linchpin.clubcrawl.jsonobject.message;


public class Mainmsgdetail{
   	private String created_at;
   	private String message;
   	private String image="";
   	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	private String receiver_id;
   	private String sender_id;
   	private String id;
   	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private boolean status=true;

 	public boolean isStatus()
	{
		return status;
	}
	public void setStatus(boolean status)
	{
		this.status = status;
	}
	public String getCreated_at(){
		return this.created_at;
	}
	public void setCreated_at(String created_at){
		this.created_at = created_at;
	}
 	public String getMessage(){
		return this.message;
	}
	public void setMessage(String message){
		this.message = message;
	}
 	public String getReceiver_id(){
		return this.receiver_id;
	}
	public void setReceiver_id(String receiver_id){
		this.receiver_id = receiver_id;
	}
 	public String getSender_id(){
 		
		return this.sender_id;
	}
	public void setSender_id(String sender_id){
		
		this.sender_id = sender_id;
	}
}
