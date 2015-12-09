package com.linchpin.clubcrawl.helper;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;

public class TabViewHelper {

	/**
	 * 
	 */
	public TabViewHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param activity activity
	 */
	public static void smoothLeftRightNavigation(
			final SlidingFragmentActivity activity) {
		// check if the content frame contains the menu frame
		final float value=0.25f;
		if (activity.findViewById(R.id.menu_frame) == null) {
			activity.setBehindContentView(R.layout.menu_frame);
			activity.getSlidingMenu().setSlidingEnabled(true);
			activity.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_FULLSCREEN);
			activity.getSlidingMenu().setMode(SlidingMenu.LEFT);
			// show home as up so we can toggle
			// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			final View v = new View(activity);
			activity.setBehindContentView(v);
			activity.getSlidingMenu().setSlidingEnabled(false);
			activity.getSlidingMenu().setMode(SlidingMenu.LEFT);
			activity.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Behind View Fragment
		MenuFragment fr = new MenuFragment();
		
		activity.getSupportFragmentManager().beginTransaction()
		.replace(R.id.menu_frame, fr,"MenuFragment").commit();



		// customize the SlidingMenu
		final SlidingMenu sm = activity.getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(value);
		sm.setFadeDegree(value);
	}
	/**
	 * @param ctx
	 * tcx
	 */
	public static void newLeftNavigationAction(final Activity ctx) {
		if (MenuFragment.activity != null) {

			final Intent ia = new Intent(ctx, MenuFragment.activity);
			((SlidingFragmentActivity) ctx).getSlidingMenu()
			.showContent();
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {

					ctx.startActivity(ia);
					ctx.overridePendingTransition(0, R.anim.slide_out_left);
					ctx.finish();
				}
			},350);
			MenuFragment.activity = null;
			// if (!(MenuFragment.currentsel.equals(“Inbox”)))

		}
	}

	public static void newLeftNavigationActionFragment(final SlidingFragmentActivity ctx,final Fragment fr,final String str) {
		if (MenuFragment.activity != null) {

			//final Intent ia = new Intent(ctx, MenuFragment.activity);
			((SlidingFragmentActivity) ctx).getSlidingMenu()
			.showContent();
			if(ctx instanceof MainScreen)
			((MainScreen) ctx).resetBottonBar();
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					FragmentManager fm = ctx.getSupportFragmentManager();
					fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					FragmentTransaction fragmentTransaction = fm.beginTransaction();

					fragmentTransaction.add(R.id.frame, fr,str);
					fragmentTransaction.addToBackStack(str);
					fragmentTransaction.commit();
					//         ctx.startActivity(ia);
					//         ctx.overridePendingTransition(0, R.anim.slide_out_left);
					//         ctx.finish();
				}
			},350);
			MenuFragment.activity = null;
			// if (!(MenuFragment.currentsel.equals(“Inbox”)))

		}
	}

}