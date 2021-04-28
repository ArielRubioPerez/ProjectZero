package com.revature.loansys


import scala.util.matching.Regex
import scala.io.StdIn
import com.revature.loansys.loanDao
import com.revature.loansys.FileUtil
import java.io.FileNotFoundException
class Cli {

  val commandArgPattern: Regex ="(\\w+)\\s*(.*)".r

  def printWelcome(): Unit = {
    println("Welcome to the Loan App CLI")
  }

  def printOptions(): Unit = {
    println("------------------------------------")
    println("| Enter a command Available:        |")
    println("| active         -Lists Active Debts|")
    println("| add            -Adds a new debt   |")
    println("| lookup         -Search a person   |")
    println("| close          -Close out a debt  |")
    println("| delete         -Delete a borrower |")
    println("| csv            -Import CSV        |")
    println("| exit           -Exits Program     |")
    println("------------------------------------")
  }

  def menu(): Unit = {
    printWelcome()
    var menuLoop = true
    while (menuLoop) {
      printOptions()
      val input = (StdIn.readLine()).toLowerCase()


      input match {
        case commandArgPattern("exit", arg) => {
          println("exiting... ")
          menuLoop = false}
        case commandArgPattern("active", arg) => {
          println("active debts...")
          activeDebt()
          }
        case commandArgPattern("add", arg) => {
          println("adding a debt...")
          addLoan()
          }
        case commandArgPattern("lookup", arg) => {
          println("looking up...")
          lookupPerson()
        }
        case commandArgPattern("close", arg) => {
          println("closing debt...")
          // println("Bring up list of who's loans?")
          // lookupPerson()
          // println("Please select a loan ID to close: ")
          // val loanID = StdIn.readInt()
          // loanDao.closeLoan(loanID)
          closeLoan()
        }
        case commandArgPattern("delete", arg) => {
          println("deleting borrower...")
          loanDao.deleteBorrower()
        }
        case commandArgPattern("csv", arg) => {
          printCSV()
        }
        case commandArgPattern(cmd, arg) => {
          println("Failed to parse command")}
        case _ => {
          println("Failed to parse any input")}
      }
    }
  }

  def activeDebt(): Unit ={
    println("Listing active loans: ")
    println("Borrower(name, current loan balance)")
    println("------------------------------------")
    loanDao.getActiveDebts()
      //.filter(_.loan > 0)   
      .foreach(println)
  }

  def lookupPerson(): Unit = {
    println("Enter first name: ")
    val fnameInput = StdIn.readLine()
    println("Enter last name:")
    val lnameInput = StdIn.readLine()
    println(s"Listing $fnameInput $lnameInput loans: ")
    println("Loan(loanId, borrowerId, initial loan, loan balance)")
    println("------------------------------------")
    loanDao.lookUpNameLoan(fnameInput, lnameInput)
      .foreach(println)
  }

  def addLoan(): Unit = {
    println("Create a loan for existing person or new person?")
    println("Type: existing or new")
    val optionInput = (StdIn.readLine()).toLowerCase()
    if (optionInput == "existing"){
      getPersons() //show all persons
      println("Please select a borrow ID: ")
      val existingID = StdIn.readInt()
      println("How much is the initial loan? ")
      val iniLoan = StdIn.readFloat()
      try {
        if(loanDao.addLoan(existingID, iniLoan)) {
          println(s"Added a loan for $iniLoan for ID: $existingID)")
          }
      } catch {
          case e: Exception => {
            println("Failed to add a loan.")
          }
        }
    }
    else if (optionInput == "new"){
      println("First name? ")
      val fname = StdIn.readLine()
      println("Last name? ")
      val lname = StdIn.readLine()
      println("Address? ")
      val address = (StdIn.readLine()).toLowerCase()
      try {
        if (loanDao.createBorrower(fname,lname,address)) {
        println(s"Added $fname $lname to the Borrower table.")
        }
      } catch {
        case e: Exception => {
          println("Failed to add a person to the database.")
        }
      }
      //give this person a loan
      val borrowID = duplicateNameCheck(fname, lname)
      println(s"Return ID: $borrowID")
      println("How much is the initial loan?")
      val initLoan = StdIn.readFloat()
      try {
        if(loanDao.addLoan(borrowID, initLoan)) {
          println(s"Added a loan for $initLoan for $fname $lname (ID: $borrowID)")
          }
      } catch {
          case e: Exception => {
            println("Failed to add a loan.")
          }
        }
    }
  }

  def duplicateNameCheck(fname: String, lname: String): Int = {
    println("Person(Id, name, address)")
    println("------------------------------------")
    loanDao.lookupName(fname, lname)
      .foreach(println)
    println("Please choose an ID to add a loan")
    StdIn.readInt()
  } 

  def getPersons(): Unit = {
    println("Person(ID, name, address)")
    println("------------------------------------")
    loanDao.showPersons()
      .foreach(println)
  }

  def closeLoan() : Unit = {
    println("Bring up list of who's loans?")
    lookupPerson()
    println("Please select a loan ID to close: ")
    val loanID = StdIn.readInt()
    loanDao.closeLoan(loanID)
  }

  def printCSV(): Unit = {
    val borrower : String = "borrower"
    val loan : String = "loan"
    println("Enter a file name: ")
    val fileName = StdIn.readLine()
    try {
    val resource = FileUtil.getTextContent(fileName)
    println("Choose a table: (1) for borrower, (2) for loan")
    val choice = StdIn.readInt()
    if (choice == 1){
      for(element <- (resource)){
        //val element2 : String = element.replace("\'", "\\'").replace("\n", "")
        val element2 : String = element.replace("\n", "")
        println(s"Importing: $element2:")
        loanDao.importTable(borrower, element2)
      }
    }
    else if (choice == 2){
      for(element <- (resource)){
        //val element2 : String = element.replace("\'", "\\'").replace("\n", "")
        val element2 : String = element.replace("\n", "")
        println(s"Importing: $element2:")
        loanDao.importTable(loan, element2)
      }
    }
    else {println("Invalid choice.")
    }
    }catch {
      case fnfe: FileNotFoundException => {
        println(s"File not found ${fnfe.getMessage()}")
      }
    }
  

  }

}
