package cn.yzapp.imageviewerlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 */
public class ImageViewer {
    public static final String INTENT_IMAGE = "INTENT_IMAGE";
    public static final String CHOOSE_RES_IS = "CHOOSE_RES_IS";
    public static final String UNCHOOSE_RES_IS = "UNCHOOSE_RES_IS";

    private static IImageLoader mImageLoader;

    private static int mChooseResIs;
    private static int mUnChooseResIs;
    private static OnChangeItemListener mChangeItemListener;

    /**
     * 设置图片加载器，必须实现
     *
     * @param imageLoader IImageLoader
     */
    public static void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    /**
     * 设置选中时的指示器颜色
     */
    public static void setChooseResIs(@DrawableRes int chooseResIs) {
        mChooseResIs = chooseResIs;
    }

    /**
     * 设置未选中时的指示器颜色
     */
    public static void setUnChooseResIs(@DrawableRes int unChooseResIs) {
        mUnChooseResIs = unChooseResIs;
    }

    public static IImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * 设置当前选中的图片的监听
     */
    public static void setOnChangeItemListener(OnChangeItemListener changeItemListener) {
        mChangeItemListener = changeItemListener;
    }

    public static void cleanOnChangeItemListener(){
        mChangeItemListener = null;
    }

    /**
     * 打开图片浏览单张
     *
     * @param context   Context
     * @param imageView ImageView
     * @param object    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     */
    public static void open(Context context, ImageView imageView, Object object) {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(object);

        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(imageView);

        open(context, imageViews, objects, 0);
    }

    /**
     * 打开图片浏览单张
     *
     * @param context   Context
     * @param imageView ImageView
     */
    /*public static void openImageViewer(Context context, ImageView imageView) {
        ArrayList<Object> objects = new ArrayList<>();
        imageView.buildDrawingCache();
        imageView.setDrawingCacheEnabled(true);
        objects.add(Bitmap.createBitmap(imageView.getDrawingCache()));
        imageView.setDrawingCacheEnabled(false);

        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(imageView);

        openImageViewer(context, imageViews, objects, 0);
    }*/

    /**
     * 打开图片浏览多张
     *
     * @param context    Context
     * @param imageViews ImageView
     * @param clickItem  点击的图片
     */
    /*public static void openImageViewer(Context context, List<ImageView> imageViews, int clickItem) {
        ArrayList<Object> objects = new ArrayList<>();
        for (ImageView imageView : imageViews) {
            imageView.setDrawingCacheEnabled(true);
            objects.add(Bitmap.createBitmap(imageView.getDrawingCache()));
            imageView.setDrawingCacheEnabled(false);
        }

        openImageViewer(context, imageViews, objects, clickItem);
    }*/

    /**
     * 打开图片浏览多张
     *
     * @param context    Context
     * @param imageViews ImageView
     * @param objects    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * @param clickItem  点击的图片
     */
    public static void open(Context context, List<ImageView> imageViews, ArrayList<Object> objects, int clickItem) {
        openImageViewer(context, imageViews, objects, clickItem, false);
    }

    /**
     * 打开图片浏览多张,只根据第一张图片来播放动画
     *
     * @param context    Context
     * @param imageViews ImageView
     * @param objects    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * @param clickItem  点击的图片
     */
    public static void openWithChoose(Context context, List<ImageView> imageViews, ArrayList<Object> objects, int clickItem) {
        openImageViewer(context, imageViews, objects, clickItem, true);
    }

    /**
     * 打开图片浏览多张
     *
     * @param context    Context
     * @param imageViews ImageView
     * @param objects    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * @param clickItem  点击的图片
     * @param sizeFirst  只根据第一张图片来播放动画
     */
    private static void openImageViewer(Context context, List<ImageView> imageViews, ArrayList<Object> objects, int clickItem, boolean sizeFirst) {
        final ShowImage showImage = new ShowImage();
        showImage.setImg(objects);

        List<int[]> sizes = new ArrayList<>();
        for (ImageView imageView : imageViews) {
            int[] location = new int[2];
            int[] size = new int[4];
            if (sizeFirst) {
                imageViews.get(clickItem).getLocationOnScreen(location);
                int width = imageView.getWidth();
                int height = imageView.getHeight();

                size[0] = width;
                size[1] = height;
                size[2] = location[0];
                size[3] = location[1];
            } else {
                imageView.getLocationOnScreen(location);
                int width = imageView.getWidth();
                int height = imageView.getHeight();

                size[0] = width;
                size[1] = height;
                size[2] = location[0];
                size[3] = location[1];
            }

            sizes.add(size);
        }

        showImage.setSizes(sizes);
        showImage.setIndex(clickItem);

        openImageViewer(context, showImage);
    }

    private static void openImageViewer(Context context, ShowImage showImage) {
        if (mImageLoader == null) {
            return;
        }

        Bundle extras = new Bundle();
        extras.putParcelable(INTENT_IMAGE, showImage);
        extras.putInt(CHOOSE_RES_IS, mChooseResIs);
        extras.putInt(UNCHOOSE_RES_IS, mUnChooseResIs);
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void changeItem(int currentItem) {
        if (mChangeItemListener != null)
            mChangeItemListener.onChangeItem(currentItem);
    }

    public interface OnChangeItemListener {
        void onChangeItem(int currentItem);
    }
}
