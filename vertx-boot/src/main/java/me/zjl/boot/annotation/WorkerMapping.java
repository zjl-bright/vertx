package me.zjl.boot.annotation;

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
public @interface WorkerMapping {

    /**
     * 请求路径
     *
     */
    String value() default "";

    String type() default "String";
}
