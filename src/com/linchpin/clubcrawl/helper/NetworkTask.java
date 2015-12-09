package com.linchpin.clubcrawl.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.linchpin.clubcrawl.R;

public class NetworkTask extends AsyncTask<String, String, String>
{
	boolean	isDoinBackground, isPostExecute, isPreExecute, isProgressDialog = true;

	public boolean isProgressDialog()
	{
		return isProgressDialog;
	}

	public void setProgressDialog(boolean isProgressDialog, String dialogMessage)
	{
		this.isProgressDialog = isProgressDialog;
		this.dialogMessage = dialogMessage;
	}

	public void setProgressDialog(boolean isProgressDialog)
	{
		this.isProgressDialog = isProgressDialog;

	}

	String			dialogMessage	= "Loading";
	DoinBackgroung	doinBackgroung;
	Result			result;
	PreNetwork		preNetwork;
	ProgressDialog	pd;
	Context			ctx;
	int				id;
	int				arg1;
	String			arg2;

	public NetworkTask(Context ctx, int id)
	{
		this.ctx = ctx;
		this.id = id;
	}

	public NetworkTask(Context ctx, int id, int arg1, String arg2)
	{
		this.ctx = ctx;
		this.id = id;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	public void exposeDoinBackground(DoinBackgroung doinBackgroung)
	{
		this.isDoinBackground = true;
		this.doinBackgroung = doinBackgroung;
	}

	public void exposePostExecute(Result result)
	{
		this.isPostExecute = true;
		this.result = result;
	}

	public void exposePreExecute(PreNetwork preNetwork)
	{
		this.isPreExecute = true;
		this.preNetwork = preNetwork;
	}

	public interface DoinBackgroung
	{

		String doInBackground(Integer id, String... params);
	}

	public interface Result
	{
		void resultfromNetwork(String object, int id, int arg1, String arg2);
	}

	public interface PreNetwork
	{
		void preNetwork(int id);
	}

	View	v;

	@Override
	protected void onPreExecute()
	{
		if (isProgressDialog)
		{
			pd = ProgressDialog.show(ctx, null, dialogMessage);
			LayoutInflater inflater = LayoutInflater.from(ctx);
			View v = inflater.inflate(R.layout.progress_layout, null);
			((TextView) v.findViewById(R.id.loading_text)).setText(dialogMessage);
			pd.setContentView(v);

			pd.setIndeterminate(true);
		}
		if (isPreExecute && preNetwork != null) preNetwork.preNetwork(id);

		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params)
	{

		String responseString = null;
		if (isDoinBackground && doinBackgroung != null)
		{

			return doinBackgroung.doInBackground(id, params);
		}
		else
		{
			String url = params[0];
			responseString = ConstantUtil.http_connection(url);
			System.out.println("response::" + responseString);
			//if (responseString != null && !responseString.equals("")) { return new Gson().fromJson(responseString, BeanResponse.class); }
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String myresult)
	{
		if (isProgressDialog && pd.isShowing()) pd.dismiss();
		if (v != null) ((CharTextView) v.findViewById(R.id.loading_text)).setAnimate(false);
		if (isPostExecute && result != null) result.resultfromNetwork(myresult, id, arg1, arg2);
		super.onPostExecute(myresult);
	}
}
