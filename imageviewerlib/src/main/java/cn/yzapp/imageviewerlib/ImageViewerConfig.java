package cn.yzapp.imageviewerlib;

import android.support.annotation.DrawableRes;

/**
 * @author nestor
 * email nestor@yzapp.cn
 * ImageViewer的全局设置
 */
public class ImageViewerConfig {
    private static IImageLoader mImageLoader;

    private static int mChooseResIs;
    private static int mUnChooseResIs;

    /**
     * 设置全局图片加载器，必须实现
     *
     * @param imageLoader IImageLoader
     */
    public static void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    /**
     * 设置全局选中时的指示器颜色
     */
    public static void setChooseResIs(@DrawableRes int chooseResIs) {
        mChooseResIs = chooseResIs;
    }

    /**
     * 设置全局未选中时的指示器颜色
     */
    public static void setUnChooseResIs(@DrawableRes int unChooseResIs) {
        mUnChooseResIs = unChooseResIs;
    }

    public static IImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static int getChooseResIs() {
        return mChooseResIs;
    }


    public static int getUnChooseResIs() {
        return mUnChooseResIs;
    }

}
