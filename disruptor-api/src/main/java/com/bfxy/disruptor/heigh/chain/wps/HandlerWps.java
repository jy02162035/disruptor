package com.bfxy.disruptor.heigh.chain.wps;

import java.util.List;

import com.bfxy.disruptor.heigh.chain.Trade;
import com.lmax.disruptor.EventHandler;

public class HandlerWps implements EventHandler<Trade> {

	private String name;
	private int mod;
	private int consumerCount;

	public HandlerWps(String name, int consumerCount, int mod) {
		this.name = name;
		this.consumerCount = consumerCount;
		this.mod = mod;
	}

	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.err.println("handler WPS : SET NAME=====" + name);
		List<Integer> fileList = event.getFileList();
		fileList.stream().forEach((i) -> {
			if (i % consumerCount == mod) {
				System.out.println("id=【" + i + "】的文件进入消费者Handle【" + name + "】进行处理");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
