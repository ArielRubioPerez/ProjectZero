package com.revature.loansys.model
import java.sql.ResultSet

case class Loan(loanId : Int, borrowerId : Int, intLoan : Double, loanBalance : Double) {
  
}

object Loan  {
	def fromResultSet (rs: ResultSet) : Loan = {
		apply(
			rs.getInt("loan_id"),
			rs.getInt("borrower_id"),
			rs.getDouble("init_loan"),
			rs.getDouble("loan_balance")
		)
	}
}
