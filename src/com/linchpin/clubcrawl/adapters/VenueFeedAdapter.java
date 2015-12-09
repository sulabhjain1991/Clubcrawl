package com.linchpin.clubcrawl.adapters;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
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
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanListVenueFeed;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanVenueFeedInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class VenueFeedAdapter extends BaseAdapter implements Result,DoinBackgroung{

	private Context mContext;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private BeanListVenueFeed beanFeedList;
	private final int POST_LIKE = 0;
	private final int POST_UNLIKE = 1;
	private final int IMAGE_FILE = 2;
	private TextView tvLIkes,tvIsLike;
	ViewHolderItem viewHolder;
	//private int position;
	private ImageView ivLogoImage;
	File f;
	
private int clickNo;
	public VenueFeedAdapter(Context context, BeanListVenueFeed beanFeedList){
		this.beanFeedList = beanFeedList;
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
		return beanFeedList.getPost().size();
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
			convertView = inflater.inflate(R.layout.venue_feed_list_row, parent, false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvFriendName = (TextView) convertView.findViewById(R.id.tvFriendName);
			viewHolder.ivFriendPic = (ImageView) convertView.findViewById(R.id.ivFriendImage);
			viewHolder.ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
			viewHolder.ivFeedPic = (ImageView) convertView.findViewById(R.id.ivFeedPic);
			viewHolder.tvFeedDate = (TextView) convertView.findViewById(R.id.tvFeedDate);
			viewHolder.tvFeedString = (TextView) convertView.findViewById(R.id.tvFeedString);
			viewHolder.llFeedMessage = (LinearLayout) convertView.findViewById(R.id.llFeedMessage);
			viewHolder.llPOst = (LinearLayout) convertView.findViewById(R.id.llPost);
			viewHolder.rlMsgs = (RelativeLayout) convertView.findViewById(R.id.rlMsgLikes);
			viewHolder.llFeedImage = (LinearLayout) convertView.findViewById(R.id.llFeedImage);
			viewHolder.tvLikesMessage = (TextView) convertView.findViewById(R.id.tvTextPostNumberOflikes);
			viewHolder.tvLikesImages = (TextView) convertView.findViewById(R.id.tvImagePostNumberOfLikes);
			viewHolder.tvLikeMessagebtn = (TextView) convertView.findViewById(R.id.tvTextPostLike);
			viewHolder.tvLikeImagebtn = (TextView) convertView.findViewById(R.id.tvImagePostLike);
			viewHolder.llShareImage = (LinearLayout) convertView.findViewById(R.id.llShareImage);
			viewHolder.llShareText = (LinearLayout) convertView.findViewById(R.id.llSharetext);
			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolderItem)convertView.getTag();

		// object item based on the position
		final BeanVenueFeedInfo feedInfo = beanFeedList.getPost().get(position);

		// assign values if the object is not null
		if(feedInfo != null) 
		{

			viewHolder.tvFriendName.setText(feedInfo.getVenue_name());
			viewHolder.tvFeedDate.setText(ConstantUtil.changeDateFormat(feedInfo.getDate(),"yyyy-MM-dd hh:mm:ss","MMMM dd, yyyy"));
			imageLoader.displayImage(ConstantUtil.OffersImageBaseUrl + feedInfo.getVenue_img(), viewHolder.ivFriendPic, options, null);
			if(beanFeedList.getPost().get(position).getPost_img().equals(""))
			{
				viewHolder.llShareText.setTag(feedInfo.getPost().toString());
				viewHolder.tvFeedString.setText(feedInfo.getPost());
				viewHolder.llFeedImage.setVisibility(View.GONE);
				viewHolder.llFeedMessage.setVisibility(View.VISIBLE);
				viewHolder.tvLikesMessage.setText(beanFeedList.getPost().get(position).getTotal_like());
				viewHolder.tvLikeMessagebtn.setTag(viewHolder.tvLikesMessage);
				if(beanFeedList.getPost().get(position).getIs_liked().equals("0"))
				{
					viewHolder.tvLikeMessagebtn.setText("Like");
					viewHolder.tvLikeMessagebtn.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));
				}
				else
				{
					viewHolder.tvLikeMessagebtn.setText("Liked");
					viewHolder.tvLikeMessagebtn.setTextColor(mContext.getResources().getColor(R.color.background_color));
				}

			}
			else if(beanFeedList.getPost().get(position).getPost().equals(""))
			{
				viewHolder.llShareImage.setTag(ConstantUtil.venueFeedsImageBaseUrl + feedInfo.getPost_img().toString());
				viewHolder.llFeedImage.setVisibility(View.VISIBLE);
				viewHolder.llFeedMessage.setVisibility(View.GONE);
				viewHolder.tvLikesImages.setText(beanFeedList.getPost().get(position).getTotal_like());
				viewHolder.tvLikeImagebtn.setTag(viewHolder.tvLikesImages);
				viewHolder.ivFeedPic.setTag(viewHolder.ivLogo);
				imageLoader.displayImage(ConstantUtil.venueFeedsImageBaseUrl + feedInfo.getPost_img(), viewHolder.ivFeedPic, options, null);
				if(beanFeedList.getPost().get(position).getIs_liked().equals("0"))
				{
					viewHolder.tvLikeImagebtn.setText("Like");
					viewHolder.tvLikeImagebtn.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));
				}
				else
				{
					viewHolder.tvLikeImagebtn.setText("Liked");
					viewHolder.tvLikeImagebtn.setTextColor(mContext.getResources().getColor(R.color.background_color));
				}
			}
			else
			{
				viewHolder.llShareImage.setTag(ConstantUtil.venueFeedsImageBaseUrl + feedInfo.getPost_img().toString());
				viewHolder.tvFeedString.setText(feedInfo.getPost());
				viewHolder.llFeedImage.setVisibility(View.VISIBLE);
				viewHolder.llFeedMessage.setVisibility(View.VISIBLE);
				viewHolder.rlMsgs.setVisibility(View.GONE);
				viewHolder.llPOst.setVisibility(View.GONE);
				viewHolder.tvLikesImages.setText(beanFeedList.getPost().get(position).getTotal_like());
				viewHolder.tvLikeImagebtn.setTag(viewHolder.tvLikesImages);
				viewHolder.ivFeedPic.setTag(viewHolder.ivLogo);
				imageLoader.displayImage(ConstantUtil.venueFeedsImageBaseUrl + feedInfo.getPost_img(), viewHolder.ivFeedPic, options, null);
				if(beanFeedList.getPost().get(position).getIs_liked().equals("0"))
				{
					viewHolder.tvLikeImagebtn.setText("Like");
					viewHolder.tvLikeImagebtn.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));
				}
				else
				{
					viewHolder.tvLikeImagebtn.setText("Liked");
					viewHolder.tvLikeImagebtn.setTextColor(mContext.getResources().getColor(R.color.background_color));
				}
				
			}




		}
		viewHolder.llShareImage.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				NetworkTask networkTask = new NetworkTask(mContext, IMAGE_FILE, 0, v.getTag().toString());
				networkTask.exposeDoinBackground(VenueFeedAdapter.this);
				networkTask.exposePostExecute(VenueFeedAdapter.this);
				networkTask.execute(v.getTag().toString());
				
			}
		});
		viewHolder.llShareText.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_TEXT,v.getTag().toString(),"",null);
				
			}
		});
		 
          viewHolder.ivFeedPic.setOnClickListener(new OnClickListener() {
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
		viewHolder.tvLikeMessagebtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(beanFeedList.getPost().get(position).getIs_liked().equals("0"))
				{
					tvLIkes = (TextView)(v.getTag());
					tvIsLike = (TextView)(v);
					NetworkTask postLikeTask = new NetworkTask(mContext, POST_LIKE,position,null);
					AppPreferences appPref = new AppPreferences(mContext);
					String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=venue_feed&like_type_id="+beanFeedList.getPost().get(position).getPost_id();
					postLikeTask.exposePostExecute(VenueFeedAdapter.this);
					postLikeTask.execute(likeUrl);
				}
				else
				{
					tvLIkes = (TextView)(v.getTag());
					tvIsLike = (TextView)(v);
					NetworkTask postLikeTask = new NetworkTask(mContext, POST_UNLIKE,position,null);
					AppPreferences appPref = new AppPreferences(mContext);
					String likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=venue_feed&like_type_id="+beanFeedList.getPost().get(position).getPost_id();
					postLikeTask.exposePostExecute(VenueFeedAdapter.this);
					postLikeTask.execute(likeUrl);
				}


			}
		});

		viewHolder.tvLikeImagebtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(beanFeedList.getPost().get(position).getIs_liked().equals("0"))
				{
					tvLIkes = (TextView)(v.getTag());
					tvIsLike = (TextView)(v);
					NetworkTask postLikeTask = new NetworkTask(mContext, POST_LIKE,position,null);
					AppPreferences appPref = new AppPreferences(mContext);
					String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=venue_feed&like_type_id="+beanFeedList.getPost().get(position).getPost_id();;
					postLikeTask.exposePostExecute(VenueFeedAdapter.this);
					postLikeTask.execute(likeUrl);
				}
				else
				{
					tvLIkes = (TextView)(v.getTag());
					tvIsLike = (TextView)(v);
					NetworkTask postLikeTask = new NetworkTask(mContext, POST_UNLIKE,position,null);
					AppPreferences appPref = new AppPreferences(mContext);
					String likeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=venue_feed&like_type_id="+beanFeedList.getPost().get(position).getPost_id();;
					postLikeTask.exposePostExecute(VenueFeedAdapter.this);
					postLikeTask.execute(likeUrl);
				


			}
			}
		});


		return convertView;

	}

	static class ViewHolderItem {

		private TextView tvFriendName;
		private ImageView ivFriendPic,ivFeedPic,ivLogo;
		private TextView tvFeedDate;
		private TextView tvFeedString;
		private TextView tvLikesMessage,tvLikesImages;
		private TextView tvLikeMessagebtn,tvLikeImagebtn;
		private LinearLayout llFeedMessage,llFeedImage,llPOst;
		private RelativeLayout rlMsgs;
		private LinearLayout llShareImage,llShareText;

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

					tvIsLike.setText("Liked");
					tvIsLike.setTextColor(mContext.getResources().getColor(R.color.background_color));
					int likes = Integer.parseInt(beanFeedList.getPost().get(position).getTotal_like());
					likes = ++likes;
					BeanVenueFeedInfo posts =beanFeedList.getPost().get(position);
					posts.setTotal_like(String.valueOf(likes));
					posts.setIs_liked("1");
					beanFeedList.getPost().set(position,posts);
					tvLIkes.setText(beanFeedList.getPost().get(position).getTotal_like());

				}
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
					tvIsLike.setText("Like");
					tvIsLike.setTextColor(mContext.getResources().getColor(R.color.color_not_liked));
					int likes = Integer.parseInt(beanFeedList.getPost().get(position).getTotal_like());
					likes = --likes;
					BeanVenueFeedInfo posts =beanFeedList.getPost().get(position);
					posts.setTotal_like(String.valueOf(likes));
					posts.setIs_liked("0");
					beanFeedList.getPost().set(position,posts);
					tvLIkes.setText(beanFeedList.getPost().get(position).getTotal_like());

				}
			}
			break;	
		case IMAGE_FILE:
			ConstantUtil.shareDialog(mContext,ConstantUtil.SHARE_IMAGE,"",arg2,f);

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
