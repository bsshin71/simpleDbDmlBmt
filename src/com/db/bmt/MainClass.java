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
		
		long   threadnum     = 0;   // �� thread �� 
		long   startvalue    = 0;   // bmt value ���۰�
		long   endvalue      = 0;   // bmt value ������
		long   quota         = 0;   // ������� ó���� �������� 
		long   remainder     = 0;   // ���� �����尡 �߰��� ó���� ��
		
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

			long startnum = startvalue;  // �����庰 ���۰�
			long endnum;                 // �����庰 ���ᰪ
			
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