package com.softwiz.boscal.sample;

public class Channel {
	private static final int MAX_REQUEST = 100; 
	private final Request[] requestQueue; 
	private int tail; // 다음에 putRequest하는 장소 
	private int head; // 다음에 takeRequest하는 장소 
	private int count; // Request의 수 
	private int finishedJob; // 처리작업수
	
	private final WorkerThread[] threadPool;
	
	public Channel(int threads) { 
		this.requestQueue = new Request[MAX_REQUEST]; 
		this.head = 0; 
		this.tail = 0; 
		this.count = 0; 
		this.finishedJob = 0;
		
		threadPool = new WorkerThread[threads]; 
		for (int i = 0; i < threadPool.length; i++) { 
			threadPool[i] = new WorkerThread("Worker-" + i, this); 
		} 
	} 
	
	public synchronized void setFinishedJob(int num ) { 
		this.finishedJob = 0;
	} 
	
	public synchronized void increaseFinishedJob( ) { 
		this.finishedJob++;
	} 
	
	public  synchronized int  getFinishedJob() { 
		return this.finishedJob;
	} 
	
	public void startWorkers() { 
		for (int i = 0; i < threadPool.length; i++) { 
			threadPool[i].start(); 
		} 
	} 
	public synchronized void putRequest(Request request) { 
		while (count >= requestQueue.length) { 
			try { 
				wait(); 
			} catch (InterruptedException e) { 
				
			} 
		} 
		requestQueue[tail] = request; 
		tail = (tail + 1) % requestQueue.length; 
		count++; 
		notifyAll(); 
	} 
	public synchronized Request takeRequest() { 
		while (count <= 0) { 
			try { 
				wait(); 
			} catch (InterruptedException e) { 
				
			} 
		} 
		Request request = requestQueue[head]; 
		head = (head + 1) % requestQueue.length; 
		count--; 
		notifyAll(); 
		return request; 
	}
	
}
