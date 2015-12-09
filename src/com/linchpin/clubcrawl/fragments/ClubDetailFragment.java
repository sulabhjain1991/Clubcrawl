package com.linchpin.clubcrawl.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.APP;
import com.linchpin.clubcrawl.BarMapScreen;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.BookingActivity;
import com.linchpin.clubcrawl.LoginScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanFbResponse;
import com.linchpin.clubcrawl.beans.BeanListClubDetail;
import com.linchpin.clubcrawl.beans.BeanLoginInfo;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUser;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class ClubDetailFragment extends ParentFragment implements Result, OnClickListener,DoinBackgroung
{

	private BeanResponse		responseBean;
	private BeanFbResponse		responseFb, responseFbRegistered;
	private BeanLoginInfo		beanLoginInfo;
	


	private static final int	CLUB_DETAIL			= 0x001;
	private static final int	ACTION_GEN_PREV		= 0x002;
	private static final int	ACTION_GEN_FREE		= 0x003;
	private static final int	ACTION_FB_CHECKIN	= 0x004;
	private static final int	ACTION_ALERT		= 0x005;
	private static final int	ACTION_BOOK_NOW		= 0x006;
	private static final int	FOLLOW				= 0x007;
	private static final int	UNFOLLOW				= 16;
	
	private static int			ACTION				= 0x000;
	private static final int			ACTION_FBLOGIN				=15;
	private static final int			CHECKIN				= 12;

	private View				view;
	private TextView			tvCallNow, tvBookNow;
	private ImageView			ivFbIcon;
	private BeanListClubDetail	beanListClubDetail;
	private String				callBtnText, offerBtnText = "", clubId, uid/**Face book Id*/
			;
	private RelativeLayout		layout, rlBookNow;
	boolean						isPaid				= false;
	private int					teachClub, tPerDay, hours, codeUsed, freeCode, offerBtnImage;
	private long				fbFanId				= 0;

	private TextView						follow;

	
	private AsyncFacebookRunner	mAsyncRunner;
	final static int			LOGIN_FB	= 13;
	final static int			SIGNUP_FB	= 14;

	

	@Override
	public void onPause()
	{
		layout.findViewById(R.id.overview_pan).setVisibility(View.GONE);
		layout.findViewById(R.id.tvLocation).setVisibility(View.VISIBLE);
		layout.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen)(getActivity()));
		layout.findViewById(R.id.ivComposePost).setVisibility(View.VISIBLE);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		layout = (RelativeLayout) getActivity().findViewById(R.id.normal_pan);
		layout.findViewById(R.id.overview_pan).setVisibility(View.VISIBLE);
		TextView overview = (TextView) layout.findViewById(R.id.overview);
		overview.setTextColor(getResources().getColor(R.color.background_color));
		((TextView) layout.findViewById(R.id.viewfeed)).setTextColor(getResources().getColor(R.color.black));
		layout.findViewById(R.id.viewfeed).setOnClickListener(this);
		layout.findViewById(R.id.tvLocation).setVisibility(View.GONE);
		layout.findViewById(R.id.ivSearch).setVisibility(View.GONE);
		layout.findViewById(R.id.ivComposePost).setVisibility(View.GONE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.pre_btn);
		ivMenu.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				getFragmentManager().popBackStack();;
			}
		});

		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_club_detail, container, false);

		clubId = getArguments().getString("clubId");
		if (clubId == null && savedInstanceState != null) clubId = savedInstanceState.getString("clubId");
		uid = getArguments().getString("uid");
		if (MenuFragment.beanUserInfo != null && MenuFragment.beanUserInfo.getUser() != null){
			uid = MenuFragment.beanUserInfo.getUser().getFbid();
			if(uid==null)
				uid = MenuFragment.beanUserInfo.getUser().getId();
			if(uid != null && uid.equals(""))
				uid = null;
		}
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{

			//http://162.13.137.28/siteadmin/api/clubdetail/?uid=null&id=" + clubId
			NetworkTask networkTask = new NetworkTask(getActivity(), CLUB_DETAIL);
			networkTask.setProgressDialog(true);
			networkTask.exposePostExecute(ClubDetailFragment.this);
			networkTask.execute(ConstantUtil.getClubDetailUrl + "uid=" + uid + "&id=" + clubId + "&&user_id=" + AppPreferences.getInstance(getActivity()).getUserId());
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
		ivFbIcon = (ImageView) view.findViewById(R.id.ivFbIcon);
		rlBookNow = (RelativeLayout) view.findViewById(R.id.rlBookNow);
		tvCallNow = (TextView) view.findViewById(R.id.tvCallNow);
		tvCallNow.setOnClickListener(this);
		follow = (TextView) view.findViewById(R.id.btnfollow);
		LinearLayout llMap = (LinearLayout) view.findViewById(R.id.llMap);
		llMap.setOnClickListener(this);
		follow.setOnClickListener(this);
		tvBookNow = (TextView) view.findViewById(R.id.tvBookNow);
		rlBookNow.setOnClickListener(this);
		return view;
	}

	public void showDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Alert!");
		alertDialogBuilder.setMessage("Your previous offer has now expired... for more please download the paid version.");
		Dialog dialog = alertDialogBuilder.create();
		dialog.show();

	}

	public void showDialogError()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Alert!");
		alertDialogBuilder.setMessage("Error");
		Dialog dialog = alertDialogBuilder.create();

		dialog.show();

	}

	protected void setClubInfo()
	{

		TextView tvClubName = (TextView) view.findViewById(R.id.tvClubName);
		TextView tvClubAddress = (TextView) view.findViewById(R.id.tvAddress);
		WebView wvDescription = (WebView) view.findViewById(R.id.wvDescription);
		String text = "<html><body style=\"text-align:left\"> %s </body></Html>";
		wvDescription.loadData(String.format(text, beanListClubDetail.getClubdetial().get(0).getSubcat_desc()), "text/html; charset=UTF-8", null);
		tvClubName.setText(beanListClubDetail.getClubdetial().get(0).getName());
		tvClubAddress.setText(beanListClubDetail.getClubdetial().get(0).getAddress());

		if (!beanListClubDetail.getClubdetial().get(0).getFollow().equals("0"))
		{
			follow.setText("Following");
			follow.setTextColor(getActivity().getResources().getColor(android.R.color.white));
			//follow.setTextSize(10.0f);
			follow.setBackgroundResource(R.drawable.following_btn_shape);
		}
		else
		{
			follow.setText("+ Follow");
				//follow.setTextSize(10.0f);
			follow.setBackgroundResource(R.drawable.follow_btn_shape);
			follow.setTextColor(getActivity().getResources().getColor(R.color.background_color));

		}
		setProfileImage(ConstantUtil.OffersImageBaseUrl + beanListClubDetail.getClubdetial().get(0).getHeader_image());
		setOfferAndCallButton();

	}

	public void setProfileImage(String url)
	{
		ImageView ivClubImage = (ImageView) view.findViewById(R.id.ivClubImage);
		AQuery aq = new AQuery(ivClubImage);
		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defult);
			options.preset = icon;
			aq.id(R.id.ivClubImage).image(url, options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.defult);
			ivClubImage.setImageBitmap(bm);
		}

	}

	private void setOfferAndCallButton()
	{
		isPaid = AppPreferences.getInstance(getActivity()).getPaidStatus();
		freeCode = Integer.parseInt(beanListClubDetail.getClubdetial().get(0).getIs_free_code());
		teachClub = Integer.parseInt(beanListClubDetail.getClubdetial().get(0).getTeachclub());
		tPerDay = Integer.parseInt(beanListClubDetail.getClubdetial().get(0).getTperday());
		if (beanListClubDetail.getClubdetial().get(0).getFbfanid() != null && !beanListClubDetail.getClubdetial().get(0).getFbfanid().equals("")) fbFanId = Long.parseLong(beanListClubDetail
				.getClubdetial().get(0).getFbfanid());
		if (beanListClubDetail.getClubdetial().get(0).getCode_used() != null && !beanListClubDetail.getClubdetial().get(0).getCode_used().equals("")) codeUsed = Integer.parseInt(beanListClubDetail
				.getClubdetial().get(0).getCode_used());
		if (beanListClubDetail.getClubdetial().get(0).getHours() != null && !beanListClubDetail.getClubdetial().get(0).getHours().equals("")) hours = Integer.parseInt(beanListClubDetail
				.getClubdetial().get(0).getHours());
		if (freeCode == 16)
		{
			tvCallNow.setText("Call to book");
			tvBookNow.setText("Book now");
			ivFbIcon.setVisibility(View.GONE);
			ACTION = ACTION_BOOK_NOW;
		}
		else if (freeCode > 0 && freeCode != 16)
		{
			tvCallNow.setText("Call now");

			if (!isPaid)
			{

				if (teachClub == 0 && tPerDay < 1 && uid != null && fbFanId != 0)
				{
					//FB LOGO+ Offer Name	
					//FB checkin
					tvBookNow.setText(beanListClubDetail.getClubdetial().get(0).getOffername());
					ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_facebook_icon));
					ACTION = ACTION_FB_CHECKIN;
				}
				else if (teachClub > 0 || tPerDay >= 1)
				{
					tvBookNow.setText("Next Offer in " + hours + " Hours");
					ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_app_icon));
					if (hours <= 30)
					{
						if (codeUsed == 1)
						{
							ACTION = ACTION_ALERT;
							///Club Logo +Next Offer  +Hours
							//Aleart
						}
						else
						{
							///Club Logo +Next Offer  +Hours
							//Gen_Prev_code= Succes-> Authorization Screen
							// Error->Aleart()	
							ACTION = ACTION_GEN_PREV;
						}

					}
					else
					{
						//Aleart						
						ACTION = ACTION_ALERT;
					}
				}
				else if (uid != null && fbFanId == 0)
				{
					//						FB LOGO+ Offer Name	"Gen_Prev_code= Succes-> Authorization Screen
					//                        Error->Aleart()"
					tvBookNow.setText(beanListClubDetail.getClubdetial().get(0).getOffername());
					ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_facebook_icon));
					ACTION = ACTION_GEN_FREE;
				}
				else if (uid == null)
				{
					//						FB LOGO+ Offer Name
					//                        Error->Aleart()"
					tvBookNow.setText(beanListClubDetail.getClubdetial().get(0).getOffername());
					ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_facebook_icon));
					ACTION=ACTION_FBLOGIN;
				}
			}
			else
			{
				if (uid != null && fbFanId != 0)
				{
					if (teachClub == 0 && tPerDay < 2)
					{

						//	Club LOGO+ Offer Name	Fb Checkin
						tvBookNow.setText(beanListClubDetail.getClubdetial().get(0).getOffername());
						ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_facebook_icon));
						ACTION = ACTION_FB_CHECKIN;

					}
					else
					{
						//Next Offer	Aleart
						tvBookNow.setText("Next Offer in " + hours + " Hour");
						ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_app_icon));
						ACTION = ACTION_ALERT;
					}
				}
				else if (uid != null && fbFanId == 0)
				{
					//						FB LOGO+ Offer Name	"Gen_Prev_code= Succes-> Authorization Screen
					//                        Error->Aleart()"
					tvBookNow.setText(beanListClubDetail.getClubdetial().get(0).getOffername());
					ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_facebook_icon));
					ACTION = ACTION_GEN_PREV;
				}
				else if (uid == null)
				{
					//						FB LOGO+ Offer Name
					//                        Error->Aleart()"
					tvBookNow.setText(beanListClubDetail.getClubdetial().get(0).getOffername());
					ivFbIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.offer_facebook_icon));
					//ACTION=ACTION_GEN_PREV;
				}
			}
		}
		else
		{
			rlBookNow.setVisibility(View.GONE);
		}

	}

	private void showClubDetailFree(int freeCode, boolean isPaid)
	{

		if (teachClub == 0 && tPerDay < 1)
		{
			if (hours < 30)
			{
				if (codeUsed == 1)
				{
					offerBtnImage = R.drawable.offer_app_icon;
					offerBtnText = "Next offer ," + hours + "hours";
				}
				else
				{
					offerBtnImage = R.drawable.offer_app_icon;
					offerBtnText = "Next offer ," + hours + "hours";
				}
			}
			else
			{
				offerBtnImage = R.drawable.offer_app_icon;
				offerBtnText = "Next offer ," + hours + "hours";
			}

		}
		else
		{
			if (uid == null)
			{
				offerBtnImage = R.drawable.offer_facebook_icon;
				offerBtnText = beanListClubDetail.getClubdetial().get(0).getOffername();
			}
			else
			{
				if (fbFanId == 0)
				{
					offerBtnImage = R.drawable.offer_facebook_icon;
					offerBtnText = beanListClubDetail.getClubdetial().get(0).getOffername();
				}
				else
				{
					checkInUserFan(freeCode, isPaid, teachClub, tPerDay, hours, codeUsed);
				}
			}

		}

	}

	private void checkInUserFan(int freeCode, boolean isPaid, int teachClub, int tPerDay, int hours, int codeUsed)
	{
		if (uid != null)
		{
			if (isPaid)
			{
				//checkInUserFanPaid
			}
			else
			{
				checkInUserFanFree(freeCode, isPaid, teachClub, tPerDay, hours, codeUsed);
			}
		}
		else
		{
			offerBtnImage = R.drawable.offer_app_icon;
			offerBtnText = beanListClubDetail.getClubdetial().get(0).getOffername();
		}

	}

	private void checkInUserFanFree(int freeCode, boolean isPaid, int teachClub, int tPerDay, int hours, int codeUsed)
	{
		if (freeCode > 0 && freeCode != 16)
		{
			if (teachClub == 0 && tPerDay < 1)
			{
				offerBtnImage = R.drawable.offer_app_icon;
				offerBtnText = beanListClubDetail.getClubdetial().get(0).getOffername();
			}
			else
			{
				if (hours < 30)
				{
					if (codeUsed == 1)
					{
						offerBtnImage = R.drawable.offer_app_icon;
						offerBtnText = "Next offer ," + hours + "hours";

					}
					else
					{
						offerBtnImage = R.drawable.offer_app_icon;
						offerBtnText = "Next offer ," + hours + "hours";
					}
				}
				else
				{
					offerBtnImage = R.drawable.offer_app_icon;
					offerBtnText = "Next offer ," + hours + "hours";
				}
			}
		}
		else
		{
			offerBtnText = "";
		}
	}

	@Override
	public void resultfromNetwork(String result, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();

		switch (id)
		{
		case FOLLOW:
			try
			{
				//{"status":"success"}
				JSONObject res = new JSONObject(result);
				if (res.get("status").equals("success") || res.get("status").equals("venue already Followed"))
				{
					follow.setText("Following");
					follow.setTextColor(getActivity().getResources().getColor(android.R.color.white));
					follow.setBackgroundResource(R.drawable.following_btn_shape);
					beanListClubDetail.getClubdetial().get(0).setFollow("1");
				}
				/*else
					{
						((TextView) arg0).setText("+ Follow");
						arg0.setBackgroundResource(R.drawable.follow_btn_shape);
						((TextView) arg0).setTextColor(((TextView) arg0).getResources().getColor(R.color.background_color));
						info.setFlag("0");
					}*/
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case UNFOLLOW:
			try
			{
				//{"status":"success"}
				JSONObject res = new JSONObject(result);
				if (res.get("status").equals("success"))
				{
					follow.setText("+ Follow");
						//follow.setTextSize(10.0f);
					follow.setBackgroundResource(R.drawable.follow_btn_shape);
					follow.setTextColor(getActivity().getResources().getColor(R.color.background_color));
					beanListClubDetail.getClubdetial().get(0).setFollow("0");
				}
				/*else
					{
						((TextView) arg0).setText("+ Follow");
						arg0.setBackgroundResource(R.drawable.follow_btn_shape);
						((TextView) arg0).setTextColor(((TextView) arg0).getResources().getColor(R.color.background_color));
						info.setFlag("0");
					}*/
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CLUB_DETAIL:
			if (result != null && !result.equals("")) beanListClubDetail = gson.fromJson(result, BeanListClubDetail.class);
			if ((beanListClubDetail == null))
			{
				Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
			}
			else
			{
				setClubInfo();
			}
			break;
		case CHECKIN:
			AppPreferences appPref = AppPreferences.getInstance(getActivity());
			if (!appPref.getSneakStatus())
			{
				NetworkTask networkTask2 = new NetworkTask(getActivity(), ACTION_GEN_FREE);
				networkTask2.setProgressDialog(true);
				networkTask2.exposePostExecute(ClubDetailFragment.this);
				//club/generatePrevCode?id=<venue_id>&uid=<facebook_id>
				networkTask2.execute(ConstantUtil.baseUrl + "club/generatecode?id=" + clubId + "&uid=" + uid);
			}
			else
			{
				ConstantUtil.showSneakDialog(getActivity());
			}
			break;
		case ACTION_GEN_FREE:
		case ACTION_GEN_PREV:
			try
			{
				//{"err":0,"bcode":"678267814795","club_logo":"1391189592_image.jpg"}
				JSONObject jresult = new JSONObject(result);
				if (jresult.has("bcode"))
				{
					Bundle b = new Bundle();
					b.putString("bcode", jresult.getString("bcode"));
					b.putString("club_logo", jresult.getString("club_logo"));
					activityCallback.onSendMessage(new AuthorizeFragment(), b, "");
				}
				else
				{
					showDialogError();
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case LOGIN_FB:

			if (result != null && !result.equals("")) {
				responseFb = gson.fromJson(result, BeanFbResponse.class);
				if (responseFb.getUserstatus().equals("newuser")) {
					fbLogout();
					Toast.makeText(getActivity(), "Login with same id from which u login inside app.", Toast.LENGTH_LONG).show();
					break;
				} else if (responseFb.getUserstatus().equals("registerd")) {
					//	message = "Login_FB_Task";
					responseFbRegistered = gson.fromJson(result,
							BeanFbResponse.class);
					if(!responseFbRegistered.getFacebook().getEmail().equals(MenuFragment.beanUserInfo.getUser().getEmail()))
					{
						fbLogout();
						Toast.makeText(getActivity(), "Login with same id from which u login inside app.", Toast.LENGTH_LONG).show();
					}
					else
					{

						//get fbid from the facebook and save into Menu Fragment
						BeanUser userInfo = MenuFragment.beanUserInfo.getUser();
						userInfo.setId(responseFbRegistered.getFacebook().getId());
						MenuFragment.beanUserInfo.setUser(userInfo);
						
						//save new MenuFragment into AppPreferences
						String responseStringUserDetails = gson.toJson(MenuFragment.beanUserInfo);
						APP.GLOBAL()
						.getEditor()
						.putString(APP.PREF.USER_DETAILS.key,
								responseStringUserDetails).commit();
						
						//Recall the clubDetail
						if (MenuFragment.beanUserInfo != null && MenuFragment.beanUserInfo.getUser() != null){
							uid = MenuFragment.beanUserInfo.getUser().getFbid();
							if(uid==null || uid.equals(""))
								uid = MenuFragment.beanUserInfo.getUser().getId();
							if(uid != null && uid.equals(""))
								uid = null;
						}
						if (ConstantUtil.isNetworkAvailable(getActivity()))
						{

							//http://162.13.137.28/siteadmin/api/clubdetail/?uid=null&id=" + clubId
							NetworkTask networkTask = new NetworkTask(getActivity(), CLUB_DETAIL);
							networkTask.setProgressDialog(true);
							networkTask.exposePostExecute(ClubDetailFragment.this);
							networkTask.execute(ConstantUtil.getClubDetailUrl + "uid=" + uid + "&id=" + clubId + "&&user_id=" + AppPreferences.getInstance(getActivity()).getUserId());
						}
						else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
					}
				}
			}
			break;
		default:
			break;
		}

	}

	private void fbCheckIn(String placeid, String msg, String lat, String lng,String fbid,String fbFanId)
	{
		try
		{
			Bundle params = new Bundle();
			params.putString("access_token", APP.GLOBAL().getPreferences().getString(APP.PREF.FB_ACCESS_TOKEN.key, ""));
			params.putString("place", fbFanId); // YOUR PLACE ID
			params.putString("message", msg);
			JSONObject coordinates = new JSONObject();
			coordinates.put("latitude", lat);
			coordinates.put("longitude", lng);
			params.putString("coordinates", coordinates.toString());
			params.putString("tags", fbid);//where xx indicates the User Id

			String response = LoginScreen.facebook.request("me/checkins", params, "POST");

			System.out.println(response);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v)
	{
		AppPreferences appPref = new AppPreferences(getActivity());
		switch (v.getId())
		{
		case R.id.viewfeed:

			Bundle b = new Bundle();
			b.putString("clubId", clubId);
			activityCallback.onSendMessage(new ViewFeedFragment(), b, "ClubDetailFragment");

			break;
		case R.id.llMap:

			Intent i = new Intent(getActivity(),BarMapScreen.class);
			i.putExtra("lattitude", beanListClubDetail.getClubdetial().get(0).getAddress_lat());
			i.putExtra("longitude", beanListClubDetail.getClubdetial().get(0).getAddress_lng());
			i.putExtra("barName", beanListClubDetail.getClubdetial().get(0).getName());
			startActivity(i);

			break;
		case R.id.btnfollow:
			if (!appPref.getSneakStatus())
			{
				/*newapi/savefollowstatus?user_id=<user_id>&follow_type_id=<follow_type_id>&follow_type=<follow_type>
					(i) follow_type_id : venue id or user id
					(ii) follow_type : venue/user
				 */
				if(beanListClubDetail.getClubdetial().get(0).getFollow().equals("0"))
				{
				NetworkTask networkTask = new NetworkTask(getActivity(), FOLLOW);
				networkTask.exposePostExecute(ClubDetailFragment.this);
				networkTask.setProgressDialog(true);
				networkTask.execute(ConstantUtil.baseUrl + "follow/savefollowstatus?follow_type=venue&user_id=" + AppPreferences.getInstance(getActivity()).getUserId() + "&follow_type_id="
						+ beanListClubDetail.getClubdetial().get(0).getId());
				}
				else
				{
					NetworkTask networkTask = new NetworkTask(getActivity(), UNFOLLOW);
					networkTask.exposePostExecute(ClubDetailFragment.this);
					networkTask.setProgressDialog(true);
					networkTask.execute(ConstantUtil.unfollowUrl +"follow_type=venue&user_id=" + AppPreferences.getInstance(getActivity()).getUserId() + "&id="
							+ beanListClubDetail.getClubdetial().get(0).getId());
				}
			}
			else
			{
				ConstantUtil.showSneakDialog(getActivity());
			}
			break;
		case R.id.rlBookNow:
			switch (ACTION)
			{
			case ACTION_BOOK_NOW:

				if (!appPref.getSneakStatus())
				{
					Intent intent = new Intent(getActivity(), BookingActivity.class);
					intent.putExtra("v_name", beanListClubDetail.getClubdetial().get(0).getName());
					intent.putExtra("v_id", beanListClubDetail.getClubdetial().get(0).getId());
					startActivity(intent);
				}
				else
				{
					ConstantUtil.showSneakDialog(getActivity());
				}
				break;
			case ACTION_ALERT:
				if (!appPref.getSneakStatus())
				{
					showDialog();
				}
				else
				{
					ConstantUtil.showSneakDialog(getActivity());
				}

				//Toast.makeText(getActivity(), "Alert", Toast.LENGTH_LONG).show();
				break;
			case ACTION_FB_CHECKIN:

				if (!appPref.getSneakStatus())
				{
					NetworkTask mytask = new NetworkTask(getActivity(), CHECKIN);
					mytask.exposeDoinBackground(ClubDetailFragment.this);
					mytask.exposePostExecute(ClubDetailFragment.this);
					mytask.execute("");

					//Toast.makeText(getActivity(), "Checkin", Toast.LENGTH_LONG).show();
				}
				else
				{
					ConstantUtil.showSneakDialog(getActivity());
				}

				break;
			case ACTION_GEN_FREE:

				if (!appPref.getSneakStatus())
				{
					NetworkTask networkTask2 = new NetworkTask(getActivity(), ACTION_GEN_FREE);
					networkTask2.setProgressDialog(true);
					networkTask2.exposePostExecute(ClubDetailFragment.this);
					//club/generatePrevCode?id=<venue_id>&uid=<facebook_id>
					networkTask2.execute(ConstantUtil.baseUrl + "club/generatecode?id=" + clubId + "&uid=" + uid);
				}
				else
				{
					ConstantUtil.showSneakDialog(getActivity());
				}

				break;
			case ACTION_FBLOGIN:

				loginToFacebook();

				break;
			case ACTION_GEN_PREV:
				if (!appPref.getSneakStatus())
				{
					//http://162.13.137.28/siteadmin/api/clubdetail/?club/generatePrevCode?id=182&uid=null
					NetworkTask networkTask1 = new NetworkTask(getActivity(), ACTION_GEN_PREV);
					networkTask1.setProgressDialog(true);
					networkTask1.exposePostExecute(ClubDetailFragment.this);
					//club/generatePrevCode?id=<venue_id>&uid=<facebook_id>
					networkTask1.execute(ConstantUtil.baseUrl + "club/generatePrevCode?id=" + clubId + "&uid=" + uid);
				}
				else
				{
					ConstantUtil.showSneakDialog(getActivity());
				}
				break;
			default:
				break;
			}
			break;
		case R.id.tvCallNow:
			if (!appPref.getSneakStatus())
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + beanListClubDetail.getClubdetial().get(0).getPhone_number()));
				startActivity(callIntent);
			}
			else
			{
				ConstantUtil.showSneakDialog(getActivity());
			}
			break;
			
			
		default:
			break;
		}

	}

	@Override
	public String doInBackground(Integer id, String... params) {
		switch (id) {
		case CHECKIN:
			fbCheckIn(beanListClubDetail.getClubdetial().get(0).getLocations_id()
					, beanListClubDetail.getClubdetial().get(0).getOffername()
					, beanListClubDetail.getClubdetial().get(0).getAddress_lat()
					, beanListClubDetail.getClubdetial().get(0).getAddress_lng()
					,uid, beanListClubDetail.getClubdetial().get(0).getFbfanid());

			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	// facebook login
	public void loginToFacebook() {

		String access_token = APP.GLOBAL().getPreferences().getString(APP.PREF.FB_ACCESS_TOKEN.key, null);
		long expires =  APP.GLOBAL().getPreferences().getLong(APP.PREF.FB_ACCESS_EXPIRES.key, 0);

		if (access_token != null) {
			LoginScreen.facebook.setAccessToken(access_token);

			Log.d("FB Sessions", "" + LoginScreen.facebook.isSessionValid());
		}

		if (expires != 0) {
			LoginScreen.facebook.setAccessExpires(expires);
		}

		if (!LoginScreen.facebook.isSessionValid()) {
			LoginScreen.facebook.authorize(getActivity(), new String[] { "email","publish_actions" },
					new DialogListener() {

				@Override
				public void onCancel() {

				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					APP.GLOBAL().getEditor().putString(APP.PREF.FB_ACCESS_TOKEN.key, LoginScreen.facebook.getAccessToken()).commit();
					APP.GLOBAL().getEditor().putLong(APP.PREF.FB_ACCESS_EXPIRES.key, LoginScreen.facebook.getAccessExpires()).commit();


					if (ConstantUtil
							.isNetworkAvailable(getActivity())) {
						NetworkTask fblLoginTask = new NetworkTask(
								getActivity(), LOGIN_FB);
						fblLoginTask
						.exposePostExecute(ClubDetailFragment.this);
						fblLoginTask.execute(ConstantUtil.fbLoginUrl
								+ "st=" + LoginScreen.facebook.getAccessToken());
					}
					// new FbLoginTask(LoginScreen.this).execute();
					else
						Toast.makeText(getActivity(),
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
			if (ConstantUtil.isNetworkAvailable(getActivity())) {
				NetworkTask fblLoginTask = new NetworkTask(getActivity(),
						LOGIN_FB);
				fblLoginTask.exposePostExecute(ClubDetailFragment.this);
				fblLoginTask.execute(ConstantUtil.fbLoginUrl + "st="
						+ LoginScreen.facebook.getAccessToken());
			}
			else
				Toast.makeText(getActivity(),
						getString(R.string.internetFailure), Toast.LENGTH_LONG)
						.show();
		}

	}
	
	private void fbLogout()
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					LoginScreen.facebook.logout(APP.GLOBAL().getApplicationContext());
					LoginScreen.facebook.setAccessToken(null);
					APP.GLOBAL().getEditor().putString(APP.PREF.FB_ACCESS_TOKEN.key, null).commit();
					APP.GLOBAL().getEditor().putLong(APP.PREF.FB_ACCESS_EXPIRES.key, 0).commit();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		
			LoginScreen.facebook.authorizeCallback(requestCode, resultCode, data);  
	}
	
	

}
