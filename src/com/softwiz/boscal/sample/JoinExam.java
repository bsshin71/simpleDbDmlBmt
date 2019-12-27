package com.softwiz.boscal.sample;

public class JoinExam {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        MyThread5 thread = new MyThread5();
        // Thread 시작 
        thread.start(); 
        System.out.println("Thread가 종료될때까지 기다립니다.");
        try {
            // 해당 쓰레드가 멈출때까지 멈춤
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread가 종료되었습니다."); 
        
        
        thread.start(); 
        System.out.println("Thread가 종료될때까지 기다립니다.");
        try {
            // 해당 쓰레드가 멈출때까지 멈춤
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread가 종료되었습니다.");
    }   

}
