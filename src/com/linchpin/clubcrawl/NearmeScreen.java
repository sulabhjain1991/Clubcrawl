package com.linchpin.clubcrawl;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.beans.BeanNearByClubList;
import com.linchpin.clubcrawl.fragments.ParentFragment;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.tickets.Results;
import com.linchpin.clubcrawl.jobjects.tickets.TicketResult;

public class NearmeScreen extends ParentFragment implements OnClickListener, Result, ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{
	private GoogleMap					mGoogleMap;
	private double						mLatitude		= 0;
	private double						mLongitude		= 0;
	protected final int					NEARBY			= 0x00002;
	protected final int					TICKETS			= 0x00001;
	private String						freecode		= "0";
	private LocationClient				locationClient;
	private LocationRequest				mLocationRequest;
	private String						currentBtn		= "Offer";
	private Marker						previousMarker	= null;
	TextView							tvOffer, tvAll, tvGuest, tvTicket;
	View								vOffer, vAll, vGuest, vTicket;
	TextView							tvLocation;
	ImageView							toggleLoc;
	private Boolean						flag			= false;
	private boolean						showmyloc		= false;
	private List<BeanNearByClubList>	listBeanNearByClubList;
	private ArrayList<Results>			results;
	private BitmapDescriptor bm;
	Button follow;
	View view;

	private void findALLViews()
	{
		
		tvOffer = (TextView) view.findViewById(R.id.tvOffers);
		tvAll = (TextView) view.findViewById(R.id.tvAll);
		tvGuest = (TextView) view.findViewById(R.id.tvGuestList);
		tvTicket = (TextView) view.findViewById(R.id.tvTickets);
		vOffer = (View) view.findViewById(R.id.vOffers);
		vAll = (View) view.findViewById(R.id.vAll);
		vGuest = (View) view.findViewById(R.id.vGuestList);
		vTicket = (View) view.findViewById(R.id.vTickets);
		follow = (Button) view.findViewById(R.id.btnfollow);
		follow.setOnClickListener(NearmeScreen.this);
		tvOffer.setOnClickListener(this);
		tvAll.setOnClickListener(this);
		tvGuest.setOnClickListener(this);
		tvTicket.setOnClickListener(this);
		//tvLocation.setOnClickListener(this);
		
	}

	private void resetSubBar()
	{
		tvOffer.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvAll.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvGuest.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvTicket.setTextColor(getResources().getColor(R.color.list_fg_nor));

		vOffer.setBackgroundColor(getResources().getColor(R.color.transparent));
		vAll.setBackgroundColor(getResources().getColor(R.color.transparent));
		vGuest.setBackgroundColor(getResources().getColor(R.color.transparent));
		vTicket.setBackgroundColor(getResources().getColor(R.color.transparent));
	}

