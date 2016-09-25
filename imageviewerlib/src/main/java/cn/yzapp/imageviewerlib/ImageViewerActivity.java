package cn.yzapp.imageviewerlib;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.io.File;

import me.relex.circleindicator.CircleIndicator;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author nestor
 *         email nestor@yzapp.cn
 */
public class ImageViewerActivity extends Activity {

    private ImageInfo mShowImage;
    private LocalBroadcastManager mLocalBroadcastManager;
    private SamplePagerAdapter mSamplePagerAdapter;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        setContentView(R.layout.activity_image_viewer);
        root = (RelativeLayout) findViewById(R.id.root);

        mShowImage = getIntent().getParcelableExtra(ImageViewer.INTENT_IMAGE);

        setViewPage();

        setOpenAnimator();

    }

    private void setViewPage() {
        final HackyViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

        mSamplePagerAdapter = new SamplePagerAdapter();
        mViewPager.setAdapter(mSamplePagerAdapter);

        setCircleIndicator(mViewPager);

        mViewPager.setCurrentItem(mShowImage.getIndex());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sendBroadcast(position);
                SmoothImageView smoothImageView = mSamplePagerAdapter.getPhotoView(position);
                if (smoothImageView != null) {
                    smoothImageView.setScale(1.0f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setCircleIndicator(HackyViewPager mViewPager) {
        CircleIndicator mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        int indicatorBackgroundId = getIntent().getIntExtra(ImageViewer.CHOOSE_RES_IS, 0);
        int indicatorUnselectedBackgroundId = getIntent().getIntExtra(ImageViewer.UNCHOOSE_RES_IS, 0);
        mIndicator.configureIndicator(-1, -1, -1, 0, 0, indicatorBackgroundId, indicatorUnselectedBackgroundId);

        if(mShowImage.getImg().size() > 1){
            mIndicator.setVisibility(View.VISIBLE);
            mIndicator.setViewPager(mViewPager);
        }
    }

    private void setOpenAnimator() {
        // 进度动画
        ValueAnimator animator = ValueAnimator.ofInt(0, 0xFF);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                root.setBackgroundColor(progress * 0x1000000);
            }
        });
        // 设置插值器为均速滚动
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(380);
        animator.start();
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
        intent.putExtra("position", -1);
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
                setCloseAnimator();

            }

            @Override
            public void onOutsidePhotoTap() {
                photoView.transformOut();
                setCloseAnimator();
            }
        });
    }

    private void setCloseAnimator() {
        // 进度动画
        ValueAnimator animator = ValueAnimator.ofInt(0xFF, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                root.setBackgroundColor(progress * 0x1000000);
            }
        });
        // 设置插值器为均速滚动
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(380);
        animator.start();
    }

    class SamplePagerAdapter extends PagerAdapter {
        private SparseArray<SmoothImageView> photoViews;
        private boolean show;

        public SamplePagerAdapter() {
            photoViews = new SparseArray<>(mShowImage.getImg().size());
        }

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

            photoViews.put(position, photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            photoViews.remove(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public SmoothImageView getPhotoView(int position) {
            return photoViews.get(position);
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
