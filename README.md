# MoMeGo
Java ME Mobile Chinese Message App

## Overview

Development scene: 2009, the mobile world of Nokia, there were only few of smart phones like iPhone 3G. Most of apps are J2ME apps.

Video Demo:
https://www.youtube.com/watch?v=FYwnZ86NiZQ


1. run in J2ME supported phones, PDA, BlackBerry OS etc.
2. Send&receive Chinese SMS on Non-Chinese OS mobile phones.
3. Support Chinsese PinYin input method, even in Non-Chinese OS.
4. Chinese/English Multiple language menu.

## Used Libs:
https://github.com/Enough-Software/j2mepolish


## Getting Started

1. Eclipse
2. WTk
3. BlackBerry_JDE_4.x.x
4. Nokia J2ME SDK
5. SonyEricsson JavaME_SDK_CLDC



---
---

跨语言平台收发中文短信软件： MoMeGo (Mobile Message GO)

开发场景：2009年年末开发，那时智能机还未普及，移动手机还是Nokia的天下，App以J2ME为主。

视频演示
https://www.youtube.com/watch?v=FYwnZ86NiZQ

1. 可运行于支持Java的移动设备，包括手机，PDA， BlackBerry等等
2. 可在德语，英语，法语等非中文操作系统系统中收发中文短信, 不管它是否装了MoMeGo，都可以接受短信
3. 自带中文拼音输入法，非中文系统中可以自由输入汉字
4. 菜单界面全汉化

不足：
1. 非中文系统的Windows Mobile OS， 触摸屏手机和BlackBerry里，无法调用MoMeGo自带的拼音输入法，无法输入中文，不过接受并显示中文短信是没有问题的
2. 不能接受从未装MoMeGo的设备里直接发送的短信－－这个是由于java的限制，技术上没法解决。只能让对方装MoMeGo，这样你才能收到对方在MoMeGo里发送的中文短信。

未完成功能：
1. 联系人添加，编辑，群组划分，群发短信，模板编辑。
2. 压缩短信（发三条短信的字数，只花一条短信的钱）等等等等
3. 远程激活：发短信可让对方手机里的MoMeGo自动运行并接受你的短信
