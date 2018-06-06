# ipfs
android调用ipfs执行存储操作

添加Jitpack依赖

```groovy
allprojects {
    repositories {
        google()
        jcenter()
		//jitpack need
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
//去 realse 版本那查看最新版本
implementation 'com.github.wulijie:ipfs:1.0'
```

