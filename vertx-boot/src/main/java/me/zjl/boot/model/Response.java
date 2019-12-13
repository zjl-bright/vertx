package me.zjl.boot.model;

import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-10-17
 * @Version: 1.0
 */
public class Response<T> implements Serializable {

  private static final long serialVersionUID = -750644833749014619L;

  //状态码
  private Integer code;

  //消息
  private String message;

  //返回结果集
  private T result;

  //是否成功
  private boolean success;

  public Response() {}

  public Integer getCode(){
    return code;
  }

  public String getMessage(){
    return message;
  }

  public T getResult(){
    return result;
  }

  public boolean getSuccess() {
    return this.success;
  }

  public Response setCode(Integer code){
    this.code = code;
    return this;
  }

  public Response setMessage(String message){
    this.message = message;
    return this;
  }

  public Response setResult(T result){
    this.result = result;
    return this;
  }

  public Response setSuccess(boolean success){
    this.success = success;
    return this;
  }

  private Response(ResponseCode responseCode) {
    this.code = responseCode.code();
    this.message = responseCode.message();
  }

  public static JsonObject ok() {
    Response resp = new Response(ResponseCode.SUCCESS).setSuccess(true);
    return JsonObject.mapFrom(resp);
  }

  public static <T> JsonObject ok(T data) {
    Response resp = new Response(ResponseCode.SUCCESS).setResult(data).setSuccess(true);
    return JsonObject.mapFrom(resp);
  }

  public static JsonObject fail401(String message) {
    return code(ResponseCode.LOST, message);
  }

  public static JsonObject fail(String message) {
    return code(ResponseCode.TIP_ERROR, message);
  }

  public static JsonObject fail500() {
    return code(ResponseCode.UNKNOWN_ERROR, null);
  }

  public static JsonObject fail500(String message) {
    return code(ResponseCode.UNKNOWN_ERROR, message);
  }

  private static JsonObject code(ResponseCode responseCode, String message){
    Response resp = new Response(responseCode).setSuccess(false);
    if(Objects.isNull(message) || Objects.equals("", message)){
      return JsonObject.mapFrom(resp);
    }else{
      return JsonObject.mapFrom(resp.setMessage(message));
    }
  }
}
