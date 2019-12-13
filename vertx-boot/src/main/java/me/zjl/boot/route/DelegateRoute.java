package me.zjl.boot.route;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import me.zjl.boot.constant.MediaType;
import me.zjl.boot.constant.VertxBootConst;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DelegateRoute implements Handler<Router>{

    private String path;

    private HttpMethod httpMethod;

    private String consumes;

    private String produces;

    private Method method;

    private Object o;

    private Boolean noProtected;

    public DelegateRoute(String path, HttpMethod httpMethod, String consumes,
                         String produces, Method method, Object o, Boolean noProtected){
        this.path = path;
        this.httpMethod = httpMethod;
        this.consumes = consumes;
        this.produces = produces;
        this.method = method;
        this.o = o;
        this.noProtected = noProtected;
    }

    @Override
    public void handle(Router router){
        Route route;
        if(noProtected){
            route = router.route(httpMethod, path);
        }else{
            route = router.route(httpMethod, VertxBootConst.Protected_Path + path);
        }

        //参数为空时，不能当做json, 所以这时候不能设置consumes
        if(!Objects.equals("", consumes)){
            route = route.consumes(consumes);
        }
        route.produces(produces)
                .handler(ctx -> {
                    try {
                        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                        List params = new ArrayList();
                        MultiMap map = ctx.request().params();

                        Parameter[] parameters = method.getParameters();
                        for(int index = 0; index < parameters.length; index++){
                            Class type = parameters[index].getType();
                            if(type == RoutingContext.class){
                                params.add(ctx);
                            }else if(type == MultiMap.class){
                                params.add(map);
                            }else if(type == JsonObject.class){
                                JsonObject jsonObject = new JsonObject();
                                String body = ctx.getBodyAsString();
                                if(Objects.nonNull(body) && !Objects.equals("", body)){
                                    if(body.indexOf("query") > -1){
                                        JsonObject ss = ctx.getBodyAsJson();
                                        String query = ss.getString("query");
                                        jsonObject = new JsonObject(query);
                                    }else{
                                        jsonObject = ctx.getBodyAsJson();
                                    }
                                }
                                params.add(jsonObject);
                            }else if(type == String.class){
                                String name = parameters[index].getName();
                                params.add(map.get(name));
                            }
                        }
                        method.invoke(o, params.toArray());
                    } catch (Exception e) {
                        ctx.fail(e);
                    }
                });
    }

    public String getPath(){
        return path;
    }

    public HttpMethod getHttpMethod(){
        return httpMethod;
    }

    public String getConsumes(){
        return consumes;
    }

    public String getProduces(){
        return produces;
    }

    public Method getMethod(){
        return method;
    }

    public DelegateRoute setPath(String path){
        this.path = path;
        return this;
    }

    public DelegateRoute setHttpMethod(HttpMethod httpMethod){
        this.httpMethod = httpMethod;
        return this;
    }

    public DelegateRoute setConsumes(String consumes){
        this.consumes = consumes;
        return this;
    }

    public DelegateRoute setProduces(String produces){
        this.produces = produces;
        return this;
    }

    public DelegateRoute setMethod(Method method){
        this.method = method;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DelegateRoute)){
            return false;
        }
        DelegateRoute target = (DelegateRoute)obj;
        if(Objects.equals(path, target.path) && httpMethod == target.httpMethod){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, httpMethod);
    }
}
