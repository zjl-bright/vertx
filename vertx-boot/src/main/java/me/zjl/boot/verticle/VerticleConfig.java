package me.zjl.boot.verticle;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;

/**
 * 部署{@link Verticle}的配置。
 *
 * 由于Verticle的部署参数太多， 全部加到{@link Deploy}中会太臃肿，所以就抽离了一个接口，让Verticle的实现里继承。
 *
 * 注意：该接口的实现类必须用{@link Deploy}注解该才有效
 *
 * <code>
 *   public class DemoVerticle extends AbstractVerticle implements VerticleConfig {
 *
 *         \@Inject
 *         private Test container;
 *
 *         \@Value("person.name")
 *         private String name;
 *
 *         public void start() {
 *         }
 *
 *         DeploymentOptions options() {
 *             //这里返回 部署参数， 从而获取最灵活的部署
 *         }
 *
 *         Class {@code <} ? extends Handler {@code <} AsyncResult {@code <} String {@code >>>} deployedHandlerClass()
 *              //这里返回部署完成之后的handler class
 *         }
 *     }
 * </code>
 *
 * created by zjl on 2018/8/22
 */
public interface VerticleConfig {

    /**
     * 部署verticle的参数
     *
     * {@link Deploy}中值 != 默认值 将会设置到options中
     *
     * note: 最好确认该方法是幂等的。
     *
     * @return 部署参数
     */
    default DeploymentOptions options() {
        return new DeploymentOptions();
    }

    /**
     * 确保该verticle是单实例的 即{@link DeploymentOptions#getInstances()} = 1
     *
     * @return true: verticle必须单利，如果{@link Deploy#instances()} != 1 或 {@link #options()}中的instances != null 报错
     *         false: 允许多利的
     */
    default boolean requireSingle() {
        return false ;
    }

    /**
     * 部署verticle完成之后的回调
     *
     * {@link io.vertx.core.Vertx#deployVerticle(String, Handler)} 中的Handler
     *
     * @return handler
     */
    default Handler<AsyncResult<String>> deployedHandler() {
        return null;
    }

}
