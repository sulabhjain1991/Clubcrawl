package com.linchpin.clubcrawl.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanListTickets;
import com.linchpin.clubcrawl.beans.BeanTicketsInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TicketsListAdapter extends BaseAdapter {

	private Context mContext;
	private Cursor cursor;
	DisplayImageOptions options;
	private ImageLoader imageLoader;
	private BeanListTickets beanListTickets;

	public TicketsListAdapter(Context context, BeanListTickets beanListTickets) {
		this.beanListTickets = beanListTickets;
		mContext = context;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beanListTickets.getResults().size();
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
		ViewHolderItem viewHolder;
		convertView = null;

		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_ticket, parent,
					false);

			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList),
					mContext);
			// well set up the ViewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.tvEventName = (TextView) convertView
					.findViewById(R.id.tvEventName);
			viewHolder.tvEventDate = (TextView) convertView
					.findViewById(R.id.tvEventDate);
			viewHolder.tvVenueName = (TextView) convertView
					.findViewById(R.id.tvVenueName);
			viewHolder.tvEventDoors = (TextView) convertView
					.findViewById(R.id.tvEventDoors);
			viewHolder.tvDescription = (TextView) convertView
					.findViewById(R.id.tvDescription);
			viewHolder.ivImageUrl = (ImageView) convertView
					.findViewById(R.id.ivImageUrl);

			// store the holder with the view.
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolderItem) convertView.getTag();

		// object item based on the position
		final BeanTicketsInfo ticketinfo = beanListTickets.getResults().get(
				position);

		// assign values if the object is not null
		if (ticketinfo != null) {
			viewHolder.tvEventName.setText(ticketinfo.getEventname());
			viewHolder.tvEventDate.setText(ticketinfo.getDate());
			viewHolder.tvVenueName.setText(ticketinfo.getVenue().getName()
					+ "," + ticketinfo.getVenue().getTown());
			viewHolder.tvEventDoors.setText(ticketinfo.getOpeningtimes()
					.getDoorsopen()
					+ " till "
					+ ticketinfo.getOpeningtimes().getDoorsclose()
					+ "(Last entry:"
					+ ticketinfo.getOpeningtimes().getLastentry() + ")");
			viewHolder.tvDescription.setText(ticketinfo.getDescription());
//			String imageUrl = ticketinfo.getImageurl().replace("/", "%2F");
//			imageLoader.displayImage(imageUrl, viewHolder.ivImageUrl, options,
//					null);
			AQuery aq = new AQuery(convertView);

			Bitmap bm;
			try {
//				ImageOptions options = new ImageOptions();
//				Bitmap icon = BitmapFactory.decodeResource(getResources(),
//						R.drawable.no_img_jst_yet);
//				options.preset = icon;
				aq.id(R.id.ivImageUrl).image(ticketinfo.getImageurl());
			} catch (Exception e) {
				bm = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.no_img_jst_yet);
				viewHolder.ivImageUrl.setImageBitmap(bm);
			}

			
		}

		return convertView;
	}

	static class ViewHolderItem {

		private TextView tvEventName;
		private TextView tvEventDate;
		private TextView tvVenueName;
		private TextView tvEventDoors;
		private TextView tvDescription;
		private ImageView ivImageUrl;

	}
}
