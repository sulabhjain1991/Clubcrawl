
package com.linchpin.clubcrawl.jobjects.inbox;


public class InboxResult{
   	private String conversation_id;
   	private String created_at;
   	private String display_user_id;
   	private String first_name;
   	private String is_logged_in="1";
   	private String last_name;
   	private String profile_pic;
   	private String subject="";
   private String last_logged_at;

 	public String getLast_logged_at() {
	return last_logged_at;
}
public void setLast_logged_at(String last_logged_at) {
	this.last_logged_at = last_logged_at;
}
	public String getConversation_id(){
		return this.conversation_id;
	}
	public void setConversation_id(String conversation_id){
		this.conversation_id = conversation_id;
	}
 	public String getCreated_at(){
		return this.created_at;
	}
	public void setCreated_at(String created_at){
		this.created_at = created_at;
	}
 	public String getDisplay_user_id(){
		return this.display_user_id;
	}
	public void setDisplay_user_id(String display_user_id){
		this.display_user_id = display_user_id;
	}
 	public String getFirst_name(){
		return this.first_name;
	}
	public void setFirst_name(String first_name){
		this.first_name = first_name;
	}
 	public String getIs_logged_in(){
		return this.is_logged_in;
	}
	public void setIs_logged_in(String is_logged_in){
		this.is_logged_in = is_logged_in;
	}
 	public String getLast_name(){
		return this.last_name;
	}
	public void setLast_name(String last_name){
		this.last_name = last_name;
	}
 	public String getProfile_pic(){
		return this.profile_pic;
	}
	public void setProfile_pic(String profile_pic){
		this.profile_pic = profile_pic;
	}
 	public String getSubject(){
		return this.subject;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
}
