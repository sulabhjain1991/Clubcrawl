package com.linchpin.clubcrawl.beans;

public class BeanNearByClubList 
{
	private String id;
	private String is_free_code;
	private String name;
	private String address;
	private String address_lat;
	private String address_lng;
	private String logo;
	private String distance;
	private String follow="";
	private String follow_status="";
	public String getFollow_status() {
		return follow_status;
	}
	public void setFollow_status(String follow_status) {
		this.follow_status = follow_status;
	}
	public String getFollow()
	{
		return follow;
	}
	public void setFollow(String follow)
	{
		this.follow = follow;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIs_free_code() {
		return is_free_code;
	}
	public void setIs_free_code(String is_free_code) {
		this.is_free_code = is_free_code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress_lat() {
		return address_lat;
	}
	public void setAddress_lat(String address_lat) {
		this.address_lat = address_lat;
	}
	public String getAddress_lng() {
		return address_lng;
	}
	public void setAddress_lng(String address_lng) {
		this.address_lng = address_lng;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
}
