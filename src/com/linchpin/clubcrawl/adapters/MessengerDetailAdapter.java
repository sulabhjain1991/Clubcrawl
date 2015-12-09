package com.linchpin.clubcrawl.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.jsonobject.message.Mainmsgdetail;

public class MessengerDetailAdapter extends BaseAdapter
{

	private Context						mContext;
	private ArrayList<Mainmsgdetail>	list		= null;
	private ArrayList<Mainmsgdetail>	filterList	= null;

	public MessengerDetailAdapter(Context context, ArrayList<Mainmsgdetail> list)
	{
		if (list != null)
		{
			this.list = list;
			filterList = new ArrayList<Mainmsgdetail>();
			filterList.addAll(this.list);
		}

		mContext = context;
	}

	public void addItem(Mainmsgdetail mainmsgdetail)
	{
		this.list.add(mainmsgdetail);
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		if (list != null) return list.size();
		else return 0;
	}

	@Override
	public Object getItem(int index)
	{
		return list.get(index);
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	public void filter(String charText)
	{
		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() < 3)
		{
			list.addAll(filterList);
		}
		else
		{
			for (Mainmsgdetail wp : filterList)
			{
				//				if (wp.getFirst_name() != null && wp.getFirst_name().toLowerCase(Locale.getDefault()).contains(charText)) list.add(wp);
				//				if (wp.getLast_name() != null && wp.getLast_name().toLowerCase(Locale.getDefault()).contains(charText)) list.add(wp);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolderItem viewHolder;
		AQuery query = new AQuery(mContext);
		if (convertView != null)
		{
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		else
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.fragment_chatting_list_single_row, parent, false);
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			//viewHolder.myImage = (ImageViewRounded) convertView.findViewById(R.id.ivuserfriendsImage);
			//	viewHolder.senderImage = (ImageViewRounded) convertView.findViewById(R.id.ivuserImage);

			viewHolder.myTime = (TextView) convertView.findViewById(R.id.tvMyDay);
			viewHolder.tvSenderDay = (TextView) convertView.findViewById(R.id.tvSenderDay);
			viewHolder.ivSenderImgMsg=(ImageView)convertView.findViewById(R.id.ivSenderImgMsg);
			viewHolder.ivMyImgMsg=(ImageView) convertView.findViewById(R.id.ivMyImgMsg);
			viewHolder.tvMyMessages = (TextView) convertView.findViewById(R.id.tvMyMessages);
			viewHolder.tvSenderMessages = (TextView) convertView.findViewById(R.id.tvSenderMessages);
			viewHolder.myPan = (RelativeLayout) convertView.findViewById(R.id.my_pan);
			viewHolder.senderPan = (RelativeLayout) convertView.findViewById(R.id.sender_pan);
			viewHolder.stillSending = (View) convertView.findViewById(R.id.stillSending);
			viewHolder.delivered = (View) convertView.findViewById(R.id.delivered);
			convertView.setTag(viewHolder);
		}
		Mainmsgdetail message = list.get(position);
		if (message != null && message.getSender_id().equals(AppPreferences.getInstance(mContext).getUserId()))
		{
			viewHolder.senderPan.setVisibility(View.GONE);
			viewHolder.myPan.setVisibility(View.VISIBLE);
			viewHolder.stillSending.setVisibility(View.VISIBLE);
			if(message.isStatus())
				viewHolder.delivered.setVisibility(View.VISIBLE);
			else
				viewHolder.delivered.setVisibility(View.GONE);

			if(!message.getImage().equals(""))
			{
				viewHolder.ivMyImgMsg.setVisibility(View.VISIBLE);
				viewHolder.tvMyMessages.setVisibility(View.GONE);
			/*	AQuery aq=new AQuery(viewHolder.ivMyImgMsg);
				ImageOptions options = new ImageOptions();
				Bitmap icon = BitmapFactory.decodeResource(convertView.getResources(), R.drawable.no_img_jst_yet);
				options.preset = icon;
				options.targetWidth = ConstantUtil.sc4reenWidth * 40 / 100;
				aq.id(viewHolder.ivMyImgMsg.getId()).image(ConstantUtil.messengerPicBaseUrl + message.getImage(), options);
				AQuery aqPostImage = new AQuery(viewHolder.ivSenderImgMsg);*/
				AQuery aqPostImage = new AQuery(viewHolder.ivMyImgMsg);
				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;
					Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.messengerPicBaseUrl + message.getImage());
					if(bmp!=null)
					{
						viewHolder.ivMyImgMsg.setImageBitmap(bmp);
					}
					else
						aqPostImage.id(viewHolder.ivMyImgMsg.getId()).image(ConstantUtil.messengerPicBaseUrl + message.getImage(), options);

				}
				catch(Exception e)
				{

				}



			}
			else
			{
				viewHolder.ivMyImgMsg.setVisibility(View.GONE);
				viewHolder.tvMyMessages.setVisibility(View.VISIBLE);
				viewHolder.tvMyMessages.setText(message.getMessage());
			}
			viewHolder.myTime.setText(getCalender(message.getCreated_at()));
		}

