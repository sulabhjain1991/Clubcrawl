package com.linchpin.clubcrawl.beans;

import java.io.Serializable;
import java.util.List;

public class BeanUser implements Serializable
{
	private String ID;
	private String first_name;
	private String last_name;
	private String username;
	private String profile_pic;
	private String age;
	private String phone;
	private String gender;
	private String marital_status;
	private String email;
	private String id;
	private String total_followers = "0";
	private String total_following_venue;
	private String total_following;
	private String fbid;
	private String followReqCount = "0";
	private String is_followed;	
	private String status_message;
	private String website="";
	private String last_logged_at;
	private String is_logged_in;
	private String by_followed;



	public String getLast_logged_at() {
		return last_logged_at;
	}
	public void setLast_logged_at(String last_logged_at) {
		this.last_logged_at = last_logged_at;
	}
	public String getIs_logged_in() {
		return is_logged_in;
	}
	public void setIs_logged_in(String is_logged_in) {
		this.is_logged_in = is_logged_in;
	}
	public String getBy_followed() {
		return by_followed;
	}
	public void setBy_followed(String by_followed) {
		this.by_followed = by_followed;
	}
	public String getIs_followed() {
		return is_followed;
	}
	public void setIs_followed(String is_followed) {
		this.is_followed = is_followed;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFbid() {
		return fbid;
	}
	public void setFbid(String fbid) {
		this.fbid = fbid;
	}
	public String getTotal_following() {
		return total_following;
	}
	public void setTotal_following(String total_following) {
		this.total_following = total_following;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getTotal_followers() {
		return total_followers;
	}
	public void setTotal_followers(String total_followers) {
		this.total_followers = total_followers;
	}
	public String getTotal_following_venue() {
		return total_following_venue;
	}
	public void setTotal_following_venue(String total_following_venue) {
		this.total_following_venue = total_following_venue;
	}
	public String getStatus_message() {
		return status_message;
	}
	public void setStatus_message(String status_message) {
		this.status_message = status_message;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private List<BeanUserPosts> post;

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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProfile_pic() {
		return profile_pic;
	}
	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}
	public String getFollowReqCount() {
		return followReqCount;
	}
	public void setFollowReqCount(String followReqCount) {
		this.followReqCount = followReqCount;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public List<BeanUserPosts> getPost() {
		return post;
	}
	public void setPost(List<BeanUserPosts> post) {
		this.post = post;
	}
	public String getMarital_status() {
		return marital_status;
	}
	public void setMarital_status(String marital_status) {
		this.marital_status = marital_status;
	}

}
