package com.linchpin.clubcrawl.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
import com.linchpin.clubcrawl.beans.BeanListOffers;
import com.linchpin.clubcrawl.beans.BeanOffersInfo;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class OffersListAdapter extends BaseAdapter
{

	private Context			mContext;
	private Cursor			cursor;
	DisplayImageOptions		options;
	private ImageLoader		imageLoader;
	private BeanListOffers	beanListOffers;
	int singleViewHeight=300;
	public OffersListAdapter(Context context, BeanListOffers beanListOffers)
	{
		this.beanListOffers = beanListOffers;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		ConstantUtil.getScreen_Height((Activity) mContext);

		singleViewHeight=(ConstantUtil.screenHeight-((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,280, mContext.getResources().getDisplayMetrics())))>>1;//


	}

	@Override
	public int getCount()
	{
		return beanListOffers.getResult().size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return 0;







	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		// inflate the layout for each item of listView
		ViewHolderItem viewHolder;
		convertView = null;

		if (convertView == null)
		{
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.home_list_adapter, parent, false);
			FrameLayout layout = (FrameLayout)convertView.findViewById(R.id.rlHomeScreenList);			
			LayoutParams params = layout.getLayoutParams();
			params.height =singleViewHeight;
			layout.setLayoutParams(params);	
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
			viewHolder.tvName.setVisibility(View.VISIBLE);
			viewHolder.tvOfferName = (TextView) convertView.findViewById(R.id.tvOffer);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.address);
			viewHolder.ivGalleryimage = (ImageView) convertView.findViewById(R.id.ivGaleeryImage);
			viewHolder.tvPlace=(TextView) convertView.findViewById(R.id.place);
			viewHolder.tvPlace.setVisibility(View.GONE);
			// store the holder with the view.
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();

		// object item based on the position
		final BeanOffersInfo offersInfo = beanListOffers.getResult().get(position);

		// assign values if the object is not null
		if (offersInfo != null)
		{
			viewHolder.tvName.setText(offersInfo.getName());
			viewHolder.tvOfferName.setText(offersInfo.getOffername());
			viewHolder.tvOfferName.setTextColor(mContext.getResources().getColor(R.color.background_color));
			viewHolder.tvAddress.setText(offersInfo.getAddress());

			if (offersInfo.getHeader_image().equals(""))
			{
				viewHolder.ivGalleryimage.setBackgroundResource(R.drawable.back_header_image);
			}
			else
			{
				imageLoader.displayImage(ConstantUtil.OffersImageBaseUrl + offersInfo.getHeader_image(), viewHolder.ivGalleryimage, options, null);
			}
		}

		return convertView;
	}

	static class ViewHolderItem
	{

		private TextView	tvName,tvPlace;
		private TextView	tvOfferName;
		private TextView	tvAddress;
		private ImageView	ivGalleryimage;

	}
}
