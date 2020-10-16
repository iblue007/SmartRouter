package com.dpzx.router_core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import dalvik.system.DexFile;

/**
 * Create by xuqunxing on  2020/10/16
 */
public class ClassUtil {
    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";

    //通过dex来获取Router$$App$$app和Router$$App$$my文件
    public static Set<String> getFileNameByDexWithPackageName(Context mContext, final String packageOfGenerateFile) throws InterruptedException {
        final Set<String> classNames = new HashSet<>();
        List<String> apkSourcePath = getApkSourcePath(mContext);
        final CountDownLatch countDownLatch = new CountDownLatch(apkSourcePath.size());
        try {
            if (apkSourcePath != null && apkSourcePath.size() > 0) {
                for (final String path : apkSourcePath) {
                    DefaultPoolExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            DexFile dexFile = null;
                            try {
                                if (path.endsWith(EXTRACTED_SUFFIX)) {
                                    //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                                    dexFile = DexFile.loadDex(path, path + ".tmp", 0);
                                } else {
                                    dexFile = new DexFile(path);
                                }
                                Enumeration<String> entries = dexFile.entries();
                                //遍历dex中的类
                                while (entries.hasMoreElements()) {
                                    String className = entries.nextElement();
                                    if (className.startsWith(packageOfGenerateFile)) {
                                        classNames.add(className);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (null != dexFile) {
                                    try {
                                        dexFile.close();
                                    } catch (Throwable ignore) {
                                    }
                                }
                            }
                            countDownLatch.countDown();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.await();
        return classNames;
    }

    /**
     * 获得程序里所有的apk（instant run 会产生很多的spilt apk）
     */
    private static List<String> getApkSourcePath(Context mContext) {
        List<String> sourcePath = new ArrayList<>();
        try {
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), 0);
            sourcePath.add(applicationInfo.sourceDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != applicationInfo.splitSourceDirs) {
                    sourcePath.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sourcePath;
    }
}