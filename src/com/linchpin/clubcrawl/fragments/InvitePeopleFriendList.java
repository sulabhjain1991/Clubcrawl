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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.FriendsInviteListAdapter;
import com.linchpin.clubcrawl.beans.BeanFriendInfo;
import com.linchpin.clubcrawl.beans.BeanInviteListFriends;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class InvitePeopleFriendList extends ParentFragment {

	View view, footerView;
	private BeanInviteListFriends beanListFrinds, beanListFriendsBeingShown;
	private ArrayList<String> friendId;
	int invitedFriend=0;
	FriendsInviteListAdapter adapter;
	List<String> selectedList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		MenuFragment.currentsel = "InvitePeopleFriendList";
		view = inflater.inflate(R.layout.invite_people_friend_list, container,
				false);
		beanListFriendsBeingShown = new BeanInviteListFriends();
		selectedList = new ArrayList<String>();
		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.layout_footer_view, null, false);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());

		setTitleBar();
		
		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			new SearchFriendListTask(getActivity(), String.valueOf(""))
			.execute();
		}
		TextView tvOk = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvOk.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				//get list of selected people and no of selected people
				getSelectedFriends();

			}
		});

		// For friends search
		performSearch();
		ListView lvFriendsList = (ListView) view
				.findViewById(R.id.lvFriendList);
		lvFriendsList.setSelector(R.color.background_grey_Color);
		lvFriendsList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BeanFriendInfo beanInviteInfo = beanListFriendsBeingShown.getFriendList().get(position);
				String friendID = beanInviteInfo.getID();
				if(selectedList.contains(friendID))
					selectedList.remove(friendID);
				else
					selectedList.add(friendID);
				beanInviteInfo.setSelected(!beanInviteInfo.isSelected());
				beanListFriendsBeingShown.getFriendList().set(position, beanInviteInfo);
				adapter.setBeanInviteListFriends(beanListFriendsBeingShown);
				adapter.notifyDataSetChanged();

			}
		});

		return view;
	}

	protected void getSelectedFriends()
	{
		int countPeople = 0;
		String strInvitePeople = "";
		if(selectedList.size()>0)
		{
			for(int i= 0;i<selectedList.size();i++)
			{
				
					countPeople++;
					strInvitePeople = strInvitePeople + "c-"+selectedList.get(i)+",";


				
			}
		}
		if(strInvitePeople.contains(","))
			strInvitePeople = strInvitePeople.substring(0, strInvitePeople.length()-1);

		AppPreferences appPreferences = AppPreferences.getInstance(getActivity());
		appPreferences.setInvitedFriendsNames(strInvitePeople);
		appPreferences.setInvitedFriendsNumber(countPeople);


		//		InviteOutFriendsFragment fr = new InviteOutFriendsFragment();
		//		FragmentManager fm = getActivity().getSupportFragmentManager();
		//		FragmentTransaction fragmentTransaction = fm.beginTransaction();

		//		fragmentTransaction.replace((R.id.frame), fr,"InviteOutFriends");
		//		fragmentTransaction.addToBackStack("InviteOutFriends");
		//		fragmentTransaction.commit();
		getActivity().getSupportFragmentManager().popBackStack();
	}

	private void performSearch() {
		final EditText etSearch = (EditText) view.findViewById(R.id.etSearchFriend);
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

//				if (ConstantUtil.isNetworkAvailable(getActivity())) {
//					new SearchFriendListTask(getActivity(), String.valueOf(s))
//					.execute();
//				}
				
				beanListFriendsBeingShown = new BeanInviteListFriends();
                int textlength = etSearch.getText().length();
                List<BeanFriendInfo> listFriendInfo = new ArrayList<BeanFriendInfo>();
              
                
                
                for (int i = 0; i < beanListFrinds.getFriendList().size(); i++)
                {
                    if (textlength <= beanListFrinds.getFriendList().get(i).getFullName().length())
                    {
                        if (etSearch.getText().toString().equalsIgnoreCase(
                        (String)
                        beanListFrinds.getFriendList().get(i).getFullName().subSequence(0,
                        textlength)))
                        {
                        	BeanFriendInfo beanInviteInfo = new BeanFriendInfo();
                        	beanInviteInfo.setFirst_name(beanListFrinds.getFriendList().get(i).getFirst_name());
                        	beanInviteInfo.setFbid(beanListFrinds.getFriendList().get(i).getFbid());
                        	beanInviteInfo.setId(beanListFrinds.getFriendList().get(i).getId());
                        	beanInviteInfo.setID((beanListFrinds.getFriendList().get(i).getID()));
                        	beanInviteInfo.setLast_name(beanListFrinds.getFriendList().get(i).getLast_name());
                        	beanInviteInfo.setProfile_pic(beanListFrinds.getFriendList().get(i).getProfile_pic());
                        	beanInviteInfo.setRelation(beanListFrinds.getFriendList().get(i).getRelation());
                        	beanInviteInfo.setSelected(beanListFrinds.getFriendList().get(i).isSelected());
                        	beanInviteInfo.setFullName((beanListFrinds.getFriendList().get(i).getFullName()));
                        	listFriendInfo.add(beanInviteInfo);
                        }
                    }
                }
                beanListFriendsBeingShown.setFriendList(listFriendInfo);
            	adapter = new FriendsInviteListAdapter(
						getActivity(), beanListFriendsBeingShown,selectedList);
				ListView lvFriendsList = (ListView) view
						.findViewById(R.id.lvFriendList);
				lvFriendsList.setAdapter(adapter);

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
		});
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

			url = ConstantUtil.invitePeopleFriendList + "search=" + chars
					+ "&logineduser=" + appPref.getUserId() + "&fbtoken=null";
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				try
				{
				beanListFriendsBeingShown = gson.fromJson(responseString,
						BeanInviteListFriends.class);
				beanListFrinds = gson.fromJson(responseString,
						
						BeanInviteListFriends.class);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
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
			myHandler1.sendMessage(myMessage);
			super.onPostExecute(result);
		}

	}

	private Handler myHandler1 = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("FriendsTask")) {
				if ((beanListFriendsBeingShown == null || beanListFriendsBeingShown.getFriendList() == null)) {
					Toast.makeText(getActivity(),
							getString(R.string.strNoResultFound),
							Toast.LENGTH_LONG).show();
				} else {
					adapter = new FriendsInviteListAdapter(
							getActivity(), beanListFriendsBeingShown,selectedList);
					ListView lvFriendsList = (ListView) view
							.findViewById(R.id.lvFriendList);
					lvFriendsList.setAdapter(adapter);
				}

			}

		}
	};

	@Override
	public void onDestroy() 
	{
		RelativeLayout rlHeader = (RelativeLayout) getActivity().findViewById(R.id.header); 
		rlHeader.setVisibility(View.VISIBLE);
		super.onDestroy();
	}

	@Override
	public void onResume()
	{
		RelativeLayout rlHeader = (RelativeLayout) getActivity().findViewById(R.id.header); 
		rlHeader.setVisibility(View.GONE);
		super.onResume();
	}

	//set the title bar by changing notification image with Edit text
	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.strSelectFriend));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
				0, //top
				0, //right
				0//bottom
				);

		ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		ivNotification.setVisibility(View.GONE);
		ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
		ivSearch.setVisibility(View.GONE);
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setText(getString(R.string.ok));
		tvEdit.setVisibility(View.VISIBLE);

	}
}
