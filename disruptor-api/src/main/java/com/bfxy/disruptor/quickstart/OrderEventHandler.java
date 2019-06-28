package com.bfxy.disruptor.quickstart;

import com.lmax.disruptor.EventHandler;

/**
 * 消费者逻辑
 * @author admin
 *
 */
public class OrderEventHandler implements EventHandler<OrderEvent>{

	/*
	 * 事件驱动模式，监听模式，当有消息发过来时，onEvent会监听到并执行消费者的逻辑
	 */
	@Override
	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
//		Thread.sleep(Integer.MAX_VALUE);
		Thread.sleep(2000);
		System.err.println("消费者: " + event.getValue());
	}

}
