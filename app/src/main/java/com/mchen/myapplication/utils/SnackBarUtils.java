package com.mchen.myapplication.utils;


import android.support.design.widget.Snackbar;
import android.view.View;

//Toast统一管理类
public class SnackBarUtils {

	private SnackBarUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	private static Snackbar snackbar;

	/**
	 * 短时间显示Toast
	 * 
	 * @param view
	 * @param message
	 */
	public static void showShort(View view, CharSequence message) {
		if (snackbar == null) {
			snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
		} else {
			snackbar.setText(message);
			snackbar.setDuration(Snackbar.LENGTH_SHORT);
		}
		snackbar.show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param view
	 * @param message
	 */
	public static void showLong(View view, CharSequence message) {
		if (snackbar == null) {
			snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
		} else {
			snackbar.setText(message);
			snackbar.setDuration(Snackbar.LENGTH_LONG);
		}
		snackbar.show();
	}

}