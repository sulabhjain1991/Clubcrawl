package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.InvitesListAdapter;
import com.linchpin.clubcrawl.beans.BeanInviteInfo;
import com.linchpin.clubcrawl.beans.BeanListInvites;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class NightsOutFragment extends ParentFragment {

	private View view;
	private String currentBtn = "UpcomingEvents";
	private BeanListInvites beanListInvites,beanListInvitesSorted;
	boolean						isSearching			= false;
	
	PullAndLoadListView lvEventCommentsList,lvInvitesList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MenuFragment.currentsel = "NightsOutFragment";
		view = inflater.inflate(R.layout.fragment_friend_profile, container,
				false);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		beanListInvitesSorted = new BeanListInvites();
		setTitleBar();
		performSearch();

		final TextView btnUpcomingEvents = (TextView) view
				.findViewById(R.id.btnFriends);
		btnUpcomingEvents.setText(getResources().getString(
				R.string.btnUpcomingEvents));

		final TextView btnInvites = (TextView) view.findViewById(R.id.btnRequests);
		btnInvites.setText(getResources().getString(R.string.btnInvites));

		// Set currentBtn Invites
		btnInvites.setBackgroundResource(R.color.button_background);
//		btnUpcomingEvents.setBackgroundResource(R.drawable.solid_pink_rectangle);
		btnUpcomingEvents.setTextColor(getResources().getColor(R.color.background_color));
		btnInvites.setTextColor(getResources().getColor(R.color.black));
	//	btnUpcomingEvents.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout rlSearchBox = (RelativeLayout) view
				.findViewById(R.id.search_box);
		rlSearchBox.setVisibility(View.GONE);

		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.nightsOutEventsList + "1&perpage=10&userid=" + appPref.getUserId();
			new NightsOutEventListTask(getActivity(), url).execute();
		} else
			Toast.makeText(getActivity(),
					getString(R.string.internetFailure),
					Toast.LENGTH_LONG).show();

		btnInvites.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//btnInvites.setBackgroundResource(R.drawable.solid_pink_rectangle);
				//btnUpcomingEvents.setBackgroundResource(R.color.button_background);
				btnInvites.setTextColor(getResources().getColor(R.color.background_color));
				btnUpcomingEvents.setTextColor(getResources().getColor(R.color.black));

				if (!currentBtn.equals("Invites")) {
					final EditText etSearch = (EditText) view.findViewById(R.id.etSearch);
					etSearch.setText("");
					if (ConstantUtil.isNetworkAvailable(getActivity())) {
						AppPreferences appPref = new AppPreferences(getActivity());
						String url = ConstantUtil.nightsOutInvites + appPref.getUserId();
						new NightsOutInvitesTask(getActivity(), url).execute();
					} else
						Toast.makeText(getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
				}
				currentBtn = "Invites";
			}
		});
		
		btnUpcomingEvents.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//btnUpcomingEvents.setBackgroundResource(R.drawable.solid_pink_rectangle);
				btnInvites.setBackgroundResource(R.color.button_background);
				btnUpcomingEvents.setTextColor(getResources().getColor(R.color.background_color));
				btnInvites.setTextColor(getResources().getColor(R.color.black));

				if (!currentBtn.equals("UpcomingEvents")) {
					final EditText etSearch = (EditText) view.findViewById(R.id.etSearch);
					etSearch.setText("");
					if (ConstantUtil.isNetworkAvailable(getActivity())) {
						AppPreferences appPref = new AppPreferences(getActivity());
						String url = ConstantUtil.nightsOutEventsList + "1&perpage=10&userid=" + appPref.getUserId();
						new NightsOutEventListTask(getActivity(), url).execute();
					} else
						Toast.makeText(getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
				}
				currentBtn = "UpcomingEvents";
			}
		});
		
		 lvEventCommentsList = (PullAndLoadListView) view.findViewById(R.id.lvFriendsList);
		lvEventCommentsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putString("e_id", beanListInvitesSorted.getEvents().get(position-1).getId());
				b.putString("currentBtn", currentBtn);
				activityCallback.onSendMessage(new EventOverviewFragment(),b, "EventOverviewFragment");
				currentBtn = "UpcomingEvents";
				
			}
		});

		lvEventCommentsList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if(currentBtn.equalsIgnoreCase("invites"))
				{
					if (ConstantUtil.isNetworkAvailable(getActivity()))
					{
						AppPreferences appPref = new AppPreferences(getActivity());
						String url = ConstantUtil.nightsOutInvites + appPref.getUserId();
						new NightsOutInvitesTask(getActivity(), url).execute();
						lvEventCommentsList.onRefreshComplete();
					
					}
					else
					{
						lvEventCommentsList.onRefreshComplete();
						Toast.makeText(getActivity(), getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();
					}
					
					
				}
				else
				{
					if (ConstantUtil.isNetworkAvailable(getActivity()))
					{
						AppPreferences appPref = new AppPreferences(getActivity());
						String url = ConstantUtil.nightsOutEventsList + "1&perpage=10&userid=" + appPref.getUserId();
						new NightsOutEventListTask(getActivity(), url).execute();
						lvEventCommentsList.onRefreshComplete();
					}
					else
					{
						lvEventCommentsList.onRefreshComplete();
						Toast.makeText(getActivity(), getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();
					}
				}
				
			}
		});
		return view;
	}
	
	
	
	// Task for getting Invites data
		public class NightsOutEventListTask extends AsyncTask<Void, Void, Void> {

			ProgressDialog pd;
			Context con;
			String url;

			public NightsOutEventListTask(Context con, String url) {
				this.con = con;
				this.url = url;
			}

			@Override
			protected void onPreExecute() {
				pd = ProgressDialog.show(getActivity(), null, "Loading...");
				pd.setContentView(R.layout.progress_layout);
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {

				Gson gson = new Gson();
				AppPreferences appPref = new AppPreferences(getActivity());

				String responseString = ConstantUtil.http_connection(url);

				if (responseString != null && !responseString.equals("")) {
					beanListInvites = gson.fromJson(responseString,
							BeanListInvites.class);
					beanListInvitesSorted.setEvents(beanListInvites.getEvents());
					beanListInvitesSorted.setStatus(beanListInvites.getStatus());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
				Message myMessage = new Message();
				myMessage.obj = "EventsTask";
				myHandlerForEvent.sendMessage(myMessage);
				super.onPostExecute(result);

			}

		}

		private Handler myHandlerForEvent = new Handler() {

			public void handleMessage(Message msg) {

				if (msg.obj.toString().equalsIgnoreCase("EventsTask")) {

					if ((beanListInvitesSorted == null)) {
						Toast.makeText(getActivity(),
								getString(R.string.connectionerror),
								Toast.LENGTH_LONG).show();
					}

					else {
						InvitesListAdapter adapter = new InvitesListAdapter(
								getActivity(), beanListInvitesSorted);
						 lvInvitesList = (PullAndLoadListView) view
								.findViewById(R.id.lvFriendsList);
						lvInvitesList.setAdapter(adapter);
					}
				}

			}
		};

	// Task for getting Invites data
	public class NightsOutInvitesTask extends AsyncTask<Void, Void, Void> {

		ProgressDialog pd;
		Context con;
		String url;

		public NightsOutInvitesTask(Context con, String url) {
			this.con = con;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());

			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				beanListInvites = gson.fromJson(responseString,
						BeanListInvites.class);
				beanListInvitesSorted.setEvents(beanListInvites.getEvents());
				beanListInvitesSorted.setStatus(beanListInvites.getStatus());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "InvitesTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	private Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("InvitesTask")) {

				if ((beanListInvites == null)) {
					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}

				else {
					InvitesListAdapter adapter = new InvitesListAdapter(
							getActivity(), beanListInvitesSorted);
					/*ListView lvInvitesList = (ListView) view
							.findViewById(R.id.lvFriendsList);*/
					lvInvitesList.setAdapter(adapter);
				}
			}

		}
	};

	//set the title bar by changing notification image with Edit text
		private void setTitleBar()
		{

			TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
			tvTitle.setText(getString(R.string.strMyProfile));
			tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
					0, //top
					0, //right
					0//bottom
					);

					ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
					ivNotification.setVisibility(View.INVISIBLE);
					ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
					ivSearch.setVisibility(View.VISIBLE);
					TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
					tvEdit.setText(getString(R.string.strEdit));
					tvEdit.setVisibility(View.GONE);

		}
		
		
		
		
	
	private void performSearch()
	{
		final EditText etSearch = (EditText) getActivity().findViewById(R.id.edSearch);
		etSearch.addTextChangedListener(new TextWatcher()
	        {
	            public void afterTextChanged(Editable s)
	            {
	                // Abstract Method of TextWatcher Interface.
	            }
	            public void beforeTextChanged(CharSequence s,
	            int start, int count, int after)
	            {
	                // Abstract Method of TextWatcher Interface.
	            }
	            public void onTextChanged(CharSequence s,
	            int start, int before, int count)
	            {
	            	List<BeanInviteInfo> listInviteInfo = new ArrayList<BeanInviteInfo>();
	                int textlength = etSearch.getText().length();
	                listInviteInfo.clear();
	                
	                
	                for (int i = 0; i < beanListInvites.getEvents().size(); i++)
	                {
	                    if (textlength <= beanListInvites.getEvents().get(i).getEvent_name().length())
	                    {
	                        if (
	                        
	                        beanListInvites.getEvents().get(i).getEvent_name().contains(etSearch.getText().toString()))
	                        {
	                        	BeanInviteInfo beanInviteInfo = new BeanInviteInfo();
	                        	beanInviteInfo.setEvent_name(beanListInvites.getEvents().get(i).getEvent_name());
	                        	beanInviteInfo.setId(beanListInvites.getEvents().get(i).getId());
	                        	beanInviteInfo.setLogo(beanListInvites.getEvents().get(i).getLogo());
	                        	listInviteInfo.add(beanInviteInfo);
	                        }
	                    }
	                }
	                beanListInvitesSorted.setEvents(listInviteInfo);
	                InvitesListAdapter adapter = new InvitesListAdapter(
							getActivity(), beanListInvitesSorted);
				/*	ListView lvInvitesList = (ListView) view
							.findViewById(R.id.lvFriendsList);*/
					lvInvitesList.setAdapter(adapter);
	               
	            }
	        });
		
	}
	
	public void refreshInvitesList(){
		if(currentBtn.equals("Invites")){
			if (ConstantUtil.isNetworkAvailable(getActivity())) {
				AppPreferences appPref = new AppPreferences(getActivity());
				String url = ConstantUtil.nightsOutInvites + appPref.getUserId();
				new NightsOutInvitesTask(getActivity(), url).execute();
			} else
				Toast.makeText(getActivity(), getString(R.string.internetFailure),
						Toast.LENGTH_LONG).show();
		}
		
	}
}
