package com.dpzx.route_compiler;

import java.util.Map;

/**
 * Create by xuqunxing on  2020/10/14
 */
public interface IRoute {

    /**
     * 模块下的路由集合
     *
     * @param routes
     */
    void loadInto(Map<String, RouteMeta> routes);
}