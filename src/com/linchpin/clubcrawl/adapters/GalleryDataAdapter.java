package com.linchpin.clubcrawl.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanGalleryData;
import com.linchpin.clubcrawl.beans.BeanListGalleryData;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GalleryDataAdapter extends BaseAdapter
{

	private Context					mContext;

	private List<BeanGalleryData>	beanGalleryList			= null;
	private List<BeanGalleryData>	beanGalleryFilterList	= null;
	DisplayImageOptions				options;
	private ImageLoader				imageLoader;
	int singleViewHeight=300;

	public GalleryDataAdapter(Context context, BeanListGalleryData beanGalleryList)
	{
		if (beanGalleryList != null && beanGalleryList.getGallery() != null)
		{
			this.beanGalleryList = beanGalleryList.getGallery();
			beanGalleryFilterList = new ArrayList<BeanGalleryData>();
			beanGalleryFilterList.addAll(this.beanGalleryList);
		}
	
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		ConstantUtil.getScreen_Height((Activity) mContext);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();	
		singleViewHeight=(ConstantUtil.screenHeight-((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,250, mContext.getResources().getDisplayMetrics())))>>1;//
		
		
		
			
	}

	@Override
	public int getCount()
	{
		if (beanGalleryList != null) return beanGalleryList.size();
		else return 0;
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

	public void filter(String charText)
	{
		charText = charText.toLowerCase(Locale.getDefault());
		if(beanGalleryList!=null)
		{
			
		beanGalleryList.clear();
		if (charText.length() < 3)
		{
			beanGalleryList.addAll(beanGalleryFilterList);
		}
		else
		{
			for (BeanGalleryData wp : beanGalleryFilterList)
			{

				if (wp.getOffer() != null && wp.getOffer().toLowerCase(Locale.getDefault()).contains(charText)) beanGalleryList.add(wp);
				if (wp.getGallery_caption() != null && wp.getGallery_caption().toLowerCase(Locale.getDefault()).contains(charText)) beanGalleryList.add(wp);

				if (wp.getAddress() != null && wp.getAddress().toLowerCase(Locale.getDefault()).contains(charText)) beanGalleryList.add(wp);

			}
		}
		notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolderItem viewHolder;
		convertView = null;
		if (convertView == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.home_list_adapter, parent, false);
			FrameLayout layout = (FrameLayout)convertView.findViewById(R.id.rlHomeScreenList);			
			LayoutParams params = layout.getLayoutParams();
			params.height =singleViewHeight;
			layout.setLayoutParams(params);			
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.address);
			viewHolder.tvPlace = (TextView) convertView.findViewById(R.id.place);
			viewHolder.tvOffer = (TextView) convertView.findViewById(R.id.tvOffer);
			viewHolder.ivHomeScreenBg = (ImageView) convertView.findViewById(R.id.ivGaleeryImage);
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();
		
		final BeanGalleryData galleryData = beanGalleryList.get(position);
		if (galleryData != null)
		{
			viewHolder.tvAddress.setText(galleryData.getAddress());
			viewHolder.tvPlace.setText(galleryData.getGallery_caption());
			viewHolder.tvOffer.setText(galleryData.getOffer());
			imageLoader.displayImage(ConstantUtil.galleryImageUrl + galleryData.getGallery_img(), viewHolder.ivHomeScreenBg, options, null);
		}
		return convertView;
	}	
	static class ViewHolderItem
	{
		private TextView	tvAddress, tvPlace,tvOffer;
		private ImageView	ivHomeScreenBg;

	}

}
