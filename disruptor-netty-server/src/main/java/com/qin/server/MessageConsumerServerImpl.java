package com.qin.server;

import com.qin.distruptor.MessageConsumer;
import com.qin.entity.TranslatorData;
import com.qin.entity.TranslatorDataWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerServerImpl extends MessageConsumer {

    public MessageConsumerServerImpl(String consumerId) {
        super(consumerId);
    }

    public void onEvent(TranslatorDataWrapper event) throws Exception {
        TranslatorData request = event.getData();
        try {
            ChannelHandlerContext ctx = event.getCtx();
            //1.业务处理逻辑
            System.out.println("Server端：id=" + request.getId()
                    + ", name=" + request.getName()
                    + ", message=" + request.getMessage());

            //2.发送响应信息
            TranslatorData response = new TranslatorData();
            response.setId("resp: " + request.getId());
            response.setName("resp: " + request.getName());
            response.setMessage("resp: " + request.getMessage());
            //写出response响应消息
            ctx.writeAndFlush(response);
        }finally {
            ReferenceCountUtil.release(request);
        }
    }
}
