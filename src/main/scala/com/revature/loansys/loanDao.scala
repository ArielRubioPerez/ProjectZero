package com.revature.loansys

import com.revature.loansys.model.Loan
import com.revature.loansys.model.Borrower
import com.revature.loansys.model.Person
import scala.util.Using
import scala.io.StdIn
import scala.collection.mutable.ArrayBuffer
import com.revature.loansys.ConnectionUtil

/**
  * A Loan Data Access Object. CRUD methods for Loans
  * 
  * Database logic goes here.
  */
object loanDao {
	def getAllDebts(): Seq[Loan] = {
		val conn = ConnectionUtil.getConnection();
		Using.Manager { use =>
		  val stmt = use(conn.prepareStatement("SELECT * FROM loan;"))
			stmt.execute()
      conn.close()
			val rs = use(stmt.getResultSet())
			val allLoans: ArrayBuffer[Loan] = ArrayBuffer()
			while (rs.next()){
				allLoans.addOne(Loan.fromResultSet(rs))
			}
			allLoans.toList
		} .get
		
	}

  def getActiveDebts(): Seq[Borrower] = {
    val query: String = "select concat(f_name, \' \', l_name) as name ,sum(loan_balance)" +
      " as loan from loan left join borrower on loan.borrower_id  = borrower.borrower_id " +
      "group by borrower.l_name, f_name;"
    val conn = ConnectionUtil.getConnection();
    Using.Manager { use =>
      val stmt = use(conn.prepareStatement(query))
      stmt.execute()
      conn.close()
      val rs = use(stmt.getResultSet())
      val activeDebts: ArrayBuffer[Borrower] = ArrayBuffer()
      while (rs.next()){
				activeDebts.addOne(Borrower.fromResultSet(rs))
			  } 
          activeDebts.toList
      } .get       
  }
  def lookupName(fname: String, lname: String): Seq[Person] = {
    val conn = ConnectionUtil.getConnection()
    Using.Manager { use =>
      val stmt = use(conn.prepareStatement("select borrower_id, concat(f_name, \' \', l_name) as name, " + 
      "address from borrower " +
	    "where (f_name = ? and l_name = ?);"))
      stmt.setString(1, fname)
      stmt.setString(2, lname)
      stmt.execute()
      conn.close()
      val rs = use(stmt.getResultSet())
      val allNames: ArrayBuffer[Person] = ArrayBuffer()
      while (rs.next()) {
        allNames.addOne(Person.fromResultSet(rs))
      }
        allNames.toList
    } .get
  }

