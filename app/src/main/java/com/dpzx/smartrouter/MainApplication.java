package com.dpzx.smartrouter;

import android.app.Application;

import com.dpzx.router_core.Router;

/**
 * Create by xuqunxing on  2020/10/16
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.getInstance().init(this);
    }
}