package com.linchpin.clubcrawl.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanFollowingInfo;
import com.linchpin.clubcrawl.beans.BeanListFollowing;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FollowingListAdapter extends BaseAdapter{


	Context context;
	BeanListFollowing following;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	String clickable;

	public FollowingListAdapter(Context context,BeanListFollowing following,String clickable) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.following=following;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		this.clickable=clickable;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return following.getFollowsName().size();
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
		final int Currentposition=position;
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.following_list_adapter, null);
		com.linchpin.clubcrawl.helper.ImageViewRounded ivFollowersImage=(ImageViewRounded) view.findViewById(R.id.ivFollowingImage);
		TextView tvFollowersName=(TextView) view.findViewById(R.id.tvFollowingName);
		TextView tvFollow=(TextView) view.findViewById(R.id.tvfollow);


		final BeanFollowingInfo followersInfo=following.getFollowsName().get(position);

		tvFollowersName.setText(followersInfo.getFirst_name()+" "+followersInfo.getLast_name());
		imageLoader.displayImage(ConstantUtil.profilePicBaseUrl
				+ followersInfo.getProfile_pic(), ivFollowersImage,
				options, null);
		if(clickable.equals("true"))
		{
			tvFollow.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
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
							following.getFollowsName().remove(Currentposition);
							notifyDataSetChanged();
							NetworkTask networkTask = new NetworkTask(context, 100);
							/*						networkTask.exposePostExecute(new Result()
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

//										following.getFollowsName().remove(Currentposition);
//										notifyDataSetChanged();


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
							 */						networkTask.setProgressDialog(false);
							 //	networkTask.execute(ConstantUtil.baseUrl + "newapi/savefollowstatus?follow_type=venue&user_id=" + AppPreferences.getInstance(mContext).getUserId() + "&follow_type_id="
							 //			+ clubsInfo.getId());
							 //String url=ConstantUtil.unFollowClickUrl+"id="+followersInfo.getID()+"&user_id="+AppPreferences.getInstance(context).getUserId() + "&follow_type=user";
							 networkTask.execute(ConstantUtil.unFollowClickUrl+"id="+followersInfo.getID()+"&user_id="+AppPreferences.getInstance(context).getUserId() + "&follow_type=user");}

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
			});

		}

		return view;
	}



}
