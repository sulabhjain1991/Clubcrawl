package com.linchpin.clubcrawl.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;

public class CustomAdapter extends BaseAdapter
{
	private TextView	textname	= null;
	private int			selectedPos	= -1;
	private Context		context;
	String[]			alphabet_list;
	RelativeLayout		layout1;

	public CustomAdapter(Context context, String[] alphabets)
	{
		this.alphabet_list = alphabets;
		this.context = context;
	}

	public void setSelectedPosition(int pos)
	{
		selectedPos = pos;
		// inform the view of this change
		//			notifyDataSetChanged();
	}

	public int getSelectedPosition()
	{
		return selectedPos;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{

		// View row= listAdd.getChildAt(position);
		if (convertView == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.main_list_alphabet_row, parent, false);

		}
		System.out.println("position=" + position);
		BaseClass bc = new BaseClass();
		bc.setFont(convertView.findViewById(R.id.rlHomeScreenList), context);
		textname = (TextView) convertView.findViewById(R.id.name);
		//	textname.setTextColor(getResources().getColor(R.color.black));
		textname.setText(alphabet_list[position]);

		return (convertView);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return alphabet_list.length;
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

}
