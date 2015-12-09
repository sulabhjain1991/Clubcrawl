package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListEventDetail {

	private List<BeanEventDetailInfo> event;
	private String status;
	public List<BeanEventDetailInfo> getEvent() {
		return event;
	}
	public void setEvent(List<BeanEventDetailInfo> event) {
		this.event = event;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
}
