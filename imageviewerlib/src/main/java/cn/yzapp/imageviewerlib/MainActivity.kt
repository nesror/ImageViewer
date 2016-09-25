package cn.yzapp.imageviewerlib

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_image_viewer.*
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.File

class MainActivity : AppCompatActivity() {

    var mImageInfo: ImageInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        mImageInfo = intent.getParcelableExtra<ImageInfo>(ImageViewer.INTENT_IMAGE)

        setViewPage()

        setOpenAnimator()
    }

    private fun setViewPage() {

    }

    private fun setOpenAnimator() {
    }


    class SamplePagerAdapter : PagerAdapter {

        var photoViews: SparseArray<SmoothImageView>? = null
        var mImageInfo: ImageInfo? = null
        var show = false

        constructor(imageInfo: ImageInfo) {
            mImageInfo = imageInfo
            photoViews = SparseArray<SmoothImageView>(imageInfo.img.size)
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


        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            super.destroyItem(container, position, `object`)
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

        private fun setPhotoView(photoView: SmoothImageView, img: Any?) {
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


}
