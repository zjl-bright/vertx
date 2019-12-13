package me.zjl.boot.annotation;

import io.vertx.core.http.HttpMethod;
import me.zjl.boot.constant.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-10-28
 * @Version: 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * 请求路径
     *
     */
    String value() default "";

    HttpMethod method() default HttpMethod.GET;

    String consumes() default "";

    String produces() default MediaType.APPLICATION_JSON_VALUE;
}
