package me.zjl.boot.json;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * created by zjl on 2018/9/11
 */
public class ImmutableDeliveryOptions extends DeliveryOptions {

    public ImmutableDeliveryOptions(String codecName) {
        super();
        super.setCodecName(codecName);
    }

    /**
     *
     * @param codecName code唯一标识
     * @param timeout the timeout value, in ms.
     */
    public ImmutableDeliveryOptions(String codecName, long timeout) {
        super();
        super.setCodecName(codecName);
        super.setSendTimeout(timeout);
    }

    @Override
    public DeliveryOptions setSendTimeout(long timeout) {
        throw new UnsupportedOperationException("immutable DeliveryOptions.");
    }


    @Override
    public DeliveryOptions setCodecName(String codecName) {
        throw new UnsupportedOperationException("immutable DeliveryOptions.");
    }

    @Override
    public DeliveryOptions addHeader(String key, String value) {
        throw new UnsupportedOperationException("immutable DeliveryOptions.");
    }

    @Override
    public DeliveryOptions setHeaders(MultiMap headers) {
        throw new UnsupportedOperationException("immutable DeliveryOptions.");
    }
}