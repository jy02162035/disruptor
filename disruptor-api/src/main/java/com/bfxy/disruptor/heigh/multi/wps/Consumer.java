package com.bfxy.disruptor.heigh.multi.wps;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.WorkHandler;

/**
 * 多消费者必须要实现WorkHandler接口 并且将消费逻辑onEvent方法中
 * @author admin
 *
 */
public class Consumer implements WorkHandler<Order> {

	private String comsumerId;
	
	private static AtomicInteger count = new AtomicInteger(0);
	
	private Random random = new Random();
	
	public Consumer(String comsumerId) {
		this.comsumerId = comsumerId;
	}

	public void onEvent(Order event) throws Exception {
		Thread.sleep(1 * random.nextInt(5));
		System.err.println("当前消费者: " + this.comsumerId + ", 消费信息ID: " + event.getId());
		count.incrementAndGet();
	}
	
	public int getCount(){
		return count.get();
	}
	

}
