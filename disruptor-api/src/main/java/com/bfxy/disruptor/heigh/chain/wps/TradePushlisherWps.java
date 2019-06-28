package com.bfxy.disruptor.heigh.chain.wps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.bfxy.disruptor.heigh.chain.Trade;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

public class TradePushlisherWps implements Runnable {

	private Disruptor<Trade> disruptor;
	private CountDownLatch latch;
	
	private static int PUBLISH_COUNT = 1;
	
	public TradePushlisherWps(CountDownLatch latch, Disruptor<Trade> disruptor) {
		this.disruptor = disruptor;
		this.latch = latch;
	}

	public void run() {
		
		TradeEventTranslatorWps eventTranslator = new TradeEventTranslatorWps();
		for(int i =0; i < PUBLISH_COUNT; i ++){
			//新的提交任务的方式
			disruptor.publishEvent(eventTranslator);			
		}
		latch.countDown();
	}
}

/**
 * 
 * @author pengshun.wu
 *
 */
class TradeEventTranslatorWps implements EventTranslator<Trade> {

	/*
	 * 生产者利用EventTranslator的实现类，很方便的投递数据方式
	 */
	@Override
	public void translateTo(Trade event, long sequence) {
		List<Integer> fileList = new ArrayList<Integer>();
		for (int i = 0; i < 1000; i++) {
			fileList.add(i);
		}
		event.setFileList(fileList);
	}

	
}











