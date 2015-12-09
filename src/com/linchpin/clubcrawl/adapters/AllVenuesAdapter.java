package com.linchpin.clubcrawl.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanClubsInfo;
import com.linchpin.clubcrawl.beans.BeanListClubs;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AllVenuesAdapter extends BaseAdapter{

	private Context				context;
	DisplayImageOptions			options;
	private ImageLoader			imageLoader;
	BeanListClubs beanListClubs;

	public AllVenuesAdapter(Context context,BeanListClubs beanListClubs) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.beanListClubs=beanListClubs;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beanListClubs.getResult().size();
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
	public View getView(int position, View convertView, ViewGroup parent) 
	{

		// TODO Auto-generated method stub
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.adapter_following_venues, null);
		com.linchpin.clubcrawl.helper.ImageViewRounded ivVenueImage=(ImageViewRounded) view.findViewById(R.id.ivVenueImage);
		TextView tvVenueName=(TextView) view.findViewById(R.id.tvVenueName);
		TextView tvVenueAddress=(TextView) view.findViewById(R.id.tvVenueAddress);
		 BeanClubsInfo clubsinfo=beanListClubs.getResult().get(position);

		tvVenueName.setText(clubsinfo.getName());
		tvVenueAddress.setText(clubsinfo.getAddress());
		imageLoader.displayImage(ConstantUtil.venuesImagesUrl
				+ clubsinfo.getLogo(), ivVenueImage,
				options, null);
		TextView tvFollowing=(TextView) view.findViewById(R.id.tvFollowing);
		tvFollowing.setVisibility(View.GONE);




		return view;


	}


}
