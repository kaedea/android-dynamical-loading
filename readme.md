##说明
Android动态加载机制的一些研究、总结和Sample项目。

##目录结构
| 文件夹        |     说明     |
| :----------- | :-----------| 
|apk  | Sample项目中用于动态加载的APK文件 | 
|doc  | 我对动态加载机制研究以及Sample项目源码分析的[文档](https://github.com/kaedea/android-dynamical-loading/tree/master/doc) | 
|level1_dynamic-load-dex    | 使用初级动态加载的Sample项目，演示如何动态加载dex/jar/apk文件 | 
| level2_dynamic-load-pluginapk     |    中级动态加载，项目来自[dynamic-load-apk](https://github.com/singwhatiwanna/dynamic-load-apk)，使用代理模式，宿主APK不用注册插件APK的组件就能够启动插件APK里的Activity/Service等组件，但是宿主APK和插件APK都得遵循事先定好的框架  | 
|level3_dynamic-load-normalapk|终极动态加载，项目来自[android-pluginmgr](https://github.com/houkx/android-pluginmgr)，非代理模式，宿主APK不用注册插件APK的组件就能直接启动普通第三方APK的Activity|