  def lookUpNameLoan(fname: String, lname: String): Seq[Loan] = {
    val conn = ConnectionUtil.getConnection()
    Using.Manager { use =>
      val stmt = use(conn.prepareStatement("select loan_id, loan.borrower_id, init_loan, loan_balance "+
      "from loan inner join borrower " + 
      "on loan.borrower_id  = borrower.borrower_id " + 
      "where (f_name = ? and l_name = ?);"))
      stmt.setString(1, fname)
      stmt.setString(2, lname)
      stmt.execute()
      conn.close()
      val rs = use(stmt.getResultSet())
      val allLoans: ArrayBuffer[Loan] = ArrayBuffer()
      while (rs.next()) {
        allLoans.addOne(Loan.fromResultSet(rs))
      }
        allLoans.toList
    } .get
  }

/**
  * createBorrower() prepares the sql statement to add a new row
  *   to the borrower table in the database.
  * @param fname input into INSERT INTO sql stmt
  * @param lname input into INSERT INTO sql stmt
  * @param address input into INSERT INTO sql stmt
  * @return true or false
  */
def createBorrower(fname: String, lname: String, address : String): Boolean = {
  val conn = ConnectionUtil.getConnection()
  Using.Manager { use =>
    val stmt = use(conn.prepareStatement("insert into borrower values (DEFAULT, ?, ?, ?);"))
      stmt.setString(1, fname)
      stmt.setString(2, lname)
      stmt.setString(3, address)
      stmt.execute()
      conn.close()
      stmt.getUpdateCount() > 0
      
      } .getOrElse(false)
    }

def createLoan(fname: String, lname: String, initLoan: Double): Boolean = {
  val conn = ConnectionUtil.getConnection()
  Using.Manager { use =>
    val stmt = use(conn.prepareStatement("insert into loan values (DEFAULT, ?, ?, ?);"))
      stmt.setString(1, fname)
      stmt.setString(2, lname)
      stmt.setDouble(3, initLoan)
      stmt.execute()
      conn.close()
      stmt.getUpdateCount() > 0
      
      } .getOrElse(false)
    } 
//addLoan with the borrower ID from the borrower table and the initial loan amount    
def addLoan(borrowID: Int, initLoan: Float): Boolean = {
  val conn = ConnectionUtil.getConnection()
  Using.Manager { use =>
    val stmt = use (conn.prepareStatement("insert into loan values (DEFAULT, ?, ?, ?);"))
      stmt.setInt(1, borrowID)
      stmt.setFloat(2, initLoan)
      stmt.setFloat(3, initLoan) //we will make the loan balance the same as the initial loan
      stmt.execute()
      conn.close()  
      stmt.getUpdateCount() > 0

    } .getOrElse(false)
}

//Show all Persons (list all borrowers in the borrower table.)
def showPersons() : Seq[Person] = {
  val conn = ConnectionUtil.getConnection()
  Using.Manager { use =>
    val stmt = use (conn.prepareStatement("select borrower_id, concat(f_name, \' \', l_name) as name, " + 
      "address from borrower;"))
    stmt.execute()
    conn.close()
    val rs = use(stmt.getResultSet())
      val allNames: ArrayBuffer[Person] = ArrayBuffer()
      while (rs.next()) {
        allNames.addOne(Person.fromResultSet(rs))
      }
        allNames.toList
    } .get
  }
  /**
    * closeLoan() will be used when a borrower pays off partial or full amount
    *   of their loan. This will be updated with an UPDATE sql statement.  
    *
    * @param loanID the loan ID which the user would like to close out. 
    */
  def closeLoan(loanID : Int) : Unit = {
    val conn = ConnectionUtil.getConnection()
    Using.Manager { use =>
      val stmt = use(conn.prepareStatement("select loan_balance "+
       "from loan where loan_id = ?;"))
       stmt.setInt(1,loanID)
       stmt.execute()
       val rs = use(stmt.getResultSet())
       rs.next()
       val loan_balance = (rs.getFloat(1))
       println(s"The current balance on loanID: $loanID is $loan_balance")
       println("Please enter payment amount: ")
       val paymentAmount : Float = StdIn.readFloat()
       
       //update loan SET loan_balance = 
       val stmt2 = use(conn.prepareStatement("UPDATE loan SET loan_balance = " +
       "(loan_balance - ?) where loan_id = ?; "))
       if ((paymentAmount <= loan_balance) && paymentAmount > 0){
        stmt2.setFloat(1,paymentAmount) 
        stmt2.setInt(2,loanID)
        stmt2.execute()
        }
      else {println("Invalid payment amount.")}

      //  while (rs.next()) {
      //   closeStmt.addOne(_.fromResultSet(rs))
      // }
       conn.close()
    }
  }
  def deleteBorrower() : Unit = {
    println("First name? ")
    val fname = StdIn.readLine()
    println("Last name? ")
    val lname = StdIn.readLine()
    println("Person(Id, name, address)")
    println("------------------------------------")
    lookupName(fname, lname)
      .foreach(println)
    println("Please choose an ID to delete")
    val borrowerID : Int = StdIn.readInt()
    val conn = ConnectionUtil.getConnection()
    Using.Manager { use =>
      val stmt = use(conn.prepareStatement("delete from borrower where borrower_id = ?;"))
      stmt.setInt(1,borrowerID)
      stmt.execute()
      conn.close()
    }

  }
  def importTable(table: String, csv : String) : Unit = {
    val conn = ConnectionUtil.getConnection()
    Using.Manager { use =>
      val stmt = use(conn.prepareStatement(s"insert into $table values ($csv)"))
      //val stmt = use(conn.prepareStatement("insert into borrower values (DEFAULT,\'TEST\',\'TEST\',\'TEST\')"))
      stmt.execute()
    }
  }
}





