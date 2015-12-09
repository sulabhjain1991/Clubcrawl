package com.linchpin.clubcrawl.fragments;



import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.ViewMorePostsAdapter;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.beans.BeanUserPosts;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class ViewMorePostsFragment extends ParentFragment implements Result
{
	com.costum.android.widget.PullAndLoadListView lvPostsList;
	ViewMorePostsAdapter adapter;
	List<BeanUserPosts> listAllPosts;
	private boolean					refreshing;
	private boolean					loadingMore;
	private boolean					isProgressBar=true;
	private boolean isLoadFinished = false;
	int currentPage=1;
	final int MORE=1;
	private BeanUserInfo beanUserInfo;
	private String noOfPosts;
	public ViewMorePostsFragment() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		MenuFragment.currentsel = "ViewMorePostsFragment";
		View view=inflater.inflate(R.layout.fragment_view_more_posts, null);
		noOfPosts = getArguments().getString("posts");
		setTitleBar();
		lvPostsList=(com.costum.android.widget.PullAndLoadListView) view.findViewById(R.id.lvPostsList);
		listAllPosts=new ArrayList<BeanUserPosts>();
		listAllPosts.addAll(MenuFragment.beanUserInfo.getUser().getPost());
		adapter=new ViewMorePostsAdapter(getActivity(),MenuFragment.beanUserInfo.getUser().getPost());
		lvPostsList.setAdapter(adapter);

		lvPostsList.setOnLoadMoreListener(new OnLoadMoreListener() {

			public void onLoadMore() {


				// Do the work to load more items at the end of list
				// here
				if(!isLoadFinished)
				{
					loadingMore = true;
					isProgressBar = false;
					if (ConstantUtil
							.isNetworkAvailable(getActivity())) {
						currentPage = currentPage + 1;
						AppPreferences appPref = new AppPreferences(
								getActivity());
						String url = ConstantUtil.viewMorePostsUrl+"userID="+ appPref.getUserId() + "&curpage="+currentPage+"&perpage=8";
						new ViewPostTask(getActivity(), url).execute();

					} else
					{
						loadingMore =false;
						lvPostsList.onLoadMoreComplete();
						Toast.makeText(
								getActivity(),
								getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
					}
				}
				else
					lvPostsList.onLoadMoreComplete();
			}
		});


		lvPostsList.setOnRefreshListener(new OnRefreshListener()
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
					currentPage = 1;

					AppPreferences appPref = new AppPreferences(
							getActivity());
					String url = ConstantUtil.viewMorePostsUrl+"userID="+ appPref.getUserId() + "&curpage="+currentPage+"&perpage=8";
					new ViewPostTask(getActivity(), url).execute();

				}
				else
				{
					lvPostsList.onRefreshComplete();
					refreshing = false;
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}

			}
		});

		return view;
	}
	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.strMyPosts));
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
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (id) {
		case MORE:
			try
			{
				BeanUserInfo beanUserInfo = gson.fromJson(object, BeanUserInfo.class);
				listAllPosts.add((BeanUserPosts) MenuFragment.beanUserInfo.getUser().getPost());

				adapter=new ViewMorePostsAdapter(getActivity(),listAllPosts);
				lvPostsList.setAdapter(adapter);




			}
			catch (Exception e)
			{
				e.printStackTrace();
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
				beanUserInfo = gson.fromJson(responseString,
						BeanUserInfo.class);

			}
			//					




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
			lvPostsList.onLoadMoreComplete();
		}

	}

	private Handler myHandler1 = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("ViewMorePostTask")) {


				if ((beanUserInfo == null)) {
					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}

				else {
					if(listAllPosts != null && currentPage == 1)
						listAllPosts.clear();
					listAllPosts.addAll(beanUserInfo.getUser().getPost());
					if(listAllPosts.size()==0)
					{
						Toast.makeText(getActivity(), "No Post", Toast.LENGTH_LONG).show();
					}
					else{



						adapter.setList(listAllPosts);
						adapter.notifyDataSetChanged();
					}
					if (listAllPosts.size()==Integer.parseInt(noOfPosts)) {

						isLoadFinished=true;
					}
					//						lvFriendsList.setDividerHeight(0);

				}
				if (refreshing)
				{
					currentPage = 0;
					refreshing = false;
					lvPostsList.onRefreshComplete();
				}
				if (loadingMore)
				{
					lvPostsList.onLoadMoreComplete();
					loadingMore = false;

				}

			}


		}
	};


}
