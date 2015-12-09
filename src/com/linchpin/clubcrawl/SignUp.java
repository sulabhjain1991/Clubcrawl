package com.linchpin.clubcrawl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.beans.BeanFbResponse;
import com.linchpin.clubcrawl.beans.BeanLoginInfo;
import com.linchpin.clubcrawl.beans.BeanProfilePicNameId;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.beans.BeanUsersDetail;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class SignUp extends PhotoCaptureGallery implements Result, DoinBackgroung, OnClickListener
{

	private String					alertString		= "";
	private BeanResponse			responseBean;
	private BeanLoginInfo			beanLoginInfo;
	private AlertDialog				alertDialog;
	private int						dialogNumber	= 0;
	private BeanUsersDetail			beanUserDetail;
	private Dialog					dialog;
	private BeanProfilePicNameId	profilePicName;
	EditText						etEmail;
	EditText						etUserName;
	EditText						etPassword;
	TextView						tvAge;
	TextView						tvSex;
	private int						screenNo		= 1;
	final static int				LOGIN			= 5;
	private final int				Sign_Up			= 1;
	private final int				USER_DETAIL		= 0;
	private final int				USER_POSTS		= 4;
	private final int				USER_FOLLOWERS	= 2;
	private final int				USER_FRIENDS	= 3;
	ImageView						ivBanner;
	boolean							signupTypeFB	= false;
	LinearLayout					llSignup2, llSignup1;
	TextView									tvNext;
	/**
	 * @author Khalid Khan
	 * @category only for Facebook
	 **/
	StringBuilder					url;

	private void findAllViews()
	{
		etEmail = (EditText) findViewById(R.id.etEmail);
		etUserName = (EditText) findViewById(R.id.etUserName);
		etPassword = (EditText) findViewById(R.id.etPassword);
		tvSex = (TextView) findViewById(R.id.tvSex);
		ivBanner = (ImageView) findViewById(R.id.ivBanner);
		tvNext = (TextView) findViewById(R.id.tvNext);
		llSignup1 = (LinearLayout) findViewById(R.id.llSignup1);
		llSignup2 = (LinearLayout) findViewById(R.id.llSignup2);

		findViewById(R.id.tvSex).setOnClickListener(this);
		findViewById(R.id.tvmaritalstatus).setOnClickListener(this);
		findViewById(R.id.tvNext).setOnClickListener(this);
		findViewById(R.id.ivCamera).setOnClickListener(this);
		findViewById(R.id.ivBack).setOnClickListener(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_screen);
		BaseClass bc = new BaseClass();
		bc.setFont(findViewById(R.id.mainLayout), this);
		findAllViews();

		alertDialog = new AlertDialog.Builder(SignUp.this).create();
		dialog = new Dialog(SignUp.this);
		setWidthOfHeader();
		screenNo=1;
		if (getIntent().hasExtra("fbResult"))
		{
			signupTypeFB = true;
			Gson gson = new Gson();
			BeanFbResponse responseFb = gson.fromJson(getIntent().getStringExtra("fbResult"), BeanFbResponse.class);
			screenNo = 2;
			url = new StringBuilder();
			url.append(ConstantUtil.fbSignUpUrl);
			url.append("email=").append(responseFb.getFacebook().getEmail());

			url.append("&first_name=").append(responseFb.getFacebook().getFirst_name());
			url.append("&last_name=").append(responseFb.getFacebook().getLast_name());

			url.append("&age=").append(responseFb.getFacebook().getAge());

			url.append("&marital_status=").append(responseFb.getFacebook().getMarital_status());
			url.append("&fbid=").append(responseFb.getFacebook().getId());

			url.append("&profile_pic=").append(responseFb.getFacebook().getProfile_pic());
			url.append("&gender=").append(responseFb.getFacebook().getGender());

			switchScreen(screenNo);
			((EditText)findViewById(R.id.etFullName)).setHint("Create a username");
			findViewById(R.id.etFBPassword).setVisibility(View.VISIBLE);
			findViewById(R.id.etFBPasswordBottom).setVisibility(View.VISIBLE);
		}
	}

	private void setAge()
	{
		final ListView lvAge = (ListView) dialog.findViewById(R.id.lvAge);
		final TextView tvAge = (TextView) findViewById(R.id.tvAge);
		lvAge.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				tvAge.setText(lvAge.getItemAtPosition(arg2).toString());
				dialog.dismiss();
			}
		});

	}

	private void setSex()
	{
		TextView male = (TextView) dialog.findViewById(R.id.male);
		TextView female = (TextView) dialog.findViewById(R.id.female);

		final TextView tvSex = (TextView) findViewById(R.id.tvSex);

		male.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvSex.setText(R.string.male);
			}
		});

		female.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvSex.setText(R.string.female);
			}
		});

	}

	private void setMaritalStatus()
	{
		TextView tvSingle = (TextView) dialog.findViewById(R.id.tvSingle);
		TextView tvInRelationship = (TextView) dialog.findViewById(R.id.tvInRelationship);
		TextView tvEngaged = (TextView) dialog.findViewById(R.id.tvEngaged);
		TextView tvMarried = (TextView) dialog.findViewById(R.id.tvmarried);
		TextView tvOPenRelation = (TextView) dialog.findViewById(R.id.tvOpenRelationship);
		TextView tvComplicated = (TextView) dialog.findViewById(R.id.tvComplicated);
		TextView tvSeprated = (TextView) dialog.findViewById(R.id.tvSeprated);
		TextView tvDivorced = (TextView) dialog.findViewById(R.id.tvDivorced);
		TextView tvWidowed = (TextView) dialog.findViewById(R.id.tvWidowed);

		final TextView tvMaritalStatus = (TextView) findViewById(R.id.tvmaritalstatus);

		tvSingle.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.single);
			}
		});

		tvInRelationship.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strRelationship);
			}
		});
		tvEngaged.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strEngaged);
			}
		});
		tvMarried.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.married);
			}
		});
		tvOPenRelation.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strOpenRelation);
			}
		});
		tvComplicated.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strComplicated);
			}
		});
		tvSeprated.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strSeprated);
			}
		});
		tvDivorced.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strDivorced);
			}
		});
		tvWidowed.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvMaritalStatus.setText(R.string.strWidowed);
			}
		});

	}

	private void showOptionsDialog()
	{
		dialog = new Dialog(SignUp.this);
		dialog.getWindow().setLayout(100, 100);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		switch (dialogNumber)
		{
		case 1:
			dialog.setContentView(R.layout.sex_options_dialog);
			break;

		case 2:
			dialog.setContentView(R.layout.marital_status_dialog);

			break;

		default:
			break;
		}

		dialog.show();
	}

	private Boolean uploadProfileImage(String picturePath)
	{

		profilePicName = uploadProfilePicFile(picturePath, SignUp.this);
		if (profilePicName == null || profilePicName.getFilename() == null)
		{
			//			Toast.makeText(SignUp.this, "Error in connection",
			//					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/*
		public class SignUpTask extends AsyncTask<Void, Void, Void>
		{
			ProgressDialog	pd;
			Context			con;

			public SignUpTask(SignUp signup)
			{
				this.con = signup;
			}

			@Override
			protected void onPreExecute()
			{
				pd = ProgressDialog.show(con, null, "Loading...");
				pd.setContentView(R.layout.progress_layout);

				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				String picturePath = returnProfileImagePath();
				Boolean signUpProcessing = true;

				if (picturePath != null)
				{
					signUpProcessing = uploadProfileImage(picturePath);
				}

				if (!signUpProcessing) { return null; }

				etEmail = (EditText) findViewById(R.id.etEmail);
				// EditText etFullName = (EditText) findViewById(R.id.etFullName);
				etUserName = (EditText) findViewById(R.id.etUserName);
				etPassword = (EditText) findViewById(R.id.etPassword);
				tvAge = (TextView) findViewById(R.id.tvAge);
				// EditText etPhone = (EditText) findViewById(R.id.etPhoneNumeber);
				tvSex = (TextView) findViewById(R.id.tvSex);
				ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);

				String url = "";
				Gson gson = new Gson();

	 * url = ConstantUtil.signupUrl + "email=" +
	 * etEmail.getText().toString() + "&first_name=" +
	 * etFullName.getText().toString() + "&last_name=&username=" +
	 * etUserName.getText().toString() + "&password=" +
	 * etPassword.getText().toString() + "&age=" +
	 * tvAge.getText().toString().trim() + "&phone=" +
	 * etPhone.getText().toString().trim() +
	 * "&marital_status=&fbid=&profile_pic=" +
	 * profilePicName.getFilename() + "&gender=" +
	 * tvSex.getText().toString().trim();

				String responseString = ConstantUtil.http_connection(url);

				if (responseString != null && !responseString.equals(""))
				{
					responseBean = gson.fromJson(responseString, BeanResponse.class);
					if (responseBean.getStatus().equals(ConstantUtil.STATUS_SUCCESS))
					{
						beanLoginInfo = gson.fromJson(responseString, BeanLoginInfo.class);
					}

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				if (pd.isShowing())
				{
					pd.dismiss();
				}
				Message myMessage = new Message();
				myMessage.obj = "Signup_Task";
				myHandler.sendMessage(myMessage);
				super.onPostExecute(result);

			}

		}

	private Handler	myHandler	= new Handler()
									{
										@SuppressWarnings("deprecation")
										public void handleMessage(Message msg)
										{
											if (msg.obj.toString().equalsIgnoreCase("Signup_Task"))
											{
												if (!isFinishing())
												{
													if ((responseBean == null))
													{
														Toast.makeText(SignUp.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
													}

													else if (responseBean.getStatus().equals(ConstantUtil.STATUS_SUCCESS) && beanLoginInfo != null)
													{

														alertDialog.setMessage(getString(R.string.strSuccessSignUp));
														alertDialog.setButton("OK", new DialogInterface.OnClickListener()
														{
															public void onClick(DialogInterface dialog, int which)
															{
																finish();
															}
														});
														alertDialog.show();
													}
													else
													{
														Toast.makeText(SignUp.this, responseBean.getMessage(), Toast.LENGTH_LONG).show();
														alertDialog.setMessage(responseBean.getMessage());
														alertDialog.setButton("OK", new DialogInterface.OnClickListener()
														{
															public void onClick(DialogInterface dialog, int which)
															{
															}
														});
														alertDialog.show();
													}
												}
											}
										}
									};*/

	private boolean checkPassword()
	{
		EditText etPassword = (EditText) findViewById(R.id.etPassword);
		if (etPassword.getText().toString().trim().equals(""))
		{
			alertString = "Please enter password";
			showAlertForIncompleteField();
			return false;
		}
		if (etPassword.getText().toString().length() < 6)
		{
			alertString = "Password must be at least 6 characters long";
			showAlertForIncompleteField();
			return false;
		}
		return true;
	}

	private boolean checkGender()
	{
		TextView tvGender = (TextView) findViewById(R.id.tvSex);
		if (tvGender.getText().toString().trim().equals(""))
		{
			alertString = "Please enter gender";
			showAlertForIncompleteField();
			return false;
		}

		return true;
	}

	private boolean checkMaritalStatus()
	{
		TextView tvMaritalStatus = (TextView) findViewById(R.id.tvmaritalstatus);
		if (tvMaritalStatus.getText().toString().trim().equals(""))
		{
			alertString = "Please enter marital status";
			showAlertForIncompleteField();
			return false;
		}

		return true;
	}

	private boolean checkEmailAddress()
	{
		EditText etEmail = (EditText) findViewById(R.id.etEmail);
		if (etEmail.getText().toString().trim().equals(""))
		{
			alertString = "Please enter email address";
			showAlertForIncompleteField();
			return false;
		}
		else
		{
			if (!ConstantUtil.isValidEmail(etEmail.getText()))
			{
				alertString = "Please enter valid email address";
				showAlertForIncompleteField();
				return false;
			}
		}
		return true;
	}

	private boolean checkUserName()
	{
		EditText etUserName = (EditText) findViewById(R.id.etUserName);
		if (etUserName.getText().toString().trim().equals(""))
		{
			alertString = "Please enter user name";
			showAlertForIncompleteField();
			return false;
		}
		return true;
	}

	private boolean checkFullName()
	{
		// EditText etFullNmae = (EditText) findViewById(R.id.etFullName);
		// if (etFullNmae.getText().toString().trim().equals("")) {
		alertString = "Please enter first name";
		showAlertForIncompleteField();
		// return false;
		// }
		return true;
	}

	@SuppressWarnings("deprecation")
	private void showAlertForIncompleteField()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(SignUp.this).create();
		alertDialog.setMessage(alertString);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.show();
	}

	// dynamically set the width and height of banner
	private void setWidthOfBanner()
	{
		ImageView ivBanner = (ImageView) findViewById(R.id.ivBanner);
		ConstantUtil.getScreen_Height(SignUp.this);
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth, ConstantUtil.screenHeight * 35 / 100);
		ivBanner.setLayoutParams(params);

	}

	private void setWidthOfHeader()
	{
		RelativeLayout rlHeaderSignup = (RelativeLayout) findViewById(R.id.rlHeaderSingup);
		ConstantUtil.getScreen_Height(SignUp.this);
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth, ConstantUtil.screenHeight * 8 / 100);
		rlHeaderSignup.setLayoutParams(params);
	}

	private void switchScreen(int n)
	{

		if (n == 1)
		{

			tvNext.setText(getString(R.string.next));
			llSignup1.setVisibility(View.VISIBLE);
			llSignup2.setVisibility(View.GONE);
			screenNo = 1;
		}
		else
		{
			tvNext.setText(getString(R.string.signUp));
			llSignup1.setVisibility(View.GONE);
			llSignup2.setVisibility(View.VISIBLE);
			screenNo = 2;
		}

	}

	@Override
	public void onBackPressed()
	{
		if(signupTypeFB)
			finish();
		else if (screenNo == 2) switchScreen(1);
		else finish();
	}

	@Override
	public String doInBackground(Integer id, String... params)
	{
		String responseString = null;
		synchronized (id)
		{
			synchronized (params)
			{

				Gson gson = new Gson();

				switch (id)
				{
				case USER_DETAIL:
					String urlUserDetails = params[0];
					String responseStringUserDetails = ConstantUtil.http_connection(urlUserDetails);
					if (responseStringUserDetails != null || !responseStringUserDetails.equals("")) APP.GLOBAL().getEditor().putString(APP.PREF.USER_DETAILS.key, responseStringUserDetails)
					.commit();
					MenuFragment.beanUserInfo = (BeanUserInfo)(gson.fromJson(APP.GLOBAL().getPreferences().getString(APP.PREF.USER_DETAILS.key, ""), BeanUserInfo.class));
					break;
				case USER_POSTS:
					String urlPosts = params[0];
					String responseStringPosts = ConstantUtil.http_connection(urlPosts);
					if (responseStringPosts != null || !responseStringPosts.equals(""))
					{

						try
						{
							JSONObject obj = new JSONObject(responseStringPosts);
							JSONArray array = obj.getJSONArray("post");

							for (int i = 0; i < array.length(); i++)
							{
								JSONObject objPosts = array.getJSONObject(i);
								String totalPosts = objPosts.getString("total_post");
								APP.GLOBAL().getEditor().putString(APP.PREF.USER_TOTAL_POSTS.key, totalPosts).commit();
							}
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				

				case Sign_Up:
					profilePicName = new BeanProfilePicNameId();
					String picturePath = returnProfileImagePath();
					Boolean signUpProcessing = true;

					if (picturePath != null && !picturePath.equals(""))
					{
						signUpProcessing = uploadProfileImage(picturePath);
					}

					if (!signUpProcessing) { return null; }

					EditText etEmail = (EditText) findViewById(R.id.etEmail);
					EditText etFullName = (EditText) findViewById(R.id.etFullName);
					EditText etUserName = (EditText) findViewById(R.id.etUserName);
					EditText etPassword = (EditText) findViewById(R.id.etPassword);
					TextView tvSex = (TextView) findViewById(R.id.tvSex);
					EditText etPhone = (EditText) findViewById(R.id.etPhoneNumeber);
					TextView tvMaritalStatus = (TextView) findViewById(R.id.tvmaritalstatus);
					//tvSex = (TextView) findViewById(R.id.tvSex);
					//ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);

					String url = "";

					url = params[0] + "email=" + etEmail.getText().toString() + "&first_name=" + etFullName.getText().toString() + "&last_name=&username=" + etUserName.getText().toString()
							+ "&password=" + etPassword.getText().toString() + "&age="
							+
							//tvAge.getText().toString().trim() + 
							"&phone=" + etPhone.getText().toString().trim() + "&marital_status=" + tvMaritalStatus.getText().toString() + "&gender=" + tvSex.getText().toString()
							+ "&fbid=&profile_pic=" + profilePicName.getFilename() + "&website=" + "";

					responseString = ConstantUtil.http_connection(url);
					break;

				default:
					break;
				}
			}
		}
		return responseString;
	}

	@Override
	public void resultfromNetwork(String responseString, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();
		switch (id)
		{

		case Sign_Up:
			if(!signupTypeFB)
			{
				if (responseString != null && !responseString.equals(""))
				{
					responseBean = gson.fromJson(responseString, BeanResponse.class);
					if (responseBean.getStatus().equals(ConstantUtil.STATUS_SUCCESS))
					{
						beanLoginInfo = gson.fromJson(responseString, BeanLoginInfo.class);
					}

				}
				if ((responseBean == null))
				{
					Toast.makeText(SignUp.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				}

				else if (responseBean.getStatus().equals(ConstantUtil.STATUS_SUCCESS) && beanLoginInfo != null)
				{

					alertDialog.setMessage(getString(R.string.strSuccessSignUp));
					alertDialog.setButton("OK", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{

							if (ConstantUtil.isNetworkAvailable(SignUp.this))
							{

								//							((CharTextView)findViewById(R.id.btnSignIn)).setAnimate(true);
								//new LoginTask(LoginScreen.this).execute();
								NetworkTask networkTask = new NetworkTask(SignUp.this, LOGIN);
								networkTask.setProgressDialog(true);
								networkTask.exposePostExecute(SignUp.this);
								networkTask.execute(ConstantUtil.loginUrl + "username_or_email=" + etUserName.getText().toString() + "&password=" + etPassword.getText().toString());
								
							}
							else Toast.makeText(SignUp.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

						}
					});
					alertDialog.show();
				}
				else
				{
					//			Toast.makeText(SignUp.this, responseBean.getMessage(),
					//					Toast.LENGTH_LONG).show();
					alertDialog.setMessage(responseBean.getMessage());
					alertDialog.setButton("OK", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
						}
					});
					alertDialog.show();
				}
			}
			else
			{
				if (responseString != null && !responseString.equals(""))
				{
					responseBean = gson.fromJson(responseString, BeanResponse.class);
					
				}
				if ((responseBean == null))
				{
					Toast.makeText(SignUp.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				}

				else
				{
					alertDialog.setMessage(getString(R.string.strSuccessSignUp));
					alertDialog.setButton("OK", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{

							if (ConstantUtil.isNetworkAvailable(SignUp.this))
							{

								//							((CharTextView)findViewById(R.id.btnSignIn)).setAnimate(true);
								//new LoginTask(LoginScreen.this).execute();
								NetworkTask networkTask = new NetworkTask(SignUp.this, LOGIN);
								networkTask.setProgressDialog(true);
								networkTask.exposePostExecute(SignUp.this);
								networkTask.execute(ConstantUtil.loginUrl + "username_or_email=" + ((EditText)findViewById(R.id.etFullName)).getText().toString().trim() + "&password=" + ((EditText)findViewById(R.id.etFBPassword)).getText().toString().trim());
							}
							else Toast.makeText(SignUp.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

						}
					});
					alertDialog.show();
				}
				
			}
			break;
		case LOGIN:
			if (responseString != null && !responseString.equals(""))
			{
				responseBean = gson.fromJson(responseString, BeanResponse.class);
				if ((responseBean == null)) Toast.makeText(SignUp.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				else if (responseBean != null && responseBean.getStatus().equals(ConstantUtil.STATUS_SUCCESS))
				{
					beanLoginInfo = gson.fromJson(responseString, BeanLoginInfo.class);
					if (beanLoginInfo != null)
					{
						Toast.makeText(SignUp.this, getString(R.string.strSuccessLogin), Toast.LENGTH_LONG).show();
						Intent i = new Intent(SignUp.this, MainScreen.class);
						i.putExtra("loginInfo", beanLoginInfo.getUserdata());
						AppPreferences appPref = new AppPreferences(SignUp.this);
						appPref.setUserId(beanLoginInfo.getUserdata().getID());
						callBackgroundTasks();
						startActivity(i);
						finish();
						if (LoginScreen.objLogin != null) LoginScreen.objLogin.finish();
						overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

					}
					else Toast.makeText(SignUp.this, responseBean.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			//				myMessage.obj = "Login_Task";
			//				myHandler.sendMessage(myMessage);
			break;

		default:
			break;
		}
	}

	private void callBackgroundTasks()
	{
		AppPreferences appPref = new AppPreferences(SignUp.this);
		NetworkTask MyProfileTask = new NetworkTask(SignUp.this, USER_DETAIL);
		MyProfileTask.setProgressDialog(false);
		MyProfileTask.exposeDoinBackground(SignUp.this);
		String userDetailsUrl = ConstantUtil.userProfileDetail + appPref.getUserId();
		MyProfileTask.execute(userDetailsUrl);

		NetworkTask TotalPosts = new NetworkTask(SignUp.this, USER_POSTS);
		TotalPosts.setProgressDialog(false);
		TotalPosts.exposeDoinBackground(SignUp.this);
		String userPostsUrl = ConstantUtil.userTotalPostsUrl + "user_id=" + appPref.getUserId();
		TotalPosts.execute(userPostsUrl);
	
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tvSex:
			dialogNumber = 1;
			showOptionsDialog();
			setSex();
			break;
		case R.id.tvmaritalstatus:
			dialogNumber = 2;
			showOptionsDialog();
			setMaritalStatus();
			break;
		case R.id.tvNext:

			if (screenNo == 1)
			{

				if (checkEmailAddress())
				{
					if (checkUserName())
					{
						if (checkPassword())
						{
							if (checkGender())
							{
								if (checkMaritalStatus())
								{
									switchScreen(2);
									TextView tvAcceptance = (TextView) findViewById(R.id.tvAcceptance);
									String strAccept = "By creating an account and signing up to Clubcrawl, I accept the <b>Terms of Service</b> and <b>Privacy Policy</b>";
									tvAcceptance.setText(Html.fromHtml(strAccept));
								}
							}
						}
					}
				}
			}
			else
			{
				if (ConstantUtil.isNetworkAvailable(SignUp.this))
				{
					NetworkTask fblLoginTask = new NetworkTask(SignUp.this, Sign_Up);					
					fblLoginTask.exposePostExecute(SignUp.this);
					if(!signupTypeFB)		
					{
						fblLoginTask.exposeDoinBackground(SignUp.this);						
						fblLoginTask.execute(ConstantUtil.signupUrl);
					}
					else
					{
						url.append("&username=").append(((EditText)findViewById(R.id.etFullName)).getText().toString().trim());
						url.append("&password=").append(((EditText)findViewById(R.id.etFBPassword)).getText().toString().trim());
						url.append("&phone=").append(((EditText)findViewById(R.id.etPhoneNumeber)).getText().toString().trim());
						fblLoginTask.execute(url.toString());

					}

				}
				//new FbLoginTask(LoginScreen.this).execute();
				else Toast.makeText(SignUp.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.ivCamera:
			openGallery(ivBanner);
			break;
		case R.id.ivBack:
			if(signupTypeFB)
			{
				finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
			}
			else if (screenNo == 2) switchScreen(1);
			else
				{finish();
				overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);}
			break;
		default:
			break;
		}

	}
	public static BeanProfilePicNameId uploadProfilePicFile(String sourceFileUri,Context con) {
		int serverResponseCode = 0;
		BeanResponse responseBean = null;
		AppPreferences appPref = new AppPreferences(con);
		String upLoadServerUri = ConstantUtil.uploadProfilePicUrl + "userID="+appPref.getUserId();
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;  
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024; 
		File sourceFile = new File(sourceFileUri); 
		if (!sourceFile.isFile()) {
			return null;
		}
		try { // open a URL connection to the Servlet
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(upLoadServerUri);
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("profile_picture", fileName); 
			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd); 
			dos.writeBytes("Content-Disposition: form-data; name=\"profile_picture\";filename=\""+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer bufferStr = new StringBuffer("");

			String line = "";
			while ((line = br.readLine()) != null) 
			{
				bufferStr.append(line);
			}
			String str = bufferStr.toString();

			JSONObject jsonObj = new JSONObject(str);
			BeanProfilePicNameId profilePicNameId = new BeanProfilePicNameId();
			if(jsonObj.has("filename")){
				profilePicNameId.setFilename(jsonObj.getString("filename"));
				if(jsonObj.has("id")){
					profilePicNameId.setId(jsonObj.getString("id"));
				}
			}
			
			responseBean = new BeanResponse();
			responseBean.setStatus(String.valueOf(serverResponseCode));
			responseBean.setMessage(serverResponseMessage);
			//close the streams //
			fileInputStream.close();
			dos.flush();
			dos.close();
			
			if(serverResponseCode == 200 && serverResponseMessage.equals("OK")){
				return profilePicNameId;
			}

		} catch (MalformedURLException ex) 
		{  
			ex.printStackTrace();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;  
	}
}
