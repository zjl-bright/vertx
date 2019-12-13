package me.zjl.boot;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import me.zjl.boot.constant.VertxBootConst;
import me.zjl.boot.container.DefaultAnnotationContainer;
import me.zjl.boot.utils.SharedReference;

import java.util.*;
import java.util.function.Consumer;

/**
 * created by zjl on 2019/8/28
 */
public class VertxBoot implements BootHooks {

    private final Vertx vertx;

    private Class<?> primarySource;

    //hooks
    private Consumer<VertxBoot> beforeStartContainerHook;
    private Consumer<VertxBoot> afterStartContainerHook;
    private Consumer<VertxBoot> beforeDeployedHook;
    private Consumer<VertxBoot> afterDeployedHook;
    private Runnable afterStartHook;

    public static void run(Vertx vertx, Class<?> primarySource, String... args) {
        new VertxBoot(vertx, primarySource).run(args);
    }

    public VertxBoot(Vertx vertx, Class<?> primarySource) {
        if(Objects.isNull(vertx)){
            throw new RuntimeException("vertx must not be null");
        }
        if(Objects.isNull(primarySource)){
            throw new RuntimeException("primarySources must not be null");
        }
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");

        this.primarySource = primarySource;
        this.vertx = vertx;
    }

    public void run(String[] args) {
        prepareContainer(args);
        refreshContainer();
    }

    //注册扫描注解
    private void prepareContainer(String[] args){
        //创建容器
        DefaultAnnotationContainer container = new DefaultAnnotationContainer(vertx, primarySource, args);
        //将container, vertxBoot设置到SharedData中
        LocalMap<String, SharedReference<?>> startMap = vertx.sharedData().getLocalMap(VertxBootConst.Key_Vertx_Start);
        startMap.put(VertxBootConst.Key_Container, new SharedReference<>(container));
        startMap.put(VertxBootConst.Key_Vertx_Boot, new SharedReference<>(this));

        container.start();
        container.deploy();
    }

    //反射注解,缓存反射结果
    private void refreshContainer(){

    }

    @Override
    public synchronized VertxBoot beforeStartContainerHook(Consumer<VertxBoot> hook) {
        this.beforeStartContainerHook = hook;
        return this;
    }

    @Override
    public synchronized VertxBoot afterStartContainerHook(Consumer<VertxBoot> hook) {
        this.afterStartContainerHook = hook;
        return this;
    }

    @Override
    public synchronized VertxBoot beforeDeployedHook(Consumer<VertxBoot> hook) {
        this.beforeDeployedHook = hook;
        return this;
    }

    @Override
    public synchronized VertxBoot afterDeployedHook(Consumer<VertxBoot> hook) {
        this.afterDeployedHook = hook;
        return this;
    }

    @Override
    public synchronized VertxBoot afterStartHook(Runnable hook) {
        this.afterStartHook = hook;
        return this;
    }
}
