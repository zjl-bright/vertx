package me.zjl.boot;

import io.vertx.core.Verticle;

import java.util.function.Consumer;

/**
 * 启动vertxBoot的钩子方法
 *
 * 执行顺序：
 *
 *    接着开始组件的扫描，然后执行
 *    2. {@link #beforeStartContainerHook(Consumer)}
 *
 *    组件加载完成之后，然后执行
 *    3. {@link #afterStartContainerHook(Consumer)}
 *
 *    然后从组件容器中获取{@link Verticle}组件，执行部署，部署完成之后，执行
 *    4. {@link #afterDeployedHook(Consumer)}
 *
 *    start方法执行完之后， 执行
 *    5. {@link #afterStartHook(Runnable)}
 *
 * created by zjl on 2019/3/14
 */
public interface BootHooks {

    /**
     * 在执行加载组件之前， 执行 钩子方法
     *
     * @param hook 钩子方法
     * @return this
     */
    BootHooks beforeStartContainerHook(Consumer<VertxBoot> hook);


    /**
     * 在执行加载组件之后， 执行 钩子方法
     *
     * @param hook 钩子方法
     * @return this
     */
    BootHooks afterStartContainerHook(Consumer<VertxBoot> hook);

    /**
     * 在执行部署{@link Verticle}之后，  执行 钩子方法
     *
     * @param hook 钩子方法
     * @return this
     */
    BootHooks beforeDeployedHook(Consumer<VertxBoot> hook);

    /**
     * 在执行部署{@link Verticle}之后，  执行 钩子方法
     *
     * @param hook 钩子方法
     * @return this
     */
    BootHooks afterDeployedHook(Consumer<VertxBoot> hook);


    /**
     * 在执行完start方法之后， 执行钩子方法
     *
     * @param hook 钩子方法
     * @return this
     */
    BootHooks afterStartHook(Runnable hook);

}
