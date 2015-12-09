package com.linchpin.clubcrawl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class PhotoCapture
{

	private  int RESULT_LOAD_IMAGE = 1, REQUEST_TAKE_PHOTO = 2;
	private TextView gallery, camera;
	private Dialog dialog;
	private Context con;
	private Fragment fr;
	public PhotoCapture(Fragment fr) 
	{
		this.fr = fr;
		this.con = fr.getActivity();
	}
	
	public PhotoCapture(Activity act)
	{
		this.con = act;
	}
	
	public void galleryCameraDialog() {
		dialog = new Dialog(con);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		// wlp.y = -100;
		wlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		wlp.width = LayoutParams.WRAP_CONTENT;
		// wlp.verticalMargin = 1;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		// window.setAttributes(wlp);
		dialog.getWindow().setLayout(80, 50);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.gallery_camera_option_dialog);
		dialog.show();

		onOptionClick();
	}

	private void onOptionClick() {
		gallery = (TextView) dialog.findViewById(R.id.gallery);
		camera = (TextView) dialog.findViewById(R.id.camera);

		
		gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				if(fr!=null)
				fr.startActivityForResult(i, RESULT_LOAD_IMAGE);
				else
					((Activity) con).startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent i = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				if(fr!=null)
					fr.startActivityForResult(i, REQUEST_TAKE_PHOTO);
					else
						((Activity) con).startActivityForResult(i, REQUEST_TAKE_PHOTO);
			}
		});
	}

}
