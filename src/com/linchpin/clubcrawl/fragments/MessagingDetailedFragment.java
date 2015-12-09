package com.linchpin.clubcrawl.fragments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.PhotoCapture;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.SwipeDismissListViewTouchListener;
import com.linchpin.clubcrawl.adapters.MessengerDetailAdapter;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.helper.Utils;
import com.linchpin.clubcrawl.jsonobject.message.Mainmsgdetail;
import com.linchpin.clubcrawl.jsonobject.message.MessageModel;

public class MessagingDetailedFragment extends ParentFragment implements TextWatcher, Result
{

	BeanResponse responseBeanPost,responseBeanUploadPic;
	private PullAndLoadListView				messegingList;
	ArrayList<Mainmsgdetail>		messagedata;
	private MessengerDetailAdapter	adapter;
	String							string, conversationId;
	static String  sender,recever;
	TextView						tvSend,tvActiveDate;
	private int deleteMessage=0;
	private int					RESULT_LOAD_IMAGE	= 1, REQUEST_TAKE_PHOTO = 2;
	EditText						etMessage;
	private EditText edSearch;
	private String				picturePath			= "";
	private final int MESSAGES=1;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		view=inflater.inflate(R.layout.fragment_chatting, container,false);
		tvSend = (TextView) view.findViewById(R.id.tvSend);
		etMessage = (EditText) view.findViewById(R.id.etMessage);
		tvActiveDate=(TextView) view.findViewById(R.id.tvActivedate);
		//getActivity().findViewById(R.id.llHeader).setVisibility(View.GONE);
		
		messegingList = (PullAndLoadListView) view.findViewById(R.id.lvchatList);
		ImageView ivuserImage=(ImageView) view.findViewById(R.id.ivuserImage);
		AQuery aq=new AQuery(ivuserImage);
		aq.id(ivuserImage.getId()).image(ConstantUtil.profilePicsUrl+getArguments().getString("profile_image"));

