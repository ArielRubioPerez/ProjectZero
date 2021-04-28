package com.revature.loansys

import java.sql.DriverManager

object Main {
  
  def main(args:Array[String]):Unit={
    
    //manually load the driver
    classOf[org.postgresql.Driver].newInstance()

    //use JDBC's DriverManager to get a connection. JDBC is DB agnostic
    //this version takes 3 arguments. location, user, pw
		// val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/loansys", "postgres", "elevator")
    // val stmt = conn.prepareStatement("SELECT * from borrower;")
    // stmt.execute()
    // val rs = stmt.getResultSet()
    // while(rs.next()){
    //   println(rs.getString("f_name") + ", " + rs.getString("l_name"))  //retrieve first name
    // }
    // conn.close()
    val cli = new Cli();
    cli.menu()

  }
}
