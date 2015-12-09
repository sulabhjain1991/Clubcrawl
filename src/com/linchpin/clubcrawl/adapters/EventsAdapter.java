package com.linchpin.clubcrawl.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
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
import com.linchpin.clubcrawl.jobjects.events.Results;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class EventsAdapter extends BaseAdapter
{

	private Context				mContext;
	DisplayImageOptions			options;
	private ImageLoader			imageLoader;
	private ArrayList<Results>	eventsresult;
	private ArrayList<Results>	eventsfilterresult;
	private String				lastSavedChar	= " ";
	HashMap<Integer, String>	map				= new HashMap<Integer, String>();
	boolean						isAlpahabetVisible;

	public EventsAdapter(Context context, ArrayList<Results> list, boolean isAlpahabetVisible)
	{
		this.eventsresult = list;
		Collections.sort(eventsresult);
		eventsfilterresult = new ArrayList<Results>();
		eventsfilterresult.addAll(this.eventsresult);
		mContext = context;
		this.isAlpahabetVisible = isAlpahabetVisible;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return eventsresult.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		// inflate the layout for each item of listView
		ViewHolderItem viewHolder;

		convertView = null;

		if (convertView == null)
		{
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_clubs_list, parent, false);
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvClubUserName);
			viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivClubUserImage);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvClubUserAddress);
			viewHolder.tvFirstAlphabet = (TextView) convertView.findViewById(R.id.tvFirstAlphabet);
			viewHolder.follow = (TextView) convertView.findViewById(R.id.follow);
			viewHolder.follow.setVisibility(View.GONE);
			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();

		// object item based on the position
		Results results = (Results) eventsresult.get(position);

		// assign values if the object is not null
		if (results != null)
		{
			if (isAlpahabetVisible)
			{
				if (map.containsKey(position - 1))
				{
					lastSavedChar = map.get(position - 1);
					if (results.getEventname().substring(0, 1).equalsIgnoreCase(lastSavedChar))
					{
						viewHolder.tvFirstAlphabet.setVisibility(View.GONE);
					}
					else
					{
						lastSavedChar = results.getEventname().substring(0, 1);
						viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
						viewHolder.tvFirstAlphabet.setText(lastSavedChar);
					}
				}
				else
				{
					lastSavedChar = results.getEventname().substring(0, 1);
					viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
					viewHolder.tvFirstAlphabet.setText(lastSavedChar);

				}
				if (map.containsKey(position))
				{

				}
				else map.put(position, lastSavedChar);
			}
			else viewHolder.tvFirstAlphabet.setVisibility(View.GONE);

			viewHolder.tvName.setText(results.getEventname());
			viewHolder.tvAddress.setText(results.getVenue().getAddress());
			imageLoader.displayImage(results.getImageurl(), viewHolder.ivProfilePic, options, null);
		}

		return convertView;

	}

	public void filter(String charText)
	{
		charText = charText.toLowerCase(Locale.getDefault());

		if (charText.length() > 2)
		{
			eventsresult.clear();
			for (Results wp : eventsfilterresult)
			{
				if (wp.getEventname() != null && wp.getEventname().toLowerCase(Locale.getDefault()).contains(charText)) eventsresult.add(wp);
			}
		}
		else
			eventsresult.addAll(eventsfilterresult);
		notifyDataSetChanged();
	}

	static class ViewHolderItem
	{

		private TextView	tvName;
		private ImageView	ivProfilePic;
		private TextView	tvAddress;
		private TextView	tvFirstAlphabet;
		private TextView	follow;

	}

}
