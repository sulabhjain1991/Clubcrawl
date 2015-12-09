package com.linchpin.clubcrawl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class APP extends Application
{
	static APP			app	= null;
	SharedPreferences	preferences;
	Editor				editor;
	public int notificationcount,MessageCount,requestCount;

	public Editor getEditor()
	{
		return this.editor;
	}


	//convert date object into calendar object...

	private Calendar getDate(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

	public String getCalender(String timestamp)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = getDate(sdf.parse(timestamp)).getTime();
			int day = date.compareTo(getDate(new Date()).getTime());
			if (day == 0) return "Today";
			else if (day < 0)
			{
				Calendar calendar = getDate(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				day = date.compareTo(calendar.getTime());
				if (day == 0) return "Yesterday";

			}
			sdf = new SimpleDateFormat("dd MMM");
			return sdf.format(date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return "";
	}


	public final boolean isValidEmail(CharSequence target)
	{
		if (target == null)
		{
			return false;
		}
		else
		{
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	/**@author Khalid Khan*/
	public static enum PREF
	{	
		USER_DETAILS("userDetails"), 
		USER_TOTAL_POSTS("totalPosts"), 
		USER_TOTAL_FOLLOWERS("totalFollowers"), 
		USER_TOTAL_FOLLOWINGS("totalFollowings"),
		/**Latitude of selected city*/
		CITY_LAT("city_lat"),
		/** Longitude of selected city*/
		CITY_LNG("city_lng"), 
		/** List of inbox friends*/
		INBOX_LIST("inbox_lst"),
		/**Latitude of selected city*/
		DEVICE_LAT("device_lat"),
		/** Longitude of selected city*/
		DEVICE_LNG("device_lng"), 
		FB_ACCESS_EXPIRES("access_expires"),
		/**List of All cities*/
		ALL_CITIES("all_cities"),
		SNEAK_PREV("sneak_preview"),
		PURCHASED("purchased"),
		FB_ACCESS_TOKEN("access_token");
		public String	key;

		private PREF(String key)
		{
			this.key = key;
		}

	}

	public String getAppVersion()
	{

		PackageInfo pInfo = null;
		try
		{
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;

	}

	public SharedPreferences getPreferences()
	{
		if (preferences == null)
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(this);

		}
		return preferences;
	}

	public static APP GLOBAL()
	{
		if (app == null) app = new APP();
		return app;
	}

	@Override
	public void onCreate()
	{

		app = this;
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		this.editor = preferences.edit();
		super.onCreate();
	}

	public boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
