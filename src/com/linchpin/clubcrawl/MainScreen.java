package com.linchpin.clubcrawl;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.linchpin.clubcrawl.MenuFragment.MenuFragmentInstance;
import com.linchpin.clubcrawl.beans.BeanNotificationCount;
import com.linchpin.clubcrawl.beans.BeanUser;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.fragments.AllOfferFragment;
import com.linchpin.clubcrawl.fragments.FriendFragment;
import com.linchpin.clubcrawl.fragments.GalleryFragment;
import com.linchpin.clubcrawl.fragments.GlobalSearch;
import com.linchpin.clubcrawl.fragments.InviteOutFriendsFragment;
import com.linchpin.clubcrawl.fragments.NightsOutFragment;
import com.linchpin.clubcrawl.fragments.SearchVenueFragment;
import com.linchpin.clubcrawl.fragments.TrendeFragment;
import com.linchpin.clubcrawl.fragments.WhosOutVenuesFragment;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.FacebookShared;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.helper.PopUpMenu;
import com.linchpin.clubcrawl.helper.TabViewHelper;
import com.linchpin.clubcrawl.interfaces.IMessanger;

public class MainScreen extends SlidingFragmentActivity implements IMessanger, OnClickListener, Result, MenuFragmentInstance
{
	TextView					tvLocation;
	String						finalResult;
	String						url					= "http://162.13.137.28/siteadmin/club/getlocations?locations_type=Location";
	public boolean						isSearching			= false;
	PopUpMenu					popup;
	private final int			NOTIFICATION		= 0;
	private MenuFragment		menuFragment;
	
	AppPreferences				appPreferences;
	EditText					edSearch;
	String						InAppBillingSKUId	= "ads_free_version_call_recorder_app";
	public IInAppBillingService	mService;
	ServiceConnection			mServiceConn		= new ServiceConnection()
													{

														@Override
														public void onServiceDisconnected(ComponentName name)
														{
															mService = null;
														}

														@Override
														public void onServiceConnected(ComponentName name, IBinder service)
														{
															mService = IInAppBillingService.Stub.asInterface(service);
														}

													};

	private void findAllViews()
	{
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		edSearch = ((EditText) findViewById(R.id.edSearch));

	}

	public void searchViewToggle()
	{
		if (!isSearching)
		{
			findViewById(R.id.normal_pan).setVisibility(View.GONE);
			findViewById(R.id.search_pan).setVisibility(View.VISIBLE);
		}
		else
		{
			findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
			findViewById(R.id.search_pan).setVisibility(View.GONE);
		}
		isSearching = !isSearching;
	}

	private void setClickListners()
	{
		tvLocation.setOnClickListener(this);
		findViewById(R.id.ivMenu).setOnClickListener(this);
		findViewById(R.id.llHome).setOnClickListener(this);
		findViewById(R.id.llSearchVenue).setOnClickListener(this);
		findViewById(R.id.llNearme).setOnClickListener(this);
		findViewById(R.id.llAllOffers).setOnClickListener(this);
		findViewById(R.id.llmore).setOnClickListener(this);
		findViewById(R.id.ivSearch).setOnClickListener(this);
		//		findViewById(R.id.ivComposePost).setOnClickListener(this);
		findViewById(R.id.btCancel).setOnClickListener(this);

		//findViewById(R.id.ivNotifications).setOnClickListener(this);
	}

	public void resetBottonBar()
	{
		int color = getResources().getColor(R.color.text_area_fg);
		ImageView iv = (ImageView) findViewById(R.id.ivHome);
		iv.setBackgroundResource(R.drawable.home_icon);
		TextView tv = (TextView) findViewById(R.id.tvHome);
		tv.setTextColor(color);

		iv = (ImageView) findViewById(R.id.ivSearchVenue);
		iv.setBackgroundResource(R.drawable.search_icon1);
		tv = (TextView) findViewById(R.id.tvSearchVenue);
		tv.setTextColor(color);

		iv = (ImageView) findViewById(R.id.ivNearMe);
		iv.setBackgroundResource(R.drawable.nearby_icon);
		tv = (TextView) findViewById(R.id.tvNearMe);
		tv.setTextColor(color);

		iv = (ImageView) findViewById(R.id.ivAllOffers);
		iv.setBackgroundResource(R.drawable.offer_icon);
		tv = (TextView) findViewById(R.id.tvAllOffers);
		tv.setTextColor(color);

		iv = (ImageView) findViewById(R.id.ivmore);
		iv.setBackgroundResource(R.drawable.more_icon);
		tv = (TextView) findViewById(R.id.tvmore);
		tv.setTextColor(color);
	}

