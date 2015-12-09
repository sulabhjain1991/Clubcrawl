package com.linchpin.clubcrawl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class ForgetPassword extends Activity
{
	private BeanResponse beanResponse;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_forgetpassword);
		setTitleValues();
		Button btnSubmit = (Button) findViewById(R.id.btnEmailSubmit);
		ImageView ivBack = (ImageView) findViewById(R.id.ivMenu);
		
		btnSubmit.setOnClickListener(new OnClickListener()
		{
			 
			@Override
			public void onClick(View v) 
			{
				EditText etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
				if(etEmailAddress.getText().toString().equals(""))
				{
					Toast.makeText(ForgetPassword.this, getString(R.string.strEmptyEmailError), Toast.LENGTH_LONG).show();
				}
				else if(!ConstantUtil.isValidEmail(etEmailAddress.getText().toString()))
				{
					Toast.makeText(ForgetPassword.this, getString(R.string.strValidEnailErro), Toast.LENGTH_LONG).show();
				}
				else if(ConstantUtil.isNetworkAvailable(ForgetPassword.this))
				{
					new ForgetPasswordTask(ForgetPassword.this).execute();
				}
				else
					Toast.makeText(ForgetPassword.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
			}
		});
		
		ivBack.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
			}
		});
		
	}
	
	
	//forget password task
	public class ForgetPasswordTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pd;
		Context con;

		public ForgetPasswordTask(Context con)
		{
			this.con = con;
		}

		@Override
		protected void onPreExecute() 
		{
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);
			

			
		}
		@Override
		protected Void doInBackground(Void... params)
		{
			EditText etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
			String url = "";
			Gson gson = new Gson();
			url = ConstantUtil.forgetPasswordUrl + "user_email="+etEmailAddress.getText().toString();
			String responseString = ConstantUtil.http_connection(url);

			if(responseString != null && !responseString.equals(""))
			{
				beanResponse = gson.fromJson(responseString, BeanResponse.class);

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			if(pd.isShowing())
			{
				pd.dismiss();
			}
			Message myMessage = new Message(); 
			myMessage.obj = "Forget_Password_Task";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}
	private Handler myHandler = new Handler() 
	{

		public void handleMessage(Message msg)
		{


			if (msg.obj.toString().equalsIgnoreCase("Forget_Password_Task"))
			{
				if (!isFinishing()) 
				{

					if((beanResponse == null ))

					{


						Toast.makeText(ForgetPassword.this, getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
					}
					else 
					{
						Toast.makeText(ForgetPassword.this,beanResponse.getMessage() , Toast.LENGTH_LONG).show();
						finish();

					}
					



				}
			}


		}
	};
	
	//set title values
	private void setTitleValues()
	{
		TextView tvTitle = (TextView) findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.strForgottenPassword));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(
				  0, //left
				  0, //top
				  0, //right
				  0//bottom
				  );
		
		ImageView ivBack = (ImageView) findViewById(R.id.ivMenu);
		ivBack.setImageResource(R.drawable.drop_btn);
		
		ImageView ivSearch = (ImageView) findViewById(R.id.ivSearch);
		ivSearch.setVisibility(View.INVISIBLE);
		
		ImageView ivComposePOst = (ImageView) findViewById(R.id.ivComposePost);
		ivComposePOst.setVisibility(View.INVISIBLE);
		
	}
	@Override
	public void onBackPressed()
	{
		finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
		super.onBackPressed();
	}

}
