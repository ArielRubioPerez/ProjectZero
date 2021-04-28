package com.revature.loansys.model
import java.sql.ResultSet

case class Borrower(name : String, loan : Int) {
  
}

object Borrower  {
	def fromResultSet (rs: ResultSet) : Borrower = {
		apply(
			rs.getString("name"),
			rs.getInt("loan"),
		)
	}
}