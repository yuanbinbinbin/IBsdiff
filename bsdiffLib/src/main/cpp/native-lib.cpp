#include <jni.h>
#include <android/log.h>

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "========= BSDiff Error =========   ", __VA_ARGS__)


extern "C" {
extern int p_main(int argc, char *argv[]);
}
extern "C"
JNIEXPORT bool JNICALL
Java_com_yb_lib_bsdiff_BsdiffUtil_bspatch(JNIEnv *env, jobject instance, jstring oldapk_,
                                          jstring patch_, jstring output_, bool openLog) {
    try {
        const char *oldapk = env->GetStringUTFChars(oldapk_, 0);
        const char *patch = env->GetStringUTFChars(patch_, 0);
        const char *output = env->GetStringUTFChars(output_, 0);
        if (openLog) {
            LOGE("old path: %s", oldapk);
            LOGE("patch path: %s", patch);
            LOGE("new  path: %s", output);
        }

        char *argv[4] = {"", const_cast<char *>(oldapk), const_cast<char *>(output),
                         const_cast<char *>(patch)};
        int result = p_main(4, argv);

        env->ReleaseStringUTFChars(oldapk_, oldapk);
        env->ReleaseStringUTFChars(patch_, patch);
        env->ReleaseStringUTFChars(output_, output);

        if (result != 0 && openLog) {
            if (result == 1) {
                LOGE("入参不正确");
            } else if (2 == result) {
                LOGE("patch 文件找不到");
            } else if (3 == result) {
                LOGE("patch 文件有问题");
            } else if (4 == result) {
                LOGE("old apk  文件有问题");
            } else if (5 == result) {
                LOGE("内存申请失败");
            } else if (6 == result) {
                LOGE("new apk 生成失败，请检查sd卡权限、文件路径等");
            }
            return false;
        }
    } catch (...) {
        if (openLog) {
            LOGE("%s", "合成失败 exception");
        }
        return false;
    }
    return true;
}