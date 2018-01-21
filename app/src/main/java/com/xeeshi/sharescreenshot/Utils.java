package com.xeeshi.sharescreenshot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ZEESHAN on 07/01/2018.
 */

public class Utils {

    /**
     * Return the int array of screen size of width and height in pixels
     *
     * @param activity
     * activity context
     *
     * @return int[] at 0 index It will be width and at 1 index it will be height
     */
    static int[] getScreenSizeInPX(Activity activity) {

        int[] length = {0, 0};
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                length[0] = metrics.widthPixels;
                length[1] = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    length[0] = (Integer) mGetRawW.invoke(display);
                    length[1] = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }

        return length;
    }

    /**
     * Return the int array of screen size of width and height in dp
     *
     * @param context
     * context to get get resources
     *
     * @return int[] at 0 index It will be width and at 1 index it will be height
     */
    static int[] getScreenSizeInDP(Context context) {
        int[] length = {0, 0};
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            length[0] = (int) (displayMetrics.widthPixels / displayMetrics.density);
            length[1] = (int) (displayMetrics.heightPixels / displayMetrics.density);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return length;
    }


    /**
     * It calculates screen size in inches
     *
     * @param activity
     * activity context
     * @return screen size in inches
     */
    static double getScreenSizeInches(Activity activity){
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try{
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {}
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {}
        }

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }

}
