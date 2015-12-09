package com.linchpin.clubcrawl.adapters;

import org.json.JSONObject;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanFollowRequestInfo;
import com.linchpin.clubcrawl.beans.BeanFollowRequestList;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FollowRequestAdapter extends BaseAdapter {

	Context context;
	BeanFollowRequestList list;
	View view;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	String result;
	
	
	public FollowRequestAdapter(Context context,BeanFollowRequestList list) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.list=list;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.getList().size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	view=inflater.inflate(R.layout.fragment_follow_req_adapter, null);
	ImageViewRounded ivFollowReqImage=(ImageViewRounded) view.findViewById(R.id.ivFollowReqImage);
	TextView tvFollowReqName=(TextView) view.findViewById(R.id.tvFollowersName);
	TextView tvAccept=(TextView) view.findViewById(R.id.tvAccept);
	TextView tvDecline=(TextView) view.findViewById(R.id.tvDecline);
	
	BeanFollowRequestInfo beanFollowReqInfo=list.getList().get(position);
	
	imageLoader.displayImage(ConstantUtil.profilePicBaseUrl
			+ beanFollowReqInfo.getProfile_pic(), ivFollowReqImage,
			options, null);
	tvFollowReqName.setText(beanFollowReqInfo.getFirst_name()+" "+beanFollowReqInfo.getLast_name());
	
	tvAccept.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			result="accept";
			
			NetworkTask networktask=new NetworkTask(context, 101);
			networktask.exposePostExecute(new Result() {
				
				@Override
				public void resultfromNetwork(String object, int id, int arg1, String arg2) {
					// TODO Auto-generated method stub
					try {
						JSONObject jObject=new JSONObject(object);
						if (jObject.get("status").equals("success"))
						{
							int currentPosition=position;
							
							list.getList().remove(currentPosition);
							notifyDataSetChanged();
							
							
						}
						else
						{
							Toast toast= Toast.makeText(context,"Network error try again later", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
						
						
						
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			networktask.setProgressDialog(false);
			networktask.execute(ConstantUtil.followersRequestUrl+"user_id="+list.getList().get(position).getUser_id()+"&follow_type_id="+AppPreferences.getInstance(context).getUserId()+"&follow_type=user&req_type="+result);
			
			
			
		}
	});
	
	tvDecline.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			result="decline";
			
			NetworkTask networktask=new NetworkTask(context, 101);
			networktask.exposePostExecute(new Result() {
				
				@Override
				public void resultfromNetwork(String object, int id, int arg1, String arg2) {
					// TODO Auto-generated method stub
					try {
						JSONObject jObject=new JSONObject(object);
						if (jObject.get("status").equals("success"))
						{
							int currentPosition=position;
							
							list.getList().remove(currentPosition);
							notifyDataSetChanged();
							
							
						}
						else
						{
							Toast toast= Toast.makeText(context,"Network error try again later", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
						
						
						
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			networktask.setProgressDialog(false);
			networktask.execute(ConstantUtil.followersRequestUrl+"user_id="+list.getList().get(position).getUser_id()+"&follow_type_id="+AppPreferences.getInstance(context).getUserId()+"&follow_type=user&req_type="+result);
			
			
			
		}
	});
	
	
		
		
		
		
		
		return view;
	}

}
