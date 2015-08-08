#Android Dynamical Loading
  
![android-dynamical-loading](https://lh3.googleusercontent.com/-vAuCNXfEFOM/VcNZXUpI9JI/AAAAAAAABJk/G5MO-6SbXyw/s800/android-dynamical-loading.jpg "android-dynamical-loading")

##相关链接
[dynamic-load-apk](https://github.com/singwhatiwanna/dynamic-load-apk)
[android-pluginmgr](https://github.com/houkx/android-pluginmgr)
[Direct-Load-apk](https://github.com/FinalLody/Direct-Load-apk)
[alibaba dexposed](https://github.com/alibaba/dexposed)


##Android动态加载的类型
动态加载按技术实现上的区别可以分为两种：

 1. JNI中动态加载`.so`库 
 2. 动态加载`dex/jar/apk`（现在动态加载普遍说的是这种）

Android中NDK的使用就是一种动态加载，在Java中动态加载`.so`库并调用封装好的方法。后者是由C++编译而成，运行在Native层，效率会比执行在应用层的Java代码高很多，所以Android中经常通过动态加载`.so`库来完成一些对性能比较有需求的工作（比如T9搜索、或者Bitmap的解码等）。此外，由于`.so`库是由C++编译而来的，相比Java更难被反编译，因此动态加载`.so`库也可以被用于安全领域。

第二种动态加载就是在Android中动态加载由Java代码编译而来的`dex`包，这是常规Android开发比较少用到的一种技术，目前大多文章说到的动态加载指的就是这种（后面文章统称“动态加载”）。

Android项目中，所有Java代码都会被编译成`dex`包，Android应用运行时，就是通过执行`dex`包里的业务代码逻辑来工作的。使用动态加载技术可以在Android应用运行时加载不同的`dex`包，而通过网络下载新的`dex`包并替换原有的`dex`包就可以达到不安装新APK文件就升级应用的目的。使用动态加载技术，一般来说会使得Android开发工作变得更加复杂，这也不是Google官方推荐的，所以不是目前主流的Android开发方式，`Github`和`StackOverflow`上面的老外们也对此不是很感兴趣，目前只有在大天朝国内才有比较深入的研究和应用，特别是一些SDK组件项目和BAT系的项目上。

##Android动态加载的大致过程
无论哪种动态加载，其实基本原理都是在程序运行时加载一些可执行的文件，然后调用这些文件的某个方法执行业务逻辑。需要说明的是，因为文件是可执行的（so库或者dex包，也就是一种动态链接库），所以不能发在SD卡等`noexec`类型的存储路径上。

对于这些外部的动态链接库，在Android上使用前，都要拷贝到`data/packagename/`内部储存文件路径，确保库不会被第三方应用恶意修改或拦截，然后再将他们加载到当前的运行环境并调用需要的方法实现动态调用。

大致的过程就是：
>  1. 把动态链接库（so/dex/jar/apk）拷贝到应用内部存储 
>  2. 动态加载库 
>  3. 调用具体的方法执行业务逻辑

##动态加载`.so`库





##动态加载`dex/jar/apk`

###动态加载的作用

 1. 项目体积过大，拆分应用
 2. 应用更新太频繁，用户体验不好
 3. 紧急BUG的修复
 4. 应用导流

###动态加载的阶段

####入门
不覆盖安装，更改APK里的代码逻辑
####进阶（代理模式）
主APK可以启动未安装的插件APK；
插件APK也可以作为一个普通APK安装并且启动，但需要做特殊的处理
####终极玩法（非代理模式）
主APK可以启动任意第三方普通APK


###入门玩法
代表项目：九游游戏SDK

####核心技术
用`DexClassLoader`把`dex/jar/apk文件`加载到内存，实例化需要的对象，再调用其方法实现业务逻辑。因为`dex/jar/apk文件`是可以动态更换的，所以业务逻辑也可以动态更改，从而达到一种热部署的作用，而不用重新安装APK包。

其中`jar`文件必须是用dx命名优化过的，因为Android无法直接运行普通Java的jar包。


###代理模式
代表项目：[dynamic-load-apk](https://github.com/singwhatiwanna/dynamic-load-apk)、23code

####相关术语 
用`DexClassLoader`加载代码，用`AssetManager`加载资源
`代理Activity`  `插件Activity`  `生命周期接口` `多插件APK的管理`

####核心技术
宿主APK提供一个代理Activity，代理Activity是一个普通的Activity，但只是一个壳，自身并没有什么业务逻辑。每次调用插件APK里的某一个Activity的时候，都是启动代理Activity，再又代理Activity同步调用插件中的Activity的生命周期，从而执行插件APK的业务逻辑。

####动态加载APK并且调用插件Activity的某个方法
用`DexClassLoader`将插件APK加载到当前的运行时环境（其实是加载APK里的DEX），通过`DexClassLoader`的`loadClass`方法获取插件APK里的Activity的实例，但是这样的Activity实例只是一般Java类的实例，没有生命周期，不是标准的系统组件（没有通过ActivityManager的一系列构造过程，也没有Context等上下文环境）。
其实Activity也是一个Java类，我们可以像操作一个普通的对象一样来操作插件Activity的实例。Activity的生命周期方法都是protected熟悉的，不过我们仍然可是用`反射`的方法进行调用。
```JAVA
protected void launchTargetActivity(final String className) {
        Log.d(TAG, "start launchTargetActivity, className=" + className);
        File dexOutputDir = this.getDir("dex", 0);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(mDexPath,
                dexOutputPath, null, localClassLoader);
        try {
            Class<?> localClass = dexClassLoader.loadClass(className);
            Constructor<?> localConstructor = localClass
                    .getConstructor(new Class[] {});
            Object instance = localConstructor.newInstance(new Object[] {});
            Log.d(TAG, "instance = " + instance);

            Method setProxy = localClass.getMethod("setProxy",
                    new Class[] { Activity.class });
            setProxy.setAccessible(true);
            setProxy.invoke(instance, new Object[] { this });

            Method onCreate = localClass.getDeclaredMethod("onCreate",
                    new Class[] { Bundle.class });
            onCreate.setAccessible(true);
            Bundle bundle = new Bundle();
            bundle.putInt(FROM, FROM_EXTERNAL);
            onCreate.invoke(instance, new Object[] { bundle });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

####启动没有注册的插件Activity的两个主要难题：
 - 如何加载res资源（使用R文件）
 - 插件Activity的生命周期的控制

#####插件Activity如何使用R文件
Activity是通过`AssetManager`来加载APK里的res资源，并且通过`Resources`来管理这些资源。其中，`Resources`是通过`AssetManager`创建的，也就是说只要有`AssetManager`的实例就OK了。对于正常的Activity，其`AssetManager`实在Framework层由ActivityManagerService完成创建的，但是插件Activity并没有这个过程，所以并没有对应的`AssetManager`实例（这也是其无法访问res资源的原因所在）。

对于插件Activity，可以通过`反射`的方法创建`AssetManager`，加载插件APK的资源，并且创建出`Resources`，这样一来插件Activity里就可以正常使用R文件了。
```JAVA
try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
}
```
注意：代理Activity有自己的`AssetManager`和`Resources`用于维护自身的res资源，因此需要再实例化一个新的`AssetManager`和`Resources`用于加载插件Activity里的res资源，也就是说其实代理Activity维护了两套res资源，并不是把新的res资源和原有的res资源合并了（所以不怕R.id重复），对两个res资源的访问都需要用对应的`Resources`。（其实有3套，Android系统会加载一套framework-res.apk资源）

#####管理插件Activity的生命周期
用代理Activity（正常的Activity实例）的生命周期同步控制插件Activity（普通类的实例）的生命周期。同步的方式可以有一下几种：

 - 在代理Activity生命周期里用反射调用插件Activity相应生命周期的接口，简单粗暴。
 - 把插件Activity的生命周期抽象成接口，在代理Activity的生命周期里调用。另外，多了这一层接口，也方便宿主控制插件Activity。

Fragment自带生命周期，用Fragment来代替Activity开发可以省去大部分生命周期的控制工作，但是会使得Activity跳转比较麻烦。

####多插件APK的管理
动态加载一个插件 APK需要三个对应的`DexClassLoader`、`AssetManager`、`Resources`实例，可以用组合的方式创建一个`PluginPackage`类存放这三个变量，再创建一个管理类`PluginManager`，用`HashMap<dexPath,pluginPackage>`的方式保存`PluginPackage`实例。


###非代理模式
代表项目：[android-pluginmgr](https://github.com/houkx/android-pluginmgr)

####相关术语
`dexmaker`  `ActivityClassGenerator`
`CJClassLoader`  `PluginClassLoader`  `proxyActivityLoaderMap`

####核心技术
在`宿主Apk`的Manifest文件注册一个本地并不存在的PlugActivity，然后动态加载`插件APK`，每当启动`插件APK`里的任何一个Activity（比如GameActivity）时，都会动态创建一个PlugActivity类继承GameActivity，这个PlugActivity就具有GameActivity的所有特性了，同时由于是在宿主APK的Manifest中注册好的了，所以具有标准Activity的生命周期，而且因为不是使用代理Activity的方式，所以PlugActivity（其实就是GameActivity）也能直接使用自己的资源文件。

一般的Activity运作方式是创建一个Activity，然后在Manifest中注册，然后再启动该Activity，`pluginmgr`则是先在Manifest中注册一个通用的PlugActivity，每次需要启动一个Activity时都动态生成一个PlugActivity去继承这个Activity，从而达到只需注册一个PlugActivity便可以完成多个Activity的功能。

####如何动态创建Activity（其实是创建dex）
用Google提供的`dexmaker`，在`pluginmgr`中，创建工作封装在`ActivityClassGenerator`里。

####启动插件的某个Activity，如何变成启动PlugActivity
偷梁换柱，宿主APK启动的时候，用`反射`替换其Application的ClassLoader（`pluginmgr`中用`CJClassLoader`替换），重写`loadClass`方法，在加载插件Activity时偷换成PlugActivity。

`pluginmgr`项目中有三种ClassLoader，一是用于替换宿主APK的Application的`CJClassLoader`，二是用于加载插件APK的`PluginClassLoader`，再来是用于加载启动插件Activity时动态生成的PlugActivity的dex包的`DexClassLoader`（存放在Map集合`proxyActivityLoaderMap`里面）。其中`CJClassLoader`是`PluginClassLoader`的Parent，而`PluginClassLoader`又是第三种`DexClassLoader`的Parent。

ClassLoader类加载Class的时候，会先使用Parent的`ClassLoader`，但Parent不能完成加载工作时，才会调用Child的`ClassLoader`去完成工作，具体工作方式见ClassLoader的`双亲代理`模式。


>java.lang.ClassLoader
Loads classes and resources from a repository. One or more class loaders are installed at runtime. These are consulted whenever the runtime system needs a specific class that is not yet available in-memory. Typically, class loaders are grouped into a tree where child class loaders delegate all requests to parent class loaders. Only if the parent class loader cannot satisfy the request, the child class loader itself tries to handle it.

所以每加载一个Activity的时候都会调用到最上级的`CJClassLoader`的`loadClass`方法，从而保证启动插件Activity的时候能顺利替换成PlugActivity。当然如何控制着三种ClassLoader的加载工作，也是`pluginmgr`项目的设计难度之一。


###代理模式与非代理模式的区别
代理模式是使用一个代理Activity去完成本应该由插件Activity完成的工作，但是


----------
待续