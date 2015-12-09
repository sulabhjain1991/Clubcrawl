package com.linchpin.clubcrawl;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.beans.BeanFbResponse;
import com.linchpin.clubcrawl.beans.BeanLoginInfo;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUser;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.CharTextView;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

@SuppressWarnings("deprecation")
public class LoginScreen extends Activity implements Result, DoinBackgroung,
OnClickListener {
	private BeanResponse		responseBean;
	private BeanFbResponse		responseFb, responseFbRegistered;
	private BeanLoginInfo		beanLoginInfo;
	private static String		APP_ID		= "168447376635182";
	final static int			LOGIN		= 1;
	final static int			LOGIN_FB	= 2;
	final static int			SIGNUP_FB	= 3;
	// Instance of Facebook Class
	public static Facebook			facebook	= new Facebook(APP_ID);
	private AsyncFacebookRunner	mAsyncRunner;
	String						FILENAME	= "AndroidSSO_data";
	String						userId;
	EditText					etUsername, etPassword;
	private final int USER_DETAIL = 0;
	private final int USER_POSTS = 1;
	private final int USER_FOLLOWERS = 2;
	private final int USER_FRIENDS = 3;
	public static LoginScreen objLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppPreferences appPref = new AppPreferences(getApplicationContext());
		
		setContentView(R.layout.activity_login_screen);
		BaseClass bc = new BaseClass();
		objLogin = this;
		bc.setFont(findViewById(R.id.mainLayout), this);
		etUsername = (EditText) findViewById(R.id.etUserName);
		etPassword = (EditText) findViewById(R.id.etPassword);
		
		appPref.setSneakStatus(false);
		findViewById(R.id.btnRegisterWithFacebook).setOnClickListener(this);
		findViewById(R.id.rlSignIn).setOnClickListener(this);
		findViewById(R.id.btnCreateAccount).setOnClickListener(this);
		findViewById(R.id.btnForgottenPassword).setOnClickListener(this);
		findViewById(R.id.btnSneakPreview).setOnClickListener(this);
		((TextView) findViewById(R.id.btnSignIn)).setText("Sign in");
		((CharTextView) findViewById(R.id.charTextview)).setText("");
	}

	@SuppressWarnings("deprecation")
	// facebook login
	public void loginToFacebook() {

		String access_token = APP.GLOBAL().getPreferences().getString(APP.PREF.FB_ACCESS_TOKEN.key, null);
		long expires =  APP.GLOBAL().getPreferences().getLong(APP.PREF.FB_ACCESS_EXPIRES.key, 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);

			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "email" ,"publish_actions"},
					new DialogListener() {

				@Override
				public void onCancel() {
					finish();
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					APP.GLOBAL().getEditor().putString(APP.PREF.FB_ACCESS_TOKEN.key, facebook.getAccessToken()).commit();
					APP.GLOBAL().getEditor().putLong(APP.PREF.FB_ACCESS_EXPIRES.key, facebook.getAccessExpires()).commit();


					if (ConstantUtil
							.isNetworkAvailable(LoginScreen.this)) {
						NetworkTask fblLoginTask = new NetworkTask(
								LoginScreen.this, LOGIN_FB);
						fblLoginTask
						.exposePostExecute(LoginScreen.this);
						fblLoginTask.execute(ConstantUtil.fbLoginUrl
								+ "st=" + facebook.getAccessToken());
					}
					// new FbLoginTask(LoginScreen.this).execute();
					else
						Toast.makeText(LoginScreen.this,
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();

					// Making Login button invisible
				}

				@Override
				public void onError(DialogError error) {
					System.out.println(error.toString());

				}

				@Override
				public void onFacebookError(FacebookError fberror) {
					System.out.println(fberror.toString());

				}

			});

		} else {
			if (ConstantUtil.isNetworkAvailable(LoginScreen.this)) {
				NetworkTask fblLoginTask = new NetworkTask(LoginScreen.this,
						LOGIN_FB);
				fblLoginTask.exposePostExecute(LoginScreen.this);
				fblLoginTask.execute(ConstantUtil.fbLoginUrl + "st="
						+ facebook.getAccessToken());
			}
			else
				Toast.makeText(LoginScreen.this,
						getString(R.string.internetFailure), Toast.LENGTH_LONG)
						.show();
		}

	}

	/*
	 * public class FbLoginTask extends AsyncTask<Void, Void, Void> { String
	 * message = ""; ProgressDialog pd; Context con;
	 * 
	 * public FbLoginTask(Context con) { this.con = con; }
	 * 
	 * @Override protected void onPreExecute() { pd = ProgressDialog.show(con,
	 * null, "Loading..."); pd.setContentView(R.layout.progress_layout);
	 * super.onPreExecute(); }
	 * 
	 * @SuppressWarnings("deprecation")
	 * 
	 * @Override protected Void doInBackground(Void... params) {
	 * 
	 * Gson gson = new Gson(); String url = ConstantUtil.fbLoginUrl + "st=" +
	 * facebook.getAccessToken(); // url = ConstantUtil.loginUrl +
	 * "username_or_email=piyush"+ // "&password=piyush"; String responseString
	 * = ConstantUtil.http_connection(url);
	 * 
	 * if (responseString != null && !responseString.equals("")) { responseFb =
	 * gson.fromJson(responseString, BeanFbResponse.class); if
	 * (responseFb.getUserstatus().equals("newuser")) {
	 * 
	 * url = ConstantUtil.fbSignUpUrl + "email=" +
	 * responseFb.getFacebook().getEmail() + "&first_name=" +
	 * responseFb.getFacebook().getFirst_name() + "&last_name=" +
	 * responseFb.getFacebook().getLast_name() + "&username=" +
	 * responseFb.getFacebook().getUsername() + "&password=" +
	 * responseFb.getFacebook().getFirst_name() + "&age=" +
	 * responseFb.getFacebook().getAge() + "&phone=" +
	 * responseFb.getFacebook().getPhone_number() + "&marital_status=" +
	 * responseFb.getFacebook().getMarital_status() + "&fbid=" +
	 * responseFb.getFacebook().getId() + "&profile_pic=" +
	 * responseFb.getFacebook().getProfile_pic() + "&gender=" +
	 * responseFb.getFacebook().getGender(); String responseStringSignUp =
	 * ConstantUtil.http_connection(url); if (responseStringSignUp != null &&
	 * !responseStringSignUp.equals("")) { message = "Fb_SignUp"; responseBean =
	 * gson.fromJson(responseStringSignUp, BeanResponse.class); } } else if
	 * (responseFb.getUserstatus().equals("registerd")) { message =
	 * "Login_FB_Task"; responseFbRegistered = gson.fromJson(responseString,
	 * BeanFbResponse.class); beanLoginInfo = new BeanLoginInfo();
	 * beanLoginInfo.setUserdata(responseFbRegistered.getFacebook());
	 * BeanUserInfo beanInfo = beanLoginInfo.getUserdata();
	 * beanInfo.setProfile_pic(responseFbRegistered.getProfile_picture());
	 * beanLoginInfo.setUserdata(beanInfo); userId =
	 * responseFbRegistered.getUser_id();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(Void result) { if (pd.isShowing())
	 * { pd.dismiss(); } Message myMessage = new Message();
	 * 
	 * myMessage.obj = message; myHandler.sendMessage(myMessage);
	 * super.onPostExecute(result);
	 * 
	 * }
	 * 
	 * }
	 */

	@Override
	public void resultfromNetwork(String result, int id,int arg1,String arg2) {
		Gson gson = new Gson();
		// Message myMessage = new Message();
		// String message = "";
		switch (id) {
		case LOGIN:
			if (result != null && !result.equals("")) 
			{
				try
				{
				responseBean = gson.fromJson(result, BeanResponse.class);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if ((responseBean == null))
				{
					Toast.makeText(LoginScreen.this,getString(R.string.connectionerror),Toast.LENGTH_LONG).show();
					((CharTextView) findViewById(R.id.charTextview)).setAnimate(false);
					((CharTextView) findViewById(R.id.charTextview)).setText("");
					((TextView) findViewById(R.id.btnSignIn))
					.setText("Sign in");
					RelativeLayout rlSign = (RelativeLayout) findViewById(R.id.rlSignIn);
					rlSign.setEnabled(true);
				}
				else if (responseBean != null
						&& responseBean.getStatus().equals(
								ConstantUtil.STATUS_SUCCESS))
				{
					beanLoginInfo = gson.fromJson(result, BeanLoginInfo.class);
					if (beanLoginInfo != null) {
						Toast.makeText(LoginScreen.this,
								getString(R.string.strSuccessLogin),
								Toast.LENGTH_LONG).show();
						Intent i = new Intent(LoginScreen.this,
								MainScreen.class);
						i.putExtra("loginInfo", beanLoginInfo.getUserdata());
						AppPreferences appPref = new AppPreferences(
								LoginScreen.this);
						appPref.setUserId(beanLoginInfo.getUserdata().getID());
						callBackgroundTasks();
						startActivity(i);
						finish();
					} else
					{
						((CharTextView) findViewById(R.id.charTextview)).setAnimate(false);
						((TextView) findViewById(R.id.btnSignIn))
						.setText("Sign in");
						((CharTextView) findViewById(R.id.charTextview)).setText("");
						Toast.makeText(LoginScreen.this,
								responseBean.getMessage(), Toast.LENGTH_LONG)
								.show();
						RelativeLayout rlSign = (RelativeLayout) findViewById(R.id.rlSignIn);
						rlSign.setEnabled(true);
					}
				} else if (responseBean != null)
				{
					Toast.makeText(LoginScreen.this, responseBean.getMessage(),
							Toast.LENGTH_LONG).show();
					((CharTextView) findViewById(R.id.charTextview)).setAnimate(false);
					((TextView) findViewById(R.id.btnSignIn))
					.setText("Sign in");
					((CharTextView) findViewById(R.id.charTextview)).setText("");
					RelativeLayout rlSign = (RelativeLayout) findViewById(R.id.rlSignIn);
					rlSign.setEnabled(true);
				}


			}
			else
			{
				((CharTextView) findViewById(R.id.charTextview)).setAnimate(false);
				((TextView) findViewById(R.id.btnSignIn))
				.setText("Sign in");
				((CharTextView) findViewById(R.id.charTextview)).setText("");
				RelativeLayout rlSign = (RelativeLayout) findViewById(R.id.rlSignIn);
				rlSign.setEnabled(true);
			}
			//				myMessage.obj = "Login_Task";
			//				myHandler.sendMessage(myMessage);
			break;

		case SIGNUP_FB:
			if (result != null && !result.equals("")) {
				//	message = "Fb_SignUp";
				responseBean = gson.fromJson(result, BeanResponse.class);
				if (responseBean == null)
					Toast.makeText(LoginScreen.this,
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				else
					Toast.makeText(LoginScreen.this, responseBean.getMessage(),
							Toast.LENGTH_LONG).show();
			}

			break;
		case LOGIN_FB:

			if (result != null && !result.equals("")) {
				responseFb = gson.fromJson(result, BeanFbResponse.class);
				if (responseFb.getUserstatus().equals("newuser")) {
					Intent i = new Intent(LoginScreen.this, SignUp.class);
						i.putExtra("fbResult", result);
						startActivity(i);
						break;
				} else if (responseFb.getUserstatus().equals("registerd")) {
					//	message = "Login_FB_Task";
					responseFbRegistered = gson.fromJson(result,
							BeanFbResponse.class);
					BeanUser userBean=responseFbRegistered.getFacebook();
					userBean.setUsername(responseFbRegistered.getUsername());
					responseFbRegistered.setFacebook(userBean);
					beanLoginInfo = new BeanLoginInfo();
					beanLoginInfo.setUserdata(responseFbRegistered
							.getFacebook());
					BeanUser beanInfo = beanLoginInfo.getUserdata();
					beanInfo.setProfile_pic(responseFbRegistered
							.getProfile_picture());
				
					beanLoginInfo.setUserdata(beanInfo);
					userId = responseFbRegistered.getUser_id();
					
				}
				if (beanLoginInfo == null)
					Toast.makeText(LoginScreen.this,
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				else if (beanLoginInfo != null) {
					Toast.makeText(LoginScreen.this,
							getString(R.string.strSuccessLogin),
							Toast.LENGTH_LONG).show();
					Intent i = new Intent(LoginScreen.this, MainScreen.class);
					i.putExtra("loginInfo", beanLoginInfo.getUserdata());
					AppPreferences appPref = new AppPreferences(
							LoginScreen.this);
					appPref.setUserId(userId);
					callBackgroundTasks();
					startActivity(i);
					finish();
				} else
					Toast.makeText(LoginScreen.this, responseBean.getMessage(),
							Toast.LENGTH_LONG).show();

				//					myMessage.obj = message;
				//					myHandler.sendMessage(myMessage);

			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlSignIn:

			if (etUsername.getText().toString().equals("")
					|| etPassword.getText().toString().equals(""))
				Toast.makeText(LoginScreen.this,
						getString(R.string.strBlankFieldsError),
						Toast.LENGTH_LONG).show();
			else if (ConstantUtil.isNetworkAvailable(LoginScreen.this)) {
				RelativeLayout rlSign = (RelativeLayout) findViewById(R.id.rlSignIn);
				rlSign.setEnabled(false);
				((CharTextView) findViewById(R.id.charTextview)).setAnimate(true);
				((TextView) findViewById(R.id.btnSignIn)).setText("Signing in");
				// new LoginTask(LoginScreen.this).execute();
				NetworkTask networkTask = new NetworkTask(LoginScreen.this,
						LOGIN);
				networkTask.setProgressDialog(false);
				networkTask.exposePostExecute(LoginScreen.this);
				networkTask.execute(ConstantUtil.loginUrl
						+ "username_or_email="
						+ etUsername.getText().toString() + "&password="
						+ etPassword.getText().toString());
			} else
				Toast.makeText(LoginScreen.this,
						getString(R.string.internetFailure), Toast.LENGTH_LONG)
						.show();

			break;
			/*
			 * case R.id.btnSignup: Intent i = new Intent(LoginScreen.this,
			 * SignUp.class); startActivity(i); break;
			 */case R.id.btnForgottenPassword:

				 Intent i2 = new Intent(LoginScreen.this, ForgetPassword.class);
				 startActivity(i2);
				 overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
				 break;
			 case R.id.btnRegisterWithFacebook:
				 loginToFacebook();
				 break;
			 case R.id.btnCreateAccount:
				 Intent i = new Intent(LoginScreen.this, SignUp.class);
				 startActivity(i);
				 overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
				 break;
			 case R.id.btnSneakPreview:
				 AppPreferences appPref = AppPreferences.getInstance(LoginScreen.this);
				 appPref.setSneakStatus(true);
				 Intent intent = new Intent(LoginScreen.this, MainScreen.class);
				 startActivity(intent);
				 break;

			 default:
				 break;
		}

	}

	private void callBackgroundTasks() {
		AppPreferences appPref = new AppPreferences(LoginScreen.this);
		NetworkTask MyProfileTask = new NetworkTask(LoginScreen.this,
				USER_DETAIL);
		MyProfileTask.setProgressDialog(false);
		MyProfileTask.exposeDoinBackground(LoginScreen.this);
		String userDetailsUrl = ConstantUtil.userProfileDetail
				+ appPref.getUserId();
		MyProfileTask.execute(userDetailsUrl);

		NetworkTask TotalPosts = new NetworkTask(LoginScreen.this, USER_POSTS);
		TotalPosts.setProgressDialog(false);
		TotalPosts.exposeDoinBackground(LoginScreen.this);
		
	}

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
							&& !responseStringUserDetails.equals(""))
						APP.GLOBAL()
						.getEditor()
						.putString(APP.PREF.USER_DETAILS.key,
								responseStringUserDetails).commit();
					
					MenuFragment.beanUserInfo = (BeanUserInfo)(gson.fromJson(APP.GLOBAL().getPreferences().getString(APP.PREF.USER_DETAILS.key, ""), BeanUserInfo.class));
					break;
				case USER_POSTS:
					String urlPosts = params[0];
					String responseStringPosts = ConstantUtil
							.http_connection(urlPosts);
					if (responseStringPosts != null
							&& !responseStringPosts.equals("")) {

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		
			facebook.authorizeCallback(requestCode, resultCode, data);  
	}
}
