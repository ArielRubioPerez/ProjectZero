package com.revature.loansys
import java.io.File
import scala.io.BufferedSource
import scala.io.Source

object FileUtil {
	def getTextContent(filename: String): Seq[String] = {
		var openedFile: BufferedSource = null
		try {
			val bufferedSource = io.Source.fromFile(filename)
			val lines = (for (line <- bufferedSource.getLines()) yield line).toList
			lines
		} finally {
			if (openedFile != null) openedFile.close()
			
		}
	}
}
