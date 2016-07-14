package cn.yzapp.imageviewerlib;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 */
public class Utils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        if (context == null) {
            return 0;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        if (scale < 2.0) {
            scale += 1.5;
        }

        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * getStatusBarHeight
     */
    public static int getStatusBarHeight(Context ct) {
        int result = 0;
        int resourceId = ct.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ct.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

}