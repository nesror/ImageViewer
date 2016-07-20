package cn.yzapp.imageviewerlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import java.io.File;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 * 图片加载接口
 */
public interface IImageLoader {
    void getImage(Context context, ImageView imageView, String Url);

    void getImage(Context context, ImageView imageView, File file);

    void getImage(Context context, ImageView imageView, @DrawableRes int res);

    void getImage(Context context, ImageView imageView, Bitmap bitmap);
}
