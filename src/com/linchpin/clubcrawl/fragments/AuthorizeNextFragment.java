package com.linchpin.clubcrawl.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class AuthorizeNextFragment  extends ParentFragment implements Result{

	
	private View		view;
	
	
	private String bcode;
	private final static int AUTHORIZE=0x002;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	view=inflater.inflate(R.layout.authorization_next, container, false);
	 setTitleBar();
	 bcode = getArguments().getString("barCode");
	TextView tvVerify=(TextView) view.findViewById(R.id.tvVerify);
	final EditText etCode=(EditText) view.findViewById(R.id.etAuthorizeCode);
	
	 
	 tvVerify.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) 
		{
			String strUniqueId=etCode.getText().toString();
			Long Id=Long.parseLong(strUniqueId);
			NetworkTask networkTask=new NetworkTask(getActivity(),AUTHORIZE);
			networkTask.setProgressDialog(true);
			networkTask.exposePostExecute(AuthorizeNextFragment.this);
			networkTask.execute(ConstantUtil.baseUrl+"club/doauthorisecode?code="+bcode+"&uniquecode="+Id);		
			
		}
	});
	 
	 
	 
	 
	return view;
}

private void setTitleBar()
{

	TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
	tvTitle.setText(getString(R.string.authorization));
	tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
			0, //top
			0, //right
			0//bottom
			);
	getActivity().findViewById(R.id.ivComposePost).setVisibility(View.GONE);
	ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
	ivMenu.setImageResource(R.drawable.pre_btn);
	
	getActivity().findViewById(R.id.ivSearch).setVisibility(View.GONE);
	TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
	tvEdit.setText(getString(R.string.strEdit));
	tvEdit.setVisibility(View.GONE);
	ivMenu.setOnClickListener(new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			getActivity().onBackPressed();
		}
	});

}

@Override
public void resultfromNetwork(String object, int id, int arg1, String arg2)
{
	switch (id)
	{
	case AUTHORIZE:
		try
		{
			JSONObject jsonObject=new JSONObject(object);
			if(jsonObject.getBoolean("auth"))	
			{
				Toast.makeText(getActivity(), "Authorisation Success!", Toast.LENGTH_LONG).show();
				AuthorizeFragment.isSucceed = true;
				getActivity().onBackPressed();

			}
			else
				Toast.makeText(getActivity(), "Authorisation failed!", Toast.LENGTH_LONG).show();
		}
		catch (JSONException e)
		{
			Toast.makeText(getActivity(), "Authorisation failed!", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		break;
	}
	
}


}
