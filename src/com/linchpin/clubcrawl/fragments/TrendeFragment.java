package com.linchpin.clubcrawl.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.TrendsListAdapter;
import com.linchpin.clubcrawl.beans.BeanListTrends;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class TrendeFragment extends ParentFragment {


	View			view;
	BeanListTrends	beanListTrends;
	PullAndLoadListView lvFeedList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{


		MenuFragment.currentsel = "TrendeFragment";
		view = inflater.inflate(R.layout.fragment_trending, container, false);

//		LinearLayout llSwitchFragmentButtons = (LinearLayout) view.findViewById(R.id.llSwitchFragmentButtons);
//		llSwitchFragmentButtons.setVisibility(View.GONE);
//		RelativeLayout rlSearchBox = (RelativeLayout) view.findViewById(R.id.search_box);
//		rlSearchBox.setVisibility(View.GONE);

		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		setTitleBar();

		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.getTrendeList + "location=" + "1";
			new TrendeListTask(getActivity(), url).execute();
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

		 lvFeedList = (PullAndLoadListView) view.findViewById(R.id.lvFriendsList);
		lvFeedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				Bundle b = new Bundle();
				b.putString("clubId", beanListTrends.getResult().get(position-1).getVenue_num());
				activityCallback.onSendMessage(new ClubDetailFragment(), b, "ClubDetailFragment");

			}
		});
		lvFeedList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					AppPreferences appPref = new AppPreferences(getActivity());
					String url = ConstantUtil.getTrendeList + "location=" + "1";
					new TrendeListTask(getActivity(), url).execute();
				}
				else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

				
				
				
			}
		});
		
		return view;
	}

	private void setTitleBar() {
		// TODO Auto-generated method stub


		TextView tvTitle = (TextView) getActivity().findViewById(
				R.id.tvLocation);
		AppPreferences appPref = AppPreferences.getInstance(getActivity());
		tvTitle.setText(getString(R.string.strTrend)+" "+appPref.getCityName());
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, // left
				0, // top
				0, // right
				0// bottom
				);
		ImageView ivComposePost=(ImageView) getActivity().findViewById(
				R.id.ivComposePost);
		ivComposePost.setVisibility(View.GONE);
		ImageView ivSearch=(ImageView) getActivity().findViewById(
				R.id.ivSearch);
		ivSearch.setVisibility(View.GONE);
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setText(getString(R.string.strEdit));
		tvEdit.setVisibility(View.GONE);
		ImageView ivMenu=(ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setBackgroundResource(R.drawable.menuicon);


	}

	// Task for get category data
	public class TrendeListTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;
		String			url;

		public TrendeListTask(Context con, String url)
		{
			this.con = con;
			this.url = url;
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

			Gson gson = new Gson();

			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				beanListTrends = gson.fromJson(responseString, BeanListTrends.class);
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
			PullAndLoadListView lvTrends=(PullAndLoadListView) view.findViewById(R.id.lvFriendsList);
			lvTrends.onRefreshComplete();
			Message myMessage = new Message();
			myMessage.obj = "TrendeTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	private Handler	myHandler	= new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (beanListTrends == null)
			{
				Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
			}
			else
			{
				TrendsListAdapter adapter = new TrendsListAdapter(getActivity(), beanListTrends);
				ListView lvFeedList = (ListView) view.findViewById(R.id.lvFriendsList);
				lvFeedList.setAdapter(adapter);
			}

		}
	};

	public void onResume() 
	{
		//setTitleBar();
		//getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen) getActivity());
		super.onResume();

	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		//ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		//ivMenu.setBackgroundResource(R.drawable.menuicon);
		//ivMenu.setOnClickListener((MainScreen) getActivity());

		super.onPause();
	}
}
