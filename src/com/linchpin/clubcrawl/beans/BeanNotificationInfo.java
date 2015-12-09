package com.linchpin.clubcrawl.beans;

public class BeanNotificationInfo {

	private String user_name;
	private String user_image;
	private String notification_date;
	private String image; 
	private String msg,message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private String post_type;
	private String post_img;
	private String notification_id;
	private String user_id;
	private String type;
	private String friendId;
	private String status;
	private String f_status;
	
	
	
	
	
	public String getPost_type() {
		return post_type;
	}
	public void setPost_type(String post_type) {
		this.post_type = post_type;
	}
	public String getPost_img() {
		return post_img;
	}
	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}
	public String getF_status() {
		return f_status;
	}
	public void setF_status(String f_status) {
		this.f_status = f_status;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private boolean isFriendRequestSent;
	
	public boolean isFriendRequestSent() {
		return isFriendRequestSent;
	}
	public void setFriendRequestSent(boolean isFriendRequestSent) {
		this.isFriendRequestSent = isFriendRequestSent;
	}
	public String getFriendId() {
		return friendId;
	}
	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_image() {
		return user_image;
	}
	public void setUser_image(String user_image) {
		this.user_image = user_image;
	}
	public String getNotification_date() {
		return notification_date;
	}
	public void setNotification_date(String notification_date) {
		this.notification_date = notification_date;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getNotification_id() {
		return notification_id;
	}
	public void setNotification_id(String notification_id) {
		this.notification_id = notification_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
}
