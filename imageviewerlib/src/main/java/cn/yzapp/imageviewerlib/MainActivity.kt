package cn.yzapp.imageviewerlib

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_image_viewer.*
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.File

@Deprecated("")
class MainActivity : AppCompatActivity() {

    private var mImageInfo: ImageInfo? = null
    private var mSamplePagerAdapter: SamplePagerAdapter? = null
    private var mLocalBroadcastManager: LocalBroadcastManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)

        setContentView(R.layout.activity_image_viewer)

        mImageInfo = intent.getParcelableExtra<ImageInfo>(ImageViewer.INTENT_IMAGE)

        setViewPage()

        setOpenAnimator()
    }

    private fun setViewPage() {
        mSamplePagerAdapter = SamplePagerAdapter(mImageInfo)
        view_pager.adapter = mSamplePagerAdapter
        setCircleIndicator(view_pager)

        view_pager.currentItem = mImageInfo?.index ?: 0

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                sendBroadcast(position)
                val smoothImageView = mSamplePagerAdapter?.getPhotoView(position)
                if (smoothImageView != null) {
                    smoothImageView.scale = 1.0f
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun sendBroadcast(position: Int) {
        val intent = Intent()
        intent.action = ImageViewer.BROADCAST_ACTION
        intent.putExtra("position", position)
        mLocalBroadcastManager?.sendBroadcast(intent)
    }

    private fun setCircleIndicator(view_pager: HackyViewPager?) {
        val indicatorBackgroundId = intent.getIntExtra(ImageViewer.CHOOSE_RES_IS, 0)
        val indicatorUnselectedBackgroundId = intent.getIntExtra(ImageViewer.UNCHOOSE_RES_IS, 0)
        indicator.configureIndicator(-1, -1, -1, 0, 0, indicatorBackgroundId, indicatorUnselectedBackgroundId)

        if (mImageInfo?.img?.size ?: 0 > 1) {
            indicator.visibility = View.VISIBLE
            indicator.setViewPager(view_pager)
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
        animator.interpolator = LinearInterpolator()
        animator.duration = 300
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
        animator.interpolator = LinearInterpolator()
        animator.duration = 300
        animator.start()
    }


    inner class SamplePagerAdapter : PagerAdapter {

        var photoViews: SparseArray<SmoothImageView>? = null
        var mImageInfo: ImageInfo? = null
        var show = false

        constructor(imageInfo: ImageInfo?) {
            mImageInfo = imageInfo
            photoViews = SparseArray<SmoothImageView>(imageInfo?.img?.size ?: 0)
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val photoView: SmoothImageView = SmoothImageView(container?.context!!)
            setImgSite(container, photoView, position)

            if (position == mImageInfo?.index && !show) {
                photoView.transformIn()
                show = true
            }

            setPhotoView(photoView, mImageInfo?.img?.get(position))

            container?.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            photoViews?.put(position, photoView)

            return photoViews!!

        }

        fun getPhotoView(position: Int): SmoothImageView? {
            return photoViews?.get(position)
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object` as View)
            photoViews?.remove(position)
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return mImageInfo?.img?.size!!
        }

        /**
         * 设置图片起始位置
         */
        private fun setImgSite(container: ViewGroup?, photoView: SmoothImageView, position: Int) {
            var size: IntArray = IntArray(0)
            if (mImageInfo?.sizes?.size ?: 0 > position) {
                size = mImageInfo?.sizes?.get(position) ?: IntArray(0)
            }
            if (size.size < 4) {
                photoView.setOriginalInfo(
                        0,
                        0,
                        Utils.getScreenWidth(container?.context!!) / 2,
                        Utils.getScreenHeight(container?.context!!) / 2)
            } else {
                photoView.setOriginalInfo(
                        size[0],
                        size[1],
                        size[2],
                        size[3])
            }
        }


    }

    fun setPhotoView(photoView: SmoothImageView, img: Any?) {
        if (img is String) {
            ImageViewerConfig.imageLoader!!.getImage(this@MainActivity, photoView, img)
        }
        if (img is Int) {
            ImageViewerConfig.imageLoader!!.getImage(this@MainActivity, photoView, img)
        }
        if (img is File) {
            ImageViewerConfig.imageLoader!!.getImage(this@MainActivity, photoView, img)
        }
        if (img is Bitmap) {
            ImageViewerConfig.imageLoader!!.getImage(this@MainActivity, photoView, img)
        }

        photoView.setOnTransformListener(object : SmoothImageView.TransformListener {
            override fun onTransformComplete(mode: Int) {
                if (mode == 2) {
                    this@MainActivity.finish()
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
