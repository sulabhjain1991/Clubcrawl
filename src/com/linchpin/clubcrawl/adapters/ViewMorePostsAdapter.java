package com.linchpin.clubcrawl.adapters;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUserPosts;
import com.linchpin.clubcrawl.fragments.LikersListFragment;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ViewMorePostsAdapter extends BaseAdapter implements Result{
	Context context;
	List<BeanUserPosts> list;
	public List<BeanUserPosts> getList() {
		return list;
	}
	public void setList(List<BeanUserPosts> list) {
		this.list = list;
	}

	View view;
	private final int POST_LIKE = 5;
	private final int POST_UNLIKE = 6;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	public ViewMorePostsAdapter(Context context,List<BeanUserPosts> list) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.list=list;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ViewHolderItem viewHolder;

		view = null;


		if(view == null)
		{
			LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view=inflater.inflate(R.layout.post_row, null);
			viewHolder = new ViewHolderItem();
			viewHolder.ivProfilPic=(ImageView) view.findViewById(R.id.ivPostProfilePic1);
			viewHolder.tvPostUsername=(TextView) view.findViewById(R.id.tvPostUsername);
			viewHolder.tvPostDate = (TextView) view.findViewById(R.id.tvPostDate);
			viewHolder.tvStrPostLike = (TextView) view.findViewById(R.id.tvStrPostLike);
			viewHolder.tvImgPostLike=(TextView) view.findViewById(R.id.tvImgPostLike);
			viewHolder.tvTextPostNoOflikes=(TextView) view.findViewById(R.id.tvTextPostNoOflikes);
			viewHolder.tvImagePostNoOfLikes=(TextView) view.findViewById(R.id.tvImagePostNoOfLikes);
			viewHolder.tvPostMessage=(TextView) view.findViewById(R.id.tvPostMessage);
			viewHolder.llTextPostLike = (LinearLayout) view.findViewById(R.id.llTextPostLike);
			viewHolder.llImagePostLike = (LinearLayout) view.findViewById(R.id.llImagePostLike);
			viewHolder.llSharetext = (LinearLayout) view.findViewById(R.id.llSharetext);
			viewHolder.llShareImage = (LinearLayout) view.findViewById(R.id.llShareImage);
			viewHolder.llImageLike=(LinearLayout) view.findViewById(R.id.llImageLikes);
			viewHolder.llTextLike=(LinearLayout) view.findViewById(R.id.llTextLike);
		}
		else
			viewHolder = (ViewHolderItem)view.getTag();






		viewHolder.llImageLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!viewHolder.tvTextPostNoOflikes.getText().toString().equals("0")||!viewHolder.tvImagePostNoOfLikes.getText().toString().equals("0"))
				{
					String postId=list.get(position).getId();

					Bundle bundle = new Bundle();
					bundle.putString("feedId", postId);
					LikersListFragment fr = new LikersListFragment();
					fr.setArguments(bundle);
					FragmentManager fm =  ((SlidingFragmentActivity) context).getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace((R.id.frame), fr,"LikersListActivity");
					fragmentTransaction.addToBackStack("LikersListActivity");
					fragmentTransaction.commit();


				}
			}
		});


		viewHolder.llTextLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!viewHolder.tvTextPostNoOflikes.getText().toString().equals("0")||!viewHolder.tvImagePostNoOfLikes.getText().toString().equals("0"))
				{
					String postId=list.get(position).getId();

					Bundle bundle = new Bundle();
					bundle.putString("feedId", postId);
					LikersListFragment fr = new LikersListFragment();
					fr.setArguments(bundle);
					FragmentManager fm =  ((SlidingFragmentActivity) context).getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace((R.id.frame), fr,"LikersListActivity");
					fragmentTransaction.addToBackStack("LikersListActivity");
					fragmentTransaction.commit();


				}
			}
		});




		String postedDate = "";
		//	setProfileImage(R.id.ivPostProfilePic1);
		imageLoader.displayImage(ConstantUtil.profilePicBaseUrl+MenuFragment.beanUserInfo.getUser().getProfile_pic(), viewHolder.ivProfilPic, options, null);
		viewHolder.tvPostUsername.setText(MenuFragment.beanUserInfo.getUser().getFirst_name() + " " + MenuFragment.beanUserInfo.getUser().getLast_name());

		Date postDate = ConstantUtil.convertStringToDate(list.get(position).getCreated_at(), "yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		long dayDifference = ConstantUtil.getDateDiffString(postDate, currentDate);
		if(dayDifference == 0)
			postedDate = "Today";
		else if(dayDifference == 1)
			postedDate = "Yesterday";
		else
			postedDate = dayDifference + " days ago";
		viewHolder.tvPostDate.setText(postedDate);

		viewHolder.tvTextPostNoOflikes.setText(list.get(position).getTotal_likes());
		viewHolder.tvImagePostNoOfLikes.setText(list.get(position).getTotal_likes());

		if(list.get(position).getIs_liked().equals("0"))
		{

			viewHolder.tvStrPostLike.setText("Like");
			viewHolder.tvImgPostLike.setText("Like");
			viewHolder.tvStrPostLike.setTextColor(context.getResources().getColor(R.color.color_not_liked));
			viewHolder.tvImgPostLike.setTextColor(context.getResources().getColor(R.color.color_not_liked));
		}
		else
		{

			viewHolder.tvStrPostLike.setText("Liked");
			viewHolder.tvImgPostLike.setText("Liked");
			viewHolder.tvStrPostLike.setTextColor(context.getResources().getColor(R.color.background_color));
			viewHolder.tvImgPostLike.setTextColor(context.getResources().getColor(R.color.background_color));

		}

		viewHolder.tvPostMessage.setText(list.get(position).getStatus());
		viewHolder.llSharetext.setTag(list.get(position).getStatus().toString());

		viewHolder.llTextPostLike.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v)
			{
				if(list.get(position).getIs_liked().equals("0"))
				{
					NetworkTask postLikeTask = new NetworkTask(context, POST_LIKE,position,null);
					AppPreferences appPref = new AppPreferences(context);
					String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getId();
					postLikeTask.exposePostExecute(ViewMorePostsAdapter.this);
					postLikeTask.execute(likeUrl);
				}
				else
				{
					NetworkTask postUnLikeTask = new NetworkTask(context, POST_UNLIKE,position,null);
					AppPreferences appPref = new AppPreferences(context);
					String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getId();
					postUnLikeTask.exposePostExecute(ViewMorePostsAdapter.this);
					postUnLikeTask.execute(unlikeUrl);
				}

			}
		});
		viewHolder.llImagePostLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(list.get(position).getIs_liked().equals("0"))
				{
					NetworkTask postLikeTask = new NetworkTask(context, POST_LIKE,position,null);
					AppPreferences appPref = new AppPreferences(context);
					String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getId();
					postLikeTask.exposePostExecute(ViewMorePostsAdapter.this);
					postLikeTask.execute(likeUrl);
				}
				else
				{
					NetworkTask postUnLikeTask = new NetworkTask(context, POST_UNLIKE,position,null);
					AppPreferences appPref = new AppPreferences(context);
					String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getId();
					postUnLikeTask.exposePostExecute(ViewMorePostsAdapter.this);
					postUnLikeTask.execute(unlikeUrl);
				}

			}
		});
		viewHolder.llSharetext.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				ConstantUtil.shareDialog(context,0,list.get(position).getStatus(),"",null);

			}
		});
		viewHolder.llShareImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConstantUtil.shareDialog(context,0,list.get(position).getStatus(),"",null);

			}
		});
		if(!list.get(position).getStatus().equals("")&&!list.get(position).getImage().equals(""))
		{
			//layout for status post
			LinearLayout llPostMessage=(LinearLayout) view.findViewById(R.id.llPostMessage);
			llPostMessage.setVisibility(View.VISIBLE);

			//layout for image post
			LinearLayout llPostImage=(LinearLayout) view.findViewById(R.id.llPostImage);
			llPostImage.setVisibility(View.VISIBLE);

			//layout for like and share of status
			RelativeLayout rlMsgLikes=(RelativeLayout) view.findViewById(R.id.rlMsgLikes);
			rlMsgLikes.setVisibility(View.GONE);

			//layout for like and share of image
			RelativeLayout rlImage=(RelativeLayout) view.findViewById(R.id.rlImageLikes);
			rlImage.setVisibility(View.VISIBLE);

			//layout for no of likes on status
			LinearLayout llText=(LinearLayout) view.findViewById(R.id.llTextLike);
			llText.setVisibility(View.GONE);

			LinearLayout llImage=(LinearLayout) view.findViewById(R.id.llImageLikes);
			llImage.setVisibility(View.VISIBLE);

			//message text
			TextView tvPostMessage = (TextView) view.findViewById(R.id.tvPostMessage);
			tvPostMessage.setVisibility(View.VISIBLE);
			tvPostMessage.setText(list.get(position).getStatus());

			//image posted
			ImageView ivPostImageView=(ImageView) view.findViewById(R.id.ivPostImage);
			ivPostImageView.setVisibility(View.VISIBLE);
			AQuery aqPostImage = new AQuery(ivPostImageView);

			try
			{
				ImageOptions options = new ImageOptions();
				Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_img_jst_yet);
				options.preset = icon;
				options.fallback=R.drawable.no_img_jst_yet;
				Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getPost().get(position).getImage());
				if(bmp!=null)
				{
					ivPostImageView.setImageBitmap(bmp);
				}
				else
					aqPostImage.id(ivPostImageView.getId()).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getPost().get(position).getImage(), options);

			}
			catch(Exception e)
			{

			}

		}
		else if(!list.get(position).getStatus().equals(""))
		{
			//layout for messages
			LinearLayout llPostMessage=(LinearLayout) view.findViewById(R.id.llPostMessage);
			llPostMessage.setVisibility(View.VISIBLE);

			//layout for image
			LinearLayout llPostImage=(LinearLayout) view.findViewById(R.id.llPostImage);
			llPostImage.setVisibility(View.GONE);

			RelativeLayout rlMsgLikes=(RelativeLayout) view.findViewById(R.id.rlMsgLikes);
			rlMsgLikes.setVisibility(View.VISIBLE);

			LinearLayout llText=(LinearLayout) view.findViewById(R.id.llTextLike);
			llText.setVisibility(View.VISIBLE);

			LinearLayout llImage=(LinearLayout) view.findViewById(R.id.llImageLikes);
			llImage.setVisibility(View.GONE);
			TextView tvPostMessage = (TextView) view.findViewById(R.id.tvPostMessage);
			tvPostMessage.setVisibility(View.VISIBLE);

			tvPostMessage.setText(list.get(position).getStatus());

			ImageView ivPostImageView=(ImageView) view.findViewById(R.id.ivPostImage);
			ivPostImageView.setVisibility(View.GONE);
		}
		else if(!list.get(position).getImage().equals(""))
		{

			LinearLayout llPostMessage=(LinearLayout) view.findViewById(R.id.llPostMessage);
			llPostMessage.setVisibility(View.GONE);

			ImageView ivPostImageView=(ImageView) view.findViewById(R.id.ivPostImage);
			ivPostImageView.setVisibility(View.VISIBLE);

			RelativeLayout rlImage=(RelativeLayout) view.findViewById(R.id.rlImageLikes);
			rlImage.setVisibility(View.VISIBLE);

			LinearLayout llImage=(LinearLayout) view.findViewById(R.id.llImageLikes);
			llImage.setVisibility(View.VISIBLE);

			AQuery aqPostImage = new AQuery(ivPostImageView);

			try
			{
				ImageOptions options = new ImageOptions();
				Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_img_jst_yet);
				options.preset = icon;
				options.fallback=R.drawable.no_img_jst_yet;
				Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + list.get(position).getImage());
				if(bmp!=null)
				{
					ivPostImageView.setImageBitmap(bmp);
				}
				else
					aqPostImage.id(ivPostImageView.getId()).image(ConstantUtil.profilePicBaseUrl + list.get(position).getImage(), options);

			}
			catch(Exception e)
			{}



		}

		return view;
	}

	public void setProfileImage(int id)
	{
		ImageView ivProfileImage = (ImageView) view.findViewById(id);
		AQuery aq = new AQuery(ivProfileImage);

		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;
			aq.id(id).image(ConstantUtil.profilePicBaseUrl + MenuFragment.beanUserInfo.getUser().getProfile_pic(), options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_img_jst_yet);
			ivProfileImage.setImageBitmap(bm);
		}

	}
	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {


		Gson gson = new Gson();
		switch (id) {

		case POST_LIKE:
			if(object != null && !object.equals(""))
			{
				BeanResponse res = (BeanResponse)gson.fromJson(object, BeanResponse.class);

				if(res.getStatus().equals("error"))
				{
					//showAlertForLike(res.getMessage());
				}
				else
				{
					//showAlertForLike("Status successfully liked");

					int likes = Integer.parseInt(list.get(arg1).getTotal_likes());
					likes = ++likes;
					BeanUserPosts posts =list.get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("1");
					list.set(arg1,posts);
					//tvLikes.setText(MenuFragment.beanUserInfo.getUser().getPost().get(0).getTotal_likes());

				}
				notifyDataSetChanged();
			}
			break;	
		case POST_UNLIKE:
			if(object != null && !object.equals(""))
			{
				BeanResponse res = (BeanResponse)gson.fromJson(object, BeanResponse.class);

				if(res.getStatus().equals("error"))
				{
					//showAlertForLike(res.getMessage());
				}
				else
				{
					//showAlertForLike("Status successfully unliked");

					int likes = Integer.parseInt(list.get(arg1).getTotal_likes());
					likes = --likes;
					BeanUserPosts posts =list.get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("0");
					list.set(arg1,posts);

				}
				notifyDataSetChanged();
			}
			break;	

		default:
			break;
		}
	}

	static class ViewHolderItem {

		private TextView tvPostUsername,tvPostDate,tvIsLiked,tvNoOfLikes,tvPost;
		private ImageView ivProfilPic,ivPostImage;
		private TextView tvImagePostNoOfLikes,tvStrPostLike,tvTextPostNoOflikes,tvPostMessage,tvImgPostLike;
		LinearLayout llTextPostLike,llSharetext,llImagePostLike,llShareImage,llTextLike,llImageLike;

	}

}
