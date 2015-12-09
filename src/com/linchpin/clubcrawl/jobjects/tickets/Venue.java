
package com.linchpin.clubcrawl.jobjects.tickets;

import java.util.List;

public class Venue{
   	private String address;
   	private String id;
   	private String latitude;
   	private String longitude;
   	private String name;
   	private String phone;
   	private String postcode;
   	private String town;
   	private String type;

 	public String getAddress(){
		return this.address;
	}
	public void setAddress(String address){
		this.address = address;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
 	public String getLatitude(){
		return this.latitude;
	}
	public void setLatitude(String latitude){
		this.latitude = latitude;
	}
 	public String getLongitude(){
		return this.longitude;
	}
	public void setLongitude(String longitude){
		this.longitude = longitude;
	}
 	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
 	public String getPhone(){
		return this.phone;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
 	public String getPostcode(){
		return this.postcode;
	}
	public void setPostcode(String postcode){
		this.postcode = postcode;
	}
 	public String getTown(){
		return this.town;
	}
	public void setTown(String town){
		this.town = town;
	}
 	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type = type;
	}
}
