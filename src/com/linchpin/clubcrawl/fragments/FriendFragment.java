package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.FriendsListAdapter;
import com.linchpin.clubcrawl.adapters.RequestListAdapter;
import com.linchpin.clubcrawl.beans.BeanFriendInfo;
import com.linchpin.clubcrawl.beans.BeanListFriends;
import com.linchpin.clubcrawl.beans.BeanListRequest;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class FriendFragment extends ParentFragment implements OnClickListener,TextWatcher {
	private View view;
	private BeanListFriends beanListFrinds, beanListFriendsBeingShown;
	private BeanListRequest beanListRequests, beanListRequestBeingShown;
	private String previousSearchedString = "";
	private String currentBtn = "Friends";
	private SearchFriendListTask searchTask;
	EditText edSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null)
		{
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) parent.removeView(view);
		}
		view = inflater.inflate(R.layout.fragment_friend_profile, container,
				false);
		MenuFragment.currentsel = "FriendFragment";

		// set the font for whole class
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		

		final TextView btnRequests = (TextView) view.findViewById(R.id.btnRequests);
		final TextView btnFriends = (TextView) view.findViewById(R.id.btnFriends);

		beanListFriendsBeingShown = new BeanListFriends();
beanListRequestBeingShown = new BeanListRequest();
		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.userFriendsList + "userID="
					+ appPref.getUserId();
			new FriendsTask(getActivity(), url).execute();
		} else
			Toast.makeText(getActivity(), getString(R.string.internetFailure),
					Toast.LENGTH_LONG).show();

		btnRequests.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnRequests
						.setBackgroundResource(R.drawable.solid_pink_rectangle);
				btnFriends.setBackgroundResource(R.color.button_background);
				btnRequests
						.setTextColor(getResources().getColor(R.color.white));
				btnFriends.setTextColor(getResources().getColor(R.color.black));
				// btnFriends.setTextColor(R.color.white);
				if (!currentBtn.equals("Request")) {
					if (ConstantUtil.isNetworkAvailable(getActivity())) {
						AppPreferences appPref = new AppPreferences(
								getActivity());
						String url = ConstantUtil.userFriendRequestList + "userID="
								+ appPref.getUserId();
						new RequestTask(getActivity(), url).execute();
					} else
						Toast.makeText(getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
				}
				currentBtn = "Request";
			}
		});

	

		btnFriends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnFriends
						.setBackgroundResource(R.drawable.solid_pink_rectangle);
				btnRequests.setBackgroundResource(R.color.button_background);
				btnFriends.setTextColor(getResources().getColor(R.color.white));
				btnRequests
						.setTextColor(getResources().getColor(R.color.black));
				if (!currentBtn.equals("Friends")) {
					if (ConstantUtil.isNetworkAvailable(getActivity())) {
						AppPreferences appPref = new AppPreferences(
								getActivity());
						String url = ConstantUtil.userFriendsList + "userID="
								+ appPref.getUserId();
						new FriendsTask(getActivity(), url).execute();
					} else
						Toast.makeText(getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
				}
				currentBtn = "Friends";
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
		ListView lvFriendsList = (ListView) view
				.findViewById(R.id.lvFriendsList);
		lvFriendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				if (beanListFriendsBeingShown.getFriends().get(arg2).getId() != null)
					b.putString("id", beanListFriendsBeingShown.getFriends()
							.get(arg2).getId());
					
				else if(currentBtn.equals("Friends"))
					b.putString("id", beanListFriendsBeingShown.getFriends()
							.get(arg2).getID());
				else
					b.putString("id", beanListRequestBeingShown.getResult()
							.get(arg2).getRelating_user());
				activityCallback.onSendMessage(new FriendDetailFragment(), b,
						"FriendProfileFragment");
				currentBtn = "Friends";
				// FriendProfileFragment fr = new FriendProfileFragment();
				// FragmentManager fm =
				// getActivity().getSupportFragmentManager();
				// FragmentTransaction fragmentTransaction =
				// fm.beginTransaction();
				//
				// fragmentTransaction.add((R.id.frame),
				// fr,"FriendProfileFragment");
				// fragmentTransaction.addToBackStack("FriendProfileFragment");
				// fragmentTransaction.commit();
			}
		});
		return view;
	}

	

	// Task for get FriendListInfo data
	public class FriendsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String url;

		public FriendsTask(Context con, String url) {
			this.con = con;
			this.url = url;
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
			AppPreferences appPref = new AppPreferences(getActivity());

			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				beanListFrinds = gson.fromJson(responseString,
						BeanListFriends.class);
				beanListFriendsBeingShown.setFriends(beanListFrinds
						.getFriends());
				beanListFriendsBeingShown.setUser(beanListFrinds.getUser());

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "FriendsTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}
	
	// Task for get FriendListInfo data
		public class RequestTask extends AsyncTask<Void, Void, Void> {
			ProgressDialog pd;
			Context con;
			String url;

			public RequestTask(Context con, String url) {
				this.con = con;
				this.url = url;
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
				AppPreferences appPref = new AppPreferences(getActivity());

				String responseString = ConstantUtil.http_connection(url);

				if (responseString != null && !responseString.equals("")) {
					beanListRequests = gson.fromJson(responseString,
							BeanListRequest.class);
					beanListRequestBeingShown.setResult((beanListRequests
							.getResult()));
					//beanListRequestBeingShown.setUser(beanListRequests.getUser());

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
				Message myMessage = new Message();
				myMessage.obj = "RequestTask";
				myHandler.sendMessage(myMessage);
				super.onPostExecute(result);

			}

		}

	private Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("FriendsTask")) {

				if ((beanListFriendsBeingShown == null))

				{

					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}

				else {
					FriendsListAdapter adapter = new FriendsListAdapter(
							getActivity(), beanListFriendsBeingShown);
					ListView lvFriendsList = (ListView) view
							.findViewById(R.id.lvFriendsList);
					lvFriendsList.setAdapter(adapter);

				}
				
			}
			else
			{
				if ((beanListRequestBeingShown == null))

				{

					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}

				else {
					RequestListAdapter adapter = new RequestListAdapter(
							getActivity(), beanListRequestBeingShown);
					ListView lvFriendsList = (ListView) view
							.findViewById(R.id.lvFriendsList);
					lvFriendsList.setAdapter(adapter);

				}

			}

		}
	};

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
		protected Void doInBackground(Void... params) {
			String url = "";
			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			url = ConstantUtil.searchFrindListUrl + "term=" + chars
					+ "&userID=" + appPref.getUserId();
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				JSONArray array = null;
				List<BeanFriendInfo> listBeanFrienfdInfo = new ArrayList<BeanFriendInfo>();
				try {
					array = new JSONArray(responseString);

					for (int i = 0; i < array.length(); i++) {
						String obj = array.getString(i);
						BeanFriendInfo beanFrindInfo = gson.fromJson(obj,
								BeanFriendInfo.class);
						listBeanFrienfdInfo.add(beanFrindInfo);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				beanListFriendsBeingShown.setFriends(listBeanFrienfdInfo);
				beanListFriendsBeingShown.setUser(null);

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		
			Message myMessage = new Message();
			myMessage.obj = "FriendsTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	// set the title bar by changing notification image with Edit text
	private void setTitleBar() {

		 edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
		TextView tvLocation = (TextView) getActivity().findViewById(R.id.tvLocation);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
		getActivity().findViewById(R.id.ivComposePost).setVisibility(View.INVISIBLE);
		getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
		tvLocation.setOnClickListener(null);
		edSearch.setOnClickListener(null);
		//	edSearch.setOnClickListener(this);
		tvLocation.setText(getString(R.string.strFriends));
		edSearch.setHint("Search a friend");
		//edSearch.removeTextChangedListener();
		edSearch.addTextChangedListener(this);
	
		ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
		ivSearch.setVisibility(View.VISIBLE);
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setText(getString(R.string.strEdit));
		tvEdit.setVisibility(View.GONE);
		
	
		

//		ImageView ivNotification = (ImageView) getActivity().findViewById(
//				R.id.ivNotifications);
//		ivNotification.setVisibility(View.GONE);
//
//		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEdit);
//		tvEdit.setVisibility(View.GONE);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(searchTask!= null)
			searchTask.cancel(true);
		if (String.valueOf(s).equals("") && currentBtn.equals("Friends") && beanListFrinds != null) {
			beanListFriendsBeingShown.setFriends(beanListFrinds
					.getFriends());
			beanListFriendsBeingShown.setUser(beanListFrinds.getUser());
			FriendsListAdapter adapter = new FriendsListAdapter(
					getActivity(), beanListFriendsBeingShown);
			ListView lvList = (ListView) view
					.findViewById(R.id.lvFriendsList);
			lvList.setAdapter(adapter);
		}
		else if (String.valueOf(s).equals("") && currentBtn.equals("Request") && beanListRequests != null) {
			beanListRequestBeingShown.setResult(beanListRequests.getResult()
					);
			//beanListFriendsBeingShown.setUser(beanListFrinds.getUser());
			RequestListAdapter adapter = new RequestListAdapter(
					getActivity(), beanListRequestBeingShown);
			ListView lvList = (ListView) view
					.findViewById(R.id.lvFriendsList);
			lvList.setAdapter(adapter);
		}
		else {
			if (ConstantUtil.isNetworkAvailable(getActivity())) {
				searchTask = new SearchFriendListTask(getActivity(), String
						.valueOf(s));
				searchTask.execute();
			}
		}
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v)
	{
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
	public void onResume() {
		setTitleBar();
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
			getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
			super.onPause();
		}
		
}
