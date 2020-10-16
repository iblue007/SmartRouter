package com.dpzx.router_core;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;

import com.dpzx.route_compiler.IRoute;
import com.dpzx.route_compiler.RouteMeta;
import com.dpzx.route_compiler.consts;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * Create by xuqunxing on  2020/10/16
 */
public class Router {

    private Map<String, RouteMeta> routeMetaMap = new ArrayMap<>();
    private static Router instance;
    private Context mContext;
    private String path;

    public static Router getInstance() {
        synchronized (Router.class) {
            if (instance == null) {
                instance = new Router();
            }
        }
        return instance;
    }

    public static void init(Context context) {
        try {
            instance.mContext = context;
            Set<String> fileNameByDexWithPackageName = ClassUtil.getFileNameByDexWithPackageName(instance.mContext, consts.PACKAGE_OF_GENERATE_FILE);
            if (fileNameByDexWithPackageName != null && fileNameByDexWithPackageName.size() > 0) {
                for (String className : fileNameByDexWithPackageName) {
                    ((IRoute) (Class.forName(className).getConstructor().newInstance())).loadInto(instance.routeMetaMap);
                }
            }
            Log.e("======", "======size:" + instance.routeMetaMap.size());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Router buile(String path1) {
        path = path1;
        return this;
    }

    public void navigetion(Context context) {
        RouteMeta routeMeta = instance.routeMetaMap.get(instance.path);
        if (routeMeta != null) {
            context.startActivity(new Intent(context, routeMeta.getClazz()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}