package com.linchpin.clubcrawl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.adapters.AllVenuesAdapter;
import com.linchpin.clubcrawl.beans.BeanClubsInfo;
import com.linchpin.clubcrawl.beans.BeanListClubs;
import com.linchpin.clubcrawl.beans.BeanListProfilePics;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.helper.Utils;

public class ComposePostScreen extends Activity implements Result,TextWatcher, OnClickListener
{
	BeanResponse responseBeanPost,responseBeanUploadPic;
	private BeanListProfilePics	responseBean;
	private int					RESULT_LOAD_IMAGE	= 1, REQUEST_TAKE_PHOTO = 2;
	private String				picturePath			= "";
	private String		clubId;
	BeanListClubs		beanListClubs;
	protected final int	CLUB_SERCH_LIST	= 0x00002,SEARCH_LIST=1;
	AllVenuesAdapter adapter;
	TextView etSearch;
	ListView			lvClubsList;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_post);

		setProfileValues();

		TextView btnPost = (TextView) findViewById(R.id.btnPost);
		TextView ivCross = (TextView) findViewById(R.id.ivCross);
		final EditText etPost = (EditText) findViewById(R.id.etUpdateStatus);
		final LinearLayout llVenues=(LinearLayout) findViewById(R.id.llVenues);
		etSearch=(TextView) findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(this);
		lvClubsList=(ListView) findViewById(R.id.lvVenues);
		beanListClubs=new BeanListClubs();
		lvClubsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(etPost.getVisibility()==View.GONE)
				{

					//BeanListClubs clubsList=new BeanListClubs();
					BeanClubsInfo clubsInfo=beanListClubs.getResult().get(position);
					etPost.setVisibility(View.VISIBLE);
					etPost.setText(MenuFragment.beanUserInfo.getUser().getUsername()+" is going to "+clubsInfo.getName());
					llVenues.setVisibility(View.GONE);
					TextView tvFindVenues=(TextView) findViewById(R.id.tvFindVenues);
					tvFindVenues.setVisibility(View.GONE);
					ImageView ivProfilePic = (ImageView) findViewById(R.id.ivAddedImage);
					ivProfilePic.setVisibility(View.VISIBLE);
				}




			}
		});




		ivCross.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();

			}
		});
		btnPost.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				EditText etStatusPost=(EditText) findViewById(R.id.etUpdateStatus);
				ImageView ivImageView=(ImageView) findViewById(R.id.ivAddedImage);
				if(etStatusPost.getText().toString().length()!=0&&ivImageView.getDrawable()!=null)
				{
					if (ConstantUtil.isNetworkAvailable(ComposePostScreen.this))
					{
						String status=etStatusPost.getText().toString();
						status=status.replace(" ", "%20");
						new UploadPicTask(ComposePostScreen.this, picturePath,status).execute();
					}
					else
					{
						Toast.makeText(ComposePostScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
					}
				}
				else if(ivImageView.getDrawable()!=null)
				{

					if (picturePath != null && !picturePath.equals(""))
					{
						if (ConstantUtil.isNetworkAvailable(ComposePostScreen.this))
						{
							new UploadPicTask(ComposePostScreen.this, picturePath,"").execute();
						}
						else
						{
							Toast.makeText(ComposePostScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
						}
					}


				}
				else if(etStatusPost.getText().toString().length()!=0)
				{
					new PostStatusTask(ComposePostScreen.this).execute();	

				}
				else if (etPost.getText().toString().equals("")&&ivImageView.getDrawable()==null)
				{
					Toast.makeText(ComposePostScreen.this, getString(R.string.strBlankStatuserr), Toast.LENGTH_LONG).show();
				}


		

			}
		});
		final Button btnGoingSomewhereView = (Button) findViewById(R.id.btnGoingSomewere);
		final Button btnImage = (Button) findViewById(R.id.btnImage);
		btnImage.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				//View ivGoingSomewhereView = (View) findViewById(R.id.viewGoingSomewhere);
				//ivGoingSomewhereView.setVisibility(View.GONE);
				llVenues.setVisibility(View.GONE);
				btnImage.setTextColor(getResources().getColor(R.color.background_color));
				btnGoingSomewhereView.setTextColor(getResources().getColor(android.R.color.black));
				EditText etPost = (EditText) findViewById(R.id.etUpdateStatus);
				etPost.setVisibility(View.VISIBLE);
				ImageView ivProfilePic = (ImageView) findViewById(R.id.ivAddedImage);
				ivProfilePic.setVisibility(View.VISIBLE);
				View viewImage = (View) findViewById(R.id.viewImage);
				viewImage.setVisibility(View.VISIBLE);
				PhotoCapture photoCap = new PhotoCapture(ComposePostScreen.this);
				photoCap.galleryCameraDialog();
				TextView tvFindVenues=(TextView) findViewById(R.id.tvFindVenues);
				tvFindVenues.setVisibility(View.GONE);
			}
		});
		btnGoingSomewhereView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				//View ivGoingSomewhereView = (View) findViewById(R.id.viewGoingSomewhere);
				//ivGoingSomewhereView.setVisibility(View.VISIBLE);
				btnImage.setTextColor(getResources().getColor(android.R.color.black));
				btnGoingSomewhereView.setTextColor(getResources().getColor(R.color.background_color));
				TextView tvFindVenues=(TextView) findViewById(R.id.tvFindVenues);
				tvFindVenues.setVisibility(View.VISIBLE);
				llVenues.setVisibility(View.VISIBLE);
				EditText etPost = (EditText) findViewById(R.id.etUpdateStatus);
				etPost.setVisibility(View.GONE);
				ImageView ivProfilePic = (ImageView) findViewById(R.id.ivAddedImage);
				ivProfilePic.setVisibility(View.GONE);
				View viewImage = (View) findViewById(R.id.viewImage);
				viewImage.setVisibility(View.GONE);
				getClubSearchList();

			}
			private void getClubSearchList()
			{
				NetworkTask networkTask = new NetworkTask(ComposePostScreen.this, CLUB_SERCH_LIST);
				networkTask.exposePostExecute(ComposePostScreen.this);
				networkTask.setProgressDialog(true);
				networkTask.execute(ConstantUtil.getClubSearchList + "st=&catid=" + 3 + "&location=" + "1&user_id=" + AppPreferences.getInstance(getApplicationContext()).getUserId());
			}

		});


		final TextView tv160 = (TextView) findViewById(R.id.tv160);
		etPost.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				tv160.setText(String.valueOf(160-s.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {

				// TODO Auto-generated method stub
			}
		});
	}

	private void setProfileValues()
	{
		setProfileImage(R.id.ivProfilePic);
		TextView tvProfileName = (TextView) findViewById(R.id.tvProfileName);
		tvProfileName.setText(MenuFragment.beanUserInfo.getUser().getFirst_name() + " " + MenuFragment.beanUserInfo.getUser().getLast_name());
		TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvUserName.setText("@"+MenuFragment.beanUserInfo.getUser().getUsername());
		View ivGoingSomewhereView = (View) findViewById(R.id.viewGoingSomewhere);
		ivGoingSomewhereView.setVisibility(View.VISIBLE);

		View viewImage = (View) findViewById(R.id.viewImage);
		viewImage.setVisibility(View.GONE);
		//		View ivGoingSomewhereView = (View) findViewById(R.id.viewGoingSomewhere);
		//		ivGoingSomewhereView.setVisibility(View.VISIBLE);
		EditText etPost = (EditText) findViewById(R.id.etUpdateStatus);
		etPost.setVisibility(View.VISIBLE);
		ImageView ivProfilePic = (ImageView) findViewById(R.id.ivAddedImage);
		ivProfilePic.setVisibility(View.GONE);
		//		View viewImage = (View) findViewById(R.id.viewImage);
		//		viewImage.setVisibility(View.GONE);


	}

	//set Profile Image using aQuerD
	public void setProfileImage(int id)
	{
		ImageView ivProfileImage = (ImageView) findViewById(id);
		AQuery aq = new AQuery(ivProfileImage);

		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;
			aq.id(id).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getProfile_pic(), options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			ivProfileImage.setImageBitmap(bm);
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
			EditText etPost = (EditText) findViewById(R.id.etUpdateStatus);
			AppPreferences appPref = new AppPreferences(con);
			url = ConstantUtil.postStatusUrl + "user_id=" + appPref.getUserId() + "&status=" + etPost.getText().toString();
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
			try{
			if (pd.isShowing())
			{
				pd.dismiss();
			}
			if ((responseBeanPost == null))

			{

				Toast.makeText(con, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
			}

			else if (responseBeanPost.getStatus().equals(ConstantUtil.STATUS_SUCCESS))
			{
				Toast.makeText(con,"Successfully posted", Toast.LENGTH_LONG).show();
				finish();

			}
			else Toast.makeText(con, responseBeanPost.getStatus(), Toast.LENGTH_LONG).show();

		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
		}

	}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode ==RESULT_OK)
		{
			if (requestCode == RESULT_LOAD_IMAGE && null != data)
			{
				Uri selectedImage = data.getData();
				String[] filePathColumn =
					{ MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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
				Bitmap myBitmap = BitmapFactory.decodeFile(picturePath);
				ImageView ivProfilePic = (ImageView) findViewById(R.id.ivAddedImage);
				ivProfilePic.setImageBitmap(myBitmap);
				//									if (ConstantUtil.isNetworkAvailable(ComposePostScreen.this))
				//									{
				//										new UploadPicTask(ComposePostScreen.this, picturePath).execute();
				//									}
				//									else
				//									{
				//										Toast.makeText(ComposePostScreen.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
				//									}
			}
		}
	}


	//Task for deleting a profile pic and get refreshed pics
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
				Toast.makeText(con,"Successfully posted", Toast.LENGTH_LONG).show();
				finish();
			}
			}
			catch(Exception e)
			{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();	
			}
			super.onPostExecute(result);

		}

	}

	@Override
	public void resultfromNetwork(String result, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();

		switch (id) {
		case CLUB_SERCH_LIST:
			if (result != null && !result.equals(""))
			{

				try {

					JSONArray array = null;
					JSONObject jsonObject = new JSONObject(result);
					array = jsonObject.optJSONArray("result");

					List<BeanClubsInfo> resultList=new ArrayList<BeanClubsInfo>();
					for(int i=0;i<array.length();i++)
					{
						String obj = array.getString(i);
						BeanClubsInfo beanClubsInfo = gson.fromJson(obj,
								BeanClubsInfo.class);
						resultList.add(beanClubsInfo);
					}

					beanListClubs.setResult(resultList);

					adapter = new AllVenuesAdapter(getApplicationContext(), beanListClubs);
					lvClubsList.setAdapter(adapter);

					/*if ((beanListClubs == null)) Toast.makeText(getApplicationContext(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				else
				{

				}*/

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();
			}

			break;
		case SEARCH_LIST:
			if (result != null && !result.equals(""))
			{


				try {

					JSONArray array = null;
					JSONObject jsonObject = new JSONObject(result);
					array = jsonObject.optJSONArray("result");

					List<BeanClubsInfo> resultList=new ArrayList<BeanClubsInfo>();
					for(int i=0;i<array.length();i++)
					{
						String obj = array.getString(i);
						BeanClubsInfo beanClubsInfo = gson.fromJson(obj,
								BeanClubsInfo.class);
						resultList.add(beanClubsInfo);
					}

					beanListClubs.setResult(resultList);

					adapter = new AllVenuesAdapter(getApplicationContext(), beanListClubs);
					lvClubsList.setAdapter(adapter);



				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			else  
				Toast.makeText(ComposePostScreen.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();

			break;

		default:
			break;
		}


	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		int textlength = etSearch.getText().length();
		List<BeanClubsInfo> listClubInfo = new ArrayList<BeanClubsInfo>();

		listClubInfo.clear();
		if (textlength == 0)
		{



			adapter = new AllVenuesAdapter(getApplicationContext(), beanListClubs);
			lvClubsList.setAdapter(adapter);









		}
		else
		{
			if (ConstantUtil.isNetworkAvailable(ComposePostScreen.this))
			{
				NetworkTask networkTask = new NetworkTask(ComposePostScreen.this, SEARCH_LIST);
				networkTask.exposePostExecute(ComposePostScreen.this);

				networkTask.execute(ConstantUtil.getClubSearchList + "st="+etSearch.getText().toString()+"&catid=" + 3 + "&location=" + "1&user_id=" + AppPreferences.getInstance(ComposePostScreen.this).getUserId());
			}
		}




	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}



	// upload pic to server
		public static BeanResponse uploadFile(String sourceFileUri, Context con,String status) {
			int serverResponseCode = 0;
			BeanResponse responseBean = null;
			AppPreferences appPref = new AppPreferences(con);
			String upLoadServerUri = ConstantUtil.postStatusUrl + "user_id=" + appPref.getUserId()+"&status="+status;;
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
				conn.setRequestProperty("my_profile_picture", fileName);
				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"my_profile_picture\";filename=\""
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
