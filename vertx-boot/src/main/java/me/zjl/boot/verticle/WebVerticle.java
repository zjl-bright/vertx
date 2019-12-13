package me.zjl.boot.verticle;

import com.google.common.base.Throwables;
import io.vertx.core.*;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.StaticHandler;
import me.zjl.boot.constant.MediaType;
import me.zjl.boot.container.Container;
import me.zjl.boot.jwt.JwtProvider;
import me.zjl.boot.model.Response;
import me.zjl.boot.route.DelegateRoute;
import me.zjl.boot.utils.SharedReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

import static me.zjl.boot.constant.VertxBootConst.Key_Container;
import static me.zjl.boot.constant.VertxBootConst.Key_Vertx_Start;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-02
 * @Version: 1.0
 */
public class WebVerticle extends AbstractVerticle {

    private final static Logger log = LoggerFactory.getLogger(WebVerticle.class);

    private JsonObject config;

    @Inject
    private JwtProvider jwtProvider;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        config = config();
        jwtProvider = new JwtProvider(vertx);
    }

    /**
     * 在这里，可以做全局的router设置。 例如， {@link BodyHandler} 等一些全局性的过滤
     *
     * @param router 主路由器
     */
    protected void generalRouter(Router router) {
        //静态文件优先判断，直接返回
        router.routeWithRegex(HttpMethod.GET, ".?(.htm|.ico|.css|.js|.text|.png|.jpg|.gif|.jpeg|.mp3|.avi)")
                .handler(StaticHandler.create().setCacheEntryTimeout(1000 * 60 * 60 * 24));
//      mainRouter.route().handler(SessionHandler.create(LocalSessionStore.create(vertx, "myapp3.sessionmap", 10000)));
        router.route().handler(routingContext -> {
            routingContext.response()
                    .setChunked(true)
                    .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
            routingContext.next();
        });
        router.route().failureHandler(this::failure);
        router.errorHandler(404, this::failure404);

//        router.route(VertxBootConst.Protected_Path + VertxBootConst.All_Path).handler(JWTAuthHandler.create(jwtProvider.getProvider()));
        router.route().handler(BodyHandler.create().setBodyLimit(10 * 1048576L)); //10M
//        router.route().last().handler(this::success);
    }

//    private void success(RoutingContext ctx){
//        ctx.response().end(Response.ok(ctx.get("res")).encodePrettily());
//    }

    private void failure(RoutingContext frc){
        if(Objects.nonNull(frc.failure())){
            log.error("failureHandler, case by : {}", Throwables.getStackTraceAsString(frc.failure()));
        }
        frc.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .end(Response.fail("发生了一些未知异常！").encodePrettily());
    }

    private void failure404(RoutingContext frc){
        frc.response().putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .end(Response.fail("界面找不到").encodePrettily());
    }

    @Override
    public void start(Promise<Void> startPromise){
        Router mainRouter = Router.router(vertx);
        generalRouter(mainRouter);
        LocalMap<String, SharedReference<?>> map = vertx.sharedData().getLocalMap(Key_Vertx_Start);
        @SuppressWarnings("unchecked")
        SharedReference<Container> sharedRef = (SharedReference<Container>) map.get(Key_Container);
        Container container = sharedRef.ref;
        List<DelegateRoute> routes = container.getRoutes();
        routes.forEach(route -> {
            route.handle(mainRouter);
        });
        int port = config.getJsonObject("http").getInteger("port");
        vertx.createHttpServer().requestHandler(mainRouter).listen(port, http -> {
            if(http.succeeded()){
                log.info("HTTP server started on {}", port);
                startPromise.complete();
            }else{
                startPromise.fail(http.cause());
            }
        });
    }
}
