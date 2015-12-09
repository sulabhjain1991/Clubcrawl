package com.linchpin.clubcrawl.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.DialogProfilePic;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.PhotoCapture;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanError;
import com.linchpin.clubcrawl.beans.BeanListProfilePics;
import com.linchpin.clubcrawl.beans.BeanListVenueInfo;
import com.linchpin.clubcrawl.beans.BeanProfilePics;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class MyProfilePicsFragments1 extends ParentFragment
{
	private View				view;
	private BeanListProfilePics	responseBean;
	private BeanListVenueInfo	beanListVenueInfo;
	private BeanResponse		responseBeanPost, responseBeanDelete, responseBeanUploadPic, responseBeanUpdateVenue;
	private int					RESULT_LOAD_IMAGE	= 1, REQUEST_TAKE_PHOTO = 2;
	private String				picturePath			= "";
	private GetVenueList		getVenueListTask;
	private int					venueId				= 0;
	private List<String>		venueList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		MenuFragment.currentsel = "MyProfilePicsFragments";
		view = inflater.inflate(R.layout.fragment_profile_pictures, container, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		//set the profile picture

		//set profile image is called on BackSackChangeListener of MainScreen
		//setProfileImage();
		setTitleBar();
		setWidthAndHeightOfImages();

		//textChanged listener for edittext vanue location
		changeVenueList();
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			new GetProfilePicsTask(getActivity()).execute();
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

		ImageView ivAdd = (ImageView) view.findViewById(R.id.ivAddPic);
		ivAdd.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				PhotoCapture photoCap = new PhotoCapture(MyProfilePicsFragments.this);
				photoCap.galleryCameraDialog();
			}
		});
		TextView btnPost = (TextView) view.findViewById(R.id.btnPost);
		btnPost.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				EditText etPost = (EditText) view.findViewById(R.id.etUpdateStatus);
				if (etPost.getText().toString().equals(""))
				{
					Toast.makeText(getActivity(), getString(R.string.strBlankStatuserr), Toast.LENGTH_LONG).show();
				}
				else if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					new PostStatusTask(getActivity()).execute();
				}
				else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

			}
		});

		TextView btnVenue = (TextView) view.findViewById(R.id.btnVenue);
		btnVenue.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				AutoCompleteTextView etVenue = (AutoCompleteTextView) view.findViewById(R.id.etVenue);
				if (venueId == 0)
				{
					Toast.makeText(getActivity(), getString(R.string.strValidVenueErros), Toast.LENGTH_LONG).show();
				}
				else if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					new UpdateVenueTask(getActivity()).execute();
				}
				else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

			}
		});

		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEdit);
		tvEdit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				EditProfileFragment fr = new EditProfileFragment();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add((R.id.frame), fr, "EditProfileScreen");
				fragmentTransaction.addToBackStack("EditProfileScreen");
				fragmentTransaction.commit();

			}
		});
		AutoCompleteTextView etVenueUpdate = (AutoCompleteTextView) view.findViewById(R.id.etVenue);
		etVenueUpdate.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String selection = (String) parent.getItemAtPosition(position);
				int pos = venueList.indexOf(selection);
				venueId = Integer.parseInt(beanListVenueInfo.getVenueList().get(pos).getId());

			}
		});
		return view;

	}

	//set Profile Image using aQuerD
	public void setProfileImage()
	{
		ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfilePic);
		AQuery aq = new AQuery(ivProfileImage);

		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;
			aq.id(R.id.ivProfilePic).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getProfile_pic(), options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			ivProfileImage.setImageBitmap(bm);
		}

	}

	public class GetProfilePicsTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;

		public GetProfilePicsTask(Context con)
		{
			this.con = con;
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
			String url = "";
			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			url = ConstantUtil.profilePicsUrl + "userid=" + appPref.getUserId();
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				responseBean = gson.fromJson(responseString, BeanListProfilePics.class);

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
			myMessage.obj = "GetProfilePicsTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	public class PostStatusTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;

		public PostStatusTask(Context con)
		{
			this.con = con;
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
			String url = "";
			Gson gson = new Gson();
			EditText etPost = (EditText) view.findViewById(R.id.etUpdateStatus);
			AppPreferences appPref = new AppPreferences(getActivity());
			url = ConstantUtil.postStatusUrl + "userid=" + appPref.getUserId() + "&post=" + etPost.getText().toString();
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				responseBeanPost = gson.fromJson(responseString, BeanResponse.class);

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
			myMessage.obj = "PostStatusTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	//Task for deleting a profile pic and get refreshed pics
	public class DeletePicTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;
		private String	picId;
		private String	messages	= "";

		public DeletePicTask(Context con, String picId)
		{
			this.con = con;
			this.picId = picId;
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
			String url = "";
			Gson gson = new Gson();
			url = ConstantUtil.deletePicUrl + "id=" + picId;
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				responseBeanDelete = gson.fromJson(responseString, BeanResponse.class);
				if (responseBeanDelete.getStatus().equals("1"))
				{
					messages = "GetProfilePicsTask";

					AppPreferences appPref = new AppPreferences(getActivity());
					url = ConstantUtil.profilePicsUrl + "userid=" + appPref.getUserId();
					responseString = ConstantUtil.http_connection(url);

					if (responseString != null && !responseString.equals(""))
					{
						responseBean = gson.fromJson(responseString, BeanListProfilePics.class);

					}
				}
				else
				{
					messages = "DeletePicsTask";
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
			Message myMessage = new Message();
			myMessage.obj = messages;
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	//Task for deleting a profile pic and get refreshed pics
	public class UploadPicTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;
		private String	picId;
		private String	messages	= "";
		String			filePath	= "";

		public UploadPicTask(Context con, String filePath)
		{
			this.con = con;
			this.picId = picId;
			this.filePath = filePath;
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
			responseBeanUploadPic = ConstantUtil.uploadFile(filePath, getActivity());

			if (responseBeanUploadPic != null && responseBeanUploadPic.getStatus().equals("200"))
			{
				messages = "GetProfilePicsTask";

				AppPreferences appPref = new AppPreferences(getActivity());
				Gson gson = new Gson();
				String url = ConstantUtil.profilePicsUrl + "userid=" + appPref.getUserId();
				String responseString = ConstantUtil.http_connection(url);

				if (responseString != null && !responseString.equals(""))
				{
					responseBean = gson.fromJson(responseString, BeanListProfilePics.class);

				}
			}
			else
			{
				messages = "UploadPicsTask";
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
			myMessage.obj = messages;
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	//Task for getting venueList
	public class GetVenueList extends AsyncTask<Void, Void, Void>
	{
		Context	con;
		String	chars	= "";

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
			url = ConstantUtil.getVenueList + "term=" + chars + "&location=1";
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				BeanError beanError = gson.fromJson(responseString, BeanError.class);
				if (beanError.getError().equals("0"))
				{
					beanListVenueInfo = gson.fromJson(responseString, BeanListVenueInfo.class);
				}

			}

			return null;
		}

		@Override
		protected void onCancelled()
		{
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
			if (beanListVenueInfo != null && beanListVenueInfo.getError().equals("0"))
			{
				venueList = new ArrayList<String>();
				for (int i = 0; i < beanListVenueInfo.getVenueList().size(); i++)
					venueList.add(beanListVenueInfo.getVenueList().get(i).getValue());
				AutoCompleteTextView etVenueUpdate = (AutoCompleteTextView) getActivity().findViewById(R.id.etVenue);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_color, R.id.tvAgeColor, venueList);
				etVenueUpdate.setAdapter(adapter);
				etVenueUpdate.setThreshold(1);
				etVenueUpdate.setTextColor(Color.BLACK);
			}
		}

	}

	public class UpdateVenueTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	pd;
		Context			con;

		public UpdateVenueTask(Context con)
		{
			this.con = con;
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
			String url = "";
			Gson gson = new Gson();
			AutoCompleteTextView etVenue = (AutoCompleteTextView) view.findViewById(R.id.etVenue);
			AppPreferences appPref = new AppPreferences(getActivity());
			url = ConstantUtil.updateVenueUrl + "userid=" + appPref.getUserId() + "&venue=" + venueId + "&location=1";
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals(""))
			{
				responseBeanUpdateVenue = gson.fromJson(responseString, BeanResponse.class);

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
			myMessage.obj = "UpdateVenueTask";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}

	private Handler	myHandler	= new Handler()
								{

									public void handleMessage(Message msg)
									{

										if (msg.obj.toString().equalsIgnoreCase("GetProfilePicsTask"))
										{

											if ((responseBean == null))

											{

												Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
											}

											else if (responseBean.getStatus().equals("true"))
											{
												List<BeanProfilePics> beanProfilePics = responseBean.getResult();

												LinearLayout llPics = (LinearLayout) view.findViewById(R.id.llPics);
												if (llPics.getChildCount() > 0) llPics.removeAllViews();
												for (int i = 0; i < beanProfilePics.size(); i++)
												{
													LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
													View v = inflater.inflate(R.layout.custom_profile_pic_layout, null);
													llPics.addView(v);

													//set picture on each imageView
													setPictures(v, beanProfilePics.get(i).getImage(), i);

													//set On click listener on cross TextView
													setOnClickListenerOnCrossButton(v, beanProfilePics.get(i), i);

												}

												//						Toast.makeText(getActivity(), getString(R.string.strSuccessLogin), Toast.LENGTH_LONG).show();
												//						Intent i=new Intent(getActivity(),MainScreen.class);
												//						i.putExtra("loginInfo", beanLoginInfo.getUserdata());
												//						startActivity(i);
												//						finish();

											}

										}

										else if (msg.obj.toString().equalsIgnoreCase("PostStatusTask"))
										{

											if ((responseBeanPost == null))

											{

												Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
											}

											else if (responseBeanPost.getStatus().equals(ConstantUtil.STATUS_SUCCESS))
											{
												Toast.makeText(getActivity(), responseBeanPost.getMessage(), Toast.LENGTH_LONG).show();

											}
											else Toast.makeText(getActivity(), responseBeanPost.getMessage(), Toast.LENGTH_LONG).show();

										}

										else if (msg.obj.toString().equalsIgnoreCase("DeletePicsTask"))
										{

											if ((responseBeanDelete == null))

											{

												Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
											}

											else Toast.makeText(getActivity(), "Not a valid image to delete.", Toast.LENGTH_LONG).show();

										}
										else if (msg.obj.toString().equalsIgnoreCase("UploadPicsTask"))
										{

											if (responseBeanUploadPic == null) Toast.makeText(getActivity(), "Image could not be uploaded.", Toast.LENGTH_LONG).show();
											else Toast.makeText(getActivity(), responseBeanUploadPic.getMessage(), Toast.LENGTH_LONG).show();

										}
										else if (msg.obj.toString().equalsIgnoreCase("UpdateVenueTask"))
										{

											if ((responseBeanUpdateVenue == null))

											{

												Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
											}

											else if (responseBeanUpdateVenue.getStatus().equals("true"))
											{
												Toast.makeText(getActivity(), getString(R.string.strVenueUpdateSucc), Toast.LENGTH_LONG).show();

											}
											else Toast.makeText(getActivity(), "Venue can not be updated", Toast.LENGTH_LONG).show();

										}

									}
								};

	protected void setPictures(View v, final String url, int id)
	{
		final AQuery aq = new AQuery(view);
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth * 40 / 100, ConstantUtil.screenWidth * 40 / 100);

		RelativeLayout rl = (RelativeLayout) v;
		rl.setLayoutParams(params);
		ImageView ivPic = (ImageView) rl.getChildAt(0);
		ivPic.setId(id);
		ivPic.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), DialogProfilePic.class);
				i.putExtra("picUrl", ConstantUtil.profilePicBaseUrl + url);
				startActivity(i);

			}
		});

		Bitmap bm;
		try
		{
			ivPic.setScaleType(ScaleType.FIT_XY);

			ImageOptions options = new ImageOptions();

			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;
			options.targetWidth = ConstantUtil.screenWidth * 40 / 100;

			aq.id(ivPic.getId()).image(ConstantUtil.profilePicBaseUrl + url, options);

		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			ivPic.setImageBitmap(bm);
		}

	}

	//textChanged listener for edittext vanue location
	private void changeVenueList()
	{
		AutoCompleteTextView etVenueUpdate = (AutoCompleteTextView) view.findViewById(R.id.etVenue);
		etVenueUpdate.setThreshold(1);
		etVenueUpdate.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if (getVenueListTask != null && getVenueListTask.getStatus() == Status.RUNNING)
				{
					getVenueListTask.cancel(true);
				}
				if (!s.equals(""))
				{
					if (ConstantUtil.isNetworkAvailable(getActivity()))
					{
						getVenueListTask = new GetVenueList(getActivity(), String.valueOf(s));
						getVenueListTask.execute();
					}
				}

			}

		});

	}

	protected void setOnClickListenerOnCrossButton(View v, final BeanProfilePics beanProfilePics, int i)
	{
		RelativeLayout rl = (RelativeLayout) v;
		ImageView ivCross = (ImageView) rl.getChildAt(1);
		ivCross.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				if (ConstantUtil.isNetworkAvailable(getActivity()))
				{
					new DeletePicTask(getActivity(), beanProfilePics.getId()).execute();
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == getActivity().RESULT_OK)
		{
			if (requestCode == RESULT_LOAD_IMAGE && null != data)
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
					new UploadPicTask(getActivity(), picturePath).execute();
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	//set the width and height of all imageviews dynamically
	private void setWidthAndHeightOfImages()
	{
		ImageView ivAdd = (ImageView) view.findViewById(R.id.ivAddPic);
		ConstantUtil.getScreen_Height(getActivity());
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth * 40 / 100, ConstantUtil.screenWidth * 40 / 100);
		ivAdd.setLayoutParams(params);

		LinearLayout llPics = (LinearLayout) view.findViewById(R.id.llPics);
		LinearLayout.LayoutParams paramsLlPics = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ConstantUtil.screenWidth * 40 / 100);
		llPics.setLayoutParams(paramsLlPics);

	}

	//set the title bar by changing notification image with Edit text
	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.strMyProfile));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
				0, //top
				0, //right
				0//bottom
		);

		ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivNotifications);
		ivNotification.setVisibility(View.GONE);

		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEdit);
		tvEdit.setVisibility(View.VISIBLE);

	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		//	setProfileImage();

	}

}
