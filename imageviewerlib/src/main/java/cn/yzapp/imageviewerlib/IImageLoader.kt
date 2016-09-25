package cn.yzapp.imageviewerlib

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.DrawableRes
import android.widget.ImageView

import java.io.File

/**
 * @author nestor
 * * email nestor@yzapp.cn
 * * 图片加载接口
 */
interface IImageLoader {
    fun getImage(context: Context, imageView: ImageView, Url: String)

    fun getImage(context: Context, imageView: ImageView, file: File)

    fun getImage(context: Context, imageView: ImageView, @DrawableRes res: Int)

    fun getImage(context: Context, imageView: ImageView, bitmap: Bitmap)
}
