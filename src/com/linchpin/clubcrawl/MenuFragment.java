package com.linchpin.clubcrawl;

import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.fragments.FeedFragment;
import com.linchpin.clubcrawl.fragments.MessagingFragment;
import com.linchpin.clubcrawl.fragments.MyProfilePicsFragments;
import com.linchpin.clubcrawl.fragments.NotificationFragment;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.ImageViewRounded;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.helper.TabViewHelper;

public class MenuFragment extends Fragment implements OnClickListener,Result
{

	private  View				view;

	RelativeLayout				rlMain, rlSecond;
	public static String		currentsel	= "";
	public static Class			activity;
	public static BeanUserInfo	beanUserInfo;
	int							curPosition;
	private final int SIGN_OUT = 0;

	private void deSelectAll(View view)
	{
		view.findViewById(R.id.vProfile).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.vFeeds).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.vNotification).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.vMessaging).setVisibility(View.INVISIBLE);

		((TextView) view.findViewById(R.id.tvProfileName)).setTextColor(getResources().getColor(R.color.grayText));
		((TextView) view.findViewById(R.id.tvFeeds)).setTextColor(getResources().getColor(R.color.grayText));
		((TextView) view.findViewById(R.id.tvNotification)).setTextColor(getResources().getColor(R.color.grayText));
		((TextView) view.findViewById(R.id.tvMessaging)).setTextColor(getResources().getColor(R.color.grayText));
		((TextView) view.findViewById(R.id.tvSignout)).setTextColor(getResources().getColor(R.color.grayText));

		ImageViewRounded imgView = ((ImageViewRounded) view.findViewById(R.id.ivProfileImage));
		imgView.setBorder(true);
		imgView.setBorderColor(getActivity().getResources().getColor(R.color.default_border_color));
		imgView.invalidate();
		imgView = ((ImageViewRounded) view.findViewById(R.id.ivFeeds));
		imgView.setBorder(true);
		imgView.setBorderColor(getActivity().getResources().getColor(R.color.default_border_color));
		imgView.invalidate();
		imgView = ((ImageViewRounded) view.findViewById(R.id.ivNotification));
		imgView.setBorder(true);
		imgView.setBorderColor(getActivity().getResources().getColor(R.color.default_border_color));
		imgView.invalidate();
		imgView = ((ImageViewRounded) view.findViewById(R.id.ivMessaging));
		imgView.setBorder(true);
		imgView.setBorderColor(getActivity().getResources().getColor(R.color.default_border_color));
		imgView.invalidate();
		imgView = ((ImageViewRounded) view.findViewById(R.id.ivSignout));
		imgView.setBorder(false);
		imgView.setBorderColor(getActivity().getResources().getColor(R.color.default_border_color));
		imgView.invalidate();
	}

	private void selectRow(int id)
	{
		ImageViewRounded imgView = null;
		TextView textView = null;
		;
		switch (id)
		{
		case R.id.UserProfile:
			view.findViewById(R.id.vProfile).setVisibility(View.VISIBLE);
			imgView = ((ImageViewRounded) view.findViewById(R.id.ivProfileImage));
			textView = ((TextView) view.findViewById(R.id.tvProfileName));
			break;
		case R.id.rlFeeds:
			view.findViewById(R.id.vFeeds).setVisibility(View.VISIBLE);
			imgView = ((ImageViewRounded) view.findViewById(R.id.ivFeeds));
			textView = ((TextView) view.findViewById(R.id.tvFeeds));
			break;
		case R.id.rlNotification:
			view.findViewById(R.id.vNotification).setVisibility(View.VISIBLE);
			imgView = ((ImageViewRounded) view.findViewById(R.id.ivNotification));
			textView = ((TextView) view.findViewById(R.id.tvNotification));
			break;
		case R.id.rlMessaging:
			view.findViewById(R.id.vMessaging).setVisibility(View.VISIBLE);
			imgView = ((ImageViewRounded) view.findViewById(R.id.ivMessaging));
			textView = ((TextView) view.findViewById(R.id.tvMessaging));
			break;
		case R.id.rlSignout:
			imgView = ((ImageViewRounded) view.findViewById(R.id.ivSignout));
			textView = ((TextView) view.findViewById(R.id.tvSignout));
			break;
		default:
			break;
		}
		if (imgView != null)
		{
			imgView.setBorder(true);
			imgView.setBorderColor(getActivity().getResources().getColor(R.color.background_color));
			imgView.invalidate();
		}
		if (textView != null) textView.setTextColor(getResources().getColor(R.color.background_color));

	}

	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{

		if (view != null)
		{
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) parent.removeView(view);
		}
		view = inflater.inflate(R.layout.slider_menu, container, false);
		deSelectAll(view);
		try
		{

			ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
			TextView tvProfileName = (TextView) view.findViewById(R.id.tvProfileName);
			tvProfileName.setText(beanUserInfo.getUser().getUsername());
			view.findViewById(R.id.tvupgrade_me).setOnClickListener(this);
			view.findViewById(R.id.UserProfile).setOnClickListener(this);
			view.findViewById(R.id.rlFeeds).setOnClickListener(this);
			view.findViewById(R.id.rlNotification).setOnClickListener(this);
			view.findViewById(R.id.rlMessaging).setOnClickListener(this);
			view.findViewById(R.id.rlSignout).setOnClickListener(this);
			changeNotificationcount();

		}
		catch (Exception e)
		{
		}
		view.findViewById(R.id.tvupgrade_me).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getActivity(),UpgradeActivity.class);
				startActivity(i);
				getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

			}
		});

		return view;
	}

	public void setProfileImage()
	{
		ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
		AQuery aq = new AQuery(ivProfileImage);

		Bitmap bm;
		try
		{

			ImageOptions options = new ImageOptions();

			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			options.preset = icon;


			if(beanUserInfo.getUser().getProfile_pic() == null || beanUserInfo.getUser().getProfile_pic().equals(""))
			{
				bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
				ivProfileImage.setImageBitmap(bm);
			}
			else
			{
				aq.id(R.id.ivProfileImage).image(ConstantUtil.profilePicBaseUrl + beanUserInfo.getUser().getProfile_pic(), options);
			}
		}

		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_img_jst_yet);
			ivProfileImage.setImageBitmap(bm);
		}



	}

	@Override
	public void onResume()
	{
		setProfileImage();
		super.onResume();
	}

	@Override
	public void onClick(View v)
	{
		deSelectAll(view);
		selectRow(v.getId());
		switch (v.getId())
		{
		case R.id.UserProfile:
			if (!currentsel.equals("MyProfilePicsFragments"))
			{
				activity = MyProfilePicsFragments.class;
				MyProfilePicsFragments fr = new MyProfilePicsFragments();
				TabViewHelper.newLeftNavigationActionFragment((SlidingFragmentActivity) getActivity(), fr, "MyProfilePicsFragments");
			}
			else ((SlidingFragmentActivity) getActivity()).getSlidingMenu().showContent();
			break;
		case R.id.rlMessaging:
			if (!currentsel.equals("FriendListFragments"))
			{
				activity = MessagingFragment.class;
				MessagingFragment fr = new MessagingFragment();
				TabViewHelper.newLeftNavigationActionFragment((SlidingFragmentActivity) getActivity(), fr, "FriendListFragments");
			}
			else ((SlidingFragmentActivity) getActivity()).getSlidingMenu().showContent();
			break;

		case R.id.rlNotification:
			if (!currentsel.equals("NotificationFragment"))
			{
				activity = NotificationFragment.class;
				NotificationFragment fr = new NotificationFragment();
				TabViewHelper.newLeftNavigationActionFragment((SlidingFragmentActivity) getActivity(), fr, "FriendListFragments");
			}
			else ((SlidingFragmentActivity) getActivity()).getSlidingMenu().showContent();
			break;

		case R.id.rlSignout:
			if(ConstantUtil.isNetworkAvailable(getActivity()))
			{
				NetworkTask networkTask = new NetworkTask(getActivity(), SIGN_OUT);
				networkTask.setProgressDialog(true);
				networkTask.exposePostExecute(MenuFragment.this);
				AppPreferences appPref = new AppPreferences(getActivity());
				String urlSignOut = ConstantUtil.signOutUrl + "user_id="+appPref.getUserId();
				networkTask.execute(urlSignOut);
			}
			else
				Toast.makeText(getActivity(),
						getString(R.string.internetFailure),
						Toast.LENGTH_LONG).show();

			break;
		case R.id.rlFeeds:
			if (!currentsel.equals("FeedFragment"))
			{

				activity = FeedFragment.class;
				// TabViewHelper.newLeftNavigationAction(getActivity());
				FeedFragment fr = new FeedFragment();
				TabViewHelper.newLeftNavigationActionFragment((SlidingFragmentActivity) getActivity(), fr, "FeedFragment");
			}
			else
			{
				((SlidingFragmentActivity) getActivity()).getSlidingMenu().showContent();
			}
			break;
		case R.id.tvupgrade_me:
			((MainScreen)getActivity()).forRemoveAd();
			break;


		default:
			break;
		}

	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		switch (id) {
		case SIGN_OUT:
			Gson gson = new Gson();
			BeanResponse response = (BeanResponse)gson.fromJson(object, BeanResponse.class);
			if(response != null)
			{
				if(response.getStatus().equals("success"))
				{

					AppPreferences appPref = new AppPreferences(getActivity());
					appPref.clearPref();
					if(LoginScreen.facebook != null)

						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									LoginScreen.facebook.logout(APP.GLOBAL().getApplicationContext());
									APP.GLOBAL().getEditor().putString(APP.PREF.FB_ACCESS_TOKEN.key, null).commit();
									APP.GLOBAL().getEditor().putLong(APP.PREF.FB_ACCESS_EXPIRES.key, 0).commit();
								}  catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}).start();


					getActivity().finish();
					Intent i = new Intent(getActivity(), LoginScreen.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					Toast.makeText(getActivity(), "Logout successful.", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
			}

			break;

		default:
			break;
		}

	}
	@Override
	public void onAttach(Activity activity) 
	{
		((MainScreen)activity).getInstance(this);
		super.onAttach(activity);
	}
	public  void changeNotificationcount()
	{

		if(view != null)
		{
			System.out.println("inside change notification count");
			//	ImageViewRounded imgViewProfile = ((ImageViewRounded) view.findViewById(R.id.ivProfileImage));
			ImageViewRounded imgNotification=(ImageViewRounded) view.findViewById(R.id.ivNotification);


			//	imgViewProfile.setNumberText(String.valueOf(APP.GLOBAL().notificationcount));
			imgNotification.setNumberText(String.valueOf(APP.GLOBAL().notificationcount));

			ImageViewRounded imgViewMessaging = ((ImageViewRounded) view.findViewById(R.id.ivMessaging));
			imgViewMessaging.setNumberText(String.valueOf(APP.GLOBAL().MessageCount));
		}
	}

	public interface MenuFragmentInstance
	{
		void getInstance(MenuFragment fr);
	}


}