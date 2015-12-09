package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.GSAdapter;
import com.linchpin.clubcrawl.adapters.NotificationsListAdapter;
import com.linchpin.clubcrawl.adapters.WhosVenueListAdapter;
import com.linchpin.clubcrawl.beans.BeanListWhosOutVenue;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.linchpin.clubcrawl.jobjects.inbox.GSearch;

public class WhosOutVenuesFragment extends ParentFragment implements TextWatcher,OnClickListener,Result {

	private View view;
	private BeanListWhosOutVenue beanListWhosVenue;
	private JSONObject jsonObject;
	private final int 			SEARCH_DATA = 1001;
	private EditText			edSearch; 
	NetworkTask					 networkTask;

	private ArrayList<GSResult>	gsResult;
	public boolean		localSearch	= false;
	
	
	private NotificationsListAdapter adapter;
	private View footerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MenuFragment.currentsel = "WhosOutVenuesFragment";
		view = inflater.inflate(R.layout.fragment_whos_out, container,
				false);
		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_footer_view, null, false);
		LinearLayout llSwithFragment = (LinearLayout) view.findViewById(R.id.llSwitchFragmentButtons);
		llSwithFragment.setVisibility(View.GONE);

		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		
		final TextView btnFeeds = (TextView) view.findViewById(R.id.btnFriends);
		btnFeeds.setText(getResources().getString(R.string.strFeeds));

		final TextView btnNotifications = (TextView) view
				.findViewById(R.id.btnRequests);
		btnNotifications.setText(getResources().getString(
				R.string.notifications));

		RelativeLayout rlSearchBox = (RelativeLayout) view
				.findViewById(R.id.search_box);
		rlSearchBox.setVisibility(View.GONE);

		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.userWhosoutVenueList + "location="+appPref.getCityId()+"&curpage="+"1"+"&perpage="+"10";
			
			new WhosoutListTask(getActivity(), url).execute();
		} else
			Toast.makeText(getActivity(), getString(R.string.internetFailure),
					Toast.LENGTH_LONG).show();
		
		ImageView ivComposePost = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		ivComposePost.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), ComposePostScreen.class);
				startActivity(i);

			}
		});

//		btnFeeds.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				btnFeeds.setBackgroundResource(R.drawable.solid_pink_rectangle);
//				btnNotifications
//						.setBackgroundResource(R.color.button_background);
//				btnFeeds.setTextColor(getResources().getColor(R.color.white));
//				btnNotifications.setTextColor(getResources().getColor(
//						R.color.black));
//				offsetNotification = 0;
//				if (!currentBtn.equals("Feeds")) {
//
//					ListView lvFriendsList = (ListView) view
//							.findViewById(R.id.lvFriendsList);
//					lvFriendsList.setDividerHeight(1);
//					if (lvFriendsList.getFooterViewsCount() > 0) {
//						lvFriendsList.removeFooterView(footerView);
//					}
//
//					if (ConstantUtil.isNetworkAvailable(getActivity())) {
//						AppPreferences appPref = new AppPreferences(
//								getActivity());
//						String url = ConstantUtil.userFeedsList
//								+ appPref.getUserId() + "&feed_start=0";
//						String urlTotalNotification = ConstantUtil.totalNotification
//								+ appPref.getUserId();
//						new FeedsTask(getActivity(), url, urlTotalNotification)
//								.execute();
//					} else
//						Toast.makeText(getActivity(),
//								getString(R.string.internetFailure),
//								Toast.LENGTH_LONG).show();
//				}
//				currentBtn = "Feeds";
//			}
//		});

//		btnNotifications.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				btnNotifications
//						.setBackgroundResource(R.drawable.solid_pink_rectangle);
//				btnFeeds.setBackgroundResource(R.color.button_background);
//				btnNotifications.setTextColor(getResources().getColor(
//						R.color.white));
//				btnFeeds.setTextColor(getResources().getColor(R.color.black));
//
//				if (!currentBtn.equals("Notifications")) {
//					ListView lvFriendsList = (ListView) view
//							.findViewById(R.id.lvFriendsList);
//					if (lvFriendsList.getFooterViewsCount() == 0) {
//						lvFriendsList.addFooterView(footerView);
//						TextView btnLoadMore = (TextView) footerView
//								.findViewById(R.id.btnLoadMore);
//						btnLoadMore.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								if (ConstantUtil
//										.isNetworkAvailable(getActivity())) {
//									offsetNotification = offsetNotification + 10;
//									AppPreferences appPref = new AppPreferences(
//											getActivity());
//									String url = ConstantUtil.notificationList
//											+ appPref.getUserId() + "&offset="
//											+ offsetNotification;
//									new NotificationTask(getActivity(), url)
//											.execute();
//								} else
//									Toast.makeText(
//											getActivity(),
//											getString(R.string.internetFailure),
//											Toast.LENGTH_LONG).show();
//
//							}
//						});
//					}
//					if (ConstantUtil.isNetworkAvailable(getActivity())) {
//						AppPreferences appPref = new AppPreferences(
//								getActivity());
//						String url = ConstantUtil.notificationList
//								+ appPref.getUserId() + "&offset=0";
//						new NotificationTask(getActivity(), url).execute();
//					} else
//						Toast.makeText(getActivity(),
//								getString(R.string.internetFailure),
//								Toast.LENGTH_LONG).show();
//				}
//				currentBtn = "Notifications";
//			}
//		});

		ListView lvFriendsList = (ListView) view
				.findViewById(R.id.lvFriendsList);
