package com.linchpin.clubcrawl.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanFeedInfo;
import com.linchpin.clubcrawl.beans.BeanListFeeds;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.fragments.LikersListFragment;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FeedsListAdapter extends BaseAdapter implements Result, DoinBackgroung{

	private Context mContext;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private BeanListFeeds beanFeedList;
	private final int POST_LIKE = 0;
	private final int POST_UNLIKE = 1;
	private final int IMAGE_FILE = 2;
	private TextView tvLIkes,tvIsLike;
	ViewHolderItem viewHolder;
	File f;
	List<BeanFeedInfo> list=new ArrayList<BeanFeedInfo>();
	private ImageView ivLogoImage;
	public List<BeanFeedInfo> getList() {
		return list;
	}
	public void setList(List<BeanFeedInfo> list) {
		this.list = list;
	}
	private int clickNo;
	public FeedsListAdapter(Context context, List<BeanFeedInfo> list){
		//this.beanFeedList = beanFeedList;
		this.list=list;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.showStubImage(R.drawable.back_header_image)
		.considerExifParams(true)
		.build();
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		// inflate the layout for each item of listView


		convertView = null;

		if(convertView == null)
		{
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.directfeed_list_row, parent, false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();

			viewHolder.tvFeedUsername = (TextView) convertView.findViewById(R.id.tvFeedUsername);
			viewHolder.ivFeedProfilePic = (ImageView) convertView.findViewById(R.id.ivFeedProfilePic);
			viewHolder.ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
			viewHolder.ivFeedImage = (ImageView) convertView.findViewById(R.id.ivFeedImage);
			viewHolder.tvFeedDate = (TextView) convertView.findViewById(R.id.tvFeedDate);
			viewHolder.tvFeedMessage = (TextView) convertView.findViewById(R.id.tvFeedMessage);
			viewHolder.llFeedMessage = (LinearLayout) convertView.findViewById(R.id.llFeedMessage);
			viewHolder.llFeedImage = (LinearLayout) convertView.findViewById(R.id.llFeedImage);
			viewHolder.tvTextFeedNoOflikes = (TextView) convertView.findViewById(R.id.tvTextFeedNoOflikes);
			viewHolder.tvImageFeedNoOfLikes = (TextView) convertView.findViewById(R.id.tvImageFeedNoOfLikes);
			viewHolder.llImageFeedNoLikes=(LinearLayout)convertView.findViewById(R.id.llImageFeedNoLikes);
			viewHolder.llTextFeedLike = (LinearLayout) convertView.findViewById(R.id.llTextFeedLike);
			viewHolder.llTextFeedLikeBtn = (LinearLayout) convertView.findViewById(R.id.llTextFeedLikeBtn);
			viewHolder.llImageFeedLike = (LinearLayout) convertView.findViewById(R.id.llImageFeedLike);
			viewHolder.llShareImageFeed = (LinearLayout) convertView.findViewById(R.id.llShareImageFeed);
			viewHolder.llSharetextFeed = (LinearLayout) convertView.findViewById(R.id.llSharetextFeed);
			viewHolder.rlMsgLikes=(RelativeLayout) convertView.findViewById(R.id.rlMsgLikes);
			viewHolder.rlImageLikes=(RelativeLayout) convertView.findViewById(R.id.rlImageLikes);
			viewHolder.tvStrFeedLike=(TextView) convertView.findViewById(R.id.tvStrFeedLike);
			viewHolder.tvImgFeedLike=(TextView) convertView.findViewById(R.id.tvImgFeedLike);
			viewHolder.rlImageLikes=(RelativeLayout) convertView.findViewById(R.id.rlImageLikes);
			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolderItem)convertView.getTag();

		// object item based on the position
		//final BeanFeedInfo feedInfo = beanFeedList.getFeedList().get(position);

		// assign values if the object is not null
		if(list != null) 
		{
			String postedDate = "";
			viewHolder.tvFeedUsername.setText(list.get(position).getFriend_name());
			Date postDate = ConstantUtil.convertStringToDate(list.get(position).getFeed_date(),"yyyy-MM-dd hh:mm:ss");
			Date currentDate = new Date();
			long dayDifference = ConstantUtil.getDateDiffString(postDate, currentDate);
			if(dayDifference == 0)
				postedDate = "Today";
			else if(dayDifference == 1)
				postedDate = "Yesterday";
			else
				postedDate = dayDifference + " days ago";
			viewHolder.tvFeedDate.setText(postedDate);
			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFriend_image(), viewHolder.ivFeedProfilePic, options, null);
			if(list.get(position).getFeed_item_type().toString().equals("post"))
			{

				// If user or frnd posts some feed


				if(list.get(position).getFeed_message()!="" && list.get(position).getFeed_status()!="")
				{
					// If feeds are both text and image

					//---------------- Setting views visibility --------------------//

					viewHolder.llFeedImage.setVisibility(View.VISIBLE);
					viewHolder.llFeedMessage.setVisibility(View.VISIBLE);
					viewHolder.llTextFeedLike.setVisibility(View.GONE);
					viewHolder.llImageFeedLike.setVisibility(View.VISIBLE);
					viewHolder.rlMsgLikes.setVisibility(View.GONE);
					viewHolder.rlImageLikes.setVisibility(View.VISIBLE);
					viewHolder.tvImageFeedNoOfLikes.setVisibility(View.VISIBLE);
					viewHolder.tvTextFeedNoOflikes.setVisibility(View.GONE);

					//------------------Setting Values-------------------------------//
					
					viewHolder.tvFeedMessage.setText(list.get(position).getFeed_status());
					AQuery aqPostImage = new AQuery(viewHolder.ivFeedImage);
					try
					{
						ImageOptions options = new ImageOptions();
						Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
						options.preset = icon;
						options.fallback=R.drawable.no_img_jst_yet;
						Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message());
						if(bmp!=null)
						{
							viewHolder.ivFeedImage.setImageBitmap(bmp);
						}
						else
							aqPostImage.id(viewHolder.ivFeedImage.getId()).image(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message(), options);

					}
					catch(Exception e)
					{

					}
					//imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message(), viewHolder.ivFeedImage, options, null);
					viewHolder.tvImageFeedNoOfLikes.setText(list.get(position).getTotal_likes().toString());
					if(list.get(position).getUser_liked().equals("1"))
					{
						viewHolder.tvImgFeedLike.setText("Liked");
						viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.background_color));
					}
					else
					{
						viewHolder.tvImgFeedLike.setText("Like");
						viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));

					}

					viewHolder.llImageFeedLike.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							if(list.get(position).getUser_liked().equals("0"))
							{
								NetworkTask postLikeTask = new NetworkTask(mContext, POST_LIKE,position,null);
								AppPreferences appPref = new AppPreferences(mContext);
								String likeUrl = "";
								if(list.get(position).getFeed_type().equals("profile_image"))
									likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								else
									likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								postLikeTask.exposePostExecute(FeedsListAdapter.this);
								postLikeTask.execute(likeUrl);
							}
							else
							{
								NetworkTask postLikeTask = new NetworkTask(mContext, POST_UNLIKE,position,null);
								AppPreferences appPref = new AppPreferences(mContext);
								String likeUrl = "";
								if(list.get(position).getFeed_type().equals("profile_image"))
									likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								else
									likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								postLikeTask.exposePostExecute(FeedsListAdapter.this);
								postLikeTask.execute(likeUrl);
							}



						}
					});

					viewHolder.llShareImageFeed.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							/*	NetworkTask networkTask = new NetworkTask(mContext, IMAGE_FILE, 0, v.getTag().toString());
							networkTask.exposeDoinBackground(FeedsListAdapter.this);
							networkTask.exposePostExecute(FeedsListAdapter.this);
							networkTask.execute(list.get(position).getFeed_status());
							//				File file = imageLoader.getDiscCache().get();
							 */
							ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_TEXT,list.get(position).getFeed_status(),"",null);



						}
					});

					viewHolder.llImageFeedNoLikes.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(!viewHolder.tvImageFeedNoOfLikes.getText().toString().equals("0"))
							{
								String feedId=list.get(position).getFeed_id();

								Bundle bundle = new Bundle();
								bundle.putString("feedId", feedId);
								LikersListFragment fr = new LikersListFragment();
								fr.setArguments(bundle);
								FragmentManager fm =  ((SlidingFragmentActivity) mContext).getSupportFragmentManager();
								FragmentTransaction fragmentTransaction = fm.beginTransaction();
								fragmentTransaction.replace((R.id.frame), fr,"LikersListActivity");
								fragmentTransaction.addToBackStack("LikersListActivity");
								fragmentTransaction.commit();


							}

						}
					});




				}
				else if(list.get(position).getFeed_status()!="")
				{
					// If feed is text only

					//---------------- Setting views visibility --------------------//

					viewHolder.llFeedImage.setVisibility(View.GONE);
					viewHolder.llFeedMessage.setVisibility(View.VISIBLE);
					viewHolder.llTextFeedLike.setVisibility(View.VISIBLE);
					viewHolder.llImageFeedLike.setVisibility(View.GONE);
					viewHolder.rlMsgLikes.setVisibility(View.VISIBLE);
					viewHolder.rlImageLikes.setVisibility(View.GONE);
					viewHolder.tvImageFeedNoOfLikes.setVisibility(View.GONE);
					viewHolder.tvTextFeedNoOflikes.setVisibility(View.VISIBLE);

					//------------------Setting Values-------------------------------//

					viewHolder.tvFeedMessage.setText(list.get(position).getFeed_status());
					viewHolder.tvTextFeedNoOflikes.setText(list.get(position).getTotal_likes().toString());

					if(list.get(position).getUser_liked().equals("1"))
					{
						viewHolder.tvStrFeedLike.setText("Liked");
						viewHolder.tvStrFeedLike.setTextColor(mContext.getResources().getColor(R.color.background_color));
					}
					else
					{
						viewHolder.tvStrFeedLike.setText("Like");
						viewHolder.tvStrFeedLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));

					}

					viewHolder.llTextFeedLikeBtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							if(list.get(position).getUser_liked().equals("0"))
							{


								NetworkTask postLikeTask = new NetworkTask(mContext, POST_LIKE,position,null);
								AppPreferences appPref = new AppPreferences(mContext);
								String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								postLikeTask.exposePostExecute(FeedsListAdapter.this);
								postLikeTask.execute(likeUrl);
							}
							else
							{
								NetworkTask postLikeTask = new NetworkTask(mContext, POST_UNLIKE,position,null);
								AppPreferences appPref = new AppPreferences(mContext);
								String likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								postLikeTask.exposePostExecute(FeedsListAdapter.this);
								postLikeTask.execute(likeUrl);
							}




						}
					});


					viewHolder.llSharetextFeed.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_TEXT,list.get(position).getFeed_status(),"",null);



						}
					});

					viewHolder.llTextFeedLike.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(!viewHolder.tvTextFeedNoOflikes.getText().toString().equals("0"))
							{
								String feedId=list.get(position).getFeed_id();

								Bundle bundle = new Bundle();
								bundle.putString("feedId", feedId);
								LikersListFragment fr = new LikersListFragment();
								fr.setArguments(bundle);
								FragmentManager fm =  ((SlidingFragmentActivity) mContext).getSupportFragmentManager();
								FragmentTransaction fragmentTransaction = fm.beginTransaction();
								fragmentTransaction.replace((R.id.frame), fr,"LikersListActivity");
								fragmentTransaction.addToBackStack("LikersListActivity");
								fragmentTransaction.commit();


							}

						}
					});





				}
				else if(list.get(position).getFeed_message()!="")
				{
					// If feed is image only

					//---------------- Setting views visibility --------------------//
					viewHolder.llFeedImage.setVisibility(View.VISIBLE);
					viewHolder.llFeedMessage.setVisibility(View.GONE);
					viewHolder.llTextFeedLike.setVisibility(View.GONE);
					viewHolder.llImageFeedLike.setVisibility(View.VISIBLE);
					viewHolder.rlMsgLikes.setVisibility(View.GONE);
					viewHolder.rlImageLikes.setVisibility(View.VISIBLE);
					viewHolder.tvImageFeedNoOfLikes.setVisibility(View.VISIBLE);
					viewHolder.tvTextFeedNoOflikes.setVisibility(View.GONE);

					//------------------Setting Values-------------------------------//
					AQuery aqPostImage = new AQuery(viewHolder.ivFeedImage);
					try
					{
						ImageOptions options = new ImageOptions();
						Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
						options.preset = icon;
						options.fallback=R.drawable.no_img_jst_yet;
						Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message());
						if(bmp!=null)
						{
							viewHolder.ivFeedImage.setImageBitmap(bmp);
						}
						else
							aqPostImage.id(viewHolder.ivFeedImage.getId()).image(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message(), options);

					}
					catch(Exception e)
					{

					}
					//imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message(), viewHolder.ivFeedImage, options, null);
					viewHolder.tvImageFeedNoOfLikes.setText(list.get(position).getTotal_likes().toString());
					if(list.get(position).getUser_liked().equals("1"))
					{
						viewHolder.tvImgFeedLike.setText("Liked");
						viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.background_color));
					}
					else
					{
						viewHolder.tvImgFeedLike.setText("Like");
						viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));

					}

					viewHolder.llImageFeedLike.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							if(list.get(position).getUser_liked().equals("0"))
							{
								NetworkTask postLikeTask = new NetworkTask(mContext, POST_LIKE,position,null);
								AppPreferences appPref = new AppPreferences(mContext);
								String likeUrl = "";
								if(list.get(position).getFeed_type().equals("profile_image"))
									likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								else
									likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								postLikeTask.exposePostExecute(FeedsListAdapter.this);
								postLikeTask.execute(likeUrl);
							}
							else
							{
								NetworkTask postLikeTask = new NetworkTask(mContext, POST_UNLIKE,position,null);
								AppPreferences appPref = new AppPreferences(mContext);
								String likeUrl = "";
								if(list.get(position).getFeed_type().equals("profile_image"))
									likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								else
									likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
								postLikeTask.exposePostExecute(FeedsListAdapter.this);
								postLikeTask.execute(likeUrl);
							}



						}
					});

					viewHolder.llShareImageFeed.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							/*	NetworkTask networkTask = new NetworkTask(mContext, IMAGE_FILE, 0, v.getTag().toString());
							networkTask.exposeDoinBackground(FeedsListAdapter.this);
							networkTask.exposePostExecute(FeedsListAdapter.this);
							networkTask.execute(list.get(position).getFeed_status());*/
							//				File file = imageLoader.getDiscCache().get();
							ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_TEXT,list.get(position).getFeed_status(),"",null);




						}
					});

					viewHolder.llImageFeedNoLikes.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(!viewHolder.tvImageFeedNoOfLikes.getText().toString().equals("0"))
							{
								String feedId=list.get(position).getFeed_id();

								Bundle bundle = new Bundle();
								bundle.putString("feedId", feedId);
								LikersListFragment fr = new LikersListFragment();
								fr.setArguments(bundle);
								FragmentManager fm =  ((SlidingFragmentActivity) mContext).getSupportFragmentManager();
								FragmentTransaction fragmentTransaction = fm.beginTransaction();
								fragmentTransaction.replace((R.id.frame), fr,"LikersListActivity");
								fragmentTransaction.addToBackStack("LikersListActivity");
								fragmentTransaction.commit();


							}

						}
					});



				}

			}

			else if(list.get(position).getFeed_item_type().toString().equals("profile_image"))
			{
				// If users frnd changes profile pic


				//---------------- Setting views visibility --------------------//
				viewHolder.llFeedImage.setVisibility(View.VISIBLE);
				viewHolder.llFeedMessage.setVisibility(View.GONE);
				viewHolder.llTextFeedLike.setVisibility(View.GONE);
				viewHolder.llImageFeedLike.setVisibility(View.VISIBLE);
				viewHolder.rlMsgLikes.setVisibility(View.GONE);
				viewHolder.rlImageLikes.setVisibility(View.VISIBLE);
				viewHolder.tvImageFeedNoOfLikes.setVisibility(View.VISIBLE);
				viewHolder.tvTextFeedNoOflikes.setVisibility(View.GONE);

				//------------------Setting Values-------------------------------//

				AQuery aqPostImage = new AQuery(viewHolder.ivFeedImage);
				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;
					Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message());
					if(bmp!=null)
					{
						viewHolder.ivFeedImage.setImageBitmap(bmp);
					}
					else
						aqPostImage.id(viewHolder.ivFeedImage.getId()).image(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message(), options);

				}
				catch(Exception e)
				{

				}
				
				
				
				//imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + list.get(position).getFeed_message(), viewHolder.ivFeedImage, options, null);
				viewHolder.tvImageFeedNoOfLikes.setText(list.get(position).getTotal_likes().toString());
				if(list.get(position).getUser_liked().equals("1"))
				{
					viewHolder.tvImgFeedLike.setText("Liked");
					viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.background_color));
				}
				else
				{
					viewHolder.tvImgFeedLike.setText("Like");
					viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));

				}

				viewHolder.llImageFeedLike.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						if(list.get(position).getUser_liked().equals("0"))
						{
							NetworkTask postLikeTask = new NetworkTask(mContext, POST_LIKE,position,null);
							AppPreferences appPref = new AppPreferences(mContext);
							String likeUrl = "";
							if(list.get(position).getFeed_type().equals("profile_image"))
								likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
							else
								likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
							postLikeTask.exposePostExecute(FeedsListAdapter.this);
							postLikeTask.execute(likeUrl);
						}
						else
						{
							NetworkTask postLikeTask = new NetworkTask(mContext, POST_UNLIKE,position,null);
							AppPreferences appPref = new AppPreferences(mContext);
							String likeUrl = "";
							if(list.get(position).getFeed_type().equals("profile_image"))
								likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=post&like_type_id="+list.get(position).getFeed_id();
							else
								likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type="+list.get(position).getLike_types()+"&like_type_id="+list.get(position).getFeed_id();
							postLikeTask.exposePostExecute(FeedsListAdapter.this);
							postLikeTask.execute(likeUrl);
						}



					}
				});

				viewHolder.llShareImageFeed.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						/*	NetworkTask networkTask = new NetworkTask(mContext, IMAGE_FILE, 0, v.getTag().toString());
						networkTask.exposeDoinBackground(FeedsListAdapter.this);
						networkTask.exposePostExecute(FeedsListAdapter.this);
						networkTask.execute(list.get(position).getFeed_status());*/
						//				File file = imageLoader.getDiscCache().get();
						ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_TEXT,list.get(position).getFeed_status(),"",null);




					}
				});
				viewHolder.llImageFeedNoLikes.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!viewHolder.tvImageFeedNoOfLikes.getText().toString().equals("0"))
						{
							String feedId=list.get(position).getFeed_id();

							Bundle bundle = new Bundle();
							bundle.putString("feedId", feedId);
							LikersListFragment fr = new LikersListFragment();
							fr.setArguments(bundle);
							FragmentManager fm =  ((SlidingFragmentActivity) mContext).getSupportFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();
							fragmentTransaction.replace((R.id.frame), fr,"LikersListActivity");
							fragmentTransaction.addToBackStack("LikersListActivity");
							fragmentTransaction.commit();


						}

					}
				});






			}
			else
			{
				// other case





			}

		}


		viewHolder.ivFeedImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickNo++ ;
				Handler handler = new Handler();
				Runnable r = new Runnable() {

					@Override
					public void run() {

						clickNo = 0;
					}
				};
				if(clickNo==1){
					handler.postDelayed(r, 250);
				}else if(clickNo == 2){
					clickNo = 0;
					final ImageView ivLogo =  (ImageView) v.getTag();

					ivLogo.setVisibility(View.VISIBLE);
					Animation animFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
					animFadeOut.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							ivLogo.setVisibility(View.GONE);

						}
					});
					ivLogo.startAnimation(animFadeOut);


				}


			}
		});




		return convertView;

	}

	static class ViewHolderItem {

		private TextView tvFeedUsername;
		private ImageView ivFeedProfilePic,ivFeedImage,ivLogo;
		private TextView tvFeedDate;
		private TextView tvFeedMessage;
		private TextView tvTextFeedNoOflikes,tvImageFeedNoOfLikes,tvStrFeedLike,tvImgFeedLike;
		private LinearLayout llImageFeedNoLikes;
		private LinearLayout llTextFeedLike,llImageFeedLike;
		private LinearLayout llFeedMessage,llFeedImage;
		private LinearLayout llSharetextFeed,llShareImageFeed,llTextFeedLikeBtn;
		private RelativeLayout rlMsgLikes,rlImageLikes;

	}


	@Override
	public void resultfromNetwork(String object, int id,int position,String arg2) 
	{
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

					/*viewHolder.tvStrFeedLike.setText("Liked");
					viewHolder.tvImgFeedLike.setText("Liked");
					viewHolder.tvStrFeedLike.setTextColor(mContext.getResources().getColor(R.color.background_color));
					viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.background_color));*/
					int likes = Integer.parseInt(list.get(position).getTotal_likes());
					likes = ++likes;
					BeanFeedInfo posts =list.get(position);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setUser_liked("1");
					list.set(position,posts);
					/*viewHolder.tvTextFeedNoOflikes.setText(list.get(position).getTotal_likes());
					viewHolder.tvImageFeedNoOfLikes.setText(list.get(position).getTotal_likes());*/

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
					/*	//showAlertForLike("Status successfully unliked");
					viewHolder.tvStrFeedLike.setText("Like");
					viewHolder.tvImgFeedLike.setText("Like");

					viewHolder.tvStrFeedLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));
					viewHolder.tvImgFeedLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));*/
					int likes = Integer.parseInt(list.get(position).getTotal_likes());
					likes = --likes;
					BeanFeedInfo posts =list.get(position);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setUser_liked("0");
					list.set(position,posts);
					/*	viewHolder.tvTextFeedNoOflikes.setText(list.get(position).getTotal_likes());
					viewHolder.tvImageFeedNoOfLikes.setText(list.get(position).getTotal_likes());*/

				}
				notifyDataSetChanged();
			}
			break;	
		case IMAGE_FILE:
			ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_IMAGE,"",arg2,f);
			break;
		default:
			break;
		}


	}


	@Override
	public String doInBackground(Integer id, String... params) {
		switch (id) {
		case IMAGE_FILE:
			f = ConstantUtil.String_to_File(params[0]);
			break;

		default:
			break;
		}
		return null;
	}




}
