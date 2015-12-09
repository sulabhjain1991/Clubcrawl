package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.GSAdapter;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.linchpin.clubcrawl.jobjects.inbox.GSearch;

public class GlobalSearch extends ParentFragment implements OnItemClickListener, TextWatcher, Result

{
	View				view;
	private ListView	lvClubsList;

	private void findAllViews()
	{
		lvClubsList = (ListView) view.findViewById(R.id.details);
		lvClubsList.setOnItemClickListener(this);
	}

	private void setAlphabatList()
	{
		//CustomAdapter adapter = new CustomAdapter(getActivity(), com.linchpin.clubcrawl.helper.Constants.TITLES);
		//	ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);
		//lvAlphabetsList.setAdapter(adapter);
	}

	@Override
	public void onResume()
	{
		((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).addTextChangedListener(this);
		getActivity().findViewById(R.id.normal_pan).setVisibility(View.GONE);
		getActivity().findViewById(R.id.search_pan).setVisibility(View.VISIBLE);

		super.onResume();
	}

	@Override
	public void onPause()
	{
		((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).removeTextChangedListener(this);
		
			
		
		
		getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
		
		super.onPause();
	}

	@Override
	public void onStop()
	{

		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		if (view == null)
		{
			view = inflater.inflate(R.layout.fragment_global_search, container, false);
			BaseClass bc = new BaseClass();
			bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
			findAllViews();
			//setAlphabatList();
			//ListView lvAlphabetsList = (ListView) view.findViewById(R.id.titles);

			//lvAlphabetsList.setOnItemClickListener(new OnItemClickListener()
			//{
			//@Override
			//public void onItemClick(AdapterView arg0, View arg1, int position, long arg3)
			//{

			/*					int selectedPos = 0;
								String selectedChar = com.linchpin.clubcrawl.helper.Constants.TITLES[position];
								for (int i = 0; i < ((GSAdapter) lvClubsList.getAdapter()).getList().size(); i++)
								{
									if (((GSAdapter) lvClubsList.getAdapter()).getList().get(i).getSearched_name().startsWith(selectedChar))
									{
										selectedPos = i;
										break;
									}
								}
								lvClubsList.setSelection(selectedPos);
							}

						});*/

		}
		else ((ViewGroup) view.getParent()).removeAllViews();
		return view;

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		GSResult info = (GSResult) ((TextView) arg1.findViewById(R.id.follow)).getTag();
		Bundle b = new Bundle();

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

	@Override
	public void afterTextChanged(Editable arg0)
	{
		// TODO Auto-generated method stub

	}

	ArrayList<GSResult>	gsResult;
	public boolean		localSearch	= false;

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		if (s.length() <2 )
		{
			NetworkTask networkTask = new NetworkTask(getActivity(), 1000);
			networkTask.setProgressDialog(false);
			networkTask.exposePostExecute(this);
			networkTask.execute("http://162.13.137.28/siteadmin/newapi/searchAll?searchkeyword=" + s + "&user=" + AppPreferences.getInstance(getActivity()).getUserId());

		}
		else
		{
			if(lvClubsList.getAdapter()!=null)
			((GSAdapter) lvClubsList.getAdapter()).filter(s.toString());
		}

	}

	@Override
	public void resultfromNetwork(String result, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();
		if (result != null && !result.equals(""))
		{
			gsResult = gson.fromJson(result, GSearch.class).getResult();
			if (gsResult != null)
			{
				GSAdapter adapter = new GSAdapter(getActivity(), gsResult, true);
				lvClubsList.setAdapter(adapter);
			}
		}

	}
	

}
