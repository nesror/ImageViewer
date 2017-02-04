package cn.yzapp.imageviewerlib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.widget.ImageView

import java.util.ArrayList

/**
 * @author nestor
 * *         email nestor@yzapp.cn
 */
class ImageViewer {

    companion object {
        val BROADCAST_ACTION = "com.github.nesror:ImageViewer"
        val INTENT_IMAGE = "INTENT_IMAGE"
        val CHOOSE_RES_IS = "CHOOSE_RES_IS"
        val UNCHOOSE_RES_IS = "UNCHOOSE_RES_IS"
    }

    private var mChangeItemListener: OnChangeItemListener? = null
    private var mBroadcastReceiver: ImageViewerBroadcastReceiver? = null
    private var mLocalBroadcastManager: LocalBroadcastManager? = null

    private var mChooseResIs: Int = 0
    private var mUnChooseResIs: Int = 0

    init {
        initRes()
    }

    private fun initRes() {
        mChooseResIs = ImageViewerConfig.chooseResIs
        mUnChooseResIs = ImageViewerConfig.unChooseResIs
    }

    /**
     * 清除当前ImageViewer选中的图片的监听
     */
    fun cleanOnChangeItemListener() {
        mChangeItemListener = null
    }

    /**
     * 设置当前ImageViewer选中时的指示器颜色
     */
    fun setChooseResIs(@DrawableRes chooseResIs: Int) {
        mChooseResIs = chooseResIs
    }

    /**
     * 设置当前ImageViewer未选中时的指示器颜色
     */
    fun setUnChooseResIs(@DrawableRes unChooseResIs: Int) {
        mUnChooseResIs = unChooseResIs
    }

    /**
     * 关联 viewPager
     */
    fun setViewPager(viewPager: ViewPager) {
        mChangeItemListener = object : OnChangeItemListener {
            override fun onChangeItem(currentItem: Int) {
                viewPager.currentItem = currentItem
            }

            override fun onDestroy() {
                mLocalBroadcastManager?.unregisterReceiver(mBroadcastReceiver)
            }
        }
    }

    /**
     * 打开图片浏览单张

     * @param context   Context
     * *
     * @param imageView ImageView
     * *
     * @param any    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     */
    fun open(context: Context, imageView: ImageView, any: Any) {
        val anyList = ArrayList<Any>()
        anyList.add(any)

        val imageViews = ArrayList<ImageView>()
        imageViews.add(imageView)

        open(context, imageViews, anyList, 0)
    }

    /**
     * 打开图片浏览多张

     * @param context    Context
     * *
     * @param imageViews ImageView
     * *
     * @param anyList    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * *
     * @param clickItem  点击的图片
     */
    fun open(context: Context, imageViews: List<ImageView>, anyList: ArrayList<Any>, clickItem: Int) {
        openImageViewer(context, imageViews, anyList, clickItem, false)
    }

    /**
     * 打开图片浏览多张,只根据第一张图片来播放动画

     * @param context    Context
     * *
     * @param imageViews ImageView
     * *
     * @param anyList    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * *
     * @param clickItem  点击的图片
     */
    fun openWithChoose(context: Context, imageViews: List<ImageView>, anyList: ArrayList<Any>, clickItem: Int) {
        openImageViewer(context, imageViews, anyList, clickItem, true)
    }

    /**
     * 打开图片浏览多张

     * @param context    Context
     * *
     * @param imageViews ImageView
     * *
     * @param anyList    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * *
     * @param clickItem  点击的图片
     * *
     * @param sizeFirst  只根据第一张图片来播放动画
     */
    private fun openImageViewer(context: Context, imageViews: List<ImageView>, anyList: ArrayList<Any>, clickItem: Int, sizeFirst: Boolean) {
        val showImage = ImageInfo(ArrayList<IntArray>(), 0, ArrayList<Any>())
        showImage.img = anyList

        val sizes = ArrayList<IntArray>()
        for (imageView in imageViews) {
            val location = IntArray(2)
            val size = IntArray(4)
            if (sizeFirst) {
                imageViews[clickItem].getLocationOnScreen(location)
                val width = imageViews[clickItem].width
                val height = imageViews[clickItem].height

                size[0] = width
                size[1] = height
                size[2] = location[0]
                size[3] = location[1]
            } else {
                imageView.getLocationOnScreen(location)
                val width = imageView.width
                val height = imageView.height

                size[0] = width
                size[1] = height
                size[2] = location[0]
                size[3] = location[1]
            }

            sizes.add(size)
        }

        showImage.sizes = sizes
        showImage.index = clickItem

        openImageViewer(context, showImage)
    }

    private fun openImageViewer(context: Context, showImage: ImageInfo) {

        registerReceiver(context)

        startActivity(context, showImage)
    }

    private fun startActivity(context: Context, showImage: ImageInfo) {
        val extras = Bundle()
        extras.putParcelable(INTENT_IMAGE, showImage)
        extras.putInt(CHOOSE_RES_IS, mChooseResIs)
        extras.putInt(UNCHOOSE_RES_IS, mUnChooseResIs)
        val intent = Intent(context, ImageViewerActivity::class.java)
        intent.putExtras(extras)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }

    private fun registerReceiver(context: Context) {
        if (mChangeItemListener == null) return

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context)

        mBroadcastReceiver = ImageViewerBroadcastReceiver(mChangeItemListener!!)
        val intentFilter = IntentFilter()
        intentFilter.addAction(BROADCAST_ACTION)
        mLocalBroadcastManager!!.registerReceiver(mBroadcastReceiver, intentFilter)


    }

    interface OnChangeItemListener {
        fun onChangeItem(currentItem: Int)

        fun onDestroy()
    }

}
