package me.zjl.boot.route;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-20
 * @Version: 1.0
 */
public class DelegateWork implements Handler<Vertx> {

    private String path;

    private Method method;

    private Object o;

    public DelegateWork(String path, Method method, Object o){
        this.path = path;
        this.method = method;
        this.o = o;
    }

    @Override
    public void handle(Vertx vertx){
        vertx.eventBus().consumer(path, message -> {
            Object body = message.body();
            try {
                Object res = method.invoke(o, body);
                message.reply(res);
            } catch (Exception e) {

            }
        });
    }

    public String getPath(){
        return path;
    }

    public Method getMethod(){
        return method;
    }

    public DelegateWork setPath(String path){
        this.path = path;
        return this;
    }

    public DelegateWork setMethod(Method method){
        this.method = method;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DelegateWork)){
            return false;
        }
        DelegateWork target = (DelegateWork)obj;
        if(Objects.equals(path, target.path)){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
