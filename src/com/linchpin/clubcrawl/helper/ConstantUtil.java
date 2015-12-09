package com.linchpin.clubcrawl.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.linchpin.clubcrawl.LoginScreen;
import com.linchpin.clubcrawl.R;

public class ConstantUtil {
	public static int screenWidth;
	public static int screenHeight;
	public static String baseUrl = "http://162.13.137.28/siteadmin/";
	public static String baseUrlLptpl = "http://lptpl.info/clubcrawl/";

	public static String loginUrl = baseUrl + "api/loginuser/?callback=?&";
	public static String fbSignUpUrl = baseUrl + "api/addfbuser?";

	public static String fbLoginUrl = baseUrl + "api/login?";
	public static String forgetPasswordUrl = baseUrl + "club/userResetPasswordRequest/?callback=?&";
	public static String gallaryUrl = baseUrl + "club/gallery/?callback=?&";
	public static String getClubDetailUrl = baseUrl + "api/clubdetail/?";
	public static String getNotificationCount = baseUrl + "api/notify?";


	public static String profilePicsUrl = baseUrl + "api/getMyProfilePicture/?";
	public static String postStatusUrl = baseUrl + "post/uploadPost?";


	public static String deletePicUrl = baseUrl + "club/deleteMyProfilePicture/?";
	public static String uploadPicUrl = baseUrl	+ "club/uploadMyProfilePicture/?";
	public static String uploadProfilePicUrl = baseUrl + "club/uploadProfilePicture?";
	public static String signupUrl = baseUrl + "api/adduser/?callback=?&";
	public static String getVenueList = baseUrl	+ "api/getVenueListCurrentLocation/?";
	public static String updateVenueUrl = baseUrl + "api/updateMyLocation?";
	public static String updateProfile = baseUrl + "api/updateuser?";
	public static String galleryImageUrl = baseUrl + "images/gallery/";
	public static String userProfileDetail = baseUrl + "api/userProfileData?userID=";
	public static String friendProfileDetail = baseUrl + "api/getUser?";
	public static String userFriendsList = baseUrl + "api/userProfileData/?";
	public static String userFriendRequestList = baseUrl + "api/friendrequestlist/?";
	public static String searchFrindListUrl = baseUrl + "follow/friendsList?";
	public static String userFeedsList = baseUrl +  "api/friendfeed?user_id=";
	public static String userVenueFeedsList = baseUrl +  "newapi/venuePost?";
	public static String userWhosoutVenueList = baseUrl +  "api/getEventVenueList?";
	public static String totalNotification = baseUrl +  "api/totalNotification?user=";
	public static String notificationList = baseUrl +  "api/notification?user=";
	public static String deleteNotification = baseUrl +  "api/deleteNotification?id=";
	public static String nightsOutInvites = baseUrl +  "api/getEventInvites/?user=";
	public static String nightsOutEventsList = baseUrl +  "api/getEventsList/?curpage=";
	public static String nightsOutEventDetail = baseUrl + "api/getEventDetail/?e_id=";
	public static String nightsOutEventComment = baseUrl + "api/eventComments/?e_id=";
	public static String neaByUrl = baseUrl + "club/nearlist/?";
	public static String neaByTketsUrl = baseUrl + "api/skiddleEventByLocation/?";
	public static String updateEventInviteStatus = baseUrl + "api/updateEventInviteStatus?e_id=";
	public static String postCommentInNightOut = baseUrl + "api/addComment?e_id=";
	public static String searchEventTask = baseUrl + "api/searchEvents?term=";
	public static String invitePeopleFriendList = baseUrl + "api/getfriendlist/?";
	public static String getCategoryInSearchVenue = baseUrl + "club/getcat/";
	public static String getClubSearchList = baseUrl + "club/searchResult?";
	public static String getTrendeList = baseUrl + "api/trendingVenue/?";
	public static String createNightUrl = baseUrl +  "api/addEvent?";
	public static String getAllOfferList = baseUrl + "club/allofferlist/?";
	public static String ticketsUrl = baseUrl + "api/skiddleapi?";
	public static String likeUrl = baseUrl + "newapi/FeedLike?";
	public static String unlikeUrl = baseUrl + "newapi/FeedUnLike?";
	public static String friendRequestUrl = baseUrl + "api/sendFriendRequest?";
	public static String signOutUrl = baseUrl + "newapi/logout?";
	public static String venueEventUrl = baseUrl + "api/getEventListByVenueNew?";
	public static String changeNotificationUrl = baseUrl + "api/changenotificationStatus?";

	public static String locationUrl = baseUrl + "club/getlocations?locations_type=Location";
	public static String userTotalPostsUrl = baseUrl+"post/usersTotalPostCount?";
	public static String toTalFollowersUrl = baseUrl+"newapi/usersFollows?";

