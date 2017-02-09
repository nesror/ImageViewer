# ImageViewer
![image](img/img1.gif)![image](img/img2.jpg)

Add ImageViewer to your project
----------------------------
[![](https://jitpack.io/v/nesror/ImageViewer.svg)](https://jitpack.io/#nesror/ImageViewer)

Step 1. Add the JitPack repository to your build file
```
	maven { url "https://www.jitpack.io" }
```

Step 2. Add the dependency
```
	compile 'com.github.nesror:ImageViewer:[look jitpack]'
```

Use
----------------------------
Step 1. 实现IImageLoader设置图片加载器，也可以使用实现类SimpleImageLoader()
```{Kotlin}
    ImageViewerConfig.imageLoader = object : SimpleImageLoader() {
            override fun getImage(context: Context, imageView: ImageView, Url: String) {
                Picasso.with(this@TabActivity).load(Url).into(imageView)
            }
        }
````
````{java}
    ImageViewerConfig.INSTANCE.setImageLoader(new SimpleImageLoader() {
            @Override
            public void getImage(Context context, ImageView imageView, String Url) {
                Picasso.with(TabActivity.this).load(Url).into(imageView);
            }
        });
````
Step 2. 使用
```{java}

    /**
     * 打开图片浏览多张
     *
     * @param context    Context
     * @param imageViews ImageView
     * @param objects    传入格式支持：String:图片的url;(@DrawableRes) int:资源id;Bitmap;File
     * @param clickItem  点击的图片
     */
    imageViewer.open(context, imageViews, objects, clickItem);
```
* 更多方法详见Demo

混淆配置
----------------------------
```
	-keep class uk.co.senab.photoview.** { *; }
	-dontwarn uk.co.senab.photoview.**
```

用到的第3方lib
----------------------------
* compile 'com.github.chrisbanes:PhotoView:1.3.0'
* compile 'me.relex:circleindicator:1.2.1@aar'
