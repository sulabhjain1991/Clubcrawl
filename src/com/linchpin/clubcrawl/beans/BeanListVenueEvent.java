package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListVenueEvent 
{
	private String status;
	private List<BeanVenueEvent> events;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<BeanVenueEvent> getEvents() {
		return events;
	}
	public void setEvents(List<BeanVenueEvent> events) {
		this.events = events;
	}

}
