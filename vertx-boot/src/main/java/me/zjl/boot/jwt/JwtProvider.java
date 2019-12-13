package me.zjl.boot.jwt;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-15
 * @Version: 1.0
 */
public class JwtProvider {

    private JWTAuth provider;

    public JwtProvider(Vertx vertx){
        provider = JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setPublicKey("keyboard cat")
                        .setSymmetric(true)));
    }

    public JWTAuth getProvider(){
        return provider;
    }

    public String createToken(String userId){
        return provider.generateToken(new JsonObject().put("userId", userId), new JWTOptions());
    }
}
