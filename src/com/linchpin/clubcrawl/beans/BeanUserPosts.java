package com.linchpin.clubcrawl.beans;

public class BeanUserPosts 
{
	private String id;
	private String status;
	private String image;
	private String created_at;
	private String total_likes = "0";
	private String is_liked="0";
	public String getTotal_likes() {
		return total_likes;
	}
	public void setTotal_likes(String total_likes) {
		this.total_likes = total_likes;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getIs_liked() {
		return is_liked;
	}
	public void setIs_liked(String is_liked) {
		this.is_liked = is_liked;
	}
	
	

}
