package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.ClubsListAdapter;
import com.linchpin.clubcrawl.adapters.CustomAdapter;
import com.linchpin.clubcrawl.adapters.EventsAdapter;
import com.linchpin.clubcrawl.beans.BeanCategoryInfo;
import com.linchpin.clubcrawl.beans.BeanClubsInfo;
import com.linchpin.clubcrawl.beans.BeanListClubs;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.events.Events;

public class SearchVenueFragment extends ParentFragment implements OnItemClickListener, OnClickListener, TextWatcher, Result
{

	View				view, footerView;
	TextView			tvBars, tvEvents, tvNightClubs, tvPubs;

	View				vBars, vEvents, vNightClubs, vPubs;
	ArrayList<String>	categoryNameList, categoryIdList;
	BeanListClubs		beanListClubs, beanSortedClubList;
	Events				events;
	private String		clubId;
	private String		currentBtn		= "Bars";
	private EditText			edSearch;
	private TextView			tvLocation;
	protected final int	SEARCH_CATEGORY	= 0x00001;
	protected final int	CLUB_SERCH_LIST	= 0x00002;
	protected final int	LOCATION		= 0x00003;
	protected final int	EVENTS_LIST		= 0x00004;
	protected final int		SEARCH_LIST
		= 0x00005;
	ListView			lvClubsList;

	private void resetSubBar()
	{
		tvBars.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvNightClubs.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvEvents.setTextColor(getResources().getColor(R.color.list_fg_nor));
		tvPubs.setTextColor(getResources().getColor(R.color.list_fg_nor));
		vBars.setBackgroundColor(getResources().getColor(R.color.transparent));
		vNightClubs.setBackgroundColor(getResources().getColor(R.color.transparent));
		vEvents.setBackgroundColor(getResources().getColor(R.color.transparent));
		vPubs.setBackgroundColor(getResources().getColor(R.color.transparent));

	}

