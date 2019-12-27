package com.softwiz.boscal.sample;


public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//MainClass main = new MainClass();

		Channel channel = new Channel(5); // 워커 쓰레드의 개수 
		channel.startWorkers(); 
		
		
		ClientThread []thread =new ClientThread[10] ;
		
		for(int i=0; i < 2; i++ ) {
			System.out.println(" start new work ");
			channel.setFinishedJob( 0 );
			
			thread[0] = new ClientThread("Alice", channel); 
			thread[1] = new ClientThread("Bobby", channel); 
			thread[2] = new ClientThread("Chris", channel);
			
			for(int j=0; j < 2; j++) {
				thread[j].start();
			}
			
			for(int j=0; j < 2; j++) {
				try {
					thread[j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("finished job num = " + channel.getFinishedJob() );
			System.out.println(" End work ");
		}
		
		
		
	}

}
