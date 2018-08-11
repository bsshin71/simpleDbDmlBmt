package com.db.bmt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DmlRunner implements Runnable {

	private  static DbConInfo    dbconfig    = null;
	private  Connection 			conn        = null;
	private  PreparedStatement 	pstmt       = null;

	private   int    runMode;
	private   long  startValue, endValue;
	private   int    thread_number;
	
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int DELETE = 3;
	public static final int SELECT = 4;
	
	
	public DmlRunner(DbConInfo dbconfig) {
		this.dbconfig = dbconfig;
	}
	
	public void setDbConInfo(DbConInfo dbconfig) {
		this.dbconfig = dbconfig;
	}
	
	public void setRunMode( int runmode ) {
		this.runMode = runmode;
	}
	
	public void setRunRange( int start, int end  ) {
		this.startValue = start;
		this.endValue   = end;
	}
	
	public static void loadDriver( String driverName ) {
		try {
			Class.forName ( driverName );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println(" Fail to load driver : + driverName ");
			System.exit(-1);
		}
	}
	public void connectDB () throws SQLException  {
		this.conn = DriverManager.getConnection( dbconfig.getUrl(), dbconfig.getUser(), dbconfig.getPasswd() );
	}
	
	public void closeDB () throws SQLException  {
			this.conn.close();
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
			pstmt.setInt(1,1);
			pstmt.setInt(2,1);
			pstmt.setInt(3,1);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