	public static String imageUrl = baseUrl + "images/locationslogo/";

	public static String profilePicBaseUrl = "http://162.13.137.28/siteadmin/images/profile_images/";
	public static String likedImageBaseUrl = "http://162.13.137.28/siteadmin/images/blog_images/";
	public static String inviteLogoImageBaseUrl = "http://162.13.137.28/siteadmin/images/complogo/searchlogo/";
	public static String nearByLogoImageBaseUrl = "http://162.13.137.28/siteadmin/images/complogo/thumb_small/";
	public static String OffersImageBaseUrl = "http://162.13.137.28/siteadmin/images/header_image/";
	public static String venueFeedsImageBaseUrl = "http://162.13.137.28/clubwebsite/";

	public static String followersListUrl = baseUrl+"follow/followersList?";
	public static String followersRequestListUrl = baseUrl+"follow/followReqUserList?";
	public static String followingListUrl = baseUrl+"follow/followingList?";
	public static String venuesListUrl = baseUrl+"newapi/venuesFollowsNames?";
	public static String venuesImagesUrl = baseUrl+"images/complogo/searchlogo/";
	public static String likersListUrl = baseUrl+"/newapi/LikeFriendNames?";
	public static String likersImageUrl=baseUrl+"images/profile_images/";
	public static String viewMorePostsUrl=baseUrl+"api/userProfileData?";
	public static String followClickUrl=baseUrl+"follow/savefollowstatus?";
	public static String unFollowClickUrl=baseUrl+"follow/unfollow?";
	public static String deletingChatUrl=baseUrl+"api/deleteWholeConversation?";
	public static String deletingSingleChatUrl=baseUrl+"api/deleteMessagedetail?";
	public static String headerImageUrl=baseUrl+"images/header_image/";
	public static String sendMessageUrl=baseUrl+"messenger/chatting?";
	public static String eventCountsUrl=baseUrl+"api/getEventCount/?e_id=";
	public static String messengerPicBaseUrl = "http://162.13.137.28/siteadmin/images/messenger_images/";
	public static String searchFriendsUrl = baseUrl+"/follow/friendsList?user_id=";
	public static String followersRequestUrl = baseUrl+"follow/followRequest?";





	public static String unfollowUrl=baseUrl+"follow/unfollow?";

	//http://162.13.137.28/siteadmin//newapi/unfollow?id=329&user_id=21&follow_type=user
	public static String STATUS_SUCCESS = "success";
	public static String STATUS_FAILURE = "fail";

	public static final int SHARE_IMAGE=1;
	public static final int SHARE_TEXT=0;

