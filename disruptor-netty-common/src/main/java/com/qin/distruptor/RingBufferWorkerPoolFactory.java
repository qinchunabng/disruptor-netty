package com.qin.distruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.qin.entity.TranslatorDataWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RingBufferWorkerPoolFactory {

    private static Map<String, MessageProducer> producers = new ConcurrentHashMap<>();

    private static Map<String, MessageConsumer> consumers = new ConcurrentHashMap<>();

    private RingBuffer<TranslatorDataWrapper> ringBuffer;

    private SequenceBarrier sequenceBarrier;

    private WorkerPool<TranslatorDataWrapper> workerPool;

    private RingBufferWorkerPoolFactory(){

    }

    private static class SingletonHolder {

        static final RingBufferWorkerPoolFactory INSTANCE = new RingBufferWorkerPoolFactory();
    }

    public static RingBufferWorkerPoolFactory getInstance(){
        return SingletonHolder.INSTANCE;
    }


    public void initAndStart(ProducerType type, int bufferSize, WaitStrategy waitStrategy, MessageConsumer[] consumers){
        //1.创建RingBuffer对象
        this.ringBuffer = RingBuffer.create(type, () -> new TranslatorDataWrapper(), bufferSize, waitStrategy);
        //2.设置序号栅栏
        this.sequenceBarrier = this.ringBuffer.newBarrier();
        //3.设置工作池
        this.workerPool = new WorkerPool<>(this.ringBuffer, this.sequenceBarrier, new EventExceptionHandler(), consumers);
        //4.把锁创建的消费者置入池中
        for(MessageConsumer consumer : consumers){
            RingBufferWorkerPoolFactory.consumers.put(consumer.getConsumerId(), consumer);
        }
        //5.添加我们的sequences
        this.ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
        //6.启动工作池
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.workerPool.start(new ThreadPoolExecutor(availableProcessors, availableProcessors, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000)));
    }

    public MessageProducer getMessageProducer(String producerId){
        return RingBufferWorkerPoolFactory.producers.computeIfAbsent(producerId, s -> {
            MessageProducer producer = new MessageProducer(producerId, ringBuffer);
            return producer;
        });
    }

    static class EventExceptionHandler implements ExceptionHandler<TranslatorDataWrapper> {

        @Override
        public void handleEventException(Throwable throwable, long l, TranslatorDataWrapper translatorDataWrapper) {
            throwable.printStackTrace();
        }

        @Override
        public void handleOnStartException(Throwable throwable) {

        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {

        }
    }
}
