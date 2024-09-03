package com.qin.client;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.qin.codec.MarshallingCodecFactory;
import com.qin.distruptor.MessageConsumer;
import com.qin.distruptor.RingBufferWorkerPoolFactory;
import com.qin.entity.TranslatorData;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8765;

    private Channel channel;

    private EventLoopGroup workGroup = new NioEventLoopGroup();

    private ChannelFuture future;

    public static void main(String[] args) {
        MessageConsumer[] consumers = new MessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerClientImpl("code:clientId:" + i);
            consumers[i] = messageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024 * 1024,
                new BlockingWaitStrategy(),
                consumers);
        NettyClient client = new NettyClient();
        client.connect(HOST, PORT);
        client.sendData();
    }

    public void connect(String host, int port){
        //注意Client与Server端的区别
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    //表示缓冲区动态调配（自适应）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    //缓冲区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline()
                                    .addLast(MarshallingCodecFactory.buildMarshallingDecoder())
                                    .addLast(MarshallingCodecFactory.buildMarshallingEncoder())
                                    .addLast(new ClientHandler());

                        }
                    });
            //绑定端口
            this.future = bootstrap.connect(host, port).sync();
            System.out.println("Client connected...");
            this.channel = future.channel();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void sendData(){
        for (int i = 0; i < 10; i++) {
            TranslatorData request = new TranslatorData();
            request.setId("" + i);
            request.setName("请求消息名称" + i);
            request.setMessage("请求消息内容" + i);
            this.channel.writeAndFlush(request);
        }
    }

    public void close() throws Exception{
        future.channel().closeFuture().sync();
        workGroup.shutdownGracefully();
        System.out.println("Server shutdown...");
    }
}
