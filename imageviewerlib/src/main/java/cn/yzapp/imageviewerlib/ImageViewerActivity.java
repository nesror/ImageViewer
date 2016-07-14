package cn.yzapp.imageviewerlib;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import me.relex.circleindicator.CircleIndicator;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 */
public class ImageViewerActivity extends Activity {

    HackyViewPager mViewPager;
    CircleIndicator mIndicator;
    RelativeLayout mRoot;

    private ShowImage mShowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mRoot = (RelativeLayout) findViewById(R.id.root);

        getIntentData();

        mViewPager.setAdapter(new SamplePagerAdapter());
        mIndicator.setViewPager(mViewPager);

        mViewPager.setCurrentItem(mShowImage.getIndex());

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    private void getIntentData() {
        mShowImage = getIntent().getParcelableExtra(ImageViewer.INTENT_IMAGE);

    }

    private void setPhotoView(final SmoothImageView photoView, String imageUrl) {
        Picasso.with(this).load(imageUrl).into(photoView);

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
            return mShowImage.getImgUrl().size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            int[] size = mShowImage.getSizes().get(position);

            SmoothImageView photoView = new SmoothImageView(container.getContext());
            photoView.setOriginalInfo(size[0], size[1], size[2], size[3]);
            if (position == mShowImage.getIndex() && !show) {
                photoView.transformIn();
                show = true;
            }
            setPhotoView(photoView, mShowImage.getImgUrl().get(position));

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
}
