# Last-Version 
1.0.0

# BSDiff
app 增量更新库，只提供old.apk 与 patch 合并成新包，不参与patch下载以及安装操作

Test
--------
1. download [old.apk](https://github.com/yuanbinbinbin/IBsdiff/tree/master/ext/test/old.apk)
2. install it
3. download [patch](https://github.com/yuanbinbinbin/IBsdiff/tree/master/ext/test/patch)
4. put patch to your phone
5. open old app and select patch
6. update

Screenshot
--------
<img src="https://github.com/yuanbinbinbin/IBsdiff/blob/master/ext/test/screenshot.jpg" alt="screenshot.jpg" />

Create patch file
--------
**Windows**

1. [Download](https://github.com/yuanbinbinbin/IBsdiff/tree/master/ext/util/bsdiff-v4.3-win-x64.zip)
2. unzip it
3. your old apk name old.apk 
4. your new apk name new.apk
5. copy old.apk and new.apk to your unzip file
6. enter dos window(cmd)
7. enter unzip file
8. execute "bsdiff old.apk new.apk patch"
9. wait
10. a new file called patch will be created
  
**mac**
1. [Download](https://github.com/yuanbinbinbin/IBsdiff/tree/master/ext/util/bsdiff-4.3-mac.tar.gz)
2. unzip it
3. your old apk name old.apk 
4. your new apk name new.apk
5. copy old.apk and new.apk to your unzip file
6. enter Terminal
7. enter unzip file
8. execute "make"
9. wait
10. a new file called bsdiff will be created
11. execute "./bsdiff old.apk new.apk patch"
12. wait
13. a new file called patch will be created

Usage
--------
With Gradle:
```groovy
  compile 'com.github.binbinrd:bsdiff:{lastVersion}'
  
  Sample:
  compile 'com.github.binbinrd:bsdiff:1.0.0'
```

How to use
--------
```groovy
    //Get the APK path of app
    String oldPath = BsdiffUtil.getOldApkPath(Context context);
    
    //Get the hash value of app
    String hash = BsdiffUtil.hash(String apkPath);
    
    //Merge new apk
    boolean isSuccess //true: merge success false: merge failure
            = BsdiffUtil.merge(
                String old, //old apk path
                String patch, //patch path
                String newPath, //output apk path
                boolean isLogOpen//can print log
                );
```

support
--------
lib support 

'armeabi',  'armeabi-v7a','arm64-v8a','x86','x86_64','mips','mips64'

If your app only supports 'armeabi-v7a','x86'

[Try like this](https://github.com/yuanbinbinbin/IBsdiff/tree/master/app/build.gradle)
```groovy
android {
    defaultConfig {
        ndk {
            abiFilters  "armeabi-v7a","x86"
        }
    }
 
}
```

Issues
--------
无

Version
--------
1.0.0  版本创建<br>
