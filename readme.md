
![android-dynamical-loading](doc/dl.jpg "android-dynamical-loading")

## 项目介绍

[![Join the chat at https://gitter.im/kaedea/android-dynamical-loading](https://badges.gitter.im/kaedea/android-dynamical-loading.svg)](https://gitter.im/kaedea/android-dynamical-loading?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
在Android开发中采用基于ClassLoader的动态加载技术，可以达到不安装新APK就升级APP的目的，也可以用来修复一些紧急BUG。本Repo的主要内容是我对Android动态加载机制的一些研究、总结文章和Sample项目。

## 目录结构
| 文件夹        |     说明     |
| :----------- | :-----------|
|apk  | 用于演示动态加载的APK文件 |
|doc  | 对动态加载机制研究以及Sample项目源码分析的[文档](https://github.com/kaedea/android-dynamical-loading/tree/master/doc) |
|classloader-working | 分析Android中ClassLoader工作机制的Demo项目|
|dynamic-load-so | 动态加载SD中的so文件的Sample项目 |
|level1_dynamic-load-dex    | 初级动态加载，演示如何动态加载dex/jar/apk文件 |
| level2_dynamic-load-pluginapk     |    中级动态加载，项目来自[dynamic-load-apk](https://github.com/singwhatiwanna/dynamic-load-apk)，代理Activity模式，插件APK里的Activity/Service等组件，但是宿主APK和插件APK都得遵循事先定好的框架  |
|level3_dynamic-load-normalapk|终极动态加载，项目来自[android-pluginmgr](https://github.com/houkx/android-pluginmgr)，动态创建Activity模式，宿主APK不用注册插件APK的组件就能直接启动普通第三方APK的Activity|

## 动态加载系列文章
在Android开发中采用动态加载技术，可以达到不安装新的APK就升级APP功能的目的，可以用来到达快速发版的目的，也可以用来修复一些紧急BUG。

现在使用得比较广泛的动态加载技术的核心一般都是使用 **ClassLoader** ，后者能够加载程序外部的类（已编译好的），从而达到升级代码逻辑的目的。虽然动态加载的核心原理比较简单，但是根据功能的复杂程度，实际在Android项目中使用的时候还要涉及许多其他方面的知识，这里分为几个篇幅分别进行介绍。

#### 简单易懂的介绍
内容：
 1. 动态加载技术在Android中的使用背景；
 2. Android的动态的加载大致可以分为“加载SO库”和“加载DEX/JAR/APK”两种；
 3. 动态加载的基础是类加载器ClassLoader；
 4. 使用动态加载的三种模式；
 5. 采用动态加载的作用与代价；
 6. 除了ClassLoader之外的动态修改代码的技术（HotFix）；
 7. 一些动态加载的开源项目；

地址：[Android动态加载技术 简单易懂的介绍](http://segmentfault.com/a/1190000004062866)
<br>

#### 类加载器ClassLoader的工作机制
内容：
 1. 类加载器ClassLoader的创建过程和加载类的过程；
 2. ClassLoader的双亲代理模式；
 3. DexClassLoader和PathClassLoader之间的区别；
 4. 使用ClassLoader加载外部类需要注意的一些问题；
 5. 自定义ClassLoader（Hack开发）

文章地址：[Android动态加载基础 ClassLoader的工作机制](http://segmentfault.com/a/1190000004062880)
<br>

#### 加载SD卡的SO库
内容：
 1. 如何编译和使用SO库；
 2. 分析Android中加载SO库相关的源码；
 3. 如何加载SD卡中的SO库（也是动态加载APK需要解决的问题）；

地址：[Android动态加载补充 加载SD卡的SO库](http://segmentfault.com/a/1190000004062899)
<br>

#### 简单的动态加载模式
内容：
 1. 如何创建我们需要的dex文件；
 2. 如何加载dex文件里面的类；
 3. 动态加载dex文件在ART虚拟机的兼容性问题；

地址：[Android动态加载入门 简单加载模式](http://segmentfault.com/a/1190000004062952)
<br>

#### 代理Activity的模式
内容：
 1. 如何启动插件APK中没有注册的Activity
 2. 代理Activity模式开源项目“dynamic-load-apk”

地址：[Android动态加载进阶 代理Activity模式](http://segmentfault.com/a/1190000004062972)
<br>

#### 动态创建Activity的模式
内容：
 1. 如何在运行时动态创建一个Activity；
 2. 自定义ClassLoader并偷梁换柱替换想要加载的类；
 3. 动态创建Activity模式开源项目“android-pluginmgr”
 4. 代理模式与动态创建类模式的区别；

地址：[Android动态加载黑科技 动态创建Activity模式](http://segmentfault.com/a/1190000004077469)

#### 还未发布的内容
1. 使用“环境注入”的模式；
2. 使用动态加载技术的情形；
3. 使用动态加载方式项目的项目结构调整和开发调试方式；
4. 开源项目“Android-Frontia”，动态加载框架的项目，专注于“插件化”和“宿主与插件之间的通讯”；


## Frontia
代理Activity模式的动态加载框架，基于dynamic-load-apk项目开发，主要注重于“插件管理”和“插件与主项目通讯”的功能，目前正在开发中。

## 动态加载开源项目
- [ACDD](https://github.com/bunnyblue/ACDD)
- [DL dynamic-load-apk](https://github.com/singwhatiwanna/dynamic-load-apk)
- [android-pluginmgr](https://github.com/houkx/android-pluginmgr)
- [Direct-Load-apk](https://github.com/FinalLody/Direct-Load-apk)
- [360 DroidPlugin](https://github.com/Qihoo360/DroidPlugin)
- [携程网 DynamicAPK](https://github.com/CtripMobile/DynamicAPK)
- [女娲 Nuwa](https://github.com/jasonross/Nuwa)
- [Android-Plugin-Framework](https://github.com/limpoxe/Android-Plugin-Framework)
- [Small](https://github.com/wequick/Small)
- [android-dynamic-load-awesome](https://github.com/liaohuqiu/android-dynamic-load-awesome)
