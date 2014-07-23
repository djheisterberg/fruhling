import java.sql.Driver
import java.sql.DriverManager

import org.junit.Assert
import org.junit.Test

package com.github.djheisterberg.fruhling.db {

  abstract class SlickProfilerCheck {

    def driver: Driver
    def url: String
    def username: String = null
    def password: String = null

    def expectedProductName: String

    @Test
    def testProductName() {
      DriverManager.registerDriver(driver)
      val cnx = DriverManager.getConnection(url, username, password)
      val dbMD = cnx.getMetaData()
      val productName = dbMD.getDatabaseProductName()
      Assert.assertEquals(expectedProductName, productName)
    }
  }
}
