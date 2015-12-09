package com.linchpin.clubcrawl;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BarMapScreen extends FragmentActivity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener
{
	private GoogleMap					mGoogleMap;
	String latitude;
	String longitude;
	String barName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar_map);
		
		latitude = getIntent().getExtras().getString("lattitude");
		longitude = getIntent().getExtras().getString("longitude");
		barName = getIntent().getExtras().getString("barName");
		
		TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvLocation.setText(barName);
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS)
		{
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}
		else
		{

			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mGoogleMap = fragment.getMap();
			MarkerOptions markerOptions = new MarkerOptions();
			
			double lat = Double.parseDouble(latitude);
			double lng = Double.parseDouble(longitude);
			LatLng latLng = new LatLng(lat, lng);
			if (mGoogleMap != null)
			{
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
			}
			markerOptions.position(latLng);
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_map_hover));
			Marker mark = mGoogleMap.addMarker(markerOptions);
			
			
		}

	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
