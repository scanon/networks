package us.kbase.networks.adaptor.ppi.local;

import java.sql.*;
import java.io.*;
import java.util.*;

import com.mchange.v2.c3p0.*;

/**
  Class to access local PPI SQL database.
  
  <pre>
  Version 2.0, 11/27/12 - updated to use c3p0
  Version 1.0, 10/3/12 - adapted from gov.lbl.scop.LocalSQL
  </pre>

  @version 2.01, 11/29/12
  @author JMC
  */
public class PPI {
    /**
       pool of connctions to the db
    */
    private static ComboPooledDataSource cpds;
    
    /**
       database URLs for RO access, separated by ;
    */
    private static String roURLs = null;

    /**
       database URLs for RW access, separated by ;
    */
    private static String rwURLs = null;

    /**
       get local property, or null if not defined.
       These are stored in ppi.properties, which is a local
       file not under version control.
    */
    public static String getProperty(String key) {
	Properties prop = new Properties();
	try {
	    PPI x = new PPI();
	    Class myClass = x.getClass();
	    prop.load(myClass.getResourceAsStream("ppi.properties"));
	}
	catch (IOException e) {
	}
	catch (SecurityException e) {
	}
	String value = prop.getProperty(key, null);
	return value;
    }

    /**
       connect to db using a particular URL or set of URLs separated by ;
    */
    final public static void connect(String urls) {
	if (cpds==null) {
	    try {
		cpds = new ComboPooledDataSource();
		cpds.setDriverClass("org.gjt.mm.mysql.Driver");

		String[] url = urls.split(";");
		for (int i=0; i<url.length; i++) {
		    try {
			cpds.setJdbcUrl(url[i]);
		    }
		    catch (Exception e2) {
			System.err.println("failed to open "+url[i]);
			cpds = null;
		    }
		    if (cpds != null) i=url.length;
		}
	    }
	    catch (Exception e) {
		System.err.println(e.getMessage());
	    }
	}
    }
    
    /**
       connect to db with default access level
    */
    final public static void connect() {
	if (roURLs==null)
	    roURLs = getProperty("db.ro_urls");
	if (roURLs==null)
	    roURLs = "jdbc:mysql://localhost/ppi?user=anonymous";
	connect(roURLs);
    }

    /**
       connect to db with rw access
    */
    final public static void connectRW() {
	if (rwURLs==null)
	    rwURLs = getProperty("db.rw_urls");
	if (rwURLs==null)
	    rwURLs = "jdbc:mysql://localhost/ppi?user=anonymous";
	connect(rwURLs);
    }

    /**
       get a connection from the pool, or null if an error occurs
    */
    final public static Connection getConnection() {
	if (cpds==null) connect();
	try {
	    return cpds.getConnection();
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }
    
    /**
       make another Statement, or null if an error occurs.
    */
    final public static Statement createStatement(Connection con) {
	if (cpds==null) connect();
	try {
	    return con.createStatement();
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }

    /**
       make another Statement that returns results one
       row at a time, or null if an error occurs.
    */
    final public static Statement createStatementOneRow(Connection con) {
	if (cpds==null) connect();
	try {
	    Statement stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						 java.sql.ResultSet.CONCUR_READ_ONLY);
	    stmt.setFetchSize(Integer.MIN_VALUE);
	    return stmt;
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }
    
    /**
       make a PreparedStatement, or null if an error occurs.
    */
    final public static PreparedStatement prepareStatement(Connection con,
							   String s) {
	if (cpds==null) connect();
	try {
	    return con.prepareStatement(s);
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }

    /**
       make a PreparedStatement w/ optional autogenerated keys, or
       null if an error occurs.
    */
    final public static PreparedStatement prepareStatement(Connection con,
							   String s,
							   int autoGeneratedKeys) {
	if (cpds==null) connect();
	try {
	    return con.prepareStatement(s, autoGeneratedKeys);
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }
    
    /**
       turn on/off manual committing, for transactions
    */
    final public static void setAutoCommit(Connection con,
					   boolean b) throws Exception {
	if (cpds==null)
	    return;
	con.setAutoCommit(b);
    }

    /**
       commit updates
    */
    final public static void commit(Connection con) throws Exception {
	if (cpds==null)
	    return;
	con.commit();
    }

    /**
       roll back updates
    */
    final public static void rollback(Connection con) throws Exception {
	if (cpds==null)
	    return;
	con.rollback();
    }
}
