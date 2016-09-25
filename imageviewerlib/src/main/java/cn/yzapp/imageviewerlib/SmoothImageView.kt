package cn.yzapp.imageviewerlib

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator

import uk.co.senab.photoview.PhotoView

/**
 * 基于 @author Dean Tao 的SmoothImageView修改而来
 *
 *
 * 原注释：
 * 2d平滑变化的显示图片的ImageView
 * 仅限于用于:从一个ScaleType==CENTER_CROP的ImageView，切换到另一个ScaleType=
 * FIT_CENTER的ImageView，或者反之 (当然，得使用同样的图片最好)
 */
class SmoothImageView : PhotoView {
    private var mOriginalWidth: Int = 0
    private var mOriginalHeight: Int = 0
    private var mOriginalLocationX: Int = 0
    private var mOriginalLocationY: Int = 0
    private var mState = STATE_NORMAL
    private var mSmoothMatrix: Matrix? = null
    private var mBitmap: Bitmap? = null
    private var mTransformStart = false
    private var mTransfrom: Transfrom? = null

    constructor(context: Context) : super(context) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initPaint()
    }

    private fun initPaint() {
        mSmoothMatrix = Matrix()
    }

    fun setOriginalInfo(width: Int, height: Int, locationX: Int, locationY: Int) {
        mOriginalWidth = width
        mOriginalHeight = height
        mOriginalLocationX = locationX
        mOriginalLocationY = locationY
        mOriginalLocationY = mOriginalLocationY - Utils.getStatusBarHeight(context)
    }

    /**
     * 用于开始进入的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    fun transformIn() {
        mState = STATE_TRANSFORM_IN
        mTransformStart = true
        invalidate()
    }

    /**
     * 用于开始退出的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    fun transformOut() {
        mState = STATE_TRANSFORM_OUT
        mTransformStart = true
        invalidate()
    }

    private inner class Transfrom {
        internal var startScale: Float = 0.toFloat()// 图片开始的缩放值
        internal var endScale: Float = 0.toFloat()// 图片结束的缩放值
        internal var scale: Float = 0.toFloat()// 属性ValueAnimator计算出来的值
        internal var startRect: LocationSizeF? = null// 开始的区域
        internal var endRect: LocationSizeF? = null// 结束的区域
        internal var rect: LocationSizeF? = null// 属性ValueAnimator计算出来的值

        internal fun initStartIn() {
            scale = startScale
            try {
                rect = startRect!!.clone() as LocationSizeF
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }

        }

        internal fun initStartOut() {
            scale = endScale
            try {
                rect = endRect!!.clone() as LocationSizeF
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }

        }

    }

    /**
     * 初始化进入的变量信息
     */
    private fun initTransform() {
        if (drawable == null) {
            return
        }

        //防止转换失败
        if (drawable is ColorDrawable) return

        if (mBitmap == null || mBitmap!!.isRecycled) {
            mBitmap = (drawable as BitmapDrawable).bitmap
        }
        //防止mTransfrom重复的做同样的初始化
        if (mTransfrom != null) {
            return
        }
        if (width == 0 || height == 0) {
            return
        }
        mTransfrom = Transfrom()

        /** 下面为缩放的计算  */
        /* 计算初始的缩放值，初始值因为是CENTR_CROP效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个大于 */
        val xSScale = mOriginalWidth / mBitmap!!.width.toFloat()
        val ySScale = mOriginalHeight / mBitmap!!.height.toFloat()
        mTransfrom!!.startScale = if (xSScale > ySScale) xSScale else ySScale
        /* 计算结束时候的缩放值，结束值因为要达到FIT_CENTER效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个小于 */
        val xEScale = width / mBitmap!!.width.toFloat()
        val yEScale = height / mBitmap!!.height.toFloat()
        mTransfrom!!.endScale = if (xEScale < yEScale) xEScale else yEScale

        /**
         * 下面计算Canvas Clip的范围，也就是图片的显示的范围，因为图片是慢慢变大，并且是等比例的，所以这个效果还需要裁减图片显示的区域
         * ，而显示区域的变化范围是在原始CENTER_CROP效果的范围区域
         * ，到最终的FIT_CENTER的范围之间的，区域我用LocationSizeF更好计算
         * ，他就包括左上顶点坐标，和宽高，最后转为Canvas裁减的Rect.
         */
        /* 开始区域 */
        mTransfrom!!.startRect = LocationSizeF()
        mTransfrom!!.startRect!!.left = mOriginalLocationX.toFloat()
        mTransfrom!!.startRect!!.top = mOriginalLocationY.toFloat()
        mTransfrom!!.startRect!!.width = mOriginalWidth.toFloat()
        mTransfrom!!.startRect!!.height = mOriginalHeight.toFloat()
        /* 结束区域 */
        mTransfrom!!.endRect = LocationSizeF()
        val bitmapEndWidth = mBitmap!!.width * mTransfrom!!.endScale// 图片最终的宽度
        val bitmapEndHeight = mBitmap!!.height * mTransfrom!!.endScale// 图片最终的宽度
        mTransfrom!!.endRect!!.left = (width - bitmapEndWidth) / 2
        mTransfrom!!.endRect!!.top = (height - bitmapEndHeight) / 2
        mTransfrom!!.endRect!!.width = bitmapEndWidth
        mTransfrom!!.endRect!!.height = bitmapEndHeight

        mTransfrom!!.rect = LocationSizeF()
    }

    private inner class LocationSizeF : Cloneable {
        internal var left: Float = 0.toFloat()
        internal var top: Float = 0.toFloat()
        internal var width: Float = 0.toFloat()
        internal var height: Float = 0.toFloat()

        override fun toString(): String {
            return "[left:$left top:$top width:$width height:$height]"
        }

        @Throws(CloneNotSupportedException::class)
        public override fun clone(): Any {
            return super.clone()
        }

    }

    private fun getBmpMatrix() {
        if (drawable == null) {
            return
        }
        if (mTransfrom == null) {
            return
        }
        if (mBitmap == null || mBitmap!!.isRecycled) {
            mBitmap = (drawable as BitmapDrawable).bitmap
        }
        /* 下面实现了CENTER_CROP的功能 */
        mSmoothMatrix!!.setScale(mTransfrom!!.scale, mTransfrom!!.scale)
        mSmoothMatrix!!.postTranslate(-(mTransfrom!!.scale * mBitmap!!.width / 2 - mTransfrom!!.rect!!.width / 2),
                -(mTransfrom!!.scale * mBitmap!!.height / 2 - mTransfrom!!.rect!!.height / 2))
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return  // couldn't resolve the URI
        }

        if (mState == STATE_TRANSFORM_IN || mState == STATE_TRANSFORM_OUT) {
            if (mTransformStart) {
                initTransform()
            }
            if (mTransfrom == null) {
                super.onDraw(canvas)
                return
            }

            if (mTransformStart) {
                if (mState == STATE_TRANSFORM_IN) {
                    mTransfrom!!.initStartIn()
                } else {
                    mTransfrom!!.initStartOut()
                }
            }

            if (mTransformStart && BuildConfig.DEBUG) {
                Log.d("SmoothImageView", "mTransfrom.startScale:" + mTransfrom!!.startScale)
                Log.d("SmoothImageView", "mTransfrom.startScale:" + mTransfrom!!.endScale)
                Log.d("SmoothImageView", "mTransfrom.scale:" + mTransfrom!!.scale)
                Log.d("SmoothImageView", "mTransfrom.startRect:" + mTransfrom!!.startRect!!.toString())
                Log.d("SmoothImageView", "mTransfrom.endRect:" + mTransfrom!!.endRect!!.toString())
                Log.d("SmoothImageView", "mTransfrom.rect:" + mTransfrom!!.rect.toString())
            }

            val saveCount = canvas.saveCount
            canvas.save()
            // 先得到图片在此刻的图像Matrix矩阵
            getBmpMatrix()
            canvas.translate(mTransfrom!!.rect!!.left, mTransfrom!!.rect!!.top)
            canvas.clipRect(0f, 0f, mTransfrom!!.rect!!.width, mTransfrom!!.rect!!.height)
            canvas.concat(mSmoothMatrix)
            drawable.draw(canvas)
            canvas.restoreToCount(saveCount)
            if (mTransformStart) {
                mTransformStart = false
                startTransform(mState)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    private fun startTransform(state: Int) {
        if (mTransfrom == null) {
            return
        }
        val valueAnimator = ValueAnimator()
        valueAnimator.duration = 380
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        if (state == STATE_TRANSFORM_IN) {
            val scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom!!.startScale, mTransfrom!!.endScale)
            val leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom!!.startRect!!.left, mTransfrom!!.endRect!!.left)
            val topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom!!.startRect!!.top, mTransfrom!!.endRect!!.top)
            val widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom!!.startRect!!.width, mTransfrom!!.endRect!!.width)
            val heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom!!.startRect!!.height, mTransfrom!!.endRect!!.height)
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder)
        } else {
            val scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom!!.endScale, mTransfrom!!.startScale)
            val leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom!!.endRect!!.left, mTransfrom!!.startRect!!.left)
            val topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom!!.endRect!!.top, mTransfrom!!.startRect!!.top)
            val widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom!!.endRect!!.width, mTransfrom!!.startRect!!.width)
            val heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom!!.endRect!!.height, mTransfrom!!.startRect!!.height)
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder)
        }

        valueAnimator.addUpdateListener { animation ->
            mTransfrom!!.scale = animation.getAnimatedValue("scale") as Float
            mTransfrom!!.rect!!.left = animation.getAnimatedValue("left") as Float
            mTransfrom!!.rect!!.top = animation.getAnimatedValue("top") as Float
            mTransfrom!!.rect!!.width = animation.getAnimatedValue("width") as Float
            mTransfrom!!.rect!!.height = animation.getAnimatedValue("height") as Float
            invalidate()
            (context as Activity).window.decorView.invalidate()
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                /*
				 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
				 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
				 */
                // 这个可以根据实际需求来修改
                if (state == STATE_TRANSFORM_IN) {
                    mState = STATE_NORMAL
                }
                if (mTransformListener != null) {
                    mTransformListener!!.onTransformComplete(state)
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }
        })
        valueAnimator.start()
    }

    fun setOnTransformListener(listener: TransformListener) {
        mTransformListener = listener
    }

    private var mTransformListener: TransformListener? = null

    interface TransformListener {
        /**
         * @param mode STATE_TRANSFORM_IN 1 ,STATE_TRANSFORM_OUT 2
         */
        fun onTransformComplete(mode: Int) // mode 1
    }

    companion object {

        private val STATE_NORMAL = 0
        private val STATE_TRANSFORM_IN = 1
        private val STATE_TRANSFORM_OUT = 2
    }
}