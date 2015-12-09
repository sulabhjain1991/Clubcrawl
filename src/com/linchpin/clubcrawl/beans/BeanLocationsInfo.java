package com.linchpin.clubcrawl.beans;

public class BeanLocationsInfo
{
	private String		locations_id;
	private String	locations_name;
	private String	locations_logo;

	public String getLocations_id()
	{
		return locations_id;
	}

	public void setLocations_id(String locations_id)
	{
		this.locations_id = locations_id;
	}

	public String getLocations_name()
	{
		return locations_name;
	}

	public void setLocations_name(String locations_name)
	{
		this.locations_name = locations_name;
	}

	public String getLocations_logo()
	{
		return locations_logo;
	}

	public void setLocations_logo(String locations_logo)
	{
		this.locations_logo = locations_logo;
	}

	public BeanLocationsInfo(String locations_id, String locations_name, String locations_logo)
	{

		this.locations_id = locations_id;
		this.locations_name = locations_name;
		this.locations_logo = locations_logo;
	}

}
