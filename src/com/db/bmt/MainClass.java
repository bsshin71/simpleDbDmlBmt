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
	
	long   threadnum     ;   // �� thread �� 
	long   startvalue    ;   // bmt value ���۰�
	long   endvalue      ;   // bmt value ������
	long   commitcount   ;   // commit count
	
	long   interval      ;   // bmt value ��  ����
	long   quota         ;   // ������� ó���� �������� 
	long   remainder     ;   // ���� �����尡 �߰��� ó���� ��
	long[] threadalloc  = null; // ������� ó���� ������ �迭�� ����. ���������� �� �� ��������� �� ó���ϵ��� �Ҵ� �ϱ� ���ؼ�..
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
		
		// �����庰�� ó���� ���� �迭�� ��´�. �迭�� ��� ���� �̿��ؼ�  �����庰�� start, end���� ����ϴ� �� �̿��Ѵ�.
		// ó���� ������  ������ ������ ����� �� �������� ���� ���  �� ������ ������ ó���� �����尡 �ʿ��ϴ�.
		// ���� ������ ����  ������ ���� �Ѱ��� �Ҵ��� �ش�.
		for( int i = 0; i < threadnum; i++ ) {
			if ( i < remainder ) 
				threadalloc[i] =  quota + 1;  // ������ �Ҵ�..
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

			long startnum = startvalue;  // �����庰 ���۰�
			long endnum;                 // �����庰 ���ᰪ
			
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