	// check validity of email
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}// Convert a date String from one format to another

	public static String changeDateFormat(String time, String fromFormat,
			String toFormat) {

		SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat(toFormat);
		String s = sdf1.format(date.getTime());
		return s;
	}

	// convert miliseconds(in long) to a string date with required format
	public static String convertMiliSecondsToString(long milliSeconds,
			String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		DateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	// convert util date into String with specific format
	public static String convertDateToString(Date date, String format) {

		DateFormat df = new SimpleDateFormat(format);
		String str = df.format(date);
		return str;

	}

	// convert String to Date
	public static Date convertStringToDate(String dateString, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;
	}

	public static long getDateDiffString(Date dateOne, Date dateTwo)
	{
		long timeOne = dateOne.getTime();
		long timeTwo = dateTwo.getTime();
		long oneDay = 1000 * 60 * 60 * 24;
		long delta = (timeTwo - timeOne) / oneDay;

		//	    if (delta > 0) {
		//	        return "dateTwo is " + delta + " days after dateOne";
		//	    }
		//	    else {
		//	        delta *= -1;
		//	        return "dateTwo is " + delta + " days before dateOne";
		//	    }
		return delta;
	}

	// convert String date to calendar
	public static Calendar convertStringToCal(String dateString, String format) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			cal.setTime(sdf.parse(dateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// all done
		return cal;
	}

	// get resolution of the screen
	public static void getScreen_Height(Activity c) {
		DisplayMetrics dm = new DisplayMetrics();
		c.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	public static void showDialog(final Context context, String title,
			String message, final String click_button) {
		System.out.println(context.getClass());
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
		alt_bld.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(click_button,
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = alt_bld.create();
		alert.setTitle("\t" + title);
		// alert.setIcon(AlertDialog.BUTTON_NEGATIVE);
		alert.show();
	}

	// check if net is available or not
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (connMgr.getActiveNetworkInfo() != null
				&& connMgr.getActiveNetworkInfo().isAvailable()
				&& connMgr.getActiveNetworkInfo().isConnected())
			return true;
		else {
			Log.v("PIPA", "Internet Connection Not Present");
			return false;
		}

	}

	// convert milliseconds into the day of the week string
	public static String dayStringFormat(long msecs) {
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime(new Date(msecs));

		int dow = cal.get(Calendar.DAY_OF_WEEK);

		switch (dow) {
		case Calendar.MONDAY:
			return "Monday";
		case Calendar.TUESDAY:
			return "Tuesday";
		case Calendar.WEDNESDAY:
			return "Wednesday";
		case Calendar.THURSDAY:
			return "Thursday";
		case Calendar.FRIDAY:
			return "Friday";
		case Calendar.SATURDAY:
			return "Saturday";
		case Calendar.SUNDAY:
			return "Sunday";
		}

		return "Unknown";
	}

	public static String http_connection(String url_string) {

		String str = null;
		try {
			//
			url_string = url_string.replace(" ", "%20");
			URL url = new URL(url_string);

			URLConnection connection = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuffer buffer = new StringBuffer("");

			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			str = buffer.toString();
			// xpp.setInput(br);
		} catch (Exception ae) {
			ae.printStackTrace();

		}
		return str;

	}


	// upload pic to server

	public static void shareDialog(final Context con,final int shareType,final String msg,final String imageUrl,final File f)
	{
		final Dialog dialog = new Dialog(con);
		dialog.getWindow().setLayout(100, 100);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.share_dialog);
		TextView tvFacebook = (TextView) dialog.findViewById(R.id.tvFacebook);
		TextView tvTwitter = (TextView) dialog.findViewById(R.id.tvTwitter);



		tvFacebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				String aarti = "Hello friends this is demo for facebook.";

				FacebookShared fs = new FacebookShared((Activity) con,con);


				fs.setData(msg,imageUrl,shareType);

				fs.loginToFacebook(); //for logout dialog

				//				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				//				   shareIntent.setType("text/plain");
				//				   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, (String) v.getTag(R.string.app_name));
				//				   shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.yahoo.com");
				//
				//				   PackageManager pm = v.getContext().getPackageManager();
				//				   List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
				//				   boolean isFacebook = false;
				//				     for (final ResolveInfo app : activityList) 
				//				     {
				//				         if ((app.activityInfo.name).contains("facebook")) 
				//				         {
				//				        	 isFacebook = true;
				//				           final ActivityInfo activity = app.activityInfo;
				//				           final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
				//				          shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				//				          shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				//				          shareIntent.setComponent(name);
				//				          v.getContext().startActivity(shareIntent);
				//				          break;
				//				        }
				//				      }
				//				     if(!isFacebook)
				//				     {
				//				    	 String appPackageName = "com.facebook.katana";
				//				    	 con.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))); 
				//				     }
				//				
			}
		});

		tvTwitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("*/*");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, (String) v.getTag(R.string.app_name));
				if(shareType == ConstantUtil.SHARE_TEXT)
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,msg);
				else
					shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f) );

				PackageManager pm = v.getContext().getPackageManager();
				List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
				boolean isTwitter = false;
				for (final ResolveInfo app : activityList) 
				{
					if ((app.activityInfo.name).contains("twitter"))
					{
						isTwitter = true;
						final ActivityInfo activity = app.activityInfo;
						final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
						shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						shareIntent.setComponent(name);
						v.getContext().startActivity(shareIntent);
						break;
					}
				}
				if(!isTwitter)
				{
					String appPackageName = "com.twitter.android";
					con.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))); 
				}

			}
		});
		dialog.show();
	}

	public static void showSneakDialog(final Context con)
	{
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(con);
		alt_bld.setMessage("Please login to unlock Clubcrawl's best kept secrets. Would you like to login?")
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				Intent i = new Intent(con,LoginScreen.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				con.startActivity(i);

			}
		})
		.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});


		AlertDialog alert = alt_bld.create();
		alert.setTitle("\t" + "ClubCrawl");
		// alert.setIcon(AlertDialog.BUTTON_NEGATIVE);
		alert.show();

	}

	public static File String_to_File(String img_url) {
		File casted_image = null;
		try {
			File rootSdDirectory = Environment.getExternalStorageDirectory();


			casted_image = new File(rootSdDirectory, "attachment.jpg");
			if (casted_image.exists()) {
				casted_image.delete();
			}
			casted_image.createNewFile();

			FileOutputStream fos = new FileOutputStream(casted_image);

			URL url = new URL(img_url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			InputStream in = connection.getInputStream();

			byte[] buffer = new byte[1024];
			int size = 0;
			while ((size = in.read(buffer)) > 0) {
				fos.write(buffer, 0, size);
			}
			fos.close();
			return casted_image;

		} catch (Exception e) {

			System.out.print(e);
			// e.printStackTrace();

		}
		return casted_image;
	}


}