		TextView tvBack=(TextView) view.findViewById(R.id.tvBack);
		tvBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack() ;
			}
		});


		ImageView ivImgMsg=(ImageView) view.findViewById(R.id.ivImgMsg);
		ivImgMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PhotoCapture photoCap = new PhotoCapture(MessagingDetailedFragment.this);
				photoCap.galleryCameraDialog();
				//openGallery();
			}
		});





		((TextView) view.findViewById(R.id.tvuserName)).setText(getArguments().getString("name"));

		messagedata = new ArrayList<Mainmsgdetail>();
		adapter = new MessengerDetailAdapter(getActivity(), messagedata);
		messegingList.setAdapter(adapter);
		NetworkTask networkTask = new NetworkTask(getActivity(), 1000);
		networkTask.setProgressDialog(true);
		networkTask.exposePostExecute(this);
		
		conversationId = getArguments().getString("message_id");
		String url=ConstantUtil.baseUrl + "/api/getmessagedetail/?messageid=" + conversationId;
		networkTask.execute(ConstantUtil.baseUrl + "/api/getmessagedetail/?messageid=" + conversationId);

		SwipeDismissListViewTouchListener touchListener =
				new SwipeDismissListViewTouchListener(
						messegingList,
						new SwipeDismissListViewTouchListener.OnDismissCallback() {
							@Override
							public void onDismiss(ListView listView, int[] reverseSortedPositions) {

								for (int position : reverseSortedPositions) {
									final Mainmsgdetail msg=messagedata.get(position-1);
									AppPreferences appPref = new AppPreferences(getActivity());
									if(msg.getSender_id().toString().equals(appPref.getUserId()))
									{
										deleteMessage=position-1;
										NetworkTask networkTask=new NetworkTask(getActivity(), 1);
										networkTask.setProgressDialog(true, "Requesting");
										networkTask.exposePostExecute(MessagingDetailedFragment.this);
										networkTask.execute(ConstantUtil.deletingSingleChatUrl+"id="+msg.getId().toString());
									}

								}

								adapter.notifyDataSetChanged();
							}
						});

		
		
		
		messegingList.setOnTouchListener(touchListener);
		messegingList.setOnScrollListener(touchListener.makeScrollListener());
		
		
		
		messegingList.setOnRefreshListener(new OnRefreshListener()
		{

			@Override
			public void onRefresh()
			{
				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					NetworkTask networkTask = new NetworkTask(getActivity(), 1000);
					networkTask.setProgressDialog(true);
					networkTask.exposePostExecute(MessagingDetailedFragment.this);
					conversationId = getArguments().getString("message_id");
					String url=ConstantUtil.baseUrl + "/api/getmessagedetail/?messageid=" + conversationId;
					networkTask.execute(ConstantUtil.baseUrl + "/api/getmessagedetail/?messageid=" + conversationId);
					messegingList.onRefreshComplete();
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();}
				    messegingList.onRefreshComplete();
			}
		});

		
		
		tvSend.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{

				
				//api/sendmessage?message=<message>&sender_id=<sender_id>&receiver_id=<receiver_id>
				int count =messagedata.size();
				NetworkTask networkTask = new NetworkTask(getActivity(), 1001,count,"");
				networkTask.setProgressDialog(false);
				networkTask.exposePostExecute(MessagingDetailedFragment.this);
				String[] ids = conversationId.split("_");
				 sender=(ids[0].equals(AppPreferences.getInstance(getActivity()).getUserId())?ids[0]:ids[1]);
				 recever=(ids[0].equals(AppPreferences.getInstance(getActivity()).getUserId())?ids[1]:ids[0]);
				String url=ConstantUtil.sendMessageUrl+"sender_id="+sender+"&receiver_id="+recever+"&message="+etMessage.getText().toString().trim();
				networkTask.execute(ConstantUtil.sendMessageUrl+"sender_id="+sender+"&receiver_id="+recever+"&message="+etMessage.getText().toString().trim());

				Mainmsgdetail result = new Mainmsgdetail();
				result.setMessage(etMessage.getText().toString().trim());
				result.setReceiver_id(recever);
				result.setSender_id(sender);
				result.setStatus(false);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result.setCreated_at(sdf.format(new Date()));
				adapter.addItem(result);
				etMessage.setText("");
				
			}
		});
		return view;
	}




	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		adapter.filter(s + "");
	}
	public String getCalender(String timestamp)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = getDate(sdf.parse(timestamp)).getTime();
			int day = date.compareTo(getDate(new Date()).getTime());
			if (day == 0) return "Today";
			else if (day < 0)
			{
				Calendar calendar = getDate(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				day = date.compareTo(calendar.getTime());
				if (day == 0) return "Yesterday";

			}
			sdf = new SimpleDateFormat("dd MMM");
			return sdf.format(date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return "";
	}
	private Calendar getDate(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

	@Override
	public void onResume() {
	//	getActivity().findViewById(R.id.ivSearch).setOnClickListener(this);
	//	getActivity().findViewById(R.id.btCancel).setOnClickListener(this);
		if (getActivity() instanceof MainScreen)
		{
			String name;
			edSearch = (EditText) getActivity().findViewById(R.id.edSearch);
			edSearch.setText("");
			//edSearch.addTextChangedListener(this);
			
			if(getArguments().getString("activetime")!=null)
				 name=getArguments().getString("name")+"\n"+"last online at "+getCalender(getArguments().getString("activetime"));

			else
				 name=getArguments().getString("name")+"\n";

			
			SpannableString spannablecontent=new SpannableString(name);
			spannablecontent.setSpan(new RelativeSizeSpan(0.5f), name.indexOf("\n"), name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setText(spannablecontent);
			((TextView) getActivity().findViewById(R.id.tvLocation)).setCompoundDrawables(null, null, null, null);
			ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivNotification.setVisibility(View.VISIBLE);
			ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
			ivSearch.setVisibility(View.GONE);
			ImageView ivCompose = (ImageView) getActivity().findViewById(R.id.ivComposePost);
			ivCompose.setVisibility(View.GONE);
			TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
			tvEdit.setText(getString(R.string.strEdit));
			tvEdit.setVisibility(View.GONE);
			ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
			ivMenu.setImageResource(R.drawable.pre_btn);
			ivMenu.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getActivity().getSupportFragmentManager().popBackStack();
				}
			});


		}
		super.onResume();
	}

	@Override
	public void onPause() {
		//getActivity().findViewById(R.id.ivSearch).setOnClickListener(null);
		//getActivity().findViewById(R.id.btCancel).setOnClickListener(null);
		getActivity().findViewById(R.id.ivSearch).setOnClickListener((MainScreen)getActivity());
		getActivity().findViewById(R.id.btCancel).setOnClickListener((MainScreen)getActivity());
	//	((TextView) ((MainScreen) getActivity()).findViewById(R.id.edSearch)).removeTextChangedListener(this);
		getActivity().findViewById(R.id.normal_pan).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.search_pan).setVisibility(View.GONE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen)getActivity());
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode ==getActivity().RESULT_OK)
		{
			if (requestCode ==RESULT_LOAD_IMAGE && null != data)
			{
				Uri selectedImage = data.getData();
				String[] filePathColumn =
					{ MediaStore.Images.Media.DATA };

				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();

			}
			else if (requestCode == REQUEST_TAKE_PHOTO && data != null)
			{
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				picturePath = Environment.getExternalStorageDirectory() + File.separator + "image.jpg";
				File file = new File(picturePath);
				try
				{
					file.createNewFile();
					FileOutputStream fo = new FileOutputStream(file);
					fo.write(bytes.toByteArray());
					fo.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (picturePath != null && !picturePath.equals(""))
			{
				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					new UploadPicTask(getActivity(), picturePath,"").execute();
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
			}
		}
	}



	@Override
	public void resultfromNetwork(String result, int id,final int arg1,String arg2)
	{
		Gson gson = new Gson();
		switch (id)
		{
		case 1000:
			if (result != null && !result.equals(""))
			{
				messegingList.onRefreshComplete();
				MessageModel inbox = gson.fromJson(result, MessageModel.class);
				messagedata = inbox.getMainmsgdetail();
				adapter = new MessengerDetailAdapter(getActivity(), messagedata);
				messegingList.setAdapter(adapter);
				NetworkTask networkTask = new NetworkTask(getActivity(), MESSAGES);
				networkTask.setProgressDialog(false);
				String url = ConstantUtil.changeNotificationUrl+"id="+conversationId+"&type=m";
				networkTask.execute(url);
				messegingList.onRefreshComplete();
				messegingList.postDelayed(new Runnable() {
					@Override
					public void run() {
						messegingList.setSelection(messagedata.size());
					}
				}, 500);

			}
			break;
		case 1001:
			if (result != null && !result.equals(""))
			{
				try
				{
					JSONObject jsonObject=new JSONObject(result);
					if(jsonObject.getString("status").equals("1"))
					{
						messegingList.postDelayed(new Runnable() {
							@Override
							public void run() {
								((Mainmsgdetail)(adapter).getItem(arg1)).setStatus(true);
								messegingList.setSelection(arg1);
								adapter.notifyDataSetChanged();

							}
						}, 100);

					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		case 1:
			try{
				JSONObject jsonObject = new JSONObject(result);
				if (result != null && !result.equals(""))
				{
					String status=jsonObject.optString("status").toString();
					if(status.equals("success"))
					{
						messagedata.remove(deleteMessage);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(getActivity(), ""+jsonObject.optString("status").toString(), Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					Toast.makeText(getActivity(), getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		default:
			break;
		}
	}


	public class UploadPicTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;
		private String	picId;
		private String	messages	= "";
		private String			filePath	= "";
		private String status="";

		public UploadPicTask(Context con, String filePath,String status)
		{
			this.con = con;
			this.picId = picId;
			this.filePath = filePath;
			this.status = status;
		}

		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			responseBeanUploadPic = uploadFile(filePath, con,status);

			if (responseBeanUploadPic != null && responseBeanUploadPic.getStatus().equals("200"))
			{

			}
			else
			{

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			try{
				if (pd.isShowing())
				{
					pd.dismiss();
				}
				if (responseBeanUploadPic == null) Toast.makeText(con, "Image could not be uploaded.", Toast.LENGTH_LONG).show();
				else
				{
					adapter.notifyDataSetChanged();
				
					//Toast.makeText(con,"Successfully posted", Toast.LENGTH_LONG).show();
					//getActivity().finish();
				}
			}
			catch(Exception e)
			{
				Toast.makeText(getActivity(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();	
			}
			super.onPostExecute(result);

		}

	}

	// upload pic to server
	public static BeanResponse uploadFile(String sourceFileUri, Context con,String status) {
		int serverResponseCode = 0;
		BeanResponse responseBean = null;
		AppPreferences appPref = new AppPreferences(con);
		
		String upLoadServerUri = ConstantUtil.sendMessageUrl + "receiver_id="+recever+"&sender_id="+sender+"&message="+"";
		String fileName = sourceFileUri;
		Bitmap bmp = Utils.decodeSampledBitmapFromResource(con.getResources(), sourceFileUri, 100, 100);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 10,os );
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024; 
		File sourceFile = new File(sourceFileUri);
		if (!sourceFile.isFile()) {
			return null;
		}
		try { // open a URL connection to the Servlet
			ByteArrayInputStream fileInputStream = new ByteArrayInputStream(os.toByteArray());
			URL url = new URL(upLoadServerUri);
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
			// connection to
			// the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("chat_image", fileName);
			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"chat_image\";filename=\""
					+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available(); // create a buffer of
			// maximum size

			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();

			responseBean = new BeanResponse();
			responseBean.setStatus(String.valueOf(serverResponseCode));
			responseBean.setMessage(serverResponseMessage);
			// close the streams //
			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBean;
	}




}
