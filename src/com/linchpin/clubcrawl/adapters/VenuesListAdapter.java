package com.linchpin.clubcrawl.adapters;

import org.json.JSONException;
import org.json.JSONObject;

import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanListVenues;
import com.linchpin.clubcrawl.beans.BeanVenuesInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class VenuesListAdapter extends BaseAdapter {

	Context context;
	BeanListVenues venues;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	String clickable;
	public VenuesListAdapter(Context context,BeanListVenues venues, String clickable) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.venues=venues;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		this.clickable=clickable;
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return venues.getVenueDetails().size();
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
		View view=inflater.inflate(R.layout.adapter_following_venues, null);
		com.linchpin.clubcrawl.helper.ImageViewRounded ivVenueImage=(ImageViewRounded) view.findViewById(R.id.ivVenueImage);
		TextView tvVenueName=(TextView) view.findViewById(R.id.tvVenueName);
		TextView tvVenueAddress=(TextView) view.findViewById(R.id.tvVenueAddress);
		final BeanVenuesInfo venuesinfo=venues.getVenueDetails().get(position);

		tvVenueName.setText(venuesinfo.getName());
		tvVenueAddress.setText(venuesinfo.getAddress());
		imageLoader.displayImage(ConstantUtil.venuesImagesUrl
				+ venuesinfo.getLogo(), ivVenueImage,
				options, null);
		final TextView tvFollowing=(TextView) view.findViewById(R.id.tvFollowing);


		if(clickable.equals("true")){


			tvFollowing.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					final Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.unfollow_user_dialog);
					dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					TextView tvName=(TextView) dialog.findViewById(R.id.tvName);
					tvName.setText(venuesinfo.getName());
					dialog.findViewById(R.id.tvUnfollow).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							try {


								dialog.cancel();
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
												venues.getVenueDetails().remove(Currentposition);
												notifyDataSetChanged();

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
											Toast.makeText(context, "Error from server", Toast.LENGTH_LONG).show();
										}







									}
								});
								networkTask.setProgressDialog(false);
								String venueId=venuesinfo.getVenue_id();
								String url=ConstantUtil.unFollowClickUrl+"id="+venuesinfo.getVenue_id()+"&user_id="+AppPreferences.getInstance(context).getUserId() + "&follow_type=venue";
								networkTask.execute(ConstantUtil.unFollowClickUrl+"id="+venuesinfo.getVenue_id()+"&user_id="+AppPreferences.getInstance(context).getUserId() + "&follow_type=venue");

							} catch (Exception e) {
								// TODO: handle exception
							}







						}

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
		else
		{
			tvFollowing.setOnClickListener(null);
		}




		return view;
	}

}
