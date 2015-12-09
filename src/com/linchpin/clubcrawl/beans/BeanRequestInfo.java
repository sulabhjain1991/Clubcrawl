
package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanRequestInfo{
   	private String created_at;
   	private String first_name;
   	private String id;
   	private String last_name;
   	private String profile_pic;
   	private String related_user;
   	private String relating_user;
   	private String status;
   	private String username;

 	public String getCreated_at(){
		return this.created_at;
	}
	public void setCreated_at(String created_at){
		this.created_at = created_at;
	}
 	public String getFirst_name(){
		return this.first_name;
	}
	public void setFirst_name(String first_name){
		this.first_name = first_name;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
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
 	public String getRelated_user(){
		return this.related_user;
	}
	public void setRelated_user(String related_user){
		this.related_user = related_user;
	}
 	public String getRelating_user(){
		return this.relating_user;
	}
	public void setRelating_user(String relating_user){
		this.relating_user = relating_user;
	}
 	public String getStatus(){
		return this.status;
	}
	public void setStatus(String status){
		this.status = status;
	}
 	public String getUsername(){
		return this.username;
	}
	public void setUsername(String username){
		this.username = username;
	}
}
