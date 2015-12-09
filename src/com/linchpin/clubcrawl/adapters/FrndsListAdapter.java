package com.linchpin.clubcrawl.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanSingleFriendInfo;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FrndsListAdapter extends BaseAdapter {

	Context context;
	List<BeanSingleFriendInfo> list;
	private ImageLoader imageLoader;
	DisplayImageOptions options;

	public FrndsListAdapter(Context context,List<BeanSingleFriendInfo> list) {
		// TODO Auto-generated constructor stub
		this.list=list;
		this.context=context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		View view=inflater.inflate(R.layout.followers_list_adapter, null);
		com.linchpin.clubcrawl.helper.ImageViewRounded ivFollowersImage=(ImageViewRounded) view.findViewById(R.id.ivFollowersImage);
		TextView tvFollowersName=(TextView) view.findViewById(R.id.tvFollowersName);
		TextView tvFollow=(TextView) view.findViewById(R.id.tvfollow);
		tvFollow.setVisibility(View.GONE);
		tvFollowersName.setText(list.get(position).getFirst_name()+" "+list.get(position).getLast_name());
		imageLoader.displayImage(ConstantUtil.profilePicBaseUrl
				+ list.get(position).getProfile_pic(), ivFollowersImage,
				options, null);




		return view;
	}

}
