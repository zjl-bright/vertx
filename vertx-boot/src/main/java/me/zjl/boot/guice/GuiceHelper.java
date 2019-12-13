package me.zjl.boot.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.Vertx;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-06
 * @Version: 1.0
 */
public class GuiceHelper {

    public static Injector guice(Vertx vertx, String[] args){
        return Guice.createInjector(new VetxModule(vertx), new PropertiesModule(args));
    }

    public static void guicce(){

    }
}
