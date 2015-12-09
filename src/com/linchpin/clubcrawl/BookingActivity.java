package com.linchpin.clubcrawl;

import java.text.DecimalFormat;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.linchpin.clubcrawl.beans.BeanUser;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class BookingActivity extends Activity implements OnClickListener, Result, DatePickerDialog.OnDateSetListener
{

	private EditText	name, age, phoneNumber, email, guest, table, date, options;
	private TextView	done, heading;
	String				vName, vID;
	private final int	BOOK_NOW	= 0x001,BOOK=2;
	private String		strDate, strTime;
	private Calendar	c			= Calendar.getInstance();
	private int			startYear	= c.get(Calendar.YEAR);
	private int			startMonth	= c.get(Calendar.MONTH);
	private int			startDay	= c.get(Calendar.DAY_OF_MONTH);
	private int			startHour	= c.get(Calendar.HOUR_OF_DAY);
	private int			startMin	= c.get(Calendar.MINUTE);
	EditText		lldate;

	private void findViews()
	{
		name = (EditText) findViewById(R.id.etEditFirstName);
		age = (EditText) findViewById(R.id.etAge);
		phoneNumber = (EditText) findViewById(R.id.etMobileNumber);
		email = (EditText) findViewById(R.id.etEmailAddress);
		guest = (EditText) findViewById(R.id.etAmountOfGuests);
		table = (EditText) findViewById(R.id.etAmountOfTables);
		lldate = (EditText) findViewById(R.id.etDateTime);
		date = (EditText) findViewById(R.id.etDateTime);
		options = (EditText) findViewById(R.id.optional);
		done = (TextView) findViewById(R.id.tvDone);
		heading = (TextView) findViewById(R.id.heading);
		done.setOnClickListener(this);
		lldate.setOnClickListener(this);
	}

	protected String validateData()
	{
		String alertString = "";

		if (name.getText().toString().trim().equals("")) alertString = "Please enter your name";
		else if (age.getText().toString().trim().equals("")) alertString = "Please enter your age";
		else if (age.getText().toString().trim().equals("0")) alertString = "Age should be greater then 0";
		else if (phoneNumber.getText().toString().trim().equals("")) alertString = "Please enter your phone number.";
		else if (phoneNumber.getText().toString().length()<6) alertString = "Please enter valid 6 digit phone number";
		else if (email.getText().toString().trim().equals("")) alertString = "Please enter your email.";
		else if (guest.getText().toString().trim().equals("")) alertString = "Please enter amount of guest.";
		else if (table.getText().toString().trim().equals("")) alertString = "Please enter amount of table.";
		else if (date.getText().toString().trim().equals("")) alertString = "Please enter date.";
		return alertString;

	}

	private void fillData(BeanUser usr)
	{
		name.setText(usr.getUsername());
		age.setText(usr.getAge());
		phoneNumber.setText(usr.getPhone());
		email.setText(usr.getEmail());
		heading.setText("Your " + vName + " booking");

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking);
		vName = getIntent().getStringExtra("v_name");
		vID = getIntent().getStringExtra("v_id");

		if (APP.GLOBAL().getPreferences().getBoolean(APP.PREF.SNEAK_PREV.key, false))
		{
			final Dialog dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_dialog_for_booking_screen);
			dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			dialog.show();

			dialog.findViewById(R.id.btnYes).setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					Intent intent = new Intent(BookingActivity.this, SignUp.class);
					startActivity(intent);
					dialog.dismiss();
				}
			});
			dialog.findViewById(R.id.btnNo).setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();

				}
			});
		}
		else
		{
			findViews();
			fillData(MenuFragment.beanUserInfo.getUser());

		}

	}

	private String getUrl()
	{
		//api/bookGuestList?venueName=<venue_name>
		//&venueDate=<venue_date>
		//&venueAmountOfGuest=<total_guest>
		//&venueTime=<time>
		//&venueAmountOfTables=<total_table>
		//&myName=<user_name>
		//&myTelephone=<phone_no>
		//&myAge=<age>
		//&myEmail=<user_email>
		//&Message=<optional_info>
		StringBuilder sb = new StringBuilder();
		sb.append(ConstantUtil.baseUrl);
		sb.append("api/bookGuestList?venueName=").append(vName);
		sb.append("&venueDate=").append(strDate);
		sb.append("&venueAmountOfGuest=").append(guest.getText().toString().trim());
		sb.append("&venueTime=").append(strTime);
		sb.append("&venueAmountOfTables=").append(table.getText().toString().trim());
		sb.append("&myName=").append(name.getText().toString().trim());
		sb.append("&myTelephone=").append(phoneNumber.getText().toString().trim());
		sb.append("&myAge=").append(age.getText().toString().trim());
		sb.append("&myEmail=").append(email.getText().toString().trim());
		sb.append("&Message=").append(options.getText().toString().trim());
		return sb.toString();
	}

	private void showAlertForIncompleteField(String alertString)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(BookingActivity.this).create();
		alertDialog.setMessage(alertString);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.show();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tvDone:

			if (ConstantUtil.isNetworkAvailable(BookingActivity.this))
			{
				String alertString = validateData();
				if (alertString.equals(""))
				{


					NetworkTask networkTask = new NetworkTask(BookingActivity.this, BOOK_NOW);
					networkTask.setProgressDialog(true, "Requesting");

					networkTask.exposePostExecute(BookingActivity.this);
					networkTask.execute(getUrl());
				}
				else
				{
					showAlertForIncompleteField(alertString);
				}
			}
			else Toast.makeText(BookingActivity.this, getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
			break;
		case R.id.etDateTime:
			DatePickerDialog dialog = new DatePickerDialog(BookingActivity.this, BookingActivity.this, startYear, startMonth, startDay);
			dialog.show();
			break;
		default:
			break;
		}

	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2)
	{
		switch (id)
		{
		case BOOK_NOW:
			try
			{
				JSONObject res = new JSONObject(object);
				if(res.has("result"))
				{
				if(res.getString("result").equals("1"))
					Toast.makeText(BookingActivity.this, "Successfully Booked", Toast.LENGTH_LONG).show();
				NetworkTask networkTask = new NetworkTask(BookingActivity.this, BOOK);
				networkTask.setProgressDialog(false, "Requesting");

				networkTask.exposePostExecute(BookingActivity.this);


				/*	api/impression?venue_id=<venue_id>&type=<type>
					(i)  venue_id : Id of venue
					(ii) type: V for venue and G for Guest List
				 */
				networkTask.execute(ConstantUtil.baseUrl+"api/impression?venue_id="+vID+"&type=G");
				}
				else
				{
					if(res.has("status"))
					{
						AlertDialog alertDialog = new AlertDialog.Builder(BookingActivity.this).create();
						alertDialog.setMessage(res.getString("status"));
						alertDialog.setButton("OK", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int which)
							{

								finish();	

							}
						});
						alertDialog.show();
					}
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case BOOK:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onDateSet(final DatePicker view, int year, int monthOfYear, int dayOfMonth)
	{

		if(view.isShown()){
			final DecimalFormat mFormat = new DecimalFormat("00");
			mFormat.format(Double.valueOf(year));
			startYear = year;
			startMonth = monthOfYear + 1;
			startDay = dayOfMonth;
			//updateStartDateDisplay();

			TimePickerDialog tpd = new TimePickerDialog(BookingActivity.this, new TimePickerDialog.OnTimeSetListener()
			{
				@Override
				public void onTimeSet(TimePicker view, int hour, int min)
				{
					startHour = hour;
					startMin = min;

					strDate = startYear + "-" + mFormat.format(Double.valueOf(startMonth)) + "-" + mFormat.format(Double.valueOf(startDay));

					strTime = mFormat.format(Double.valueOf(startHour)) + ":" + mFormat.format(Double.valueOf(startMin));
					date.setText(strDate + " " + strTime);
				}
			}, startHour, startMin, false);
			tpd.show();}
	}

}
