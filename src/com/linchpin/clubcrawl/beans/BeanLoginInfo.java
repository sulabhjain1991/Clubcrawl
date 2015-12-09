package com.linchpin.clubcrawl.beans;

public class BeanLoginInfo extends BeanResponse
{
	
	private String useremail;
	
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public BeanUser getUserdata() {
		return userdata;
	}
	public void setUserdata(BeanUser userdata) {
		this.userdata = userdata;
	}
	private BeanUser userdata;

}
