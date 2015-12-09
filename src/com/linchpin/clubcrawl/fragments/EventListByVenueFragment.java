package com.linchpin.clubcrawl.fragments;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.linchpin.clubcrawl.adapters.EventVenueAdapter;
import com.linchpin.clubcrawl.adapters.NotificationsListAdapter;
import com.linchpin.clubcrawl.beans.BeanListVenueEvent;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class EventListByVenueFragment extends ParentFragment {

	private View view;
	private BeanListVenueEvent beanListWhosVenue;
	private JSONObject jsonObject;
	ListView lvVenueList;

	private ImageView ivHeaderImage;
	private NotificationsListAdapter adapter;
	private View footerView;
	private String venueId;
	private String header_img;
	ImageLoader imageLoader;
	DisplayImageOptions			options;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MenuFragment.currentsel = "WhosOutVenuesFragment";
		view = inflater.inflate(R.layout.fragment_whos_out, container,
				false);
		ivHeaderImage=(ImageView) view.findViewById(R.id.ivHeaderImage);
		lvVenueList = (ListView) view
				.findViewById(R.id.lvFriendsList);
		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.layout_footer_view, null, false);
		LinearLayout llSwithFragment = (LinearLayout) view.findViewById(R.id.llSwitchFragmentButtons);
		llSwithFragment.setVisibility(View.GONE);
		venueId = getArguments().getString("venueId");
		header_img=getArguments().getString("header_img");
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.displayImage(ConstantUtil.headerImageUrl + header_img, ivHeaderImage, options, null);
		ivHeaderImage.setVisibility(View.VISIBLE);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		setTitleBar();



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
			String url = ConstantUtil.venueEventUrl + "venue="+venueId;

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



		ListView lvFriendsList = (ListView) view
				.findViewById(R.id.lvFriendsList);
		lvFriendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{

				Bundle b = new Bundle();


				b.putString("id", beanListWhosVenue.getEvents().get(arg2).getId()
						);

				activityCallback.onSendMessage(new FriendDetailFragment(), b,
						"FriendProfileFragment");

				// TODO Auto-generated method stub
				//				Bundle b = new Bundle();
				//				
				//				activityCallback.onSendMessage(new FriendProfileFragment(), b,
				//						"FriendProfileFragment");
			}
		});


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
				beanListWhosVenue= (BeanListVenueEvent)gson.fromJson(responseString,
						BeanListVenueEvent.class);

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
				} else {

					EventVenueAdapter adapter = new EventVenueAdapter(
							getActivity(), beanListWhosVenue,true);


					lvVenueList.setAdapter(adapter);

				}

			}

		}
	};


	//set the title bar by changing notification image with Edit text
	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.strWhosOut));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
				0, //top
				0, //right
				0//bottom
				);

		ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		ivNotification.setVisibility(View.GONE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.pre_btn);
		ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
		ivSearch.setVisibility(View.GONE);
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setText(getString(R.string.strEdit));
		tvEdit.setVisibility(View.GONE);
		ivMenu.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				getActivity().onBackPressed();

			}
		});

	}

	@Override
	public void onPause()
	{
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);

		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener(((MainScreen) getActivity()));
		super.onPause();
	}

}
