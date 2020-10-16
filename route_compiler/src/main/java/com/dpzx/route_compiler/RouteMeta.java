package com.dpzx.route_compiler;

/**
 * Create by xuqunxing on  2020/10/14
 */
public class RouteMeta {
    //路由名称
    private String path;
    //路由的类型（目前只支持activity）
    private RouteType routeType;
    //需要注解的activity类
    private Class clazz;

    public RouteMeta(String path, RouteType routeType) {
        this.path = path;
        this.routeType = routeType;
    }

    public RouteMeta(String path, RouteType routeType, Class clazz) {
        this.path = path;
        this.routeType = routeType;
        this.clazz = clazz;
    }

    public static RouteMeta build(String path, RouteType routeType, Class clazz) {
        return new RouteMeta(path, routeType, clazz);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}