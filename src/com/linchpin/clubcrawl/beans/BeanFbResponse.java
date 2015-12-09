package com.linchpin.clubcrawl.beans;

public class BeanFbResponse extends Response
{
	private String		userstatus;
	private String		error;
	private String		user_id;
	private BeanUser	facebook;
	private String		profile_picture;
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProfile_picture()
	{
		return profile_picture;
	}

	public void setProfile_picture(String profile_picture)
	{
		this.profile_picture = profile_picture;
	}

	public BeanUser getFacebook()
	{
		return facebook;
	}

	public void setFacebook(BeanUser facebook)
	{
		this.facebook = facebook;
	}

	public String getUser_id()
	{
		return user_id;
	}

	public void setUser_id(String user_id)
	{
		this.user_id = user_id;
	}

	public String getUserstatus()
	{
		return userstatus;
	}

	public void setUserstatus(String userstatus)
	{
		this.userstatus = userstatus;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	private String	first_name;

	private String	last_name;

	public String getFirst_name()
	{
		return first_name;
	}

	public void setFirst_name(String first_name)
	{
		this.first_name = first_name;
	}

	public String getLast_name()
	{
		return last_name;
	}

	public void setLast_name(String last_name)
	{
		this.last_name = last_name;
	}

}
