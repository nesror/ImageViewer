package cn.yzapp.imageviewer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.yzapp.imageviewerlib.DefaultImageLoader;
import cn.yzapp.imageviewerlib.ImageViewer;
import cn.yzapp.imageviewerlib.ImageViewerConfig;
import cn.yzapp.imageviewerlib.Utils;

@Deprecated
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setImagerViewerConfig();

        initView();
    }

    private void initView() {

        final ImageViewer imageViewer = new ImageViewer();

        GridLayout layout = (GridLayout) findViewById(R.id.layout);

        int width = (Utils.INSTANCE.getScreenWidth(this) - Utils.INSTANCE.dip2px(this, 32)) / 3;
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, width);

        final ArrayList<Object> urlList = new ArrayList<>();
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add(R.mipmap.ic_launcher);
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");

        final List<ImageView> imgs = new ArrayList<>();

        int i = 0;
        for (Object url : urlList) {

            final View view = View.inflate(this, R.layout.view_img, null);
            final ImageView img = (ImageView) view.findViewById(R.id.img);
            if (url instanceof Integer) {
                Picasso.with(this).load((int) url).into(img);
            } else {
                Picasso.with(this).load((String) url).into(img);
            }
            img.setLayoutParams(lp);

            imgs.add(img);

            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageViewer.open(MainActivity.this, imgs, urlList, finalI);
                }
            });

            if (layout != null) {
                layout.addView(view);
            }

            i++;
        }
    }

    /**
     * ImageViewer的全局设置
     */
    private void setImagerViewerConfig() {
        ImageViewerConfig.INSTANCE.setImageLoader(new DefaultImageLoader() {
            @Override
            public void getImage(Context context, ImageView imageView, String Url) {
                Picasso.with(MainActivity.this).load(Url).into(imageView);
            }
        });
        //ImageViewerConfig.setChooseResIs(R.drawable.img_point_focused);
        //ImageViewerConfig.setUnChooseResIs(R.drawable.img_point_nomal);
    }

}
