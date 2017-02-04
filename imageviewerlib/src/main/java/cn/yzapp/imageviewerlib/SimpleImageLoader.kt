package cn.yzapp.imageviewerlib

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.DrawableRes
import android.widget.ImageView

import java.io.File

/**
 * @author nestor
 * * email: nestor@yzapp.cn
 * * 图片加载默认实现类，文件与链接的图片加载需要根据自己的项目里用到的图片加载类来自己实现
 * * 也可以使用IImageLoader
 */
open class SimpleImageLoader : IImageLoader {
    override fun getImage(context: Context, imageView: ImageView, Url: String) {
        // 需要你自己来实现
    }

    override fun getImage(context: Context, imageView: ImageView, file: File) {
        // 需要你自己来实现
    }

    override fun getImage(context: Context, imageView: ImageView, @DrawableRes res: Int) {
        imageView.setImageResource(res)
    }

    override fun getImage(context: Context, imageView: ImageView, bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }
}
