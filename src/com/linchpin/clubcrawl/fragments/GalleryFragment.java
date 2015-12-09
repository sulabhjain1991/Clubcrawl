package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;

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
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.GSAdapter;
import com.linchpin.clubcrawl.adapters.GalleryDataAdapter;
import com.linchpin.clubcrawl.beans.BeanListGalleryData;
import com.linchpin.clubcrawl.fragments.NotificationFragment.NotificationTask;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.linchpin.clubcrawl.jobjects.inbox.GSearch;

public class GalleryFragment extends ParentFragment implements TextWatcher, Result, OnItemClickListener,OnClickListener
{

	private View				view;
	private PullAndLoadListView		homescreenList;
	private GalleryDataAdapter	homeAdapter;
	private BeanListGalleryData	listGalleryData;
	private final int GALLERY_DATA=1000;
	private final int SEARCH_DATA = 1001;
	private EditText			edSearch; 
	NetworkTask networkTask;

	private ArrayList<GSResult>	gsResult;
	public boolean		localSearch	= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.home_fragment, container, false);
		BaseClass bc = new BaseClass();
		edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		homescreenList = (PullAndLoadListView) view.findViewById(R.id.homeScreeList);
		homescreenList.setOnItemClickListener(this);

		homescreenList.setOnRefreshListener(new OnRefreshListener()
		{

			@Override
			public void onRefresh()
			{
				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					NetworkTask networkTask = new NetworkTask(getActivity(), GALLERY_DATA);
					networkTask.setProgressDialog(true);
					networkTask.exposePostExecute(GalleryFragment.this);
					String url=ConstantUtil.gallaryUrl + "location=" + AppPreferences.getInstance(getActivity()).getCityId();
					networkTask.execute(ConstantUtil.gallaryUrl + "location=" + AppPreferences.getInstance(getActivity()).getCityId());
					homescreenList.onRefreshComplete();
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();}
				    homescreenList.onRefreshComplete();
			}
		});


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

		return view;
	}

	public void onResume()
	{
		super.onResume();

		getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen) getActivity());
		getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			NetworkTask networkTask = new NetworkTask(getActivity(), GALLERY_DATA);
			networkTask.setProgressDialog(true);
			networkTask.exposePostExecute(GalleryFragment.this);
			String url=ConstantUtil.gallaryUrl + "location=" + AppPreferences.getInstance(getActivity()).getCityId();
			networkTask.execute(ConstantUtil.gallaryUrl + "location=" + AppPreferences.getInstance(getActivity()).getCityId());
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

		if (getActivity() instanceof MainScreen)
		{
			EditText edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			edSearch.setText("");
			edSearch.addTextChangedListener(this);
			TextView tvLocation = (TextView) getActivity().findViewById(R.id.tvLocation);
			tvLocation.setText(AppPreferences.getInstance(getActivity()).getCityName());
			tvLocation.setOnClickListener((MainScreen) getActivity());
			tvLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.drop_btn), null);
			edSearch.setHint("Search a venue or postcode");
			ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivNotification.setVisibility(View.VISIBLE);
			ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
			ivSearch.setVisibility(View.VISIBLE);
			TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
			tvEdit.setText(getString(R.string.strEdit));
			tvEdit.setVisibility(View.GONE);
			((MainScreen) getActivity()).isSearching = false;


		}
		MenuFragment.currentsel = "GalleryFragment";
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
			homeAdapter = new GalleryDataAdapter(getActivity(), listGalleryData);
			homescreenList.setAdapter(homeAdapter);
			if(edSearch.getVisibility() == View.VISIBLE)
			{
				homescreenList.setVisibility(View.GONE );
			}
			else
			{
				homescreenList.setVisibility(View.VISIBLE);
			}

		}
		else
		{
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
	public void resultfromNetwork(String result, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();
		switch (id) {
		case GALLERY_DATA:

			if (result != null && !result.equals("")) listGalleryData = gson.fromJson(result, BeanListGalleryData.class);
			if ((listGalleryData == null)) Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
			else
			{
				homescreenList.setVisibility(View.VISIBLE);
				homeAdapter = new GalleryDataAdapter(getActivity(), listGalleryData);
				homescreenList.setAdapter(homeAdapter);
			}

			break;
		case SEARCH_DATA:

			if (result != null && !result.equals(""))
			{
				gsResult = gson.fromJson(result, GSearch.class).getResult();
				if (gsResult != null && gsResult.size()>0 && !result.contains("No Record Found"))
				{
					homescreenList.setVisibility(View.VISIBLE);
					GSAdapter adapter = new GSAdapter(getActivity(), gsResult, true);
					homescreenList.setAdapter(adapter);
				}
				else
					homescreenList.setVisibility(View.GONE);

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
			Bundle b = new Bundle();
			b.putString("clubId", listGalleryData.getGallery().get(arg2).getClub_url());
			activityCallback.onSendMessage(new ClubDetailFragment(), b, "ClubDetailFragment");
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btCancel:
			((MainScreen) getActivity()).searchViewToggle();
			edSearch.setText("");
			homescreenList.setVisibility(View.VISIBLE);
			break;
		case R.id.ivSearch:
			((MainScreen) getActivity()).searchViewToggle();
			homescreenList.setVisibility(View.GONE);
			break;
		default:
			break;
		}


	};

	@Override
	public void onPause() 
	{
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(null);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(null);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener((MainScreen)getActivity());
		getActivity().findViewById(R.id.btCancel).setOnClickListener((MainScreen)getActivity());
		((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).removeTextChangedListener(this);
		getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
		// TODO Auto-generated method stub
		super.onPause();
	}


}
