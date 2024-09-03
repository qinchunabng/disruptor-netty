package com.qin.entity;

import io.netty.channel.ChannelHandlerContext;

public class TranslatorDataWrapper {

    private TranslatorData data;

    private ChannelHandlerContext ctx;

    public TranslatorData getData() {
        return data;
    }

    public void setData(TranslatorData data) {
        this.data = data;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
