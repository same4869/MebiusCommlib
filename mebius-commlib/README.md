[![](https://jitpack.io/v/same4869/MebiusCommlib.svg)](https://jitpack.io/#same4869/MebiusCommlib)

### Commlib
此库为其他库的基础，需要保持尽可能纯净，且禁止业务相关

#### 相关功能
- MebiusUIUtil：view相关，屏幕相关的通用工具类，显示隐藏等
- MebiusStatusBarUtil：状态栏相关设置
- MebiusSPUtil：通用SP工具
- MebiusNetworkUtil：判断网络状态等工具类
- MebiusMD5Utils：给对应字符串md5，如果是对大文件md5的话，可以使用md5文件夹下的
- MebiusFileUtil：文件相关工具类
- MebiusExt：通用扩展
- MebiusExecutor：基于线程池的一个任务系统
- MebiusDeviceUtil：设备信息相关工具类
- MebiusCommUtil：通用业务工具类，基于rxbind的，onclick事件，防抖等
- JsonParser：不依赖gson等序列化框架，通过外部依赖注入的方式
- GlobalRxDisposeManager：如果使用rxjava，防止内存泄漏工具类
- livedata包：防止倒灌的livedata



--------------------------------------------------------------

# Module kotlin-demo

The module shows the Dokka syntax usage.

# Package org.jetbrains.kotlin.demo

Contains assorted useful stuff.

## Level 2 heading

Text after this heading is also part of documentation for `org.jetbrains.kotlin.demo`

# Package org.jetbrains.kotlin.demo2

Useful stuff in another package.