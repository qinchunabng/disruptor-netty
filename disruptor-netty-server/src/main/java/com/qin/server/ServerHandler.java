package com.qin.server;

import com.qin.distruptor.MessageProducer;
import com.qin.distruptor.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.qin.entity.TranslatorData;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TranslatorData request = (TranslatorData) msg;
        //自己的应用服务器应该有一个ID生成规则
        String produceId ="code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(produceId);
        messageProducer.onData(request, ctx);
    }
}
