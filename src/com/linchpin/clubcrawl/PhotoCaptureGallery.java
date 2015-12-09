package com.linchpin.clubcrawl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.Utils;

public class PhotoCaptureGallery extends Activity {

	private  int RESULT_LOAD_IMAGE = 1, REQUEST_TAKE_PHOTO = 2;
	private ImageView photo;
	private TextView gallery, camera;
	Dialog dialog;
	public static String picturePath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	public void openGallery(ImageView photo) {
		this.photo = photo;
		galleryCameraDialog();
	}

	

	private void galleryCameraDialog() {
		dialog = new Dialog(this);
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
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_TAKE_PHOTO);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data && data.getData() != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();

			try {
				if (photo != null) {
					Bitmap bitmap=Utils.decodeSampledBitmapFromResource(getResources(),picturePath,200,200);
					//setWidthAndHeightOfImage();
					photo.setImageBitmap(bitmap);
				} else {
					Toast.makeText(this, "Selected image type not supported.", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			//setWidthAndHeightOfImage();
            photo.setImageBitmap(thumbnail);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            picturePath = Environment.getExternalStorageDirectory()+File.separator + "image.jpg";
            File file = new File(picturePath);
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}

	private void setWidthAndHeightOfImage() 
	{
		ConstantUtil.getScreen_Height(PhotoCaptureGallery.this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ConstantUtil.screenWidth*30/100,ConstantUtil.screenWidth*30/100);
		params.gravity = Gravity.CENTER;
		params.setMargins(10, 10, 10, 10);
		photo.setLayoutParams(params);
	}
	
	public String returnProfileImagePath(){
		return picturePath;
	}
	
}
