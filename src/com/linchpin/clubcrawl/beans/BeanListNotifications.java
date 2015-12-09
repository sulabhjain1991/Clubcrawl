package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListNotifications {

	private List<BeanNotificationInfo> result;
	private String hasmore;

	public String getHasmore() {
		return hasmore;
	}

	public void setHasmore(String hasmore) {
		this.hasmore = hasmore;
	}

	public List<BeanNotificationInfo> getResult() {
		return result;
	}

	public void setResult(List<BeanNotificationInfo> result) {
		this.result = result;
	}
	
}
