package cn.yzapp.imageviewerlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import java.io.File;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 * 图片加载默认实现类，文件与链接的图片加载需要根据自己的项目里用到的图片加载类来自己实现
 * 也可以使用IImageLoader
 */
public class DefaultImageLoader implements IImageLoader{
    @Override
    public void getImage(Context context, ImageView imageView, String Url) {
        // 需要你自己来实现
    }

    @Override
    public void getImage(Context context, ImageView imageView, File file) {
        // 需要你自己来实现
    }

    @Override
    public void getImage(Context context, ImageView imageView, @DrawableRes int res) {
        imageView.setImageResource(res);
    }

    @Override
    public void getImage(Context context, ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
