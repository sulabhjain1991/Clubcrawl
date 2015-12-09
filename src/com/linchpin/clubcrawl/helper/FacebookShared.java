package com.linchpin.clubcrawl.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.linchpin.clubcrawl.R;

public class FacebookShared {

	private static String APP_ID = "168447376635182"; // Replace with your App

	JSONObject json;                                                                                                                 // ID



	// Instance of Facebook Class

	private static Facebook facebook = new Facebook(APP_ID);

	private AsyncFacebookRunner mAsyncRunner;

	String FILENAME = "AndroidSSO_data";

	private SharedPreferences mPrefs;
	private final int SHARE_IMAGE=1;
	private final int SHARE_TEXT=0;
	private int shareType;




	String str_disc = null;

	//        String img_title = null;

	String image_URl = null;

	Activity activity;

	Context ctx;

	Handler handler;

	



	/**

	 * create constructor in FacebookShared class this function use for

	 * initialization

	 */



	public FacebookShared() {

		try {

			handler = new Handler();

		} catch (Exception e) {



			e.printStackTrace();

		}

	}



	public FacebookShared(Activity fa, Context context) {

		try {

			handler = new Handler();

			ctx = context;

			activity = fa;

			mAsyncRunner = new AsyncFacebookRunner(facebook);

		} catch (Exception e) {



			e.printStackTrace();

		}

	}



	public void setData(String str, String imageURl,int shareType) {

		try {

			str_disc = str;

			this.shareType = shareType;

			image_URl = imageURl;

			//img_title = title_1;

		} catch (Exception e) {



			e.printStackTrace();

		}

	}



	/**

	 * loginToFacebook() create for login in facebook

	 **/



	public void loginToFacebook() {

		try {



			mAsyncRunner = new AsyncFacebookRunner(facebook);



			mPrefs = activity.getPreferences(Context.MODE_PRIVATE);



			String access_token = mPrefs.getString("access_token", null);



			long expires = mPrefs.getLong("access_expires", 0);



			if (access_token != null && expires != 0) {



				try {

					facebook.setAccessToken(access_token);

					facebook.setAccessExpires(expires);

					new FacebookSubmitTask(activity,shareType).execute();

					// fbSubmit(facebook, str_disc, image_URl);

				} catch (Exception e) {



					e.printStackTrace();

				}

			}



			else if (!facebook.isSessionValid()) {

				try {



					facebook.authorize(activity, new String[] { "email","publish_stream","publish_actions" }, new DialogListener() {



						public void onCancel() {

							// Function to handle cancel event

						}



						public void onComplete(Bundle values) {

							try {



								// Function to handle complete event

								// Edit Preferences and update facebook

								// acess_token



								SharedPreferences.Editor editor = mPrefs.edit();



								editor.putString("access_token",

										facebook.getAccessToken());



								editor.putLong("access_expires",

										facebook.getAccessExpires());



								editor.commit();
								new FacebookSubmitTask(activity,shareType).execute();
								





							} catch (Exception e) {



								e.printStackTrace();

							}



						}



						public void onError(DialogError error) {

							// Function to handle error



						}



						public void onFacebookError(FacebookError fberror) {

							// Function to handle Facebook errors



						}



					});

				} catch (Exception e) {



					e.printStackTrace();

				}



			}



		} catch (Exception e) {



			e.printStackTrace();

		}



	}










	// ============================================================================================================



	public void onResult(int requestCode, int resultCode, Intent data) {



		try {

			facebook.authorizeCallback(requestCode, resultCode, data);

		} catch (Exception e) {



			e.printStackTrace();

		}

	}



	// ==================================================================================================================



	/**

	 * Get Profile information by making request to Facebook Graph API

	 * */

	public void getProfileInformation() {

		try {

			mAsyncRunner.request("me", new RequestListener() {



				public void onComplete(String response, Object state) {

					Log.d("Profile", response);

					String json = response;

					try {

						// Facebook Profile JSON data

						JSONObject profile = new JSONObject(json);



						// getting name of the user

						final String name = profile.getString("name");



						// getting email of the user

						final String email = profile.getString("email");



						activity.runOnUiThread(new Runnable() {



							public void run() {

								Toast.makeText(ctx,

										"Name: " + name + "\nEmail: " + email,

										Toast.LENGTH_LONG).show();

							}



						});



					} catch (JSONException e) {

						e.printStackTrace();

					}

				}



				public void onIOException(IOException e, Object state) {

				}



				public void onFileNotFoundException(FileNotFoundException e,

						Object state) {

				}



				public void onMalformedURLException(MalformedURLException e,

						Object state) {

				}



				public void onFacebookError(FacebookError e, Object state) {

				}

			});

		} catch (Exception e) {



			e.printStackTrace();

		}



	}



	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

	}



	private void readObject(java.io.ObjectInputStream in) throws IOException,

	ClassNotFoundException {



	}


	private class FacebookSubmitTask extends AsyncTask<Void, String, String>
	{
		Context	con;
		private ProgressDialog pd;

		public FacebookSubmitTask(Context con,int shareType)
		{
			this.con = con;
		}

		@Override
		protected void onPreExecute() 
		{
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			pd.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(Void... params)
		{
			//fbSubmit(facebook, str_disc,image_URl);
			if(shareType == SHARE_IMAGE)
				postImageOnWall(facebook, image_URl);
			else
				postMessageOnWall(facebook, str_disc);
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			if(pd != null && pd.isShowing())
				pd.dismiss();
			if (json!= null && !json.isNull("id")) {



				Toast.makeText(

						ctx,

						"Your Message has been Submitted Successfully.",

						Toast.LENGTH_LONG).show();

				//  logoutFromFacebook();

			}
			else 
				Toast.makeText(

						ctx,

						"Your Message has not been Submitted .",

						Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}


	//post message on wall
	private void postMessageOnWall(Facebook fb,String strMessage)
	{

		if (fb != null) {

			if (fb.isSessionValid()) {

				Log.d("Tests", "Testing graph API wall post");
				try {
					String response = facebook.request("me");
					Bundle parameters = new Bundle();
					parameters.putString("message", str_disc);
					parameters.putString("description", "test test test");
					response = facebook.request("me/feed", parameters, 
							"POST");
					Log.d("Tests", "got response: " + response);
					if (response == null || response.equals("") || 
							response.equals("false")) {

					}
					else
						json = Util.parseJson(response);
				} catch(Exception e) {
					e.printStackTrace();
				}

			}
		}
	}


	private void postImageOnWall(Facebook fb,String imageUri)
	{

		if (fb != null) {

			if (fb.isSessionValid()) {

				Log.d("Tests", "Testing graph API wall post");
				try {
					String response = facebook.request("me");
					Bundle parameters = new Bundle();
					parameters.putString("picture", imageUri);
					//	parameters.putString("description", "test test test");
					response = facebook.request("me/feed", parameters, 
							"POST");
					Log.d("Tests", "got response: " + response);
					if (response == null || response.equals("") || 
							response.equals("false")) {

					}
					else
						json = Util.parseJson(response);
				} catch(Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
	
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		

		
			facebook.authorizeCallback(requestCode, resultCode, data);  
	}


}