	private void selectSubBarItem(int id)
	{

		switch (id)
		{
			case R.id.tvOffers:
				tvOffer.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vOffer.setBackgroundColor(getResources().getColor(R.color.background_color));

				break;
			case R.id.tvAll:
				tvAll.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vAll.setBackgroundColor(getResources().getColor(R.color.background_color));
				break;
			case R.id.tvGuestList:
				tvGuest.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vGuest.setBackgroundColor(getResources().getColor(R.color.background_color));
				break;
			case R.id.tvTickets:
				tvTicket.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vTicket.setBackgroundColor(getResources().getColor(R.color.background_color));
				break;
			default:
				break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if (view == null)
		{
	
		getActivity().setTheme(R.style.Theme_AppCompat_Light);
		view = inflater.inflate(R.layout.activity_nearme, container,false);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
		 boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		 findALLViews();
			resetSubBar();
			selectSubBarItem(R.id.tvAll);
			currentBtn = "All";
/*		if(!statusOfGPS)
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NearmeScreen.this);
			alertDialogBuilder.setTitle("Alert!");
			// set dialog message
			alertDialogBuilder
				.setMessage("Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						finish();
						
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						finish();
						dialog.dismiss();

					}
				});

			alertDialogBuilder.show();
			
		
			
			
			
			
		}*/
		
		
//		else
//		{
		
		results = new ArrayList<Results>();
		MapsInitializer.initialize(getActivity());
		bm = BitmapDescriptorFactory.fromResource(R.drawable.logo_round_icon_hover45x);
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if (status != ConnectionResult.SUCCESS)
		{
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
			dialog.show();
		}
		else
		{

			SupportMapFragment fragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
			mGoogleMap = fragment.getMap();
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setSmallestDisplacement(1000);
			mLocationRequest.setFastestInterval(1000);
			locationClient = new LocationClient(getActivity(), this, this);
			locationClient.connect();
			init();
			changeLocation();
		}
//	}
		}
		else ((ViewGroup) view.getParent()).removeAllViews();
		return view;
	}

	private void init()
	{
		toggle(flag);
		if (!currentBtn.equals("Tickets"))
		{
			if (!showmyloc) getNereBy(freecode);
			else getNereBy(APP.GLOBAL().getPreferences().getString(APP.PREF.DEVICE_LAT.key, "0.0"), APP.GLOBAL().getPreferences().getString(APP.PREF.DEVICE_LNG.key, "0.0"), "", freecode);
		}
		else getNereTickets();

	}

	private void toggle(Boolean flag)
	{
		if (flag == true)
		{
			tvLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			tvLocation.setText("Near Me");
		}
		else
		{tvLocation = (TextView) getActivity().findViewById(R.id.tvLocation);
			tvLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			tvLocation.setText(AppPreferences.getInstance(getActivity()).getCityName());
		}
		final RelativeLayout rlBottom = (RelativeLayout) view.findViewById(R.id.bottomLayout);
		previousMarker = null;
		if(rlBottom.getVisibility() == View.VISIBLE)
		{
			rlBottom.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume()
	{
		tvLocation = (TextView) getActivity().findViewById(R.id.tvLocation);
		if (!tvLocation.getText().toString().trim().equals(AppPreferences.getInstance(getActivity()).getCityName())) init();
		getActivity().findViewById(R.id.ivSearch).setVisibility(View.GONE);
		toggleLoc = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		toggleLoc.setImageResource(R.drawable.map_1_gray);
		getActivity().findViewById(R.id.ivMenu).setVisibility(View.GONE);
		getActivity().findViewById(R.id.ivSearch).setVisibility(View.GONE);
		toggleLoc.setOnClickListener(this);
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		toggleLoc = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		toggleLoc.setImageResource(R.drawable.icon1);
		getActivity().findViewById(R.id.ivMenu).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
		super.onPause();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
		System.out.println("Connection result::" + arg0);
		locationClient.connect();

	}

	@Override
	public void onConnected(Bundle arg0)
	{
		locationClient.requestLocationUpdates(mLocationRequest, NearmeScreen.this);
	}

	@Override
	public void onDisconnected()
	{
		locationClient.connect();

	}

	@Override
	public void onLocationChanged(Location location)
	{
		prepareUrlForOffers(location);
	}

	private void getNereBy(String freecode)
	{
		getNereBy(APP.GLOBAL().getPreferences().getString(APP.PREF.CITY_LAT.key, "51.508515"), APP.GLOBAL().getPreferences().getString(APP.PREF.CITY_LNG.key, "-0.12548719999995228"), AppPreferences
				.getInstance(getActivity()).getCityId(), freecode);
	}

	private void getNereBy(String lat, String lng, String cityid, String freecode)
	{
		cityid = AppPreferences
				.getInstance(getActivity()).getCityId();
		AppPreferences appPref = new AppPreferences(getActivity());
		String url = ConstantUtil.neaByUrl + "lat=" + lat + "&lng=" + lng + "&location=" + cityid + "&isfreecode=" + freecode+"&user_id="+appPref.getUserId();
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			NetworkTask networkTask = new NetworkTask(getActivity(), NEARBY);
			networkTask.exposePostExecute(this);
			networkTask.setProgressDialog(true);
			networkTask.execute(url);
			LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
			if (mGoogleMap != null)
			{
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
			}
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
	}

	private void getNereTickets()
	{
		String url = ConstantUtil.neaByTketsUrl, lat, lng;
		if (showmyloc)
		{
			lat = APP.GLOBAL().getPreferences().getString(APP.PREF.DEVICE_LAT.key, "0.0");
			lng = APP.GLOBAL().getPreferences().getString(APP.PREF.DEVICE_LNG.key, "0.0");
		}
		else
		{
			lat = APP.GLOBAL().getPreferences().getString(APP.PREF.CITY_LAT.key, "51.500153");
			lng = APP.GLOBAL().getPreferences().getString(APP.PREF.CITY_LNG.key, "-0.126236");

		}
		url += "lat=" + lat + "&lng=" + lng;
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			NetworkTask networkTask = new NetworkTask(getActivity(), TICKETS);
			networkTask.exposePostExecute(this);
			networkTask.setProgressDialog(true);
			networkTask.execute(url);
			LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
			if (mGoogleMap != null)
			{
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
			}
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
	}

	public void prepareUrlForOffers(Location location)
	{
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		APP.GLOBAL().getEditor().putString(APP.PREF.DEVICE_LAT.key, mLatitude + "").commit();
		APP.GLOBAL().getEditor().putString(APP.PREF.DEVICE_LNG.key, mLongitude + "").commit();
		//
		// LatLng latLng = new LatLng(mLatitude, mLongitude);
		// if (ConstantUtil.isNetworkAvailable(NearmeScreen.this))
		// getNereBy("51.508515", "-0.12548719999995228",
		// AppPreferences.getInstance(this).getCityId(), "13");
		// else Toast.makeText(NearmeScreen.this,
		// getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
		//
		// mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		// mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

	}
	 int itemId;
	private void setProfileImage(String url)
	{
		ImageView ivLogoClub = (ImageView) view.findViewById(R.id.ivLogoClub);
		AQuery aq = new AQuery(ivLogoClub);

		setWidthAndHeightOfImage();
		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;

			aq.id(R.id.ivLogoClub).image(url, options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			ivLogoClub.setImageBitmap(bm);
		}
	}

	private void setWidthAndHeightOfImage()
	{
		ImageView ivLogoClub = (ImageView) view.findViewById(R.id.ivLogoClub);
		ConstantUtil.getScreen_Height(getActivity());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ConstantUtil.screenWidth * 20 / 100, ConstantUtil.screenWidth * 20 / 100);
		params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params.setMargins(10, 10, 10, 10);
		ivLogoClub.setLayoutParams(params);

	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
			case R.id.ivComposePost:
				changeLocation();
				break;
			case R.id.tvLocation:
				Intent intent = new Intent(getActivity(), LocationSelection.class);
				startActivity(intent);
				break;
			case R.id.tvAll:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("All"))
				{
					currentBtn = "All";
					freecode = "0";
					previousMarker = null;
					follow.setVisibility(View.VISIBLE);
					init();
//					if (ConstantUtil.isNetworkAvailable(NearmeScreen.this)) getNereBy("0");
//					else Toast.makeText(NearmeScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}

				break;
			case R.id.tvOffers:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("Offer"))
				{
					freecode = "13";
					previousMarker = null;
					currentBtn = "Offer";
					follow.setVisibility(View.VISIBLE);
					init();
//					if (ConstantUtil.isNetworkAvailable(NearmeScreen.this)) getNereBy("13");
//					else Toast.makeText(NearmeScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}

				break;
			case R.id.tvGuestList:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("Guest"))
				{
					freecode = "16";
					previousMarker = null;
					currentBtn = "Guest";
					follow.setVisibility(View.VISIBLE);
					init();
//					if (ConstantUtil.isNetworkAvailable(NearmeScreen.this)) getNereBy("16");
//					else Toast.makeText(NearmeScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.tvTickets:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("Tickets"))
				{
					previousMarker = null;
					currentBtn = "Tickets";
					init();
					follow.setVisibility(View.GONE);
//					if (ConstantUtil.isNetworkAvailable(NearmeScreen.this)) getNereTickets();
//					else Toast.makeText(NearmeScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.btnfollow:
				
				if(follow.getText().toString().equals("+ Follow"))
				{
				// To follow
				
				if(!AppPreferences.getInstance(getActivity()).getSneakStatus()){
				NetworkTask networkTask = new NetworkTask(getActivity(), FOLLOW);
				networkTask.exposePostExecute(new Result() {
					
					@Override
					public void resultfromNetwork(String object, int id, int arg1, String arg2) {
						// TODO Auto-generated method stub
						JSONObject result;
						try {
							result = new JSONObject(object);
							if (result.get("status").equals("success")||result.get("status").equals("1")||result.get("status").equals("0")||result.get("status").equals("2"))
							{
								follow.setText("Following");
								follow.setTextColor(getResources().getColor(android.R.color.white));
								follow.setBackgroundResource(R.drawable.following_btn_shape);
							}
							else
							{
								Toast toast= Toast.makeText(getActivity(),"Network error try again later", Toast.LENGTH_SHORT);

								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						
					}
				});
				networkTask.setProgressDialog(true);
				
				networkTask.execute(ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&follow_type_id="+listBeanNearByClubList.get(itemId).getId()+"&follow_type=venue");
				}
				else
				{
					ConstantUtil.showSneakDialog(getActivity());
				}
		        }
		        else
		        {
			     // to unfollow
					if(!AppPreferences.getInstance(getActivity()).getSneakStatus()){
					NetworkTask networkTask = new NetworkTask(getActivity(), FOLLOW);
					networkTask.exposePostExecute(new Result() {
						
						@Override
						public void resultfromNetwork(String object, int id, int arg1, String arg2) {
							// TODO Auto-generated method stub
							JSONObject result;
							try {
								result = new JSONObject(object);
								if (result.get("status").equals("success"))
								{
									follow.setText("+ Follow");
									follow.setTextColor(getResources().getColor(R.color.background_color));
									follow.setBackgroundResource(R.drawable.follow_btn_transparent);
								}
								else
								{
									Toast toast= Toast.makeText(getActivity(),"Network error try again later", Toast.LENGTH_SHORT);

									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
							
						}
					});
					networkTask.setProgressDialog(true);
					networkTask.execute(ConstantUtil.unFollowClickUrl+"follow_type=venue&id="+listBeanNearByClubList.get(itemId).getId()+ "&user_id="+AppPreferences.getInstance(getActivity()).getUserId());
					}
					else
					{
						ConstantUtil.showSneakDialog(getActivity());
					}
			        
			
			
			
			
		        }

					
				break;
			default:
				break;
		}

	}
	private void changeLocation() 
	{
		String msg = "";
		if (!showmyloc)
		{
			msg = getString(R.string.map_myloc);
		}
		else msg = getString(R.string.map_city);
	
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Near Me");
		// set dialog message
		alertDialogBuilder
			.setMessage("Would you like to use your current location to find venues that are close by?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					showmyloc = !showmyloc;
					flag = !flag;
					init();

					dialog.dismiss();
					
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.dismiss();

				}
			});

		alertDialogBuilder.show();
		
	
		
		
		
		
		
		}
	
		
		
		
		
		
	
	private static final int	FOLLOW				= 0x007;
	
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
					if (res.get("status").equals("success")||res.get("status").equals("venue already Followed"))
					{
						follow.setText("Following");
						follow.setTextColor(getResources().getColor(android.R.color.white));
						follow.setBackgroundResource(R.drawable.following_btn_shape);
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
			case NEARBY:
				if (result != null && !result.equals(""))
				{
					listBeanNearByClubList = new ArrayList<BeanNearByClubList>();
					JSONArray array = null;
					try
					{
						array = new JSONArray(result);
						for (int i = 0; i < array.length(); i++)
						{
							String obj = array.getString(i);
							BeanNearByClubList beanNearByClub = gson.fromJson(obj, BeanNearByClubList.class);
							listBeanNearByClubList.add(beanNearByClub);
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}

				}
				if (listBeanNearByClubList == null) Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				else
				{
					mGoogleMap.clear();
					if (listBeanNearByClubList.size() == 0)
					{
						/*Toast.makeText(NearmeScreen.this,
								"No near by hospital found.", Toast.LENGTH_LONG)
								.show();*/
					}
					else
					{
						for (int i = 0; i < listBeanNearByClubList.size(); i++)
						{
							MarkerOptions markerOptions = new MarkerOptions();
							markerOptions.snippet(String.valueOf(i));
							double lat = Double.parseDouble(listBeanNearByClubList.get(i).getAddress_lat());
							double lng = Double.parseDouble(listBeanNearByClubList.get(i).getAddress_lng());
							LatLng latLng = new LatLng(lat, lng);
							markerOptions.position(latLng);
							markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_round_icon_hover45x));
							Marker mark = mGoogleMap.addMarker(markerOptions);
							mark.setTitle(String.valueOf(i));
							mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
							{
								@Override
								public boolean onMarkerClick(Marker arg0)
								{
									if (!arg0.getTitle().equals("nearMe"))
									{
										if (previousMarker != null) previousMarker.setIcon(bm);
										arg0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.logo_map_hover2));

										previousMarker = arg0;
										itemId = Integer.parseInt(arg0.getTitle());
										final RelativeLayout rlBottom = (RelativeLayout) view.findViewById(R.id.bottomLayout);
										if (rlBottom.getVisibility() == View.GONE)
										{

											Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
											rlBottom.setAnimation(slideUp);
											TextView tvClubName = (TextView) view.findViewById(R.id.tvClubName);
											TextView tvClubAddress = (TextView) view.findViewById(R.id.tvClubAddress);
										 if( !listBeanNearByClubList.get(itemId).getFollow_status().equals("0"))
										 {

												follow.setText("Following");
												follow.setTextColor(getResources().getColor(android.R.color.white));
												follow.setBackgroundResource(R.drawable.following_btn_shape);
											}
											else
											{
												follow.setText("+ Follow");
												follow.setBackgroundResource(R.drawable.follow_btn_transparent);
												follow.setTextColor(getResources().getColor(R.color.background_color));
												
											}
											tvClubName.setText(listBeanNearByClubList.get(itemId).getName());

											tvClubAddress.setText(listBeanNearByClubList.get(itemId).getAddress());
											setProfileImage(ConstantUtil.nearByLogoImageBaseUrl + listBeanNearByClubList.get(itemId).getLogo());
											rlBottom.setVisibility(View.VISIBLE);
										}
										else
										{
											moveUpSliding();
										}
									}
									return true;

								}
							});

						}
					}
				}

				break;
			case TICKETS:
				if (result != null && !result.equals(""))
				{
					final TicketResult ticketResponse = gson.fromJson(result, TicketResult.class);

					if (ticketResponse == null && !ticketResponse.getError().equals("0") && ticketResponse.getPagecount().equals("0")) Toast.makeText(getActivity(),
							getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
					else
					{

						mGoogleMap.clear();
						results = ticketResponse.getResults();
						if (results.size() == 0)
						{//Toast.makeText(NearmeScreen.this, "No near by hospital found.", Toast.LENGTH_LONG).show();
							
						}
						else
						{
							for (int i = 0; i < results.size(); i++)
							{
								MarkerOptions markerOptions = new MarkerOptions();
								markerOptions.snippet(String.valueOf(i));
								double lat = Double.parseDouble(results.get(i).getVenue().getLatitude());
								double lng = Double.parseDouble(results.get(i).getVenue().getLongitude());
								LatLng latLng = new LatLng(lat, lng);
								markerOptions.position(latLng);
								markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_round_icon_hover45x));
								Marker mark = mGoogleMap.addMarker(markerOptions);
								mark.setTitle(String.valueOf(i));
								mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
								{
									@Override
									public boolean onMarkerClick(Marker arg0)
									{
										if (!arg0.getTitle().equals("nearMe"))
										{
											if (previousMarker != null) previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.logo_map));
											else
											arg0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.logo_map_hover2));

											previousMarker = arg0;
											final int id = Integer.parseInt(arg0.getTitle());
											final RelativeLayout rlBottom = (RelativeLayout) view.findViewById(R.id.bottomLayout);
											if (rlBottom.getVisibility() == View.GONE)
											{

												Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
												rlBottom.setAnimation(slideUp);
												TextView tvClubName = (TextView) view.findViewById(R.id.tvClubName);
												TextView tvClubAddress = (TextView) view.findViewById(R.id.tvClubAddress);
												Button btnFollow=(Button) view.findViewById(R.id.btnfollow);
												
												tvClubName.setText(results.get(id).getVenue().getName());
												tvClubAddress.setText(results.get(id).getVenue().getAddress());
												setProfileImage(results.get(id).getImageurl());
												rlBottom.setVisibility(View.VISIBLE);
											}
											else
											{
												Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
												rlBottom.setAnimation(slideDown);
												rlBottom.setVisibility(View.GONE);
												slideDown.setAnimationListener(new AnimationListener()
												{

													@Override
													public void onAnimationStart(Animation animation)
													{
													}

													@Override
													public void onAnimationRepeat(Animation animation)
													{
													}

													@Override
													public void onAnimationEnd(Animation animation)
													{
														Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
														TextView tvClubName = (TextView) view.findViewById(R.id.tvClubName);
														tvClubName.setText(results.get(id).getEventname());
														setProfileImage(results.get(id).getImageurl());
														rlBottom.setAnimation(slideUp);
														rlBottom.setVisibility(View.VISIBLE);
													}
												});
											}
										}
										return true;
									}
								});

							}
						}
					}
				}
				/*if(flag)
				{
					//device  lat and set pin

					MarkerOptions markerOptions = new MarkerOptions();
					double lat = Double.parseDouble(APP.GLOBAL().getPreferences()
							.getString(APP.PREF.DEVICE_LAT.key, "0.0")
							);
					double lng = Double.parseDouble(APP.GLOBAL().getPreferences()
							.getString(APP.PREF.DEVICE_LNG.key, "0.0"));
					LatLng latLng = new LatLng(lat, lng);
					markerOptions.position(latLng);
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.logo_map));
					Marker mark = mGoogleMap.addMarker(markerOptions);
				}
				else
				{
					//city lat and set pin
					MarkerOptions markerOptions = new MarkerOptions();
					double lat = Double.parseDouble(APP.GLOBAL().getPreferences()
							.getString(APP.PREF.CITY_LAT.key, "0.0")
							);
					double lng = Double.parseDouble(APP.GLOBAL().getPreferences()
							.getString(APP.PREF.CITY_LNG.key, "0.0"));
					LatLng latLng = new LatLng(lat, lng);
					markerOptions.position(latLng);
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.logo_map));
					Marker mark = mGoogleMap.addMarker(markerOptions);
				}*/
				break;
			default:
				break;
		}
		if (flag)
		{
			//device  lat and set pin

			MarkerOptions markerOptionsCurrentLocation = new MarkerOptions();
			double lat = Double.parseDouble(APP.GLOBAL().getPreferences().getString(APP.PREF.DEVICE_LAT.key, "0.0"));
			double lng = Double.parseDouble(APP.GLOBAL().getPreferences().getString(APP.PREF.DEVICE_LNG.key, "0.0"));
			LatLng latLng = new LatLng(lat, lng);
			markerOptionsCurrentLocation.position(latLng);
			markerOptionsCurrentLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_point64x));

			Marker mark = mGoogleMap.addMarker(markerOptionsCurrentLocation);
			mark.setTitle("nearMe");
		}
