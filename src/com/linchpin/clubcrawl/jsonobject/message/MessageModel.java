package com.linchpin.clubcrawl.jsonobject.message;

import java.util.ArrayList;

public class MessageModel
{
	private ArrayList<Mainmsgdetail>	mainmsgdetail;
	private String						status;

	public ArrayList<Mainmsgdetail> getMainmsgdetail()
	{
		return this.mainmsgdetail;
	}

	public void setMainmsgdetail(ArrayList<Mainmsgdetail> mainmsgdetail)
	{
		this.mainmsgdetail = mainmsgdetail;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}