		else
		{
			viewHolder.myPan.setVisibility(View.GONE);
			viewHolder.senderPan.setVisibility(View.VISIBLE);
			if(!message.getImage().equals(""))
			{
				viewHolder.ivSenderImgMsg.setVisibility(View.VISIBLE);
				viewHolder.tvSenderMessages.setVisibility(View.GONE);
				/*AQuery aq=new AQuery(viewHolder.ivSenderImgMsg);
				ImageOptions options = new ImageOptions();
				Bitmap icon = BitmapFactory.decodeResource(convertView.getResources(), R.drawable.no_img_jst_yet);
				options.preset = icon;
				options.targetWidth = ConstantUtil.screenWidth * 40 / 100;

				aq.id(viewHolder.ivSenderImgMsg.getId()).image(ConstantUtil.messengerPicBaseUrl + message.getImage(), options);*/

				AQuery aqPostImage = new AQuery(viewHolder.ivSenderImgMsg);
				try
				{
					ImageOptions options = new ImageOptions();
					Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_img_jst_yet);
					options.preset = icon;
					options.fallback=R.drawable.no_img_jst_yet;
					Bitmap bmp=aqPostImage.getCachedImage(ConstantUtil.messengerPicBaseUrl + message.getImage());
					if(bmp!=null)
					{
						viewHolder.ivSenderImgMsg.setImageBitmap(bmp);
					}
					else
						aqPostImage.id(viewHolder.ivSenderImgMsg.getId()).image(ConstantUtil.messengerPicBaseUrl + message.getImage(), options);

				}
				catch(Exception e)
				{

				}


			}
			else
			{
				viewHolder.ivSenderImgMsg.setVisibility(View.GONE);
				viewHolder.tvSenderMessages.setVisibility(View.VISIBLE);
				viewHolder.tvSenderMessages.setText(message.getMessage());
			}
			viewHolder.tvSenderDay.setText(getCalender(message.getCreated_at()));
		}


		return convertView;
	}

	private Calendar getDate(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

	public String getCalender(String timestamp)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = getDate(sdf.parse(timestamp)).getTime();
			int day = date.compareTo(getDate(new Date()).getTime());
			if (day == 0) return "Today";
			else if (day < 0)
			{
				Calendar calendar = getDate(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				day = date.compareTo(calendar.getTime());
				if (day == 0) return "Yesterday";

			}
			sdf = new SimpleDateFormat("dd MMM");
			return sdf.format(date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return "";
	}

	static class ViewHolderItem
	{

		private TextView	myTime, tvSenderDay, tvMyMessages, tvSenderMessages;
		View				stillSending, delivered;
		RelativeLayout		myPan;
		RelativeLayout		senderPan;
		private ImageViewRounded	myImage, senderImage;
		ImageView ivSenderImgMsg,ivMyImgMsg;

	}

}
