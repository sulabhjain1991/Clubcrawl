package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListInvites {

	private List<BeanInviteInfo> events;
	private String status;
	public List<BeanInviteInfo> getEvents() {
		return events;
	}
	public void setEvents(List<BeanInviteInfo> events) {
		this.events = events;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
