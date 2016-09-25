package cn.yzapp.imageviewerlib

import java.util.*
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by nestor on 9/25 025.
 *
 * sizes:依次为图片的宽，高，x轴坐标，y轴坐标
 * 不传就不显示缩放效果
 *
 * index:当前点击的图片索引
 *
 * img:设置图片
 * 传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
 */
class ImageInfo(var sizes: List<IntArray>, var index: Int, var img: List<Any>) : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ImageInfo> = object : Parcelable.Creator<ImageInfo> {
            override fun createFromParcel(source: Parcel): ImageInfo = ImageInfo(source)
            override fun newArray(size: Int): Array<ImageInfo?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            ArrayList<IntArray>().apply {
                source.readList(this, IntArray::class.java.classLoader)
            },
            source.readInt(), ArrayList<Any>().apply {
        source.readList(this, Any::class.java.classLoader)
    })

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeList(sizes)
        dest?.writeInt(index)
        dest?.writeList(img)
    }
}