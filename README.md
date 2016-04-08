
![android-dynamical-loading](android-frontia/doc/banner_frontia_2.jpg "android-dynamical-loading")

#### 项目介绍

[![Join the chat at https://gitter.im/kaedea/android-dynamical-loading](https://badges.gitter.im/kaedea/android-dynamical-loading.svg)](https://gitter.im/kaedea/android-dynamical-loading?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

在 Android 开发中采用基于 ClassLoader 的动态加载技术，可以达到不安装新 APK 就升级 APP 的目的（插件化），也可以用来修复一些紧急 BUG（热修复），此外也可以用动态加载技术来精简 APK 的体积（移除 SO 库、拆分边缘业务模块）以及实现多主题在线下载。本REPO的主要内容是我对 Android 动态加载机制的一些研究、总结文章和 DEMO 项目。

这里提前说明，**个人不推荐使用插件化技术来动态发布和升级APP的业务功能**，因为这会让 APP 的开发和维护变得十分繁琐、不可控。不过，将插件化技术用于快速修复BUG以及精简APK体积还是挺值得尝试的，此外研究动态加载技术，对于学习Android 框架层的工作机制还是挺有帮助。

#### 项目结构
| 文件夹        |     说明     |
| :----------- | :-----------|
|[android-frontia](android-frontia/) | Android 插件化开发框架 Frontia |
|[tech-dynamical-loading](/tech-dynamical-loading) | Android 动态加载技术文章以及相关项目 |

其中，**android-frontia** 是基于 ClassLoader 的插件化框架，相比其他开源项目，Frontia 的特点更加专注于插件的下载、更新、安装、管理，以及插件和宿主之间的交互。**tech-dynamical-loading** 是 Android 动态加载加载技术分析的系列文章以及相关项目。

具体说明请进入相关目录。

#### LICENSE
Copyright (C) 2016 Kaede (谢晓枫) <kidhaibara@gmail.com> <br>
Copyright (C) 2015 HouKx <hkx.aidream@gmail.com> <br>
Copyright (C) 2014 singwhatiwanna (任玉刚) <singwhatiwanna@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at  <br>
　　http://www.apache.org/licenses/LICENSE-2.0 <br>
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
