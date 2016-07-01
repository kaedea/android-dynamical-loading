### 插件化框架设计
基于ClassLoader的动态加载框架。

#### 项目整体结构
// 待补充

#### 插件化框架
// 待补充

#### 腾讯视频SDK插件化
通过插件化，把腾讯视频SDK分离出去，精简APK体积。

##### 一期
将腾讯视频SDK插件内置于APP的assets目录之下
插件为APK格式，不过后缀名改为.so

#### 二期
从网络下载腾讯视频SDK插件

插件查询的API格式
```` json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "tv.danmaku.plugin.tencentsdk", // 插件识别ID
    "description": "tmedia component",
    "list": [ // 插件列表
      {
        "version": 1, // version code
        "download_url": "下载链接",
        "enable": 1, // 是否启动，大于等于1表示true
        "force_update": 0,
        "min_app_build": 20, // 最低哔哩哔哩客户端版本要求
        "status": "working"
      },
      {
        "version": 2,
        "download_url": "下载链接",
        "enable": 0,
        "force_update": 0,
        "min_app_build": 25,
        "status": "abandon, no longer use"
      },
      {
        "version": 3,
        "download_url": "下载链接",
        "enable": 1,
        "force_update": 0,
        "min_app_build": 25,
        "status": "working"
      }
    ]
  }
}
````

插件下载链接
```` http
http://www.bilibili.com/
````
