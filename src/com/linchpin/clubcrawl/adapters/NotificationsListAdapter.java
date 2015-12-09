package com.linchpin.clubcrawl.adapters;

import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanNotificationInfo;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class NotificationsListAdapter extends BaseAdapter implements Result {

	private Context mContext;
	private Cursor cursor;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	List<BeanNotificationInfo> beanNotificationsList;
	private BeanResponse beanResponse;
	private int position=-1;
	private final int FRIEND_REQUEST=0;








	public NotificationsListAdapter(Context context, List<BeanNotificationInfo> beanNotificationsList){
		this.beanNotificationsList = beanNotificationsList;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beanNotificationsList.size();
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolderItem viewHolder;
		this.position = position;
		convertView = null;

		if(convertView == null)
		{
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_notifications_list, parent, false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvFriendName = (TextView) convertView.findViewById(R.id.tvFriendName);
			viewHolder.ivFriendImage = (ImageView) convertView.findViewById(R.id.ivFriendImage);
			viewHolder.tvNotificationDate = (TextView) convertView.findViewById(R.id.tvNotificationDate);
			viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
			viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
			viewHolder.ivlikedPhoto = (ImageView) convertView.findViewById(R.id.ivlikedPhoto);
			viewHolder.rlLiked = (RelativeLayout) convertView.findViewById(R.id.rlLiked);
			viewHolder.rlStatus=(RelativeLayout) convertView.findViewById(R.id.rlStatus);
			viewHolder.ivStatusImage=(ImageView) convertView.findViewById(R.id.ivStatusImage);

			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolderItem)convertView.getTag();

		// object item based on the position
		final BeanNotificationInfo notificationsInfo = beanNotificationsList.get(position);

		// assign values if the object is not null
		if(notificationsInfo != null) 
		{
			String postedDate = "";

			viewHolder.tvFriendName.setText(notificationsInfo.getUser_name());
			Date postDate = ConstantUtil.convertStringToDate(notificationsInfo.getNotification_date(),"yyyy-MM-dd HH:mm:ss");
			Date currentDate = new Date();
			long dayDifference = ConstantUtil.getDateDiffString(postDate, currentDate);
			if(dayDifference == 0)
				postedDate = "Today";
			else if(dayDifference == 1)
				postedDate = "Yesterday";
			else
				postedDate = dayDifference + " days ago";
			viewHolder.tvNotificationDate.setText(postedDate);

			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + notificationsInfo.getUser_image(), viewHolder.ivFriendImage, options, null);
			if(notificationsInfo.getPost_type().toString().equals("image"))
			{
				String msg = notificationsInfo.getMsg();
				int len = notificationsInfo.getUser_name().length();
				msg = msg.substring(len+1);
				viewHolder.tvMsg.setText(msg);
				viewHolder.rlLiked.setVisibility(View.VISIBLE);
				viewHolder.rlStatus.setVisibility(View.GONE);
				//imageLoader.displayImage(ConstantUtil.profilePicBaseUrl+notificationsInfo.getPost_img(),, options,null);
				AQuery aqPostImage = new AQuery(viewHolder.ivlikedPhoto);
				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;
					Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl+notificationsInfo.getPost_img());
					if(bmp!=null)
					{
						viewHolder.ivlikedPhoto.setImageBitmap(bmp);
					}
					else
						aqPostImage.id(viewHolder.ivlikedPhoto.getId()).image(ConstantUtil.profilePicBaseUrl + notificationsInfo.getPost_img(), options);

				}
				catch(Exception e)
				{

				}

			}
			else if(notificationsInfo.getPost_type().toString().equals("status and image"))
			{
				String msg = notificationsInfo.getMsg();
				int len = notificationsInfo.getUser_name().length();
				msg = msg.substring(len+1);
				viewHolder.tvMsg.setText(msg);
				viewHolder.rlLiked.setVisibility(View.GONE);
				viewHolder.rlStatus.setVisibility(View.VISIBLE);
				viewHolder.tvStatus.setText(notificationsInfo.getMessage().toString());
				viewHolder.ivStatusImage.setVisibility(View.VISIBLE);
				AQuery aqPostImage = new AQuery(viewHolder.ivStatusImage);
				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;
					Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.profilePicBaseUrl + notificationsInfo.getPost_img());
					if(bmp!=null)
					{
						viewHolder.ivStatusImage.setImageBitmap(bmp);
					}
					else
						aqPostImage.id(viewHolder.ivStatusImage.getId()).image(ConstantUtil.profilePicBaseUrl + notificationsInfo.getPost_img(), options);

				}
				catch(Exception e)
				{

				}

			}
			else if(notificationsInfo.getPost_type().toString().equals("status"))
			{
				String msg = notificationsInfo.getMsg();
				int len = notificationsInfo.getUser_name().length();
				msg = msg.substring(len+1);
				viewHolder.tvMsg.setText(msg);
				viewHolder.rlLiked.setVisibility(View.GONE);
				viewHolder.rlStatus.setVisibility(View.VISIBLE);
				viewHolder.ivStatusImage.setVisibility(View.GONE);
				viewHolder.tvStatus.setText(notificationsInfo.getMessage().toString());
			}

			else if(notificationsInfo.getPost_type().toString().equals("follow"))
			{
				String msg = notificationsInfo.getMsg();
				viewHolder.tvMsg.setText(msg);
				viewHolder.rlLiked.setVisibility(View.GONE);
				viewHolder.rlStatus.setVisibility(View.GONE);
				viewHolder.ivStatusImage.setVisibility(View.GONE);
			}





			/*
			 * 
			 * 
			 * Don't delete
			 * if(notificationsInfo.getType().equals("st"))
			{
				viewHolder.tvMsg1.setVisibility(View.VISIBLE);
				viewHolder.rlLiked.setVisibility(View.GONE);
				viewHolder.tvMsg1.setText(notificationsInfo.getImage());

			}
			else if(!notificationsInfo.getType().equals("fb"))
			{
				viewHolder.tvMsg1.setVisibility(View.GONE);
				viewHolder.rlLiked.setVisibility(View.VISIBLE);
				imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + notificationsInfo.getImage(), viewHolder.ivlikedPhoto, options, null);
			}
			else if(!notificationsInfo.getF_status().equals("4"))
			{
				viewHolder.tvMsg1.setVisibility(View.GONE);
				viewHolder.rlLiked.setVisibility(View.VISIBLE);
				viewHolder.ivlikedPhoto.setImageResource(R.drawable.add_user);
			}
			else
			{
				viewHolder.tvMsg1.setVisibility(View.VISIBLE);
				viewHolder.rlLiked.setVisibility(View.GONE);
				viewHolder.ivlikedPhoto.setImageResource(R.drawable.add_user); 
			}*/

		}
		/*		
		 * 
		 * 
		 * 
		 * 
		 * 
		 * DOn't delete
		 * 
		 * 
		 * viewHolder.ivlikedPhoto.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{

				if(notificationsInfo.getType().equals("fb"))
				{
					NetworkTask networkTask = new NetworkTask(mContext,
							FRIEND_REQUEST,NotificationsListAdapter.this.position,null);
					networkTask.setProgressDialog(true);
					networkTask.exposePostExecute(NotificationsListAdapter.this);
					networkTask.execute(ConstantUtil.friendRequestUrl
							+ "userID="
							+ notificationsInfo.getFriendId() + "&myID="
							+ notificationsInfo.getUser_id());
				}


			}
		});*/


		return convertView;
	}



	static class ViewHolderItem {

		private TextView tvFriendName;
		private ImageView ivFriendImage;
		private TextView tvNotificationDate;
		private TextView tvMsg,tvStatus;
		private RelativeLayout rlLiked,rlStatus;
		private ImageView ivlikedPhoto,ivStatusImage;

	}

	public List<BeanNotificationInfo> getBeanNotificationsList() {
		return beanNotificationsList;
	}

	public void setBeanNotificationsList(
			List<BeanNotificationInfo> beanNotificationsList) {
		this.beanNotificationsList = beanNotificationsList;
	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) 
	{
		if(object != null && !object.equals(""))
		{
			Gson gson = new Gson();
			BeanResponse response = (BeanResponse)gson.fromJson(object, BeanResponse.class);
			if(response != null)
			{
				BeanNotificationInfo notificationInfo = beanNotificationsList.get(arg1);
				notificationInfo.setF_status("3");
				notifyDataSetChanged();
			}
		}


	}
}
