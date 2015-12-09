package com.linchpin.clubcrawl.beans;

import java.io.Serializable;

public class BeanUserInfo implements Serializable
{
	BeanUser user;

	public BeanUser getUser() {
		return user;
	}

	public void setUser(BeanUser user) {
		this.user = user;
	}
}
