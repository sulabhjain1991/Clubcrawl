package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListVenueInfo extends BeanError
{
	
	private List<BeanVenueInfo> venueList;
	public List<BeanVenueInfo> getVenueList() {
		return venueList;
	}
	public void setVenueList(List<BeanVenueInfo> venueList) {
		this.venueList = venueList;
	}

}
