package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.APP;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.SwipeDismissListViewTouchListener;
import com.linchpin.clubcrawl.adapters.FrndsListAdapter;
import com.linchpin.clubcrawl.adapters.MessengerListAdapter;
import com.linchpin.clubcrawl.beans.BeanFriendsList;
import com.linchpin.clubcrawl.beans.BeanSingleFriendInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.Inbox;
import com.linchpin.clubcrawl.jobjects.inbox.InboxResult;

public class MessagingFragment extends Fragment implements TextWatcher, Result,OnClickListener
{

	private View					view;
	private ListView				lvsearchfrnds;
	private PullAndLoadListView     messegingList;
	ArrayList<InboxResult>			inboxResults,filteredinboxResults;
	List<BeanSingleFriendInfo> singleList=new ArrayList<BeanSingleFriendInfo>();
	private MessengerListAdapter	adapter;
	private EditText edSearch;
	private Inbox inbox,filteredInbox;
	private int positionDelete=0;
	List<BeanSingleFriendInfo> list=new ArrayList<BeanSingleFriendInfo>();
	BeanFriendsList friendsList=new BeanFriendsList();
	FrndsListAdapter frndAdapter=null;
	JSONArray array;
	boolean isFirst=false;
	@Override
	public void onAttach(Activity activity)
	{
		if (activity instanceof MainScreen)
		{
			((EditText) activity.findViewById(R.id.edSearch)).addTextChangedListener(this);
			((TextView) activity.findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
			((TextView) activity.findViewById(R.id.tvLocation)).setText("Messenger");
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		view = inflater.inflate(R.layout.messaging_list, container, false);
		messegingList = (PullAndLoadListView) view.findViewById(R.id.friend_list);
		lvsearchfrnds=(ListView) view.findViewById(R.id.lvSearchFrnds);
		inboxResults = new ArrayList<InboxResult>();
		filteredinboxResults = new ArrayList<InboxResult>();
		filteredInbox = new Inbox();
		frndAdapter=null;

		if(ConstantUtil.isNetworkAvailable(getActivity()))
		{
			new SearchFriends().execute();

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

			lvsearchfrnds.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub

					BeanSingleFriendInfo frndInfo=singleList.get(position);
					Bundle bundle=new Bundle();
					bundle.putString("message_id",AppPreferences.getInstance(getActivity()).getUserId()+"_"+frndInfo.getID() );
					bundle.putString("profile_image", frndInfo.getProfile_pic());
					bundle.putString("name", frndInfo.getFirst_name()+" "+frndInfo.getLast_name());
					bundle.putString("activetime",frndInfo.getLast_logged_at());
					MessagingDetailedFragment fr = new MessagingDetailedFragment();
					FragmentManager fm = getActivity().getSupportFragmentManager();
					fr.setArguments(bundle);
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace((R.id.frame), fr,"MessagingDetailedFragment");
					fragmentTransaction.addToBackStack("MessagingDetailedFragment");
					fragmentTransaction.commit();
					edSearch.setText("");
					getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
					getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);




				}
			});






