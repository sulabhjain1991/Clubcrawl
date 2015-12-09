package com.linchpin.clubcrawl.adapters;

import java.util.HashMap;
import java.util.List;
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
import android.widget.Toast;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanClubsInfo;
import com.linchpin.clubcrawl.beans.BeanListClubs;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ClubsListAdapter extends BaseAdapter
{

	private Context				mContext;
	DisplayImageOptions			options;
	private ImageLoader			imageLoader;
	private List<BeanClubsInfo>	beanListClubs;
	private String				lastSavedChar	= " ";
	HashMap<Integer, String>	map				= new HashMap<Integer, String>();
	boolean						isAlpahabetVisible;

	public ClubsListAdapter(Context context, BeanListClubs beanListClubs, boolean isAlpahabetVisible)
	{
		this.beanListClubs = beanListClubs.getResult();
		mContext = context;
		this.isAlpahabetVisible = isAlpahabetVisible;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount()
	{
		return beanListClubs.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolderItem viewHolder;
		convertView = null;
		if (convertView == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.adapter_clubs_list, parent, false);
			BaseClass bc = new BaseClass();
			bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), mContext);
			viewHolder = new ViewHolderItem();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvClubUserName);
			viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivClubUserImage);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvClubUserAddress);
			viewHolder.tvFirstAlphabet = (TextView) convertView.findViewById(R.id.tvFirstAlphabet);
			viewHolder.follow = (TextView) convertView.findViewById(R.id.follow);
			convertView.setTag(viewHolder);
		}
		else viewHolder = (ViewHolderItem) convertView.getTag();

		final BeanClubsInfo clubsInfo = beanListClubs.get(position);
		if (clubsInfo != null)
		{
			if (isAlpahabetVisible)
			{
				if (map.containsKey(position - 1))
				{
					lastSavedChar = map.get(position - 1);
					if (clubsInfo.getName().substring(0, 1).equalsIgnoreCase(lastSavedChar))
					{
						viewHolder.tvFirstAlphabet.setVisibility(View.GONE);
					}
					else
					{
						lastSavedChar = clubsInfo.getName().substring(0, 1);
						viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
						viewHolder.tvFirstAlphabet.setText(lastSavedChar);
					}
				}
				else
				{
					lastSavedChar = clubsInfo.getName().substring(0, 1);
					viewHolder.tvFirstAlphabet.setVisibility(View.VISIBLE);
					viewHolder.tvFirstAlphabet.setText(lastSavedChar);

				}
				if (map.containsKey(position))
				{

				}
				else map.put(position, lastSavedChar);
			}
			else viewHolder.tvFirstAlphabet.setVisibility(View.GONE);

			viewHolder.tvName.setText(clubsInfo.getName());
			viewHolder.follow.setTag(clubsInfo);
			viewHolder.follow.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(final View arg0)
				{
					if(clubsInfo.getFlag().equals("0"))
					{
						try
						{
					if(!AppPreferences.getInstance(mContext).getSneakStatus())
					{
						
					final BeanClubsInfo info = (BeanClubsInfo) arg0.getTag();
					NetworkTask networkTask = new NetworkTask(mContext, 100);
					networkTask.exposePostExecute(new Result()
					{

						@Override
						public void resultfromNetwork(String object, int id,int arg1,String arg2)
						{
							
							try
							{
								//{"status":"success"}
								JSONObject result = new JSONObject(object);
								if (result.get("status").equals("success")||result.get("status").equals("1")||result.get("status").equals("0")||result.get("status").equals("2"))
								{
									//((TextView) arg0).setText("Following");
									//((TextView) arg0).setTextColor(((TextView) arg0).getResources().getColor(android.R.color.white));
									((TextView) arg0).setBackgroundResource(R.drawable.following_btn);
									info.setFlag("1");
								}
							
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
					networkTask.setProgressDialog(true);
					String url=ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(mContext).getUserId()+"&follow_type_id="+ clubsInfo.getId()+"&follow_type=venue";
					networkTask.execute(ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(mContext).getUserId()+"&follow_type_id="+ clubsInfo.getId()+"&follow_type=venue");

				}
				
				else
				{
					ConstantUtil.showSneakDialog(mContext);
				}
					}
					
				catch(Exception e)
				{
					Toast.makeText(mContext, "Error in API's", Toast.LENGTH_LONG).show();
				}
				
				}
					
					else
					{
						try{
						if(!AppPreferences.getInstance(mContext).getSneakStatus())
						{
							
						final BeanClubsInfo info = (BeanClubsInfo) arg0.getTag();
						NetworkTask networkTask = new NetworkTask(mContext, 100);
						networkTask.exposePostExecute(new Result()
						{

							@Override
							public void resultfromNetwork(String object, int id,int arg1,String arg2)
							{
								
								try
								{
									//{"status":"success"}
									JSONObject result = new JSONObject(object);
									if (result.get("status").equals("success"))
									{
										//((TextView) arg0).setText("+ Follow");
										//((TextView) arg0).setTextColor(((TextView) arg0).getResources().getColor(R.color.background_color));
										((TextView) arg0).setBackgroundResource(R.drawable.follow_btn);
										info.setFlag("0");
									}
									
								}
								catch (JSONException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
						networkTask.setProgressDialog(true);
						
						networkTask.execute(ConstantUtil.unFollowClickUrl+"follow_type=venue&id="+clubsInfo.getId()+ "&user_id="+AppPreferences.getInstance(mContext).getUserId());

					}
					
					else
					{
						ConstantUtil.showSneakDialog(mContext);
					}
						
						}
						catch (Exception e) {
							// TODO: handle exception
						Toast.makeText(mContext, "Error in API's", Toast.LENGTH_LONG).show();
						}
						
						}
					
				
					
					
					
					
					
					
					
				}
			});
			viewHolder.tvAddress.setText(clubsInfo.getAddress());
			if (clubsInfo.getFlag().equals("1"))
			{
				viewHolder.follow.setBackgroundResource(R.drawable.following_btn);
				//viewHolder.follow.setText("Following");
				//viewHolder.follow.setTextColor(viewHolder.follow.getResources().getColor(android.R.color.white));
			}
			else
			{
				//viewHolder.follow.setText("+ Follow");
				viewHolder.follow.setBackgroundResource(R.drawable.follow_btn);
				//viewHolder.follow.setTextColor(viewHolder.follow.getResources().getColor(R.color.background_color));
			}
			imageLoader.displayImage(ConstantUtil.inviteLogoImageBaseUrl + clubsInfo.getLogo(), viewHolder.ivProfilePic, options, null);
		}

		return convertView;

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
