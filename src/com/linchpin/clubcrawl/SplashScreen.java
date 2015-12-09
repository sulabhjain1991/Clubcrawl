package com.linchpin.clubcrawl;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends Activity implements Result,DoinBackgroung
{
	private final int USER_DETAIL = 0;
	private final int USER_POSTS = 1;
	private final int USER_FOLLOWERS = 2;
	private final int USER_FRIENDS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		Handler handler = new Handler();
		final AppPreferences appPref = new AppPreferences(SplashScreen.this);

		if(!ConstantUtil.isNetworkAvailable(this))
		{
			Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
		}

		else {

			if(!appPref.getUserId().equals(""))
				callBackgroundTasks();
			handler.postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					if(appPref.getUserId().equals(""))
					{
						Intent i = new Intent(SplashScreen.this, LoginScreen.class);
						startActivity(i);
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
						finish();
					}
					else
					{
						Intent i = new Intent(SplashScreen.this, MainScreen.class);
						startActivity(i);
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
						finish();
					}

				}
			}, 2000);
			//new WaitingTask(this).execute();
		}
	}

	private class WaitingTask extends AsyncTask<Void, String, String>
	{
		Context	con;

		public WaitingTask(Context con)
		{
			this.con = con;
		}

		@Override
		protected String doInBackground(Void... params)
		{

			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			Intent i = new Intent(SplashScreen.this, SplashScreen.class);
			startActivity(i);
			finish();
			super.onPostExecute(result);
		}

	}

	private void callBackgroundTasks() {
		AppPreferences appPref = new AppPreferences(SplashScreen.this);
		NetworkTask MyProfileTask = new NetworkTask(SplashScreen.this,
				USER_DETAIL);
		MyProfileTask.setProgressDialog(false);
		MyProfileTask.exposeDoinBackground(SplashScreen.this);
		String userDetailsUrl = ConstantUtil.userProfileDetail
				+ appPref.getUserId();
		MyProfileTask.execute(userDetailsUrl);

		NetworkTask TotalPosts = new NetworkTask(SplashScreen.this, USER_POSTS);
		TotalPosts.setProgressDialog(false);
		TotalPosts.exposeDoinBackground(SplashScreen.this);
		String userPostsUrl = ConstantUtil.userTotalPostsUrl + "user_id="
				+ appPref.getUserId();
		TotalPosts.execute(userPostsUrl);

	}

	@SuppressWarnings("null")
	@Override
	public String doInBackground(Integer id, String... params) {

		synchronized (id) {
			synchronized (params) {

				Gson gson = new Gson();

				switch (id) {
				case USER_DETAIL:
					String urlUserDetails = params[0];
					String responseStringUserDetails = ConstantUtil
							.http_connection(urlUserDetails);
					if (responseStringUserDetails != null
							|| !responseStringUserDetails.equals(""))
						APP.GLOBAL()
						.getEditor()
						.putString(APP.PREF.USER_DETAILS.key,
								responseStringUserDetails).commit();
					MenuFragment.beanUserInfo = (BeanUserInfo)(gson.fromJson(APP.GLOBAL().getPreferences().getString(APP.PREF.USER_DETAILS.key, ""), BeanUserInfo.class));
/*					else
					{
						Toast.makeText(getApplicationContext(),"Error in API's", Toast.LENGTH_LONG).show();
					}
					*/
					
					break;
				case USER_POSTS:
					String urlPosts = params[0];
					String responseStringPosts = ConstantUtil
							.http_connection(urlPosts);
					if (responseStringPosts != null
							|| !responseStringPosts.equals("")) {

						try {
							JSONObject obj = new JSONObject(responseStringPosts);
							JSONArray array = obj.getJSONArray("post");

							for (int i = 0; i < array.length(); i++) {
								JSONObject objPosts = array.getJSONObject(i);
								String totalPosts = objPosts
										.getString("total_post");
								APP.GLOBAL()
								.getEditor()
								.putString(
										APP.PREF.USER_TOTAL_POSTS.key,
										totalPosts).commit();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;


				default:
					break;
				}
			}
		}
		return null;

	}


	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

}
