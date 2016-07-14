# ImageViewer
仿微信朋友圈图片浏览，带进入和退出的动画

Add ImageViewer to your project
----------------------------
[![](https://jitpack.io/v/nesror/ImageViewer.svg)](https://jitpack.io/#nesror/ImageViewer)

Step 1. Add the JitPack repository to your build file
```
	maven { url "https://www.jitpack.io" }
```

Step 2. Add the dependency
```
	compile 'com.github.nesror:ImageViewer:[look download]'
```

Use
----------------------------
请看demo

混淆配置
----------------------------
```
	-keep class uk.co.senab.photoview.** { *; }
	-dontwarn uk.co.senab.photoview.**
```

用到的第3方lib
----------------------------
* compile 'com.github.chrisbanes.photoview:library:1.2.2'
* compile 'me.relex:circleindicator:1.2.1@aar'
* compile 'com.squareup.picasso:picasso:2.5.2'
* 图片缩放代码参考自CocolVook/SimplifyReader
