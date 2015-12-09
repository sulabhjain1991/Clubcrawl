package com.linchpin.clubcrawl.adapters;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanFollowersInfo;
import com.linchpin.clubcrawl.beans.BeanListFollowers;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FollowersListAdapter extends BaseAdapter {

	Context context;
	BeanListFollowers followers;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	String clickable;
	public FollowersListAdapter(Context context,BeanListFollowers followers, String clickable) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.followers=followers;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		this.clickable=clickable;

	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return followers.getFollowsName().size();
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
		final TextView tvFollow=(TextView) view.findViewById(R.id.tvfollow);
		final BeanFollowersInfo followersInfo=followers.getFollowsName().get(position);
		String userName=followersInfo.getFirst_name()+" "+followersInfo.getLast_name();
		tvFollowersName.setText(userName);
		imageLoader.displayImage(ConstantUtil.profilePicBaseUrl
				+ followersInfo.getProfile_pic(), ivFollowersImage,
				options, null);
		if(followersInfo.getFollow_status().equals("3"))
		{
			tvFollow.setText("+Follow");
			tvFollow.setTextColor(context.getResources().getColor(R.color.background_color));
			tvFollow.setBackgroundResource(R.drawable.follow_btn_shape);


		}
		else if(followersInfo.getFollow_status().equals("2"))
		{
			tvFollow.setText("Requested");
			tvFollow.setTextColor(context.getResources().getColor(android.R.color.white));
			tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
		}
		else if(followersInfo.getFollow_status().equals("1"))
		{
			tvFollow.setText("Following");
			tvFollow.setTextColor(context.getResources().getColor(android.R.color.white));
			tvFollow.setBackgroundResource(R.drawable.following_btn_shape);

		}
		
		if(clickable.equals("true")){
		tvFollow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(followersInfo.getFollow_status().equals("3"))
				{

					/*tvFollow.setText("Following");
					tvFollow.setTextColor(context.getResources().getColor(android.R.color.white));
					tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
					 */
					NetworkTask networkTask = new NetworkTask(context, 100);
					tvFollow.setText("Requested");
					tvFollow.setTextColor(context.getResources().getColor(R.color.white));
					tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
					followersInfo.setFollow_status("2");

					networkTask.exposePostExecute(new Result()
					{

						@Override
						public void resultfromNetwork(String object, int id,int arg1,String arg2)
						{
							try
							{
								JSONObject result = new JSONObject(object);
								if (result.get("status").equals("2"))
								{
									/*tvFollow.setText("Requested");
									tvFollow.setTextColor(context.getResources().getColor(R.color.white));
									tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
									followersInfo.setFollow_status("2");*/
								}
								else if(result.get("status").equals("1"))
								{

									tvFollow.setText("Following");
									tvFollow.setTextColor(context.getResources().getColor(R.color.white));
									tvFollow.setBackgroundResource(R.drawable.following_btn_shape);
									followersInfo.setFollow_status("1");
								}
								else
								{
									Toast toast= Toast.makeText(context,"Network error try again later", Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}



							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}



						}
					});



					networkTask.setProgressDialog(false);
					String 	url=ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(context).getUserId()+"&follow_type_id="+followersInfo.getUser_id()+"&follow_type=user";
					networkTask.execute(ConstantUtil.followClickUrl+"user_id="+AppPreferences.getInstance(context).getUserId()+"&follow_type_id="+followersInfo.getUser_id()+"&follow_type=user");







				}
				else if(followersInfo.getFollow_status().equals("2"))
				{

					Toast.makeText(context, "Follow request sent already", Toast.LENGTH_SHORT).show();					

				}
				else
				{
					final Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.unfollow_user_dialog);

					dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					TextView tvName=	(TextView) dialog.findViewById(R.id.tvName);
					tvName.setText(followersInfo.getFirst_name()+" "+followersInfo.getLast_name());
					dialog.findViewById(R.id.tvUnfollow).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {					

							dialog.cancel();
							/*tvFollow.setText("+Follow");
							tvFollow.setTextColor(context.getResources().getColor(R.color.background_color));
							tvFollow.setBackgroundResource(R.drawable.follow_btn_shape);*/
							NetworkTask networkTask = new NetworkTask(context, 100);
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
											tvFollow.setText("+Follow");
											tvFollow.setTextColor(context.getResources().getColor(R.color.background_color));
											tvFollow.setBackgroundResource(R.drawable.follow_btn_shape);
											followersInfo.setFollow_status("3");
										}
										else
										{
											Toast toast= Toast.makeText(context,"Network error try again later", Toast.LENGTH_SHORT);
											//toast.makeText(""+result.get("status").toString());
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
										}

									}
									catch (JSONException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}







								}
							});
							//networkTask.setProgressDialog(false);
							//	networkTask.execute(ConstantUtil.baseUrl + "newapi/savefollowstatus?follow_type=venue&user_id=" + AppPreferences.getInstance(mContext).getUserId() + "&follow_type_id="
							//			+ clubsInfo.getId());
							String url=ConstantUtil.unFollowClickUrl+"follow_type=user&id=" + followersInfo.getUser_id() + "&follow_type_id="+ AppPreferences.getInstance(context).getUserId();
							url=ConstantUtil.unFollowClickUrl+"follow_type=user&id=" + followersInfo.getUser_id() + "&follow_type_id="+ AppPreferences.getInstance(context).getUserId();
							networkTask.execute(ConstantUtil.unFollowClickUrl+"follow_type=user&id="+followersInfo.getUser_id()+ "&user_id="+AppPreferences.getInstance(context).getUserId());}

					});
					dialog.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
					dialog.show();

				}

			}
		});

		}

		return view;
	}

}
