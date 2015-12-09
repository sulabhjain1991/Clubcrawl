package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
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
import android.widget.TextView;
import android.widget.Toast;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.FeedsListAdapter;
import com.linchpin.clubcrawl.adapters.GSAdapter;
import com.linchpin.clubcrawl.beans.BeanFeedInfo;
import com.linchpin.clubcrawl.beans.BeanListFeeds;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.linchpin.clubcrawl.jobjects.inbox.GSearch;

public class FeedFragment extends ParentFragment implements TextWatcher,OnClickListener,Result {

	private View view;
	private BeanListFeeds beanListFeeds;
	private JSONObject jsonObject;
	private final int 			SEARCH_DATA = 1001;
	private EditText			edSearch; 
	NetworkTask					 networkTask;
	FeedsListAdapter fadapter;
	private ArrayList<GSResult>	gsResult;
	public boolean		localSearch	= false;
	String total = "0";
	PullAndLoadListView lvFeedList;
	private final int	FEEDS	= 1;
	private boolean					refreshing;
	private boolean					loadingMore;
	private boolean					isProgressBar=true;
	private boolean isLoadFinished = false;
	int currentPage=0;
	List<BeanFeedInfo> listAllFeeds;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MenuFragment.currentsel = "FeedFragment";
		view = inflater.inflate(R.layout.direct_feed_fragment, container,
				false);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		setTitleBar();
		listAllFeeds=new ArrayList<BeanFeedInfo>();
		lvFeedList = (PullAndLoadListView) view
				.findViewById(R.id.lvFeedList);

		networkCall();

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

		lvFeedList.setOnItemClickListener(new OnItemClickListener() {

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

		lvFeedList.setOnLoadMoreListener(new OnLoadMoreListener() {

			public void onLoadMore() {


				// Do the work to load more items at the end of list
				// here
				if(!isLoadFinished)
				{
					loadingMore = true;
					isProgressBar = false;
					if (ConstantUtil
							.isNetworkAvailable(getActivity())) {
						currentPage = currentPage + 10;
						AppPreferences appPref = new AppPreferences(
								getActivity());
						String url = ConstantUtil.userFeedsList + appPref.getUserId()
								+ "&feed_start="+currentPage;
						new ViewPostTask(getActivity(), url).execute();

					} else
					{
						loadingMore =false;
						lvFeedList.onLoadMoreComplete();
						Toast.makeText(
								getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
					}
				}
				else
					lvFeedList.onLoadMoreComplete();
			}
		});


		lvFeedList.setOnRefreshListener(new OnRefreshListener()
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
					currentPage = 0;

					AppPreferences appPref = new AppPreferences(
							getActivity());
					String url = ConstantUtil.userFeedsList + appPref.getUserId()
							+ "&feed_start="+currentPage;
					new ViewPostTask(getActivity(), url).execute();

				}
				else
				{
					lvFeedList.onRefreshComplete();
					refreshing = false;
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}

			}
		});








		return view;
	}





	private void networkCall() {
		// TODO Auto-generated method stub
		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.userFeedsList + appPref.getUserId()
					+ "&feed_start="+currentPage;
			//	String url1 = ConstantUtil.totalNotification + appPref.getUserId();
			//new FeedsTask(getActivity(), url, url1).execute();
			NetworkTask networkTask = new NetworkTask(getActivity(), FEEDS);
			networkTask.setProgressDialog(true, "Requesting");
			networkTask.exposePostExecute(FeedFragment.this);
			networkTask.execute(url);

		} else
			Toast.makeText(getActivity(), getString(R.string.internetFailure),
					Toast.LENGTH_LONG).show();

	}
	
	// set the title bar by changing notification image with Edit text
	private void setTitleBar() {

		TextView tvTitle = (TextView) getActivity().findViewById(
				R.id.tvLocation);
		tvTitle.setText(getString(R.string.strFeeds));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, // left
				0, // top
				0, // right
				0// bottom
				);

		//		ImageView ivNotification = (ImageView) getActivity().findViewById(
		//				R.id.ivNotifications);
		//		ivNotification.setVisibility(View.GONE);
		//
		//		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEdit);
		//		tvEdit.setVisibility(View.GONE);

	}
	public void onResume()
	{
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		if (getActivity() instanceof MainScreen)
		{
			edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			edSearch.setText("");
			edSearch.addTextChangedListener(this);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText((getString(R.string.strFeeds)));
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


			 fadapter = new FeedsListAdapter(
					getActivity(),listAllFeeds);

			lvFeedList.setAdapter(fadapter);


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
				gsResult = gson.fromJson(object, GSearch.class).getResult();
				if (gsResult != null && gsResult.size()>0 && !object.contains("No Record Found"))
				{

					lvFeedList.setVisibility(View.VISIBLE);
					GSAdapter gsAdapter = new GSAdapter(getActivity(), gsResult, true);
					lvFeedList.setAdapter(gsAdapter);
				}
				else
					lvFeedList.setVisibility(View.GONE);

			}
			break;

		case FEEDS:
			if(object!=null)
			{
			try
			{
				JSONObject jsonObject = new JSONObject(object);
				if(!jsonObject.equals("") && jsonObject!=null){

					JSONArray array = null;

					try {
						array = jsonObject.optJSONArray("feedList");
						List<BeanFeedInfo> FeedsInfo = new ArrayList<BeanFeedInfo>();
						BeanListFeeds listFeeds=new BeanListFeeds();
						for(int i=0;i<array.length();i++)
						{
							String obj = array.getString(i);
							BeanFeedInfo beanFeedInfo = gson.fromJson(obj,
									BeanFeedInfo.class);
							FeedsInfo.add(beanFeedInfo);





						}
						listFeeds.setFeedList(FeedsInfo);
						listAllFeeds.addAll(listFeeds.getFeedList());
						
						fadapter=new FeedsListAdapter(getActivity(),listAllFeeds);
						lvFeedList.setAdapter(fadapter);

					} catch (Exception e) {
						// TODO: handle exception
					}					

				}
				else {

					Toast.makeText(getActivity(), getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();
				}


			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			}
			else
			{
				Toast.makeText(getActivity(), "Error in API's", Toast.LENGTH_SHORT).show();
			}


			break;


		default:
			break;
		}


	} 


	public class ViewPostTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String url;

		public ViewPostTask(Context con, String url) {
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
				beanListFeeds = gson.fromJson(responseString,
						BeanListFeeds.class);

			}




			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "ViewMorePostTask";
			myHandler1.sendMessage(myMessage);

			super.onPostExecute(result);

		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			lvFeedList.onLoadMoreComplete();
		}

	}

	private Handler myHandler1 = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("ViewMorePostTask")) {


				if ((beanListFeeds == null)) {
					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}

				else {
					if(listAllFeeds != null && currentPage == 0)
						listAllFeeds.clear();
					
				
					listAllFeeds.addAll(beanListFeeds.getFeedList());
					if(listAllFeeds.size()==0)
					{
						Toast.makeText(getActivity(), "No Post", Toast.LENGTH_LONG).show();
					}
					else{



						fadapter.setList(listAllFeeds);
						fadapter.notifyDataSetChanged();
					}
/*					if (beanListFeeds.getFeedList().get(currentPage).getFeed_has_more().equals("0")) {

//						isLoadFinished=true;
					}*/
					//						lvFriendsList.setDividerHeight(0);

				}
				if (refreshing)
				{
					currentPage = 0;
					refreshing = false;
					lvFeedList.onRefreshComplete();
				}
				if (loadingMore)
				{
					lvFeedList.onLoadMoreComplete();
					loadingMore = false;

				}

			}


		}
	};


}