//		else
//		{
//			//city lat and set pin
//			MarkerOptions markerOptionsCurrentLocation = new MarkerOptions();
//			double lat = Double.parseDouble(APP.GLOBAL().getPreferences().getString(APP.PREF.CITY_LAT.key, "51.508515"));
//			double lng = Double.parseDouble(APP.GLOBAL().getPreferences().getString(APP.PREF.CITY_LNG.key, "-0.12548719999995228"));
//			LatLng latLng = new LatLng(lat, lng);
//			markerOptionsCurrentLocation.position(latLng);
//			markerOptionsCurrentLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_point64x));
//			Marker mark = mGoogleMap.addMarker(markerOptionsCurrentLocation);
//		}
	}

	protected void moveUpSliding() 
	{
		final RelativeLayout rlBottom = (RelativeLayout) view.findViewById(R.id.bottomLayout);
		Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
		rlBottom.setAnimation(slideDown);
		rlBottom.setVisibility(View.GONE);
		slideDown.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
				TextView tvClubName = (TextView) view.findViewById(R.id.tvClubName);
				tvClubName.setText(listBeanNearByClubList.get(itemId).getName());
				setProfileImage(ConstantUtil.nearByLogoImageBaseUrl + listBeanNearByClubList.get(itemId).getLogo());
				 if( !listBeanNearByClubList.get(itemId).getFollow_status().equals("0"))
				 {

						follow.setText("Following");
						follow.setTextColor(getResources().getColor(android.R.color.white));
						follow.setBackgroundResource(R.drawable.following_btn_shape);
					}
					else
					{
						follow.setText("+ Follow");
						follow.setBackgroundResource(R.drawable.follow_btn_transparent);
						follow.setTextColor(getResources().getColor(R.color.background_color));
						
					}
				
				
				rlBottom.setAnimation(slideUp);
				rlBottom.setVisibility(View.VISIBLE);
				
				
			}
		});
		
	}
}
