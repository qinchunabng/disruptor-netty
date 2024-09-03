package com.qin.distruptor;

import com.lmax.disruptor.WorkHandler;
import com.qin.entity.TranslatorDataWrapper;

public abstract class MessageConsumer implements WorkHandler<TranslatorDataWrapper> {

    protected String consumerId;

    public MessageConsumer(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
