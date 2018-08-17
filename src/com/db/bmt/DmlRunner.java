package com.db.bmt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmlRunner implements Runnable {

	Logger log  = LoggerFactory.getLogger( this.getClass() );

	private  static DbConInfo   dbconfig    = null;
	private  Connection 		conn        = null;
	private  PreparedStatement 	pstmt       = null;
    private  CheckTime          checktime   = null;
	private  DML_TYPE    runMode;
	private  long        startValue, endValue;
	private  int         thread_number;
	private  long        commitcount;
	private  AUTOCOMMIT  autocommit;
	
	
	
	public static enum PARAM_TYPE { INT, LONG, DOUBLE, VARCHAR, STRING }
	public static enum DML_TYPE   { INSERT, SELECT, UPDATE, DELETE }
	public enum ONOFF_TYPE { OFF, ON };
	public enum AUTOCOMMIT { OFF, ON };
	
	public DmlRunner(DbConInfo dbconfig) {
		setDbConInfo( dbconfig );
	}
	
	public void setChecktime(CheckTime checktime) {
		this.checktime = checktime;
	}

	public void setDbConInfo(DbConInfo dbconfig) {
		if( this.dbconfig == null ) {
			this.dbconfig = dbconfig;
		}
	}
	
	public void setRunMode( DML_TYPE runmode ) {
		this.runMode = runmode;
	}
	
	public long getCommitcount() {
		return commitcount;
	}

	public void setCommitcount(long commitcount) {
		this.commitcount = commitcount;
		if ( this.commitcount > 1 ) { 
			autocommit = AUTOCOMMIT.OFF;
		} else {
			autocommit = AUTOCOMMIT.ON;
		}
	}

	public void setRunRange( long start, long end  ) {
		this.startValue = start;
		this.endValue   = end;
	}
	
	public void loadDriver( String driverName ) {
		log.debug(" drivername = " + driverName );
		
		try {
			Class.forName ( driverName );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.error(" Fail to load driver : + driverName ");
			System.exit(-1);
		}
	}
	public void connectDB () throws SQLException  {
		log.debug(" dburl    = " + dbconfig.getUrl()    );
		log.debug(" dbuser   = " + dbconfig.getUser()   );
		log.debug(" dbpasswd = " + dbconfig.getPasswd() );
		
		this.conn = DriverManager.getConnection( dbconfig.getUrl(), dbconfig.getUser(), dbconfig.getPasswd() );
		
	}
	
	public void closeDB () throws SQLException  {
			if( this.conn != null ) this.conn.close();
	}
	
	public void beginTrans() throws SQLException {
			this.conn.setAutoCommit( false );
	}
	
	public void commitTrans() throws SQLException {
			this.conn.commit();
	}
	
	public void rollbackTrans() throws SQLException {
			this.conn.rollback();
	}
	
	public void initDB() {
		
		Statement stmt = null;

		// if not runinitquery=1  then don not run initquery  and just skip 
		if( dbconfig.getRuninitquery().intValue() != ONOFF_TYPE.ON.ordinal() ) return;
		
		try {
			connectDB();
			List<String> initquery = dbconfig.getInitquery();
			for(int i=0; initquery != null && i < initquery.size(); i++ ) {
				String   query  = (String) initquery.get(i);
				log.debug("initquery : " + query );
				try {
					stmt  = conn.createStatement();
					stmt.executeQuery( query );
					stmt.close();
				} catch ( SQLException se ) {
					log.error( "error  initDB function " + se.getMessage() );
				}
			}
		} catch (SQLException se ) {
			se.printStackTrace();
		} finally {
			try {	
				closeDB();	
			} catch (SQLException e) {	
				e.printStackTrace();
			}
		}
	}
	
	public int selectDML() {
		return 0;
	}

	public int deleteDML() {
		return 0;
	}

	public int insertDML() {
		try {
		//	db.prepareStatement(  Property.insertQuery );
			pstmt =  conn.prepareStatement( dbconfig.insertquery );
			List<String>  params = dbconfig.getInsertparams();
			
			long exec_count = 0;
			for(long value = startValue; value <= endValue; value++) {
				for(int i=0; params != null && i < params.size(); i++ ) {
					String      paramtype  = (String) params.get( i );
					PARAM_TYPE  param_type = PARAM_TYPE.valueOf( paramtype.toUpperCase() );
					
					switch ( param_type ) {
					   case INT     : pstmt.setInt   (i+1, (int) value ); break;
					   case LONG    : pstmt.setLong  (i+1,       value ); break;
					   case DOUBLE  : pstmt.setDouble(i+1,       value ); break;
					   case VARCHAR : pstmt.setString(i+1, String.valueOf(value) ); break;
					}
				}	
				pstmt.executeUpdate();
				
				if( autocommit == autocommit.OFF && ( (exec_count%commitcount)==0 )  ) {
					 commitTrans();
				}
			}
			if( autocommit == autocommit.OFF  ) {
				 commitTrans();
			}

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ( pstmt != null ) pstmt.close();
			} catch (SQLException se) {
				log.error("error in insertDML : " + se.getMessage() );
			}
		}
		
		return 0;
	}

	public int updateDML() {
		return 0;
	}

	@Override
	public void run() {
		try {
			connectDB();
			if( autocommit == autocommit.OFF ) beginTrans();  
			switch ( this.runMode ) {
				case INSERT :	insertDML(); break;
				case UPDATE :	updateDML(); break;
				case DELETE :	deleteDML(); break;
				case SELECT :	selectDML(); break;
			}
			closeDB();
		} catch (SQLException se ) {
			se.printStackTrace();
		} 
	}

}
