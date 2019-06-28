package com.bfxy.disruptor.heigh.multi;

import com.lmax.disruptor.RingBuffer;

public class Producer {
	
	private RingBuffer<Order> ringBuffer;
	
	public Producer(RingBuffer<Order> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void sendData(String uuid) {
		long sequence = ringBuffer.next();
		try {
			Order order = ringBuffer.get(sequence);
			order.setId(uuid);
		} finally {
			System.out.println("*******************生产者生成数据： " + uuid);
			ringBuffer.publish(sequence);
		}
	}

}
