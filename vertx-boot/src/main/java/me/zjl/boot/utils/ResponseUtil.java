package me.zjl.boot.utils;

import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.ext.web.RoutingContext;
import me.zjl.boot.model.Response;

import java.util.Objects;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-10-28
 * @Version: 1.0
 */
public class ResponseUtil {

    public static Boolean endIfParamBlank(final RoutingContext context, Object param, final String tip) {
        if(param instanceof String){
            if(Strings.isNullOrEmpty(param.toString())){
                context.response().end(Response.ok(tip).encodePrettily());
                return true;
            }
        }else{
            if(Objects.isNull(param)){
                context.response().end(Response.ok(tip).encodePrettily());
                return true;
            }
        }
        return false;
    }

    public static Boolean endIfExpressionTrue(final RoutingContext context, Boolean flag, final String tip) {
        if(flag){
            context.response().end(Response.ok(tip).encodePrettily());
            return true;
        }
        return false;
    }

    public static void end(final RoutingContext context, Object res) {
//        context.put("res", res);
//        context.next();
        if(res instanceof AsyncResult){
            AsyncResult asy = (AsyncResult)res;
            if(asy.succeeded()){
                context.response().end(Response.ok(asy.result()).encodePrettily());
            }else{
                context.fail(asy.cause());
            }
        }else{
            context.response().end(Response.ok(res).encodePrettily());
        }
    }
}
