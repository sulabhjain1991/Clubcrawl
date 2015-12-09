package com.linchpin.clubcrawl.adapters;

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
import com.linchpin.clubcrawl.beans.BeanListWhosOutVenue;
import com.linchpin.clubcrawl.beans.BeanWhosOutVenue;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WhosVenueListAdapter extends BaseAdapter
{

	private Context				mContext;
	DisplayImageOptions			options;
	private ImageLoader			imageLoader;
	private List<BeanWhosOutVenue>	beanListClubs;
	

	public WhosVenueListAdapter(Context context, BeanListWhosOutVenue beanListClubs)
	{
		this.beanListClubs = beanListClubs.getVenueList();
		
		mContext = context;
		
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
			convertView = inflater.inflate(R.layout.adapter_whos_out_venue, parent, false);
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvClubUserName);
			viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivClubUserImage);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvClubUserAddress);
//			viewHolder.tvFirstAlphabet = (TextView) convertView.findViewById(R.id.tvFirstAlphabet);
//			viewHolder.follow = (TextView) convertView.findViewById(R.id.follow);
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();

		final BeanWhosOutVenue clubsInfo = beanListClubs.get(position);
		if (clubsInfo != null)
		{
			
			viewHolder.tvName.setText(clubsInfo.getLabel());
			
			viewHolder.tvAddress.setText(clubsInfo.getAddress());
			
			imageLoader.displayImage(ConstantUtil.inviteLogoImageBaseUrl + clubsInfo.getImg(), viewHolder.ivProfilePic, options, null);
		}

		return convertView;

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
