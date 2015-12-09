package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListFeeds {

	
	private List<BeanFeedInfo> feedList;
	private String hasmore;
	public List<BeanFeedInfo> getFeedList() {
		return feedList;
	}

	public void setFeedList(List<BeanFeedInfo> feedList) {
		this.feedList = feedList;
	}

	public String getHasmore() {
		return hasmore;
	}

	public void setHasmore(String hasmore) {
		this.hasmore = hasmore;
	}
		
}
