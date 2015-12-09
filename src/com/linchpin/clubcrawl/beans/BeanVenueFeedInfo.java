
package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanVenueFeedInfo{
   	private String date;
   	private String is_liked;
   	private String post;
   	private String post_id;
   	private String post_img;
   	private String total_like;
   	private String venue_id;
   	private String venue_img;
   	private String venue_name;

 	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date = date;
	}
 	public String getIs_liked(){
		return this.is_liked;
	}
	public void setIs_liked(String is_liked){
		this.is_liked = is_liked;
	}
 	public String getPost(){
		return this.post;
	}
	public void setPost(String post){
		this.post = post;
	}
 	public String getPost_id(){
		return this.post_id;
	}
	public void setPost_id(String post_id){
		this.post_id = post_id;
	}
 	public String getPost_img(){
		return this.post_img;
	}
	public void setPost_img(String post_img){
		this.post_img = post_img;
	}
 	public String getTotal_like(){
		return this.total_like;
	}
	public void setTotal_like(String total_like){
		this.total_like = total_like;
	}
 	public String getVenue_id(){
		return this.venue_id;
	}
	public void setVenue_id(String venue_id){
		this.venue_id = venue_id;
	}
 	public String getVenue_img(){
		return this.venue_img;
	}
	public void setVenue_img(String venue_img){
		this.venue_img = venue_img;
	}
 	public String getVenue_name(){
		return this.venue_name;
	}
	public void setVenue_name(String venue_name){
		this.venue_name = venue_name;
	}
}
