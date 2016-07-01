# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\ADT\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}




-optimizationpasses 5
-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-dontpreverify
-verbose
#-applymapping qqlive_proguard_mapping.txt
#-dontoptimize
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizations method/inlining/*

#-allowaccessmodification
-keepattributes *Annotation*
-keepattributes Signature
#-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
#-repackageclasses


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
#-dontnote com.android.vending.licensing.ILicensingService

-keep class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static long serialVersionUID;
    static java.io.ObjectStreamField[] serialPersistentFields;
    void writeObject(java.io.ObjectOutputStream);
    void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class **.R$* {
	public static <fields>;
}

-keep class sun.misc.Unsafe {*;}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
	public static final android.os.Parcelable$Creator *;
}



#======   以下为app中定义的类或者第三方库中用到的类 (ashercai 2014-8-8)  ============================
#         规则：
#         1) 第三方库，如果已经混淆过，建议全部保留
#         2) 第三方库，如果包含动态库，建议全部保留
#         3) App的类，如果用到动态库，建议保留包 (如果明确动态库中没有创建Java对象或访问Java类成员，可混淆)
#         4) App的类，如果用到了反射，需检查代码，将涉及的类和成员保留
#         5) App的类，定义为@JavascriptInterface的成员，需要保留
#=============================================================================================


-keepattributes Exceptions,InnerClasses

-keep class pi.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.httpproxy.api.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.nonp2pproxy.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.p2pproxy.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.httpproxy.CKeyFacade {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.api.TVK_IMediaPlayer$* {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.api.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.api.TVK_IMediaPlayer$* {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.view.TVK_PlayerVideoView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, boolean, boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.tencent.qqlive.mediaplayer.view.TVK_PlayerVideoView_Scroll {
    public <init>(android.content.Context);
    public <init>(android.content.Context, boolean, boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.tencent.qqlive.mediaplayer.playernative.** {
    <fields>;
    <methods>;
}

-keep class  com.tencent.qqlive.mediaplayer.view.IVideoViewBase{
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.videoad.VideoPreAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoMidAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoPauseAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoIvbAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoPostrollAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep class  com.tencent.qqlive.mediaplayer.bullet.BulletController{
   <fields>;
   <methods>;
}

-keep class  com.tencent.qqlive.mediaplayer.bullet.api{
   <fields>;
   <methods>;
}
-keep class  com.tencent.qqlive.mediaplayer.playerController.api.**{
   <fields>;
   <methods>;
}

-keep class  com.tencent.qqlive.ona.base.AppLaunchReporter{
   <fields>;
   <methods>;
}

-keep class com.qq.taf.** {*;}
-keep class com.tencent.ads.** {*;}
-keep class com.tencent.omg.stat.** {*;}
-keep class vspi.** {*;}

-keep class com.tencent.qqlive.api.** {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.PlayerController.MediaController {
   <fields>;
   <methods>;
}


-keep class com.tencent.qqlive.mediaplayer.player.PlayerImageCapture$* {
    <fields>;
    <methods>;
}

-keep class com.tencent.updata.jni.** {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.dlna.DlnaManager {
    <methods>;
 }
-keep class org.cybergarage.xml.parser.XmlPullParser {
    <fields>;
    <methods>;
}
-keep class org.cybergarage.xml.parser.JaxpParser {
    <fields>;
    <methods>;
}
-keep class org.cybergarage.xml.parser.kXML2Parser {
    <fields>;
    <methods>;
}
-keep class org.cybergarage.xml.parser.XercesParser {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.ona.base.AppLaunchReporter {
    <fields>;
    <methods>;
}
-keep class com.tencent.ads.utility.AdSetting {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.UIController {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.playerController.Loading {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.playerController.MediaControllerView {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.recommendController.LimitPlayView {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.recommend.RecommendInfo {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.uicontroller.UIControllerListener {
    <fields>;
    <methods>;
}
-keep class com.tencent.ads.service.AppAdConfig {
    <fields>;
    <methods>;
}


-keep class me.kaede.pluginpackage.** {
    <fields>;
    <methods>;
}


