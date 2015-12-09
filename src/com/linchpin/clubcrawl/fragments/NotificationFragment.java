package com.linchpin.clubcrawl.fragments;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.GSAdapter;
import com.linchpin.clubcrawl.adapters.NotificationsListAdapter;
import com.linchpin.clubcrawl.beans.BeanListNotifications;
import com.linchpin.clubcrawl.beans.BeanNotificationInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.linchpin.clubcrawl.jobjects.inbox.GSearch;

public class NotificationFragment  extends ParentFragment implements TextWatcher,Result,OnClickListener
{

	private BeanListNotifications beanListNotifications;
	private List<BeanNotificationInfo> listBeanNotificationInfo;
	private int offsetNotification = 0;

	private NotificationsListAdapter adapter;
	private View footerView;
	View view;
	String status0List="";
	private com.costum.android.widget.PullAndLoadListView lvFriendsList;
	private final int 			SEARCH_DATA = 1001;
	private EditText			edSearch; 
	NetworkTask					 networkTask;

	private ArrayList<GSResult>	gsResult;
	public boolean		localSearch	= false;
	private boolean					refreshing;
	private boolean					loadingMore;
	private boolean					isProgressBar=true;
	private boolean isLoadFinished = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MenuFragment.currentsel = "NotificationFragment";
		view = inflater.inflate(R.layout.fragment_notification, container,
				false);
		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.layout_footer_view, null, false);
		LinearLayout llSwithFragment = (LinearLayout) view.findViewById(R.id.llSwitchFragmentButtons);
		llSwithFragment.setVisibility(View.GONE);
		edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
		listBeanNotificationInfo = new ArrayList<BeanNotificationInfo>();
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		setTitleBar();


		RelativeLayout rlSearchBox = (RelativeLayout) view
				.findViewById(R.id.search_box);
		rlSearchBox.setVisibility(View.GONE);

		lvFriendsList = (com.costum.android.widget.PullAndLoadListView) view
				.findViewById(R.id.lvFriendsList);
		
		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			AppPreferences appPref = new AppPreferences(
					getActivity());
			String url = ConstantUtil.notificationList
					+ appPref.getUserId() + "&offset=0";
			new NotificationTask(getActivity(), url).execute();
		} else
			Toast.makeText(getActivity(),
					getString(R.string.internetFailure),
					Toast.LENGTH_LONG).show();


		// set a listener to be invoked when the list reaches the end
		lvFriendsList.setOnLoadMoreListener(new OnLoadMoreListener() {

			public void onLoadMore() {


				// Do the work to load more items at the end of list
				// here
				if(!isLoadFinished)
				{
					loadingMore = true;
					isProgressBar = false;
					if (ConstantUtil
							.isNetworkAvailable(getActivity())) {
						offsetNotification = offsetNotification + 10;
						AppPreferences appPref = new AppPreferences(
								getActivity());
						String url = ConstantUtil.notificationList
								+ appPref.getUserId() + "&offset="
								+ offsetNotification;
						new NotificationTask(getActivity(), url)
						.execute();
					} else
					{
						loadingMore =false;
						lvFriendsList.onLoadMoreComplete();
						Toast.makeText(
								getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
					}
				}
				else
					lvFriendsList.onLoadMoreComplete();
			}
		});

		lvFriendsList.setOnRefreshListener(new OnRefreshListener()
		{

			@Override
			public void onRefresh()
			{
				// Your code to refresh the list contents goes here

				// for example:
				// If this is a webservice call, it might be asynchronous so
				// you would have to call listView.onRefreshComplete(); when
				// the webservice returns the data
				refreshing = true;
				isProgressBar = false;
				isLoadFinished = false;
				if (ConstantUtil.isNetworkAvailable(getActivity())) {
					offsetNotification = 0;

					AppPreferences appPref = new AppPreferences(
							getActivity());
					String url = ConstantUtil.notificationList
							+ appPref.getUserId() + "&offset=0";
					new NotificationTask(getActivity(), url).execute();
				}
				else
				{
					lvFriendsList.onRefreshComplete();
					refreshing = false;
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}

			}
		});



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

	

		lvFriendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long id) {
				if(edSearch.getText().toString().equals(""))
				{

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

		return view;
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
	public void onTextChanged(CharSequence s, int start, int before, int count) 
	{
		int textlength = edSearch.getText().length();
		if(networkTask != null)
			networkTask.cancel(true);

		if (textlength == 0)
		{

			if (lvFriendsList.getAdapter() == null
					|| offsetNotification == 0) {
				adapter = new NotificationsListAdapter(getActivity(),
						listBeanNotificationInfo);
				lvFriendsList.addFooterView(footerView);
				lvFriendsList.setAdapter(adapter);
			} else {
				adapter.setBeanNotificationsList(listBeanNotificationInfo);
				adapter.notifyDataSetChanged();
			}
			if (beanListNotifications.getHasmore().equals("0")) {

				if (lvFriendsList.getFooterViewsCount() > 0) {

					footerView.setVisibility(View.GONE);
					lvFriendsList.removeFooterView(footerView);
				}
			}
			lvFriendsList.setDividerHeight(0);


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

	// set the title bar by changing notification image with Edit text
	private void setTitleBar() {

		TextView tvTitle = (TextView) getActivity().findViewById(
				R.id.tvLocation);
		tvTitle.setText(getString(R.string.strNotification));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, // left
				0, // top
				0, // right
				0// bottom
				);

	

	}

	public class NotificationTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String url;

		public NotificationTask(Context con, String url) {
			this.con = con;
			this.url = url;

		}

		@Override
		protected void onPreExecute() {
			if(isProgressBar)
			{
				pd = ProgressDialog.show(con, null, "Loading...");
				pd.setContentView(R.layout.progress_layout);
			}

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());

			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				parseJson(responseString);
				if(!status0List.equals(""))
				{
					NetworkTask myTask = new NetworkTask(getActivity(), 1);
					myTask.setProgressDialog(false);
					String url = ConstantUtil.changeNotificationUrl+"id="+status0List+"&type=n";
					myTask.execute(url);
				}
		


			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "NotificationTask";
			myHandler1.sendMessage(myMessage);

			super.onPostExecute(result);

		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			lvFriendsList.onLoadMoreComplete();
		}

	}

	private Handler myHandler1 = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("NotificationTask")) {


				if ((beanListNotifications == null)) {
					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}

				else {
					if(listBeanNotificationInfo != null && offsetNotification == 0)
						listBeanNotificationInfo.clear();
					listBeanNotificationInfo.addAll(beanListNotifications
							.getResult());
					if(listBeanNotificationInfo.size()==0)
					{
						Toast.makeText(getActivity(), "No Notification", Toast.LENGTH_LONG).show();
					}
					else{
						if (beanListNotifications.getHasmore().equals("0")) {

							isLoadFinished=true;
						}

						if (lvFriendsList.getAdapter() == null
								|| offsetNotification == 0) {
							adapter = new NotificationsListAdapter(getActivity(),
									listBeanNotificationInfo);
							//							lvFriendsList.addFooterView(footerView);
							lvFriendsList.setAdapter(adapter);
						} else {
							adapter.setBeanNotificationsList(listBeanNotificationInfo);
							adapter.notifyDataSetChanged();
						}
					
						//						lvFriendsList.setDividerHeight(0);

					}
					if (refreshing)
					{
						offsetNotification = 0;
						refreshing = false;
						lvFriendsList.onRefreshComplete();
					}
					if (loadingMore)
					{
						lvFriendsList.onLoadMoreComplete();
						loadingMore = false;

					}

				}
			}

		}
	};

	public void parseJson(String resString) 
	{
		try
		{
			JSONObject obj = new JSONObject(resString);
			JSONArray array = obj.getJSONArray("result");
			beanListNotifications = new BeanListNotifications();
			List<BeanNotificationInfo> listNotification = new ArrayList<BeanNotificationInfo>();
			beanListNotifications.setHasmore(obj.getString("hasmore"));
			status0List = "";
			for(int i = 0;i<array.length();i++)
			{
				BeanNotificationInfo info = new BeanNotificationInfo();
				JSONObject obj1 = array.getJSONObject(i);
				if(obj1.has("notification_id"))
					info.setNotification_id(obj1.getString("notification_id"));
				if(obj1.has("notification_date"))
					info.setNotification_date(obj1.getString("notification_date"));
				if(obj1.has("status"))
				{
					info.setStatus(obj1.getString("status"));
					if(info.getStatus().equals("0"))
						status0List = status0List + info.getNotification_id()+",";
				}
				if(obj1.has("post_type"))
					info.setPost_type(obj1.getString("post_type"));
				if(obj1.has("message"))
					info.setMessage(obj1.getString("message"));
				if(obj1.has("post_img"))
					info.setPost_img(obj1.getString("post_img"));
				if(obj1.has("f_status"))
					info.setF_status(obj1.getString("f_status"));
				if(obj1.has("user_id"))
					info.setUser_id(obj1.getString("user_id"));
				if(obj1.has("user_name"))
					info.setUser_name(obj1.getString("user_name"));
				if(obj1.has("user_image"))
					info.setUser_image(obj1.getString("user_image"));
				if(obj1.has("image"))
					info.setImage(obj1.getString("image"));
				if(obj1.has("type"))
					info.setType(obj1.getString("type"));
				if(obj1.has("msg"))
					info.setMsg(obj1.getString("msg"));
				listNotification.add(info);

			}
			if(!status0List.equals(""))
				status0List = status0List.substring(0, status0List.length()-1);
			System.out.println("list::"+status0List);
			beanListNotifications.setResult(listNotification);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
				gsResult = gson.fromJson(object, GSearch.class).getResult();
				if (gsResult != null && gsResult.size()>0 && !object.contains("No Record Found"))
				{
					lvFriendsList.setVisibility(View.VISIBLE);
					GSAdapter gsAdapter = new GSAdapter(getActivity(), gsResult, true);
					lvFriendsList.setAdapter(gsAdapter);
				}
				else
					lvFriendsList.setVisibility(View.GONE);

			}
			break;

		default:
			break;
		}


	} 

	public void onResume()
	{
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		if (getActivity() instanceof MainScreen)
		{
			EditText edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			edSearch.setText("");
			edSearch.addTextChangedListener(this);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText((getString(R.string.strNotification)));
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
}
