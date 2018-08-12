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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.bmt.DmlRunner.DML_TYPE;
import com.moandjiezana.toml.Toml;

/* 
 * toml parser reference  URL 
 * https://github.com/agrison/jtoml 
 * https://github.com/mwanji/toml4j
 * */

public class MainClass {

	public static void main(String[] args) {

		Logger log  = LoggerFactory.getLogger( MainClass.class );
		
		long   threadnum     = 0;   // 총 thread 수 
		long   startvalue    = 0;   // bmt value 시작값
		long   endvalue      = 0;   // bmt value 최종값
		long   quota         = 0;   // 쓰레드당 처리할 구간범위 
		long   remainder     = 0;   // 최종 쓰레드가 추가로 처리할 값
		
		log.info("begin paring configuration file....");
		
		Toml toml        = new Toml().read( new File("conf/bmt.toml") );
		Toml dbload      = toml.getTable("dbload");
		
		threadnum        = dbload.getLong("threadnum").longValue();
		List<Long> range = dbload.getList("paramrange");

		startvalue       = (Long) range.get(0).longValue();
		endvalue         = (Long) range.get(1).longValue();
		quota            = (long) Math.ceil ( ( endvalue - startvalue) / threadnum );
		remainder        = endvalue - ( threadnum * quota );
		
		List<Toml>  databases = toml.getTables("database");
		for(int i=0; i < databases.size(); i++ ) {
			Toml       db    = (Toml) databases.get(i);
			DbConInfo dbinfo = db.to( DbConInfo.class );
			
			if ( dbinfo.getBmt().intValue() == 0 ) {
				log.info(" skip bmt : " + dbinfo.getDbproduct() );
				continue;  // do not run bmt for this database
			}
			
			List<String> initquery = dbinfo.getInitquery();
			for( int j=0; j < initquery.size(); j++) {
				String query = (String) initquery.get(j);
				System.out.println(" initquery [" + j + "] = " + query );
			}
			
			DmlRunner dbinit  =  new DmlRunner( dbinfo );
			dbinit.loadDriver ( dbinfo.getDriver() );
			dbinit.initDB();

			long startnum = startvalue;  // 쓰레드별 시작값
			long endnum;                 // 쓰레드별 종료값
			
			for( int j = 0;   j < threadnum;  j++ ) {
				endnum = ( (j+1) == threadnum ) ? ( j +  1 ) * quota + remainder :  ( j +  1 ) * quota ;
				
				log.debug("threadnum = " +  j );
				log.debug("startnum  = " +  startnum );
				log.debug("endnum    = " +  endnum   );
				
				DmlRunner  dml    = new DmlRunner( dbinfo );
				dml.setRunRange ( startnum, endnum );
				dml.setRunMode  ( DML_TYPE.INSERT  );
				
				startnum = startnum + endnum;
				
				Thread   thread   = new Thread( dml,  "A" );
				thread.start();
			}
			System.out.println("============= end of " + dbinfo.getDbproduct() + "============" );
		}
	}
}