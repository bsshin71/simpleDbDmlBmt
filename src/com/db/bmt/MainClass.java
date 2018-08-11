package com.db.bmt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.moandjiezana.toml.Toml;



/* 
 * toml parser ÂüÁ¶ URL 
 * https://github.com/agrison/jtoml 
 * https://github.com/mwanji/toml4j
 * */

public class MainClass {

	public static void main(String[] args) {
		Toml toml = new Toml().read( new File("conf/bmt.toml") );
		Toml load = toml.getTable("dbload");
		
		List<Toml>  databases = toml.getTables("database");
		for(int i=0; i < databases.size(); i++ ) {
			Toml db = (Toml) databases.get(i);

			DbConInfo dbinfo = db.to(DbConInfo.class);
			
			System.out.println("driver = " + dbinfo.getDriver() );
			
//			List<String> initquery = dbinfo.getInitquery();
//
//			for( int j=0; j < initquery.size(); j++) {
//				String query = (String) initquery.get(j);
//				System.out.println(" initquery [" + j + "] = " + query );
//			}
			
			DmlRunner  dml    = new DmlRunner( dbinfo );
			dml.loadDriver    ( dbinfo.getDriver() );
			dml.setRunMode ( dml.INSERT );

			Thread   thread   = new Thread(dml, "A");
			thread.start();
		}
	}

}
