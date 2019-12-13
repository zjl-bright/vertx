package me.zjl.boot.container;

import com.google.inject.Injector;
import io.vertx.core.Vertx;
import me.zjl.boot.annotation.RequestMapping;
import me.zjl.boot.annotation.WorkerMapping;
import me.zjl.boot.guice.GuiceHelper;
import me.zjl.boot.route.DelegateRoute;
import me.zjl.boot.route.DelegateWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by zjl on 2019/2/26
 */
public class DefaultAnnotationContainer extends AbstractLoadContainer {

    private final static Logger log = LoggerFactory.getLogger(DefaultAnnotationContainer.class);

    private final Vertx vertx;

    private final Injector injector;

    private final Class<?> primarySource;

    private AtomicBoolean started = new AtomicBoolean(false);

    private AtomicBoolean deployed = new AtomicBoolean(false);

    public DefaultAnnotationContainer(Vertx vertx, Class<?> primarySource, String[] args){
        this.vertx = vertx;
        this.injector = GuiceHelper.guice(vertx, args);
        this.primarySource = primarySource;
    }

    @Override
    public List<DelegateRoute> getRoutes(){
        return routes();
    }

    @Override
    public List<DelegateWork> getWorks(){
        return works();
    }

    @Override
    public void start() {
        register(RequestMapping.class, WorkerMapping.class);
        scan(primarySource);
        if(started.compareAndSet(false, true)) {
            log.info("container starting..............");
            load(injector);
            log.info("Container started successfully");
        }else{
            throw new RuntimeException("Container startup failed");
        }
    }

    @Override
    public void deploy(){
        if(deployed.compareAndSet(false, true)) {
            log.info("container deploying..............");
            deployVerticle(vertx);
            log.info("container deployed successfully");
        }else{
            throw new RuntimeException("Container deployed failed");
        }
    }
}
