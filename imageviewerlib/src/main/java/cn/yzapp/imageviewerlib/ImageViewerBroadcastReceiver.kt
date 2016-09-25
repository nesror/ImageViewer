package cn.yzapp.imageviewerlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @author nestor
 * *         email nestor@yzapp.cn
 */
class ImageViewerBroadcastReceiver(var mOnChangeItemListener: ImageViewer.OnChangeItemListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getIntExtra("position", 0) >= 0) {
            mOnChangeItemListener.onChangeItem(intent.getIntExtra("position", 0))
        }
        if (intent.getBooleanExtra("onDestroy", false)) {
            mOnChangeItemListener.onDestroy()
        }
    }
}
