package com.linchpin.clubcrawl.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.inbox.GSResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GSAdapter extends BaseAdapter
{

	private Context				mContext;
	DisplayImageOptions			options;
	private ImageLoader			imageLoader;
	private ArrayList<GSResult>	sResult;
	private ArrayList<GSResult>	sFilterResult;
	private String				lastSavedChar	= " ";
	HashMap<Integer, String>	map				= new HashMap<Integer, String>();
	boolean						isAlpahabetVisible;

	public GSAdapter(Context context, ArrayList<GSResult> list, boolean isAlpahabetVisible)
	{
		this.sResult = list;
		sFilterResult = new ArrayList<GSResult>();
		if (sResult != null)
		{
			Collections.sort(sResult);
			sFilterResult.addAll(this.sResult);
		}
		mContext = context;
		this.isAlpahabetVisible = isAlpahabetVisible;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	public ArrayList<GSResult> getList()
	{
		return sResult;
	}

	@Override
	public int getCount()
	{
		if (sResult != null) return sResult.size();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolderItem viewHolder;
		convertView = null;
		if (convertView == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_clubs_list, parent, false);
			
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvClubUserName);
			convertView.findViewById(R.id.lluser_detail).setVisibility(View.GONE);
			viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivClubUserImage);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvClubUserAddress);
			viewHolder.tvFirstAlphabet = (TextView) convertView.findViewById(R.id.tvFirstAlphabet);
			viewHolder.follow = (TextView) convertView.findViewById(R.id.follow);
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();
		final GSResult results = (GSResult) sResult.get(position);
		if (results != null)
		{
			if (isAlpahabetVisible)
			{
				if (map.containsKey(position - 1))
				{
					lastSavedChar = map.get(position - 1);
					if (results.getSearched_name().substring(0, 1).equalsIgnoreCase(lastSavedChar))
					{
						viewHolder.tvFirstAlphabet.setVisibility(View.GONE);
					}
					else
					{
						lastSavedChar = results.getSearched_name().substring(0, 1);
						viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
						viewHolder.tvFirstAlphabet.setText(lastSavedChar);
					}
				}
				else
				{
					lastSavedChar = results.getSearched_name().substring(0, 1);
					viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
					viewHolder.tvFirstAlphabet.setText(lastSavedChar);

				}
				if (map.containsKey(position))
				{

				}
				else map.put(position, lastSavedChar);
			}
			else viewHolder.tvFirstAlphabet.setVisibility(View.GONE);

			viewHolder.tvName.setText(results.getSearched_name());
			viewHolder.tvAddress.setText("");
			viewHolder.follow.setTag(results);
			if(results.getSearched_type().equals("e"))
				viewHolder.follow.setVisibility(View.GONE);
			else
				viewHolder.follow.setVisibility(View.VISIBLE);
			viewHolder.follow.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(final View arg0)
				{
					final GSResult info = (GSResult) arg0.getTag();
					NetworkTask networkTask = new NetworkTask(mContext, 100);
					networkTask.exposePostExecute(new Result()
					{

						@Override
						public void resultfromNetwork(String object, int id, int arg1, String arg2)
						{
							try
							{
								//{"status":"success"}
								JSONObject result = new JSONObject(object);
								if (result.get("status").equals("success"))
								{
									((TextView) arg0).setText("Following");
									((TextView) arg0).setTextColor(((TextView) arg0).getResources().getColor(android.R.color.white));
									((TextView) arg0).setBackgroundResource(R.drawable.following_btn_shape);
									info.setUser_followed("1");
								}
								/*else
								{
									((TextView) arg0).setText("+ Follow");
									arg0.setBackgroundResource(R.drawable.follow_btn_shape);
									((TextView) arg0).setTextColor(((TextView) arg0).getResources().getColor(R.color.background_color));
									info.setFlag("0");
								}*/
							}
							catch (JSONException e)
							{
								e.printStackTrace();
							}
						}
					});
					networkTask.setProgressDialog(true);
					networkTask.execute(ConstantUtil.baseUrl + "follow/savefollowstatus?follow_type=venue&user_id=" + AppPreferences.getInstance(mContext).getUserId() + "&follow_type_id="
							+ info.getSearched_id());

				}
			});
			if (results.getUser_followed().equals("1"))
			{
				viewHolder.follow.setBackgroundResource(R.drawable.following_btn_shape);
				viewHolder.follow.setText("Following");
				viewHolder.follow.setTextColor(viewHolder.follow.getResources().getColor(android.R.color.white));
			}
			else
			{
				viewHolder.follow.setText("+ Follow");
				viewHolder.follow.setBackgroundResource(R.drawable.follow_btn_shape);
				viewHolder.follow.setTextColor(viewHolder.follow.getResources().getColor(R.color.background_color));
			}
			if(results.getSearched_type().equals("u"))
			imageLoader.displayImage(ConstantUtil.profilePicBaseUrl +results.getSearched_image(), viewHolder.ivProfilePic, options, null);
			if(results.getSearched_type().equals("v"))
				imageLoader.displayImage(ConstantUtil.inviteLogoImageBaseUrl +results.getSearched_image(), viewHolder.ivProfilePic, options, null);
		}
		return convertView;

	}

	public void filter(String charText)
	{
		if (sResult != null)
		{
			charText = charText.toLowerCase(Locale.getDefault());

			if (charText.length() > 2)
			{
				sResult.clear();
				for (GSResult wp : sFilterResult)
				{
					if (wp.getSearched_name() != null && wp.getSearched_name().toLowerCase(Locale.getDefault()).contains(charText)) sResult.add(wp);
				}
			}
			else sResult.addAll(sFilterResult);
		}
		notifyDataSetChanged();
	}

	static class ViewHolderItem
	{

		private TextView	tvName;
		private ImageView	ivProfilePic;
		private TextView	tvAddress;
		private TextView	tvFirstAlphabet;
		private TextView	follow;

	}

}
