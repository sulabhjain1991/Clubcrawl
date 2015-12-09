package com.linchpin.clubcrawl.beans;

public class BeanClubsInfo {

	private String id="";
	private String name="";
	private String address="";
	private String logo="";
	private String flag="0";
	public String getFlag()
	{
		return flag;
	}
	public void setFlag(String flag)
	{
		this.flag = flag;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	
}
