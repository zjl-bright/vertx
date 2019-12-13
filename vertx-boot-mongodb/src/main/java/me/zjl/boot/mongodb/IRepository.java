package me.zjl.boot.mongodb;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;

import java.util.List;

public interface IRepository {

    //有ID就更新文档，没ID就插入文档
    void save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

    //插入文档，有ID会报错
    void insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

    //更新文档
    void updates(String collection, JsonObject query, JsonObject document, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler);

    //更换文档
    void replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler);

    //只匹配返回的第一条数据
    void findOne(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler);

    //普通查询
    void find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    //只排序, 针对小型数据集
    void findWithSort(String collection, JsonObject query, JsonObject sort, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    //排序且分页, 针对小型数据集
    void findWithSortAndPage(String collection, JsonObject query, JsonObject sort, int skip, int limit, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    //查询大型数据集，handler一条一条的接受
    void findBath(String collection, JsonObject query, Handler<JsonObject> handler);

    //删除
    void remove(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler);

    //计量
    void count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);
}
