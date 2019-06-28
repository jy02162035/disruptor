package com.bfxy.disruptor.heigh.chain.wps;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bfxy.disruptor.heigh.chain.Trade;
import com.bfxy.disruptor.heigh.chain.TradePushlisher;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 单消费者模式
 * @author admin
 *
 */
public class MainWps {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
			
		//构建一个线程池用于提交任务
		ExecutorService es1 = Executors.newFixedThreadPool(1);
		// 构建一个线程池用于并发消费任务，因为是单消费者，因此有几个消费端，就要设置几个线程数
		ExecutorService es2 = Executors.newFixedThreadPool(10);
		//1 构建Disruptor
		Disruptor<Trade> disruptor = new Disruptor<Trade>(
				new EventFactory<Trade>() {
					public Trade newInstance() {
						return new Trade();
					}
				},
				1024*1024,
				es2,
				ProducerType.SINGLE,
				new BusySpinWaitStrategy());
		
		
		//2 把消费者设置到Disruptor中 handleEventsWith
		
		//2.2 并行操作: 可以有两种方式去进行
		//1 handleEventsWith方法 添加多个handler实现即可
		//2 handleEventsWith方法 分别进行调用
		int consumerCount = 10;
		for (int i = 0; i < consumerCount; i++) {
			disruptor.handleEventsWith(new HandlerWps("WPS000"+i, consumerCount, i));
		}
		

		
		//3 启动disruptor
		RingBuffer<Trade> ringBuffer = disruptor.start();
		
		CountDownLatch latch = new CountDownLatch(1);
		
		long begin = System.currentTimeMillis();
		// 生产数据线程
		es1.submit(new TradePushlisherWps(latch, disruptor));
		
		
		latch.await();	//进行向下
		
		disruptor.shutdown();
		es1.shutdown();
		es2.shutdown();
		System.err.println("总耗时: " + (System.currentTimeMillis() - begin));
		
		
	}
}
