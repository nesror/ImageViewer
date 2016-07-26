package cn.yzapp.imageviewerlib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import me.relex.circleindicator.CircleIndicator;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 */
public class ImageViewerActivity extends Activity {

    private ShowImage mShowImage;
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        setContentView(R.layout.activity_image_viewer);
        final HackyViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        CircleIndicator mIndicator = (CircleIndicator) findViewById(R.id.indicator);

        mShowImage = getIntent().getParcelableExtra(ImageViewer.INTENT_IMAGE);

        int indicatorBackgroundId = getIntent().getIntExtra(ImageViewer.CHOOSE_RES_IS, 0);
        int indicatorUnselectedBackgroundId = getIntent().getIntExtra(ImageViewer.UNCHOOSE_RES_IS, 0);
        mIndicator.configureIndicator(-1, -1, -1, 0, 0, indicatorBackgroundId, indicatorUnselectedBackgroundId);

        mViewPager.setAdapter(new SamplePagerAdapter());
        mIndicator.setViewPager(mViewPager);

        mViewPager.setCurrentItem(mShowImage.getIndex());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sendBroadcast(position);
                ((SmoothImageView) mViewPager.getChildAt(position)).setScale(1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void sendBroadcast(int position) {
        Intent intent = new Intent();
        intent.setAction(ImageViewer.BROADCAST_ACTION);
        intent.putExtra("position", position);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        intent.setAction(ImageViewer.BROADCAST_ACTION);
        intent.putExtra("onDestroy", true);
        mLocalBroadcastManager.sendBroadcast(intent);

        super.onDestroy();
    }

    private void setPhotoView(final SmoothImageView photoView, Object img) {
        if (img instanceof String) {
            ImageViewerConfig.getImageLoader().getImage(this, photoView, (String) img);
        }
        if (img instanceof Integer) {
            ImageViewerConfig.getImageLoader().getImage(this, photoView, (int) img);
        }
        if (img instanceof File) {
            ImageViewerConfig.getImageLoader().getImage(this, photoView, (File) img);
        }
        if (img instanceof Bitmap) {
            ImageViewerConfig.getImageLoader().getImage(this, photoView, (Bitmap) img);
        }

        photoView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v2) {
                photoView.transformOut();
            }
        });
    }

    class SamplePagerAdapter extends PagerAdapter {

        private boolean show;

        @Override
        public int getCount() {
            return mShowImage.getImg().size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {


            SmoothImageView photoView = new SmoothImageView(container.getContext());
            setImgSite(position, photoView);

            if (position == mShowImage.getIndex() && !show) {
                photoView.transformIn();
                show = true;
            }
            setPhotoView(photoView, mShowImage.getImg().get(position));

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**
     * 设置图片起始位置
     */
    private void setImgSite(int position, SmoothImageView photoView) {
        int[] size = null;
        if (mShowImage.getSizes() != null && mShowImage.getSizes().size() > position) {
            size = mShowImage.getSizes().get(position);
        }

        if (size == null || size.length < 4) {
            photoView.setOriginalInfo(
                    0,
                    0,
                    Utils.getScreenWidth(ImageViewerActivity.this) / 2,
                    Utils.getScreenHeight(ImageViewerActivity.this) / 2);
        } else {
            photoView.setOriginalInfo(
                    size[0],
                    size[1],
                    size[2],
                    size[3]);
        }
    }
}
