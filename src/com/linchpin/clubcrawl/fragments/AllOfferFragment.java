package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.GSAdapter;
import com.linchpin.clubcrawl.adapters.OffersListAdapter;
import com.linchpin.clubcrawl.adapters.TicketsListAdapter;
import com.linchpin.clubcrawl.beans.BeanListOffers;
import com.linchpin.clubcrawl.beans.BeanListTickets;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.linchpin.clubcrawl.jobjects.inbox.GSearch;

public class AllOfferFragment extends ParentFragment implements Result, OnItemClickListener, OnClickListener, ConnectionCallbacks, OnConnectionFailedListener,TextWatcher
,com.google.android.gms.location.LocationListener
{

	private static final int	OFFER	= 0x0001;
	private static final int	TICKETS	= 0x0002;

	enum ADAPTER_TYPE
	{
		NOTHING, OFFER, TICKETS,GUEST

	}

	View					view, vOffers, vGuestListsAndTables, vTickets;
	BeanListOffers			beanListOffers;
	BeanListTickets			beanListTickets;
	String					currentBtn	= "AllOffers";
	private double			mLatitude	= 0;
	private double			mLongitude	= 0;
	private LocationRequest	mLocationRequest;
	private LocationClient	locationClient;
	TextView				tvOffers, tvGuestListsAndTables, tvTickets;
	ListView				lvOffersList;
	ADAPTER_TYPE			adapter_type;
	String currentTab="";
	private final int 			SEARCH_DATA = 1001;
	private EditText			edSearch; 
	NetworkTask					 networkTask;

	private ArrayList<GSResult>	gsResult;
	public boolean		localSearch	= false;
	private void findAllViews()
	{
		lvOffersList = (ListView) view.findViewById(R.id.lvOffers);
		tvOffers = (TextView) view.findViewById(R.id.tvGuestList);
		tvGuestListsAndTables = (TextView) view.findViewById(R.id.tvGuestlistsAndTables);
		tvTickets = (TextView) view.findViewById(R.id.tvTickets);
		edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
		vOffers = (View) view.findViewById(R.id.vGuestList);
		vGuestListsAndTables = (View) view.findViewById(R.id.vGuestlistsAndTables);
		vTickets = (View) view.findViewById(R.id.vTickets);
		lvOffersList.setOnItemClickListener(this);
		tvOffers.setOnClickListener(this);
		tvGuestListsAndTables.setOnClickListener(this);
		tvTickets.setOnClickListener(this);
	}

	private void resetSubBar()
	{
		tvOffers.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvGuestListsAndTables.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvTickets.setTextColor(getResources().getColor(R.color.list_fg_nor));

		vOffers.setBackgroundColor(getResources().getColor(R.color.transparent));
		vGuestListsAndTables.setBackgroundColor(getResources().getColor(R.color.transparent));
		vTickets.setBackgroundColor(getResources().getColor(R.color.transparent));
	}

	private void selectSubBarItem(int id)
	{
		if (getActivity() instanceof MainScreen)
		{
			currentTab=((TextView) view.findViewById(id)).getText().toString();
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText(currentTab);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
		}
		switch (id)
		{
		case R.id.tvGuestList:
			tvOffers.setTextColor(getResources().getColor(R.color.list_fg_selected));
			vOffers.setBackgroundColor(getResources().getColor(R.color.background_color));

			break;
		case R.id.tvGuestlistsAndTables:
			tvGuestListsAndTables.setTextColor(getResources().getColor(R.color.list_fg_selected));
			vGuestListsAndTables.setBackgroundColor(getResources().getColor(R.color.background_color));
			break;
		case R.id.tvTickets:
			tvTickets.setTextColor(getResources().getColor(R.color.list_fg_selected));
			vTickets.setBackgroundColor(getResources().getColor(R.color.background_color));
			break;

		default:
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (view == null)
		{
			MenuFragment.currentsel = "SearchVenueFragment";
			view = inflater.inflate(R.layout.fragment_alloffers, container, false);
			BaseClass bc = new BaseClass();
			bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
			findAllViews();
			resetSubBar();
			selectSubBarItem(R.id.tvGuestList);
			getListfrom("13", AppPreferences.getInstance(getActivity()).getCityId(), "");
			ImageView ivComposePost = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivComposePost.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					AppPreferences appPref = new AppPreferences(getActivity());
					if(!appPref.getSneakStatus())
					{
						Intent i = new Intent(getActivity(), ComposePostScreen.class);
						startActivity(i);
					}
					else
					{
						ConstantUtil.showSneakDialog(getActivity());
					}

				}
			});
		}
		else ((ViewGroup) view.getParent()).removeAllViews();
		return view;
	}

	private void getLatitudeAndLongitude()
	{
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if (status != ConnectionResult.SUCCESS)
		{
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
			dialog.show();
		}
		else
		{
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setSmallestDisplacement(1000);
			mLocationRequest.setFastestInterval(1000);
			locationClient = new LocationClient(getActivity(), AllOfferFragment.this, AllOfferFragment.this);
			locationClient.connect();
		}
	}

	public void prepareUrlForOffers(Location location)
	{
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		//		String url = ConstantUtil.ticketsUrl + "lattitude=" + mLatitude + "&longitude=" + mLongitude;
		String url = ConstantUtil.ticketsUrl + "lattitude=" + 51.508515 + "&longitude=" + -0.12548719999995228;

		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{

			NetworkTask networkTask = new NetworkTask(getActivity(), TICKETS);
			networkTask.setProgressDialog(true);
			networkTask.exposePostExecute(AllOfferFragment.this);
			networkTask.execute(url);
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		prepareUrlForOffers(arg0);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
		locationClient.connect();
	}

	@Override
	public void onConnected(Bundle arg0)
	{
		locationClient.requestLocationUpdates(mLocationRequest, AllOfferFragment.this);
	}

	@Override
	public void onDisconnected()
	{
		locationClient.connect();
	}

	private void getListfrom(String freecode, String location, String filter)
	{
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			NetworkTask networkTask = new NetworkTask(getActivity(), OFFER);
			networkTask.setProgressDialog(true);
			networkTask.exposePostExecute(AllOfferFragment.this);
			networkTask.execute(ConstantUtil.getAllOfferList + "isfreecode=" + freecode + "&filter=" + filter + "&location=" + location);
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.tvGuestList:
			resetSubBar();
			selectSubBarItem(id);

			getListfrom("13", AppPreferences.getInstance(getActivity()).getCityId(), "");

			break;
		case R.id.tvGuestlistsAndTables:
			resetSubBar();
			selectSubBarItem(id);
			getListfrom("16", AppPreferences.getInstance(getActivity()).getCityId(), "");
			break;
		case R.id.tvTickets:
			resetSubBar();
			selectSubBarItem(id);
			getLatitudeAndLongitude();
			break;

		case R.id.btCancel:
			((MainScreen) getActivity()).searchViewToggle();
			edSearch.setText("");
			break;
		case R.id.ivSearch:
			((MainScreen) getActivity()).searchViewToggle();
			break;

		default:
			break;
		}

	}

	@Override
	public void afterTextChanged(Editable s)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		int textlength = edSearch.getText().length();
		if(networkTask != null)
			networkTask.cancel(true);

		if (textlength == 0)
		{
			view.findViewById(R.id.llTabLayout).setVisibility(View.VISIBLE);
			if(adapter_type == ADAPTER_TYPE.OFFER)
			{
				OffersListAdapter adapter = new OffersListAdapter(getActivity(), beanListOffers);
				lvOffersList.setAdapter(adapter);
			}
			else
			{
				TicketsListAdapter adapter = new TicketsListAdapter(getActivity(), beanListTickets);
				lvOffersList.setAdapter(adapter);
			}


		}
		else
		{
			view.findViewById(R.id.llTabLayout).setVisibility(View.GONE);
			if (ConstantUtil.isNetworkAvailable(getActivity()))
			{
				networkTask = new NetworkTask(getActivity(), SEARCH_DATA);
				networkTask.setProgressDialog(false);
				networkTask.exposePostExecute(this);
				networkTask.execute("http://162.13.137.28/siteadmin/newapi/searchAll?searchkeyword=" + s + "&user=" + AppPreferences.getInstance(getActivity()).getUserId());
			}
			//			for (int i = 0; i < beanListClubs.getResult().size(); i++)
			//			{
			//				if (textlength <= beanListClubs.getResult().get(i).getName().length())
			//				{
			//					if (edSearch.getText().toString().equalsIgnoreCase((String) beanListClubs.getResult().get(i).getName().subSequence(0, textlength)))
			//					{
			//						listClubInfo.add(beanListClubs.getResult().get(i));
			//					}
			//				}
			//			}
			//			beanSortedClubList.setResult(listClubInfo);
			//			ClubsListAdapter adapter = new ClubsListAdapter(getActivity(), beanSortedClubList, false);

			//	ListView lvClubsList = (ListView) view.findViewById(R.id.details);
			//			lvClubsList.setAdapter(adapter);
			//			ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);
			//			lvAlphabetsList.setVisibility(View.GONE);
		}


	}
	@Override
	public void onResume()
	{
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		if (getActivity() instanceof MainScreen)
		{
			EditText edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			edSearch.addTextChangedListener(this);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText(currentTab);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
			ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivNotification.setVisibility(View.VISIBLE);
			ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
			ivSearch.setVisibility(View.VISIBLE);
			TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
			tvEdit.setText(getString(R.string.strEdit));
			tvEdit.setVisibility(View.GONE);
			if(!edSearch.getText().toString().equals(""))
			{
				getActivity().findViewById(R.id.normal_pan).setVisibility(View.GONE);
				getActivity().findViewById(R.id.search_pan).setVisibility(View.VISIBLE);
			}

		}
		super.onResume();
	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();
		switch (id)
		{
		case OFFER:
			adapter_type = ADAPTER_TYPE.OFFER;
			if (object != null && !object.equals("")) beanListOffers = gson.fromJson(object, BeanListOffers.class);
			if (beanListOffers.getResult().size() != 0)
			{
				OffersListAdapter adapter = new OffersListAdapter(getActivity(), beanListOffers);
				lvOffersList.setAdapter(adapter);
			}
			else Toast.makeText(getActivity(), getResources().getString(R.string.strNoResultFound), Toast.LENGTH_LONG).show();
			break;
		case TICKETS:
			adapter_type = ADAPTER_TYPE.TICKETS;
			if (object != null && !object.equals("")) beanListTickets = gson.fromJson(object, BeanListTickets.class);
			TicketsListAdapter adapter = new TicketsListAdapter(getActivity(), beanListTickets);
			lvOffersList.setAdapter(adapter);
			break;

		case SEARCH_DATA:

			if (object != null && !object.equals(""))
			{
				gsResult = gson.fromJson(object, GSearch.class).getResult();
				if (gsResult != null && gsResult.size()>0 && !object.contains("No Record Found"))
				{
					lvOffersList.setVisibility(View.VISIBLE);
					GSAdapter gsAdapter = new GSAdapter(getActivity(), gsResult, true);
					lvOffersList.setAdapter(gsAdapter);
				}
				else
					lvOffersList.setVisibility(View.GONE);

			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		if(edSearch.getText().toString().equals(""))
		{
			if (adapter_type == ADAPTER_TYPE.OFFER)
			{
				Bundle b = new Bundle();
				b.putString("clubId", beanListOffers.getResult().get(arg2).getId());
				activityCallback.onSendMessage(new ClubDetailFragment(), b, "ClubDetailFragment");
			}
			else if (adapter_type == ADAPTER_TYPE.TICKETS)
			{
				Bundle b = new Bundle();
				b.putString("clubId", beanListTickets.getResults().get(arg2).getId());
				b.putString("eventType", beanListTickets.getResults().get(arg2).getVenue().getType());
				activityCallback.onSendMessage(new TicketDetail(), b, "TicketDetail");
			}
		}
		else
		{
			Bundle b = new Bundle();
			GSResult info = (GSResult) ((TextView) arg1.findViewById(R.id.follow)).getTag();
			if (info.getSearched_type().equals("v"))
			{
				b.putString("clubId", info.getSearched_id());
				activityCallback.onSendMessage(new ClubDetailFragment(), b, "ClubDetailFragment");
			}
			else if (info.getSearched_type().equals("u"))
			{
				b.putString("id", info.getSearched_id());
				activityCallback.onSendMessage(new FriendDetailFragment(), b, "FriendDetailFragment");
			}
			else if(info.getSearched_type().equals("e"))
			{
				b.putString("e_id", info.getSearched_id());
				b.putString("currentBtn", "globalSearch");
				activityCallback.onSendMessage(new EventOverviewFragment(), b, "EventOverviewFragment");
			}
		}

	}

	@Override
	public void onPause() {
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(null);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(null);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener((MainScreen)getActivity());
		getActivity().findViewById(R.id.btCancel).setOnClickListener((MainScreen)getActivity());
		((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).removeTextChangedListener(this);
		getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
		super.onPause();
	}
	
	

}
