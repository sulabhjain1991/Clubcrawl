package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListFriends 
{
	private BeanUserInfo user;
	private List<BeanFriendInfo> friends;
	private BeanFbId facebook;
	public BeanUserInfo getUser() {
		return user;
	}
	public void setUser(BeanUserInfo user) {
		this.user = user;
	}
	public List<BeanFriendInfo> getFriends() {
		return friends;
	}
	public void setFriends(List<BeanFriendInfo> friends) {
		this.friends = friends;
	}
	public BeanFbId getFacebook() {
		return facebook;
	}
	public void setFacebook(BeanFbId facebook) {
		this.facebook = facebook;
	}

}
