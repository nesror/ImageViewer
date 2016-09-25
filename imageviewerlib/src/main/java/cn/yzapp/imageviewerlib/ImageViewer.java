package cn.yzapp.imageviewerlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nestor
 *         email nestor@yzapp.cn
 */
public class ImageViewer {
    public static final String BROADCAST_ACTION = "com.github.nesror:ImageViewer";

    public static final String INTENT_IMAGE = "INTENT_IMAGE";
    public static final String CHOOSE_RES_IS = "CHOOSE_RES_IS";
    public static final String UNCHOOSE_RES_IS = "UNCHOOSE_RES_IS";

    private OnChangeItemListener mChangeItemListener;
    private ImageViewerBroadcastReceiver mBroadcastReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    private int mChooseResIs;
    private int mUnChooseResIs;

    public ImageViewer() {
        initRes();
    }

    private void initRes() {
        mChooseResIs = ImageViewerConfig.getChooseResIs();
        mUnChooseResIs = ImageViewerConfig.getUnChooseResIs();
    }

    /**
     * 清除当前ImageViewer选中的图片的监听
     */
    public void cleanOnChangeItemListener() {
        mChangeItemListener = null;
    }

    /**
     * 设置当前ImageViewer选中时的指示器颜色
     */
    public void setChooseResIs(@DrawableRes int chooseResIs) {
        mChooseResIs = chooseResIs;
    }

    /**
     * 设置当前ImageViewer未选中时的指示器颜色
     */
    public void setUnChooseResIs(@DrawableRes int unChooseResIs) {
        mUnChooseResIs = unChooseResIs;
    }

    /**
     * 关联 viewPager
     */
    public void setViewPager(final ViewPager viewPager) {
        mChangeItemListener = new OnChangeItemListener() {
            @Override
            public void onChangeItem(int currentItem) {
                viewPager.setCurrentItem(currentItem);
            }

            @Override
            public void onDestroy() {
                if (mLocalBroadcastManager != null)
                    mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
            }
        };
    }

    /**
     * 打开图片浏览单张
     *
     * @param context   Context
     * @param imageView ImageView
     * @param object    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     */
    public void open(Context context, ImageView imageView, Object object) {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(object);

        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(imageView);

        open(context, imageViews, objects, 0);
    }

    /**
     * 打开图片浏览多张
     *
     * @param context    Context
     * @param imageViews ImageView
     * @param objects    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * @param clickItem  点击的图片
     */
    public void open(Context context, List<ImageView> imageViews, ArrayList<Object> objects, int clickItem) {
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
    public void openWithChoose(Context context, List<ImageView> imageViews, ArrayList<Object> objects, int clickItem) {
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
    private void openImageViewer(Context context, List<ImageView> imageViews, ArrayList<Object> objects, int clickItem, boolean sizeFirst) {
        final ImageInfo showImage = new ImageInfo(new ArrayList<int[]>(), 0, new ArrayList<>());
        showImage.setImg(objects);

        List<int[]> sizes = new ArrayList<>();
        for (ImageView imageView : imageViews) {
            int[] location = new int[2];
            int[] size = new int[4];
            if (sizeFirst) {
                imageViews.get(clickItem).getLocationOnScreen(location);
                Integer width = imageViews.get(clickItem).getWidth();
                Integer height = imageViews.get(clickItem).getHeight();

                size[0] = width;
                size[1] = height;
                size[2] = location[0];
                size[3] = location[1];
            } else {
                imageView.getLocationOnScreen(location);
                Integer width = imageView.getWidth();
                Integer height = imageView.getHeight();

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

    private void openImageViewer(Context context, ImageInfo showImage) {
        if (ImageViewerConfig.getImageLoader() == null) {
            return;
        }

        registerReceiver(context);

        startActivity(context, showImage);
    }

    private void startActivity(Context context, ImageInfo showImage) {
        Bundle extras = new Bundle();
        extras.putParcelable(INTENT_IMAGE, showImage);
        extras.putInt(CHOOSE_RES_IS, mChooseResIs);
        extras.putInt(UNCHOOSE_RES_IS, mUnChooseResIs);
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    private void registerReceiver(Context context) {
        if (mChangeItemListener == null) return;

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);

        mBroadcastReceiver = new ImageViewerBroadcastReceiver(mChangeItemListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);


    }

    public interface OnChangeItemListener {
        void onChangeItem(int currentItem);

        void onDestroy();
    }
}
