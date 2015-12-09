package com.linchpin.clubcrawl.beans;

import java.io.Serializable;

public class BeanUsersDetail implements Serializable
{
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getProfilePicId()
	{
		return profilePicId;
	}

	public void setProfilePicId(String profilePicId)
	{
		this.profilePicId = profilePicId;
	}

	private String	email;
	private String	userName;
	private String	password;
	private String	sex;
	private String	profilePicId;

	public BeanUsersDetail(String etEmail, String etUserName, String etPassword, String tvSex, String tvProfilePicId)
	{
		this.email = etEmail;
		this.userName = etUserName;
		this.password = etPassword;
		this.sex = tvSex;
		this.profilePicId = tvProfilePicId;
	}

}
