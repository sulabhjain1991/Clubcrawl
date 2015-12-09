package com.linchpin.clubcrawl.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanListTrends;
import com.linchpin.clubcrawl.beans.BeanTrendsInfo;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TrendsListAdapter extends BaseAdapter {

	private Context mContext;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private BeanListTrends beanTrendsList;

	public TrendsListAdapter(Context context, BeanListTrends beanTrendsList) {
		this.beanTrendsList = beanTrendsList;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beanTrendsList.getResult().size();
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

		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_trende_list, parent,
					false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),
					mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.ivImage = (ImageView) convertView
					.findViewById(R.id.ivImage);
			viewHolder.ivImage.setImageResource(R.drawable.black_dot);

			LinearLayout llTrendsPosition = (LinearLayout) convertView
					.findViewById(R.id.llTrendsPosition);
			llTrendsPosition.setVisibility(View.VISIBLE);

			viewHolder.tvVenueName = (TextView) convertView
					.findViewById(R.id.tvClubUserName);
			viewHolder.ivVenueImage = (ImageView) convertView
					.findViewById(R.id.ivClubUserImage);
			viewHolder.tvPosition = (TextView) convertView
					.findViewById(R.id.tvPosition);
			viewHolder.tvVenueAddress = (TextView) convertView
					.findViewById(R.id.tvClubUserAddress);

			// store the holder with the view.
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolderItem) convertView.getTag();

		// object item based on the position
		final BeanTrendsInfo trendsInfo = beanTrendsList.getResult().get(
				position);

		// assign values if the object is not null
		if (trendsInfo != null) {
			viewHolder.tvPosition.setText(String.valueOf(position + 1));

			viewHolder.tvVenueName.setText(String.valueOf(trendsInfo.getVenue_name()));
			viewHolder.tvVenueAddress.setText(String.valueOf(trendsInfo.getAddress()));

			
			if(!String.valueOf(trendsInfo.getVenue_image()).equals(null) )
			imageLoader.displayImage(ConstantUtil.inviteLogoImageBaseUrl
					+ trendsInfo.getVenue_image(), viewHolder.ivVenueImage,
					options, null);
			/*else
				viewHolder.ivVenueImage.setBackgroundResource(R.drawable.no_img_jst_yet);*/
		}

		return convertView;

	}

	static class ViewHolderItem {

		private TextView tvPosition;
		private ImageView ivVenueImage;
		private TextView tvVenueName;
		private TextView tvVenueAddress;
		private ImageView ivImage;

	}
}
