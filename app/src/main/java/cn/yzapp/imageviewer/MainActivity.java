package cn.yzapp.imageviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.yzapp.imageviewerlib.ImageViewer;
import cn.yzapp.imageviewerlib.ShowImage;
import cn.yzapp.imageviewerlib.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        GridLayout layout = (GridLayout) findViewById(R.id.layout);

        int width = (Utils.getScreenWidth(this) - Utils.dip2px(this, 32)) / 3;
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, width);

        final ArrayList<String> urlList = new ArrayList<>();
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");

        final ShowImage showImage = new ShowImage();
        showImage.setImgUrl(urlList);
        final List<ImageView> imgs = new ArrayList<>();

        int i = 0;
        for (String url : urlList) {

            final View view = View.inflate(this, R.layout.view_img, null);
            final ImageView img = (ImageView) view.findViewById(R.id.img);
            Picasso.with(this).load(url).into(img);
            img.setLayoutParams(lp);

            imgs.add(img);

            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<int[]> sizes = new ArrayList<>();
                    for (ImageView imageView : imgs) {
                        int[] location = new int[2];
                        imageView.getLocationOnScreen(location);
                        int width = imageView.getWidth();
                        int height = imageView.getHeight();

                        int[] size = new int[4];
                        size[0] = width;
                        size[1] = height;
                        size[2] = location[0];
                        size[3] = location[1];

                        sizes.add(size);
                    }

                    showImage.setSizes(sizes);
                    showImage.setIndex(finalI);
                    ImageViewer.openImageViewer(MainActivity.this, showImage);

                }
            });

            layout.addView(view);

            i++;
        }
    }
}
