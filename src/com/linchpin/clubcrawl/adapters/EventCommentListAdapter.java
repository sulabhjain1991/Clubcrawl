package com.linchpin.clubcrawl.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanEventCommentInfo;
import com.linchpin.clubcrawl.beans.BeanListEventComents;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class EventCommentListAdapter extends BaseAdapter {

	private BeanListEventComents beanEventCommentList;
	private Context mContext;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	
	public EventCommentListAdapter(Context context, BeanListEventComents beanEventCommentList){
		this.beanEventCommentList = beanEventCommentList;
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
		return beanEventCommentList.getList().size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		// inflate the layout for each item of listView
		ViewHolderItem viewHolder;

		convertView = null;

		if(convertView == null)
		{
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_events_comment, parent, false);
			viewHolder = new ViewHolderItem();
			viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
		//	viewHolder.tvColon = (TextView) convertView.findViewById(R.id.tvColon);
		//	viewHolder.tvComment.setVisibility(View.VISIBLE);
		//	viewHolder.tvColon.setVisibility(View.GONE);
			
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),mContext);
			// well set up the ViewHolder
			
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvEventName);
			viewHolder.ivUserPic = (ImageView) convertView.findViewById(R.id.ivEventLogo);
			viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			
			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolderItem)convertView.getTag();

		// object item based on the position
		final BeanEventCommentInfo eventCommentInfo = beanEventCommentList.getList().get(position);

		// assign values if the object is not null
		if(eventCommentInfo != null) 
		{
					
			viewHolder.tvUserName.setText(eventCommentInfo.getUser());
			viewHolder.tvComment.setText(eventCommentInfo.getComment());
			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + eventCommentInfo.getProfile_pic(), viewHolder.ivUserPic, options, null);
		}

		return convertView;
	
	}

	static class ViewHolderItem {

		private TextView tvUserName;
		private ImageView ivUserPic;
		private TextView tvComment;
		private TextView tvColon;
	}
}
