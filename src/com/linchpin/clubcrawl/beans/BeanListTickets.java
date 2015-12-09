package com.linchpin.clubcrawl.beans;

import java.util.ArrayList;
import java.util.List;

public class BeanListTickets
{

	private String						error;
	private String						pagecount;
	private ArrayList<BeanTicketsInfo>	results;
	private String						totalcount;

	public String getError()
	{
		return this.error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public String getPagecount()
	{
		return this.pagecount;
	}

	public void setPagecount(String pagecount)
	{
		this.pagecount = pagecount;
	}

	public ArrayList<BeanTicketsInfo> getResults()
	{
		return this.results;
	}

	public void setResults(ArrayList<BeanTicketsInfo> results)
	{
		this.results = results;
	}

	public String getTotalcount()
	{
		return this.totalcount;
	}

	public void setTotalcount(String totalcount)
	{
		this.totalcount = totalcount;
	}

}
