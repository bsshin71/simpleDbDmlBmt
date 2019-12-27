package com.db.bmt;

import java.util.HashMap;

public class CheckTime {
	
	HashMap<String, Long> laptime = new HashMap<String, Long>();

	public HashMap<String, Long> getLaptime() {
		return laptime;
	}

	public void setLaptime(HashMap<String, Long> laptime) {
		this.laptime = laptime;
	} 
	
	public void putLapTime(String name, long elapsed ) {
		this.laptime.put(name, new Long( elapsed ) );
	}

}
