package com.linchpin.clubcrawl.beans;

public class BeanFriendInfo 
{
	private String ID;
	private String first_name;
	private String last_name;
	private String profile_pic;
	private String relation;
	private String id;
	private String fbid;
	private boolean isSelected;
	private String fullName;
	private String is_logged_in;
	private String message;
	private String msg_time;
	public String getIs_logged_in() {
		return is_logged_in;
	}
	public void setIs_logged_in(String is_logged_in) {
		this.is_logged_in = is_logged_in;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMsg_time() {
		return msg_time;
	}
	public void setMsg_time(String msg_time) {
		this.msg_time = msg_time;
	}
	public String getFullName() {
		return getFirst_name()+" " + getLast_name();
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getFbid() {
		return fbid;
	}
	public void setFbid(String fbid) {
		this.fbid = fbid;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getProfile_pic() {
		return profile_pic;
	}
	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}

}
