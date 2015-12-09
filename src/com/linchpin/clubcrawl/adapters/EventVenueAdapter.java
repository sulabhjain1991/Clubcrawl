package com.linchpin.clubcrawl.adapters;

import java.util.HashMap;
import java.util.List;
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
import com.linchpin.clubcrawl.beans.BeanListVenueEvent;
import com.linchpin.clubcrawl.beans.BeanVenueEvent;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class EventVenueAdapter extends BaseAdapter
{

	private Context				mContext;
	DisplayImageOptions			options;
	private ImageLoader			imageLoader;
	private List<BeanVenueEvent>	beanListClubs;
	private String				lastSavedChar	= " ";
	HashMap<Integer, String>	map				= new HashMap<Integer, String>();
	boolean						isAlpahabetVisible;

	public EventVenueAdapter(Context context, BeanListVenueEvent beanListClubs, boolean isAlpahabetVisible)
	{
		this.beanListClubs = beanListClubs.getEvents();
		mContext = context;
		this.isAlpahabetVisible = isAlpahabetVisible;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount()
	{
		return beanListClubs.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolderItem viewHolder;
		convertView = null;
		if (convertView == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_event_venue_info, parent, false);
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvClubUserName);
			viewHolder.tvUsername=(TextView) convertView.findViewById(R.id.tvUsername);
			viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivClubUserImage);
			viewHolder.tvFirstAlphabet = (TextView) convertView.findViewById(R.id.tvFirstAlphabet);
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();

		final BeanVenueEvent clubsInfo = beanListClubs.get(position);
		if (clubsInfo != null)
		{
			if (isAlpahabetVisible)
			{
				if (map.containsKey(position - 1))
				{
					lastSavedChar = map.get(position - 1);
					if (clubsInfo.getEvent_date().equals(lastSavedChar))
					{
						viewHolder.tvFirstAlphabet.setVisibility(View.GONE);
					}
					else
					{
						lastSavedChar = clubsInfo.getEvent_date();
						viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
						viewHolder.tvFirstAlphabet.setText(lastSavedChar);
					}
				}
				else
				{
					lastSavedChar = clubsInfo.getEvent_date();
					viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
					viewHolder.tvFirstAlphabet.setText(lastSavedChar);

				}
				if (map.containsKey(position))
				{

				}
				else map.put(position, lastSavedChar);
			}
			else viewHolder.tvFirstAlphabet.setVisibility(View.GONE);

			viewHolder.tvName.setText(clubsInfo.getName());
			viewHolder.tvUsername.setText(clubsInfo.getUsername());
			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl + clubsInfo.getImg(), viewHolder.ivProfilePic, options, null);
		}

		return convertView;

	}

	static class ViewHolderItem
	{

		private TextView	tvName,tvUsername;
		private ImageView	ivProfilePic;
		
		private TextView	tvFirstAlphabet;
		

	}

}
