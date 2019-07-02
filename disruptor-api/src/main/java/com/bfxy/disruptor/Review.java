package com.bfxy.disruptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class Review {

	
	public static void main(String[] args) throws Exception {
		
		//ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		
		//CopyOnWriteArrayList<String> cowal = new CopyOnWriteArrayList<>();
		
		//cowal.add("aaaa");
		
//		AtomicLong count = new AtomicLong(1);
//		boolean flag = count.compareAndSet(0, 2);
//		System.err.println(flag);
//		System.err.println(count.get());
		
		/**
		 * 传统线程阻塞等待另外一个线程通知
		 */
		Object lock = new Object();
		Thread AA = new Thread(() -> {
			int sum = 0;
			for (int i = 0; i < 10; i++) {
				sum +=i;
			}
			synchronized (lock) {
				try {
					// wait在此一致阻塞，一直等待被唤醒，wait会释放Object锁，而notify不释放锁
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("wait|notify === " + sum);
		});
		
		AA.start();
		Thread.sleep(2000);
		synchronized (lock) {
			// wait和notify必须配合synchronized使用
			lock.notify();
		}
		
		System.out.println("********************************************************************************");
		/**
		 * 使用LockSupport来解决传统线程之间消息传递
		 *  LockSupport : 
		 *  */
		Thread A = new Thread(new Runnable() {
			
			@Override
			public void run() {
				int sum = 0;
				for(int i =0; i < 10; i ++){
					sum += i;
				}
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 4秒之后，将本线程挂起
				LockSupport.park();	//后执行
				System.err.println("LockSupport.park()|LockSupport.unpark(A): " + sum);
			}
		});
		
		A.start();
		
		Thread.sleep(1000);
		// 休眠1秒钟，唤醒A线程
		LockSupport.unpark(A);	//先执行
		
		
		/**
		 * 回顾Executors创建线程池的弊端
		 */
		// 1.newCachedThreadPool
		// 内部ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		// 由于采用SynchronousQueue队列没有任何存储功能，当线程来临时直接创建线程执行，
		// 当有大量线程过来时候，如果超过最大线程数，将会把线程池撑爆
		Executors.newCachedThreadPool();
		
		// 2.newFixedThreadPool
		// 内部ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		// 虽然线程数固定，但采用LinkedBlockingQueue来存储等待任务，当有海量线程过来时，进入队列堆积，把系统撑爆
		Executors.newFixedThreadPool(10);
		
		// 生产环境必须要采用下面方式定义线程池
		// 对于线程池数和队列数定义依据：计算机密集型/IO密集型
		// 计算机密集型：大量基于内存的操作，核心线程数少一些【CPU核数+1或者CPU核数*2】
		// IO密集型: 大量数据存取，写到DB，写到文件等等比较慢，核心线程数多一些【CPU核数/(1-阻塞系数)】阻塞系数一般为0.8到0.9一个范围
		// 项目中肯定多个线程池，这之间要平衡以下线程数
		ThreadPoolExecutor pool = new ThreadPoolExecutor(
				5,
				Runtime.getRuntime().availableProcessors() * 2,
				60, //池里线程如果空闲，过60秒会被回收
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(200),// 有界队列ArrayBlockingQueue
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("order-thread");
						if(t.isDaemon()) {
							t.setDaemon(false);
						}
						if(Thread.NORM_PRIORITY != t.getPriority()) {
							t.setPriority(Thread.NORM_PRIORITY);
						}
						return t;
					}
				},// 采用自定义创建线程的方式，比如根据线程r的特性处理某些业务
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						System.err.println("拒绝策略:" + r);
					}
				});
		
		pool.shutdown();
		
		ReentrantLock reentrantLock = new ReentrantLock(true);

	
		
		
		
		
		
		
		
		
		
		
	}
}