	public void bottomBarItemSelected(int id)
	{
		ImageView iv;
		TextView tv = null;
		switch (id)
		{
			case R.id.llHome:
				iv = (ImageView) findViewById(R.id.ivHome);
				iv.setBackgroundResource(R.drawable.home_1);
				tv = (TextView) findViewById(R.id.tvHome);

				break;
			case R.id.llSearchVenue:
				iv = (ImageView) findViewById(R.id.ivSearchVenue);
				iv.setBackgroundResource(R.drawable.search_icon_hover);
				tv = (TextView) findViewById(R.id.tvSearchVenue);
				break;
			case R.id.llNearme:
				iv = (ImageView) findViewById(R.id.ivNearMe);
				iv.setBackgroundResource(R.drawable.nearby_icon_hover);
				tv = (TextView) findViewById(R.id.tvNearMe);

				break;
			case R.id.llAllOffers:
				iv = (ImageView) findViewById(R.id.ivAllOffers);
				iv.setBackgroundResource(R.drawable.offer_icon_hover);
				tv = (TextView) findViewById(R.id.tvAllOffers);
				break;
			case R.id.llmore:
				iv = (ImageView) findViewById(R.id.ivmore);
				iv.setBackgroundResource(R.drawable.more_icon_hover48x);
				tv = (TextView) findViewById(R.id.tvmore);

				break;

			default:
				break;
		}
		if (null != tv) tv.setTextColor(getResources().getColor(R.color.background_color));
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_AppCompat_Light);
		setContentView(R.layout.activity_main_screen);
		bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);
		appPreferences = new AppPreferences(MainScreen.this);
		BaseClass bc = new BaseClass();
		bc.setFont(findViewById(R.id.mainLayout), this);
		findAllViews();
		setClickListners();
		if(getIntent().getSerializableExtra("loginInfo") != null)
		{
		BeanUser user = (BeanUser) getIntent().getSerializableExtra("loginInfo");
		if (MenuFragment.beanUserInfo == null) MenuFragment.beanUserInfo = new BeanUserInfo();
		MenuFragment.beanUserInfo.setUser(user);
		}
		else
		{
			Gson gson = new Gson();
			MenuFragment.beanUserInfo = (BeanUserInfo)(gson.fromJson(APP.GLOBAL().getPreferences().getString(APP.PREF.USER_DETAILS.key, ""), BeanUserInfo.class));
		}
		FragmentManager fm = getSupportFragmentManager();

		;
		//	getSupportFragmentManager().popBackStack();
		GalleryFragment fr = new GalleryFragment();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.add(R.id.frame, fr);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		TabViewHelper.smoothLeftRightNavigation(this);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && getSupportFragmentManager().getBackStackEntryCount() == 1)
			{
			 FragmentManager.BackStackEntry backEntry=getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1);
			    String str=backEntry.getName();
