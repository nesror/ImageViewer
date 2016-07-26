package cn.yzapp.imageviewerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * @author nestor
 * email nestor@yzapp.cn
 */
public class ImageViewerBroadcastReceiver extends BroadcastReceiver {
    private ImageViewer.OnChangeItemListener mOnChangeItemListener;

    public ImageViewerBroadcastReceiver() {
    }

    public ImageViewerBroadcastReceiver(ImageViewer.OnChangeItemListener onChangeItemListener) {
        mOnChangeItemListener = onChangeItemListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mOnChangeItemListener != null) {
            mOnChangeItemListener.onChangeItem(intent.getIntExtra("position", 0));
            if (intent.getBooleanExtra("onDestroy", false)) {
                mOnChangeItemListener.onDestroy();
            }
        }
    }
}
