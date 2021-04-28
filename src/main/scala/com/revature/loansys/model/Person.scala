package com.revature.loansys.model
import java.sql.ResultSet

case class Person(id: Int, name : String, address: String) {
  
}

object Person {
	def fromResultSet (rs: ResultSet) : Person = {
		apply(
			rs.getInt("borrower_id"),
			rs.getString("name"),
            rs.getString("address")
		)
	}
}