package com.linchpin.clubcrawl.fragments;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.beans.BeanUserPosts;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class FriendDetailFragment extends ParentFragment implements Result
{
	private View				view;
	private BeanUserInfo beanUserinfo;
	private String friendId;
	private Bitmap profilePicBitmap;
	private final int POST_LIKE = 5;
	private final int POST_UNLIKE = 6;
	private String noOfPosts,noOfFollowers,noOfFollowings;

	private final int USER_DETAIL = 0;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		MenuFragment.currentsel = "FriendDetailFragment";
		view = inflater.inflate(R.layout.fragment_view_friend_profile_detail, container, false);
		friendId = getArguments().getString("id");
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		BaseClass bc = new BaseClass();
		setTitleBar("");
		ConstantUtil.getScreen_Height(getActivity());
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());

		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
			AppPreferences appPref = new AppPreferences(getActivity());
			NetworkTask MyProfileTask = new NetworkTask(getActivity(), USER_DETAIL);
			MyProfileTask.setProgressDialog(true);

			MyProfileTask.exposePostExecute(FriendDetailFragment.this);
			String userDetailsUrl = ConstantUtil.friendProfileDetail + "userID="+friendId+"&current_user=" + appPref.getUserId();
			MyProfileTask.execute(userDetailsUrl);
		}
		else
		{
			Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
		}

		view.findViewById(R.id.rlFollowers).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FollowersListFragment fr = new FollowersListFragment();
				Bundle bundle=new Bundle();
				bundle.putString("clickable", "false");
				bundle.putString("id", ""+beanUserinfo.getUser().getID());
				fr.setArguments(bundle);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.replace((R.id.frame), fr,"FriendDetailFragment");
				fragmentTransaction.addToBackStack("FriendDetailFragment");
				fragmentTransaction.commit();


			}
		});

		view.findViewById(R.id.rlFollowing).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FollowingListFragment fr = new FollowingListFragment();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				Bundle bundle=new Bundle();
				bundle.putString("clickable", "false");
				bundle.putString("id", beanUserinfo.getUser().getID());
				fr.setArguments(bundle);
				fragmentTransaction.replace((R.id.frame), fr,"FriendDetailFragment");
				fragmentTransaction.addToBackStack("FriendDetailFragment");
				fragmentTransaction.commit();


			}
		});

		view.findViewById(R.id.tvFrndsFollowingVenue).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				VenuesListFragment fr = new VenuesListFragment();
				Bundle bundle=new Bundle();
				bundle.putString("clickable", "false");
				bundle.putString("id", ""+beanUserinfo.getUser().getID());
				fr.setArguments(bundle);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.replace((R.id.frame), fr,"FriendDetailFragment");
				fragmentTransaction.addToBackStack("FriendDetailFragment");
				fragmentTransaction.commit();
			}
		});
		final TextView tvComposePost=(TextView) view.findViewById(R.id.tvComposePost);

		tvComposePost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				if(beanUserinfo.getUser().getIs_followed().equals("3"))
				{

					/*tvFollow.setText("Following");
					tvFollow.setTextColor(context.getResources().getColor(android.R.color.white));
					tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
					 */
					NetworkTask networkTask = new NetworkTask(getActivity(), 100);
					tvComposePost.setText("Requested");
					tvComposePost.setTextColor(getActivity().getResources().getColor(R.color.white));
					tvComposePost.setBackgroundResource(R.drawable.following_btn_shape);
					beanUserinfo.getUser().setIs_followed("2");
					networkTask.exposePostExecute(new Result()
					{

						@Override
						public void resultfromNetwork(String object, int id,int arg1,String arg2)
						{/*
							try
							{
								JSONObject result = new JSONObject(object);
								if (result.get("status").equals("success"))
								{
									tvFollow.setText("Requested");
									tvFollow.setTextColor(context.getResources().getColor(R.color.white));
									tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
									followersInfo.setFollow_status("2");
								}
								else if(result.get("status").equals("1"))
								{

									tvComposePost.setText("Following");
									tvComposePost.setTextColor(getActivity().getResources().getColor(R.color.white));
									tvComposePost.setBackgroundResource(R.drawable.following_btn_shape);
									beanUserinfo.getUser().setIs_followed("1");
								}
								else
								{
									Toast toast= Toast.makeText(getActivity(),"Network error try again later", Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}



							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}



						 */}
					});



					networkTask.setProgressDialog(false);
					String 	url=ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&follow_type_id="+beanUserinfo.getUser().getID()+"&follow_type=user";
					networkTask.execute(ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(getActivity()).getUserId()+"&follow_type_id="+beanUserinfo.getUser().getID()+"&follow_type=user");







				}
				else if(beanUserinfo.getUser().getIs_followed().equals("2"))
				{

					Toast.makeText(getActivity(), "Follow request sent already", Toast.LENGTH_SHORT).show();					

				}
				else
				{
					final Dialog dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.unfollow_user_dialog);

					dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					TextView tvName=	(TextView) dialog.findViewById(R.id.tvName);
					tvName.setText(beanUserinfo.getUser().getFirst_name()+" "+beanUserinfo.getUser().getLast_name());
					dialog.findViewById(R.id.tvUnfollow).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {					

							dialog.cancel();
							/*tvFollow.setText("+Follow");
							tvFollow.setTextColor(context.getResources().getColor(R.color.background_color));
							tvFollow.setBackgroundResource(R.drawable.follow_btn_shape);*/
							NetworkTask networkTask = new NetworkTask(getActivity(), 100);
							networkTask.exposePostExecute(new Result()
							{

								@Override
								public void resultfromNetwork(String object, int id,int arg1,String arg2)
								{
									try
									{
										//{"status":"success"}
										JSONObject result = new JSONObject(object);
										if (result.get("status").equals("success"))
										{
											tvComposePost.setText("+Follow");
											tvComposePost.setTextColor(getActivity().getResources().getColor(R.color.background_color));
											tvComposePost.setBackgroundResource(R.drawable.follow_btn_shape);
											beanUserinfo.getUser().setIs_followed("3");
										}
										else
										{
											Toast toast= Toast.makeText(getActivity(),"Network error try again later", Toast.LENGTH_SHORT);
											//toast.makeText(""+result.get("status").toString());
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
										}

									}
									catch (JSONException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}







								}
							});
							//networkTask.setProgressDialog(false);
							//	networkTask.execute(ConstantUtil.baseUrl + "newapi/savefollowstatus?follow_type=venue&user_id=" + AppPreferences.getInstance(mContext).getUserId() + "&follow_type_id="
							//			+ clubsInfo.getId());
							String url=ConstantUtil.unFollowClickUrl+"follow_type=user&id=" + beanUserinfo.getUser().getID() + "&follow_type_id="+ AppPreferences.getInstance(getActivity()).getUserId();
							url=ConstantUtil.unFollowClickUrl+"follow_type=user&id=" + beanUserinfo.getUser().getID() + "&follow_type_id="+ AppPreferences.getInstance(getActivity()).getUserId();
							networkTask.execute(ConstantUtil.unFollowClickUrl+"follow_type=user&id="+beanUserinfo.getUser().getID()+ "&user_id="+AppPreferences.getInstance(getActivity()).getUserId());}

					});
					dialog.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
					dialog.show();

				}





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
				b.putString("posts", ""+beanUserinfo.getUser().getPost().size());
				b.putString("isFriend", "true");
				activityCallback.onSendMessage(fr,b, "ViewMorePosts");



			}
		});


		return view;

	}







	private void setAllInfo() {
		setTitleBar(beanUserinfo.getUser().getFirst_name());
		view.findViewById(R.id.llFollowerRequests).setVisibility(View.GONE);
		TextView tvProfileName = (TextView) view.findViewById(R.id.tvname);
		tvProfileName.setText(beanUserinfo.getUser().getFirst_name() + " " + beanUserinfo.getUser().getLast_name());
		TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
		tvStatus.setText(beanUserinfo.getUser().getMarital_status());
		TextView tvWebsite = (TextView) view.findViewById(R.id.tvurl);
		tvWebsite.setText(beanUserinfo.getUser().getWebsite());
		TextView tvStatusMessage = (TextView) view.findViewById(R.id.tvStatusMessage);
		tvStatusMessage.setText(beanUserinfo.getUser().getStatus_message());
		TextView tvAge = (TextView) view.findViewById(R.id.tvAge);
		if(beanUserinfo.getUser().getAge().equals("0")|| beanUserinfo.getUser().getAge() == null || beanUserinfo.getUser().getAge().equals(""))
			tvAge.setVisibility(View.GONE);
		else
		{
			tvAge.setText(beanUserinfo.getUser().getAge()+" years old");
			tvAge.setVisibility(View.VISIBLE);
		}


		//                                        Visibility of bottom layout                              // 

		if(beanUserinfo.getUser().getIs_followed().equals("2"))
		{
			LinearLayout llBottom=(LinearLayout) view.findViewById(R.id.llBottomLayout);
			llBottom.setVisibility(View.VISIBLE);
			setNonClickable();

		}
		else if(beanUserinfo.getUser().getIs_followed().equals("1"))
		{
			LinearLayout llBottom=(LinearLayout) view.findViewById(R.id.llBottomLayout);
			llBottom.setVisibility(View.VISIBLE);
		}
		else if(beanUserinfo.getUser().getIs_followed().equals("3"))
		{
			LinearLayout llBottom=(LinearLayout) view.findViewById(R.id.llBottomLayout);
			llBottom.setVisibility(View.GONE);
		}
		else
		{
			Toast.makeText(getActivity(), "Error from server", Toast.LENGTH_SHORT).show();
		}


		/////////////////////////////////////////////////////////////////////////////////////////////////////////		

		TextView tvPosts = (TextView) view.findViewById(R.id.tvNoOfPosts);
		tvPosts.setText(String.valueOf(beanUserinfo.getUser().getPost().size()));
		TextView tvNoOfFollowers = (TextView) view.findViewById(R.id.tvNoOfFollowers);
		tvNoOfFollowers.setText(beanUserinfo.getUser().getTotal_followers());
		TextView tvNoOffollowings = (TextView) view.findViewById(R.id.tvNoOfFollowings);
		tvNoOffollowings.setText(beanUserinfo.getUser().getTotal_following());

		TextView tvFrndsFollowingVenue = (TextView) view.findViewById(R.id.tvFrndsFollowingVenue);
		String followingVenue = "";
		if(beanUserinfo.getUser().getTotal_following_venue().equals(""))
			followingVenue = "0";
		else
			followingVenue = beanUserinfo.getUser().getTotal_following_venue();

		tvFrndsFollowingVenue.setText(Html.fromHtml("Following "+"<font color='#FF2763'>"+beanUserinfo.getUser(). getTotal_following_venue()+"</font>"+" venues"));
		//	ListView lvFriendsPosts = (ListView) view.findViewById(R.id.lvFriendsPosts);
		//	lvFriendsPosts.setAdapter(new FriendsPostAdapter(getActivity(), beanUserinfo.getUser()));
		TextView tvComposePost=(TextView) view.findViewById(R.id.tvComposePost);
		if(beanUserinfo.getUser().getIs_followed().equals("1"))
		{
			tvComposePost.setText("Following");
			tvComposePost.setTextColor(getActivity().getResources().getColor(R.color.white));
			tvComposePost.setBackgroundResource(R.drawable.following_btn_shape);
		}
		else if(beanUserinfo.getUser().getIs_followed().equals("2"))
		{
			tvComposePost.setText("Requested");
			tvComposePost.setTextColor(getActivity().getResources().getColor(R.color.white));
			tvComposePost.setBackgroundResource(R.drawable.following_btn_shape);
		}
		else if(beanUserinfo.getUser().getIs_followed().equals("3"))
		{
			tvComposePost.setText("+ Follow");
			tvComposePost.setTextColor(getActivity().getResources().getColor(R.color.background_color));
			tvComposePost.setBackgroundResource(R.drawable.follow_btn_shape);
		}
		else
		{
			Toast.makeText(getActivity(), "Errror in API", Toast.LENGTH_SHORT).show();
		}


		LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout llPosts = (LinearLayout) view.findViewById(R.id.llPosts2);
		final LinearLayout llPosts1 = (LinearLayout) view.findViewById(R.id.llPosts);
		int end;
		if(beanUserinfo.getUser().getPost().size()>5)
		{
			end=5;
		}
		else
			end=beanUserinfo.getUser().getPost().size();
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
			tvPostProfileName.setText(beanUserinfo.getUser().getFirst_name() + " " + beanUserinfo.getUser().getLast_name());


			//posted date
			String postedDate = "";
			TextView tvCreatedDate = (TextView) llInclude.findViewById(R.id.tvPostDate);
			Date postDate = ConstantUtil.convertStringToDate(beanUserinfo.getUser().getPost().get(i).getCreated_at(), "yyyy-MM-dd HH:mm:ss");
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

					if(beanUserinfo.getUser().getProfile_pic() == null || beanUserinfo.getUser().getProfile_pic().equals(""))
					{
						bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
						ivRound.setImageBitmap(bm);
					}
					else
					{
						aq.id(R.id.ivPostProfilePic1).image(ConstantUtil.profilePicBaseUrl + beanUserinfo.getUser().getProfile_pic(), options);
					}
				}
				catch(Exception e)
				{}
			} 


			//set no of likes in both image likes and status likes
			final TextView tvTextPostNumberOflikes = (TextView) llInclude.findViewById(R.id.tvTextPostNoOflikes);
			tvTextPostNumberOflikes.setText(beanUserinfo.getUser().getPost().get(i).getTotal_likes());
			final TextView tvImagePostNumberOfLikes = (TextView) llInclude.findViewById(R.id.tvImagePostNoOfLikes);
			tvImagePostNumberOfLikes.setText(beanUserinfo.getUser().getPost().get(i).getTotal_likes());


			//if both image and status are not blank
			if(!beanUserinfo.getUser().getPost().get(i).getStatus().equals("")&&!beanUserinfo.getUser().getPost().get(i).getImage().equals(""))
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
				tvPostMessage.setText(beanUserinfo.getUser().getPost().get(i).getStatus());

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


					aqPostImage.id(ivPostImageView.getId()).image(ConstantUtil.profilePicBaseUrl + beanUserinfo.getUser().getPost().get(i).getImage(), options);

				}
				catch(Exception e)
				{

				}

			}
			else if(!beanUserinfo.getUser().getPost().get(i).getStatus().equals(""))
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

				tvPostMessage.setText(beanUserinfo.getUser().getPost().get(i).getStatus());

				ImageView ivPostImageView=(ImageView) llInclude.findViewById(R.id.ivPostImage);
				ivPostImageView.setVisibility(View.GONE);
			}
			else if(!beanUserinfo.getUser().getPost().get(i).getImage().equals(""))
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
					Bitmap bm = aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + beanUserinfo.getUser().getPost().get(i).getImage());

					if(bm!=null)
						ivPostImageView.setImageBitmap(bm);
					else
						aqPostImage.id(ivPostImageView.getId()).image(ConstantUtil.profilePicBaseUrl + beanUserinfo.getUser().getPost().get(i).getImage(), options);

				}
				catch(Exception e)
				{}



			}




			if(beanUserinfo.getUser().getPost() == null || beanUserinfo.getUser().getPost().size() == 0)
			{
				View  viewLine = view.findViewById(R.id.view);
				viewLine.setVisibility(View.GONE);
				llPosts1.setVisibility(View.GONE);
			}
			else if(beanUserinfo.getUser().getPost().get(i).getIs_liked().equals("0"))
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
						String postId=beanUserinfo.getUser().getPost().get(id).getId();

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
						String postId=beanUserinfo.getUser().getPost().get(id).getId();

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
					if(beanUserinfo.getUser().getPost().get(id).getIs_liked().equals("0"))
					{
						NetworkTask postLikeTask = new NetworkTask(getActivity(), POST_LIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+beanUserinfo.getUser().getPost().get(id).getId();
						postLikeTask.exposePostExecute(FriendDetailFragment.this);
						postLikeTask.execute(likeUrl);
					}
					else
					{
						NetworkTask postUnLikeTask = new NetworkTask(getActivity(), POST_UNLIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+beanUserinfo.getUser().getPost().get(id).getId();
						postUnLikeTask.exposePostExecute(FriendDetailFragment.this);
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
					if(beanUserinfo.getUser().getPost().get(id).getIs_liked().equals("0"))
					{
						NetworkTask postLikeTask = new NetworkTask(getActivity(), POST_LIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String likeUrl = ConstantUtil.likeUrl + "user_id=" + beanUserinfo.getUser().getID()+"&like_type=post&like_type_id="+beanUserinfo.getUser().getPost().get(id).getId();
						postLikeTask.exposePostExecute(FriendDetailFragment.this);
						postLikeTask.execute(likeUrl);
					}
					else
					{
						NetworkTask postUnLikeTask = new NetworkTask(getActivity(), POST_UNLIKE,id,null);
						AppPreferences appPref = new AppPreferences(getActivity());
						String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" +beanUserinfo.getUser().getID()+"&like_type=post&like_type_id="+beanUserinfo.getUser().getPost().get(id).getId();
						postUnLikeTask.exposePostExecute(FriendDetailFragment.this);
						postUnLikeTask.execute(unlikeUrl);
					}

				}
			});





			if(beanUserinfo.getUser().getPost() == null || beanUserinfo.getUser().getPost().size() == 0)
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
						ConstantUtil.shareDialog(getActivity(),0,beanUserinfo.getUser().getPost().get(id).getStatus(),"",null);
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
						ConstantUtil.shareDialog(getActivity(),0,beanUserinfo.getUser().getPost().get(id).getStatus(),"",null);
						llPosts1.setVisibility(View.VISIBLE);



					}
				});

			}




			llPosts.addView(llInclude);


		}
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
			aq.id(id).image(ConstantUtil.profilePicBaseUrl + beanUserinfo.getUser().getProfile_pic(), options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			ivProfileImage.setImageBitmap(bm);
		}

	}





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
	private void setTitleBar(String friendName)
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(friendName);
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


	public void setNonClickable()
	{
		view.findViewById(R.id.rlFollowers).setOnClickListener(null);
		view.findViewById(R.id.rlFollowing).setOnClickListener(null);
		view.findViewById(R.id.tvFrndsFollowingVenue).setOnClickListener(null);
		view.findViewById(R.id.tvMorePosts).setOnClickListener(null);
		view.findViewById(R.id.tvCall).setOnClickListener(null);
		view.findViewById(R.id.tvText).setOnClickListener(null);



	}




	@Override
	public void resultfromNetwork(String object, int id,int arg1,String arg2) 
	{
		Gson gson = new Gson();
		switch (id) {
		case USER_DETAIL:
			if(object != null && !object.equals(""))
			{
				beanUserinfo = (BeanUserInfo)gson.fromJson(object, BeanUserInfo.class);
				//beanUserInfo=new BeanUserInfo();
				//beanUserInfo.setUser(beanUser);

				setProfileImage(R.id.ivFriendsProfilePic);

				setAllInfo();
			}
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

					int likes = Integer.parseInt(beanUserinfo.getUser().getPost().get(arg1).getTotal_likes());
					likes = ++likes;
					BeanUserPosts posts =beanUserinfo.getUser().getPost().get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("1");
					beanUserinfo.getUser().getPost().set(arg1,posts);
					tvLikes.setText(beanUserinfo.getUser().getPost().get(arg1).getTotal_likes());
					tvLikesImages.setText(beanUserinfo.getUser().getPost().get(arg1).getTotal_likes());

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

					int likes = Integer.parseInt(beanUserinfo.getUser().getPost().get(arg1).getTotal_likes());
					likes = --likes;
					BeanUserPosts posts =beanUserinfo.getUser().getPost().get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("0");
					beanUserinfo.getUser().getPost().set(arg1,posts);
					tvLikes.setText(beanUserinfo.getUser().getPost().get(arg1).getTotal_likes());
					tvLikesImages.setText(beanUserinfo.getUser().getPost().get(arg1).getTotal_likes());

				}
			}
			break;	

		default:
			break;
		}

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

	@Override
	public void onPause()
	{
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);

		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener(((MainScreen) getActivity()));
		super.onPause();
	}


}
