package com.softwiz.boscal.sample.simplethread;



public class QueryRunner extends Thread {
	public QueryRunner(String name) { 
		super(name);  
	} 
	
	public void run() { 
		try { 
			Thread.sleep(1000); 
			System.out.println( Thread.currentThread().getName() + "execute...." );
		} catch (InterruptedException e) { 
				
		} 
	}
}
