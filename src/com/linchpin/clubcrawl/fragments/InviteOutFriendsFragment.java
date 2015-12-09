package com.linchpin.clubcrawl.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanError;
import com.linchpin.clubcrawl.beans.BeanListVenueInfo;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.MyTextView;

public class InviteOutFriendsFragment extends ParentFragment implements
DatePickerDialog.OnDateSetListener {

	private View view, footerView;
	private int countInvitedPeople;
	private String strInvitedPeopleIds;
	private String strDateTime;
	private Calendar c = Calendar.getInstance();
	private int startYear = c.get(Calendar.YEAR);
	private int startMonth = c.get(Calendar.MONTH);
	private int startDay = c.get(Calendar.DAY_OF_MONTH);
	private int startHour = c.get(Calendar.HOUR_OF_DAY);
	private int startMin = c.get(Calendar.MINUTE);
	private GetVenueList getVenueListTask;
	private int venueId = 0;
	private List<String> venueList;
	private BeanListVenueInfo beanListVenueInfo;
	private BeanResponse responseBean;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		MenuFragment.currentsel = "InviteOutFriends";
		view = inflater.inflate(R.layout.fragment_invite_friends, container,
				false);
		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.layout_footer_view, null, false);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		setTitleBar();
		setInvitedPeoples();
		changeVenueList();

		TextView tvSetDateTime = (TextView) view.findViewById(R.id.tvDateTime);
		tvSetDateTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog dialog = new DatePickerDialog(getActivity(),
						InviteOutFriendsFragment.this, startYear, startMonth,
						startDay);
				dialog.show();
			}
		});
		
		final Button btnMakePrivate = (Button) view.findViewById(R.id.btnMakePrivate);

		btnMakePrivate.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v)
			{
				if(btnMakePrivate.getTag().toString().equals("0"))
				{
					btnMakePrivate.setBackgroundResource(R.drawable.on_btn);
					btnMakePrivate.setTag("1");
				}
				else
				{
					btnMakePrivate.setBackgroundResource(R.drawable.off_btn);
					btnMakePrivate.setTag("0");
				}
				
			}
		});
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);

		tvEdit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {

				String alertString = validateData();
				if(!alertString.equals(""))
					showAlertForIncompleteField(alertString,0);
				else
				{
					if (ConstantUtil.isNetworkAvailable(getActivity())) {
						AppPreferences appPref = new AppPreferences(getActivity());
						new CreateNightTask(getActivity()).execute();
					} else
						Toast.makeText(getActivity(), getString(R.string.internetFailure),
								Toast.LENGTH_LONG).show();
				}
			}
		});

		MyTextView etVenueUpdate = (MyTextView)view.findViewById(R.id.etEventLocation);
		etVenueUpdate.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				String selection = (String) parent.getItemAtPosition(position);
				int pos = venueList.indexOf(selection);
				venueId = Integer.parseInt(beanListVenueInfo.getVenueList().get(pos).getId());



			}
		});

		TextView tvInvitePeopleFrndList = (TextView) view
				.findViewById(R.id.tvInvitePeople);
		tvInvitePeopleFrndList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InvitePeopleFriendList fr = new InvitePeopleFriendList();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.replace((R.id.frame), fr,"InvitePeopleFriendList");
				fragmentTransaction.addToBackStack("InvitePeopleFriendList");
				fragmentTransaction.commit();
				

			}
		});
		return view;
	}

	protected String validateData() 
	{
		String alertString = "";
		EditText etEventName = (EditText) view.findViewById(R.id.etEventName);
		EditText etEventDetail = (EditText) view.findViewById(R.id.etEventDetails);
		if(etEventName.getText().toString().equals(""))
			alertString = "Please enter event name";
		else if(etEventDetail.getText().toString().equals(""))
			alertString = "Please enter event details";
		else if(venueId == 0)
			alertString = "Please enter event location";
		
		else if(countInvitedPeople == 0)
			alertString = "Please Invite atleast one people";
		else if(strDateTime == null)
			alertString = "Please enter date of event";

		return alertString;




	}

	private void setInvitedPeople(){
		String invitedPeople = "0";
		TextView tvInvitedPeople = (TextView) view.findViewById(R.id.tvInvitePeople);
		tvInvitedPeople.setText(getResources().getString(R.string.invitePeople)+ "(" + invitedPeople + ")");
	}

	//set the title bar by changing notification image with Edit text
		public void setTitleBar()
		{

			TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
			tvTitle.setText(getString(R.string.strInviteFriends));
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
					tvEdit.setText(getString(R.string.strDone));
					tvEdit.setVisibility(View.VISIBLE);

		}

	@Override
	public void onDateSet(final DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		if(view.isShown()){
		final DecimalFormat mFormat= new DecimalFormat("00");
		mFormat.format(Double.valueOf(year));
		startYear = year;
		startMonth = monthOfYear+1;
		startDay = dayOfMonth;
		//updateStartDateDisplay();

		TimePickerDialog tpd = new TimePickerDialog(getActivity(),
				new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hour, int min) {
				// TODO Auto-generated method stub
				startHour = hour;
				startMin = min;
				TextView tvSetDateTime = (TextView) InviteOutFriendsFragment.this.view
						.findViewById(R.id.tvDateTime);
				strDateTime = startYear + "-" + mFormat.format(Double.valueOf(startMonth))
						+ "-" + mFormat.format(Double.valueOf(startDay)) + " " + mFormat.format(Double.valueOf(startHour)) + ":"
						+ mFormat.format(Double.valueOf(startMin));
				tvSetDateTime.setText(startYear + "-" + mFormat.format(Double.valueOf(startMonth))
						+ "-" + mFormat.format(Double.valueOf(startDay)) + " " + mFormat.format(Double.valueOf(startHour)) + ":"
						+ mFormat.format(Double.valueOf(startMin)));
			}
		}, startHour, startMin, false);
		tpd.show();
		}
	}

	//called from onBackStackChangeListner to set invited peoples selected from previous screen
	public void setInvitedPeoples()
	{
		System.out.println("entered in setInvitedPeoples");
		AppPreferences appPref = new AppPreferences(getActivity());
		countInvitedPeople = appPref.getInvitedFriendsNumber();
		strInvitedPeopleIds = appPref.getInvitedFriendsNames();
		appPref.setInvitedFriendsNames("");
		appPref.setInvitedFriendsNumber(0);
		TextView tvInvitePeopleFrndList = (TextView) view
				.findViewById(R.id.tvInvitePeople);
		if(countInvitedPeople == 0)
			tvInvitePeopleFrndList.setText("");
		else
		tvInvitePeopleFrndList.setText("Select friends("+countInvitedPeople+")");


	}

	//textChanged listener for edittext vanue location
	private void changeVenueList() 
	{
		MyTextView etVenueUpdate = (MyTextView)view.findViewById(R.id.etEventLocation);
		etVenueUpdate.setThreshold(1);
		etVenueUpdate.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(getVenueListTask != null && getVenueListTask.getStatus() == Status.RUNNING)
				{
					getVenueListTask.cancel(true);
				}
				if(!s.equals(""))
				{
					if(ConstantUtil.isNetworkAvailable(getActivity()))
					{
						getVenueListTask = new GetVenueList(getActivity(), String.valueOf(s));
						getVenueListTask.execute();
					}
				}

			} 

		});

	}

	//Task for getting venueList
	public class GetVenueList extends AsyncTask<Void, Void, Void>
	{
		Context con;
		String chars = "";

		public GetVenueList(Context con, String chars)
		{
			this.con = con;
			this.chars = chars;
		}


		@Override
		protected Void doInBackground(Void... params)
		{


			String url = "";
			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			url = ConstantUtil.getVenueList + "term="+chars+"&location=1";
			String responseString = ConstantUtil.http_connection(url);

			if(responseString != null && !responseString.equals(""))
			{
				BeanError beanError = gson.fromJson(responseString, BeanError.class);
				if(beanError.getError().equals("0"))
				{
					beanListVenueInfo = gson.fromJson(responseString, BeanListVenueInfo.class);
				}

			}

			return null;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) 
		{

			super.onPostExecute(result);
			//			Message myMessage = new Message(); 
			//			myMessage.obj = "getVenueList";
			//			myHandler.sendMessage(myMessage);
			if(beanListVenueInfo != null && beanListVenueInfo.getError().equals("0"))
			{
				venueList = new ArrayList<String>();
				for(int i=0;i<beanListVenueInfo.getVenueList().size();i++)
					venueList.add(beanListVenueInfo.getVenueList().get(i).getValue());
				MyTextView etVenueUpdate = (MyTextView)getActivity().findViewById(R.id.etEventLocation);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>  
				(getActivity(),R.layout.listview_color,R.id.tvAgeColor,venueList);  
				etVenueUpdate.setAdapter(adapter);
				etVenueUpdate.setThreshold(1);
				etVenueUpdate.setTextColor(Color.BLACK);
			}
		}

	}
	@SuppressWarnings("deprecation")
	private void showAlertForIncompleteField(String alertString,final int issuccess) {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.setMessage(alertString);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(issuccess==1)
				{
					//((MainScreen)getActivity()).onBackPressed();
					((MainScreen)getActivity()).resetBottonBar();
				((MainScreen)getActivity()).bottomBarItemSelected(R.id.llHome);
				((MainScreen)getActivity()).onSendMessage(new GalleryFragment(), null, "GalleryFragment");
				}
					
			}
		});
		alertDialog.show();
	}

	// Task for get FeedListInfo data
	public class CreateNightTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;

		String urlTotalNotification;

		public CreateNightTask(Context con) {
			this.con = con;

		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			AppPreferences appPref = new AppPreferences(getActivity());
			EditText etEventName = (EditText) view.findViewById(R.id.etEventName);

			EditText etEventDetail = (EditText) view.findViewById(R.id.etEventDetails);
			Gson gson = new Gson();
			 Button btnMakePrivate = (Button) view.findViewById(R.id.btnMakePrivate);
			String url = ConstantUtil.createNightUrl + 
					"e_name="+ etEventName.getText().toString()+
					"&e_details="+etEventDetail.getText().toString()+
					"&e_user="+appPref.getUserId()+
					"&e_venue="+venueId+
					"&e_date="+ strDateTime+
					"&e_people="+strInvitedPeopleIds+
					"&setting="+btnMakePrivate.getTag().toString()+
					"&cur_location="+appPref.getCityId();


			String responseString = ConstantUtil.http_connection(url);


			if (responseString != null && !responseString.equals(""))
			{
				responseBean = gson.fromJson(responseString, BeanResponse.class);

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			
				
							Message myMessage = new Message();
							myMessage.obj = "CreateNightOut";
							myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}
	private Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj.toString().equalsIgnoreCase("CreateNightOut")) {
				if(responseBean == null)
				{
					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				}
				else if(responseBean.getStatus().equals(ConstantUtil.STATUS_SUCCESS))
					showAlertForIncompleteField(responseBean.getMessage(),1);
				else if(responseBean.getStatus().equals(ConstantUtil.STATUS_FAILURE))
				{
					showAlertForIncompleteField(responseBean.getMessage(),0);
				}
				
			}

		}
	};
	
	public void onResume() 
	{
		super.onResume();
		System.out.println("inside onREsume");
	};



}
