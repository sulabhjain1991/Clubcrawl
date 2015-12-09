package com.linchpin.clubcrawl.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences
{

	private static final String		APP_SHARED_PREFS	= "com.linchpin.clubcrawl"; //  Name of the file -.xml
	private SharedPreferences		appSharedPrefs;
	private Editor					prefsEditor;
	Context							con;
	private static AppPreferences	appPreferences;

	public static AppPreferences getInstance(Context context)
	{
		if (appPreferences == null) appPreferences = new AppPreferences(context);
		return appPreferences;
	}

	public AppPreferences(Context context)
	{
		this.con = context;
		this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}

	public boolean getIsAdPurchased()
	{
		return appSharedPrefs.getBoolean("AdPurchased", false);
	}
	public void setAdPurchased(boolean adPurchased)
	{
		prefsEditor.putBoolean("adPurchsed", adPurchased);
		prefsEditor.commit();
	}
	public String getCityId()
	{
		return appSharedPrefs.getString("cityId", "1");
	}

	public void setCityId(String cityId)
	{
		prefsEditor.putString("cityId", cityId);
		prefsEditor.commit();

	}

	public String getimageUrl()
	{
		return appSharedPrefs.getString("imageUrl", "");
	}

	public void setimageUrl(String imageUrl)
	{
		prefsEditor.putString("imageUrl", imageUrl);
		prefsEditor.commit();

	}

	public String getCityName()
	{
		return appSharedPrefs.getString("cityName", "London");
	}

	public void setCityName(String cityName)
	{
		prefsEditor.putString("cityName", cityName);
		prefsEditor.commit();

	}

	public String getUserId()
	{
		return appSharedPrefs.getString("userId", "");
	}

	public void setUserId(String userId)
	{
		prefsEditor.putString("userId", userId);
		prefsEditor.commit();

	}

	public boolean getPaidStatus()
	{
		return appSharedPrefs.getBoolean("paidStatus", false);
	}

	public void setPaidStatus(Boolean paidStatus)
	{
		prefsEditor.putBoolean("paidStatus", paidStatus);
		prefsEditor.commit();

	}
	
	public boolean getSneakStatus()
	{
		return appSharedPrefs.getBoolean("sneakStatus", false);
	}

	public void setSneakStatus(Boolean paidStatus)
	{
		prefsEditor.putBoolean("sneakStatus", paidStatus);
		prefsEditor.commit();

	}

	public int getInvitedFriendsNumber()
	{
		return appSharedPrefs.getInt("invitedFriendsNumber", 0);
	}

	public void setInvitedFriendsNumber(int invitedFriendsNumber)
	{
		prefsEditor.putInt("invitedFriendsNumber", invitedFriendsNumber);
		prefsEditor.commit();

	}

	public String getInvitedFriendsNames()
	{
		return appSharedPrefs.getString("invitedFriendsNames", "");
	}

	public void setInvitedFriendsNames(String invitedFriendsNames)
	{
		prefsEditor.putString("invitedFriendsNames", invitedFriendsNames);
		prefsEditor.commit();

	}
	
	public void clearPref()
	{
		prefsEditor.clear();
		prefsEditor.commit();
	}

}
