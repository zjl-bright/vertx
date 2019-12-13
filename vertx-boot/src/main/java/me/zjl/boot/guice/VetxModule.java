package me.zjl.boot.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.vertx.core.Vertx;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-06
 * @Version: 1.0
 */
public class VetxModule extends AbstractModule {

    private final Vertx vertx;

    VetxModule(Vertx vertx){
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    Vertx provideVetx() {
        return vertx;
    }
}
