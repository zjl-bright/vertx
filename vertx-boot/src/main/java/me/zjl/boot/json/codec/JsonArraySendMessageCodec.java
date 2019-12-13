package me.zjl.boot.json.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import me.zjl.boot.json.JsonArraySend;

/**
 * json array zero copy on eventBus send
 *
 * //TODO register on {@link io.vertx.core.eventbus.EventBus#registerDefaultCodec(Class, MessageCodec)}
 *
 * created by zjl on 2018/9/3
 */
public class JsonArraySendMessageCodec implements MessageCodec<JsonArraySend, JsonArraySend> {

    @Override
    public void encodeToWire(Buffer buffer, JsonArraySend array) {
        array.writeToBuffer(buffer);
        array.send();

    }

    @Override
    public JsonArraySend decodeFromWire(int pos, Buffer buffer) {
        JsonArraySend array = new JsonArraySend();
        array.readFromBuffer(pos, buffer);
        return array;
    }

    @Override
    public JsonArraySend transform(JsonArraySend array) {
        array.send();
        return array;
    }

    @Override
    public String name() {
        return JsonArraySend.Codec_Name;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
