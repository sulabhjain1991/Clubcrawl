package com.linchpin.clubcrawl.jobjects.inbox;


public class GSResult implements Comparable
{
	private String	searched_id;
	private String	searched_image;
	private String	searched_name;
	private String	searched_type;
	private String	user_followed;

	public String getSearched_id()
	{
		return this.searched_id;
	}

	public void setSearched_id(String searched_id)
	{
		this.searched_id = searched_id;
	}

	public String getSearched_image()
	{
		return this.searched_image;
	}

	public void setSearched_image(String searched_image)
	{
		this.searched_image = searched_image;
	}

	public String getSearched_name()
	{
		return this.searched_name;
	}

	public void setSearched_name(String searched_name)
	{
		this.searched_name = searched_name;
	}

	public String getSearched_type()
	{
		return this.searched_type;
	}

	public void setSearched_type(String searched_type)
	{
		this.searched_type = searched_type;
	}

	public String getUser_followed()
	{
		return this.user_followed;
	}

	public void setUser_followed(String user_followed)
	{
		this.user_followed = user_followed;
	}

	@Override
	public int compareTo(Object arg0)
	{
		return this.getSearched_name().compareTo(((GSResult) arg0).getSearched_name());
	}
}
