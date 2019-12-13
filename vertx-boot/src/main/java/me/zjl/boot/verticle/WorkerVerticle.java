package me.zjl.boot.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.shareddata.LocalMap;
import me.zjl.boot.container.Container;
import me.zjl.boot.route.DelegateWork;
import me.zjl.boot.utils.SharedReference;

import java.util.List;

import static me.zjl.boot.constant.VertxBootConst.Key_Container;
import static me.zjl.boot.constant.VertxBootConst.Key_Vertx_Start;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-20
 * @Version: 1.0
 */
public class WorkerVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise){
        LocalMap<String, SharedReference<?>> map = vertx.sharedData().getLocalMap(Key_Vertx_Start);
        @SuppressWarnings("unchecked")
        SharedReference<Container> sharedRef = (SharedReference<Container>) map.get(Key_Container);
        Container container = sharedRef.ref;

        List<DelegateWork> works = container.getWorks();
        works.forEach(work -> {
            work.handle(vertx);
        });
    }
}
