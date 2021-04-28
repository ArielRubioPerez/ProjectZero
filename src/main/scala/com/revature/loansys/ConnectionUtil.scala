package com.revature.loansys

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet 

object ConnectionUtil {
  var conn: Connection = null;
	/** retrieve connection, login detail hardcoded (bad)
      *  
      * @return Connection
      */
	def getConnection() : Connection = {

        //start a connection if there is none
		if (conn == null || conn.isClosed()) {
			classOf[org.postgresql.Driver].newInstance() //load the driver

            //getConnection takes a url, username, password
            conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/loansys?user=postgres",
                "postgres",
                "notmypasswordlol"
            )
		}
        //return conn, after initialization
        conn
	}

	

}
