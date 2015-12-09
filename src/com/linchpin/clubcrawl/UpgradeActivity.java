package com.linchpin.clubcrawl;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.FacebookShared;

public class UpgradeActivity extends Activity {
	
	TextView tvCancel,tvUpgrademe;
	public IInAppBillingService	mService;
	AppPreferences				appPreferences;
	String						InAppBillingSKUId	= "ads_free_version_call_recorder_app";
	ServiceConnection			mServiceConn		= new ServiceConnection()
	{

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mService = IInAppBillingService.Stub.asInterface(service);
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	setContentView(R.layout.activity_upgrade);
	bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);
	appPreferences = new AppPreferences(UpgradeActivity.this);	
	initAll();
		setListeners();
		forRemoveAd();
		super.onCreate(savedInstanceState);
	}

	public void forRemoveAd()
	{
		System.out.println("Enter in for remove ad method");
		new GetPurchaseItems().execute();
	}
	private void setListeners() {
		// TODO Auto-generated method stub
		
		tvCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
			}
		});
		
		tvUpgrademe.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//In-app purchase//
			//	forRemoveAd();
			
			}
		});
		
	}

	private void initAll() {
		// TODO Auto-generated method stub
		tvCancel=(TextView) findViewById(R.id.tvCancel);
		tvUpgrademe=(TextView) findViewById(R.id.tvUpgrademe);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1001)
		{
			{
				if (resultCode == RESULT_OK)
				{
					try
					{
						////////////Come here if first time purchased(only once)/////////////////
						appPreferences.setAdPurchased(true);
						String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
						JSONObject jo = new JSONObject(purchaseData);
						String sku = jo.getString("productId");

					}
					catch (JSONException e)
					{

						e.printStackTrace();
					}
				}
			}

		}
		else
			FacebookShared.onActivityResult(requestCode, resultCode, data);
	}
	
	
	/////////////////////////////////In-app purchase code///////////////////////////////////
	public class GetPurchaseItems extends AsyncTask<Void, Void, String>
	{

		Bundle				skuDetails;
		int					response;
		private String		sku;
		boolean				error	= false;
		Bundle				querySkus;
		ArrayList<String>	skuList;

		@Override
		protected void onPreExecute()
		{

			try
			{

			}
			catch (Exception e)
			{

			}
		}

		@Override
		protected String doInBackground(Void... params)
		{

			try
			{
				// TODO Auto-generated method stub
				if (mService != null)
				{
					skuList = new ArrayList<String>();
					skuList.add(InAppBillingSKUId);
					querySkus = new Bundle();
					querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

					try
					{

						skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
					}
					catch (RemoteException e)
					{
						// TODO Auto-generated catch block

						e.printStackTrace();
						return "customException";
					}
					int response = skuDetails.getInt("RESPONSE_CODE");

					if (response == 0)
					{
						ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

						for (String thisResponse : responseList)
						{
							JSONObject object = null;
							try
							{
								object = new JSONObject(thisResponse);
							}
							catch (JSONException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
								return "customException";
							}

							try
							{
								sku = object.getString("productId");
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "customException";
							}
							String price = null;
							try
							{
								price = object.getString("price");
								//context.title = object.getString("description");
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (InAppBillingSKUId.equals(sku))
							{
								//context.mPremiumUpgradePrice = price;
							}
							else
							{
								// Show that toast and Exit to Home screen;
								error = true;

							}

						}
					}
					try
					{

						Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", null);
						int responseForBuySku = buyIntentBundle.getInt("RESPONSE_CODE");
						response = responseForBuySku;
						if (responseForBuySku == 0)
						{

							PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
							try
							{
								startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
							}
							catch (SendIntentException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "customException";
							}
						}
						else if (responseForBuySku == 7)
						{
							response = 7;
						}

					}
					catch (RemoteException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "customException";
					}
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "customException";
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try
			{
				if (result != null && result.equals("customException"))
				{
					Toast.makeText(UpgradeActivity.this, "An error has occured, Please try again again later", Toast.LENGTH_LONG).show();
				}
				else
				{
					if (response == 7)
					{
						/////////come here if  already purchased
						appPreferences.setAdPurchased(true);

					}
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	
	
}
