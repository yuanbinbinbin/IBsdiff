package com.yb.lib.bsdiff;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class BsdiffUtil {

    private BsdiffUtil() {
    }

    //region merge
    static {
        System.loadLibrary("bsdiff-lib");
    }

    public static boolean merge(String oldApkPat, String patchPath, String outApkPath, boolean openLog) {
        return (new BsdiffUtil().bspatch(oldApkPat, patchPath, outApkPath, openLog));
    }

    native boolean bspatch(String oldApkPat, String patchPath, String outApkPath, boolean openLog);
    //endregion

    //region hash
    public static String hash(String path) {
        return MD5Util.getFileMD5(new File(path));
    }

    private static class MD5Util {


        private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};

        /**
         * Get MD5 of a file (lower case)
         *
         * @return empty string if I/O error when get MD5
         */
        public static String getFileMD5(File file) {

            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                FileChannel ch = in.getChannel();
                return MD5(ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
            } catch (FileNotFoundException e) {
                return "";
            } catch (IOException e) {
                return "";
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // 关闭流产生的错误一般都可以忽略
                    }
                }
            }

        }

        /**
         * 计算MD5校验
         *
         * @param buffer
         * @return 空串，如果无法获得 MessageDigest实例
         */

        private static String MD5(ByteBuffer buffer) {
            String s = "";
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(buffer);
                byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
                // 用字节表示就是 16 个字节
                char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
                // 所以表示成 16 进制需要 32 个字符
                int k = 0; // 表示转换结果中对应的字符位置
                for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                    // 转换成 16 进制字符的转换
                    byte byte0 = tmp[i]; // 取第 i 个字节
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>>,
                    // 逻辑右移，将符号位一起右移
                    str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
                }
                s = new String(str); // 换后的结果转换为字符串

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return s;
        }

    }
    //endregion

    //region old apk path
    public static String getOldApkPath(Context context) {
        if (context == null) {
            return "";
        }
        return context.getApplicationContext().getApplicationInfo().sourceDir;
    }
    //endregion
}
