package com.linchpin.clubcrawl;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.adapters.LocationListAdapter;
import com.linchpin.clubcrawl.beans.BeanListLocationInfo;
import com.linchpin.clubcrawl.beans.BeanLocationsInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class LocationSelection extends Activity implements Result
{
	private final int				GET_LOCATION	= 1;
	private ListView				lvLocations;
	private ImageView				ivImageView;
	private LocationListAdapter		adapter;
	AQuery							aquery			= new AQuery(this);
	String							ImageUrl		= ConstantUtil.imageUrl;
	AppPreferences					appPreferences;
	private BeanListLocationInfo	listLocationInfo;
	int								Listposition	= -1;
	TextView						tvDone;
	TextView						previousSelection;
	protected final int				LOCATION		= 0x00003;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_screen);
		appPreferences = new AppPreferences(LocationSelection.this);
		lvLocations = (ListView) findViewById(R.id.lvcitylist);
		ivImageView = (ImageView) findViewById(R.id.ivCityImage);
		tvDone = (TextView) findViewById(R.id.tvdone);
		setData(APP.GLOBAL().getPreferences().getString(APP.PREF.ALL_CITIES.key, ""));
		getLocationTask();
		Bitmap preset = aquery.getCachedImage(R.drawable.no_img);
		aquery.id(R.id.ivCityImage).image(ConstantUtil.imageUrl + AppPreferences.getInstance(this).getimageUrl(), true, true, 0, R.drawable.no_img, preset, AQuery.FADE_IN);

		final AppPreferences appPreferences = new AppPreferences(LocationSelection.this);

		lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
			{
				if (Listposition != -1) ((TextView) getViewByPosition(Listposition, lvLocations).findViewById(R.id.tvlocationsName)).setTextColor(getResources().getColor(R.color.list_fg_nor));

				Listposition = position;
				BeanLocationsInfo info = listLocationInfo.getLocations().get(Listposition);
				arg1.setSelected(true);
//				((TextView) arg1.findViewById(R.id.tvlocationsName)).setTextColor(getResources().getColor(R.color.list_fg_selected));

				Bitmap preset = aquery.getCachedImage(R.drawable.no_img);
				aquery.id(R.id.ivCityImage).image(ConstantUtil.imageUrl + info.getLocations_logo(), true, true, 0, R.drawable.no_img, preset, AQuery.FADE_IN);

			}

		});
		tvDone = (TextView) findViewById(R.id.tvdone);
		tvDone.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (Listposition != -1)
				{
					BeanLocationsInfo info = listLocationInfo.getLocations().get(Listposition);
					appPreferences.setCityId(info.getLocations_id());
					appPreferences.setCityName(info.getLocations_name());
					appPreferences.setimageUrl(info.getLocations_logo());
					Toast.makeText(getApplicationContext(), "Successfully updated city", Toast.LENGTH_LONG).show();
					if (ConstantUtil.isNetworkAvailable(LocationSelection.this))
					{
						NetworkTask networkTask = new NetworkTask(LocationSelection.this, LOCATION);
						networkTask.exposePostExecute(LocationSelection.this);
						networkTask.setProgressDialog(true);
						networkTask.execute("http://maps.google.com/maps/api/geocode/json?address=" + AppPreferences.getInstance(LocationSelection.this).getCityName() + "&sensor=false");

					}
				}

			}
		});
	}

	public View getViewByPosition(int pos, ListView listView)
	{
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (lastListItemPosition != -1 && pos < firstListItemPosition || pos > lastListItemPosition)
		{
			return listView.getAdapter().getView(pos, null, listView);
		}
		else
		{
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	private void getLocationTask()
	{
		String locationUrl = "http://162.13.137.28/siteadmin/club/getlocations?locations_type=Location";
		if (ConstantUtil.isNetworkAvailable(LocationSelection.this))
		{
			NetworkTask fblLoginTask = new NetworkTask(LocationSelection.this, GET_LOCATION);
			fblLoginTask.exposePostExecute(LocationSelection.this);
			fblLoginTask.execute(ConstantUtil.locationUrl);
		}
		//new FbLoginTask(LoginScreen.this).execute();
		else Toast.makeText(LocationSelection.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

	}

	private void setData(String data)
	{
		if (!data.equals("") && data != null)
		{
			APP.GLOBAL().getEditor().putString(APP.PREF.ALL_CITIES.key, data).commit();
			Gson gson = new Gson();
			listLocationInfo = gson.fromJson(data, BeanListLocationInfo.class);
			adapter = new LocationListAdapter(getApplicationContext(), listLocationInfo);

			aquery.id(R.id.ivCityImage).image(ConstantUtil.imageUrl + appPreferences.getimageUrl(), false, false);
			lvLocations.setAdapter(adapter);
		}
	}

	@Override
	public void resultfromNetwork(String data, int id,int arg1,String arg2)
	{
		switch (id)
		{
			case GET_LOCATION:
				if (!data.equals("") && data != null) setData(data);
				else Toast.makeText(LocationSelection.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				break;
			case LOCATION:
				try
				{
					JSONObject jsonObject = new JSONObject(data);
					if (jsonObject.get("status").equals("OK"))
					{
						jsonObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
						APP.GLOBAL().getEditor().putString(APP.PREF.CITY_LAT.key, jsonObject.getString("lat")).commit();
						APP.GLOBAL().getEditor().putString(APP.PREF.CITY_LNG.key, jsonObject.getString("lng")).commit();
						LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("lng")));
						LocationSelection.this.finish();
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			default:
				break;
		}

	}

}
