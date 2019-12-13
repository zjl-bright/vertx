package me.zjl.boot.mongodb;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @Auther: zhaojl@hshbao.com
 * @Date: 2019-09-19
 * @Version: 1.0
 */
@Singleton
public class MongoRepository implements IRepository{

  private final static Logger log = LoggerFactory.getLogger(MongoRepository.class);

  private final MongoClient mongoClient;

  private final UpdateOptions options;

  @Inject
  public MongoRepository(Vertx vertx, @Named("mongodb.connection_string")String connection_string){
    this.mongoClient = MongoClient.createShared(vertx, new JsonObject().put("connection_string", connection_string));
    this.options = new UpdateOptions().setMulti(true);
  }

  //有ID就更新文档，没ID就插入文档
  @Override
  public void save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler){
    mongoClient.save(collection, document, resultHandler);
  }

  //插入文档，有ID会报错
  @Override
  public void insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler){
    mongoClient.insert(collection, document, resultHandler);
  }

  //更新文档
  @Override
  public void updates(String collection, JsonObject query, JsonObject document, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler){
    JsonObject update = new JsonObject().put("$set", document);
    mongoClient.updateCollectionWithOptions(collection, query, update, options, resultHandler);
  }

  //更换文档
  @Override
  public void replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler){
    mongoClient.replaceDocuments(collection, query, replace,  resultHandler);
  }

  // 一条语句执行多个UPDATE, REPLACE, INSERT, DELETE; 批量操作
  public void insertAll(String collection, JsonArray jsonArray, Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler){
    List<BulkOperation> operations = new ArrayList(jsonArray.size());

    int count = jsonArray.size();
    for(int i = 0; i < count; i++){
      BulkOperation operation = BulkOperation.createInsert(jsonArray.getJsonObject(i));
      operations.add(operation);
    }
    mongoClient.bulkWrite(collection, operations, resultHandler);
  }

  //只匹配返回的第一条数据
  @Override
  public void findOne(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler){
    mongoClient.findOne(collection, query, null, resultHandler);
  }

  //普通查询
  @Override
  public void find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler){
    mongoClient.find(collection, query, resultHandler);
  }

  //只排序, 针对小型数据集
  @Override
  public void findWithSort(String collection, JsonObject query, JsonObject sort, Handler<AsyncResult<List<JsonObject>>> resultHandler){
    FindOptions findOptions = new FindOptions().setSort(sort);
    mongoClient.findWithOptions(collection, query, findOptions, resultHandler);
  }

  //排序且分页, 针对小型数据集
  @Override
  public void findWithSortAndPage(String collection, JsonObject query, JsonObject sort, int skip, int limit, Handler<AsyncResult<List<JsonObject>>> resultHandler){
    FindOptions findOptions = new FindOptions().setSort(sort).setSkip(skip).setLimit(limit);
    mongoClient.findWithOptions(collection, query, findOptions, resultHandler);
  }

  //查询大型数据集，handler一条一条的接受
  @Override
  public void findBath(String collection, JsonObject query, Handler<JsonObject> handler){
    FindOptions options = new FindOptions().setBatchSize(100);
    mongoClient.findBatchWithOptions(collection, query, options)
      .exceptionHandler(throwable -> Throwables.getStackTraceAsString(throwable))
      .endHandler(v -> System.out.println("End of research"))
      .handler(handler);
  }

  //删除
  @Override
  public void remove(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler){
    mongoClient.removeDocuments(collection, query, resultHandler);
  }

  //计量
  @Override
  public void count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler){
    mongoClient.count(collection, query, resultHandler);
  }
}
