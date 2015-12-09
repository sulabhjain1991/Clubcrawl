package com.linchpin.clubcrawl.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.EventCommentListAdapter;
import com.linchpin.clubcrawl.beans.BeanEventDetailInfo;
import com.linchpin.clubcrawl.beans.BeanListEventComents;
import com.linchpin.clubcrawl.beans.BeanListEventDetail;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EventOverviewFragment extends ParentFragment implements Result
{

	private View					view;
	private String					e_id, currentBtn, status;
	private BeanEventDetailInfo		beanEventDetailInfo;
	private BeanListEventDetail		beanListEventDetail;
	private BeanListEventComents	beanListEventComents;
	private ImageLoader imageLoader;
	String ename;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		view = inflater.inflate(R.layout.event_detail_fragment, container, false);
		e_id = getArguments().getString("e_id");
		currentBtn = getArguments().getString("currentBtn");
		// set the font for whole class
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());

		setTitleBar();
		if (currentBtn.equalsIgnoreCase("Invites"))
		{
			LinearLayout llAcceptEventOptions = (LinearLayout) view.findViewById(R.id.llAcceptEvent);
			llAcceptEventOptions.setVisibility(View.VISIBLE);
		}

		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			new GetEventData(getActivity()).execute();
			if (ConstantUtil.isNetworkAvailable(getActivity()))
			{
				String url = ConstantUtil.nightsOutEventComment + e_id;
				new GetCommentList(getActivity(), url).execute();
			}
			
			if (ConstantUtil.isNetworkAvailable(getActivity()))
			{
				String url = ConstantUtil.eventCountsUrl+ e_id;
				NetworkTask task=new NetworkTask(getActivity(), 101);
				task.exposePostExecute(EventOverviewFragment.this);
				task.execute(url);
			}
			
			
			
			
			
			
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

		

		TextView btnPost = (TextView) view.findViewById(R.id.btnPost);
		btnPost.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				EditText etUpdateStatus = (EditText) view.findViewById(R.id.etUpdateStatus);
				if (!(etUpdateStatus.getText().toString().trim().equals("")))
				{
					String commentText = etUpdateStatus.getText().toString();
					new PostComment(getActivity(), commentText).execute();
					etUpdateStatus.setText(null);
				}
				else
				{
					Toast.makeText(getActivity(), getResources().getString(R.string.enterString), Toast.LENGTH_LONG).show();
				}
			}
		});

		TextView tvAccept = (TextView) view.findViewById(R.id.tvAccept);
		tvAccept.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				status = "1";
				new UpdateEvent(getActivity()).execute();
			}
		});

		TextView tvDecline = (TextView) view.findViewById(R.id.tvDecline);
		tvDecline.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				status = "0";
				new UpdateEvent(getActivity()).execute();
			}
		});

		TextView tvMaybe = (TextView) view.findViewById(R.id.tvMaybe);
		tvMaybe.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				status = "2";
				new UpdateEvent(getActivity()).execute();
			}
		});

		return view;
	}

	// BeanVenueFeedInfo Comment
	public class UpdateEvent extends AsyncTask<Void, Void, Void>
	{

		ProgressDialog	pd;
		Context			con;
		String			success	= "";

		public UpdateEvent(Context con)
		{
			this.con = con;
		}

		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(getActivity(), null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.updateEventInviteStatus + e_id + "&user=" + appPref.getUserId() + "&status=" + status;

			String responseString = ConstantUtil.http_connection(url);
			if (responseString != null)
			{
				try
				{
					JSONObject jsonObj = new JSONObject(responseString);
					if ((jsonObj.get("status")).equals("success"))
					{
						success = "success";
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
			if (success.equals("success"))
			{
				getActivity().getSupportFragmentManager().popBackStack();
			}
			else
			{
				Toast.makeText(getActivity(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
			}
		}

	}

	// Task for update Event
	public class PostComment extends AsyncTask<Void, Void, Void>
	{

		ProgressDialog	pd;
		Context			con;
		String			comment;
		String			status	= "false";

		public PostComment(Context con, String comment)
		{
			this.con = con;
			this.comment = comment;
		}

		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(getActivity(), null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			String url = ConstantUtil.postCommentInNightOut + e_id + "&comment=" + comment + "&user=" + appPref.getUserId();

			String responseString = ConstantUtil.http_connection(url);
			if (responseString != null)
			{

				try
				{
					JSONObject jsonObj = new JSONObject(responseString);
					if ((String.valueOf(jsonObj.get("status")).equals("true")))
					{
						status = "true";
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			if (status.equals("true"))
			{

				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					String url = ConstantUtil.nightsOutEventComment + e_id;
					new GetCommentList(getActivity(), url).execute();
				}
				else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getActivity(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
			}
		}

	}

	// Task for getting Comment data
	public class GetCommentList extends AsyncTask<Void, Void, Void>
	{

		ProgressDialog	pd;
		Context			con;
		String			url;

		public GetCommentList(Context con, String url)
		{
			this.con = con;
			this.url = url;
		}

		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(getActivity(), null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());

			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				beanListEventComents = gson.fromJson(responseString, BeanListEventComents.class);
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
			Message myMessage = new Message();
			myMessage.obj = "EventCommentsTask";
			myHandler1.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	private Handler	myHandler1	= new Handler()
								{

									public void handleMessage(Message msg)
									{

										if (msg.obj.toString().equalsIgnoreCase("EventCommentsTask"))
										{

											if ((beanListEventComents == null))
											{
												Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
											}
											else if (beanListEventComents.getList().size() == 0)
											{

											}

											else
											{
												EventCommentListAdapter adapter = new EventCommentListAdapter(getActivity(), beanListEventComents);
												ListView lvComments = (ListView) view.findViewById(R.id.lvComments);
												lvComments.setAdapter(adapter);
											}
										}

									}
								};

	public class GetEventData extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;
		private String	message;

		public GetEventData(Context con)
		{
			this.con = con;
		}

		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(getActivity(), null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			String url = "";
			Gson gson = new Gson();
			url = ConstantUtil.nightsOutEventDetail + e_id;
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				beanListEventDetail = gson.fromJson(responseString, BeanListEventDetail.class);
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
			Message myMessage = new Message();
			myMessage.obj = "EventDetailTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	private Handler	myHandler	= new Handler()
								{

									public void handleMessage(Message msg)
									{

										if (msg.obj.toString().equalsIgnoreCase("EventDetailTask"))
										{

											if ((beanListEventDetail == null))
											{
												Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
											}
											else if (beanListEventDetail.getEvent().size() == 0)
											{
												setVisibilityGone();
											}
											else
											{
												setEventInfo();
											}

										}

									}
								};

	protected void setVisibilityGone()
	{
		TextView tvEventName = (TextView) view.findViewById(R.id.tvEventName);
		LinearLayout llEventDetail = (LinearLayout) view.findViewById(R.id.llEventDetail);

		tvEventName.setVisibility(View.GONE);
		llEventDetail.setVisibility(View.GONE);
	}

	protected void setEventInfo()
	{
		TextView tvEventName = (TextView) view.findViewById(R.id.tvEventName);
		TextView tvVenue = (TextView) view.findViewById(R.id.tvVenue);
		TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
		TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
		tvEventName.setText(beanListEventDetail.getEvent().get(0).getEvent_name());
		 ename=beanListEventDetail.getEvent().get(0).getEvent_name();
			TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);

			tvTitle.setText(ename);
			tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
					0, //top
					0, //right
					0//bottom
			);

		tvVenue.setText(beanListEventDetail.getEvent().get(0).getVenue());
		tvDate.setText(ConstantUtil.changeDateFormat(beanListEventDetail.getEvent().get(0).getEvent_date(), "yyyy-MM-dd", "MMM dd") + "   "
				);
		tvTime.setText(ConstantUtil.changeDateFormat(beanListEventDetail.getEvent().get(0).getEvent_time(), "hh:mm:ss", "hh:mm a"));
		ImageView ivEventLogo=(ImageView) view.findViewById(R.id.ivEventLogo);
		
		AQuery aqPostImage = new AQuery(ivEventLogo);
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;
			options.fallback=R.drawable.no_img_jst_yet;
			Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl+beanListEventDetail.getEvent().get(0).getLogo());
			if(bmp!=null)
			{
				ivEventLogo.setImageBitmap(bmp);
			}
			else
				aqPostImage.id(ivEventLogo.getId()).image(ConstantUtil.profilePicBaseUrl + beanListEventDetail.getEvent().get(0).getLogo(), options);

		}
		catch(Exception e)
		{

		}
		
		
		
		
	}

	//set the title bar by changing notification image with Edit text
	private void setTitleBar()
	{

	
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

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		JSONObject jsonResponse;
		switch (id) {
		case 101:
			if(object!=null || object!="")
			{
				try {
					jsonResponse=new JSONObject(object);
					String strNotGoing=jsonResponse.optString("notGoing").toString();
					String strGoing=jsonResponse.optString("going").toString();
					String strMayBe=jsonResponse.optString("mayBe").toString();
					TextView tvGoing=(TextView) view.findViewById(R.id.tvGoing);
					TextView tvMayBe=(TextView) view.findViewById(R.id.tvMayBe);
					TextView tvNotGoing=(TextView) view.findViewById(R.id.tvNotGoing);
					tvGoing.setText("("+strGoing+")");
					tvNotGoing.setText("("+strNotGoing+")");
					tvMayBe.setText("("+strMayBe+")");
					
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getActivity(), "Error in API's", Toast.LENGTH_LONG).show();
				}
				
			}
			
			
			break;

		default:
			break;
		}
		
		
		
		
		
	}

	
}
