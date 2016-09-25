package cn.yzapp.imageviewerlib

import android.content.Context
import android.view.Display
import android.view.WindowManager

/**
 * @author nestor
 * * email nestor@yzapp.cn
 */
object Utils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context?, dpValue: Float): Int {
        if (context == null) {
            return 0
        }
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context?, pxValue: Float): Int {
        if (context == null) {
            return 0
        }
        var scale = context.resources.displayMetrics.density
        if (scale < 2.0) {
            scale += 1.5f
        }

        return (pxValue / scale + 0.5f).toInt()
    }


    /**
     * getStatusBarHeight
     */
    fun getStatusBarHeight(ct: Context): Int {
        var result = 0
        val resourceId = ct.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = ct.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 屏幕宽度
     */
    fun getScreenWidth(context: Context): Int {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.width
    }

    /**
     * 屏幕高度
     */
    fun getScreenHeight(context: Context): Int {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.height
    }

}