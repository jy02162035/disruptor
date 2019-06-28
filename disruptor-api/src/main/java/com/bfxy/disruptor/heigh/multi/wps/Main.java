package com.bfxy.disruptor.heigh.multi.wps;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 单生产者多消费者
 * @author admin
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		int ringBufferSize = 1024*1024;
		//1 创建RingBuffer
		// 多生产多消费不需要线程池
		RingBuffer<Order> ringBuffer =
				RingBuffer.create(ProducerType.SINGLE,
						new EventFactory<Order>() {
							public Order newInstance() {
								return new Order();
							}
						},
						ringBufferSize,
						new YieldingWaitStrategy());
		
		//2 通过ringBuffer 创建一个屏障
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		
		//3 创建多个消费者数组:
		Consumer[] consumers = new Consumer[10];
		for(int i = 0; i < consumers.length; i++) {
			consumers[i] = new Consumer("C" + i);
		}
		
		//4 构建多消费者工作池
		WorkerPool<Order> workerPool = new WorkerPool<Order>(
				ringBuffer,
				sequenceBarrier,
				new EventExceptionHandler(),
				consumers);
		
		//5 设置多个消费者的sequence序号 用于单独统计每个消费者的消费进度, 并且设置到容器ringbuffer中
		ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
		
		//6 启动workerPool
		workerPool
		.start(Executors.newFixedThreadPool(10));
		
		// 7生产者投递消息
//		final CountDownLatch latch = new CountDownLatch(1);
//		for(int i = 0; i < 100; i++) {
//			final Producer producer = new Producer(ringBuffer);
//			new Thread(new Runnable() {
//				public void run() {
//					try {
//						latch.await();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					for(int j = 0; j<100; j++) {
//						producer.sendData(UUID.randomUUID().toString());
//					}
//				}
//			}).start();
//		}
//		
//		Thread.sleep(2000);
//		System.err.println("----------线程创建完毕，开始生产数据----------");
//		latch.countDown();
		
		final Producer producer = new Producer(ringBuffer);
		for(int j = 0; j<10; j++) {
			producer.sendData(UUID.randomUUID().toString());
		}
		
		Thread.sleep(10000);
		
		System.err.println("任务总数:" + consumers[2].getCount());
	}
	
	static class EventExceptionHandler implements ExceptionHandler<Order> {
		public void handleEventException(Throwable ex, long sequence, Order event) {
		}

		public void handleOnStartException(Throwable ex) {
		}

		public void handleOnShutdownException(Throwable ex) {
		}
	}
	
	
	
	
}