//			    if(str)
//			    Fragment fragment=getSupportFragmentManager().findFragmentByTag(str);
			finish();
			
			}
		else if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			getSupportFragmentManager().popBackStack();
		}
		
		return true;
	}
	


	@Override
	public void onSendMessage(Fragment fr, Bundle b, String fragmentName)
	{

		FragmentManager fm = getSupportFragmentManager();

		if (fr instanceof GalleryFragment || fr instanceof SearchVenueFragment || fr instanceof AllOfferFragment || fr instanceof TrendeFragment || fr instanceof FriendFragment
				|| fr instanceof InviteOutFriendsFragment || fr instanceof NightsOutFragment || fr instanceof WhosOutVenuesFragment) fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction fragmentTransaction = fm.beginTransaction();

		fr.setArguments(b);
		fragmentTransaction.replace((R.id.frame), fr/*, fragmentName*/);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onClick(View v)
	{
		final AppPreferences appPref = new AppPreferences(MainScreen.this);
		int id = v.getId();
		switch (id)
		{
		
			case R.id.tvLocation:
				Intent intent = new Intent(MainScreen.this, LocationSelection.class);
				startActivity(intent);
				break;
			case R.id.ivMenu:

				if (!appPref.getSneakStatus())
				{
					toggle();

					NetworkTask networkTask = new NetworkTask(MainScreen.this, NOTIFICATION, 0, null);
					networkTask.setProgressDialog(false);
					networkTask.exposePostExecute(MainScreen.this);
					String url = ConstantUtil.getNotificationCount + "userID=" + appPref.getUserId();
					networkTask.execute(url);
				}
				else
				{
					ConstantUtil.showSneakDialog(MainScreen.this);
				}
				break;
			case R.id.llHome:
				isSearching = true;
				searchViewToggle();
				edSearch.setText("");
				resetBottonBar();
				bottomBarItemSelected(id);
				onSendMessage(new GalleryFragment(), null, "");
				break;
			case R.id.llSearchVenue:
				isSearching = true;
				searchViewToggle();
				edSearch.setText("");
				resetBottonBar();
				bottomBarItemSelected(id);
				onSendMessage(new SearchVenueFragment(), null, "");
				break;
			case R.id.llNearme:
				isSearching = true;
				/*Intent i = new Intent(MainScreen.this, NearmeScreen.class);
				startActivity(i);*/
				searchViewToggle();
				edSearch.setText("");
				resetBottonBar();
				bottomBarItemSelected(id);
				onSendMessage(new NearmeScreen(), null, "");
				break;
			case R.id.llAllOffers:
				isSearching = true;
				searchViewToggle();
				edSearch.setText("");
				resetBottonBar();
				bottomBarItemSelected(id);
				onSendMessage(new AllOfferFragment(), null, "");
				break;
			case R.id.llmore:
				isSearching = true;
				searchViewToggle();
				edSearch.setText("");
				resetBottonBar();
				bottomBarItemSelected(id);
				LinearLayout llMore = (LinearLayout) findViewById(R.id.llmore);
				popup = new PopUpMenu(MainScreen.this, llMore);
				popup.getMenuInflater().inflate(R.menu.custom_menu_for_more, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopUpMenu.OnMenuItemClickListener()
				{
					public boolean onMenuItemClick(MenuItem item)
					{
						switch (item.getItemId())
						{
							case R.id.trend:
								isSearching = true;
								searchViewToggle();
								edSearch.setText("");
								onSendMessage(new TrendeFragment(), null, "");
								/*FragmentManager fm = getSupportFragmentManager();
								fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
								TrendeFragment fr = new TrendeFragment();
								FragmentTransaction fragmentTransaction = fm.beginTransaction();

								fragmentTransaction.add(R.id.frame, fr, "SearchVenueFragment");
								fragmentTransaction.addToBackStack("TrendeFragment"null);
								fragmentTransaction.commit();*/
								break;

						/*	case R.id.friends:
								isSearching = true;
								searchViewToggle();
								edSearch.setText("");
								if (!appPref.getSneakStatus())
								{
									//Toast.makeText(getApplicationContext(), "Friends", Toast.LENGTH_LONG).show();
									onSendMessage(new FriendFragment(), null, "");
									FragmentManager fm1 = getSupportFragmentManager();
									fm1.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
									FriendFragment Ffragment = new FriendFragment();
									FragmentTransaction fragmentTransaction1 = fm1.beginTransaction();

									fragmentTransaction1.add(R.id.frame, Ffragment, "FtiendFragment");
									fragmentTransaction1.addToBackStack("FriendFragment"null);
									fragmentTransaction1.commit();
								}
								else
								{
									ConstantUtil.showSneakDialog(MainScreen.this);
								}
								break;*/
							case R.id.inviteFriendsOut:
								isSearching = true;
								searchViewToggle();
								edSearch.setText("");
								if (!appPref.getSneakStatus())
								{
									onSendMessage(new InviteOutFriendsFragment(), null, "");
									//								FragmentManager fm2 = getSupportFragmentManager();
									//								fm2.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
									//								InviteOutFriendsFragment fragmentFriendsOut = new InviteOutFriendsFragment();
									//								FragmentTransaction fragmentTransaction2 = fm2.beginTransaction();
									//
									//					fragmentTransaction2.add(R.id.frame, fragmentFriendsOut/*, "InviteOutFriends"*/);
									//					fragmentTransaction2.addToBackStack(/*"InviteOutFriends"*/null);
									//					fragmentTransaction2.commit();	
								}
								else
								{
									ConstantUtil.showSneakDialog(MainScreen.this);
								}
								break;

							case R.id.nightsout:
								isSearching = true;
								searchViewToggle();
								edSearch.setText("");
								if (!appPref.getSneakStatus())
								{

									onSendMessage(new NightsOutFragment(), null, "");
									/*FragmentManager fm3 = getSupportFragmentManager();
									fm3.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
									NightsOutFragment nightsOutFragment = new NightsOutFragment();
									FragmentTransaction fragmentTransaction3 = fm3.beginTransaction();

									fragmentTransaction3.add(R.id.frame, nightsOutFragment, "NightsOutFragment");
									fragmentTransaction3.addToBackStack("NightsOutFragment"null);
									fragmentTransaction3.commit();*/
								}
								else
								{
									ConstantUtil.showSneakDialog(MainScreen.this);
								}
								break;
							case R.id.whosOut:
								isSearching = true;
								searchViewToggle();
								edSearch.setText("");
								if (!appPref.getSneakStatus())
								{
									onSendMessage(new WhosOutVenuesFragment(), null, "");
									//						FragmentManager fm4 = getSupportFragmentManager();
									//						fm4.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
									//						WhosOutVenuesFragment whosOutFragment = new WhosOutVenuesFragment();
									//						FragmentTransaction fragmentTransaction4 = fm4.beginTransaction();
									//
									//						fragmentTransaction4.add(R.id.frame, whosOutFragment/*, "whosOutFragment"*/);
									//						fragmentTransaction4.addToBackStack(null);
									//						fragmentTransaction4.commit();	
								}
								else
								{
									ConstantUtil.showSneakDialog(MainScreen.this);
								}
								break;
						}

						//		}
						return true;

					}
				});
				popup.show();
				break;
			case R.id.btCancel:
				searchViewToggle();
				edSearch.setText("");
				onBackPressed();
				break;
			case R.id.ivSearch:
				searchViewToggle();
				onSendMessage(new GlobalSearch(), null, "");
				break;
			default:
				break;
		}

	}

	public void forRemoveAd()
	{
		System.out.println("Enter in for remove ad method");
		new GetPurchaseItems().execute();
	}

	/////////////////////////////////In-app purchase code///////////////////////////////////
	public class GetPurchaseItems extends AsyncTask<Void, Void, String>
	{

		Bundle				skuDetails;
		int					response;
		private String		sku;
		boolean				error	= false;
		Bundle				querySkus;
		ArrayList<String>	skuList;

		@Override
		protected void onPreExecute()
		{

			try
			{

			}
			catch (Exception e)
			{

			}
		}

		@Override
		protected String doInBackground(Void... params)
		{

			try
			{
				// TODO Auto-generated method stub
				if (mService != null)
				{
					skuList = new ArrayList<String>();
					skuList.add(InAppBillingSKUId);
					querySkus = new Bundle();
					querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

					try
					{

						skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
					}
					catch (RemoteException e)
					{
						// TODO Auto-generated catch block

						e.printStackTrace();
						return "customException";
					}
					int response = skuDetails.getInt("RESPONSE_CODE");

					if (response == 0)
					{
						ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

						for (String thisResponse : responseList)
						{
							JSONObject object = null;
							try
							{
								object = new JSONObject(thisResponse);
							}
							catch (JSONException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
								return "customException";
							}

							try
							{
								sku = object.getString("productId");
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "customException";
							}
							String price = null;
							try
							{
								price = object.getString("price");
								//context.title = object.getString("description");
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (InAppBillingSKUId.equals(sku))
							{
								//context.mPremiumUpgradePrice = price;
							}
							else
							{
								// Show that toast and Exit to Home screen;
								error = true;

							}

						}
					}
					try
					{

						Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", null);
						int responseForBuySku = buyIntentBundle.getInt("RESPONSE_CODE");
						response = responseForBuySku;
						if (responseForBuySku == 0)
						{

							PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
							try
							{
								startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
							}
							catch (SendIntentException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "customException";
							}
						}
						else if (responseForBuySku == 7)
						{
							response = 7;
						}

					}
					catch (RemoteException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "customException";
					}
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "customException";
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try
			{
				if (result != null && result.equals("customException"))
				{
					Toast.makeText(MainScreen.this, "An error has occured, Please try again again later", Toast.LENGTH_LONG).show();
				}
				else
				{
					if (response == 7)
					{
						/////////come here if  already purchased
						appPreferences.setAdPurchased(true);

					}
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub

		super.onDestroy();
		if (mServiceConn != null)
		{
			unbindService(mServiceConn);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1001)
		{
			{
				if (resultCode == RESULT_OK)
				{
					try
					{
						////////////Come here if first time purchased(only once)/////////////////
						appPreferences.setAdPurchased(true);
						String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
						JSONObject jo = new JSONObject(purchaseData);
						String sku = jo.getString("productId");

					}
					catch (JSONException e)
					{

						e.printStackTrace();
					}
				}
			}

		}
		else
			FacebookShared.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2)
	{
		switch (id)
		{
			case NOTIFICATION:
				if (object != null && !object.equals(""))
				{
					Gson gson = new Gson();
					BeanNotificationCount countsNotification = gson.fromJson(object, BeanNotificationCount.class);
					APP.GLOBAL().notificationcount = Integer.parseInt(countsNotification.getNotification_count());
					APP.GLOBAL().MessageCount = Integer.parseInt(countsNotification.getMsg_count());
					APP.GLOBAL().requestCount = Integer.parseInt(countsNotification.getRequest_count());
					if (menuFragment != null) menuFragment.changeNotificationcount();

				}

				break;

			default:
				break;
		}

	}

	@Override
	public void getInstance(MenuFragment fr)
	{
		menuFragment = fr;

	}
	
	

}
