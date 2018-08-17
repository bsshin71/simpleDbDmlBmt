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

	Logger log  = LoggerFactory.getLogger( this.getClass() );
	
	long   threadnum     ;   // 총 thread 수 
	long   startvalue    ;   // bmt value 시작값
	long   endvalue      ;   // bmt value 최종값
	long   commitcount   ;   // commit count
	
	long   interval      ;   // bmt value 의  범위
	long   quota         ;   // 쓰레드당 처리할 구간범위 
	long   remainder     ;   // 최종 쓰레드가 추가로 처리할 값
	long[] threadalloc  = null; // 쓰레드당 처리할 범위를 배열에 담든다. 나머지값을 알 쪽 쓰레브부터 더 처리하도록 할당 하기 위해서..
	Toml   configfile;
	Toml   dbload;
	
	
	public void loadConfig(String file ) {
		log.info("begin paring configuration file....");
		
		this.configfile  = new Toml().read( new File(file) );
		this.dbload      = configfile.getTable("dbload");
		
		threadnum        = dbload.getLong("threadnum").longValue();
		commitcount      = dbload.getLong("commitcount").longValue();
		List<Long> range = dbload.getList("paramrange");
		startvalue       = (Long) range.get(0).longValue();
		endvalue         = (Long) range.get(1).longValue();
		interval         =  endvalue - startvalue + 1;
		quota            = (long) Math.floor ( ( endvalue - startvalue) / threadnum );
		remainder        = interval - ( threadnum * quota );
		
		this.threadalloc  = new long[( int) threadnum];
		
		// 쓰데드별로 처리할 양을 배열에 담는다. 배열에 담긴 값을 이용해서  쓰레드별로 start, end값을 계산하는 데 이용한다.
		// 처리할 범위가  쓰레드 개수로 나누어서 딱 떨어지지 않을 경우  그 나머지 값들을 처리할 쓰레드가 필요하다.
		// 앞쪽 쓰레드 부터  나머지 값을 한개씩 할당해 준다.
		for( int i = 0; i < threadnum; i++ ) {
			if ( i < remainder ) 
				threadalloc[i] =  quota + 1;  // 나머지 할당..
			else
				threadalloc[i] =  quota;
			
			log.debug(" threadalloc["+i+"] = " + threadalloc[i]);
		}

	}
	
	public void runBmt() {
		
		log.debug(" start rum bmt ~~~~");
		
		List<Toml>  databases = configfile.getTables("database");
		for(int i=0; i < databases.size(); i++ ) {
			Toml       db    = (Toml) databases.get(i);
			DbConInfo dbinfo = db.to( DbConInfo.class );
			
			if ( dbinfo.getEnable().intValue() == 0 ) {
				log.info(" skip bmt : " + dbinfo.getDbproduct() );
				continue;  // do not run bmt for this database
			}
			
			DmlRunner dbinit  =  new DmlRunner( dbinfo );
			dbinit.loadDriver ( dbinfo.getDriver() );
			dbinit.initDB();

			long startnum = startvalue;  // 쓰레드별 시작값
			long endnum;                 // 쓰레드별 종료값
			
			CheckTime []checktime = new CheckTime[ (int)threadnum ];
			Thread    []thread    = new Thread[ (int)threadnum ];
			
			for( int j = 0;   j < threadnum;  j++ ) {
				endnum =  startnum + threadalloc[j] - 1 ;
				
				log.debug( " thread["+j+"]  start="+startnum + ", endnum="+ endnum + " threadalloc=["+j+"]="+ threadalloc[j]);
				log.debug("threadnum = " +  j );
				log.debug("startnum  = " +  startnum );
				log.debug("endnum    = " +  endnum   );
				
				checktime[j] = new CheckTime();
				DmlRunner  dml    = new DmlRunner( dbinfo );
				dml.setCommitcount( commitcount );
				dml.setRunRange ( startnum, endnum );
				dml.setRunMode  ( DML_TYPE.INSERT  );
				dml.setChecktime( checktime[j] );
				
				startnum = endnum + 1;
				
				thread[j]   = new Thread( dml,  "thread-"+j );
				thread[j].start();
				
			}
			// Wait until Thread done.
			for( int j = 0;   j < threadnum;  j++ ) {
				try {
					thread[j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.out.println("============= end of " + dbinfo.getDbproduct() + "============" );
		}
	}
	
	public static void main(String[] args) {

		MainClass main = new MainClass();
		
		main.loadConfig( "conf/bmt.toml" );
		main.runBmt();
	}
}