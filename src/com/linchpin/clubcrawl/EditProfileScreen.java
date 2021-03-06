package com.linchpin.clubcrawl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.beans.BeanProfilePicNameId;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.fragments.ParentFragment;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class EditProfileScreen extends ParentFragment {

	private int RESULT_LOAD_IMAGE = 1, REQUEST_TAKE_PHOTO = 2;
	private BeanResponse beanResponse;
 
	private BeanUserInfo userInfoBean;
	private Boolean signUpProcessing = false;

	private String picturePath = "";
	private EditText firstName, lastName, age, phnNum,etWebsite,etStatusMsg;

	private TextView userName;
	private BeanProfilePicNameId profilePicName;
	private TextView tvGender;
	private Dialog dialog;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		view = inflater.inflate(R.layout.fragment_profile_edit, container,
				false);

		//set the font for whole class
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());

		//		//Invisible the Activity header
		//		RelativeLayout rlHeader = (RelativeLayout) EditProfileScreen.this.findViewById(R.id.header); 
		//		rlHeader.setVisibility(View.GONE);

		userInfoBean = MenuFragment.beanUserInfo;

		firstName = (EditText) view.findViewById(R.id.etEditFirstName);
		lastName = (EditText) view.findViewById(R.id.etEditLastName);
		userName = (TextView) view.findViewById(R.id.tvEditUserName);
		age = (EditText) view.findViewById(R.id.etEditAge);
		phnNum = (EditText) view.findViewById(R.id.etEditPhoneNo);
		tvGender= (TextView) view.findViewById(R.id.tvGender);
		etStatusMsg = (EditText) view.findViewById(R.id.etAboutYourself);
		etWebsite= (EditText) view.findViewById(R.id.etWebsite);
		setTitleBar();
		//		setWidthOfHeader();
		setProfileInfo();
		setProfileImage(); // Set profile image

		// Edit profile image
		RelativeLayout rlEditProfileImage = (RelativeLayout) view.findViewById(R.id.rlEditProfileImage);
		rlEditProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PhotoCapture photoCapture = new PhotoCapture(
						EditProfileScreen.this);
				photoCapture.galleryCameraDialog();
			}
		});
		final TextView tvSex = (TextView) view.findViewById(R.id.tvGender);
		tvSex.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) { 
			showOptionsDialog(); setSex(); } });


		//		ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
		//		ivBack.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				// TODO Auto-generated method stub
		//				EditProfileScreen.this.getSupportFragmentManager().popBackStack();
		//			}
		//		});

		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveEditedProfile();
			}
		});

		return view;

	}


	//set the profile info on activity start
	private void setProfileInfo() {
		TextView tvGender = (TextView) view.findViewById(R.id.tvGender);
		EditText etStatus = (EditText) view.findViewById(R.id.etAboutYourself);
		
		firstName.setText(userInfoBean.getUser().getFirst_name());
		lastName.setText(userInfoBean.getUser().getLast_name());
		userName.setText(userInfoBean.getUser().getUsername());
		etWebsite.setText(userInfoBean.getUser().getWebsite());
		etStatus.setText(userInfoBean.getUser().getStatus_message());
		if (userInfoBean.getUser().getAge() != null) {
			age.setText(userInfoBean.getUser().getAge());
		} else {
			age.setText("0");
		}
		if (userInfoBean.getUser().getPhone() != null) {
			phnNum.setText(userInfoBean.getUser().getPhone());
		} else {
			phnNum.setText("0");
		}

		if (userInfoBean.getUser().getGender().equals("female")) {
			tvGender.setText(getString(R.string.female));
		} else {
			tvGender.setText(getString(R.string.male));
		}

	}

	//check validation and call async Task
	private void saveEditedProfile() {
		 String incompleteError="";
		TextView userName = (TextView) view.findViewById(R.id.tvEditUserName);
		EditText firstName = (EditText) view.findViewById(R.id.etEditFirstName);
		if (userName.getText().toString().trim().equals("")) 
		{
			incompleteError = "Please enter user name.";
		} else if (firstName.getText().toString().trim().equals("")) {
			incompleteError = "Please enter first name.";

		} 
		else if (lastName.getText().toString().trim().equals("")) {
			incompleteError = "Please enter last name.";
		}
		else if (age.getText().toString().trim().equals("0")) {
			incompleteError = "Please enter age greater then 0.";
		}
		else if (!phnNum.getText().toString().equals("")&&phnNum.getText().toString().length()<6) {
			incompleteError = "Please enter valid 6 digit phone number.";
		}
		else if (Patterns.WEB_URL.matcher(etWebsite.getText().toString()).matches()==false) {
			incompleteError="Please enter valid website";
		}
		else {
			new updateProfileTask().execute();
		}


		if (!incompleteError.equals("")) {
			openErrorDialog(incompleteError);
		}
	}

	//method called from doInBackground for uploading th profile pic and it returns true if uploaded sucessfully else false
	private Boolean uploadProfileImage(String picturePath) {

		profilePicName =uploadProfilePicFile(picturePath,
				getActivity());
		if (profilePicName == null || profilePicName.getFilename() == null) {

			return false;
		}
		return true;
	}

	//AsyncTask for updating profile both Image and profile Info
	public class updateProfileTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), null, "Loading...");
			pd.setContentView(R.layout.progress_layout);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {


			if (picturePath != null && !picturePath.equals("")) {
				signUpProcessing = uploadProfileImage(picturePath);
			}

			if (picturePath != null &&!picturePath.equals("") && !signUpProcessing) {
				return null;
			}

			String url = "";
			Gson gson = new Gson();
			AppPreferences appPref = new AppPreferences(getActivity());
			String genderStr = "male";
			TextView tvGender;
			tvGender = (TextView) view.findViewById(R.id.tvGender);
			if (tvGender.getText().toString().equals(getString(R.string.female))) {
				genderStr = "female";
			}
			url = ConstantUtil.updateProfile + "userID=" + appPref.getUserId()
					+ "&first_name=" + firstName.getText().toString()
					+ "&last_name=" + lastName.getText().toString()
					+ "&username=" + userName.getText().toString() + "&age="
					+ age.getText().toString() + "&phone="
					+ phnNum.getText().toString()+
					"&website="
					+ etWebsite.getText().toString()
					+"&status_message="+etStatusMsg.getText().toString()
					+ "&marital_status="+userInfoBean.getUser().getMarital_status()+"&fbid=&gender=" + genderStr;
			String responseString = ConstantUtil.http_connection(url);

			if (responseString != null && !responseString.equals("")) {
				beanResponse = gson
						.fromJson(responseString, BeanResponse.class);
			}

			if(signUpProcessing == true || (beanResponse!=null && beanResponse.getError().equals("0"))){
				updateProfile();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "Update_Profile";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);
		}
	}

	private Handler myHandler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
			.create();

			if (msg.obj.toString().equalsIgnoreCase("Update_Profile")) {

				if ((beanResponse == null)) {
					Toast.makeText(getActivity(),
							getString(R.string.connectionerror),
							Toast.LENGTH_LONG).show();
				} 
				else if(signUpProcessing && beanResponse.getError().equals("1"))
				{
					picturePath = null;
					Toast.makeText(getActivity(),
							getString(R.string.strProfilePicUpdated),
							Toast.LENGTH_LONG).show();
					getActivity().getSupportFragmentManager().popBackStack();
				}
				else {
					picturePath = null;
					//getActivity().finish();
										getActivity().getSupportFragmentManager().popBackStack();
				}
			}
		}
	};

	//Method called from doInBackground for getting the user profile info after updating and put into MenuFragment.beanUserInfo
	private void updateProfile() {

		/*RadioButton male, female;
		male = (RadioButton) findViewById(R.id.rbMale);
		female = (RadioButton) findViewById(R.id.rbFemale);

		userInfoBean.setFirst_name(firstName.getText().toString());
		userInfoBean.setLast_name(lastName.getText().toString());
		userInfoBean.setUsername(userName.getText().toString());
		userInfoBean.setAge(age.getText().toString());
		userInfoBean.setPhone_number(phnNum.getText().toString());
		if (female.isChecked()) {
			userInfoBean.setGender("female");
		} else {
			userInfoBean.setGender("male");
		}*/
		String url = "";
		Gson gson = new Gson();
		AppPreferences appPref = new AppPreferences(getActivity());
		url = ConstantUtil.userProfileDetail + appPref.getUserId();
		String responseString = ConstantUtil.http_connection(url);

		if(responseString != null || responseString.equals(""))
		{
			BeanUserInfo beanUserInfo = gson.fromJson(responseString, BeanUserInfo.class);
			if(beanUserInfo != null)
				MenuFragment.beanUserInfo = beanUserInfo;
		}

		//		try {
		//			JSONObject jsonObj = new JSONObject(url);
		//			userInfoBean.setID((jsonObj.get("ID")).toString());
		//			userInfoBean.setFirst_name(jsonObj.get("first_name").toString());
		//			userInfoBean.setLast_name(jsonObj.get("last_name").toString());
		//			userInfoBean.setAge(jsonObj.get("age").toString());
		//			userInfoBean.setPhone_number(jsonObj.get("phone").toString());
		//			userInfoBean.setGender(jsonObj.)
		//		} catch (JSONException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}


	}


	//dialog if something is inComplete in User Profile
	@SuppressWarnings("deprecation")
	private void openErrorDialog(String incompleteError) {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
		.create();
		alertDialog.setMessage(incompleteError);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	// set profile image
	private void setProfileImage() {
		ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
		AQuery aq = new AQuery(ivProfileImage);

		//		setWidthAndHeightOfImage();
		Bitmap bm;
		try {
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(),
					R.drawable.no_img_jst_yet);
			options.preset = icon;
			options.fallback=R.drawable.no_img_jst_yet;

			aq.id(R.id.ivProfileImage).image(
					ConstantUtil.profilePicBaseUrl
					+ MenuFragment.beanUserInfo.getUser().getProfile_pic(),
					options);
		} catch (Exception e) {
			bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.no_img_jst_yet);
			ivProfileImage.setImageBitmap(bm);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ImageView photo = (ImageView) view.findViewById(R.id.ivProfileImage);

		if (resultCode == getActivity().RESULT_OK ) {
			if (requestCode == RESULT_LOAD_IMAGE && null != data && data.getData() != null) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getActivity().getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();

				try {
					if (photo != null) {
//						setWidthAndHeightOfImage();
						photo.setImageBitmap(BitmapFactory
								.decodeFile(picturePath));
					} else {
						Toast.makeText(getActivity(),
								"Selected image type not supported.",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			} else if (requestCode == REQUEST_TAKE_PHOTO && data != null) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				picturePath = Environment.getExternalStorageDirectory()
						+ File.separator + "image.jpg";
				File file = new File(picturePath);
				try {
					file.createNewFile();
					FileOutputStream fo = new FileOutputStream(file);
					fo.write(bytes.toByteArray());
					fo.close();
//					setWidthAndHeightOfImage();
					photo.setImageBitmap(BitmapFactory
							.decodeFile(picturePath));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}




			}

			/*
			 * if (picturePath != null && !picturePath.equals("")) { if
			 * (ConstantUtil.isNetworkAvailable(EditProfileScreen.this)) { new
			 * UploadPicTask(EditProfileScreen.this, picturePath).execute(); } else {
			 * Toast.makeText(EditProfileScreen.this,
			 * getString(R.string.internetFailure), Toast.LENGTH_LONG).show(); }
			 * }
			 */
		}
	}

//	//set width and Height of profile Image
//	private void setWidthAndHeightOfImage() {
//		ImageView photo = (ImageView) view.findViewById(R.id.ivProfileImage);
//		ConstantUtil.getScreen_Height(getActivity());
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//				ConstantUtil.screenWidth * 30 / 100,
//				ConstantUtil.screenWidth * 30 / 100);
//		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//		params.setMargins(10, 10, 10, 10);
//		photo.setLayoutParams(params);
//
//	}

//	 


	//set the width and height of header
	private void setWidthOfHeader() {
		RelativeLayout rlHeaderEditProfile = (RelativeLayout) view.findViewById(R.id.rlHeaderEditProfile);
		ConstantUtil.getScreen_Height(getActivity());
		LinearLayout.LayoutParams params = new LayoutParams(
				ConstantUtil.screenWidth, ConstantUtil.screenHeight * 8 / 100);
		rlHeaderEditProfile.setLayoutParams(params);
	}
	
	private void setSex() {
		TextView male = (TextView) dialog.findViewById(R.id.male);
		TextView female = (TextView) dialog.findViewById(R.id.female);

		final TextView tvSex = (TextView) view.findViewById(R.id.tvGender);

		male.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvSex.setText(R.string.male);
			}
		});

		female.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				tvSex.setText(R.string.female);
			}
		});

	}
	
	private void showOptionsDialog() {
		dialog = new Dialog(getActivity());
		dialog.getWindow().setLayout(100, 100);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		
			dialog.setContentView(R.layout.sex_options_dialog);
			
		

		dialog.show();
	}
	
	//set the title bar by changing notification image with Edit text
		private void setTitleBar()
		{

			TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
			tvTitle.setText(getString(R.string.editProfile));
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
					tvEdit.setText(getString(R.string.strSave));
					tvEdit.setVisibility(View.VISIBLE);

		}

		public static BeanProfilePicNameId uploadProfilePicFile(String sourceFileUri,Context con) {
			int serverResponseCode = 0;
			BeanResponse responseBean = null;
			AppPreferences appPref = new AppPreferences(con);
			String upLoadServerUri = ConstantUtil.uploadProfilePicUrl + "userID="+appPref.getUserId();
			String fileName = sourceFileUri;

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
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(upLoadServerUri);
				conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("profile_picture", fileName); 
				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd); 
				dos.writeBytes("Content-Disposition: form-data; name=\"profile_picture\";filename=\""+ fileName + "\"" + lineEnd);
				dos.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

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
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuffer bufferStr = new StringBuffer("");

				String line = "";
				while ((line = br.readLine()) != null) 
				{
					bufferStr.append(line);
				}
				String str = bufferStr.toString();

				JSONObject jsonObj = new JSONObject(str);
				BeanProfilePicNameId profilePicNameId = new BeanProfilePicNameId();
				if(jsonObj.has("filename")){
					profilePicNameId.setFilename(jsonObj.getString("filename"));
					if(jsonObj.has("id")){
						profilePicNameId.setId(jsonObj.getString("id"));
					}
				}
				
				responseBean = new BeanResponse();
				responseBean.setStatus(String.valueOf(serverResponseCode));
				responseBean.setMessage(serverResponseMessage);
				//close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
				
				if(serverResponseCode == 200 && serverResponseMessage.equals("OK")){
					return profilePicNameId;
				}

			} catch (MalformedURLException ex) 
			{  
				ex.printStackTrace();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;  
		}
		
}
