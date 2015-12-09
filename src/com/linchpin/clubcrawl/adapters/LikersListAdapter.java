package com.linchpin.clubcrawl.adapters;

import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanLikersInfo;
import com.linchpin.clubcrawl.beans.BeanListLikers;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LikersListAdapter extends BaseAdapter {

	Context context;
	BeanListLikers likers;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	
	public LikersListAdapter(Context context,BeanListLikers likers) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.likers=likers;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return likers.getLikeUserName().size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.likers_list_adapter, null);
		com.linchpin.clubcrawl.helper.ImageViewRounded ivLikersImage=(ImageViewRounded) view.findViewById(R.id.ivLikersImage);
		TextView tvLikersName=(TextView) view.findViewById(R.id.tvLikersName);
		TextView tvFollow=(TextView) view.findViewById(R.id.tvfollow);
		BeanLikersInfo likersInfo=likers.getLikeUserName().get(position);
	
		tvLikersName.setText(likersInfo.getFirst_name()+" "+likersInfo.getLast_name());
		imageLoader.displayImage(ConstantUtil.likersImageUrl
				+ likersInfo.getProfile_pic(), ivLikersImage,
				options, null);
		if(likersInfo.getFollow_status().equals("1"))
		{
			tvFollow.setText("Following");
			tvFollow.setTextColor(context.getResources().getColor(android.R.color.white));
			tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
		}
		else
		{
			tvFollow.setText("+Follow");
			tvFollow.setTextColor(context.getResources().getColor(R.color.background_color));
			tvFollow.setBackgroundResource(R.drawable.follow_btn_shape);
		}
		
		
		
		return view;
	}


}
