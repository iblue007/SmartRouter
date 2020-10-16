package com.dpzx.route_compiler;

/**
 * 路由跳转的类型
 * Create by xuqunxing on  2020/10/14
 */
public enum RouteType {
    ACTIVITY(0, "android.app.Activity"),
    UNKNOW(-1, "unkown route type");
    int id;
    String className;

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public RouteType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RouteType setClassName(String className) {
        this.className = className;
        return this;
    }
}
