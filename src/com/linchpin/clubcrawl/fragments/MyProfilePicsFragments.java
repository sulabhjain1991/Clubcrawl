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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.linchpin.clubcrawl.APP;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.ComposePostScreen;
import com.linchpin.clubcrawl.DialogProfilePic;
import com.linchpin.clubcrawl.EditProfileScreen;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.PhotoCapture;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanError;
import com.linchpin.clubcrawl.beans.BeanListProfilePics;
import com.linchpin.clubcrawl.beans.BeanListVenueInfo;
import com.linchpin.clubcrawl.beans.BeanProfilePics;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.beans.BeanUserPosts;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.helper.Utils;

public class MyProfilePicsFragments extends ParentFragment implements Result,DoinBackgroung
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
	private String noOfPosts;
	private final int PROFILE_IMAGE = 4;
	private final int POST_LIKE = 5;
	private final int POST_UNLIKE = 6;
	private final int USER_DETAIL = 0;
	private final int USER_POSTS = 1;
	private final int USER_FOLLOWERS = 2;
	private final int USER_FRIENDS = 3;
	private Bitmap icon,profilePicBitmap;
	public static int fromDialog = 0;
	PullToRefreshScrollView mPullRefreshScrollView;;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		MenuFragment.currentsel = "MyProfilePicsFragments";
		view = inflater.inflate(R.layout.fragment_my_profile, container, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		BaseClass bc = new BaseClass();
		ConstantUtil.getScreen_Height(getActivity());
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
		//set the profile picture
		getInfoFromPreferences();
		//set profile image is called on BackSackChangeListener of MainScreen
		setProfileImage(R.id.ivProfilePic);
		//setProfileImage(R.id.ivPostProfilePic);
		//		((MainScreen)getActivity()).resetBottonBar();
		setAllInfo();
		setTitleBar();



		ImageView ivAdd = (ImageView) view.findViewById(R.id.ivAddPic);
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth * 35 / 100, ConstantUtil.screenWidth * 35 / 100);
		ivAdd.setLayoutParams(params);
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) 
			{
				getProfileTask();
				callBackgroundTasks();

			}
		});

	
		
		ivAdd.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				PhotoCapture photoCap = new PhotoCapture(MyProfilePicsFragments.this);
				photoCap.galleryCameraDialog();
			}
		});

		TextView tvMorePost=(TextView) view.findViewById(R.id.tvMorePosts);
		tvMorePost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fm = getActivity().getSupportFragmentManager();
				ViewMorePostsFragment fr = new ViewMorePostsFragment();
				Bundle b = new Bundle();
				b.putString("posts", noOfPosts);
				b.putString("isFriend", "false");

				activityCallback.onSendMessage(fr,b, "ViewMorePosts");



			}
		});

		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				//				Intent i = new Intent(getActivity(),EditProfileScreen.class);
				//				startActivity(i);
				EditProfileScreen fr = new EditProfileScreen();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.replace((R.id.frame), fr,"EditProfileFragment");
				fragmentTransaction.addToBackStack("EditProfileFragment");
				fragmentTransaction.commit();

			}
		});

		LinearLayout llfollowRequest=(LinearLayout) view.findViewById(R.id.llFollowerRequests);
		llfollowRequest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FollowRequestFragment fr = new FollowRequestFragment();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.replace((R.id.frame), fr,"FollowRequest");
				fragmentTransaction.addToBackStack("FollowRequest");
				fragmentTransaction.commit();
			}
		});
		
		
		
		
		TextView tvComposePost = (TextView) view.findViewById(R.id.tvComposePost);
		tvComposePost.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v)
			{

				Intent i = new Intent(getActivity(),ComposePostScreen.class);
				startActivity(i);

			}
		});

		TextView tvFollowingVenue = (TextView) view.findViewById(R.id.tvFollowingVenue);
		tvFollowingVenue.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v)
			{

				/*Intent i = new Intent(getActivity(),VenuesListActivity.class);
				startActivity(i);*/
				VenuesListFragment fr = new VenuesListFragment();
				Bundle bundle=new Bundle();
				bundle.putString("clickable", "true");
				fr.setArguments(bundle);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.replace((R.id.frame), fr,"FollowingVenue");
				fragmentTransaction.addToBackStack("FollowingVenue");
				fragmentTransaction.commit();

			}
		});



		RelativeLayout rlFollower=(RelativeLayout) view.findViewById(R.id.rlFollowers);
		rlFollower.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*	Intent i=new Intent(getActivity(),FollowersListActivity.class);
				startActivity(i);*/
				FollowersListFragment fr = new FollowersListFragment();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				Bundle bundle=new Bundle();
				bundle.putString("clickable", "true");
				fr.setArguments(bundle);
				fragmentTransaction.replace((R.id.frame), fr,"FollowerListActivity");
				fragmentTransaction.addToBackStack("FollowerListActivity");
				fragmentTransaction.commit();

			}
		});
		RelativeLayout rlFollowing=(RelativeLayout) view.findViewById(R.id.rlFollowing);
		rlFollowing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent i=new Intent(getActivity(),FollowingListActivity.class);
				startActivity(i);*/

				FollowingListFragment fr = new FollowingListFragment();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				Bundle bundle=new Bundle();
				bundle.putString("clickable", "true");
				fr.setArguments(bundle);
				fragmentTransaction.replace((R.id.frame), fr,"FollowingListActivity");
				fragmentTransaction.addToBackStack("FollowingListActivity");
				fragmentTransaction.commit();


			}
		});

		return view;

	}



	private void getProfileTask() 
	{
		NetworkTask getProfileTask = new NetworkTask(getActivity(), PROFILE_IMAGE);
		AppPreferences appPref = new AppPreferences(getActivity());
		String profilePicUrl = ConstantUtil.profilePicsUrl + "userid=" + appPref.getUserId();
		getProfileTask.setProgressDialog(false);
		getProfileTask.exposePostExecute(MyProfilePicsFragments.this);
		getProfileTask.execute(profilePicUrl);

	}



	private void setAllInfo() {
		LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout llPosts = (LinearLayout) view.findViewById(R.id.llPosts2);
		final LinearLayout llPosts1 = (LinearLayout) view.findViewById(R.id.llPosts);


		int end;
		if(MenuFragment.beanUserInfo.getUser().getPost().size()>5)
		{
			end=5;
		}
		else
			end=MenuFragment.beanUserInfo.getUser().getPost().size();
		llPosts.removeAllViews();
		for(int i=0;i<end;i++)
		{

			LinearLayout llInclude=(LinearLayout) inflater.inflate(R.layout.post_row, null);
			llInclude.setTag(i);

			//invisiblw the line below last post
			if(i==end-1)
			{
				View view=llInclude.findViewById(R.id.bottomView);
				view.setVisibility(View.GONE);
			}

			//profile name of the user
			TextView tvPostProfileName = (TextView) llInclude.findViewById(R.id.tvPostUsername);
			tvPostProfileName.setText(MenuFragment.beanUserInfo.getUser().getFirst_name() + " " + MenuFragment.beanUserInfo.getUser().getLast_name());


			//posted date
			String postedDate = "";
			TextView tvCreatedDate = (TextView) llInclude.findViewById(R.id.tvPostDate);
			Date postDate = ConstantUtil.convertStringToDate(MenuFragment.beanUserInfo.getUser().getPost().get(i).getCreated_at(), "yyyy-MM-dd HH:mm:ss");
			Date currentDate = new Date();
			long dayDifference = ConstantUtil.getDateDiffString(postDate, currentDate);
			if(dayDifference == 0)
				postedDate = "Today";
			else if(dayDifference == 1)
				postedDate = "Yesterday";
			else
				postedDate = dayDifference + " days ago";
			tvCreatedDate.setText(postedDate);

			//profile pic
			ImageViewRounded ivRound=(ImageViewRounded) llInclude.findViewById(R.id.ivPostProfilePic1);
			if(profilePicBitmap != null)
				ivRound.setImageBitmap(profilePicBitmap);
			else
			{
				AQuery aq = new AQuery(ivRound);

				Bitmap bm;
				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;

					if(MenuFragment.beanUserInfo.getUser().getProfile_pic() == null || MenuFragment.beanUserInfo.getUser().getProfile_pic().equals(""))
					{
						bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
						ivRound.setImageBitmap(bm);
					}
					else
					{
						aq.id(R.id.ivPostProfilePic1).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getProfile_pic(), options);
					}
				}
				catch(Exception e)
				{}
			} 


			//set no of likes in both image likes and status likes
			final TextView tvTextPostNumberOflikes = (TextView) llInclude.findViewById(R.id.tvTextPostNoOflikes);
			tvTextPostNumberOflikes.setText(MenuFragment.beanUserInfo.getUser().getPost().get(i).getTotal_likes());
			final TextView tvImagePostNumberOfLikes = (TextView) llInclude.findViewById(R.id.tvImagePostNoOfLikes);
			tvImagePostNumberOfLikes.setText(MenuFragment.beanUserInfo.getUser().getPost().get(i).getTotal_likes());
			

			//if both image and status are not blank
			if(!MenuFragment.beanUserInfo.getUser().getPost().get(i).getStatus().equals("")&&!MenuFragment.beanUserInfo.getUser().getPost().get(i).getImage().equals(""))
			{
				//layout for status post
				LinearLayout llPostMessage=(LinearLayout) llInclude.findViewById(R.id.llPostMessage);
				llPostMessage.setVisibility(View.VISIBLE);

				//layout for image post
				LinearLayout llPostImage=(LinearLayout) llInclude.findViewById(R.id.llPostImage);
				llPostImage.setVisibility(View.VISIBLE);

				//layout for like and share of status
				RelativeLayout rlMsgLikes=(RelativeLayout) llInclude.findViewById(R.id.rlMsgLikes);
				rlMsgLikes.setVisibility(View.GONE);

				//layout for like and share of image
				RelativeLayout rlImage=(RelativeLayout) llInclude.findViewById(R.id.rlImageLikes);
				rlImage.setVisibility(View.VISIBLE);

				//layout for no of likes on status
				LinearLayout llText=(LinearLayout) llInclude.findViewById(R.id.llTextLike);
				llText.setVisibility(View.GONE);

				LinearLayout llImage=(LinearLayout) llInclude.findViewById(R.id.llImageLikes);
				llImage.setVisibility(View.VISIBLE);

				//message text
				TextView tvPostMessage = (TextView) llInclude.findViewById(R.id.tvPostMessage);
				tvPostMessage.setVisibility(View.VISIBLE);
				tvPostMessage.setText(MenuFragment.beanUserInfo.getUser().getPost().get(i).getStatus());

				//image posted
				ImageView ivPostImageView=(ImageView) llInclude.findViewById(R.id.ivPostImage);
				ivPostImageView.setVisibility(View.VISIBLE);
				AQuery aqPostImage = new AQuery(ivPostImageView);

				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;


					aqPostImage.id(ivPostImageView.getId()).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getPost().get(i).getImage(), options);

				}
				catch(Exception e)
				{

				}

			}
			else if(!MenuFragment.beanUserInfo.getUser().getPost().get(i).getStatus().equals(""))
			{
				//layout for messages
				LinearLayout llPostMessage=(LinearLayout) llInclude.findViewById(R.id.llPostMessage);
				llPostMessage.setVisibility(View.VISIBLE);

				//layout for image
				LinearLayout llPostImage=(LinearLayout) llInclude.findViewById(R.id.llPostImage);
				llPostImage.setVisibility(View.GONE);

				RelativeLayout rlMsgLikes=(RelativeLayout) llInclude.findViewById(R.id.rlMsgLikes);
				rlMsgLikes.setVisibility(View.VISIBLE);

				LinearLayout llText=(LinearLayout) llInclude.findViewById(R.id.llTextLike);
				llText.setVisibility(View.VISIBLE);

				LinearLayout llImage=(LinearLayout) llInclude.findViewById(R.id.llImageLikes);
				llImage.setVisibility(View.GONE);
				TextView tvPostMessage = (TextView) llInclude.findViewById(R.id.tvPostMessage);
				tvPostMessage.setVisibility(View.VISIBLE);

				tvPostMessage.setText(MenuFragment.beanUserInfo.getUser().getPost().get(i).getStatus());

				ImageView ivPostImageView=(ImageView) llInclude.findViewById(R.id.ivPostImage);
				ivPostImageView.setVisibility(View.GONE);
			}
			else if(!MenuFragment.beanUserInfo.getUser().getPost().get(i).getImage().equals(""))
			{

				LinearLayout llPostMessage=(LinearLayout) llInclude.findViewById(R.id.llPostMessage);
				llPostMessage.setVisibility(View.GONE);

				ImageView ivPostImageView=(ImageView) llInclude.findViewById(R.id.ivPostImage);
				ivPostImageView.setVisibility(View.VISIBLE);

				RelativeLayout rlImage=(RelativeLayout) llInclude.findViewById(R.id.rlImageLikes);
				rlImage.setVisibility(View.VISIBLE);

				LinearLayout llImage=(LinearLayout) llInclude.findViewById(R.id.llImageLikes);
				llImage.setVisibility(View.VISIBLE);

				AQuery aqPostImage = new AQuery(ivPostImageView);

				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;
					Bitmap bm = aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getPost().get(i).getImage());

					if(bm!=null)
						ivPostImageView.setImageBitmap(bm);
					else
						aqPostImage.id(ivPostImageView.getId()).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getPost().get(i).getImage(), options);

				}
				catch(Exception e)
				{}



			}




			if(MenuFragment.beanUserInfo.getUser().getPost() == null || MenuFragment.beanUserInfo.getUser().getPost().size() == 0)
			{
				View  viewLine = view.findViewById(R.id.view);
				viewLine.setVisibility(View.GONE);
				llPosts1.setVisibility(View.GONE);
			}
			else if(MenuFragment.beanUserInfo.getUser().getPost().get(i).getIs_liked().equals("0"))
			{
				llPosts1.setVisibility(View.VISIBLE);
				//TextView tvLike = (TextView) llInclude.findViewById(R.id.tvLike);
				TextView tvTextPostLike=(TextView) llInclude.findViewById(R.id.tvStrPostLike);
				TextView tvImgPostLike=(TextView) llInclude.findViewById(R.id.tvImgPostLike);
				tvTextPostLike.setText("Like");
				tvTextPostLike.setTextColor(getResources().getColor(R.color.color_not_liked));
				tvImgPostLike.setText("Like");
				tvImgPostLike.setTextColor(getResources().getColor(R.color.color_not_liked));

			}
			else
			{
				llPosts.setVisibility(View.VISIBLE);
				TextView tvTextPostLike=(TextView) llInclude.findViewById(R.id.tvStrPostLike);
				tvTextPostLike.setText("Liked");
				tvTextPostLike.setTextColor(getResources().getColor(R.color.background_color));
				TextView tvImgPostLike=(TextView) llInclude.findViewById(R.id.tvImgPostLike);
				tvImgPostLike.setText("Liked");
				tvImgPostLike.setTextColor(getResources().getColor(R.color.background_color));



			}
			LinearLayout llPostImage=(LinearLayout) llInclude.findViewById(R.id.llPostImage);
			LinearLayout llText=(LinearLayout) llInclude.findViewById(R.id.llTextLike);
			llPostImage.setTag(llInclude);
			llText.setTag(llInclude);
			llText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout llInclude = (LinearLayout) v.getTag();
					int id = Integer.parseInt(llInclude.getTag().toString());
					if(!tvTextPostNumberOflikes.getText().toString().equals("0"))
					{
						String postId=MenuFragment.beanUserInfo.getUser().getPost().get(id).getId();

						Bundle bundle = new Bundle();
						bundle.putString("feedId", postId);

						LikersListFragment fr = new LikersListFragment();
						fr.setArguments(bundle);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fm.beginTransaction();

						fragmentTransaction.replace((R.id.frame), fr,"LikersListFragment");
						fragmentTransaction.addToBackStack("LikersListFragment");
						fragmentTransaction.commit();



					}
				}
			});


			llPostImage.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout llInclude = (LinearLayout) v.getTag();
					int id = Integer.parseInt(llInclude.getTag().toString());
					if(!tvImagePostNumberOfLikes.getText().toString().equals("0"))
					{
						String postId=MenuFragment.beanUserInfo.getUser().getPost().get(id).getId();

						Bundle bundle = new Bundle();
						bundle.putString("feedId", postId);

						LikersListFragment fr = new LikersListFragment();
						fr.setArguments(bundle);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fm.beginTransaction();

						fragmentTransaction.replace((R.id.frame), fr,"LikersListFragment");
						fragmentTransaction.addToBackStack("LikersListFragment");
						fragmentTransaction.commit();



					}
				}
			});

			TextView tvTextPostLike = (TextView) llInclude.findViewById(R.id.tvStrPostLike);
			tvTextPostLike.setTag(llInclude);
			tvTextPostLike.setOnClickListener(new OnClickListener() 
			{

				@Override
				public void onClick(View v)
				{
					LinearLayout llInclude = (LinearLayout) v.getTag();
					int id = Integer.parseInt(llInclude.getTag().toString());
					if(MenuFragment.beanUserInfo.getUser().getPost().get(id).getIs_liked().equals("0"))
					{
						NetworkTask postLikeTask = new NetworkTask(getActivity(), POST_LIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+MenuFragment.beanUserInfo.getUser().getPost().get(id).getId();
						postLikeTask.exposePostExecute(MyProfilePicsFragments.this);
						postLikeTask.execute(likeUrl);
					}
					else
					{
						NetworkTask postUnLikeTask = new NetworkTask(getActivity(), POST_UNLIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+MenuFragment.beanUserInfo.getUser().getPost().get(id).getId();
						postUnLikeTask.exposePostExecute(MyProfilePicsFragments.this);
						postUnLikeTask.execute(unlikeUrl);
					}

				}
			});


			TextView tvImagePostLike = (TextView) llInclude.findViewById(R.id.tvImgPostLike);
			tvImagePostLike.setTag(llInclude);
			tvImagePostLike.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					LinearLayout llInclude = (LinearLayout) v.getTag();
					int id = Integer.parseInt(llInclude.getTag().toString());
					if(MenuFragment.beanUserInfo.getUser().getPost().get(id).getIs_liked().equals("0"))
					{
						NetworkTask postLikeTask = new NetworkTask(getActivity(), POST_LIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+MenuFragment.beanUserInfo.getUser().getPost().get(id).getId();
						postLikeTask.exposePostExecute(MyProfilePicsFragments.this);
						postLikeTask.execute(likeUrl);
					}
					else
					{
						NetworkTask postUnLikeTask = new NetworkTask(getActivity(), POST_UNLIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+MenuFragment.beanUserInfo.getUser().getPost().get(id).getId();
						postUnLikeTask.exposePostExecute(MyProfilePicsFragments.this);
						postUnLikeTask.execute(unlikeUrl);
					}

				}
			});

		
			


			if(MenuFragment.beanUserInfo.getUser().getPost() == null || MenuFragment.beanUserInfo.getUser().getPost().size() == 0)
			{
				View  viewLine = view.findViewById(R.id.view);
				viewLine.setVisibility(View.GONE);
				llPosts.setVisibility(View.GONE);
			}
			else
			{
				TextView tvTextPostShare = (TextView) llInclude.findViewById(R.id.tvTextPostShare);

				tvTextPostShare.setTag(llInclude);
				tvTextPostShare.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LinearLayout llInclude = (LinearLayout) v.getTag();
						int id = Integer.parseInt(llInclude.getTag().toString());
						ConstantUtil.shareDialog(getActivity(),0,MenuFragment.beanUserInfo.getUser().getPost().get(id).getStatus(),"",null);
						llPosts1.setVisibility(View.VISIBLE);



					}
				});
				TextView tvImagePostShare = (TextView) llInclude.findViewById(R.id.tvImagePostShare);

				tvImagePostShare.setTag(llInclude);

				tvImagePostShare.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LinearLayout llInclude = (LinearLayout) v.getTag();
						int id = Integer.parseInt(llInclude.getTag().toString());
						ConstantUtil.shareDialog(getActivity(),0,MenuFragment.beanUserInfo.getUser().getPost().get(id).getStatus(),"",null);
						llPosts1.setVisibility(View.VISIBLE);



					}
				});

			}




			llPosts.addView(llInclude);

		}





		TextView tvProfileName = (TextView) view.findViewById(R.id.tvname);
		tvProfileName.setText(MenuFragment.beanUserInfo.getUser().getFirst_name() + " " + MenuFragment.beanUserInfo.getUser().getLast_name());
		TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
		tvStatus.setText(MenuFragment.beanUserInfo.getUser().getMarital_status());
		TextView tvWebsite = (TextView) view.findViewById(R.id.tvurl);
		tvWebsite.setText(MenuFragment.beanUserInfo.getUser().getWebsite());
		TextView tvAge = (TextView) view.findViewById(R.id.tvAge);
		if(MenuFragment.beanUserInfo.getUser().getAge().equals("0")|| MenuFragment.beanUserInfo.getUser().getAge() == null || MenuFragment.beanUserInfo.getUser().getAge().equals(""))
			tvAge.setVisibility(View.GONE);
		else
		{
			tvAge.setText(MenuFragment.beanUserInfo.getUser().getAge()+" years old");
			tvAge.setVisibility(View.VISIBLE);
		}

		TextView tvStatusMessage = (TextView) view.findViewById(R.id.tvStatusMessage);
		tvStatusMessage.setText(MenuFragment.beanUserInfo.getUser().getStatus_message());

		TextView tvPosts = (TextView) view.findViewById(R.id.tvNoOfPosts);
		tvPosts.setText(noOfPosts);
		TextView tvNoOfFollowers = (TextView) view.findViewById(R.id.tvNoOfFollowers);
		tvNoOfFollowers.setText(MenuFragment.beanUserInfo.getUser().getTotal_followers());
		TextView tvNoOffollowings = (TextView) view.findViewById(R.id.tvNoOfFollowings);
		tvNoOffollowings.setText(MenuFragment.beanUserInfo.getUser().getTotal_following());
		tvProfileName.setText(MenuFragment.beanUserInfo.getUser().getFirst_name());
		TextView tvFollowingVenues = (TextView) view.findViewById(R.id.tvFollowingVenue);
		String followingVenue = "";
		if(MenuFragment.beanUserInfo.getUser().getTotal_following_venue() == null || MenuFragment.beanUserInfo.getUser().getTotal_following_venue().equals(""))
			followingVenue = "0";
		else
			followingVenue = MenuFragment.beanUserInfo.getUser().getTotal_following_venue();
		if(MenuFragment.beanUserInfo.getUser().getFollowReqCount().equals("0"))
		{
			LinearLayout llFollowers = (LinearLayout) view.findViewById(R.id.llFollowerRequests);
			llFollowers.setVisibility(View.GONE);
		}
		else
		{
			TextView tvFolloweRequests = (TextView) view.findViewById(R.id.tvNoOfFolloweRequest);
			tvFolloweRequests.setVisibility(View.VISIBLE);
			tvFolloweRequests.setText(MenuFragment.beanUserInfo.getUser().getFollowReqCount());
		}

		tvFollowingVenues.setText(Html.fromHtml("Following "+"<font color='#FF2763'>"+ MenuFragment.beanUserInfo.getUser().getTotal_following_venue()+"</font>"+" venues"));


		/*

			TextView tvPostProfileName = (TextView) view.findViewById(R.id.tvPostUsername);
			tvPostProfileName.setText(MenuFragment.beanUserInfo.getUser().getFirst_name() + " " + MenuFragment.beanUserInfo.getUser().getLast_name());
			String postedDate = "";
			TextView tvCreatedDate = (TextView) view.findViewById(R.id.tvPostDate);
			Date postDate = ConstantUtil.convertStringToDate(MenuFragment.beanUserInfo.getUser().getPost().get(0).getCreated_at(), "yyyy-MM-dd HH:mm:ss");
			Date currentDate = new Date();
			long dayDifference = ConstantUtil.getDateDiffString(postDate, currentDate);
			if(dayDifference == 0)
				postedDate = "Today";
			else if(dayDifference == 1)
				postedDate = "Yesterday";
			else
				postedDate = dayDifference + " days ago";
			tvCreatedDate.setText(postedDate);

			TextView tvLikes = (TextView) view.findViewById(R.id.tvNoOfLikes);
			tvLikes.setText(MenuFragment.beanUserInfo.getUser().getPost().get(0).getTotal_likes());

			TextView tvPostMessage = (TextView) view.findViewById(R.id.tvPostMessage);
			tvPostMessage.setText(MenuFragment.beanUserInfo.getUser().getPost().get(0).getMsg());
		 */
		//}

	}



	//set Profile Image using aQuerD
	public void setProfileImage(int id)
	{
		ImageView ivProfileImage = (ImageView) view.findViewById(id);
		AQuery aq = new AQuery(ivProfileImage);

		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;
			options.fallback=R.drawable.no_img_jst_yet;

			if(MenuFragment.beanUserInfo.getUser().getProfile_pic() == null || MenuFragment.beanUserInfo.getUser().getProfile_pic().equals(""))
			{
				bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
				ivProfileImage.setImageBitmap(bm);
			}
			else
			{
				aq.id(id).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getProfile_pic(), options);
			}



			profilePicBitmap = aq.getCachedImage(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getProfile_pic());
		}
		catch (Exception e)
		{
			//			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			//			ivProfileImage.setImageBitmap(bm);
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
			responseBeanUploadPic = uploadFile(filePath, getActivity());

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
						//						setOnClickListenerOnCrossButton(v, beanProfilePics.get(i), i);

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
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth * 35 / 100, ConstantUtil.screenWidth * 35 / 100);

		RelativeLayout rl = (RelativeLayout) v;
		rl.setLayoutParams(params);
		ImageView ivPic = (ImageView) rl.getChildAt(0);
		ivPic.setId(id);
		ivPic.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{/*
				Intent i = new Intent(getActivity(), DialogProfilePic.class);
				i.putExtra("picUrl", ConstantUtil.profilePicBaseUrl + url);
				startActivity(i);*/
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				DialogProfilePic fr = new DialogProfilePic();
				Bundle b = new Bundle();
				b.putString("picUrl", ConstantUtil.profilePicBaseUrl + url);
				fr.setArguments(b);
				transaction.add(R.id.frame, fr);
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();
				//activityCallback.onSendMessage(fr,b, "ViewMorePosts");

			}
		});

		Bitmap bm;
		try
		{
			ivPic.setScaleType(ScaleType.FIT_XY);

			ImageOptions options = new ImageOptions();



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
			if (requestCode == RESULT_LOAD_IMAGE && null != data && data.getData() != null)
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
		LinearLayout.LayoutParams params = new LayoutParams(ConstantUtil.screenWidth * 30 / 100, ConstantUtil.screenWidth * 30 / 100);
		ivAdd.setLayoutParams(params);

		LinearLayout llPics = (LinearLayout) view.findViewById(R.id.llPics);
		LinearLayout.LayoutParams paramsLlPics = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ConstantUtil.screenWidth * 30 / 100);
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
		tvTitle.setEnabled(false);

		ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		ivNotification.setVisibility(View.GONE);
		ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
		ivSearch.setVisibility(View.GONE);
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setText(getString(R.string.strEdit));
		tvEdit.setVisibility(View.VISIBLE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen)(getActivity()));

	}

	@Override
	public void onResume()
	{
		if(fromDialog == 0)
		{
			getProfileTask();
			callBackgroundTasks();
		}
		fromDialog = 0;

		super.onResume();
		//	setProfileImage();

	}
	private void getInfoFromPreferences()
	{
		noOfPosts = APP.GLOBAL().getPreferences().getString(APP.PREF.USER_TOTAL_POSTS.key, "");
		Gson gson = new Gson();
		MenuFragment.beanUserInfo = (BeanUserInfo)(gson.fromJson(APP.GLOBAL().getPreferences().getString(APP.PREF.USER_DETAILS.key, ""), BeanUserInfo.class));

	}



	@Override
	public void resultfromNetwork(String object, int id,int arg1,String arg2) 
	{

		Gson gson = new Gson();
		switch (id) {
		case PROFILE_IMAGE:

			if (object != null && !object.equals(""))
			{
				responseBean = gson.fromJson(object, BeanListProfilePics.class);
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
						// setOnClickListenerOnCrossButton(v, beanProfilePics.get(i), i);

					}

					//						Toast.makeText(getActivity(), getString(R.string.strSuccessLogin), Toast.LENGTH_LONG).show();
					//						Intent i=new Intent(getActivity(),MainScreen.class);
					//						i.putExtra("loginInfo", beanLoginInfo.getUserdata());
					//						startActivity(i);
					//						finish();

				}


			}

			break;
		case USER_DETAIL:
			getInfoFromPreferences();
			//set profile image is called on BackSackChangeListener of MainScreen
			setProfileImage(R.id.ivProfilePic);
			setProfileImage(R.id.ivPostProfilePic);
			setAllInfo();
			break;
		case USER_POSTS:
			getInfoFromPreferences();
			//set profile image is called on BackSackChangeListener of MainScreen
			//			setProfileImage(R.id.ivProfilePic);
			setAllInfo();
			mPullRefreshScrollView.onRefreshComplete();
			break;
		case USER_FOLLOWERS:
			getInfoFromPreferences();
			//set profile image is called on BackSackChangeListener of MainScreen
			//			setProfileImage(R.id.ivProfilePic);
			setAllInfo();
			break;
		case USER_FRIENDS:
			getInfoFromPreferences();
			//set profile image is called on BackSackChangeListener of MainScreen
			//			setProfileImage(R.id.ivProfilePic);
			setAllInfo();
			break;
		case POST_LIKE:
			LinearLayout llInclude = (LinearLayout) view.findViewWithTag(arg1);
			if(object != null && !object.equals(""))
			{
				BeanResponse res = (BeanResponse)gson.fromJson(object, BeanResponse.class);

				if(res.getStatus().equals("error"))
				{
					showAlertForLike(res.getMessage());
				}
				else
				{
					//showAlertForLike("Status successfully liked");
					TextView tvLikes = (TextView) llInclude.findViewById(R.id.tvTextPostNoOflikes);
					TextView tvIsLike = (TextView) llInclude.findViewById(R.id.tvStrPostLike);
					tvIsLike.setText("Liked");
					tvIsLike.setTextColor(getResources().getColor(R.color.background_color));

					TextView tvLikesImages = (TextView) llInclude.findViewById(R.id.tvImagePostNoOfLikes);
					TextView tvIsLikeImages = (TextView) llInclude.findViewById(R.id.tvImgPostLike);
					tvIsLikeImages.setText("Liked");
					tvIsLikeImages.setTextColor(getResources().getColor(R.color.background_color));

					int likes = Integer.parseInt(MenuFragment.beanUserInfo.getUser().getPost().get(arg1).getTotal_likes());
					likes = ++likes;
					BeanUserPosts posts =MenuFragment.beanUserInfo.getUser().getPost().get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("1");
					MenuFragment.beanUserInfo.getUser().getPost().set(arg1,posts);
					tvLikes.setText(MenuFragment.beanUserInfo.getUser().getPost().get(arg1).getTotal_likes());
					tvLikesImages.setText(MenuFragment.beanUserInfo.getUser().getPost().get(arg1).getTotal_likes());

				}
			}
			break;	
		case POST_UNLIKE:
			LinearLayout llInclude1 = (LinearLayout) view.findViewWithTag(arg1);
			if(object != null && !object.equals(""))
			{
				BeanResponse res = (BeanResponse)gson.fromJson(object, BeanResponse.class);

				if(res.getStatus().equals("error"))
				{
					showAlertForLike(res.getMessage());
				}
				else
				{
					//showAlertForLike("Status successfully unliked");
					TextView tvLikes = (TextView) llInclude1.findViewById(R.id.tvTextPostNoOflikes);
					TextView tvIsLike = (TextView) llInclude1.findViewById(R.id.tvStrPostLike);
					tvIsLike.setText("Like");
					tvIsLike.setTextColor(getResources().getColor(R.color.color_not_liked));

					TextView tvLikesImages = (TextView) llInclude1.findViewById(R.id.tvImagePostNoOfLikes);
					TextView tvIsLikeImages = (TextView) llInclude1.findViewById(R.id.tvImgPostLike);
					tvIsLikeImages.setText("Like");
					tvIsLikeImages.setTextColor(getResources().getColor(R.color.color_not_liked));

					int likes = Integer.parseInt(MenuFragment.beanUserInfo.getUser().getPost().get(arg1).getTotal_likes());
					likes = --likes;
					BeanUserPosts posts =MenuFragment.beanUserInfo.getUser().getPost().get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("0");
					MenuFragment.beanUserInfo.getUser().getPost().set(arg1,posts);
					tvLikes.setText(MenuFragment.beanUserInfo.getUser().getPost().get(arg1).getTotal_likes());
					tvLikesImages.setText(MenuFragment.beanUserInfo.getUser().getPost().get(arg1).getTotal_likes());

				}
			}
			break;	

		default:
			break;
		}

	}

	private void callBackgroundTasks()
	{
		AppPreferences appPref = new AppPreferences(getActivity());
		NetworkTask MyProfileTask = new NetworkTask(getActivity(), USER_DETAIL);
		MyProfileTask.setProgressDialog(false);
		MyProfileTask.exposeDoinBackground(MyProfilePicsFragments.this);
		MyProfileTask.exposePostExecute(MyProfilePicsFragments.this);
		String userDetailsUrl = ConstantUtil.userProfileDetail + appPref.getUserId();
		MyProfileTask.execute(userDetailsUrl);

		NetworkTask TotalPosts = new NetworkTask(getActivity(), USER_POSTS);
		TotalPosts.setProgressDialog(false);
		TotalPosts.exposeDoinBackground(MyProfilePicsFragments.this);
		TotalPosts.exposePostExecute(MyProfilePicsFragments.this);
		String userPostsUrl = ConstantUtil.userTotalPostsUrl + "user_id=" + appPref.getUserId();
		TotalPosts.execute(userPostsUrl);
		NetworkTask TotalFollowers = new NetworkTask(getActivity(), USER_FOLLOWERS);
		TotalFollowers.setProgressDialog(false);
		TotalFollowers.exposeDoinBackground(MyProfilePicsFragments.this);
		TotalFollowers.exposePostExecute(MyProfilePicsFragments.this);
		String userTotalFollowersUrl = ConstantUtil.toTalFollowersUrl + "user_id=" + appPref.getUserId();
		TotalFollowers.execute(userTotalFollowersUrl);
	}



	@Override
	public String doInBackground(Integer id, String... params) 
	{
		synchronized (id){
			synchronized (params)
			{


				Gson gson = new Gson();

				switch (id) {
				case USER_DETAIL:
					String urlUserDetails = params[0];
					String responseStringUserDetails = ConstantUtil.http_connection(urlUserDetails);
					if(responseStringUserDetails != null && !responseStringUserDetails.equals(""))
						APP.GLOBAL().getEditor().putString(APP.PREF.USER_DETAILS.key, responseStringUserDetails).commit();
					break;
				case USER_POSTS:
					String urlPosts = params[0];
					String responseStringPosts = ConstantUtil.http_connection(urlPosts);
					if(responseStringPosts != null && !responseStringPosts.equals(""))
					{

						try {
							JSONObject obj = new JSONObject(responseStringPosts);
							JSONArray array = obj.getJSONArray("post");

							for(int i=0;i<array.length();i++)
							{
								JSONObject objPosts = array.getJSONObject(i);
								String totalPosts = objPosts.getString("total_post");
								APP.GLOBAL().getEditor().putString(APP.PREF.USER_TOTAL_POSTS.key, totalPosts).commit();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case USER_FOLLOWERS:
					String urlFollowers = params[0];
					String responseStringFollowers = ConstantUtil.http_connection(urlFollowers);
					System.out.println("responseStringFollowers::"+responseStringFollowers);
					if(responseStringFollowers != null && !responseStringFollowers.equals(""))
					{
						try {
							JSONObject obj = new JSONObject(responseStringFollowers);
							JSONArray arrayFollow = obj.getJSONArray("follow");

							for(int i=0;i<arrayFollow.length();i++)
							{
								JSONObject objFollow = arrayFollow.getJSONObject(i);
								String followCount = objFollow.getString("follow_count");
								APP.GLOBAL().getEditor().putString(APP.PREF.USER_TOTAL_FOLLOWERS.key, followCount);
							}
							JSONArray arrayFollowing = obj.getJSONArray("following");

							for(int i=0;i<arrayFollowing.length();i++)
							{
								JSONObject objFollowing = arrayFollowing.getJSONObject(i);
								String followingCount = objFollowing.getString("following_count");
								APP.GLOBAL().getEditor().putString(APP.PREF.USER_TOTAL_FOLLOWINGS.key, followingCount).commit();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					break;

				default:
					break;
				}
			}
		}
		return null;
	}
	private void showAlertForLike(String alertMessage) {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.setMessage(alertMessage);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	public static BeanResponse uploadFile(String sourceFileUri, Context con) {
		int serverResponseCode = 0;
		BeanResponse responseBean = null;
		AppPreferences appPref = new AppPreferences(con);
		String upLoadServerUri = ConstantUtil.uploadPicUrl + "userID="
				+ appPref.getUserId();
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
