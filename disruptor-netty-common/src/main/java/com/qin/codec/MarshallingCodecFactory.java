package com.qin.codec;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingCodecFactory {

    /**
     * 创建JBoss Marshalling解码器MarshallingDecoder
     * @return
     */
    public static MarshallingDecoder buildMarshallingDecoder(){
        //通过Marshalling工具类使用river协议创建MarshallerFactory，
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("river");
        //创建MarshallingConfiguration对象，配置版本号为5
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(4);
        //根据marshallerFactory和configuration创建provider
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        //构建Netty的MarshallingDecoder对象，两个参数分别为provider和单个消息序列化的最大长度
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024 * 1024 * 1);
        return decoder;
    }

    /**
     * 创建JBoss Marshalling编码器MarshallingEncoder
     * @return
     */
    public static MarshallingEncoder buildMarshallingEncoder(){
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("river");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(4);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        //构建Netty的MarshallingEncoder对象，MarshallingEncoder用于实现序列化接口的POJO对象序列化为二进制数组
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }
}