//
		lvFriendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
//				 TODO Auto-generated method stub
				if(edSearch.getText().toString().equals(""))
				{
				Bundle b = new Bundle();
				b.putString("venueId", beanListWhosVenue.getVenueList().get(arg2).getId());
				b.putString("header_img", beanListWhosVenue.getVenueList().get(arg2).getHeader_img());
				activityCallback.onSendMessage(new EventListByVenueFragment(), b,
						"EventListByVenueFragment");
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
		});
			
		// lvFriendsList.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// if (firstVisibleItem == 0 && visibleItemCount == 0 && totalItemCount
		// == 0 && currentBtn.equals("Feeds"))
		// {
		// return;
		// }
		// if (firstVisibleItem + visibleItemCount == totalItemCount) {
		// if (tvLoadMore.getVisibility() == View.GONE) {
		// tvLoadMore.setVisibility(View.VISIBLE);
		// }
		// } else {
		// if (tvLoadMore.getVisibility() == View.VISIBLE) {
		// tvLoadMore.setVisibility(View.GONE);
		// }
		// }
		//
		//
		// }
		// });

		return view;
	}





	// Task for get FeedListInfo data
	public class WhosoutListTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String url;
		//String urlTotalNotification;

		public WhosoutListTask(Context con, String url) {
			this.con = con;
			this.url = url;
			//this.urlTotalNotification = urlTotalNotification;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			Gson gson = new Gson();

			String responseString = ConstantUtil.http_connection(url);
			

			if (responseString != null && !responseString.equals("")) {
				beanListWhosVenue= (BeanListWhosOutVenue)gson.fromJson(responseString,
						BeanListWhosOutVenue.class);
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "WhosOutVenueTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	private Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("WhosOutVenueTask")) {

				if ((beanListWhosVenue == null))

				{

					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}
				else if ((beanListWhosVenue.getVenueList() == null))

				{

					Toast.makeText(getActivity(),
							"No venue found",
							Toast.LENGTH_LONG).show();
				}
				else {
					
					WhosVenueListAdapter adapter = new WhosVenueListAdapter(
							getActivity(), beanListWhosVenue);
					ListView lvVenueList = (ListView) view
							.findViewById(R.id.lvFriendsList);
					lvVenueList.setAdapter(adapter);

				}

			}

		}
	};

	
	
	public void onResume()
	{
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		if (getActivity() instanceof MainScreen)
		{
			 edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			edSearch.setText("");
			edSearch.addTextChangedListener(this);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText((getString(R.string.strWhosOut)));
			((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
			ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivNotification.setVisibility(View.VISIBLE);
			ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
			ivSearch.setVisibility(View.VISIBLE);
			TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
			tvEdit.setText(getString(R.string.strEdit));
			tvEdit.setVisibility(View.GONE);
			

		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id)
		{


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
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int textlength = edSearch.getText().length();
		if(networkTask != null)
			networkTask.cancel(true);

		if (textlength == 0)
		{

			WhosVenueListAdapter adapter = new WhosVenueListAdapter(
					getActivity(), beanListWhosVenue);
			ListView lvVenueList = (ListView) view
					.findViewById(R.id.lvFriendsList);
			lvVenueList.setAdapter(adapter);


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
		}

		
	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1,
			String arg2) {
		Gson gson = new Gson();
		switch (id) {

		case SEARCH_DATA:

			if (object != null && !object.equals(""))
			{
				ListView lvVenueList = (ListView) view
						.findViewById(R.id.lvFriendsList);
				gsResult = gson.fromJson(object, GSearch.class).getResult();
				if (gsResult != null && gsResult.size()>0 && !object.contains("No Record Found"))
				{
					
					lvVenueList.setVisibility(View.VISIBLE);
					GSAdapter gsAdapter = new GSAdapter(getActivity(), gsResult, true);
					lvVenueList.setAdapter(gsAdapter);
				}
				else
					lvVenueList.setVisibility(View.GONE);

			}
			break;

		default:
			break;
		}


	} 

}
