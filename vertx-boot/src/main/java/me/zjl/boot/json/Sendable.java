package me.zjl.boot.json;

import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.impl.ClusterSerializable;

/**
 * eventBus send json的时候，会发生一次copy， 而Sendable的实现类是避免发生这次copy
 *
 * created by zjl on 2018/9/1
 */
public interface Sendable extends ClusterSerializable {

    String JsonArray_Key = "_jsonArray";


    /**
     *
     * @return true: sendable的实现类已经发送 对象将变得不可变  false: mutable
     */
    boolean isSend();

    /**
     * 设置该Sendable已经经过eventBus send,  那么之后，该对象将 immutable
     *
     * 由实现类保证 eventBus send之后的immutable
     *
     * note: 该方法一般由内部调用， 调用者不需要处理该方法
     *
     */
    void send();

    /**
     *
     * @return 如果本身是一个jsonArray，用json包装， key =  {@link #JsonArray_Key} , value = jsonArray
     */
    JsonObject toJson();

}