	private void selectSubBarItem(int id)
	{
		if (getActivity() instanceof MainScreen)
		{
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText(((TextView) view.findViewById(id)).getText().toString());
			((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
		}
		switch (id)
		{
			case R.id.tvBars:
				tvBars.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vBars.setBackgroundColor(getResources().getColor(R.color.background_color));
				getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
				getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
				getActivity().findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);

				break;
			case R.id.tvEvents:
				tvEvents.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vEvents.setBackgroundColor(getResources().getColor(R.color.background_color));
				getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
				getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
				getActivity().findViewById(R.id.ivSearch).setVisibility(View.GONE);
				break;
			case R.id.tvNightClubs:
				tvNightClubs.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vNightClubs.setBackgroundColor(getResources().getColor(R.color.background_color));
				getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
				getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
				getActivity().findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
				break;
			case R.id.tvPubs:
				tvPubs.setTextColor(getResources().getColor(R.color.list_fg_selected));
				vPubs.setBackgroundColor(getResources().getColor(R.color.background_color));
				getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
				getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
				getActivity().findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}

	private void findAllViews()
	{
		tvBars = (TextView) view.findViewById(R.id.tvBars);
		tvEvents = (TextView) view.findViewById(R.id.tvEvents);
		tvNightClubs = (TextView) view.findViewById(R.id.tvNightClubs);
		tvPubs = (TextView) view.findViewById(R.id.tvPubs);

		vBars = (View) view.findViewById(R.id.vBars);
		vNightClubs = (View) view.findViewById(R.id.vNightClubs);
		vEvents = (View) view.findViewById(R.id.vEvents);
		vPubs = (View) view.findViewById(R.id.vPubs);

		lvClubsList = (ListView) view.findViewById(R.id.details);
		lvClubsList.setOnItemClickListener(this);
		tvBars.setOnClickListener(this);
		tvEvents.setOnClickListener(this);
		tvNightClubs.setOnClickListener(this);
		tvPubs.setOnClickListener(this);

	}

	@Override
	public void onAttach(Activity activity)
	{
		MenuFragment.currentsel = "AllOfferFragment";
		categoryNameList = new ArrayList<String>();
		categoryIdList = new ArrayList<String>();
		beanSortedClubList = new BeanListClubs();
		super.onAttach(activity);
	}

	@Override
	public void onResume()
	{
		if (getActivity() instanceof MainScreen)
		{
			edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			tvLocation = (TextView) getActivity().findViewById(R.id.tvLocation);
			getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
			getActivity().findViewById(R.id.ivComposePost).setVisibility(View.INVISIBLE);
			getActivity().findViewById(R.id.btCancel).setOnClickListener(this);

			tvLocation.setOnClickListener(null);
			edSearch.setOnClickListener(null);
			//	edSearch.setOnClickListener(this);
			tvLocation.setText(currentBtn);
			edSearch.setHint("Search a venue or postcode");
			//edSearch.removeTextChangedListener();
			edSearch.addTextChangedListener(this);
		
			ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
			ivSearch.setVisibility(View.VISIBLE);
			TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
			tvEdit.setText(getString(R.string.strEdit));
			tvEdit.setVisibility(View.GONE);

		}
		super.onResume();
	}

	@Override
	public void onPause()
	{
		getActivity().findViewById(R.id.ivComposePost).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(null);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(null);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener((MainScreen)getActivity());
		getActivity().findViewById(R.id.btCancel).setOnClickListener((MainScreen)getActivity());
		((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).removeTextChangedListener(this);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		if (view == null)
		{
			view = inflater.inflate(R.layout.fragment_search_venue, container, false);
			BaseClass bc = new BaseClass();
			bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
			findAllViews();
			resetSubBar();
			footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_footer_view, null, false);

			if (ConstantUtil.isNetworkAvailable(getActivity()))
			{
				NetworkTask networkTask = new NetworkTask(getActivity(), SEARCH_CATEGORY);
				networkTask.exposePostExecute(SearchVenueFragment.this);
				networkTask.setProgressDialog(true);
				networkTask.execute(ConstantUtil.getCategoryInSearchVenue);
			}
			else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
			setAlphabatList();
			ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);
			lvAlphabetsList.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView arg0, View arg1, int position, long arg3)
				{

					int selectedPos = 0;
					String selectedChar = com.linchpin.clubcrawl.helper.Constants.TITLES[position];
					for (int i = 0; i < beanSortedClubList.getResult().size(); i++)
					{
						if (beanSortedClubList.getResult().get(i).getName().startsWith(selectedChar))
						{
							selectedPos = i;
							break;
						}
					}
					//ListView lvClubsList = (ListView) SearchVenueFragment.this.view.findViewById(R.id.details);
					lvClubsList.setSelection(selectedPos);
				}

			});
			selectSubBarItem(R.id.tvBars);
			if (getActivity() instanceof MainScreen)
			{
				((TextView) getActivity().findViewById(R.id.tvLocation)).setText("Bars");
				((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
			}
			ImageView ivComposePost = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivComposePost.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					AppPreferences appPref = new AppPreferences(getActivity());
					if (!appPref.getSneakStatus())
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

	private void setAlphabatList()
	{
		CustomAdapter adapter = new CustomAdapter(getActivity(), com.linchpin.clubcrawl.helper.Constants.TITLES);
		ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);
		lvAlphabetsList.setAdapter(adapter);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
			case R.id.tvBars:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("Bars"))
				{
					currentBtn = "Bars";
					clubId = categoryIdList.get(0);
					if (ConstantUtil.isNetworkAvailable(getActivity())) gerClubSearchList();
					else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.tvEvents:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("Events"))
				{
					currentBtn = "Events";
					clubId = categoryIdList.get(1);
					if (ConstantUtil.isNetworkAvailable(getActivity()))
					{
						NetworkTask networkTask = new NetworkTask(getActivity(), LOCATION);
						networkTask.exposePostExecute(SearchVenueFragment.this);
						networkTask.setProgressDialog(true);
						networkTask.execute("http://maps.google.com/maps/api/geocode/json?address=" + AppPreferences.getInstance(getActivity()).getCityName() + "&sensor=false");
					}
					else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.tvNightClubs:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("NightClub"))
				{
					currentBtn = "NightClub";
					clubId = categoryIdList.get(2);
					if (ConstantUtil.isNetworkAvailable(getActivity())) gerClubSearchList();
					else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}

				break;
			case R.id.tvPubs:
				resetSubBar();
				selectSubBarItem(id);
				if (!currentBtn.equals("Pubs"))
				{
					currentBtn = "Pubs";
					clubId = categoryIdList.get(3);
					if (ConstantUtil.isNetworkAvailable(getActivity())) gerClubSearchList();
					else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
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
	public void afterTextChanged(Editable arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
	{

		if (!currentBtn.equals("Events"))
		{
			int textlength = edSearch.getText().length();
			List<BeanClubsInfo> listClubInfo = new ArrayList<BeanClubsInfo>();
			
			listClubInfo.clear();
			if (textlength == 0)
			{
				beanSortedClubList.setResult(beanListClubs.getResult());
				view.findViewById(R.id.frame).setVisibility(View.VISIBLE);
				ClubsListAdapter adapter = new ClubsListAdapter(getActivity(), beanSortedClubList, true);
				//ListView lvClubsList = (ListView) view.findViewById(R.id.details);
				lvClubsList.setAdapter(adapter);
				ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);
				lvAlphabetsList.setVisibility(View.VISIBLE);
			}
			else
			{
				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					view.findViewById(R.id.frame).setVisibility(View.GONE);
					NetworkTask networkTask = new NetworkTask(getActivity(), SEARCH_LIST);
					networkTask.exposePostExecute(SearchVenueFragment.this);
					
					networkTask.execute(ConstantUtil.getClubSearchList + "st="+edSearch.getText().toString()+"&catid=" + clubId + "&location=" + "1&user_id=" + AppPreferences.getInstance(getActivity()).getUserId());
				}
//				for (int i = 0; i < beanListClubs.getResult().size(); i++)
//				{
//					if (textlength <= beanListClubs.getResult().get(i).getName().length())
//					{
//						if (edSearch.getText().toString().equalsIgnoreCase((String) beanListClubs.getResult().get(i).getName().subSequence(0, textlength)))
//						{
//							listClubInfo.add(beanListClubs.getResult().get(i));
//						}
//					}
//				}
//				beanSortedClubList.setResult(listClubInfo);
//				ClubsListAdapter adapter = new ClubsListAdapter(getActivity(), beanSortedClubList, false);

				//	ListView lvClubsList = (ListView) view.findViewById(R.id.details);
//				lvClubsList.setAdapter(adapter);
//				ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);
//				lvAlphabetsList.setVisibility(View.GONE);
			}
		}
		else
		{
			((EventsAdapter) lvClubsList.getAdapter()).filter(arg0.toString());
		}
	}

	private void gerClubSearchList()
	{
		NetworkTask networkTask = new NetworkTask(getActivity(), CLUB_SERCH_LIST);
		networkTask.exposePostExecute(SearchVenueFragment.this);
		networkTask.setProgressDialog(true);
		networkTask.execute(ConstantUtil.getClubSearchList + "st=&catid=" + clubId + "&location=" + "1&user_id=" + AppPreferences.getInstance(getActivity()).getUserId());
	}

	@Override
	public void resultfromNetwork(String result, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();

		switch (id)
		{
			case SEARCH_CATEGORY:
				if (result != null && !result.equals(""))
				{
					try
					{
						JSONArray jsonArray = new JSONArray(result);
						for (int i = 0; i < jsonArray.length(); i++)
						{
							String catName = jsonArray.getString(i);
							BeanCategoryInfo beanCategoryInfo = gson.fromJson(catName, BeanCategoryInfo.class);
							categoryNameList.add(beanCategoryInfo.getName());
							categoryIdList.add(beanCategoryInfo.getId());
							if (i == 0)
							{
								clubId = categoryIdList.get(0);
							}
						}

						if (categoryNameList.size() == 0)
						{
							Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();

						}
						else
						{
							tvBars.setText(categoryNameList.get(0));
							tvNightClubs.setText(categoryNameList.get(2));
							tvEvents.setText(categoryNameList.get(1));

							tvPubs.setText(categoryNameList.get(3));

							if (ConstantUtil.isNetworkAvailable(getActivity()))
							{
								gerClubSearchList();
							}
							else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

						}

					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
				break;
			case CLUB_SERCH_LIST:
				if (result != null && !result.equals(""))
				{
					beanListClubs = gson.fromJson(result, BeanListClubs.class);
					beanSortedClubList.setResult(beanListClubs.getResult());
				}

				if ((beanListClubs == null)) Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				else
				{
					ClubsListAdapter adapter = new ClubsListAdapter(getActivity(), beanSortedClubList, true);
					lvClubsList.setAdapter(adapter);
				}
				break;
			case SEARCH_LIST:
				if (result != null && !result.equals(""))
				{
					beanSortedClubList = gson.fromJson(result, BeanListClubs.class);
				}

				if ((beanSortedClubList == null)) Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				else
				{
					ClubsListAdapter adapter = new ClubsListAdapter(getActivity(), beanSortedClubList, true);
					lvClubsList.setAdapter(adapter);
				}
				break;
			case LOCATION:
				try
				{
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.get("status").equals("OK"))
					{
						jsonObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
						NetworkTask networkTask = new NetworkTask(getActivity(), EVENTS_LIST);
						networkTask.exposePostExecute(SearchVenueFragment.this);
						networkTask.setProgressDialog(true);
						networkTask.execute(ConstantUtil.baseUrl + "api/skiddleapi?lattitude=" + jsonObject.getString("lat") + "&longitude=" + jsonObject.getString("lng"));

					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				break;
			case EVENTS_LIST:
				if (result != null && !result.equals(""))
				{
					events = gson.fromJson(result, Events.class);
					if (!events.getTotalcount().equals("0"))
					{
						EventsAdapter adapter = new EventsAdapter(getActivity(), events.getResults(), true);
						lvClubsList.setAdapter(adapter);
					}
					else Toast.makeText(getActivity(), "No event", Toast.LENGTH_LONG).show();

				}

			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		if (!currentBtn.equals("Events"))
		{
			Bundle b = new Bundle();
			b.putString("clubId", beanSortedClubList.getResult().get(arg2).getId());
			activityCallback.onSendMessage(new ClubDetailFragment(), b, "ClubDetailFragment");
		}
		else
		{

			Bundle b = new Bundle();
			b.putString("clubId", events.getResults().get(arg2).getId());
			b.putString("eventType", events.getResults().get(arg2).getVenue().getType());
			activityCallback.onSendMessage(new TicketDetail(), b, "TicketDetail");
		}

	}
}
