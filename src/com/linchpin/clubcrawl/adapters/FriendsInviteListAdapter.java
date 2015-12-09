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
import com.linchpin.clubcrawl.beans.BeanFriendInfo;
import com.linchpin.clubcrawl.beans.BeanInviteListFriends;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FriendsInviteListAdapter extends BaseAdapter {

	private BeanInviteListFriends beanInviteListFriends;
	public BeanInviteListFriends getBeanInviteListFriends() {
		return beanInviteListFriends;
	}

	public void setBeanInviteListFriends(BeanInviteListFriends beanInviteListFriends) {
		this.beanInviteListFriends = beanInviteListFriends;
	}

	private Context mContext;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private List<String> selectedList;

	public FriendsInviteListAdapter(Context context,
			BeanInviteListFriends beanInviteFrindList,List<String> selectedList) {
		this.beanInviteListFriends = beanInviteFrindList;
		mContext = context;
		this.selectedList = selectedList;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beanInviteListFriends.getFriendList().size();
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

	View view;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// inflate the layout for each item of listView
		ViewHolderItem viewHolder;

		// convertView = null;

		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_friends_list,
					parent, false);
			view = convertView;

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),
					mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvFriendName = (TextView) convertView
					.findViewById(R.id.tvFriendName);
			viewHolder.ivFriendPic = (ImageView) convertView
					.findViewById(R.id.ivFriendImage);
			viewHolder.ivIsSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
			
//			viewHolder.cbInvitedFriend
//					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView,
//								boolean isChecked) {
//							// TODO Auto-generated method stub
//							beanInvitedFriendList
//									.setFriendList(beanInviteListFriends
//											.getFriendList());
//						}
//					});

			// store the holder with the view.
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolderItem) convertView.getTag();

		// object item based on the position
		final BeanFriendInfo friendInfo = beanInviteListFriends.getFriendList()
				.get(position);

		// assign values if the object is not null
		if (friendInfo != null) {

			viewHolder.tvFriendName.setText(friendInfo.getFirst_name() + " "
					+ friendInfo.getLast_name());
			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl
					+ friendInfo.getProfile_pic(), viewHolder.ivFriendPic,
					options, null);
			if(selectedList.contains(friendInfo.getID()))
				viewHolder.ivIsSelected.setVisibility(View.VISIBLE);
			else
				viewHolder.ivIsSelected.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolderItem {

		private TextView tvFriendName;
		private ImageView ivFriendPic;
		private ImageView ivIsSelected;
	}

}
