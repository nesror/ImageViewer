package cn.yzapp.imageviewerlib

import android.support.annotation.DrawableRes

/**
 * @author nestor
 * * email nestor@yzapp.cn
 * * ImageViewer的全局设置
 */
object ImageViewerConfig {
    /**
     * 设置全局图片加载器，必须实现

     * @param imageLoader IImageLoader
     */
    var imageLoader: IImageLoader? = null

    /**
     * 设置全局选中时的指示器颜色
     */
    var chooseResIs: Int = 0
    /**
     * 设置全局未选中时的指示器颜色
     */
    var unChooseResIs: Int = 0

}
