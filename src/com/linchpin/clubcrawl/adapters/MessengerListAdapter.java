package com.linchpin.clubcrawl.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.jobjects.inbox.InboxResult;

public class MessengerListAdapter extends BaseAdapter
{

	private Context					mContext;
	private ArrayList<InboxResult>	list		= null;
	private ArrayList<InboxResult>	filterList	= null;

	public MessengerListAdapter(Context context, ArrayList<InboxResult> list)
	{
		if (list != null)
		{
			this.list = list;
			filterList = new ArrayList<InboxResult>();
			filterList.addAll(this.list);
		}

		mContext = context;
	}

	@Override
	public int getCount()
	{
		if (list != null) return list.size();
		else return 0;
	}

	@Override
	public Object getItem(int arg0)
	{
		return list.get(arg0);
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
			for (InboxResult wp : filterList)
			{
				if (wp.getFirst_name() != null && wp.getFirst_name().toLowerCase(Locale.getDefault()).contains(charText)) list.add(wp);
				if (wp.getLast_name() != null && wp.getLast_name().toLowerCase(Locale.getDefault()).contains(charText)) list.add(wp);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolderItem viewHolder;
		if (convertView != null)
		{
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		else
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.mesenger_friend_lst, parent, false);
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.subject = (TextView) convertView.findViewById(R.id.subject);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.profile_pic);
			viewHolder.status = (ImageView) convertView.findViewById(R.id.status);
			convertView.setTag(viewHolder);
		}
		InboxResult inboxItem = list.get(position);
		if (inboxItem != null)
		{
			viewHolder.name.setText(inboxItem.getFirst_name() + " " + inboxItem.getLast_name());
			AQuery query = new AQuery(mContext);
			query.id(viewHolder.profilePic).image(ConstantUtil.baseUrl + "images/profile_images/" + inboxItem.getProfile_pic(), false, false);
			if(inboxItem.getIs_logged_in() == null)
				viewHolder.status.setImageResource(R.drawable.logo_map);
			else if (inboxItem.getIs_logged_in().equals("1")) 
				viewHolder.status.setImageResource(R.drawable.round_logo_red);
			else viewHolder.status.setImageResource(R.drawable.logo_map);
			viewHolder.subject.setText(inboxItem.getSubject());
			if(inboxItem.getCreated_at() != null)
				viewHolder.time.setText(getCalender(inboxItem.getCreated_at()));

		}
		/*convertView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				int position = ((ViewHolderItem) v.getTag()).position;
				Intent intent = new Intent(mContext, MessagingDetailedActivity.class);
				intent.putExtra("message_id", list.get(position).getConversation_id());
				intent.putExtra("profile_image", list.get(position).getProfile_pic());
				intent.putExtra("name", list.get(position).getFirst_name()+" "+list.get(position).getLast_name());
				mContext.startActivity(intent);

			}
		});*/
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
		int position;
		private TextView	name, subject, time;
		private ImageView	profilePic, status;

	}

}
