package me.zjl.boot.container;

import me.zjl.boot.route.DelegateRoute;
import me.zjl.boot.route.DelegateWork;

import java.lang.annotation.Annotation;
import java.util.List;

/*
 * 可注册的组件容器
 *
 * 该类主要作用：
 * 1. 对需要加载的目标组件进行注册
 * 2. 启动容器
 *
 * note: 扫描加载组件时，避免触发类的初始化
 *
 * created by zjl on 2019/2/26
 */
public interface Container {

    void register(Class<? extends Annotation>... annotatedClasses);

    void scan(Class<?> primarySource);

    void start();

    List<DelegateRoute> getRoutes();

    List<DelegateWork> getWorks();

    void deploy();
}
