package com.linchpin.clubcrawl.adapters;

import java.util.Date;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUser;
import com.linchpin.clubcrawl.beans.BeanUserPosts;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FriendsPostAdapter extends BaseAdapter implements Result{
	Context context;
	BeanUser beanUser;
	View view;
	private final int POST_LIKE = 5;
	private final int POST_UNLIKE = 6;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	public FriendsPostAdapter(Context context,BeanUser beanUser) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.beanUser=beanUser;
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
		return beanUser.getPost().size();
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
		ViewHolderItem viewHolder;

		view = null;
		
		
		if(view == null)
		{
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 view=inflater.inflate(R.layout.postslist_single_row, null);
		 viewHolder = new ViewHolderItem();
		viewHolder.ivProfilPic=(ImageView) view.findViewById(R.id.ivPostProfilePic);
		 viewHolder.tvPostUsername=(TextView) view.findViewById(R.id.tvPostUsername);
		  viewHolder.tvStatus = (TextView) view.findViewById(R.id.tvPostDate);
			viewHolder.tvIsLiked = (TextView) view.findViewById(R.id.tvLike);
			viewHolder.tvNoOfLikes=(TextView) view.findViewById(R.id.tvNoOfLikes);
			viewHolder.tvPost=(TextView) view.findViewById(R.id.tvPost);
			viewHolder.rlLike = (RelativeLayout) view.findViewById(R.id.rlLike);
			viewHolder.rlShare = (RelativeLayout) view.findViewById(R.id.rlShare);
		 
		}
		else
			viewHolder = (ViewHolderItem)view.getTag();
		// TODO Auto-generated method stub
		
		
		String postedDate = "";
		imageLoader.displayImage(ConstantUtil.profilePicBaseUrl+beanUser.getProfile_pic(), viewHolder.ivProfilPic, options, null);
		
		viewHolder.tvPostUsername.setText(beanUser.getFirst_name() + " " + beanUser.getLast_name());
		Date postDate = ConstantUtil.convertStringToDate(beanUser.getPost().get(position).getCreated_at(), "yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		long dayDifference = ConstantUtil.getDateDiffString(postDate, currentDate);
		if(dayDifference == 0)
			postedDate = "Today";
		else if(dayDifference == 1)
			postedDate = "Yesterday";
		else
			postedDate = dayDifference + " days ago";
		viewHolder.tvStatus.setText(postedDate);
		viewHolder.tvNoOfLikes.setText(beanUser.getPost().get(position).getTotal_likes());
		
		if(beanUser.getPost().get(position).getIs_liked().equals("0"))
		{
			
			viewHolder.tvIsLiked.setText("Like");
			viewHolder.tvIsLiked.setTextColor(context.getResources().getColor(R.color.color_not_liked));
		}
		else
		{
			
			viewHolder.tvIsLiked.setText("Liked");
			viewHolder.tvIsLiked.setTextColor(context.getResources().getColor(R.color.background_color));

		}
		viewHolder.tvPost.setText(beanUser.getPost().get(position).getStatus());
		viewHolder.rlShare.setTag(beanUser.getPost().get(position).getStatus().toString());
		viewHolder.rlShare.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				ConstantUtil.shareDialog(context,ConstantUtil.SHARE_TEXT,v.getTag().toString(),"",null);
				
			}
		});

		viewHolder.rlLike.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v)
			{
				if(beanUser.getPost().get(position).getIs_liked().equals("0"))
				{
					NetworkTask postLikeTask = new NetworkTask(context, POST_LIKE,position,null);
					AppPreferences appPref = new AppPreferences(context);
					String likeUrl = ConstantUtil.likeUrl + "user_id=" + appPref.getUserId()+"&like_type=status&like_type_id="+beanUser.getPost().get(position).getId();
					postLikeTask.exposePostExecute(FriendsPostAdapter.this);
					postLikeTask.execute(likeUrl);
				}
				else
				{
					NetworkTask postUnLikeTask = new NetworkTask(context, POST_UNLIKE,position,null);
					AppPreferences appPref = new AppPreferences(context);
					String unlikeUrl = ConstantUtil.unlikeUrl + "user_id=" + appPref.getUserId()+"&like_type=status&like_type_id="+beanUser.getPost().get(position).getId();
					postUnLikeTask.exposePostExecute(FriendsPostAdapter.this);
					postUnLikeTask.execute(unlikeUrl);
				}

			}
		});

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
			aq.id(id).image(ConstantUtil.profilePicBaseUrl + beanUser.getProfile_pic(), options);
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
				
					int likes = Integer.parseInt(beanUser.getPost().get(arg1).getTotal_likes());
					likes = ++likes;
					BeanUserPosts posts =beanUser.getPost().get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("1");
					beanUser.getPost().set(arg1,posts);
					//tvLikes.setText(beanUser.getPost().get(0).getTotal_likes());

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
					
					int likes = Integer.parseInt(beanUser.getPost().get(arg1).getTotal_likes());
					likes = --likes;
					BeanUserPosts posts =beanUser.getPost().get(arg1);
					posts.setTotal_likes(String.valueOf(likes));
					posts.setIs_liked("0");
					beanUser.getPost().set(arg1,posts);

				}
				notifyDataSetChanged();
			}
			break;	

		default:
			break;
		}
	}
	
	static class ViewHolderItem {

		private TextView tvPostUsername,tvStatus,tvIsLiked,tvNoOfLikes,tvPost;
		private ImageView ivProfilPic;
		RelativeLayout rlLike,rlShare;

	}
}
