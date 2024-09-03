package com.qin.client;

import com.qin.distruptor.MessageConsumer;
import com.qin.entity.TranslatorData;
import com.qin.entity.TranslatorDataWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerClientImpl extends MessageConsumer {

    public MessageConsumerClientImpl(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWrapper event) throws Exception {
        TranslatorData response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //业务逻辑处理
        try {
            System.out.println("Server端：id=" + response.getId());
        }finally {
            //需要释放回收内存到内存池
            ReferenceCountUtil.release(response);
        }
    }
}
