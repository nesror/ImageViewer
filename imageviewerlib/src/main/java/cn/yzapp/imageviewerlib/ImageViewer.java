package cn.yzapp.imageviewerlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author: GuSiheng
 * @time: 7/14 014-11:29.
 * @email: gusiheng@qccr.com
 * @desc:
 */
public class ImageViewer {
    public static final String INTENT_IMAGE = "INTENT_IMAGE";

    /**
     * 打开图片浏览
     */
    public static void openImageViewer(Context context, ShowImage showImage) {
        Bundle extras = new Bundle();
        extras.putParcelable(INTENT_IMAGE, showImage);
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }
}
