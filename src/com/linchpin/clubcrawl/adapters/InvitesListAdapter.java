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
import com.linchpin.clubcrawl.beans.BeanInviteInfo;
import com.linchpin.clubcrawl.beans.BeanListInvites;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class InvitesListAdapter extends BaseAdapter{
	
	private BeanListInvites beanInvitesList;
	private Context mContext;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private String searchInvites="", character;

	public InvitesListAdapter(Context context, BeanListInvites beanInvitesList){
		this.beanInvitesList = beanInvitesList;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		try
		{
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		}
		catch(Exception e)
		{
			
		}
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();
	}
	
	public InvitesListAdapter(Context context, BeanListInvites beanInvitesList, String searchInvites, String character){
		this.beanInvitesList = beanInvitesList;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();
		this.searchInvites = searchInvites;
		this.character = character;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beanInvitesList.getEvents().size();
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
			convertView = inflater.inflate(R.layout.adapter_invites_list, parent, false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvEventName = (TextView) convertView.findViewById(R.id.tvEventName);
			viewHolder.tvEventName.setTextColor(mContext.getResources().getColor(R.color.black));
			viewHolder.ivEventLogo = (ImageView) convertView.findViewById(R.id.ivEventLogo);

			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolderItem)convertView.getTag();

		// object item based on the position
		final BeanInviteInfo inviteInfo = beanInvitesList.getEvents().get(position);

		// assign values if the object is not null
		if(inviteInfo != null) 
		{
					
			viewHolder.tvEventName.setText(inviteInfo.getEvent_name());
			imageLoader.displayImage(ConstantUtil.inviteLogoImageBaseUrl + inviteInfo.getLogo(), viewHolder.ivEventLogo, options, null);
		}

		if(searchInvites.equals("Search")){
			if(inviteInfo.getEvent_name().startsWith(character)){
				viewHolder.tvEventName.setText(inviteInfo.getEvent_name());
				imageLoader.displayImage(ConstantUtil.inviteLogoImageBaseUrl + inviteInfo.getLogo(), viewHolder.ivEventLogo, options, null);
			}
		}
		return convertView;
	
	}
	
	static class ViewHolderItem {

		private TextView tvEventName;
		private ImageView ivEventLogo;

	}
	
	

}