			messegingList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
				{
					InboxResult result=(InboxResult) filteredInbox.getList().get((position-1));

					Bundle bundle=new Bundle();
					bundle.putString("message_id", result.getConversation_id());
					bundle.putString("profile_image", result.getProfile_pic());
					bundle.putString("name", result.getFirst_name()+" "+result.getLast_name());
					bundle.putString("activetime",result.getLast_logged_at());
					MessagingDetailedFragment fr = new MessagingDetailedFragment();
					FragmentManager fm = getActivity().getSupportFragmentManager();
					fr.setArguments(bundle);
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace((R.id.frame), fr,"MessagingDetailedFragment");
					fragmentTransaction.addToBackStack("MessagingDetailedFragment");
					fragmentTransaction.commit();


					edSearch.setText("");


				}
			});

			SwipeDismissListViewTouchListener touchListener =
					new SwipeDismissListViewTouchListener(
							messegingList,
							new SwipeDismissListViewTouchListener.OnDismissCallback() {
								@Override
								public void onDismiss(ListView listView, int[] reverseSortedPositions) {

									for (int position : reverseSortedPositions) {
										final InboxResult result=(InboxResult) filteredInbox.getList().get((position));
										AppPreferences appPref = new AppPreferences(getActivity());

										positionDelete=position;
										NetworkTask networkTask=new NetworkTask(getActivity(), 1);
										networkTask.setProgressDialog(true, "Requesting");
										networkTask.exposePostExecute(MessagingFragment.this);
										networkTask.execute(ConstantUtil.deletingChatUrl+"&sid="+appPref.getUserId()+"&rid="+result.getDisplay_user_id());


									}

									adapter.notifyDataSetChanged();
								}
							});
				messegingList.setOnScrollListener(touchListener.makeScrollListener());
				messegingList.setOnTouchListener(touchListener);
		}
		else
		{
			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
		}
		
		messegingList.setOnRefreshListener(new OnRefreshListener()
		{

			@Override
			public void onRefresh()
			{
				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					MenuFragment.currentsel = "FriendListFragments";
					NetworkTask networkTask = new NetworkTask(getActivity(), 1000);
					networkTask.setProgressDialog(true);
					networkTask.exposePostExecute(MessagingFragment.this);
					String url=ConstantUtil.baseUrl + "follow/messengerList?user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&curpage=1&perpage=50";
					networkTask.execute(ConstantUtil.baseUrl + "follow/messengerList?user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&curpage=1&perpage=50");
					messegingList.onRefreshComplete();
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();}
				    messegingList.onRefreshComplete();
			}
		});

		return view;

	}



	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {

		if(array==null)
		{
			Toast.makeText(getActivity(), "No Friends", Toast.LENGTH_SHORT).show();
		}

		else{

			if (String.valueOf(s).equals("") ) {
				filteredInbox.setList(inbox.getList());
				inboxResults.clear();
				inboxResults.addAll(filteredInbox.getList());

			}

			else {

				singleList.clear();

				for(int i=0;i<list.size();i++)
				{
					String name=list.get(i).getFirst_name().toString()+" "+list.get(i).getLast_name();
					if(name.length()>=s.length())
					{
					String sname=name.substring(0, s.length());
					if(sname.equalsIgnoreCase(s.toString()))
					{
						singleList.add(list.get(i));
						if(frndAdapter==null)
						{
							frndAdapter=new FrndsListAdapter(getActivity(), singleList);
							lvsearchfrnds.setAdapter(frndAdapter);
						}
						else
							frndAdapter.notifyDataSetChanged();
					}
					}
					

				}






			}}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

		// TODO Auto-generated method stub
	}

	@Override
	public void afterTextChanged(Editable s) {

		// TODO Auto-generated method stub
	}

	@Override
	public void resultfromNetwork(String result, int id,int arg1,String arg2)
	{
		Gson gson = new Gson();
		switch (id)
		{
		case 1000:
			if (result != null && !result.equals(""))
			{
				messegingList.onRefreshComplete();
				if(result.contains("No Records"))
				{
					Toast.makeText(getActivity(), "No Friends", Toast.LENGTH_LONG).show();
				}
				else if(result.contains("No Feeds"))
				{
					Toast.makeText(getActivity(), "No feeds", Toast.LENGTH_LONG).show();
				}
				else
				{


					APP.GLOBAL().getEditor().putString(APP.PREF.INBOX_LIST.key, result);
					inbox = gson.fromJson(result, Inbox.class);
					filteredinboxResults = inbox.getList();
					filteredInbox.setList(filteredinboxResults );
					if(filteredinboxResults.size() == 0)
					{
						Toast.makeText(getActivity(), "No Messages", Toast.LENGTH_LONG).show();
					}
					else
					{
						adapter= new MessengerListAdapter(getActivity(), filteredInbox.getList());

						messegingList.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
					}
				}
			}
			else
			{
				messegingList.onRefreshComplete();
				Toast.makeText(getActivity(), "No Messages", Toast.LENGTH_LONG).show();
			}
			
			break;

		case 1:
			try {
				JSONObject jsonObject = new JSONObject(result);
				if(!jsonObject.equals("") && jsonObject!=null)
				{
					String status=jsonObject.optString("status").toString();
					if(status.equals("success"))
					{
						filteredInbox.getList().remove(positionDelete);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(getActivity(), ""+jsonObject.optString("status").toString(), Toast.LENGTH_LONG).show();
					}
				}


			} catch (Exception e) {
				// TODO: handle exception
			}

			break;
		default:
			break;
		}
	};

	@Override
	public void onResume()
	{


		if (getActivity() instanceof MainScreen)
		{
			//frndAdapter=null;
			//singleList.clear();
			edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			TextView tvLocation = (TextView) getActivity().findViewById(R.id.tvLocation);
			getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
			getActivity().findViewById(R.id.ivComposePost).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.btCancel).setOnClickListener(this);

			tvLocation.setOnClickListener(null);
			edSearch.setOnClickListener(null);
			//	edSearch.setOnClickListener(this);
			tvLocation.setText("Messenger");
			edSearch.setHint("Search a friend");
			//edSearch.removeTextChangedListener();
			edSearch.addTextChangedListener(this);

			ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
			ivSearch.setVisibility(View.VISIBLE);
			TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
			tvEdit.setText(getString(R.string.strEdit));
			tvEdit.setVisibility(View.GONE);

		}


		MenuFragment.currentsel = "FriendListFragments";
		NetworkTask networkTask = new NetworkTask(getActivity(), 1000);
		networkTask.setProgressDialog(true);
		networkTask.exposePostExecute(this);
		String url=ConstantUtil.baseUrl + "follow/messengerList?user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&curpage=1&perpage=50";
		networkTask.execute(ConstantUtil.baseUrl + "follow/messengerList?user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&curpage=1&perpage=50");
		super.onResume();
	}

	@Override
	public void onPause()
	{
		//frndAdapter=null;
		//singleList.clear();
		list.clear();
		getActivity().findViewById(R.id.ivComposePost).setVisibility(View.VISIBLE);
		edSearch.setHint("Search a venue or postcode");
		((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).removeTextChangedListener(this);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener((MainScreen)getActivity());
		getActivity().findViewById(R.id.btCancel).setOnClickListener((MainScreen)getActivity());
		//getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btCancel:
			((MainScreen) getActivity()).searchViewToggle();
			edSearch.setText("");
			singleList.clear();
			messegingList.setVisibility(view.VISIBLE);
			lvsearchfrnds.setVisibility(View.GONE);
			break;
		case R.id.ivSearch:
			((MainScreen) getActivity()).searchViewToggle();
			messegingList.setVisibility(view.GONE);
			lvsearchfrnds.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}

	}

	// Task for get FriendListInfo data
	public class SearchFriendListTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String chars;

		public SearchFriendListTask(Context con, String chars) {
			this.con = con;
			this.chars = chars;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			String url = "";
			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			url = ConstantUtil.searchFrindListUrl + "term=" + chars
					+ "&userID=" + appPref.getUserId();
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				JSONArray array = null;
				filteredinboxResults = new ArrayList<InboxResult>();
				try {
					array = new JSONArray(responseString);

					for (int i = 0; i < array.length(); i++) 
					{
						JSONObject obj = array.getJSONObject(i);
						InboxResult results = new InboxResult();
						results.setConversation_id(appPref.getUserId()+"_"+obj.getString("id"));
						results.setFirst_name(obj.getString("first_name"));
						results.setLast_name(obj.getString("last_name"));
						//						/results.setLast_logged_at(obj.getString("Last_logged_at"));

						if(obj.getString("is_logged_in") == null || obj.getString("is_logged_in").equals("null"))
							results.setIs_logged_in("0");
						else 
							results.setIs_logged_in(obj.getString("is_logged_in"));
						results.setSubject(obj.getString("message"));
						results.setCreated_at(obj.getString("msg_time"));
						if(obj.getString("relation").equals("friend"))
							filteredinboxResults.add(results);


					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				filteredInbox.setList(filteredinboxResults);


			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}

			adapter= new MessengerListAdapter(getActivity(), filteredInbox.getList());
			messegingList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			super.onPostExecute(result);

		}

	}

	String responseString ="";

	class SearchFriends extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			try {

				String url=ConstantUtil.searchFriendsUrl+AppPreferences.getInstance(getActivity()).getUserId();
				responseString = ConstantUtil.http_connection(url);

			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();


			}


			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			JSONObject object;

			Gson gson = new Gson();

			if(!responseString.equals("")||responseString!=null)
			{
				try {
					object=new JSONObject(responseString);
					array=object.optJSONArray("friendList");
					if(array==null)
					{
						Toast.makeText(getActivity(), "No Friends", Toast.LENGTH_SHORT).show();
						getActivity().findViewById(R.id.ivSearch).setOnClickListener(null);
					}
					else
					{

						for(int i=0;i<array.length();i++)
						{
							String obj = array.getString(i);
							BeanSingleFriendInfo frndInfo=gson.fromJson(obj, BeanSingleFriendInfo.class);
							list.add(frndInfo);
						}


						friendsList.setList(list);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}



			super.onPostExecute(result);
		}

	}
}
