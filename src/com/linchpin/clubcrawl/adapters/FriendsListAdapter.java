package com.linchpin.clubcrawl.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanFriendInfo;
import com.linchpin.clubcrawl.beans.BeanListFriends;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FriendsListAdapter extends BaseAdapter{

	private Context mContext;
	private Cursor cursor;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private BeanListFriends beanFrindList;

	public FriendsListAdapter(Context context, BeanListFriends beanFrindList){
		this.beanFrindList = beanFrindList;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder()
		.showImageOnFail(R.drawable.no_img_jst_yet)
		.showImageOnLoading(R.drawable.no_img_jst_yet)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(beanFrindList == null || beanFrindList.getFriends()==null)
			return 0;
		return beanFrindList.getFriends().size();
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
		// inflate the layout for each item of listView
		ViewHolderItem viewHolder;

		convertView = null;



		/*
		 * The convertView argument is essentially a "ScrapView" as described is Lucas post 
		 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
		 * It will have a non-null value when ListView is asking you recycle the row layout. 
		 * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
		 */

		if(convertView == null)
		{
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_friends_list, parent, false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvFriendName = (TextView) convertView.findViewById(R.id.tvFriendName);

			viewHolder.ivFriendPic = (ImageView) convertView.findViewById(R.id.ivFriendImage);


			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolderItem)convertView.getTag();




		// object item based on the position
		final BeanFriendInfo friendInfo = beanFrindList.getFriends().get(position);

		// assign values if the object is not null
		if(friendInfo != null) 
		{
//			AQuery aq = new AQuery(convertView);
//			//
//			//			  
//			//
//			Bitmap bm;
//			try {
//
//				//		     aq.id(R.id.address).text(galleryData.getAddress());
//				//		     aq.id(R.id.place).text(galleryData.getGallery_caption());
//				if(aq.getCachedImage(ConstantUtil.profilePicBaseUrl+friendInfo.getProfile_pic())==null)
//				{
//					aq.id(R.id.ivFriendImage).image(ConstantUtil.profilePicBaseUrl+friendInfo.getProfile_pic());
//				}
//				else
//				{
//					Bitmap bmImage = aq.getCachedImage(ConstantUtil.profilePicBaseUrl+friendInfo.getProfile_pic());
//					viewHolder.ivFriendPic.setImageBitmap(bmImage);
//				}
//
//
//
//			} catch (Exception e) {
//				bm = BitmapFactory.decodeResource(mContext.getResources(),
//						R.drawable.no_img_jst_yet);
//				viewHolder.ivFriendPic.setImageBitmap(bm);
//			}

					
			viewHolder.tvFriendName.setText(friendInfo.getFirst_name() + " " + friendInfo.getLast_name());

			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl+friendInfo.getProfile_pic(), viewHolder.ivFriendPic, options, null);
		}



		return convertView;
	}

	// our ViewHolder.
	// caches our TextView
	static class ViewHolderItem {

		private TextView tvFriendName;
		private ImageView ivFriendPic;

	}

}
