package com.softwiz.boscal.sample.simplethread;

import com.softwiz.boscal.sample.ClientThread;

public class MainClass {

	public void doWork() {
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		QueryRunner []thread =new QueryRunner[10] ;
		
		for(int i=0; i < 2; i++ ) {
			System.out.println(" start new work ");
			
			thread[0] = new QueryRunner("Worker-0"); 
			thread[1] = new QueryRunner("Worker-1"); 
			thread[2] = new QueryRunner("Worker-2");
			
			for(int j=0; j < 3; j++) {
				thread[j].start();
			}
			
			for(int j=0; j < 3; j++) {
				try {
					thread[j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println(" End work ");
		}
		
	}

}
