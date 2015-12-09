package com.linchpin.clubcrawl.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;
import com.linchpin.clubcrawl.R;

public class CharTextView extends TextView
{

	private String			mText[]		=
		{ " .  ", " .. ", " ..." };
	private int				mIndex;
	private long			mDelay		= 300;						//Default 500ms delay
	boolean					isRotate	= true;
	private CharSequence	mFText		= "";
	private boolean			isAnimate	= true;

	public boolean isAnimate()
	{
		return isAnimate;
	}

	public void setAnimate(boolean isAnimate)
	{
		this.isAnimate = isAnimate;
		if (isAnimate) animateText(" ");
	}

	public CharTextView(Context context)
	{
		super(context);
		if (isAnimate) animateText(" ");
	}

	private void init(Context context, AttributeSet attrs)
	{
		int[] textSizeAttr = new int[]
				{ android.R.attr.text };

		TypedArray a = context.obtainStyledAttributes(attrs, textSizeAttr);
		mFText = a.getText(0) + "    ";
		setText(mFText);
		a = context.obtainStyledAttributes(attrs, R.styleable.CharText);
		isAnimate = a.getBoolean(R.styleable.CharText_isAnimate, true);
		a.recycle();
	}
	@Override
	protected void onDetachedFromWindow()
	{
		isAnimate=false;
		super.onDetachedFromWindow();
	}
	@Override
	public void setText(CharSequence text, BufferType type)
	{
		// TODO Auto-generated method stub
		super.setText(text, type);
		mFText = text + "    ";
	}

	public CharTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
		if (isAnimate) animateText("   ");
	}

	private Handler		mHandler		= new Handler();
	private Runnable	characterAdder	= new Runnable()
	{
		@Override
		public void run()
		{

			if (isAnimate)
			{	
				if (mIndex < mText.length)
				{
					String temp=mFText.toString().trim();

					setText(mFText.toString().trim() + mText[mIndex++]);
					mFText=temp;
					mHandler.postDelayed(characterAdder, mDelay);
				}
				else if (isRotate)
				{
					mIndex = 0;
					mHandler.postDelayed(characterAdder, mDelay);
				}
			}
		}
	};

	public void animateText(CharSequence text)
	{
		mIndex = 0;
		mHandler.removeCallbacks(characterAdder);
		mHandler.postDelayed(characterAdder, mDelay);
	}

	public void setCharacterDelay(long millis)
	{
		mDelay = millis;
	}
}