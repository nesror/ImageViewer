package cn.yzapp.imageviewerlib

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_image_viewer.*

import java.io.File

import uk.co.senab.photoview.PhotoViewAttacher
import kotlin.properties.Delegates

/**
 * @author nestor
 * *         email nestor@yzapp.cn
 */
class ImageViewerActivity : Activity() {

    private var mShowImage: ImageInfo by Delegates.notNull()
    private var mLocalBroadcastManager: LocalBroadcastManager by Delegates.notNull()
    private val mSamplePagerAdapter: SamplePagerAdapter by lazy {
        SamplePagerAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_viewer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        mShowImage = intent.getParcelableExtra(ImageViewer.INTENT_IMAGE)

        setViewPage()
        setOpenAnimator()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            overridePendingTransition(0, 0)
        }
    }

    override fun onDestroy() {
        val intent = Intent()
        intent.action = ImageViewer.BROADCAST_ACTION
        intent.putExtra("position", -1)
        intent.putExtra("onDestroy", true)
        mLocalBroadcastManager.sendBroadcast(intent)

        super.onDestroy()
    }

    private fun setViewPage() {
        view_pager.adapter = mSamplePagerAdapter

        setCircleIndicator(view_pager)

        view_pager.currentItem = mShowImage.index
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                sendBroadcast(position)
                val smoothImageView = mSamplePagerAdapter.getPhotoView(position)
                smoothImageView?.scale = 1.0f
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun setCircleIndicator(mViewPager: HackyViewPager) {
        val indicatorBackgroundId = intent.getIntExtra(ImageViewer.CHOOSE_RES_IS, 0)
        val indicatorUnselectedBackgroundId = intent.getIntExtra(ImageViewer.UNCHOOSE_RES_IS, 0)
        indicator.configureIndicator(-1, -1, -1, 0, 0, indicatorBackgroundId, indicatorUnselectedBackgroundId)

        if (mShowImage.img.size > 1) {
            indicator.visibility = View.VISIBLE
            indicator.setViewPager(mViewPager)
        }
    }

    private fun setOpenAnimator() {
        // 进度动画
        val animator = ValueAnimator.ofInt(0, 0xFF)
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            root.setBackgroundColor(progress * 0x1000000)
        }
        // 设置插值器为均速滚动
        //animator.interpolator = LinearInterpolator()
        animator.duration = 380
        animator.start()
    }

    private fun setCloseAnimator() {
        // 进度动画
        val animator = ValueAnimator.ofInt(0xFF, 0)
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            root.setBackgroundColor(progress * 0x1000000)
        }
        // 设置插值器为均速滚动
        //animator.interpolator = LinearInterpolator()
        animator.duration = 380
        animator.start()
    }

    private fun sendBroadcast(position: Int) {
        val intent = Intent()
        intent.action = ImageViewer.BROADCAST_ACTION
        intent.putExtra("position", position)
        mLocalBroadcastManager.sendBroadcast(intent)
    }

    internal inner class SamplePagerAdapter : PagerAdapter() {
        private val photoViews: SparseArray<SmoothImageView> = SparseArray(mShowImage.img.size)
        private var show: Boolean = false

        override fun getCount(): Int {
            return mShowImage.img.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = SmoothImageView(container.context)
            setImgSite(position, photoView)

            if (position == mShowImage.index && !show) {
                photoView.transformIn()
                show = true
            }
            setPhotoView(photoView, mShowImage.img[position])

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            photoViews.put(position, photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
            photoViews.remove(position)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        fun getPhotoView(position: Int): SmoothImageView? {
            return photoViews.get(position)
        }

        /**
         * 设置图片起始位置
         */
        private fun setImgSite(position: Int, photoView: SmoothImageView) {
            var size: IntArray? = null
            if (mShowImage.sizes.size > position) {
                size = mShowImage.sizes[position]
            }

            if (size == null || size.size < 4) {
                photoView.setOriginalInfo(
                        0,
                        0,
                        Utils.getScreenWidth(this@ImageViewerActivity) / 2,
                        Utils.getScreenHeight(this@ImageViewerActivity) / 2)
            } else {
                photoView.setOriginalInfo(
                        size[0],
                        size[1],
                        size[2],
                        size[3])
            }
        }


        private fun setPhotoView(photoView: SmoothImageView, img: Any) {
            if (img is String) {
                ImageViewerConfig.imageLoader.getImage(this@ImageViewerActivity, photoView, img)
            }
            if (img is Int) {
                ImageViewerConfig.imageLoader.getImage(this@ImageViewerActivity, photoView, img)
            }
            if (img is File) {
                ImageViewerConfig.imageLoader.getImage(this@ImageViewerActivity, photoView, img)
            }
            if (img is Bitmap) {
                ImageViewerConfig.imageLoader.getImage(this@ImageViewerActivity, photoView, img)
            }

            photoView.setOnTransformListener(object : SmoothImageView.TransformListener {
                override fun onTransformComplete(mode: Int) {
                    if (mode == 2) {
                        finish()
                    }
                }
            })

            photoView.setOnPhotoTapListener(object : PhotoViewAttacher.OnPhotoTapListener {
                override fun onPhotoTap(view: View, v: Float, v2: Float) {
                    photoView.transformOut()
                    setCloseAnimator()

                }

                override fun onOutsidePhotoTap() {
                    photoView.transformOut()
                    setCloseAnimator()
                }
            })
        }

    }

}
