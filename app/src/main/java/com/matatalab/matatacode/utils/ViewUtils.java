package com.matatalab.matatacode.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.matatalab.matatacode.app.BaseApplication;

import java.lang.reflect.Field;


public class ViewUtils {

    private static float DEVICE_DENSITY = 0;

    public static float getDensity(Context context) {
        if (context == null) {
            context = BaseApplication.self();
        }
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float getSpValue(float value) {
        if (DEVICE_DENSITY == 0) {
            DEVICE_DENSITY = BaseApplication.self().getResources().getDisplayMetrics().densityDpi;
        }
        return value * DEVICE_DENSITY / 160;
    }

    public static int getSpValueInt(float value) {
        return (int) getSpValue(value);
    }

    /**
     * 动态设置字体 考虑用户设置了放大字体的时候
     *
     * @param value
     * @return
     */
    public static float getSpValueForFont(float value) {
        float scale = BaseApplication.self().getResources().getDisplayMetrics().scaledDensity;
        return value * scale + 0.5f;
    }

    /**
     * 动态设置字体 考虑用户设置了放大字体的时候
     *
     * @param value
     * @return
     */
    public static int getSpValueForFontInt(float value) {
        return (int) getSpValueForFont(value);
    }

    public static float getPxValue(float dpValue) {
        if (DEVICE_DENSITY == 0) {
            DEVICE_DENSITY = BaseApplication.self().getResources().getDisplayMetrics().densityDpi;
        }
        return dpValue * 160 / DEVICE_DENSITY;
    }

    public static int getPxValueInt(float dpValue) {
        return (int) getPxValue(dpValue);
    }

    public static boolean isChildOf(View c, View p) {
        if (c == p) {
            return true;
        } else if (p instanceof ViewGroup) {
            int count = ((ViewGroup) p).getChildCount();
            for (int i = 0; i < count; i++) {
                View ci = ((ViewGroup) p).getChildAt(i);
                if (isChildOf(c, ci)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Rect getRectBlock(View child) {
        int[] posContainer = new int[2];
        getChildPos(child, null, posContainer);
        return new Rect(posContainer[0], posContainer[1], posContainer[0] + child.getWidth(), posContainer[1] + child.getHeight());
    }

    /**
     * @param child
     * @param parent       if null, return child position to view tree root.
     * @param posContainer
     */
    public static void getChildPos(View child, View parent, int[] posContainer) {
        if (posContainer == null || posContainer.length < 2) {
            return;
        }
        int x = 0;
        int y = 0;
        View vc = child;
        while (vc.getParent() != null) {
            x += vc.getLeft();
            y += vc.getTop();
            if (vc.getParent() == parent) {
                posContainer[0] = x;
                posContainer[1] = y;
                if (posContainer.length >= 4) {
                    posContainer[2] = vc.getMeasuredWidth();
                    posContainer[3] = vc.getMeasuredHeight();
                }
                break;
            }
            try {
                vc = (View) vc.getParent();
                if (posContainer.length >= 4) {
                    posContainer[2] = vc.getMeasuredWidth();
                    posContainer[3] = vc.getMeasuredHeight();
                }
            } catch (ClassCastException e) {
                break;
            }
        }
        if (parent == null) {
            posContainer[0] = x;
            posContainer[1] = y;
        }
    }

    public static String getActivityName(Context ctx) {
        Context c = ctx;
        if (!(ctx instanceof Activity) && (ctx instanceof ContextWrapper)) {
            c = ((ContextWrapper) ctx).getBaseContext();
        }
        return c.getClass().getName();
    }

    protected int[] getPicBounds(View v) {
        int[] posContainer = new int[4];
        ViewUtils.getChildPos(v, null, posContainer);
        posContainer[0] += v.getPaddingLeft();
        posContainer[1] += v.getPaddingTop();
        posContainer[2] = v.getWidth() - v.getPaddingLeft() - v.getPaddingRight();
        posContainer[3] = v.getHeight() - v.getPaddingTop() - v.getPaddingBottom();
        return posContainer;
    }

    /**
     * 获取状态栏高度，取出的高度是实际像素高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int value = 0, statusBarHeight = 0;
        try {
            Class<?> classR = Class.forName("com.android.internal.R$dimen");
            Object obj = classR.newInstance();
            Field field = classR.getField("status_bar_height");
            value = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = BaseApplication.self().getResources().getDimensionPixelSize(value);
            return statusBarHeight;
        } catch (Exception e1) {
            e1.printStackTrace();
            return 0;
        }
    }

    /*
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        return BaseApplication.self().getResources().getDisplayMetrics().widthPixels;
    }

    /*
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        return BaseApplication.self().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取一个View的截图Bitmap
     */
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    /**
     * 通过反射获取imageview的某个属性值
     * <p>
     * 因为getMaxWidth竟然要API 16，为了兼容性，我们采用反射的方案。
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;

    }

    /**
     * 上下文的获取
     *
     * @return
     */
    public static Context getContext() {
        return BaseApplication.getContext();
    }

    /**
     * 获取资源
     *
     * @return
     */
    public static Resources getResources() {
        return getContext().getResources();
    }


    /**
     * @param dip
     * @return
     */
    public static int dip2px(int dip) {
        // 公式 1: px = dp * (dpi / 160)
        // 公式 2: dp = px / denistity;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        // metrics.densityDpi
        return (int) (dip * density + 0.5f);
    }

    public static int px2dip(int px) {
        // 公式 1: px = dp * (dpi / 160)
        // 公式 2: dp = px / denistity;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        // metrics.densityDpi
        return (int) (px / density + 0.5f);
    }

}
