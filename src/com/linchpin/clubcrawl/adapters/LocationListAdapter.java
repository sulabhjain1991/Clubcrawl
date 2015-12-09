package com.linchpin.clubcrawl.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanListLocationInfo;
import com.linchpin.clubcrawl.beans.BeanLocationsInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;

public class LocationListAdapter extends BaseAdapter
{
	Context					context;
	BeanListLocationInfo	listLocationInfo;
	int						selected;

	public LocationListAdapter(Context conext, BeanListLocationInfo listLocationInfo)
	{
		String cid = AppPreferences.getInstance(conext).getCityId();
		selected = getCurrentIndex(listLocationInfo.getLocations(), cid);
		this.context = conext;
		this.listLocationInfo = listLocationInfo;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return listLocationInfo.getLocations().size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	private int getCurrentIndex(List<BeanLocationsInfo> beanLocationsInfos, String cid)
	{

		for (BeanLocationsInfo info : beanLocationsInfos)
			if (info.getLocations_id().equals(cid)) return beanLocationsInfos.indexOf(info);
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.adapter_locations_list, null);
		TextView tvLocationsName = (TextView) view.findViewById(R.id.tvlocationsName);

		tvLocationsName.setText("" + listLocationInfo.getLocations().get(position).getLocations_name());
		if(position==selected)
		{
			view.setSelected(true);
			tvLocationsName.setTextColor(context.getResources().getColor(R.color.list_fg_selected));
		}
		return view;

	}

}
