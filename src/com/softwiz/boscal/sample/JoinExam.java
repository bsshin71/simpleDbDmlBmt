package com.softwiz.boscal.sample;

public class JoinExam {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        MyThread5 thread = new MyThread5();
        // Thread ���� 
        thread.start(); 
        System.out.println("Thread�� ����ɶ����� ��ٸ��ϴ�.");
        try {
            // �ش� �����尡 ���⶧���� ����
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread�� ����Ǿ����ϴ�."); 
        
        
        thread.start(); 
        System.out.println("Thread�� ����ɶ����� ��ٸ��ϴ�.");
        try {
            // �ش� �����尡 ���⶧���� ����
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread�� ����Ǿ����ϴ�.");
    }   

}
