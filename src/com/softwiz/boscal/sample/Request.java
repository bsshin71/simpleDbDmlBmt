package com.softwiz.boscal.sample;

import java.util.Random;

public class Request {
	private final String name; // ÀÇ·ÚÀÚ 
	private final int number; // ¸®Äù½ºÆ® ¹øÈ£ 
	private static final Random random = new Random(); 
	public Request(String name, int number) { 
		this.name = name; 
		this.number = number; 
	} 
	public void execute() { 
		System.out.println(Thread.currentThread().getName() + " executes " + this); 
		try { 
				Thread.sleep(random.nextInt(1000)); 
		} catch (InterruptedException e) { 
			
		} 
	} 
	public String toString() { 
		return "[ Request from " + name + " No." + number + " ]"; 
	}
}
